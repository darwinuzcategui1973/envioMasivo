package com.desige.webDocuments.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;

import com.desige.webDocuments.utils.Constants;
import com.focus.util.Archivo;

/**
 * Clase para replicar un repositorio no encriptado
 * como su equivalente exacto con archivos encriptados.
 * 
 * Se asume:
 * 
 * 1.- El repositorio de archivos sin encriptar
 * tendra la estructura que se necesita dentro del proyecto Qweb.
 * 
 * 2.- El nombre de los archivos sin encriptar, sera exactamente el mismo
 * que generaria Qweb dentro de su logica (pueden o no tener extension)
 * 
 * @author FJR
 *
 */
public class ReplicateRepository {
	
	/**
	 * 
	 * @param pathArchivosNoProtegidos
	 * @param pathFinalArchivosProtegidos
	 * @param pw
	 */
	public static void executeReplication(String pathArchivosNoProtegidos, String pathFinalArchivosProtegidos, PrintWriter pw){
		try {
			File initialPath = new File(pathArchivosNoProtegidos);
			File finalPath = new File(pathFinalArchivosProtegidos);
			
			//se valida el directorio inicial
			if(!initialPath.exists()){
				throw new Exception("Initial directory: '" + initialPath.getAbsolutePath() + "' was not found.");
			}
			
			//se valida el directorio destino
			if(!finalPath.exists() && !finalPath.mkdirs()){
				throw new Exception("Not able to create: '" + finalPath.getAbsolutePath() + "'.");
			}
			
			//parametros de entrada validados
			iterateOverRepository(initialPath, finalPath, pw);
			
			//abrimos el directorio resultante
			pw.println("Proceso finalizado");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				pw.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}
	
	/**
	 * 
	 * @param fileToCheck: Elemento a procesar (puede representar un directorio o archivo)
	 * @param basePathToWrite: Directorio base para los archivos procesados
	 * @param executionLog: Log para usar como bitacora de avance del proceso.
	 * 
	 */
	private static void iterateOverRepository(File fileToCheck, File basePathToWrite, PrintWriter executionLog) throws Exception{
		if(fileToCheck.isDirectory()){
			executionLog.println("");
			executionLog.println("Directorio '" + fileToCheck.getAbsolutePath());
			
			File[] listOfFiles = fileToCheck.listFiles();
			if(listOfFiles != null){
				for (File file : listOfFiles) {
					iterateOverRepository(file, basePathToWrite, executionLog);
				}
			}
		} else {
			//tenemos un archivo, procesamos su ruta absoluta de destino junto con su nombre
			StringBuilder finalPath = new StringBuilder(basePathToWrite.getAbsolutePath() 
					+ File.separator + fileToCheck.getParentFile().getName() + File.separator);
			if(fileToCheck.getName().contains(".")){
				finalPath.append(fileToCheck.getName().substring(0, fileToCheck.getName().lastIndexOf(".")));
			} else {
				finalPath.append(fileToCheck.getName());
			}
			
			Archivo.writeEncrypt(new FileInputStream(fileToCheck),
					new File(finalPath.toString()),
					Constants.PASS_ENCRYTED);
			
			executionLog.println("Procesado: '" + fileToCheck.getAbsolutePath() + "' -> " + finalPath);
		}
	}
}

