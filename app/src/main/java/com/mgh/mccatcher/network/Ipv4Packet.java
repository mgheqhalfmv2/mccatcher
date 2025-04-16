package com.mgh.mccatcher.network;

import com.mgh.mccatcher.utils.BinaryStream;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * 目前只支持udp协议
 */
public class Ipv4Packet {

    public static final int IPV4_HEADER_SIZE = 20;// 标准ipv4报文首部长度
    public static final int UDP_HEADER_SIZE = 8;// 标准udp报文首部长度

    private byte version;
    private byte IHL;
    private byte typeOfService;
    private int totalLength;
    private int identifier;
    private int flagsAndFragment;
    private int TTL;

    private int headerChecksum;
    private IpProtocol protocol;
    private InetSocketAddress srcSocketAddress;
    private InetSocketAddress dstSocektAddress;

    private int udpChecksum;
    private int udpTotalLength;

    private byte[] userData;

    public Ipv4Packet(Ipv4Packet other){
        // 拷贝构造
        this.version = other.version;
        this.IHL = other.IHL;
        this.typeOfService = other.typeOfService;
        this.totalLength = other.totalLength;
        this.identifier = other.identifier;
        this.flagsAndFragment = other.flagsAndFragment;
        this.TTL = (byte) other.TTL;
        this.protocol = other.protocol;
        this.headerChecksum = other.headerChecksum;
        this.srcSocketAddress = other.srcSocketAddress;
        this.dstSocektAddress = other.dstSocektAddress;
        this.udpTotalLength = other.udpTotalLength;
        this.udpChecksum = other.udpChecksum;
        this.userData = null;
    }
    public Ipv4Packet(byte[] data){
        BinaryStream bs = new BinaryStream(data);
        int verAndHLength = (byte) bs.getByte();
        this.version = (byte) ((verAndHLength >> 4 ) & 0x0f);
        this.IHL = (byte) (verAndHLength & 0x0f);
        this.typeOfService = (byte) bs.getByte();// 服务类型
        this.totalLength = bs.getShort();// 总长度，字节
        this.identifier = bs.getShort();
        this.flagsAndFragment = bs.getShort();// 3位标志，13位片位移
        this.TTL = bs.getByte();
        this.protocol = IpProtocol.getIpProtocolByCode(bs.getByte());
        this.headerChecksum = bs.getShort();

        // 32位源ip地址
        String sourceHost = bs.getByte() + "." +
                bs.getByte() + "." +
                bs.getByte() + "." +
                bs.getByte();


        // 32位目的ip地址
        String destinationHost = bs.getByte() + "." +
                bs.getByte() + "." +
                bs.getByte() + "." +
                bs.getByte();

        // 上层协议数据
        int sourcePort = bs.getShort();
        srcSocketAddress = new InetSocketAddress(sourceHost, sourcePort);

        int destinationPort = bs.getShort();
        dstSocektAddress = new InetSocketAddress(destinationHost, destinationPort);

        if(this.protocol == IpProtocol.UDP){
            this.udpTotalLength = bs.getShort();
            this.udpChecksum = bs.getShort();
            this.userData = bs.get();
        }else{
            // 不支持的协议
        }
    }

    public boolean hasUserData(){
        return userData != null && userData.length > 0;
    }

    public IpProtocol getProtocol() {
        return protocol;
    }

    public InetSocketAddress getSrcSocketAddress(){
        return srcSocketAddress;
    }

    public InetAddress getSrcAddress(){
        return srcSocketAddress.getAddress();
    }

    public InetSocketAddress getDstSocketAddress(){
        return dstSocektAddress;
    }

    public InetAddress getDstAddress(){
        return dstSocektAddress.getAddress();
    }

    public int getDstPort(){
        return dstSocektAddress.getPort();
    }

    public int getSrcPort(){
        return srcSocketAddress.getPort();
    }

    public byte[] getUserData(){
        return userData;
    }

    public void setUserData(byte[] data){
        this.userData = data;
    }

    public void swapSrcAndDst(){
        InetSocketAddress temp = srcSocketAddress;
        srcSocketAddress = dstSocektAddress;
        dstSocektAddress = temp;
    }

    public byte[] toBinary(){
        // 更新必要字段: 长度、校验和
        this.udpTotalLength = UDP_HEADER_SIZE + this.userData.length;
        this.udpChecksum = 0;// 直接置零，不需要进行校验
        this.totalLength = IPV4_HEADER_SIZE + this.udpTotalLength;

        ByteBuffer header = ByteBuffer.allocate(20);
        header.put((byte) ((this.version & 0xff) << 4 | (this.IHL & 0xff)));
        header.put((byte) (this.typeOfService & 0xff));
        header.putShort((short) this.totalLength);
        header.putShort((short) this.identifier);
        header.putShort((short) this.flagsAndFragment);
        header.put((byte) this.TTL);
        header.put((byte) this.protocol.getCode());
        header.putShort((short) 0);
        header.put(this.srcSocketAddress.getAddress().getAddress());
        header.put(this.dstSocektAddress.getAddress().getAddress());
        header.flip();
        int newIpChecksum = 0;
        // 将首部按16位分组累加
        for(int i =0;i<20;i+=2){
            newIpChecksum += (header.getShort() & 0xFFFF);
            // 超过16位需要进位
            newIpChecksum = (newIpChecksum & 0xFFFF) + (newIpChecksum >> 16);
        }
        this.headerChecksum = ~newIpChecksum;// 取反

        // ipv4 header
        BinaryStream bs = new BinaryStream();
        bs.putByte((byte) ((this.version & 0xff) << 4 | (this.IHL & 0xff)));
        bs.putByte((byte) (this.typeOfService & 0xff));
        bs.putShort(this.totalLength);
        bs.putShort(this.identifier);
        bs.putShort(this.flagsAndFragment);
        bs.putByte((byte) this.TTL);
        bs.putByte((byte) this.protocol.getCode());
        bs.putShort((short) this.headerChecksum);
        bs.put(this.srcSocketAddress.getAddress().getAddress());
        bs.put(this.dstSocektAddress.getAddress().getAddress());
        // udp header
        bs.putShort(this.srcSocketAddress.getPort());
        bs.putShort(this.dstSocektAddress.getPort());
        bs.putShort(this.udpTotalLength);
        bs.putShort(this.udpChecksum);
        // user data
        bs.put(this.userData);

        return bs.getBuffer();
    }
}