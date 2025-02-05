package com.desige.webDocuments.persistent.managers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZip {
	static final int BUFFER = 2048;

	public static void main(String argv[]) {
		try {
			BufferedOutputStream dest = null;
			FileInputStream fis = new FileInputStream(argv[0]);
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				//System.out.println("Extracting: " + entry);
				int count;
				byte data[] = new byte[BUFFER];
				// Escribir los archivos en el disco
				FileOutputStream fos = new FileOutputStream(entry.getName());
				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
			zis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//public ProcesoRecuperacion(ZipInputStream zipInputStream) throws IOException {
	//	byte[] bytes = readBytes(zipInputStream);
	//}

	//byte[] bytes = readBytes(zipInputStream);

	private byte[] readBytes(InputStream inputStream) throws IOException {
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
}
