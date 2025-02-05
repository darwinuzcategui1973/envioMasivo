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
import com.focus.qweb.dao.ProgramAuditDAO;
import com.focus.qweb.to.ProgramAuditTO;

/**
 * Created by IntelliJ IDEA. User: Administrador Date: 20/03/2006 Time: 10:45:52
 * AM To change this template use File | Settings | File Templates.
 */
public class UpdateProgramAuditAction extends SuperAction {
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {
		
		super.init(mapping, form, request, response);
		String cmd = request.getParameter("cmd");

		ProgramAudit forma = (ProgramAudit) form;
		//forma.setNameProgram(nombreProgramAudit != null?nombreProgramAudit:"");
		//forma.setIdRegisterClass(ToolsHTML.isNumeric(idRegisterClass)?Integer.parseInt(idRegisterClass):0);;
		
		try {
			putObjectSession("normasPadre",HandlerNorms.getAllNormasPrincipales(null).values());
			putObjectSession("usuarios",HandlerDBUser.getAllUsers(null,null));
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		request.getSession().setAttribute("frmProgramAudit1", form);
		try {
			getUserSession();
			if (ToolsHTML.checkValue(cmd)) {
				if (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad)) {
					form = new ProgramAudit();
					
					((ProgramAudit)form).cleanForm();
					((ProgramAudit)form).setIdNorm(HandlerParameters.PARAMETROS.getIdNormAudit());
					
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
	private static boolean processCmd(ProgramAudit forma, HttpServletRequest request) throws ApplicationExceptionChecked {
		//System.out.println("forma = " + forma.getCmd());
		boolean resp = false;
		StringBuffer mensaje = null;
		ResourceBundle rb = ToolsHTML.getBundle(request);
		ProgramAuditDAO oProgramAuditDAO = new ProgramAuditDAO();

		
		/*
		if ((!SuperActionForm.cmdDelete.equalsIgnoreCase(forma.getCmd())) && (!noExisteProgramAudit(forma))) {
			request.setAttribute("info", rb.getString("reg.existe"));
			forma.cleanForm();
			return true;	
		}
		*/

		if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)) {
			//System.out.println("Insertar Registro...");
			try {
				if (noExisteProgramAudit(forma, false)) {
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
				
				ProgramAuditTO oProgramAuditTO = new ProgramAuditTO(forma);
				try {
					oProgramAuditDAO.actualizar(oProgramAuditTO);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				request.setAttribute("info", rb.getString("app.editOk"));
				
				return true;
			} else if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdClosed)) {
					//System.out.println("Cerrando el programa...");
					
					ProgramAuditTO oProgramAuditTO = new ProgramAuditTO(forma);
					try {
						// verificamos que no haya planes abiertos
						if(!PlanAuditDAO.isProgramHasPlanOpen(oProgramAuditTO.getIdProgramAuditInt())) {
							oProgramAuditTO.setStatus("C");
							oProgramAuditDAO.actualizar(oProgramAuditTO);
							request.setAttribute("info", rb.getString("app.editOk"));
						} else {
							request.setAttribute("info", rb.getString("app.notProgramClose"));
						}
					} catch (Exception e) {
						request.setAttribute("info", rb.getString("app.notProgramClose"));
						e.printStackTrace();
					}
					
					
					return true;
			} else {
				if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdDelete)) {
					//System.out.println("Elimando registro....");
					if (ProgramAuditDAO.delete(forma)) {
						forma.cleanForm();
						request.setAttribute("info", rb.getString("app.delete"));
					} else {
						mensaje = new StringBuffer(rb.getString("app.notDelete"));
						//mensaje.append(" ").append(HandlerProgramAudit.getMensaje());
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

	public static boolean noExisteProgramAudit(ProgramAudit forma, boolean isEdit) {
		Connection con;
		PreparedStatement st;
		ResultSet rs;
		boolean swNoExiste = true;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT * FROM programaudit WHERE nameProgram='").append(forma.getNameProgram().trim()).append("'");
			
			if(isEdit){
				sql.append(" AND idProgramAudit <> ").append(forma.getIdProgramAudit());
			}
			
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			if (rs.next()) {
				swNoExiste = false;
				forma.setIdProgramAudit(Integer.parseInt(rs.getString("idarea")));
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
