package com.focus.filesystem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.io.PrintWriter;
import java.util.Calendar;
import javax.swing.JOptionPane;
import java.awt.Desktop;

import org.apache.commons.dbutils.DbUtils;

import com.desige.webDocuments.util.EncryptorMD5;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.StringUtil;
import com.focus.qwds.ondemand.server.excepciones.ErrorDeAplicaqcion;
import com.focus.util.Archivo;

public class FileSystem {

	public static int contador = 0;

	private String driver;
	private String url;
	private String user;
	private String passwd;
	private String ruta;
	private String rutaDownload;
	private static final String PATH_LOG = "C:\\qweb\\repositorios\\convertir";
	File carplog;
	
	public FileSystem() {
	}

	public String start(boolean prueba) {
		Connection cone = null;

		try {
			// Class.forName("org.postgresql.Driver").newInstance();
			// Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
			Class.forName(getDriver()).newInstance();

			// cone =
			// DriverManager.getConnection("jdbc:postgresql://localhost:5432/qwebds43",
			// "qwebdocuments", "qwebdocuments");
			// cone =
			// DriverManager.getConnection("jdbc:jtds:sqlserver://ccsfocus05:1433/focus451",
			// "qwebdocuments", "qwebdocuments");
			cone = DriverManager.getConnection(getUrl(), getUser(), getPasswd());

			String path;
			path = getRuta();

			File f = new File(path);
			if (!f.exists()) {
				throw new Exception("Carpeta destino no existe");
			}

			contador = 0;

			if (prueba) {
				return "Conexion exitosa";
			}

			//vaciarTabla(cone, path, "backupfiles", "idbackup", "archivo");
			//vaciarTabla(cone, path, "doccheckout", "idcheckout", "datafile");
			// vaciarTabla(cone, path, "tbl_planillasacop1", "idplanillasacop1",
			// "data");
			// vaciarTabla(cone, path, "tbl_planillasacop1esqueleto",
			// "idplanillasacop1", "data");
			vaciarTabla(cone, path, "versiondoc", "numver", "data");

			//ydavila 03/2016
			//comentar si no se van a extraer las versiones
			//vaciarTabla(cone, path, "versiondocview", "numver", "data");
			//ydavila

			// select * from backupfiles; -- archivo -- idbackup
			// select * from doccheckout -- datafile -- idcheckout
			// select * from tbl_planillasacop1 -- data -- idplanillasacop1
			// select * from tbl_planillasacop1esqueleto -- data --
			// idplanillasacop1
			// select * from versiondoc -- data -- numver
			// select * from versiondocview -- data -- numver
			System.out.println("Ejecutado sin error");

		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		} finally {
			if (cone != null) {
				try {
					cone.close();
				} catch (SQLException e) {
				}
			}
		}
		return "Resultado exitoso";
	}

	public String startDownload(boolean prueba) {
		Connection cone = null;

		try {
			// Class.forName("org.postgresql.Driver").newInstance();
			// Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
			Class.forName(getDriver()).newInstance();

			// cone =
			// DriverManager.getConnection("jdbc:postgresql://localhost:5432/qwebds43",
			// "qwebdocuments", "qwebdocuments");
			// cone =
			// DriverManager.getConnection("jdbc:jtds:sqlserver://ccsfocus05:1433/focus451",
			// "qwebdocuments", "qwebdocuments");
			cone = DriverManager.getConnection(getUrl(), getUser(), getPasswd());

			String path;
			path = getRuta();

			File f = new File(path);
			if (!f.exists()) {
				throw new Exception("Carpeta repositorio no existe");
			}

			String pathDownload;
			pathDownload = getRutaDownload();

			File fd = new File(pathDownload);
			if (!fd.exists()) {
				throw new Exception("Carpeta download no existe");
			}


			contador = 0;

			if (prueba) {
				return "Conexion exitosa";
			}

			downloadTabla(cone, path, pathDownload, "versiondoc", "numver", "numDoc");

			// select * from backupfiles; -- archivo -- idbackup
			// select * from doccheckout -- datafile -- idcheckout
			// select * from tbl_planillasacop1 -- data -- idplanillasacop1
			// select * from tbl_planillasacop1esqueleto -- data --
			// idplanillasacop1
			// select * from versiondoc -- data -- numver
			// select * from versiondocview -- data -- numver

			System.out.println("Ejecutado sin error");

		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		} finally {
			if (cone != null) {
				try {
					cone.close();
					System.out.println("Conexion cerrada");
				} catch (SQLException e) {
				}
			}
		}
		return "Resultado exitoso";
	}

	public void vaciarTabla(Connection cone, String path, String nombreTabla, String campoClave, String campoData) throws Exception {
		ResultSet rs;
		ResultSet rs2;
		StringBuffer sb = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		StringBuffer nameFile = new StringBuffer();
		InputStream imagenBuffer = null;
		StringBuffer ruta = new StringBuffer();
		File carpeta;

		PreparedStatement pst = null;

		// consulta el campo clave de la tabla
		sb.append("SELECT ").append(campoClave).append(" FROM ").append(nombreTabla);
		pst = cone.prepareStatement(sb.toString());
		rs = pst.executeQuery();

		// consulta el campo con la imagen del archivo
		sb2.append("SELECT ").append(campoData).append(" FROM ").append(nombreTabla).append(" WHERE ").append(campoClave).append("=?");
		pst = cone.prepareStatement(sb2.toString());

		while (rs.next()) {
			System.out.println(rs.getString(1));
			pst.setInt(1, rs.getInt(1));
			rs2 = pst.executeQuery();
			if (rs2.next()) {
				imagenBuffer = (InputStream) rs2.getBinaryStream(campoData);
				if (imagenBuffer != null) {
					ruta.setLength(0);
					ruta.append(path).append(File.separatorChar).append(zero(rs.getString(1), 3));
					carpeta = new File(ruta.toString());
					if (!carpeta.exists()) {
						carpeta.mkdir();
					}
					nameFile.setLength(0);
					String extension=".doc";
					StringBuffer sb4 = new StringBuffer();
					PreparedStatement pst2 = null;
					ResultSet rs4;
					sb4.append("SELECT nameFile from Documents where cast(numgen as char) = ");
					sb4.append(rs.getString(1));
					pst2 = cone.prepareStatement(sb4.toString());
					rs4 = pst2.executeQuery();
					while (rs4.next()) {
						//if (flag == 0) {
							//creamos el archivo de salida para logs
						//	PrintWriter pw = null;
						//	String executionLogPath = path + "convertir_" + Calendar.getInstance().getTimeInMillis() + ".txt";
						//	pw = new PrintWriter(executionLogPath);
						//	flag = 1;
						//}
						System.out.println("rs4.getString(1)= " + rs4.getString(1));
						extension = rs4.getString(1).substring(rs4.getString(1).length() -4, rs4.getString(1).length());
					}
						nameFile.append(nombreTabla).append(rs.getString(1));

						// almacena el archivo sin encriptar
						//saveFileInDisk(imagenBuffer, ruta.toString(),path);
						// EncryptorMD5.getMD5(nameFile.toString()));

						// ydavila 11042016     
						//hay que descomentar las siguientes dos instrucciones para realizar la extracci�n de DB al repositorio
						//colocando el nombre real del archivo y dejando el archivo desencriptado
						ruta=ruta.append(extension);
						saveFileInDisk(imagenBuffer, ruta.toString(),EncryptorMD5.getMD5(nameFile.toString()));
						// ydavila 11042016 		

						// almacena el archivo encriptado
						//Archivo.writeDocumentToDisk(nombreTabla, rs.getInt(1), imagenBuffer, path);
						//path=path.concat(extension);
						//saveFileInDisk(imagenBuffer, ruta.toString(),path);
						//String out="";
						//findMascaraNameDocument(nombreTabla,rs.getInt(1), path);
						//saveFileInDisk(imagenBuffer, ruta.toString(),rs4.getString("nameFile"));
						//EncryptorMD5.getMD5(nameFile.toString());

						//Archivo.writeDocumentToDisk(nombreTabla, rs.getInt(1), imagenBuffer, path);
						//String a="";
						//System.out.println("Procesados:" + (++contador) + a + imagenBuffer);
					//////}
				}
			}
			// break;
		}
		

		
		
		
		
		
		
		
		//abrimos el directorio resultante
		//JOptionPane.showMessageDialog(null,
			//	"El proceso finalizo con aparente exito."
			//		+ "\nAl cerrar esta ventana vera los resultados."
			//		+ "\nEl archivo con el log de ejecucion tambien sera abierto automaticamemte",
			//	"Proceso Finalizado",
			//	JOptionPane.INFORMATION_MESSAGE);
	//	String executionLogPath = PATH_LOG + File.separator 
	//			+ "run_" + Calendar.getInstance().getTimeInMillis() + ".txt";
	//	Desktop.getDesktop().open(carplog);
	//	Desktop.getDesktop().open(new File(executionLogPath));
	}

	/*	// busca nombre del archivo encriptado (m�scara)
	public static File findMascaraNameDocument(String nombreTabla, int idDocument, String path)
			throws NoSuchAlgorithmException, IOException, ErrorDeAplicaqcion {
		File out = new File(Archivo.getNameFileEncripted(nombreTabla, idDocument, path));
		return out;
	}*/



	public void downloadTabla(Connection cone, String path, String pathDownload, String nombreTabla, String campoClave, String campoIdDocument) throws Exception {
		ResultSet rs;
		ResultSet rs2;
		StringBuffer sb = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		String nameFile;
		InputStream imagenBuffer = null;
		StringBuffer ruta = new StringBuffer();
		File carpeta;
		StringBuffer nameFileDownload = new StringBuffer();
		File fileEncripted;
		File fileOut;
		String[] name = null;
		String ext = null;
		String nameDocument = null;

		PreparedStatement pst = null;

		// consulta el campo clave de la tabla
		/*
		sb.append("SELECT a.").append(campoClave).append(", b.nameDocument, b.nameFile ");
		sb.append("FROM ").append(nombreTabla).append(" a, documents b ");
		sb.append("WHERE a.").append(campoIdDocument).append("=b.numgen AND b.active='1' and nameDocument like 'Organigrama Gesti�n de la Calidad%'");
		pst = cone.prepareStatement(sb.toString());
		rs = pst.executeQuery();
		 */

		sb.append("SELECT DISTINCT(d.numGen), vd.numver, d.nameDocument, d.nameFile ");
		sb.append("FROM Documents d,person p,TypeDocuments td,VersionDoc vd ");
		sb.append("WHERE p.nameUser = d.owner AND td.idTypeDoc = cast(d.type as int) "); 
		sb.append("AND vd.numDoc = d.numGen ");
		sb.append("AND d.active = '1' ");
		sb.append("AND cast(d.type as int)<>1001 "); 
		sb.append("AND p.accountActive='1' ");
		sb.append("AND vd.numVer = (SELECT MAX(numVer) FROM VersionDoc vdi WHERE vdi.numDoc = d.numGen ) ");
		pst = cone.prepareStatement(sb.toString());
		rs = pst.executeQuery();


		while (rs.next()) {
			//System.out.println(rs.getString(1));
			// nombre de archivo a generar

			nameFile = rs.getString("nameFile");
			name = nameFile.split("\\.");
			ext = name[name.length-1];

			nameDocument = rs.getString("nameDocument").replaceAll("[^A-Za-z0-9���������� ]", "");

			fileEncripted = new File(getNameFileEncripted(nombreTabla, rs.getInt(campoClave), path));

			nameFileDownload.setLength(0);
			nameFileDownload.append(pathDownload).append(File.separatorChar);
			nameFileDownload.append(nameDocument).append(" (").append(rs.getString("numgen")).append("_").append(rs.getString(campoClave)).append(").").append(ext);

			fileOut = new File(nameFileDownload.toString());

			System.out.println(nameDocument);

			if(fileEncripted.exists()) {
				imagenBuffer = Archivo.readEncrypt(fileEncripted, Constants.PASS_ENCRYTED);

				Archivo.write(imagenBuffer, fileOut);
			} else {
				System.out.println("No existe el archivo");
			}

			//break;
		}
	}

	public String zero(String cad, int largo) {
		if (largo > 60) {
			return cad;
		}
		cad = "000000000000000000000000000000000000000000000000000000000000".concat(cad);
		return cad.substring(cad.length() - largo);
	}

	public static void saveFileInDisk(InputStream imagenBuffer, String path, String nameFile) {
		try {
			//ydavila
			//String ruta = path + File.separator + nameFile;
			String ruta="";
			//extension=path.substring(path.length() -4, path.length());
			nameFile=nameFile.concat(path.substring(path.length() -4, path.length()));
			path=path.substring(0, path.length() -4);
			ruta = path + File.separator + nameFile;
			File fichero = new File(ruta);
			System.out.println("Salvando archivo: " + ruta);
			BufferedInputStream in = new BufferedInputStream(imagenBuffer);
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fichero));
			byte[] bytes = new byte[8096];
			int len = 0;
			while ((len = in.read(bytes)) > 0) {
				out.write(bytes, 0, len);
			}
			out.flush();
			out.close();
			in.close();
			System.out.println("archivo salvado");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getNameFileEncripted(String nombreTabla, int id, String path) throws NoSuchAlgorithmException, ErrorDeAplicaqcion {
		StringBuffer nameFile = new StringBuffer(nombreTabla.toLowerCase()).append(id);
		StringBuffer ruta = new StringBuffer(path).append("/");
		ruta.append(StringUtil.zero(String.valueOf(id), 3)).append("/");
		ruta.append(EncryptorMD5.getMD5(nameFile.toString()));
		return ruta.toString();
	}


	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getRuta() {
		return ruta;
	}

	public void setRuta(String ruta) {
		this.ruta = ruta;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getRutaDownload() {
		return rutaDownload;
	}

	public void setRutaDownload(String rutaDownload) {
		this.rutaDownload = rutaDownload;
	}

	public void vaciarTablaFromQuery(Connection cone, String path, String query, String nombreTabla, String campoClave, String campoData) throws Exception {
		ResultSet rs;
		ResultSet rs2;
		StringBuffer sb2 = new StringBuffer();
		StringBuffer nameFile = new StringBuffer();
		InputStream imagenBuffer = null;
		StringBuffer ruta = new StringBuffer();
		File carpeta;

		PreparedStatement pst = null;

		// consulta el campo clave de la tabla
		pst = cone.prepareStatement(query.toString());
		rs = pst.executeQuery();


		// consulta el campo con la imagen del archivo
		sb2.append("SELECT ").append(campoData).append(" FROM ").append(nombreTabla).append(" WHERE ").append(campoClave).append("=?");
		pst = cone.prepareStatement(sb2.toString());

		while(rs.next()) {
			//System.out.println(rs.getString(1));
			pst.setInt(1,rs.getInt(1));
			rs2 = pst.executeQuery();
			if(rs2.next()) {
				imagenBuffer = (InputStream)rs2.getBinaryStream(campoData);
				if(imagenBuffer!=null) {
					ruta.setLength(0);
					ruta.append(path).append(File.separatorChar);
					carpeta = new File(ruta.toString());
					if(!carpeta.exists()){
						carpeta.mkdir();
					}

					nameFile.setLength(0);
					nameFile.append(rs.getString("nameFile"));

					// almacena el archivo sin encriptar
					saveFileInDisk(imagenBuffer, ruta.toString(), nameFile.toString());
					// almacena el archivo encriptado
					Archivo.writeDocumentToDisk(nombreTabla, rs.getInt(1), imagenBuffer, path);

					System.out.println("Procesados:"+(++contador));
				}
			}
			//break;
		}
	}


	public static void main(String[] args) {
		// FileSystem t = new FileSystem();

		// FileSystem t = new FileSystem("C:\\data_file_focus");
		// t.setRuta("\\\\ubuntu-sun\\opt\\data_qweb");
		// t.start();
		//String name = "Reporte Semanal de Merchandising Promotor -Canal A/S";
		//String name="Tr�ptico �Qu� es ISO?";
		//System.out.println(name);
		//System.out.println(name.replaceAll("[^A-Za-z0-9���������� ]", "").replace(File.separator, "").replace("/", ""));
		//if(true) return ;

		FileSystem file = new FileSystem();
		file.setDriver("org.postgresql.Driver");
		file.setUrl("jdbc:postgresql://localhost:5432/promoting");
		file.setUser("qwebdocuments");
		file.setPasswd("qwebdocuments");
		file.setRuta("C:/qwebproduccion_data_files");
		file.setRutaDownload("C:/compartida/promoting");

		String resultado = file.startDownload(false);

		System.out.println(resultado);

	}


	/**
	 * M�todo que respalda las versiones del documento
	 * 
	 * @param con
	 * @param id
	 * @param stream
	 * @throws Exception
	 */
	/*public static synchronized void returnFileConvertedToDisk(Connection con, long id,
			int idDocument, InputStream stream) throws Exception {
		StringBuffer sb5 = new StringBuffer();
		//PreparedStatement pstmt = con
		//		.prepareStatement("UPDATE BackUpFiles SET archivo = ".append stream.append( " WHERE iddocument = ").append( idocument = ");"
		PreparedStatement pstmt = null;
		pstmt = con.prepareStatement(sb5.toString());
		sb5.append("UPDATE BackUpFiles SET archivo = ").append(stream).append( " WHERE iddocument = ").append(idDocument );
		System.out.println("pas� por filesystem return file converted to disk");

		//pstmt.setLong(1, id);
		//pstmt.setLong(2, idDocument);
		//pstmt.setTimestamp(3, dateUpdated);
		pstmt.executeUpdate();

		DbUtils.closeQuietly(pstmt);

		//Archivo.writeDocumentToDisk("BackUpFiles", (int) id, stream);
	}*/
}

