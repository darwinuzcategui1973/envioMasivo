package com.desige.webDocuments.persistent.managers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.desige.webDocuments.bean.PdfDocumentDetailRequest;
import com.desige.webDocuments.bean.PdfSacopDetailRequest;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.sacop.forms.plantilla1;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;

import sun.jdbc.rowset.CachedRowSet;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 05/12/2005
 * Time: 03:26:18 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Title: HandlerSacop.java <br/>
 * Copyright: (c) 2004 Desige Servicios de Computaci�n<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v1.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>28/03/2004 (NC) Creation</li>
 *          <li>03/08/2005 (SR) Se valido ( primeravez) que el usuario una vez
 *          creado, se le informara que cambiara su passwd por seguridad.</li>
 *          </ul>
 */
public class HandlerSacop extends HandlerBD implements Serializable {

	private static final String tablePerson = "person";

	// public static final String tableUser = "person";
	// private static String mensaje = null;

	/**
	 * 
	 * @param perfil
	 * @throws Exception
	 */
	public synchronized static void load1(plantilla1 perfil) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT nameUser FROM ")
				.append(tablePerson)
				// sql.append(tablePerson).append(" p ,").append(tableUser).append(" u ");
				// sql.append(" WHERE p.nameUser=u.nameUser");
				// sql.append(" AND u.nameUser = '").append(perfil.getUser()).append("'");
				.append(" WHERE nameUser = '").append(perfil.getUser())
				.append("'").append(" AND accountActive = '")
				.append(Constants.permission).append("'");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		// System.out.println("sql.toString() = " + sql.toString());
		if ((prop != null)
				&& prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			perfil.setUser(prop.getProperty("nameUser"));
			// perfil.setClave(prop.getProperty("clave"));
		}
	}

	/**
	 * 
	 * @param perfil
	 * @throws Exception
	 */
	public synchronized static void load(plantilla1 perfil) throws Exception {
		StringBuilder sql = new StringBuilder(
				"SELECT nameUser, Apellidos,Nombres, cargo  FROM ")
				.append(tablePerson).append(" WHERE nameUser = '")
				.append(perfil.getUser()).append("'");
		// sql.append(" AND accountActive = '").append(Constants.permission).append("'");

		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ((prop != null)
				&& prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			perfil.setUser(prop.getProperty("nameUser"));
			perfil.setApellidos(prop.getProperty("Apellidos"));
			perfil.setNombres(prop.getProperty("Nombres"));
			// buscamos el cargo y area por hibernate
			perfil.setCargo(HandlerDBUser.getCargoAndArea(prop
					.getProperty("cargo")));
		} else {
			if (prop != null) {
				sql.setLength(0);
				sql.append("SELECT nameUser, Apellidos,Nombres, cargo  FROM ")
						.append(tablePerson).append(" WHERE nameUser = '")
						.append(perfil.getUser()).append("'");
				if ((prop != null)
						&& prop.getProperty("isEmpty")
								.equalsIgnoreCase("false")) {
					perfil.setUser(prop.getProperty("nameUser"));
					perfil.setApellidos(prop.getProperty("Apellidos"));
					perfil.setNombres(prop.getProperty("Nombres"));
					// buscamos el cargo y area por hibernate
					perfil.setCargo(HandlerDBUser.getCargoAndArea(prop
							.getProperty("cargo")));
				}
			}
		}
	}

	/**
	 * 
	 * @param nameField
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public synchronized static boolean isCodInField(String nameField,
			String value) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT nameUser FROM ")
				.append(tablePerson).append(" WHERE ").append(nameField)
				.append(" = '").append(value).append("'")
				.append(" AND accountActive = '").append(Constants.permission)
				.append("'");
		// System.out.println("[isCodInField] = " + sql.toString());
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		return (datos.size() > 0);
	}

	/**
	 * 
	 * @param nameField
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public synchronized static boolean isCodInField1(String nameField,
			String value) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT idAddress FROM address")
				.append(" WHERE ").append(nameField).append(" = '")
				.append(value).append("'");
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		return (datos.size() > 0);
	}

	public synchronized static List<PdfDocumentDetailRequest> findDocRelatedFromSacop(String idPlanillaSacop) throws Exception {
		
		List<PdfDocumentDetailRequest> lista = new ArrayList<PdfDocumentDetailRequest>();
		
		String ids = null;
		
		StringBuilder sql = new StringBuilder();
		sql.append("select noconformidadesref from tbl_planillasacop1 where idplanillasacop1 = ").append(idPlanillaSacop);
		CachedRowSet crs = JDBCUtil.executeQuery(sql,Thread.currentThread().getStackTrace()[1].getMethodName());

		if(crs.next()) {
			ids =crs.getString("noconformidadesref");
		}
		
		if(ids.trim().length()>0) {
			sql.setLength(0);
			sql.append("SELECT a.prefix , a.number, a.nameDocument, b.TypeDoc, c.idDocumentRelated, a.numGen, c.noconformidadesref ");
			sql.append("FROM documents a ");
			if(Constants.MANEJADOR_POSTGRES == Constants.MANEJADOR_ACTUAL) {
				sql.append("LEFT JOIN typedocuments b ON CAST(a.type AS INTEGER) = b.idTypeDoc ");
				
			} else {
				sql.append("LEFT JOIN typedocuments b ON a.type=b.idTypeDoc ");
				
			}
			//sql.append("LEFT JOIN typedocuments b ON a.type=b.idTypeDoc ");
			sql.append("LEFT JOIN tbl_planillasacop1 c ON a.numGen =c.idDocumentRelated and c.idplanillasacop1 = ").append(idPlanillaSacop).append(" ");
			sql.append("WHERE numGen IN (").append(ids).append(") ");
	
			crs = JDBCUtil.executeQuery(sql,Thread.currentThread().getStackTrace()[1].getMethodName());
			
			PdfDocumentDetailRequest obj = null;
			while(crs.next()) {
				obj = new PdfDocumentDetailRequest();
				if (crs.getString("prefix")!=null) {
					obj.setPrefixNumber(crs.getString("prefix").concat(crs.getString("number")));
				}else {
					obj.setPrefixNumber(crs.getString("number"));
					
				}
				obj.setNameDocument(crs.getString("nameDocument"));
				obj.setTypeDoc(crs.getString("TypeDoc"));
				obj.setTypeRelation( crs.getInt("idDocumentRelated")==crs.getInt("numGen") ? "O" : "R" );
				lista.add(obj);
			}
		}
		
		return lista;
	}
	

	public synchronized static List<PdfSacopDetailRequest> findSacopRelated(String idPlanillaSacop) throws Exception {
		
		List<PdfSacopDetailRequest> lista = new ArrayList<PdfSacopDetailRequest>();
		
		String ids = null;
		
		StringBuilder sql = new StringBuilder();
		sql.append("select sacop_relacionadas from tbl_planillasacop1 where idplanillasacop1 = ").append(idPlanillaSacop);
		CachedRowSet crs = JDBCUtil.executeQuery(sql,Thread.currentThread().getStackTrace()[1].getMethodName());

		if(crs.next()) {
			ids =crs.getString("sacop_relacionadas");
		}
		/*
		 * detail.sacopsNumber = getHtml(pre + i);
    detail.sacopsDescription = getHtml("data_sacopsDescription_" + i);
    detail.sacopsDate = getHtml("data_sacopsDate_" + i);
		 */
		
		if(ids.trim().length()>0) {
			sql.setLength(0);
			sql.append("SELECT sacopnum ,descripcion,fechaemision ");
			sql.append("FROM tbl_planillasacop1 ");
			sql.append("WHERE idplanillasacop1 IN (").append(ids).append(") ");
	
			crs = JDBCUtil.executeQuery(sql,Thread.currentThread().getStackTrace()[1].getMethodName());
			
			PdfSacopDetailRequest obj = null;
			while(crs.next()) {
				obj = new PdfSacopDetailRequest();
				/*
				 * Prueba con Darwin  sacopsDate /sacopsNumber/sacopsDescription
				 */
				
				
				obj.setSacopsNumber(crs.getString("sacopnum"));
				obj.setSacopsDescription(crs.getString("descripcion"));
				obj.setSacopsDate(ToolsHTML.formatDateShow(crs.getString("fechaemision"),false));
				// obj.setSacopsDate( crs.getString("fechaemision" ));
				lista.add(obj);
			}
		}
		
		return lista;
	}
	
public synchronized static Date fechaCierreSacop(String idPlanillaSacop) throws Exception {
		
	   //pdf.setDatImpreso(ToolsHTML.sdfShow.format(new java.util.Date()));
		Date fechaCierre = null;
		
		idPlanillaSacop = "'"+idPlanillaSacop.trim()+"'";
		
		
		StringBuilder sql = new StringBuilder();
		sql.append("select fecha_cierre from tbl_planillasacop1 where sacopnum = ").append(idPlanillaSacop);
		CachedRowSet crs = JDBCUtil.executeQuery(sql,Thread.currentThread().getStackTrace()[1].getMethodName());

		if(crs.next()) {
			fechaCierre =crs.getDate("fecha_cierre");
		}
		
		return fechaCierre;
	}


}