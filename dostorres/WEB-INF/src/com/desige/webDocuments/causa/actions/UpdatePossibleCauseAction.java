package com.desige.webDocuments.causa.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.causa.forms.PossibleCause;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.sacop.forms.ProcesosSacop;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.dao.PossibleCauseDAO;
import com.focus.qweb.to.PossibleCauseTO;

/**
 * Created by IntelliJ IDEA. User: Administrador Date: 20/03/2006 Time: 10:45:52
 * AM To change this template use File | Settings | File Templates.
 */
public class UpdatePossibleCauseAction extends SuperAction {
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {
		
		super.init(mapping, form, request, response);
		String cmd = request.getParameter("cmd");
		String nombrePossibleCause = request.getParameter("nombrePossibleCause");

		PossibleCause forma = (PossibleCause) form;
		if (nombrePossibleCause != null) {
			forma.setDescPossibleCause(nombrePossibleCause);
		}

		request.getSession().setAttribute("otroProcesoSacop", form);
		try {
			getUserSession();
			if (ToolsHTML.checkValue(cmd)) {
				if ((cmd.equalsIgnoreCase(SuperActionForm.cmdNew)) || (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))) {
					if (cmd.equalsIgnoreCase(SuperActionForm.cmdNew)) {
						removeObjectSession("valor");
					}
					form = new ProcesosSacop();
					cmd = SuperActionForm.cmdInsert;
				} else {
					processCmd(forma, request);
					cmd = SuperActionForm.cmdLoad;
				}
				((SuperActionForm) form).setCmd(cmd);
				request.setAttribute("cmd", cmd);
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
	
	/**
	 * 
	 * @param forma
	 * @param request
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	private static boolean processCmd(PossibleCause forma, HttpServletRequest request) throws ApplicationExceptionChecked {
		//System.out.println("forma = " + forma.getCmd());
		boolean resp = false;
		StringBuffer mensaje = null;
		ResourceBundle rb = ToolsHTML.getBundle(request);
		PossibleCauseDAO oPossibleCauseDAO = new PossibleCauseDAO();

		
		/*
		if ((!SuperActionForm.cmdDelete.equalsIgnoreCase(forma.getCmd())) && (!noExistePossibleCause(forma))) {
			request.setAttribute("info", rb.getString("reg.existe"));
			forma.cleanForm();
			return true;	
		}
		*/

		if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)) {
			//System.out.println("Insertar Registro...");
			try {
				if (noExistePossibleCause(forma, false)) {
					resp = HandlerProcesosSacop.insert(forma);
				} else {
					request.setAttribute("info", rb.getString("reg.existe"));
					forma.cleanForm();
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (resp) {
				request.setAttribute("info", rb.getString("app.editOk"));
				forma.cleanForm();
				return true;
			} else {
				mensaje = new StringBuffer(rb.getString("app.notEdit"));
				mensaje.append(" ").append(HandlerTypeDoc.getMensaje());
			}
		} else {
			if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdEdit)) {
				if (noExistePossibleCause(forma, true)) {
					if (noExistePossibleCauseenSacop(forma)) {
						//System.out.println("Editando Registro...");
						
						PossibleCauseTO oPossibleCauseTO = new PossibleCauseTO(forma);
						try {
							oPossibleCauseDAO.actualizar(oPossibleCauseTO);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						request.setAttribute("info", rb.getString("app.editOk"));
					} else {
						PossibleCause frmPossibleCause = new PossibleCause();
						frmPossibleCause.setIdPossibleCause(forma.getIdPossibleCause());
						try {
							HandlerProcesosSacop.load(frmPossibleCause);
							forma.setDescPossibleCause(frmPossibleCause.getDescPossibleCause());
						} catch (Exception e) {
							e.printStackTrace();
							throw new ApplicationExceptionChecked(e.getMessage());
						}

						PossibleCauseTO oPossibleCauseTO = new PossibleCauseTO(forma);
						try {
							oPossibleCauseDAO.actualizar(oPossibleCauseTO);
						} catch (Exception e) {
							e.printStackTrace();
						}

						//request.setAttribute("info", rb.getString("app.editOk"));
						request.setAttribute("info", rb.getString("scp.areaensacop"));
					}
				} else {
					//existe el area con un id distinto y el mismo nombre que se le quiere colocar a esta
					request.setAttribute("info", rb.getString("reg.existe"));
					forma.cleanForm();
				}
				
				return true;
			} else {
				if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdDelete)) {
					//System.out.println("Elimando registro....");
					if (HandlerProcesosSacop.delete(forma)) {
						forma.cleanForm();
						request.setAttribute("info", rb.getString("app.delete"));
					} else {
						mensaje = new StringBuffer(rb.getString("app.notDelete"));
						//mensaje.append(" ").append(HandlerTypeDoc.getMensaje());
					}
				}
			}
		}
		if (mensaje != null) {
			//System.out.println("mensaje = " + mensaje);
			request.setAttribute("info", mensaje.toString());
		}
		return false;
	}

	public static boolean noExistePossibleCause(PossibleCause forma, boolean isEdit) {
		Connection con;
		PreparedStatement st;
		ResultSet rs;
		boolean swNoExiste = true;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT * FROM possiblecause WHERE descPossibleCause='").append(forma.getDescPossibleCause().trim()).append("'");
			
			if(isEdit){
				sql.append(" AND idPossibleCause <> ").append(forma.getIdPossibleCause());
			}
			
			//System.out.println("sql=" + sql.toString());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			if (rs.next()) {
				swNoExiste = false;
				forma.setIdPossibleCause(Integer.parseInt(rs.getString("idPossibleCause")));
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

	private static boolean noExistePossibleCauseenSacop(PossibleCause forma) {
		Connection con;
		PreparedStatement st;
		ResultSet rs;
		boolean swNoExiste = true;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT p1.idPossibleCause FROM possiblecause ta , tbl_planillasacop1 p1 WHERE ta.idPossibleCause=p1.idPossibleCause AND ta.idPossibleCause=").append(forma.getIdPossibleCause()).append("");
			//System.out.println("sql=" + sql.toString());
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

}
