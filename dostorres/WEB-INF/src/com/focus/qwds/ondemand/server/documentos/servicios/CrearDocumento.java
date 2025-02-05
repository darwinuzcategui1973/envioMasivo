package com.focus.qwds.ondemand.server.documentos.servicios;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerStruct;

import com.desige.webDocuments.utils.ApplicationExceptionChecked;

import com.desige.webDocuments.utils.StringUtil;

import com.focus.qwds.ondemand.server.excepciones.ErrorDeAplicaqcion;
import com.focus.qwds.ondemand.server.usuario.entidades.Usuario;
import com.focus.qwds.ondemand.server.utils.JDBCUtils;

public class CrearDocumento {

	static Logger log = LoggerFactory.getLogger(CrearDocumento.class.getName());

	public CrearDocumento() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static Usuario obtenerDatosUsuario(String mailUsuarioRecibe) throws ErrorDeAplicaqcion {

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Usuario result = null;
		int idPerson = 0;
		int idNode = 0;
		String usuarioNombre = "";

		try {
			
			log.info("Inicio de  CrearDocumento... y mail recibe..:" + mailUsuarioRecibe);
			if (mailUsuarioRecibe != null) { 
				
				con = JDBCUtils.getConnection();
			

				pstm = con.prepareStatement("SELECT idPerson,nameUser,idNodeService FROM person WHERE email=?");
				pstm.setString(1, mailUsuarioRecibe);

				rs = pstm.executeQuery();

				if (rs.next()) {

					result = new Usuario();
					
					idPerson = rs.getInt(1);
					usuarioNombre = rs.getString(2);
					idNode = rs.getInt(3);

					result.setId(idPerson);
					result.setNombreUsuario(usuarioNombre);
					result.setIdNode(idNode);
			

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
		log.info("fin de  CrearDocumento...:" + usuarioNombre);
		
		return result;
	}

	public static void crearDocumentoBorrador(Usuario datosUsuario, String nombreArchivo, byte[] archivo, String descript, String palabraClave, String extension ) {

		BaseDocumentForm forma = new BaseDocumentForm();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date fecha = new Date();
		String fechaCadena = sdf.format(fecha);

		// extraemos la extension del archivo
		//StringBuffer nombre = new StringBuffer(StringUtil.getOnlyNameFile(nombreArchivo));
		//nombre = nombre.reverse();
		//StringBuffer extension = new StringBuffer(nombre.substring(0, nombre.indexOf(".") + 1));
		//extension = extension.reverse();
		//String extensionTxt = extension.toString();

		// inicializamos los datos de documento
		forma.setIdDocument("000"); 
		// forma.setDescript("Documentos desde Servicio ");
		forma.setNumberGen("000");
		forma.setDateCreation(fechaCadena);
		forma.setNameDocument(nombreArchivo);
		forma.setNameFile(nombreArchivo+"."+extension);
		forma.setIdNode(String.valueOf(datosUsuario.getIdNode()));
		forma.setOwner(datosUsuario.getNombreUsuario());
		forma.setNameOwner(datosUsuario.getNombreUsuario());
		forma.setDescript(descript);
		forma.setComments("Servicio Externo De Documentos");
		forma.setContextType(extension);
		forma.setDocPublic("1");
		forma.setDocOnline("1");
		forma.setTypeDocument("1");
		forma.setMayorVer("0");
		forma.setMinorVer("0");
		forma.setNumber("");
		forma.setDateApproved(null);
		forma.setDatePublic(null);
		forma.setDateExpires("9999-12-31 12:59:59 pm");
		forma.setApproved(null);
		forma.setIdRegisterClass(0);
		forma.setKeys(palabraClave);

		
		InputStream inputStream = new ByteArrayInputStream(archivo);

		try {

			log.info("Begin crearDocumentoBorrador...");

			HandlerStruct.createRegisterExternalService(forma, String.valueOf(datosUsuario.getId()), inputStream);
		} catch (ApplicationExceptionChecked e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.info("fin crearDocumentoBorrador...");

	}

}
