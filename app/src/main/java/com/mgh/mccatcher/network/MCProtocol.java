package com.mgh.mccatcher.network;

import android.util.Log;

import com.mgh.mccatcher.MyVpnService;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public interface MCProtocol {

    Map<Byte, String> namePool70 = new HashMap<>();
    Map<Byte, String> namePool84 = new HashMap<>();
    Map<Byte, String> namePool90 = new HashMap<>();
    Map<Byte, String> namePool113 = new HashMap<>();

    byte LOGIN_PACKET_84 = (byte) 0x01;
    byte PLAY_STATUS_PACKET_84 = (byte) 0x02;
    byte SERVER_TO_CLIENT_HANDSHAKE_PACKET_84 = (byte) 0x03;
    byte CLIENT_TO_SERVER_HANDSHAKE_PACKET_84 = (byte) 0x04;
    byte DISCONNECT_PACKET_84 = (byte) 0x05;
    byte BATCH_PACKET_84 = (byte) 0x06;
    byte TEXT_PACKET_84 = (byte) 0x07;
    byte SET_TIME_PACKET_84 = (byte) 0x08;
    byte START_GAME_PACKET_84 = (byte) 0x09;
    byte ADD_PLAYER_PACKET_84 = (byte) 0x0a;
    byte ADD_ENTITY_PACKET_84 = (byte) 0x0b;
    byte REMOVE_ENTITY_PACKET_84 = (byte) 0x0c;
    byte ADD_ITEM_ENTITY_PACKET_84 = (byte) 0x0d;
    byte TAKE_ITEM_ENTITY_PACKET_84 = (byte) 0x0e;
    byte MOVE_ENTITY_PACKET_84 = (byte) 0x0f;
    byte MOVE_PLAYER_PACKET_84 = (byte) 0x10;
    byte RIDER_JUMP_PACKET_84 = (byte) 0x11;
    byte REMOVE_BLOCK_PACKET_84 = (byte) 0x12;
    byte UPDATE_BLOCK_PACKET_84 = (byte) 0x13;
    byte ADD_PAINTING_PACKET_84 = (byte) 0x14;
    byte EXPLODE_PACKET_84 = (byte) 0x15;
    byte LEVEL_EVENT_PACKET_84 = (byte) 0x16;
    byte BLOCK_EVENT_PACKET_84 = (byte) 0x17;
    byte ENTITY_EVENT_PACKET_84 = (byte) 0x18;
    byte MOB_EFFECT_PACKET_84 = (byte) 0x19;
    byte UPDATE_ATTRIBUTES_PACKET_84 = (byte) 0x1a;
    byte MOB_EQUIPMENT_PACKET_84 = (byte) 0x1b;
    byte MOB_ARMOR_EQUIPMENT_PACKET_84 = (byte) 0x1c;
    byte INTERACT_PACKET_84 = (byte) 0x1e;
    byte USE_ITEM_PACKET_84 = (byte) 0x1f;
    byte PLAYER_ACTION_PACKET_84 = (byte) 0x20;
    byte HURT_ARMOR_PACKET_84 = (byte) 0x21;
    byte SET_ENTITY_DATA_PACKET_84 = (byte) 0x22;
    byte SET_ENTITY_MOTION_PACKET_84 = (byte) 0x23;
    byte SET_ENTITY_LINK_PACKET_84 = (byte) 0x24;
    byte SET_HEALTH_PACKET_84 = (byte) 0x25;
    byte SET_SPAWN_POSITION_PACKET_84 = (byte) 0x26;
    byte ANIMATE_PACKET_84 = (byte) 0x27;
    byte RESPAWN_PACKET_84 = (byte) 0x28;
    byte DROP_ITEM_PACKET_84 = (byte) 0x29;
    byte CONTAINER_OPEN_PACKET_84 = (byte) 0x2a;
    byte CONTAINER_CLOSE_PACKET_84 = (byte) 0x2b;
    byte CONTAINER_SET_SLOT_PACKET_84 = (byte) 0x2c;
    byte CONTAINER_SET_DATA_PACKET_84 = (byte) 0x2d;
    byte CONTAINER_SET_CONTENT_PACKET_84 = (byte) 0x2e;
    byte CRAFTING_DATA_PACKET_84 = (byte) 0x2f;
    byte CRAFTING_EVENT_PACKET_84 = (byte) 0x30;
    byte ADVENTURE_SETTINGS_PACKET_84 = (byte) 0x31;
    byte BLOCK_ENTITY_DATA_PACKET_84 = (byte) 0x32;
    byte PLAYER_INPUT_PACKET_84 = (byte) 0x33;
    byte FULL_CHUNK_DATA_PACKET_84 = (byte) 0x34;
    byte SET_DIFFICULTY_PACKET_84 = (byte) 0x35;
    byte CHANGE_DIMENSION_PACKET_84 = (byte) 0x36;
    byte SET_PLAYER_GAMETYPE_PACKET_84 = (byte) 0x37;
    byte PLAYER_LIST_PACKET_84 = (byte) 0x38;
    byte TELEMETRY_EVENT_PACKET_84 = (byte) 0x39;
    byte SPAWN_EXPERIENCE_ORB_PACKET_84 = (byte) 0x3a;
    byte CLIENTBOUND_MAP_ITEM_DATA_PACKET_84 = (byte) 0x3b;
    byte MAP_INFO_REQUEST_PACKET_84 = (byte) 0x3c;
    byte REQUEST_CHUNK_RADIUS_PACKET_84 = (byte) 0x3d;
    byte CHUNK_RADIUS_UPDATED_PACKET_84 = (byte) 0x3e;
    byte ITEM_FRAME_DROP_ITEM_PACKET_84 = (byte) 0x3f;
    byte REPLACE_SELECTED_ITEM_PACKET_84 = (byte) 0x40;
    byte ADD_ITEM_PACKET_84 = (byte) 0x41;


    /**
     * Actual Minecraft: 0.14.3 PE protocol version 70
     */
    byte LOGIN_PACKET_70 = (byte) 0x8f;
    byte PLAY_STATUS_PACKET_70 = (byte) 0x90;
    byte DISCONNECT_PACKET_70 = (byte) 0x91;
    byte BATCH_PACKET_70 = (byte) 0x92;
    byte TEXT_PACKET_70 = (byte) 0x93;
    byte SET_TIME_PACKET_70 = (byte) 0x94;
    byte START_GAME_PACKET_70 = (byte) 0x95;
    byte ADD_PLAYER_PACKET_70 = (byte) 0x96;
    byte REMOVE_PLAYER_PACKET_70 = (byte) 0x97;
    byte ADD_ENTITY_PACKET_70 = (byte) 0x98;
    byte REMOVE_ENTITY_PACKET_70 = (byte) 0x99;
    byte ADD_ITEM_ENTITY_PACKET_70 = (byte) 0x9a;
    byte TAKE_ITEM_ENTITY_PACKET_70 = (byte) 0x9b;
    byte MOVE_ENTITY_PACKET_70 = (byte) 0x9c;
    byte MOVE_PLAYER_PACKET_70 = (byte) 0x9d;
    byte REMOVE_BLOCK_PACKET_70 = (byte) 0x9e;
    byte UPDATE_BLOCK_PACKET_70 = (byte) 0x9f;
    byte ADD_PAINTING_PACKET_70 = (byte) 0xa0;
    byte EXPLODE_PACKET_70 = (byte) 0xa1;
    byte LEVEL_EVENT_PACKET_70 = (byte) 0xa2;
    byte BLOCK_EVENT_PACKET_70 = (byte) 0xa3;
    byte ENTITY_EVENT_PACKET_70 = (byte) 0xa4;
    byte MOB_EFFECT_PACKET_70 = (byte) 0xa5;
    byte UPDATE_ATTRIBUTES_PACKET_70 = (byte) 0xa6;
    byte MOB_EQUIPMENT_PACKET_70 = (byte) 0xa7;
    byte MOB_ARMOR_EQUIPMENT_PACKET_70 = (byte) 0xa8;
    byte INTERACT_PACKET_70 = (byte) 0xa9;
    byte USE_ITEM_PACKET_70 = (byte) 0xaa;
    byte PLAYER_ACTION_PACKET_70 = (byte) 0xab;
    byte HURT_ARMOR_PACKET_70 = (byte) 0xac;
    byte SET_ENTITY_DATA_PACKET_70 = (byte) 0xad;
    byte SET_ENTITY_MOTION_PACKET_70 = (byte) 0xae;
    byte SET_ENTITY_LINK_PACKET_70 = (byte) 0xaf;
    byte SET_HEALTH_PACKET_70 = (byte) 0xb0;
    byte SET_SPAWN_POSITION_PACKET_70 = (byte) 0xb1;
    byte ANIMATE_PACKET_70 = (byte) 0xb2;
    byte RESPAWN_PACKET_70 = (byte) 0xb3;
    byte DROP_ITEM_PACKET_70 = (byte) 0xb4;
    byte CONTAINER_OPEN_PACKET_70 = (byte) 0xb5;
    byte CONTAINER_CLOSE_PACKET_70 = (byte) 0xb6;
    byte CONTAINER_SET_SLOT_PACKET_70 = (byte) 0xb7;
    byte CONTAINER_SET_DATA_PACKET_70 = (byte) 0xb8;
    byte CONTAINER_SET_CONTENT_PACKET_70 = (byte) 0xb9;
    byte CRAFTING_DATA_PACKET_70 = (byte) 0xba;
    byte CRAFTING_EVENT_PACKET_70 = (byte) 0xbb;
    byte ADVENTURE_SETTINGS_PACKET_70 = (byte) 0xbc;
    byte BLOCK_ENTITY_DATA_PACKET_70 = (byte) 0xbd;
    byte PLAYER_INPUT_PACKET_70 = (byte) 0xbe;
    byte FULL_CHUNK_DATA_PACKET_70 = (byte) 0xbf;
    byte SET_DIFFICULTY_PACKET_70 = (byte) 0xc0;
    byte CHANGE_DIMENSION_PACKET_70 = (byte) 0xc1;
    byte SET_PLAYER_GAMETYPE_PACKET_70 = (byte) 0xc2;
    byte PLAYER_LIST_PACKET_70 = (byte) 0xc3;
    byte TELEMETRY_EVENT_PACKET_70 = (byte) 0xc4;
    byte SPAWN_EXPERIENCE_ORB_PACKET_70 = (byte) 0xc5;
    byte CLIENTBOUND_MAP_ITEM_DATA_PACKET_70 = (byte) 0xc6;
    byte MAP_INFO_REQUEST_PACKET_70 = (byte) 0xc7;
    byte REQUEST_CHUNK_RADIUS_PACKET_70 = (byte) 0xc8;
    byte CHUNK_RADIUS_UPDATE_PACKET_70 = (byte) 0xc9;
    byte ITEM_FRAME_DROP_ITEM_PACKET_70 = (byte) 0xca;
    byte REPLACE_SELECTED_ITEM_PACKET_70 = (byte) 0xcb;


    /**
     * 以下为0.16版本（协议版本为91）的数据包ID，与0.15相似，但加入了几个新的数据包
     */

    //byte LOGIN_PACKET_90 = (byte) 0x01;
    //byte PLAY_STATUS_PACKET_90 = (byte) 0x02;
    byte SERVER_TO_CLIENT_HANDSHAKE_PACKET_90 = (byte) 0x03;
    byte CLIENT_TO_SERVER_HANDSHAKE_PACKET_90 = (byte) 0x04;
    byte DISCONNECT_PACKET_90 = (byte) 0x05;
    byte BATCH_PACKET_90 = (byte) 0x06;
    byte RESOURCE_PACKS_INFO_PACKET_90 = (byte) 0x07;
    byte RESOURCE_PACK_STACK_PACKET_90 = (byte) 0x08;
    byte RESOURCE_PACK_CLIENT_RESPONSE_PACKET_90 = (byte) 0x09;
    byte TEXT_PACKET_90 = (byte) 0x0a;
    byte SET_TIME_PACKET_90 = (byte) 0x0b;
    byte START_GAME_PACKET_90 = (byte) 0x0c;
    byte ADD_PLAYER_PACKET_90 = (byte) 0x0d;
    byte ADD_ENTITY_PACKET_90 = (byte) 0x0e;
    byte REMOVE_ENTITY_PACKET_90 = (byte) 0x0f;
    byte ADD_ITEM_ENTITY_PACKET_90 = (byte) 0x10;
    byte ADD_HANGING_ENTITY_PACKET_90 = (byte) 0x11;
    byte TAKE_ITEM_ENTITY_PACKET_90 = (byte) 0x12;
    byte MOVE_ENTITY_PACKET_90 = (byte) 0x13;
    byte MOVE_PLAYER_PACKET_90 = (byte) 0x14;
    byte RIDER_JUMP_PACKET_90 = (byte) 0x15;
    byte REMOVE_BLOCK_PACKET_90 = (byte) 0x16;
    byte UPDATE_BLOCK_PACKET_90 = (byte) 0x17;
    byte ADD_PAINTING_PACKET_90 = (byte) 0x18;
    byte EXPLODE_PACKET_90 = (byte) 0x19;
    byte LEVEL_SOUND_EVENT_PACKET_90 = (byte) 0x1a;
    byte LEVEL_EVENT_PACKET_90 = (byte) 0x1b;
    byte BLOCK_EVENT_PACKET_90 = (byte) 0x1c;
    byte ENTITY_EVENT_PACKET_90 = (byte) 0x1d;
    byte MOB_EFFECT_PACKET_90 = (byte) 0x1e;
    byte UPDATE_ATTRIBUTES_PACKET_90 = (byte) 0x1f;
    byte MOB_EQUIPMENT_PACKET_90 = (byte) 0x20;
    byte MOB_ARMOR_EQUIPMENT_PACKET_90 = (byte) 0x21;
    byte INTERACT_PACKET_90 = (byte) 0x22;
    byte USE_ITEM_PACKET_90 = (byte) 0x23;
    byte PLAYER_ACTION_PACKET_90 = (byte) 0x24;
    byte HURT_ARMOR_PACKET_90 = (byte) 0x25;
    byte SET_ENTITY_DATA_PACKET_90 = (byte) 0x26;
    byte SET_ENTITY_MOTION_PACKET_90 = (byte) 0x27;
    byte SET_ENTITY_LINK_PACKET_90 = (byte) 0x28;
    byte SET_HEALTH_PACKET_90 = (byte) 0x29;
    byte SET_SPAWN_POSITION_PACKET_90 = (byte) 0x2a;
    byte ANIMATE_PACKET_90 = (byte) 0x2b;
    byte RESPAWN_PACKET_90 = (byte) 0x2c;
    byte DROP_ITEM_PACKET_90 = (byte) 0x2d;
    byte INVENTORY_ACTION_PACKET_90 = (byte) 0x2e;
    byte CONTAINER_OPEN_PACKET_90 = (byte) 0x2f;
    byte CONTAINER_CLOSE_PACKET_90 = (byte) 0x30;
    byte CONTAINER_SET_SLOT_PACKET_90 = (byte) 0x31;
    byte CONTAINER_SET_DATA_PACKET_90 = (byte) 0x32;
    byte CONTAINER_SET_CONTENT_PACKET_90 = (byte) 0x33;
    byte CRAFTING_DATA_PACKET_90 = (byte) 0x34;
    byte CRAFTING_EVENT_PACKET_90 = (byte) 0x35;
    byte ADVENTURE_SETTINGS_PACKET_90 = (byte) 0x36;
    byte BLOCK_ENTITY_DATA_PACKET_90 = (byte) 0x37;
    byte PLAYER_INPUT_PACKET_90 = (byte) 0x38;
    byte FULL_CHUNK_DATA_PACKET_90 = (byte) 0x39;
    byte SET_COMMANDS_ENABLED_PACKET_90 = (byte) 0x3a;
    byte SET_DIFFICULTY_PACKET_90 = (byte) 0x3b;
    byte CHANGE_DIMENSION_PACKET_90 = (byte) 0x3c;
    byte SET_PLAYER_GAME_TYPE_PACKET_90 = (byte) 0x3d;
    byte PLAYER_LIST_PACKET_90 = (byte) 0x3e;
    byte EVENT_PACKET_90 = (byte) 0x3f;
    byte SPAWN_EXPERIENCE_ORB_PACKET_90 = (byte) 0x40;
    byte CLIENTBOUND_MAP_ITEM_DATA_PACKET_90 = (byte) 0x41;
    byte MAP_INFO_REQUEST_PACKET_90 = (byte) 0x42;

    byte REQUEST_CHUNK_RADIUS_PACKET_90 = (byte) 0x43;
    byte CHUNK_RADIUS_UPDATED_PACKET_90 = (byte) 0x44;
    byte ITEM_FRAME_DROP_ITEM_PACKET_90 = (byte) 0x45;
    byte REPLACE_SELECTED_ITEM_PACKET_90 = (byte) 0x46;
    byte GAME_RULES_CHANGED_PACKET_90 = (byte) 0x47;
    byte CAMERA_PACKET_90 = (byte) 0x48;
    byte ADD_ITEM_PACKET_90 = (byte) 0x49;

    byte BOSS_EVENT_PACKET_90 = (byte) 0x4a;
    byte AVAILABLE_COMMANDS_PACKET_90 = (byte) 0x4b;
    byte COMMAND_STEP_PACKET_90 = (byte) 0x4c;
    byte RESOURCE_PACK_DATA_INFO_PACKET_90 = (byte) 0x4d;
    byte RESOURCE_PACK_CHUNK_DATA_PACKET_90 = (byte) 0x4e;
    byte RESOURCE_PACK_CHUNK_REQUEST_PACKET_90 = (byte) 0x4f;



    // 以下为 1.1.0-1.1.7 的数据包ID-------------------------
    byte DISCONNECT_PACKET_113 = 0x05;
    byte RESOURCE_PACKS_INFO_PACKET_113 = 0x06;
    byte RESOURCE_PACK_STACK_PACKET_113 = 0x07;
    byte RESOURCE_PACK_CLIENT_RESPONSE_PACKET_113 = 0x08;
    byte TEXT_PACKET_113 = 0x09;
    byte SET_TIME_PACKET_113 = 0x0a;
    byte START_GAME_PACKET_113 = 0x0b;
    byte ADD_PLAYER_PACKET_113 = 0x0c;
    byte ADD_ENTITY_PACKET_113 = 0x0d;
    byte REMOVE_ENTITY_PACKET_113 = 0x0e;
    byte ADD_ITEM_ENTITY_PACKET_113 = 0x0f;
    byte ADD_HANGING_ENTITY_PACKET_113 = 0x10;
    byte TAKE_ITEM_ENTITY_PACKET_113 = 0x11;
    byte MOVE_ENTITY_PACKET_113 = 0x12;
    byte MOVE_PLAYER_PACKET_113 = 0x13;
    byte RIDER_JUMP_PACKET_113 = 0x14;
    byte REMOVE_BLOCK_PACKET_113 = 0x15;
    byte UPDATE_BLOCK_PACKET_113 = 0x16;
    byte ADD_PAINTING_PACKET_113 = 0x17;
    byte EXPLODE_PACKET_113 = 0x18;
    byte LEVEL_SOUND_EVENT_PACKET_113 = 0x19;
    byte LEVEL_EVENT_PACKET_113 = 0x1a;
    byte BLOCK_EVENT_PACKET_113 = 0x1b;
    byte ENTITY_EVENT_PACKET_113 = 0x1c;
    byte MOB_EFFECT_PACKET_113 = 0x1d;
    byte UPDATE_ATTRIBUTES_PACKET_113 = 0x1e;
    byte MOB_EQUIPMENT_PACKET_113 = 0x1f;
    byte MOB_ARMOR_EQUIPMENT_PACKET_113 = 0x20;
    byte INTERACT_PACKET_113 = 0x21;
    byte BLOCK_PICK_REQUEST_PACKET_113 = 0x22;
    byte USE_ITEM_PACKET_113 = 0x23;
    byte PLAYER_ACTION_PACKET_113 = 0x24;
    byte ENTITY_FALL_PACKET_113 = 0x25;
    byte HURT_ARMOR_PACKET_113 = 0x26;
    byte SET_ENTITY_DATA_PACKET_113 = 0x27;
    byte SET_ENTITY_MOTION_PACKET_113 = 0x28;
    byte SET_ENTITY_LINK_PACKET_113 = 0x29;
    byte SET_HEALTH_PACKET_113 = 0x2a;
    byte SET_SPAWN_POSITION_PACKET_113 = 0x2b;
    byte ANIMATE_PACKET_113 = 0x2c;
    byte RESPAWN_PACKET_113 = 0x2d;
    byte DROP_ITEM_PACKET_113 = 0x2e;
    byte INVENTORY_ACTION_PACKET_113 = 0x2f;
    byte CONTAINER_OPEN_PACKET_113 = 0x30;
    byte CONTAINER_CLOSE_PACKET_113 = 0x31;
    byte CONTAINER_SET_SLOT_PACKET_113 = 0x32;
    byte CONTAINER_SET_DATA_PACKET_113 = 0x33;
    byte CONTAINER_SET_CONTENT_PACKET_113 = 0x34;
    byte CRAFTING_DATA_PACKET_113 = 0x35;
    byte CRAFTING_EVENT_PACKET_113 = 0x36;
    byte ADVENTURE_SETTINGS_PACKET_113 = 0x37;
    byte BLOCK_ENTITY_DATA_PACKET_113 = 0x38;
    byte PLAYER_INPUT_PACKET_113 = 0x39;
    byte FULL_CHUNK_DATA_PACKET_113 = 0x3a;
    byte SET_COMMANDS_ENABLED_PACKET_113 = 0x3b;
    byte SET_DIFFICULTY_PACKET_113 = 0x3c;
    byte CHANGE_DIMENSION_PACKET_113 = 0x3d;
    byte SET_PLAYER_GAME_TYPE_PACKET_113 = 0x3e;
    byte PLAYER_LIST_PACKET_113 = 0x3f;
    byte SIMPLE_EVENT_PACKET_113 = 0x40;
    byte EVENT_PACKET_113 = 0x41;
    byte SPAWN_EXPERIENCE_ORB_PACKET_113 = 0x42;
    byte CLIENTBOUND_MAP_ITEM_DATA_PACKET_113 = 0x43;
    byte MAP_INFO_REQUEST_PACKET_113 = 0x44;
    byte REQUEST_CHUNK_RADIUS_PACKET_113 = 0x45;
    byte CHUNK_RADIUS_UPDATED_PACKET_113 = 0x46;
    byte ITEM_FRAME_DROP_ITEM_PACKET_113 = 0x47;
    byte REPLACE_ITEM_IN_SLOT_PACKET_113 = 0x48;
    byte GAME_RULES_CHANGED_PACKET_113 = 0x49;
    byte CAMERA_PACKET_113 = 0x4a;
    byte ADD_ITEM_PACKET_113 = 0x4b;
    byte BOSS_EVENT_PACKET_113 = 0x4c;
    byte SHOW_CREDITS_PACKET_113 = 0x4d;
    byte AVAILABLE_COMMANDS_PACKET_113 = 0x4e;
    byte COMMAND_STEP_PACKET_113 = 0x4f;
    byte COMMAND_BLOCK_UPDATE_PACKET_113 = 0x50;
    byte UPDATE_TRADE_PACKET_113 = 0x51;
    byte UPDATE_EQUIP_PACKET_113 = 0x52;
    byte RESOURCE_PACK_DATA_INFO_PACKET_113 = 0x53;
    byte RESOURCE_PACK_CHUNK_DATA_PACKET_113 = 0x54;
    byte RESOURCE_PACK_CHUNK_REQUEST_PACKET_113 = 0x55;
    byte TRANSFER_PACKET_113 = 0x56;
    byte PLAY_SOUND_PACKET_113 = 0x57;
    byte STOP_SOUND_PACKET_113 = 0x58;
    byte SET_TITLE_PACKET_113 = 0x59;
    byte ADD_BEHAVIOR_TREE_PACKET_113 = 0x5a;
    byte STRUCTURE_BLOCK_UPDATE_PACKET_113 = 0x5b;
    byte SHOW_STORE_OFFER_PACKET_113 = 0x5c;
    byte PURCHASE_RECEIPT_PACKET_113 = 0x5d;

    byte BATCH_PACKET_113 = (byte) 0xfe;


    static void initPool()  {
        // 偷个懒，用反射获取字段名与值初始化
        for (Field field : MCProtocol.class.getDeclaredFields()) {
            String name = field.getName();
            if(name.startsWith("namePool")){
                continue;
            }
            try{
                if(name.endsWith("70")){
                    namePool70.put(field.getByte(null), name);
                }else if(name.endsWith("84")){
                    namePool84.put(field.getByte(null), name);
                }else if(name.endsWith("90")){
                    namePool90.put(field.getByte(null), name);
                }
            }catch (IllegalAccessException e){
                Log.e(MyVpnService.TAG, e.getMessage(), e);
            }
        }
    }

    static String getNameById(String version, byte userDataId) {
        String prefix = version;
        if(version.length() > 4){
            prefix = version.substring(0, 4);
        }
        switch(prefix){
            case "0.11":
            case "0.12":
            case "0.13":
            case "0.14":
                return namePool70.getOrDefault(userDataId, null);
            case "0.15":
                return namePool84.getOrDefault(userDataId, null);
            case "0.16":
                return namePool90.getOrDefault(userDataId, null);
        }
        return null;
        // 默认用0.14的名称池
        // return namePool70.getOrDefault(userDataId, null);
    }
}