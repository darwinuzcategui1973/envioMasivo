package com.desige.webDocuments.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;

/**
 * Created by IntelliJ IDEA.
 * User: lcisneros
 * Date: Mar 1, 2007
 * Time: 4:56:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class EncryptorMD5 {

        /**
     *
     * Encripta un texto en formato MD5.
     *
     * @param in un String que contiene el texto a encriptar
     * @return el texto pasado como parametro, encriptado con MD5
     * @throws java.security.NoSuchAlgorithmException
     */
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


    public static void main(String[] args) throws NoSuchAlgorithmException {
        BufferedReader in;
        in = new BufferedReader(new InputStreamReader(System.in));

        //System.out.println("Ingrese el valor a encriptar");
        String value = null;
        try {
            value = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //System.out.println("Valor encriptado: ");

        //System.out.println(getMD5(value));

    }
}
