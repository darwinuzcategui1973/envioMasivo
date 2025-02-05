package com.desige.webDocuments.norms.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.desige.webDocuments.norms.forms.BaseNormsForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerNorms;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: ${name}.java<br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * 
 * @author Nelson Crespo (NC)
 * @version Acsel-e v2.2 <br>
 *          Changes:<br>
 *          <ul>
 *          <li>22/08/2004 (NC) Creation</li>
 *          <li>18/04/2006 (SR) Se creo un metodo</li>
 *          <ul>
 */
public class UpdateNormsAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger("[V4.0 FTP] "
			+ UpdateNormsAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);
		String cmd = request.getParameter("cmd");
		log.debug("[UpdateNormsAction] cmd = " + cmd);
		try {
			getUserSession();
			BaseNormsForm forma = (BaseNormsForm) form;
			forma.setContextType(null);
			forma.setFileNameFisico(null);
			forma.setPath(null);
			// en tal caso que traiga un archivo lo procesamos
			procesaFicheros(request, forma);
			if (ToolsHTML.checkValue(cmd)) {
				if ((cmd.equalsIgnoreCase(SuperActionForm.cmdNew))
						|| (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))) {
					form = new BaseNormsForm();
					putObjectSession("newNorms", form);
					cmd = SuperActionForm.cmdInsert;
				} else {
					if (processCmd(forma, request)) {
						return goSucces();
					}
				}
				((BaseNormsForm) form).setCmd(cmd);
				if (!SuperActionForm.cmdInsert.equalsIgnoreCase(cmd)) {
					request.setAttribute("cmd", SuperActionForm.cmdLoad);
				} else {
					request.setAttribute("cmd", cmd);
					// return goTo("newNorm");
					return goSucces();
				}
			}
			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			ae.printStackTrace();
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return goError();
	}

	private static boolean processCmd(BaseNormsForm forma,
			HttpServletRequest request) throws ApplicationExceptionChecked,
			Exception {
		log.debug("forma = " + forma.getCmd());
		boolean resp = false;
		StringBuffer mensaje = null;
		ResourceBundle rb = ToolsHTML.getBundle(request);
		if (SuperActionForm.cmdInsert.equalsIgnoreCase(forma.getCmd())) {
			if (!noExisteNorma(forma)) {
				request.setAttribute("info", rb.getString("norms.existe"));
				forma.cleanForm();
				return true;
			}
		}
		if (SuperActionForm.cmdEdit.equalsIgnoreCase(forma.getCmd())) {
			String[] items = HandlerBD.getField(new String[] { "idNorm" },
					"norms", new String[] { "titleNorm", "idNorm" },
					new String[] { forma.getName(), forma.getId() },
					new String[] { "=", "<>" }, new Object[] { "",
							new Integer(1) });
			if (items != null) {
				throw new ApplicationExceptionChecked("norms.existe");
			}
		}
		if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)) {
			log.debug("Insertar Registro...");
			try {
				// if (noExisteNorma(forma)) {
				resp = HandlerNorms.insert(forma);
				// } else {
				// request.setAttribute("info",rb.getString("norms.existe"));
				// forma.cleanForm();
				// return true;
				// }
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (resp) {
				request.setAttribute("info", rb.getString("app.editOk"));
				forma.cleanForm();
				return true;
			} else {
				mensaje = new StringBuffer(rb.getString("app.notEdit"));
				mensaje.append(" ").append(HandlerNorms.getMensaje());
			}
		} else {
			if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdEdit)) {
				log.debug("Editando Registro...");
				// if (noExisteNorma(forma)){
				if (HandlerNorms.edit(forma)) {
					request.setAttribute("info", rb.getString("app.editOk"));
					return true;
				} else {
					mensaje = new StringBuffer(rb.getString("app.notEdit"));
					mensaje.append(" ").append(HandlerNorms.getMensaje());
				}
				/*
				 * }else{
				 * request.setAttribute("info",rb.getString("norms.existe"));
				 * forma.cleanForm(); return true; }
				 */
			} else {
				if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdDelete)) {
					log.debug("Elimando registro....");
					if (HandlerNorms.delete(forma)) {
						forma.cleanForm();
						request.setAttribute("info", rb.getString("app.delete"));
					} else {
						mensaje = new StringBuffer(
								rb.getString("app.notDelete"));
						mensaje.append(" ").append(HandlerNorms.getMensaje());
					}
				}
			}
		}
		if (mensaje != null) {
			log.debug("mensaje = " + mensaje);
			request.setAttribute("info", mensaje.toString());
		}
		return false;
	}

	private static boolean noExisteNorma(BaseNormsForm forma) {
		Connection con;
		PreparedStatement st;
		ResultSet rs;
		boolean swNoExiste = true;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT titleNorm FROM norms WHERE titleNorm='")
					.append(forma.getName().trim()).append("'");
			sql.append(" AND active = '").append(Constants.permission)
					.append("'");
			log.debug("sql=" + sql.toString());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			if (rs.next()) {
				swNoExiste = false;
			} else {
				swNoExiste = true;
			}
			if (con != null) {
				con.close();
			}
			if (st != null) {
				st.close();
			}
			if (rs != null) {
				rs.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return swNoExiste;
	}

	private synchronized boolean procesaFicheros(HttpServletRequest req,
			BaseNormsForm forma) {
		boolean resp = false;
		try {
			String path = ToolsHTML.getPath().concat("tmp"); // \\tmp
			forma.setPath(path);
			if (forma.getNameFile() != null
					&& forma.getNameFile().getFileSize() > 0) {
				FormFile file = forma.getNameFile();
				// ricaviamo il nome del file
				String fileName = file.getFileName();
				// ricaviamo il MIME type del file
				String contentType = file.getContentType();
				// ricaviamo le dimensioni del file (in Bytes)
				int sizeTemp = file.getFileSize();
				String size = (sizeTemp + " Bytes");
				// costruiamo una stringa che indicher� il percorso assoluto sul
				// server nel quale
				// sar� salvato il file, ad
				// esempio C:\files\mioFile.txt
				// ricaviamo i dati del file mediante un InputStream
				InputStream inStream = file.getInputStream();
				FileOutputStream outStream = new FileOutputStream(
						path.concat(File.separator) + fileName);
				// salviamo il file nel percorso specificato
				while (inStream.available() > 0) {
					outStream.write(inStream.read());
				}
				// String path = "C:\\files\\" + fileName;

				forma.setContextType(contentType);
				forma.setFileNameFisico(fileName);
				log.debug("getFileNameFisico=" + forma.getFileNameFisico()
						+ " contentType=" + forma.getContextType() + " path="
						+ forma.getPath());
				// eliminiamo il file temporaneo che abbiamo creato
				file.destroy();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}
