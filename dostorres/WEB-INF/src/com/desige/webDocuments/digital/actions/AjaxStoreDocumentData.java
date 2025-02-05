package com.desige.webDocuments.digital.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.dao.DigitalDAO;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.to.DigitalTO;
import com.desige.webDocuments.utils.beans.Users;

public class AjaxStoreDocumentData extends Action{
	private static final Logger log = LoggerFactory.getLogger(AjaxStoreDocumentData.class);
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		//almacenamos los valores asociados al documento del modulo de digitalizados.
		//el documento no cambia de estatus
		try {
			DigitalDAO digitalDAO = new DigitalDAO();
			
			//obtenemos los valores completos
			DigitalTO digitalTO = new DigitalTO();
			digitalTO.setIdDigital(request.getParameter("idDigital"));
			digitalTO = digitalDAO.findById(digitalTO);
			log.info("Se obtuvo la informacion del registro digitalizado de id: " 
					+ digitalTO.getIdDigital());
			
			Users user = HandlerDBUser.getUser(request.getParameter("owner"));
			log.info("Obtenida la informacion del usuario (propietario) " + request.getParameter("owner"));
			
			//colocamos los nuevos sin alterar los que no estan involucrados
			digitalTO.setType(request.getParameter("typeDocument"));
			digitalTO.setIdNode(request.getParameter("idRout"));
			digitalTO.setNameDocument(request.getParameter("nameDocument"));
			digitalTO.setOwnerTypeDoc(Long.toString(user.getIdPerson()));
			digitalTO.setVersionMayor(request.getParameter("mayorVer"));
			digitalTO.setVersionMenor(request.getParameter("minorVer"));
			digitalTO.setCodigo(request.getParameter("number"));
			digitalTO.setPublicDoc(request.getParameter("docPublic"));
			digitalTO.setLote(request.getParameter("loteDoc"));
			digitalTO.setComentarios(request.getParameter("comentarios"));
			digitalTO.setUrl(request.getParameter("url"));
			digitalTO.setPalabrasClaves(request.getParameter("palabrasClaves"));
			digitalTO.setDescripcion(request.getParameter("descripcion"));
			
			log.info("Se coloco la nueva informacion del registro digitalizado de id: " 
					+ digitalTO.getIdDigital() + " en el DTO correspondiente");
			
			//guardamos el DTO
			digitalDAO.save(digitalTO);
			log.info("Almacenada en la base de datos la nueva informacion "
					+ " del registro digitalizado de id: " + digitalTO.getIdDigital());
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Error: " + e.getLocalizedMessage(), e);
		}
		
		return null;
	}
}
