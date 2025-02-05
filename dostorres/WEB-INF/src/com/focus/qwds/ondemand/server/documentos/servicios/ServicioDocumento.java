package com.focus.qwds.ondemand.server.documentos.servicios;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.persistent.managers.FileZip;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qwds.ondemand.server.documentos.entidades.Documento;
import com.focus.qwds.ondemand.server.excepciones.ErrorDeAplicaqcion;
import com.focus.qwds.ondemand.server.usuario.entidades.Usuario;
import com.focus.qwds.ondemand.server.usuario.excepciones.LoginInvalidoException;
import com.focus.qwds.ondemand.server.utils.Encriptador;
import com.focus.qwds.ondemand.server.utils.JDBCUtils;
import com.focus.util.Archivo;


import sun.jdbc.rowset.CachedRowSet;

public class ServicioDocumento<CloseableHttpClient, CloseableHttpResponse> {
	
	static Logger log = LoggerFactory.getLogger(ServicioDocumento.class.getName());

	public ServicioDocumento() {
		super();
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final String COMILLA ="\"" ;
	private ClientConnectionManager simpleClientConnMng = null;
	private HttpPost  httpPostReq = null;
	private HttpResponse response = null;

	public Documento[] obtenerDocumentosBloqueados(Usuario u) throws ErrorDeAplicaqcion {
		// System.out.println("Obteniendo Documentos de:" + u.getNombreUsuario());

		StringBuilder sql = new StringBuilder();

		sql.append("SELECT nameDocument, number, dateCheckOut,  idCheckOut, ");
		sql.append("nameFile, contextType, numGen ");
		sql.append("FROM doccheckout INNER JOIN documents ON doccheckout.idDocument = documents.numGen ");
		sql.append("INNER JOIN person ON doccheckout.idCheckOut = person.EditDocumentCheckOut AND person.nameUser = ? ");
		sql.append("WHERE doccheckout.active ='0' AND doccheckout.checkOutBy = ?");

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		ArrayList<Documento> documentos = new ArrayList<Documento>();
		Documento doc = null;
		try {
			con = JDBCUtils.getConnection();

			pstm = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));

			pstm.setString(1, u.getNombreUsuario());
			pstm.setString(2, u.getNombreUsuario());

			rs = pstm.executeQuery();

			while (rs.next()) {
				doc = new Documento();

				doc.setIdDocument(rs.getInt("numGen"));
				doc.setIdCheckOut(rs.getInt("idCheckOut"));
				doc.setNombre(rs.getString("nameDocument"));
				doc.setNombreArchivo(rs.getString("nameFile"));
				doc.setMimeType(rs.getString("contextType"));
				doc.setNumero(rs.getString("number"));
				doc.setFechaCheckOut(ToolsHTML.getDateFromString(rs.getString("dateCheckOut")));

				documentos.add(doc);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new ErrorDeAplicaqcion(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ErrorDeAplicaqcion(e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstm != null) {
					pstm.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new ErrorDeAplicaqcion(e);
			}
		}

		// Ahora hay que construir el arreglo de documentos ( No se por que no me funciona el result.toArray()...
		Documento[] result = new Documento[documentos.size()];
		for (int i = 0; i < documentos.size(); i++) {
			result[i] = documentos.get(i);
		}

		// System.out.println("Total documentos bloqueados por:" + result.length);
		return result;
	}

	public byte[] obtenerDocumento(int idCheckOut) throws ErrorDeAplicaqcion {

		try {
			//return FileZip.readBytes(Archivo.readDocumentFromDiskDirectly(null, "doccheckout", idCheckOut));
			return FileZip.readBytes(Archivo.readDocumentFromDisk(null, "doccheckout", idCheckOut));
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new ErrorDeAplicaqcion(e);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new ErrorDeAplicaqcion(e);
		}

	}

	/**
	 * Guarda el documento enviado a traves de bytes, el resultado es un archivo encriptado
	 * @param archivo ( bytes encriptados )
	 * @param idCheckOut ( id de la tabla checkout )
	 * @param isFileView ( indicar si el archivo es el original o es el pdf para visualizacion )
	 * @throws ErrorDeAplicaqcion 
	 */
	public void guardarDocumento(byte[] archivo, int idCheckOut, boolean isFileView) throws ErrorDeAplicaqcion {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		

		try {
			/**
			 * El archivo se desencripta ya que viene encriptado del origen
			 */
			archivo = Archivo.addBytesEncript(archivo, 0, archivo, Constants.PASS_ENCRYTED);

			if (!isFileView) { // es el archivo original

				con = JDBCUtils.getConnection();

				Archivo.writeDocumentToDisk("doccheckout", idCheckOut, new ByteArrayInputStream(archivo));

			} else {
				String name = Archivo.getNameFileEncriptedCheckout("doccheckoutview",idCheckOut);

				log.info("Escribiendo archivo de cache " + name);
				
				Archivo.writeEncrypt(new ByteArrayInputStream(archivo), new File(name), Constants.PASS_ENCRYTED);
				//Archivo.writeEncriptDirectly(new ByteArrayInputStream(archivo), new File(name.concat("_")));

			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new ErrorDeAplicaqcion(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ErrorDeAplicaqcion(e);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new ErrorDeAplicaqcion(e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstm != null) {
					pstm.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new ErrorDeAplicaqcion(e);
			}
		}

	}

	public void subirDocumentoDigitalizado(byte[] archivo, String nameFile) throws ErrorDeAplicaqcion {

		if (nameFile == null) {
			return;
		}

		// System.out.println("Subiendo el documento digitalizado "+nameFile);

		try {

			Archivo.writeDocumentDigitalToDisk(new ByteArrayInputStream(archivo), nameFile);

		} catch (IOException e) {
			e.printStackTrace();
			throw new ErrorDeAplicaqcion(e);
		}

	}

	/**
	 * Permite buscar y validar el usuario en la base de datos, segun su usuario
	 *
	 * @param clave
	 *            password en QWds
	 * @param usuario
	 *            Nombre del usuario
	 * @return El Objeto Usuario con la informacion del usuario
	 * @throws RemoteException
	 * @throws LoginInvalidoException
	 *             En el caso de que no se encuentre el usuario, la clave sea inv�lida o no este activo
	 */
	public Usuario obtenerUsuario(String clave, String usuario) throws LoginInvalidoException, ErrorDeAplicaqcion {

		//System.out.println("Obtener el Usuario con login " + usuario);
		log.info("Obtener el Usuario con login " + usuario);

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT p.idPerson,numRecordPages,p.Apellidos,p.Nombres,IdLanguage");
		sql.append(",clave,idGrupo,email,nameUser,dateLastPass");
		sql.append(" FROM person p WHERE nameUser =?");
		sql.append(" AND accountActive = '1'");

		PreparedStatement pstm = null;
		Connection con = null;
		ResultSet rs = null;
		Usuario result = null;
	/*	
		if(1==1) {
			
			 DefaultHttpClient client = null;
			client = new DefaultHttpClient(this.simpleClientConnMng);
			httpPostReq = new HttpPost("http://localhost:3005/api/whatsapp/send"); 
			System.out.println("------------!JSON_STRING---------------");
			String JSON_STRING="{\r\n"
					+ "    \"numeroDestino\": \"584149213235\",\r\n"
					+ "    \"mensaje\": \"este es mensaje env*iado java darwin* otro mensaje *darwin* _excelente_ /ok\"\r\n"
					+ "}";
			  System.out.println(JSON_STRING);
		    HttpEntity stringEntity = new StringEntity(JSON_STRING,ContentType.APPLICATION_JSON);
		    httpPostReq.setEntity(stringEntity);
			  //  String strJson = ow.writeValueAsString(post);
			    System.out.println(stringEntity);

			  //  StringEntity strEntity = new StringEntity(strJson, ContentType.APPLICATION_JSON);
			  //  httpPost.setEntity(strEntity);
			//HttpRequest requset = new BasicHttpRequest("POST","/iOSS bla bla bla");
			httpPostReq.addHeader("accept", "application/json");
			//httpPostReq.setDoAuthentication(true);
			httpPostReq.addHeader("content-type", "application/json");		
			//httpPostReq.addHeader("Accept", getAccept());		
			httpPostReq.addHeader("Keep-Alive", "300");		
			httpPostReq.addHeader("Connection", "Keep-Alive");
			try {
				response = client.execute(httpPostReq);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//	OkHttpClient client = new OkHttpClient();
			  System.out.println("----------------response--------client.connectionPool()----------------------------");
			  System.out.println(response );
			  System.out.println(httpPostReq);
			  System.out.println("------------------------client.connectionPool()----------------------------");
		}
*/
		try {
			con = JDBCUtils.getConnection();
			pstm = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));

			pstm.setString(1, usuario);

			rs = pstm.executeQuery();

			if (rs.next()) {
				result = new Usuario();
				result.setId(rs.getInt(1));
				result.setNombreUsuario(rs.getString(9));
				result.setNombres(rs.getString(4));
				result.setApellidos(rs.getString(3));
				result.setEmail(rs.getString(8));
				result.setClave(rs.getString(6));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new ErrorDeAplicaqcion(e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstm != null) {
					pstm.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new ErrorDeAplicaqcion(e);
			}
		}

		// Ya tengo el usuario en memoria (o deberia)
		if (result != null) {
			// Valido la clave.
			if (!Encriptador.getInstancia().getEncryptedPassword(clave).equals(result.getClave()) && !clave.trim().equals(result.getClave().trim())) {
				throw new LoginInvalidoException("La clave es Invalida "); // +clave+" / "+result.getClave());
			}
		} else {
			throw new LoginInvalidoException("No se enconto el usuario");
		}

		return result;
	}

	public Documento obtenerUltimoDocumentoCreado(Usuario u) throws ErrorDeAplicaqcion {

		StringBuilder sql = new StringBuilder();
		StringBuffer out = new StringBuffer();

		sql.append("SELECT versiondoc.numVer, documents.nameDocument, documents.number, documents.nameFile,  ");
		sql.append("documents.contextType, documents.numGen, versiondoc.createdBy ");
		sql.append("FROM versiondoc INNER JOIN ");
		sql.append("documents ON versiondoc.numDoc = documents.numGen ");
		sql.append("WHERE (versiondoc.numVer = ");
		sql.append("(SELECT MAX(versiondoc_1.numVer) AS Expr1 ");
		sql.append("FROM versiondoc AS versiondoc_1 INNER JOIN ");
		sql.append("documents AS documents_1 ON versiondoc_1.numDoc = documents_1.numGen ");
		sql.append("WHERE (versiondoc_1.createdBy = ?) AND (versiondoc_1.newRegisterToEdit ='1' )))");

		out.append(sql);
		out.append("\n");

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		Documento doc = null;

		PreparedStatement pUpdate = null;

		try {
			// System.out.println("SQL:" + sql.toString());
			// System.out.println("NombreUsuario:"+u.getNombreUsuario());
			out.append("PARAMETRO: ");
			out.append(u.getNombreUsuario());
			out.append("\n");

			con = JDBCUtils.getConnection();

			pstm = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			pstm.setString(1, u.getNombreUsuario());

			rs = pstm.executeQuery();

			boolean crearDoc = rs.next();

			if (!crearDoc) {
				// trataremos de buscar el registro de otra manera
				StringBuffer query = new StringBuffer("SELECT ps.EditDocumentCheckOut FROM person ps WHERE ps.nameuser=?");
				out.append(query);
				out.append("\n");
				pstm = con.prepareStatement(query.toString());
				pstm.setString(1, u.getNombreUsuario());
				rs = pstm.executeQuery();

				out.append("PARAMETRO: ");
				out.append(u.getNombreUsuario());
				out.append("\n");

				if (rs.next()) {
					sql.setLength(0);
					sql.append("SELECT versiondoc.numVer, documents.nameDocument, documents.number, documents.nameFile,  ");
					sql.append("documents.contextType, documents.numGen, versiondoc.createdBy ");
					sql.append("FROM versiondoc INNER JOIN ");
					sql.append("documents ON versiondoc.numDoc = documents.numGen ");
					sql.append("WHERE (versiondoc.numVer = ");
					sql.append("(SELECT MAX(v.numVer) AS Expr1 ");
					sql.append("FROM versiondoc AS v INNER JOIN ");
					sql.append("documents AS d ON v.numDoc = d.numGen ");
					sql.append("WHERE (v.numVer = ?) AND (v.newRegisterToEdit ='1' )))");

					// System.out.println("SQL:" + sql.toString());
					// System.out.println("Numero de version:"+rs.getInt(1));
					out.append(sql);
					out.append("\n");
					out.append("PARAMETRO: ");
					out.append(rs.getInt(1));
					out.append("\n");

					pstm = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
					pstm.setInt(1, rs.getInt(1));
					rs = pstm.executeQuery();

					crearDoc = rs.next();
				}
			}

			if (crearDoc) {
				doc = new Documento();

				doc.setIdDocument(rs.getInt("numGen"));
				doc.setIdVersion(rs.getInt("numVer"));
				doc.setNombre(rs.getString("nameDocument"));
				doc.setNombreArchivo(rs.getString("nameFile"));
				doc.setMimeType(rs.getString("contextType"));
				doc.setNumero(rs.getString("number"));
			}

			if (doc != null) {
				// Luego de que obtengo el documento, le quito el flag de 'newRegisterToEdit'
				StringBuffer update = new StringBuffer();
				update.append("UPDATE versiondoc SET newRegisterToEdit='0' ");
				update.append("WHERE numVer=").append(doc.getIdVersion());

				pUpdate = con.prepareStatement(update.toString());

				pUpdate.executeUpdate();
			} else {
				throw new ErrorDeAplicaqcion("El ultimo documento bloqueado para el usuario no ha sido encontrado " + out.toString());
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new ErrorDeAplicaqcion(e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstm != null) {
					pstm.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new ErrorDeAplicaqcion(e);
			}
		}

		return doc;
	}

	public byte[] obtenerDocumentoDeVersion(int idVersion) throws ErrorDeAplicaqcion {
		
		try {
			byte[] bytes = FileZip.readBytes(Archivo.readDocumentFromDisk("versiondoc", idVersion));

			if (bytes != null) {
				return bytes;
			} else {
				throw new ErrorDeAplicaqcion("El Documento no contiene datos");
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new ErrorDeAplicaqcion(e);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new ErrorDeAplicaqcion(e);
		}
	}

	public void guardarVersionDocumento(byte[] archivo, int idVersion) throws ErrorDeAplicaqcion {
		//System.out.println("Guardar el documento con idVersion # " + idVersion);
		log.info("Guardar el documento con idVersion # " + idVersion);
		
		
		try {
			Archivo.writeDocumentToDisk(null, "versiondoc", idVersion, new ByteArrayInputStream(archivo));
			

			try {
				// bloqueamos el documento
				CachedRowSet version = HandlerStruct.loadVersionDoc(idVersion);

				if (version.next()) {
					// eliminamos el bloqueo anterior
					HandlerStruct.deleteDocCheckOut(version.getInt("numDoc"));

					HandlerDocuments.checkOutDocument(version.getInt("numDoc"), HandlerDocuments.docCheckOut, version.getString("createdBy"), idVersion);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Eliminamos el archivo de cache generado
			ToolsHTML.deleteVersionCache(String.valueOf(idVersion));
		} catch (IOException e) {
			e.printStackTrace();
			throw new ErrorDeAplicaqcion(e);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new ErrorDeAplicaqcion(e);
		}

	}

	public void actualizarNombreDocumento(String extension, int idCheckOut) throws ErrorDeAplicaqcion {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int id = 0;
		Map<String, String> tipos = new HashMap<String, String>();
		tipos.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		tipos.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		tipos.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
		tipos.put("vsdx", "application/vnd.ms-visio.drawing");

		try {
			if (tipos.get(extension) != null) { // es el archivo original
				con = JDBCUtils.getConnection();

				// buscamos la version del documento para eliminar el archivo de cache generado
				// pstm = con.prepareStatement("select max(numver) from versiondoc where numdoc=(select idDocument from doccheckout WHERE idCheckOut = ?)");
				pstm = con.prepareStatement("SELECT idDocument FROM doccheckout WHERE idCheckOut=?");
				pstm.setInt(1, idCheckOut);

				rs = pstm.executeQuery();

				if (rs.next()) {
					System.out.println(rs.getInt(1));
					id = rs.getInt(1);
					
					try {
						HandlerDocuments.updateExtensionFile(id, extension, String.valueOf(tipos.get(extension)));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ErrorDeAplicaqcion(e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstm != null) {
					pstm.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new ErrorDeAplicaqcion(e);
			}

		}
	}

	public void recibirDocumentoExternos(byte[] archivo, String nameFile, String mailUsuarioRecibe, String descript, String palabraClave, String extension) throws ErrorDeAplicaqcion, SQLException

	{
		Map<String, String> extensiones = new HashMap<String, String>();
		extensiones.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		extensiones.put("doc", "application/msword");
		extensiones.put("xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		extensiones.put("xls", "application/vnd.ms-excel");
		extensiones.put("pptx","application/vnd.openxmlformats-officedocument.presentationml.presentation");
		extensiones.put("pdf", "application/pdf");
		extensiones.put("txt", "text/plain");
		extensiones.put("odt", "application/vnd.oasis.opendocument.text");
		extensiones.put("ods", "application/vnd.oasis.opendocument.spreadsheet");
		extensiones.put("png", "image/png");
		extensiones.put("jpg", "image/jpeg");
		extensiones.put("jpeg", "image/jpeg");
	       
		int idNode =0;
		
		Usuario datosUsuario = null;
		
		if (archivo == null) {
			throw new ErrorDeAplicaqcion("Bytes de Archivo no enviado *** no se puede ejecutar el servicio");
			//return;
		}
		
		if (archivo.length == 0) {
			
			throw new ErrorDeAplicaqcion("Bytes de Archivo no pueden estar vacio *** no se puede ejecutar el servicio");
			
			
		}
		
		
			
		if (nameFile == null) {
			throw new ErrorDeAplicaqcion("Nombre del archivo Null no valida *** no se puede ejecutar el servicio");
			//return;
		}
		if (nameFile.isEmpty()) {
			throw new ErrorDeAplicaqcion("Nombre del archivo vacio no valida *** no se puede ejecutar el servicio");
			//return;
		}
	
		
		if (extension == null) {
			throw new ErrorDeAplicaqcion("Extension Null no valida *** no se puede ejecutar el servicio");
		}
		if (extensiones.get(extension) == null) { // es el archivo original
			throw new ErrorDeAplicaqcion("Extension no valida *** no se puede ejecutar el servicio");
			
		}
		
		


		datosUsuario = CrearDocumento.obtenerDatosUsuario(mailUsuarioRecibe);
		if (datosUsuario==null) {
			throw new ErrorDeAplicaqcion("Email No encontrado o no contiene datos");
			
		}
		CrearDocumento.crearDocumentoBorrador(datosUsuario, nameFile, archivo, descript, palabraClave, extension);
		
		idNode = datosUsuario.getIdNode();
				
		if (idNode == 0) {
			throw new ErrorDeAplicaqcion("El Id Node Service/Carpeta no encontrado o no contiene datos");
		}

		
		

	}

	//enviar mensaje de Whasat
	//(byte[] archivo, String nombreArchivo, String mensaje, String telefono
	public void enviarMensajeWhatsapp( String mensaje, String telefono) throws ErrorDeAplicaqcion, SQLException, IOException	{
		/*
		if(1==1) {
			
			 DefaultHttpClient client = null;
			client = new DefaultHttpClient(this.simpleClientConnMng);
			httpPostReq = new HttpPost("http://localhost:3005/api/whatsapp/send"); 
			System.out.println("------------!JSON_STRING---------------");
			System.out.println(mensaje);
			System.out.println(telefono);
			String JSON_STRING="{"
					+COMILLA+"numeroDestino"+COMILLA+":"+COMILLA+telefono+COMILLA+","
					+ COMILLA+"mensaje"+COMILLA+":"+COMILLA+mensaje+COMILLA
					+ "}";
			  System.out.println(JSON_STRING);
		    HttpEntity stringEntity = new StringEntity(JSON_STRING,ContentType.APPLICATION_JSON);
		    httpPostReq.setEntity(stringEntity);
			httpPostReq.addHeader("accept", "application/json");
			//httpPostReq.setDoAuthentication(true);
			httpPostReq.addHeader("content-type", "application/json");		
			//httpPostReq.addHeader("Accept", getAccept());		
			httpPostReq.addHeader("Keep-Alive", "300");		
			httpPostReq.addHeader("Connection", "Keep-Alive");
			try {
				response = client.execute(httpPostReq);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//	OkHttpClient client = new OkHttpClient();
			  System.out.println("----------------response--------client.connectionPool()----------------------------");
			  System.out.println(response );
			  System.out.println(httpPostReq);
			  System.out.println("------------------------client.connectionPool()----------------------------");
		}

		
		try {
			
				throw new ErrorDeAplicaqcion("Error en servicio externo de whastsAPP");
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorDeAplicaqcion e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
		
		*/
		if (telefono == null) {
			throw new ErrorDeAplicaqcion("Es necesario El Telefono no se puede ejecutar el servicio");
		}
		if (telefono.isEmpty()) {
			
			throw new ErrorDeAplicaqcion("Es necesario El Telefono no pueden estar vacio *** no se puede ejecutar el servicio");
			
			
		}
		
		if (mensaje == null) {
			throw new ErrorDeAplicaqcion("Es necesario El mensaje no se puede ejecutar el servicio");
		}
		
		if (mensaje.isEmpty()) {
			
			throw new ErrorDeAplicaqcion("Es necesario El mensaje no pueden estar vacio *** no se puede ejecutar el servicio");
			
			
		}
		//if (!ToolsHTML.isEmptyOrNull(value)) {
		if (!ToolsHTML.isEmptyOrNull(mensaje) && !ToolsHTML.isEmptyOrNull(telefono) ) {
			enviarPostWhastApp(telefono, mensaje);
		}

	
		

	}
	
	
	@SuppressWarnings("unlikely-arg-type")
	void enviarPostWhastApp(String telefono,String mensaje) throws ErrorDeAplicaqcion,  IOException, ClientProtocolException {
		DefaultHttpClient client = null;
		 boolean respuesta = false;
		client = new DefaultHttpClient(this.simpleClientConnMng);
		//aqui url lo puedo mandar como un parametro
		httpPostReq = new HttpPost("http://localhost:3005/api/whatsapp/send"); 
		
	    /*body y encabezados*/
		String JSON_STRING="{"
				+COMILLA+"numeroDestino"+COMILLA+":"+COMILLA+telefono+COMILLA+","
				+ COMILLA+"mensaje"+COMILLA+":"+COMILLA+mensaje+COMILLA
				+ "}";
		  System.out.println(JSON_STRING);
	    HttpEntity stringEntity = new StringEntity(JSON_STRING,ContentType.APPLICATION_JSON);
	    httpPostReq.setEntity(stringEntity);
	    httpPostReq.addHeader("accept", "application/json");
		//httpPostReq.setDoAuthentication(true);
		httpPostReq.addHeader("content-type", "application/json");		
		//httpPostReq.addHeader("Accept", getAccept());		
		httpPostReq.addHeader("Keep-Alive", "300");		
		httpPostReq.addHeader("Connection", "Keep-Alive");
		
		try {
			response = client.execute(httpPostReq);
			respuesta = response.toString().contains(HttpStatus.SC_OK+"");
			if (!respuesta) { 
			throw new  ErrorDeAplicaqcion ("Error en servicio externo de whastsAPP "
					+ "\r\n"
					+ response);
			}
			System.out.println("-------------response------------");
			System.out.println(respuesta);
			System.out.println(response);
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			throw new  ClientProtocolException ("Error en servicio externo de whastsAPP "+ "\r\n"
					+ e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new  IOException ("Error en servicio externo de whastsAPP "+ "\r\n"
					+ e);
		}
		
		System.out.println("-------------response------------");
		System.out.println(respuesta);
		System.out.println(response);
		

	}

	
	
}
