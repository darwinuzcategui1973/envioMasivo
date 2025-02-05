package com.desige.webDocuments.document.servlet;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JFrame;

/**
 * Title: SuperFrame.java <br/> 
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>27/06/2004 (NC) Creation </li>
 </ul>
 */
public class SuperFrame extends JFrame {

    public Object sendFile(String dirServer,String service,String fileName) throws Exception {
        //System.out.println("BEGIN up FILE");
        String boundary =  "*****";
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;

        URL url = new URL(dirServer+service);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //System.out.println("Conection LINK..............."+url.getPath());
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
        FileInputStream fileInputStream = new FileInputStream( new File(fileName));
        DataOutputStream dos = new DataOutputStream( conn.getOutputStream() );
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"upload\";" + " filename=\"" + fileName +"\"" + lineEnd);
        dos.writeBytes(lineEnd);

        // create a buffer of maximum size

        bytesAvailable = fileInputStream.available();
        bufferSize = Math.min(bytesAvailable, maxBufferSize);
        buffer = new byte[bufferSize];

        // read file and write it into form...

        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        //System.out.println("BEGIN while");
        while (bytesRead > 0)
        {
            //System.out.println("into WHILE");
            dos.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }
        //System.out.println("END while");
        // send multipart form data necesssary after file data...

        dos.writeBytes(lineEnd);
        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

        // close streams

        fileInputStream.close();
        dos.flush();
        dos.close();
        //System.out.println("END up FILE");
        return null;
    }

    public Object sendRequeriment(String contextType,String dirServer,String service,Object data) throws Exception{
        URL url = new URL(dirServer+service);
        URLConnection uc = url.openConnection();
        uc.setDoOutput(true);
        uc.setDoInput(true);
        uc.setUseCaches(false);

        uc.setRequestProperty("Content-type",contextType);
        uc.setRequestProperty("Content-length",String.valueOf(service.length()));
        ObjectOutputStream objOut = new ObjectOutputStream(uc.getOutputStream());
//        //System.out.println("data.toString().getBytes() = " + data.toString().getBytes());
        //;
        if (data!=null){
            objOut.writeObject(data);
//            objOut.write(data.toString().getBytes());
        }
        InputStream in = uc.getInputStream();
        ObjectInputStream objStream;
        objStream = new ObjectInputStream(uc.getInputStream());
        Object dataRead = objStream.readObject();
        objStream.close();
        in.close();
        return dataRead;
    }
}
