/*
 * DeleteFileThread.java
 *
 * Created on 7 de agosto de 2008, 14:52
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.focus.qwds.ondemand.server.utils;

import java.io.File;


public class DeleteFileThread extends Thread {

	public static String NOMBRE_HILO = "DeleteFileThread:Borrado de archivo";
	private File fichero = null;

	public DeleteFileThread() {
		this.setName(DeleteFileThread.NOMBRE_HILO);
	}

	public DeleteFileThread(String fileName) {
		this.setName(DeleteFileThread.NOMBRE_HILO);
		fichero = new File(fileName);
	}
	
	public DeleteFileThread(File file) {
		this.setName(DeleteFileThread.NOMBRE_HILO);
		fichero = file;
	}
	

	public void run() {
		try {
			if (fichero != null) {
				for (int i = 0; i < 10; i++) {
					// //System.out.println("Eliminando fichero ="+fileName+" /
					// intento numero "+(i+1));
					fichero.delete();
					if (fichero.exists()) {
						// //System.out.println("Todavia existe el fichero
						// (wait)="+fileName);
						Thread.sleep(5 * 1000);
					} else {
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
