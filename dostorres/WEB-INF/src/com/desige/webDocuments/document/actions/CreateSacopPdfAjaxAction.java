package com.desige.webDocuments.document.actions;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.DbUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.bean.PdfSacopRequest;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.managers.HandlerSacop;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.PdfSacop;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qweb.dao.PlanillaSacop1DAO;
import com.focus.qweb.to.PlanillaSacop1TO;
import com.focus.util.Archivo;
import com.google.gson.Gson;

public class CreateSacopPdfAjaxAction extends Action {

	static Logger log = LoggerFactory.getLogger(CreateSacopPdfAjaxAction.class.getName());

	
	public ActionForward execute(ActionMapping actionMapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		String message = "";
		try {
			log.info("Begin CreateSacopPdfAjaxAction...");

			Users usuario = (Users) request.getSession().getAttribute("user");

			if (usuario == null || !HandlerDBUser.isValidSessionUser(usuario.getUser(), request.getSession())) {
				throw new ApplicationExceptionChecked("E0035");
			}
			Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
			ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",defaultLocale);

			String data = ToolsHTML.getBody(request);

			Gson gson = new Gson();
			PdfSacopRequest pdfData = gson.fromJson(data, PdfSacopRequest.class);

			// find document related
			pdfData.setListRelationDocs(HandlerSacop.findDocRelatedFromSacop(pdfData.getIdSacop()));
			
			// busca sacop relacionas
			pdfData.setListSacops(HandlerSacop.findSacopRelated(pdfData.getIdSacop()));
								
			String Idplanillasacop1 = pdfData.getIdSacop();
			PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
			PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO();
			oPlanillaSacop1TO.setIdplanillasacop1(Idplanillasacop1);

			if(!oPlanillaSacop1DAO.cargar(oPlanillaSacop1TO)) {
				throw new Exception(rb.getString("sacop.register.sacopNotFound"));
			}
			
			// completamos datos faltantes
			pdfData.setCausaRaiz(oPlanillaSacop1TO.getAccionobservacion());
			pdfData.setDecripcionAccionPrincipal(oPlanillaSacop1TO.getDescripcionAccionPrincipal());
			pdfData.setNameRelatedDocument(oPlanillaSacop1TO.getNameDocumentAssociate());
			pdfData.setNameFile(oPlanillaSacop1TO.getNameFile());

			pdfData.setNormIso(oPlanillaSacop1TO.getRequisitosaplicable());
			
			// construimos el pdf
			PdfSacop pdfSacop = new PdfSacop();
			InputStream inputStreamOfPdfFile = pdfSacop.generatePdf(pdfData);
				
			// escribimos el archivo al disco para verificar que fue generado correctamente
			/*
			if(false) {
				Archivo.escribirEnDisco("C:\\warfile\\sacopregister.pdf", inputStreamOfPdfFile); //solo para probar la correcta escritura en disco
				throw new Exception(rb.getString("sacop.register.registerExists"));
			}
			*/

			if( ToolsHTML.parseInt(oPlanillaSacop1TO.getIdRegisterGenerated())>0 ) {
				throw new Exception(rb.getString("sacop.register.registerExists"));
			}

			if(oPlanillaSacop1TO.getIdDocumentRelatedInt()==0) {
				throw new Exception(rb.getString("sacop.register.notRelated"));
			}
			
			BaseDocumentForm forma = new BaseDocumentForm();
			BaseDocumentForm formulario = new BaseDocumentForm();
			
			formulario.setIdDocument(oPlanillaSacop1TO.getIdDocumentRelated());
			formulario.setNumberGen(oPlanillaSacop1TO.getIdDocumentRelated());
			
			Hashtable subNodos = new Hashtable();
	        Hashtable tree = HandlerStruct.loadAllNodes(null,DesigeConf.getProperty("application.admon"),
	                                          DesigeConf.getProperty("application.admon"),subNodos,null);

			HandlerStruct.loadDocument(formulario,true,true,tree, request);
			
			if(formulario.getNameDocument()==null && formulario.getNameDocument().isEmpty()) {
				throw new Exception(rb.getString("sacop.register.notFound"));
			}
			
			String numeroSacop = pdfData.getTitleSacop().trim().split(":")[1];

			Users emisorSacop = HandlerDBUser.load(Long.parseLong(oPlanillaSacop1TO.getEmisor()), true);

            // iniciamos con los datos del registro con los del formulario
            forma.setIdDocument(formulario.getIdDocument());
            forma.setNumberGen(formulario.getNumberGen());
            forma.setApproved(formulario.getApproved());
            forma.setCharge(formulario.getCharge());
            forma.setCheck(formulario.getCheck());
            forma.setContextType(formulario.getContextType());
            forma.setDateApproved(oPlanillaSacop1TO.getFechaCierre());
            forma.setDateCreation(oPlanillaSacop1TO.getFechaemision());
            //forma.setDateDead(formulario.getDateDead()); // fecha de retencion
            forma.setDateExpires(formulario.getDateExpires());
            forma.setDatePublic(formulario.getDatePublic());
            forma.setDocOnline(formulario.getDocOnline());
            forma.setDocPublic(formulario.getDocPublic());
            forma.setExpires(formulario.getExpires());
            forma.setIdNode(formulario.getIdNode());
            forma.setIdUser((int)emisorSacop.getIdPerson());
            forma.setLastOperation(formulario.getLastOperation());
            forma.setLastVersionApproved(formulario.getLastVersionApproved());
			forma.setNameDocument("Solicitud de Acción Operativa Nro: ".concat(numeroSacop));
			forma.setDescript("Planilla SACOP  Nro: ".concat(numeroSacop));
			forma.setKeys("Planilla SACOP ".concat(numeroSacop));
            
            forma.setNameFile(formulario.getNameDocument().replaceAll("[^0-9A-Za-z]","").concat(".pdf"));
            forma.setNameOwner(emisorSacop.getNamePerson());  // nombre del usuario
            forma.setNumber(formulario.getNumber());
            forma.setOwner(emisorSacop.getUser());
            forma.setStatu(formulario.getStatu());
            forma.setStatuDoc(formulario.getStatuDoc());
            forma.setToForFiles(formulario.getToForFiles());
            forma.setTypeFormat(formulario.getTypeFormat());
            forma.setIdDocumentOrigen(formulario.getIdDocument());
            forma.setIdVersionOrigen(String.valueOf(formulario.getNumVer()));
            forma.setIdProgramAudit(formulario.getIdProgramAudit());
            forma.setIdPlanAudit(formulario.getIdPlanAudit());
            
        
            forma.setTypeDocument(DesigeConf.getProperty("typeDocs.docRegister"));

            forma.setMayorVer("0");
            forma.setMinorVer("0");
            
            forma.setNormISO(pdfData.getNormIso());
            
            BaseStructForm carpeta = (BaseStructForm)tree.get(formulario.getIdNode());

            BaseStructForm localidad = (BaseStructForm)HandlerStruct.getLocationFromNode(tree, formulario.getIdNode());
        	
    		byte typePrefix = 0;
			if (localidad != null) {
				typePrefix = localidad.getTypePrefix();
			}
            if (carpeta!=null) {
                String nodeActive = carpeta.getIdNode();
                HandlerStruct handlerStruct = new HandlerStruct();
                if (carpeta.getHeredarPrefijo() == Constants.permission) {
                    String prefix = handlerStruct.getParentPrefixinst(tree,nodeActive,0==typePrefix);
                    if (0==typePrefix) {
                        forma.setPrefix(carpeta.getPrefix()+prefix);
                    } else {
                        forma.setPrefix(prefix+carpeta.getPrefix());
                    }
                } else {
                    forma.setPrefix(carpeta.getPrefix());
                }
            }

            String number = HandlerParameters.getLengthNumberDocuments();
            StringBuffer numero = new StringBuffer("");
            //se genera el número del documento
            if (number!=null) {
                String numByLocation = String.valueOf(HandlerParameters.PARAMETROS.getNumDocLoc());
                String numberDoc = EditDocumentAction.numCorrelativo(formulario.getIdNode(),formulario.getIdLocation(),numByLocation,null,tree);
//                        String numberDoc = EditDocumentAction.numCorrelativo(register.getIdRout(),idLocation);
//                        String numberDoc = String.valueOf(IDDBFactorySql.getNextID("numDocs"));
                for (int cont = numberDoc.length(); cont < Integer.parseInt(number); cont++){
                    numero.append("0");
                }
                numero.append(numberDoc);
            }
            forma.setNumber(numero.toString());
            forma.setIdNode(formulario.getIdNode());
            forma.setDateApproved(null);//El registro obviamente no puede estar aprobado ni publicado
            forma.setDocPublic(null);
            forma.setDatePublic(null);
            forma.setDateExpires("9999-12-31 12:59:59 pm");
            forma.setApproved(null);
            forma.setOwner(emisorSacop.getUser());
            forma.setNameOwner(emisorSacop.getNamePerson());
            forma.setIdRegisterClass(1);  // registro de evidencia
    

            HandlerStruct.createRegisterSacop(forma,usuario.getUser(),inputStreamOfPdfFile);

            java.util.Date dateParameter = new java.util.Date();
            dateParameter = ToolsHTML.getDateFromString(oPlanillaSacop1TO.getFechaCierre());
            
            // publicamos el documento
            boolean result = HandlerDocuments.publicDocumentEraser(forma.getNumberGen(),"Registro proveniente de Sacop", dateParameter, HandlerProcesosSacop.getDateFirstAccion(oPlanillaSacop1TO.getIdplanillasacop1()) );
            
            // colocamos los documentos relacionados
            Connection con = null;
            try {
            	// numGen, numGenLink, numVer, numVerLink
            	StringBuilder sb = new StringBuilder();
            	sb.append(forma.getNumberGen()).append("_");  // doc origen
            	sb.append(formulario.getIdDocument()); //.append(","); // doc relacionado
            	//sb.append(formulario.getIdDocument()).append("_"); // doc relacionado
            	//sb.append(forma.getNumberGen());  // doc origen
            	//sb.append(forma.getNumVer()).append("_");  // version origen
            	//sb.append(formulario.getNumVer()).append("");  // version relacionado
            	//"2_2,2_2,5_5,2_2,5_5"; // formato de ids
            	forma.setDocRelations(sb.toString());
            	
	            int numGen = Integer.parseInt(forma.getNumberGen());
	            int numGenLink = Integer.parseInt(formulario.getIdDocument());
	            int numVer = forma.getNumVer();
	            int numVerLink = formulario.getNumVer();

	            con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				
	            HandlerStruct.deleteDocumentsLinks(numGen,numVer, con);
				HandlerStruct.createDocumentsLinks(numGen,numGenLink,numVer,numVerLink, con);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				DbUtils.closeQuietly(con);
			}
            
            
            if(result) {
            	// almacenamos el id del documento para no generar uno nuevamente
            	oPlanillaSacop1TO.setIdRegisterGenerated(forma.getNumberGen());
            	oPlanillaSacop1DAO.actualizar(oPlanillaSacop1TO);
            }

			message = "{ 'success':true, 'message':'".concat(rb.getString("sacop.register.success")).concat("'}");


		} catch (Exception e) {
			message = "{ 'success':false, 'message':'"+e.getMessage()+"'}";
			e.printStackTrace();
		} finally {
			try {
				response.getOutputStream().print(message.replaceAll("'", "\""));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

}
