package com.desige.webDocuments.area.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.area.forms.Area;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.sacop.forms.ProcesosSacop;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.dao.AreaDAO;
import com.focus.qweb.to.AreaTO;

/**
 * Created by IntelliJ IDEA. User: Administrador Date: 20/03/2006 Time: 10:45:52
 * AM To change this template use File | Settings | File Templates.
 */
public class UpdateAreaAction extends SuperAction {
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {
		
		super.init(mapping, form, request, response);
		String cmd = request.getParameter("cmd");
		String nombreArea = request.getParameter("nombreArea");
		String prefijoArea = request.getParameter("prefijoArea");

		Area forma = (Area) form;
		if (nombreArea != null) {
			forma.setArea(nombreArea);
		}
		if (prefijoArea != null) {
			forma.setPrefijo(prefijoArea);
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

			if (String.valueOf(request.getParameter("redirect")).equals("cargo")) {
				ActionForward af = new ActionForward(); // "loadCargo.do?idarea="+forma.getIdarea());
				af.setPath("/loadCargo.do?idarea=" + forma.getIdarea());
				return af;
				// return mapping.findForward("successCargo");
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
	private static boolean processCmd(Area forma, HttpServletRequest request) throws ApplicationExceptionChecked {
		//System.out.println("forma = " + forma.getCmd());
		boolean resp = false;
		StringBuffer mensaje = null;
		ResourceBundle rb = ToolsHTML.getBundle(request);
		AreaDAO oAreaDAO = new AreaDAO();

		
		/*
		if ((!SuperActionForm.cmdDelete.equalsIgnoreCase(forma.getCmd())) && (!noExisteArea(forma))) {
			request.setAttribute("info", rb.getString("reg.existe"));
			forma.cleanForm();
			return true;	
		}
		*/

		if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)) {
			//System.out.println("Insertar Registro...");
			try {
				if (noExisteArea(forma, false)) {
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
				if (noExisteArea(forma, true)) {
					if (noExisteAreaenSacop(forma)) {
						//System.out.println("Editando Registro...");
						forma.setActive(Constants.permission);
						
						AreaTO oAreaTO = new AreaTO(forma);
						try {
							oAreaDAO.actualizar(oAreaTO);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						request.setAttribute("info", rb.getString("app.editOk"));
					} else {
						Area frmArea = new Area();
						frmArea.setIdarea(forma.getIdarea());
						try {
							HandlerProcesosSacop.load(frmArea);
							forma.setArea(frmArea.getArea());
						} catch (Exception e) {
							e.printStackTrace();
							throw new ApplicationExceptionChecked(e.getMessage());
						}

						forma.setActive(Constants.permission);

						AreaTO oAreaTO = new AreaTO(forma);
						try {
							oAreaDAO.actualizar(oAreaTO);
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
						mensaje.append(" ").append(HandlerTypeDoc.getMensaje());
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

	public static boolean noExisteArea(Area forma, boolean isEdit) {
		Connection con;
		PreparedStatement st;
		ResultSet rs;
		boolean swNoExiste = true;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT area,idarea FROM tbl_area WHERE area='").append(forma.getArea().trim()).append("'");
			
			if(isEdit){
				sql.append(" AND idarea <> ").append(forma.getIdarea());
			}
			
			//sql.append(" and prefijo='").append(forma.getPrefijo().trim()).append("'");
			sql.append(" and activea='").append(Constants.permission).append("'");
			//System.out.println("sql=" + sql.toString());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			if (rs.next()) {
				swNoExiste = false;
				forma.setIdarea(Integer.parseInt(rs.getString("idarea")));
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

	private static boolean noExisteAreaenSacop(Area forma) {
		Connection con;
		PreparedStatement st;
		ResultSet rs;
		boolean swNoExiste = true;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT area FROM tbl_area ta , tbl_planillasacop1 p1 WHERE ta.idarea=").append(forma.getIdarea()).append("");
			sql.append(" AND ta.idarea=p1.usernotificado");
			sql.append(" and p1.active='").append(Constants.permission).append("'");
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
