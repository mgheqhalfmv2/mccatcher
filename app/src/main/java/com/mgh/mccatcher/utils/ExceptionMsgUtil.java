package com.mgh.mccatcher.utils;

public class ExceptionMsgUtil {

    public static String transformErrorMsg(Exception ex){
        StringBuilder sb = new StringBuilder();
        sb.append("msg: ").append(ex.getMessage());
        sb.append("\ntype: ").append(ex);
        StackTraceElement trace = ex.getStackTrace()[0];
        sb.append("\nclass: ").append(trace.getClassName());
        sb.append("\nfile: ").append(trace.getFileName());
        sb.append("\nmethod: ").append(trace.getMethodName());
        sb.append("\nline: ").append(trace.getLineNumber());

        return sb.toString();
    }




}