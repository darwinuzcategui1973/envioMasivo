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

import com.desige.webDocuments.dao.ConfDocumentoDAO;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.files.forms.DocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

import sun.jdbc.rowset.CachedRowSet;

/**
 * Created by IntelliJ IDEA. User: srodriguez Date: 03/04/2006 Time: 11:29:28 AM
 * To change this template use File | Settings | File Templates.
 */

public class ChangeLocationDocument extends SuperAction {
	// private static ArrayList elementos =
	// ToolsHTML.getProperties("visor.OpenBrowser");
	// private POIFSFileSystem fs;
	public String error;
	private static Logger log = LoggerFactory.getLogger(ChangeLocationDocument.class.getName());
	private ResourceBundle rb = null;
	private String dateSystem = ToolsHTML.sdf.format(new Date());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);

		try {
			Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
			rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);

			int modeloReporte = 2;

			changeLocation(request, response);

			return goSucces();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
		}
		return goError();
	}

	private String getQuery(HttpServletRequest request) throws ApplicationExceptionChecked {
		return request.getSession().getAttribute("querySearchLocation").toString();
	}

	public void changeLocation(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// Se Escribe la Informaci�n en el Documento
		StringBuffer sql_query = new StringBuffer();
		log.debug("sql_query: " + sql_query);
		BaseDocumentForm forma = new BaseDocumentForm();
		Connection con = null;
		PreparedStatement st = null;
		boolean isChangeAllField = false;

		try {
            Users usuario = getUserSession();



			// campos adicionales en la tabla documents
			ConfDocumentoDAO conf = null;
			ArrayList lista = null;
			DocumentForm obj;
			StringBuffer camposDeUbicacion = new StringBuffer();
			HashMap listaCamposUbicacion = new HashMap();
			conf = new ConfDocumentoDAO();
			lista = (ArrayList) conf.findAll();
			
			if("1".equals(String.valueOf(HandlerParameters.PARAMETROS.getChangeAditionalField()))) {
				isChangeAllField = true;
			}

			if (lista != null) {
				for (int i = 0; i < lista.size(); i++) {
					obj = (DocumentForm) lista.get(i);
					if (obj.getLocation() == 1 || isChangeAllField) {
						listaCamposUbicacion.put(obj.getId(), obj);
						camposDeUbicacion.append(",");
						camposDeUbicacion.append(obj.getId());

						// tomamos los valores del request
						if(request.getParameter(obj.getId()) != null && !request.getParameter(obj.getId()).equals("")) {
							forma.set(obj.getId().toUpperCase(), request.getParameter(obj.getId()));
						}
					}
				}
			}

			if(camposDeUbicacion.length()==0) {
				throw new Exception("No hay campos de ubicacion definidos");
			}
			sql_query.append(getQuery(request).replaceAll("FROM documents",camposDeUbicacion.toString().concat(" FROM documents")));

			CachedRowSet crs = JDBCUtil.executeQuery(sql_query, Thread.currentThread().getStackTrace()[1].getMethodName());

			// Si fue cambiada la ubicacion se deja en la traza
			boolean isChangeLocation = false;
			Timestamp time = new Timestamp(new Date().getTime());

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);

			while (crs.next()) {
				Iterator ite = listaCamposUbicacion.values().iterator();
				String valorActual = "";
				String valorAnterior = "";
				isChangeLocation = false;
				while (ite.hasNext()) {
					obj = (DocumentForm) ite.next();
					valorAnterior = String.valueOf(crs.getString(obj.getId()) == null ? "" : crs.getString(obj.getId()));
					valorActual = (String) forma.get(obj.getId().toUpperCase());
					if (!valorAnterior.equalsIgnoreCase(valorActual)) {
						isChangeLocation = true;
						break;
					}
				}
				if (isChangeLocation) {
					int numdoc = Integer.parseInt(crs.getString("numgen"));
					int node = Integer.parseInt(ToolsHTML.isEmptyOrNull(crs.getString("idnode"), "0"));
					String historyType = "23";

					ite = listaCamposUbicacion.values().iterator();
					String razonHistory = HandlerStruct.getHistoryLocationHTML(ite, forma, crs);
					
					// actualizamos la ubicacion del documento en la tabla de documentos
					ite = listaCamposUbicacion.values().iterator();
					JDBCUtil.executeUpdate(HandlerStruct.getQueryUpdateLocation(ite, forma, crs));
					
					// actualizamos la ubicacion del documento en el historico
					HandlerDocuments.updateHistoryDocs(con, numdoc, node, 0, usuario.getUser(), time, historyType, razonHistory, new String[] { crs.getString("mayorver"), crs.getString("minorver") });
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
			return JDBCUtil.getScalar(con,"SELECT tbl_cargo.cargo FROM tbl_cargo INNER JOIN person ON tbl_cargo.idcargo = person.cargo WHERE (person.idperson = '" + idperson + "')");
		} catch (Exception e) {
			e.printStackTrace(); // To change body of catch statement use
									// File | Settings | File Templates.
			return idperson;
		}
	}

}
