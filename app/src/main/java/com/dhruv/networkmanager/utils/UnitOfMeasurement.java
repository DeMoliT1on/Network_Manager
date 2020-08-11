package com.dhruv.networkmanager.utils;

public class UnitOfMeasurement {
    public static String usageB(long bytes){

        if(bytes>1000000000L){
            return String.format("%.2f",(double)bytes/1000000000.0)+"GB";
        }
        else if(bytes>1000000L){
            return String.format("%.2f",(double)bytes/1000000.0)+"MB";
        }
        else if(bytes>1000L){
            return String.format("%.2f",(double)bytes/1000.0)+"KB";
        }
        else{
            return bytes+"B";
        }
    }
    public static String usageIB(long bytes){


        return null;
    }
}
