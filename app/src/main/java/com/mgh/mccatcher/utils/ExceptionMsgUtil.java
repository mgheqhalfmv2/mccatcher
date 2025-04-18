package com.mgh.mccatcher.utils;

import java.time.LocalDateTime;

public class ExceptionMsgUtil {

    public static String transformErrorMsg(Exception ex){
        StringBuilder sb = new StringBuilder();
        sb.append("时间：").append(LocalDateTime.now());
        sb.append("\nmsg: ").append(ex.getMessage());
        sb.append("\ntype: ").append(ex);
        StackTraceElement[] traces = ex.getStackTrace();
        for(int i =0;i<traces.length;++i){
            sb.append(i).append('-').append(traces.length);
            sb.append("\nclass: ").append(traces[i].getClassName());
            sb.append("\nfile: ").append(traces[i].getFileName());
            sb.append("\nmethod: ").append(traces[i].getMethodName());
            sb.append("\nline: ").append(traces[i].getLineNumber());
            sb.append("\n");
        }
        return sb.toString();
    }




}