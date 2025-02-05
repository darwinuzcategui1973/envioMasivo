package com.focus.qweb.ctrl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

public class ManualAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(ManualAction.class.getName());

	private Users usuario = null;

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		super.init(mapping, form, request, response);

		try {
			usuario = getUserSession();

			System.out.println(request.getSession().getAttribute(Constants.MODULO_ACTIVO));
			int moduloActivo = 0;
			try {
				moduloActivo = ((Integer)request.getSession().getAttribute(Constants.MODULO_ACTIVO)).intValue();
			} catch(Exception e) {
				moduloActivo = -1;
			}

			String nameFileHelp = "";

			switch (moduloActivo) {
			case Constants.MODULO_PRINCIPAL:
				nameFileHelp = "helpPrincipal";
				break;
		//	case Constants.MODULO_ESTRUCTURA:
		//		nameFileHelp = "helpEOC";
		//		break;
		//	case Constants.MODULO_FLUJO:
			//	nameFileHelp = "helpFlujosDeTrabajo";
		//		break;
		//	case Constants.MODULO_EXPEDIENTE:
			//	nameFileHelp = "helpExpedientes";
		//		break;
		//	case Constants.MODULO_LISTA_MAESTRA:
			//	nameFileHelp = "helpListaMaestra";
			//	break;
		//	case Constants.MODULO_SACOP:
			//	nameFileHelp = "helpSACOPs";
		//		break;
		//	case Constants.MODULO_BUSCAR:
			//	nameFileHelp = "helpBuscar";
			//	break;
			case Constants.MODULO_MENSAJES:
				nameFileHelp = "helpMensajes";
				break;
	//		case Constants.MODULO_ESTADISTICAS:
	//			nameFileHelp = "helpEstadisticas";
	//			break;
	//		case Constants.MODULO_DIGITALIZAR:
	//			nameFileHelp = "helpDigitalizar";
	//			break;
			case Constants.MODULO_ADMINISTRACION:
				nameFileHelp = "helpConfiguracionGeneral";
				break;
			case Constants.MODULO_PERFIL:
				nameFileHelp = "helpPerfil";
				break;
			case Constants.MODULO_ADMINISTRACION_TECNICA:
				nameFileHelp = "helpConfiguracionTecnica";
				break;
			case Constants.MODULO_INTRODUCCION:
				nameFileHelp = "helpIntroduccion";
				break;
	//		case Constants.MODULO_FICHA_MAESTRA:
	//			nameFileHelp = "helpFichaMaestra";
	//			break;
			default:
				nameFileHelp = "helpIntroduccion";
			}
			request.getSession().setAttribute("MANUAL_AYUDA", DesigeConf.getProperty(nameFileHelp));

		} catch (ApplicationExceptionChecked e) {
			e.printStackTrace();
			return goError(e.getKeyError());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return goSucces();
	}

}
