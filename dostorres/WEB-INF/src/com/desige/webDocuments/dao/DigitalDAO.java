package com.desige.webDocuments.dao;

import java.io.File;
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.to.DigitalTO;
import com.desige.webDocuments.typeDocuments.forms.TypeDocumentsForm;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.StringUtil;
import com.desige.webDocuments.utils.ToolsHTML;

/**
 * 
 * @author ybracho
 * 
 */
public class DigitalDAO implements Serializable {

	private static final long serialVersionUID = -4417524967957233640L;
	private CachedRowSet crs = null;
	private StringBuffer query = new StringBuffer();

	/**
	 * 
	 * @return
	 */
	public int nextId() {
		return HandlerStruct.proximo("Digital", "Digital", "idDigital");
	}

	/**
	 * 
	 * @param digitalTO
	 * @return
	 * @throws Exception
	 */
	public boolean save(DigitalTO digitalTO) throws Exception {
		boolean isNew = false;

		query.setLength(0);
		query.append(
				"update digital set nameFile=?,nameDocument=?,type=?,dateCreation=?,numberTest=?, ")
				.append("idPerson=?,idPersonDelete=?,dateDelete=?,idStatusDigital=?,comentario=?,lote=?,ownerTypeDoc=?,idNode=?,")
				.append("typesetter=?,checker=?,visible=?,")
				.append("versionMayor=?,versionMenor=?,codigo=?,publicado=?,expira=?,fechaPublicacion=?,")
				.append("fechaVencimiento=?,comentarios=?,url=?,palabrasClaves=?,descripcion=?,otrosDatos=? ")
				.append("where idDigital=? ");

		ArrayList<Object> parametro = new ArrayList<Object>();
		parametro.add(digitalTO.getNameFile());
		parametro.add(ToolsHTML.isEmptyOrNull(digitalTO.getNameDocument(), ""));
		parametro.add(ToolsHTML.parseInt(digitalTO.getType()));
		parametro.add(new Timestamp(new Date().getTime()));
		parametro.add(ToolsHTML.parseInt(digitalTO.getNumberTest()));
		parametro.add(ToolsHTML.parseInt(digitalTO.getIdPerson()));
		parametro.add(ToolsHTML.parseInt(digitalTO.getIdPersonDelete()));
		parametro.add(ToolsHTML.parseTimestamp(digitalTO.getDateDelete(),
				ToolsHTML.date));
		parametro.add(ToolsHTML.parseInt(digitalTO.getIdStatusDigital()));
		parametro.add(digitalTO.getComentario());
		parametro.add(digitalTO.getLote());
		parametro.add(ToolsHTML.parseInt(digitalTO.getOwnerTypeDoc()));
		parametro.add(ToolsHTML.parseInt(digitalTO.getIdNode()));
		parametro.add(ToolsHTML.parseInt(digitalTO.getTypesetter()));
		parametro.add(ToolsHTML.parseInt(digitalTO.getChecker()));
		parametro.add(ToolsHTML.parseInt(digitalTO.getVisible()));
		parametro
				.add(ToolsHTML.isEmptyOrNull(digitalTO.getVersionMayor(), "0"));
		parametro
				.add(ToolsHTML.isEmptyOrNull(digitalTO.getVersionMenor(), "0"));
		parametro.add(ToolsHTML.isEmptyOrNull(digitalTO.getCodigo(), ""));
		parametro.add(ToolsHTML.parseInt(digitalTO.getPublicado(), 1));
		parametro.add(ToolsHTML.parseInt(digitalTO.getExpira(), 1));
		parametro.add(ToolsHTML.parseTimestamp(digitalTO.getFechaPublicacion(),
				ToolsHTML.date));
		parametro.add(ToolsHTML.parseTimestamp(digitalTO.getFechaVencimiento(),
				ToolsHTML.date));
		parametro.add(ToolsHTML.isEmptyOrNull(digitalTO.getComentarios(), ""));
		parametro.add(ToolsHTML.isEmptyOrNull(digitalTO.getUrl(), ""));
		parametro
				.add(ToolsHTML.isEmptyOrNull(digitalTO.getPalabrasClaves(), ""));
		parametro.add(ToolsHTML.isEmptyOrNull(digitalTO.getDescripcion(), ""));
		parametro.add(ToolsHTML.isEmptyOrNull(digitalTO.getOtrosDatos(), ""));

		parametro.add(new Integer(digitalTO.getIdDigital())); // clave

		int act = JDBCUtil.executeUpdate(query, parametro);
		if (act == 0) {
			query.setLength(0);
			query.append(
					"insert into digital (nameFile,nameDocument,type,dateCreation,numberTest,")
					.append("idPerson,idPersonDelete,dateDelete,idStatusDigital,comentario,lote,")
					.append("ownerTypeDoc,idNode,typesetter,checker,visible,")
					.append("versionMayor,versionMenor,codigo,publicado,expira,fechaPublicacion,")
					.append("fechaVencimiento,comentarios,url,palabrasClaves,descripcion,otrosDatos,")
					.append("idDigital") // clave
					.append(") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			act = JDBCUtil.executeUpdate(query, parametro);
			if (act == 0)
				throw new Exception(
						"No se puede agregar el registro a la tabla digital");
			isNew = true;
		}
		return isNew;
	}

	/**
	 * 
	 * @param digitalTO
	 * @throws Exception
	 */
	public void deleteFile(DigitalTO digitalTO) throws Exception {

		// buscamos los archivos que vamos a borrar
		query.setLength(0);
		query.append(
				"select idDigital,nameFile from digital where idDigital IN (")
				.append(digitalTO.getIdDigital()).append(") ");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		HashMap lista = new HashMap();
		while (crs.next()) {
			lista.put(crs.getString("idDigital"), crs.getString("nameFile"));
		}

		String[] ids = null;
		if (digitalTO.getIdDigital().indexOf(",") != -1) {
			ids = digitalTO.getIdDigital().split(",");
		} else {
			ids = new String[] { digitalTO.getIdDigital() };
		}

		// eliminamos los archivos en disco
		File arc = null;
		for (int i = 0; i < ids.length; i++) {

			// eliminamos el pdf
			digitalTO.setIdDigital(ids[i]);
			arc = new File(digitalTO.getNameFileDiskFullPath());
			arc.delete();

			// eliminamos el archivo original si existe
			if (lista.containsKey(ids[i])) {
				String nameFile = (String) lista.get(ids[i]);
				if (!nameFile.endsWith(".pdf")) {
					// extraemos la extension del archivo
					StringBuffer name = new StringBuffer(
							StringUtil.getOnlyNameFile(nameFile));
					name = name.reverse();
					StringBuffer ext = new StringBuffer(name.substring(0,
							name.indexOf(".") + 1));
					ext = ext.reverse();

					arc = new File(digitalTO.getNameFileDiskFullPath(ext
							.toString()));
					arc.delete();

				}
			}

		}
		System.gc();
	}

	/**
	 * 
	 * @param digitalTO
	 * @throws Exception
	 */
	public void delete(DigitalTO digitalTO) throws Exception {

		// buscamos los archivos que vamos a borrar
		query.setLength(0);
		query.append(
				"select idDigital,nameFile from digital where idDigital IN (")
				.append(digitalTO.getIdDigital()).append(") ");
		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		HashMap lista = new HashMap();
		while (crs.next()) {
			lista.put(crs.getString("idDigital"), crs.getString("nameFile"));
		}

		query.setLength(0);
		query.append("update digital set idPersonDelete=?,dateDelete=?,idStatusDigital=? ");

		ArrayList<Object> parametro = new ArrayList<Object>();
		parametro.add(ToolsHTML.parseInt(digitalTO.getIdPersonDelete()));
		parametro.add(new Timestamp(new Date().getTime()));
		parametro.add(ToolsHTML.parseInt(Constants.STATUS_DIGITAL_ELIMINADO));

		String[] ids = null;
		if (digitalTO.getIdDigital().indexOf(",") != -1) {
			query.append("where idDigital IN (")
					.append(digitalTO.getIdDigital()).append(") ");
			ids = digitalTO.getIdDigital().split(",");
		} else {
			parametro.add(Integer.parseInt(digitalTO.getIdDigital()));
			query.append("where idDigital=? ");
			ids = new String[] { digitalTO.getIdDigital() };
		}
		query.append(" and idStatusDigital!=")
				.append(Constants.STATUS_DIGITAL_PROCESADO)
				.append(" and idStatusDigital!=")
				.append(Constants.STATUS_DIGITAL_PROCESADO_VERIFICADO);

		int act = JDBCUtil.executeUpdate(query, parametro);

		purgarEliminados();

	}

	/**
	 * 
	 * @throws Exception
	 */
	public void purgarEliminados() throws Exception {

		// buscamos los archivos que vamos a eliminar
		query.setLength(0);
		query.append(
				"select idDigital,nameFile from digital where idStatusDigital = ")
				.append(Constants.STATUS_DIGITAL_ELIMINADO);
		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		HashMap lista = new HashMap();
		while (crs.next()) {
			lista.put(crs.getString("idDigital"), crs.getString("nameFile"));
		}

		query.setLength(0);
		query.append("delete from digital where idStatusDigital = ").append(
				Constants.STATUS_DIGITAL_ELIMINADO);

		int act = JDBCUtil.executeUpdate(query);

		// eliminamos los archivos en disco
		DigitalTO digitalTO = new DigitalTO();
		for (Iterator ite = lista.keySet().iterator(); ite.hasNext();) {
			// eliminamos el pdf
			String id = (String) ite.next();
			digitalTO.setIdDigital(id);

			File arc = new File(digitalTO.getNameFileDiskFullPath());
			arc.delete();

			// eliminamos el archivo nativo si existe
			String nameFile = (String) lista.get(id);
			if (!nameFile.endsWith(".pdf")) {
				// extraemos la extension del archivo
				StringBuffer name = new StringBuffer(
						StringUtil.getOnlyNameFile(nameFile));
				name = name.reverse();
				StringBuffer ext = new StringBuffer(name.substring(0,
						name.indexOf(".") + 1));
				ext = ext.reverse();

				arc = new File(
						digitalTO.getNameFileDiskFullPath(ext.toString()));
				arc.delete();
			}

		}

		System.gc();
	}

	/**
	 * 
	 * @param digitalTO
	 * @throws Exception
	 */
	public void reject(DigitalTO digitalTO) throws Exception {
		query.setLength(0);
		query.setLength(0);
		query.append("UPDATE digital SET comentario=?,idStatusDigital=? WHERE idDigital=? ");

		ArrayList<Object> parametro = new ArrayList<Object>();
		parametro.add(digitalTO.getComentario());
		parametro.add(new Integer(digitalTO.getIdStatusDigital()));
		parametro.add(new Integer(digitalTO.getIdDigital()));

		JDBCUtil.executeUpdate(query, parametro);
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Collection findAllByOrder() throws Exception {

		query.setLength(0);
		query.append("select * from digital order by idDigital"); // probado en
																	// mssql
		crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		Collection<DigitalTO> lista = new ArrayList<DigitalTO>();
		fill(crs, (ArrayList<DigitalTO>) lista);

		return lista;
	}

	/**
	 * 
	 * @param idPerson
	 * @return
	 * @throws Exception
	 */
	public Collection<DigitalTO> findAllByOrder(int idPerson) throws Exception {

		query.setLength(0);
		query.append("select d.* ").append("from digital d ")
				.append("where d.idStatusDigital not in(?,?,?) ")
				.append("and ( ")
				.append("(d.idPerson=? and d.typesetter=? and d.checker=?) ") // si
																				// el
																				// que
																				// escanea,digita
																				// y
																				// verifica
																				// son
																				// los
																				// mismos
				.append("or (d.checker=? and d.idStatusDigital=?) ") // si es el
																		// que
																		// verifica
																		// y
																		// esta
																		// por
																		// verificar
				.append("or (d.idPerson=? and d.idStatusDigital=?) ") // si es
																		// el
																		// que
																		// scanea
																		// y
																		// esta
																		// rechazado
																		// para
																		// escanear
				.append("or (d.typesetter=? and (d.idStatusDigital=? or d.idStatusDigital=?)) ") // si
																									// es
																									// el
																									// que
																									// digita
																									// y
																									// es
																									// nuevo
																									// o
																									// rechazado
																									// para
																									// digitar
				.append(" ) ").append("order by idDigital ");

		ArrayList parametros = new ArrayList();
		parametros.add(new Integer(Constants.STATUS_DIGITAL_ELIMINADO));
		parametros.add(new Integer(Constants.STATUS_DIGITAL_PROCESADO));
		parametros.add(new Integer(
				Constants.STATUS_DIGITAL_PROCESADO_VERIFICADO));
		parametros.add(new Integer(idPerson));
		parametros.add(new Integer(idPerson));
		parametros.add(new Integer(idPerson));
		parametros.add(new Integer(idPerson));
		parametros.add(new Integer(Constants.STATUS_DIGITAL_POR_VERIFICAR));
		parametros.add(new Integer(idPerson));
		parametros.add(new Integer(Constants.STATUS_DIGITAL_RECHAZADO_TO_SCAN));
		parametros.add(new Integer(idPerson));
		parametros.add(new Integer(Constants.STATUS_DIGITAL_NUEVO));
		parametros.add(new Integer(Constants.STATUS_DIGITAL_RECHAZADO_TO_TYPESETTER));
		
		query.setLength(0);
		query.append("select d.* ");
		query.append("from digital d ");
		query.append("where d.idStatusDigital not in(?,?,?) "); 
		query.append("and ( ");
		query.append("(d.idPerson=? and d.typesetter=? and d.checker=?) "); // si el que escanea,digita y verifica son los mismos
		query.append("or (d.checker=? and d.idStatusDigital=?) "); // si es el que verifica y esta por verificar
		query.append("or (d.idPerson=? and d.idStatusDigital=?) "); // si es el que scanea y esta rechazado para escanear
		query.append("or (d.typesetter=? and (d.idStatusDigital=? or d.idStatusDigital=?)) "); // si es el que digita y es nuevo o rechazado para digitar
		query.append(" ) ");
		query.append("order by idDigital ");

		crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());

		Collection<DigitalTO> lista = new ArrayList<DigitalTO>();
		fill(crs, (ArrayList<DigitalTO>) lista);

		return lista;
	}

	/**
	 * 
	 * @param idPerson
	 * @return
	 * @throws Exception
	 */
	public Collection<DigitalTO> findAllDocByOrder(int idPerson)
			throws Exception {

		query.setLength(0);
		query.append("select d.* ")
				.append("from digital d ")
				.append("where d.idStatusDigital in(?,?,?) ")
				.append("and ( d.idPerson=? or d.typesetter=? or d.checker=? )")
				.append("order by idDigital ");

		ArrayList parametros = new ArrayList();
		parametros.add(new Integer(Constants.STATUS_DIGITAL_ELIMINADO));
		parametros.add(new Integer(Constants.STATUS_DIGITAL_PROCESADO));
		parametros.add(new Integer(
				Constants.STATUS_DIGITAL_PROCESADO_VERIFICADO));

		parametros.add(new Integer(idPerson));
		parametros.add(new Integer(idPerson));
		parametros.add(new Integer(idPerson));

		crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());

		Collection<DigitalTO> lista = new ArrayList<DigitalTO>();
		fill(crs, (ArrayList<DigitalTO>) lista);

		return lista;
	}

	/**
	 * 
	 * @param crs
	 * @param lista
	 * @return
	 * @throws SQLException
	 */
	private ArrayList fill(CachedRowSet crs, ArrayList<DigitalTO> lista)
			throws SQLException {
		while (crs.next()) {
			DigitalTO f = fillFromCache(crs);
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
	private DigitalTO fill(CachedRowSet crs) throws SQLException {
		DigitalTO f = new DigitalTO();
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
	private DigitalTO fillFromCache(CachedRowSet crs) throws SQLException {
		DigitalTO f = new DigitalTO();
		if (crs != null) {
			f.setIdDigital(crs.getString("idDigital"));
			f.setNameFile(crs.getString("nameFile"));
			f.setNameDocument(crs.getString("nameDocument"));
			f.setType(crs.getString("Type"));
			f.setDateCreation(crs.getString("dateCreation"));
			f.setNumberTest(crs.getString("numberTest"));
			f.setIdPerson(crs.getString("idPerson"));
			f.setIdStatusDigital(crs.getString("idStatusDigital"));
			f.setComentario(crs.getString("comentario"));
			f.setLote(crs.getString("lote"));
			f.setOwnerTypeDoc(crs.getString("ownerTypeDoc"));
			f.setIdNode(crs.getString("idNode"));
			f.setTypesetter(crs.getString("typesetter"));
			f.setChecker(crs.getString("checker"));
			f.setVisible(crs.getString("visible"));
			f.setVersionMayor(crs.getString("versionMayor"));
			f.setVersionMenor(crs.getString("versionMenor"));
			f.setCodigo(crs.getString("codigo"));
			f.setPublicado(crs.getString("publicado"));
			f.setExpira(crs.getString("expira"));
			if(crs.getTimestamp("fechaPublicacion") != null){
				f.setFechaPublicacion(ToolsHTML.sdf1.format(crs.getTimestamp("fechaPublicacion")));
			}
			if(crs.getTimestamp("fechaVencimiento") != null){
				f.setFechaPublicacion(ToolsHTML.sdf1.format(crs.getTimestamp("fechaVencimiento")));
			}
			f.setComentarios(crs.getString("comentarios"));
			f.setUrl(crs.getString("url"));
			f.setPalabrasClaves(crs.getString("palabrasClaves"));
			f.setDescripcion(crs.getString("descripcion"));
			f.setOtrosDatos(crs.getString("otrosDatos"));
			f.setPublicDoc("1");
			f.setInternalMetaData(crs.getString("internalMetaData"));
			
			// tipo de documento
			TypeDocumentsForm forma = new TypeDocumentsForm();
			forma.setId(f.getType());
			try {
				HandlerTypeDoc.load(forma);
				f.setPublicDoc(String.valueOf(forma.getPublicDoc()));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return f;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public int countRegister() throws Exception {
		query.setLength(0);
		query.append("select count(*) As reg from digital");
		crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		crs.next();
		return crs.getInt(1);
	}

	/**
	 * 
	 * @param digitalTO
	 * @return
	 * @throws Exception
	 */
	public DigitalTO findById(DigitalTO digitalTO) throws Exception {
		Collection<DigitalTO> lista = new ArrayList<DigitalTO>();
		ArrayList parametros = new ArrayList();
		parametros.add(new Integer(digitalTO.getIdDigital()));
				
		query.append("SELECT * ");
		query.append("FROM digital WHERE idDigital=? ");

		crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());

		return fill(crs);
	}

	/**
	 * 
	 * @param digitalTO
	 * @return
	 * @throws Exception
	 */
	public DigitalTO findByNameFileAndInternalMetaData(DigitalTO digitalTO) throws Exception {
		Collection<DigitalTO> lista = new ArrayList<DigitalTO>();
		ArrayList parametros = new ArrayList();
		parametros.add(digitalTO.getNameFile());
		parametros.add(digitalTO.getInternalMetaData());
		
		query.append("SELECT * ");
		query.append("FROM digital WHERE namefile=? ");
		query.append("AND internalMetaData=? ");
		
		crs = JDBCUtil.executeQuery(query,parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
		
		return fill(crs);
	}
	
	public String findPreviousByOrder(DigitalTO digitalTO) throws Exception {
		return findByOrder(digitalTO, true);
	}

	/**
	 * 
	 * @param digitalTO
	 * @return
	 * @throws Exception
	 */
	public String findNextByOrder(DigitalTO digitalTO) throws Exception {
		return findByOrder(digitalTO, false);
	}

	/**
	 * 
	 * @param digitalTO
	 * @param isPrevious
	 * @return
	 * @throws Exception
	 */
	public String findByOrder(DigitalTO digitalTO, boolean isPrevious)
			throws Exception {
		Collection<DigitalTO> lista = new ArrayList<DigitalTO>();
		ArrayList parametros = new ArrayList();
		String idPerson = String.valueOf(digitalTO.getUsuario().getIdPerson()); 
		
		String limiteMsSql = "";
		String limitePostgres = "";

		query.setLength(0);
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

		query.setLength(0);
		query.append("select ")
				.append(limiteMsSql)
				.append(" d.idDigital ")
				.append("from digital d ")
				.append("where d.idStatusDigital not in(?,?,?) and d.idDigital")
				.append(isPrevious ? "<" : ">")
				.append("? ")
				.append("and ((d.idPerson=? and d.typesetter=? and d.checker=?) ")
				// si
				// el
				// que
				// escanea,digita
				// y
				// verifica
				// son
				// los
				// mismos
				.append("or (d.checker=? and d.idStatusDigital=?) ")
				// si es el
				// que
				// verifica
				// y esta
				// por
				// verificar
				.append("or (d.idPerson=? and d.idStatusDigital=?) ")
				// si es el
				// que
				// scanea y
				// esta
				// rechazado
				// para
				// escanear
				.append("or (d.typesetter=? and (d.idStatusDigital=? or d.idStatusDigital=?))) ")
				// si
				// es
				// el
				// que
				// digita
				// y
				// es
				// nuevo
				// o
				// rechazado
				// para
				// digitar
				.append("order by idDigital ").append(isPrevious ? "DESC" : "")
				.append(" ").append(limitePostgres);

		parametros = new ArrayList();
		parametros.add(new Integer(Constants.STATUS_DIGITAL_PROCESADO));
		parametros.add(new Integer(Constants.STATUS_DIGITAL_PROCESADO_VERIFICADO));

		parametros.add(new Integer(digitalTO.getIdDigital()));

		parametros.add(new Integer(idPerson));
		parametros.add(new Integer(idPerson));
		parametros.add(new Integer(idPerson));

		parametros.add(new Integer(idPerson));
		parametros.add(new Integer(Constants.STATUS_DIGITAL_POR_VERIFICAR));

		parametros.add(new Integer(idPerson));
		parametros.add(new Integer(Constants.STATUS_DIGITAL_RECHAZADO_TO_SCAN));

		parametros.add(new Integer(idPerson));
		parametros.add(new Integer(Constants.STATUS_DIGITAL_NUEVO));
		parametros.add(new Integer(Constants.STATUS_DIGITAL_RECHAZADO_TO_TYPESETTER));
		
		query.setLength(0);
		query.append("select ").append(limiteMsSql).append(" d.idDigital ");
		query.append("from digital d ");
		query.append("where d.idStatusDigital not in(?,?) and d.idDigital").append(isPrevious?"<":">").append("? "); 
		query.append("and ((d.idPerson=? and d.typesetter=? and d.checker=?) "); // si el que escanea,digita y verifica son los mismos
		query.append("or (d.checker=? and d.idStatusDigital=?) "); // si es el que verifica y esta por verificar
		query.append("or (d.idPerson=? and d.idStatusDigital=?) "); // si es el que scanea y esta rechazado para escanear
		query.append("or (d.typesetter=? and (d.idStatusDigital=? or d.idStatusDigital=?))) "); // si es el que digita y es nuevo o rechazado para digitar
		query.append("order by idDigital ").append(isPrevious?"DESC":"").append(" ").append(limitePostgres);
		
		crs = JDBCUtil.executeQuery(query,parametros, Thread.currentThread().getStackTrace()[1].getMethodName());

		crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			return crs.getString("idDigital");
		} else {
			return "";
		}
	}

	/**
	 * 
	 * @param inicio
	 * @return
	 * @throws Exception
	 */
	public ArrayList findAllNumber(String inicio) throws Exception {
		ArrayList<String> lista = new ArrayList<String>();
		ArrayList parametros = new ArrayList();

		String limiteMsSql = "";
		String limitePostgres = "";
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			limiteMsSql = " top 500 ";
			break;
		case Constants.MANEJADOR_POSTGRES:
			limitePostgres = " limit 500 ";
			break;
		case Constants.MANEJADOR_MYSQL:
			limitePostgres = " limit 500 ";
			break;
		}

		query.setLength(0);
		query.append("select ").append(limiteMsSql).append(" codigo ")
				.append("from digital ")
				.append("where idStatusDigital != ? and codigo>=? ")
				.append("order by codigo ").append(limitePostgres);

		parametros = new ArrayList();
		parametros.add(new Integer(Constants.STATUS_DIGITAL_ELIMINADO));
		parametros.add(inicio);

		crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			lista.add(crs.getString(1));
		}

		return lista;
	}

	/**
	 * 
	 * @param inicio
	 * @return
	 * @throws Exception
	 */
	public String findNextNumber(String inicio) throws Exception {
		if (inicio != null && !inicio.trim().equals("")) {
			ArrayList numeracion = findAllNumber(inicio);
			while (numeracion.contains(inicio) && numeracion.size() > 0) {
				// buscamos dentro del vector el numero disponible
				String ultimo = (String) numeracion.get(numeracion.size() - 1);
				while (inicio != ultimo
						&& Long.parseLong(inicio) < Long.parseLong(ultimo)) {
					inicio = String.valueOf(Integer.parseInt(inicio) + 1);
					if (!numeracion.contains(inicio)) {
						return inicio;
					}
				}
				inicio = String.valueOf(Integer.parseInt(inicio) + 1);
				numeracion = findAllNumber(inicio);
			}
		}

		return inicio;
	}

}
