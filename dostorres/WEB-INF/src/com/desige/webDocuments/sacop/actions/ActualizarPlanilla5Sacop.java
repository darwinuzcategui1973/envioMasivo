package com.desige.webDocuments.sacop.actions;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.sacop.forms.plantilla1;
import com.desige.webDocuments.utils.DateException;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.TrackingException;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.focus.qweb.dao.PlanillaSacop1DAO;
import com.focus.qweb.to.PlanillaSacop1TO;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 23/12/2005
 * Time: 11:42:15 AM
 * To change this template use File | Settings | File Templates.
 */

public class ActualizarPlanilla5Sacop extends SuperAction {
	
    //static Logger log = LoggerFactory.getLogger(ActualizarPlanillaSacop.class.getName()); 
    public synchronized ActionForward execute(ActionMapping mapping,
    		ActionForm form, HttpServletRequest request, HttpServletResponse response) {
    	
    	plantilla1 forma = null;
    	
        try {
            super.init(mapping,form,request,response);
            
			Locale defaultLocale = new Locale(
					DesigeConf.getProperty("language.Default"),
					DesigeConf.getProperty("country.Default"));
			ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",defaultLocale);

            
            forma = (plantilla1) form;
            boolean swModificarBD=false;
            String estadoAnterior = null;

            //solo modifica el usuario emisor o responsable y uno de ellos le toca firmar en x momento en
            //del satus
            if(!ToolsHTML.isEmptyOrNull((String)getSessionObject("modificando"))){
            	if (getSessionObject("modificando").equals("1")){
                    swModificarBD=true;
                }
            }
            
            //buscamos el numero de planilla del sacop
            //Collection sacop= HandlerProcesosSacop.getInfResponsable("idplanillasacop1",forma.getIdplanillasacop1(),false);
            //Iterator it = sacop.iterator();
            
            if(swModificarBD){
            	
            	//Plantilla1BD formabd = (Plantilla1BD) it.next();
            	PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
            	PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO();
            	
            	oPlanillaSacop1TO.setIdplanillasacop1(forma.getIdplanillasacop1());
            	oPlanillaSacop1DAO.cargar(oPlanillaSacop1TO);
            	
				estadoAnterior = oPlanillaSacop1TO.getEstado();
				
				
            	
            	oPlanillaSacop1TO.setEstado(LoadSacop.edoCerrado);
            	oPlanillaSacop1TO.setFechaCierre(ToolsHTML.calendarToTimeStampString(Calendar.getInstance()));
            	oPlanillaSacop1TO.setAccionesEstablecidas(request.getParameter("accionesEstablecidas")!=null?request.getParameter("accionesEstablecidas"):"");
            	oPlanillaSacop1TO.setEliminarcausaraiz(request.getParameter("eliminarcausaraiz")!=null?request.getParameter("eliminarcausaraiz"):"");
            	//DARWIN UZCATEGUI1
            	oPlanillaSacop1TO.setEliminarcausaraiztxt(forma.getEliminarcausaraiztxt());
            	oPlanillaSacop1TO.setAccionesestablecidastxt(forma.getAccionesEstablecidastxt());

            	oPlanillaSacop1TO.setRequireTracking(request.getParameter("requireTracking")!=null?request.getParameter("requireTracking"):"");
            	oPlanillaSacop1TO.setRequireTrackingDate(request.getParameter("requireTrackingDate")!=null?request.getParameter("requireTrackingDate"):"");
            	
            	// Creacion de sacop de seguimiento
                boolean tieneSeguimientoPendiente_ = oPlanillaSacop1DAO.isSacopDeSeguimientoPendiente(oPlanillaSacop1TO.getSacopnum());
            	if(!ToolsHTML.isEmptyOrNull(oPlanillaSacop1TO.getRequireTrackingDate()) && !tieneSeguimientoPendiente_) {
    				try {
    					 
    					Date dateLast =  oPlanillaSacop1DAO.fechaSacopDeSeguimientoMayor(oPlanillaSacop1TO.getSacopnum());
    					
    					Date dateTracking = ToolsHTML.sdfShowWithoutHour.parse(oPlanillaSacop1TO.getRequireTrackingDate());
    					Date dateToday = ToolsHTML.removeTime(new Date());
    					
    					if (dateLast == null ) {
    						dateLast =  dateToday;
    								}
    					
    					Date dateLastClosetracking  = ToolsHTML.removeTime(dateLast);
    					    					
    					String dateCreation = ToolsHTML.sdfShowConvert1.format(dateTracking);
    					oPlanillaSacop1TO.setRequireTrackingDate(dateCreation);
    					
    					if(oPlanillaSacop1TO.getRequireTracking().equals("0")) {
    						
    						// validamos que la fecha de seguimiento sea mayor a la fecha actual
    						if( dateTracking.getTime() < dateToday.getTime() ) {
    							throw new DateException(rb.getString("scp.dateTrackingIncorrect"));
    						}
    						
    						//validamos que la fecha de seguimiento sea mayor a la  fecha de la ultima sacop de seguimiento cerrada
    						if( dateTracking.getTime() < dateLastClosetracking.getTime()  ) {
    							throw new DateException(rb.getString("scp.dateTrackingIncorrectWithLast"));
    						}
    						
    						
    						//*** INI codigo anterior de seguimiento

    						// vamos actualizar el registro y el historico
    						//oPlanillaSacop1TO.getIdDocumentAssociate()
    						
    						// dejamos la traza en el historico
    						BaseDocumentForm formaDoc = new BaseDocumentForm();
    						formaDoc.setIdDocument(oPlanillaSacop1TO.getIdDocumentRelated());
    						formaDoc.setNumberGen(oPlanillaSacop1TO.getIdDocumentRelated());
    						
    						HandlerStruct.loadDocument(formaDoc,true,false,null, request);
    						
    						/*
    						// actualizamos el vencimiento
    						HandlerStruct.updateDocumentDateExpire(ToolsHTML.parseInt(formaDoc.getLastVersionApproved()), oPlanillaSacop1TO.getRequireTrackingDate());
    						
    						Users usuario = getUserSession();
    						int numdoc = Integer.parseInt(oPlanillaSacop1TO.getIdDocumentRelated());
    						int node = ToolsHTML.parseInt(formaDoc.getIdNode());
    						String razonHistory = "Actualizacion de vencimiento desde sacop ".concat(oPlanillaSacop1TO.getSacopnum());
    						
    						HandlerDocuments.updateHistoryDocs(JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName()), numdoc, node, 0,
    								usuario.getUser(),
    								new Timestamp(new Date().getTime()), "24",
    								razonHistory, new String[] { formaDoc.getMayorVer(),
    										formaDoc.getMinorVer() });*/

    						//*** FIN codigo anterior de seguimiento    						
    						
    						// creamos la sacop ticket:SACOPS_seguimiento_NC
    						
    						StringBuilder descripcion = new StringBuilder("Esta SACOP ha sido creada para realizar el seguimiento de la ");
    						descripcion.append(oPlanillaSacop1TO.getSacopnum()).append("-").append("");
    						descripcion.append(" cuya fecha de planificación de seguimiento es: ");
    						descripcion.append(oPlanillaSacop1TO.getRequireTrackingDate());

    						PlanillaSacop1TO planSacopTO = new PlanillaSacop1TO();
    						planSacopTO.setIdplanillasacop1("0");
    						planSacopTO.setSacopnum("SA-0000");  //NUMERO CORRELATIVO SACOP POR DEFAULT
    						planSacopTO.setEmisor(oPlanillaSacop1TO.getEmisor());
    						planSacopTO.setUsernotificado(oPlanillaSacop1TO.getUsernotificado());
    						planSacopTO.setRespblearea(oPlanillaSacop1TO.getRespblearea());
    						planSacopTO.setEstado(LoadSacop.edoBorrador);
    						planSacopTO.setOrigensacop(oPlanillaSacop1TO.getOrigensacop());
    						planSacopTO.setFechaemision(oPlanillaSacop1TO.getRequireTrackingDate());
    						planSacopTO.setFechaSacops1(oPlanillaSacop1TO.getRequireTrackingDate());
    						planSacopTO.setRequisitosaplicable(oPlanillaSacop1TO.getRequisitosaplicable());
    						planSacopTO.setProcesosafectados(oPlanillaSacop1TO.getProcesosafectados());
    						planSacopTO.setSolicitudinforma(oPlanillaSacop1TO.getSolicitudinforma());
    						planSacopTO.setDescripcion(descripcion.toString());
    						planSacopTO.setCausasnoconformidad(oPlanillaSacop1TO.getCausasnoconformidad());
    						planSacopTO.setAccionesrecomendadas(oPlanillaSacop1TO.getAccionesrecomendadas());
    						planSacopTO.setCorrecpreven(oPlanillaSacop1TO.getCorrecpreven());
    						planSacopTO.setRechazoapruebo(oPlanillaSacop1TO.getRechazoapruebo());
    						planSacopTO.setNoaceptada(null);
    						planSacopTO.setAccionobservacion(oPlanillaSacop1TO.getAccionobservacion());
    						planSacopTO.setFechaestimada(oPlanillaSacop1TO.getRequireTrackingDate());
    						planSacopTO.setFechareal(null);
    						planSacopTO.setAccionesEstablecidas(null);
    						planSacopTO.setAccionesestablecidastxt(null);
    						planSacopTO.setEliminarcausaraiz(null);
    						planSacopTO.setEliminarcausaraiztxt(null);
    						planSacopTO.setNameFile(null);
    						planSacopTO.setContentType(null);
    						planSacopTO.setData("");
    						planSacopTO.setActivecomntresponsablecerrar("1");
    						planSacopTO.setActive("1");
    						planSacopTO.setFechaculminar(oPlanillaSacop1TO.getRequireTrackingDate());
    						planSacopTO.setComntresponsablecerrar(null);
    						planSacopTO.setSacopRelacionadas(oPlanillaSacop1TO.getIdplanillasacop1());  // sacop relacionada
    						planSacopTO.setNoconformidadesref(formaDoc.getIdDocument());
    						planSacopTO.setNoconformidades("");
    						planSacopTO.setIdplanillasacop1esqueleto("0");
    						planSacopTO.setClasificacion("0");
    						planSacopTO.setFechaWhenDiscovered(oPlanillaSacop1TO.getFechaWhenDiscovered());
    						planSacopTO.setArchivoTecnica(null);
    						planSacopTO.setFechaVerificacion(null);
    						planSacopTO.setFechaCierre(null);
    						planSacopTO.setUsuarioSacops1(oPlanillaSacop1TO.getUsuarioSacops1());
    						planSacopTO.setFechaSacops1(oPlanillaSacop1TO.getRequireTrackingDate());
    						planSacopTO.setIdDocumentRelated(formaDoc.getIdDocument());
    						planSacopTO.setTrackingSacop("1");
    						
    						if(oPlanillaSacop1TO.getTrackingSacop().equals("1")) {
    							planSacopTO.setNumberTrackingSacop(oPlanillaSacop1TO.getNumberTrackingSacop());
    						} else {
    							planSacopTO.setNumberTrackingSacop(oPlanillaSacop1TO.getSacopnum());
    						}
    						
    						planSacopTO.setIdRegisterGenerated("0");
    						
    						oPlanillaSacop1DAO.insertar(planSacopTO);
    						

    						if(oPlanillaSacop1TO.getSacopRelacionadas()!=null && !oPlanillaSacop1TO.getSacopRelacionadas().trim().equals("")) {
    							oPlanillaSacop1TO.setSacopRelacionadas(oPlanillaSacop1TO.getSacopRelacionadas().concat(",").concat(planSacopTO.getIdplanillasacop1()));
    						} else {
    							oPlanillaSacop1TO.setSacopRelacionadas(planSacopTO.getIdplanillasacop1());
    						}
    						
    		            	HandlerProcesosSacop.mandarMailsUsuariosdelSacop(planSacopTO.getIdplanillasacop1(),planSacopTO.getSacopnum(),"Seguimiento.");
    		            	
    		            	// si la planilla tiene seguimiento no se cerrara y quedara verificada
        					oPlanillaSacop1TO.setRequireTrackingDate(null);
    		            	oPlanillaSacop1TO.setEstado(estadoAnterior);
    		            	oPlanillaSacop1TO.setFechaCierre(null);
    		            	//oPlanillaSacop1TO.setAccionesEstablecidas(request.getParameter("accionesEstablecidas")!=null?request.getParameter("accionesEstablecidas"):"");
    		            	//oPlanillaSacop1TO.setEliminarcausaraiz(request.getParameter("eliminarcausaraiz")!=null?request.getParameter("eliminarcausaraiz"):"");
    		            	//DARWIN UZCATEGUI1
    		            	//oPlanillaSacop1TO.setEliminarcausaraiztxt(forma.getEliminarcausaraiztxt());
    		            	//oPlanillaSacop1TO.setAccionesestablecidastxt(forma.getAccionesEstablecidastxt());
    		            	oPlanillaSacop1TO.setRequireTracking("");
    		            	oPlanillaSacop1TO.setRequireTrackingDate("");
    		            
    		            	// pruebo aqui un momento despues lo quito
    		            	oPlanillaSacop1TO.setEliminarcausaraiztxt("");
    		            	oPlanillaSacop1TO.setAccionesestablecidastxt("");
    		            	
    		            	request.setAttribute("info", rb.getString("scp.sacopWithTracking"));
    						
    					}
    					
    				} catch (TrackingException ex) {
    					throw ex;
    				} catch (DateException ex) {
    					// no se pudo formatear
    					oPlanillaSacop1TO.setRequireTrackingDate(null);
    					// relanzamos la excepcion por la fecha erronea
    					throw ex;
    				} catch (Exception ex) {
    					ex.printStackTrace();
    					// no se pudo formatear
    					oPlanillaSacop1TO.setRequireTrackingDate(null);
    					
    					throw ex;
    				}
            	} else {
                	// si vamos a cerrar la planilla buscamos si tiene sacops de seguimiento que no esten cerradas o rechazadas.
                	boolean tieneSeguimientoPendiente = oPlanillaSacop1DAO.isSacopDeSeguimientoPendiente(oPlanillaSacop1TO.getSacopnum());
                	if(tieneSeguimientoPendiente) {
                		throw new TrackingException(rb.getString("scp.sacopWithTracking"));
                	}
            	}
            	
            	// prueba blanquear 
            	
            	
            	oPlanillaSacop1DAO.actualizar(oPlanillaSacop1TO);
            	
            	// despues de actulizar seteo Eliminarcausaraiztxt y AccionesEstablecidastxt a blanco  
            	forma.setEliminarcausaraiztxt("");
            	forma.setAccionesEstablecidastxt("");
            	
            	// Aqui eliminamos La Sacop_Intouch preconfigurada de manera esqueleto,si se cumple la condicion en
            	// que en realidad solicito la eliminacion logica de la sacop preconfigurada.
            	// String continuar_Sacop_Intouch = request.getParameter("continuar_Sacop_Intouch");
            	// HandlerProcesosWonderWare handlerProcesosWonderWare = new HandlerProcesosWonderWare();
            	// handlerProcesosWonderWare.eliminamosSaco_Intouch(forma.getIdplanillasacop1esqueleto(),continuar_Sacop_Intouch,  tx);
            	//__________________________________________________________________________________________________________

            	//mandar mails
            	if(oPlanillaSacop1TO.getEstado().equals(LoadSacop.edoCerrado)) {
	            	HandlerProcesosSacop.mandarMailsUsuariosdelSacop(forma.getIdplanillasacop1(),
	            			forma.getSacopnum(),
	            			"Cerrado.");
	            	
	            	// prueba blanquear 
	            	
	            	forma.setEliminarcausaraiztxt("");
	            	forma.setAccionesEstablecidastxt("");
	            	
            	}
            	crearSacopBorrador(oPlanillaSacop1TO,Boolean.parseBoolean(request.getParameter("createRelatedSACOP")));
            }
            
        } catch (TrackingException ex){
        	request.setAttribute("info", ex.getMessage());
        	return goError();
        } catch (DateException ex){
        	request.setAttribute("info", ex.getMessage());
        	return goError();
        } catch (Exception ex){
        	request.setAttribute("info", ex.getMessage());
        	ex.printStackTrace();
        	return goError();
        }finally{
        	/**/
        }
        
         	
    	        
        return goSucces();
    }
    
    /**
     * 
     * @param formaBorrador
     * @param relacionarConCerrada
     * @param sacopIdToRelate
     */
    private void crearSacopBorrador(PlanillaSacop1TO formaOriginal, Boolean relacionarConCerrada){
    	try {
    		int largo = Integer.parseInt(HandlerParameters.PARAMETROS.getLengthDocNumber());
    		String sufijo = "";
    		
    		while(sufijo.length() < largo){
    			sufijo += "0";
    		}
    		
    		String sacopNum = formaOriginal.getSacopnum().substring(0, formaOriginal.getSacopnum().lastIndexOf("-"));
    		sacopNum += "-" + sufijo;
    		
    		PlanillaSacop1TO formaBorrador = new PlanillaSacop1TO();
			if(relacionarConCerrada){
				
				//el usuario quiere crear la SACOP nueva y relacionar esta
				formaBorrador.setIdplanillasacop1(null);
				formaBorrador.setSacopnum(sacopNum);
				formaBorrador.setEmisor(formaOriginal.getEmisor());
				formaBorrador.setUsernotificado(formaOriginal.getUsernotificado());
				formaBorrador.setRespblearea(formaOriginal.getRespblearea());
				formaBorrador.setEstado(LoadSacop.edoBorrador);
				formaBorrador.setOrigensacop(formaOriginal.getOrigensacop());
				formaBorrador.setFechaemision(ToolsHTML.calendarToTimeStampString(Calendar.getInstance()));
				formaBorrador.setDescripcion(formaOriginal.getDescripcion());
				formaBorrador.setCausasnoconformidad(formaOriginal.getCausasnoconformidad());
				//formaBorrador.setIdClasificacion(formaOriginal.getIdClasificacion());
				formaBorrador.setSacopRelacionadas(formaOriginal.getIdplanillasacop1());
				formaBorrador.setActive("1");
				formaBorrador.setNoconformidades(formaOriginal.getNoconformidades());
				formaBorrador.setNoconformidadesref(formaOriginal.getNoconformidadesref());
				formaBorrador.setFechaWhenDiscovered(formaOriginal.getFechaWhenDiscovered());
				formaBorrador.setCorrecpreven(formaOriginal.getCorrecpreven());
				formaBorrador.setRequisitosaplicable(formaOriginal.getRequisitosaplicable());
				formaBorrador.setSolicitudinforma(formaOriginal.getSolicitudinforma());
				formaBorrador.setProcesosafectados(formaOriginal.getProcesosafectados());
				formaBorrador.setAccionesrecomendadas(formaOriginal.getAccionesrecomendadas());

				formaBorrador.setUsuarioSacops1(formaOriginal.getUsuarioSacops1()); // el solicitante
				
				// datos del hallazgo asociado (registro)
				formaBorrador.setIdDocumentRelated(formaOriginal.getIdDocumentRelated());
				formaBorrador.setNumVerDocumentAssociate(formaOriginal.getNumVerDocumentAssociate());
				formaBorrador.setNameDocumentAssociate(formaOriginal.getNameDocumentAssociate());
				
				PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
				
				oPlanillaSacop1DAO.insertar(formaBorrador);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
