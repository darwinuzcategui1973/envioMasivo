package com.focus.qweb.bean;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;

import com.desige.webDocuments.dao.ConfDocumentoDAO;
import com.desige.webDocuments.document.actions.EditDocumentAction;
import com.desige.webDocuments.document.actions.ShowDocument;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.files.forms.DocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.StringUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.migracion.bean.MigracionBean;
import com.focus.qweb.dao.IndiceDAO;
import com.focus.qweb.to.EquivalenciasTO;

import sun.jdbc.rowset.CachedRowSet;

public class Migracion {

	private SimpleDateFormat dateFormat;
	private SimpleDateFormat format;

	private PrintWriter log;
	private PrintWriter ok;
	private PrintWriter mal;

	private Reader datos;
	private ArrayList equivalencias;
	private HashMap listaTipoDocumento;
	private HashMap listaNormaIso;
	private HttpServletRequest request; 
	
	private boolean validacion = false;

	public static String errorProceso = "";

	private static String nameFileLog = "";
	
	private ArrayList conf = new ArrayList();
	private DocumentForm obj = null;
	

	private String[] etiquetas = new String[] { 
			"numero", //0
			"nombre", //1
			"carpetaDestino", //2 
			"carpetaActual", //3
			"publicado", //4
			"propietario", //5
			"tipoDocumento", //6
			"norma", //7
			"palabrasClaves", //8 
			"url", //9 
			"descripcion", //10 
			"comentarios", //11
			"nombreFisico", //12
			"status", //13
			"creado", //14
			"vencimiento", //15 
			"verMayor", //16
			"verMenor", //17
			"documentosRelacionados", //18 
			"d1","d2","d3","d4","d5","d6","d7","d8","d9","d10",
			"d11","d12","d13","d14","d15","d16","d17","d18","d19","d20",
			"d21","d22","d23","d24","d25","d26","d27","d28","d29","d30",
			"d31","d32","d33","d34","d35","d36","d37","d38","d39","d40",
			"d41","d42","d43","d44","d45","d46","d47","d48","d49","d50",
			"d51","d52","d53","d54","d55","d56","d57","d58","d59","d60",
			"d61","d62","d63","d64","d65","d66","d67","d68","d69","d70",
			"d71","d72","d73","d74","d75","d76","d77","d78","d79","d80",
			"d81","d82","d83","d84","d85","d86","d87","d88","d89","d90",
			"d91","d92","d93","d94","d95","d96","d97","d98","d99","d100"
			};

	public String getNameFileLog() {
		return nameFileLog;
	}

	public void setUp(String prefijo, String path) throws ClassNotFoundException, SQLException, IOException {
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		format = new SimpleDateFormat("dd-MM-yyyy: hh:mm:ss");
		prefijo = "";
		String diahora = format.format(new Date());
		diahora = diahora.replaceAll(":", "_");
		diahora = diahora.replaceAll(" ", "");
		nameFileLog = "migracion_" + diahora;
		String nameFile = path.concat("logs").concat(File.separator).concat(nameFileLog);

		log = new PrintWriter(new BufferedWriter(new FileWriter(nameFile + prefijo + "_LOG.txt")));
		ok = new PrintWriter(new BufferedWriter(new FileWriter(nameFile + prefijo + "_OK.txt")));
		mal = new PrintWriter(new BufferedWriter(new FileWriter(nameFile + prefijo + "_MAL.txt")));
	}

	public synchronized void inicio(HttpServletRequest request, String nombreLogs, Reader datos, ArrayList equivalencias, String path, boolean isValidacion) throws Exception {

		//System.out.println("Procesando datos = ");

		this.validacion=isValidacion;
		this.datos = datos;
		this.equivalencias = equivalencias;
		setUp(nombreLogs, path);
		this.request = request;
		request.getSession().setAttribute("porcentajeBarra", "0");
		migrar();
	}

	public void migrar() throws ApplicationExceptionChecked {

		log.println(format.format(new Date()) + " iniciando la migracion");

		int numeroFilaActual = 0; // Contador de la fila actual

		String esPublico = "1"; // Logica inversa como la tabla

		// String carpetaEOC = "I.N.T.T.T.\\Oficina California\\";
		// String carpetaEOC = "Sogampi\\Oficina\\";
		String carpetaEOC = (String) DesigeConf.getProperty("raizMigracion");

		String dateSystem = ToolsHTML.date.format(new java.util.Date());

		Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
		ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);

		String filaActual = new String();
		MigracionBean bean = null;
		Connection con = null;

		int addFila = 0;
		int aux = 0;
		HashMap<String,String> claves = new HashMap<String,String>();
		String cadena;
		try {
			if (datos != null) {
				// claves por tipo
				StringBuffer query = new StringBuffer("SELECT id,idTypeDoc,clave FROM confdocumento_typedocument WHERE clave!=0 ORDER BY idTypeDoc,clave");
				CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
				while(crs.next()) {
					cadena = "";
					if(claves.containsKey(crs.getString("idTypeDoc"))) {
						cadena = claves.get(crs.getString("idTypeDoc"));
						cadena = cadena + ",";
						claves.remove(crs.getString("idTypeDoc"));
					}
					cadena += crs.getString("id");
					claves.put(crs.getString("idTypeDoc"),cadena);
				}
				
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				numeroFilaActual = datos.getRowBegin()-1;
				addFila = datos.getRowBegin();
				
				Hashtable subNodos = new Hashtable();
				// Para llenar el tree, colocamos el usuario como administrador,el grupo como administrador, la seguridad nula para que carque toda la estructura
				Hashtable tree = HandlerStruct.loadAllNodes(con, null, DesigeConf.getProperty("application.admon"), DesigeConf.getProperty("application.admon"), subNodos, null);
				
				int porcentaje = 0;
				int idNodo = 0;
				int idNodoPadre = 0;
				
    			// los datos para los nombre de los campos
    			ConfDocumentoDAO oConfDocumentoDAO = new ConfDocumentoDAO();
    			conf = (ArrayList) oConfDocumentoDAO.findAll();
    			StringBuffer msg = new StringBuffer();
				
    			// los valores equivalentes en la tabla de indice
    			IndiceDAO indiceDAO = new IndiceDAO();
    			HashMap mapIndice = indiceDAO.listarMapa();
    			
				principal: while (numeroFilaActual < datos.getRowsCount()) {
					try {
						numeroFilaActual++;
						porcentaje = ((numeroFilaActual*100)/datos.getRowsCount());
						request.getSession().setAttribute("porcentajeBarra", porcentaje);
						if(numeroFilaActual >= datos.getRowsCount()) {
							break;
						}
						log.println("--------------------------------------------------------------");
						log.println(format.format(new Date()) + " Carga de la fila N: " + (numeroFilaActual + addFila));

						String[] vItems = new String[43];
						Properties data = new Properties();
						bean = new MigracionBean();
						//HashMap<String> mapa = new HashMap<String>();
						EquivalenciasTO equiv = null;
						String eti;
						String val=null;
						String key = "";
						
						for(int i=0; i<equivalencias.size();i++) {
							equiv = (EquivalenciasTO)equivalencias.get(i);
							eti = etiquetas[Integer.parseInt(equiv.getIndice())-1];
							if(equiv.getPosicion().equals("1000")) {
								val = datos.getRowString(numeroFilaActual);
							} else if(Integer.parseInt(equiv.getPosicion())>0) {
								val = datos.getValue(numeroFilaActual, Integer.parseInt(equiv.getPosicion())-1);
							} else {
								val = "";
							}

							// buscamos el valor en la tabla de indice
							if(val!=null && !val.trim().equals("")) {
								key=equiv.getIndice().concat("-").concat(val);
								if(mapIndice.containsKey(key)) {
									val=(String)mapIndice.get(key);
								}
							}

							if(val.trim().equals("")) {
								val = equiv.getValor();
							}
							data.put(eti, val);
							////System.out.println(eti + " -> " + data.getProperty(eti));
						}

						bean.setNumero(data.getProperty("numero"));
						if (ToolsHTML.isEmptyOrNull(bean.getNumero())) {
							bean.setNumero("");
						}
						

						bean.setNombre(StringUtil.getOnlyNameFile(data.getProperty("nombre")));
						if (ToolsHTML.isEmptyOrNull(bean.getNombre())) {
							mal.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) + "| Nombre del documento está vacío o no es válido");
							mal.flush();
							continue principal;
						} else {
							bean.setNombre(bean.getNombre().replaceAll("\"", ""));
						}
						log.println(format.format(new Date()) + " Nombre: " + bean.getNombre());
						////System.out.println("------- " + format.format(new Date()) + "Nombre: " + bean.getNombre());

						bean.setCarpetaDestino(data.getProperty("carpetaDestino"));
						log.println(format.format(new Date()) + " carpetaDestino =: " + bean.getCarpetaDestino());
						if (ToolsHTML.isEmptyOrNull(bean.getCarpetaDestino())) {
							mal.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) + "|La Carpeta Destino está vacía o no es válida");
							mal.flush();
							continue principal;
						} else {
							// reemplazamos a la barra de window
							bean.setCarpetaDestino(bean.getCarpetaDestino().replaceAll("/", "\\\\"));
							// ///////////////////////////////////////////////////////////////////////
							// El 0 representa el nodo ppal, el [1] la localidad y
							// luego
							// carpetas y procesos...
							String[] estructura = bean.getCarpetaDestino().split("\\\\");

							// Carpetas y procesos
							idNodo = 0;
							int[] nodeInfo = {0, -1};
							for (int struc = 0; struc < estructura.length; struc++) {
								log.println(format.format(new Date()) + " Cargando: \"" + estructura[struc]+"\"");
								idNodoPadre = idNodo;
								// PENDIENTE: aqui podemos crear la nueva carpeta en caso de no existir
								nodeInfo = buscarIdNode(con, idNodoPadre, estructura[struc]);
								idNodo = nodeInfo[0];
								
								if((struc + 1) == estructura.length){
									//estamos en el ultimo nodo de la carpeta destino, validamos que sea
									//una carpeta o proceso
									if((nodeInfo[1] == 0) || (nodeInfo[1] == 1)){
										//el ultimo nodo no es una carpeta o proceso
										mal.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) + "|El Destino final no es una carpeta o proceso ->"+estructura[struc]);
										mal.flush();
										continue principal;
									}
								}
								
								//verificamos existencia o no del nodo en cuestion
								if (struc !=0 && idNodo==0) {
									if(isValidacion()) {
										mal.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) + "|La Carpeta Destino no es válida ->"+estructura[struc]);
										mal.flush();
										continue principal;
									} else {
										// creamos la carpeta faltante
										idNodo = crearNode(con, idNodoPadre, estructura[struc]);
									}
								}
								
							}
						}
						

						int lengthFile = StringUtil.getOnlyNameFile(data.getProperty("carpetaActual")).length();
						bean.setCarpetaActual(data.getProperty("carpetaActual"));
						if(bean.getCarpetaActual().indexOf(".")!=-1) {
							if(bean.getCarpetaActual().substring(bean.getCarpetaActual().lastIndexOf(".")).length()<=4) {
								bean.setCarpetaActual(bean.getCarpetaActual().substring(0, bean.getCarpetaActual().length() - lengthFile - 1));
							}
						}
						log.println(format.format(new Date()) + " carpetaActual =: " + bean.getCarpetaActual());
						if (ToolsHTML.isEmptyOrNull(bean.getCarpetaActual())) {
							mal.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) + "| Carpeta Actual está vacía o no es válida");
							mal.flush();
							continue principal;
						}

						//publicado = dateSystem;
						bean.setPublicado(data.getProperty("publicado")); // La fecha de publicacion la colocaremos con la de aprobacion
						if (ToolsHTML.isEmptyOrNull(bean.getPublicado()) || bean.getPublicado().length()<10 || bean.getPublicado().equals("1899-12-31") || bean.getPublicado().equals("1900-01-01")) {
							bean.setPublicado(null);
							esPublico = "1";
						}else{
							esPublico = "0";
						}
						log.println(format.format(new Date()) + " publicado =: " + bean.getPublicado());

						bean.setPropietario(data.getProperty("propietario"));
						log.println(format.format(new Date()) + " propietario =: " + bean.getPropietario());
						if (ToolsHTML.isEmptyOrNull(bean.getPropietario())) {
							mal.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) + "|El propietario es inválido");
							mal.flush();
							continue principal;
						}

						bean.setTipoDoc(obtenerIdTipoDocumento(con, data.getProperty("tipoDocumento")));
						log.println(format.format(new Date()) + " tipoDocumento =: " + bean.getTipoDoc());
						if (ToolsHTML.isEmptyOrNull(bean.getTipoDoc())) {
							mal.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) + "|El tipo de Documento está vacío o no es válido");
							mal.flush();
							continue principal;
						}

						bean.setNorma(data.getProperty("norma"));
						if(!ToolsHTML.isEmptyOrNull(bean.getNorma())) {
							bean.setNorma(obtenerIdNormaIso(bean.getNorma()));
							if (ToolsHTML.isEmptyOrNull(bean.getNorma())) {
								mal.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) + "|La norma no es válida");
								mal.flush();
								continue principal;
							}
						}
						log.println(format.format(new Date()) + " norma =: " + bean.getNorma());

						bean.setPalabrasClaves(data.getProperty("palabrasClaves"));
						log.println(format.format(new Date()) + " palabrasClaves =: " + bean.getPalabrasClaves());
						if (!ToolsHTML.isEmptyOrNull(bean.getPalabrasClaves())) {
							bean.setPalabrasClaves(bean.getPalabrasClaves().replaceAll("\"", ""));
							int indexKeys = bean.getPalabrasClaves().indexOf("Estacion_Scanneo");
							if (indexKeys > 1) {
								bean.setPalabrasClaves(bean.getPalabrasClaves().substring(0, indexKeys - 1));
							}
						} else {
							bean.setPalabrasClaves("");
						}

						bean.setUrl(data.getProperty("url"));
						log.println(format.format(new Date()) + " url =: " + bean.getUrl());
						if (bean.getUrl() == null) {
							bean.setUrl("");
						}

						bean.setDescripcion(data.getProperty("descripcion"));
						log.println(format.format(new Date()) + " descripcion =: " + bean.getDescripcion());
						if (ToolsHTML.isEmptyOrNull(bean.getDescripcion())) {
							bean.setDescripcion("");
						}

						bean.setComentarios(data.getProperty("comentarios"));
						if (ToolsHTML.isEmptyOrNull(bean.getComentarios())) {
							bean.setComentarios("");
						}
						log.println(format.format(new Date()) + " comentarios =: " + bean.getComentarios());

						bean.setNombreFisico(data.getProperty("nombreFisico"));
						if(bean.getNombreFisico().indexOf(bean.getCarpetaActual())!=-1) {
							bean.setNombreFisico(bean.getNombreFisico().substring(bean.getCarpetaActual().length() + 1));
						}
						if (ToolsHTML.isEmptyOrNull(bean.getNombreFisico())) {
							mal.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) + "|El nombre fisico esta vacio");
							mal.flush();
							continue principal;
						}
						log.println(format.format(new Date()) + " nombreFisico =: " + bean.getNombreFisico());

						bean.setStatus(data.getProperty("status"));
						if (ToolsHTML.isEmptyOrNull(bean.getStatus()) || "AB".indexOf(bean.getStatus().toUpperCase())==-1 ) {
							mal.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) + "|El Status esta vacio o no es válido");
							mal.flush();
							continue principal;
						}
						bean.setStatus(bean.getStatus().toUpperCase());
						

						bean.setCreado(data.getProperty("creado"));
						if (ToolsHTML.isEmptyOrNull(bean.getCreado()) || bean.getCreado().equals("1899-12-31") || bean.getCreado().equals("1900-01-01")) {
							bean.setCreado(dateSystem);
						}
						log.println(format.format(new Date()) + " creado =: " + bean.getCreado());


						bean.setVencimiento(data.getProperty("vencimiento"));
						if (ToolsHTML.isEmptyOrNull(bean.getVencimiento()) || bean.getVencimiento().equals("1899-12-31") || bean.getVencimiento().equals("1900-01-01")) {
							bean.setVencimiento("");
						}
						log.println(format.format(new Date()) + " vencimiento =: " + bean.getVencimiento());

						bean.setVersionMayor(data.getProperty("verMayor"));
						if (ToolsHTML.isEmptyOrNull(bean.getVersionMayor())) {
							mal.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) + "|La versión mayor no es válida");
							mal.flush();
							continue principal;
						}
						log.println(format.format(new Date()) + " verMayor =: " + bean.getVersionMayor());

						bean.setVersionMenor(data.getProperty("verMenor"));
						if (ToolsHTML.isEmptyOrNull(bean.getVersionMenor())) {
							mal.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) + "|La versión menor no es válida");
							mal.flush();
							continue principal;
						}
						log.println(format.format(new Date()) + " verMenor =: " + bean.getVersionMenor());

						bean.setDocumentoRelacionado(data.getProperty("documentosRelacionados"));
						log.println(format.format(new Date()) + " documentosRelacionados =: " + bean.getDocumentoRelacionado());
						
						
		                // campos adicionales
                		for(int k=0;k<conf.size();k++){
						    obj=(DocumentForm)conf.get(k);
						    String valor = data.getProperty(obj.getId());
							bean.set(obj.getId().toUpperCase(),valor);
							if (ToolsHTML.isEmptyOrNull(valor)) {
								bean.set(obj.getId().toUpperCase(),"");
							}
							msg.setLength(0);
							msg.append(format.format(new Date())).append(" ");
							msg.append(obj.getEtiqueta02()).append(" =: ");
							msg.append(String.valueOf(bean.get(obj.getId().toUpperCase())));
							log.println(msg.toString());
                        }
						

						log.println(format.format(new Date()) + " Ir a Agregar documento ");
						////System.out.println(format.format(new Date()) + " Ir a Agregar documento ");



						/** ***** VALIDACIONES DE CORRELACION DE DATOS ************ */
						BaseDocumentForm forma = new BaseDocumentForm();
						String path = bean.getCarpetaActual();
						forma.setIdNode(String.valueOf(idNodo));

						// Obligatorio Nombre del documento, carpeta destino,
						// carpeta actual, propietario, tipo de documento,
						// nombre fisico, status,

						// Si esta publicado:
						// fecha de aprobacion obligatoria
						// fecha de aprobacion mayor a fecha de creacion
						// fecha de aprobacion menor a fecha de vencimiento
						// fecha de vencimiento mayor a la fecha actual
						if (bean.getStatus().equals("A")) {
							if (ToolsHTML.isEmptyOrNull(bean.getPublicado()) || esPublico == null && !"0".equals(esPublico)) {
								// ERROR
								mal.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) + "|La fecha de aprobación requerida si está en status Aprobado");
								mal.flush();
								continue principal;
							}
						}
						////System.out.println("----- " + format.format(new Date()) + " publicado: " + bean.getPublicado());

						
						if(esPublico!=null && "0".equals(esPublico) && bean.getStatus()!=null && bean.getStatus().equals("A")){
						    //Si tiene fecha de aprobacion se valida que sea mayor a fecha de creacion
							if(!ToolsHTML.isEmptyOrNull(bean.getPublicado()) && !ToolsHTML.isEmptyOrNull(bean.getCreado()) &&
									ToolsHTML.compareDates(ToolsHTML.date.parse(bean.getPublicado()), ToolsHTML.date.parse(bean.getCreado()))<=0){
								//ERROR
								mal.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) + "| Fecha de aprobación inválida, no puede ser menor o igual a la fecha de creacion");
								mal.flush();
								continue principal;
							}
						    //Si tiene fecha de vencimento se valida que sea mayor a fecha actual y de aprobacion.
							if(!ToolsHTML.isEmptyOrNull(bean.getPublicado()) && !ToolsHTML.isEmptyOrNull(bean.getVencimiento()) &&
									ToolsHTML.compareDates(ToolsHTML.date.parse(bean.getPublicado()), ToolsHTML.date.parse(bean.getVencimiento()))>=0){
								//ERROR
								mal.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) + "| Fecha de aprobación inválida, no puede ser mayor o igual a la fecha de vencimiento");
								mal.flush();
								continue principal;
							}
							if(!ToolsHTML.isEmptyOrNull(bean.getVencimiento()) && !ToolsHTML.isEmptyOrNull(dateSystem) &&
									ToolsHTML.compareDates(ToolsHTML.date.parse(bean.getVencimiento()), ToolsHTML.date.parse(dateSystem))<=0){
								//ERROR
								mal.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) + "| Fecha de vencimiento inválida, no puede ser menor o igual a la fecha actual");
								mal.flush();
								continue principal;
							}
						}

						// SI SE MANEJA FECHA DE EXPIRACION DE APROBADOS
						// Si no tiene fecha de vencimiento de aprobacion se
						// calcula
						// SI SE MANEJA FECHA DE EXPIRACION DE BORRADORES
						// Se calcula fecha de expiracion de borrador

						// CARGA DE ESTRUCTURA
						String idNodeDocument = String.valueOf(idNodo);
						String idLocation = HandlerStruct.getIdLocationToNode(tree, idNodeDocument);
						// BaseStructForm location = HandlerStruct.loadStruct(idLocation);
						//BaseStructForm location = (BaseStructForm) tree.get(idLocation);

						// Fin de la Carga de la Estructura

		                BaseStructForm carpeta = (BaseStructForm)tree.get(idNodeDocument);
		                BaseStructForm localidad = (BaseStructForm)tree.get(idLocation);

		                HandlerStruct handlerStruct = new HandlerStruct();
		                byte typePrefix = 0;
		                if (idLocation!=null) {
		                    if (localidad!=null) {
		                    	if (localidad.getCheckborradorCorrelativo() == Constants.permission){
		                    		//Se valida si la localidad requiere que los borradors tengan correlativo
		                    		if(bean.getStatus()!=null && bean.getStatus().equals("B") && ToolsHTML.isEmptyOrNull(bean.getNumero())){
		                    			//ERROR
		            					mal.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) +  "| El número está vacío. Es Borrador pero la Localidad puede requerir correlativo");
		            					mal.flush();
		            					continue principal;
		                    		}
		                    	}

		                        typePrefix = localidad.getTypePrefix();

		        				//Si no existe version mayor se coloca cero o A dependiendo del tipo
		                        if(ToolsHTML.isEmptyOrNull(bean.getVersionMayor())){
		                        	forma.setMayorVer(ToolsHTML.getInitValue(localidad.getMajorId(),false));
		                        }

		        				//Si no existe version menor se coloca cero
		                        if(ToolsHTML.isEmptyOrNull(bean.getVersionMenor())){
			                        if (!ToolsHTML.isEmptyOrNull(localidad.getMinorKeep())) {
			                            if (ToolsHTML.isNumeric(localidad.getMinorKeep())) {
			                                int numHist = Integer.parseInt(localidad.getMinorKeep().trim());
			                                if (numHist > 0) {
			                                    forma.setMinorVer(ToolsHTML.getInitValue(localidad.getMinorId(),true));
			                                } else {
			                                    forma.setMinorVer(ToolsHTML.getInitValue(localidad.getMinorId(),true));
			                                }
			                            }
			                        } else {
			                            forma.setMinorVer(ToolsHTML.getInitValue(localidad.getMinorId(),true));
			                        }
		                        }
		                    }
		                }

						//Calculo de prefijo
		                if (carpeta!=null) {
		                    if (carpeta.getHeredarPrefijo() == Constants.permission) {
		                        String prefix = handlerStruct.getParentPrefixinst(tree,idNodeDocument,0==typePrefix);
		                        if (0==typePrefix) {
		                            forma.setPrefix(carpeta.getPrefix()+prefix);
		                        } else {
		                            forma.setPrefix(prefix+carpeta.getPrefix());
		                        }
		                    } else {
		                        forma.setPrefix(carpeta.getPrefix());
		                    }
		                }

		                forma.setIdLocation(idLocation);
		                forma.setNodeActive(idNodeDocument);


					    //Se calcula correlativo
					    //Correlativo obligatorio segun propiedad de la localidad
		                String borradorCorrelativo="0";
		                if(localidad!=null){
		                	borradorCorrelativo = String.valueOf(localidad.getCheckborradorCorrelativo());
		                }

					    if (ToolsHTML.isEmptyOrNull(borradorCorrelativo) && ("1".equals(borradorCorrelativo))){
					    	if(ToolsHTML.isEmptyOrNull(bean.getNumero())){
								//ERROR
							}else{
				                if (!ToolsHTML.isEmptyOrNull(forma.getDocPublic())) {
				                }
							}

					    }else{
					    	if(ToolsHTML.isEmptyOrNull(bean.getNumero())){
								//Se calcula correlativo
							}
					    }

		                String numCorrelativo = null;
		                String numByLocation = String.valueOf(HandlerParameters.PARAMETROS.getNumDocLoc());
		                if (!ToolsHTML.isEmptyOrNull(numByLocation)) {
		                    numByLocation = numByLocation.trim();
		                }
		                //busca para ver si se repite este numero creado por el usuario, si se repite te manda un null en respuesta.
		                if (!ToolsHTML.isEmptyOrNull(forma.getNumber())) {
		                     //numCorrelativo = EditDocumentAction.chkNumCorrelativo(forma.getIdNode(),forma.getNumber(),numByLocation,tree,forma);
		                     numCorrelativo = EditDocumentAction.chkNumCorrelativo(forma.getIdNode(),forma.getNumber(),numByLocation,tree);
		                    //esta repetido el numero si es nullo numCorrelativo
		                    if (ToolsHTML.isEmptyOrNull(numCorrelativo)) {
		                        //El Número debe ser único para todo el Sistema
		                        if ("0".equalsIgnoreCase(numByLocation)) {
		                             forma.setNumCorrelativoExiste(forma.getNumber());
		                        } else  if (("1".equalsIgnoreCase(numByLocation)) ||(("2".equalsIgnoreCase(numByLocation)))) {
		                             forma.setNumCorrelativoExiste(forma.getPrefix()+forma.getNumber());
		                        }
		                     }
		                }
		                
		                
						/** ***** FIN VALIDACIONES ************ */

						/**
						 * *** SE INSERTA EL DOCUMENTO INVOCANDO EL MISMO METODO
						 * DEL MANEJADOR ***
						 */

						// Valores necesitados en HandlerStruct.upDocument()
						if (bean.getPublicado() == null)
							forma.setDateApproved(null);
						else
							forma.setDateApproved(bean.getPublicado());

						forma.setOwner(bean.getPropietario());
						forma.setComments(bean.getComentarios());
						forma.setMayorVer(bean.getVersionMayor());
						forma.setMinorVer(bean.getVersionMenor());
						forma.setNameFile(bean.getNombreFisico());
						forma.setDateCreation(bean.getCreado());

						// En el metodo updateDocument() se calcula NumberGen y
						// NumVer

						// Valores necesitados en
						// HandlerStruct.getPreparedStatementDocumentInsert()
						if (bean.getStatus() != null && bean.getStatus().equals("B"))
							forma.setDocPublic("1");
						else
							forma.setDocPublic("0");
						// forma.setPrefix(""); //Ya se colocó el valor
						forma.setNormISO(bean.getNorma());
						// forma.setDocProtected("");
						// forma.setDocOnline("");
						forma.setURL(bean.getUrl());
						forma.setKeys(bean.getPalabrasClaves());
						forma.setDescript(bean.getDescripcion());
						forma.setNameDocument(bean.getNombre());
						forma.setNumber(bean.getNumero());
						forma.setTypeDocument(bean.getTipoDoc());
						if (bean.getStatus() != null && bean.getStatus().equals("A")) {
							forma.setDatePublic(dateSystem);
						}
						// forma.setIdNode(""); //Ya se colocó el valor
						if (bean.getStatus() != null && bean.getStatus().equals("A"))
							forma.setApproved("1");
						else
							forma.setApproved(null);
						// Tambien se necesita comments, owner, nameFile pero ya
						// fueron seteados

						// Valores necesitados en
						// HandlerStruct.getPreparementStatementVersionInsert()
						if (esPublico.equals("0") && bean.getStatus() != null && bean.getStatus().equals("A"))
							forma.setDateExpires(bean.getVencimiento());

						forma.setDateExpiresDrafts(null);

						if (bean.getVencimiento() != null && bean.getStatus() != null && bean.getStatus().equals("A"))
							forma.setExpires("0");
						// Tambien se necesita dateApproved, mayorVer, minorVer
						// pero ya fueron seteados

						// Valores necesitado en
						// EditDocumentAction.actualizarActiveFactory()
						// Inactivo activeFcatory
						if (bean.getStatus() != null && bean.getStatus().equals("A"))
							forma.setCheckactiveFactory("0");
						// forma.setDescripcionActiveFactory("");
						// forma.setIdDocument("");
						// Tambien se necesita descript, nameDocument, prefix,
						// number,nameFile pero ya fueron seteados

						forma.setDocRelations("");

						File file = new File(path + File.separator + forma.getNameFile());
						if(!file.exists()) {
							mal.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) + "| El archivo no existe \""+file.getAbsolutePath()+"\"");
							mal.flush();
							continue principal;
						}

						
						// campos adicionales
						try {
							StringBuffer campo = new StringBuffer();
							for(int x=1; x<=100; x++) {
								campo.setLength(0);
								campo.append("D").append(x);
								forma.set(campo.toString(),(String)bean.get(campo.toString()));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						// VALIDACION DE NO DUPLICACION SEGUN CAMPOS ADICIONALES
						if(claves.containsKey(forma.getTypeDocument())) {
							String[] campos = claves.get(forma.getTypeDocument()).split(",");
							StringBuffer condic = new StringBuffer();
							condic.append("SELECT numgen FROM documents WHERE active='1' AND ");
							String sep = "";
							for(int i=0; i<campos.length; i++) {
								condic.append(sep).append(campos[i]);
								condic.append("='").append(forma.get(campos[i].toUpperCase())).append("'");
								sep = " AND ";
							}
							// buscamos en los documentos esta clave
							PreparedStatement pst = con.prepareStatement(JDBCUtil.replaceCastMysql(condic.toString()));
							ResultSet rs = pst.executeQuery();
							if(rs.next()) {
								mal.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) + "| El documento está registrado segun los valores claves");
								mal.flush();
								continue principal;
							}
						}
						// FIN VALIDACION DE NO DUPLICACION SEGUN CAMPOS ADICIONALES
						
						
						// HandlerStruct.upDocument(forma, path, request,
						// false);
						
						
						
						if(!isValidacion()) {
							try {
								procesaFicheros(con, forma, path);
							} catch (Exception e) {
								mal.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) + "| Error al tratar de subir documento: " + e);
								mal.flush();
							}
						}

						/** **** FIN ****** */

						ok.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) + "|" + bean.getNumero());
						log.println(format.format(new Date()) + " Resultado: OK");

						log.flush();
						ok.flush();
						mal.flush();

					} catch (Exception errGen) {
						mal.println(format.format(new Date()) + "|" + (numeroFilaActual + addFila) + "| Error no controlado " + errGen.getMessage());
						mal.flush();
						errGen.printStackTrace();
					}

					filaActual = "";

				} // FIN DE CICLO LEYENDO ARCHIVO

			} // END if(bf!=null)
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ok.println(format.format(new Date()) + "|FIN DEL PROCESO");
			mal.println(format.format(new Date()) + "|FIN DEL PROCESO");
			log.println(format.format(new Date()) + "|FIN DEL PROCESO");

			log.flush();
			ok.flush();
			mal.flush();

			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	private String obtenerCorrelativo(Connection con, String idNode, String idLocation, Hashtable tree) throws Exception {
		String correlativo = "1";
		String numByLocation = "0";
		String resetDocNumber = "1";
		
		numByLocation = String.valueOf(HandlerParameters.PARAMETROS.getNumDocLoc());
		resetDocNumber = HandlerParameters.PARAMETROS.getResetDocNumber();

		String number = HandlerParameters.getLengthNumberDocuments(con);
		StringBuffer numero = new StringBuffer("");
		// se genera el número del documento
		if (number != null) {
			String numberDoc = EditDocumentAction.numCorrelativo(con, idNode, idLocation, numByLocation, resetDocNumber, tree);
			for (int cont = numberDoc.length(); cont < Integer.parseInt(number); cont++) {
				numero.append("0");
			}
			numero.append(numberDoc);
		}
		correlativo = numero.toString();
		return correlativo;
	}

	private String obtenerIdTipoDocumento(Connection con, String tipo) throws Exception {
		
		String tipoDoc = "";
		if(listaTipoDocumento==null) {
			PreparedStatement st = null;
			ResultSet rs = null;
			listaTipoDocumento = new HashMap();
	
			StringBuffer sql = new StringBuffer("SELECT idTypeDoc,TypeDoc FROM typedocuments ");
			try {
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
				rs = st.executeQuery();
				while (rs.next()) {
					listaTipoDocumento.put(rs.getString("TypeDoc"),rs.getString("idTypeDoc"));
				}
			} catch (Exception e) {
				//System.out.println("Error en MigracionExcel.obtenerIdTipoDocumento: " + e);
				return tipoDoc;
			}
		}
		if(listaTipoDocumento.containsKey(tipo)) {
			tipoDoc = String.valueOf(listaTipoDocumento.get(tipo));
		}
		return tipoDoc;
	}

	// TODO Verificar funcionamiento de Nomas ISO
	private String obtenerIdNormaIso(String normaIso) throws Exception {
		String idNorm = "";
		if(listaNormaIso==null) {
			Connection con = null;
			PreparedStatement st = null;
			ResultSet rs = null;
			listaNormaIso = new HashMap();
	
			StringBuffer sql = new StringBuffer("SELECT idNorm,titleNorm FROM norms ");
			try {
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
				rs = st.executeQuery();
				while (rs.next()) {
					listaNormaIso.put(rs.getString("titleNorm"), rs.getString("idNorm"));
				}
			} catch (Exception e) {
				//System.out.println("Error en MigracionExcel.obtenerIdNormaIso: " + e);
				return idNorm;
			} finally {
				if (con != null) {
					con.close();
				}
				if (st != null) {
					st.close();
				}
				if (rs != null) {
					rs.close();
				}
			}
		}
		if(listaNormaIso.containsKey(normaIso)) {
			idNorm = (String)listaNormaIso.get(normaIso);
		} else {
			//buscaremos 
			Iterator ite = listaNormaIso.keySet().iterator();
			String valor = null;
			while(ite.hasNext()) {
				valor = (String)ite.next();
				if(normaIso.toLowerCase().indexOf(valor.toLowerCase())!=-1) {
					idNorm = (String)listaNormaIso.get(valor);
				}
			}
		}
		return idNorm;
	}

	private int[] buscarIdNode(int idNodoPadre, String nombre) throws Exception {
		return buscarIdNode(null, idNodoPadre, nombre);
	}

	private int[] buscarIdNode(Connection con, int idNodoPadre, String nombre) throws Exception {
		PreparedStatement st = null;
		ResultSet rs = null;

		StringBuffer sql = new StringBuffer("SELECT IdNode, nodetype FROM struct WHERE (Name = ");
		sql.append(" '").append(nombre).append("' ) AND (IdNodeParent = ");
		sql.append(" '").append(idNodoPadre).append("') ");
		int[] idNodo = {0, -1};
		
		boolean cerrar = false;
		try {
			if (con == null || con.isClosed()) {
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				cerrar = true;
			}
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			if (rs.next()) {
				idNodo[0] = Integer.parseInt(rs.getString("IdNode").trim());
				idNodo[1] = Integer.parseInt(rs.getString("nodetype").trim());
			}
		} catch (Exception e) {
			//System.out.println("Error en MigracionExcel.buscarIdNode: " + e);
			return idNodo;
		} finally {
			if (con != null) {
				if (cerrar) {
					con.close();
				}
			}
			if (st != null) {
				st.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return idNodo;
	}

	private int crearNode(Connection con, int idNodoPadre, String nombre) throws Exception {
		PreparedStatement st = null;
		String idNodo = "0";
		ResultSet rs = null;

        BaseStructForm frmCarpetaPrt = getNode(con, idNodoPadre, nombre);
        if(frmCarpetaPrt!=null) {
	        frmCarpetaPrt.setIdNode(idNodo);
	        frmCarpetaPrt.setNodeType(DesigeConf.getProperty("folderType"));
	        frmCarpetaPrt.setIdNodeParent(String.valueOf(idNodoPadre));
	        frmCarpetaPrt.setDescription("");
	        frmCarpetaPrt.setName(nombre);
	        frmCarpetaPrt.setNameIcon("menu_folder_closed.gif");
	        frmCarpetaPrt.setPrefix("");
	        
			HandlerStruct.insert(con, frmCarpetaPrt);

			idNodo = frmCarpetaPrt.getIdNode();
        }
		return Integer.parseInt(idNodo);
	}

	private BaseStructForm getNode(Connection con, int idNodoPadre, String nombre) throws Exception {
		PreparedStatement st = null;
		ResultSet rs = null;
		BaseStructForm node = new BaseStructForm();

		StringBuffer sql = new StringBuffer("SELECT * FROM struct WHERE IdNodeParent = '").append(idNodoPadre).append("' ");

		try {
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			if (rs.next()) {
				node.setIdNode(rs.getString("IdNode").trim());
				node.setName(rs.getString("name").trim());
				node.setIdNodeParent(rs.getString("IdNodeParent").trim());
				node.setNameIcon(rs.getString("nameIcon").trim());
				node.setNodeType(rs.getString("nodeType").trim());
				node.setDateCreation(rs.getString("dateCreation").trim());
				node.setOwner(rs.getString("owner").trim());
				node.setDescription(rs.getString("description").trim());
				node.setToImpresion(rs.getInt("toImpresion"));
				node.setHeredarPrefijo(rs.getByte("heredarPrefijo"));
			}
		} catch (Exception e) {
			return null;
		}
		return node;
	}
	
	public synchronized boolean procesaFicheros(BaseDocumentForm forma, String path) {
		return procesaFicheros(null, forma, path);
	}

	public synchronized boolean procesaFicheros(Connection con, BaseDocumentForm forma, String path) {
		boolean resp = false;
		try {
			if (forma != null) {
				//System.out.println("----- procesaFicheros forma.getNumber(): " + forma.getNumber());
				// CARGA DE ESTRUCTURA
				// Para llenar el tree, colocamos el usuario como
				// administrador,el grupo como administrador, la seguridad nula
				// para que carque toda la estructura
				Hashtable subNodos = new Hashtable();
				Hashtable tree = HandlerStruct.loadAllNodes(con, null, DesigeConf.getProperty("application.admon"), DesigeConf.getProperty("application.admon"), subNodos, null);
				// String idNodeDocument = forma.getIdNode();
				// String idLocation = HandlerStruct.getIdLocationToNode(tree,
				// idNodeDocument);
				// BaseStructForm location =
				// HandlerStruct.loadStruct(idLocation);
				// BaseStructForm location = (BaseStructForm)
				// tree.get(idLocation);
				// Fin de la Carga de la Estructura

				String numCorrelativo = null;
				// en caso que sea publico, validamos el num correlativo
				// ----o------------//
				String numByLocation = String.valueOf(HandlerParameters.PARAMETROS.getNumDocLoc());
				
				// if (!ToolsHTML.isEmptyOrNull(numByLocation)) {
				// numByLocation = numByLocation.trim();
				// }
				// num correlativo existe es una variable que trae el ;refijo,
				// luego mmas adelante se le agrega el numero
				String iqualPrefijo = forma.getNumCorrelativoExiste();
				// o en caso que sea borrador y las propiedades de la localidad
				// se
				// le exige colocar numero a un borrador, este entonces lo trae
				// en la variable forma.getNumber()
				if ((!ToolsHTML.isEmptyOrNull(forma.getNumber())) || (forma.getDocPublic().equalsIgnoreCase("0"))) {
					forma.setNumCorrelativoExiste(null);
					if (!ToolsHTML.isEmptyOrNull(forma.getNumber())) {
						// obtenemos el prefijo
						String prefix = forma.getPrefix();
						// busca para ver si se repite este numero creado por el
						// usuario, si se repite te manda un null en respuesta.
						// numCorrelativo =
						// EditDocumentAction.numCorrelativo(forma.getIdNode(),forma.getIdLocation(),forma.getNumber(),forma);
						numCorrelativo = EditDocumentAction.chkNumCorrelativo(con, forma.getIdNode(), forma.getNumber(), numByLocation, tree);
						if (ToolsHTML.isEmptyOrNull(numCorrelativo)) {
							// el numero introducido por el usuario se repite y
							// se le manda un mensaje que este num ya esta en
							// uso
							// El Número debe ser único para todo el Sistema
							if ("0".equalsIgnoreCase(numByLocation)) {
								forma.setNumCorrelativoExiste(forma.getNumber());
							} else {
								forma.setNumCorrelativoExiste(prefix + forma.getNumber());
							}
						}
					}
				}
				// Ingresamos el TypeNumByLocation, de tal manera que en
				// administracion ya quede predefinido el tipo de
				// numcorrelativo y este inhabiliatdo
				// aqui se ve si ya escojieron el tipo de numero correlativo
				// String numByLocation =
				// HandlerParameters.PARAMETROS.getNumByLocation();

				if (Constants.typeNumByLocationVacio.compareTo(String.valueOf(HandlerParameters.PARAMETROS.getTypeNumByLocation()).trim()) == 0 || (ToolsHTML.isEmptyOrNull(numByLocation))) {
					HandlerParameters.actualizaParametro(con, Constants.typeNumByLocationLLeno);
				}

				// pasa si el usuario coloco un numero que no este repetido o no
				// haya colocado un numero correlativo
				boolean swmismoprefijo = false;
				if (!ToolsHTML.isEmptyOrNull(forma.getNumCorrelativoExiste())) {
					swmismoprefijo = forma.getNumCorrelativoExiste().equalsIgnoreCase(iqualPrefijo);
				}

				if ((swmismoprefijo) || ToolsHTML.isEmptyOrNull(forma.getNumCorrelativoExiste())) {
					// Se consigue el num correlativo en caso que el usuario no
					// haya colocado un numero correlativo
					// y en caso que sea publico
					if ((ToolsHTML.isEmptyOrNull(numCorrelativo)) && (forma.getDocPublic().equalsIgnoreCase("0"))) {
						numCorrelativo = EditDocumentAction.numCorrelativo(con, forma.getIdNode(), forma.getIdLocation(), numByLocation, HandlerParameters.PARAMETROS.getResetDocNumber(), tree);
					}
					if (ToolsHTML.isEmptyOrNull(numCorrelativo)) {
						forma.setNumber("");
					} else {
						forma.setNumber(numCorrelativo);
					}

					File file = new File(path + File.separator + forma.getNameFile());

					MimetypesFileTypeMap mime1 = new MimetypesFileTypeMap();
					// FileDataSource fileData = new FileDataSource(file);

					String extensionDoc = ShowDocument.getExtensionFile(forma.getNameFile());
					if (extensionDoc != null) {
						if (extensionDoc.equals(".DOC")) {
							forma.setContextType("application/msword");
						} else if (extensionDoc.equals(".XLS") || extensionDoc.equals("XLT")) {
							forma.setContextType("application/msexcel");
						} else if (extensionDoc.equals(".PPT")) {
							forma.setContextType("application/vnd.ms-powerpoint");
						} else if (extensionDoc.equals(".VSD")) {
							forma.setContextType("application/vnd.ms-visio.viewer");
						} else if (extensionDoc.equals(".PDF")) {
							forma.setContextType("application/pdf");
						} else if (extensionDoc.equals(".RTF")) {
							forma.setContextType("text/rtf");
						} else if (extensionDoc.equals(".CDR")) {
							forma.setContextType("application/coreldraw");
						} else if (extensionDoc.equals(".TIF")) {
							forma.setContextType("image/tiff");
						} else {
							forma.setContextType(mime1.getContentType(file));
							// forma.setContextType(fileData.getContentType());
						}
					} else {
						forma.setContextType(mime1.getContentType(file));
					}
					//System.out.println("------- extensionDoc: " + extensionDoc);
					//System.out.println("------- forma.getContextType(): " + forma.getContextType());
					try {
						resp = HandlerStruct.upDocument(con, forma, path, null, true, forma.getDateCreation(), false, "admin", false);
						// Si no dio error y el documento es publico se modifica
						// fecha de publicacion
						// Se obtiene ultimo documento activo que se haya
						// ingresado
						// Si el nombre coincide, se modifica fecha de
						// publicacion
						if (forma != null && forma.getDatePublic() != null) {
							BaseDocumentForm lastDocument = getLastDocument(con);
							if (lastDocument != null && lastDocument.getNameDocument() != null && lastDocument.getNameDocument().equals(forma.getNameDocument()) && forma.getNameFile().equals(lastDocument.getNameFile())) {
								updateDatePublic(con, lastDocument.getNumberGen(), forma.getDatePublic());
							}
						}

					} catch (Exception e) {
						//System.out.println("--- error al tratar de subir el archivo = " + e.getMessage());
					}

				} else {
					errorProceso = "Número correlativo existe";
					resp = false;
				}
			}
		} catch (Exception e) {
			//System.out.println("e.getMessage() = " + e.getMessage());
			e.printStackTrace();
			errorProceso = e.getMessage();
			resp = false;
		}
		return resp;
	}

	public static synchronized BaseDocumentForm getLastDocument() throws Exception {
		return getLastDocument(null);
	}

	public static synchronized BaseDocumentForm getLastDocument(Connection con) throws Exception {
		StringBuffer sql = new StringBuffer("SELECT * from documents where active = '1' and numGen = (select max(numGen) from documents) ");
		BaseDocumentForm forma = new BaseDocumentForm();
		try {
			Properties prop = JDBCUtil.doQueryOneRow(con, sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				forma.setNumberGen(prop.getProperty("numGen"));
				forma.setNameDocument(prop.getProperty("nameDocument"));
				forma.setNameFile(prop.getProperty("nameFile"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return forma;
	}

	public static void updateDatePublic(String numGen, String datePublic) throws Exception {
		updateDatePublic(null, numGen, datePublic);
	}

	public static void updateDatePublic(Connection con, String numGen, String datePublic) throws Exception {
		boolean cerrar = false;
		PreparedStatement st = null;
		try {
			if (ToolsHTML.isNumeric(numGen) && !ToolsHTML.isEmptyOrNull(datePublic) && datePublic.length() > 9) {
				if (con == null || con.isClosed()) {
					con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
					cerrar = true;
				}
				StringBuffer sql = new StringBuffer("");
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					sql.append("Update documents set datePublic=CONVERT(datetime,'").append(datePublic).append("',120) where numGen = ").append(numGen);
					break;
				case Constants.MANEJADOR_POSTGRES:
					sql.append("Update documents set datePublic=CAST('").append(datePublic).append("' AS timestamp) where numGen = ").append(numGen);
					break;
				case Constants.MANEJADOR_MYSQL:
					sql.append("Update documents set datePublic=TIMESTAMP('").append(datePublic).append("') where numGen = ").append(numGen);
					break;
				}
				// //System.out.println("update : " + sql.toString());
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
				st.executeUpdate();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (cerrar) {
				JDBCUtil.closeConnection(st, con, "updateDatePublic()");
			}
		}
	}

	public boolean isValidacion() {
		return validacion;
	}

	public void setValidacion(boolean validacion) {
		this.validacion = validacion;
	}
	
	

}
