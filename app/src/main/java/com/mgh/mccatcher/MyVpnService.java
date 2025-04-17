package com.mgh.mccatcher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.VpnService;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.mgh.mccatcher.adapter.PacketItem;
import com.mgh.mccatcher.network.IpProtocol;
import com.mgh.mccatcher.network.Ipv4Packet;
import com.mgh.mccatcher.network.RakNetPacketInfo;
import com.mgh.mccatcher.utils.ExceptionMsgUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyVpnService extends VpnService {

    public static final String TAG = "MyVpnService";

    public static String TARGET_PACKAGE_NAME = "";

    public static String MINECRAFT_VERSION = "";

    public static int PACKET_FILTER = 0;// 过滤器, 使用位运算

    public static final String DNS_SERVER = "114.114.114.114";

    public static final String VIRTUAL_ADDRESS = "135.168.1.1";

    private ParcelFileDescriptor vpnInterface = null;
    private ExecutorService executorService;

    private Selector selector = null;
    private Map<String, DatagramChannel> channels = null;

    public static Handler mActivityHandler = null;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivityHandler = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null && CustomAction.USER_STOP_VPN_ACTION.equals(intent.getAction())){
            stopVpn();
            return START_NOT_STICKY;
        }
        if(executorService == null){
            VpnService.Builder builder = new VpnService.Builder();
            builder.setSession("捕获MC数据包")
                    .addAddress(VIRTUAL_ADDRESS, 32)
                    .addRoute("0.0.0.0", 0)
                    .addDnsServer(DNS_SERVER);

            try {
                builder.addAllowedApplication(TARGET_PACKAGE_NAME);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, e.getMessage(), e);
            }

            vpnInterface = builder.establish();
            if(vpnInterface != null){
                try {
                    selector = Selector.open();
                    channels = new HashMap<>();
                    executorService = Executors.newFixedThreadPool(2);
                    executorService.execute(this::catchFromRemote);
                    executorService.execute(this::catchFromLocal);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                    stopVpn();
                    closeService(e);
                }
            }else{
                Log.d(TAG, "创建虚拟接口失败");
                stopVpn();
                closeService("创建虚拟接口失败");
                return START_NOT_STICKY;
            }
        }
        return START_STICKY;
    }

    /**
     * 捕获本机向外的流量
     */
    private void catchFromLocal(){
        Log.e(TAG, "catchFromLocal启动");
        try{
            FileChannel fin = new FileInputStream(vpnInterface.getFileDescriptor()).getChannel();
            ByteBuffer fromLocalBuffer = ByteBuffer.allocate(2000);
            while(!Thread.currentThread().isInterrupted()){
                fromLocalBuffer.clear();// 不擦除内容，但后续写会直接覆盖 position=0, limit=capacity
                int readBytes = fin.read(fromLocalBuffer);
                if(readBytes > 0){
                    fromLocalBuffer.flip();// position=0, limit=实际长度
                    byte[] data = new byte[readBytes];
                    fromLocalBuffer.get(data);// 深拷贝，data数据与buffer独立
                    Ipv4Packet packet = new Ipv4Packet(data);
                    if(packet.getProtocol() == IpProtocol.UDP){
                        String key = packet.getDstAddress().getHostAddress()
                                + ":" + packet.getDstPort()
                                + ":" + packet.getSrcPort();
                        DatagramChannel currentChannel = channels.getOrDefault(key, null);
                        if(currentChannel == null){
                            currentChannel = DatagramChannel.open();
                            protect(currentChannel.socket());
                            try {
                                currentChannel.connect(packet.getDstSocketAddress());
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage(), e);
                                closeChannel(currentChannel);
                                continue;
                            }
                            currentChannel.configureBlocking(false);
                            selector.wakeup();// 唤醒阻塞在select的selector
                            currentChannel.register(selector, SelectionKey.OP_READ, packet);// 存储packet对象，可以取出
                            channels.put(key, currentChannel);
                        }

                        try {
                            if(packet.hasUserData()){
                                fromLocalBuffer.clear();
                                fromLocalBuffer.put(packet.getUserData());
                                fromLocalBuffer.flip();// 调整指针
                                currentChannel.write(fromLocalBuffer);
                                // 添加记录
                                logPacket(packet);
                            }
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage(), e);
                            channels.remove(key);
                            closeChannel(currentChannel);
                        }
                    }
                }
            }
            Log.e(TAG, "catchFromLocal被打断");

        }catch (IOException e){
            if(MainActivity.isRunning){
                // 异常打断vpn
                Log.e(TAG, e.getMessage(), e);
                stopVpn();
                closeService(e);
            }
        } finally {
            Log.d(TAG, "关闭catchFromLocal");
        }
    }

    /**
     * 将接收到的外部数据包写回网络栈
     */
    private void catchFromRemote(){
        Log.e(TAG, "catchFromRemote启动");
        try {
            FileChannel fos = new FileOutputStream(vpnInterface.getFileDescriptor()).getChannel();
            while(!Thread.currentThread().isInterrupted()){
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "线程被中断，退出循环");
                        break;
                    }
                    continue;
                }
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> its = keys.iterator();
                ByteBuffer fromRemoteBuffer = ByteBuffer.allocate(2000);
                while (its.hasNext() && !Thread.interrupted()) {
                    SelectionKey key = its.next();
                    its.remove();
                    if (key.isValid() && key.isReadable()) {
                        DatagramChannel channel = (DatagramChannel) key.channel();
                        fromRemoteBuffer.clear();
                        int readBytes = channel.read(fromRemoteBuffer);
                        if(readBytes > 0){
                            byte[] data = new byte[readBytes];
                            fromRemoteBuffer.flip();
                            fromRemoteBuffer.get(data);
                            Ipv4Packet packet = new Ipv4Packet((Ipv4Packet) key.attachment());
                            packet.swapSrcAndDst();
                            packet.setUserData(data);

                            fromRemoteBuffer.clear();
                            fromRemoteBuffer.put(packet.toBinary());
                            fromRemoteBuffer.flip();
                            fos.write(fromRemoteBuffer);
                            // 添加记录
                            logPacket(packet);
                        }
                    }
                }
            }

            Log.e(TAG, "catchFromRemote被打断");

        } catch(ClosedSelectorException e) {
            Log.d(TAG, "selector已关闭，退出循环");
        } catch(IOException e) {
            Log.d(TAG, "catchFromRemote线程: " + !Thread.currentThread().isInterrupted());
            if(MainActivity.isRunning){
                // 异常打断vpn
                Log.e(TAG, e.getMessage(), e);
                stopVpn();
                closeService(e);
            }
        } finally {
            Log.d(TAG, "关闭catchFromRemote");
        }
    }

    public void closeService(String data){
        Message msg = mActivityHandler.obtainMessage(CustomAction.ERROR_STOP_VPN_ACTION);
        Bundle bundle = new Bundle();
        bundle.putString("error", data);
        msg.setData(bundle);
        mActivityHandler.sendMessage(msg);
    }

    public void closeService(Exception ex){
        Message msg = mActivityHandler.obtainMessage(CustomAction.ERROR_STOP_VPN_ACTION);
        Bundle bundle = new Bundle();
        bundle.putString("error", ExceptionMsgUtil.transformErrorMsg(ex));
        msg.setData(bundle);
        mActivityHandler.sendMessage(msg);
    }

    public void logPacket(Ipv4Packet packet){
        //Log.i(TAG, "logPacket");
        if(mActivityHandler != null){
            String title = "";
            if(DNS_SERVER.equals(packet.getDstAddress().getHostAddress()) ||
                    DNS_SERVER.equals(packet.getSrcAddress().getHostAddress())){
                if((PACKET_FILTER & CustomFilter.DNS_FILTER) != 0){
                    return;
                }
                title = "DNS";
            }else{
                RakNetPacketInfo info = new RakNetPacketInfo(packet.getUserData());
                if(((PACKET_FILTER & CustomFilter.ACK_NACK_FILTER) != 0) && info.isAckOrNack()){
                    return;
                }
                if(((PACKET_FILTER & CustomFilter.CONNECTED_PING_PONG_FILTER) != 0) && info.isPingPong()){
                    return;
                }
                title = info.getName();
            }

            PacketItem item = new PacketItem();
            item.setTitle(title);
            if (VIRTUAL_ADDRESS.equals(packet.getSrcAddress().getHostAddress())) {
                item.setFrom("127.0.0.1:" + packet.getSrcPort());
            }else{
                item.setFrom(packet.getSrcSocketAddress().toString());
            }
            if(VIRTUAL_ADDRESS.equals(packet.getDstAddress().getHostAddress())){
                item.setTo("127.0.0.1:" + packet.getDstPort());
            }else{
                item.setTo(packet.getDstSocketAddress().toString());
            }
            item.setData(packet.getUserData());

            Message message = mActivityHandler.obtainMessage(CustomAction.BROADCAST_ACTION);
            Bundle bundle = new Bundle();
            bundle.putSerializable("packet", item);
            message.setData(bundle);
            mActivityHandler.sendMessage(message);
        }
    }

    private void closeChannel(DatagramChannel channel){
        try{
            channel.close();
        }catch (IOException e){
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void stopVpn() {
        Log.d(TAG, "stopVpn");

        // 关闭VPN接口
        if (vpnInterface != null) {
            try {
                vpnInterface.close(); // 关闭 VPN 隧道
                vpnInterface = null;
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        if(selector!=null && selector.isOpen()){
            // 关闭所有通道
            for (SelectionKey key : selector.selectedKeys()) {
                key.cancel();
                closeChannel((DatagramChannel) key.channel());
            }
            try {
                selector.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        channels.clear();

        // 停止所有线程
        if(executorService!=null){
            executorService.shutdownNow();
            executorService = null;
        }

        stopSelf(); // 停止服务本身
    }

}