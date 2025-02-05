package com.focus.util;

import java.security.NoSuchAlgorithmException;

public class Encriptor {
	
    public static String getMD5(String in) throws java.security.NoSuchAlgorithmException {
        String checksum = null;
        if (in != null) {
            java.security.MessageDigest md5 = java.security.MessageDigest.getInstance("MD5");
            checksum = toHexString(md5.digest(in.getBytes()));
        }
        return checksum;
    }
    
    public static String toHexString(byte[] digest) {
        char[] values={'0', '1', '2', '3', '4', '5', '6', '7', '8',
        '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        StringBuffer hex=new StringBuffer();
        for(int i=0; i<digest.length; i++) {
            byte b=digest[i];
            hex.append(values[(b >> 4) & 0xf]);
            hex.append(values[b & 0x0f]);
        }
        return hex.toString();
    }
    
    public static String getSID(String ticket) throws NoSuchAlgorithmException {
    	//return Encriptor.getMD5(Constants.numero(ticket,10)).substring(0,10);
    	return "";
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
		System.out.println(Encriptor.getMD5("jairo"));
	}
}
