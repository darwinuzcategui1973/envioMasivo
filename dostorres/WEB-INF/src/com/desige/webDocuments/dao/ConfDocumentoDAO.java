package com.desige.webDocuments.dao; 

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.files.forms.DocumentForm;
import com.desige.webDocuments.persistent.utils.JDBCUtil;

public class ConfDocumentoDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1160087416580606921L;
	private CachedRowSet crs = null;
	private StringBuffer query = new StringBuffer();

	/**
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public boolean save(DocumentForm files) throws Exception {
		boolean isNew = false;

		query.setLength(0);
		query.append("update conf_documento set etiqueta01=?,etiqueta02=?,tipo=?,longitud=?,entrada=?,valores=?,condicion=?,editable=?,visible=?,criterio=?,auditable=?,orden=?,imprimir=?,location=? where id=? ");

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
		parametro.add(files.getLocation());
		parametro.add(files.getId());

		int act = JDBCUtil.executeUpdate(query, parametro);
		if (act == 0) {
			query.setLength(0);
			query.append("insert into conf_documento (etiqueta01,etiqueta02,tipo,longitud,entrada,valores,condicion,editable,visible,criterio,auditable,orden,imprimir,location,id) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			act = JDBCUtil.executeUpdate(query, parametro);
			if (act == 0)
				throw new Exception(
						"No se puede agregar el registro a la configuracion de documentos");
			isNew = true;
		}
		return isNew;
	}

	/**
	 * 
	 * @param files
	 * @throws Exception
	 */
	public void delete(DocumentForm files) throws Exception {

		query.setLength(0);
		query.append("delete from conf_documento where id=? ");

		ArrayList<Object> parametro = new ArrayList<Object>();
		parametro.add(files.getId());

		int act = JDBCUtil.executeUpdate(query, parametro);
		if (act != 0) {
			try {
				query.setLength(0);
				query.append(JDBCUtil.alterTableDropUnique("documents",
						files.getId()));
				act = JDBCUtil.executeUpdate(query);
			} catch (SQLException ex) {
				// no hacemos nada en particular
			}

			query.setLength(0);
			query.append(JDBCUtil.alterTableDrop("documents", files.getId()));
			act = JDBCUtil.executeUpdate(query);
		}
	}

	/**
	 * 
	 * @param files
	 * @throws Exception
	 */
	public void updateAuditable(DocumentForm files) throws Exception {
		query.setLength(0);
		query.append("update conf_documento set auditable=? where id=? ");

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
		query.append("select * from conf_documento order by orden"); // probado
																		// en
																		// mssql
		crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		Collection<DocumentForm> lista = new ArrayList<DocumentForm>();
		fill(crs, (ArrayList<DocumentForm>) lista);

		return lista;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Collection findAllByOrderUbicacion() throws Exception {
		query.setLength(0);
		query.append("select * from conf_documento where location='1' order by orden"); // probado
																						// mssql
		crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		Collection<DocumentForm> lista = new ArrayList<DocumentForm>();
		fill(crs, (ArrayList<DocumentForm>) lista);

		return lista;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean findFieldAditional() throws Exception {
		query.setLength(0);
		query.append("select * from conf_documento where visible=1"); // probado
																		// en
																		// mssql
		crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		return crs.next();
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean findFieldLocation(Connection con) throws Exception {
		query.setLength(0);
		query.append("select * from conf_documento where location=1 and visible=1"); // probado
																						// mssql
		crs = JDBCUtil.executeQuery(query, null, con, Thread.currentThread().getStackTrace()[1].getMethodName());

		return crs.next();
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Collection<DocumentForm> findAll() throws Exception {
		query.setLength(0);
		query.append("select * from conf_documento order by ").append(JDBCUtil.getCastAsIntString("replace(id,'d', '')")); // probado
																								// en
		crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		Collection<DocumentForm> lista = new ArrayList<DocumentForm>();
		fill(crs, (ArrayList<DocumentForm>) lista);

		return lista;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public CachedRowSet findAllByField() throws Exception {
		query.setLength(0);
		query.append("select a.id, a.etiqueta01, b.idTypeDoc ")
				.append("from  conf_documento a  LEFT OUTER JOIN confdocumento_typedocument b ON a.id=b.id ");
		return JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	/**
	 * 
	 * @param idTypeDoc
	 * @return
	 * @throws Exception
	 */
	public CachedRowSet findSelectByIdTypeDoc(String idTypeDoc)
			throws Exception {
		query.setLength(0);
		query.append("SELECT a.id,b.etiqueta01,a.clave ")
				.append("FROM confdocumento_typedocument a, conf_documento b ")
				.append("WHERE a.id=b.id and a.idTypeDoc=").append(idTypeDoc)
				.append(" ").append("AND a.clave>0 ").append("ORDER BY clave ");
		return JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	/**
	 * 
	 * @param crs
	 * @param lista
	 * @return
	 * @throws SQLException
	 */
	private ArrayList fill(CachedRowSet crs, ArrayList<DocumentForm> lista)
			throws SQLException {
		while (crs.next()) {
			DocumentForm f = new DocumentForm();
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
			f.setLocation(crs.getInt("location"));

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
		Collection<DocumentForm> lista = new ArrayList<DocumentForm>();

		query.setLength(0);
		query.append("select count(*) As reg from conf_documento");

		crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		crs.next();
		return crs.getInt(1);
	}

	/**
	 * 
	 * @param id
	 * @param idTypeDocs
	 * @throws Exception
	 */
	public void mergeTypeDocuments(String id, String[] idTypeDocs)
			throws Exception {
		ArrayList parametros = new ArrayList();
		parametros.add(id);

		// consultamos las relaciones actuales
		query.setLength(0);
		query.append("SELECT * FROM confdocumento_typedocument WHERE id=?");
		CachedRowSet crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());

		// eliminamos las relaciones actuales
		query.setLength(0);
		query.append("DELETE FROM confdocumento_typedocument WHERE id=?");
		JDBCUtil.executeUpdate(query, parametros);

		// insertamos las nuevas relaciones
		if (idTypeDocs != null) {
			parametros.add(0);
			parametros.add(0);

			query.setLength(0);
			query.append("INSERT INTO confdocumento_typedocument VALUES(?,?,?)");
			for (int i = 0; i < idTypeDocs.length; i++) {
				parametros.set(1, new Integer(idTypeDocs[i]));
				parametros.set(2, new Integer(0));
				// ubicamos si tiene un orden establecido
				crs.beforeFirst();
				Integer clave = 0;

				while (crs.next()) {
					if (crs.getString("idTypeDoc").equals(idTypeDocs[i])) {
						clave = new Integer(crs.getInt("clave"));
					}
				}

				// parametros.set(3, clave);

				JDBCUtil.executeUpdate(query, parametros);
			}
		}
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ArrayList typeDocumentsAssociate(String id) throws Exception {

		query.setLength(0);
		query.append("SELECT * FROM confdocumento_typedocument WHERE id=?");

		ArrayList parametros = new ArrayList();
		parametros.add(id);

		CachedRowSet crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());

		ArrayList lista = new ArrayList();
		while (crs.next()) {
			lista.add(crs.getString("idTypeDoc"));
		}
		return lista;
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public StringBuffer typeDocumentsAssociateString(String id)
			throws Exception {

		query.setLength(0);
		query.append("SELECT * FROM confdocumento_typedocument WHERE id=?");

		ArrayList parametros = new ArrayList();
		parametros.add(id);

		CachedRowSet crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());

		StringBuffer lista = new StringBuffer();
		String sep = "";
		String coma = ",";
		while (crs.next()) {
			lista.append(sep).append(crs.getString("idTypeDoc"));
			sep = coma;
		}
		return lista;
	}

	/**
	 * 
	 * @param idTypeDoc
	 * @throws Exception
	 */
	public void updateFieldSelectByIndex(String idTypeDoc) throws Exception {
		StringBuilder query = new StringBuilder(2048)
				.append("UPDATE confdocumento_typedocument SET clave=0 ")
				.append("WHERE idTypeDoc=").append(idTypeDoc).append(" ");

		JDBCUtil.executeUpdate(query);
	}

	/**
	 * 
	 * @param idTypeDoc
	 * @param id
	 * @param orden
	 * @throws Exception
	 */
	public void updateFieldSelectByIndex(String idTypeDoc, String id, int orden)
			throws Exception {
		StringBuilder query = new StringBuilder(2048)
				.append("UPDATE confdocumento_typedocument SET clave=")
				.append(orden).append(" ").append("WHERE idTypeDoc=")
				.append(idTypeDoc).append(" ").append("AND id='").append(id)
				.append("'");

		JDBCUtil.executeUpdate(query);
	}

} 
