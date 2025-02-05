package com.desige.webDocuments.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.to.PreviewTO;
import com.desige.webDocuments.utils.ToolsHTML;

public class PreviewDAO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 4308126158021814238L;
	private CachedRowSet crs = null;
	private StringBuffer query = new StringBuffer();

	/**
	 * 
	 * @param idDocument
	 * @param idVersion
	 * @param idUsuario
	 * @return
	 * @throws Exception
	 */
	public boolean save(String idDocument, String idVersion, String idUsuario)
			throws Exception {
		PreviewTO previewTO = new PreviewTO();
		previewTO.setIdDocument(idDocument);
		previewTO.setIdVersion(idVersion);
		previewTO.setIdUsuario(idUsuario);

		return save(previewTO);
	}

	/**
	 * 
	 * @param previewTO
	 * @return
	 * @throws Exception
	 */
	public boolean save(PreviewTO previewTO) throws Exception {
		boolean isNew = false;

		query.setLength(0);
		query.append("UPDATE preview SET contador=contador+1,actualiza=? ")
				.append("WHERE idDocument=? AND idVersion=? AND idUsuario=? ");

		ArrayList<Object> parametro = new ArrayList<Object>();
		parametro.add(new Timestamp(new Date().getTime()));
		parametro.add(ToolsHTML.parseInt(previewTO.getIdDocument()));// clave
		parametro.add(ToolsHTML.parseInt(previewTO.getIdVersion()));// clave
		parametro.add(ToolsHTML.parseInt(previewTO.getIdUsuario()));// clave

		int act = JDBCUtil.executeUpdate(query, parametro);

		if (act == 0) {
			parametro = new ArrayList<Object>();
			parametro.add(new Timestamp(new Date().getTime()));
			parametro.add(new Timestamp(new Date().getTime()));
			parametro.add(ToolsHTML.parseInt(previewTO.getIdDocument()));// clave
			parametro.add(ToolsHTML.parseInt(previewTO.getIdVersion()));// clave
			parametro.add(ToolsHTML.parseInt(previewTO.getIdUsuario()));// clave

			query.setLength(0);
			query.append(
					"INSERT INTO preview (contador,registra,actualiza,idDocument,idVersion,idUsuario")
					.append(") values(1,?,?,?,?,?)");

			act = JDBCUtil.executeUpdate(query, parametro);
			if (act == 0)
				throw new Exception(
						"No se puede agregar el registro a la tabla preview");
			isNew = true;
		}
		return isNew;
	}

	/**
	 * 
	 * @param previewTO
	 * @return
	 * @throws Exception
	 */
	public Collection findAllByOrder(PreviewTO previewTO) throws Exception {
		query.setLength(0);
		query.append(
				"SELECT c.mayorVer,c.MinorVer,b.nameUser,a.idDocument,a.idVersion,a.idUsuario,a.contador,a.registra,a.actualiza ")
				.append("FROM preview a, person b, versiondoc c ")
				.append("WHERE a.idUsuario=b.idPerson ")
				.append("AND a.idVersion=c.numver ");

		if (ToolsHTML.parseInt(previewTO.getIdDocument()) > 0) {
			query.append("AND a.idDocument=").append(previewTO.getIdDocument())
					.append(" ");
		}
		query.append("order by a.idDocument,a.idVersion desc,a.actualiza desc,a.idUsuario ");

		crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		Collection<PreviewTO> lista = new ArrayList<PreviewTO>();
		fill(crs, (ArrayList<PreviewTO>) lista);

		return lista;
	}

	/**
	 * 
	 * @param previewTO
	 * @return
	 * @throws Exception
	 */
	public Collection findAllByOrderListDist(PreviewTO previewTO)
			throws Exception {

		query.setLength(0);
		query.append(
				"SELECT c.mayorVer,c.MinorVer,b.nameUser,a.idDocument,a.idVersion,a.idUsuario,a.contador,a.registra,a.actualiza ")
				.append("FROM listdistdocument l ")
				.append("LEFT OUTER JOIN preview a ON l.idDocument=a.idDocument AND l.idUsuario=a.idUsuario AND l.idVersion=a.idVersion ")
				.append("LEFT OUTER JOIN person b ON l.idUsuario=b.idPerson ")
				.append("LEFT OUTER JOIN versiondoc c ON l.idVersion=c.numver ");
		if (ToolsHTML.parseInt(previewTO.getIdDocument()) > 0) {
			query.append("WHERE l.idDocument=")
					.append(previewTO.getIdDocument()).append(" ");
		}
		query.append("order by l.idDocument,l.idVersion desc,a.actualiza desc,l.idUsuario ");

		crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		Collection<PreviewTO> lista = new ArrayList<PreviewTO>();
		fill(crs, (ArrayList<PreviewTO>) lista);

		return lista;
	}

	/**
	 * 
	 * @param crs
	 * @param lista
	 * @return
	 * @throws SQLException
	 */
	private ArrayList fill(CachedRowSet crs, ArrayList<PreviewTO> lista)
			throws SQLException {

		while (crs.next()) {
			PreviewTO f = fillFromCache(crs);
			lista.add(f);
		}
		return lista;
	}

	/**
	 * 
	 * @param crs
	 * @return
	 * @throws SQLException
	 */
	private PreviewTO fill(CachedRowSet crs) throws SQLException {
		PreviewTO f = new PreviewTO();
		if (crs.next()) {
			f = fillFromCache(crs);
		}
		return f;
	}

	/**
	 * 
	 * @param crs
	 * @return
	 * @throws SQLException
	 */
	private PreviewTO fillFromCache(CachedRowSet crs) throws SQLException {
		PreviewTO f = new PreviewTO();
		if (crs != null) {
			f.setIdDocument(crs.getString("idDocument"));
			f.setIdVersion(crs.getString("idVersion"));
			f.setIdUsuario(crs.getString("idUsuario"));
			f.setContador(crs.getString("contador"));
			f.setRegistra(crs.getString("registra"));
			f.setActualiza(crs.getString("actualiza"));

			f.setMayorVer(crs.getString("mayorVer"));
			f.setMinorVer(crs.getString("minorVer"));
			f.setNameUser(crs.getString("nameUser"));
		}
		return f;
	}

}
