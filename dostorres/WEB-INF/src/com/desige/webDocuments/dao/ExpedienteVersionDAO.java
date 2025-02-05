package com.desige.webDocuments.dao;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.desige.webDocuments.bean.ExpedienteRequest;
import com.desige.webDocuments.files.facade.FilesFacade;
import com.desige.webDocuments.files.forms.ExpedienteDetalleForm;
import com.desige.webDocuments.files.forms.ExpedienteForm;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.util.Archivo;

import sun.jdbc.rowset.CachedRowSet;

/**
 * 
 * @author ybracho
 * 
 */
public class ExpedienteVersionDAO implements Serializable {

	private static final long serialVersionUID = -8237257689001913711L;
	// private CachedRowSet crs = null;
	private StringBuffer query = new StringBuffer();

	// private StringBuffer update = new StringBuffer();
	// private StringBuffer insert1 = new StringBuffer();
	// private StringBuffer insert2 = new StringBuffer();

	// private static int campos = 0;

	/**
	 * 
	 */
	public ExpedienteVersionDAO() {
	}

	/**
	 * 
	 * @param exp
	 * @param usuario
	 * @param cone
	 * @throws Exception
	 */
	public void save(ExpedienteDetalleForm exp, Users usuario, Connection cone)
			throws Exception {

		query.setLength(0);
		query.append(
				"INSERT INTO expediente_version (f1,numgen,orden,numver,filesVersion,nameUser,ownerFiles,datePrint) ")
				.append("VALUES(?,?,?,?,?,?,?,?)");

		ArrayList<Object> parametro = new ArrayList<Object>();
		parametro.add(exp.getF1());
		parametro.add(exp.getNumgen());
		parametro.add(exp.getOrden());
		parametro.add(exp.getNumver());
		parametro.add(exp.getFilesVersion());
		parametro.add(exp.getNameUser());
		parametro.add(exp.getOwnerFiles());
		parametro.add(new Timestamp(exp.getDatePrint().getTime()));
		// parametro.set(6,JDBCUtil.convertDateSql(exp.getDatePrint()));

		JDBCUtil.executeUpdate(query, parametro, cone);

	}

	/**
	 * 
	 * @param expDetForm
	 * @return
	 * @throws Exception
	 */
	public CachedRowSet findAllWithName(ExpedienteForm expDetForm)
			throws Exception {

		query.setLength(0);
		query.append("select b.nameFile, c.name, b.owner,d.data ")
				.append("from expediente_version a, documents b, struct c, versiondoc d ")
				.append("where a.numgen=b.numgen ")
				.append("and b.idNode=c.idNode ")
				.append("and d.numver=(select max(numver) from versiondoc where versiondoc.numdoc=a.numgen) ")
				.append("and a.f1=? ");

		ArrayList<Object> parametro = new ArrayList<Object>();
		parametro.add(expDetForm.getF1());
		return JDBCUtil.executeQuery(query, parametro, Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	/**
	 * 
	 * @param expDetForm
	 * @return
	 * @throws Exception
	 */
	public CachedRowSet findAll(ExpedienteForm expDetForm) throws Exception {
		query.setLength(0);
		query.append(
				"select a.numgen,b.namedocument,c.mayorver,c.minorver,b.prefix,b.number,")
				.append("b.type,b.owner,a.orden,d.nombres,d.apellidos,e.typedoc,")
				.append("b.statu,c.statu as statuVer,c.dateExpiresDrafts,c.dateExpires, b.nameFile, f.name As folder, c.numver, a.filesVersion, ")
				.append("b.toForFiles, b.active, a.numver AS realVer ")
				.append("from expediente_version a, documents b, versiondoc c, person d, typedocuments e, struct f ")
				.append("where a.numgen=b.numgen ")
				.append("and b.numgen=c.numdoc ")
				.append("and b.owner=d.nameUser ")
				.append("and ").append(JDBCUtil.getCastAsIntString("b.type")).append("=e.idtypedoc ")
				.append("and b.idNode=f.idNode ")
				.append("and c.numver=a.numver ").append("and a.f1=? ");
		if (expDetForm.getNumVer() != 0) {
			query.append("and a.filesVersion=? ");
		}
		query.append("order by a.orden ");

		ArrayList<Object> parametro = new ArrayList<Object>();
		parametro.add(expDetForm.getF1());
		if (expDetForm.getNumVer() != 0) {
			parametro.add(expDetForm.getNumVer());
		}

		return JDBCUtil.executeQuery(query, parametro, Thread.currentThread().getStackTrace()[1].getMethodName());
	}
	
	
	public CachedRowSet findByVersion(ExpedienteForm expDetForm) throws Exception {
		ArrayList<Object> parametro = new ArrayList<Object>();
		parametro.add(expDetForm.getF1());
		
		String parametroVersion = null; 
		if (expDetForm.getNumVer() != 0) {
			parametroVersion = String.valueOf(expDetForm.getNumVer());
		} else {
			parametroVersion = "(select max(a2.filesVersion) from expediente_version a2 where a2.f1="+expDetForm.getF1()+")";
		}
		
		query.setLength(0);
		query.append("select a.numgen,b.namedocument,c.mayorver,c.minorver,b.prefix,b.number,")
			.append("b.type,b.owner,a.orden,d.nombres,d.apellidos,e.typedoc,")
			.append("b.statu,c.statu as statuVer,c.dateExpiresDrafts,c.dateExpires, b.nameFile, f.name As folder, c.numver, a.filesVersion, ")
			.append("b.toForFiles, b.active, a.numver AS realVer ")
			.append("from expediente_version a, documents b, versiondoc c, person d, typedocuments e, struct f ")
			.append("where a.numgen=b.numgen ")
			.append("and b.numgen=c.numdoc ")
			.append("and b.owner=d.nameUser ")
			.append("and ").append(JDBCUtil.getCastAsIntString("b.type")).append("=e.idtypedoc ")
			.append("and b.idNode=f.idNode ")
			.append("and c.numver=a.numver ")
			.append("and a.f1=? ")
			.append("and a.filesVersion=").append(parametroVersion).append(" ")
			.append("order by a.orden ");

		return JDBCUtil.executeQuery(query, parametro, Thread.currentThread().getStackTrace()[1].getMethodName());
	}
	
	public List<ExpedienteRequest> findAllExpedientes(ExpedienteForm expDetForm) throws Exception {
		List<ExpedienteRequest> lista = new ArrayList<ExpedienteRequest>();
	
		CachedRowSet crs = findAll(expDetForm);
		while(crs.next()) {
			lista.add(new ExpedienteRequest(crs));
		}
		
		String nameFileCacheTemp;
		File cache;
		for(ExpedienteRequest item : lista) {
			nameFileCacheTemp = Archivo.getNameFileEncripted(null, "versiondocview", Integer.parseInt(item.getNumver()), null);
			cache = new File(nameFileCacheTemp);
			item.setPdfGenerated(cache.exists());
		}
		
		return lista;
	}
	
	public List<ExpedienteRequest> findByVersionExpedientes(ExpedienteForm expDetForm) throws Exception {
		List<ExpedienteRequest> lista = new ArrayList<ExpedienteRequest>();
	
		CachedRowSet crs = findByVersion(expDetForm);
		while(crs.next()) {
			lista.add(new ExpedienteRequest(crs));
		}
		
		String nameFileCacheTemp;
		File cache;
		for(ExpedienteRequest item : lista) {
			nameFileCacheTemp = Archivo.getNameFileEncripted(null, "versiondocview", Integer.parseInt(item.getNumver()), null);
			cache = new File(nameFileCacheTemp);
			item.setPdfGenerated(cache.exists());
		}
		
		return lista;
	}

	/**
	 * 
	 * @param expDetForm
	 * @return
	 * @throws Exception
	 */
	public CachedRowSet findAllVersionGroup(ExpedienteForm expDetForm)
			throws Exception {

		query.setLength(0);
		query.append("select filesVersion,ownerFiles,datePrint,nameUser ")
				.append("from expediente_version ").append("where f1=? ")
				.append("group by filesVersion,ownerFiles,datePrint,nameUser ")
				.append("order by filesVersion desc");

		ArrayList<Object> parametro = new ArrayList<Object>();
		parametro.add(expDetForm.getF1());
		return JDBCUtil.executeQuery(query, parametro, Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	/**
	 * 
	 * @param expDetForm
	 * @return
	 * @throws Exception
	 */
	public int getNewVersion(ExpedienteForm expDetForm) throws Exception {
		query.setLength(0);
		query.append("select max(filesVersion) from expediente_version where f1=? group by f1");

		ArrayList<Object> parametro = new ArrayList<Object>();
		parametro.add(expDetForm.getF1());

		CachedRowSet crs = JDBCUtil.executeQuery(query, parametro, Thread.currentThread().getStackTrace()[1].getMethodName());
		if (crs.next()) {
			return (crs.getInt(1) + 1);
		}
		return 1;
	}

	/**
	 * 
	 * @param expDetForm
	 * @return
	 * @throws Exception
	 */
	public static boolean isConstainDocument(ExpedienteForm expDetForm)
			throws Exception {
		StringBuilder query = new StringBuilder(1024)
				.append("select count(*) from expediente_version where f1=? ");

		ArrayList<Object> parametro = new ArrayList<Object>();
		parametro.add(expDetForm.getF1());

		CachedRowSet crs = JDBCUtil.executeQuery(query, parametro, Thread.currentThread().getStackTrace()[1].getMethodName());
		crs.next();
		return crs.getInt(1) > 0;
	}

	/**
	 * 
	 * @param files
	 *            Contiene el id del expediente a salvar
	 * @param usuario
	 *            es el usuario que realiza la operacion
	 * @return true si la operacion de salvar es exitosa
	 */
	public boolean saveVersion(ExpedienteForm files, Users usuario) {
		Connection cone = null;
		try {
			// buscamos los documentos del expediente
			ExpedienteDAO oExpedienteDAO = new ExpedienteDAO();
			oExpedienteDAO.findById(files);

			CachedRowSet rel = findAll(files);

			// identificamos el numero de version
			int filesVersion = getNewVersion(files);
			// Date fecha = new Date();

			// abrimos la conexion a la base de datos
			cone = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			cone.setAutoCommit(false);

			// lo almacenamos en la tabla de versiones
			ExpedienteDetalleForm exp = null;
			StringBuilder insertVersion = new StringBuilder(
					"insert into expediente_version (f1,numgen,orden,numver,filesVersion,nameUser,ownerFiles) values(?,?,?,?,?,?,?)");
			ArrayList<Object> parametro = null;

			while (rel.next()) {
				exp = new ExpedienteDetalleForm();
				exp.setF1(files.getF1());
				exp.setNumgen(rel.getInt("numgen"));
				exp.setOrden(rel.getInt("orden"));
				exp.setNumver(rel.getInt("numver"));
				exp.setFilesVersion(filesVersion);
				exp.setNameUser(usuario.getUser());
				exp.setOwnerFiles(files.getF3());
				// exp.setDatePrint(fecha);

				if (parametro == null) {
					parametro = new ArrayList<Object>();
					for (int i = 0; i < 7; i++)
						parametro.add(0);
				}
				parametro.set(0, exp.getF1());
				parametro.set(1, exp.getNumgen());
				parametro.set(2, exp.getOrden());
				parametro.set(3, exp.getNumver());
				parametro.set(4, exp.getFilesVersion());
				parametro.set(5, exp.getNameUser());
				parametro.set(6, exp.getOwnerFiles());
				// parametro.set(6,JDBCUtil.convertDateSql(exp.getDatePrint()));

				JDBCUtil.executeUpdate(insertVersion, parametro, cone);
			}

			StringBuilder comentarios = new StringBuilder(1024)
					.append("Version guadada/Version save<br/>&nbsp;&nbsp;&nbsp;<b>- ")
					.append(filesVersion).append(" -</b>");

			FilesFacade.updateHistoryFiles(cone, files.getF1(),
					usuario.getUser(), null, "18", comentarios.toString(),
					new String[] { "0", "0" });

			cone.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if (cone != null) {
				try {
					cone.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			if (cone != null) {
				try {
					cone.close();
					cone = null;
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

		return false;
	}

}
