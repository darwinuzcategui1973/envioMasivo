package com.desige.webDocuments.sacop.actions;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.mail.actions.SendMailTread;
import com.desige.webDocuments.mail.forms.MailForm;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qweb.dao.PlanillaSacop1DAO;
import com.focus.qweb.to.PlanillaSacop1TO;

/**
 * Created by IntelliJ IDEA. User: Administrador Date: 05/04/2006 Time: 03:53:21 PM To change this template use File | Settings | File Templates.
 */

public class SaveSolicitudSacop extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(SaveSolicitudSacop.class.getName());

	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
		ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);
		super.init(mapping, form, request, response);

		Users user = (Users) request.getSession().getAttribute("user");

		try {
			log.info("Iniciando LoadSolicitudSacop");

			// cargamos el documento
			BaseDocumentForm forma = new BaseDocumentForm();
			forma.setIdDocument(getParameter("idDocument"));
			forma.setNumberGen(getParameter("idDocument"));
			HandlerStruct.loadDocument(forma, true, false, null, request);

			// recolectamos los parametros de la ventana
			String origen = request.getParameter("origen");
			String fecha = request.getParameter("fecha");
			String descripcion = request.getParameter("descripcion");
			String responsable = request.getParameter("responsable");

			PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
			PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO();

			oPlanillaSacop1TO.setIdplanillasacop1("0");
			oPlanillaSacop1TO.setSacopnum("SA-0000");  //NUMERO CORRELATIVO SACOP POR DEFAULT
			oPlanillaSacop1TO.setEmisor(responsable);
			oPlanillaSacop1TO.setUsernotificado(responsable);
			oPlanillaSacop1TO.setRespblearea("0");
			oPlanillaSacop1TO.setEstado("0");
			oPlanillaSacop1TO.setOrigensacop(origen);
			oPlanillaSacop1TO.setFechaemision(null);
			oPlanillaSacop1TO.setFechaSacops1(ToolsHTML.getDateLargeSql());
			oPlanillaSacop1TO.setRequisitosaplicable("");
			oPlanillaSacop1TO.setProcesosafectados("");
			oPlanillaSacop1TO.setSolicitudinforma("");
			oPlanillaSacop1TO.setDescripcion(descripcion);
			oPlanillaSacop1TO.setCausasnoconformidad("");
			oPlanillaSacop1TO.setAccionesrecomendadas("");
			oPlanillaSacop1TO.setCorrecpreven("");  //("0");
			oPlanillaSacop1TO.setRechazoapruebo(null);
			oPlanillaSacop1TO.setNoaceptada(null);
			oPlanillaSacop1TO.setAccionobservacion(null);
			oPlanillaSacop1TO.setFechaestimada(null);
			oPlanillaSacop1TO.setFechareal(null);
			oPlanillaSacop1TO.setAccionesEstablecidas(null);
			oPlanillaSacop1TO.setAccionesestablecidastxt(null);
			oPlanillaSacop1TO.setEliminarcausaraiz(null);
			oPlanillaSacop1TO.setEliminarcausaraiztxt(null);
			oPlanillaSacop1TO.setNameFile(null);
			oPlanillaSacop1TO.setContentType(null);
			oPlanillaSacop1TO.setData("");
			oPlanillaSacop1TO.setActivecomntresponsablecerrar("1");
			oPlanillaSacop1TO.setActive("1");
			oPlanillaSacop1TO.setFechaculminar(ToolsHTML.getDateLargeSql());
			oPlanillaSacop1TO.setComntresponsablecerrar(null);
			oPlanillaSacop1TO.setSacopRelacionadas("");
			oPlanillaSacop1TO.setNoconformidadesref(forma.getIdDocument());
			oPlanillaSacop1TO.setNoconformidades("");
			oPlanillaSacop1TO.setIdplanillasacop1esqueleto("0");
			oPlanillaSacop1TO.setClasificacion("0");
			oPlanillaSacop1TO.setFechaWhenDiscovered(fecha);
			oPlanillaSacop1TO.setArchivoTecnica(null);
			oPlanillaSacop1TO.setFechaVerificacion(null);
			oPlanillaSacop1TO.setFechaCierre(null);
			oPlanillaSacop1TO.setUsuarioSacops1(String.valueOf(user.getIdPerson()));
			oPlanillaSacop1TO.setFechaSacops1(ToolsHTML.getDateShortSql());
			oPlanillaSacop1TO.setFechaestimada(ToolsHTML.getDateShortSql().toString());
			oPlanillaSacop1TO.setIdDocumentRelated(forma.getIdDocument());;

			oPlanillaSacop1DAO.insertar(oPlanillaSacop1TO);

			//Enviamos un correo al 
        	MailForm formaMail = new MailForm();
			formaMail.setFrom(HandlerParameters.PARAMETROS.getMailAccount());
			formaMail.setNameFrom(rb.getString("mail.system"));
			formaMail.setTo(user.getEmail());
			formaMail.setSubject(rb.getString("mail.nameUser")+ " - " + rb.getString("requestSacop.title"));
			formaMail.setMensaje(rb.getString("requestSacop.mailMessage"));
			try {
    			//SendMailAction.send(formaMail);
    			if(formaMail!=null){
    				SendMailTread mail = new SendMailTread(formaMail);
    				mail.start();
    			}
    		} catch (Exception ex) {
    			log.debug("Exception");
    			log.error(ex.getMessage());
    			ex.printStackTrace();
    		}

			log.info("Return to success");
			return goSucces();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}

		log.info("Return to error");
		return goError();
	}
}
