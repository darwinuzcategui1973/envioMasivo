package com.desige.webDocuments.persistent.managers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.dao.ConfDocumentoDAO;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.document.forms.BeanVersionForms;
import com.desige.webDocuments.document.forms.CheckOutDocForm;
import com.desige.webDocuments.files.forms.DocumentForm;
import com.desige.webDocuments.mail.actions.SendMailTread;
import com.desige.webDocuments.mail.forms.MailForm;
import com.desige.webDocuments.norms.forms.BaseNormsForm;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.sacop.forms.Plantilla1BD;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.structured.forms.DataHistoryStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.IDDBFactorySql;
import com.desige.webDocuments.utils.StringUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.BeanCheckSec;
import com.desige.webDocuments.utils.beans.NodesTree;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.workFlows.actions.EditWorkFlowAction;
import com.focus.util.Archivo;

/**
 * Title: HandlerStruct.java <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simon Rodriguez (SR)
 * @version WebDocuments v1.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>18/04/2004 (NC) Creation</li>
 *          <li>19/05/2005 (SR) se agrego UserGroup_toEditRegister para valiudar
 *          si tienepermisos o no para ver el archivo de esa carpeta</li>
 *          <li>22/0572005 (NC) Se agregó el método "getParentPrefix" para
 *          construir los prefijos de las carpertas</li>
 *          <li>22/0572005 (SR) Se valido si es null el object security</li>
 *          <li>29/05/2005 (NC) Cambios en el manejo de la Seguridad</li>
 *          <li>13/06/2005 (SR) Se coloco un parametro EnLinea.doc</li>
 *          <li>16/06/2005 (SR) Utilizar
 *          DesigeConf.getProperty("documentoenlinea")</li>
 *          <li>22/06/2005 (SR) Condicionen metodo loadDocument D.docPublic=0 en
 *          query para obligar al documento que este publicado.</li>
 *          <li>24/07/2005 Cambios para manipular los comentarios al momento de
 *          mover una carpeta o proceso
 *          <li>24/07/2005 Cambios para manipular los comentarios al momento de
 *          mover una carpeta o proceso
 *          <li>29/07/2005 (SR)Se creo un metodo moveNodeArch en el metodo
 *          moveNode se llamo a moveNodeArch
 *          <li>21/02/2006 (SR)Se agrego un campo al query statuhist en
 *          loadDocument
 *          <li>30/06/2006 (NC) Cambios para los Documentos Vinculados</li>
 *          <li>13/07/2006 (NC) Cambios para heredar o no los Prefijos de las
 *          Carpetas</li>
 *          <li>27/07/2006 (SR) Se valido que en documents Links no se repitiera
 *          la misma dta y que no insertara nullos</li>
 *          </ul>
 */
public class HandlerStructcopia extends HandlerBD implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8035288093242191562L;
	private static String tableModules = "module";
	private static String tableLocation = "location";
	private static Hashtable nodos;
	static Logger log = LoggerFactory.getLogger(HandlerStruct.class.getName());

	// public static byte permission = 1;

	/**
	 * Método para obtener la Mayor Versión del Documento Actual
	 * 
	 * @param numDoc
	 * @return
	 * @throws Exception
	 */
	public static String getMayorVersionDocument(int numDoc) throws Exception {
		StringBuilder sql = new StringBuilder(1024)
				.append("SELECT MAX(vd.numVer) AS numVer FROM versiondoc vd WHERE vd.numDoc = ")
				.append(numDoc);

		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		log.debug("[getMayorVersionDocument] = " + sql.toString());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			return prop.getProperty("numVer");
		}
		return null;
	}

	// SIMON 12 DE JULIO 2005 INICIO
	public static String getMayorStatusVersionDocument(int numVer)
			throws Exception {
		StringBuilder sql = new StringBuilder(1024).append(
				"SELECT statu FROM versiondoc vd WHERE vd.numVer = ").append(
				numVer);
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		log.debug("[getMayorStatuVersionDocument] = " + sql.toString());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			return prop.getProperty("statu").trim();
		}
		return null;
	}

	// SIMON 12 DE JULIO 2005 FIN

	/**
	 * Método que permite cargar los datos del Documentos
	 * 
	 * @param forma
	 * @throws Exception
	 */
	// HandlerStruct.loadDocument(forma,true,false);
	// SIMON 22 DE JUNIO 2005 INICIO
	public static void loadDocument(BaseDocumentForm forma,
			boolean loadMayorVersion, boolean loadLastApproved,
			boolean publicado) throws Exception {
		if (forma != null) {
			StringBuilder condicion = new StringBuilder(" ")
					.append(" FROM documents d,versiondoc vda, person p WHERE vda.numDoc = d.numGen AND ")
					.append("vda.numVer = (SELECT MAX(numVer) FROM versiondoc vdb WHERE vda.numDoc = vdb.numDoc and vdb.statu='")
					.append(BaseDocumentForm.statuApproved).append("')")
					.append("AND d.numGen ='").append(forma.getNumberGen())
					.append("' AND owner = p.nameUser");
			log.debug("condicion:" + condicion);
			String result = HandlerBD.getField("lastVersionApproved",condicion.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			log.debug("{result}:" + result);
			if ("0".equalsIgnoreCase(result)) {
				StringBuilder condicion1 = new StringBuilder(" ")
						.append("FROM documents d,versiondoc vda, person p WHERE vda.numDoc = d.numGen AND ")
						.append("vda.numVer =(SELECT MAX(numVer) FROM VrsionDoc vdb WHERE vda.numDoc = vdb.numDoc and vdb.statu='")
						.append(BaseDocumentForm.statuApproved).append("')")
						.append(" AND d.numGen ='")
						.append(forma.getNumberGen())
						.append("' AND owner = p.nameUser");
				String numVer = HandlerBD.getField("numVer",condicion1.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
				if (!ToolsHTML.isEmptyOrNull(numVer)) {
					// if
					// (!(ToolsHTML.isEmptyOrNull(HandlerBD.getField("numVer",condicion1.toString())))){
					int valor = Integer.parseInt(numVer);
					HandlerStruct.actualizarVersionDoc("numGen",
							Integer.parseInt(forma.getNumberGen()),
							"lastVersionApproved", "documents", valor);
				}
			}
			StringBuilder sql = new StringBuilder();
			/*
			 * sql.append(
			 * "SELECT numGen,number,nameDocument,datePublic,dateCreated,owner,nameFile,type,prefix,normISO,docProtected,d.statu AS statuDoc"
			 * ); sql.append(
			 * " ,idNode,docPublic,toForFiles,docOnline,url,keys,descript,contextType,d.comments AS commentsDoc,vda.statu,"
			 * ); switch (Constants.MANEJADOR_ACTUAL) { case
			 * Constants.MANEJADOR_MSSQL:
			 * sql.append("(p.Apellidos + ' ' + p.Nombres) AS Nombre"); break;
			 * case Constants.MANEJADOR_POSTGRES:
			 * sql.append("(p.Apellidos || ' ' || p.Nombres) AS Nombre"); break;
			 * } sql.append(
			 * " ,d.lastOperation,p.cargo,d.lastVersionApproved,vda.approved, vda.expires,vda.MayorVer,vda.MinorVer,"
			 * ); sql.append(
			 * " vda.dateApproved,vda.dateExpires,vda.numVer,vda.nameDocVersion,vda.ownerVersion "
			 * );
			 */
			sql.append("SELECT d.*,d.statu AS statuDoc,d.comments AS commentsDoc,vda.statu,vda.typeWF,");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append("(p.Apellidos + ' ' + p.Nombres) AS Nombre, ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append("(p.Apellidos || ' ' || p.Nombres) AS Nombre, ");
				break;
			
			}
			sql.append(
					" p.cargo,vda.approved, vda.expires,vda.MayorVer,vda.MinorVer,td.type_Formato,")
					.append(" vda.dateApproved,vda.dateExpires,vda.numVer,vda.nameDocVersion,vda.ownerVersion ")
					.append(" FROM documents d,versiondoc vda, person p, typedocuments td ")
					.append(" WHERE vda.numDoc = d.numGen ")
					.append(" AND cast(d.type as int) = td.idTypeDoc ")
					.append(" AND vda.numVer = ");
			if (!loadLastApproved) {
				if (!loadMayorVersion && forma.getNumVer() != 0) {
					sql.append(forma.getNumVer());
				} else {
					sql.append("(SELECT MAX(numVer) FROM versiondoc vdb WHERE vda.numDoc = vdb.numDoc)");
				}
			} else {
				sql.append(" d.lastVersionApproved ");
				if (publicado) {
					sql.append(" AND d.docPublic='0'");
				}
			}
			sql.append(" AND d.numGen = ").append(forma.getNumberGen())
					.append(" AND owner = p.nameUser");
			log.debug("[loadDocument] = " + sql.toString());
			Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				log.debug("Cargando DATOS");
				forma.setNumber(prop.getProperty("number"));
				forma.setNameDocument(ToolsHTML.isEmptyOrNull(prop
						.getProperty("nameDocVersion")) ? prop
						.getProperty("nameDocument") : prop
						.getProperty("nameDocVersion"));
				forma.setLoadDoc(true);
				String date = prop.getProperty("dateCreated");
				if (!ToolsHTML.isEmptyOrNull(date)) {
					forma.setDateCreation(ToolsHTML.formatDateShow(date, true));
				} else {
					forma.setDateCreation("");
				}
				date = prop.getProperty("datePublic");
				if (!ToolsHTML.isEmptyOrNull(date)) {
					forma.setDatePublic(ToolsHTML.date.format(ToolsHTML.date
							.parse(date)));
				} else {
					forma.setDatePublic("");
				}

				forma.setOwner(ToolsHTML.isEmptyOrNull(prop
						.getProperty("ownerVersion")) ? prop
						.getProperty("owner") : prop
						.getProperty("ownerVersion"));
				forma.setNameOwner(ToolsHTML.isEmptyOrNull(prop
						.getProperty("ownerVersion")) ? prop
						.getProperty("owner") : HandlerDBUser.getNameUser(prop
						.getProperty("ownerVersion")));
				forma.setNameFile(prop.getProperty("nameFile"));
				forma.setTypeDocument(prop.getProperty("type"));
				forma.setPrefix(prop.getProperty("prefix"));
				forma.setNormISO(prop.getProperty("normISO"));
				forma.setDocProtected(prop.getProperty("docProtected"));
				forma.setDocPublic(prop.getProperty("docPublic"));
				forma.setDocOnline(prop.getProperty("docOnline"));
				forma.setStatu(Integer.parseInt(prop.getProperty("statu")
						.trim()));
				forma.setURL(prop.getProperty("url"));
				forma.setKeys(prop.getProperty("keysDoc"));
				forma.setDescript(prop.getProperty("descript"));
				forma.setApproved(prop.getProperty("approved"));
				forma.setExpires(prop.getProperty("expires"));
				forma.setMayorVer(prop.getProperty("MayorVer"));
				forma.setMinorVer(prop.getProperty("MinorVer"));
				forma.setContextType(prop.getProperty("contextType"));
				forma.setNormISODescript(HandlerNorms.getDescriptNormIso(forma
						.getNormISO()));
				forma.setComments(prop.getProperty("commentsDoc"));
				forma.setDateApproved(ToolsHTML.formatDateShow(
						prop.getProperty("dateApproved"), false));
				forma.setDateExpires(ToolsHTML.formatDateShow(
						prop.getProperty("dateExpires"), false));
				String numVer = prop.getProperty("numVer");
				forma.setNumVer(Integer.parseInt(numVer));
				forma.setDocRelations(getDocumentsLinks(
						prop.getProperty("numGen"), numVer));
				forma.setIdNode(prop.getProperty("idNode"));
				forma.setNumVer(Integer.parseInt(prop.getProperty("numVer")));
				forma.setStatuDoc(prop.getProperty("statuDoc"));
				forma.setLastOperation(prop.getProperty("lastOperation").trim());
				forma.setCharge(prop.getProperty("cargo"));
				forma.setToForFiles(prop.getProperty("toForFiles"));
				forma.setDateDead(prop.getProperty("dateDead"));
				forma.setLoteDoc(prop.getProperty("loteDoc"));

				// campos adicionales en la tabla documents
				try {
					ConfDocumentoDAO conf = new ConfDocumentoDAO();
					ArrayList lista = (ArrayList) conf.findAll();

					if (lista != null) {
						for (int i = 0; i < lista.size(); i++) {
							DocumentForm obj = (DocumentForm) lista.get(i);
							forma.set(obj.getId().toUpperCase(),
									prop.getProperty(obj.getId()));
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				forma.setLoadDoc(false);
			}
		}
	}

	/**
	 * 
	 * @param forma
	 * @param loadMayorVersion
	 * @param loadLastApproved
	 * @param struct
	 * @param request
	 * @throws Exception
	 */
	public static void loadDocument(BaseDocumentForm forma,
			boolean loadMayorVersion, boolean loadLastApproved,
			Hashtable struct, HttpServletRequest request) throws Exception {
		if (forma != null) {
			if (request != null) {
				if (request.getParameter("idPerson") != null) {
					forma.setIdUser(Integer.parseInt(request
							.getParameter("idPerson")));
				} else {
					Users usuario = (Users) request.getSession().getAttribute(
							"user");
					forma.setIdUser((int) usuario.getIdPerson());
				}
			}

			StringBuilder sql = new StringBuilder("");
			/*
			 * StringBuilder sql = new StringBuilder(
			 * "SELECT numGen,number,nameDocument,datePublic,dateCreated,owner,nameFile,type,prefix,normISO,docProtected,d.statu AS statuDoc,vda.typeWF"
			 * ); sql.append(
			 * " ,idNode,docPublic,toForFiles,docOnline,url,keys,descript,contextType,d.comments AS commentsDoc,vda.statu,"
			 * ); switch (Constants.MANEJADOR_ACTUAL) { case
			 * Constants.MANEJADOR_MSSQL:
			 * sql.append("(p.Apellidos + ' ' + p.Nombres) AS Nombre"); break;
			 * case Constants.MANEJADOR_POSTGRES:
			 * sql.append("(p.Apellidos || ' ' || p.Nombres) AS Nombre"); break;
			 * } sql.append(
			 * " ,d.lastOperation,d.lastVersionApproved,d.lastVersionApproved,"
			 * );
			 */

			sql.append("SELECT d.*,d.statu AS statuDoc,d.comments AS commentsDoc,vda.statu,vda.typeWF,");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append("(p.Apellidos + ' ' + p.Nombres) AS Nombre, ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append("(p.Apellidos || ' ' || p.Nombres) AS Nombre, ");
				break;
			
			}
			sql.append(
					" p.cargo,vda.approved, vda.expires,vda.MayorVer,vda.MinorVer,td.type_Formato,")
					.append(" vda.dateApproved,vda.dateExpires,vda.numVer,vda.nameDocVersion,vda.ownerVersion ")
					.append(" FROM documents d,versiondoc vda, person p,typedocuments td WHERE vda.numDoc = d.numGen")
					.append(" AND cast(d.type as int) = td.idTypeDoc")
					.append(" AND vda.numVer = ");
			if (!loadLastApproved) {
				if (!loadMayorVersion && forma.getNumVer() != 0) {
					sql.append(forma.getNumVer());
				} else {
					sql.append("(SELECT MAX(numVer) FROM versiondoc vdb WHERE vda.numDoc = vdb.numDoc AND vdb.active='1')");
				}
			} else {
				sql.append(" d.lastVersionApproved ");
			}
			sql.append(" AND d.numGen = ").append(forma.getNumberGen())
					.append(" AND owner = p.nameUser");
			log.debug("[loadDocument] = " + sql.toString());
			Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				forma.setNumber(prop.getProperty("number"));
				forma.setNameDocumentOld(prop.getProperty("nameDocument"));
				forma.setNameDocument(ToolsHTML.isEmptyOrNull(prop
						.getProperty("nameDocVersion")) ? prop
						.getProperty("nameDocument") : prop
						.getProperty("nameDocVersion"));
				forma.setLoadDoc(true);
				forma.setLoteDoc(ToolsHTML.isEmptyOrNull(
						prop.getProperty("loteDoc"), ""));
				String date = prop.getProperty("dateCreated");
				if (!ToolsHTML.isEmptyOrNull(date)) {
					forma.setDateCreation(ToolsHTML.formatDateShow(date, true));
				} else {
					forma.setDateCreation("");
				}
				date = prop.getProperty("datePublic");
				if (!ToolsHTML.isEmptyOrNull(date)) {
					forma.setDatePublic(ToolsHTML.date.format(ToolsHTML.date
							.parse(date)));
				} else {
					forma.setDatePublic("");
				}
				forma.setOwner(ToolsHTML.isEmptyOrNull(prop
						.getProperty("ownerVersion")) ? prop
						.getProperty("owner") : prop
						.getProperty("ownerVersion"));
				forma.setNameOwner(ToolsHTML.isEmptyOrNull(prop
						.getProperty("ownerVersion")) ? HandlerDBUser
						.getNameUser(prop.getProperty("owner")) : HandlerDBUser
						.getNameUser(prop.getProperty("ownerVersion")));
				forma.setOwnerOld(prop.getProperty("owner"));
				forma.setNameOwnerOld(HandlerDBUser.getNameUser(prop
						.getProperty("owner")));

				forma.setNameFile(changeExtTxtToHtml(quitarSpacioenBlancoNameFile(prop
						.getProperty("nameFile"))));
				forma.setTypeDocument(prop.getProperty("type"));
				forma.setIdNode(prop.getProperty("idNode"));
				forma.setTypeFormat(prop.getProperty("type_Formato"));
				String typeWF = prop.getProperty("typeWF");
				if (ToolsHTML.isNumeric(typeWF)) {
					forma.setTypeWF(Byte.parseByte(prop.getProperty("typeWF")));
				} else {
					forma.setTypeWF(Constants.notPermission);
				}
				log.debug("type WF: " + forma.getTypeWF());
				String prefixDoc = null;// properties.getProperty("prefix");
				// byte heredarPrefijos = 0;
				// Se Busca la Localidad a la Cual pertenece el Documento
				if (struct != null) {
					// String idLocation =
					// HandlerStruct.getIdLocationToNode(struct,forma.getIdNode());
					// if (idLocation!=null) {
					// BaseStructForm localidad =
					// (BaseStructForm)struct.get(idLocation);
					// if (localidad!=null) {
					// heredarPrefijos = localidad.getHeredarPrefijo();
					// }
					// }

					// if (!prefijos.containsKey(forma.getIdNode())) {
					prefixDoc = ToolsHTML.getPrefixToDoc(struct,
							forma.getIdNode());
					// prefijos.put(forma.getIdNode(),prefixDoc);
					// } else {
					// prefixDoc = (String)prefijos.get(forma.getIdNode());
					// }

					// Si está activado el Heredar Prefijo se reconstruye el
					// Mismo
					// if (Constants.permission == heredarPrefijos) {
					// prefixDoc =
					// ToolsHTML.getPrefixToDoc(struct,forma.getIdNode());
					// } else {
					// En Caso Contrario se coloca como Prefijo sólo el prefijo
					// de la Carpeta
					// BaseStructForm folder =
					// (BaseStructForm)struct.get(forma.getIdNode());
					// if (folder!=null) {
					// prefixDoc = folder.getPrefix();
					// }
					// }
				} else {
					// el prefijo se construye dinamicamente
					// prefixDoc = prop.getProperty("prefix");
					Users user = (Users) request.getSession().getAttribute(
							"user");
					if (user == null) {
						user = HandlerDBUser.load(forma.getIdUser(), true);
					}
					prefixDoc = ToolsHTML.getPrefixToDoc(request.getSession(),
							user, forma.getIdNode());
				}

				if (!ToolsHTML.isEmptyOrNull(prefixDoc)) {
					forma.setPrefix(prefixDoc.trim());
				} else {
					forma.setPrefix("");
				}
				// forma.setPrefix(prop.getProperty("prefix"));
				forma.setNormISO(prop.getProperty("normISO"));
				forma.setDocProtected(prop.getProperty("docProtected"));
				forma.setDocPublic(prop.getProperty("docPublic"));
				forma.setDocOnline(prop.getProperty("docOnline"));
				forma.setStatu(Integer.parseInt(prop.getProperty("statu")
						.trim()));
				forma.setURL(prop.getProperty("url"));
				forma.setKeys(prop.getProperty("keysDoc"));
				forma.setDescript(prop.getProperty("descript"));
				forma.setApproved(prop.getProperty("approved"));
				forma.setExpires(prop.getProperty("expires"));
				String mayorVer = prop.getProperty("MayorVer");
				forma.setMayorVer(!ToolsHTML.isEmptyOrNull(mayorVer) ? mayorVer
						.trim() : "");
				String minorVer = prop.getProperty("MinorVer");
				forma.setMinorVer(!ToolsHTML.isEmptyOrNull(minorVer) ? minorVer
						.trim() : "");
				forma.setContextType(prop.getProperty("contextType"));
				if (!ToolsHTML.isEmptyOrNull(forma.getNormISO())) {
					forma.setNormISODescript(HandlerNorms
							.getDescriptNormIso(forma.getNormISO()));
					forma.setNormISODescriptId(HandlerNorms
							.getFileNormIso(forma.getNormISO()));
				} else {
					forma.setNormISODescript("");
					forma.setNormISODescriptId("0");
				}
				forma.setComments(prop.getProperty("commentsDoc"));
				forma.setDateApproved(ToolsHTML.formatDateShow(
						prop.getProperty("dateApproved"), false));
				forma.setDateExpires(ToolsHTML.formatDateShow(
						prop.getProperty("dateExpires"), false));
				String numVer = prop.getProperty("numVer");
				forma.setDocRelations(getDocumentsLinks(
						prop.getProperty("numGen"), numVer));

				forma.setNumVer(Integer.parseInt(numVer));
				forma.setStatuDoc(prop.getProperty("statuDoc").trim());
				forma.setLastOperation(prop.getProperty("lastOperation").trim());
				forma.setCharge(HandlerDBUser.getCargoAndArea(prop
						.getProperty("cargo")));
				String lastVersionApproved = prop
						.getProperty("lastVersionApproved");
				forma.setLastVersionApproved(lastVersionApproved);
				if (!loadLastApproved) {
					if (!"0".equalsIgnoreCase(lastVersionApproved)) {
						String query = "SELECT dateApproved FROM versiondoc WHERE numVer = "
								+ lastVersionApproved;
						Properties propI = JDBCUtil.doQueryOneRow(query,Thread.currentThread().getStackTrace()[1].getMethodName());
						if (propI.getProperty("isEmpty").equalsIgnoreCase(
								"false")) {
							forma.setDateApproved(ToolsHTML.formatDateShow(
									propI.getProperty("dateApproved"), true));
						}
					}
				}
				forma.setToForFiles(prop.getProperty("toForFiles"));
				forma.setDateDead(prop.getProperty("dateDead"));
				forma.setLoteDoc(prop.getProperty("loteDoc"));

				// campos adicionales en la tabla documents
				try {
					ConfDocumentoDAO conf = new ConfDocumentoDAO();
					ArrayList lista = (ArrayList) conf.findAll();

					if (lista != null) {
						for (int i = 0; i < lista.size(); i++) {
							DocumentForm obj = (DocumentForm) lista.get(i);
							forma.set(obj.getId().toUpperCase(),
									prop.getProperty(obj.getId()));
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				forma.setLoadDoc(false);
			}
		}
	}

	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	public static void loadDocumentNorms(BaseNormsForm forma) throws Exception {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		if (forma != null) {
			try {
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				StringBuilder sql = new StringBuilder(
						"SELECT * FROM norms WHERE active ='")
						.append(Constants.permission).append("'")
						.append(" AND idNorm IN(").append(forma.getId()).append(")");
				// System.out.println("[loadDocumentNorms] = " +
				// sql.toString());
				st = con.prepareStatement(sql.toString());
				rs = st.executeQuery();
				if (rs.next()) {
					log.debug("Cargando DATOS");
					forma.setFileNameFisico(quitarSpacioenBlancoNameFile(rs
							.getString("nameFile")));
					forma.setContextType(rs.getString("contextType"));
					forma.setLoadDoc(true);
				} else {
					forma.setLoadDoc(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.closeQuietly(con, st, rs);
			}
		}
	}

	/**
	 * 
	 * @param idDocument
	 *            -> id del documento
	 * @return nombre del archivo
	 */
	public static String loadNameFileDocument(String idDocument) {
		try {
			StringBuilder query = new StringBuilder(1024)
					.append("SELECT nameFile FROM documents WHERE numGen=?");

			ArrayList parametros = new ArrayList();
			parametros.add(new Integer(idDocument));

			CachedRowSet crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
			if (crs.next()) {
				return crs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	public static void loadDocumentSacop(Plantilla1BD forma) throws Exception {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		if (forma != null) {

			try {
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				StringBuilder sql = new StringBuilder(
						"SELECT nameFile,contentType FROM tbl_planillasacop1 WHERE active ='")
						.append(Constants.permission).append("' ")
						.append(" AND idplanillasacop1=").append(forma.getId());
				// System.out.println("[loadDocumentSacop] = " +
				// sql.toString());
				st = con.prepareStatement(sql.toString());
				rs = st.executeQuery();
				if (rs.next()) {
					// System.out.println("Cargando DATOS");
					forma.setNameFile(quitarSpacioenBlancoNameFile(rs
							.getString("nameFile")));
					forma.setContentType(rs.getString("contentType"));
					// forma.setLoadDoc(true);
				} else {
					// forma.setLoadDoc(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.closeQuietly(con, st, rs);
			}
		}
	}

	/**
	 * 
	 * @param idNode
	 * @return
	 * @throws Exception
	 */
	public static Vector getDocsAprovedAndNotPublicForNode(String idNode)
			throws Exception {
		idNode = (idNode == null ? "1" : idNode);
		StringBuilder sql = new StringBuilder(1024)
				.append("SELECT d.numGen FROM documents d,versiondoc vd")
				.append(" WHERE docPublic = '")
				.append(Constants.permission)
				.append("'")
				.append(" AND d.numGen = vd.numDoc AND d.lastVersionApproved = vd.numVer")
				.append(" AND d.lastVersionApproved <> '0' AND d.idNode = '")
				.append(idNode).append("'");
		return JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	/**
	 * 
	 * @param idDoc
	 * @return
	 * @throws Exception
	 */
	public static String getDocumentsLinks(String idDoc) throws Exception {
		StringBuilder sql = new StringBuilder(1024).append(
				"SELECT numGenLink FROM documentslinks WHERE numGen = ")
				.append(idDoc);
		StringBuilder resp = new StringBuilder(1024);
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		boolean first = true;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			if (first) {
				resp.append(properties.getProperty("numGenLink"));
				first = false;
			} else {
				resp.append(",").append(properties.getProperty("numGenLink"));
			}
		}
		return resp.toString();
	}

	/**
	 * 
	 * @param idDoc
	 * @param numVer
	 * @return
	 * @throws Exception
	 */
	public static String getDocumentsLinks(String idDoc, String numVer)
			throws Exception {
		StringBuilder sql = new StringBuilder(1024)
				.append("SELECT numGenLink,numVerLink FROM documentslinks WHERE numGen = ")
				.append(idDoc).append("   AND numVer = ").append(numVer);
		// System.out.println("[getDocumentsLinks] = " + sql);
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		boolean first = true;
		StringBuilder resp = new StringBuilder(1024);
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			if (first) {
				resp.append(properties.getProperty("numGenLink")).append("_")
						.append(properties.getProperty("numVerLink"));
				first = false;
			} else {
				resp.append(",").append(properties.getProperty("numGenLink"))
						.append("_")
						.append(properties.getProperty("numVerLink"));
			}
		}
		return resp.toString();
	}

	/**
	 * Método que permite Cargar los Documentos del Nodo dado como parámetro
	 * 
	 * @param idNode
	 * @return
	 * @throws Exception
	 */
	public static Collection getAllDocuments(String idNode, String user,
			String idGroup, String active, Hashtable securityStruct,
			String prefix) throws Exception {
		ArrayList resp = new ArrayList();
		StringBuilder sql = new StringBuilder(
				"SELECT numGen,number,nameDocument,owner,nameFile,d.statu, vd.statu as statuVer, vd.dateExpiresDrafts, vd.dateExpires,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.Nombres) AS Nombre");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.Nombres) AS Nombre");
			break;
		
		}
		sql.append(
				",c.cargo,d.prefix  FROM documents d,person p,tbl_cargo c,tbl_area a, versiondoc vd WHERE d.Owner = p.nameUser AND d.active = '")
				.append(active)
				.append("'")
				.append(" AND p.accountActive = '1'")
				//ydavila 001-00-003256
				.append(" AND c.idcargo=").append(("cast(p.cargo as int)")).append(" and c.idarea=a.idarea");
		if (ToolsHTML.checkValue(idNode)) {
			sql.append(" AND IdNode = '").append(idNode).append("'");
		}
		sql.append(
				" AND vd.numVer = (SELECT MAX(numVer) FROM versiondoc vdi WHERE vdi.numDoc = d.numGen) ")
				.append(" ORDER BY number,lower(nameDocument)");
		log.debug("[getAllDocuments] sql " + sql);
		Hashtable permission = new Hashtable();
		HandlerDocuments.getAllSecurityForDocAndIdStruct(idGroup, idNode,
				false, permission);
		HandlerDocuments.getAllSecurityForDocAndIdStruct(user, idNode, true,
				permission);
		boolean isAdmon = idGroup.equalsIgnoreCase(DesigeConf
				.getProperty("application.admon"));
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			String idDocument = properties.getProperty("numGen");
			PermissionUserForm forma = (PermissionUserForm) permission
					.get(idDocument);
			if (forma == null && securityStruct != null) {
				// El Documento no tiene seguridad definida, se procede a cargar
				// la seguridad
				// definida para la Carpeta
				forma = (PermissionUserForm) securityStruct.get(idNode);
			}
			if ((forma != null && forma.getToViewDocs() == Constants.permission)
					|| isAdmon) {
				BaseDocumentForm bean = new BaseDocumentForm();
				bean.setNumberGen(idDocument);
				bean.setNumber(properties.getProperty("number"));
				bean.setNameDocument(properties.getProperty("nameDocument"));
				bean.setOwner(properties.getProperty("Nombre"));
				//ydavila Ticket 001-00-003023
				bean.setrespsolcambio(properties.getProperty("respsolcambio"));
				bean.setrespsolelimin(properties.getProperty("respsolelimin"));
				bean.setrespsolimpres(properties.getProperty("respsolimpres"));
				
				bean.setNameFile(properties.getProperty("nameFile"));
				String sStatu = properties.getProperty("statu");
				if (!ToolsHTML.isEmptyOrNull(sStatu)
						&& ToolsHTML.isNumeric(sStatu))
					bean.setStatu(Integer.parseInt(sStatu.trim()));
				else
					bean.setStatu(0);
				bean.setCharge(properties.getProperty("cargo"));
				// El Prefijo se Construye Dinámicamente
				// por lo tanto se construye fuera del Método y se pasa como
				// parámetro para
				// solamente enlazarlo...
				bean.setPrefix(properties.getProperty("prefix"));
				bean.setPrefix(prefix);

				// YSA se setean valores del estatus
				String dateSystem = ToolsHTML.sdf.format(new Date());
				Locale defaultLocale = new Locale(
						DesigeConf.getProperty("language.Default"),
						DesigeConf.getProperty("country.Default"));
				ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",
						defaultLocale);

				bean.setStatu(Integer.parseInt(properties.getProperty(
						"statuVer").trim()));
				String statusDocumento = "";
				int statuDocuments = 0;
				if (!ToolsHTML.isEmptyOrNull(sStatu)
						&& ToolsHTML.isNumeric(sStatu))
					statuDocuments = Integer.parseInt(sStatu.trim());

				switch (bean.getStatu()) {
				case 1: // Borrador 1
					statusDocumento = rb.getString("wf.statuDoc1");
					break;
				case 3: // Revisado 3
					statusDocumento = rb.getString("wf.statuDoc5");
					break;
				case 5: // Aprobado 5
					statusDocumento = rb.getString("wf.statuDoc5");
					break;
				case 6: // Obsoleto 6
					statusDocumento = rb.getString("wf.statuDoc6");
					break;
				default:
					break;
				}

				if (!ToolsHTML.isEmptyOrNull(dateSystem)
						&& dateSystem.length() > 9) {
					if (bean.getStatu() == 1
							&& !ToolsHTML.isEmptyOrNull(properties
									.getProperty("dateExpiresDrafts"))
							&& properties.getProperty("dateExpiresDrafts")
									.length() > 9
							&& dateSystem.substring(0, 10).compareTo(
									properties.getProperty("dateExpiresDrafts")
											.substring(0, 10)) >= 0) {
						// Borradores expirados
						statusDocumento += " Expirado";

					} else if (bean.getStatu() == 5
							&& !ToolsHTML.isEmptyOrNull(properties
									.getProperty("dateExpires"))
							&& properties.getProperty("dateExpires").length() > 9
							&& dateSystem.substring(0, 10).compareTo(
									properties.getProperty("dateExpires")
											.substring(0, 10)) >= 0) {
						// Aprobados expirados
						statusDocumento += " Expirado";
					}
				}

				switch (statuDocuments) {
				case 2: // En Revision
					statusDocumento += " / " + rb.getString("wf.statuDoc2");
					break;
				case 4: // En Aprobacion
					statusDocumento += " / " + rb.getString("wf.statuDoc4");
					break;
				case 15: // En FTP
					statusDocumento += " / " + rb.getString("wf.statuDoc9");
					break;
				default:
					break;
				}
				bean.setStatuDoc(statusDocumento);

				resp.add(bean);
			}
		}
		return resp;
	}

	/**
	 * Método que permita la carga de la información de lo datos de las
	 * Subcarpetas y/o procesos del nodo dado como parámetro
	 * 
	 * @param idNode
	 * @param comparator
	 * @return
	 * @throws Exception
	 */
	public static Collection getAllNodes(String idNode, String comparator,
			Hashtable security, String userOwner, boolean isAdmon, String grupo)
			throws Exception {
		ArrayList resp = new ArrayList();
		StringBuilder sql = new StringBuilder(1024);
		String typeOrden = String.valueOf(HandlerParameters.PARAMETROS.getTypeOrder());

		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("SELECT s.*,(p.Apellidos + ' ' + p.Nombres) AS nombre ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("SELECT s.*,(p.Apellidos || ' ' || p.Nombres) AS nombre ");
			break;

		}
		//ydavila Ticket 001-00-003023
		//sql.append(",c.cargo FROM struct s,person p,tbl_cargo c,tbl_area a")
		sql.append(",c.cargo, s.respsolcambio, s.respsolelimin, s.respsolimpres FROM struct s,person p,tbl_cargo c,tbl_area a")
				.append(" WHERE s.Owner = p.nameUser AND p.AccountActive = '")
				.append(Constants.permission)
				.append("'")
				.append(" AND c.idcargo=cast(p.cargo as int) and c.idarea=a.idarea");
		boolean set = true;
		if (ToolsHTML.checkValue(idNode)) {
			sql.append(" AND idNodeParent = '").append(idNode).append("'");
			set = true;
		}
		if (ToolsHTML.checkValue(comparator)) {
			if (set) {
				sql.append(" AND ");
			} else {
				sql.append(" WHERE ");
			}
			sql.append(" NodeType ").append(comparator).append(" '5'");
		}
		sql.append(" ORDER BY IdNodeParent");
		// Ordenar por Fecha de Creación
		if (Constants.notPermissionSt.compareTo(typeOrden) == 0) {
			sql.append(",s.IdNode");
		} else {
			// Ordenar Alfabeticamente
			sql.append(",lower(Name)");
		}

		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		PermissionUserForm forma = null;
		ArrayList folders = ToolsHTML.getList("addDoc");

		// vamos a armar la estructura en un TreeMap para ubicar a los padres
		// rapidamente
		// StructAccess sa = new StructAccess(grupo,userOwner);

		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			String nodeType = properties.getProperty("NodeType");
			// if (!loadMyFolder || (loadMyFolder&&folders.contains(nodeType)))
			// {
			if (folders.contains(nodeType)) {
				String id = properties.getProperty("IdNode");
				String IdNodeParent = properties.getProperty("IdNodeParent");
				if (security != null) {

					// String newId = sa.getIdNodeSecurityGroup(id);
					// forma =
					// (PermissionUserForm)security.get(sa.getIdNodeSecurityGroup(newId));
					forma = (PermissionUserForm) security.get(id);

				}
				String own = properties.getProperty("Owner");
				boolean isOwner = userOwner.equalsIgnoreCase(own);
				if ((forma != null && forma.getToView() == Constants.permission)
						|| isAdmon) { // El
										// Usuario
										// no
										// tiene
										// ningún
										// tipo
										// de
										// restricción
										// sobre
										// la
										// carpeta
					String name = properties.getProperty("Name");
					//ydavila Ticket 001-00-003023
					String respsolcambio = properties.getProperty("respsolcambio");
					String respsolelimin = properties.getProperty("respsolelimin");
					String respsolimpres = properties.getProperty("respsolimpres");
					
					String NameIcon = properties.getProperty("NameIcon");
					String owner = properties.getProperty("nombre");
					String dateCreation = properties
							.getProperty("DateCreation");
					String description = properties.getProperty("Description");
					//ydavila Ticket 001-00-003023
					//BaseStructForm bean = new BaseStructForm(id, name,
					//		IdNodeParent, NameIcon);					
					BaseStructForm bean = new BaseStructForm(id, name,
							IdNodeParent, NameIcon, respsolcambio, respsolelimin, respsolimpres);
					bean.setOwnerStruct(isOwner);
					bean.setNodeType(nodeType);
					bean.setOwner(owner);
					bean.setDateCreation(ToolsHTML.formatDateShow(dateCreation,
							true));
					bean.setRout(getRout(name, IdNodeParent, bean.getIdNode()));
					bean.setDescription(description);
					bean.setChargeOwner(properties.getProperty("cargo"));
					loadRestData(bean);
					resp.add(bean);
				}
			}
		}
		return resp;
	}

	/**
	 * 
	 * @param node
	 * @param nodeRelated
	 * @return
	 */
	public static boolean isNodeRelated(String node, String nodeRelated) {
		boolean resp = false;
		try {
			while (ToolsHTML.checkValue(nodeRelated)) {
				NodesTree bean = HandlerStruct.getNameNode(nodeRelated);
				if (bean == null) {
					nodeRelated = null;
					break;
				} else {
					nodeRelated = bean.getNodeFather();
					if (node.equalsIgnoreCase(nodeRelated)) {
						resp = true;
						break;
					}
				}
				log.debug("nodeRelated = " + nodeRelated);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("resp = " + resp);
		return resp;
	}

	/**
	 * 
	 * @param tree
	 * @param nodeActive
	 * @return
	 */
	public static String getIdLocationToNode(Hashtable tree, String nodeActive) {
		BaseStructForm localidad = null;
		if (tree != null && nodeActive != null) {
			localidad = tree.get(nodeActive) != null ? (BaseStructForm) tree
					.get(nodeActive) : null;
		}
		String resp = "1";
		if (localidad != null) {
			String idNode = localidad.getIdNodeParent();
			String locationType = DesigeConf.getProperty("locationType");
			if (locationType.equalsIgnoreCase(localidad.getNodeType())) {
				return localidad.getIdNode();
			} else {
				while (idNode != null && !"0".equalsIgnoreCase(idNode)) {
					localidad = (BaseStructForm) tree.get(idNode);
					if (localidad != null
							&& locationType.equalsIgnoreCase(localidad
									.getNodeType())) {
						resp = localidad.getIdNode();
						break;
					}
					resp = idNode;
					if (localidad != null) {
						idNode = localidad.getIdNodeParent();
					} else {
						break;
					}
				}
			}
		} else {
			log.debug("localidad is NULL to Node " + nodeActive);
			log.debug("tree: " + tree);
		}
		return resp;
	}

	/**
	 * 
	 * @param tree
	 * @param nodeActive
	 * @return
	 */
	public static BaseStructForm getLocationFromNode(Hashtable tree,
			String nodeActive) {
		BaseStructForm localidad = null;
		if (tree != null && nodeActive != null) {
			localidad = tree.get(nodeActive) != null ? (BaseStructForm) tree
					.get(nodeActive) : null;
		}
		String idNode = "";
		String resp = "";
		if (localidad != null) {
			idNode = localidad.getIdNodeParent();
			String locationType = DesigeConf.getProperty("locationType");
			if (locationType.equalsIgnoreCase(localidad.getNodeType())) {
				return localidad;
			} else {
				while (idNode != null && !"0".equalsIgnoreCase(idNode)) {
					localidad = (BaseStructForm) tree.get(idNode);
					if (localidad != null
							&& locationType.equalsIgnoreCase(localidad
									.getNodeType())) {
						resp = localidad.getIdNode();
						break;
					}
					resp = idNode;
					if (localidad != null) {
						idNode = localidad.getIdNodeParent();
					} else {
						break;
					}
				}
			}
		} else {
			log.debug("localidad is NULL to Node " + nodeActive);
			log.debug("tree: " + tree);
		}
		return localidad;
	}

	public static String getParentPrefix(Hashtable tree, String nodeActive,
			boolean asc) {
		BaseStructForm localidad = (BaseStructForm) tree.get(nodeActive);
		String idNode = "";
		Vector prefijos = new Vector();
		StringBuilder result = new StringBuilder(50);
		if (localidad != null) {
			idNode = localidad.getIdNodeParent();
			String locationType = DesigeConf.getProperty("locationType");
			if (locationType.equalsIgnoreCase(localidad.getNodeType())) {
				return "";
			} else {
				while (idNode != null && !"0".equalsIgnoreCase(idNode)) {
					localidad = (BaseStructForm) tree.get(idNode);
					if (localidad != null
							&& locationType.equalsIgnoreCase(localidad
									.getNodeType())) {
						break;
					}
					if (localidad != null) {
						if (localidad.getHeredarPrefijo() == Constants.permission) {
							prefijos.add(localidad.getPrefix());
							idNode = localidad.getIdNodeParent();
						} else {
							prefijos.add(localidad.getPrefix());
							break;
						}
					} else {
						break;
					}
				}
			}
		}
		if (asc) {
			for (int row = 0; row < prefijos.size(); row++) {
				result.append((String) prefijos.elementAt(row));
			}
		} else {
			for (int row = prefijos.size() - 1; row >= 0; row--) {
				result.append((String) prefijos.elementAt(row));
			}
		}
		return result.toString();
	}

	public String getParentPrefixinst(Hashtable tree, String nodeActive,
			boolean asc) {
		BaseStructForm localidad = (BaseStructForm) tree.get(nodeActive);
		String idNode = "";
		Vector prefijos = new Vector();
		StringBuilder result = new StringBuilder(50);
		if (localidad != null) {
			idNode = localidad.getIdNodeParent();
			String locationType = DesigeConf.getProperty("locationType");
			if (locationType.equalsIgnoreCase(localidad.getNodeType())) {
				return "";
			} else {
				while (idNode != null && !"0".equalsIgnoreCase(idNode)) {
					localidad = (BaseStructForm) tree.get(idNode);
					if (localidad != null
							&& locationType.equalsIgnoreCase(localidad
									.getNodeType())) {
						break;
					}
					if (localidad != null) {
						// System.out.println("[HandlerStruct] HeredarPrefijo = "
						// + localidad.getHeredarPrefijo());
						// System.out.println("[HandlerStruct] Name = " +
						// localidad.getName());
						// System.out.println("[HandlerStruct] Prefix = " +
						// localidad.getPrefix());
						// Si se Hereda el Prefijo se añade el Mismo...
						if (localidad.getHeredarPrefijo() == Constants.permission) {
							prefijos.add(localidad.getPrefix());
							idNode = localidad.getIdNodeParent();
						} else {
							prefijos.add(localidad.getPrefix());
							// System.out.println("prefijos = " + prefijos);
							// if (prefijos.size()> 0) {
							// prefijos.remove(prefijos.size() - 1);
							// }
							break;
						}
					} else {
						break;
					}
				}
			}
		}
		if (asc) {
			for (int row = 0; row < prefijos.size(); row++) {
				result.append((String) prefijos.elementAt(row));
			}
		} else {
			for (int row = prefijos.size() - 1; row >= 0; row--) {
				result.append((String) prefijos.elementAt(row));
			}
		}
		return result.toString();
	}

	public static String getIdLocation(String idNode) throws Exception {
		String base = "SELECT Name,IdNodeParent FROM struct WHERE IdNode = ";
		String resp = null;
		String idNodeAnt = null;
		while (!idNode.equalsIgnoreCase("0")) {
			String query = base + idNode;
			Properties prop = JDBCUtil.doQueryOneRow(query,Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				idNodeAnt = idNode;
				idNode = prop.getProperty("IdNodeParent");
				if (!idNode.equalsIgnoreCase("0")) {
					resp = idNodeAnt;
				}
			}
		}
		return resp;
	}

	public static String getAllNodesXNodeParent(String idNodeParent)
			throws Exception {
		Connection con = null;
		String retorno = null;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			retorno = getAllNodesXNodeParent(con, idNodeParent);
		} finally {
			JDBCUtil.closeConnection(con, "Error en getAllNodesXNodeParent()");
		}
		return retorno;
	}

	public static String getAllNodesXNodeParent(Connection con,
			String idNodeParent) throws Exception {
		String base = "SELECT idNode FROM struct WHERE cast(idNodeParent as int) in (";
		StringBuilder resp = new StringBuilder();
		String lista = idNodeParent;
		if (!ToolsHTML.isEmptyOrNull(idNodeParent)) {
			PreparedStatement st;
			String query = base + idNodeParent + ")";
			try {
				st = con.prepareStatement(query);
				ResultSet rs = null;
				rs = st.executeQuery();

				while (rs.next()) {
					resp.append(",").append(rs.getString("idnode"));
				}
				rs.close();
				st.close();
				if (resp != null && resp.length() > 1) {
					// lista += resp.toString();
					lista += ","
							+ getAllNodesXNodeParent(con, resp.toString()
									.substring(1, resp.toString().length()));
				}
			} catch (Exception e) {
				// System.out.println("Error en getAllNodesXNodeParent: " + e);
				e.printStackTrace();
			}
		}
		return lista;
	}

	/**
	 * 
	 * @param name
	 * @param IdNodeParent
	 * @param idNode
	 * @return
	 */
	public static String getRout(String name, String IdNodeParent, String idNode) {
		return getRout(name, IdNodeParent, idNode, null, null);
	}

	/**
	 * 
	 * @param name
	 * @param IdNodeParent
	 * @param idNode
	 * @param nodos
	 * @param nodesTree
	 * @return
	 */
	public static String getRout(String name, String IdNodeParent,
			String idNode, Hashtable nodos,
			Hashtable<String, NodesTree> nodesTree) {
		return getRout(null, name, IdNodeParent, idNode, nodos, nodesTree);
	}

	/**
	 * 
	 * @param con
	 * @param name
	 * @param IdNodeParent
	 * @param idNode
	 * @param nodos
	 * @param nodesTree
	 * @return
	 */
	public static String getRout(Connection con, String name,
			String IdNodeParent, String idNode, Hashtable nodos,
			Hashtable<String, NodesTree> nodesTree) {
		if (nodos == null) {
			nodos = getNameNodes(con);
		}

		StringBuilder p = new StringBuilder(
				"<a href=\"javascript:showSelect('"
						+ idNode
						+ "')\" class=\"ahref_b\" onmouseover=\"javascript:showStatus(0);return true;\" onmouseout=\"showStatus(1);return true;\">"
						+ name + "</a>");

		try {
			boolean contains = false;
			int cont = 0;
			NodesTree bean = null;
			while (ToolsHTML.checkValue(IdNodeParent)) {
				IdNodeParent = IdNodeParent.trim();
				cont++;
				if (cont == 10) {
					break;
				}
				contains = nodos.containsKey(IdNodeParent.trim());
				if (!contains) {
					if (nodesTree == null) {
						bean = HandlerStruct.getNameNode(con, IdNodeParent);
					} else {
						if (nodesTree.containsKey(IdNodeParent)) {
							bean = nodesTree.get(IdNodeParent);
							bean = (bean.getIdNode() == null ? null : bean);
						} else {
							bean = HandlerStruct.getNameNode(con, IdNodeParent);
							nodesTree.put(IdNodeParent,
									bean == null ? new NodesTree() : bean);
						}

					}
					if (bean != null) {
						nodos.put(bean.getIdNode().trim(), bean);
					}
				} else {
					bean = (NodesTree) nodos.get(IdNodeParent);
				}
				if (bean == null) {
					IdNodeParent = null;
					break;
				}
				IdNodeParent = bean.getNodeFather();
				p.insert(0, "&nbsp;" + File.separator + "&nbsp;");
				p.insert(
						0,
						"<a href=\"javascript:showSelect('"
								+ bean.getIdNode()
								+ "')\" class=\"ahref_b\" onmouseover=\"javascript:showStatus(0);return true;\" onmouseout=\"showStatus(1);return true;\">"
								+ bean.getNameNode() + "</a>");
				// p.insert(0,
				// "<a href=\"loadStructMain.do?target=&idNodeSelected=" +
				// bean.getIdNode() + "\">" + bean.getNameNode() + "</a>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return p.toString();
	}

	public static Hashtable loadAllNodes(Hashtable security, String userOwner,
			String idGroup) throws Exception {
		Hashtable resp = new Hashtable();
		Hashtable subNodos = new Hashtable();
		StringBuilder sql = new StringBuilder(
				"SELECT s.IdNode,s.[Name],s.IdNodeParent,s.NameIcon,s.NodeType,s.DateCreation,s.Owner");
		sql.append(" ,s.Description,s.toImpresion,s.heredarPrefijo,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		
		}
		sql.append("c.cargo ");
		sql.append(" FROM struct s,person p,tbl_cargo c,tbl_area a");
		sql.append(" WHERE s.Owner = p.nameUser");
		sql.append(" AND c.idcargo=cast(p.cargo as int) and c.idarea=a.idarea");
		sql.append(" ORDER BY IdNodeParent");
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		PermissionUserForm forma = null;
		boolean isAdmon = idGroup.equalsIgnoreCase(DesigeConf
				.getProperty("application.admon"));
		forma = null;
		String heredarPrefijo = null;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			String nodeType = properties.getProperty("NodeType");
			String id = properties.getProperty("IdNode");
			String IdNodeParent = properties.getProperty("IdNodeParent");
			boolean isSite = nodeType.equalsIgnoreCase(DesigeConf
					.getProperty("siteType"));
			if (security != null) {
				forma = (PermissionUserForm) security.get(id);
			}
			String own = properties.getProperty("Owner");
			boolean isOwner = userOwner.equalsIgnoreCase(own);
			if ((forma != null && forma.getToView() == Constants.permission)
					|| isAdmon || isSite) {
				String name = properties.getProperty("Name");
				String NameIcon = properties.getProperty("NameIcon");
				String owner = properties.getProperty("nombre");
				//ydavila Ticket 001-00-003023
				String respsolcambio = properties.getProperty("respsolcambio");
				String respsolelimin = properties.getProperty("respsolelimin");
				String respsolimpres = properties.getProperty("respsolimpres");
				
				String dateCreation = properties.getProperty("DateCreation");
				String description = properties.getProperty("Description");
				BaseStructForm bean = new BaseStructForm(id, name,
						IdNodeParent, NameIcon, respsolcambio, respsolelimin, respsolimpres);
				bean.setToImpresion(1);
				bean.setOwnerStruct(isOwner);
				bean.setNodeType(nodeType);
				bean.setOwnerStruct(own);
				bean.setOwner(owner);
				bean.setDateCreation(ToolsHTML.formatDateShow(dateCreation,
						true));
				bean.setRout(getRout(name, IdNodeParent, bean.getIdNode()));
				bean.setDescription(description);
				bean.setChargeOwner(properties.getProperty("cargo"));
				heredarPrefijo = properties.getProperty("heredarPrefijo");
				if (ToolsHTML.isNumeric(heredarPrefijo)) {
					bean.setHeredarPrefijo(Byte.parseByte(heredarPrefijo));
				}
				loadRestData(bean);
				resp.put(bean.getIdNode(), bean);
				// Se Guarda la Información del Nodo
				if (subNodos.containsKey(IdNodeParent)) {
					Vector childs = (Vector) subNodos.get(IdNodeParent);
					childs.add(id);
				} else {
					subNodos.put(IdNodeParent, new Vector());
				}
			}
		}
		return resp;
	}

	public Hashtable loadAllNodesinst(Hashtable security, String userOwner,
			String idGroup, Hashtable subNodos, String ids) throws Exception {
		Hashtable resp = new Hashtable();
		StringBuilder sql = new StringBuilder(
				"SELECT * FROM struct s,location l ");
		sql.append(" WHERE s.idNode = l.idNode AND nodeType = '1'");
		if (ids != null && ids.length() > 0) {
			sql.append(" AND s.IdNode IN (").append(ids).append(")");
		}
		sql.append(" ORDER BY IdNodeParent");
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		// Carga de Carpetas/Procesos
		sql = new StringBuilder(
				"SELECT s.IdNode,s.[Name],s.IdNodeParent,s.NameIcon,s.NodeType,s.DateCreation,s.Owner");
		sql.append(" ,s.Description,s.toImpresion,s.heredarPrefijo,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append(" (p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append(" (p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		
		}
		sql.append(" p.cargo,m.Prefix,m.parentPrefix"); // NEW
		sql.append(" FROM struct s,person p,module m");
		sql.append(" WHERE s.Owner = p.nameUser");
		sql.append("   AND s.idNode = m.idNode"); // NEW
		sql.append("   AND nodeType <> '1'  "); // NEW
		if (ids != null && ids.length() > 0) {
			sql.append(" AND s.IdNode IN (").append(ids).append(")");
		}
		sql.append(" ORDER BY IdNodeParent");
		datos.addAll(JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName()));
		PermissionUserForm forma = null;
		boolean isAdmon = idGroup.equalsIgnoreCase(DesigeConf
				.getProperty("application.admon"));
		forma = null;
		String heredarPrefijo = null;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			String nodeType = properties.getProperty("NodeType");
			String id = properties.getProperty("IdNode");
			String IdNodeParent = properties.getProperty("IdNodeParent");
			boolean isSite = nodeType.equalsIgnoreCase(DesigeConf
					.getProperty("siteType"));
			if (security != null) {
				forma = (PermissionUserForm) security.get(id);
			}
			String own = properties.getProperty("Owner");
			boolean isOwner = userOwner.equalsIgnoreCase(own);
			if ((forma != null && forma.getToView() == Constants.permission)
					|| isAdmon || isSite) {
				String name = properties.getProperty("Name");
				String NameIcon = properties.getProperty("NameIcon");
				String owner = properties.getProperty("nombre");
				//ydavila Ticket 001-00-003023
				String respsolcambio = properties.getProperty("respsolcambio");
				String respsolelimin = properties.getProperty("respsolelimin");
				String respsolimpres = properties.getProperty("respsolimpres");
				
				String dateCreation = properties.getProperty("DateCreation");
				String description = properties.getProperty("Description");
				BaseStructForm bean = new BaseStructForm(id, name,
						IdNodeParent, NameIcon, respsolcambio, respsolelimin, respsolimpres);
				bean.setToImpresion(1);
				bean.setOwnerStruct(isOwner);
				bean.setNodeType(nodeType);
				bean.setOwnerStruct(own);
				bean.setOwner(owner);
				bean.setDateCreation(ToolsHTML.formatDateShow(dateCreation,
						true));
				bean.setRout(getRout(name, IdNodeParent, bean.getIdNode()));
				bean.setDescription(description);
				bean.setChargeOwner(properties.getProperty("cargo"));
				heredarPrefijo = properties.getProperty("heredarPrefijo");
				if (ToolsHTML.isNumeric(heredarPrefijo)) {
					bean.setHeredarPrefijo(Byte.parseByte(heredarPrefijo));
				}
				loadRestData(bean, properties);
				resp.put(bean.getIdNode(), bean);
				// Se Guarda la Información del Nodo
				if (subNodos != null) {
					if (subNodos.containsKey(IdNodeParent)) {
						Vector childs = (Vector) subNodos.get(IdNodeParent);
						childs.add(id);
						subNodos.put(IdNodeParent, childs);
					} else {
						Vector childs = new Vector();
						childs.add(id);
						subNodos.put(IdNodeParent, childs);
					}
				}
			}
		}
		return resp;
	}

	/**
	 * 
	 * @param security
	 * @param userOwner
	 * @param idGroup
	 * @param subNodos
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public static Hashtable loadAllNodes(Hashtable security, String userOwner,
			String idGroup, Hashtable subNodos, String ids) throws Exception {
		return loadAllNodes(null, security, userOwner, idGroup, subNodos, ids);
	}

	/**
	 * 
	 * @param con
	 * @param security
	 * @param userOwner
	 * @param idGroup
	 * @param subNodos
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public static Hashtable loadAllNodes(Connection con, Hashtable security,
			String userOwner, String idGroup, Hashtable subNodos, String ids)
			throws Exception {
		String typeOrden = String.valueOf(HandlerParameters.PARAMETROS.getTypeOrder());
		Hashtable resp = new Hashtable();
		StringBuilder sql = new StringBuilder(
				"SELECT * FROM struct s,location l ")
				.append(" WHERE s.idNode = l.idNode AND nodeType = '1'");
		if (ids != null && ids.length() > 0) {
			sql.append(" AND s.IdNode IN (").append(ids).append(")");
		}
		sql.append(" ORDER BY IdNodeParent");
		// Ordenar por Fecha de Creación
		if (Constants.notPermissionSt.compareTo(typeOrden) == 0) {
			sql.append(",s.IdNode");
		} else {
			// Ordenar Alfabeticamente
			sql.append(",lower(s.name)");
		}
		Vector datos = JDBCUtil.doQueryVector(con, sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		// Carga de Carpetas/Procesos
		sql = new StringBuilder(
				"SELECT s.IdNode,s.Name,s.IdNodeParent,s.NameIcon,s.NodeType,s.DateCreation,s.Owner")
				.append(" ,s.Description,s.toImpresion,s.heredarPrefijo,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.Nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.Nombres) AS nombre, ");
			break;
		
		}
		sql.append("p.cargo ")
				.append(" ,m.Prefix,m.parentPrefix")
				// NEW
				.append(" FROM struct s,person p,module m")
				.append(" WHERE s.Owner = p.nameUser")
				.append("   AND s.idNode = m.idNode")
				// NEW
				.append("   AND nodeType <> '1'  ")
				// NEW
				.append("   AND p.accountActive = '")
				.append(Constants.permission).append("'");
		if (ids != null && ids.length() > 0) {
			sql.append(" AND s.IdNode IN (").append(ids).append(")");
		}
		sql.append(" ORDER BY IdNodeParent");
		if (Constants.notPermissionSt.compareTo(typeOrden) == 0) {
			sql.append(",s.IdNode");
		} else {
			// Ordenar Alfabeticamente
			sql.append(",lower(s.name)");
		}
		// System.out.println(sql.toString());
		datos.addAll(JDBCUtil.doQueryVector(con, sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName()));
		PermissionUserForm forma = null;
		boolean isAdmon = idGroup.equalsIgnoreCase(DesigeConf
				.getProperty("application.admon"));
		forma = null;
		String heredarPrefijo = null;
		Hashtable nodos = getNameNodes(con); // hace una precarga de los nodos
		Hashtable<String, NodesTree> nodesTree = new Hashtable<String, NodesTree>();
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			String nodeType = properties.getProperty("NodeType");
			String id = properties.getProperty("IdNode");
			String IdNodeParent = properties.getProperty("IdNodeParent");
			boolean isSite = nodeType.equalsIgnoreCase(DesigeConf
					.getProperty("siteType"));
			if (security != null) {
				forma = (PermissionUserForm) security.get(id);
			}
			String own = properties.getProperty("Owner");
			boolean isOwner = false;
			if (userOwner != null) {
				isOwner = userOwner.equalsIgnoreCase(own);
			}
			if ((forma != null && forma.getToView() == Constants.permission)
					|| isAdmon || isSite) {
				String name = properties.getProperty("Name");
				String NameIcon = properties.getProperty("NameIcon");
				String owner = properties.getProperty("nombre");
				//ydavila Ticket 001-00-003023
				String respsolcambio = properties.getProperty("respsolcambio");
				String respsolelimin = properties.getProperty("respsolelimin");
				String respsolimpres = properties.getProperty("respsolimpres");
				
				String dateCreation = properties.getProperty("DateCreation");
				String description = properties.getProperty("Description");
				BaseStructForm bean = new BaseStructForm(id, name,
						IdNodeParent, NameIcon, respsolcambio, respsolelimin, respsolimpres);
				bean.setToImpresion(1);
				bean.setOwnerStruct(isOwner);
				bean.setNodeType(nodeType);
				bean.setOwnerStruct(own);
				bean.setOwner(owner);
				bean.setDateCreation(ToolsHTML.formatDateShow(dateCreation,
						true));
				bean.setRout(getRout(con, name, IdNodeParent, bean.getIdNode(),
						nodos, nodesTree));
				bean.setDescription(description);
				bean.setChargeOwner(properties.getProperty("cargo"));
				heredarPrefijo = properties.getProperty("heredarPrefijo");
				if (ToolsHTML.isNumeric(heredarPrefijo)) {
					bean.setHeredarPrefijo(Byte.parseByte(heredarPrefijo));
				}
				loadRestData(bean, properties);
				resp.put(bean.getIdNode().trim(), bean);
				// Se Guarda la Información del Nodo
				if (subNodos != null) {
					if (subNodos.containsKey(IdNodeParent)) {
						Vector childs = (Vector) subNodos.get(IdNodeParent);
						childs.add(id);
						subNodos.put(IdNodeParent, childs);
					} else {
						Vector childs = new Vector();
						childs.add(id);
						subNodos.put(IdNodeParent, childs);
					}
				}
			}
		}
		return resp;
	}

	public static BaseStructForm loadStruct(String idStruct)
			throws ApplicationExceptionChecked, Exception {
		StringBuilder sql = new StringBuilder("SELECT s.*,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.Nombres) AS nombre ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.Nombres) AS nombre ");
			break;
		
		}
		sql.append("FROM struct s,person p ");
		sql.append("WHERE s.Owner = p.nameUser AND idNode = ").append(
				ToolsHTML.isEmptyOrNull(idStruct) ? "'1'" : idStruct);
		// System.out.println("[loadStruct]" + sql.toString());
		BaseStructForm bean = null;
		Properties properties = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (properties.getProperty("isEmpty").equalsIgnoreCase("false")) {
			String id = properties.getProperty("IdNode");
			String name = properties.getProperty("Name");
			//ydavila Ticket 001-00-003023
			String respsolcambio = properties.getProperty("respsolcambio");
			String respsolelimin = properties.getProperty("respsolelimin");
			String respsolimpres = properties.getProperty("respsolimpres");
			
			String IdNodeParent = properties.getProperty("IdNodeParent");
			String NameIcon = properties.getProperty("NameIcon");
			bean = new BaseStructForm(id, name, IdNodeParent, NameIcon, respsolcambio, respsolelimin, respsolimpres);
			
			bean.setNodeType(properties.getProperty("NodeType"));
			bean.setOwner(properties.getProperty("nombre"));
			//ydavila Ticket 001-00-003023
			bean.setrespsolcambio(properties.getProperty("respsolcambio"));
			bean.setrespsolelimin(properties.getProperty("respsolelimin"));
			bean.setrespsolimpres(properties.getProperty("respsolimpres"));
			
			bean.setDateCreation(ToolsHTML.formatDateShow(
					properties.getProperty("DateCreation"), true));
			bean.setRout(getRout(name, IdNodeParent, bean.getIdNode()));
			bean.setDescription(properties.getProperty("Description"));
			loadRestData(bean);
		}
		return bean;
	}

	public static void load(String owner, BaseStructForm forma)
			throws Exception {
		forma.clearForm();
		StringBuilder sql = new StringBuilder(2048);
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("SELECT s.*,(p.Apellidos + ' ' + p.Nombres) AS nombre ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("SELECT s.*,(p.Apellidos || ' ' || p.Nombres) AS nombre ");
			break;

		}
		sql.append(
				",c.cargo FROM struct s,person p,tbl_cargo c,tbl_area a WHERE IdNode = ")
				.append(forma.getIdNode())
				.append(" AND s.Owner = p.nameUser")
				.append(" AND c.idcargo=cast(p.cargo as int) and c.idarea=a.idarea");
		log.debug("[Load] = " + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ((prop != null)
				&& prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setName(prop.getProperty("Name"));
			forma.setIdNodeParent(prop.getProperty("IdNodeParent"));
			forma.setNameIcon(prop.getProperty("NameIcon"));
			forma.setNodeType(prop.getProperty("NodeType"));
			forma.setOwner(prop.getProperty("nombre"));
			//ydavila Ticket 001-00-003023
			forma.setrespsolcambio(prop.getProperty("respsolcambio"));
			forma.setrespsolelimin(prop.getProperty("respsolelimin"));
			forma.setrespsolimpres(prop.getProperty("respsolimpres"));
			
			String dateCreation = ToolsHTML.formatDateShow(
					prop.getProperty("DateCreation"), true);
			forma.setDateCreation(dateCreation);
			forma.setRout(getRout(forma.getName(), forma.getIdNodeParent(),
					forma.getIdNode()));
			forma.setDescription(prop.getProperty("Description"));
			forma.setChargeOwner(prop.getProperty("cargo"));
			String heredarPrefijo = prop.getProperty("heredarPrefijo");
			if (ToolsHTML.isNumeric(heredarPrefijo)) {
				forma.setHeredarPrefijo(Byte.parseByte(heredarPrefijo));
			}
			if (forma.getOwner().equalsIgnoreCase(owner)) {
				forma.setOwnerStruct(true);
			} else {
				forma.setOwnerStruct(false);
			}
			forma.setToListDist(ToolsHTML.parseInt(prop
					.getProperty("toListDist")));

			if (forma.getToListDist() == 1) {
				forma.setToListDistNameUser(getListDistNameUser(forma
						.getIdNode()));
			} else {
				// buscamos si tiene una lista heredada
				int idNodeParentToList = findIdNodeParentToListDist(ToolsHTML
						.parseInt(forma.getIdNodeParent()));
				if (idNodeParentToList > 0) {
					forma.setToListDistNameUser(getListDistNameUser(String
							.valueOf(idNodeParentToList)));
				}
			}

			loadRestData(forma);
		}
	}

	/**
	 * 
	 * @param idNode
	 * @return
	 * @throws Exception
	 */
	public static String getListDistNameUser(String idNode) throws Exception {
		// cargaremos la lista de distribucion de esta carpeta
		StringBuilder query = new StringBuilder(
				"SELECT nameUser FROM	listDist WHERE idNode=").append(idNode);
		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		query.setLength(0);
		String sep = "";
		while (crs.next()) {
			query.append(sep).append(crs.getString(1));
			sep = Constants.COMA;
		}
		return query.toString();
	}

	/**
	 * 
	 * @param idNode
	 * @return
	 * @throws Exception
	 */
	public static int findIdNodeParentToListDist(int idNode) throws Exception {
		if (idNode > 1) {
			StringBuilder query = new StringBuilder(
					"SELECT idNode,idNodeParent,toListDist from struct");
			CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
			HashMap<Integer, NodesTree> nodes = new HashMap<Integer, NodesTree>();
			while (crs.next()) {
				nodes.put(
						crs.getInt("idNode"),
						new NodesTree(crs.getString("idNode"), "", crs
								.getString("idNodeParent"), crs
								.getString("toListDist")));
			}
			NodesTree node = null;

			do {
				node = (NodesTree) nodes.get(idNode);
				if ("1".equals(node.getToListDist())) {
					return idNode;
				}
				idNode = ToolsHTML.parseInt(node.getNodeFather());
			} while (idNode != 1);
		}
		return 0;
	}

	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	private static void loadRestData(BaseStructForm forma) throws Exception {
		int nodeType = Integer.parseInt(forma.getNodeType());
		StringBuilder sql = new StringBuilder(2048);
		if (nodeType == 1) {
			sql.append("SELECT * FROM ").append(tableLocation)
					.append(" WHERE IdNode = ").append(forma.getIdNode());
			Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				String number = prop.getProperty("Number");
				if (ToolsHTML.isNumeric(number)) {
					forma.setNumber(Integer.parseInt(number.trim()));
				}
				forma.setMajorId(Byte.parseByte(prop.getProperty("MajorID")));
				forma.setMajorKeep(prop.getProperty("HistAprob"));
				forma.setMinorId(Byte.parseByte(prop.getProperty("MinorID")));
				String histRev = prop.getProperty("HistRev");
				if (!ToolsHTML.isEmptyOrNull(histRev)) {
					forma.setMinorKeep(histRev);
				} else {
					forma.setMinorKeep("");
				}

				forma.setDisableUserWF(Byte.parseByte(prop
						.getProperty("AllowUserWF")));
				forma.setCopy(ToolsHTML.parseByte(prop.getProperty("copy"),
						(byte) 1));
				forma.setSequential(Byte.parseByte(prop
						.getProperty("Secuential")));
				forma.setConditional(Byte.parseByte(prop
						.getProperty("Conditional")));
				forma.setTypePrefix(Byte.parseByte(prop
						.getProperty("typePrefix")));
				forma.setNotify(Byte.parseByte(prop
						.getProperty("AutomaticNotified")));
				String days = prop.getProperty("Days");
				if (ToolsHTML.isNumeric(days)) {
					forma.setExpireWF(Byte.parseByte(days.trim()));
				}
				String numberDays = prop.getProperty("NumberDays");
				if (ToolsHTML.isNumeric(numberDays)) {
					forma.setDaysWF(numberDays.trim());
				} else {
					forma.setDaysWF("");
				}
				forma.setShowCharge(Integer.parseInt(prop
						.getProperty("showCharge")));

				int timeDocVenc = Integer.parseInt(prop
						.getProperty("timeDocVenc"));
				String txttimeDocVenc = prop.getProperty("txttimeDocVenc");
				if (!ToolsHTML.isEmptyOrNull(String.valueOf(timeDocVenc))) {
					if (ToolsHTML.isNumeric(String.valueOf(timeDocVenc))) {
						forma.setTimeDocVenc(timeDocVenc);
						forma.setTxttimeDocVenc(txttimeDocVenc);
					} else {
						forma.setTimeDocVenc(Integer.parseInt("0"));
						forma.setTxttimeDocVenc("");
					}
				}
				forma.setCheckvijenToprint(prop
						.getProperty("checkvijenToprint") != null ? Byte
						.parseByte(prop.getProperty("checkvijenToprint")) : 0);
				int vijenToprint;
				if (ToolsHTML.isEmptyOrNull(prop.getProperty("vijenToprint"))) {
					vijenToprint = 0;
				} else {
					vijenToprint = Integer.parseInt(prop
							.getProperty("vijenToprint"));
				}
				forma.setVijenToprint(vijenToprint);
				String checkborradorCorrelativo = prop
						.getProperty("checkborradorCorrelativo");
				if (!ToolsHTML.isEmptyOrNull(checkborradorCorrelativo)) {
					forma.setCheckborradorCorrelativo(checkborradorCorrelativo != null ? Byte
							.parseByte(checkborradorCorrelativo) : 0);
				}
				String cantExpDoc = prop.getProperty("cantExpDoc");
				forma.setCantExpDoc("7");
				if (!ToolsHTML.isEmptyOrNull(cantExpDoc)) {
					forma.setCantExpDoc(cantExpDoc.trim());
				}
				forma.setUnitExpDoc(Constants.days);
				String unitExpDoc = prop.getProperty("unitExpDoc");
				if (!ToolsHTML.isEmptyOrNull(unitExpDoc)) {
					forma.setUnitExpDoc(unitExpDoc.trim());
				}
				// Carga del Parámetro que indica si se hereda o no el Prefijo
				// de la Carpeta
				// unitExpDoc = prop.getProperty("heredarPrefijo");
				// //System.out.println("unitExpDoc = " + unitExpDoc);
				// if (ToolsHTML.isNumeric(unitExpDoc)) {
				// forma.setHeredarPrefijo(Byte.parseByte(unitExpDoc.trim()));
				// }
			}
		} else {
			sql.append("SELECT * FROM ").append(tableModules)
					.append(" WHERE IdNode = ").append(forma.getIdNode());
			Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				forma.setPrefix(prop.getProperty("Prefix"));
				forma.setParentPrefix(prop.getProperty("parentPrefix"));
			}
		}
	}

	/**
	 * 
	 * @param forma
	 * @param prop
	 * @throws Exception
	 */
	private static void loadRestData(BaseStructForm forma, Properties prop)
			throws Exception {
		int nodeType = Integer.parseInt(forma.getNodeType());
		// StringBuilder sql = new StringBuilder("");
		if (nodeType == 1) {
			// sql.append("SELECT * FROM ").append(tableLocation).append(" WHERE
			// IdNode =
			// ").append(forma.getIdNode());
			// if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			String number = prop.getProperty("Number");
			if (ToolsHTML.isNumeric(number)) {
				forma.setNumber(Integer.parseInt(number.trim()));
			}
			forma.setMajorId(Byte.parseByte(prop.getProperty("MajorID")));
			forma.setMajorKeep(prop.getProperty("HistAprob"));
			forma.setMinorId(Byte.parseByte(prop.getProperty("MinorID")));
			String histRev = prop.getProperty("HistRev");
			if (!ToolsHTML.isEmptyOrNull(histRev)) {
				forma.setMinorKeep(histRev);
			} else {
				forma.setMinorKeep("");
			}

			forma.setDisableUserWF(Byte.parseByte(prop
					.getProperty("AllowUserWF")));
			forma.setSequential(Byte.parseByte(prop.getProperty("Secuential")));
			forma.setConditional(Byte.parseByte(prop.getProperty("Conditional")));
			forma.setTypePrefix(Byte.parseByte(prop.getProperty("typePrefix")));
			forma.setNotify(Byte.parseByte(prop
					.getProperty("AutomaticNotified")));
			String days = prop.getProperty("Days");
			if (ToolsHTML.isNumeric(days)) {
				forma.setExpireWF(Byte.parseByte(days.trim()));
			}
			String numberDays = prop.getProperty("NumberDays");
			if (ToolsHTML.isNumeric(numberDays)) {
				forma.setDaysWF(numberDays.trim());
			} else {
				forma.setDaysWF("");
			}
			forma.setShowCharge(Integer.parseInt(prop.getProperty("showCharge")));

			int timeDocVenc = Integer.parseInt(prop.getProperty("timeDocVenc"));
			String txttimeDocVenc = prop.getProperty("txttimeDocVenc");
			if (!ToolsHTML.isEmptyOrNull(String.valueOf(timeDocVenc))) {
				if (ToolsHTML.isNumeric(String.valueOf(timeDocVenc))) {
					forma.setTimeDocVenc(timeDocVenc);
					forma.setTxttimeDocVenc(txttimeDocVenc);
				} else {
					forma.setTimeDocVenc(Integer.parseInt("0"));
					forma.setTxttimeDocVenc("");
				}
			}
			forma.setCheckvijenToprint(prop.getProperty("checkvijenToprint") != null ? Byte
					.parseByte(prop.getProperty("checkvijenToprint")) : 0);
			int vijenToprint;
			if (ToolsHTML.isEmptyOrNull(prop.getProperty("vijenToprint"))) {
				vijenToprint = 0;
			} else {
				vijenToprint = Integer.parseInt(prop
						.getProperty("vijenToprint"));
			}
			forma.setVijenToprint(vijenToprint);
			String checkborradorCorrelativo = prop
					.getProperty("checkborradorCorrelativo");
			if (!ToolsHTML.isEmptyOrNull(checkborradorCorrelativo)) {
				forma.setCheckborradorCorrelativo(checkborradorCorrelativo != null ? Byte
						.parseByte(checkborradorCorrelativo) : 0);
			}
			String cantExpDoc = prop.getProperty("cantExpDoc");
			forma.setCantExpDoc("7");
			if (!ToolsHTML.isEmptyOrNull(cantExpDoc)) {
				forma.setCantExpDoc(cantExpDoc.trim());
			}
			forma.setUnitExpDoc(Constants.days);
			String unitExpDoc = prop.getProperty("unitExpDoc");
			if (!ToolsHTML.isEmptyOrNull(unitExpDoc)) {
				forma.setUnitExpDoc(unitExpDoc.trim());
			}
			// }
		} else {
			// sql.append("SELECT * FROM ").append(tableModules).append(" WHERE
			// IdNode =
			// ").append(forma.getIdNode());
			// Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			// if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setPrefix(prop.getProperty("Prefix"));
			forma.setParentPrefix(prop.getProperty("parentPrefix"));
			// }
		}
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	private static String getTableName(int type) {
		return (type == 1) ? tableLocation : tableModules;
	}

	/**
	 * 
	 * @param table
	 * @param id
	 * @return
	 * @throws Exception
	 */
	private static boolean checkExist(String table, String id) throws Exception {
		StringBuilder query = new StringBuilder("SELECT IdNode FROM ")
				.append(table).append(" WHERE IdNode = ").append(id);
		Properties prop = JDBCUtil.doQueryOneRow(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		return (prop.getProperty("isEmpty").equalsIgnoreCase("false"));
	}

	/**
	 * 
	 * @param table
	 * @param forma
	 * @param editNode
	 * @param con
	 * @throws Exception
	 */
	private static void updateTableAssociate(String table,
			BaseStructForm forma, boolean editNode, Connection con)
			throws Exception {
		PreparedStatement st = null;
		StringBuffer sql = new StringBuffer("");
		if (table.equalsIgnoreCase(tableModules)) {
			if (editNode) {
				// sql.append("UPDATE ").append(table).append(" SET Description
				// =
				// '").append(forma.getDescription());
				sql.append("UPDATE ").append(table)
						.append(" SET Description = ? ");
				// sql.append(", Prefix='").append(forma.getPrefix()).append("'
				// WHERE IdNode =
				// ").append(forma.getIdNode());
				sql.append(", Prefix=? WHERE IdNode = ").append(
						forma.getIdNode());
				st = con.prepareStatement(sql.toString());
				st.setString(1, forma.getDescription());
				st.setString(2, forma.getPrefix());
				st.executeUpdate();
			} else {
				sql.append("INSERT INTO ").append(table)
						.append("(IdNode,Prefix,parentPrefix) VALUES(?,?,?)");
				/*
				 * sql.append(forma.getIdNode()).append(",'"); b
				 * sql.append(forma.getPrefix()).append("','");
				 * sql.append(forma.getParentPrefix()).append("')");
				 */
				st = con.prepareStatement(sql.toString());
				st.setInt(1, Integer.parseInt(forma.getIdNode()));
				st.setString(2, forma.getPrefix());
				st.setString(3, forma.getParentPrefix());
				st.executeUpdate();
			}
		} else {
			if (editNode) {
				sql = new StringBuffer("UPDATE ").append(table).append(
						" SET MajorID = ? , MinorID = ? ");
				ToolsHTML.setFieldInQueryEdit(sql,
						new Integer(forma.getNumber()), "Number");
				ToolsHTML.setFieldInQueryEdit(sql, forma.getMajorKeep(),
						"HistAprob");
				ToolsHTML.setFieldInQueryEdit(sql, forma.getMinorKeep(),
						"HistRev");
				ToolsHTML.setFieldInQueryEdit(sql,
						new Byte(forma.getDisableUserWF()), "AllowUserWF");
				ToolsHTML.setFieldInQueryEdit(sql, new Byte(forma.getCopy()),
						"copy");
				ToolsHTML.setFieldInQueryEdit(sql,
						new Byte(forma.getSequential()), "Secuential");
				ToolsHTML.setFieldInQueryEdit(sql,
						new Byte(forma.getConditional()), "Conditional");
				ToolsHTML.setFieldInQueryEdit(sql, new Byte(forma.getNotify()),
						"AutomaticNotified");
				ToolsHTML.setFieldInQueryEdit(sql,
						new Byte(forma.getExpireWF()), "Days");
				ToolsHTML.setFieldInQueryEdit(sql, forma.getDaysWF(),
						"NumberDays");
				ToolsHTML.setFieldInQueryEdit(sql,
						new Byte(String.valueOf(forma.getShowCharge())),
						"showCharge");
				// Heredar Prefijo 0=>NO 1=>SI
				// ToolsHTML.setFieldInQueryEdit(sql,new
				// Integer(forma.getHeredarPrefijo()),"heredarPrefijo");
				ToolsHTML.setFieldInQueryEdit(sql,
						new Byte(forma.getTypePrefix()), "typePrefix");
				// simon 2005/09/20 inicio
				// ToolsHTML.setFieldInQueryEdit(sql,new
				// Integer(forma.getDocInline()),"docInline");
				ToolsHTML.setFieldInQueryEdit(sql,
						new Byte(String.valueOf(forma.getTimeDocVenc())),
						"timeDocVenc");
				if (!ToolsHTML.isEmptyOrNull(forma.getTxttimeDocVenc())) {
					ToolsHTML.setFieldInQueryEdit(sql,
							forma.getTxttimeDocVenc(), "txttimeDocVenc");
				}
				// simon 2005/09/20 fin
				ToolsHTML.setFieldInQueryEdit(sql, forma.getCantExpDoc(),
						"cantExpDoc");
				ToolsHTML.setFieldInQueryEdit(sql, forma.getUnitExpDoc(),
						"unitExpDoc");

				ToolsHTML.setFieldInQueryEdit(sql,
						new Integer(forma.getVijenToprint()), "vijenToprint");
				ToolsHTML.setFieldInQueryEdit(sql,
						new Byte(forma.getCheckvijenToprint()),
						"checkvijenToprint");
				ToolsHTML.setFieldInQueryEdit(sql,
						new Byte(forma.getCheckborradorCorrelativo()),
						"checkborradorCorrelativo");
				sql.append(" WHERE IdNode = ").append(forma.getIdNode());
				st = con.prepareStatement(sql.toString());
				st.setInt(1, forma.getMajorId());
				st.setInt(2, forma.getMinorId());
				int pos = 3;
				pos = ToolsHTML.setValueInQuery(st,
						new Integer(forma.getNumber()), pos);
				pos = ToolsHTML.setValueInQuery(st, forma.getMajorKeep(), pos);
				pos = ToolsHTML.setValueInQuery(st, forma.getMinorKeep(), pos);
				pos = ToolsHTML.setValueInQuery(st,
						new Integer(forma.getDisableUserWF()), pos);
				pos = ToolsHTML.setValueInQuery(st,
						new Integer(forma.getCopy()), pos);
				pos = ToolsHTML.setValueInQuery(st,
						new Integer(forma.getSequential()), pos);
				pos = ToolsHTML.setValueInQuery(st,
						new Integer(forma.getConditional()), pos);
				pos = ToolsHTML.setValueInQuery(st,
						new Integer(forma.getNotify()), pos);
				pos = ToolsHTML.setValueInQuery(st,
						new Integer(forma.getExpireWF()), pos);
				pos = ToolsHTML.setValueInQuery(st, forma.getDaysWF(), pos);
				pos = ToolsHTML.setValueInQuery(st,
						new Integer(forma.getShowCharge()), pos);
				// Heredar Prefijo
				// pos = ToolsHTML.setValueInQuery(st,new
				// Integer(forma.getHeredarPrefijo()),pos);
				pos = ToolsHTML.setValueInQuery(st,
						new Integer(forma.getTypePrefix()), pos);
				// simon 2005/09/20 inicio
				// pos = ToolsHTML.setValueInQuery(st,new
				// Integer(forma.getDocInline()),pos);
				pos = ToolsHTML.setValueInQuery(st,
						new Integer(forma.getTimeDocVenc()), pos);
				if (!ToolsHTML.isEmptyOrNull(forma.getTxttimeDocVenc())) {
					pos = ToolsHTML.setValueInQuery(st,
							new Integer(forma.getTxttimeDocVenc()), pos);
				}
				pos = ToolsHTML.setValueInQuery(st, forma.getCantExpDoc(), pos);
				pos = ToolsHTML.setValueInQuery(st, forma.getUnitExpDoc(), pos);
				pos = ToolsHTML.setValueInQuery(st, forma.getVijenToprint(),
						pos);
				pos = ToolsHTML.setValueInQuery(st,
						forma.getCheckvijenToprint(), pos);
				pos = ToolsHTML.setValueInQuery(st,
						forma.getCheckborradorCorrelativo(), pos);
				st.executeUpdate();
			} else {
				StringBuffer fields = new StringBuffer(100);
				sql = new StringBuffer(100);
				// fields.append("?,?,?,?,?,?,?,?,?,?,?");
				fields.append("?,?,?,CAST(? AS bit),CAST(? AS bit),CAST(? AS bit),CAST(? AS bit),CAST(? AS bit),CAST(? AS bit),CAST(? AS bit),CAST(? AS bit),?");
				sql.append("INSERT INTO ")
						.append(table)
						.append(" (IdNode,MajorID,MinorID,AllowUserWF,copy,Secuential,");
				sql.append("Conditional,AutomaticNotified,showCharge,typePrefix,timeDocVenc,txttimeDocVenc");
				if (forma.getNumber() != 0) {
					ToolsHTML.setFieldInQuery(sql, fields,
							new Integer(forma.getNumber()), "Number");
				}
				ToolsHTML.setFieldInQuery(sql, fields, forma.getMajorKeep(),
						"HistAprob");
				ToolsHTML.setFieldInQuery(sql, fields, forma.getMinorKeep(),
						"HistRev");
				if (forma.getExpireWF() == 0) {
					ToolsHTML.setFieldInQuery(sql, fields,
							new Byte(forma.getExpireWF()), "Days");
					ToolsHTML.setFieldInQuery(sql, fields, forma.getDaysWF(),
							"NumberDays");
				}
				ToolsHTML.setFieldInQuery(sql, fields, forma.getCantExpDoc(),
						"cantExpDoc");
				ToolsHTML.setFieldInQuery(sql, fields, forma.getUnitExpDoc(),
						"unitExpDoc");
				// simon 2005/09/20 inicio
				ToolsHTML.setFieldInQuery(sql, fields, forma.getVijenToprint(),
						"vijenToprint");
				ToolsHTML.setFieldInQuery(sql, fields,
						forma.getCheckvijenToprint(), "checkvijenToprint");
				ToolsHTML.setFieldInQuery(sql, fields,
						forma.getCheckborradorCorrelativo(),
						"checkborradorCorrelativo");
				sql.append(") VALUES (").append(fields.toString()).append(")");

				st = con.prepareStatement(sql.toString());
				st.setInt(1, Integer.parseInt(forma.getIdNode()));
				st.setInt(2, forma.getMajorId());
				st.setInt(3, forma.getMinorId());
				st.setObject(4, Integer.parseInt(JDBCUtil.getByte(forma
						.getDisableUserWF())));
				st.setObject(5,
						Integer.parseInt(JDBCUtil.getByte(forma.getCopy())));
				st.setObject(6, Integer.parseInt(JDBCUtil.getByte(forma
						.getSequential())));

				st.setObject(7, Integer.parseInt(JDBCUtil.getByte(forma
						.getConditional())));
				st.setObject(8,
						Integer.parseInt(JDBCUtil.getByte(forma.getNotify())));
				st.setObject(9, forma.getShowCharge());

				st.setObject(10, Integer.parseInt(JDBCUtil.getByte(forma
						.getHeredarPrefijo())));

				// Luis Cisneros
				// 28/02/07
				// Si la Tabla es la de Location, debo colocar como noveno
				// parametro el Tipo de
				// prefijo.
				if (table.equals("location")) {
					st.setObject(10, Integer.parseInt(JDBCUtil.getByte(forma
							.getTypePrefix())));
				}
				// Fin 28/02/07

				// if (forma.getHeredarPrefijo()==Constants.permission) {
				// st.setObject(10,forma.getTypePrefix());
				// } else {
				// st.setObject(10,Constants.notPermission);
				// }

				// simon 2005/09/20 inicio
				if (!ToolsHTML.isEmptyOrNull(String.valueOf(forma
						.getTimeDocVenc()))) {
					st.setObject(11, forma.getTimeDocVenc());
				} else {
					st.setObject(11, 0);
				}
				if (!ToolsHTML.isEmptyOrNull(forma.getTxttimeDocVenc())) {
					st.setString(12, forma.getTxttimeDocVenc());
				} else {
					st.setString(12, "");
				}

				int pos = 13;
				// simon 2005/09/20 fin
				if (forma.getNumber() != 0) {
					pos = ToolsHTML.setValueInQuery(st,
							new Integer(forma.getNumber()), pos);
				}
				pos = ToolsHTML.setValueInQuery(st, forma.getMajorKeep(), pos);
				pos = ToolsHTML.setValueInQuery(st, forma.getMinorKeep(), pos);
				if (forma.getExpireWF() == 0) {
					pos = ToolsHTML.setValueInQuery(st, forma.getExpireWF(),
							pos);
					pos = ToolsHTML.setValueInQuery(st, forma.getDaysWF(), pos);
				}
				pos = ToolsHTML.setValueInQuery(st, forma.getCantExpDoc(), pos);
				pos = ToolsHTML.setValueInQuery(st, forma.getUnitExpDoc(), pos);
				pos = ToolsHTML.setValueInQuery(st, forma.getVijenToprint(),
						pos);
				pos = ToolsHTML.setValueInQuery(st,
						forma.getCheckvijenToprint(), pos);
				pos = ToolsHTML.setValueInQuery(st,
						forma.getCheckborradorCorrelativo(), pos);
				// System.out.println(st.toString());

				st.executeUpdate();
			}
		}
	}

	/**
	 * Método para Actualizar los datos de la Estructura
	 * 
	 * @param forma
	 * @return
	 */
	public synchronized static boolean edit(BaseStructForm forma) {
		int nodeType = Integer.parseInt(forma.getNodeType());
		String table = getTableName(nodeType);
		Connection con = null;
		PreparedStatement st = null;
		StringBuilder edit = new StringBuilder("UPDATE struct SET Name='");
		edit.append(forma.getName())
				.append("', Description = ? , heredarPrefijo = '")
				.append(forma.getHeredarPrefijo()).append("'");
		boolean changeOwner = false;
		if (!ToolsHTML.isEmptyOrNull(forma.getOwner())) {
			edit.append(", Owner = ? ");
			changeOwner = true;
		}
		//ydavila Ticket 001-00-003023
		boolean changerespsolcambio = false;
		if (!ToolsHTML.isEmptyOrNull(forma.getrespsolcambio())) {
			edit.append(", respsolcambio = ? ");
			changerespsolcambio = true;
		}
		boolean changerespsolelimin = false;
		if (!ToolsHTML.isEmptyOrNull(forma.getrespsolelimin())) {
			edit.append(", respsolelimin = ? ");
			changerespsolelimin = true;
		}
		boolean changerespsolimpres = false;
		if (!ToolsHTML.isEmptyOrNull(forma.getrespsolimpres())) {
			edit.append(", respsolimpres = ? ");
			changerespsolcambio = true;
		}
		edit.append(" WHERE IdNode = ").append(forma.getIdNode());
		boolean resp = false;
		try {
			boolean editNode = checkExist(table, forma.getIdNode());
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);
			st = con.prepareStatement(edit.toString());
			st.setString(1, forma.getDescription());
			if (changeOwner) {
				st.setString(2, forma.getOwner());
			}
			//ydavila Ticket 001-00-003023
			if (changerespsolcambio) {
			st.setString(3, forma.getrespsolcambio());
			System.out.println("update.st.setString() = " + st);
			st.executeUpdate();
			}
			if (changerespsolelimin) {
				st.setString(4, forma.getrespsolelimin());
				System.out.println("update.st.setString() = " + st);
				st.executeUpdate();
				}
			if (changerespsolimpres) {
				st.setString(5, forma.getrespsolimpres());
				System.out.println("update.st.setString() = " + st);
				st.executeUpdate();
				}
			updateTableAssociate(table, forma, editNode, con);
			st.executeUpdate();

			// agregamos los usuarios de la lista de distribucion
			addListDistToStruct(forma, con, st);

			con.commit();
			resp = true;
		} catch (Exception e) {
			applyRollback(con);
			setMensaje(e.getMessage() != null ? e.getMessage() : "");
			e.printStackTrace();
			resp = false;
		} finally {
			setFinally(con, st);
		}

		return resp;
	}

	/**
	 * Agrega los usuarios seleccionados a la lista de distribucion
	 * 
	 * @param forma
	 * @param con
	 * @param st
	 * @throws SQLException
	 */
	public static void addListDistToStruct(BaseStructForm forma,
			Connection con, PreparedStatement st) throws SQLException {
		// ingresaremos los usuarios asociados
		String[] listUser = new String[0];
		if (forma.getToListDistNameUser() != null
				&& !forma.getToListDistNameUser().trim().equals("")) {
			listUser = forma.getToListDistNameUser().split(",");
		}
		StringBuilder queryDelete = new StringBuilder(
				"DELETE FROM listdist WHERE idNode=?");
		StringBuilder queryUpdate = new StringBuilder(
				"INSERT INTO listdist (idNode,nameUser) VALUES(?,?)");
		int idNode = ToolsHTML.parseInt(forma.getIdNode());

		// eliminamos los usuarios anteriores del nodo referentes a la lista de
		// distribucion
		st = con.prepareStatement(queryDelete.toString());
		st.setInt(1, idNode);
		st.executeUpdate();

		// agregamos los nuevos usuarios de la lista de distribucion al nodo
		forma.setToListDist(0); // colocamos que no tiene lista de distribucion
								// por defecto
		st = con.prepareStatement(queryUpdate.toString());
		for (int i = 0; i < listUser.length; i++) {
			st.setInt(1, idNode);
			st.setString(2, listUser[i]);
			st.executeUpdate();
			forma.setToListDist(1); // colocamos que ya tiene lista de
									// distribucion
		}

		StringBuilder edit = new StringBuilder("UPDATE struct SET toListDist=")
				.append(forma.getToListDist());
		edit.append(" WHERE IdNode = ").append(forma.getIdNode());
		st = con.prepareStatement(edit.toString());
		st.executeUpdate();

	}

	/**
	 * Procedimiento para verificar el estatu de las versiones del Documento
	 */
	public static void checkVersionDocs(String idDocumen) {
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT numVer,statu FROM versiondoc");
		sql.append(" WHERE numDoc = ").append(idDocumen);
		sql.append("   AND statu = '").append(HandlerDocuments.docApproved)
				.append("' ");
		sql.append("  ORDER BY numVer DESC");
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			// Si Hay más de una Versión Aprobada para el Documento se procede a
			// Llevar a Obsoleta
			if (datos.size() > 1) {
				Properties prop = (Properties) datos.get(0);
				String numVer = prop.getProperty("numVer");
				StringBuilder upd = new StringBuilder(50);
				upd.append("UPDATE versiondoc SET statu = ").append(
						HandlerDocuments.docObs);
				upd.append(" WHERE statu = '")
						.append(HandlerDocuments.docApproved).append("'");
				upd.append(" AND numDoc = ").append(idDocumen);
				upd.append(" AND numVer <> ").append(numVer);
				JDBCUtil.doUpdate(upd.toString());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Collection getAllVersionForDocument(String idDocumen,
			String idVersion, PermissionUserForm securityDocsfrm)
			throws Exception {
		StringBuilder sql = new StringBuilder(
				"SELECT a.numVer,a.MayorVer,a.MinorVer,a.dateApproved,a.dateCreated,a.dateExpires,a.statu,a.dateExpiresDrafts");
		sql.append(",a.statuhist,nameDocVersion,a.ownerVersion,b.cargo ");
		sql.append("FROM versiondoc a, person b ");
		sql.append("WHERE a.ownerversion=b.nameUser AND a.numDoc = ").append(
				idDocumen);
		if (!ToolsHTML.isEmptyOrNull(idVersion)) {
			sql.append(" AND a.numVer = ").append(idVersion);
		}

		sql.append(" AND b.accountActive='1' ");
		sql.append(" ORDER BY a.numVer DESC");
		// System.out.println("sql.toString() = " + sql.toString());
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		ArrayList resp = new ArrayList();

		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			BeanVersionForms bean = new BeanVersionForms();
			bean.setNumDocument(idDocumen);
			// obtenemos los status en que fueron pasando cada versión.
			if (!ToolsHTML.isEmptyOrNull(properties.getProperty("statuhist"))) {
				bean.setStatuhist(properties.getProperty("statuhist").trim());
			} else {
				bean.setStatuhist("0");
			}
			// ****************************************//
			// comprobamos la seguridad del documento
			// PermissionUserForm securityDocsfrm=null;
			// if (!ToolsHTML.isEmptyOrNull(bean.getNumDocument())){
			// securityDocsfrm=securityDocs.get(bean.getNumDocument())!=null?(PermissionUserForm)securityDocs.get(bean.getNumDocument()):null;
			// }
			if (securityDocsfrm != null) {
				// carga toda la permisologia al bean
				ToolsHTML.checkDocsSecurityLoad(securityDocsfrm, bean);
			}
			// ****************************************//
			bean.setNumVersion(Integer.parseInt(properties
					.getProperty("numVer")));
			bean.setMayorVersion(properties.getProperty("MayorVer"));
			bean.setMinorVersion(properties.getProperty("MinorVer"));
			bean.setDateApproved(ToolsHTML.formatDateShow(
					properties.getProperty("dateApproved"), false));
			bean.setDateCreated(ToolsHTML.formatDateShow(
					properties.getProperty("dateCreated"), true));
			bean.setDateExpires(ToolsHTML.formatDateShow(
					properties.getProperty("dateExpires"), false));
			bean.setDateExpiresDrafts(ToolsHTML.formatDateShow(
					properties.getProperty("dateExpiresDrafts"), false));
			bean.setStatu(properties.getProperty("statu").trim());
			bean.setNameDocVersion(properties.getProperty("nameDocVersion"));
			bean.setOwnerVersion(properties.getProperty("ownerVersion"));
			bean.setCharge(HandlerDBUser.getCargoAndArea(properties
					.getProperty("cargo")));
			resp.add(bean);
		}

		return resp;
	}
	
	//ydavila Ticket 001-00-003023 rutina para entrar a la EOC desde Lista Maestra, Buscar o Principal
	public static Collection getPublishedVersionForDocument(String idDocumen,
			String idVersion, PermissionUserForm securityDocsfrm)
			throws Exception {
		StringBuilder sql = new StringBuilder(
				"SELECT a.numVer,a.MayorVer,a.MinorVer,a.dateApproved,a.dateCreated,a.dateExpires,a.statu,a.dateExpiresDrafts");
		sql.append(",a.statuhist,nameDocVersion,a.ownerVersion,b.cargo ");
		sql.append("FROM versiondoc a, person b ");
		sql.append("WHERE a.ownerversion=b.nameUser AND a.numDoc = ").append(
				idDocumen);
		if (!ToolsHTML.isEmptyOrNull(idVersion)) {
			sql.append(" AND a.numVer = ").append(idVersion);
		}
		sql.append(" AND a.statu='5' ");
		sql.append(" AND b.accountActive='1' ");
		sql.append(" ORDER BY a.numVer DESC");
		// System.out.println("sql.toString() = " + sql.toString());
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		ArrayList resp = new ArrayList();

		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			BeanVersionForms bean = new BeanVersionForms();
			bean.setNumDocument(idDocumen);
			// obtenemos los status en que fueron pasando cada versión.
			if (!ToolsHTML.isEmptyOrNull(properties.getProperty("statuhist"))) {
				bean.setStatuhist(properties.getProperty("statuhist").trim());
			} else {
				bean.setStatuhist("0");
			}
			// ****************************************//
			// comprobamos la seguridad del documento
			// PermissionUserForm securityDocsfrm=null;
			// if (!ToolsHTML.isEmptyOrNull(bean.getNumDocument())){
			// securityDocsfrm=securityDocs.get(bean.getNumDocument())!=null?(PermissionUserForm)securityDocs.get(bean.getNumDocument()):null;
			// }
			if (securityDocsfrm != null) {
				// carga toda la permisologia al bean
				ToolsHTML.checkDocsSecurityLoad(securityDocsfrm, bean);
			}
			// ****************************************//
			bean.setNumVersion(Integer.parseInt(properties
					.getProperty("numVer")));
			bean.setMayorVersion(properties.getProperty("MayorVer"));
			bean.setMinorVersion(properties.getProperty("MinorVer"));
			bean.setDateApproved(ToolsHTML.formatDateShow(
					properties.getProperty("dateApproved"), false));
			bean.setDateCreated(ToolsHTML.formatDateShow(
					properties.getProperty("dateCreated"), true));
			bean.setDateExpires(ToolsHTML.formatDateShow(
					properties.getProperty("dateExpires"), false));
			bean.setDateExpiresDrafts(ToolsHTML.formatDateShow(
					properties.getProperty("dateExpiresDrafts"), false));
			bean.setStatu(properties.getProperty("statu").trim());
			bean.setNameDocVersion(properties.getProperty("nameDocVersion"));
			bean.setOwnerVersion(properties.getProperty("ownerVersion"));
			bean.setCharge(HandlerDBUser.getCargoAndArea(properties
					.getProperty("cargo")));
			resp.add(bean);
		}

		return resp;
	}
	

	// public synchronized static Collection getAllVersionForDocument(String
	// idDocumen,String
	// idVersion) throws Exception{
	// StringBuilder sql = new StringBuilder("SELECT
	// numVer,MayorVer,MinorVer,dateApproved,dateCreated,dateExpires,statu,dateExpiresDrafts
	// FROM
	// versiondoc WHERE numDoc = ").append(idDocumen);
	// if (!ToolsHTML.isEmptyOrNull(idVersion)) {
	// sql.append(" AND numVer = ").append(idVersion);
	// }
	// sql.append(" AND active = ").append(Constants.permission);
	// sql.append(" ORDER BY numVer DESC");
	// //System.out.println("sql.toString() = " + sql.toString());
	// Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
	// ArrayList resp = new ArrayList();
	//
	// for (int row = 0; row < datos.size(); row++) {
	// Properties properties = (Properties) datos.elementAt(row);
	// BeanVersionForms bean = new BeanVersionForms();
	// bean.setNumDocument(idDocumen);
	// bean.setNumVersion(Integer.parseInt(properties.getProperty("numVer")));
	// bean.setMayorVersion(properties.getProperty("MayorVer"));
	// bean.setMinorVersion(properties.getProperty("MinorVer"));
	// bean.setDateApproved(ToolsHTML.formatDateShow(properties.getProperty("dateApproved"),false));
	// bean.setDateCreated(ToolsHTML.formatDateShow(properties.getProperty("dateCreated"),true));
	// bean.setDateExpires(ToolsHTML.formatDateShow(properties.getProperty("dateExpires"),false));
	// bean.setDateExpiresDrafts(ToolsHTML.formatDateShow(properties.getProperty("dateExpiresDrafts"),false));
	// bean.setStatu(properties.getProperty("statu"));
	// resp.add(bean);
	// }
	//
	// return resp;
	// }

	// SIMON 17 DE JUNIO 2005 INICIO
	// getPreparementStatementVersionInsert(forma,con,value,unit,valueDrafts,unitDrafts,timeCreate);
	// newToEdit idPerson fue agregado para saber si coloco true o false en la
	// tabla de versiones
	// para la edicion de registros via internet.
	public synchronized static void getPreparementStatementVersionInsert(
			BaseDocumentForm forma, Connection con, int value, String unit,
			int valueDrafts, String unitDrafts, Timestamp time,
			boolean newToEdit, String idPerson) throws Exception {
		PreparedStatement st = null;
		java.util.Date dateExpire = ToolsHTML.getDateExpireDocument(
				forma.getDateApproved(), forma.getDateExpires(), value, unit);
		String dateExp = null;
		if (dateExpire != null) {
			dateExp = ToolsHTML.sdf.format(dateExpire);
		}
		// SIMON 17 DE JUNIO 2005 INICIO
		String dateDrafts = (String) time.toString();
		java.util.Date dateExpireDrafts = ToolsHTML.getDateExpireDocument(
				dateDrafts, forma.getDateExpiresDrafts(), valueDrafts,
				unitDrafts);
		String dateExpDrafts = null;
		if (dateExpireDrafts != null) {
			dateExpDrafts = ToolsHTML.sdf.format(dateExpireDrafts);
		}
		// SIMON 17 DE JUNIO 2005 FIN

		StringBuffer fields = new StringBuffer("?,?,?,?");
		StringBuffer update = new StringBuffer(
				"INSERT INTO versiondoc (numVer,numDoc,dateCreated,statu");
		ToolsHTML.setFieldInQuery(update, fields, forma.getMayorVer(),
				"MayorVer");
		ToolsHTML.setFieldInQuery(update, fields, forma.getMinorVer(),
				"MinorVer");
		ToolsHTML.setFieldInQuery(update, fields,
				JDBCUtil.getByte(forma.getApproved(), true), "approved");
		ToolsHTML.setFieldInQuery(update, fields, forma.getDateApproved(),
				"dateApproved");
		ToolsHTML.setFieldInQuery(update, fields,
				JDBCUtil.getByte(forma.getExpires(), true), "expires");
		ToolsHTML.setFieldInQuery(update, fields, dateExp, "dateExpires");
		// SIMON 17 DE JUNIO 2005 INICIO
		// ToolsHTML.setFieldInQuery(update,fields,dateExpDrafts,"dateExpiresDrafts");
		ToolsHTML.setFieldInQuery(update, fields, dateExpDrafts,
				"dateExpiresDrafts");

		// SIMON 17 DE JUNIO 2005 FIN
		ToolsHTML.setFieldInQuery(update, fields, forma.getComments(),
				"comments");

		// Luis Cisneros. Le agrego a la tabla un campo para poder identificar
		// si el archivo es
		// recien creado
		ToolsHTML.setFieldInQuery(update, fields,
				JDBCUtil.getByte(String.valueOf(newToEdit)),
				"newRegisterToEdit");
		ToolsHTML.setFieldInQuery(update, fields, idPerson, "createdBy");
		ToolsHTML.setFieldInQuery(update, fields, forma.getNameDocument(),
				"nameDocVersion");
		ToolsHTML.setFieldInQuery(update, fields, forma.getOwner(),
				"ownerVersion");

		update.append(") Values (").append(fields.toString()).append(")");

		// Luis Cisneros, necesito validar el query.
		// System.out.println("[getPreparementStatementVersionInsert:UPDATE]" +
		// update.toString());

		st = con.prepareStatement(update.toString());
		st.setInt(1, forma.getNumVer());
		st.setInt(2, Integer.parseInt(forma.getNumberGen()));
		st.setTimestamp(3, time);
		st.setString(4, getStatuDoc(forma.getApproved()));
		int pos = 5;
		Integer values = null;
		pos = ToolsHTML.setValueInQuery(st, forma.getMayorVer(), pos);
		pos = ToolsHTML.setValueInQuery(st, forma.getMinorVer(), pos);
		if (forma.getApproved() != null
				&& ToolsHTML.isNumeric(forma.getApproved().trim())) {
			values = new Integer(forma.getApproved().trim());
			pos = ToolsHTML.setValueInQuery(st, values, pos);
			if ((values.intValue() == 0)
					&& ToolsHTML.isEmptyOrNull(forma.getDateApproved())) {
				new Exception().printStackTrace();
				throw new ApplicationExceptionChecked("E0010");
			}
		}
		pos = ToolsHTML.setValueInQuery(st, forma.getDateApproved(), 4, pos);
		if (forma.getExpires() != null
				&& ToolsHTML.isNumeric(forma.getExpires().trim())) {
			values = new Integer(forma.getExpires().trim());
			pos = ToolsHTML.setValueInQuery(st, values, pos);
			if ((values.intValue() == 0)
					&& ToolsHTML.isEmptyOrNull(forma.getExpires())) {
				throw new ApplicationExceptionChecked("E0011");
			}
		}
		pos = ToolsHTML.setValueInQuery(st, dateExp, 4, pos);
		// SIMON 17 DE JUNIO 2005 INICIO
		// pos = ToolsHTML.setValueInQuery(st,dateExpDrafts,4,pos);
		pos = ToolsHTML.setValueInQuery(st, dateExpDrafts, 4, pos);
		// SIMON 17 DE JUNIO 2005 FIN
		// SIMON 13 DE JUNIO 2005 INICIO
		if (ToolsHTML.isEmptyOrNull(forma.getNameFile())) {
			pos = ToolsHTML.setValueInQuery(st,
					DesigeConf.getProperty("documentoenlinea"), pos);
		} else {
			pos = ToolsHTML.setValueInQuery(st, forma.getComments(), pos);
		}

		// Luis Cisneros Valores (para que carajo le paso los valores en la
		// linea anterior si tengo
		// que colocarlos nuevamente en el statemet????
		pos = ToolsHTML.setValueInQuery(st,
				JDBCUtil.getByte(String.valueOf(newToEdit)), pos);
		pos = ToolsHTML.setValueInQuery(st, idPerson, pos);

		// nombre y propietario actuales del documento
		pos = ToolsHTML.setValueInQuery(st, forma.getNameDocument(), pos);
		pos = ToolsHTML.setValueInQuery(st, forma.getOwner(), pos);

		st.executeUpdate();
	}

	// SIMON 17 DE JUNIO 2005 FIN

	// private synchronized static void
	// getPreparementStatementVersionInsert(BaseDocumentForm
	// forma,Connection con,
	// int value,String unit,Timestamp time) throws Exception{
	// PreparedStatement st = null;
	// java.util.Date dateExpire =
	// ToolsHTML.getDateExpireDocument(forma.getDateApproved(),forma.getDateExpires(),value,unit);
	// String dateExp = null;
	// if (dateExpire!=null) {
	// dateExp = ToolsHTML.sdf.format(dateExpire);
	// }
	// StringBuilder fields = new StringBuilder("?,?,?,?");
	// StringBuilder update = new StringBuilder("INSERT INTO versiondoc
	// (numVer,numDoc,dateCreated,statu");
	// ToolsHTML.setFieldInQuery(update,fields,forma.getMayorVer(),"MayorVer");
	// ToolsHTML.setFieldInQuery(update,fields,forma.getMinorVer(),"MinorVer");
	// ToolsHTML.setFieldInQuery(update,fields,forma.getApproved(),"approved");
	// ToolsHTML.setFieldInQuery(update,fields,forma.getDateApproved(),"dateApproved");
	// ToolsHTML.setFieldInQuery(update,fields,forma.getExpires(),"expires");
	// ToolsHTML.setFieldInQuery(update,fields,dateExp,"dateExpires");
	// ToolsHTML.setFieldInQuery(update,fields,forma.getComments(),"comments");
	// update.append(") Values (").append(fields.toString()).append(")");
	// st = con.prepareStatement(update.toString());
	// st.setInt(1,forma.getNumVer());
	// st.setInt(2,Integer.parseInt(forma.getNumberGen()));
	// st.setTimestamp(3,time);
	// st.setString(4,getStatuDoc(forma.getApproved()));
	// int pos = 5;
	// Integer values = null;
	// // if (ToolsHTML.isNumeric(forma.getMayorVer())) {
	// // values = new Integer(forma.getMayorVer());
	// // pos = ToolsHTML.setValueInQuery(st,values,pos);
	// // }
	// pos = ToolsHTML.setValueInQuery(st,forma.getMayorVer(),pos);
	// pos = ToolsHTML.setValueInQuery(st,forma.getMinorVer(),pos);
	// // if (ToolsHTML.isNumeric(forma.getMinorVer())) {
	// // values = new Integer(forma.getMinorVer());
	// // pos = ToolsHTML.setValueInQuery(st,values,pos);
	// // }
	// if
	// (forma.getApproved()!=null&&ToolsHTML.isNumeric(forma.getApproved().trim()))
	// {
	// values = new Integer(forma.getApproved().trim());
	// pos = ToolsHTML.setValueInQuery(st,values,pos);
	// if
	// ((values.intValue()==0)&&ToolsHTML.isEmptyOrNull(forma.getDateApproved()))
	// {
	// new Exception().printStackTrace();
	// throw new ApplicationExceptionChecked("E0010");
	// }
	// }
	// pos = ToolsHTML.setValueInQuery(st,forma.getDateApproved(),4,pos);
	// if
	// (forma.getExpires()!=null&&ToolsHTML.isNumeric(forma.getExpires().trim())){
	// values = new Integer(forma.getExpires().trim());
	// pos = ToolsHTML.setValueInQuery(st,values,pos);
	// if ((values.intValue()==0) &&
	// ToolsHTML.isEmptyOrNull(forma.getExpires())) {
	// throw new ApplicationExceptionChecked("E0011");
	// }
	// }
	// pos = ToolsHTML.setValueInQuery(st,dateExp,4,pos);
	// //SIMON 13 DE JUNIO 2005 INICIO
	// if (ToolsHTML.isEmptyOrNull(forma.getNameFile())){
	//
	// // pos =
	// ToolsHTML.setValueInQuery(st,forma.getComments().substring(0,30),pos);
	// pos =
	// ToolsHTML.setValueInQuery(st,DesigeConf.getProperty("documentoenlinea"),pos);
	// }else{
	// pos = ToolsHTML.setValueInQuery(st,forma.getComments(),pos);
	// }
	// st.executeUpdate();
	// }

	public synchronized static boolean updateVersionDoc(BaseDocumentForm forma,
			String path) {
		boolean resp = false;
		Connection con = null;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			saveBD(con, forma.getNameFile(), forma.getComments(), path,
					forma.getNumVer());
			resp = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			setFinally(con);
		}
		return resp;
	}

	public synchronized static boolean upDocument(BaseDocumentForm forma,
			String path, HttpServletRequest request,
			boolean actualizarActiveFactory, String dateCreation,
			boolean deleteFile, String idCreator)
			throws ApplicationExceptionChecked {
		return upDocument(null, forma, path, request, actualizarActiveFactory,
				dateCreation, deleteFile, idCreator, true);
	}

	public synchronized static boolean upDocument(Connection con,
			BaseDocumentForm forma, String path, HttpServletRequest request,
			boolean actualizarActiveFactory, String dateCreation,
			boolean deleteFile, String idCreator, boolean isConvertPDF)
			throws ApplicationExceptionChecked {
		boolean resp = false;
		PreparedStatement st = null;
		boolean cerrar = false;
		try {
			java.util.Date date = new java.util.Date();
			Timestamp time = new Timestamp(date.getTime());
			// System.out.println("time = " + time);

			int numberDoc = proximo(con, "docs", "documents", "numGen");
			forma.setNumberGen(String.valueOf(numberDoc));
			int numVer = proximo(con, "versiondoc", "versiondoc", "numver");

			// EXPIRACION DE DOCUMENTOS APROBADOS
			String[] items = new String[] { "monthExpireDoc", "unitTimeExpire" };
			String[] values = HandlerParameters.getFieldsExpired(con, items,
					forma.getTypeDocument());
			String monthExpireDoc = values != null ? values[0] : null;
			String unit = values != null ? values[1].trim() : null;
			int value = 0;
			if (ToolsHTML.isNumeric(monthExpireDoc.trim())) {
				value = Integer.parseInt(monthExpireDoc.trim());
			}
			// EXPIRACION DE BORRADORES
			String[] itemsDrafts = new String[] { "monthsExpireDrafts",
					"unitTimeExpireDrafts" };
			String[] valuesDrafts = HandlerParameters.getFieldsExpired(con,
					itemsDrafts, forma.getTypeDocument());
			String monthsExpireDrafts = valuesDrafts != null ? valuesDrafts[0]
					: null;
			String unitDrafts = valuesDrafts != null ? valuesDrafts[1].trim()
					: null;
			int valueDrafts = 0;
			if (ToolsHTML.isNumeric(monthsExpireDrafts.trim())) {
				valueDrafts = Integer.parseInt(monthsExpireDrafts.trim());
			}

			// SIMON 17 DE JUNIO 2005 FIN
			forma.setNumVer(numVer);
			if (con == null || con.isClosed()) {
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				con.setAutoCommit(false);
				cerrar = true;
			}
			java.util.Date dateCreate = ToolsHTML
					.getDateCreationMovements(forma.getDateApproved());
			try {
				if (!ToolsHTML.isEmptyOrNull(dateCreation)) {
					dateCreate = ToolsHTML
							.getDateCreationMovements(dateCreation);
					time = new Timestamp(dateCreate.getTime());
				}
			} catch (Exception e) {
				dateCreate = ToolsHTML.getDateCreationMovements(forma
						.getDateApproved());
			}
			Timestamp timeCreate = new Timestamp(dateCreate.getTime());
			getPreparedStatementDocumentInsert(forma, con, time, timeCreate,
					numVer);
			getPreparementStatementVersionInsert(forma, con, value, unit,
					valueDrafts, unitDrafts, timeCreate, false, idCreator);
			HandlerDocuments.updateHistoryDocs(con, numberDoc, 0, 0, forma
					.getOwner(), time, "1",
					forma.getComments() != null ? forma.getComments() : null,
					new String[] { forma.getMayorVer(), forma.getMinorVer() });

			// Asignamos la ubicacion del documento.
			// campos adicionales en la tabla documents
			ConfDocumentoDAO conf = null;
			ArrayList lista = null;
			DocumentForm obj;
			StringBuilder camposDeUbicacion = new StringBuilder();
			HashMap listaCamposUbicacion = new HashMap();
			try {
				conf = new ConfDocumentoDAO();
				lista = (ArrayList) conf.findAll();

				if (lista != null) {
					for (int i = 0; i < lista.size(); i++) {
						obj = (DocumentForm) lista.get(i);
						if (obj.getLocation() == 1) {
							listaCamposUbicacion.put(obj.getId(), obj);
							camposDeUbicacion
									.append(camposDeUbicacion.length() == 0 ? ""
											: ",");
							camposDeUbicacion.append(obj.getId());
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			// la ubicacion se deja en la traza si hay campos
			boolean isChangeLocation = listaCamposUbicacion.size() > 0;
			Iterator ite = listaCamposUbicacion.values().iterator();
			String valorActual = "";
			String valorAnterior = "";
			if (isChangeLocation) {
				ite = listaCamposUbicacion.values().iterator();

				int numdoc = Integer.parseInt(forma.getNumberGen());
				int node = Integer.parseInt(ToolsHTML.isEmptyOrNull(
						forma.getIdNode(), "0"));
				String historyType = "22";
				String razonHistory = getHistoryLocationHTML(ite, forma, null);
				HandlerDocuments
						.updateHistoryDocs(
								con,
								numdoc,
								node,
								0,
								forma.getOwner(),
								new Timestamp(new Date().getTime()),
								historyType,
								razonHistory,
								new String[] { forma.getMayorVer(),
										forma.getMinorVer() });
			}

			getPreparedStatementDocumentLinks(forma, con);
			isConvertPDF = false; // true; la conversion se realizar a nivel de
									// visor y no de servidor
			// isConvertPDF = true;
			// if(forma.getNameFile()!=null) {
			// isConvertPDF =
			// (!forma.getNameFile().toLowerCase().endsWith("jpg") &&
			// !forma.getNameFile().toLowerCase().endsWith("jpeg") &&
			// !forma.getNameFile().toLowerCase().endsWith("gif"));
			// }

			// se verifica si el archivo es de una extension de doble version
			if (ToolsHTML.extensionIsDobleVersion(forma.getNameFile()
					.toLowerCase()
					.substring(forma.getNameFile().lastIndexOf(".")))) {
				isConvertPDF = true;
			}
			saveBD(con, forma, path, deleteFile, isConvertPDF);
			// OJO NO SE HEREDARA LOS PERMISOS YA QUE ESTOS YA SON PROPIOS DE LA
			// CARPETA Y EL
			// USUARIO QUE ESTA EN SESSION //
			// HandlerDocuments.permisosHeredadosDocs(forma,numberDoc,con);
			if (cerrar) {
				con.commit();
			}
			// procesamos wonderware para su actualizacion en activefactory
			/*
			 * if (actualizarActiveFactory) { EditDocumentAction actActvefactory
			 * = new EditDocumentAction(); Users usuario = new Users(); //
			 * getUserSession(); usuario.setNameUser(forma.getOwner());
			 * if(Constants.ACTIVE_FACTORY) {
			 * actActvefactory.actualizarActiveFactory(forma, request, usuario);
			 * } }
			 */
			resp = true;
		} catch (ApplicationExceptionChecked ae) {
			if (cerrar) {
				applyRollback(con);
			}
			throw ae;// new ApplicationExceptionChecked(ae.getKeyError());
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (cerrar) {
				applyRollback(con);
			}
			resp = false;
		} catch (Exception ex) {
			request.getSession().setAttribute("info2", ex.getMessage());

			ex.printStackTrace();
			if (cerrar) {
				applyRollback(con);
			}
			resp = false;
		} finally {
			if (cerrar) {
				setFinally(con, st);
			}
		}
		return resp;
	}

	public synchronized static boolean createRegister(BaseDocumentForm forma,
			int idVersionToCopy, String idPerson)
			throws ApplicationExceptionChecked {
		boolean resp = false;
		Connection con = null;
		PreparedStatement st = null;
		try {
			java.util.Date date = new java.util.Date();
			Timestamp time = new Timestamp(date.getTime());
			// System.out.println("time = " + time);
			// int numberDoc = IDDBFactorySql.getNextID("docs");
			int numberDoc = HandlerStruct
					.proximo("docs", "documents", "numGen");
			forma.setNumberGen(String.valueOf(numberDoc));
			// int numVer = IDDBFactorySql.getNextID("versiondoc");
			int numVer = HandlerStruct.proximo("versiondoc", "versiondoc",
					"numVer");

			String[] items = new String[] { "monthExpireDoc", "unitTimeExpire" };
			String[] values = HandlerParameters.getFieldsExpired(items,
					forma.getTypeDocument());
			String monthExpireDoc = values != null ? values[0] : null;
			String unit = values != null ? values[1].trim() : null;
			int value = 0;
			if (ToolsHTML.isNumeric(monthExpireDoc.trim())) {
				value = Integer.parseInt(monthExpireDoc.trim());
			}

			// SIMON 17 DE JUNIO 2005 INICIO
			String monthsExpireDrafts = HandlerParameters.PARAMETROS.getMonthsExpireDrafts();
			String unitDrafts = HandlerParameters.PARAMETROS.getUnitTimeExpireDrafts();
			
			int valueDrafts = 0;
			if (ToolsHTML.isNumeric(monthsExpireDrafts.trim())) {
				valueDrafts = Integer.parseInt(monthsExpireDrafts.trim());
			}
			// SIMON 17 DE JUNIO 2005 FIN
			forma.setNumVer(numVer);
			// InputStream dataVersion = recuperarFileBD(null, idVersionToCopy);
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);
			java.util.Date dateCreate = new java.util.Date();
			Timestamp timeCreate = new Timestamp(dateCreate.getTime());
			getPreparedStatementDocumentInsert(forma, con, time, timeCreate,
					numVer);
			// getPreparementStatementVersionInsert(forma,con,value,unit,timeCreate);
			// SIMON 17 DE JUNIO 2005 INICIO
			getPreparementStatementVersionInsert(forma, con, value, unit,
					valueDrafts, unitDrafts, timeCreate, true, idPerson);
			// SIMON 17 DE JUNIO 2005 FIN
			HandlerDocuments.updateHistoryDocs(con, numberDoc, 0, 0,
					forma.getOwner(), time, "1", null,
					new String[] { forma.getMayorVer(), forma.getMinorVer() });
			// getPreparedStatementDocumentLinks(forma,con);
			// copyVersionDoc(con, dataVersion, forma.getNumVer());
			copyVersionDoc(con, idVersionToCopy, forma.getNumVer());
			// hreda dicho documento los permisos de la carpeta
			// esta herencia es automatica 2007-11-14
			// HandlerDocuments.permisosHeredadosDocs(forma, numberDoc, con);
			con.commit();
			resp = true;
		} catch (ApplicationExceptionChecked ae) {
			ae.printStackTrace();
			applyRollback(con);
			throw ae;// new ApplicationExceptionChecked(ae.getKeyError());
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			applyRollback(con);
			resp = false;
		} catch (Exception ex) {
			ex.printStackTrace();
			applyRollback(con);
			resp = false;
		} finally {
			setFinally(con, st);
		}
		return resp;
	}

	private static String getStatuDoc(String valor) {
		if (!ToolsHTML.isEmptyOrNull(valor)) {
			if (valor.trim().compareTo("0") == 0) {// El Documento está Aprobado
				return HandlerDocuments.docApproved;
			}
		}
		return HandlerDocuments.docTrash; // Es Un Borrador
	}

	private static long getVersionApprovedDoc(String valor, long numVer) {
		if (!ToolsHTML.isEmptyOrNull(valor)) {
			if (valor.trim().equalsIgnoreCase("0")) {// El Documento está
														// Aprobado
				return numVer;
			}
		}
		return 0;
	}

	/**
	 * Procedimiento para establecer las relaciones entre los diversos
	 * documentos
	 * 
	 * @param forma
	 * @param con
	 * @throws SQLException
	 */

	public static void getPreparedStatementDocumentLinks(
			BaseDocumentForm forma, Connection con) throws SQLException {
		PreparedStatement st = null;
		ResultSet rs = null;
		if (!ToolsHTML.isEmptyOrNull(forma.getDocRelations())) {
			StringBuilder insert = new StringBuilder(
					"INSERT INTO documentslinks (numGen,numGenLink,NumVer,NumVerLink) VALUES (?,?,?,?)");
			StringTokenizer items = new StringTokenizer(
					forma.getDocRelations(), ",");
			int numeGen = Integer.parseInt(forma.getNumberGen());
			int numGenLink;
			long numVerLink;
			String[] dataDoc;
			while (items.hasMoreTokens()) {
				// Se Obtiene el Número del Documento y el Número de la Versión
				// Los mismos se encuentran separados po "_"
				dataDoc = items.nextToken().split("_");
				numGenLink = Integer.parseInt(dataDoc[0]);
				numVerLink = Long.parseLong(dataDoc[1]);
				// Relacion A => B
				StringBuilder query = new StringBuilder(50);
				query.append(
						"SELECT numgen FROM documentslinks WHERE numgen = ")
						.append(numeGen).append(" AND numGenLink = ");
				query.append(numGenLink).append(" AND NumVer = ")
						.append(forma.getNumVer()).append(" AND NumVerLink = ")
						.append(numVerLink);
				// String query =
				// "SELECT numgen FROM documentslinks WHERE numgen = " + numeGen
				// + "
				// AND numGenLink = " + numGenLink;
				st = con.prepareStatement((query.toString()));
				rs = st.executeQuery();
				if (!rs.next()) {
					st = con.prepareStatement(insert.toString());
					st.setInt(1, numeGen);
					st.setInt(2, numGenLink);
					st.setLong(3, forma.getNumVer());
					st.setLong(4, numVerLink);
					st.executeUpdate();
				}
				// Relacion B => A
				query = new StringBuilder(50);
				query.append(
						"SELECT numgen FROM documentslinks WHERE numgen = ")
						.append(numGenLink).append(" AND numGenLink = ");
				query.append(numeGen).append(" AND NumVer = ")
						.append(numVerLink).append(" AND NumVerLink = ")
						.append(forma.getNumVer());
				// query = "SELECT numgen FROM documentslinks WHERE numgen = " +
				// numGenLink + " AND
				// numGenLink = " + numeGen;
				st = con.prepareStatement((query.toString()));
				rs = st.executeQuery();
				if (!rs.next()) {
					st = con.prepareStatement(insert.toString());
					st.setInt(1, numGenLink);
					st.setInt(2, numeGen);
					st.setLong(3, numVerLink);
					st.setLong(4, forma.getNumVer());
					st.executeUpdate();
				}
			}
		}
	}

	public static void getPreparedStatementDocumentLinks(String numDoc,
			int numVer, String docRelations, Connection con)
			throws SQLException {
		PreparedStatement st = null;
		ResultSet rs = null;
		boolean swNoNull = true;
		if (!ToolsHTML.isEmptyOrNull(docRelations)) {
			StringBuilder insert = new StringBuilder(
					"INSERT INTO documentslinks (numGen,numGenLink,NumVer,NumVerLink) VALUES (?,?,?,?)");
			StringTokenizer items = new StringTokenizer(docRelations, ",");
			int numeGen = 0;
			if (!ToolsHTML.isEmptyOrNull(numDoc)) {
				numeGen = Integer.parseInt(numDoc);
			} else {
				swNoNull = false;
			}
			int numGenLink = 0;
			long numVerLink = 0;
			String[] dataDoc;
			while (items.hasMoreTokens()) {
				// Se Obtiene el Número del Documento y el Número de la Versión
				// Los mismos se encuentran separados po "_"
				dataDoc = items.nextToken().split("_");

				// System.out.println("dataDoc = " + dataDoc);
				if (!ToolsHTML.isEmptyOrNull(dataDoc[0])) {
					numGenLink = Integer.parseInt(dataDoc[0]);
				} else {
					swNoNull = false;
				}
				if (!ToolsHTML.isEmptyOrNull(dataDoc[1])) {
					numVerLink = Long.parseLong(dataDoc[1]);
				} else {
					swNoNull = false;
				}
				// verificamos que estos valores no sten insertados enla base de
				// datos.. , si
				// existen daria error
				// por repetir primary key
				if (swNoNull) {
					StringBuilder sql = new StringBuilder(
							"Select numGen from  documentslinks where ");
					sql.append(" numGen=").append(numeGen).append(" AND ");
					sql.append(" numGenLink=").append(numGenLink)
							.append(" AND ");
					sql.append(" NumVer=").append(numVer).append(" AND ");
					sql.append(" NumVerLink=").append(numVerLink).append("  ");
					st = con.prepareStatement((sql.toString()));
					rs = st.executeQuery();
					if (rs != null) {
						if (!rs.next()) {
							// Relacion A => B
							st = con.prepareStatement((insert.toString()));
							st.setInt(1, numeGen);
							st.setInt(2, numGenLink);
							st.setLong(3, numVer);
							st.setLong(4, numVerLink);
							st.executeUpdate();
							// Relacion B => A
							st = con.prepareStatement((insert.toString()));
							st.setInt(1, numGenLink);
							st.setInt(2, numeGen);
							st.setLong(3, numVerLink);
							st.setLong(4, numVer);
							st.executeUpdate();
						}
						rs.close();
					}
				}
			}
		}
	}

	public synchronized static void deleteDocumentsLinks(int numeGen,
			int numVer, Connection con) throws SQLException {
		PreparedStatement st = null;
		// Eliminamos la Relacion A => B
		StringBuilder insert = new StringBuilder(
				"DELETE FROM documentslinks WHERE numGen = ? AND numVer = ?");
		st = con.prepareStatement((insert.toString()));
		st.setInt(1, numeGen);
		st.setInt(2, numVer);
		st.executeUpdate();
		// Eliminamos la Relaciones B => A
		insert = new StringBuilder(
				"DELETE FROM documentslinks WHERE numGenLink = ?  AND numVerLink = ?");
		st = con.prepareStatement((insert.toString()));
		st.setInt(1, numeGen);
		st.setInt(2, numVer);
		st.executeUpdate();

	}

	public static void getPreparedStatementDocumentInsert(
			BaseDocumentForm forma, Connection con, Timestamp time,
			Timestamp dateCreation, long numVersion) throws SQLException {
		PreparedStatement st = null;
		StringBuffer fields = new StringBuffer(100);
		StringBuffer insert = new StringBuffer(100);

		if (ToolsHTML.isEmptyOrNull(forma.getDateDead())) {
			forma.setDateDead(ToolsHTML.getDateDeadDoc(con, dateCreation,
					forma.getTypeDocument(), forma.getDateDead()));
		}

		// Nos asegurarmos que el nombre del archivo no tiene una ruta absoluta
		forma.setNameFile(StringUtil.getOnlyNameFile(forma.getNameFile()));

		if (forma.getDocPublic() != null
				&& "0".compareTo(forma.getDocPublic().trim()) == 0) {
			fields.append("?,?,?,?,?,?,?,?,?,?,?,?,?");
			insert.append("INSERT INTO documents (nameDocument,owner,number,type,numGen,IdNode,datePublic,versionPublic,nameFile,statu,lastVersionApproved,contextType,dateCreation");
		} else {
			fields.append("?,?,?,?,?,?,?,?,?,?,?");
			insert.append("INSERT INTO documents (nameDocument,owner,number,type,numGen,IdNode,nameFile,statu,lastVersionApproved,contextType,dateCreation");
		}

		ToolsHTML.setFieldInQuery(insert, fields, forma.getPrefix(), "prefix");
		ToolsHTML
				.setFieldInQuery(insert, fields, forma.getNormISO(), "normISO");
		if (ToolsHTML.isNumeric(forma.getDocProtected()))
			ToolsHTML.setFieldInQuery(insert, fields,
					new Byte(JDBCUtil.getByte(forma.getDocProtected())),
					"docProtected");
		if (ToolsHTML.isNumeric(forma.getDocPublic()))
			ToolsHTML.setFieldInQuery(insert, fields,
					new Byte(JDBCUtil.getByte(forma.getDocPublic())),
					"docPublic");
		if (ToolsHTML.isNumeric(forma.getDocOnline()))
			ToolsHTML.setFieldInQuery(insert, fields,
					new Byte(JDBCUtil.getByte(forma.getDocOnline())),
					"docOnline");
		ToolsHTML.setFieldInQuery(insert, fields, forma.getURL(), "url");
		ToolsHTML.setFieldInQuery(insert, fields, forma.getKeys(), "keysDoc");
		ToolsHTML.setFieldInQuery(insert, fields, forma.getDescript(),
				"descript");
		ToolsHTML.setFieldInQuery(insert, fields, forma.getComments(),
				"comments");
		ToolsHTML.setFieldInQuery(insert, fields,
				Byte.parseByte(forma.getToForFiles()), "toForFiles");

		ToolsHTML.setFieldInQuery(insert, fields, forma.getDateDead(),
				"dateDead");
		ToolsHTML.setFieldInQuery(insert, fields,
				ToolsHTML.isEmptyOrNull(forma.getLoteDoc(), ""), "loteDoc");

		// campos adicionales
		try {
			StringBuilder campo = new StringBuilder();
			for (int i = 1; i <= 100; i++) {
				campo.setLength(0);
				campo.append("D").append(i);
				if (forma.get(campo.toString()) != null
						&& !forma.get(campo.toString()).toString().trim()
								.equals("")) {
					fields.append(",?");
					insert.append(",").append(campo.toString().toLowerCase());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		insert.append(") VALUES (").append(fields.toString()).append(")");
		System.out.println("QUERY HANDLERSTRUCT");
		
		st = con.prepareStatement((insert.toString()));
		st.setString(1, forma.getNameDocument());
		st.setString(2, forma.getOwner());
		st.setString(3, forma.getNumber());
		st.setString(4, forma.getTypeDocument());
		st.setInt(5, Integer.parseInt(forma.getNumberGen()));
		st.setInt(6, Integer.parseInt(forma.getIdNode()));
		int pos = 14;
		if (forma.getDocPublic() != null
				&& "0".compareTo(forma.getDocPublic().trim()) == 0) {
			if (forma.getApproved() != null
					&& "0".compareTo(forma.getApproved()) != 0) {
				forma.setApproved("0");
			}
			st.setTimestamp(7, time);
			st.setLong(8, numVersion);
			// SIMON 13 DE JUNIO 2005 INICIO
			if (ToolsHTML.isEmptyOrNull(forma.getNameFile())) {
				st.setString(9, DesigeConf.getProperty("enlinea"));
				forma.setContextType(DesigeConf.getProperty("contextype"));
			} else {
				st.setString(9, forma.getNameFile());
			}

			// SIMON 13 DE JUNIO 2005 FIN
			st.setString(10, getStatuDoc(forma.getApproved()));
			st.setLong(11,
					getVersionApprovedDoc(forma.getApproved(), numVersion));
			st.setString(12, forma.getContextType());
			st.setTimestamp(13, dateCreation);
		} else {
			// SIMON 13 DE JUNIO 2005 INICIO
			if (ToolsHTML.isEmptyOrNull(forma.getNameFile())) {
				st.setString(7, DesigeConf.getProperty("enlinea"));
				forma.setContextType(DesigeConf.getProperty("contextype"));
			} else {
				st.setString(7, forma.getNameFile());

			}
			// SIMON 13 DE JUNIO 2005 FIN
			st.setString(8, getStatuDoc(forma.getApproved()));
			st.setLong(9,
					getVersionApprovedDoc(forma.getApproved(), numVersion));
			st.setString(10, forma.getContextType());
			st.setTimestamp(11, dateCreation);
			pos = 12;
		}
		pos = ToolsHTML.setValueInQuery(st, forma.getPrefix(), pos);
		pos = ToolsHTML.setValueInQuery(st, forma.getNormISO(), pos);
		Integer values = null;

		if (ToolsHTML.isNumeric(forma.getDocProtected())) {
			values = new Integer(forma.getDocProtected());
			pos = ToolsHTML.setValueInQuery(st, values, pos);
		}
		if (ToolsHTML.isNumeric(forma.getDocPublic())) {
			values = new Integer(forma.getDocPublic());
			pos = ToolsHTML.setValueInQuery(st, values, pos);
		}
		if (ToolsHTML.isNumeric(forma.getDocOnline())) {
			values = new Integer(forma.getDocOnline());
			pos = ToolsHTML.setValueInQuery(st, values, pos);
		}

		pos = ToolsHTML.setValueInQuery(st, forma.getURL(), pos);
		pos = ToolsHTML.setValueInQuery(st, forma.getKeys(), pos);
		pos = ToolsHTML.setValueInQuery(st, forma.getDescript(), pos);

		// SIMON 13 DE JUNIO 2005 INICIO
		if (ToolsHTML.isEmptyOrNull(forma.getNameFile())) {
			// pos =
			// ToolsHTML.setValueInQuery(st,forma.getComments().substring(0,1),pos);
			if (ToolsHTML.isEmptyOrNull(forma.getComments())) {
				pos = ToolsHTML.setValueInQuery(st,
						DesigeConf.getProperty("documentoenlinea"), pos);
			} else {
				pos = ToolsHTML.setValueInQuery(st, forma.getComments(), pos);
			}
		} else {
			pos = ToolsHTML.setValueInQuery(st, forma.getComments(), pos);
		}

		pos = ToolsHTML.setValueInQuery(st,
				Byte.parseByte(forma.getToForFiles()), pos);
		pos = ToolsHTML.setValueInQuery(st,
				ToolsHTML.parseTimestamp(forma.getDateDead(), ToolsHTML.sdf),
				pos);
		pos = ToolsHTML.setValueInQuery(st, forma.getLoteDoc(), pos);

		// campos adicionales
		try {
			StringBuilder campo = new StringBuilder();
			for (int i = 1; i <= 100; i++) {
				campo.setLength(0);
				campo.append("D").append(i);
				if (forma.get(campo.toString()) != null
						&& !forma.get(campo.toString()).toString().trim()
								.equals("")) {
					st.setString(pos++, (String) forma.get(campo.toString()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		st.executeUpdate();
	}

	// 20 DE JUNIO 2005 SIMON INICIO
	// HandlerStruct.actualizarVersionDoc(Integer.parseInt(forma.getNumberGen()),"lastVersionApproved","documents",valor);
	public static synchronized void actualizarVersionDoc(String campocond,
			int numVer, String campo, String tabla, int valor) throws Exception {
		Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		con.setAutoCommit(false);
		StringBuilder edit = new StringBuilder("UPDATE  ");
		edit.append(tabla).append(" SET ");

		edit.append(campo).append("=").append(valor);
		edit.append(" where ").append(campocond).append("=").append(numVer);
		PreparedStatement pstmt = con.prepareStatement((edit.toString()));
		pstmt.executeUpdate();
		pstmt.close();
		con.commit();
		if (con != null) {
			con.close();
		}
	}

	public static synchronized void actualizarVersionDoc(int numVer,
			String campo, int valor) throws Exception {
		Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		con.setAutoCommit(false);
		StringBuilder edit = new StringBuilder("UPDATE versiondoc SET ");
		edit.append(campo).append("=").append(valor);
		edit.append(" where numVer=").append(numVer);
		PreparedStatement pstmt = con.prepareStatement((edit.toString()));
		pstmt.executeUpdate();
		pstmt.close();
		con.commit();
		if (con != null) {
			con.close();
		}
	}

	public static synchronized void actualizarVersionDoc(int numVer,
			String campo, byte valor) throws Exception {
		Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		con.setAutoCommit(false);
		StringBuilder edit = new StringBuilder("UPDATE versiondoc SET ");
		edit.append(campo).append("=CAST(").append(valor).append(" as bit) ");
		edit.append(" where numVer=").append(numVer);
		PreparedStatement pstmt = con.prepareStatement((edit.toString()));
		pstmt.executeUpdate();
		pstmt.close();
		con.commit();
		if (con != null) {
			con.close();
		}
	}

	// 20 DE JUNIO 2005 SIMON FIN

	/*
	 * Actualiza un campo en la tabla versiondoc, filtrando por numVer.
	 * tipoValor = 0 es String tipoValor = 1 es Date tipoValor = 2 es int
	 * 
	 * HandlerStruct.actualizarVersionDoc(numVer, "dayEmailNearExpireSend",
	 * dateSystem, 1);
	 */
	public static synchronized void actualizarVersionDoc(int numVer,
			String campo, String valor, int tipoValor) throws Exception {
		Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		con.setAutoCommit(false);
		StringBuilder edit = new StringBuilder("UPDATE versiondoc SET ");

		if (tipoValor == 0) {
			edit.append(campo).append("='").append(valor).append("'");
		} else if (tipoValor == 1) {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				edit.append(campo).append("=CONVERT(datetime,'").append(valor)
						.append("',120) ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				edit.append(campo).append("=CAST('").append(valor)
						.append("' AS timestamp) ");
				break;

			}
		} else if (tipoValor == 2) {
			edit.append(campo).append("=").append(valor);
		}

		edit.append(" where numVer=").append(numVer);
		PreparedStatement pstmt = con.prepareStatement((edit.toString()));
		pstmt.executeUpdate();
		pstmt.close();
		con.commit();
		if (con != null) {
			con.close();
		}
	}

	public static synchronized void saveBD(Connection con, int id,
			InputStream stream, CheckOutDocForm forma) throws Exception {
		Archivo.writeDocumentToDisk("versiondoc", id, stream);

		// TODO: CAMBIO PARA DOBLE VERSION
		// salva el archivo en la tabla
		saveDocView(con, forma);

	}

	public static synchronized void saveNewVersionBD(Connection con, int id,
			InputStream stream, CheckOutDocForm forma) throws Exception {
		Archivo.writeDocumentToDisk("versiondoc", id, stream);
	}

	// copia el archivo desde doccheckout hacia versiondoc
	public static synchronized void saveBDFromCheckOut(Connection con,
			int numVer, int idCheckOut, CheckOutDocForm forma) throws Exception {
		Archivo.copyDocumentInDisk("doccheckout", idCheckOut, "versiondoc",
				numVer);
		// salva el archivo en la tabla de doble version
		saveDocView(con, forma);

	}

	// TODO: CAMBIO PARA DOBLE VERSION
	public static synchronized void saveBDView(Connection con, int id,
			InputStream stream) throws Exception {
		Archivo.writeDocumentToDisk("versiondocview", id, stream);
	}

	// TODO: CAMBIO PARA DOBLE VERSION
	// saveBD(con,forma.getNameFile(),forma.getComments(),path,forma.getNumVer());
	public static synchronized void saveBD(Connection con, String nameFile,
			String comentarios, String path, int id) throws Exception {
		saveBD(con, nameFile, comentarios, path, id, true);
	}

	public static synchronized void saveBD(Connection con, String nameFile,
			String comentarios, String path, int id, boolean isConvertPDF)
			throws Exception {
		BaseDocumentForm forma = new BaseDocumentForm();
		forma.setNameFile(nameFile);
		forma.setComments(comentarios);
		forma.setNumVer(id);
		saveBD(con, forma, path, true, isConvertPDF);
	}

	// TODO: CAMBIO PARA DOBLE VERSION
	public static synchronized void saveBD(Connection con,
			BaseDocumentForm forma, String path, boolean deleteFile,
			boolean isConvertPDF) throws Exception {
		String nameFile = forma.getNameFile();
		String comentarios = forma.getComments();
		int id = forma.getNumVer();

		String nameFileParalelo = forma.getNameFileParalelo();

		if (ToolsHTML.isEmptyOrNull(nameFile)) {
			FileOutputStream out = new FileOutputStream(path + File.separator
					+ DesigeConf.getProperty("enlinea"));
			// Connect print stream to the output stream
			PrintStream p = new PrintStream(out);
			p.println(comentarios);
			p.close();
			nameFile = DesigeConf.getProperty("enlinea");
		}

		// System.out.println("Salvando Archivo "+path + File.separator +
		// nameFile);
		String fullName = path + File.separator + nameFile;
		File fichero = new File(fullName);
		FileInputStream streamEntrada = new FileInputStream(fichero);

		Archivo.writeDocumentToDisk(con, forma.getNameFile(), "versiondoc", id,
				streamEntrada);
		streamEntrada.close();

		// salva el archivo de visualizacion en la tabla
		if (isConvertPDF) {
			saveDocView(con, forma, path);
		}

		// INFO:ZIP - Eliminamos los archivos involucrados en el proceso
		if (deleteFile) {
			System.gc();
			fichero.delete();
		}

	}

	public static synchronized void saveBD(Connection con, String nameFile,
			String path, int id) throws Exception {
		if (ToolsHTML.isEmptyOrNull(nameFile)) {
			FileOutputStream out = new FileOutputStream(path + File.separator
					+ DesigeConf.getProperty("enlinea"));
			// Connect print stream to the output stream
			PrintStream p = new PrintStream(out);
			p.println("This is written to a file");
			p.close();
			nameFile = DesigeConf.getProperty("enlinea");
		}
		// System.out.println("Salvando Archivo");
		File fichero = new File(path + File.separator + nameFile);
		FileInputStream streamEntrada = new FileInputStream(fichero);

		Archivo.writeDocumentToDisk("versiondoc", id, streamEntrada);

		streamEntrada.close();

		System.gc();
		fichero.delete();
	}

	/**
	 * 
	 * @param con
	 * @param nameUser
	 * @param idWF
	 * @throws Exception
	 * 
	 *             Marca en los participantes del flujo quien lo edito
	 */
	public static synchronized void saveUserEditorBD(Connection con,
			String nameUser, int idWF) throws Exception {
		PreparedStatement pstmt = con
				.prepareStatement(("update user_workflows set editor='1' where idworkflow=? and idUser=?"));
		pstmt.setInt(1, idWF);
		pstmt.setString(2, nameUser);
		pstmt.executeUpdate();
		pstmt.close();
	}

	public static synchronized void copyVersionDoc(Connection con,
			InputStream dataVersion, int id) throws Exception {
		Archivo.writeDocumentToDisk("versiondoc", id, dataVersion);
	}

	public static synchronized void copyVersionDoc(Connection con,
			int idVersionCopy, int id) throws Exception {
		Archivo.copyDocumentInDisk("versiondoc", idVersionCopy, "versiondoc",
				id);
	}

	public static synchronized void copyVersionDocFromCheckOut(Connection con,
			int idCheckOut, int id) throws Exception {
		Archivo.copyDocumentInDisk("doccheckout", idCheckOut, "versiondoc", id);
	}

	public static synchronized void saveDocView(Connection con,
			CheckOutDocForm forma) throws Exception {
		BaseDocumentForm forma2 = new BaseDocumentForm();
		forma2.setNameFile(forma.getNameFile());
		forma2.setNumVer(forma.getNumVer());
		forma2.setNumVer(forma.getNumVer());
		forma2.setNameFileParalelo(forma.getNameFileParalelo());

		saveDocView(con, forma2, forma.getPath());
	}

	public static synchronized void saveDocView(Connection con,
			BaseDocumentForm forma, String path) throws Exception {
		int id = forma.getNumVer();
		String nameFileParalelo = forma.getNameFileParalelo();
		PreparedStatement pstmt = null;

		if (nameFileParalelo != null && !nameFileParalelo.trim().equals("")) {
			try {
				try {
					if (isVersionDocView(con, id) != null) {
						pstmt = con
								.prepareStatement(("UPDATE versiondocview set nameFile=? WHERE numVer=?"));
						pstmt.setString(1,
								StringUtil.getOnlyNameFile(nameFileParalelo));
						pstmt.setInt(2, id);
						pstmt.executeUpdate();
						pstmt.close();
					} else {
						pstmt = con
								.prepareStatement(("INSERT INTO versiondocview (numVer,nameFile) values(?,?) "));
						pstmt.setInt(1, id);
						pstmt.setString(2,
								StringUtil.getOnlyNameFile(nameFileParalelo));
						pstmt.executeUpdate();
						pstmt.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				// System.out.println("Salvando Archivo");
				File fichero2 = new File(path + File.separator
						+ nameFileParalelo);
				FileInputStream streamEntrada = new FileInputStream(fichero2);

				Archivo.writeDocumentToDisk("versiondocview", id, streamEntrada);
				streamEntrada.close();

				// eliminamos el archivo
				System.gc();
				fichero2.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (Constants.PRINTER_PDF) {
				try {
					String nombreArchivo = StringUtil.getOnlyNameFile(forma
							.getNameFile());
					if (forma.getDocOnline() != null
							&& forma.getDocOnline().equals("0")
							|| ToolsHTML.isEmptyOrNull(nombreArchivo)) {
						// FileOutputStream out = new FileOutputStream(path +
						// File.separator + DesigeConf.getProperty("enlinea"));
						// PrintStream p = new PrintStream(out);
						// p.close();
						nombreArchivo = DesigeConf.getProperty("enlinea");
					}

					File fichero = new File(path + File.separator
							+ nombreArchivo);
					if (!fichero.exists()) {
						// si no esta el fichero lo buscaremos en el repositorio
						// y lo ponemos en la carpeta temporal para convertirlo
						// luego a pdf
						InputStream in = Archivo.readDocumentFromDisk(
								"versiondoc", id);
						Archivo.copyfile(in, fichero);
						in.close();
					}
					fichero = new File(path + File.separator + nombreArchivo);
					FileInputStream streamEntrada = new FileInputStream(fichero);
					Archivo.deleteDocumentCachedInDisk(id);
					Archivo.writeDocumentToPdfDisk(con, fichero,
							"versiondocview", id, streamEntrada);
					streamEntrada.close();
					if (fichero.exists()) {
						System.gc();
						fichero.delete();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Crea al archivo físico, sacando la informacion de la base de datos y
	 * colocandolo en el path indicado.
	 * 
	 * @param forma
	 *            el bean que representa el archivo a mostrar, contiene
	 * @param path
	 *            la ruta del archivo que se va a consultar.
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public static BaseDocumentForm createFilePDF(BaseDocumentForm forma,
			String path) throws ApplicationExceptionChecked {
		return createFile(forma, path, false, false, true);
	}

	public static BaseDocumentForm createFile(BaseDocumentForm forma,
			String path) throws ApplicationExceptionChecked {
		return createFile(forma, path, false, false);
	}

	public static BaseDocumentForm createFile(BaseDocumentForm forma,
			String path, boolean editar) throws ApplicationExceptionChecked {
		return createFile(forma, path, editar, false);
	}

	public static BaseDocumentForm createFile(BaseDocumentForm forma,
			String path, boolean editar, boolean download)
			throws ApplicationExceptionChecked {
		return createFile(forma, path, editar, download, false);
	}

	public static BaseDocumentForm createFile(BaseDocumentForm forma,
			String path, boolean editar, boolean download, boolean onlyPDF)
			throws ApplicationExceptionChecked {
		Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		InputStream imagenRecuperada = null;
		boolean isDobleVersion = false;

		// TODO: CAMBIO PARA DOBLE VERSION
		String newNameFile = isVersionDocView(con, forma.getNumVer());
		if (!editar && newNameFile != null && !newNameFile.trim().equals("")) {
			isDobleVersion = true;
			forma.setNameFile(newNameFile.trim());
			imagenRecuperada = recuperarFileBDView(forma.getNumVer());
		} else {
			imagenRecuperada = recuperarFileBD(forma.getNumVer());
		}

		if (imagenRecuperada != null) {
			if ((onlyPDF || !editar && Constants.PRINTER_PDF && !download)) {
				if (!forma.isExpediente()
						&& (forma.getNameFile().toLowerCase().endsWith("jpg")
								|| forma.getNameFile().toLowerCase()
										.endsWith("jpeg") || forma
								.getNameFile().toLowerCase().endsWith("gif"))) {
					// si es una imagen la almacenamos en disco sin convertirla
					// a pdf
					saveFileInDisk(imagenRecuperada, path, forma.getNameFile());
				} else {
					saveFileInDiskPDF(imagenRecuperada, path,
							forma.getNameFile(), forma, isDobleVersion);
				}
			} else {
				saveFileInDisk(imagenRecuperada, path, forma.getNameFile());
			}

			if (forma.getNameFile().endsWith(".pdf")) {
				forma.setContextType("application/pdf");
			}
		} else {
			throw new ApplicationExceptionChecked("E0009");
		}
		try {
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return forma;
	}

	/**
	 * 
	 * @param forma
	 * @param path
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public static BaseNormsForm createFileNorms(BaseNormsForm forma, String path)
			throws ApplicationExceptionChecked {
		
		String norms =forma.getId();
		int pos = norms.indexOf(',');
		if (pos != -1) {
			// Toma solo primera norma
			norms = norms.substring(0,pos);
		}
		
		Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		InputStream imagenRecuperada = recuperarFileBDNorms(con,
				Integer.parseInt(norms));
		if (imagenRecuperada != null) {
			saveFileInDisk(imagenRecuperada, path, forma.getFileNameFisico());
		} else {
			throw new ApplicationExceptionChecked("E0009");
		}
		DbUtils.closeQuietly(con);
		return forma;
	}

	/**
	 * 
	 * @param forma
	 * @param path
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	/*
	public static Plantilla1BD createFileSacop(Plantilla1BD forma, String path)
			throws ApplicationExceptionChecked {
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(
					forma.getData());
			InputStream imagenRecuperada = (InputStream) stream;

			if (imagenRecuperada != null) {
				saveFileInDisk(imagenRecuperada, path, forma.getNameFile());
			} else {
				throw new ApplicationExceptionChecked("E0009");
			}
		} catch (ApplicationExceptionChecked ae) {
			throw ae;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return forma;
	}
	*/

	/**
	 * 
	 * @param con
	 * @param numVer
	 * @return
	 */
	public static String isVersionDocView(Connection con, int numVer) {
		Connection conLocal = null;
		ResultSet rs = null;
		Statement stmt = null;
		String nameFile = null;
		try {
			conLocal = (con == null) ? JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName()) : con;
			stmt = conLocal.createStatement();

			// TODO: CAMBIO PARA DOBLE VERSION
			// consulta si hay una version paralela
			String consultaDobleVersion = "SELECT nameFile FROM versiondocview WHERE numVer = "
					+ numVer;
			rs = stmt.executeQuery(consultaDobleVersion);
			if (rs.next()) {
				nameFile = rs.getString(1);
			}
			// FIN: CAMBIO PARA DOBLE VERSION
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeQuietly(stmt, rs);
			if (con == null) {
				setFinally(conLocal);
			}
		}
		return nameFile;
	}

	/**
	 * Extrae el archivo de la tabla de la base de datos y la retorna como un
	 * I.S.
	 * 
	 * @param con
	 * @param numVer
	 * @return
	 */
	public static InputStream recuperarFileBD(int numVer) {
		try {
			return Archivo.readDocumentFromDisk("versiondoc", numVer);
		} catch (Exception e) {
			e.printStackTrace();
			setMensaje(formatMessage(e.getMessage()));
		}

		return null;
	}

	// TODO: CAMBIO PARA DOBLE VERSION
	/**
	 * 
	 * Extrae el archivo de la tabla de la base de datos de archivos
	 * visualizadores y la retorna como un I.S.
	 * 
	 * @param con
	 * @param numVer
	 * @return
	 */
	public static InputStream recuperarFileBDView(int numVer) {

		try {
			return Archivo.readDocumentFromDisk("versiondocview", numVer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param con
	 * @param numVer
	 * @return
	 */
	public static InputStream recuperarFileBDNorms(Connection con, int numVer) {

		Connection conLocal = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conLocal = (con == null) ? JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName()) : con;
			String consultaGenerada = new StringBuilder("SELECT namefile FROM norms WHERE idnorm=").append(numVer).toString();
//			String consultaGenerada = "SELECT data FROM norms WHERE idNorm = "
//					+ numVer;
			log.debug("consultaGenerada = " + consultaGenerada);
			pst = conLocal.prepareStatement((consultaGenerada));
			rs = pst.executeQuery();
			if (rs.next()) {
				// return FileZip.unZip(results.getBinaryStream("data"));
//				byte[] data = results.getBytes("data");
//				InputStream is = new ByteArrayInputStream(data);
//				return FileZip.unZip(is);
				String source = new StringBuilder(2048).append(HandlerNorms.getNormasPath())
						.append(File.separator).append(rs.getString("namefile")).toString();
				File sourceFile = new File(source);
				if (sourceFile.exists()) {
					return new FileInputStream(sourceFile);
				} else {
					return null;
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
			if (con == null) {
				setFinally(conLocal);
			}
		}

		return null;
	}

	/**
	 * Crea el archivo físico en el disco. tomando los bytes del I.S. pasado
	 * como parametro.
	 * 
	 * 
	 * @param imagenBuffer
	 * @param path
	 * @param nameFile
	 */
	public static void saveFileInDisk(InputStream imagenBuffer, String path,
			String nameFile) {
		FileOutputStream out = null;
		try {
			String ruta = path + File.separator + nameFile;
			log.debug("Salvando Archivo: " + ruta);
			File fichero = new File(ruta);
			if (fichero.exists()) {
				System.gc();
				fichero.delete();
			}

			 out = new FileOutputStream(fichero);
			 IOUtils.copy(imagenBuffer, out);

//			BufferedInputStream in = new BufferedInputStream(imagenBuffer);
//			out = new FileOutputStream(fichero);
//			byte[] bytes = new byte[8096];
//			int len = 0;
//			while ((len = in.read(bytes)) > 0) {
//				out.write(bytes, 0, len);
//			}
//			out.flush();
//			out.close();
//			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(imagenBuffer);

		}
	}

	/**
	 * Crea el archivo físico en el disco. tomando los bytes del I.S. pasado
	 * como parametro.
	 * 
	 * 
	 * @param imagenBuffer
	 * @param path
	 * @param nameFile
	 */
	public static void saveFileInDiskPDFVeryPdf(InputStream imagenBuffer,
			String path, String nameFile) {
		try {
			String ruta = path + File.separator + nameFile;
			log.debug("Salvando Archivo: " + ruta);
			File fichero = new File(ruta);
			BufferedInputStream in = new BufferedInputStream(imagenBuffer);
			BufferedOutputStream out = new BufferedOutputStream(
					new FileOutputStream(fichero));
			byte[] bytes = new byte[8096];
			int len = 0;
			while ((len = in.read(bytes)) > 0) {
				out.write(bytes, 0, len);
			}
			out.flush();
			out.close();
			in.close();

			// ejecutamos la rutina que transformara el archivo en pdf
			try {
				StringBuilder sb = new StringBuilder();
				String pdf = ruta.substring(0, ruta.indexOf("."))
						.concat(".pdf");
				sb.append("html2pdf.exe \"");
				sb.append(ruta);
				sb.append("\" \"");
				sb.append(pdf);
				sb.append("\"");
				// System.out.println(sb.toString());
				Process p = Runtime.getRuntime().exec(sb.toString());

				// Se obtiene el stream de salida del programa
				InputStream is = p.getInputStream();

				// Se prepara un bufferedReader para poder leer la salida más
				// comodamente.
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));

				// Se lee la primera linea
				String aux = br.readLine();

				// Mientras se haya leido alguna linea
				while (aux != null) {
					// Se escribe la linea en pantalla
					// System.out.println(aux);

					// y se lee la siguiente.
					aux = br.readLine();
				}
			} catch (Exception e) {
				// Se lanza una excepción si no se encuentra en ejecutable o el
				// fichero no es
				// ejecutable.
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Crea el archivo físico en el disco. tomando los bytes del I.S. pasado
	 * como parametro.
	 * 
	 * 
	 * @param imagenBuffer
	 * @param path
	 * @param nameFile
	 */
	public static void saveFileInDiskPDF(InputStream imagenBuffer, String path,
			String nameFile, BaseDocumentForm forma, boolean isDobleVersion) {
		try {

			String ruta = path + File.separator
					+ StringUtil.getOnlyNameFile(nameFile);
			log.info("Salvando Archivo: " + ruta);

			if (ruta.toLowerCase().endsWith(".ai")) {
				// renombramos a pdf
				ruta = ruta.substring(0, ruta.length() - 2).concat("pdf");
				forma.setNameFile(StringUtil.getOnlyNameFile(ruta));
				nameFile = StringUtil.getOnlyNameFile(ruta);
			}
			String nameFileCache = Archivo.getNameFileEncripted(null,
					"versiondocview", forma.getNumVer(), null);
			File cache = new File(nameFileCache);

			boolean existeCache = cache.exists();

			// verificamos si hay que generar nuevamente el cache
			if ("1".equals(HandlerParameters.PARAMETROS.getDisabledCache())
					&& !isDobleVersion) {
				cache.delete();
				existeCache = false;
			}

			// eliminamos el cache de un archivo pdf ya que el mismo es
			// visulizador
			if (ruta.toLowerCase().endsWith(".pdf") && existeCache
					&& !isDobleVersion) {
				cache.delete();
				existeCache = false;
			}

			if (!existeCache) {
				File fichero = new File(ruta);
				if (fichero.exists()) {
					System.gc();
					fichero.delete();
				}

				fichero = new File(ruta);
				BufferedInputStream in = new BufferedInputStream(imagenBuffer);
				BufferedOutputStream out = new BufferedOutputStream(
						new FileOutputStream(fichero));
				byte[] bytes = new byte[8096];
				int len = 0;
				while ((len = in.read(bytes)) > 0) {
					out.write(bytes, 0, len);
				}
				out.flush();
				out.close();
				in.close();
			}

			// ejecutamos la rutina que transformara el archivo en pdf
			try {
				String rutaPdf = ruta;

				// if (!ruta.toLowerCase().endsWith(".pdf")) {
				PdfConvert pdfConvert = new PdfConvert();
				log.info("Iniciamos conversion a PDF -> " + rutaPdf);
				// si existe el documento
				rutaPdf = pdfConvert.saveToPdf(ruta, forma.getNumVer(),
						isDobleVersion);
				log.info("Finalizada conversion a PDF-> " + rutaPdf);
				// }

				if (rutaPdf != null && rutaPdf.toLowerCase().endsWith(".pdf")) {
					forma.setNameFile(pdfConvert.getNameFile());

					PdfSecurity pdfSecurity = new PdfSecurity(rutaPdf);

					String fileInicio = "";
					if (forma.getFileInicio() == null) {
						forma.setFileInicio("pag"
								.concat(String.valueOf(forma.getIdUser()))
								.concat("_").concat(forma.getIdDocument())
								.concat("_")
								.concat(String.valueOf(forma.getNumVer()))
								.concat(".pdf"));
					}
					fileInicio = ToolsHTML.getPathTmp().concat(
							forma.getFileInicio());
					File inicio = new File(fileInicio);
					if (forma.isExpediente()
							&& !ToolsHTML.isEmptyOrNull(forma.getFileInicio())
							&& inicio.exists()) {
						// no añadimos la pagina inicial de firmantes
						// seran impresos individualmente los archivos
						// ---
						// Esta rutina tambien uno los archivos de expediente,
						// no deshabilitarla
						pdfSecurity.addFileBefore(fileInicio);
					}

					pdfSecurity.setCopyContents(ToolsHTML.copyContents()
							&& forma.isCopyContents());

					log.info("forma.getNumVerFiles()=" + forma.getNumVerFiles());
					log.info("forma.isExpediente()=" + forma.isExpediente());

					if (forma.getNumVerFiles() == 0 && forma.isExpediente()) {
						if (forma.isExpediente()) {
							pdfSecurity.setBackground(PdfSecurity.BG_ERASER);
						} else {
							pdfSecurity.setBackground(PdfSecurity.BG_PREVIEW);
						}
					} else {
						if (!forma.isExpediente()) {
							pdfSecurity.setBackground(PdfSecurity.BG_PREVIEW);

							boolean isPrintable = HandlerDocuments
									.isDocumentStatuVersionPrintable(Integer
											.parseInt(forma.getIdDocument()),
											forma.getNumVer());

							if (isPrintable) {
								/*
								 * String version = null; version =
								 * String.valueOf
								 * (forma.getMayorVer()).concat("."
								 * ).concat(String
								 * .valueOf(forma.getMinorVer()));
								 * PdfSecurity.setBackgroundId
								 * (pdfSecurity.getNameFilePdf(),
								 * forma.getIdDocument(),
								 * (forma.getPrefix()!=null
								 * ?forma.getPrefix():"")
								 * .concat(forma.getNumber()), version );
								 */
								log.warn("Se comento la manera en la que se agregaba la informacion de version en el pie de pagina del documento."
										+ " A partir de la version 4.5.2 esto se hace en el cliente y no en el servidor.");
							} else {
								log.info("El documento "
										+ pdfSecurity.getNameFilePdf()
										+ "(id="
										+ forma.getIdDocument()
										+ ") no posee el status requerido para que la informacion de pie de pagina sea agregada.");
							}
						}
					}

					pdfSecurity.setControlada(forma.isControlada());
					pdfSecurity.setPrinting(forma.isPrinting());
					pdfSecurity.setIdUser(forma.getIdUser());
					pdfSecurity.applySecurity(forma, forma.isExpediente(),
							forma.getIdFiles());
				}
			} catch (Exception e) {
				// Se lanza una excepción si no se encuentra en ejecutable o el
				// fichero no es
				// ejecutable.
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static boolean editDocument(BaseDocumentForm forma,
			Users usuario, ResourceBundle rb)
			throws ApplicationExceptionChecked {
		boolean resp = false;
		StringBuffer edit = new StringBuffer(
				"UPDATE documents SET nameDocument = ? , owner = ? , type = ?");
		ToolsHTML.setFieldInQueryEdit(edit, forma.getNormISO(), "normISO");
		ToolsHTML
				.setFieldInQueryEdit(edit,
						JDBCUtil.getByte(forma.getDocProtected(), true),
						"docProtected");
		ToolsHTML.setFieldInQueryEdit(edit,
				JDBCUtil.getByte(forma.getDocPublic(), true), "docPublic");
		ToolsHTML.setFieldInQueryEdit(edit,
				JDBCUtil.getByte(forma.getDocOnline(), true), "docOnline");
		ToolsHTML.setFieldInQueryEdit(edit, forma.getURL(), "url");
		ToolsHTML.setFieldInQueryEdit(edit, forma.getKeys(), "keysDoc");
		ToolsHTML.setFieldInQueryEdit(edit, forma.getDescript(), "descript");
		ToolsHTML.setFieldInQueryEditAcceptNull(edit, forma.getNumber(),
				"number");
		ToolsHTML.setFieldInQueryEdit(edit,
				JDBCUtil.getByte(forma.getToForFiles(), true), "toForFiles");
		ToolsHTML.setFieldInQueryEdit(edit, forma.getDateDead(), "dateDead");
		ToolsHTML.setFieldInQueryEdit(edit, forma.getLoteDoc(), "loteDoc");

		// campos adicionales en la tabla documents
		ConfDocumentoDAO conf = null;
		ArrayList lista = null;
		DocumentForm obj;
		StringBuilder camposDeUbicacion = new StringBuilder();
		HashMap listaCamposUbicacion = new HashMap();
		try {
			conf = new ConfDocumentoDAO();
			lista = (ArrayList) conf.findAll();

			if (lista != null) {
				for (int i = 0; i < lista.size(); i++) {
					obj = (DocumentForm) lista.get(i);
					ToolsHTML.setFieldInQueryEditAcceptNull(edit,
							forma.get(obj.getId().toUpperCase()), obj.getId());
					if (obj.getLocation() == 1) {
						listaCamposUbicacion.put(obj.getId(), obj);
						camposDeUbicacion
								.append(camposDeUbicacion.length() == 0 ? ""
										: ",");
						camposDeUbicacion.append(obj.getId());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		log.debug("forma.getStatuDoc() = " + forma.getStatuDoc());
		log.debug("forma.getStatu() = " + forma.getStatu());
		if ("0".equalsIgnoreCase(forma.getDocPublic())) {
			// if
			// (!HandlerDocuments.docApproved.equalsIgnoreCase(forma.getStatuDoc()))
			// {
			// throw new ApplicationExceptionChecked("E0033");
			// }

			ToolsHTML.setFieldInQueryEdit(edit, forma.getDatePublic(),
					"datePublic");
			ToolsHTML.setFieldInQueryEdit(edit, forma.getDatePublic(),
					"versionPublic");
		}
		edit.append(" WHERE numGen = ").append(forma.getNumberGen());
		log.debug("edit = " + edit);
		Connection con = null;
		PreparedStatement st = null;
		try {

			// primero vamos a consultar el nombre y propietario actual del
			// documento
			ArrayList parametros = new ArrayList();
			StringBuilder query = new StringBuilder(
					"select nameDocument, owner ");
			if (camposDeUbicacion.length() > 0) {
				query.append(",");
				query.append(camposDeUbicacion);
			}
			query.append(" from documents where numgen=?");
			parametros.add(new Integer(forma.getNumberGen()));

			CachedRowSet crsDocument = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());

			// obtenemos el nombre completo del propietario anterior
			String ownerAfter = "";
			String mailOwnerAfter = "";
			if (crsDocument.next()) {
				ownerAfter = HandlerDBUser.getNameUser(crsDocument
						.getString("owner"));
				mailOwnerAfter = HandlerDBUser.getMail(crsDocument
						.getString("owner"));
			}
			crsDocument.beforeFirst();
			String mailOwnerBefore = HandlerDBUser.getMail(forma.getOwner());

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);
			st = con.prepareStatement((edit.toString()));
			st.setString(1, forma.getNameDocument());
			st.setString(2, forma.getOwner());
			st.setString(3, forma.getTypeDocument());
			int pos = 4;
			pos = ToolsHTML.setValueInQuery(st, forma.getNormISO(), pos);
			Integer values = null;
			if (ToolsHTML.isNumeric(forma.getDocProtected())) {
				values = new Integer(forma.getDocProtected());
				pos = ToolsHTML.setValueInQuery(st, values, pos);
			}
			if (ToolsHTML.isNumeric(forma.getDocPublic())) {
				values = new Integer(forma.getDocPublic());
				pos = ToolsHTML.setValueInQuery(st, values, pos);
			}
			if (ToolsHTML.isNumeric(forma.getDocOnline())) {
				values = new Integer(forma.getDocOnline());
				pos = ToolsHTML.setValueInQuery(st, values, pos);
			}
			pos = ToolsHTML.setValueInQuery(st, forma.getURL(), pos);
			pos = ToolsHTML.setValueInQuery(st, forma.getKeys(), pos);
			pos = ToolsHTML.setValueInQuery(st, forma.getDescript(), pos);
			pos = ToolsHTML.setValueInQueryAcceptNull(st, forma.getNumber(),
					pos);
			pos = ToolsHTML.setValueInQuery(st,
					new Integer(forma.getToForFiles()), pos);
			pos = ToolsHTML.setValueInQuery(st, ToolsHTML.parseTimestamp(
					forma.getDateDead(), ToolsHTML.date), pos);
			pos = ToolsHTML.setValueInQuery(st, forma.getLoteDoc(), pos);

			// campos adicionales en la tabla documents
			try {
				if (lista != null) {
					for (int i = 0; i < lista.size(); i++) {
						obj = (DocumentForm) lista.get(i);
						pos = ToolsHTML.setValueInQueryAcceptNull(st,
								forma.get(obj.getId().toUpperCase()), pos);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if ("0".equalsIgnoreCase(forma.getDocPublic())) {
				java.util.Date fecha = ToolsHTML.date.parse(forma
						.getDatePublic());
				st.setTimestamp(pos, new Timestamp(fecha.getTime()));
				pos++;
				st.setLong(pos, forma.getNumVer());
			}
			st.executeUpdate();
			deleteDocumentsLinks(Integer.parseInt(forma.getNumberGen()),
					forma.getNumVer(), con);
			getPreparedStatementDocumentLinks(forma, con);

			// actualizamos el nombre de documento y el propietario en la
			// version :JAIRO:
			if (crsDocument.next()) {
				// si el nombre actual es diferente al nombre nuevo lo
				// actualizamos
				if (!ToolsHTML.isEmptyOrNull(
						crsDocument.getString("nameDocument"), "").equals(
						forma.getNameDocument())) {
					edit.setLength(0);
					edit.append("update versiondoc set nameDocVersion='")
							.append(forma.getNameDocument()).append("' ");
					//TODO crear este ticket 
					//ydavila sin ticket propiedades cambiar nombre
					//edit.append("where versiondoc.numver=");
					edit.append("where versiondoc.numver in(");
					edit.append((new StringBuffer("select max(numver) from versiondoc where numdoc=").append(forma.getNumberGen()))
					//ydavila sin ticket propiedades cambiar nombre
					.append(")"));
					st = con.prepareStatement((edit.toString()));
					st.executeUpdate();

					// dejamos la traza en el historico
					int numdoc = Integer.parseInt(forma.getNumberGen());
					int node = Integer.parseInt(ToolsHTML.isEmptyOrNull(
							forma.getIdNode(), "0"));
					forma.setRazonDeCambio(forma.getRazonDeCambioName());
					String razonHistory = EditWorkFlowAction
							.getMessageChangeNameHistory(forma,
									crsDocument.getString("nameDocument"), rb);
					HandlerDocuments.updateHistoryDocs(con, numdoc, node, 0,
							usuario.getUser(),
							new Timestamp(new Date().getTime()), "15",
							razonHistory, new String[] { forma.getMayorVer(),
									forma.getMinorVer() });

					MailForm formaMail = new MailForm();
					formaMail.setFrom(HandlerParameters.PARAMETROS.getMailAccount());
					formaMail.setNameFrom(rb.getString("mail.nameUser"));
					formaMail.setTo(mailOwnerAfter);
					formaMail.setSubject(rb.getString("doc.subjectChangeName"));
					formaMail.setMensaje(EditWorkFlowAction
							.getMessageChangeName(forma,
									crsDocument.getString("nameDocument"), rb,
									usuario.getNamePerson()));
					try {
						if (formaMail != null) {
							SendMailTread mail = new SendMailTread(formaMail);
							mail.start();
						}
					} catch (Exception ex) {
						log.debug("Exception");
						log.error(ex.getMessage());
						ex.printStackTrace();
					}

				}

				// si el propietario actual es diferente al propietario nuevo lo
				// actualizamos
				if (!ToolsHTML
						.isEmptyOrNull(crsDocument.getString("owner"), "")
						.equals(forma.getOwner())) {
					edit.setLength(0);
					edit.append("update versiondoc set ownerVersion='");
					edit.append(forma.getOwner()).append("' ");
					edit.append("where versiondoc.numver=");
					edit.append((new StringBuffer("select max(numver) from versiondoc where numdoc=").append(forma.getNumberGen())));
					st = con.prepareStatement((edit.toString()));
					st.executeUpdate();

					// dejamos la traza en el historico
					int numdoc = Integer.parseInt(forma.getNumberGen());
					int node = Integer.parseInt(ToolsHTML.isEmptyOrNull(
							forma.getIdNode(), "0"));
					forma.setRazonDeCambio(forma.getRazonDeCambioOwner());
					String razonHistory = EditWorkFlowAction
							.getMessageChangeOwnerHistory(forma, ownerAfter, rb);
					HandlerDocuments.updateHistoryDocs(con, numdoc, node, 0,
							usuario.getUser(),
							new Timestamp(new Date().getTime()), "16",
							razonHistory, new String[] { forma.getMayorVer(),
									forma.getMinorVer() });

					MailForm formaMail = new MailForm();
					formaMail.setFrom(HandlerParameters.PARAMETROS.getMailAccount());
					formaMail.setNameFrom(rb.getString("mail.nameUser"));
					formaMail.setTo(ToolsHTML.isEmptyOrNull(mailOwnerAfter, "")
							.concat(";").concat(mailOwnerBefore));
					formaMail
							.setSubject(rb.getString("doc.subjectChangeOwner"));
					formaMail.setMensaje(EditWorkFlowAction
							.getMessageChangeOwner(forma, ownerAfter, rb,
									usuario.getNamePerson()));
					try {
						if (formaMail != null) {
							SendMailTread mail = new SendMailTread(formaMail);
							mail.start();
						}
					} catch (Exception ex) {
						log.debug("Exception");
						log.error(ex.getMessage());
						ex.printStackTrace();
					}
				}

				// si es cambiado el valor para pertenercer a expedientes lo
				// dejamos en el historico
				if (!ToolsHTML.isEmptyOrNull(forma.getRazonDeCambioFiles())) {
					int numdoc = Integer.parseInt(forma.getNumberGen());
					int node = Integer.parseInt(ToolsHTML.isEmptyOrNull(
							forma.getIdNode(), "0"));
					String historyType = (forma.getToForFiles().equals("0") ? "19"
							: "20");
					forma.setRazonDeCambio(forma.getRazonDeCambioFiles());
					String razonHistory = EditWorkFlowAction
							.getMessageChangeFiles(forma, rb);
					HandlerDocuments.updateHistoryDocs(con, numdoc, node, 0,
							usuario.getUser(),
							new Timestamp(new Date().getTime()), historyType,
							razonHistory, new String[] { forma.getMayorVer(),
									forma.getMinorVer() });
					forma.setRazonDeCambioFiles(null);
				}

				// Si fue cambiada la ubicacion se deja en la traza
				boolean isChangeLocation = false;
				Iterator ite = listaCamposUbicacion.values().iterator();
				String valorActual = "";
				String valorAnterior = "";
				while (ite.hasNext()) {
					obj = (DocumentForm) ite.next();
					valorAnterior = String.valueOf(crsDocument.getString(obj
							.getId()) == null ? "" : crsDocument.getString(obj
							.getId()));
					valorActual = (String) forma.get(obj.getId().toUpperCase());
					if (!valorAnterior.equalsIgnoreCase(valorActual)) {
						isChangeLocation = true;
						break;
					}
				}
				if (isChangeLocation) {
					ite = listaCamposUbicacion.values().iterator();
					int numdoc = Integer.parseInt(forma.getNumberGen());
					int node = Integer.parseInt(ToolsHTML.isEmptyOrNull(
							forma.getIdNode(), "0"));
					String historyType = "23";
					String razonHistory = getHistoryLocationHTML(ite, forma,
							crsDocument);
					HandlerDocuments.updateHistoryDocs(con, numdoc, node, 0,
							usuario.getUser(),
							new Timestamp(new Date().getTime()), historyType,
							razonHistory, new String[] { forma.getMayorVer(),
									forma.getMinorVer() });
				}
			}

			con.commit();
			resp = true;
		} catch (Exception ex) {
			applyRollback(con);
			ex.printStackTrace();
			setMensaje(ex.getMessage());
		} finally {
			setFinally(con, st);
		}
		return resp;
	}

	private static String getInsertStruct(BaseStructForm forma) throws Exception {
		StringBuffer insert = new StringBuffer("INSERT INTO struct (IdNode,Name,IdNodeParent,NameIcon,NodeType,");
		//ydavila Ticket 001-00-003023
		//insert.append("Owner,heredarPrefijo,toListDist,Description,DateCreation) VALUES(");
		insert.append("Owner,heredarPrefijo,toListDist,respsolcambio,respsolelimin,respsolimpres, Description,DateCreation) VALUES(");
		insert.append(forma.getIdNode()).append(",'").append(forma.getName()).append("','").append(forma.getIdNodeParent());
		insert.append("','").append(forma.getNameIcon()).append("','").append(forma.getNodeType());
		insert.append("','").append(forma.getOwner());
		insert.append("','").append(forma.getHeredarPrefijo());
		insert.append("',").append(forma.getToListDist());
		//ydavila Ticket 001-00-003023
		insert.append(",'").append(forma.getrespsolcambio()).append("'");
		insert.append(",'").append(forma.getrespsolelimin()).append("'");
		insert.append(",'").append(forma.getrespsolimpres()).append("'");
		insert.append(", ? ,? )");
		System.out.println("insert.toString() = " + insert.toString());
		return insert.toString();
	}

	/**
	 * Este método permite agregar un nuevo nodo a la aplicación el tipo del
	 * mismo viene indicado como parámetro
	 * 
	 * @param forma
	 * @return
	 * @throws Exception
	 */
	public synchronized static boolean insert(BaseStructForm forma)
			throws Exception {
		return insert(null, forma);
	}

	public synchronized static boolean insert(Connection con,
			BaseStructForm forma) throws Exception {
		boolean cerrar = false;
		PreparedStatement st = null;
		int nodeType = Integer.parseInt(forma.getNodeType());
		String table = getTableName(nodeType);
		Vector securityGroup = (Vector) HandlerGrupo
				.getAllSecurityForGroupAndIdNode(forma.getIdNodeParent());
		Vector securityUser = (Vector) HandlerDBUser
				.getAllSecurityForUsersAndIdNode(forma.getIdNodeParent());
		boolean resp = false;
		try {
			// agarramos el consecutivo
			int num = proximo("struct", "idnode");
			forma.setIdNode(String.valueOf(num));
			boolean editNode = checkExist(table, forma.getIdNode());
			if (con == null) {
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				cerrar = true;
			}
			con.setAutoCommit(false);
			// int idHistoryStruct = IDDBFactorySql.getNextID("historystruct");
			int idHistoryStruct = proximo("historystruct", "idHistory");
			java.util.Date date = new Date();
			String dateCreation = ToolsHTML.sdf.format(date);
			forma.setDateCreation(dateCreation);

			st = con.prepareStatement((getInsertStruct(forma)));
			st.setString(1, forma.getDescription());
			st.setTimestamp(2, new Timestamp(date.getTime()));
			st.executeUpdate();

			// actualiza los parametros por localidad en tablas asociadas.
			updateTableAssociate(table, forma, editNode, con);

			// agregamos los usuarios de la lista de distribucion
			addListDistToStruct(forma, con, st);

			setPrepareStaHistory(con, String.valueOf(num), "0", "0",
					forma.getOwner(), DataHistoryStructForm.creation,
					new Timestamp(date.getTime()), idHistoryStruct, null);
			String idForm = String.valueOf(num);
			PermissionUserForm permissionUserForm = null;
			for (int row = 0; row < securityGroup.size(); row++) {
				permissionUserForm = (PermissionUserForm) securityGroup
						.elementAt(row);
				permissionUserForm.setIdStruct(idForm);
				HandlerGrupo.insertSecurityStructGroup(permissionUserForm,
						permissionUserForm.getIdStruct(), con);
			}
			for (int row = 0; row < securityUser.size(); row++) {
				permissionUserForm = (PermissionUserForm) securityUser
						.elementAt(row);
				permissionUserForm.setIdStruct(idForm);
				HandlerDBUser.insertSecurityStructUser(permissionUserForm,
						permissionUserForm.getIdStruct(), con);
			}
			con.commit();
			resp = true;
		} catch (Exception e) {
			e.printStackTrace();
			if (cerrar) {
				applyRollback(con);
				setMensaje(e.getMessage());
				resp = false;
			}
		} finally {
			if (con != null) {
				if (cerrar) {
					con.close();
				} else {
					con.setAutoCommit(true);
				}
			}
			if (st != null) {
				st.close();
			}
		}
		return resp;
	}

	private static void setPrepareStaHistory(Connection con, String nodeChange,
			String newFather, String nodeAnt, String user, String type,
			Timestamp time, int idHistory, String comments) throws Exception {
		if (con.getAutoCommit()) {
			throw new Exception("La conexión es Inválida");
		}
		log.debug("Update History to Struct...");
		PreparedStatement st = null;
		StringBuilder history = new StringBuilder(
				"INSERT INTO historystruct (idHistory,idNode,idNodeFatherAnt,idNodeFatherNew,");
		history.append("nameUser,dateChange,type,comments) VALUES(?,?,?,?,?,?,?,?)");
		st = con.prepareStatement((history.toString()));
		log.debug("history.toString() = " + history.toString());
		st.setInt(1, idHistory);
		st.setInt(2, Integer.parseInt(nodeChange.trim()));
		st.setInt(3, Integer.parseInt(nodeAnt.trim()));
		st.setInt(4, Integer.parseInt(newFather.trim()));
		st.setString(5, user);
		st.setTimestamp(6, time);
		st.setString(7, type);
		st.setString(8, comments);
		st.executeUpdate();
	}

	// 26DE JULIO 2005 INICIO
	// public synchronized static void moveNodeArch(String nodeChange,String
	// nodeFather,String
	// nodeAnt){
	// Connection con = null;
	// PreparedStatement st = null;
	// StringBuilder sql= new
	// StringBuilder("select * from documents where idnode="+ nodeChange +"
	// order by number");
	// //System.out.println("[sql]"+sql);
	// try {
	// con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	// con.setAutoCommit(false);
	// String nuevoNumCorrelativo=null;
	// String numCorrelativExiste=null;
	// Vector data = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
	// for (int row = 0; row < data.size(); row++) {
	// Properties properties = (Properties) data.elementAt(row);
	// //si existe un numero con este query, quiere decir que hay que asignarle
	// un nuevo numero al
	// documento ya que
	// //existe en esa localidad.
	// StringBuilder buscarNumCorrExist=new
	// StringBuilder(" from documents where idnode=");
	// buscarNumCorrExist.append(properties.getProperty("IdNode").trim()).append("
	// and number in
	// (select number from documents where
	// numgen=").append(properties.getProperty("numGen").trim()).append(")");
	// numCorrelativExiste=HandlerBD.getField2("number",buscarNumCorrExist.toString());
	// if (!ToolsHTML.isEmptyOrNull(numCorrelativExiste)){
	// //obtenemos el numero correlativo a esakkura
	// nuevoNumCorrelativo=EditDocumentAction.numCorrelativo(properties.getProperty("IdNode").trim(),properties.getProperty("IdNode").trim());
	// StringBuilder sql1 = new StringBuilder("UPDATE documents SET ");
	// sql1.append("number='").append(nuevoNumCorrelativo).append("'");
	// sql1.append(" WHERE numGen = ").append(properties.getProperty("numGen").trim());
	// st = con.prepareStatement(sql1.toString());
	// st.executeUpdate();
	// }
	// con.commit();
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// //return resp;
	// }

	// 28 DE JULIO 2005 FIN
	public synchronized static boolean moveNode(String nodeChange,
			String newFather, String nodeAnt, String user, String comments) {
		boolean resp = false;
		Connection con = null;
		PreparedStatement st = null;
		StringBuilder sql = new StringBuilder(
				"UPDATE struct SET idNodeParent='").append(newFather.trim());
		sql.append("' WHERE idNode = ").append(nodeChange);
		log.debug("[sql]" + sql);
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			// int idHistoryStruct = IDDBFactorySql.getNextID("historystruct");
			int idHistoryStruct = HandlerStruct.proximo("historystruct",
					"historystruct", "idHistory");
			con.setAutoCommit(false);
			st = con.prepareStatement((sql.toString()));
			st.executeUpdate();
			setPrepareStaHistory(con, nodeChange, newFather, nodeAnt, user,
					DataHistoryStructForm.move,
					new Timestamp(new Date().getTime()), idHistoryStruct,
					comments);
			// actualizamos cada uno de los archivos inrternos, con su nuevo
			// prefix y number
			con.commit();
			// es para mover los numeros correlativos tambien, se coloco en
			// comentario
			// porque al mover una carpeta completa, siempre van a ser difrentes
			// los numcorrelativos
			// con la ayuda del prefijo
			// moveNodeArch(nodeChange,newFather,nodeAnt);
			resp = true;
		} catch (Exception ex) {
			applyRollback(con);
			if (ex != null && ex.getMessage() != null) {
				ex.printStackTrace();
				setMensaje(ex.getMessage());
			}
			resp = false;
		} finally {
			setFinally(con, st);
		}
		return resp;
	}

	/**
	 * Método para Eliminar el Nodo indicado como parámetro. Se valida que dicho
	 * nodo se encuentre vacío
	 * 
	 * @param forma
	 * @return
	 */
	public synchronized static boolean delete(BaseStructForm forma)
			throws ApplicationExceptionChecked, Exception {
		Connection con = null;
		PreparedStatement st = null;
		boolean resp = false;
		boolean isNodeParent = true;
		try {
			isNodeParent = isNodeParent(forma.getIdNode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (isNodeParent) {
			throw new ApplicationExceptionChecked("E0024");
		}
		if (HandlerDocuments.isNodeParent(forma.getIdNode())) {
			throw new ApplicationExceptionChecked("E0025");
		}
		if (!isNodeParent) {
			try {
				int nodeType = Integer.parseInt(forma.getNodeType());
				String table = getTableName(nodeType);
				StringBuilder sql = new StringBuilder(
						"DELETE FROM struct WHERE IdNode = ");
				sql.append(forma.getIdNode());
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				con.setAutoCommit(false);
				st = con.prepareStatement((sql.toString()));
				st.executeUpdate();
				if (!checkExist(table, forma.getIdNode())) {
					sql = new StringBuilder("DELETE FROM ").append(table)
							.append(" WHERE idNode = ")
							.append(forma.getIdNode());
					st = con.prepareStatement((sql.toString()));
					st.executeUpdate();
				}
				con.commit();
				resp = true;
			} catch (Exception e) {
				applyRollback(con);
				setMensaje(e.getMessage());
				resp = false;
			} finally {
				setFinally(con, st);
			}
		}
		return resp;
	}

	public synchronized static boolean isNodeParent(String id) throws Exception {
		StringBuilder sql = new StringBuilder(
				"SELECT * FROM struct WHERE IdNodeParent = '");
		sql.append(id).append("'");
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		return datos.size() > 0;
	}

	public synchronized static Collection getChildsNode(String id) {
		String typeOrden = null;
		try {
			typeOrden = String.valueOf(HandlerParameters.PARAMETROS.getTypeOrder());
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Error: ", ex);
		}
		StringBuilder sql = new StringBuilder(
				"SELECT IdNode FROM struct WHERE IdNodeParent = '");
		sql.append(id).append("' AND NodeType <> '5' ");
		sql.append(" ORDER BY ");
		if (typeOrden == null || "0".compareTo(typeOrden) == 0)
			sql.append("IdNode");
		else
			sql.append("lower(name)");
		ArrayList resp = new ArrayList();
		try {
			Vector data = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < data.size(); row++) {
				Properties properties = (Properties) data.elementAt(row);
				resp.add(properties.getProperty("IdNode"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;

	}

	// childs =
	// HandlerStruct.getStructChilds(forma.getIdStruct(),forma.getIdPerson(),true,true);
	public synchronized static Hashtable getStructChilds(String id,
			long idUser, boolean isUser, boolean recursive) throws Exception {
		Hashtable result = new Hashtable();
		if (!ToolsHTML.isEmptyOrNull(id)) {
			// obtengo todos los nodos hijos de este nodo padre id
			// (IdNodeParent)
			Collection datos = getChildsNode(id);
			if (datos != null && !datos.isEmpty()) {
				Vector resultado = new Vector();
				for (Iterator iterator = datos.iterator(); iterator.hasNext();) {
					String node = (String) iterator.next();
					boolean exits = false;
					int modify = 0;
					if (isUser) {
						// chequeamos si en permissionstruct hay permisologia
						// para ese nodo con ese
						// usuario
						exits = HandlerDBUser.checkExistSecurity(node, idUser);
						modify = HandlerDBUser
								.checkModifySecurity(node, idUser);
					} else {
						// chequeamos si en permissionstruct hay permisologia
						// para ese nodo con ese
						// grupo
						exits = HandlerGrupo.checkExistSecurity(node, idUser);
						modify = HandlerGrupo.checkModifySecurity(node, idUser);
					}
					// //System.out.println("existe hijo node" + node +
					// " tiene permisologia=" + exits + " fue modificada=" +
					// exits);
					// guardamos el nodo con su permisologia si tiene o no
					// tiene.
					BeanCheckSec bean = new BeanCheckSec(node, exits, modify);
					resultado.add(bean);
					// Si es recursive entonces se cargaran todos los nodos y
					// subnodos del nodo Dado
					if (recursive) {
						getChields(node, result, idUser, isUser);
					}
				}
				if (!resultado.isEmpty()) {
					result.put(id, resultado);
				}
			}

		}
		// simon inicio
		/*
		 * Enumeration h= result.keys(); while(h.hasMoreElements()){ Collection
		 * hijo =(Collection)result.get(h.nextElement()); if
		 * ((hijo!=null)&&(!hijo.isEmpty())){ for (Iterator
		 * it=hijo.iterator();it.hasNext();){ BeanCheckSec ej = (BeanCheckSec)
		 * it.next();
		 * //System.out.println("ej.getIdNode()="+ej.getIdNode()+" ej.isExits()="
		 * +ej.isExits()); } } }
		 */
		// simons fin
		return result;
	}

	private static void getChields(String id, Hashtable data, long idUser,
			boolean isUser) throws Exception {
		if (!ToolsHTML.isEmptyOrNull(id)) {
			// getChildsNode se obtiene todos los nodos de este padre node id
			Collection datos = getChildsNode(id);
			if (datos != null && !datos.isEmpty()) {
				Vector resultado = new Vector();
				for (Iterator iterator = datos.iterator(); iterator.hasNext();) {
					String idNode = (String) iterator.next();
					boolean exits = false;
					int modify = 0;
					if (isUser) {
						// chequeamos si en permissionstruct hay permisologia
						// para ese nodo con ese
						// usuario
						exits = HandlerDBUser
								.checkExistSecurity(idNode, idUser);
						modify = HandlerDBUser.checkModifySecurity(idNode,
								idUser);
					} else {
						// chequeamos si en permissionstruct hay permisologia
						// para ese nodo con ese
						// grupo
						exits = HandlerGrupo.checkExistSecurity(idNode, idUser);
						modify = HandlerGrupo.checkModifySecurity(idNode,
								idUser);
					}
					// //System.out.println("existe hijo node" + idNode +
					// " tiene permisologia=" + exits + " fue modificada=" +
					// exits);
					// guardamos el nodo con su permisologia si tiene o no
					// tiene.
					BeanCheckSec bean = new BeanCheckSec(idNode, exits, modify);
					resultado.add(bean);
					getChields(idNode, data, idUser, isUser);
				}
				if (!resultado.isEmpty()) {
					data.put(id, resultado);
				}
			}
		}
	}

	public synchronized static String getValueField(String field, String id)
			throws Exception {
		StringBuilder sql = new StringBuilder("SELECT ").append(field).append(
				" FROM struct ");
		sql.append(" WHERE IdNodeParent = '").append(id).append("'");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ((prop != null)
				&& prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			return prop.getProperty(field);
		}
		return null;
	}

	public synchronized static String getTypeNode(String id) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT NodeType FROM struct ");
		sql.append(" WHERE IdNode = '").append(id).append("'");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ((prop != null)
				&& prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			return prop.getProperty("NodeType");
		}
		return null;
	}

	/**
	 * Este método permite cargar todos los Nodos
	 */
	public static Hashtable getNameNodes() {
		return getNameNodes(null);
	}

	public static Hashtable getNameNodes(Connection con) {
		Hashtable result = new Hashtable();
		try {
			String sql = "SELECT IdNode,IdNodeParent,Name FROM struct ORDER by IdNodeParent ";
			Vector datos = JDBCUtil.doQueryVector(con, sql,Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int i = 0; i < datos.size(); i++) {
				Properties properties = (Properties) datos.elementAt(i);
				NodesTree nodo = new NodesTree(properties.getProperty("IdNode")
						.trim(), properties.getProperty("Name"),
						properties.getProperty("IdNodeParent"));
				result.put(nodo.getIdNode(), nodo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public synchronized static String getNameNodeString(String id)
			throws Exception {
		NodesTree nd = getNameNode(null, id);
		return (nd == null ? "" : nd.getNameNode());
	}

	public synchronized static NodesTree getNameNode(String id)
			throws Exception {
		return getNameNode(null, id);
	}

	public synchronized static NodesTree getNameNode(Connection con, String id)
			throws Exception {
		StringBuilder sql = new StringBuilder(
				"SELECT IdNodeParent,Name FROM struct ");
		sql.append(" WHERE IdNode = ").append(id);
		Properties prop = JDBCUtil.doQueryOneRow(con, sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ((prop != null)
				&& prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			String idNodeParent = prop.getProperty("IdNodeParent");
			String name = prop.getProperty("Name");
			return new NodesTree(id, name, idNodeParent);
			// return new Search3(idNodeParent.trim(),name);
		}
		return null;
	}

	/**
	 * Este método permite Cargar el històrico del Nodo enviado como parámetro
	 * 
	 * @param idNode
	 * @return
	 */
	public static synchronized Collection getHistoryNode(String idNode)
			throws Exception {
		ArrayList resp = new ArrayList();
		StringBuilder sql = new StringBuilder(
				"SELECT * FROM historystruct WHERE idNode = ");
		sql.append(idNode);
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			DataHistoryStructForm forma = new DataHistoryStructForm();
			forma.setIdHistory(Integer.parseInt(properties
					.getProperty("idHistory")));
			forma.setIdNodeFatherAnt(Integer.parseInt(properties
					.getProperty("idNodeFatherAnt")));
			forma.setIdNodeFatherNew(Integer.parseInt(properties
					.getProperty("idNodeFatherNew")));
			forma.setNameUser(properties.getProperty("nameUser"));
			forma.setDateChange(ToolsHTML.formatDateShow(
					properties.getProperty("dateChange"), true));
			forma.setTypeMovement(properties.getProperty("type"));
			resp.add(forma);
		}
		return resp;
	}

	public static synchronized DataHistoryStructForm loadHistory(
			String idHistory) throws Exception {
		if (ToolsHTML.isEmptyOrNull(idHistory)) {
			throw new Exception("El id del Histórico esta Nulo ");
		}
		StringBuilder sql = new StringBuilder(
				"SELECT hs.*,s.dateCreation,s.Owner,s.NodeType,s.NameIcon,s.Name");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append(",(p.Apellidos + ' '+p.Nombres) AS nameOwner");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append(",(p.Apellidos || ' ' || p.Nombres) AS nameOwner");
			break;

		}
		sql.append(" FROM historystruct hs,struct s,person p WHERE s.IdNode = hs.idNode");
		sql.append(" AND p.nameUser=s.Owner");
		sql.append(" AND idHistory = ").append(idHistory);
		DataHistoryStructForm forma = new DataHistoryStructForm();
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setIdHistory(Integer.parseInt(idHistory));
			forma.setIdNode(Integer.parseInt(prop.getProperty("idNode")));
			String value = prop.getProperty("idNodeFatherAnt");
			if (ToolsHTML.isNumeric(value)) {
				forma.setIdNodeFatherAnt(Integer.parseInt(value));
			}
			value = prop.getProperty("idNodeFatherNew");
			if (ToolsHTML.isNumeric(value)) {
				forma.setIdNodeFatherNew(Integer.parseInt(value));
			}
			forma.setNameOwner(prop.getProperty("nameOwner"));
			forma.setNameUser(prop.getProperty("nameUser"));
			forma.setDateChange(ToolsHTML.formatDateShow(
					prop.getProperty("dateChange"), true));
			forma.setTypeMovement(prop.getProperty("type").trim());
			forma.setOwner(prop.getProperty("Owner"));
			forma.setDateCreation(ToolsHTML.formatDateShow(
					prop.getProperty("dateCreation"), true));
			forma.setNameIcon(prop.getProperty("NameIcon"));
			forma.setName(prop.getProperty("Name"));
			forma.setNodeType(prop.getProperty("NodeType"));
			forma.setNameUser(HandlerDBUser.getNameUser(forma.getNameUser()));
			// Al ser de Tipo Movimiento se debe Cargar el Resto de la
			// Información
			if (forma.getTypeMovement().equalsIgnoreCase(
					DataHistoryStructForm.move)) {
				forma.setNameNodeFatherAnt(getField("Name", "struct", "IdNode",
						String.valueOf(forma.getIdNodeFatherAnt()), "=", 2,Thread.currentThread().getStackTrace()[1].getMethodName()));
				forma.setNameNodeFatherNew(getField("Name", "struct", "IdNode",
						String.valueOf(forma.getIdNodeFatherNew()), "=", 2,Thread.currentThread().getStackTrace()[1].getMethodName()));
				forma.setComments(prop.getProperty("comments"));
			}
		}
		return forma;
	}

	public static DataHistoryStructForm loadHistoryDoc(String idHistory,
			String iDnode) throws Exception {
		StringBuilder sql = new StringBuilder("");
		Properties prop = null;
		// viene buscando datos para el historico de impresion
		if ((ToolsHTML.isEmptyOrNull(idHistory))
				&& (!ToolsHTML.isEmptyOrNull(iDnode))) {
			sql = new StringBuilder(
					"SELECT hs.*,d.dateCreation,d.Owner,d.nameDocument,");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" (p.Apellidos + ' '+p.Nombres) AS nameOwner,");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" (p.Apellidos || ' ' || p.Nombres) AS nameOwner,");
				break;

			}
			sql.append("d.comments AS docComments, v.createdBy ");
			sql.append(" FROM historydocs hs,documents d,person p, versiondoc v WHERE hs.idNode = d.numGen");
			sql.append(" AND d.numGen = ").append(iDnode);
			sql.append(" AND d.Owner = p.nameUser ");
			sql.append(" AND d.numGen = v.numDoc ");
			sql.append(" AND hs.MayorVer = v.MayorVer ");
			sql.append(" AND hs.MinorVer = v.MinorVer ");
			sql.append(" AND hs.type = '1' ");
			// viene buscando datos para el historico check out

			log.debug("[loadHistoryDoc] " + sql.toString());
			prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		} else if ((!ToolsHTML.isEmptyOrNull(idHistory))
				&& (ToolsHTML.isEmptyOrNull(iDnode))) {
			StringBuilder base = new StringBuilder();
			base = new StringBuilder(
					"SELECT hs.*,d.dateCreation,d.Owner,d.nameDocument,");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				base.append(" (p.Apellidos + ' '+p.Nombres) AS nameOwner,");
				break;
			case Constants.MANEJADOR_POSTGRES:
				base.append(" (p.Apellidos || ' ' || p.Nombres) AS nameOwner,");
				break;

			}
			base.append("d.comments AS docComments, v.createdBy ");
			base.append(" FROM historydocs hs,documents d,person p, versiondoc v WHERE hs.idNode = d.numGen");
			base.append(" AND idHistory = ").append(idHistory);
			base.append(" AND d.Owner = p.nameUser ");
			base.append(" AND d.numGen = v.numDoc ");
			base.append(" AND hs.MayorVer = v.MayorVer ");

			sql.setLength(0);
			sql.append(base).append(" AND hs.MinorVer = v.MinorVer ");

			log.debug("[loadHistoryDoc] " + sql.toString());
			prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (!prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				// si no lo encuentra, intentamos buscar solo con la
				// version mayor del documento.
				log.debug("[loadHistoryDoc] " + base.toString());
				prop = JDBCUtil.doQueryOneRow(base.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			}

		} else {
			throw new Exception("El id del Histórico esta Nulo ");
		}
		DataHistoryStructForm forma = new DataHistoryStructForm();

		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setIdHistory(idHistory != null ? Integer.parseInt(idHistory)
					: 0);
			forma.setIdNode(prop.getProperty("idNode") != null ? Integer
					.parseInt(prop.getProperty("idNode")) : 0);
			String value = prop.getProperty("idNodeFatherAnt") != null ? prop
					.getProperty("idNodeFatherAnt") : "";
			if (ToolsHTML.isNumeric(value)) {
				forma.setIdNodeFatherAnt(Integer.parseInt(value));
			}
			value = prop.getProperty("idNodeFatherNew") != null ? prop
					.getProperty("idNodeFatherNew") : "";
			if (ToolsHTML.isNumeric(value)) {
				forma.setIdNodeFatherNew(Integer.parseInt(value));
			}
			forma.setNameOwner(prop.getProperty("nameOwner") != null ? prop
					.getProperty("nameOwner") : "");

			// SOLO SI SE CONSULTA CREACION
			if (prop.getProperty("type") != null
					&& prop.getProperty("type").equals("1"))
				forma.setNameUser(prop.getProperty("createdBy") != null ? prop
						.getProperty("createdBy") : "");
			else
				forma.setNameUser(prop.getProperty("nameUser") != null ? prop
						.getProperty("nameUser") : "");

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("dateChange"))) {
				forma.setDateChange(ToolsHTML.formatDateShow(
						prop.getProperty("dateChange"), true));
			}

			forma.setTypeMovement(prop.getProperty("type").trim());
			forma.setOwner(prop.getProperty("Owner") != null ? prop
					.getProperty("Owner") : "");
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("dateCreation"))) {
				forma.setDateCreation(ToolsHTML.formatDateShow(
						prop.getProperty("dateCreation"), true));
			}

			forma.setNameIcon(DesigeConf.getProperty("imgDocument"));
			forma.setName(prop.getProperty("Name") != null ? prop
					.getProperty("Name") : "");
			forma.setNodeType("5");
			String comments = prop.getProperty("comments") != null ? prop
					.getProperty("comments") : "";
			forma.setComments(null);
			if (!ToolsHTML.isEmptyOrNull(comments)) {
				forma.setComments(comments);
			} else {
				if ("1".equalsIgnoreCase(forma.getTypeMovement())) {
					comments = prop.getProperty("docComments");
					if (!ToolsHTML.isEmptyOrNull(comments)) {
						forma.setComments(comments);
					}
				}
			}
			forma.setNameUser(HandlerDBUser.getNameUser(forma.getNameUser()));
			// Al ser de Tipo Movimiento se debe Cargar el Resto de la
			// Información
			if (forma.getTypeMovement().equalsIgnoreCase(
					DataHistoryStructForm.move)) {
				forma.setNameNodeFatherAnt(getField("Name", "struct", "IdNode",
						String.valueOf(forma.getIdNodeFatherAnt()), "=", 2,Thread.currentThread().getStackTrace()[1].getMethodName()));
				forma.setNameNodeFatherNew(getField("Name", "struct", "IdNode",
						String.valueOf(forma.getIdNodeFatherNew()), "=", 2,Thread.currentThread().getStackTrace()[1].getMethodName()));

			}
			String mayorVer = prop.getProperty("MayorVer");
			forma.setMayorVer(!ToolsHTML.isEmptyOrNull(mayorVer) ? mayorVer
					.trim() : "");
			String minorVer = prop.getProperty("MinorVer");
			forma.setMinorVer(!ToolsHTML.isEmptyOrNull(minorVer) ? minorVer
					.trim() : "");
		}
		return forma;
	}

	public static DataHistoryStructForm loadHistoryFiles(String idHistory,
			String iDnode) throws Exception {
		StringBuilder sql = new StringBuilder("");
		// viene buscando datos para el historico de impresion
		if ((ToolsHTML.isEmptyOrNull(idHistory))
				&& (!ToolsHTML.isEmptyOrNull(iDnode))) {
			sql = new StringBuilder(
					"SELECT hs.*,d.f2 As dateCreation,d.f3 As Owner,d.f1 As nameDocument,");
			sql.append(JDBCUtil
					.changeSqlSignal(" (p.Apellidos+' '+p.Nombres) AS nameOwner,"));
			sql.append("'' AS docComments, hs.nameUser As createdBy ");
			sql.append(" FROM historyfiles hs,expediente d,person p, versiondoc v WHERE hs.idNode = d.f1");
			sql.append(" AND d.f1 = ").append(iDnode);
			sql.append(" AND d.f3 = p.nameUser ");
			// sql.append(" AND hs.MayorVer = v.MayorVer ");
			// sql.append(" AND hs.MinorVer = v.MinorVer ");
			sql.append(" AND hs.type = 1 ");
			// viene buscando datos para el historico check out
		} else if ((!ToolsHTML.isEmptyOrNull(idHistory))
				&& (ToolsHTML.isEmptyOrNull(iDnode))) {
			sql = new StringBuilder(
					"SELECT hs.*,d.f2 As dateCreation,d.f3 As Owner,d.f1 As nameDocument,");
			sql.append(JDBCUtil
					.changeSqlSignal(" (p.Apellidos +' '+p.Nombres) AS nameOwner,"));
			sql.append("'' AS docComments, hs.nameUser As createdBy ");
			sql.append(" FROM historyfiles hs,expediente d,person p WHERE hs.idNode = d.f1");
			sql.append(" AND idHistory = ").append(idHistory);
			sql.append(" AND d.f3 = p.nameUser ");
			// sql.append(" AND hs.MayorVer = v.MayorVer ");
			// sql.append(" AND hs.MinorVer = v.MinorVer ");
		} else {
			throw new Exception("El id del Histórico esta Nulo ");
		}
		DataHistoryStructForm forma = new DataHistoryStructForm();

		log.debug("[loadHistoryDoc] " + sql.toString());

		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setIdHistory(idHistory != null ? Integer.parseInt(idHistory)
					: 0);
			forma.setIdNode(prop.getProperty("idNode") != null ? Integer
					.parseInt(prop.getProperty("idNode")) : 0);
			String value = prop.getProperty("idNodeFatherAnt") != null ? prop
					.getProperty("idNodeFatherAnt") : "";
			if (ToolsHTML.isNumeric(value)) {
				forma.setIdNodeFatherAnt(Integer.parseInt(value));
			}
			value = prop.getProperty("idNodeFatherNew") != null ? prop
					.getProperty("idNodeFatherNew") : "";
			if (ToolsHTML.isNumeric(value)) {
				forma.setIdNodeFatherNew(Integer.parseInt(value));
			}
			forma.setNameOwner(prop.getProperty("nameOwner") != null ? prop
					.getProperty("nameOwner") : "");

			// SOLO SI SE CONSULTA CREACION
			if (prop.getProperty("type") != null
					&& prop.getProperty("type").equals("1"))
				forma.setNameUser(prop.getProperty("createdBy") != null ? prop
						.getProperty("createdBy") : "");
			else
				forma.setNameUser(prop.getProperty("nameUser") != null ? prop
						.getProperty("nameUser") : "");

			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("dateChange"))) {
				forma.setDateChange(ToolsHTML.formatDateShow(
						prop.getProperty("dateChange"), true));
			}

			forma.setTypeMovement(prop.getProperty("type").trim());
			forma.setOwner(prop.getProperty("Owner") != null ? prop
					.getProperty("Owner") : "");
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("dateCreation"))) {
				forma.setDateCreation(ToolsHTML.formatDateShow(
						prop.getProperty("dateCreation"), true));
			}

			forma.setNameIcon(DesigeConf.getProperty("imgDocument"));
			forma.setName(prop.getProperty("Name") != null ? prop
					.getProperty("Name") : "");
			forma.setNodeType("5");
			String comments = prop.getProperty("comments") != null ? prop
					.getProperty("comments") : "";
			forma.setComments(null);
			if (!ToolsHTML.isEmptyOrNull(comments)) {
				forma.setComments(comments);
			} else {
				if ("1".equalsIgnoreCase(forma.getTypeMovement())) {
					comments = prop.getProperty("docComments");
					if (!ToolsHTML.isEmptyOrNull(comments)) {
						forma.setComments(comments);
					}
				}
			}
			forma.setNameUser(HandlerDBUser.getNameUser(forma.getNameUser()));
			// Al ser de Tipo Movimiento se debe Cargar el Resto de la
			// Información
			if (forma.getTypeMovement().equalsIgnoreCase(
					DataHistoryStructForm.move)) {
				forma.setNameNodeFatherAnt(getField("Name", "struct", "IdNode",
						String.valueOf(forma.getIdNodeFatherAnt()), "=", 2,Thread.currentThread().getStackTrace()[1].getMethodName()));
				forma.setNameNodeFatherNew(getField("Name", "struct", "IdNode",
						String.valueOf(forma.getIdNodeFatherNew()), "=", 2,Thread.currentThread().getStackTrace()[1].getMethodName()));

			}
			String mayorVer = prop.getProperty("MayorVer");
			forma.setMayorVer(!ToolsHTML.isEmptyOrNull(mayorVer) ? mayorVer
					.trim() : "");
			String minorVer = prop.getProperty("MinorVer");
			forma.setMinorVer(!ToolsHTML.isEmptyOrNull(minorVer) ? minorVer
					.trim() : "");
		}
		return forma;
	}

	public static String loadHistoryImpresion(String numgen) throws Exception {
		Locale defaultLocale = new Locale(
				DesigeConf.getProperty("language.Default"),
				DesigeConf.getProperty("country.Default"));
		ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",
				defaultLocale);
		StringBuilder strHistorico = new StringBuilder("");
		if (ToolsHTML.isEmptyOrNull(numgen)) {
			throw new Exception("El id del Histórico esta Nulo ");
		}
		StringBuilder edit = new StringBuilder();
		StringBuilder stadisticaImpresion = new StringBuilder("");
		edit.append("SELECT vrsdoc.mayorver,vrsdoc.minorver,slct.solicitante,atrz.autorizante,sprt.datesolicitud,sprt.comments, ");
		edit.append("sprt.statusautorizante,sprt.statusimpresion,sprt.destinatarios from ");
		edit.append("tbl_solicitudimpresion sprt, ");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			edit.append("(select idperson,(nombres+ ' ' + apellidos) As solicitante from person) slct, ");
			edit.append("(select idperson,(nombres+ ' ' + apellidos) As autorizante from person) atrz, ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			edit.append("(select idperson,(nombres|| ' ' || apellidos) AS solicitante from person) slct, ");
			edit.append("(select idperson,(nombres|| ' ' || apellidos) AS autorizante from person) atrz, ");
			break;

		}
		edit.append("(select mayorver,minorver,numver,numdoc from versiondoc ) vrsdoc, ");
		edit.append("documents doc "); // adicional faltaba la version actual
		edit.append("where slct.idperson=sprt.solicitante and ");
		edit.append("atrz.idperson=sprt.autorizante and ");
		edit.append("vrsdoc.numver=sprt.numver and vrsdoc.numdoc=sprt.numgen and ");
		edit.append("sprt.numgen=? and sprt.active='1' ");
		edit.append("and doc.versionPublic=vrsdoc.numver "); // adicional
																// faltaba la
																// version
																// actual
		edit.append("order by sprt.numsolicitud desc");

		log.debug("[loadHistoryImpresion]" + edit.toString());

		PreparedStatement st;
		try {
			Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement((edit.toString()));
			ResultSet rs = null;
			st.setInt(1, Integer.parseInt(numgen));
			rs = st.executeQuery();

			int j = 0;
			int executeimpresion = 0;
			int canceloimpresion = 0;
			int aproboimpresion = 0;
			int anuloimpresion = 0;
			int rechazoimpresion = 0;
			int controladas = 0;
			int nocontroladas = 0;
			while (rs.next()) {
				if (ToolsHTML.isEmptyOrNull(rs.getString("destinatarios"))) {
					nocontroladas += 1;
				} else {
					controladas += 1;
				}

				strHistorico.append((++j)).append("-)")
						.append(rb.getString("imp.histsolicitudimpresion"));
				strHistorico.append("<br><br>");
				strHistorico.append(
						rb.getString("imp.histsolicita")
								+ rs.getString("solicitante")).append("<br>");
				strHistorico.append(
						rb.getString("imp.histautoriza")
								+ rs.getString("autorizante")).append("<br>");
				strHistorico.append("Version=")
						.append(rs.getString("mayorver")).append(".")
						.append(rs.getString("minorver")).append("<br>");
				strHistorico.append(
						rb.getString("imp.histfechasolicitud")
								+ rs.getString("datesolicitud")).append("");
				strHistorico.append(rs.getString("comments") != null ? rs
						.getString("comments") : "");
				char c = rs.getString("statusimpresion").charAt(0);
				switch (c) {
				case '1':
					strHistorico.append(rb.getString("imp.histimpr"));
					break;
				case '2':
					strHistorico.append(rb.getString("imp.histimprdid"));
					executeimpresion += 1;
					// si se imprimio es porque se aprobo
					aproboimpresion += 1;
					break;
				case '3':
					strHistorico.append(rb.getString("imp.histcancel"));
					canceloimpresion += 1;
					break;
				case '5':
					strHistorico.append(rb.getString("imp.histvigntaprobada"));
					aproboimpresion += 1;
					break;
				case '6':
					strHistorico.append(rb.getString("imp.histanuloimpresion"));
					anuloimpresion += 1;
					// si se anulo por expirar el tiempo de aprobación.
					aproboimpresion += 1;
					break;
				case '7':
					strHistorico.append(rb.getString("imp.histrechazo"));
					rechazoimpresion += 1;
					break;
				}
				strHistorico.append("<br><br><br>");
			}
			stadisticaImpresion
					.append(rb.getString("imp.histcopiascontroladas"))
					.append(controladas).append("<br>");
			stadisticaImpresion
					.append(rb.getString("imp.histcopiasnocontroladas"))
					.append(nocontroladas).append("<br>");
			stadisticaImpresion.append(rb.getString("imp.histaprobacionesimp"))
					.append(aproboimpresion).append("<br>");
			// stadisticaImpresion.append(rb.getString("imp.histimpresosdocimp")).append(executeimpresion
			// ).append("<br>");
			// stadisticaImpresion.append("Número de cancelación de impresion
			// :").append(canceloimpresion).append("<br>");
			stadisticaImpresion.append(rb.getString("imp.histrechazoimp"))
					.append(rechazoimpresion).append("<br>");
			stadisticaImpresion.append(rb.getString("imp.histanulacionimp"))
					.append(anuloimpresion).append("<br>");

			rs.close();
			st.close();
			con.close();
		} catch (Exception e) {

		}
		if (strHistorico.length() > 0) {
		} else {
			strHistorico.append("No se ha impreso ninguna vez este documento.");
		}
		stadisticaImpresion.append("<br>").append("<br>")
				.append(strHistorico.toString());
		return stadisticaImpresion.toString();
	}

	public static synchronized Collection getHistoryDoc(String idNode)
			throws Exception {
		ArrayList resp = new ArrayList();
		StringBuilder sql = new StringBuilder(
				"SELECT * FROM historydocs WHERE idNode = ");
		sql.append(idNode).append(" ORDER BY idHistory");
		log.debug("[getHistoryDoc] = " + sql);
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			DataHistoryStructForm forma = new DataHistoryStructForm();
			forma.setIdHistory(Integer.parseInt(properties
					.getProperty("idHistory")));
			forma.setIdNodeFatherAnt(Integer.parseInt(properties
					.getProperty("idNodeFatherAnt")));
			forma.setIdNodeFatherNew(Integer.parseInt(properties
					.getProperty("idNodeFatherNew")));
			forma.setNameUser(properties.getProperty("nameUser"));
			forma.setDateChange(ToolsHTML.formatDateShow(
					properties.getProperty("dateChange"), true));
			forma.setTypeMovement(properties.getProperty("type").trim());
			String mayorVer = properties.getProperty("MayorVer");
			forma.setMayorVer(!ToolsHTML.isEmptyOrNull(mayorVer) ? mayorVer
					.trim() : "");
			String minorVer = properties.getProperty("MinorVer");
			forma.setMinorVer(!ToolsHTML.isEmptyOrNull(minorVer) ? minorVer
					.trim() : "");
			resp.add(forma);
		}
		return resp;
	}

	public static synchronized Collection getHistoryFiles(String idNode)
			throws Exception {
		ArrayList resp = new ArrayList();
		StringBuilder sql = new StringBuilder(
				"SELECT * FROM historyfiles WHERE idNode = ");
		sql.append(idNode);
		sql.append(" order by dateChange ");
		log.debug("[getHistoryFiles] = " + sql);
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			DataHistoryStructForm forma = new DataHistoryStructForm();
			forma.setIdHistory(Integer.parseInt(properties
					.getProperty("idHistory")));
			forma.setIdNodeFatherAnt(0);
			forma.setIdNodeFatherNew(0);
			forma.setNameUser(properties.getProperty("nameUser"));
			forma.setDateChange(ToolsHTML.formatDateShow(
					properties.getProperty("dateChange"), true));
			forma.setTypeMovement(properties.getProperty("type").trim());
			String mayorVer = properties.getProperty("MayorVer");
			forma.setMayorVer(!ToolsHTML.isEmptyOrNull(mayorVer) ? mayorVer
					.trim() : "");
			String minorVer = properties.getProperty("MinorVer");
			forma.setMinorVer(!ToolsHTML.isEmptyOrNull(minorVer) ? minorVer
					.trim() : "");
			resp.add(forma);
		}
		return resp;
	}

	public synchronized static Hashtable loadSecurityStruct(boolean isUser,
			long idStruct) throws Exception {
		StringBuilder sql = new StringBuilder(100);
		log.debug("[loadSecurityStruct]");
		String id = "";
		if (isUser) {
			sql.append("SELECT * FROM permissionstructuser WHERE idStruct = ")
					.append(idStruct);
			id = "idPerson";
		} else {
			sql.append("SELECT * FROM permissionstructgroups WHERE idStruct = ")
					.append(idStruct);
			id = "idGroup";
		}
		log.debug("sql = " + sql);
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		Hashtable result = new Hashtable();
		for (int row = 0; row < datos.size(); row++) {
			Properties prop = (Properties) datos.elementAt(row);
			PermissionUserForm forma = new PermissionUserForm();
			forma.setIdUser(prop.getProperty(id));
			forma.setToRead(Byte.parseByte(prop.getProperty("toRead")));
			forma.setToAddFolder(Byte.parseByte(prop.getProperty("toAddFolder")));
			forma.setToAddProcess(Byte.parseByte(prop
					.getProperty("toAddProcess")));
			forma.setToDelete(Byte.parseByte(prop.getProperty("toDelete")));
			forma.setToEdit(Byte.parseByte(prop.getProperty("toEdit")));
			forma.setToMove(Byte.parseByte(prop.getProperty("toMove")));
			forma.setToAdmon(Byte.parseByte(prop.getProperty("toAdmon")));
			forma.setToAddDocument(Byte.parseByte(prop
					.getProperty("toAddDocument")));
			forma.setToDownload(Byte.parseByte(ToolsHTML.isEmptyOrNull(
					prop.getProperty("toDownload"), "0")));
			result.put(forma.getIdUser(), forma);
		}
		return result;
	}

	public static String quitarSpacioenBlancoNameFile(String nameFile) {
		StringBuilder str = new StringBuilder("");
		int i = 0;
		int j = 0;
		while (i < nameFile.length()) {
			j = nameFile.charAt(i);
			if ((j != 32)) {
				str.append(nameFile.charAt(i));
			}
			i++;
		}
		return str.toString();
	}

	public static String changeExtTxtToHtml(String nameFile) {
		return (nameFile.indexOf(".") != -1
				&& nameFile.toLowerCase().endsWith("txt") ? nameFile.substring(
				0, nameFile.indexOf(".") + 1).concat("html") : nameFile);
	}

	public static int proximo(String tabla1, String tabla2, String campo1) {
		return proximo(null, tabla1, tabla2, campo1);
	}

	public static int proximo(Connection con, String tabla1, String tabla2,
			String campo1) {
		int num = 0;
		try {
			num = IDDBFactorySql.getNextID(con, tabla1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return num;
	}

	public static int proximoNoUpdate(String tabla1) {
		int num = 0;
		try {
			num = IDDBFactorySql.getNextIDNoUpdate(tabla1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return num;
	}

	public static int proximo(String tabla1, String campo1) {
		int num = 0;
		try {
			String IdNode;
			boolean sw = false;
			// buscamos el numero actual de la tabla
			num = IDDBFactorySql.getNextID(tabla1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return num;
	}

	public static synchronized Hashtable getAllLocationsIds() {
		Hashtable result = new Hashtable();
		try {
			StringBuilder sql = new StringBuilder(
					"SELECT s.idNode,cantExpDoc,unitExpDoc FROM struct s,location l");
			sql.append(" WHERE NodeType = '1' AND s.idNode = l.idNode");
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				BaseStructForm location = new BaseStructForm();
				location.setIdNode(properties.getProperty("idNode"));
				location.setCantExpDoc(properties.getProperty("cantExpDoc"));
				location.setUnitExpDoc(properties.getProperty("unitExpDoc"));
				result.put(location.getIdNode(), location);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public static String getAllNodesChilds(String idNode) {
		String nodo;
		StringBuilder result = new StringBuilder(100);
		boolean isFirst = true;
		try {
			StringBuilder sql = new StringBuilder(
					" SELECT idNode FROM struct WHERE idNodeParent = '")
					.append(idNode).append("' ");
			long t0 = System.currentTimeMillis();
			log.info("Obtenidos nodos hijos de '" + idNode + "', en "
					+ ((System.currentTimeMillis() - t0) + " ms"));
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int i = 0; i < datos.size(); i++) {
				Properties properties = (Properties) datos.elementAt(i);
				nodo = properties.getProperty("idNode");
				// Guardamos el Nodo Actual y buscamos los hijos del Mismo...
				if (!isFirst) {
					result.append(",");
				}
				result.append(nodo);
				isFirst = false;
				String data = getAllNodesChilds(nodo);
				// Si trajo resultado se adjuntan al Actual
				if (!"".equalsIgnoreCase(data)) {
					result.append(",").append(data);
				}
			}
			return result.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	/**
	 * 
	 * @param idFolder
	 * @param lenNumber
	 * @param resetNumber
	 * @param minValue
	 * @param maxValue
	 * @return
	 */
	public static String getNumDocInFolder(String idFolder, String lenNumber,
			String resetNumber, String minValue, String maxValue) {
		return getNumDocInFolder(null, idFolder, lenNumber, resetNumber,
				minValue, maxValue);
	}

	/**
	 * 
	 * @param con
	 * @param idFolder
	 * @param lenNumber
	 * @param resetNumber
	 * @param minValue
	 * @param maxValue
	 * @return
	 */
	public static String getNumDocInFolder(Connection con, String idFolder,
			String lenNumber, String resetNumber, String minValue,
			String maxValue) {
		try {
			StringBuilder sql = new StringBuilder(50);

			sql.append("SELECT MAX(number) AS number FROM documents");
			sql.append(" WHERE idNode =  ").append(idFolder);
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append("   AND len(number) = ").append(lenNumber);
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append("   AND length(number) = ").append(lenNumber);
				break;

			}
			sql.append("   AND active = '").append(Constants.permission)
					.append("' ");
			// Por lo menos se consultan los que empiecen por digito para
			// descartar
			// algunos que no sean numericos
			if (!ToolsHTML.isEmptyOrNull(minValue)) {
				sql.append(" AND number >= '" + minValue + "'");
			}
			if (!ToolsHTML.isEmptyOrNull(maxValue)) {
				sql.append(" AND number <= '" + maxValue + "'");
			}
			// //System.out.println("getNumDocInFolder sql.toString(): " +
			// sql.toString());
			Properties prop = JDBCUtil.doQueryOneRow(con, sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop != null
					&& prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				String dato = prop.getProperty("number");
				log.debug("[getNumDocInFolder] dato = " + dato);
				if (ToolsHTML.isNumeric(dato)) {
					return String.valueOf(Integer.parseInt(dato.trim()) + 1);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (ToolsHTML.isEmptyOrNull(resetNumber)) {
			return "1";
		} else {
			return resetNumber.trim();
		}
	}

	/**
	 * 
	 * @param idPerson
	 * @param idStruct
	 * @return
	 */
	public static PermissionUserForm getSecurityForIDUserInStruct(
			long idPerson, String idStruct) {
		PermissionUserForm perm = new PermissionUserForm();
		// System.out.println("[getSecurityForIDUserInStruct] sql = " + sql);
		try {
			StringBuilder sql = new StringBuilder();
			// sql.append("SELECT toViewDocs,toImpresion");
			sql.append("SELECT * ");
			sql.append(" FROM permissionstructuser");
			sql.append(" WHERE idPerson = ").append(idPerson);
			sql.append(" AND idStruct = ").append(idStruct);
			Properties properties = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (properties.getProperty("isEmpty").equalsIgnoreCase("false")) {
				// perm.setToViewDocs(Byte.parseByte(properties.getProperty("toViewDocs")));
				// perm.setToImpresion(Byte.parseByte(properties.getProperty("toImpresion")));
				perm.setIdStruct(idStruct);
				HandlerDocuments.setDataSecurityDoc(perm, properties, null);
				return perm;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param idGroup
	 * @param idStruct
	 * @return
	 */
	public static PermissionUserForm getSecurityForIDGroupInStruct(
			String idGroup, String idStruct) {
		PermissionUserForm perm = new PermissionUserForm();
		try {
			StringBuilder sql = new StringBuilder(1024);
			// sql.append("SELECT toViewDocs,toImpresion,idStruct");
			sql.append("SELECT * ").append("  FROM permissionstructgroups")
					.append(" WHERE idGroup = ").append(idGroup)
					.append(" AND idStruct = ").append(idStruct);
			log.debug("[getSecurityForGroupInDoc] " + sql);
			Properties properties = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (properties != null
					&& properties.getProperty("isEmpty").equalsIgnoreCase(
							"false")) {
				// perm.setToViewDocs(Byte.parseByte(properties.getProperty("toViewDocs")));
				// perm.setToImpresion(Byte.parseByte(properties.getProperty("toImpresion")));
				HandlerDocuments.setDataSecurityDoc(perm, properties, null);
				return perm;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param ite
	 * @param forma
	 * @param crsDocument
	 * @return
	 * @throws SQLException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static String getHistoryLocationHTML(Iterator ite,
			BaseDocumentForm forma, CachedRowSet crsDocument)
			throws SQLException, IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		boolean inicio = crsDocument == null;

		StringBuilder comentario = new StringBuilder(2048)
				.append("<table border=\"1\" style=\"border:1px solid black;\" cellspacing=\"1\" cellpadding=\"1\" align=\"center\" width=\"90%\">")
				.append("<tr>")
				.append("<td style=\"border:0;\"><b>Ubicaci&oacute;n</b></td>");
		if (!inicio) {
			comentario.append("<td style=\"border:0;\"><b>Anterior</b></td>");
		}
		comentario.append("<td style=\"border:0;\"><b>Actual</b></td>").append(
				"</tr>");
		boolean color = false;
		String valorAnterior = null;
		while (ite.hasNext()) {
			color = !color;
			DocumentForm obj = (DocumentForm) ite.next();
			if (!inicio) {
				valorAnterior = String.valueOf(crsDocument.getString(obj
						.getId()) == null ? "" : crsDocument.getString(obj
						.getId()));
			}
			String valorActual = (String) forma.get(obj.getId().toUpperCase());

			comentario.append("<tr>").append("<td style=\"border:0;\">")
					.append(obj.getEtiqueta02()).append(": ").append("</td>");
			if (!inicio) {
				comentario.append("<td style=\"border:0;\">")
						.append(valorAnterior).append("</td>");
			}
			comentario.append("<td style=\"border:0;\">").append(valorActual)
					.append("</td>").append("</tr>");
		}
		comentario.append("</table>");
		return comentario.toString();
	}

	/**
	 * 
	 * @param ite
	 * @param forma
	 * @param crsDocument
	 * @return
	 * @throws SQLException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static StringBuilder getQueryUpdateLocation(Iterator ite,
			BaseDocumentForm forma, CachedRowSet crsDocument)
			throws SQLException, IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		String sep = "";

		StringBuilder query = new StringBuilder(1024)
				.append("UPDATE documents SET ");
		boolean color = false;
		while (ite.hasNext()) {
			color = !color;
			DocumentForm obj = (DocumentForm) ite.next();
			String valorActual = (String) forma.get(obj.getId().toUpperCase());

			if (valorActual != null && !valorActual.equals("")) {
				query.append(sep).append(obj.getId()).append("='")
						.append(valorActual).append("'");
				sep = ",";
			}
		}
		query.append(" WHERE numgen=").append(crsDocument.getInt("numgen"));
		return query;
	}

	/**
	 * 
	 * @param idNode
	 */
	public static final BaseStructForm getParentLocationInfo(String idNode) {
		BaseStructForm forma = null;

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean keepSearching = true;

		try {
			// debemos ubicar la localidad padre de este nodo
			// para obtener de dicha localidad ciertos atributos
			StringBuilder query = new StringBuilder(1024)
					.append("SELECT s.idnodeparent, l.majorid, l.minorid ")
					.append("FROM struct s LEFT JOIN location l ON s.idnode = l.idnode ")
					.append("WHERE s.idnode = ? ");

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			ps = con.prepareStatement((query.toString()));

			while (keepSearching) {
				ps.setString(1, idNode);

				rs = ps.executeQuery();
				if (rs.next()) {
					// verifico si es una localidad, porque en ese caso solo
					// puede ser la localidad padre
					if (null != rs.getString(2) && null != rs.getString(3)) {
						// encontre la localidad
						keepSearching = false;
						log.info("Fue encontrada la informacion de la localidad "
								+ "con el nodo " + idNode);
						forma.setMajorId(rs.getByte(2));
						forma.setMinorId(rs.getByte(3));
					} else {
						// debo seguir buscando en los nodos padres
						// hasta llegar a la localidad
						idNode = rs.getString(1);
					}
				} else {
					keepSearching = false;
					log.error("Buscando con el nodo " + idNode
							+ " no encontramos informacion de su padre.");
				}
			}
		} catch (Exception e) {
		} finally {
			JDBCUtil.closeQuietly(con, ps, rs);
		}

		return forma;
	}
	
	public synchronized static String getIdNodeParent(String id) {
		StringBuffer sql = new StringBuffer("SELECT idNodeParent FROM struct WHERE IdNode = '").append(id).append("' ");

		try {

			CachedRowSet data = JDBCUtil.executeQuery(sql, Thread.currentThread().getStackTrace()[1].getMethodName());
			if(data.next()) {
				return data.getString(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0";

	}

	public synchronized static CachedRowSet getAllChildsNode(String id, Users user) {
		String typeOrden = null;
		try {
			typeOrden = String.valueOf(HandlerParameters.PARAMETROS.getTypeOrder());
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Error: ", ex);
		}
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT a.name,a.nameIcon,a.idNode,b.majorId,b.minorId,pg.toView,pu.toView ");
		sql.append("FROM struct a ");
		//ydavila struct sin ticket
		//sql.append("LEFT OUTER JOIN location b USING(idNode) ");
		//ydavila struct sin ticket
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("LEFT OUTER JOIN location b ON(a.idNode=b.idNode) ");
		break;
	    case Constants.MANEJADOR_POSTGRES:
		sql.append("LEFT OUTER JOIN location b USING(idNode) ");
		break;
	}
		sql.append("LEFT OUTER JOIN permissionstructgroups pg ON a.idNode=pg.idStruct AND pg.idGroup='").append(user.getIdGroup()).append("' ");
		sql.append("LEFT OUTER JOIN permissionstructuser pu ON a.idNode=pu.idStruct AND pu.idPerson='").append(user.getIdPerson()).append("' ");
		sql.append("WHERE a.IdNodeParent = '").append(id).append("' ");
		sql.append("AND a.NodeType <> '5' ");
		sql.append("AND (pg.toView IS NULL OR pg.toView='1') ");
		sql.append("AND (pu.toView IS NULL OR pu.toView='1') ");
		sql.append(" ORDER BY ");
		sql.append(typeOrden == null || "0".compareTo(typeOrden) == 0?"a.IdNode":"lower(a.name)");


		CachedRowSet data=null;
		try {

			data = JDBCUtil.executeQuery(sql, Thread.currentThread().getStackTrace()[1].getMethodName());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;

	}

	/**
	 * 
	 * @param con
	 * @param object
	 * @param property
	 * @param property2
	 * @param subNodos
	 * @param object2
	 * @param b
	 * @return
	 */
	public static Hashtable loadAllNodes(Connection con, Object object, String property, String property2, Hashtable subNodos, Object object2, boolean b) {
		// TODO Auto-generated method stub
		return null;
	}	
	
	//ydavila Ticket 001-00-003023 - tabla STRUCT - RESPONSABLE SOLICITUD DE CAMBIO 
	private static String getUpdateidrespsolcambioUser_Struct(String oldOwner, String newrespsolcambio) {
		StringBuffer sql = new StringBuffer("UPDATE  struct set respsolcambio = '");
		sql.append(newrespsolcambio).append("'");
		sql.append(" where  respsolcambio='").append(oldOwner).append("'");
		log.debug("[getUpdateidRespSolCambio_Struct]" + sql.toString());
		return sql.toString();
	}
	//ydavila Ticket 001-00-003023 - tabla STRUCT - RESPONSABLE ELIMINACIÓN 
	private static String getUpdateidrespsoleliminUser_Struct(String oldOwner, String newrespsolelimin) {
		StringBuffer sql = new StringBuffer("UPDATE  struct set respsolelimin = '");
		sql.append(newrespsolelimin).append("'");
		sql.append(" where  respsolelimin='").append(oldOwner).append("'");
		log.debug("[getUpdateidRespSolElimin_Struct]" + sql.toString());
		return sql.toString();
	}
	//ydavila Ticket 001-00-003023 - tabla STRUCT - RESPONSABLE IMPRESIÓN
		private static String getUpdateidrespsolimpresUser_Struct(String oldOwner, String newrespsolimpres) {
			StringBuffer sql = new StringBuffer("UPDATE  struct set respsolimpres = '");
			sql.append(newrespsolimpres).append("'");
			sql.append(" where  respsolimpres='").append(oldOwner).append("'");
			log.debug("[getUpdateidrespsolimpres_Struct]" + sql.toString());
			return sql.toString();
		}
	}
