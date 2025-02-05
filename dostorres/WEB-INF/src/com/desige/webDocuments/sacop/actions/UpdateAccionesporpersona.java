package com.desige.webDocuments.sacop.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.sacop.forms.PlantillaAccion;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 14/03/2006
 * Time: 09:06:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateAccionesporpersona extends SuperAction {
	private static final Logger log = LoggerFactory.getLogger(UpdateAccionesporpersona.class);
	
	/**
	 * 
	 */
	public synchronized ActionForward execute(ActionMapping mapping,
			ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping,form,request,response);

        try {
            String cmd = request.getParameter("cmd");
            PlantillaAccion formaAccion = (PlantillaAccion)form;
            request.getSession().setAttribute("otroProcesoSacop",form);
        	Users user = getUserSession();
        	formaAccion.setIdplanillasacop1(Long.parseLong(request.getParameter("idplanillasacop")));
        	//System.out.println("formaAccion.getComentario()="+formaAccion.getComentario());
        	//System.out.println("formaAccion.getIdplanillasacopaccion()="+formaAccion.getIdplanillasacopaccion());
        	//System.out.println("user.getIdPerson()="+user.getIdPerson());
        	
        	formaAccion.setCmd(cmd);
        	String firma=request.getParameter("firma")!=null?request.getParameter("firma"):"0";
        	
            if (ToolsHTML.checkValue(cmd)){
            	if(SuperActionForm.cmdEdit.equals(cmd)){
            		log.info("Se esta editando una accion de la sacop " + request.getParameter("idplanillasacop"));
            		checkPreviousEvidences(user, formaAccion);
            	}
            	
            	processCmd(formaAccion,request,user,firma);
            	cmd = SuperActionForm.cmdLoad;
            	((SuperActionForm) form).setCmd(cmd);
            	request.setAttribute("cmd", cmd);
            }
            
            putObjectSession("idplanillasacop",request.getParameter("idplanillasacop")!=null?request.getParameter("idplanillasacop"):"");
            putAtributte("idaccion",new Long(formaAccion.getIdplanillasacopaccion()));

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
	 * @param user
	 * @param firma
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	private static boolean processCmd (PlantillaAccion forma,HttpServletRequest request,Users user,String firma) 
			throws ApplicationExceptionChecked{
		//System.out.println("forma = " + forma.getCmd());
        boolean resp = false;
        StringBuffer mensaje = null;
        ResourceBundle rb = ToolsHTML.getBundle(request);
        if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdEdit)){
        	//System.out.println("Editando Registro...");
        	//si en vez de guardar=0, firman electronicamente con 1, se le manda correo al responsable de que la
        	//acción ya se ha firmado en updateAccionPorPersona.
        	resp = HandlerProcesosSacop.updateAccionPorPersona(forma, String.valueOf(user.getIdPerson()), firma);
        	request.setAttribute("info",rb.getString("app.editOk"));
        	resp = true;
        }
        
        /*
        if (mensaje != null){
            //System.out.println("mensaje = " + mensaje);
            request.setAttribute("info",mensaje.toString());
        }
        */
        return resp;
    }
	
	/**
	 * 
	 * @param baseEvidencesPath
	 * @param user
	 * @param formaAccion
	 */
	private void checkPreviousEvidences(Users user, PlantillaAccion formaAccion){
		log.info("Verificando evidencias de la accion '" + formaAccion.getIdplanillasacopaccion() + "' para la sacop '" 
				+ formaAccion.getIdplanillasacop1() + "'");
		
		final String evidencePath = HandlerProcesosSacop.getSacopPlanillaBagFolder(String.valueOf(formaAccion.getIdplanillasacop1()))
				+ File.separator + formaAccion.getIdplanillasacopaccion()
				+ File.separator + user.getNameUser();
		
//		final String evidencePath = ToolsHTML.getPath().concat( + HandlerProcesosSacop.EVIDENCIAS_NAME_DIR 
//				+ File.separator + formaAccion.getIdplanillasacop1()
//				+ File.separator + formaAccion.getIdplanillasacopaccion()
//				+ File.separator + user.getNameUser();
		File evidenceDir  = new File(evidencePath);
		if (!evidenceDir.exists()) {
			evidenceDir.mkdirs();
		}
		
		//recorremos los archivos del directorio de evidencias de la accion para saber si el usuario
		//ya tenia una evidencia previa y proceder a eliminarla si es el caso
		if(formaAccion.isDelPreviousEvidence() || formaAccion.getEvidencia().getFileSize() > 0){
			formaAccion.setUpdateEvidenceField(true);
			
			//borramos la evidencia previa
			File[] evidencesFiles = evidenceDir.listFiles();
			for (File file : evidencesFiles) {
				file.delete();
				log.info("Borramos archivo de evidencia " + file.getAbsolutePath());
			}
			
			if(formaAccion.getEvidencia().getFileSize() > 0){
				try {
					String fixedFileName = formaAccion.getEvidencia().getFileName();
					fixedFileName.replaceAll("\\s", "_");
					fixedFileName.replaceAll("ñ", "n");
					fixedFileName.replaceAll("Ñ", "N");
					
					log.info("modificado nombre de '" + formaAccion.getEvidencia().getFileName()
							+ "' a '" + fixedFileName + "'");
					
					File result = new File(evidenceDir.getAbsolutePath() + File.separator 
							+ fixedFileName);
					FileOutputStream fos = new FileOutputStream(result);
					fos.write(formaAccion.getEvidencia().getFileData());
					fos.flush();
					fos.close();
					log.info("Escrito archivo de evidencia " + result.getAbsolutePath());
				} catch (Exception e) {
					// TODO: handle exception
					log.error("Error creando evidencia en su ruta final. Error: " + e.getMessage(), e);
				}
			}
		} else {
			formaAccion.setUpdateEvidenceField(false);
			log.info("El archivo de evidencia de la accion se deja tal cual estaba");
		}
	}
}
