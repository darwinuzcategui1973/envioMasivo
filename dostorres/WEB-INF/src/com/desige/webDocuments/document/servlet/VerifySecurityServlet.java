package com.desige.webDocuments.document.servlet;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerNorms;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qweb.dao.RegisterClassDAO;
import com.focus.util.LanguagePropertiesCheck;

import sun.jdbc.rowset.CachedRowSet;

/**
 * Title: ServletLoadDocuments.java.java <br/> Copyright: (c) 2004 Focus
 * Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC) *
 * @author Ing. Sim�n Rodrigu�z (SR)
 * @version WebDocuments v3.0 <br/> Changes:<br/>
 *          <ul>
 *          <li>30/06/2004 (NC) Creation </li>
 *          <li>27/07/2005 (SR) Se valido que el documento sea publico para
 *          agregar el numero correlativo forma.getDocPublic </li>
 *          <li>16/08/2005 (SR) Se agrego la session de info2</li>
 *          <li>23/06/2006 (NC) Se agreg� el uso del Log y se sincronizaron los
 *          Hilos </li>
 *          <li>19/10/2015 (JR) Se corrigio el procedimiento que revisa los permisos de la structura por usuario</li>
 *          </ul>
 */
public class VerifySecurityServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -936468215553367035L;
	private static Logger log = LoggerFactory.getLogger("[V4.0 FTP] " + VerifySecurityServlet.class.getName());
	private StringBuffer sb = new StringBuffer();
	private CachedRowSet crs = null;
	private CachedRowSet crsDetalle = null;
	private ArrayList<Object> parametros = new ArrayList<Object>();
	private String idGrupo = "";
	private String idPerson = "";

	public void init(ServletConfig config) throws ServletException {

		try {
			log.info("--- Iniciando servlet 'verifysecurityservlet'");
			ServletContext ctx = config.getServletContext();
			
			ToolsHTML.setServletContext(ctx);
			

			// Verificamos los archivos properties para comprobar todas las etiquetas
			File f = new File("");
			String path = ToolsHTML.getPath();
			path = path.concat("WEB-INF").concat(File.separator).concat("classes").concat(File.separator);
			System.out.println(path);
			LanguagePropertiesCheck.compare(path);

			// cargamos el maestro de parametros
			HandlerParameters.loadParametros();
			
			try {
				// guardamos los documentos retornados en memoria para futuras consultas
	            Users usuario = new Users();
	            //Se Coloca el Grupo Administrador
	            usuario.setIdGroup(DesigeConf.getProperty("application.admon"));
	            //Se Coloca el Id del Usuario Administrador del Sistema
	            usuario.setIdPerson(Long.parseLong(DesigeConf.getProperty("application.userAdmonKey")));
	            usuario.setUser(DesigeConf.getProperty("application.userAdmon"));
	            Hashtable tree = ToolsHTML.checkTree(null,usuario);
	            Hashtable prefijos = new Hashtable();
	           // ArrayList<Integer> result = HandlerDocuments.getAllDocNearExpiration(tree,prefijos,false);
	            
	            //   HandlerParameters.DOCUMENTOS_POR_VENCER.removeAll(HandlerParameters.DOCUMENTOS_POR_VENCER);
	            // HandlerParameters.DOCUMENTOS_POR_VENCER.addAll(result);
	            System.out.println("Documentos por vencer : "+HandlerParameters.DOCUMENTOS_POR_VENCER.size());
			} catch(Exception e) {
				e.printStackTrace();
			}
            

			// asignamos el valor del parametro
			// Constants.PRINTER_PDF = "1".equals(String.valueOf(HandlerParameters.PARAMETROS.getOpenoffice()));
			
			if(Constants.JDBC_CONNECTION) {
			
				//System.out.println("Revisa los permisos de grupos en la estructura");
				//log.info("--- Iniciando revision de permisos de grupos en la estructura");
				//revisarPermisosModificadosGrupos(true);
				//log.info("--- Finalizando revision de permisos de grupos en la estructura");
				
				//System.out.println("Revisa los permisos de los usuarios en la estructura");
				log.info("--- Iniciando revision de permisos de usuarios en la estructura");
				revisarPermisosModificadosUsuarios(true);
				log.info("--- Finalizando revision de permisos de usuarios en la estructura");
				
				//System.out.println("Eliminamos permisos a estructuras que no existen");
	
				//System.out.println("vamos a actualizar el nuevo campo de nombre de documento en versiondoc");
				log.info("--- Iniciando actualizacion del campo de nombre de documento");
				//actualizarNameDocumentToVersionDoc();
				log.info("--- Fianlizando actualizacion del campo de nombre de documento");
				
				//System.out.println("vamos a actualizar el nuevo campo de propietario de documento en versiondoc");
				log.info("--- Iniciando actualizacion del campo propietario de documento");
				//actualizarOwnerToVersionDoc();
				log.info("--- Iniciando actualizacion del campo propietario de documento");
				
				// iniciamos el hilo que eliminara los archivos de cache cada viernes (No ejecutamos por que el cache se maneja de otra forma)
				//Tareas task = Tareas.getInstance();
				//task.start();
				log.info("--- Finalizando servlet 'verifysecurityservlet'");
				
				
				// PROCEDIMIENTOS DE CARGA INICIAL
				
				
				// cargamos la tabla de registro
				RegisterClassDAO.recargarRegisterClass();
				
				// cargamos la tabla de normas para facilitar la consulta posterior
				// HandlerNorms.loadMasterNorms();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void eliminarPermisosNoValidos() throws Exception {
		sb.setLength(0);
		sb.append("delete from permissionstructgroups where idStruct in ");
		sb.append("(select a.idStruct nodos from permissionstructgroups a left outer join struct b ");
		sb.append("on rtrim(ltrim(a.idStruct))=rtrim(ltrim(b.idNode)) where b.idNode is null group by a.idStruct) ");

		int act = JDBCUtil.executeUpdate(sb);
	}

	public void revisarPermisosModificadosGrupos(boolean primera) throws Exception {
		// preguntamos cuantas estructuras estan sin permisoModificado
		sb.setLength(0);
		sb.append("SELECT a.idGroup FROM permissionstructgroups a, struct b, groupusers c ");
		sb.append("WHERE a.idStruct=b.idNode ");
		sb.append("AND a.idGroup=c.idGrupo  ");
		sb.append("AND (a.permisoModificado IS NULL OR a.permisoModificado='0')  ");
		sb.append("GROUP BY a.idGroup  ");
		sb.append("ORDER BY a.idGroup ");
		
		log.info("+ Query inicial para permisos de grupos ");
		crs = JDBCUtil.executeQuery(sb, Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("- Query inicial para permisos de grupos ");
		
		if (crs.next()) {
			// aqui tengo el grupo
			String idGrupo = crs.getString(1);
			log.info("+ Actualizando grupo " + idGrupo);
			actualizarPermisoModificadoGrupos(crs, primera);
			log.info("- Actualizando grupo " + idGrupo);
		}
	}

	public void actualizarPermisoModificadoGrupos(CachedRowSet crs, boolean primera) throws Exception {
		idGrupo = crs.getString(1);
		//System.out.println("Buscamos el primer elemento del grupo ".concat(idGrupo));
		parametros.removeAll(parametros);
		sb.setLength(0);

		parametros.add(new Integer(idGrupo));
		sb.append("SELECT a.* FROM permissionstructgroups a, struct b ");
		sb.append("WHERE a.idStruct=b.idNode ");
		sb.append("AND a.idGroup = ? ");
		if (! primera) {
			sb.append("AND ( a.permisoModificado IS NULL OR a.permisoModificado='0' ) ");
		}
		sb.append("ORDER BY b.idNodeParent ");

		log.info("+ Ejecutando query sb primera=" + primera + ", idgrupo=" + idGrupo);
		crs = JDBCUtil.executeQuery(sb, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("- Ejecutando query sb primera=" + primera + ", idgrupo=" + idGrupo);
		
		if (crs.next()) {
			// seteamos el objeto

			// Se Cargan los Ids de Todos los Nodos Hijos....
			log.info("+ HandlerStruct.getAllNodesChilds (idgrupo/idStruct) " 
					+ idGrupo + "/" + crs.getString("idStruct"));
			String hijos = HandlerStruct.getAllNodesChilds(crs.getString("idStruct"));
			log.info("- HandlerStruct.getAllNodesChilds (idgrupo/idStruct) " 
					+ idGrupo + "/" + crs.getString("idStruct"));
			
			// ahora buscamos el detalle para la comparacion
			parametros.removeAll(parametros);
			sb.setLength(0);

			parametros.add(new Integer(crs.getInt("idStruct")));
			parametros.add(new Integer(idGrupo));
			sb.append(" UPDATE permissionstructgroups SET permisoModificado= ? ");
			sb.append(" WHERE idGroup = ? ");
			sb.append(" AND ( permisoModificado IS NULL OR permisoModificado='0' ) ");
			sb.append(" AND idStruct IN (").append(hijos.concat(!hijos.trim().equals("") ? "," : "").concat(crs.getString("idStruct"))).append(") ");
			sb.append(" AND (CASE WHEN toView IS NULL THEN '0' ELSE toView END)='").append(JDBCUtil.getByte(crs.getString("toView"))).append("'");
			sb.append(" AND (CASE WHEN toRead IS NULL THEN '0' ELSE toRead END)='").append(JDBCUtil.getByte(crs.getString("toRead"))).append("'");
			sb.append(" AND (CASE WHEN toAddFolder IS NULL THEN '0' ELSE toAddFolder END)='").append(JDBCUtil.getByte(crs.getString("toAddFolder"))).append("'");
			sb.append(" AND (CASE WHEN toAddProcess IS NULL THEN '0' ELSE toAddProcess END)='").append(JDBCUtil.getByte(crs.getString("toAddProcess"))).append("'");
			sb.append(" AND (CASE WHEN toDelete IS NULL THEN '0' ELSE toDelete END)='").append(JDBCUtil.getByte(crs.getString("toDelete"))).append("'");
			sb.append(" AND (CASE WHEN toMove IS NULL THEN '0' ELSE toMove END)='").append(JDBCUtil.getByte(crs.getString("toMove"))).append("'");
			sb.append(" AND (CASE WHEN toEdit IS NULL THEN '0' ELSE toEdit END)='").append(JDBCUtil.getByte(crs.getString("toEdit"))).append("'");
			sb.append(" AND (CASE WHEN toAddDocument IS NULL THEN '0' ELSE toAddDocument END)='").append(JDBCUtil.getByte(crs.getString("toAddDocument"))).append("'");
			sb.append(" AND (CASE WHEN toAdmon IS NULL THEN '0' ELSE toAdmon END)='").append(JDBCUtil.getByte(crs.getString("toAdmon"))).append("'");
			sb.append(" AND (CASE WHEN active IS NULL THEN '0' ELSE active END)='").append(JDBCUtil.getByte(crs.getString("active"))).append("'");
			sb.append(" AND (CASE WHEN toViewDocs IS NULL THEN '0' ELSE toViewDocs END)='").append(JDBCUtil.getByte(crs.getString("toViewDocs"))).append("'");
			sb.append(" AND (CASE WHEN toEditDocs IS NULL THEN '0' ELSE toEditDocs END)='").append(JDBCUtil.getByte(crs.getString("toEditDocs"))).append("'");
			sb.append(" AND (CASE WHEN toAdmonDocs IS NULL THEN '0' ELSE toAdmonDocs END)='").append(JDBCUtil.getByte(crs.getString("toAdmonDocs"))).append("'");
			sb.append(" AND (CASE WHEN toDelDocs IS NULL THEN '0' ELSE toDelDocs END)='").append(JDBCUtil.getByte(crs.getString("toDelDocs"))).append("'");
			sb.append(" AND (CASE WHEN toReview IS NULL THEN '0' ELSE toReview END)='").append(JDBCUtil.getByte(crs.getString("toReview"))).append("'");
			sb.append(" AND (CASE WHEN toApproved IS NULL THEN '0' ELSE toApproved END)='").append(JDBCUtil.getByte(crs.getString("toApproved"))).append("'");
			sb.append(" AND (CASE WHEN toMoveDocs IS NULL THEN '0' ELSE toMoveDocs END)='").append(JDBCUtil.getByte(crs.getString("toMoveDocs"))).append("'");
			sb.append(" AND (CASE WHEN toCheckOut IS NULL THEN '0' ELSE toCheckOut END)='").append(JDBCUtil.getByte(crs.getString("toCheckOut"))).append("'");
			sb.append(" AND (CASE WHEN toEditRegister IS NULL THEN '0' ELSE toEditRegister END)='").append(JDBCUtil.getByte(crs.getString("toEditRegister"))).append("'");
			sb.append(" AND (CASE WHEN toImpresion IS NULL THEN '0' ELSE toImpresion END)='").append(JDBCUtil.getByte(crs.getString("toImpresion"))).append("'");
			sb.append(" AND (CASE WHEN toCheckTodos IS NULL THEN '0' ELSE toCheckTodos END)='").append(JDBCUtil.getByte(crs.getString("toCheckTodos"))).append("'");
			sb.append(" AND (CASE WHEN toDoFlows IS NULL THEN '0' ELSE toDoFlows END)='").append(JDBCUtil.getByte(crs.getString("toDoFlows"))).append("'");
			sb.append(" AND (CASE WHEN docInline IS NULL THEN '0' ELSE docInline END)='").append(JDBCUtil.getByte(crs.getString("docInline"))).append("'");
			sb.append(" AND (CASE WHEN toFlexFlow IS NULL THEN '0' ELSE toFlexFlow END)='").append(JDBCUtil.getByte(crs.getString("toFlexFlow"))).append("'");
			sb.append(" AND (CASE WHEN toChangeUsr IS NULL THEN '0' ELSE toChangeUsr END)='").append(JDBCUtil.getByte(crs.getString("toChangeUsr"))).append("'");
			sb.append(" AND (CASE WHEN toCompleteFlow IS NULL THEN '0' ELSE toCompleteFlow END)='").append(JDBCUtil.getByte(crs.getString("toCompleteFlow"))).append("'");
			sb.append(" AND (CASE WHEN toPublicEraser IS NULL THEN '0' ELSE toPublicEraser END)='").append(JDBCUtil.getByte(crs.getString("toPublicEraser"))).append("'");
			sb.append(" AND (CASE WHEN toDownload IS NULL THEN '0' ELSE toDownload END)='").append(JDBCUtil.getByte(crs.getString("toDownload"))).append("'");
			
			log.info("+ Ejecutando update (idgrupo/idstruct)" 
					+ idGrupo + "/" + crs.getString("idStruct"));
			int act = JDBCUtil.executeUpdate(sb, parametros);
			log.info("- Ejecutando update (idgrupo/idstruct)" 
					+ idGrupo + "/" + crs.getString("idStruct"));
		}
		
		revisarPermisosModificadosGrupos(false);

	}

	public void revisarPermisosModificadosUsuarios(boolean primera) throws Exception {
		// preguntamos cuantas estructuras estan sin permisoModificado
		sb.setLength(0);
		sb.append("SELECT a.idPerson FROM permissionstructuser a, struct b ");
		sb.append("WHERE a.idStruct=b.idNode ");
		sb.append("AND (a.permisoModificado IS NULL OR a.permisoModificado=0)  ");
		sb.append("GROUP BY a.idPerson  ");
		sb.append("ORDER BY a.idPerson ");

		crs = JDBCUtil.executeQuery(sb, Thread.currentThread().getStackTrace()[1].getMethodName());
		if (crs.next()) {
			actualizarPermisoModificadoUsuarios(crs, primera);
		}

	}

	public void actualizarPermisoModificadoUsuarios(CachedRowSet crs, boolean primera) throws Exception {
		idPerson = crs.getString(1);
		//System.out.println("Buscamos el primer elemento de los usuarios ".concat(idPerson));
		parametros.removeAll(parametros);
		sb.setLength(0);

		
		String limiteMsSql = "";
		String limitePostgres = "";
		
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			limiteMsSql = " top 1 ";
			break;
		case Constants.MANEJADOR_POSTGRES:
			limitePostgres = " limit 1 ";
			break;
		case Constants.MANEJADOR_MYSQL:
			limitePostgres = " limit 1 ";
			break;
		}
		
		parametros.add(new Integer(idPerson));
		sb.append("SELECT ").append(limiteMsSql).append(" a.* FROM permissionstructuser a, struct b ");
		sb.append("WHERE a.idStruct=b.idNode ");
		sb.append("AND a.idPerson = ? ");
		if (!primera) {
			sb.append("AND ( a.permisoModificado IS NULL OR a.permisoModificado=0 ) ");
		}
		sb.append("ORDER BY b.idNodeParent ").append(limitePostgres).append(" "); // MODIFICADO: SOLO TRABAJA EL PRIMER PRODUCTO

		crs = JDBCUtil.executeQuery(sb, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			// seteamos el objeto

			// Se Cargan los Ids de Todos los Nodos Hijos....
			//MODIFICADO:NO TIENE SENTIDO //String hijos = HandlerStruct.getAllNodesChilds(crs.getString("idStruct"));

			// ahora buscamos el detalle para la comparacion
			parametros.removeAll(parametros);
			sb.setLength(0);

			parametros.add(new Integer(crs.getInt("idStruct")));
			parametros.add(new Integer(idPerson));
			sb.append(" UPDATE permissionstructuser SET permisoModificado= ? ");
			sb.append(" WHERE idPerson = ? ");
			sb.append(" AND ( permisoModificado IS NULL OR permisoModificado=0 ) ");
			//MODIFICADO:NO TIENE SENTIDO //sb.append(" AND idStruct IN (").append(hijos.concat(!hijos.trim().equals("") ? "," : "").concat(crs.getString("idStruct"))).append(") ");
			sb.append(" AND (CASE WHEN toView IS NULL THEN '0' ELSE toView END)='").append(JDBCUtil.getByte(crs.getString("toView"))).append("'");
			sb.append(" AND (CASE WHEN toRead IS NULL THEN '0' ELSE toRead END)='").append(JDBCUtil.getByte(crs.getString("toRead"))).append("'");
			sb.append(" AND (CASE WHEN toViewSub IS NULL THEN '0' ELSE toViewSub END)='").append(JDBCUtil.getByte(crs.getString("toViewSub"))).append("'");
			sb.append(" AND (CASE WHEN toAddFolder IS NULL THEN '0' ELSE toAddFolder END)='").append(JDBCUtil.getByte(crs.getString("toAddFolder"))).append("'");
			sb.append(" AND (CASE WHEN toAddProcess IS NULL THEN '0' ELSE toAddProcess END)='").append(JDBCUtil.getByte(crs.getString("toAddProcess"))).append("'");
			sb.append(" AND (CASE WHEN toDelete IS NULL THEN '0' ELSE toDelete END)='").append(JDBCUtil.getByte(crs.getString("toDelete"))).append("'");
			sb.append(" AND (CASE WHEN toMove IS NULL THEN '0' ELSE toMove END)='").append(JDBCUtil.getByte(crs.getString("toMove"))).append("'");
			sb.append(" AND (CASE WHEN toEdit IS NULL THEN '0' ELSE toEdit END)='").append(JDBCUtil.getByte(crs.getString("toEdit"))).append("'");
			sb.append(" AND (CASE WHEN toAddDocument IS NULL THEN '0' ELSE toAddDocument END)='").append(JDBCUtil.getByte(crs.getString("toAddDocument"))).append("'");
			sb.append(" AND (CASE WHEN toAdmon IS NULL THEN '0' ELSE toAdmon END)='").append(JDBCUtil.getByte(crs.getString("toAdmon"))).append("'");
			sb.append(" AND (CASE WHEN active IS NULL THEN '0' ELSE active END)='").append(JDBCUtil.getByte(crs.getString("active"))).append("'");
			sb.append(" AND (CASE WHEN toViewDocs IS NULL THEN '0' ELSE toViewDocs END)='").append(JDBCUtil.getByte(crs.getString("toViewDocs"))).append("'");
			sb.append(" AND (CASE WHEN toEditDocs IS NULL THEN '0' ELSE toEditDocs END)='").append(JDBCUtil.getByte(crs.getString("toEditDocs"))).append("'");
			sb.append(" AND (CASE WHEN toAdmonDocs IS NULL THEN '0' ELSE toAdmonDocs END)='").append(JDBCUtil.getByte(crs.getString("toAdmonDocs"))).append("'");
			sb.append(" AND (CASE WHEN toDelDocs IS NULL THEN '0' ELSE toDelDocs END)='").append(JDBCUtil.getByte(crs.getString("toDelDocs"))).append("'");
			sb.append(" AND (CASE WHEN toReview IS NULL THEN '0' ELSE toReview END)='").append(JDBCUtil.getByte(crs.getString("toReview"))).append("'");
			sb.append(" AND (CASE WHEN toApproved IS NULL THEN '0' ELSE toApproved END)='").append(JDBCUtil.getByte(crs.getString("toApproved"))).append("'");
			sb.append(" AND (CASE WHEN toMoveDocs IS NULL THEN '0' ELSE toMoveDocs END)='").append(JDBCUtil.getByte(crs.getString("toMoveDocs"))).append("'");
			sb.append(" AND (CASE WHEN toCheckOut IS NULL THEN '0' ELSE toCheckOut END)='").append(JDBCUtil.getByte(crs.getString("toCheckOut"))).append("'");
			sb.append(" AND (CASE WHEN toEditRegister IS NULL THEN '0' ELSE toEditRegister END)='").append(JDBCUtil.getByte(crs.getString("toEditRegister"))).append("'");
			sb.append(" AND (CASE WHEN toImpresion IS NULL THEN '0' ELSE toImpresion END)='").append(JDBCUtil.getByte(crs.getString("toImpresion"))).append("'");
			sb.append(" AND (CASE WHEN toCheckTodos IS NULL THEN '0' ELSE toCheckTodos END)='").append(JDBCUtil.getByte(crs.getString("toCheckTodos"))).append("'");
			sb.append(" AND (CASE WHEN toDoFlows IS NULL THEN '0' ELSE toDoFlows END)='").append(JDBCUtil.getByte(crs.getString("toDoFlows"))).append("'");
			sb.append(" AND (CASE WHEN docInline IS NULL THEN '0' ELSE docInline END)='").append(JDBCUtil.getByte(crs.getString("docInline"))).append("'");
			sb.append(" AND (CASE WHEN toFlexFlow IS NULL THEN '0' ELSE toFlexFlow END)='").append(JDBCUtil.getByte(crs.getString("toFlexFlow"))).append("'");
			sb.append(" AND (CASE WHEN toChangeUsr IS NULL THEN '0' ELSE toChangeUsr END)='").append(JDBCUtil.getByte(crs.getString("toChangeUsr"))).append("'");
			sb.append(" AND (CASE WHEN toCompleteFlow IS NULL THEN '0' ELSE toCompleteFlow END)='").append(JDBCUtil.getByte(crs.getString("toCompleteFlow"))).append("'");
			sb.append(" AND (CASE WHEN toPublicEraser IS NULL THEN '0' ELSE toPublicEraser END)='").append(JDBCUtil.getByte(crs.getString("toPublicEraser"))).append("'");

			int act = JDBCUtil.executeUpdate(sb, parametros);

		}
		revisarPermisosModificadosUsuarios(false);

	}

	public void actualizarNameDocumentToVersionDoc() throws Exception {
		// preguntamos cuanto campos estan sin nombre
		sb.setLength(0);
		sb.append("update versiondoc set nameDocVersion=(select documents.nameDocument from documents where documents.numgen=versiondoc.numdoc) ");
		sb.append("where versiondoc.nameDocVersion is null");

		int act = JDBCUtil.executeUpdate(sb);
		//System.out.println("nameDocVersion update:" + act);
	}

	public void actualizarOwnerToVersionDoc() throws Exception {
		// preguntamos cuanto campos estan sin nombre
		sb.setLength(0);
		sb.append("update versiondoc set ownerVersion=(select documents.owner from documents where documents.numgen=versiondoc.numdoc) ");
		sb.append("where versiondoc.ownerVersion is null");

		int act = JDBCUtil.executeUpdate(sb);
		//System.out.println("ownerVersion update:" + act);
	}

}
