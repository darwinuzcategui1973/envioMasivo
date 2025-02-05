package com.focus.filesystem;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.swing.JOptionPane;

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
	private static final String PATH_ARCHIVOS_NO_PROTEGIDOS = "C:\\qweb\\repositorios\\convertir";
	
	
	public static void main(String[] args) {
		PrintWriter pw = null;
		
		try {
			File initialPath = new File(PATH_ARCHIVOS_NO_PROTEGIDOS);
			File finalPath = new File(initialPath.getParent() + File.separator + "encriptados");
			
			//se valida el directorio inicial
			if(!initialPath.exists()){
				JOptionPane.showMessageDialog(null,
						"El directorio indicado '" + initialPath.getAbsolutePath() + "' no fue encontrado."
						+ "\nAjuste la ruta e intente de nuevo.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				
				throw new Exception("Initial directory: '" + initialPath.getAbsolutePath() + "' was not found.");
			}
			
			//se valida el directorio destino
			if(!finalPath.exists() && !finalPath.mkdirs()){
				JOptionPane.showMessageDialog(null,
						"El directorio '" + finalPath.getAbsolutePath() + "' no pudo ser creado."
						+ "\nVerifique la permisologia o intente crearlo manualmente e intente de nuevo.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				
				throw new Exception("Not able to create: '" + finalPath.getAbsolutePath() + "'.");
			}
			
			//creamos el archivo de salida para logs
			String executionLogPath = initialPath.getParent() + File.separator 
					+ "run_" + Calendar.getInstance().getTimeInMillis() + ".txt";
			pw = new PrintWriter(executionLogPath);
			
			//parametros de entrada validados
			iterateOverRepository(initialPath, finalPath, pw);
			
			//abrimos el directorio resultante
			JOptionPane.showMessageDialog(null,
					"El proceso finalizo con aparente exito."
						+ "\nAl cerrar esta ventana vera los resultados."
						+ "\nEl archivo con el log de ejecucion tambien sera abierto automaticamemte",
					"Proceso Finalizado",
					JOptionPane.INFORMATION_MESSAGE);
			
			Desktop.getDesktop().open(initialPath.getParentFile());
			Desktop.getDesktop().open(new File(executionLogPath));
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
			executionLog.println("Entrando a directorio '" + fileToCheck.getAbsolutePath());
			
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
