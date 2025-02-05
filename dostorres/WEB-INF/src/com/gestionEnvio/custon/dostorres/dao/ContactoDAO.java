package com.gestionEnvio.custon.dostorres.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.to.PersonTO;
import com.gestionEnvio.custon.dostorres.interfaces.ObjetoDosTorresDAO;
import com.gestionEnvio.custon.dostorres.interfaces.ObjetoDosTorresTO;
import com.gestionEnvio.custon.dostorres.to.ContactoTO;

import sun.jdbc.rowset.CachedRowSet;



public class ContactoDAO implements ObjetoDosTorresDAO {

	private StringBuffer query = new StringBuffer();
	//private ArrayList<Object> parametros = null;
	
	@Override
	public boolean cargar(ObjetoDosTorresTO oContactoTO) throws Exception {
		
		ContactoTO contactoTO = null;
		contactoTO = (ContactoTO) oContactoTO;

		query.setLength(0);
		query.append("SELECT * FROM CONDT.dbo.CTADIRECTORIO WHERE IDCONTACTO=").append(contactoTO.getIdInt());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, contactoTO);
			return true;
		}
		return false;
	}
		
	@Override
	public ArrayList<ContactoTO> listar() throws Exception {
		ContactoTO contactoTO = null;
		ArrayList<ContactoTO> lista = new ArrayList<ContactoTO>();
		
		query.setLength(0);
		query.append("SELECT * FROM CONDT.dbo.CTADIRECTORIO ORDER BY  IDCONTACTO");
		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		
		while (crs.next()) {
			contactoTO = new ContactoTO();
			load(crs, contactoTO);
			lista.add(contactoTO);
		}
		return lista;
	}
	
	public Map<String,ContactoTO> listarContactoAlls() throws Exception {
		ContactoTO contactoTO = null;
		Map<String,ContactoTO> lista = new HashMap<String,ContactoTO>();

		query.setLength(0);
		query.append("SELECT IDCONTACTO,ORIGEN,CODIGO,NOMBRE,TELEFONO1,TELEFONO2,FAX,CELULAR,");
		query.append(" CAST(DIRECCION AS VARCHAR(4000)) AS DIRECCION, ");
		query.append("EMAIL,EMAIL2,EMAIL3 ");
		query.append(" from CONDT.dbo.CTADIRECTORIO ");
		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			contactoTO = new ContactoTO();
			load(crs, contactoTO);
			lista.put(contactoTO.getCodigo(),contactoTO);
		}
		return lista;
	}

	
	public void load(CachedRowSet crs, ContactoTO contactoTO) throws SQLException {
		//StringBuilder dir = new StringBuilder();
		String valorTexto = String.valueOf(crs.getString("DIRECCION"));
		valorTexto = !ToolsHTML.isEmptyOrNull(valorTexto) ? valorTexto : "S/N DIRECCION";
		//dir.append(valorTexto);
		//System.out.println("****************************dao----**************");
		//System.out.println(valorTexto);
		//System.out.println("****************************dao----**************");
		contactoTO.setId(crs.getString("IDCONTACTO"));
		contactoTO.setOrigen(crs.getString("ORIGEN"));
		contactoTO.setCodigo(crs.getString("CODIGO"));
		contactoTO.setNombre(crs.getString("NOMBRE"));
		contactoTO.setTelefono1(crs.getString("TELEFONO1"));
		contactoTO.setTelefono2(crs.getString("TELEFONO2"));
		contactoTO.setFax(crs.getString("FAX"));
		contactoTO.setCelular(crs.getString("CELULAR"));
		//contactoTO.setDireccion(crs.getString("DIRECCION"));
		contactoTO.setDireccion(valorTexto.toString());
		contactoTO.setEmail(crs.getString("EMAIL"));
		contactoTO.setEmail2(crs.getString("EMAIL2"));
		contactoTO.setEmail3(crs.getString("EMAIL3"));
			}
	
}