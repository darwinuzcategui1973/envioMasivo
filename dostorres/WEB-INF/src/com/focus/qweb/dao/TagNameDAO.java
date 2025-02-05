package com.focus.qweb.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.TagNameTO;

/**
 * 
 * @author JRivero
 * 
 */
public class TagNameDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	TagNameTO tagNameTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oTagNameTO) throws Exception {
		tagNameTO = (TagNameTO) oTagNameTO;

		int num = HandlerStruct.proximo("idtagname2", "idtagname2", "idtagname2");
		if(tagNameTO.getIdTagName2Int()==0) {
			tagNameTO.setIdTagName2(String.valueOf(num));
		}
		
		query.setLength(0);
		query.append("INSERT INTO tbl_tagname (idtagname2,tagname,active,idtipo) ");
		query.append("VALUES(?,?,?,?)");
		
		parametros = new ArrayList<Object>();
		parametros.add(tagNameTO.getIdTagName2Int());
		parametros.add(tagNameTO.getTagName());
		parametros.add(tagNameTO.getActiveInt());
		parametros.add(tagNameTO.getIdTipo());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oTagNameTO) throws Exception {
		tagNameTO = (TagNameTO) oTagNameTO;

		query.setLength(0);
		query.append("UPDATE tbl_tagname SET ");
		query.append("tagname=?,active=?,idtipo=? ");
		query.append("WHERE idTagName2=?");

		parametros = new ArrayList<Object>();
		parametros.add(tagNameTO.getTagName());
		parametros.add(tagNameTO.getActiveInt());
		parametros.add(tagNameTO.getIdTipo());
		parametros.add(tagNameTO.getIdTagName2Int()); // primary key


		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oTagNameTO) throws Exception {
		tagNameTO = (TagNameTO) oTagNameTO;

		query.setLength(0);
		query.append("DELETE FROM tbl_tagname WHERE idTagName2=?");

		parametros = new ArrayList<Object>();
		parametros.add(tagNameTO.getIdTagName2Int());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oTagNameTO) throws Exception {
		tagNameTO = (TagNameTO) oTagNameTO;

		query.setLength(0);
		query.append("SELECT * FROM tbl_tagname WHERE idTagName2=").append(tagNameTO.getIdTagName2Int());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, tagNameTO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<TagNameTO> listar() throws Exception {
		ArrayList<TagNameTO> lista = new ArrayList<TagNameTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_tagname ORDER BY idTagName2");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			tagNameTO = new TagNameTO();
			load(crs, tagNameTO);
			lista.add(tagNameTO);
		}
		return lista;
	}
	
	public ArrayList<TagNameTO> listar(String active) throws Exception {
		ArrayList<TagNameTO> lista = new ArrayList<TagNameTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_tagname ");
		query.append("WHERE active=").append(active).append(" ");
		query.append("ORDER BY idTagName2");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			tagNameTO = new TagNameTO();
			load(crs, tagNameTO);
			lista.add(tagNameTO);
		}
		return lista;
	}

	public ArrayList<TagNameTO> listarByTagName(String tagName) throws Exception {
		ArrayList<TagNameTO> lista = new ArrayList<TagNameTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_tagname ");
		query.append("WHERE tagname='").append(tagName).append("' ");
		query.append("ORDER BY idTagName2");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			tagNameTO = new TagNameTO();
			load(crs, tagNameTO);
			lista.add(tagNameTO);
		}
		return lista;
	}

	public ArrayList<TagNameTO> listarByTagNameDiferentId(String tagName, String id) throws Exception {
		ArrayList<TagNameTO> lista = new ArrayList<TagNameTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_tagname ");
		query.append("WHERE tagname='").append(tagName).append("' ");
		query.append("AND idtagname2<>").append(id).append(" ");
		query.append("ORDER BY idTagName2");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			tagNameTO = new TagNameTO();
			load(crs, tagNameTO);
			lista.add(tagNameTO);
		}
		return lista;
	}

	public ArrayList<TagNameTO> listarByTagName(String active,String tagName) throws Exception {
		ArrayList<TagNameTO> lista = new ArrayList<TagNameTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_tagname ");
		query.append("WHERE active=").append(active).append(" ");
		query.append("AND tagname='").append(tagName).append("' ");
		query.append("ORDER BY idTagName2");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			tagNameTO = new TagNameTO();
			load(crs, tagNameTO);
			lista.add(tagNameTO);
		}
		return lista;
	}

	/**
	 * 
	 */
	public ArrayList<TagNameTO> listarByIdTipo(String active, String idTipo) throws Exception {
		ArrayList<TagNameTO> lista = new ArrayList<TagNameTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_tagname ");
		query.append("WHERE active=").append(active).append(" ");
		if(idTipo!=null) {
			query.append("AND idtipo='").append(idTipo).append("' ");
		}
		query.append("ORDER BY idTagName2");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			tagNameTO = new TagNameTO();
			load(crs, tagNameTO);
			lista.add(tagNameTO);
		}
		return lista;
	}

	private void load(CachedRowSet crs, TagNameTO tagNameTO) throws SQLException {
		tagNameTO.setIdTagName2(crs.getString("idTagName2"));
		tagNameTO.setTagName(crs.getString("tagname"));
		tagNameTO.setActive(crs.getString("active"));
		tagNameTO.setIdTipo(crs.getString("idtipo"));
	}

}
