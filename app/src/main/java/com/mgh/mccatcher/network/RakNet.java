package com.mgh.mccatcher.network;

import android.util.Log;
import com.mgh.mccatcher.MyVpnService;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public interface RakNet {

    byte UNCONNECTED_PING = (byte) 0x01;
    byte UNCONNECTED_PONG = (byte) 0x1c;
    byte OPEN_CONNECTION_REQUEST_1 = (byte) 0x05;
    byte OPEN_CONNECTION_REPLY_1 = (byte) 0x06;
    byte OPEN_CONNECTION_REQUEST_2 = (byte) 0x07;
    byte OPEN_CONNECTION_REPLY_2 = (byte) 0x08;
    byte INCOMPATIBLE_PROTOCOL_VERSION = (byte) 0x19;

    byte ACK = (byte) 0xc0;
    byte NACK = (byte) 0xa0;

    byte CONNECTION_REQUEST = (byte) 0x09;

    byte CONNECTION_REQUEST_ACCEPTED = (byte) 0x10;

    byte NEW_INCOMING_CONNECTION = (byte) 0x13;

    byte DISCONNECTION = (byte) 0x15;

    byte CONNECTED_PING = (byte) 0x00;

    byte CONNECTED_PONG = (byte) 0x03;

    Map<Byte, String> raknetNamePool = new HashMap<>();

    static String getNameById(byte id){
        return raknetNamePool.getOrDefault(id, null);
    }

    static void initPool(){
        for (Field field : RakNet.class.getDeclaredFields()) {
            String name = field.getName();
            if("raknetNamePool".equals(name)){
                continue;
            }
            try{
                raknetNamePool.put(field.getByte(null), name);
            }catch (IllegalAccessException e){
                Log.e(MyVpnService.TAG, e.getMessage(), e);
            }

        }
    }

}