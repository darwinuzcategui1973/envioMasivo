package com.desige.webDocuments.persistent.managers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.desige.webDocuments.mail.actions.DeleteFileThread;

public class FileZip {
	
	private long length = 0;
	private int available = 0;
	private static boolean COMPRIMIR=false;
	
	public FileZip() {
	}

	//FileInputStream
	public FileInputStream zip(FileInputStream flujo) throws IOException {
		length = flujo.available();
		available = flujo.available();
		return flujo;
	}
	
	public InputStream zip(InputStream flujo) throws IOException {
		length = flujo.available();
		available = flujo.available();
		return flujo;
	}

	public InputStream zipInputStream(InputStream flujo) throws IOException {
		length = flujo.available();
		available = flujo.available();
		return flujo;
	}
	
	/*
	 * Metodo en deshuso 
	 */
	public InputStream zip_NO_UTILIZADO(InputStream flujo) throws IOException {
		if(!COMPRIMIR) {
			available = flujo.available();
			return flujo;
		}
		String fullNameZip = System.getProperty("user.dir").concat(File.separator);
		fullNameZip = fullNameZip.concat("qweb_"+ new Date().getTime() + ".zip");
		byte data[] = new byte[2048];
		FileOutputStream dest = new FileOutputStream(fullNameZip);
		// mas seguro new CRC32()
		CheckedOutputStream checksum = new CheckedOutputStream(dest, new CRC32());
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(checksum));
		InputStream fi = flujo;
		BufferedInputStream origin = new BufferedInputStream(fi, data.length);
		ZipEntry entry = new ZipEntry("documento");
		out.putNextEntry(entry);
		int count;
		while ((count = origin.read(data, 0, data.length)) != -1) {
			out.write(data, 0, count);
		}
		origin.close();
		out.close();

		File ficheroZip = new File(fullNameZip);
		FileInputStream streamComprimido = new FileInputStream(ficheroZip);
		length = ficheroZip.length();
		available = streamComprimido.available();
		
		//System.out.println("PATH = "+ficheroZip.getAbsolutePath());
		//System.out.println("NAME = "+ficheroZip.getName());

		// liberarmos el archivo
		//streamComprimido.close(); // da error si lo cerramos
		
		DeleteFileThread hacer2 = new DeleteFileThread(fullNameZip);
		hacer2.start();
		
		return streamComprimido;
	}
	
	public static InputStream unZip(InputStream flujo) {
		return flujo;
		/*
		if(!COMPRIMIR) {
			return flujo;
		}

		ZipInputStream zis = null;
		try {
			zis = new ZipInputStream(new BufferedInputStream(flujo));
			//zis.getNextEntry();
			if(zis.getNextEntry() == null) {
				flujo.reset(); // reiniciamos el flujo
				return flujo; // retornamos el flujo original
			}
		} catch (Exception e) {
			e.printStackTrace();
			return flujo; // retornamos el flujo original
		}
		return zis;
		*/
	}
	
	public static boolean isFormatZip(InputStream flujo) {
		ZipInputStream zis = null;
		try {
			zis = new ZipInputStream(new BufferedInputStream(flujo));
			if(zis.getNextEntry() == null) {
				try {
					flujo.reset(); // reiniciamos el flujo
				} catch (Exception e) {
					//System.out.println("El flujo no acepta el reset()");
				}
				zis = null;
			}
		} catch (Exception e) {
			zis = null;
			e.printStackTrace();
		}
		return (zis!=null);
	}
	
	
	public static byte[] readBytes(InputStream inputStream) throws IOException {
		byte[] bytes = null; 
		byte[] buffer = new byte[4096];
		int bytesRead = 0;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			if (bytes != null) {
				byte[] oldBytes = bytes;
				bytes = new byte[oldBytes.length + bytesRead];
				System.arraycopy(oldBytes, 0, bytes, 0, oldBytes.length);
				System.arraycopy(buffer, 0, bytes, oldBytes.length, bytesRead);
			} else {
				bytes = new byte[bytesRead];
				System.arraycopy(buffer, 0, bytes, 0, bytesRead);
			}
		}
		return bytes;
	}
	
	
	public long length() {
		return length;
	}
	public int available() {
		return available;
	}

	public void deleteFile(String nameFile) {
		DeleteFileThread hacer1 = new DeleteFileThread(nameFile);
		hacer1.start();
	}
	public void deleteFile(File file) {
		DeleteFileThread hacer1 = new DeleteFileThread(file);
		hacer1.start();
	}
	
}
