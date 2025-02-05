package com.desige.webDocuments.workFlows.actions;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.mail.forms.MailForm;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.mail.SendMail;
import com.focus.util.NetworkInfo;

public class SolicitarLicenciaAction extends Action {

	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		try {
			ResourceBundle rb = ToolsHTML.getBundle(request);
			PrintWriter out = response.getWriter();

			String emp = request.getParameter("emp");
			String rif = request.getParameter("rif");
			String mac = NetworkInfo.getSerialMac();//NetworkInfo.getMacAddress();
			String os  = System.getProperty("os.name") + ", UserAgent=" + request.getHeader("User-Agent");
			String ver = rb.getString("lic.version");
			String host = request.getServerName(); ;
			String user = System.getProperty("user.name");  ;

			if (emp == null || emp.trim().equals(""))
				return null;
			if (rif == null || rif.trim().equals(""))
				return null;
			if (mac == null || mac.trim().equals(""))
				return null;
			
			// validamos la codificacion de la licencia
			
			// crearemos el archivo que almacenara la licencia
			StringBuffer cad = new StringBuffer();
			cad.append("<html>");
			cad.append("<body style='color:white;font-family:Arial;'>");
			cad.append("<center>");
			cad.append("<div id='fondo2' class='centerbox' style='width:600px;padding-top:10px;border:1px solid #000;'>");
			cad.append("	<div style='position:abolute;margin-top:-12px;height:23px;border:0px solid white;width:612px;text-align:left;background-color:white;'>");
			cad.append("		<table border='0' cellspacing='0' cellpadding='0' width='100%' style='padding-top:3px;'>");
			cad.append("			<tr>");
			cad.append("				<td aling='left' colspan='2'><b style='font-family:Tahoma;font-size:10pt;color:#000;width:600px;'>&nbsp;CONFORMACION DE LICENCIA - Qwebdocuments</b></td>");
			cad.append("			</tr>");
			cad.append("		</table>");
			cad.append("	</div>");
			cad.append("	<div id='error' style='padding:10px;background-color:#045FB4;'>");
			cad.append("		<table border='0' width='100%' cellpadding='2' cellspacing='5' >");
			cad.append("			<tr>");
			cad.append("				<td class='titleLeft left' width='30%'>Nombre de la Empresa : </td>");
			cad.append("				<td class='titleLeft left'>").append(emp).append("</td>");
			cad.append("			</tr>");
			cad.append("			<tr>");
			cad.append("				<td class='titleLeft left'>Numero de R.I.F. : </td>");
			cad.append("				<td class='titleLeft left'>").append(rif).append("</td>");
			cad.append("			</tr>");
			cad.append("			<tr>");
			cad.append("				<td class='titleLeft left'>Serial de M&aacute;quina: </td>");
			cad.append("				<td class='titleLeft left'>").append(mac).append("</td>");
			cad.append("			</tr>");
			cad.append("			<tr>");
			cad.append("				<td class='titleLeft left'>Sistema Operativo : </td>");
			cad.append("				<td class='titleLeft left'>").append(os).append("</td>");
			cad.append("			</tr>");
			cad.append("			<tr>");
			cad.append("				<td class='titleLeft left'>Versi&oacute;n : </td>");
			cad.append("				<td class='titleLeft left'>").append(ver).append("</td>");
			cad.append("			</tr>");
			cad.append("			<tr>");
			cad.append("				<td class='titleLeft left'>Nombre del Servidor : </td>");
			cad.append("				<td class='titleLeft left'>").append(host).append("</td>");
			cad.append("			</tr>");
			cad.append("			<tr>");
			cad.append("				<td class='titleLeft left'>Nombre del Usuario : </td>");
			cad.append("				<td class='titleLeft left'>").append(user).append("</td>");
			cad.append("			</tr>");
			cad.append("			<tr>");
			cad.append("				<td class='titleLeft' colspan='2' style='padding-top:10;'>");
			cad.append("				");
			cad.append("				</td>");
			cad.append("			</tr>");
			cad.append("		</table>");
			cad.append("	</div>");
			cad.append("</div>");
			cad.append("</center>");
			cad.append("");
			cad.append("</body>");
			cad.append("</html>");
			
			
			MailForm formaMail = new MailForm();
			formaMail.setFrom("soporte@focus.com.ve");
			formaMail.setNameFrom("Qwebdocuments");
			formaMail.setTo("soporte@focus.com.ve;ggamez@focus.com.ve");
			//formaMail.setTo("soporte@focus.com.ve;ggamez@focus.com.ve");
			formaMail.setSubject("Solicitud de clave de conformación");
			formaMail.setMensaje(cad.toString());

			// envio de correo directo por gmail de la cuenta jrivero@focus.com.ve
			SendMail.sendMailToGMail(formaMail);
			
			out.print("true");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
