package com.desige.webDocuments.persistent.managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.focus.util.Archivo;

public class PdfSecurityPdftk {

	public static final String BG_NO_CONTROLADA = "img/nocontrolada.pdf";
	public static final String BG_PREVIEW = "img/preview.pdf";

	private final char[] OWNER_PW = { 'q', 'w', 'e', 'b', '0', '1', '2', '3', '4', '5' };
	private String nameFilePdf = null;
	private boolean Printing = false;
	private boolean DegradedPrinting = false;
	private boolean ModifyContents = false;
	private boolean Assembly = false;
	private boolean CopyContents = false;
	private boolean ScreenReaders = false;
	private boolean ModifyAnnotations = false;
	private boolean FillIn = false;
	private boolean AllFeatures = false;

	private ArrayList<String> fileBefore = new ArrayList<String>();
	private ArrayList<String> fileAfter = new ArrayList<String>();

	private String background = null;

	public PdfSecurityPdftk() {
		this.nameFilePdf = "";
		this.setBackground("");
	}

	public PdfSecurityPdftk(String nameFilePdf) {
		this.nameFilePdf = nameFilePdf;
	}

	public String quitarExtension(String name) {
		while (name != null && !name.trim().equals("") && !name.endsWith(".")) {
			name = name.substring(0, name.length() - 1);
		}
		return name;
	}

	public void applySecurity() {
		try {
			boolean correcto = false;

			String ruta = nameFilePdf;
			String rutaTemp = nameFilePdf.concat("_");

			File fichero = new File(ruta);
			File ficheroTemp = null;

			if (fichero.exists()) {

				// ejecutamos la rutina que da seguridad al pdf
				StringBuffer sb = new StringBuffer();
				StringBuffer allow = new StringBuffer();

				// String pdf = quitarExtension(ruta).concat(".pdf");

				// si hay que sumar pdfs rlo hacemos primero
				sb.append("pdftk ");

				if (getFileBefore().size() > 0) {
					for (int i = 0; i < getFileBefore().size(); i++) {
						sb.append("\"");
						sb.append(getFileBefore().get(i));
						sb.append("\" ");
					}
				}

				sb.append("\"");
				sb.append(ruta);
				sb.append("\" ");

				if (getFileAfter().size() > 0) {
					for (int i = 0; i < getFileAfter().size(); i++) {
						sb.append("\"");
						sb.append(getFileAfter().get(i));
						sb.append("\" ");
					}
				}

				if (getFileBefore().size() > 0 || getFileAfter().size() > 0) {
					sb.append("cat ");
				}

				sb.append(" output \"");
				sb.append(rutaTemp);
				sb.append("\" ");
				
				//System.out.println(sb.toString());

				run(sb.toString());

				ficheroTemp = new File(rutaTemp);

				//System.out.println("Fue creado el archivo = "+rutaTemp+" : Cierto ="+ficheroTemp.exists());

				if (fichero.delete()) {
					//System.out.println("Borramos el original");
				} else {
					//System.out.println("No borramos el original");
				}

				// borramos los archivos que concatenamos si los hay
				try {
					if (getFileBefore().size() > 0) {
						File borrar;
						for (int i = 0; i < getFileBefore().size(); i++) {
							borrar = new File(String.valueOf(getFileBefore().get(i)));
							if (borrar.exists()) {
								borrar.delete();
							}
						}
					}
					if (getFileAfter().size() > 0) {
						File borrar;
						for (int i = 0; i < getFileAfter().size(); i++) {
							borrar = new File(String.valueOf(getFileAfter().get(i)));
							if (borrar.exists()) {
								borrar.delete();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				//System.out.println("ficheroTemp ="+String.valueOf(ficheroTemp.exists())+" "+rutaTemp);
				//System.out.println("fichero ="+String.valueOf(fichero.exists()));
				if (PdfSecurity.renameTo(ficheroTemp, fichero)) {
					//System.out.println("Renombramos el temporal para original");
				} else {
					//System.out.println("No se puede renombrar el temporal para original");
				}

				// aplicamos la seguridad al documento
				sb.setLength(0);
				sb.append("pdftk ");
				sb.append("\"");
				sb.append(ruta);
				sb.append("\" ");

				if (background != null && !background.trim().equals("")) {
					File fondo = new File(getBackground());
					if (fondo.exists()) {
						sb.append("background \"");
						sb.append(getBackground().replace('/', File.separatorChar));
						sb.append("\"");
					}
				}

				sb.append(" output \"");
				sb.append(rutaTemp);
				sb.append("\" ");

				// coloca la clave
				sb.append(" owner_pw ");
				sb.append(OWNER_PW);

				// no pregunta
				sb.append(" dontask ");

				// aqui otorgamos los permisos
				allow.append("allow ");
				if (isPrinting())
					allow.append("Printing ");
				if (isDegradedPrinting())
					allow.append("DegradedPrinting ");
				if (isModifyContents())
					allow.append("ModifyContents ");
				if (isAssembly())
					allow.append("Assembly ");
				if (isCopyContents())
					allow.append("CopyContents ");
				if (isScreenReaders())
					allow.append("ScreenReaders ");
				if (isModifyAnnotations())
					allow.append("ModifyAnnotations ");
				if (isFillIn())
					allow.append("FillIn ");
				if (isAllFeatures())
					allow.append("AllFeatures ");

				if (allow.toString().trim().equals("allow")) {
					allow.setLength(0);
				}

				sb.append(allow);

				//System.out.println(sb.toString());

				run(sb.toString());

				// borramos el archivo.
				if (fichero.delete()) {
					//System.out.println("El borrado ha sido correcto");
				} else {
					//System.out.println("El borrado no se ha podido realizar");
				}

				// renombramos el archivo.
				// fichero = new File(ruta);
				ficheroTemp = new File(rutaTemp);

				//System.out.println("ficheroTemp ="+String.valueOf(ficheroTemp.exists()));
				//System.out.println("fichero ="+String.valueOf(fichero.exists()));
				if (PdfSecurity.renameTo(ficheroTemp, fichero)) {
					//System.out.println("El renombrado ha sido correcto");
				}else{
					//System.out.println("El renombrado no se ha podido realizar ficheroTemp="+ficheroTemp.exists()+" - "+fichero.exists());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		PdfSecurity pdf = new PdfSecurity();
		//pdf.setNameFilePdf("c:/pdf/chao.pdf");
		//pdf.setBackground("C:/tempoffice/marca.pdf");
		pdf.setNameFilePdf("/home/ubuntu/appdownload/pdf/chao.pdf");
		pdf.setBackground("/home/ubuntu/appdownload/pdf/marca.pdf");
		// pdf.setPrinting(true);
		pdf.applySecurity();
		System.exit(0);
	}

	public synchronized void run(String comando) throws Exception {
		Runtime r = Runtime.getRuntime();
		Process p = null;
	    String nameFile = null;
		/* para ejecutar el comando debemos crear un archivo que ejecute el comando en linux */
		if (!System.getProperty("os.name").startsWith("Windows")) {
			Archivo script = new Archivo();
			nameFile = System.getProperty("user.dir").concat(File.separator).concat("pdftk.sh");
			//r.exec("rm -f ".concat(nameFile));  // borramos el archivo si existe
			script.escribir(nameFile, comando);  // creamos el nuevo archivo con el comando
			r.exec("chmod 755 ".concat(nameFile)); // asignamos permiso de ejecucion al archivo
			comando = nameFile; // ahora solo ejecutamos el archivo .sh generado 
		}
		
		//System.out.println(comando);
		p = r.exec(comando);  // ejecutamos el comando
		
		
		// Se obtiene el stream de salida del programa
		InputStream is = p.getInputStream();
		// Se prepara un bufferedReader para poder leer la salida más comodamente.
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		// Se lee la primera linea
		String aux = br.readLine();
		// Mientras se haya leido alguna linea
		while (aux != null) {
			// Se escribe la linea en pantalla
			//System.out.println(aux);
			// y se lee la siguiente.
			aux = br.readLine();
		}
		//if (!System.getProperty("os.name").startsWith("Windows")) {
			//r.exec("rm -f ".concat(nameFile));  // borramos el archivo
		//}
	}

	// renombrar un fichero
	public static boolean renameTo(File source, File destination) {
		if (!destination.exists()) {
			// intentamos con renameTo
			boolean result = source.renameTo(destination);
			if (!result) {
				// intentamos copiar
				result = true;
				result &= copiar(source, destination);
				result &= source.delete();
			}
			return (result);
		} else {
			// Si el fichero destination existe, cancelamos...
			return (false);
		}
	}

	/**
	 * copia el fichero source en el fichero destination devuelve true si funciona correctamente
	 */
	public static boolean copiar(File source, File destination) {
		boolean resultado = false;
		// declaración del flujo
		java.io.FileInputStream sourceFile = null;
		java.io.FileOutputStream destinationFile = null;
		try {
			// creamos fichero
			destination.createNewFile();
			// abrimos flujo
			sourceFile = new java.io.FileInputStream(source);
			destinationFile = new java.io.FileOutputStream(destination);
			// lectura por segmentos de 0.5Mb
			byte buffer[] = new byte[512 * 1024];
			int nbLectura;
			while ((nbLectura = sourceFile.read(buffer)) != -1) {
				destinationFile.write(buffer, 0, nbLectura);
			}
			// copia correcta
			resultado = true;
		} catch (java.io.FileNotFoundException f) {
		} catch (java.io.IOException e) {
		} finally {
			// pase lo que pase, cerramos flujo
			try {
				sourceFile.close();
			} catch (Exception e) {
			}
			try {
				destinationFile.close();
			} catch (Exception e) {
			}
		}
		return (resultado);
	}

	// getters and setters

	public boolean isAllFeatures() {
		return AllFeatures;
	}

	public void setAllFeatures(boolean allFeatures) {
		AllFeatures = allFeatures;
	}

	public boolean isAssembly() {
		return Assembly;
	}

	public void setAssembly(boolean assembly) {
		Assembly = assembly;
	}

	public boolean isCopyContents() {
		return CopyContents;
	}

	public void setCopyContents(boolean copyContents) {
		CopyContents = copyContents;
	}

	public boolean isDegradedPrinting() {
		return DegradedPrinting;
	}

	public void setDegradedPrinting(boolean degradedPrinting) {
		DegradedPrinting = degradedPrinting;
	}

	public boolean isFillIn() {
		return FillIn;
	}

	public void setFillIn(boolean fillIn) {
		FillIn = fillIn;
	}

	public boolean isModifyAnnotations() {
		return ModifyAnnotations;
	}

	public void setModifyAnnotations(boolean modifyAnnotations) {
		ModifyAnnotations = modifyAnnotations;
	}

	public boolean isModifyContents() {
		return ModifyContents;
	}

	public void setModifyContents(boolean modifyContents) {
		ModifyContents = modifyContents;
	}

	public boolean isPrinting() {
		return Printing;
	}

	public void setPrinting(boolean printing) {
		Printing = printing;
	}

	public boolean isScreenReaders() {
		return ScreenReaders;
	}

	public void setScreenReaders(boolean screenReaders) {
		ScreenReaders = screenReaders;
	}

	public String getNameFilePdf() {
		return nameFilePdf;
	}

	public void setNameFilePdf(String nameFilePdf) {
		this.nameFilePdf = nameFilePdf;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String path, String background) {
		this.background = path.concat(File.separator).concat(background).replace('/', File.separatorChar);
	}

	public void setBackground(String background) {
		this.background = background.replace('/', File.separatorChar);
	}

	public ArrayList getFileAfter() {
		return fileAfter;
	}

	public void setFileAfter(ArrayList fileAfter) {
		this.fileAfter = fileAfter;
	}

	public ArrayList getFileBefore() {
		return fileBefore;
	}

	public void setFileBefore(ArrayList fileBefore) {
		this.fileBefore = fileBefore;
	}

	public void addFileBefore(String nameFile) {
		fileBefore.add(nameFile);
	}

	public void addFileAfter(String nameFile) {
		fileAfter.add(nameFile);
	}

}
