package com.mgh.mccatcher.adapter;

import com.mgh.mccatcher.CustomFilter;
import com.mgh.mccatcher.network.RakNet;

import java.io.Serializable;

public class PacketItem implements Serializable {

    private String title;
    private String from = "";
    private String to = "";
    private byte[] data = null;

    public PacketItem(){
    }

    public String getDataHexString(){
        if(data == null || data.length == 0){
            return "无数据";
        }

        StringBuilder sb = new StringBuilder(data.length);
        for(int i =0;i<data.length;++i){
            if(i > 0 && i % 16 == 0) sb.append('\n');
            sb.append(String.format("%02X", data[i] & 0xff)).append(' ');
        }
        return sb.toString();
    }


    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public byte[] getData() {
        return data;
    }

    public int getSize() {
        if(data == null || data.length == 0){
            return 0;
        }
        return data.length;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}