package com.focus.filesystem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
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
			//vaciarTabla(cone, path, "versiondocview", "numver", "data");

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
		pst = cone.prepareStatement(JDBCUtil.replaceCastMysql(sb.toString()));
		rs = pst.executeQuery();

		// consulta el campo con la imagen del archivo
		sb2.append("SELECT ").append(campoData).append(" FROM ").append(nombreTabla).append(" WHERE ").append(campoClave).append("=?");
		pst = cone.prepareStatement(JDBCUtil.replaceCastMysql(sb2.toString()));

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
					nameFile.append(nombreTabla).append(rs.getString(1));

					// almacena el archivo sin encriptar
					// saveFileInDisk(imagenBuffer, ruta.toString(),
					// EncryptorMD5.getMD5(nameFile.toString()));
					// almacena el archivo encriptado
					Archivo.writeDocumentToDisk(nombreTabla, rs.getInt(1), imagenBuffer, path);
					
					System.out.println("Procesados:" + (++contador));
				}
			}
			// break;
		}
	}

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
		sb.append("WHERE a.").append(campoIdDocument).append("=b.numgen AND b.active='1' and nameDocument like 'Organigrama Gestión de la Calidad%'");
		pst = cone.prepareStatement(sb.toString());
		rs = pst.executeQuery();
		 */
		
		sb.append("SELECT DISTINCT(d.numGen), vd.numver, d.nameDocument, d.nameFile ");
		sb.append("FROM documents d,person p,typedocuments td,versiondoc vd ");
		sb.append("WHERE p.nameUser = d.owner AND td.idTypeDoc = cast(d.type as int) "); 
		sb.append("AND vd.numDoc = d.numGen ");
		sb.append("AND d.active = '1' ");
		sb.append("AND cast(d.type as int)<>1001 "); 
		sb.append("AND p.accountActive='1' ");
		sb.append("AND vd.numVer = (SELECT MAX(numVer) FROM versiondoc vdi WHERE vdi.numDoc = d.numGen ) ");
		pst = cone.prepareStatement(JDBCUtil.replaceCastMysql(sb.toString()));
		rs = pst.executeQuery();


		while (rs.next()) {
			//System.out.println(rs.getString(1));
			// nombre de archivo a generar
			
			nameFile = rs.getString("nameFile");
			name = nameFile.split("\\.");
			ext = name[name.length-1];
			
			nameDocument = rs.getString("nameDocument").replaceAll("[^A-Za-z0-9áéíóúÁÉÍÓÚ ]", "");

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
			String ruta = path + File.separator + nameFile;
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
		pst = cone.prepareStatement(JDBCUtil.replaceCastMysql(query.toString()));
		rs = pst.executeQuery();

		
		// consulta el campo con la imagen del archivo
		sb2.append("SELECT ").append(campoData).append(" FROM ").append(nombreTabla).append(" WHERE ").append(campoClave).append("=?");
		pst = cone.prepareStatement(JDBCUtil.replaceCastMysql(sb2.toString()));
		
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
					//Archivo.writeDocumentToDisk(nombreTabla, rs.getInt(1), imagenBuffer, path);
					
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
		//String name="Tríptico ¿Qué es ISO?";
		//System.out.println(name);
		//System.out.println(name.replaceAll("[^A-Za-z0-9áéíóúÁÉÍÓÚ ]", "").replace(File.separator, "").replace("/", ""));
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
}
