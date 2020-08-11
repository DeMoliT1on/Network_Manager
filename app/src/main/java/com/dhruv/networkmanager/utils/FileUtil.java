package com.dhruv.networkmanager.utils;

import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;

public class FileUtil {

    public static String getPath(Uri docUri){

        if(isExternalDirectory(docUri)){
            final String docId = DocumentsContract.getDocumentId(docUri);
           String[] contents =docId.split(":");
           if(contents.length>1){
               switch (contents[0]){
                   case "primary":return Environment.getExternalStorageDirectory()+"/"+contents[1]+"/";
                   default:return "/storage/"+contents[0]+"/"+contents[1]+"/";
               }
           }
           else {
               switch (contents[0]){
                   case "primary":return Environment.getExternalStorageDirectory()+"/";
                   default:return "/storage/"+contents[0]+"/";
               }
           }
        }
        else if(isDownloadsDirectory(docUri)){
            final String docId=DocumentsContract.getDocumentId(docUri);
            switch (docId){
                case "downloads":return Environment.getExternalStorageDirectory()+"/"+"Download/";
                default:return docId.split(":")[1]+"/";
            }
        }

        return null;
    }

    public static boolean isExternalDirectory(Uri docUri){
        return "com.android.externalstorage.documents".equals(docUri.getAuthority());
    }

    public static boolean isDownloadsDirectory(Uri docUri){
        return "com.android.providers.downloads.documents".equals(docUri.getAuthority());
    }
}
