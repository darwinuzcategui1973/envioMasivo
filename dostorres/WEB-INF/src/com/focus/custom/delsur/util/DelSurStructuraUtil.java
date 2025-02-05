package com.focus.custom.delsur.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.beans.NodesTree;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.custom.delsur.dto.CreditoDTO;
import com.focus.custom.delsur.dto.RecaudoCreditoDTO;

public final class DelSurStructuraUtil {
	private static int MAIN_LOCATION_ID;
	
	//nodos pre definidos por solicitud
	private static final String NODE_NAME_ANTECEDENTES_JURIDICOS = "Antecedentes Juridicos";
	private static final String NODE_NAME_ANTECEDENTES_ECONOMICOS = "Antecedentes Economicos";
	private static final String NODE_NAME_ANTECEDENTES_FINANCIEROS = "Antecedentes Financieros";
	//private static final String NODE_NAME_DATOS_GENERALES = "Datos Generales";
	private static final String NODE_INFORMACION_INTERNA = "Informacion Interna";
	private static final String NODE_NAME_GARANTIAS = "Garantias";
	
	private static final Logger log = LoggerFactory.getLogger(DelSurStructuraUtil.class);
	private static final String LOCATION_NODE_TYPE = "1";
	private static final String FOLDER_NODE_TYPE = "3";
	
	/**
	 * 
	 * @param props
	 */
	private static int checkCreditLocation(CustomProperties props){
		final String query = "SELECT l.idnode, COUNT(*) "
				+ " FROM location l, struct s "
				+ " WHERE s.idnode = l.idnode "
				+ " AND s.name = ?"
				+ " GROUP BY l.idnode";
		
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int locationCode = -1;
		
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			ps = con.prepareStatement(JDBCUtil.replaceCastMysql(query));
			
			ps.setString(1, props.getLocationNameToCredits());
			
			rs = ps.executeQuery();
			if(rs.next() && rs.getInt(2) > 0){
				locationCode = rs.getInt(1);
				log.info("La localidad para los expedientes de creditos existe.");
			} else {
				log.info("La localidad de los expedientes de creditos, no existe, debe ser creada");
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Error verificando la localidad en la estructura. "
					+ "El error fue: " + e.getMessage(), e);
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
			try {
				ps.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
			try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
		return locationCode;
	}
	
	/**
	 * 
	 * @param props
	 */
	private static String getNodeId(String idNodeParent, String nodeName){
		final String query = "SELECT s.idnode "
				+ " FROM struct s "
				+ " WHERE s.idnodeparent = ?"
				+ " AND s.name = ? ";
		
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String nodeId = null;
		
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			ps = con.prepareStatement(JDBCUtil.replaceCastMysql(query));
			
			ps.setString(1, idNodeParent);
			ps.setString(2, nodeName);
			
			rs = ps.executeQuery();
			if(rs.next() && rs.getString(1)!= null){
				nodeId = rs.getString(1);
				log.info("El nodo solicitado '" + nodeName +"' tiene como id:" + nodeId);
			} else {
				log.info("El nodo solicitado '" + nodeName +"', no existe, debe ser creado");
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Error verificando la localidad en la estructura. "
					+ "El error fue: " + e.getMessage(), e);
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
			try {
				ps.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
			try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
		return nodeId;
	}
	
	/**
	 * Basandonos en el nombre de la localidad donde seran almacenados los
	 * expedientes de credito. La creamos si no existe o en caso de existir solo
	 * verificamos.
	 * 
	 * @param props
	 * @return
	 */
	public static int crearLocalidadCredito(CustomProperties props){
		int locationId = checkCreditLocation(props);
		
		if(locationId == -1){
			NodesTree mainNodeName = null;
			
			try {
				mainNodeName = HandlerStruct.getNameNode("1");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return locationId;
			}
			
			BaseStructForm forma = new BaseStructForm();
			forma.setCantExpDoc("1");
			forma.setCheckborradorCorrelativo((byte) 0);
			forma.setCheckvijenToprint((byte) 0);
			forma.setCmd("insert");
			forma.setConditional((byte) 1);
			forma.setCopy((byte) 0);
			forma.setDaysWF("");
			forma.setDescription("Localidad para los expedientes de credito");
			forma.setDisableUserWF((byte) 1);
			forma.setExpireWF((byte) 1);
			forma.setHeredarPrefijo((byte) 0);
			forma.setIdNode("");
			forma.setIdNodeParent("1");
			forma.setMajorId((byte) 0);
			forma.setMajorKeep("5");
			forma.setMinorId((byte) 0);
			forma.setName(props.getLocationNameToCredits());
			forma.setNameIcon("menu_link_external.gif");
			forma.setNodeType(LOCATION_NODE_TYPE);
			forma.setNotify((byte) 1);
			forma.setNumber(0);
			forma.setOwner("admin");
			forma.setParentPrefix("");
			forma.setPrefix("");
			forma.setRout(mainNodeName.getNameNode());
			forma.setSequential((byte) 1);
			forma.setShowCharge(0);
			forma.setTimeDocVenc(0);
			forma.setToImpresion(0);
			forma.setToListDist(0);
			forma.setToListDistNameUser("");
			forma.setTxttimeDocVenc("");
			forma.setTypePrefix((byte) 0);
			forma.setUnitExpDoc("D");
			forma.setVijenToprint(0);
			
			try {
				HandlerStruct.insert(forma);
				locationId = Integer.parseInt(forma.getIdNode());
				MAIN_LOCATION_ID = locationId;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return locationId;
	}
	
	/**
	 * 
	 * @param props
	 * @param nodeName Nombre del nodo a crear
	 * @param nodeParent id del nodo padre
	 * @param descripcion Descripcion del nodo a ser creado
	 * @return
	 */
	public static String crearNodoEnLocalidadDeCredito(CustomProperties props,
			String nodeName, String nodeParent, String descripcion){
		String nodeId = null;
		
		BaseStructForm forma = new BaseStructForm();
		forma.setCmd("insert");
		forma.setConditional((byte) 1);
		forma.setDescription(descripcion);
		forma.setDisableUserWF((byte) 1);
		forma.setExpireWF((byte) 1);
		forma.setIdNode("");
		forma.setIdNodeParent(nodeParent);
		forma.setName(nodeName);
		forma.setNameIcon("menu_folder_closed.gif");
		forma.setNodeType(FOLDER_NODE_TYPE);
		forma.setNotify((byte) 1);
		forma.setOwner("admin");
		forma.setParentPrefix("");
		forma.setPrefix("");
		forma.setSequential((byte) 1);
		forma.setToListDist(0);
		forma.setToListDistNameUser("");
		
		try {
			HandlerStruct.insert(forma);
			nodeId = forma.getIdNode();
			log.info("Creado nodo '" + nodeName 
					+ "' dentro del idParent=" + nodeParent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.getLocalizedMessage(), e);
		}
		
		return nodeId;
	}
	
	/**
	 * 
	 * @param creditos
	 * @param props
	 * @param creditLocationId
	 */
	public static void procesarNodosYRecaudos(List<CreditoDTO> creditos,
			CustomProperties props,	int creditLocationId,
			String carpetaParaDigitalizados){
		
		PermissionUserForm forma = new PermissionUserForm();
		forma.setCommand(Constants.cmdToStruct);
		forma.setIdDocument("null");
		forma.setNodeType(FOLDER_NODE_TYPE);
		forma.setToAddDocument((byte) 1);
		forma.setToDownload((byte) 1);
		forma.setToEditDocs((byte) 1);
		forma.setToView((byte) 1);
		forma.setToViewDocs((byte) 1);
		
		if(creditos != null){
			for (CreditoDTO creditoDTO : creditos) {
				//debemos verificar paso a paso la estructura contenida
				//en cada registro, para crear los nodos respectivos
				//antes de cualquier intento de import
				
				//tomamos el usuario
				Users usuario = HandlerDBUser.getUser(creditoDTO.getCodigoUsuarioAbanks());
				/*
				 * forma.setIdPerson(usuario.getIdPerson());
				//forma.setIdGroup(Long.parseLong(usuario.getIdGroup()));
				forma.setIdUser(creditoDTO.getCodigoUsuarioAbanks());
				forma.setNameUser(usuario.getNameUser());
				
				//asignamos permisos de visualizacion para la localidad principal
				forma.setIdStruct(Integer.toString(MAIN_LOCATION_ID));
				forma.setNodeType(LOCATION_NODE_TYPE);
				try {
					HandlerDBUser.updateSecurityStructUser(forma, 
							LOCATION_NODE_TYPE, 
							usuario.getNameUser());
					log.info("Asignada seguridad al usuario " + usuario.getNameUser()
							+ " en la localidad principal de los creditos");
				} catch (Exception e) {
					// TODO: handle exception
					log.error("No se le pudo asignar seguridad al usuario "
							+ usuario.getNameUser()
							+ " en la localidad principal de los creditos", e);
				}
				*/
				//de aqui en adelante seran solo nodos tipo carpeta
				forma.setNodeType(FOLDER_NODE_TYPE);
				
				//verificamos la region
				String regionId = getNodeId(Integer.toString(creditLocationId), 
						creditoDTO.getNombreRegion());
				if(regionId == null){
					//debemos crear la carpeta de la region
					regionId = crearNodoEnLocalidadDeCredito(props, 
							creditoDTO.getNombreRegion(), 
							Integer.toString(creditLocationId), 
							"Nodo de la region " + creditoDTO.getNombreRegion());
				}
				//asignamos seguridad al usuario en este nodo
				/*
				forma.setIdStruct(regionId);
				try {
					HandlerDBUser.updateSecurityStructUser(forma, 
							FOLDER_NODE_TYPE, 
							usuario.getNameUser());
					log.info("Asignada seguridad al usuario " + usuario.getNameUser()
							+ " en la carpeta de la region " + creditoDTO.getNombreRegion());
				} catch (Exception e) {
					// TODO: handle exception
					log.error("No se le pudo asignar seguridad al usuario "
							+ usuario.getNameUser()
							+ " en la carpeta de la region " + creditoDTO.getNombreRegion(), e);
				}
				*/
				
				//verificamos la agencia
				String agenciaId = getNodeId(regionId, 
						creditoDTO.getNombreAgencia());
				if(agenciaId == null){
					agenciaId = crearNodoEnLocalidadDeCredito(props, 
							creditoDTO.getNombreAgencia(), 
							regionId, 
							"Nodo de la agencia " + creditoDTO.getNombreAgencia());
				}
				//asignamos seguridad al usuario en este nodo
				/*
				forma.setIdStruct(agenciaId);
				try {
					HandlerDBUser.updateSecurityStructUser(forma, 
							FOLDER_NODE_TYPE, 
							usuario.getNameUser());
					log.info("Asignada seguridad al usuario " + usuario.getNameUser()
							+ " en la carpeta de la agencia " + creditoDTO.getNombreAgencia());
				} catch (Exception e) {
					// TODO: handle exception
					log.error("No se le pudo asignar seguridad al usuario "
							+ usuario.getNameUser()
							+ " en la carpeta de la agencia " + creditoDTO.getNombreAgencia(), e);
				}
				*/
				
				//verifico la carpeta de datos generales
				//2013-08-26: Se elimina del arbol taxonomico la carpeta de datos generales
				/*
				String datosGeneralesId = getNodeId(agenciaId, 
						"Datos Generales");
				if(datosGeneralesId == null){
					datosGeneralesId = crearNodoEnLocalidadDeCredito(props, 
							"Datos Generales", 
							agenciaId, 
							"Nodo para los datos generales de las solicitudes de credito");
				}
				//asignamos seguridad al usuario en este nodo
				forma.setIdStruct(datosGeneralesId);
				try {
					HandlerDBUser.updateSecurityStructUser(forma, 
							FOLDER_NODE_TYPE, 
							usuario.getNameUser());
					log.info("Asignada seguridad al usuario " + usuario.getNameUser()
							+ " en la carpeta de Datos Generales");
				} catch (Exception e) {
					// TODO: handle exception
					log.error("No se le pudo asignar seguridad al usuario "
							+ usuario.getNameUser()
							+ " en la carpeta de Datos Generales", e);
				}
				*/
				
				//verificamos el producto
				String productoId = getNodeId(agenciaId, 
						creditoDTO.getNombreProducto());
				if(productoId == null){
					productoId = crearNodoEnLocalidadDeCredito(props, 
							creditoDTO.getNombreProducto(), 
							agenciaId, 
							"Nodo para el tipo de producto " + creditoDTO.getNombreProducto());
				}
				//asignamos seguridad al usuario en este nodo
				/*
				forma.setIdStruct(productoId);
				try {
					HandlerDBUser.updateSecurityStructUser(forma, 
							FOLDER_NODE_TYPE, 
							usuario.getNameUser());
					log.info("Asignada seguridad al usuario " + usuario.getNameUser()
							+ " en la carpeta asociada al producto " + creditoDTO.getNombreProducto());
				} catch (Exception e) {
					// TODO: handle exception
					log.error("No se le pudo asignar seguridad al usuario "
							+ usuario.getNameUser()
							+ " en la carpeta asociada al producto " + creditoDTO.getNombreProducto(), e);
				}
				*/
				
				//verificamos la modalidad
				String modalidadId = getNodeId(productoId, 
						creditoDTO.getNombreModalidadProducto());
				if(modalidadId == null){
					modalidadId = crearNodoEnLocalidadDeCredito(props, 
							creditoDTO.getNombreModalidadProducto(), 
							productoId, 
							"Nodo para la modalidad " + creditoDTO.getNombreModalidadProducto());
				}
				//asignamos seguridad al usuario en este nodo
				/*
				forma.setIdStruct(modalidadId);
				try {
					HandlerDBUser.updateSecurityStructUser(forma, 
							FOLDER_NODE_TYPE, 
							usuario.getNameUser());
					log.info("Asignada seguridad al usuario " + usuario.getNameUser()
							+ " en la carpeta de la modalidad " + creditoDTO.getNombreModalidadProducto());
				} catch (Exception e) {
					// TODO: handle exception
					log.error("No se le pudo asignar seguridad al usuario "
							+ usuario.getNameUser()
							+ " en la carpeta de la modalidad " + creditoDTO.getNombreModalidadProducto(), e);
				}
				*/
				
				//verifico la carpeta de la solicitud
				String solicitudId = getNodeId(modalidadId, 
						Integer.toString(creditoDTO.getNumeroSolicitud()));
				if(solicitudId == null){
					solicitudId = crearNodoEnLocalidadDeCredito(props, 
							Integer.toString(creditoDTO.getNumeroSolicitud()), 
							modalidadId, 
							"Nodo para los documentos especificos de la solicitud de credito " + creditoDTO.getNumeroSolicitud());
				}
				//asignamos seguridad al usuario en este nodo
				/*
				forma.setIdStruct(solicitudId);
				try {
					HandlerDBUser.updateSecurityStructUser(forma, 
							FOLDER_NODE_TYPE, 
							usuario.getNameUser());
					log.info("Asignada seguridad al usuario " + usuario.getNameUser()
							+ " en la carpeta de la solicitud " + creditoDTO.getNumeroSolicitud());
				} catch (Exception e) {
					// TODO: handle exception
					log.error("No se le pudo asignar seguridad al usuario "
							+ usuario.getNameUser()
							+ " en la carpeta de la solicitud " + creditoDTO.getNumeroSolicitud(), e);
				}
				*/
				
				//verifico la carpeta de "Antecedentes Juridicos" de la solicitud
				String antecedentesJuridicosId = getNodeId(solicitudId, 
						NODE_NAME_ANTECEDENTES_JURIDICOS);
				if(antecedentesJuridicosId == null){
					antecedentesJuridicosId = crearNodoEnLocalidadDeCredito(props, 
							NODE_NAME_ANTECEDENTES_JURIDICOS, 
							solicitudId, 
							"Nodo para los Antecedentes Juridicos especificos de la solicitud de credito " + creditoDTO.getNumeroSolicitud());
				}
				//asignamos seguridad al usuario en este nodo
				/*
				forma.setIdStruct(antecedentesJuridicosId);
				try {
					HandlerDBUser.updateSecurityStructUser(forma, 
							FOLDER_NODE_TYPE, 
							usuario.getNameUser());
					log.info("Asignada seguridad al usuario " + usuario.getNameUser()
							+ " en la carpeta de Antecedentes Juridicos de la solicitud " + creditoDTO.getNumeroSolicitud());
				} catch (Exception e) {
					// TODO: handle exception
					log.error("No se le pudo asignar seguridad al usuario "
							+ usuario.getNameUser()
							+ " en la carpeta de Antecedentes Juridicos de la solicitud " + creditoDTO.getNumeroSolicitud(), e);
				}
				*/
				
				//verifico la carpeta de "Antecedentes Economicos" de la solicitud
				String antecedentesEconomicosId = getNodeId(solicitudId, 
						NODE_NAME_ANTECEDENTES_ECONOMICOS);
				if(antecedentesEconomicosId == null){
					antecedentesEconomicosId = crearNodoEnLocalidadDeCredito(props, 
							NODE_NAME_ANTECEDENTES_ECONOMICOS, 
							solicitudId, 
							"Nodo para los Antecedentes Economicos especificos de la solicitud de credito " + creditoDTO.getNumeroSolicitud());
				}
				//asignamos seguridad al usuario en este nodo
				/*
				forma.setIdStruct(antecedentesEconomicosId);
				try {
					HandlerDBUser.updateSecurityStructUser(forma, 
							FOLDER_NODE_TYPE, 
							usuario.getNameUser());
					log.info("Asignada seguridad al usuario " + usuario.getNameUser()
							+ " en la carpeta de Antecedentes Economicosde la solicitud " + creditoDTO.getNumeroSolicitud());
				} catch (Exception e) {
					// TODO: handle exception
					log.error("No se le pudo asignar seguridad al usuario "
							+ usuario.getNameUser()
							+ " en la carpeta de Antecedentes Economicos de la solicitud " + creditoDTO.getNumeroSolicitud(), e);
				}
				*/
				//verifico la carpeta de "Antecedentes Financieros" de la solicitud
				String antecedentesFinancierosId = getNodeId(solicitudId, 
						NODE_NAME_ANTECEDENTES_FINANCIEROS);
				if(antecedentesFinancierosId == null){
					antecedentesFinancierosId = crearNodoEnLocalidadDeCredito(props, 
							NODE_NAME_ANTECEDENTES_FINANCIEROS, 
							solicitudId, 
							"Nodo para los Antecedentes Financieros especificos de la solicitud de credito " + creditoDTO.getNumeroSolicitud());
				}
				//asignamos seguridad al usuario en este nodo
				/*
				forma.setIdStruct(antecedentesFinancierosId);
				try {
					HandlerDBUser.updateSecurityStructUser(forma, 
							FOLDER_NODE_TYPE, 
							usuario.getNameUser());
					log.info("Asignada seguridad al usuario " + usuario.getNameUser()
							+ " en la carpeta de Antecedentes Financieros de la solicitud " + creditoDTO.getNumeroSolicitud());
				} catch (Exception e) {
					// TODO: handle exception
					log.error("No se le pudo asignar seguridad al usuario "
							+ usuario.getNameUser()
							+ " en la carpeta de Antecedentes Financieros de la solicitud " + creditoDTO.getNumeroSolicitud(), e);
				}
				*/
				//verifico la carpeta de "Informacion Interna" de la solicitud
				String informacionInternaId = getNodeId(solicitudId, 
						NODE_INFORMACION_INTERNA);
				if(informacionInternaId == null){
					informacionInternaId = crearNodoEnLocalidadDeCredito(props, 
							NODE_INFORMACION_INTERNA, 
							solicitudId, 
							"Nodo para la Informacion Interna especifica de la solicitud de credito " + creditoDTO.getNumeroSolicitud());
				}
				//asignamos seguridad al usuario en este nodo
				/*
				forma.setIdStruct(informacionInternaId);
				try {
					HandlerDBUser.updateSecurityStructUser(forma, 
							FOLDER_NODE_TYPE, 
							usuario.getNameUser());
					log.info("Asignada seguridad al usuario " + usuario.getNameUser()
							+ " en la carpeta de Informacion Interna de la solicitud " + creditoDTO.getNumeroSolicitud());
				} catch (Exception e) {
					// TODO: handle exception
					log.error("No se le pudo asignar seguridad al usuario "
							+ usuario.getNameUser()
							+ " en la carpeta de Informacion Interna de la solicitud " + creditoDTO.getNumeroSolicitud(), e);
				}
				*/
				//verifico la carpeta de "Garantias" de la solicitud
				String garantiasId = getNodeId(solicitudId, 
						NODE_NAME_GARANTIAS);
				if(garantiasId == null){
					garantiasId = crearNodoEnLocalidadDeCredito(props, 
							NODE_NAME_GARANTIAS, 
							solicitudId, 
							"Nodo para las Garantias especificas de la solicitud de credito " + creditoDTO.getNumeroSolicitud());
				}
				//asignamos seguridad al usuario en este nodo
				/*
				forma.setIdStruct(garantiasId);
				try {
					HandlerDBUser.updateSecurityStructUser(forma, 
							FOLDER_NODE_TYPE, 
							usuario.getNameUser());
					log.info("Asignada seguridad al usuario " + usuario.getNameUser()
							+ " en la carpeta de Garantias de la solicitud " + creditoDTO.getNumeroSolicitud());
				} catch (Exception e) {
					// TODO: handle exception
					log.error("No se le pudo asignar seguridad al usuario "
							+ usuario.getNameUser()
							+ " en la carpeta de Garantias de la solicitud " + creditoDTO.getNumeroSolicitud(), e);
				}
				*/
				String palabrasClave = "" + creditoDTO.getNumeroSolicitud()
						+ "," + creditoDTO.getNumeroIdentificacionCliente()
						+ "," + creditoDTO.getCodigoCliente()
						+ "," + creditoDTO.getNombreProducto()
						+ "," + creditoDTO.getNombreSolicitante()
						+ "," + creditoDTO.getApellidoSolicitante();
				
				if(creditoDTO.getListadoRecaudos() == null){
					//la creamos vacia para ejecutar sin problemas el metodo siguiente
					creditoDTO.setListadoRecaudos(new LinkedList<RecaudoCreditoDTO>());
				} else {
					for (int i = 0; i < creditoDTO.getListadoRecaudos().size(); i++) {
						RecaudoCreditoDTO recaudoDTO = creditoDTO.getListadoRecaudos().get(i);
						String idNode = null;
						String nodeName = props.getNodeNameToDocId(recaudoDTO.getCodigoDocumento());
						nodeName = (nodeName == null ? null : nodeName.toLowerCase().trim());
						
						if(NODE_NAME_ANTECEDENTES_ECONOMICOS.toLowerCase().trim().equals(nodeName)){
							idNode = antecedentesEconomicosId;
						} else if(NODE_NAME_ANTECEDENTES_FINANCIEROS.toLowerCase().trim().equals(nodeName)){
							idNode = antecedentesFinancierosId;
						} else if(NODE_NAME_ANTECEDENTES_JURIDICOS.toLowerCase().trim().equals(nodeName)){
							idNode = antecedentesJuridicosId;
						} else if(NODE_INFORMACION_INTERNA.toLowerCase().trim().equals(nodeName)){
							idNode = informacionInternaId;
						} else if(NODE_NAME_GARANTIAS.toLowerCase().trim().equals(nodeName)){
							idNode = garantiasId;
						}
						
						recaudoDTO.setIdNode(idNode);
						creditoDTO.getListadoRecaudos().set(i, recaudoDTO);
					}
				}
				
				DelSurRemoteFileUtil.readRemoteFiles(props.getMaxWaitReading(),
						props.getRemotePathPrefix(), 
						carpetaParaDigitalizados, 
						props.getRemotePathSeparator(),
						palabrasClave,
						usuario,
						creditoDTO);
			}
		} else {
			log.info("No se tienen solicitudes de credito para ser procesadas por QWeb");
		}
	}
}
