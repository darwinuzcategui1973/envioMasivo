package com.desige.webDocuments.files.facade;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;

import com.desige.webDocuments.bean.ExpedienteRequest;
import com.desige.webDocuments.dao.ConfExpedienteDAO;
import com.desige.webDocuments.dao.ExpedienteDAO;
import com.desige.webDocuments.dao.ExpedienteDetalleDAO;
import com.desige.webDocuments.dao.ExpedienteHistoryDAO;
import com.desige.webDocuments.dao.ExpedienteVersionDAO;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.files.actions.PrepareFilesToVisorAction;
import com.desige.webDocuments.files.forms.ExpedienteDetalleForm;
import com.desige.webDocuments.files.forms.ExpedienteForm;
import com.desige.webDocuments.files.forms.FilesForm;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HtmlToPdfConvert;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DocumentsNotFoundException;
import com.desige.webDocuments.utils.FilesDocumentNotValidException;
import com.desige.webDocuments.utils.FilesNotFoundInDiskException;
import com.desige.webDocuments.utils.StringUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qwds.ondemand.server.excepciones.ErrorDeAplicaqcion;
import com.focus.util.Archivo;
import com.focus.util.ConcatPDFFile;

import sun.jdbc.rowset.CachedRowSet;

public class FilesFacade {

	public static final String FILES_NAME_DIR = "expedientes";

	private static Logger log = LoggerFactory.getLogger(PrepareFilesToVisorAction.class);
	private HttpServletRequest request;

	public FilesFacade() {
	}

	public FilesFacade(HttpServletRequest request) {
		this.request = request;
	}
	
	public static String getFilesPath() {
		String result = "";
		try {
			result = new StringBuilder(2048).append(ToolsHTML.getRepository()).append(File.separator).append(FILES_NAME_DIR).toString();
			
			File file = new File(result);
			if(!file.exists()) {
				file.mkdir();
			}
			file = null;
		} catch (ErrorDeAplicaqcion e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @param servlet Usado cuando se esta regenerando el archivo desde el servlet
	 * @param form
	 * @param action
	 * @param isReader
	 * @param doQuerys Usado cuando se esta regenerando el archivo desde el servlet
	 * @throws Exception
	 */
	public void storeStructure(Servlet servlet, ActionForm form, SuperAction action, 
			boolean isReader, boolean doQuerys) throws Exception {
		String fileNameIn = "filesRegisterBase.jsp";
		String fileNameOut = "filesRegister.jsp";
		if(isReader) {
			fileNameIn = "filesViewBase.jsp";
			fileNameOut = "filesView.jsp";
		}

		StringBuffer html = new StringBuffer();
		ArrayList lista;
		FilesForm campo;
		boolean isName = true;
		StringBuffer cad = new StringBuffer();
		StringBuffer finalJavaScript = new StringBuffer();
		StringBuffer condicion = new StringBuffer();
		String nameBefore = "";
		String idBefore = "";
		boolean isId = false;
		boolean isOwner = false;
		boolean isFecha = false;
		boolean isNumber = false;
		boolean isDelete = false;
		FilesForm files = (FilesForm) form;

		// variables para el query
		StringBuffer query = new StringBuffer();
		StringBuffer query1 = new StringBuffer();
		StringBuffer query2 = new StringBuffer();
		StringBuffer query3 = new StringBuffer();
		StringBuffer query4 = new StringBuffer();

		ConfExpedienteDAO oConfExpedienteDAO = new ConfExpedienteDAO();
		if(files != null){
			if(request != null && request.getParameter("eliminar")!=null && String.valueOf(request.getParameter("eliminar")).equals("true")) {
				oConfExpedienteDAO.delete(files);
				isDelete=true;
			} else {
				oConfExpedienteDAO.save(files);
			}
		}
		
		// crear el html para la pagina de registro
		lista = (ArrayList) oConfExpedienteDAO.findAllByOrder();

		// creamos un arreglo ordenado
		TreeMap<String,String> mapa = new TreeMap<String,String>();
		for (int i = 0; i < lista.size(); i++) {
			campo = (FilesForm) lista.get(i);
			mapa.put(campo.getId(), campo.getEtiqueta02());
		}

		html.append("  <table cellspacing='2' cellpadding='").append(isReader?3:0).append("' border='0' class='texto' width='100%'>\n");
		for (int i = 0; i < lista.size(); i++) {
			cad.setLength(0);
			campo = (FilesForm) lista.get(i);
			if (campo.getVisible() == 1) {
				isName = !campo.getEtiqueta02().equals(nameBefore);
				isId = campo.getId().equals("f1");
				isOwner = campo.getId().equals("f3");
				isFecha = campo.getTipo() == 3;
				isNumber = campo.getTipo()==2;
				String validDate = Constants.VALID_DATE_JAVASCRIPT;

				// condicion
				if(!campo.getCondicion().trim().equals("")) {
					String[] requeridos = campo.getCondicion().trim().split("-");
					condicion.append(StringUtil.replace("if(!isEmpty(document.forms[0].P1)) {\n",new String[]{campo.getId()}));
					for(int k=0;k<requeridos.length;k++){
						condicion.append(StringUtil.replace("				  if(!isValido(document.forms[0].P1,'P2')) return false;\n",new String[]{requeridos[k],mapa.get(requeridos[k])}));
					}
					condicion.append("				}\n");
				}

				if (isName) {
					html.append("    <tr>\n");
					html.append("      <td class='titleLeft' width='200' style='background: url(img/btn160.gif);' >\n");
					html.append("        ");
					html.append("<%=usuario.getLanguage().equals(\"es\")?\"").append(campo.getEtiqueta02()).append("\":\"").append(campo.getEtiqueta01()).append("\"%>");
					html.append(":\n");
					html.append("      </td>\n");
					html.append("      <td>\n");
				}

				if (isOwner && !isReader) {
					HandlerDBUser.getAllUsers();
					String[] valores = campo.getValores().split(",");
					cad.append("        <html:select name='files' property='A1' >\n".replaceAll("A1", campo.getId()));
					//cad.append("            <html:option value='0'>&nbsp;</html:option>");
					cad.append("            <logic:present name='usuarios'>");
					cad.append("                    <html:optionsCollection name='usuarios' value='id' label='descript' />");
					cad.append("            </logic:present>");
					cad.append("        \n</html:select>\n");
				} else if (campo.getEntrada().equals("text") || isReader) {
					if (isFecha) {
						cad.append("        <table cellspacing='0' cellpadding='0' border='0'>\n");
						cad.append("          <tr><td>\n");
					}
					//cad.append(StringUtil.replace("        <input type='text' name='P1' maxlength='P2' size='P3' P4 P5 />\n",new String[]{campo.getId(),String.valueOf(campo.getLongitud()), String.valueOf(campo.getLongitud() < 70 ? campo.getLongitud() : 70), isFecha || isId ? "readonly='true'" : "", isNumber?"onkeyup='format(this)' onchange='format(this)'":""}));
					if(isReader) {
						cad.append(StringUtil.replace("        <span class='texto'><bean:write name='files' property='P1' /></span>\n",new String[]{campo.getId().equals("f2")?"fecha":(campo.getId().equals("f3")?"nameUser":campo.getId())}));
					} else {
						cad.append(StringUtil.replace("        <html:text name='files' property='P1' maxlength='P2' size='P3' P4 P5 P6 />\n",new String[]{campo.getId().equals("f2")?"fecha":campo.getId(),String.valueOf(campo.getLongitud()), String.valueOf(campo.getLongitud() < 70 ? campo.getLongitud() : 70), isId || campo.getEditable()==0 ? "readonly='true'" : "", isNumber?"onkeyup='format(this)' onchange='format(this)'":"", isFecha?validDate:"" }));
					}
					if (isFecha) {
						cad.append("          </td><td>\n");
					}
				} else if (campo.getEntrada().equals("radio")) {
					String[] valores = campo.getValores().split(",");
					String valor;
					for (int x = 0; x < valores.length; x++) {
						valor = valores[x];
						if(valores[x].indexOf("(")!=-1){
							valor = valor.substring(0,valores[x].indexOf("("));
							String[] cond =  valores[x].substring(valores[x].indexOf("(")).replaceAll("[()]","").split("-");
							for(int k=0; k<cond.length; k++) {
								condicion.append(StringUtil.replace("				if(!isItemValid(document.forms[0].P1,'P2',document.forms[0].P3,'P4')) return false;\n",new String[]{campo.getId(),valor,cond[k],mapa.get(cond[k])}));
								//isItemValid(document.forms[0].f4,'Marca Comercial(f5)',document.forms[0].f5,'Propietario');
							}
						}
						cad.append(StringUtil.replace("        <html:radio name='files' property='P1' value='P2' />P2&nbsp;&nbsp;\n",new String[]{campo.getId(),valor}));
					}
				} else if (campo.getEntrada().equals("checkbox")) {
					if(campo.getValores().indexOf(",")==-1) {
						cad.append(StringUtil.replace("        <html:checkbox name='files' property='P1' value='P2' />P2&nbsp;&nbsp;\n<!--otroP1-->",new String[]{campo.getId(),campo.getValores()}));
					} else {
						String[] valores = campo.getValores().split(",");
						finalJavaScript.append(StringUtil.replace("marcar('P1');\n",new String[]{campo.getId()}));
						cad.append(StringUtil.replace("        <html:hidden name='files' property='P1' />\n",new String[]{campo.getId()}));
						for(int x=0;x<valores.length;x++) {
							cad.append(StringUtil.replace("        <input type='checkbox' name='P1_' value='P2' onclick=\"llenar('P3')\" />P4&nbsp;\n",new String[]{campo.getId(),valores[x],campo.getId(),valores[x]}));
						}
					}
				} else if (campo.getEntrada().equals("select")) {
					String[] valores = campo.getValores().split(",");
					cad.append("        <html:select name='files' property='A1' >\n".replaceAll("A1", campo.getId()));
					for (int x = 0; x < valores.length; x++) {
						cad.append("          <html:option value='A1'>A1</html:option>\n".replaceAll("A1", valores[x]));
					}
					cad.append("        </html:select>\n");
				} else if (campo.getEntrada().equals("textarea")) {
					cad.append(StringUtil.replace("        <html:textarea name='files' property='P1' rows='5' cols='50'></html:textarea>\n",new String[]{campo.getId()}));
				}

				if (isFecha) {
					if (!isReader && !campo.getId().equals("f2")) {
						cad.append(" <a href=\"javascript:show_calendar('forms[0].A1', 'forms[0].A1', 'forms[0].A1');\"".replaceAll("A1", campo.getId()));
						cad.append(" onmouseover=\"window.status='Seleccione la fecha';return true;\"");
						cad.append(" onmouseout=\"window.status='';return true;\">");
						cad.append(" <img src=\"images/calendario.gif\" border=\"0\" title=\"Seleccionar Fecha\"></a>");
					} else {
						//html.append("<html:hidden name='files' property='A1' />".replaceAll("A1", campo.getId()));
						cad.append("&nbsp;");
					}
					cad.append("          </td></tr></table>\n");
				}

				if (isName) {
					html.append(cad);
					html.append("      </td>\n");
					html.append("    </tr>\n");
				} else {
					html.replace(0, html.length(), html.toString().replaceAll("<!--otro".concat(idBefore).concat("-->"), cad.toString()));
					// htmlCopy.setLength(0);
					// htmlCopy.append(html.toString());
					// html.setLength(0);
					// html.append(htmlCopy.toString());
					// html.append(htmlCopy.toString().replaceAll("<!--otro-->",
					// cad.toString()));

				}
				idBefore = campo.getId();
				nameBefore = campo.getEtiqueta02();
			} else {
				html.append("<html:hidden name='files' property='A1' />".replaceAll("A1",
						("f2".equals(campo.getId()) ? "fecha" : campo.getId())));
			}
		}
		html.append("  </table>\n");

		// leemos el archivo base y escribimos el archivo jsp final
		if(action != null){
			fileNameIn = ToolsHTML.getPath().concat(fileNameIn);
			fileNameOut = ToolsHTML.getPath().concat(fileNameOut);
		} else {
			fileNameIn = ToolsHTML.getPath().concat(fileNameIn);
			fileNameOut = ToolsHTML.getPath().concat(fileNameOut);
		}
		
		Archivo archivo = new Archivo();
		String contenido = archivo.leer(fileNameIn).toString();
		contenido = contenido.replaceAll("<!--validacion-->", condicion.toString());
		contenido = contenido.replaceAll("<!--codigo-->", html.toString());
		contenido = contenido.replaceAll("<!--finalCodeJS-->", finalJavaScript.toString());
		archivo.escribir(fileNameOut, contenido);

		if(isReader && doQuerys) {
			// Modificamos la tabla expediente
			boolean isColumnNew=true;
			if(!files.getId().equals("f1") && !files.getId().equals("f2")) {
				query.append(JDBCUtil.describeTable("expediente"));

				CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName()); // obtenemos la estructura de la tabla
				while(crs.next()){
					//System.out.println(crs.getString("COLUMN_NAME"));
					if((crs.getString("COLUMN_NAME").equals(files.getId()))){
						isColumnNew=false;
						break;
					}
				}

				query.setLength(0);
				query1.setLength(0);
				query2.setLength(0);
				query3.setLength(0);
				query4.setLength(0);
				if(!isDelete) {
					if(isColumnNew){
						query.append(JDBCUtil.alterTableAdd("expediente",files.getId(),String.valueOf(files.getLongitud())));
						query1.append(JDBCUtil.alterTableDrop("expediente_history",files.getId()));
						query2.append(JDBCUtil.alterTableAdd("expediente_history",files.getId(),String.valueOf(files.getLongitud())));
						query4.append(JDBCUtil.alterTableAddUnique("expediente",files.getId()));
					} else {
						query.append(JDBCUtil.alterTableModify("expediente",files.getId(),String.valueOf(files.getLongitud())));
						query2.append(JDBCUtil.alterTableModify("expediente_history",files.getId(),String.valueOf(files.getLongitud())));
						query3.append(JDBCUtil.alterTableDropUnique("expediente",files.getId()));
						query4.append(JDBCUtil.alterTableAddUnique("expediente",files.getId()));
					}
					JDBCUtil.executeUpdate(query);
					try {
						if(query1.length()>0) {
							JDBCUtil.executeUpdate(query1);
						}
					}catch(SQLException e){
						//No especificamos el error;
					}
					JDBCUtil.executeUpdate(query2);
					try{
						if(query3.length()>0) {
							JDBCUtil.executeUpdate(query3);
						}
					}catch(Exception e){
						// antes de crearlo lo eliminamos si existe;
					}
					if(files.getAuditable()==1) {
						try{
							JDBCUtil.executeUpdate(query4);
						}catch(Exception e){
							files.setAuditable(0);
							oConfExpedienteDAO.updateAuditable(files);
							throw new ApplicationExceptionChecked("E0123");
						}
					}
				}
			}
		}
	}

	/**
	 *
	 * @param form
	 * @throws Exception
	 * Almacena la ficha de expedientes en la tabla correspondiente
	 */
	public void storeFiles(ActionForm form,Users usuario) throws Exception {
		ExpedienteDAO oExpedienteDAO = new ExpedienteDAO();

		ExpedienteForm files = (ExpedienteForm)form;

		oExpedienteDAO.save(files,usuario);
	}



	public Collection findAllFiles() throws Exception{
		ExpedienteDAO oExpedienteDAO = new ExpedienteDAO();

		ArrayList lista = (ArrayList)oExpedienteDAO.findAll();

		return lista;
	}

	public Collection findAllFiles(ExpedienteForm files, ArrayList conf, HttpServletRequest request) throws Exception{
		ExpedienteDAO oExpedienteDAO = new ExpedienteDAO();

		ArrayList lista = (ArrayList)oExpedienteDAO.findAll(files, conf, request);

		return lista;
	}

	public Collection findAllFilesByUser(ExpedienteForm files, ArrayList conf, HttpServletRequest request, Users usuario) throws Exception{
		ExpedienteDAO oExpedienteDAO = new ExpedienteDAO();

		ArrayList lista = (ArrayList)oExpedienteDAO.findAll(files, conf, request, usuario);

		return lista;
	}

	// updateHistoryFiles(con,idDocument,0,0,owner,time,checkOut,null);
	public static void updateHistoryFiles(Connection con, int idDoc, String nameUser, Timestamp dateChange, String type, String comments, String[] datosVersion) throws Exception {
		dateChange = (dateChange==null?new Timestamp(new java.util.Date().getTime()):dateChange);

		// int idHistory = IDDBFactorySql.getNextID("historydocs");
		int idHistory = HandlerStruct.proximo(con,"historyfiles", "historyfiles", "idHistory");
		
		PreparedStatement st = null;
		StringBuffer insert = new StringBuffer(100);

		if (datosVersion != null) {

//			idHistory
//			idNode
//			nameUser
//			dateChange
//			type
//			comments
//			MayorVer
//			MinorVer


			insert.append("INSERT INTO historyfiles (idHistory,idNode,nameUser,");
			insert.append("dateChange,");
			insert.append("type,comments,MayorVer,MinorVer)");
			insert.append(" VALUES(?,?,?,?,?,?,?,?)");
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(insert.toString()));
			int pos=1;
			st.setInt(pos++, idHistory);
			st.setInt(pos++, idDoc);
			st.setString(pos++, nameUser);
			st.setTimestamp(pos++, dateChange);
			st.setString(pos++, type);
			st.setString(pos++, comments);
			st.setString(pos++, datosVersion[0]);
			st.setString(pos++, datosVersion[1]);
			st.executeUpdate();
		} else {
			throw new ApplicationExceptionChecked("0056");
		}
	}

	public ExpedienteDetalleForm saveVersion(ExpedienteForm files, Users usuario) throws FilesDocumentNotValidException {
		ExpedienteDetalleDAO oExpedienteDetalleDAO = new ExpedienteDetalleDAO();

		return oExpedienteDetalleDAO.saveVersion(files, usuario);
	}


	/**
	 * 
	 * @param data
	 * @param datos
	 * @param file
	 * @param dataDetalle
	 * @param bloque
	 * @param etiquetas
	 * @return
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public String cargarExpediente(String data, Collection datos, ExpedienteForm file, String dataDetalle, String bloque, 
			String[] etiquetas) throws IllegalArgumentException, SecurityException, 
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		StringBuffer firmantes = new StringBuffer();
		String detalle = null;
		FilesForm campo = null;
		if (datos != null) {
			Iterator<FilesForm> ite = datos.iterator();
			while (ite.hasNext()) {
				campo = ite.next();
				if("f1_f2".indexOf(campo.getId())==-1 && campo.getImprimir()==1) {
					detalle = dataDetalle;
					detalle = detalle.replaceAll(etiquetas[0], campo.getEtiqueta02() );
					detalle = detalle.replaceAll(etiquetas[1], String.valueOf(file.get(campo.getId().toUpperCase())));
					firmantes.append(detalle);
				}
			}
			data = data.replaceAll(bloque, firmantes.toString());
		}
		return data;
	}
	
	/**
	 * 
	 * @param request
	 * @param oExpedienteForm
	 * @param namePerson
	 */
	private String createExpPortada(HttpServletRequest request, ExpedienteForm oExpedienteForm,
			Users user) throws Exception{
		String namePdfFinal = null;
		
		String path = ToolsHTML.getPath();
		String pathFileHtml = path.concat("expediente.html");
		String nameFileOutput = path.concat("tmp").concat(File.separator)
				.concat(user.getUser()).concat(File.separator).concat("expediente")
				.concat(Integer.toString(oExpedienteForm.getF1()))
				.concat(File.separator).concat("Portada")
				.concat(String.valueOf(oExpedienteForm.getF1())).concat(".html");
		log.info("Portada de expediente sera almacenada en: " + nameFileOutput);
		
		String servidor = request.getScheme() + "://" + ToolsHTML.getServerName(request) + ":" + request.getServerPort() + request.getContextPath() + "/";
		File fileHtml = new File(pathFileHtml);

		if (fileHtml.exists()) {
			// HandlerDocuments.isPrinting(usuario, forma);
			ResourceBundle rb = ToolsHTML.getBundle(request);

			Archivo arc = new Archivo();
			String data = arc.leer(pathFileHtml).toString();
			String dataDetalle = "";
			String dataRevisores = "";
			String dataCreador = "";

			// cambio de datos a las etiquetas
			data = data.replaceAll("---lblCODIGO---", rb.getString("doc.number"));
			data = data.replaceAll("---lblVERSION---", rb.getString("doc.Ver"));
			data = data.replaceAll("---lblFECHA_DE_CREACION---", rb.getString("doc.dateCreation"));
			data = data.replaceAll("---lblSOLICITADO---", rb.getString("imp.solicitante"));
			data = data.replaceAll("---lblIMPRESO---", rb.getString("imp.datePrint"));
			
			data = data.replaceAll("---lblINFORMATION---", rb.getString("files.information"));
			
			// cambio de datos a la pagina
			data = data.replaceAll("---SERVIDOR---", servidor);
			data = data.replaceAll("---ENCABEZADO1---", rb.getString("files.files"));
			data = data.replaceAll("---CODIGO---", String.valueOf(oExpedienteForm.getF1()));
			data = data.replaceAll("---VERSION---", String.valueOf(oExpedienteForm.getNumVer()));
			data = data.replaceAll("---FECHA_DE_CREACION---", oExpedienteForm.getFecha());
			data = data.replaceAll("---SOLICITADO---", user.getNamePerson());
			data = data.replaceAll("---IMPRESO---", ToolsHTML.sdfShow.format(new java.util.Date()));
			
			FilesForm files = new FilesForm();
			ConfExpedienteDAO conf = new ConfExpedienteDAO();
			ArrayList campos = (ArrayList)conf.findAllByOrder();
			
			dataDetalle = data.substring(data.indexOf("<!--etiqueta1A-->"), data.indexOf("<!--etiqueta1B-->"));
			data = data.substring(0, data.indexOf("<!--etiqueta1A-->")).concat(data.substring(data.indexOf("<!--etiqueta1B-->")));
			
			data = cargarExpediente(data,
					campos,
					oExpedienteForm,
					dataDetalle,
					"<!--etiqueta1B-->",
					new String[] { "---ETIQUETA---", "---VALOR---" });
			
			arc.escribir(nameFileOutput, data, "UTF-8");
			
			namePdfFinal = nameFileOutput.replaceAll(".html", ".pdf");
			HtmlToPdfConvert.generatePDF(nameFileOutput, namePdfFinal);
			
			File fileDelete = new File(nameFileOutput);
			if(fileDelete.exists()) {
				FileUtils.forceDelete(fileDelete);
			}
			
		}	
		return namePdfFinal;
	}


	public BaseDocumentForm construirDocumentoPdfFromFiles(HttpServletRequest request, Users usuario, String f1, String numVersion, 
			String imprimir, ArrayList<BaseDocumentForm> documentos, ExpedienteForm files, ExpedienteDetalleForm exp) 
			throws DocumentsNotFoundException, ApplicationExceptionChecked, Exception {
		
		BaseDocumentForm forma = null;
		
		//preparamos el directorio temporal para el documento
		String tmpFilesDir = ToolsHTML.getPath() 
				+ "tmp" + File.separator + usuario.getUser() + File.separator + "expediente" + f1;
		File tmpFiles = new File(tmpFilesDir);
		tmpFiles.mkdirs();
		for (int i = tmpFiles.listFiles().length; i > 0; i--) {
			log.info("Eliminando archivo temporal " + tmpFiles.listFiles()[i-1].getName());
			tmpFiles.listFiles()[i-1].delete();
		}
		
		//borramos el contenido del directorio, si es que hay algo
		ExpedienteDetalleDAO oExpedienteDetalleDAO = new ExpedienteDetalleDAO();
		ExpedienteVersionDAO oExpedienteVersionDAO = new ExpedienteVersionDAO();
		CachedRowSet crs = null;
		List<ExpedienteRequest> listDoc = null;
		boolean isVersion;
		
		log.info("Parametros recibidos f1='"  + f1 
				+ "', numVersion='" + numVersion 
				+ "', imprimir='" + imprimir + "'");
		
		int f1Int = -1;
		
		try {
			f1Int = Integer.parseInt(f1);
		} catch (Exception e) {
			// TODO: handle exception
			throw new ApplicationExceptionChecked("E0120");
		}
		files.setF1(f1Int);
		//obtenemos la info del expediente
		new ExpedienteDAO().findById(files);

		files.setNumVer(Integer.parseInt(numVersion));
		files.setPrinting(Boolean.parseBoolean(imprimir));
		
		
		//obtenemos el detalle a mostrar
		if(files.getNumVer()!=0) {
			isVersion = true;
			crs = oExpedienteVersionDAO.findAll(files);
			listDoc = oExpedienteVersionDAO.findAllExpedientes(files);
		} else {
			isVersion = false;
			crs = oExpedienteDetalleDAO.findAll(files);
			listDoc = oExpedienteDetalleDAO.findAllExpedientes(files);
		}
		
		Hashtable tree = ToolsHTML.checkTree(null, usuario);
		
		//tenemos los archivos involucrados en el expediente
		//debemos obtenerlos del repositorio y colocarlos en el
		//orden respectivo para que el visor los tome
		// solo validamos que tenga el pdf generado
		//TODO: solo preparar los archivos con pdf
		boolean incluirDocumento = false;
		while(crs!=null && crs.next()) {
			
			incluirDocumento = false;
			for(ExpedienteRequest doc: listDoc) {
				if(doc.getNumgen().equals(crs.getString("numgen"))) {
					try {
						if(doc.getStatuVer().equals(HandlerDocuments.docBorrador) || !doc.getStatuVer().equals(HandlerDocuments.docApproved) || !doc.getStatuVer().equals(doc.getStatu()) ) {
							throw new FilesDocumentNotValidException("El documento no esta aprobado para ser guardado en una version");
						}
						if(!doc.isActive()) {
							throw new FilesDocumentNotValidException("El documento no existe en el sistema");
						}
						if(!doc.isPdfGenerated()) {
							throw new FilesDocumentNotValidException("El documento no tiene pdf generado");
						}
						if(doc.getToForFiles()==null || doc.getToForFiles().equals("true") || doc.getToForFiles().equals("1") ) {
							throw new FilesDocumentNotValidException("El documento no esta marcado como expediente");
						}
						incluirDocumento = true;
					} catch(FilesDocumentNotValidException e) {
						incluirDocumento = false;
					}
					break;
				}
            }				

			if(incluirDocumento) {
				forma = new BaseDocumentForm();
				forma.setIdDocument(crs.getString("numgen"));
				forma.setNumberGen(crs.getString("numgen"));
				forma.setNumVer(isVersion?crs.getInt("numver"):0);
				forma.setIdFiles(files.getF1());
				forma.setNumVerFiles(files.getNumVer());
				forma.setExpediente(false);
				forma.setToForFiles(crs.getString("toForFiles"));
				//en expedientes siempre es una copia
				forma.setNumberCopies(1);
				// Se carga la información del documento en el bean forma,
				// sacandola de la base de datos.
				HandlerStruct.loadDocument(forma, !isVersion, false, tree, request);
				
				//request.getSession().setAttribute("showDocumentI", forma);
				forma.setPrinting(files.isPrinting());
				
				documentos.add(forma);
			}
		}
		
		if(documentos.size()==0){
			throw new DocumentsNotFoundException("No hay documentos en el expediente");
		}
		//recorremos los documentos para colocarlos en el directorio respectivo
		int orden = 0;
		String pieces = "";
		List<String> listFilesForFiles = new ArrayList<String>();
		String outputNameFileFinal = tmpFilesDir + File.separator + "expediente"+ files.getF1() +".pdf";
		String newNameFile = null;
		String oldNameFile = null;

		String namePdfPortada = createExpPortada(request, files, usuario);
		
		for (Iterator<BaseDocumentForm> iterator = documentos.iterator(); iterator.hasNext();) {
			BaseDocumentForm baseDocumentForm = iterator.next();
			HandlerStruct.createFile(baseDocumentForm, 
					tmpFiles.getAbsolutePath());
			
			//debo verificar que documento es este para proceder a cambiarle el nombre
			//basado en el orden del expediente
			//renombramos segun el orden del expediente
			String finalPieceName = "ExpPiece_" + (++orden) 
					+ baseDocumentForm.getNameFile().substring(baseDocumentForm.getNameFile().lastIndexOf("."));
			pieces += finalPieceName + ";";
			log.info("Renombrando " + baseDocumentForm.getNameFile() + " como " + finalPieceName);
			
			oldNameFile = tmpFilesDir + File.separator + baseDocumentForm.getNameFile();
			newNameFile = tmpFilesDir + File.separator + finalPieceName;
			new File(oldNameFile).renameTo(new File(newNameFile));
			
			listFilesForFiles.add(newNameFile);

		}
		
		pieces = pieces.substring(0, pieces.length() -1);
		request.setAttribute("expedientePiecesNames", pieces);
		
		ConcatPDFFile.concat(listFilesForFiles, outputNameFileFinal);
		
		for(String nameFile: listFilesForFiles) {
			File file = new File(nameFile);
			if(file.exists()) {
				FileUtils.forceDelete(file);
			}
		}
		
		if(exp!=null) {
			// Salvamos el pdf
			StringBuilder sb = new StringBuilder(getFilesPath()).append(File.separator);
			sb.append("files_").append(exp.getF1()).append("_").append(exp.getFilesVersion()).append(".pdf");
			
			log.info("Guardando expediente: ".concat(sb.toString()));
			
			Archivo.writeEncrypt(Archivo.retrieveByteArrayInputStream(new File(outputNameFileFinal)), new File(sb.toString()), Constants.PASS_ENCRYTED);
		}
		
		return forma;
			
	}
	
	public void recuperarVersionAlmacenada(HttpServletRequest request, Users usuario, ExpedienteForm files) throws FilesNotFoundInDiskException, Exception {
		//preparamos el directorio temporal para el documento
		String tmpFilesDir = ToolsHTML.getPath() 
				+ "tmp" + File.separator + usuario.getUser() + File.separator + "expediente" + files.getF1();
		File tmpFiles = new File(tmpFilesDir);
		tmpFiles.mkdirs();
		for (int i = tmpFiles.listFiles().length; i > 0; i--) {
			log.info("Eliminando archivo temporal " + tmpFiles.listFiles()[i-1].getName());
			tmpFiles.listFiles()[i-1].delete();
		}
		
		String outputNameFileFinal = tmpFilesDir + File.separator + "expediente"+ files.getF1() +".pdf";

		// archivo de expedientes almacenado
		StringBuilder filesVersionStore = new StringBuilder(getFilesPath()).append(File.separator);
		filesVersionStore.append("files_").append(files.getF1()).append("_").append(files.getFilesVersion()).append(".pdf");
		
		File fileIn = new File(filesVersionStore.toString());
		
		if(!fileIn.exists()) {
			throw new FilesNotFoundInDiskException("El archivo de expediente no existe");
		}
		
		createExpPortada(request, files, usuario);
		
		Archivo.writeEncrypt(Archivo.retrieveByteArrayInputStream(fileIn), new File(outputNameFileFinal), Constants.PASS_ENCRYTED);

	}

	public ExpedienteForm findById(ExpedienteForm files) throws Exception {
		ExpedienteDAO oExpedienteDAO = new ExpedienteDAO();
		return oExpedienteDAO.findById(files);
	}

	public ExpedienteForm findByIdAndNumVersionFromHistory(ExpedienteForm files) throws Exception {
		ExpedienteHistoryDAO oExpedienteHistoryDAO = new ExpedienteHistoryDAO();
		return oExpedienteHistoryDAO.findById(files);
	}


	public void saveFileToPdf() {
		
		
	}



}
