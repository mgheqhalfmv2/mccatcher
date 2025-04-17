package com.mgh.mccatcher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.mgh.mccatcher.adapter.AppListAdapter;
import com.mgh.mccatcher.adapter.PacketItem;
import com.mgh.mccatcher.adapter.PacketListAdapter;
import com.mgh.mccatcher.network.MCProtocol;
import com.mgh.mccatcher.network.RakNet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity {

    public static final int VPN_REQUEST_CODE = 1008;

    private Button startBtn;
    private Button checkedPackageBtn;
    private Button clearBtn;
    private TextView packageTextView;

    private CheckBox auto_smooth_btn;
    private CheckBox filter_dns_btn;
    private CheckBox filter_ack_btn;
    private CheckBox filter_ping_pong_btn;

    // synchronizedList 保证线程安全
    private List<PacketItem> items = Collections.synchronizedList( new ArrayList<>() );
    private PacketListAdapter packetListAdapter;
    private ListView packetListView;

    public static boolean isRunning = false;

    private boolean autoSmoothSroll = false;

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what == CustomAction.BROADCAST_ACTION){
                PacketItem item = (PacketItem) msg.getData().getSerializable("packet");
                if(item != null){
                    packetListAdapter.addItem(item);
                    if(autoSmoothSroll) packetListView.smoothScrollToPosition(items.size() - 1);// 自动滑到最后一个
                    // Log.d(MyVpnService.TAG, "add success");
                }
            }else if(msg.what == CustomAction.ERROR_STOP_VPN_ACTION){
                toast("服务异常: " + msg.getData().getString("error"));
                isRunning = false;
                updateButton(true);
            }
        }
    };

    @Override
    protected void onResume() {
        Log.d(MyVpnService.TAG, "ac::onResume: " + items.size());
        super.onResume();

        // 刷新数据，防止挂后台再打开不显示
        packetListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        Log.d(MyVpnService.TAG, "ac::onDestroy: " + items.size());
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(MyVpnService.TAG, "ac::onSaveInstanceState: " + items.size());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Log.d(MyVpnService.TAG, "ac::onRestoreInstanceState: " + items.size());
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(MyVpnService.TAG, "ac::onCreate: " + items.size());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RakNet.initPool();
        MCProtocol.initPool();

        packetListAdapter = new PacketListAdapter(this, R.layout.packet_list_item, items);
        packetListView = findViewById(R.id.packet_list_view);
        packetListView.setAdapter(packetListAdapter);
        packageTextView = findViewById(R.id.package_text_view);
        startBtn = findViewById(R.id.start_btn);
        checkedPackageBtn = findViewById(R.id.checked_package_btn);
        clearBtn = findViewById(R.id.clear_btn);

        auto_smooth_btn = findViewById(R.id.auto_smooth_btn);
        filter_dns_btn = findViewById(R.id.filter_btn_dns);
        filter_ack_btn = findViewById(R.id.filter_btn_ack_nack);
        filter_ping_pong_btn = findViewById(R.id.filter_btn_ping_pong);

        checkedPackageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.showAppListDialog();
            }
        });
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packetListAdapter.clearAllItems();
            }
        });
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    isRunning = false;
                    Intent intent = new Intent(MainActivity.this, MyVpnService.class);
                    intent.setAction(CustomAction.USER_STOP_VPN_ACTION);
                    MainActivity.this.startService(intent);
                    MainActivity.this.updateButton(true);
                } else {
                    String packageName = packageTextView.getText().toString().trim();
                    if (packageName.isEmpty()) {
                        MainActivity.this.toast("未选择应用");
                        return;
                    }
                    MainActivity.this.toast("启动...");
                    MyVpnService.mActivityHandler = mHandler;
                    MyVpnService.TARGET_PACKAGE_NAME = packageName;
                    MyVpnService.PACKET_FILTER = 0;
                    // checked
                    if (filter_dns_btn.isChecked())
                        MyVpnService.PACKET_FILTER |= CustomFilter.DNS_FILTER;
                    if (filter_ack_btn.isChecked())
                        MyVpnService.PACKET_FILTER |= CustomFilter.ACK_NACK_FILTER;
                    if (filter_ping_pong_btn.isChecked())
                        MyVpnService.PACKET_FILTER |= CustomFilter.CONNECTED_PING_PONG_FILTER;
                    autoSmoothSroll = auto_smooth_btn.isChecked();
                    MainActivity.this.updateButton(false);
                    isRunning = true;
                    MainActivity.this.startToCatch();

                }
            }
        });
        packetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PacketItem item = (PacketItem) parent.getItemAtPosition(position);
                showDetailDialog(MainActivity.this, item);
            }
        });
    }

    public void updateButton(boolean enable){
        if(enable){
            startBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.start_btn_color)));
            startBtn.setText("开启");

            clearBtn.setEnabled(true);
            checkedPackageBtn.setEnabled(true);
            filter_dns_btn.setActivated(true);
            filter_ping_pong_btn.setClickable(true);
            filter_ack_btn.setClickable(true);
            auto_smooth_btn.setActivated(true);
        }else{
            // 保留按钮的默认形状和padding，仅修改颜色
            startBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.close_btn_color)));
            startBtn.setText("关闭");

            clearBtn.setEnabled(false);
            checkedPackageBtn.setEnabled(false);
            filter_dns_btn.setActivated(false);
            filter_ping_pong_btn.setClickable(false);
            filter_ack_btn.setClickable(false);
            auto_smooth_btn.setActivated(false);
        }
    }

    public void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showDetailDialog(Context context, PacketItem item) {
        // 创建弹窗
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.packet_detail_dialog);

        // 设置弹窗宽度
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int)(context.getResources().getDisplayMetrics().widthPixels * 0.9);
            window.setAttributes(params);
        }

        // 绑定视图
        TextView tvTitle = dialog.findViewById(R.id.tv_detail_title);
        TextView tvFrom = dialog.findViewById(R.id.tv_detail_from);
        TextView tvTo = dialog.findViewById(R.id.tv_detail_to);
        TextView tvSize = dialog.findViewById(R.id.tv_detail_size);
        TextView tvByteData = dialog.findViewById(R.id.tv_byte_data);
        Button btnClose = dialog.findViewById(R.id.detail_btn_close);

        // 设置数据
        tvTitle.setText(item.getTitle());
        tvFrom.setText(item.getFrom());
        tvTo.setText(item.getTo());
        tvSize.setText(item.getSize() + "字节");
        tvByteData.setText(item.getDataHexString());
        // 关闭按钮点击事件
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // 显示弹窗
        dialog.show();
    }


    private void showAppListDialog() {
        // 获取所有已安装应用
        PackageManager pm = getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);

        // 过滤掉系统应用
        List<PackageInfo> filteredPackages = new ArrayList<>();
        for (PackageInfo pi : packages) {
            if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // 快速查找mc
                if(pi.packageName.startsWith("com.mojang"))
                    filteredPackages.add(pi);
            }
        }

        // 创建对话框
        AppListAdapter adapter = new AppListAdapter(this, R.layout.app_list_item,filteredPackages);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择应用");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PackageInfo selectedApp = filteredPackages.get(which);
                packageTextView.setText(selectedApp.packageName);
                MyVpnService.MINECRAFT_VERSION = selectedApp.versionName;
                Toast.makeText(MainActivity.this,
                        "已选择: " + selectedApp.applicationInfo.loadLabel(pm),
                        Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startToCatch() {
        Intent intent = VpnService.prepare(this);
        if (intent != null) {
            startActivityForResult(intent, VPN_REQUEST_CODE);
        } else {
            onActivityResult(VPN_REQUEST_CODE, RESULT_OK, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK) {
            Intent service = new Intent(this, MyVpnService.class);
            startService(service);
        }
    }


}