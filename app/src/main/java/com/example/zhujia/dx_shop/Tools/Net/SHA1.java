package com.example.zhujia.dx_shop.Tools.Net;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//使用sha-1方式进行加密
public class SHA1 {

    public static String digest(String content){
        StringBuilder builder = new StringBuilder();
        try {
            MessageDigest msgDitest = MessageDigest.getInstance("SHA-1");
            msgDitest.update(content.getBytes());
            byte[] digests = msgDitest.digest();
            //将每个字节转为16进制
            for (int i=0;i<digests.length;i++){
                builder.append(Integer.toHexString(digests[i] & 0xff +8));//+8为加盐操作
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return  builder.toString();
    }
}
