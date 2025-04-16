package com.mgh.mccatcher.network;

public enum IpProtocol {

    ICMP(1),
    TCP(6),
    UDP(17);

    private int code;

    private IpProtocol(int code){
        this.code = code;
    }

    public int getCode(){
        return this.code;
    }

    public static IpProtocol getIpProtocolByCode(int code){
        for (IpProtocol protocol : IpProtocol.values()) {
            if(protocol.getCode() == code){
                return protocol;
            }
        }
        return null;
    }





}