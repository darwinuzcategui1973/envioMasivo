package com.focus.wonderware.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.mail.Session;
import javax.transaction.Transaction;

import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.sacop.actions.LoadSacop;
import com.desige.webDocuments.sacop.forms.Plantilla1BD;
import com.desige.webDocuments.sacop.forms.Plantilla1BDesqueleto;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;
import com.focus.qweb.dao.ActiveFactoryDAO;
import com.focus.qweb.dao.ConfTagNameDAO;
import com.focus.qweb.dao.PlanillaSacop1DAO;
import com.focus.qweb.dao.PlanillaSacop1EsqueletoDAO;
import com.focus.qweb.dao.SacopIntouchHijoDAO;
import com.focus.qweb.dao.SacopIntouchPadreDAO;
import com.focus.qweb.dao.TagNameDAO;
import com.focus.qweb.to.ActiveFactoryTO;
import com.focus.qweb.to.ConfTagNameTO;
import com.focus.qweb.to.PlanillaSacop1EsqueletoTO;
import com.focus.qweb.to.PlanillaSacop1TO;
import com.focus.qweb.to.SacopIntouchHijoTO;
import com.focus.qweb.to.SacopIntouchPadreTO;
import com.focus.qweb.to.TagNameTO;
import com.focus.wonderware.forms.ActiveFactory_frm;
import com.focus.wonderware.intocuh_sacop.forms.Sacop_Intouch_Conftagname;
import com.focus.wonderware.intocuh_sacop.forms.Sacop_Intouchh_frm;
import com.focus.wonderware.intocuh_sacop.forms.sacop_intouch_padre_frm;

/**
 * Created by IntelliJ IDEA. User: srodriguez Date: Feb 15, 2007 Time: 1:59:50 PM To change this template use File | Settings | File Templates.
 */
public class HandlerProcesosWonderWare extends HandlerBD {


	public ActiveFactory_frm getActiveFactoryDocuments(String numgen) throws Exception {
		ActiveFactory_frm activeFactory = null;
		
		ActiveFactoryDAO oActiveFactoryDAO = new ActiveFactoryDAO();
		ActiveFactoryTO oActiveFactoryTO = new ActiveFactoryTO();
		
		if (!ToolsHTML.isEmptyOrNull(numgen)) {
			oActiveFactoryDAO.cargar(oActiveFactoryTO);
			oActiveFactoryTO.setNumgen(numgen.trim());
			
			oActiveFactoryDAO.cargarByNumGen(oActiveFactoryTO);
			
			activeFactory = new ActiveFactory_frm(oActiveFactoryTO);
		}
		return activeFactory;
	}

	public Collection getActiveFactoryDocuments(String numgen, ActiveFactory_frm activeFactory) {
		Vector result = new Vector();

		ArrayList ls = new ArrayList();
		ActiveFactoryDAO oActiveFactoryDAO = new ActiveFactoryDAO();
		ActiveFactoryTO oActiveFactoryTO = new ActiveFactoryTO();

		try {
			// ActiveFactory_frm activeFactory = null;
			if (!ToolsHTML.isEmptyOrNull(numgen)) {
				ls = oActiveFactoryDAO.listarAlls(numgen.trim());
				
				for (Iterator iter = ls.iterator(); iter.hasNext();) {
					oActiveFactoryTO = (ActiveFactoryTO) iter.next();
					activeFactory = new ActiveFactory_frm(oActiveFactoryTO);
				}
				
			} else {
				
				ls = oActiveFactoryDAO.listarAlls();
				
				for (Iterator iter = ls.iterator(); iter.hasNext();) {
					oActiveFactoryTO = (ActiveFactoryTO) iter.next();
					activeFactory = new ActiveFactory_frm(oActiveFactoryTO);

					Search bean = new Search(String.valueOf(activeFactory.getNumgen()), activeFactory.getDescripcion());
					bean.setAditionalInfo(activeFactory.getUrl());
					result.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean insert(ActiveFactory_frm forma) {
		ActiveFactoryDAO oActiveFactoryDAO = new ActiveFactoryDAO();
		ActiveFactoryTO oActiveFactoryTO = new ActiveFactoryTO();
		
		try {
			int num = HandlerStruct.proximo("idactivefactorydocument", "idactivefactorydocument", "idactivefactorydocument");
			forma.setIdactivefactorydocument(num);
			
			oActiveFactoryTO = new ActiveFactoryTO(forma);
			oActiveFactoryDAO.insertar(oActiveFactoryTO);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	public void update(ActiveFactory_frm forma) throws ApplicationExceptionChecked {
		ActiveFactoryDAO oActiveFactoryDAO = new ActiveFactoryDAO();
		ActiveFactoryTO oActiveFactoryTO = new ActiveFactoryTO();
		
		try {
			
			oActiveFactoryTO = new ActiveFactoryTO(forma);
			oActiveFactoryDAO.actualizar(oActiveFactoryTO);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleted(ActiveFactory_frm forma) throws ApplicationExceptionChecked {
			ActiveFactoryDAO oActiveFactoryDAO = new ActiveFactoryDAO();
			ActiveFactoryTO oActiveFactoryTO = new ActiveFactoryTO();
			
			try {
				
				oActiveFactoryTO = new ActiveFactoryTO(forma);
				oActiveFactoryDAO.eliminar(oActiveFactoryTO);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	public Collection getSacopIntouchWonderware(long idplanillasacop1) {
		SacopIntouchHijoDAO oSacopIntouchHijoDAO = new SacopIntouchHijoDAO();
		SacopIntouchHijoTO oSacopIntouchHijoTO = new SacopIntouchHijoTO();
		
		Sacop_Intouchh_frm sacop_intouch_frm = null;

		ArrayList lista = new ArrayList();
		try {
			lista = oSacopIntouchHijoDAO.listar(String.valueOf(Constants.permission), String.valueOf(idplanillasacop1));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}

	public Collection getSacopIntouchWonderwareEditamosTagName(String id, Plantilla1BDesqueleto planillasacop1Wonderware) {
		SacopIntouchPadreDAO oSacopIntouchPadreDAO = new SacopIntouchPadreDAO();
		sacop_intouch_padre_frm ssacop_intouch_padre_frm = null;
		
		Vector result = new Vector();
		
		try {
			ArrayList lista = oSacopIntouchPadreDAO.listar(String.valueOf(Constants.permission), id);
			
			Iterator iter = lista.iterator();
			if (iter.hasNext()) {
				ssacop_intouch_padre_frm = new sacop_intouch_padre_frm((SacopIntouchPadreTO) iter.next());
				
				planillasacop1Wonderware.setIdplanillasacop1(ssacop_intouch_padre_frm.getIdplanillasacop1());
				
				// procedimiento o metodo que me trae todos los tagnames activos
				result = getSacopIntouchWonderwareHijo(String.valueOf(ssacop_intouch_padre_frm.getIdplanillasacop1()));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public sacop_intouch_padre_frm getSacopIntouchWonderwarePadre(String idplanilla, boolean swActivado) {
		sacop_intouch_padre_frm ssacop_intouch_padre_frm = new sacop_intouch_padre_frm();
		byte activado = Constants.notPermission;
		if (swActivado) {
			activado = Constants.permission;
		}
		try {
			Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			StringBuffer query1 = new StringBuffer("select * from tbl_sacop_intouch_padre c where  c.active=").append(activado);
			query1.append(" and c.idplanillasacop1=").append(idplanilla.toString());

			PreparedStatement pst = con.prepareStatement(JDBCUtil.replaceCastMysql(query1.toString()));
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				/*
				 * //System.out.println("rs.getString(\"enable\") = " + rs.getString("enable")); //System.out.println("rs.getString(\"idplanillasacop1\") = " +
				 * rs.getString("idplanillasacop1")); //System.out.println("rs.getString(\"idsacopintouchpadre\") = " + rs.getString("idsacopintouchpadre"));
				 * //System.out.println("rs.getString(\"active\") = " + rs.getString("active"));
				 */
				ssacop_intouch_padre_frm.setActive(Byte.parseByte(rs.getString("active")));
				ssacop_intouch_padre_frm.setEnable(Byte.parseByte(rs.getString("enable")));
				ssacop_intouch_padre_frm.setIdplanillasacop1(Long.parseLong(rs.getString("idplanillasacop1")));
				ssacop_intouch_padre_frm.setIdsacopintouchpadre(Long.parseLong(rs.getString("idsacopintouchpadre")));

			}
			rs.close();
			pst.close();
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// HibernateUtil.closeSession();
		}
		return ssacop_intouch_padre_frm;
	}

	public Collection getSacopIntouchWonderwarePadre() {
		SacopIntouchPadreDAO oSacopIntouchPadreDAO = new SacopIntouchPadreDAO();
		
		Vector result = new Vector();
		try {
			sacop_intouch_padre_frm ssacop_intouch_padre_frm = null;
			
			ArrayList lista = oSacopIntouchPadreDAO.listarOrderByIdSacopIntouchPadre(Constants.permissionSt);
			
			// Hashtable unico = new Hashtable();
			Search bean = null;
			int i = 0;
			Plantilla1BDesqueleto plantilla1BDesqueleto = null;// new Plantilla1BDesqueleto();
			
			for (Iterator iter = lista.iterator(); iter.hasNext();) {
				ssacop_intouch_padre_frm = new sacop_intouch_padre_frm((SacopIntouchPadreTO) iter.next());
				StringBuffer key = new StringBuffer();
				key.append(ssacop_intouch_padre_frm.getIdsacopintouchpadre());

				plantilla1BDesqueleto = getPlantilla1BDesqueleto(ssacop_intouch_padre_frm.getIdplanillasacop1());

				if (plantilla1BDesqueleto != null) {
					// el id de la tabla sacop_padre, el numero de la plantilla esqueleto
					bean = new Search(String.valueOf(ssacop_intouch_padre_frm.getIdsacopintouchpadre()), plantilla1BDesqueleto.getSacopnum());
					// el id del la tabla esqueleto de esa plantoilla
					bean.setAditionalInfo(String.valueOf(plantilla1BDesqueleto.getIdplanillasacop1()));
					// como noi hay mas vaiables en serach, usamos contact para emulr si esta habilitado o deshabilitado
					bean.setContact(String.valueOf(ssacop_intouch_padre_frm.getEnable()));
					result.add(bean);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Plantilla1BDesqueleto getPlantilla1BDesqueleto(long idplanillasacop1esqueleto) {
		PlanillaSacop1EsqueletoDAO oPlanillaSacop1EsqueletoDAO = new PlanillaSacop1EsqueletoDAO();
		PlanillaSacop1EsqueletoTO oPlanillaSacop1EsqueletoTO = new PlanillaSacop1EsqueletoTO();
		
		boolean result = false;
		Plantilla1BDesqueleto plantilla1BDesqueleto = null;
		try {
			
			oPlanillaSacop1EsqueletoTO.setIdplanillasacop1(String.valueOf(idplanillasacop1esqueleto));
			if(oPlanillaSacop1EsqueletoDAO.cargar(oPlanillaSacop1EsqueletoTO)) {
				plantilla1BDesqueleto = new Plantilla1BDesqueleto(oPlanillaSacop1EsqueletoTO);
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return plantilla1BDesqueleto;
	}

	// _________________________SacopIntouchWonderwareHijo
	// Inicio__________________________________________________________________________________________________
	public Collection getSacopIntouchWonderwareHijo(Hashtable llenarLosIngresados, String planillasacop1Wonderware) {
		SacopIntouchHijoDAO oSacopIntouchHijoDAO = new SacopIntouchHijoDAO();
		
		Vector result = new Vector();
		try {
			Sacop_Intouchh_frm sacop_Intouchh_frm = null;

			Search bean = null;
			int i = 0;
				
			ArrayList list = oSacopIntouchHijoDAO.listarByTagNameActive(Constants.permissionSt, planillasacop1Wonderware);
			
			Iterator iter1 = list.iterator();
			if (iter1.hasNext()) {
				sacop_Intouchh_frm = new Sacop_Intouchh_frm((SacopIntouchHijoTO)iter1.next());
				
				llenarLosIngresados.put(sacop_Intouchh_frm.getTagname(), sacop_Intouchh_frm.getTagname());
				bean = new Search(sacop_Intouchh_frm.getTagname(), sacop_Intouchh_frm.getTagname());
				bean.setAditionalInfo(String.valueOf(++i));
				result.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Vector getSacopIntouchWonderwareHijo(String idplanillasacop1) {
		Vector<Search> result = new Vector<Search>();
		
		SacopIntouchHijoDAO oSacopIntouchHijoDAO = new SacopIntouchHijoDAO();

		try {
			SacopIntouchHijoTO oSacopIntouchHijoTO = null;

			ArrayList lista = oSacopIntouchHijoDAO.listarByTagNameActive(String.valueOf(Constants.permission), idplanillasacop1);
			
			Search bean = null;
			int i = 0;
			for (Iterator iter = lista.iterator(); iter.hasNext();) {
				oSacopIntouchHijoTO = (SacopIntouchHijoTO) iter.next();

				bean = new Search(oSacopIntouchHijoTO.getTagName(), oSacopIntouchHijoTO.getTagName());
				bean.setAditionalInfo(String.valueOf(++i));
				result.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList HiloChecSacopIntouchWonderwareHijo(String idplanillasacop1, String tagname, boolean soloLosNoActivadosEnSacop) {
		ArrayList result = new ArrayList();
		
		SacopIntouchHijoDAO oSacopIntouchHijoDAO = new SacopIntouchHijoDAO();
		
		try {
			Sacop_Intouchh_frm sacop_Intouchh_frm = null;

			ArrayList lista = oSacopIntouchHijoDAO.listar(Constants.permissionSt, idplanillasacop1, tagname.trim());

			int i = 0;
			for (Iterator iter = lista.iterator(); iter.hasNext();) {
				sacop_Intouchh_frm = new Sacop_Intouchh_frm((SacopIntouchHijoTO)iter.next());
				
				result.add(sacop_Intouchh_frm);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// _________________________SacopIntouchWonderwareHijo Fin__________________________________________________________________________________________________
	public Collection getIntouch(String idtipo) {
		Vector<Search> result = new Vector<Search>();
		
		TagNameDAO oTagNameDAO = new TagNameDAO();
		TagNameTO oTagNameTO = null;
		
		try {
			
			ArrayList lista = oTagNameDAO.listarByIdTipo(Constants.permissionSt,idtipo);
			
			Iterator iter = lista.iterator();
			Search bean = null;
			while (iter.hasNext()) {
				oTagNameTO = (TagNameTO) iter.next();
				bean = new Search(String.valueOf(oTagNameTO.getIdTagName2()), oTagNameTO.getTagName());
				bean.setAditionalInfo(String.valueOf(oTagNameTO.getActive()));
				result.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return result;
	}

	public Collection getIntouch(Hashtable noEstenSacopIntocuh) {
		Vector result = new Vector();
		
		TagNameDAO oTagNameDAO = new TagNameDAO();
		
		try {
			ArrayList lista = oTagNameDAO.listar(Constants.permissionSt);
			
			Iterator iter = lista.iterator();
			Search bean = null;
			while (iter.hasNext()) {
				TagNameTO tagname = (TagNameTO) iter.next();
				if (!noEstenSacopIntocuh.containsKey(tagname.getTagName())) {
					bean = new Search(tagname.getTagName(), tagname.getTagName());
					noEstenSacopIntocuh.put(tagname.getTagName(), tagname.getTagName());
					result.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return result;
	}

	public void getSacop_Intouch_Conftagname(ArrayList cadena1, ArrayList cadena2, String cad2) {
		try {
			/*
			 * :AlarmGroup;Group;Comment;EventLogged;EventLoggingPriority;LoLoAlarmDisable;LoAlarmDisable;HiAlarmDisable;HiHiAlarmDisable;MinDevAlarmDisable;
			 * MajDevAlarmDisable;RocAlarmDisable;DSCAlarmDisable;LoLoAlarmInhibitor;LoAlarmInhibitor;HiAlarmInhibitor;HiHiAlarmInhibitor;MinDevAlarmInhibitor;
			 * MajDevAlarmInhibitor;RocAlarmInhibitor;DSCAlarmInhibitor "Reactor";"$System";"";No;0;0;0;0;0;0;0;0;0;"";"";"";"";"";"";"";""
			 * "Conveyor";"$System";"";No;0;0;0;0;0;0;0;0;0;"";"";"";"";"";"";"";""
			 */
			// leyenda del archivo
			// ____________________________________________________________________________
			// el primer campo con los dos puntos es el tipo de tags.
			// los demas campos son los tipo de alarmas
			// y debajo de la linea, el primer campo es el tags, y los demas campos de la linea son los valores de los tipos
			// de alarma que se encuentran en el primer nivel.
			// ____________________________________________________________________________
			ConfTagNameDAO oConfTagNameDAO = new ConfTagNameDAO();
			
			String tipoTag = (String) cadena1.get(0);
			Sacop_Intouch_Conftagname sacop_Intouch_Conftagname = null;
			
			ArrayList lista = oConfTagNameDAO.listar(Constants.permissionSt, tipoTag, false);
			
			String tagNameBD = "";
			for (Iterator iter = lista.iterator(); iter.hasNext();) {
				tagNameBD = "";
				sacop_Intouch_Conftagname = new Sacop_Intouch_Conftagname((ConfTagNameTO) iter.next());
				
				if (sacop_Intouch_Conftagname == null) {
					continue;
				}
				String idTipoTagConf = sacop_Intouch_Conftagname.getTipotag();
				String tipoalarma = sacop_Intouch_Conftagname.getTipoalarma();
				String valor = sacop_Intouch_Conftagname.getValor();
				int indice = -1;
				String valorCad2 = null;
				// en el titulo se encuentra el tipo de tags y los tipos de alarmas.
				if (cadena1.contains(tipoalarma)) {
					indice = cadena1.indexOf(tipoalarma);
					// para que no estalle la coleccion
					if (cadena2.size() >= indice) {
						// en los campos de abajo se encuentran los valores, y el indice que usamos es el indice de
						// cadena1 donde estan los tipos de alarmas.
						// comparamos valor de la base de datos con los que trae cadena2 de las segundas filas del archivo
						valorCad2 = (String) cadena2.get(indice);
						if (valor.equalsIgnoreCase(valorCad2)) {
							TagNameTO tagname = new TagNameTO();
							tagNameBD = (String) cadena2.get(0);
							tagname.setTagName(tagNameBD.replace("\"", "").trim());

							TagNameDAO oTagNameDAO = new TagNameDAO();
							ArrayList lista1 = oTagNameDAO.listar();
							
							Iterator iter1 = lista1.iterator();
							// verificamos que no c repita
							Hashtable hashUnico = new Hashtable();
							TagNameTO tagnamebd = null;
							while (iter1.hasNext()) {
								tagnamebd = (TagNameTO) iter1.next();
								hashUnico.put(tagnamebd.getTagName(), tagnamebd.getTagName());
							}
							// condicionamos que verificamos que no c repita
							if (!hashUnico.containsKey(tagname.getTagName())) {
								int num = HandlerStruct.proximo("idtagname2", "idtagname2", "idtagname2");
								tagname.setActive(String.valueOf(Constants.permission));
								tagname.setIdTipo(idTipoTagConf);
								tagname.setIdTagName2(String.valueOf(num));
								oTagNameDAO.insertar(tagname);
							}

						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Collection getEsqueletoEn_Planillasacop(long idplanillasacop1esqueleto) {
		Collection result = null;
		
		PlanillaSacop1EsqueletoDAO oPlanillaSacop1EsqueletoDAO = new PlanillaSacop1EsqueletoDAO();
		
		try {
			
			ArrayList lista = oPlanillaSacop1EsqueletoDAO.listar(Constants.permissionSt, LoadSacop.edoCerrado, String.valueOf(idplanillasacop1esqueleto));
			
			Iterator iter = lista.iterator();
			while (iter.hasNext()) {
				Plantilla1BD plantilla1BD = new Plantilla1BD((PlanillaSacop1EsqueletoTO) iter.next());
				result.add(plantilla1BD);
				// result=true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean getExisteIdEsqueletoEn_Planillasacop1(long idplanillasacop1esqueleto) {
		boolean result = false;
		PlanillaSacop1EsqueletoDAO oPlanillaSacop1EsqueletoDAO = new PlanillaSacop1EsqueletoDAO();
		
		try {
			
			String[] estados = new String[]{LoadSacop.edoEmitida,LoadSacop.edoAprobado,LoadSacop.edoEnEjecucion,LoadSacop.edoPendienteVerifSeg,LoadSacop.edoVerificacion};
			
			ArrayList lista = oPlanillaSacop1EsqueletoDAO.listar(Constants.permissionSt,estados, String.valueOf(idplanillasacop1esqueleto));
			
			Iterator iter = lista.iterator();
			if (iter.hasNext()) {
				// si es verdadero, no me genera ninguna planilla
				result = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Collection getSacop_Intouch_Conftagname() {
		Vector result = new Vector();
		ConfTagNameDAO oConfTagNameDAO = new ConfTagNameDAO();
		
		try {
			
			ArrayList lista = oConfTagNameDAO.listar(Constants.permissionSt,null, true);
			
			for (Iterator iter = lista.iterator(); iter.hasNext();) {
				ConfTagNameTO sacop_Intouch_Conftagname = (ConfTagNameTO) iter.next();
				if (sacop_Intouch_Conftagname != null) {
					Search bean = new Search(String.valueOf(sacop_Intouch_Conftagname.getIdTagName()), sacop_Intouch_Conftagname.getTipoTag());
					bean.setAditionalInfo(sacop_Intouch_Conftagname.getTipoAlarma());
					bean.setContact(sacop_Intouch_Conftagname.getValor());
					result.add(bean);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Collection getTagnameWonderwareQueStanPreconfiguradosInSacopIntouch() {
		ArrayList objetosTagname = new ArrayList();
		JDBCUtil jDBCUtil = new JDBCUtil();

		try {
			// obtenemos todos los tags preconfiurados en la sacop
			HandlerProcesosWonderWare handlerProcesosWonderWare = new HandlerProcesosWonderWare();
			String noSeUsa = "";
			boolean soloLosNoActivadosEnSacop = true;
			Collection resultado = handlerProcesosWonderWare.HiloChecSacopIntouchWonderwareHijo(noSeUsa, noSeUsa, soloLosNoActivadosEnSacop);
			Iterator it = resultado.iterator();
			StringBuffer inCondicion = new StringBuffer();

			TagNameDAO oTagNameDAO = new TagNameDAO();

			while (it.hasNext()) {
				
				Sacop_Intouchh_frm sacop_Intouchh_frm = (Sacop_Intouchh_frm) it.next();

				// verificamos que en realidad este activo ese tagnames...
				ArrayList list = oTagNameDAO.listarByTagName(Constants.permissionSt,sacop_Intouchh_frm.getTagname().trim());
						
				Iterator iter1 = list.iterator();
				if (iter1.hasNext()) {
					inCondicion.append("'").append(sacop_Intouchh_frm.getTagname()).append("'");
					if (it.hasNext()) {
						inCondicion.append(",");
					}
				}
			}

			// agarramos todos los tags configurados en la sacop
			StringBuffer queryAlarmIntouch = new StringBuffer("select tagname from v_AlarmEventHistory ");
			queryAlarmIntouch.append(" where tagname in ( ").append(inCondicion.toString()).append(" ) ");
			queryAlarmIntouch.append(" and eventstamp > ?");
			java.util.Calendar ca = java.util.Calendar.getInstance();
			ca.add(java.util.Calendar.MINUTE, -1);

			java.sql.Timestamp mydateTimeStamp = new java.sql.Timestamp(ca.getTimeInMillis());

			Connection con = jDBCUtil.connectwonderware();
			PreparedStatement pst = con.prepareStatement(JDBCUtil.replaceCastMysql(queryAlarmIntouch.toString()));
			pst.setTimestamp(1, mydateTimeStamp);

			ResultSet rs = pst.executeQuery();
			Search srch = null;

			while (rs.next()) {
				String tagname = rs.getString("tagname");
				srch = new Search(tagname, tagname);

				objetosTagname.add(srch);

			}
			if (rs != null) {
				rs.close();
			}
			if (pst != null) {
				pst.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
			// System.out.println(e);
		}
		return objetosTagname;
	}

	public Plantilla1BD getAllPlanillasSacop1(String id, boolean noSeUsa) {
		PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
		
		Plantilla1BD result = null;
		try {
			Plantilla1BD plantilla1BD = null;
			
			ArrayList lista = oPlanillaSacop1DAO.listarById(id);
			
			int i = 0;
			Iterator iter = lista.iterator();
			if (iter.hasNext()) {
				plantilla1BD = new Plantilla1BD((PlanillaSacop1TO) iter.next());
				result = plantilla1BD;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList getAllPlanillasSacop1(String idEsqueleto) {
		PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
		
		ArrayList result = new ArrayList();
		try {
			Plantilla1BD plantilla1BD = null;
			
			ArrayList lista = oPlanillaSacop1DAO.listarByIdEsqueleto(Constants.permissionSt,idEsqueleto);
			
			int i = 0;
			for (Iterator iter = lista.iterator(); iter.hasNext();) {
				plantilla1BD = new Plantilla1BD((PlanillaSacop1TO) iter.next());
				result.add(plantilla1BD);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public void eliminamosSaco_Intouch(long idplanillasacop1esqueleto, String continuar_Sacop_Intouch, String deshabilitar_Sacop) {
		try {
			SacopIntouchPadreDAO oSacopIntouchPadreDAO = new SacopIntouchPadreDAO();
			SacopIntouchPadreTO oSacopIntouchPadreTO = new SacopIntouchPadreTO();
			
			oSacopIntouchPadreTO.setIdPlanillaSacop1(String.valueOf(idplanillasacop1esqueleto));
			boolean isCargado = oSacopIntouchPadreDAO.cargarByIdplanillasacop1(oSacopIntouchPadreTO);
			
			byte habilitar = Constants.permission;
			boolean swActivado = true;
			if (isCargado) {
				// habilitar o deshabilitar sacop
				habilitar = Constants.permission;
				if ("1".equalsIgnoreCase(deshabilitar_Sacop)) {
					habilitar = Constants.notPermission;
				}
				oSacopIntouchPadreTO.setEnable(Byte.toString(habilitar));

				habilitar = Constants.permission;
				if ("1".equalsIgnoreCase(continuar_Sacop_Intouch)) {
					habilitar = Constants.notPermission;
				}
				oSacopIntouchPadreTO.setActive(Byte.toString(habilitar));
				
				oSacopIntouchPadreDAO.actualizar(oSacopIntouchPadreTO);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
