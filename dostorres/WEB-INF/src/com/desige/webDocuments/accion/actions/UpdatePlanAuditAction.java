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

import com.desige.webDocuments.accion.forms.PlanAudit;
import com.desige.webDocuments.accion.forms.ProgramAudit;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerNorms;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.dao.PlanAuditDAO;
import com.focus.qweb.to.PlanAuditTO;

/**
 * Created by IntelliJ IDEA. User: Administrador Date: 20/03/2006 Time: 10:45:52
 * AM To change this template use File | Settings | File Templates.
 */
public class UpdatePlanAuditAction extends SuperAction {
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {
		
		super.init(mapping, form, request, response);
		String cmd = request.getParameter("cmd");

		
		PlanAudit forma = (PlanAudit) form;
		
		ProgramAudit formaProgram = (ProgramAudit)getSessionObject("editTypeDoc");
		
		forma.setIdProgramAudit(formaProgram.getIdProgramAudit());
		try {
			putObjectSession("normasPadrePlan",HandlerNorms.getAllNormasPrincipalesProgram(null, formaProgram.getIdNormCheck()).values());
			putObjectSession("normasPadrePlanDetalle",HandlerNorms.getAllNormasPrincipalesProgramDetalle(null, formaProgram.getIdNormCheck()).values());
			putObjectSession("usuariosPlan",HandlerDBUser.getAllUsers(null,null));
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		request.getSession().setAttribute("frmPlanAudit1", form);
		try {
			getUserSession();
			if (ToolsHTML.checkValue(cmd)) {
				if (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad)) {
					form = new PlanAudit();
					
					((PlanAudit)form).cleanForm();
					((PlanAudit)form).setIdNorm(HandlerParameters.PARAMETROS.getIdNormAudit());
					
					cmd = SuperActionForm.cmdInsert;
				} else {
					if (cmd.equalsIgnoreCase(SuperActionForm.cmdNew)) {
						removeObjectSession("valorPlan");
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
	private static boolean processCmd(PlanAudit forma, HttpServletRequest request) throws ApplicationExceptionChecked {
		//System.out.println("forma = " + forma.getCmd());
		boolean resp = false;
		StringBuffer mensaje = null;
		ResourceBundle rb = ToolsHTML.getBundle(request);
		PlanAuditDAO oPlanAuditDAO = new PlanAuditDAO();

		
		/*
		if ((!SuperActionForm.cmdDelete.equalsIgnoreCase(forma.getCmd())) && (!noExistePlanAudit(forma))) {
			request.setAttribute("info", rb.getString("reg.existe"));
			forma.cleanForm();
			return true;	
		}
		*/

		if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)) {
			//System.out.println("Insertar Registro...");
			try {
				if (noExistePlanAudit(forma, false)) {
					resp = HandlerNorms.insert(forma);
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
				//System.out.println("Editando Registro...");
				
				PlanAuditTO oPlanAuditTO = new PlanAuditTO(forma);
				try {
					oPlanAuditDAO.actualizar(oPlanAuditTO);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				request.setAttribute("info", rb.getString("app.editOk"));
				
				return true;
			} else if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdClosed)) {
					PlanAuditTO oPlanAuditTO = new PlanAuditTO(forma);
					try {
						if(oPlanAuditTO.getStatusPlan().equals("A")) {
							oPlanAuditTO.setStatusPlan("C");
							oPlanAuditDAO.actualizar(oPlanAuditTO);
							request.setAttribute("info", rb.getString("app.editOk"));
							
							return true;
						} else {
							mensaje = new StringBuffer(rb.getString("app.notPlanClose"));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
			} else {
				if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdDelete)) {
					//System.out.println("Elimando registro....");
					if (HandlerNorms.delete(forma)) {
						forma.cleanForm();
						request.setAttribute("info", rb.getString("app.delete"));
					} else {
						mensaje = new StringBuffer(rb.getString("app.notDelete"));
						//mensaje.append(" ").append(HandlerPlanAudit.getMensaje());
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

	public static boolean noExistePlanAudit(PlanAudit forma, boolean isEdit) {
		Connection con;
		PreparedStatement st;
		ResultSet rs;
		boolean swNoExiste = true;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT * FROM planaudit WHERE namePlan='").append(forma.getNamePlan().trim()).append("'");
			
			if(isEdit){
				sql.append(" AND idPlanAudit <> ").append(forma.getIdPlanAudit());
			}
			
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			if (rs.next()) {
				swNoExiste = false;
				forma.setIdPlanAudit(Integer.parseInt(rs.getString("idPlanAudit")));
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
