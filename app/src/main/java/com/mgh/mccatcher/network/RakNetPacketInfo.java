package com.mgh.mccatcher.network;

import static com.mgh.mccatcher.network.RakNet.ACK;
import static com.mgh.mccatcher.network.RakNet.CONNECTED_PING;
import static com.mgh.mccatcher.network.RakNet.CONNECTED_PONG;
import static com.mgh.mccatcher.network.RakNet.CONNECTION_REQUEST;
import static com.mgh.mccatcher.network.RakNet.CONNECTION_REQUEST_ACCEPTED;
import static com.mgh.mccatcher.network.RakNet.DISCONNECTION;
import static com.mgh.mccatcher.network.RakNet.INCOMPATIBLE_PROTOCOL_VERSION;
import static com.mgh.mccatcher.network.RakNet.NACK;
import static com.mgh.mccatcher.network.RakNet.NEW_INCOMING_CONNECTION;
import static com.mgh.mccatcher.network.RakNet.OPEN_CONNECTION_REPLY_1;
import static com.mgh.mccatcher.network.RakNet.OPEN_CONNECTION_REPLY_2;
import static com.mgh.mccatcher.network.RakNet.OPEN_CONNECTION_REQUEST_1;
import static com.mgh.mccatcher.network.RakNet.OPEN_CONNECTION_REQUEST_2;
import static com.mgh.mccatcher.network.RakNet.UNCONNECTED_PING;
import static com.mgh.mccatcher.network.RakNet.UNCONNECTED_PONG;

import com.mgh.mccatcher.MyVpnService;
import com.mgh.mccatcher.utils.BinaryStream;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RakNetPacketInfo {

    /**
     * 由于会存在被分割的包，记录分割唯一id，赋值名称
     */
    public static final Map<Integer, String> splitPacketMap = new ConcurrentHashMap<>();

    public static final Map<Integer, Integer> splitCountMap = new ConcurrentHashMap<>();

    public static final int TYPE_ACK_NACK = 0;

    public static final int TYPE_CONNECTED_PING_PONG = 1;

    public static final int TYPE_NORMAL = 2;

    private String name = "";

    private int  type = TYPE_NORMAL;

    public RakNetPacketInfo(byte[] packet){
        if(packet == null || packet.length == 0){
            this.name =  "INVALID";
            return;
        }
        byte packetId = packet[0];
        switch (packetId) {
            case UNCONNECTED_PING:
            case UNCONNECTED_PONG:
            case OPEN_CONNECTION_REQUEST_1:
            case OPEN_CONNECTION_REPLY_1:
            case OPEN_CONNECTION_REQUEST_2:
            case OPEN_CONNECTION_REPLY_2:
            case INCOMPATIBLE_PROTOCOL_VERSION:
                this.name = RakNet.getNameById(packetId);
                return;
            case ACK:
            case NACK:
                this.type = TYPE_ACK_NACK;
                this.name = RakNet.getNameById(packetId);
                return;
        }

        if((packetId & 0x80) != 0){
            // 解析raknet数据
            BinaryStream bs = new BinaryStream(packet);
            // 1bit isValid
            // 1bit isAck
            // 1bit isNack
            // 1bit isPakcettPair
            // 1bit isContinuousSend
            // 1bit requiresBAndS
            int datagramId = bs.getByte();

            // 3bytes(LE) sequenceNumber
            int sequenceNumber = bs.getLTriad();

            // 下面的封装内容, 可能有多个，但这里只解析一个出来
            // 3 bits reliability
            // 1bit isSegmented
            byte flags = (byte) (bs.getByte() & 0xff);
            Reliability reliability = Reliability.getReliabilityByValue((((flags & 0b11100000) >>> 5) & 0x07));
            if(reliability == null){
                this.name = String.format("%s: 0x%02X", "UNKNOWN",(packetId & 0xff));
                return;
            }
            boolean isSplit = (flags & 0b00010000) != 0;
            // 2bytes lengthInBits: 用户数据的大小，单位为bit
            int lengthInBits = bs.getShort();

            // if reliable: 3bytes(LE) reliableIndex
            if(reliability.isReliable()){
                int reliableIndex = bs.getLTriad();
            }
            // if sequence: 3bytes(LE) sequencedIndex
            if(reliability.isSequenced()){
                int sequencedIndex = bs.getLTriad();
            }
            // if arrange: 3bytes(LE) arrangeIndex
            // if arrange: 1byte arrangeChannel
            if(reliability.isArranged()){
                int arrangeIndex = bs.getLTriad();
                int arrangeChannel = bs.getByte();
            }

            // if segment: 4bytes splitSize
            // if segment: 2bytes splitId
            // if segment: 4bytes splitIndex
            int splitId = -1;
            int splitIndex = -1;
            if(isSplit){
                int splitSize = bs.getInt();
                splitId = bs.getShort();
                splitIndex = bs.getInt();

                if(splitIndex > 0){
                    String name = splitPacketMap.getOrDefault(splitId, "碎片");
                    this.name = name + "-" + splitIndex;
                    return;
                }

                // 移除Map中的元素，否则会不断堆积
                Integer count = splitCountMap.get(splitId);
                if(count == null){
                    splitCountMap.put(splitId, 0);
                }else{
                    if(count + 1 == splitSize){
                        splitCountMap.remove(splitId);
                        splitPacketMap.remove(splitId);
                    }else{
                        splitCountMap.put(splitId, count + 1);
                    }
                }
            }

            if(Math.ceil((double) lengthInBits / 8) > 0){
                // bytes 用户数据
                byte userDataId = (byte) bs.getByte();
                switch(userDataId){
                    case CONNECTED_PING:
                    case CONNECTED_PONG:
                        this.type = TYPE_CONNECTED_PING_PONG;
                        this.name = RakNet.getNameById(userDataId);
                        return;
                    case CONNECTION_REQUEST:
                    case CONNECTION_REQUEST_ACCEPTED:
                    case NEW_INCOMING_CONNECTION:
                    case DISCONNECTION:
                        this.name = RakNet.getNameById(userDataId);
                        return;
                    default:
                        if((userDataId & 0xff) == 0x8e || (userDataId & 0xff) == 0xfe){
                            userDataId = (byte) bs.getByte();
                        }
                        this.name = MCProtocol.getNameById(MyVpnService.MINECRAFT_VERSION, userDataId);
                        if(this.name == null){
                            this.name = String.format("%s: 0x%02X", "UNKNOWN",(userDataId & 0xff));
                        }
                        if(splitIndex == 0){
                            splitPacketMap.put(splitId, this.name);
                            this.name = this.name + "-0";
                        }
                        break;
                }
            }
        }


    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isAckOrNack(){
        return type == TYPE_ACK_NACK;
    }

    public boolean isPingPong(){
        return type == TYPE_CONNECTED_PING_PONG;
    }

    enum Reliability{
        UNRELIABLE(0),
        UNRELIABLE_SEQUENCED(1),
        RELIABLE(2),
        RELIABLE_ARRANGED(3),
        RELIABLE_SEQUENCED(4),
        UNRELIABLE_WITH_ACK_RECEIPT(5),
        RELIABLE_WITH_ACK_RECEIPT(6),
        RELIABLE_ARRANGED_WITH_ACK_RECEIPT(7);

        private final Integer value;

        Reliability(int value){
            this.value = value;
        }

        public boolean isReliable(){
            return value != UNRELIABLE.getValue() &&
                    value != UNRELIABLE_SEQUENCED.getValue() &&
                    value != UNRELIABLE_WITH_ACK_RECEIPT.getValue();
        }

        public boolean isArranged(){
            if(value == UNRELIABLE_SEQUENCED.getValue() ||
                    value == RELIABLE_ARRANGED.getValue() ||
                    value == RELIABLE_SEQUENCED.getValue() ||
                    value == RELIABLE_ARRANGED_WITH_ACK_RECEIPT.getValue()){
                return true;
            }
            return false;
        }

        public boolean isSequenced(){
            if(value == UNRELIABLE_SEQUENCED.getValue() ||
                    value == RELIABLE_SEQUENCED.getValue()){
                return true;
            }
            return false;
        }

        public int getValue() {
            return value;
        }

        public static Reliability getReliabilityByValue(int value){
            for (Reliability reliability : Reliability.values()) {
                if(reliability.getValue() == value){
                    return reliability;
                }
            }
            return null;
        }
    }


}