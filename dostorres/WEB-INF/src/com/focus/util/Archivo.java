package com.focus.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.persistent.managers.PdfConvert;
import com.desige.webDocuments.util.EncryptorMD5;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.StringUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qwds.ondemand.server.excepciones.ErrorDeAplicaqcion;

public class Archivo {
	private static final Logger log = LoggerFactory.getLogger(Archivo.class);

	public static final int BUFFER_SIZE = 8192;
	// public static final String CACHE_DOBLE_VERSION_SUFIJO = ""; //_lastcachedobleversion";

	/***
	 * 
	 */
	public Archivo() {

	}

	/**
	 * 
	 * @param nameFile
	 * @return
	 */
	public ArrayList<String> leerPorLinea(String nameFile) {
		ArrayList<String> lista = new ArrayList<String>();

		File f = new File(nameFile);
		BufferedReader entrada = null;
		try {
			entrada = new BufferedReader(new FileReader(f));
			String linea;
			while (entrada.ready()) {
				linea = entrada.readLine();
				// //System.out.println(linea);
				if (linea != null && !linea.trim().equals(""))
					lista.add(linea);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(entrada);
		}

		return lista;
	}

	/**
	 * 
	 * @param nameFile
	 * @return
	 */
	public StringBuffer leer(String nameFile) {
		StringBuffer data = new StringBuffer();
		try {
			File inputFile = new File(nameFile.replace('/', File.separatorChar));
			// Creamos entradas y salidas por c�nsola
			FileInputStream fis = new FileInputStream(inputFile);
			int c;
			while ((c = fis.read()) != -1) {
				data.append((char) c);
			}
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * 
	 * @param nameFileOutput
	 * @param contenido
	 * @return
	 */
	public boolean escribir(String nameFileOutput, String contenido) {
		boolean existe = false;

		try {
			File outputFile = new File(nameFileOutput.replace('/', File.separatorChar));
			FileOutputStream fos = new FileOutputStream(outputFile);

			char[] data = contenido.toCharArray();

			for (int i = 0; i < data.length; i++) {
				fos.write(data[i]);
			}
			fos.close();// importante , no dejarse abierto
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return existe;
	}

	public boolean escribir(String nameFileOutput, String contenido, String charSet) {
		boolean existe = false;

		try {
			File outputFile = new File(nameFileOutput.replace('/', File.separatorChar));
			FileOutputStream fos = new FileOutputStream(outputFile);

			byte[] data = contenido.getBytes(charSet);

			for (int i = 0; i < data.length; i++) {
				fos.write(data[i]);
			}
			fos.close();// importante , no dejarse abierto
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return existe;
	}

	/**
	 * 
	 * @param args
	 */
	public static void mainDePruebaGeneralLecturaEscritura(String[] args) {
		try {
			File inputFile = new File("original.txt");
			File outputFile = new File("outagain.txt");
			// Creamos entradas y salidas por c�nsola
			FileInputStream fis = new FileInputStream(inputFile);
			FileOutputStream fos = new FileOutputStream(outputFile);
			int c;
			// Mientras el valor del m�todo read() del objeto fis sea != -1 -->
			// ejecuta metodo
			// write del objeto fos
			// traduciendo: mientras no termine de leer el fichero
			// inputfile, copialo ( y si no existe lo crea y si existe lo
			// sobreescribe)
			// en el fichero outputfile

			while ((c = fis.read()) != -1) {
				// lee byte a byte de fis y lo vuelca en fos
				fos.write(c);
			}
			// en realidad trabaja entre la ram(FileInputStream y
			// FileOutputStream) y el HD
			// (inputFile , outputFile)
			fis.close();
			fos.close();// importante , no dejarse abierto canales
		} catch (FileNotFoundException e) {
			// la excepci�n provendria de no encontrar original.txt
			// originada en la linea FileInputStream fis = new
			// FileInputStream(inputFile);
			// java exige recoger la excepcion al usar este canal ( try{..}
			// catch{..} )
			// el fichero de salida no genera excepci�n , ya que se va a crear o
			// sobreescribir
			System.err.println("FileStreamsTest: " + e);
		} catch (IOException e) {
			// excepci�n m�s gen�rica de entrada / salida
			System.err.println("FileStreamsTest: " + e);
		}
	}

	// public static void main(String[] args) {
	// Archivo a = new Archivo();
	// String sb = new String();
	//
	// sb = a.leer("C:/appworkspace48/qwebds4/inicio.html").toString();
	//
	// sb = sb.replaceAll("---LOGO---", "/img/logos/empresa.gif");
	// sb = sb.replaceAll("---ENCABEZADO1---", "FORMATO DE IMPRESION");
	//
	// //System.out.println(sb.toString());
	//
	// a.escribir("c:/pdf/salida2.html", sb.toString());
	//
	// }

	/**
	 * 
	 * @param nameFile
	 * @param response
	 * @return
	 */
	public boolean leerBinary(String nameFile, HttpServletResponse response) {
		StringBuffer data = new StringBuffer();
		FileInputStream fis = null;
		try {
			File inputFile = new File(nameFile.replace('/', File.separatorChar));
			// Creamos entradas y salidas por c�nsola
			fis = new FileInputStream(inputFile);
			int c;
			ServletOutputStream out = response.getOutputStream();
			while ((c = fis.read()) != -1) {
				out.write(c);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @param file
	 */
	public static void deleteFile(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			int i = files.length;
			for (int i_2_ = 0; i_2_ < i; i_2_++) {
				File file_3_ = files[i_2_];
				deleteFile(file_3_);
			}
		}
		file.delete();
	}

	/**
	 * Verificamos si debemos esperar a que el archivo sea escrito o si ya el mismo dejo de ser modificado
	 * 
	 * @param nameFile
	 * @param seconds
	 *            , cantidad de segundos a esperar
	 */
	public static void waitCreationFile(String nameFile, int seconds) {
		// Esperamos almenos 20 para que el archivo pdf sea creado
		File arc = new File(nameFile);

		// para forzar siempre al menos la espera de un segundo
		long size = arc.length();

		int maxSeconds = 10;

		// for (int xx = 0; (!arc.exists() || !arc.canWrite()) && xx < seconds;
		// xx++) {
		for (int xx = 0; xx < seconds; xx++) {
			Wait.middleSec(); // esperamos medio segundo

			arc = new File(nameFile);

			if (!arc.exists()) {
				continue;
			}

			if ((xx + 1) >= maxSeconds) {
				// llegamos a la maxima espera

				if (size == 0) {
					// el archivo sigue estando en 0 bites, suspendemos el
					// proceso de espera
					break;
				} else if (arc.length() == size) {
					// finalizo la espera y el tama�o escrito es el mismo de
					// hace unos segundos
					// asumimos que termino la escritura
					log.info("Finalizando con size=[" + arc.length() + "]");
					break;
				}
			} else {
				if ((size > 0) && (arc.length() == size)) {
					// el size del archivo es el mismo de hace 2 segundos
					// asumimos que se finalizo su escritura
					log.info("Finalizando con size=[" + arc.length() + "]");
					break;
				}
			}

			// el archivo aun sigue siendo modificado
			log.info("Sizes -> [actual: " + arc.length() + "/ anterior: " + size + "] Waiting one second more for file create... " + xx);
			size = arc.length();

			// veo si esta ser�a la ultima iteracion, para darle un segundo mas
			// a este proceso
			if ((xx + 1) == (seconds - 1)) {
				seconds++;
			}

			System.gc();
		}

		arc = null;
	}

	/**
	 * 
	 * @param in
	 * @param out
	 */
	public static void copiar(String in, String out) {
		FileInputStream fIn;
		FileOutputStream fOut;
		FileChannel fIChan, fOChan;
		long fSize;
		MappedByteBuffer mBuf;

		try {
			File f = new File(in);
			if (!f.exists())
				return;

			f = new File(out);
			if (f.exists()) {
				System.gc();
				f.delete();
			}
			// fIn = new FileInputStream(args[0]);
			// fOut = new FileOutputStream(args[1]);
			fIn = new FileInputStream(in);
			fOut = new FileOutputStream(out);

			fIChan = fIn.getChannel();
			fOChan = fOut.getChannel();

			fSize = fIChan.size();

			mBuf = fIChan.map(FileChannel.MapMode.READ_ONLY, 0, fSize);

			fOChan.write(mBuf); // this copies the file

			fIChan.close();
			fIn.close();

			fOChan.close();
			fOut.close();
		} catch (IOException exc) {
			// System.out.println("copy1 " + in + " " + out);
			exc.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException exc) {
			// System.out.println("copy2 " + in + " " + out);
			exc.printStackTrace();
		}
	}

	/**
	 * copia un InputStream a un archivo en disco
	 * 
	 */
	public static void copiar(InputStream in, File out) {
		FileInputStream fIn;
		FileOutputStream fOut;
		FileChannel fIChan, fOChan;
		long fSize;
		MappedByteBuffer mBuf;

		try {
			if (out.exists()) {
				System.gc();
				out.delete();
			}

			// crea el directorio si no existe
			File carpeta = new File(out.getParent());
			if (!carpeta.exists()) {
				carpeta.mkdir();
			}

			fIn = (FileInputStream) in;
			fOut = new FileOutputStream(out);

			fIChan = fIn.getChannel();
			fOChan = fOut.getChannel();

			fSize = fIChan.size();

			mBuf = fIChan.map(FileChannel.MapMode.READ_ONLY, 0, fSize);

			fOChan.write(mBuf); // this copies the file

			// fIChan.close(); // no cerramos el input ya que se utilizara luego
			// fIn.close(); // no cerramos el input ya que se utilizara luego

			fOChan.close();
			fOut.close();
		} catch (IOException exc) {
			// System.out.println("copy1 " + in + " " + out.getAbsolutePath());
			exc.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException exc) {
			// System.out.println("copy2 " + in + " " + out.getAbsolutePath());
			exc.printStackTrace();
		}
	}

	/**
	 * 
	 * @param srFile
	 * @param dtFile
	 */
	public static synchronized void copyfile(String srFile, String dtFile) {
		try {
			System.out.println("Archivo: Copiando archivo ".concat(srFile).concat(" a ").concat(dtFile));
			File f1 = new File(srFile);
			File f2 = new File(dtFile);

			if (f2.exists()) {
				System.gc();
				f2.delete();
			}

			// crea el directorio si no existe
			File carpeta = new File(f2.getParent());
			if (!carpeta.exists()) {
				carpeta.mkdir();
			}

			InputStream in = new FileInputStream(f1);

			// For Append the file.
			// OutputStream out = new FileOutputStream(f2,true);

			// For Overwrite the file.
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			// //System.out.println("File copied.");
			log.info("File " + f2.getAbsolutePath() + " was copied.");
		} catch (FileNotFoundException ex) {
			// System.out.println(ex.getMessage() +
			// " in the specified directory.");
			ex.printStackTrace();
		} catch (IOException e) {
			// System.out.println(e.getMessage());
		}
	}

	/**
	 * 
	 * @param in
	 * @param f2
	 */
	public static void copyfile(InputStream in, File f2) {
		try {

			if (f2.exists()) {
				System.gc();
				f2.delete();
			}

			// crea el directorio si no existe
			File carpeta = new File(f2.getParent());
			if (!carpeta.exists()) {
				carpeta.mkdir();
			}

			f2 = new File(f2.getAbsolutePath());
			// For Append the file.
			// OutputStream out = new FileOutputStream(f2,true);

			// For Overwrite the file.
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			// System.out.println("File copied.");
			log.info("File " + f2.getAbsolutePath() + " was copied.");
		} catch (FileNotFoundException ex) {
			// System.out.println(ex.getMessage() +
			// " in the specified directory.");
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// conversion de pdf
	public static void writeDocumentToPdfDisk(File fuente, String nombreTabla, int idDocument, InputStream in) throws Exception {
		writeDocumentToPdfDisk(null, fuente, nombreTabla, idDocument, in);
	}

	public static void writeDocumentToPdfDisk(Connection con, File fuente, String nombreTabla, int idDocument, InputStream in) throws Exception {
		// writeDocumentToDisk(null, nombreTabla, idDocument, inputStream);
		// }
		// public static void writePdfEncrypt(InputStream in, File out, int
		// pass, String extFile, File outPdf) throws Exception {

		if (!isPDF(in)) {
			PdfConvert pdfConvert = new PdfConvert();
			String ruta = fuente.getAbsolutePath();
			String rutaPdf = pdfConvert.saveToPdf(con, ruta, idDocument, false);
			File pdf = new File(rutaPdf);
			FileInputStream inPdf = new FileInputStream(pdf);

			File out = new File(getNameFileEncripted(con, nombreTabla, idDocument, null));
			writeEncrypt(inPdf, out, Constants.PASS_ENCRYTED);
			if (pdf.exists()) {
				System.gc();
				pdf.delete();
			}
		}

	}

	public static void writeEncrypt(InputStream in, File out, int pass) throws IOException {

		DataInputStream fileIn = null;
		DataOutputStream fileOut = null;

		/**
		 * Como voy a leer de un archivo binario archivo tengo que utilizar un try a pesar de que aviento IOException y FileNotFoundException
		 */
		try {
			System.out.println("Generando archivo " + out.getParent());
			File carpeta = new File(out.getParent());

			if (out.exists()) {
				System.gc();
				FileUtils.forceDelete(out);
			}

			if (!carpeta.exists()) {
				carpeta.mkdir();
			}

			// Creo los objetos con los que voy a leer
			fileIn = new DataInputStream(in);
			fileOut = new DataOutputStream(new FileOutputStream(out));

			byte[] bytes = new byte[fileIn.available()];

			byte[] buf = new byte[BUFFER_SIZE];
			int bytesRead = 0;

			while (true) {
				int read = fileIn.read(buf);
				if (read == -1) {
					break;
				} else if (read > 0) {
					bytes = addBytesEncript(bytes, bytesRead, buf, pass);
					// System.out.println("cuanto vale read " + read);
					bytesRead += read;
				}
			}

			fileOut.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fileIn.close();
			fileOut.close();
			log.info("Archivo escrito en: " + out.getAbsolutePath());
		}
	}

	/**
	 * Escribe el archivo directamente al sitio destino sin encriptarlo o desencriptarlo
	 * @param in flujo de bytes
	 * @param out archivo de destino donde se escribiran los bytes
	 * @throws IOException
	 */
	public static void writeEncriptDirectly(InputStream in, File out) throws IOException {

		DataInputStream fileIn = null;
		DataOutputStream fileOut = null;

		/**
		 * Como voy a leer de un archivo binario archivo tengo que utilizar un try a pesar de que aviento IOException y FileNotFoundException
		 */
		try {
			System.out.println("Generando archivo " + out.getParent());
			File carpeta = new File(out.getParent());

			if (out.exists()) {
				System.gc();
				FileUtils.forceDelete(out);
			}

			if (!carpeta.exists()) {
				carpeta.mkdir();
			}

			// Creo los objetos con los que voy a leer
			fileIn = new DataInputStream(in);
			fileOut = new DataOutputStream(new FileOutputStream(out));

			byte[] bytes = new byte[fileIn.available()];

			byte[] buf = new byte[BUFFER_SIZE];
			int bytesRead = 0;

			while (true) {
				int read = fileIn.read(buf);
				if (read == -1) {
					break;
				} else if (read > 0) {
					bytes = addBytesEncript(bytes, bytesRead, buf,Constants.PASS_ENCRYTED);
					// bytes = addBytesEncriptDirectly(bytes, bytesRead, buf);
					bytesRead += read;
				}
			}

			fileOut.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fileIn.close();
			fileOut.close();
			log.info("Archivo escrito en: " + out.getAbsolutePath());
		}
	}

	public static InputStream readEncryptDirectly(File in) throws IOException {

		DataInputStream fileIn = null;
		InputStream out = null;
		byte[] bytes = null;

		/**
		 * Como voy a leer de un archivo binario archivo tengo que utilizar un try a pesar de que aviento IOException y FileNotFoundException
		 */
		try {
			if (!in.exists()) {
				return null;
			}

			out = new FileInputStream(in);
			fileIn = new DataInputStream(out);

			bytes = new byte[out.available()];

			byte[] buf = new byte[BUFFER_SIZE];
			int bytesRead = 0;

			while (true) {
				int read = fileIn.read(buf);
				if (read == -1) {
					break;
				} else if (read > 0) {
					bytes = addBytesEncriptDirectly(bytes, bytesRead, buf);
					bytesRead += read;
				}
			}

			out = new ByteArrayInputStream(bytes);

			bytes = null;
			System.gc();
		} catch (EOFException eof) {
			//
		} finally {
			if (fileIn != null) {
				fileIn.close();
			}
		}
		return out;
	}

	public static InputStream readEncrypt(File in, int pass) throws IOException {

		DataInputStream fileIn = null;
		InputStream out = null;
		byte[] bytes = null;

		/**
		 * Como voy a leer de un archivo binario archivo tengo que utilizar un try a pesar de que aviento IOException y FileNotFoundException
		 */
		log.info(in.getAbsoluteFile().getAbsolutePath());
		try {
			if (!in.exists()) {
				return null;
			}

			out = new FileInputStream(in);
			fileIn = new DataInputStream(out);

			bytes = new byte[out.available()];

			byte[] buf = new byte[BUFFER_SIZE];
			int bytesRead = 0;

			while (true) {
				int read = fileIn.read(buf);
				if (read == -1) {
					break;
				} else if (read > 0) {
					bytes = addBytesEncript(bytes, bytesRead, buf, pass);
					bytesRead += read;
				}
			}

			out = new ByteArrayInputStream(bytes);

			bytes = null;
			System.gc();
		} catch (EOFException eof) {
			//
		} finally {
			if (fileIn != null) {
				fileIn.close();
			}
		}
		return out;
	}

	public static byte[] addBytesEncript(byte[] arreglo, int inicio, byte[] nuevo, int pass) {

		int arr = arreglo.length;
		
		for (int i = 0; i < nuevo.length && inicio < arr; i++) {
			arreglo[inicio++] = (byte) (nuevo[i] ^ pass);
		}
		return arreglo;
	}

	private static byte[] addBytesEncriptDirectly(byte[] arreglo, int inicio, byte[] nuevo) {

		int arr = arreglo.length;
		for (int i = 0; i < nuevo.length && inicio < arr; i++) {
			arreglo[inicio++] = (byte) (nuevo[i]);
		}
		return arreglo;
	}

	public static void writeFile(InputStream in, File out) throws IOException {

		DataInputStream fileIn = null;
		DataOutputStream fileOut = null;

		/**
		 * Como voy a leer de un archivo binario archivo tengo que utilizar un try a pesar de que aviento IOException y FileNotFoundException
		 */
		try {
			File carpeta = new File(out.getParent());

			if (out.exists()) {
				System.gc();
				out.delete();
			}

			if (!carpeta.exists()) {
				carpeta.mkdir();
			}

			// Creo los objetos con los que voy a leer
			fileIn = new DataInputStream(in);
			fileOut = new DataOutputStream(new FileOutputStream(out));

			byte[] bytes = new byte[fileIn.available()];

			// Utilizo un loop infinito para leer todo el archivo // muy lento
			// escribir directo a disco
			// while (true) {
			// escribo a fileOut
			// fileOut.write(fileIn.readByte() ^ pass);
			// }

			for (int i = 0; i < bytes.length; i++) {
				bytes[i] = (byte) (fileIn.readByte());
			}

			fileOut.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fileIn.close();
			fileOut.close();
		}
	}

	public static byte[] getBytes(File file) throws IOException {
		// Creo un FIS para leer el documento y extraerle los bytes
		FileInputStream fis = new FileInputStream(file);

		// Bytes del archivo
		int bytes = fis.available();

		// Buffer donde se van a guardar los datos del archivo
		byte[] newFile = new byte[bytes];

		// leer el archivo y guardar sus bytes en el arreglo.
		fis.read(newFile);

		// Cerrar el FIS para liberar el archivo y los recursos
		fis.close();

		return newFile;
	}

	public static boolean delete(File file) {
		System.gc();
		return file.delete();

	}

	// lee el documento desde el disco
	public static InputStream readDocumentFromDisk(String nombreTabla, int idDocument) throws NoSuchAlgorithmException, IOException, ErrorDeAplicaqcion {
		return readDocumentFromDisk(null, nombreTabla, idDocument);
	}

	public static InputStream readDocumentFromDisk(Connection con, String nombreTabla, int idDocument)
			throws NoSuchAlgorithmException, IOException, ErrorDeAplicaqcion {
		File fichero = new File(getNameFileEncripted(nombreTabla, idDocument, null));
		System.out.println("Documento:");
		System.out.println(nombreTabla);
		System.out.println(idDocument);
		System.out.println(fichero.getAbsolutePath());
		System.out.println(fichero.getName());

		return readEncrypt(fichero, Constants.PASS_ENCRYTED);
	}

	public static InputStream readDocumentFromDiskDirectly(Connection con, String nombreTabla, int idDocument)
			throws NoSuchAlgorithmException, IOException, ErrorDeAplicaqcion {
		File fichero = new File(getNameFileEncripted(nombreTabla, idDocument, null));
		System.out.println("Documento leido encriptado:");
		System.out.println(nombreTabla);
		System.out.println(idDocument);
		System.out.println(fichero.getAbsolutePath());
		System.out.println(fichero.getName());

		return readEncryptDirectly(fichero);
	}

	// escribe el documento en el disco - Es para el proceso de vaciado de la
	// base de datos
	public static synchronized void writeDocumentToDisk(String nombreTabla, int idDocument, InputStream inputStream, String path)
			throws NoSuchAlgorithmException, IOException, ErrorDeAplicaqcion {
		File out = new File(getNameFileEncripted(nombreTabla, idDocument, path));
		writeEncrypt(inputStream, out, Constants.PASS_ENCRYTED);
	}

	// para el ondemand
	public static synchronized void writeDocumentToDisk(String nombreTabla, int idDocument, InputStream inputStream)
			throws NoSuchAlgorithmException, IOException, ErrorDeAplicaqcion {
		writeDocumentToDisk(null, nombreTabla, idDocument, inputStream);
	}

	// para el ondemand
	public static synchronized void writeDocumentDigitalToDisk(InputStream inputStream, String nameFile) throws IOException, ErrorDeAplicaqcion {

		File file = new File(ToolsHTML.getPathTmp().concat(nameFile));
		write(inputStream, file);
	}

	public static synchronized void writeDocumentToDisk(InputStream inputStream, String nameFile) throws IOException, ErrorDeAplicaqcion {

		File file = new File(nameFile);
		write(inputStream, file);
	}
	public static synchronized void writeDocumentToDisk(String nombreArchivo, String nombreTabla, int idDocument, InputStream inputStream)
			throws NoSuchAlgorithmException, IOException, ErrorDeAplicaqcion {
		writeDocumentToDisk(null, nombreArchivo, nombreTabla, idDocument, inputStream);
	}

	public static synchronized void writeDocumentToDisk(Connection con, String nombreArchivo, String nombreTabla, int idDocument, InputStream inputStream)
			throws NoSuchAlgorithmException, IOException, ErrorDeAplicaqcion {
		File out = new File(getNameFileEncripted(con, nombreTabla, idDocument, null));
		writeEncrypt(inputStream, out, Constants.PASS_ENCRYTED);
	}

	// copia un archivo de una tabla a otra (de un directorio a otro);
	public static synchronized void copyDocumentInDisk(String nombreTablaOrigen, int idDocumentOri, String nombreTablaDestino, int idDocumentDestino)
			throws NoSuchAlgorithmException, IOException, ErrorDeAplicaqcion {
		String nameFileOrigen = getNameFileEncripted(nombreTablaOrigen, idDocumentOri, null);
		copyfile(nameFileOrigen, getNameFileEncripted(nombreTablaDestino, idDocumentDestino, null));
	}

	// copia un archivo de una tabla a otra (de un directorio a otro);
	public static synchronized void copyDocumentInDiskFromDocCheckout(String nombreTablaOrigen, int idDocumentOri, String nombreTablaDestino, int idDocumentDestino)
			throws NoSuchAlgorithmException, IOException, ErrorDeAplicaqcion {
		String nameFileOrigen = getNameFileEncriptedCheckout(nombreTablaOrigen, idDocumentOri);
		copyfile(nameFileOrigen, getNameFileEncripted(nombreTablaDestino, idDocumentDestino, null));
	}

	
	// copia un archivo de una tabla a otra (de un directorio a otro);
	public static synchronized void copyDocumentInDisk(Connection con, String nombreTablaOrigen, int idDocumentOri, String nombreTablaDestino,
			int idDocumentDestino) throws NoSuchAlgorithmException, IOException, ErrorDeAplicaqcion {
		copyfile(getNameFileEncripted(con, nombreTablaOrigen, idDocumentOri, null), getNameFileEncripted(con, nombreTablaDestino, idDocumentDestino, null));
	}

	// elimina un archivo del repositorio
	public static synchronized void deleteDocumentCachedInDisk(int idDocument) throws NoSuchAlgorithmException, IOException, ErrorDeAplicaqcion {
		File out = new File(getNameFileEncripted("versiondocview", idDocument, null));
		System.out.println(out.getAbsolutePath());
		if (out.exists()) {
			out.delete();
		}
	}

	// elimina un archivo del repositorio
	public static synchronized void deleteDocumentCachedInDisk(Connection con, int idDocument)
			throws NoSuchAlgorithmException, IOException, ErrorDeAplicaqcion {
		File out = new File(getNameFileEncripted(con, "versiondocview", idDocument, null));
		System.out.println(out.getAbsolutePath());
		if (out.exists()) {
			out.delete();
		}
	}

	/**
	 * Solo para archivos de doble version
	 * 
	 * @param idDocument
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws ErrorDeAplicaqcion
	 */
	public static void recoverDocumentCachedInDisk(int idDocument) throws NoSuchAlgorithmException, IOException, ErrorDeAplicaqcion {
		recoverDocumentCachedInDisk(null, idDocument);
	}

	public static void recoverDocumentCachedInDisk(Connection con, int idDocument) throws NoSuchAlgorithmException, IOException, ErrorDeAplicaqcion {
		File out = new File(getNameFileEncripted(con, "versiondocview", idDocument, null));
		// File lastCache = new File(getNameFileEncripted("versiondocview",idDocument, null) + CACHE_DOBLE_VERSION_SUFIJO);
		File lastCache = new File(getNameFileEncripted(con, "versiondocview", idDocument, null));
		if (out.exists()) {
			// ydavila Ticket 001-00-003196 No conformidad de visualizaci�n de documentos doble versi�n
			// out.delete();
		}

		if (lastCache.exists()) {
			log.info("Recuperando cache desde " + lastCache.getAbsolutePath());
			// ydavila Ticket 001-00-003196 No conformidad de visualizaci�n de documentos doble versi�n
			// lastCache.delete();
			// ydavila esto fue lo �ltimo 20062016
			if (out.exists() && !out.getPath().equals(lastCache.getPath())) {
				out.delete();
			}
			// ydavila Ticket 001-00-003196 No conformidad de visualizaci�n de documentos doble versi�n
			Archivo.renameTo(lastCache, out);

		} else {
			log.info("No existe cache para recuperar: " + lastCache.getAbsolutePath());
		}
	}

	/**
	 * 
	 * @param nombreTabla
	 * @param id
	 * @param path
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws ErrorDeAplicaqcion
	 */
	public static String getNameFileEncripted(String nombreTabla, int id, String path) throws NoSuchAlgorithmException, ErrorDeAplicaqcion {
		return getNameFileEncripted(null, nombreTabla, id, path);
	}

	/**
	 * 
	 * @param con
	 * @param nombreTabla
	 * @param id
	 * @param path
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws ErrorDeAplicaqcion
	 */
	public static String getNameFileEncripted(Connection con, String nombreTabla, int id, String path) throws NoSuchAlgorithmException, ErrorDeAplicaqcion {
		StringBuilder nameFile = new StringBuilder(nombreTabla.toLowerCase()).append(id);
		StringBuilder ruta = new StringBuilder(path == null ? ToolsHTML.getRepository(con) : path).append("/");
		ruta.append(StringUtil.zero(String.valueOf(id), 3)).append("/");
		ruta.append(EncryptorMD5.getMD5(nameFile.toString()));
		// System.out.println(ruta.toString());
		return ruta.toString();
	}

	public static String getNameFileEncriptedCheckout(String tableName, int id) throws NoSuchAlgorithmException, ErrorDeAplicaqcion {
		StringBuilder nameFile = new StringBuilder(tableName.toLowerCase()).append(id);
		StringBuilder ruta = new StringBuilder(ToolsHTML.getRepository(null)).append("/checkout/");

		File rutaFile = new File(ruta.toString());
		if (!rutaFile.exists()) {
			rutaFile.mkdir();
		}

		ruta.append(EncryptorMD5.getMD5(nameFile.toString()));
		return ruta.toString();
	}

	/**
	 * 
	 * @param conn
	 */
	protected static void setFinally(Connection conn) {
		if (conn != null) {
			try {
				conn.setAutoCommit(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				closeConection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected static void closeConection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isPDF(File inputFile) {
		StringBuffer data = new StringBuffer();
		try {
			FileInputStream fis = new FileInputStream(inputFile);
			int c;
			int cont = 0;
			while ((c = fis.read()) != -1) {
				data.append((char) c);
				if (cont++ > 2)
					break;
			}
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data.toString().toUpperCase().indexOf("%PDF") != -1;
	}

	/**
	 * 
	 * @param fis
	 * @return
	 */
	public static boolean isPDF(InputStream fis) {
		StringBuilder data = new StringBuilder();
		try {
			int c;
			int cont = 0;
			while ((c = fis.read()) != -1) {
				data.append((char) c);
				if (cont++ > 2)
					break;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data.toString().toUpperCase().indexOf("%PDF") != -1;
	}

	/**
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void write(InputStream in, File out) throws IOException {

		DataInputStream fileIn = null;
		DataOutputStream fileOut = null;

		/**
		 * Como voy a leer de un archivo binario archivo tengo que utilizar un try a pesar de que aviento IOException y FileNotFoundException
		 */
		try {
			File carpeta = new File(out.getParent());

			if (out.exists()) {
				System.gc();
				out.delete();
			}

			if (!carpeta.exists()) {
				carpeta.mkdir();
			}

			// Creo los objetos con los que voy a leer
			fileIn = new DataInputStream(in);
			fileOut = new DataOutputStream(new FileOutputStream(out));

			byte[] bytes = new byte[fileIn.available()];

			// Utilizo un loop infinito para leer todo el archivo // muy lento
			// escribir directo a disco
			// while (true) {
			// escribo a fileOut
			// fileOut.write(fileIn.readByte() ^ pass);
			// }

			for (int i = 0; i < bytes.length; i++) {
				bytes[i] = (byte) (fileIn.readByte());
			}

			fileOut.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fileIn.close();
			fileOut.close();
		}
	}

	/**
	 * 
	 * @param source
	 * @param destination
	 * @return
	 */
	public static boolean renameTo(File source, File destination) {
		if (!destination.exists() && !source.getPath().equals(destination.getPath())) {
			// intentamos con renameTo
			System.gc();
			boolean result = source.renameTo(destination);
			if (!result) {
				// intentamos copiar
				result = true;
				result &= copiar(source, destination);
				System.gc();
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
		// declaraci�n del flujo
		FileInputStream sourceFile = null;
		FileOutputStream destinationFile = null;
		try {
			// creamos fichero
			destination.createNewFile();
			// abrimos flujo
			sourceFile = new FileInputStream(source);
			destinationFile = new FileOutputStream(destination);
			IOUtils.copy(sourceFile, destinationFile);

			resultado = true;
		} catch (FileNotFoundException f) {
		} catch (IOException e) {
		} finally {
			// pase lo que pase, cerramos flujo
			IOUtils.closeQuietly(destinationFile);
			IOUtils.closeQuietly(sourceFile);
		}
		return (resultado);
	}
	
	public static void escribirEnDisco(String name, InputStream inputStreamOfPdfFile) {
		OutputStream outStream = null;
		try {
			byte[] buffer = new byte[inputStreamOfPdfFile.available()];
		    inputStreamOfPdfFile.read(buffer);
			File targetFile = new File(name);
			if(targetFile.exists()) {
				FileUtils.forceDelete(targetFile);
			}
		    outStream = new FileOutputStream(targetFile);
		    outStream.write(buffer);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(outStream!=null) {
				try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	public static void main(String[] args) {
		// desencripta el archivo
		String name = "C:/qweb/repositorios/qweb/184/323651b3d1612f33681580588b02198b";
		String dest = "C:/qweb/repositorios/qweb/184/323651b3d1612f33681580588b02198b.doc";

		File origen = new File(name);
		File destino = new File(dest);

		FileOutputStream destinationFile = null;
		InputStream origenStream = null;

		try {
			origenStream = readEncrypt(origen, Constants.PASS_ENCRYTED);

			destino.createNewFile();
			destinationFile = new FileOutputStream(destino);

			IOUtils.copy(origenStream, destinationFile);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// pase lo que pase, cerramos flujo
			IOUtils.closeQuietly(destinationFile);
			IOUtils.closeQuietly(origenStream);
		}
	}
	
	public static ByteArrayInputStream retrieveByteArrayInputStream(File file) throws IOException {
	    return new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
	}	
}
