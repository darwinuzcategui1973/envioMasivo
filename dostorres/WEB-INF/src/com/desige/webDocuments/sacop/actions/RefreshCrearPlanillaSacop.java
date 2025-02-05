package com.desige.webDocuments.sacop.actions;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.sacop.forms.plantilla1;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.focus.qweb.dao.ActionRecommendedDAO;
import com.focus.qweb.dao.AreaDAO;
import com.focus.qweb.dao.CargoDAO;
import com.focus.qweb.dao.PossibleCauseDAO;
import com.focus.qweb.to.AreaTO;
import com.focus.qweb.to.CargoTO;

/**
 * Created by IntelliJ IDEA.
 * User: Administrador
 * Date: 25/03/2006
 * Time: 09:39:36 AM
 * To change this template use File | Settings | File Templates.
 */

public class RefreshCrearPlanillaSacop extends SuperAction {
    static Logger log = LoggerFactory.getLogger(RefreshCrearPlanillaSacop.class.getName());
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {

        super.init(mapping,form,request,response);

        try{
                Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"),DesigeConf.getProperty("country.Default"));
                ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",defaultLocale);

                String sacoprelacionada=request.getParameter("sacoprelacionada")!=null?request.getParameter("sacoprelacionada"):"";
                putAtributte("sacoprelacionada",sacoprelacionada);
                
                
    			// causas
    			PossibleCauseDAO oPossibleCauseDAO = new PossibleCauseDAO();
    			putAtributte("listPossibleCause", oPossibleCauseDAO.listarPossibleCauseAlls(null));
    			putAtributte("ListOrderPossibleCause", oPossibleCauseDAO.listarOrderPossibleCauseAlls(null));
    			
    			
    			// acciones recomendadas
    			ActionRecommendedDAO oActionRecommendedDAO = new ActionRecommendedDAO();
    			putAtributte("listActionRecommended", oActionRecommendedDAO.listarActionRecommendedAlls(null));
    			putAtributte("listOrderActionRecommended", oActionRecommendedDAO.listarOrderActionRecommendedAlls(null));
                
				
              //se usa cuando es intouch-sacop
                String edoBorradorenbd =  request.getParameter("edoBorradorenbd")!=null?request.getParameter("edoBorradorenbd"):"";
                putAtributte("edoBorradorenbd",edoBorradorenbd);
                if (ToolsHTML.isEmptyOrNull(edoBorradorenbd)){
                      putAtributte("EsnuevanoSacop","1");
                }

                plantilla1 forma = (plantilla1)form;
                //variable que me permite mostrar de tres en tres los textos de las formas
                putAtributte("mostrardetrestext",request.getParameter("mostrardetrestext")!=null?request.getParameter("mostrardetrestext"):"0");

                StringBuffer requisitosaplicable=new StringBuffer();
                StringBuffer procesosafectados = new StringBuffer();
                StringBuffer solicitudinforma = new StringBuffer();
                String cargosacop = String.valueOf(HandlerParameters.PARAMETROS.getCargosacop());
                removeAttribute("areaAfectada");
                removeAttribute("norms");
                removeAttribute("norms1");
                removeAttribute("procesosSacop");
                removeAttribute("procesosSacop1");
                removeAttribute("usuarios");
                removeAttribute("usuarios1");
                removeAttribute("sacoplantilla");
                boolean showCharge=false;
                if (cargosacop.equalsIgnoreCase(Constants.typeNumByLocationVacio)||(ToolsHTML.isEmptyOrNull(cargosacop))) {
                	showCharge=true;
                }

                if (forma.getUsersSelected()!=null) {
                	Object[] datos = forma.getUsersSelected();
                	int pos = 0;
                	for (int row = 0; row < datos.length; row++) {
                		Object dato = datos[row];
                		solicitudinforma.append(HandlerDocuments.getField("idperson","person","nameUser",dato.toString(),"=",1,Thread.currentThread().getStackTrace()[1].getMethodName()));
                		if (row+1<datos.length){
                			solicitudinforma.append(",");
                		}
                	}
                }
                
                Collection usuarios =null;
                Collection usuarios1=null;
                if (ToolsHTML.checkValue(solicitudinforma.toString())){
                	log.info("Verificando listado de usuarios cuando se tienen valores de 'Informa a:'");
                	//obtenemos los usuarios no seleccionados
                	usuarios = HandlerProcesosSacop.getSolicitudinformaNotIn("idPerson",solicitudinforma.toString(),showCharge);
                	//obtenemos los usuarios  seleccionados
                	usuarios1 = HandlerProcesosSacop.getSolicitudinforma("idPerson",solicitudinforma.toString(),showCharge,true);
                }else{
                	log.info("Verificando listado de usuarios cuando no se tienen valores de 'Informa a:'");
                	usuarios = HandlerProcesosSacop.getSolicitudinforma("idPerson",null,showCharge,true);
                }
                
                Collection usuariosArea = HandlerProcesosSacop.getSolicitudinforma("idPerson",null,showCharge, true);
                
                if (forma.getNormsisoSelected()!=null) {
                	Object[] datos = forma.getNormsisoSelected();
                	int pos = 0;
                	for (int row = 0; row < datos.length; row++) {
                		Object dato = datos[row];
                		requisitosaplicable.append(dato.toString());
                		if (row+1<datos.length){
                			requisitosaplicable.append(",");
                		}
                	}
                	forma.setNormsisoSelected(null);
                }

                //obtenemos las normas  seleccionados
                Collection norms=null;
                Collection norms1=null;
                if (ToolsHTML.checkValue(requisitosaplicable.toString())){
                      //obtenemos las normas no seleccionados
                     norms = HandlerProcesosSacop.getRequisitosAplicableSacopNotIn("idNorm",requisitosaplicable.toString());
                     norms1 = HandlerProcesosSacop.getRequisitosAplicableSacop("idNorm",requisitosaplicable.toString());
                }else{
                     norms = HandlerProcesosSacop.getRequisitosAplicableSacop("idNorm",null);
                }

                if (forma.getProceafectadosSelected()!=null) {
                	Object[] datos = forma.getProceafectadosSelected();
                	int pos = 0;
                	for (int row = 0; row < datos.length; row++) {
                		Object dato = datos[row];
                		procesosafectados.append(dato.toString());
                		if (row+1<datos.length){
                			procesosafectados.append(",");
                		}
                	}
                	forma.setProceafectadosSelected(null);
                }

                String responsable=request.getParameter("responsable")!=null?request.getParameter("responsable"):"";
                if (ToolsHTML.isEmptyOrNull(responsable)){
                    responsable=request.getParameter("responsablee")!=null?request.getParameter("responsablee"):"";
                }
                if(ToolsHTML.isEmptyOrNull(responsable)){
                	return goError();
                }
                
                //obtenemos los procesos  seleccionados  para la seguridad del usuario
                Collection procesosSacop=null;
                Collection procesosSacop1=null;
                String idGrupoArea="";
                String idPersonArea="";
                if (!ToolsHTML.isEmptyOrNull(responsable)){
                    //String responsableArea=responsable;
                    String query=" from person where nameuser='"+responsable+"'" +" and accountActive='"+Constants.permission+"'";
                    idPersonArea=HandlerDocuments.getField2("idperson",query.toString());
                    idGrupoArea=HandlerDocuments.getField2("idGrupo",query.toString());
                    StringBuffer idStructs = new StringBuffer(50);
                    idStructs.append("1");
                    Hashtable security = HandlerGrupo.getAllSecurityForGroup(idGrupoArea,idStructs);
                    HandlerDBUser.getAllSecurityForUser(Long.parseLong(idPersonArea.toString()),security,idStructs);
                    procesosSacop = HandlerProcesosSacop.getProcesosSacop(security,responsable,idGrupoArea);
                }
                forma.setEdodelsacop(LoadSacop.obtenerEdoBorrador("0"));
//                putAtributte("responsable",responsable.toString());
                if(!ToolsHTML.isEmptyOrNull(responsable)){
                    StringBuffer query=new StringBuffer(" from person p where p.nameuser='");
                    query.append(responsable.toString()).append("'");
                    query.append(" and  p.accountactive='").append(Constants.permission).append("'");
                    String cargo=HandlerDocuments.getField2("cargo",query.toString());
                    
                    putAtributte("responsable",responsable.toString());
                    putAtributte("areaAfectada",HandlerDBUser.getCargoAndArea(cargo,false));
                }else{
                    putAtributte("responsable","-1");
                    putAtributte("areaAfectada","-1");
                }
                
                /*
                if (forma.getFechaWhenDiscovered() != null) {
                	forma.setFechaWhenDiscovered(ToolsHTML.sdfShowWithoutHour.format(forma.getFechaWhenDiscovered()));
    			}
                */
                
				if (forma.getNoconformidadesref() != null && !forma.getNoconformidadesref().trim().equals("")) {
					forma.setNoConformidadesDetail(LoadSacop.getNoConformidadesDetail(forma.getNoconformidadesref(),forma.getIdplanillasacop1()));
				}
                
                
                // se obtienen los origen de la SACOP
                Collection clasificacionPlanillasSacop = HandlerProcesosSacop.getClasificacionPlanillasSacop(null);
				putAtributte("clasificacionPlanillasSacop", clasificacionPlanillasSacop);
				putAtributte("idClasificacion", request.getParameter("clasificacionSACOP"));
				
				
				// es el area que tenemos que buscar del responsable del sacop
				String[] values = HandlerDocuments.getFields(new String[] { "nombres", "apellidos", "cargo" }, "person", "idperson",
						forma.getUsuarioSacops1(), false);
				
				String nombre = null;
				String cargo = null;
				if (!showCharge) {
					if (values != null) {
						nombre = values[0] + " " + values[1];
						cargo = values[2];
					}
				} else {
					if (values != null) {
						nombre = values[0] + " " + values[1];
						cargo = values[2];
					}
				}
				CargoTO oCargoTO = new CargoTO();
				CargoDAO oCargoDAO = new CargoDAO();
				
				AreaTO oAreaTO = new AreaTO();
				AreaDAO oAreaDAO = new AreaDAO();
				
				oCargoTO.setIdCargo(cargo);
				oCargoDAO.cargar(oCargoTO);
				
				oAreaTO.setIdarea(oCargoTO.getIdArea());
				oAreaDAO.cargar(oAreaTO);
			
				forma.setSolicitantetxt(nombre);
				forma.setCargoSolicitante(oCargoTO.getCargo() + " -" + oAreaTO.getArea() + "-");
				// fin: cargo del solicitante


				
				//putAtributte("areaAfectada",area);
                putAtributte("norms",norms);
                putAtributte("norms1",norms1);
                putAtributte("procesosSacop",procesosSacop);
                putAtributte("procesosSacop1",procesosSacop1);
                putAtributte("usuarios",usuarios);
                putAtributte("usuarios1",usuarios1);
                putAtributte("usuariosArea",usuariosArea);
                putAtributte("sacoplantilla",forma);
                return goSucces();
        }catch(Exception e){
        	log.error(e.getMessage());
            e.printStackTrace();
        } finally {
        	/**/
        }
        
        return goError();
    }
}
