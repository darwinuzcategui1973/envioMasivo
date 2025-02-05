package com.desige.webDocuments.dao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import com.desige.webDocuments.files.facade.FilesFacade;
import com.desige.webDocuments.files.forms.ExpedienteForm;
import com.desige.webDocuments.files.forms.FilesForm;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.IDDBFactorySql;
import com.desige.webDocuments.utils.StringUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Users;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIConversion.User;

import sun.jdbc.rowset.CachedRowSet;

public class ExpedienteDAO implements Serializable {

	private CachedRowSet crs = null;
	private StringBuffer query = new StringBuffer();
	private StringBuffer update = new StringBuffer();
	private StringBuffer insert1 = new StringBuffer();
	private StringBuffer insert2 = new StringBuffer();
	private ConfExpedienteDAO conf;
	
	private static int campos = 0;

	public ExpedienteDAO() {
		conf = new ConfExpedienteDAO(); 
	}
	/**
	 * 
	 * @return El numero de campos configurados por el usuario
	 * @throws Exception
	 */
	public int getCampos() throws Exception {
		campos = conf.countRegister();
		return campos;
	}
	
	public void save(ExpedienteForm files,Users usuario) throws Exception { 
		query.setLength(0);
		update.setLength(0);
		String sep="";
		String coma=",";
		ArrayList<Object> parametro = new ArrayList<Object>();
		ArrayList lista = (ArrayList)conf.findAll();
		TreeMap<String,FilesForm> mapa = new TreeMap<String,FilesForm>();
		FilesForm obj;
		String col = "";
		Object valor = null;
		Object valorOld = null;
		StringBuffer comentario = new StringBuffer();
		boolean cambios=false;
		
		boolean isNew=true;
		for(int i=0;i<lista.size();i++) {
			obj = (FilesForm)lista.get(i);
			mapa.put(obj.getId(), obj);
		}
		
		// consultamos los datos del actual expediente si existe
		ExpedienteForm filesOld = new ExpedienteForm();
		if(files.getF1()!=0) {
			filesOld.setF1(files.getF1());
			findById(filesOld);
			if(filesOld.getF2()!=null) {
				isNew=false;
				// comparamos los cambios
			}
		}

		int c = getCampos();
		comentario.append(isNew?"Registro / Register:<br/><br/>":"Cambios / Changes:<br/><br/>");
		for (int i = 3; i <=c ; i++) {
			col = "f".concat(String.valueOf(i));
			valor = ToolsHTML.isEmptyOrNull(files.get(col.toUpperCase()),"");;
			obj = mapa.get(col);

			if(isNew) {
				insert1.append(sep);
				insert2.append(sep);
				insert1.append("f").append(i);
				insert2.append("?");

				comentario.append("*&nbsp;");
				comentario.append(obj.getEtiqueta02());
				comentario.append(" / ");
				comentario.append(obj.getEtiqueta01());
				comentario.append(":</br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				comentario.append(valor);
				comentario.append("<br/><br/>");
			} else {
				update.append(sep);
				update.append("f").append(i).append("=?");

				valorOld = ToolsHTML.isEmptyOrNull(filesOld.get(col.toUpperCase()),"");;
				if(!valor.equals(valorOld)) {
					cambios=true;
					comentario.append("*&nbsp;");
					comentario.append(obj.getEtiqueta02());
					comentario.append(" / ");
					comentario.append(obj.getEtiqueta01());
					comentario.append(":</br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
					comentario.append(valorOld);
					comentario.append(" -> ");
					comentario.append(valor);
					comentario.append("<br/><br/>");
				}
			}

			parametro.add(valor);
			sep=coma;
		}

		Connection cone = null;
		try {
			cone = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			cone.setAutoCommit(false);

			int act = -1;
			if (isNew) {
				files.setF1(IDDBFactorySql.getNextID("expediente")); // buscamos el nuevo id de registro
				parametro.add(files.getF1());
				query.setLength(0);
				query.append(StringUtil.replace("insert into expediente (P1,f1) values(P2,?)",new String[]{insert1.toString(),insert2.toString()}));
				act = JDBCUtil.executeUpdate(query,parametro);
				FilesFacade.updateHistoryFiles(cone, files.getF1(), usuario.getUser(), null, "1", comentario.toString(), new String[]{"0","0"});
			} else {
				if(cambios) {
					query.setLength(0);
					query.append("update expediente set P1 where f1=? ".replaceAll("P1", update.toString()));
					parametro.add(files.getF1());
					act = JDBCUtil.executeUpdate(query, parametro,cone);
					FilesFacade.updateHistoryFiles(cone, files.getF1(), usuario.getUser(), null, "5", comentario.toString(), new String[]{"0","0"});
				}
			}
			
			cone.commit();
		}catch(SQLException e){
			e.printStackTrace();
			if (cone != null) {
				cone.rollback();
			}
			if(e.getMessage().indexOf("duplic")!=-1) {
				throw new ApplicationExceptionChecked("E0122");
			} else {
				throw e;
			}
		}catch(Exception e) {
			e.printStackTrace();
			if (cone != null) {
				cone.rollback();
			}
			throw e;
		} finally{
			if (cone != null) {
				cone.close();
			}
		}
		}

	public ExpedienteForm findById(ExpedienteForm files) throws Exception {
		ArrayList<Integer> parametro = new ArrayList<Integer>();
		query.setLength(0);
		
		parametro.add(files.getF1());
		query.append("select * from expediente where f1=?");
		
		crs = JDBCUtil.executeQuery(query,parametro, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			files.setF1(crs.getInt("f1"));
			files.setF2(crs.getDate("f2"));
			int c = getCampos();
			for(int i=3; i<=c; i++) {
				files.set("F".concat(String.valueOf(i)),crs.getString("f".concat(String.valueOf(i))));
			}
		}

		return files;
	}
	
	public Collection findAll() throws Exception {
		Collection<ExpedienteForm> lista = new ArrayList<ExpedienteForm>();
		query.setLength(0);
		query.append("select * from expediente order by f1"); 
		crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		ExpedienteForm f = new ExpedienteForm();
		while (crs.next()) {
			f = new ExpedienteForm();
			f.setF1(crs.getInt("f1"));
			f.setF2(crs.getDate("f2"));
			int c = getCampos();
			for(int i=3; i<=c; i++) {
				f.set("F".concat(String.valueOf(i)),crs.getString("f".concat(String.valueOf(i))));
			}
			lista.add(f);
		}

		return lista;
	}

	public Collection findAll(ExpedienteForm files, ArrayList conf, HttpServletRequest request) throws Exception {
		return findAll(files, conf, request, null);
	}
	public Collection findAll(ExpedienteForm files, ArrayList conf, HttpServletRequest request, Users usuario) throws Exception {
		Collection<ExpedienteForm> lista = new ArrayList<ExpedienteForm>();
		boolean isWhere=false;

		query.setLength(0);
		query.append("select * from expediente ");
		if(usuario!=null) {
			query.append("where f3='").append(usuario.getUser()).append("' ");
			isWhere = true;
		}

		FilesForm obj;
		String valor;
		boolean isDate = false;
        for(int i=0; i<conf.size(); i++) {
        	obj = (FilesForm)conf.get(i);
        	
        	valor = String.valueOf(files.get(obj.getId().toUpperCase()));
        	valor = valor.replaceAll("'", "");
        	
        	if(obj.getCriterio()==1) {
        		if(!ToolsHTML.isEmptyOrNull(valor) && !valor.equals("0")) {
        			
                	isDate = obj.getId().equals("f2") || obj.getTipo()==FilesForm.TYPE_DATE;

                	valor = (obj.getId().equals("f2")?ToolsHTML.date.format((Date)files.get(obj.getId().toUpperCase())):valor);
                	
        			
                	query.append(!isWhere?"where ":" and ");
        			query.append(isDate?JDBCUtil.changeSqlDate(obj.getId()):obj.getId());
        			query.append(isDate?"=":" like ");
        			query.append(isDate?"'":"'%");
        			query.append(valor);
        			query.append(isDate?"'":"%'");
                	isWhere=true;
        		}
        	}
        }

        query.append(" order by f1 "); 
		crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		request.getSession().removeAttribute("queryFilesReport");
		if(crs.size()>0) {
			request.getSession().setAttribute("queryFilesReport",query.toString());
		}

		ExpedienteForm f = new ExpedienteForm();
		while (crs.next()) {
			f = new ExpedienteForm();
			f.setF1(crs.getInt("f1"));
			f.setF2(crs.getDate("f2"));
			int c = getCampos();
			for(int i=3; i<=c; i++) {
				f.set("F".concat(String.valueOf(i)),crs.getString("f".concat(String.valueOf(i))));
			}
			lista.add(f);
		}

		return lista;
	}
	
	public boolean deleteById(ExpedienteForm files) throws Exception {
		ArrayList<Integer> parametro = new ArrayList<Integer>();
		query.setLength(0);

		parametro.add(files.getF1());
		
		// para eliminar el expediente verificamos que no tenga documentos asociados
		query.setLength(0);
		query.append("select f1 from expediente_detalle where f1=?");
		CachedRowSet crs = JDBCUtil.executeQuery(query,parametro, Thread.currentThread().getStackTrace()[1].getMethodName());
		if(crs.next()) {
			return false;
		}
		
		query.setLength(0);
		query.append("select f1 from expediente_version where f1=?");
		crs = JDBCUtil.executeQuery(query,parametro, Thread.currentThread().getStackTrace()[1].getMethodName());
		if(crs.next()) {
			return false;
		}

		query.setLength(0);
		query.append("delete from expediente where f1=?");
		
		return (JDBCUtil.executeUpdate(query,parametro)>0);
	}

	public static void main2(String args[]) {
		try {
			// Class c =
			// Class.forName("com.desige.webDocuments.files.forms.ExpedienteForm");
			// Class c = f.getClass();
			// Method m[] = c.getDeclaredMethods();
			// for (int i = 0; i < m.length; i++)
			// //System.out.println(m[i].toString());

			ExpedienteForm f = new ExpedienteForm();
			f.setF1(100);
			f.setF2(new Date());
			f.setF3("Status del producto");
			f.setF4("nombre del producto");

			Class c = f.getClass();
			
			Class params[] = {};
			Object paramsObj[] = {};
			String aMethod = "getF3";
			
			Method thisMethod = c.getDeclaredMethod(aMethod, params);
			
			//System.out.println(thisMethod.invoke(f, paramsObj));
			
			//System.out.println(f.getClass().getDeclaredMethod("getF4", new Class[]{}).invoke(f, new Object[]{}));
			
		} catch (Throwable e) {
			System.err.println(e);
		}
	}
	
	public static void main(String[] arg) {
		
		ExpedienteForm f = new ExpedienteForm();
		
		try {
			//System.out.println("abcdefg".charAt(3));
			//f.setF3("ventana");
			f.set("F3","hola mundo");
			//System.out.println(f.get("F3"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
