package com.focus.qweb.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.PersonTO;

import sun.jdbc.rowset.CachedRowSet;

/**
 * 
 * @author JRivero
 * 
 */
public class PersonDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oPersonTO) throws Exception {
		PersonTO personTO = null;
		personTO = (PersonTO) oPersonTO;

		query.setLength(0);
		query.append("INSERT INTO person (");
		query.append("idPerson,nameUser,clave,idGrupo,Nombres,Apellidos,cargo, ");
		query.append("email,Direccion,Ciudad,Estado,CodPais,CodigoPostal,Tlf, ");
		query.append("IdLanguage,numRecordPages,accountActive,primeravez,idarea,dateLastPass, ");
		query.append("EditDocumentCheckOut,dateLastPassEdit,edit,published,search,publishedOption, ");
		query.append("filesOption,searchOption,menuHeadOption,modo,lado,ppp,panel,pagina,separar, ");
		query.append("minimo,typeDocuments,ownerTypeDoc,idNodeDigital,typesetter,checker, ");
		query.append("lote,correlativo,javaWebStart,idNodeService,sacopOption ");
		query.append(" ) ");
		query.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		
		parametros = new ArrayList<Object>();
		parametros.add(personTO.getIdPersonInt());
		parametros.add(personTO.getNameUser());
		parametros.add(personTO.getClave());
		parametros.add(personTO.getIdGrupoInt());
		parametros.add(personTO.getNombres());
		parametros.add(personTO.getApellidos());
		parametros.add(personTO.getCargo());
		parametros.add(personTO.getEmail());
		parametros.add(personTO.getDireccion());
		parametros.add(personTO.getCiudad());
		parametros.add(personTO.getEstado());
		parametros.add(personTO.getCodPais());
		parametros.add(personTO.getCodigoPostal());
		parametros.add(personTO.getTlf());
		parametros.add(personTO.getIdLanguage());
		parametros.add(personTO.getNumRecordPagesInt());
		parametros.add(personTO.getAccountActiveByte());
		parametros.add(personTO.getPrimeravezByte());
		parametros.add(personTO.getIdarea());
		parametros.add(personTO.getDateLastPass());
		parametros.add(personTO.getEditDocumentCheckOutInt());
		parametros.add(personTO.getDateLastPassEdit());
		parametros.add(personTO.getEditInt());
		parametros.add(personTO.getPublished());
		parametros.add(personTO.getSearch());
		parametros.add(personTO.getPublishedOption());
		parametros.add(personTO.getFilesOption());
		parametros.add(personTO.getSearchOption());
		parametros.add(personTO.getMenuHeadOption());
		parametros.add(personTO.getModoInt());
		parametros.add(personTO.getLadoInt());
		parametros.add(personTO.getPppInt());
		parametros.add(personTO.getPanelInt());
		parametros.add(personTO.getPaginaInt());
		parametros.add(personTO.getSepararInt());
		parametros.add(personTO.getMinimoInt());
		parametros.add(personTO.getTypeDocumentsInt());
		parametros.add(personTO.getOwnerTypeDocInt());
		parametros.add(personTO.getIdNodeDigitalInt());
		parametros.add(personTO.getTypesetterInt());
		parametros.add(personTO.getCheckerInt());
		parametros.add(personTO.getLote());
		parametros.add(personTO.getCorrelativo());
		parametros.add(personTO.getJavaWebStart());
		parametros.add(personTO.getIdNodeServiceInt());
		parametros.add(personTO.getSacopOption());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oPersonTO) throws Exception {
		PersonTO personTO = null;
		personTO = (PersonTO) oPersonTO;

		query.setLength(0);
		query.append("UPDATE person SET ");
		query.append("nameUser=?,clave=?,idGrupo=?,Nombres=?,Apellidos=?,cargo=?, ");
		query.append("email=?,Direccion=?,Ciudad=?,Estado=?,CodPais=?,CodigoPostal=?,Tlf=?, ");
		query.append("IdLanguage=?,numRecordPages=?,accountActive=?,primeravez=?,idarea=?,dateLastPass=?, ");
		query.append("EditDocumentCheckOut=?,dateLastPassEdit=?,edit=?,published=?,search=?,publishedOption=?, ");
		query.append("filesOption=?,searchOption=?,menuHeadOption=?,modo=?,lado=?,ppp=?,panel=?,pagina=?,separar=?, ");
		query.append("minimo=?,typeDocuments=?,ownerTypeDoc=?,idNodeDigital=?,typesetter=?,checker=?, ");
		query.append("lote=?,correlativo=?,javaWebStart=?,ideNodeService=?,sacopOption=? ");
		query.append("WHERE idPerson=?");

		parametros = new ArrayList<Object>();
		parametros.add(personTO.getNameUser());
		parametros.add(personTO.getClave());
		parametros.add(personTO.getIdGrupoInt());
		parametros.add(personTO.getNombres());
		parametros.add(personTO.getApellidos());
		parametros.add(personTO.getCargo());
		parametros.add(personTO.getEmail());
		parametros.add(personTO.getDireccion());
		parametros.add(personTO.getCiudad());
		parametros.add(personTO.getEstado());
		parametros.add(personTO.getCodPais());
		parametros.add(personTO.getCodigoPostal());
		parametros.add(personTO.getTlf());
		parametros.add(personTO.getIdLanguage());
		parametros.add(personTO.getNumRecordPagesInt());
		parametros.add(personTO.getAccountActiveByte());
		parametros.add(personTO.getPrimeravezByte());
		parametros.add(personTO.getIdarea());
		parametros.add(personTO.getDateLastPass());
		parametros.add(personTO.getEditDocumentCheckOutInt());
		parametros.add(personTO.getDateLastPassEdit());
		parametros.add(personTO.getEditInt());
		parametros.add(personTO.getPublished());
		parametros.add(personTO.getSearch());
		parametros.add(personTO.getPublishedOption());
		parametros.add(personTO.getFilesOption());
		parametros.add(personTO.getSearchOption());
		parametros.add(personTO.getMenuHeadOption());
		parametros.add(personTO.getModoInt());
		parametros.add(personTO.getLadoInt());
		parametros.add(personTO.getPppInt());
		parametros.add(personTO.getPanelInt());
		parametros.add(personTO.getPaginaInt());
		parametros.add(personTO.getSepararInt());
		parametros.add(personTO.getMinimoInt());
		parametros.add(personTO.getTypeDocumentsInt());
		parametros.add(personTO.getOwnerTypeDocInt());
		parametros.add(personTO.getIdNodeDigitalInt());
		parametros.add(personTO.getTypesetterInt());
		parametros.add(personTO.getCheckerInt());
		parametros.add(personTO.getLote());
		parametros.add(personTO.getCorrelativo());
		parametros.add(personTO.getJavaWebStart());
		//aqui DARWINUZCATEGUI
		parametros.add(personTO.getIdNodeServiceInt());
		//aqui DARWINUZCATEGUI
		parametros.add(personTO.getIdPersonInt()); // primary key
		parametros.add(personTO.getSacopOption());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oPersonTO) throws Exception {
		PersonTO personTO = null;
		personTO = (PersonTO) oPersonTO;

		query.setLength(0);
		query.append("DELETE FROM person WHERE idPerson=?");

		parametros = new ArrayList<Object>();
		parametros.add(personTO.getIdPersonInt());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oPersonTO) throws Exception {
		PersonTO personTO = null;
		personTO = (PersonTO) oPersonTO;

		query.setLength(0);
		query.append("SELECT * FROM person WHERE idPerson=").append(personTO.getIdPersonInt());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, personTO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<PersonTO> listar() throws Exception {
		PersonTO personTO = null;
		ArrayList<PersonTO> lista = new ArrayList<PersonTO>();

		query.setLength(0);
		query.append("SELECT * FROM person ORDER BY idPerson");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			personTO = new PersonTO();
			load(crs, personTO);
			lista.add(personTO);
		}
		return lista;
	}
	
	/**
	 * 
	 */
	public Map<String,PersonTO> listarPersonAlls() throws Exception {
		PersonTO personTO = null;
		Map<String,PersonTO> lista = new HashMap<String,PersonTO>();

		query.setLength(0);
		query.append("SELECT * FROM person");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			personTO = new PersonTO();
			load(crs, personTO);
			lista.put(personTO.getIdPerson(),personTO);
		}
		return lista;
	}
	
	/**
	 * 
	 */
	public ArrayList<PersonTO> listarPersonWithOutTmpAll() throws Exception {
		PersonTO personTO = null;
		ArrayList<PersonTO> lista = new ArrayList<PersonTO>();

		query.setLength(0);
		query.append("SELECT * FROM person WHERE accountActive = '1' and nameuser!='temporal' ORDER BY Apellidos ");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			personTO = new PersonTO();
			load(crs, personTO);
			lista.add(personTO);
		}
		return lista;
	}
	
	/**
	 * 
	 */
	public ArrayList<PersonTO> listarPersonAll() throws Exception {
		PersonTO personTO = null;
		ArrayList<PersonTO> lista = new ArrayList<PersonTO>();

		query.setLength(0);
		query.append("SELECT * FROM person WHERE accountActive = '1' ORDER BY lower(Apellidos) ");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			personTO = new PersonTO();
			load(crs, personTO);
			lista.add(personTO);
		}
		return lista;
	}
	

	
	public void load(CachedRowSet crs, PersonTO personTO) throws SQLException {
		personTO.setIdPerson(crs.getString("idPerson"));
		personTO.setId(crs.getString("idPerson"));
		personTO.setNameUser(crs.getString("nameUser"));
		personTO.setClave(crs.getString("clave"));
		personTO.setIdGrupo(crs.getString("idGrupo"));
		personTO.setNombres(crs.getString("Nombres"));
		personTO.setApellidos(crs.getString("Apellidos"));
		personTO.setCargo(crs.getString("cargo"));
		personTO.setEmail(crs.getString("email"));
		personTO.setDireccion(crs.getString("Direccion"));
		personTO.setCiudad(crs.getString("Ciudad"));
		personTO.setEstado(crs.getString("Estado"));
		personTO.setCodPais(crs.getString("CodPais"));
		personTO.setCodigoPostal(crs.getString("CodigoPostal"));
		personTO.setTlf(crs.getString("Tlf"));
		personTO.setIdLanguage(crs.getString("IdLanguage"));
		personTO.setNumRecordPages(crs.getString("numRecordPages"));
		personTO.setAccountActive(crs.getString("accountActive"));
		personTO.setPrimeravez(crs.getString("primeravez"));
		personTO.setIdarea(crs.getString("idarea"));
		personTO.setDateLastPass(crs.getString("dateLastPass"));
		personTO.setEditDocumentCheckOut(crs.getString("EditDocumentCheckOut"));
		personTO.setDateLastPassEdit(crs.getString("dateLastPassEdit"));
		personTO.setEdit(crs.getString("edit"));
		personTO.setPublished(crs.getString("published"));
		personTO.setSearch(crs.getString("search"));
		personTO.setPublishedOption(crs.getString("publishedOption"));
		personTO.setFilesOption(crs.getString("filesOption"));
		personTO.setSearchOption(crs.getString("searchOption"));
		personTO.setMenuHeadOption(crs.getString("menuHeadOption"));
		personTO.setModo(crs.getString("modo"));
		personTO.setLado(crs.getString("lado"));
		personTO.setPpp(crs.getString("ppp"));
		personTO.setPanel(crs.getString("panel"));
		personTO.setPagina(crs.getString("pagina"));
		personTO.setSeparar(crs.getString("separar"));
		personTO.setMinimo(crs.getString("minimo"));
		personTO.setTypeDocuments(crs.getString("typedocuments"));
		personTO.setOwnerTypeDoc(crs.getString("ownerTypeDoc"));
		personTO.setIdNodeDigital(crs.getString("idNodeDigital"));
		personTO.setTypesetter(crs.getString("typesetter"));
		personTO.setChecker(crs.getString("checker"));
		personTO.setLote(crs.getString("lote"));
		personTO.setCorrelativo(crs.getString("correlativo"));
		personTO.setJavaWebStart(crs.getString("javaWebStart"));
		personTO.setIdNodeService(crs.getString("idNodeService"));
		personTO.setSacopOption(crs.getString("sacopOption"));
	}

}
