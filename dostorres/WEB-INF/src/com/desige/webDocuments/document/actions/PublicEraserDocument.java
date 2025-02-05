package com.desige.webDocuments.document.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.dao.ConfDocumentoDAO;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.files.forms.DocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Created by IntelliJ IDEA. User: srodriguez Date: 03/04/2006 Time: 11:29:28 AM
 * To change this template use File | Settings | File Templates.
 */

public class PublicEraserDocument extends SuperAction {
	// private static ArrayList elementos =
	// ToolsHTML.getProperties("visor.OpenBrowser");
	// private POIFSFileSystem fs;
	public String error;
	private static Logger log = LoggerFactory.getLogger(PublicEraserDocument.class.getName());
	private ResourceBundle rb = null;
	private String dateSystem = ToolsHTML.sdf.format(new Date());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);

		try {
			Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
			rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);

			int modeloReporte = 2;

			publicEraser(request, response);

			return goSucces();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
		}
		return goError();
	}

	private String getQuery(HttpServletRequest request) throws ApplicationExceptionChecked {
		return request.getSession().getAttribute("querySearchReport").toString();
	}

	public void publicEraser(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// Se Escribe la Informaci�n en el Documento
		StringBuffer sql_query = new StringBuffer();
		BaseDocumentForm forma = new BaseDocumentForm();
		Connection con = null;
		PreparedStatement st = null;

		try {
            Users usuario = getUserSession();
			sql_query.append(getQuery(request)); // el query a ejecutar

			CachedRowSet crs = JDBCUtil.executeQuery(sql_query, Thread.currentThread().getStackTrace()[1].getMethodName());

			// Si fue cambiada la ubicacion se deja en la traza
			boolean isChangeLocation = false;
			Timestamp time = new Timestamp(new Date().getTime());

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);
			
			String idDocument;
			String comments = request.getParameter("comentarios");

			while (crs.next()) {
				idDocument = crs.getString("numgen");
				
				// comprobamos si es el primer borrador de este documento
				if(HandlerDocuments.isUniqueVersionEraser(crs.getInt("numgen"))) {
					// publicamos
					HandlerDocuments.publicDocumentEraser(idDocument,comments);
				}
				
			}

		} catch (Exception ex) {
	        if (con!=null){
	            try {
	                con.rollback();
	            } catch (Exception e) {
	            }
	        }
			ex.printStackTrace();
			HandlerBD.setMensaje(ex.getMessage());
		} finally {
	        if (con!=null) {
	            try {
	            	con.commit();
	                con.setAutoCommit(true);
	                log.debug("connection setAutoCommit");
	            } catch (Exception e) {
	                log.debug("Error in setAutoCommit connection");
	            }
	            try {
	                con.close();
	            } catch (Exception e) {
	                log.debug("Error cerrando Connection...");
	            }
	        }
		}
	}

	private String getCargo(Connection con, String idperson) {

		try {
			return JDBCUtil.getScalar(con, "SELECT tbl_cargo.cargo FROM tbl_cargo INNER JOIN person ON tbl_cargo.idcargo = person.cargo WHERE (person.idperson = '" + idperson + "')");
		} catch (Exception e) {
			e.printStackTrace(); // To change body of catch statement use
									// File | Settings | File Templates.
			return idperson;
		}
	}

}
