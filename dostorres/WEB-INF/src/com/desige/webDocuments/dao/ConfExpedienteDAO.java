package com.desige.webDocuments.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.files.forms.FilesForm;
import com.desige.webDocuments.persistent.utils.JDBCUtil;

/**
 * 
 * @author ybracho
 * 
 */
public class ConfExpedienteDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3870103944546789287L;
	private CachedRowSet crs = null;
	private StringBuffer query = new StringBuffer();

	/**
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public boolean save(FilesForm files) throws Exception {
		query.setLength(0);
		query.append("update conf_expediente set etiqueta01=?,etiqueta02=?,tipo=?,longitud=?,entrada=?,valores=?,condicion=?,editable=?,visible=?,criterio=?,auditable=?,orden=?,imprimir=? where id=? ");

		ArrayList<Object> parametro = new ArrayList<Object>();
		parametro.add(files.getEtiqueta01());
		parametro.add(files.getEtiqueta02());
		parametro.add(files.getTipo());
		parametro.add(files.getLongitud());
		parametro.add(files.getEntrada());
		parametro.add(files.getValores());
		parametro.add(files.getCondicion());
		parametro.add(files.getEditable());
		parametro.add(files.getVisible());
		parametro.add(files.getCriterio());
		parametro.add(files.getAuditable());
		parametro.add(files.getOrden());
		parametro.add(files.getImprimir());
		parametro.add(files.getId());

		boolean isNew = false;
		int act = JDBCUtil.executeUpdate(query, parametro);
		if (act == 0) {
			query.setLength(0);
			query.append("insert into conf_expediente (etiqueta01,etiqueta02,tipo,longitud,entrada,valores,condicion,editable,visible,criterio,auditable,orden,imprimir,id) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			act = JDBCUtil.executeUpdate(query, parametro);
			if (act == 0)
				throw new Exception(
						"No se puede agregar el registro a la configuracion de expedientes");
			isNew = true;
		}
		return isNew;
	}

	/**
	 * 
	 * @param files
	 * @throws Exception
	 */
	public void delete(FilesForm files) throws Exception {

		if (files.getId() != null && "f1,f2,f3".indexOf(files.getId()) != -1) {
			return;
		}

		query.setLength(0);
		query.append("delete from conf_expediente where id=?");

		ArrayList<Object> parametro = new ArrayList<Object>();
		parametro.add(files.getId());

		int act = JDBCUtil.executeUpdate(query, parametro);
		if (act != 0) {
			try {
				query.setLength(0);
				query.append(JDBCUtil.alterTableDropUnique("expediente",
						files.getId()));
				act = JDBCUtil.executeUpdate(query);
			} catch (SQLException ex) {
				// no hacemos nada en particular
			}

			query.setLength(0);
			query.append(JDBCUtil.alterTableDrop("expediente", files.getId()));
			act = JDBCUtil.executeUpdate(query);
		}
	}

	/**
	 * 
	 * @param files
	 * @throws Exception
	 */
	public void updateAuditable(FilesForm files) throws Exception {
		query.setLength(0);
		query.append("update conf_expediente set auditable=? where id=? ");

		ArrayList<Object> parametro = new ArrayList<Object>();
		parametro.add(files.getAuditable());
		parametro.add(files.getId());

		JDBCUtil.executeUpdate(query, parametro);
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Collection findAllByOrder() throws Exception {
		query.setLength(0);
		query.append("select * from conf_expediente order by orden"); // probado
																		// en
																		// mssql
		crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		Collection<FilesForm> lista = new ArrayList<FilesForm>();
		fill(crs, (ArrayList<FilesForm>) lista);

		return lista;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Collection findAll() throws Exception {

		query.setLength(0);
		query.append("select * from conf_expediente order by ").append(JDBCUtil.getCastAsIntString("replace(id,'f', '')")).append(""); // probado
																									// en
		crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		Collection<FilesForm> lista = new ArrayList<FilesForm>();
		fill(crs, (ArrayList<FilesForm>) lista);

		return lista;
	}

	/**
	 * 
	 * @param crs
	 * @param lista
	 * @return
	 * @throws SQLException
	 */
	private ArrayList fill(CachedRowSet crs, ArrayList<FilesForm> lista)
			throws SQLException {
		while (crs.next()) {
			FilesForm f = new FilesForm();
			f.setId(crs.getString("id"));
			f.setEtiqueta01(crs.getString("etiqueta01"));
			f.setEtiqueta02(crs.getString("etiqueta02"));
			f.setTipo(crs.getInt("tipo"));
			f.setLongitud(crs.getInt("longitud"));
			f.setEntrada(crs.getString("entrada"));
			f.setValores(crs.getString("valores"));
			f.setCondicion(crs.getString("condicion"));
			f.setEditable(crs.getInt("editable"));
			f.setVisible(crs.getInt("visible"));
			f.setCriterio(crs.getInt("criterio"));
			f.setAuditable(crs.getInt("auditable"));
			f.setOrden(crs.getInt("orden"));
			f.setImprimir(crs.getInt("imprimir"));
			lista.add(f);
		}
		return lista;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public int countRegister() throws Exception {
		query.setLength(0);
		query.append("select count(*) As reg from conf_expediente");

		crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		crs.next();
		return crs.getInt(1);
	}

}
