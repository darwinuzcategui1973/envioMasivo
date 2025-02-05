package com.desige.webDocuments.accion.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.accion.forms.ActionRecommended;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.sacop.forms.ProcesosSacop;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.dao.ActionRecommendedDAO;
import com.focus.qweb.to.ActionRecommendedTO;

/**
 * Created by IntelliJ IDEA. User: Administrador Date: 20/03/2006 Time: 10:45:52
 * AM To change this template use File | Settings | File Templates.
 */
public class UpdateActionRecommendedAction extends SuperAction {
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {
		
		super.init(mapping, form, request, response);
		String cmd = request.getParameter("cmd");
		String nombreActionRecommended = request.getParameter("descActionRecommended");
		String idRegisterClass = request.getParameter("idRegisterClass");

		ActionRecommended forma = (ActionRecommended) form;
		forma.setDescActionRecommended(nombreActionRecommended != null?nombreActionRecommended:"");
		forma.setIdRegisterClass(ToolsHTML.isNumeric(idRegisterClass)?Integer.parseInt(idRegisterClass):0);;

		request.getSession().setAttribute("otroProcesoSacop", form);
		try {
			getUserSession();
			if (ToolsHTML.checkValue(cmd)) {
				if (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad)) {
					form = new ProcesosSacop();
					cmd = SuperActionForm.cmdInsert;
				} else {
					if (cmd.equalsIgnoreCase(SuperActionForm.cmdNew)) {
						removeObjectSession("valor");
					}
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
	private static boolean processCmd(ActionRecommended forma, HttpServletRequest request) throws ApplicationExceptionChecked {
		//System.out.println("forma = " + forma.getCmd());
		boolean resp = false;
		StringBuffer mensaje = null;
		ResourceBundle rb = ToolsHTML.getBundle(request);
		ActionRecommendedDAO oActionRecommendedDAO = new ActionRecommendedDAO();

		
		/*
		if ((!SuperActionForm.cmdDelete.equalsIgnoreCase(forma.getCmd())) && (!noExisteActionRecommended(forma))) {
			request.setAttribute("info", rb.getString("reg.existe"));
			forma.cleanForm();
			return true;	
		}
		*/

		if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)) {
			//System.out.println("Insertar Registro...");
			try {
				if (noExisteActionRecommended(forma, false)) {
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
				if (noExisteActionRecommended(forma, true)) {
					if (noExisteActionRecommendedenSacop(forma)) {
						//System.out.println("Editando Registro...");
						
						ActionRecommendedTO oActionRecommendedTO = new ActionRecommendedTO(forma);
						try {
							oActionRecommendedDAO.actualizar(oActionRecommendedTO);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						request.setAttribute("info", rb.getString("app.editOk"));
					} else {
						ActionRecommended frmActionRecommended = new ActionRecommended();
						frmActionRecommended.setIdActionRecommended(forma.getIdActionRecommended());
						try {
							HandlerProcesosSacop.load(frmActionRecommended);
							forma.setDescActionRecommended(frmActionRecommended.getDescActionRecommended());
						} catch (Exception e) {
							e.printStackTrace();
							throw new ApplicationExceptionChecked(e.getMessage());
						}

						ActionRecommendedTO oActionRecommendedTO = new ActionRecommendedTO(forma);
						try {
							oActionRecommendedDAO.actualizar(oActionRecommendedTO);
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
						//mensaje.append(" ").append(HandlerProcesosSacop.getMensaje());
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

	public static boolean noExisteActionRecommended(ActionRecommended forma, boolean isEdit) {
		Connection con;
		PreparedStatement st;
		ResultSet rs;
		boolean swNoExiste = true;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT * FROM actionrecommended WHERE descActionRecommended='").append(forma.getDescActionRecommended().trim()).append("'");
			
			if(isEdit){
				sql.append(" AND idActionRecommended <> ").append(forma.getIdActionRecommended());
			}
			
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			if (rs.next()) {
				swNoExiste = false;
				forma.setIdActionRecommended(Integer.parseInt(rs.getString("idarea")));
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

	private static boolean noExisteActionRecommendedenSacop(ActionRecommended forma) {
		Connection con;
		PreparedStatement st;
		ResultSet rs;
		boolean swNoExiste = true;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT p1.idActionRecommended FROM actionrecommended ta , tbl_planillasacop1 p1 WHERE ta.idActionRecommended=p1.idActionRecommended AND ta.idActionRecommended=").append(forma.getIdActionRecommended()).append("");
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
