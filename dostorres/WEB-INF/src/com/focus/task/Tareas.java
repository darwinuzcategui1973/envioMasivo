package com.focus.task;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import com.desige.webDocuments.utils.ToolsHTML;


public class Tareas extends Thread {
	
	private static final int segHora = (1000*60)*60;
	private static final int segDia = segHora*24;
	private static final long vigencia = segDia*4;
	private static boolean isStarTomcat = true;
	
	private static Tareas instancia = null;
	
	private Tareas() {
		super("Mantenimiento-Cache");
	}
	
	public static Tareas getInstance() {
		if(instancia==null) {
			instancia = new Tareas();
		}
		return instancia;
	}

	public void run() {
		Calendar cal;
		while(true) {
			try {
				if(isStarTomcat) {
					if(ToolsHTML.getServletContext()!=null) {
						//System.out.println("Ya esta cargado el context, elimino el cache");
						isStarTomcat=false;
						deleteFilesCache(true);
					} else {
						//System.out.println("me duermo unos segundos para verificar que ya esta cargado el context");
						this.sleep(10000); // me duermo unos segundos para verificar que ya esta cargado el context
					}
				} else {
					cal=Calendar.getInstance();
					if(cal.get(Calendar.DAY_OF_WEEK)==6) { //dia="Viernes";
						//System.out.println("Hoy es viernes, a esperar las ocho");
						if(cal.get(Calendar.HOUR)>=20) {
							//System.out.println("Ya son la ocho, a trabajar");
							// Es viernes y son mas de las ocho
							// hora del mantenimiento de los archivos de cache
							deleteFilesCache(false);
						} else {
							this.sleep(segHora); // me duermo una hora para esperar las ocho de la noche
						}
					} else {
						//System.out.println("No es viernes, me duermo");
						this.sleep(segDia); // me duermo un dia completo
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void deleteFilesCache(boolean forzar) {
		File dir = new File(ToolsHTML.getPathCache());
		Date date = new Date();
		if(dir.isDirectory()) {
			File[] lista = dir.listFiles();
			for(int i=0; i<lista.length;i++) {
				if(lista[i].getAbsolutePath().toLowerCase().endsWith(".dat")) {
					//System.out.println(lista[i].getAbsolutePath());
					if(forzar || date.getTime()>(lista[i].lastModified()+Tareas.vigencia)) { 
						//tiene mas de 4 dias se elimina
						//System.out.println("Archivo viejo sera borrado");
						lista[i].delete();
					}
				}
			}
		}
	}
	
	public static void main2(String[] args) {
		File dir = new File("E:/temp/");
		Date date = new Date();
		if(dir.isDirectory()) {
			File[] lista = dir.listFiles();
			for(int i=0; i<lista.length;i++) {
				//System.out.println(lista[i].getAbsolutePath());
				if(date.getTime()>(lista[i].lastModified()+Tareas.vigencia)) { 
					// tiene mas de 4 dias se elimina
					//System.out.println("Archivo viejo sera borrado");
					//lista[i].delete();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		String name = "xxxxx.xxx";
		File f = new File(name);
		StringBuffer cad = new StringBuffer(f.getAbsolutePath().replaceAll(name,""));
		System.out.println(cad);
	}
	
}
