/**
 * 
 */
package com.gestionEnvio.custon.dostorres.persistent.managers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.document.actions.TituloDetalle;
import com.desige.webDocuments.document.actions.TotalTablaDetalle;
import com.desige.webDocuments.document.forms.BeanFirmsDoc;
import com.desige.webDocuments.files.forms.DocumentForm;
import com.desige.webDocuments.mail.actions.SendMailTread;
import com.desige.webDocuments.mail.forms.MailForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.sacop.forms.InformarPorMail;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;
import com.gestionEnvio.custon.dostorres.bean.PdfReciboDetalleRequest;
import com.gestionEnvio.custon.dostorres.actions.EnviarWhastAppTread;
import com.gestionEnvio.custon.dostorres.bean.PdfReciboDetalleGastos;
import com.gestionEnvio.custon.dostorres.bean.TituloDetalleRecibo;
import com.gestionEnvio.custon.dostorres.forms.BaseReciboForm;
import com.gestionEnvio.custon.dostorres.forms.WhastAppForm;
import com.gestionEnvio.custon.dostorres.to.ContactoTO;
import com.gestionEnvio.custon.dostorres.util.NumeroLetras;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.log.SysoCounter;
import com.itextpdf.text.pdf.PdfPCell;

/**
 * @author DARWIN PC
 *
 */
public class HandlerDBRecibos extends HandlerBD {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Logger log = LoggerFactory.getLogger("[V10 GESTIONRECIBO] " + HandlerBD.class.getName());
	private static final String ACCOUNT_SID = "ACb05770c37a54e8f68031d84ab14cd256";
	private static final String AUTH_TOKEN = "723281251802b36c7df8822380487451";
	private static final String comilla ="\"" ;
	private static final String salto ="\\n" ;
	private static final String saltoM ="<br>" ;
	private static final String lista ="-";
	private static final String listan="- ";
	private static final String textu="> ";
	
	//String salto =" ****** ____ "+"hola";
	// private static final String NUMEROTWILIO="";

	/*
	 * darwinuzcategui despues la muevo a un archivo
	 * 
	 * 
	 */
	public synchronized static Collection<Search> getAllNombresConRecibos() throws Exception {
		return getAllNombresConRecibos((Connection) null);
	}

	public synchronized static Collection<Search> getAllNombresConRecibos(Connection con) throws Exception {
		ArrayList<Search> resp = new ArrayList<Search>();
		StringBuilder query = new StringBuilder(60);
		StringBuffer cad = new StringBuffer();

		query.append("SELECT  DISTINCT CODCLI, NOMCLI AS NOMBRE ");
		cad.append(" GROUP by CODCLI,NOMCLI ");

		query.append("FROM CONDT.dbo.ENCDOCVENTA ");
		// query.append(" WHERE accountActive = '1' ");
		query.append(" WHERE TIPODOC = '").append(Constants.TIPO_DOC).append("' ");
		query.append(cad);
		query.append(" ORDER BY CODCLI ");

		// query.append(" and c.idarea=a.idarea");

		Vector<?> datos = JDBCUtil.doQueryVector(con, query.toString(),
				Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			Search bean = new Search(properties.getProperty("CODCLI"), properties.getProperty("NOMBRE"));
			// bean.setAditionalInfo(properties.getProperty("cargo"));
			resp.add(bean);
		}
		return resp;
	}

	// DARWINUZCATEGUI TODOS LOS LOCALES EN RECIBOS

	public synchronized static Collection<Search> getAllLocalesConRecibos() throws Exception {
		return getAllLocalesConRecibos((Connection) null);
	}

	public synchronized static Collection<Search> getAllLocalesConRecibos(Connection con) throws Exception {
		ArrayList<Search> resp = new ArrayList<Search>();
		StringBuilder query = new StringBuilder(60);
		StringBuffer cad = new StringBuffer();

		query.append("SELECT  DISTINCT NUMREPORTEZ AS numlocal,NUMREPORTEZ  ");
		cad.append(" GROUP by NUMREPORTEZ ");

		query.append("FROM CONDT.dbo.ENCDOCVENTA ");
		// query.append(" WHERE accountActive = '1' ");
		query.append(" WHERE TIPODOC = '").append(Constants.TIPO_DOC).append("' ");
		query.append(cad);
		query.append(" ORDER BY NUMREPORTEZ ");

		// query.append(" and c.idarea=a.idarea");

		Vector<?> datos = JDBCUtil.doQueryVector(con, query.toString(),
				Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			Search bean = new Search(properties.getProperty("NUMREPORTEZ"), properties.getProperty("NUMREPORTEZ"));
			// Search bean = new Search( properties.getProperty("NUMREPORTEZ") );
			// bean.setAditionalInfo(properties.getProperty("cargo"));
			resp.add(bean);
		}
		return resp;
	}

	public static Collection getRecibosCondominio(String aviso, String codcli, String local, String nombre,
			String unNombre, String unLocal, String emisionFrom, String emisionTo, String periodo, String orderBy,
			BaseReciboForm rec, int sw) {
		Connection conn = null;
		PreparedStatement stquery = null;
		ResultSet rs = null;

		Vector result = new Vector();
	
		try {

			conn = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());

			// query completo

			String query = getQueryRecibos(conn, aviso, codcli, local, nombre, unNombre, unLocal, emisionFrom,
					emisionTo, periodo, orderBy, sw);
			log.debug("[getRecibosCondominio] = " + query+ "cual es Sw--"+sw);
			System.out.println("[getRecibosCondominio] = cual es sw"+sw + query);

			// stquery = conn.prepareStatement(JDBCUtil.replaceCastMysql(query.toString()));
			stquery = conn.prepareStatement(query.toString());
			rs = stquery.executeQuery();

			while (rs.next()) {

				BaseReciboForm forma = new BaseReciboForm();

				forma.setNumero(rs.getString("numdoc") != null ? rs.getString("numdoc").trim() : "");
				forma.setNomcli(rs.getString("nombre") != null ? rs.getString("nombre").trim() : "");
				forma.setCodcli(rs.getString("codcli") != null ? rs.getString("codcli").trim() : "");
				forma.setNumlocal(rs.getString("numlocal") != null ? rs.getString("numlocal").trim() : "");
				forma.setAnio(rs.getString("ANIO") != null ? rs.getString("ANIO").trim() : "");
				forma.setMes(rs.getString("MES") != null ? rs.getString("MES").trim() : "");
				forma.setTotal(rs.getFloat("montoBS"));
				forma.setTotal_mm(rs.getFloat("montoD"));
				forma.setTasa(rs.getFloat("tasa"));
				forma.setAlicuota(rs.getFloat("alicuota"));
				forma.setDateEmitida(rs.getString("emision") != null ? rs.getString("emision") : "");
				forma.setDateVence(rs.getString("vencimiento") != null ? rs.getString("vencimiento") : "");

				if (rec != null) {
					// if (doc != null && doc.getListaCampos() != null) {
					// System.out.println(rec);
					BaseReciboForm obj = null;
					System.out.println(obj);
					for (int i = 0; i < rec.getListaCampos().size(); i++) {
						// System.out.println(i);
						obj = (BaseReciboForm) rec.getListaCampos().get(i);
					}
				}

				// forma.setDateApproved(ToolsHTML.formatDateShow(!ToolsHTML.isEmptyOrNull(dateApprove)
				// ? dateApprove : "", false));
				// forma.setDateExpires(ToolsHTML.formatDateShow(!ToolsHTML.isEmptyOrNull(dateExpires)
				// ? dateExpires : "", false));

				result.add(forma);
			}
			rs.close();
			conn.close();
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	// query
	public static String getQueryRecibos(Connection conn, String aviso, String codcli, String local, String nombre,
			String unNombre, String unLocal, String emisionFrom, String emisionTo, String periodo, String orderBy,int sw) {
		// StringBuilder sql = new StringBuilder(2048);
		StringBuilder query = new StringBuilder(2048);

		if (!ToolsHTML.isEmptyOrNull(unNombre) && unNombre.length() > 2) {
			codcli = "";
			codcli = unNombre.trim();
		}
		if (!ToolsHTML.isEmptyOrNull(unLocal) && unLocal.length() > 2) {
			// if (!ToolsHTML.isEmptyOrNull(unLocal)) {
			local = "";
			local = unLocal.trim();
		}

		query.append("SELECT  DISTINCT CODCLI as codcli,");
		query.append("NUMREPORTEZ AS numlocal, ");
		query.append("NOMCLI AS nombre, ");
		query.append("EMITIDA AS emision, ");
		query.append("VENCE AS vencimiento, ");
		query.append("NUMERO AS numdoc,MES,ANIO,");
		query.append("max(TOTAL) as montoBS, max(TOTAL_MM) as montoD,");
		query.append("TASA, ALICUOTA ");
		query.append(" FROM  CONDT.dbo.ENCDOCVENTA ");
		// query.append("WHERE CODCLI LIKE '%%%J%%' and TIPODOC LIKE '%%FP%%' ");
		query.append("WHERE TIPODOC LIKE '%%FP%%' ");
		if (!ToolsHTML.isEmptyOrNull(codcli)) {
			// System.out.println("------------------------------codcli-------------------------");
			// System.out.println(codcli);
			// System.out.println("------------------------------codcli-------------------------");
			query.append("and CODCLI LIKE '%%%").append(codcli).append("%%%' ");
		}
		if (!ToolsHTML.isEmptyOrNull(aviso)) {
			query.append("and NUMERO LIKE '%%%").append(aviso).append("%%%' ");
		}
		if (!ToolsHTML.isEmptyOrNull(local)) {
			// System.out.println("------------------------------local-------------------------");
			// System.out.println(local);
			// System.out.println("------------------------------local-------------------------");

			query.append("and NUMREPORTEZ LIKE '%%%").append(local).append("%%%' ");
		}
		
		if (sw>1) {
		
			query.append("and STATUS LIKE '%%%").append("AVISO").append("%%%' ");
		}
		
		
		if (!ToolsHTML.isEmptyOrNull(nombre)) {
			query.append("and NOMCLI LIKE '%%%").append(nombre).append("%%%' ");
		}
		// inicio aqui
		if (!ToolsHTML.isEmptyOrNull(emisionFrom) && !ToolsHTML.isEmptyOrNull(emisionTo) && emisionFrom.length() > 9
				&& emisionTo.length() > 9) {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				query.append(" AND (EMITIDA BETWEEN CONVERT(datetime, '" + emisionFrom + " 0:00:00 AM', 120) ");
				query.append(" AND CONVERT(datetime, '" + emisionTo + " 11:59:59 PM', 120) ) ");

				break;
			case Constants.MANEJADOR_POSTGRES:
				query.append(" AND (EMITIDA BETWEEN CAST('").append(emisionFrom).append("' as timestamp)  ");
				query.append(" AND CAST('").append(emisionTo).append("' as timestamp) ) ");

				break;
			case Constants.MANEJADOR_MYSQL:
				query.append(" AND (EMITIDA BETWEEN TIMESTAMP('").append(emisionFrom).append("')  ");
				query.append(" AND TIMESTAMP('").append(emisionTo).append("') ) ");

				break;
			}
		}

		if (!ToolsHTML.isEmptyOrNull(emisionFrom) && emisionFrom.length() > 9) {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				query.append(" AND (EMITIDA >= CONVERT(datetime, '" + emisionFrom + " 0:00:00 AM', 120) ) ");

				break;
			case Constants.MANEJADOR_POSTGRES:
				query.append(" AND (EMITIDA >= CAST('").append(emisionFrom).append("' as timestamp) ) ");

				break;
			case Constants.MANEJADOR_MYSQL:
				query.append(" AND (EMITIDA >= TIMESTAMP('").append(emisionFrom).append("') ) ");

				break;
			}
		}

		if (!ToolsHTML.isEmptyOrNull(emisionTo) && emisionTo.length() > 9) {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				query.append(" AND (EMITIDA <= CONVERT(datetime, '" + emisionTo + " 11:59:59 PM', 120) ) ");

				break;
			case Constants.MANEJADOR_POSTGRES:
				query.append(" AND (EMITIDA <= CAST('").append(emisionTo).append("' as timestamp) ) ");

				break;
			case Constants.MANEJADOR_MYSQL:
				query.append(" AND (EMITIDA <= TIMESTAMP('").append(emisionTo).append("') ) ");

				break;
			}
		}

		//// getUltimoPerido
		if (ToolsHTML.isEmptyOrNull(emisionTo)) {
			if (ToolsHTML.isEmptyOrNull(periodo)) {
				String aniomes = getUltimoPerido();
				String mm = "01";
				String yyyy = "2023";

				if (aniomes.length() > 5) {
					yyyy = aniomes.substring(0, 4);
					mm = aniomes.substring(4, 6);
					// System.out.println("--------------------yyyy------------------");
					// System.out.println(yyyy);
					// System.out.println(mm);
				}

				query.append("and MES = '").append(mm).append("' ");
				query.append("and ANIO = '").append(yyyy).append("' ");
			}

			if (!ToolsHTML.isEmptyOrNull(periodo)) {
				String mm = "01";
				String yyyy = "2023";

				if (periodo.length() > 6) {
					mm = periodo.substring(0, 2);
					yyyy = periodo.substring(3, 7);
				}

				query.append("and MES = '").append(mm).append("' ");
				query.append("and ANIO = '").append(yyyy).append("' ");
			}
		}

		// fin aqui
		query.append(
				"GROUP by  CODCLI, NUMREPORTEZ,NOMCLI ,NUMERO, MES,EMITIDA ,VENCE,ANIO,TOTAL,TOTAL_MM,TASA,ALICUOTA ");
		/*
		 * if (!ToolsHTML.isEmptyOrNull(orderBy)) {
		 * query.append(" ORDER BY ").append(orderBy); System.out.
		 * println("--xxxxxxxxxxxxxxxxxx**********-entro if*************************");
		 * System.out.println(orderBy); System.out.
		 * println("--xxxxxxxxxxxxxxxxxx**********-entro if*************************");
		 * 
		 * // sql.append(" asc"); }
		 */
		query.append("ORDER by numero ");

		// System.out.println("---------------ccccc------consulta
		// *******************************");
		// System.out.println(query.toString());
		// System.out.println("------------------consulta
		// *******************************");
		// query.append("(p.apellidos + ' ' + p.nombres) AS Nombre,");

		return query.toString();
	}

	public String getOrdenRecibos(String ordenVersion, String ordenNombre, String ordenTypeDocument, String ordenNumber,
			String ordenPropietario, String ordenApproved) {
		StringBuilder orderBy = new StringBuilder(100);
		boolean sworderBye = false;
		if (!ToolsHTML.isEmptyOrNull(ordenVersion)) {
			if (sworderBye) {
				orderBy.append(",");
			}
			orderBy.append("  vd.MayorVer,vd.MinorVer");
			sworderBye = true;
		}
		if (!ToolsHTML.isEmptyOrNull(ordenNombre)) {
			if (sworderBye) {
				orderBy.append(",");
			}
			orderBy.append("  lower(NOMCLI)");
			sworderBye = true;
		}
		if (!ToolsHTML.isEmptyOrNull(ordenTypeDocument)) {
			if (sworderBye) {
				orderBy.append(",");
			}
			orderBy.append("  lower(td.typeDoc)");
			sworderBye = true;
		}
		if (!ToolsHTML.isEmptyOrNull(ordenNumber)) {
			if (sworderBye) {
				orderBy.append(",");
			}
			orderBy.append("  lower(d.prefix),d.number ");
			sworderBye = true;
		}
		if (!ToolsHTML.isEmptyOrNull(ordenPropietario)) {
			if (sworderBye) {
				orderBy.append(",");
			}
			orderBy.append(" lower(p.apellidos), lower(p.nombres) ");
			// orderBy.append(" nombre");
			sworderBye = true;
		}
		if (!ToolsHTML.isEmptyOrNull(ordenApproved)) {
			if (sworderBye) {
				orderBy.append(",");
			}
			orderBy.append(" vd.dateApproved");
			sworderBye = true;
		}
		return orderBy.toString();
	}
	
	public static void mandarMailsDarwin(String avisoPRM, String codcliPRM, String localPRM, String nombrePRM,
			String unNombrePRM, String unLocalPRM, String emisionFromPRM, String emisionToPRM, String periodoPRM,int sw) {
		String salida = "";
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String codcli = "V1715";
		String mm = "01";
		String yyyy = "2023";

		//// getUltimoPerido
		// unNombrePRM

		if (!ToolsHTML.isEmptyOrNull(unNombrePRM) && unNombrePRM.length() > 2) {
			codcliPRM = "";
			codcliPRM = unNombrePRM.trim();
		}
		if (!ToolsHTML.isEmptyOrNull(unLocalPRM) && unLocalPRM.length() > 2) {
			// if (!ToolsHTML.isEmptyOrNull(unLocal)) {
			localPRM = "";
			localPRM = unLocalPRM.trim();
		}

		try {
			Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"),
					DesigeConf.getProperty("country.Default"));
			ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);

			// query de envio de recibos
			StringBuilder sql = new StringBuilder(
					" SELECT  DISTINCT CODCLI, NOMCLI AS NOMBRE,NUMERO,EMITIDA,VENCE,MES,ANIO,NUMREPORTEZ,CEDRIF,ALICUOTA,TASA  ")
					.append("FROM CONDT.dbo.ENCDOCVENTA ").append(" WHERE TIPODOC = '").append(Constants.TIPO_DOC)
					.append("'");

			if (!ToolsHTML.isEmptyOrNull(codcliPRM)) {
				// System.out.println("------------------------------codcli-------------------------");
				// System.out.println(codcli);
				// System.out.println("------------------------------codcli-------------------------");
				 sql.append(" and CODCLI LIKE '%%%").append(codcliPRM).append("%%%' ");
				//sql.append(" and CODCLI LIKE '%%%").append(codcli).append("%%%' ");
			}

			// .append("' ").append("and CODCLI LIKE '%%%").append(codcliPRM).append("%%%'
			// ");
			// .append(" and mes LIKE '%%%").append(mesPRM).append("%%%' AND ANIO LIKE
			// '%%%").append(anioPRM).append("%%%' ");

			if (!ToolsHTML.isEmptyOrNull(avisoPRM)) {
				sql.append(" and NUMERO LIKE '%%%").append(avisoPRM).append("%%%' ");
			}

			if (!ToolsHTML.isEmptyOrNull(localPRM)) {
				// System.out.println("------------------------------local-------------------------");
				// System.out.println(localPRM);
				// System.out.println("------------------------------local-------------------------");

				sql.append("and NUMREPORTEZ LIKE '%%%").append(localPRM).append("%%%' ");
			}
			
			if (sw>1) {
				
				sql.append("and STATUS LIKE '%%%").append("AVISO").append("%%%' ");
			}
			
			
			if (!ToolsHTML.isEmptyOrNull(nombrePRM)) {
				sql.append("and NOMCLI LIKE '%%%").append(nombrePRM).append("%%%' ");
			}

			// inicio aqui
			if (!ToolsHTML.isEmptyOrNull(emisionFromPRM) && !ToolsHTML.isEmptyOrNull(emisionToPRM)
					&& emisionFromPRM.length() > 9 && emisionToPRM.length() > 9) {
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					sql.append(" AND (EMITIDA BETWEEN CONVERT(datetime, '" + emisionFromPRM + " 0:00:00 AM', 120) ");
					sql.append(" AND CONVERT(datetime, '" + emisionToPRM + " 11:59:59 PM', 120) ) ");

					break;
				case Constants.MANEJADOR_POSTGRES:
					sql.append(" AND (EMITIDA BETWEEN CAST('");
					sql.append(emisionFromPRM).append("' as timestamp)  ");
					sql.append(" AND CAST('").append(emisionToPRM).append("' as timestamp) ) ");

					break;
				case Constants.MANEJADOR_MYSQL:
					sql.append(" AND (EMITIDA BETWEEN TIMESTAMP('").append(emisionFromPRM).append("')  ");
					sql.append(" AND TIMESTAMP('").append(emisionToPRM).append("') ) ");

					break;
				}
			}

			if (!ToolsHTML.isEmptyOrNull(emisionFromPRM) && emisionFromPRM.length() > 9) {
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					sql.append(" AND (EMITIDA >= CONVERT(datetime, '" + emisionFromPRM + " 0:00:00 AM', 120) ) ");

					break;
				case Constants.MANEJADOR_POSTGRES:
					sql.append(" AND (EMITIDA >= CAST('").append(emisionFromPRM).append("' as timestamp) ) ");

					break;
				case Constants.MANEJADOR_MYSQL:
					sql.append(" AND (EMITIDA >= TIMESTAMP('").append(emisionFromPRM).append("') ) ");

					break;
				}
			}

			if (!ToolsHTML.isEmptyOrNull(emisionToPRM) && emisionToPRM.length() > 9) {
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					sql.append(" AND (EMITIDA <= CONVERT(datetime, '" + emisionToPRM + " 11:59:59 PM', 120) ) ");

					break;
				case Constants.MANEJADOR_POSTGRES:
					sql.append(" AND (EMITIDA <= CAST('").append(emisionToPRM).append("' as timestamp) ) ");

					break;
				case Constants.MANEJADOR_MYSQL:
					sql.append(" AND (EMITIDA <= TIMESTAMP('").append(emisionToPRM).append("') ) ");

					break;
				}
			}

			if (ToolsHTML.isEmptyOrNull(emisionToPRM)) {

				if (ToolsHTML.isEmptyOrNull(periodoPRM)) {
					String aniomes = getUltimoPerido();

					if (aniomes.length() > 5) {
						yyyy = aniomes.substring(0, 4);
						mm = aniomes.substring(4, 6);
						// System.out.println("--------------------aniomesyyyy------------------");
						// System.out.println(yyyy);
						// System.out.println(mm);
					}

					sql.append(" and MES = '").append(mm).append("' ");
					sql.append(" and ANIO = '").append(yyyy).append("' ");
				}

				if (!ToolsHTML.isEmptyOrNull(periodoPRM)) {

					if (periodoPRM.length() > 6) {
						mm = periodoPRM.substring(0, 2);
						yyyy = periodoPRM.substring(3, 7);
					}

					sql.append(" and MES = '").append(mm).append("' ");
					sql.append(" and ANIO = '").append(yyyy).append("' ");
				}

			}
			//// Ultimo

			sql.append(" GROUP by CODCLI,NOMCLI,NUMERO,EMITIDA,VENCE,MES,ANIO,NUMREPORTEZ,CEDRIF,ALICUOTA,TASA ")
					.append(" ORDER BY CODCLI ");
			// //System.out.println("[mandarMailsUsuariosdelSacop]"+sql.toString());

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			pst = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = pst.executeQuery();

			if (rs != null) {
				Hashtable unSoloMailMandar = new Hashtable();
				ArrayList dataUnSoloMailMandar = new ArrayList();

				StringTokenizer stk = null;
				StringTokenizer stk2 = null;
				String contacto = null;
				String contactotxt = null;
				String correocontacto = "";
				StringBuffer SolicitudInforma = new StringBuffer("");
				String correo = null;
				String usuario = null;
				String nombre = null;
				String nombreCli = null;
				String numero = null;
				String emision = null;
				String vencefc = null;
				String periodo = null;
				String mesParm = null;
				String anioParm = null;
				String local = null;
				String alicuota = null;
				String rif = null;
				Double tasa = null;
				String estado = "POR PAGAR";
				String direccion = "handler S/N COLOQUE EN LA BASEDATOS LA DIRECCION";
				String nameFilePdf = null;
				String fondoDeReserva = null;
				String saldoTotalDolaresSTR = null;

				log.info("inicio de crear pdf ");
				String path = ToolsHTML.getPath().concat("recibos").concat(File.separator);
				// String servidor = request.getScheme() + "://" +
				// ToolsHTML.getServerName(request) + ":" + request.getServerPort() +
				// request.getContextPath() + "/";

				// cargamos los usuario
				Map<String, ContactoTO> listContactos = HandlerUtilDosTorres.listarContactoAlls();
				ContactoTO contactoTO = null;
				Collection detalleRecibo = null;
				PdfPortadaRecibo pdf = new PdfPortadaRecibo();
				while (rs.next()) {
					stk = new StringTokenizer(rs.getString("CODCLI"), ",");
					// stk2 = new StringTokenizer(rs.getString("responsables"), ",");
					contacto = rs.getString("CODCLI") != null ? rs.getString("CODCLI") : "";
					numero = rs.getString("NUMERO") != null ? rs.getString("NUMERO").trim() : "";
					/// forma.setDateApproved(ToolsHTML.formatDateShow(!ToolsHTML.isEmptyOrNull(dateApprove)
					/// ? dateApprove : "", false));
					emision = rs.getString("EMITIDA") != null
							? ToolsHTML.formatDateShow(rs.getString("EMITIDA").toString(), false)
							: "";
					vencefc = rs.getString("VENCE") != null
							? ToolsHTML.formatDateShow(rs.getString("VENCE").toString(), false)
							: "";
					periodo = rs.getString("MES") != null ? rs.getString("MES").concat("/").concat(rs.getString("ANIO"))
							: "";
					mesParm = rs.getString("MES") != null ? rs.getString("MES") : "";
					anioParm = rs.getString("ANIO") != null ? rs.getString("ANIO") : "";
					local = rs.getString("NUMREPORTEZ") != null ? rs.getString("NUMREPORTEZ") : "";
					nombreCli = rs.getString("NOMBRE") != null ? rs.getString("NOMBRE") : "";
					rif = rs.getString("CEDRIF") != null ? rs.getString("CEDRIF") : "";
					alicuota = rs.getString("ALICUOTA") != null ? rs.getString("ALICUOTA").toString() : "";
					tasa = rs.getDouble("TASA");
					String tasaSTR = String.format("%1$,.2f", tasa);

					// contactoTO = listContactos.get(String.valueOf(contacto));
					// System.out.println("***********xxxxxxxxxxxxxxxxxxxxxxxxxx-------*****CONTATOto*************************");
					// System.out.println(listContactos.get(String.valueOf(contacto)));
					// System.out.println("***********xxxxxxxxxxxxxxxxxxxxxxxxxx-------*****CONTATOto*************************");
					contactoTO = listContactos.get(String.valueOf(contacto));
					// Hashtable unSoloMailMandar = new Hashtable();
					if (!listContactos.containsKey(contacto)) {
						// System.out.println("***********-------*****CONTATOto*************************");
						// System.out.println(contactoTO);
						// System.out.println(listContactos);
						System.out.println("***********-------*****CONTATOto*************************");
					}
					if (contactoTO != null) {
						// nombre = contactoTO.getNombre() + " " + contactoTO.getApellidos();
						nombre = contactoTO.getNombre();
						correo = contactoTO.getEmail();
						direccion = contactoTO.getDireccion();
						pdf.setLblDireccion("DIRECCIÓN");
						pdf.setDatDireccion(direccion);
						pdf.setDatAviso(numero);
						pdf.setDatNombre(nombre);

						// System.out.println("****************if*************************");
						// System.out.println(correo);
						// System.out.println(direccion);
						// System.out.println("************************************");
						InformarPorMail Informarpormail = new InformarPorMail();
						Informarpormail.setNombre(nombre);
						Informarpormail.setCorreo(correo);
						Informarpormail.setId("0");// contacto
						correocontacto = correo;
						// unSoloMailMandar.put(contacto, Informarpormail);
					}

					// dirrecion = rs.getString("NUMERO") != null ? rs.getString("NUMERO") : "";
					// aqui genero el pdf

					// !ToolsHTML.isEmptyOrNull(dateApprove)

					if (!ToolsHTML.isEmptyOrNull(numero)) {
						nameFilePdf = path.concat("AvisoNo-").concat(String.valueOf(numero)).concat(".pdf");
						log.info("inicio de nombre " + nameFilePdf + " path " + path);

						// cambio de datos a las etiquetas
						pdf.setLblCodigo(rb.getString("doc.number"));
						pdf.setLblAviso("Aviso");
						pdf.setLblNombre("CLIENTE");
						pdf.setLblDireccion("DIRECCIÓN");
						pdf.setLblEmision("EMISIÓN");
						pdf.setLblFechaDeVencimiento("VENCIMIENTO");
						pdf.setLblPeriodoRecibo("PERIODO");
						pdf.setLblLocal("LOCAL");
						pdf.setLblRIF("R.I.F");
						pdf.setLblAlicuota("ALICUOTA");
						// datos DATA
						pdf.setDatCodigo(contacto);
						pdf.setDatAviso(numero);
						pdf.setDatNombre(nombreCli);
						// pdf.setDatDireccion(direccion);

						pdf.setDatEmision(emision);
						pdf.setDatFechaDeVencimiento(vencefc);
						pdf.setDatPeriodoRecibo(periodo);
						pdf.setDatLocal(local);
						pdf.setDatRIF(rif);
						pdf.setDatAlicuota(alicuota);
						pdf.setDatTasaCambiaria(tasaSTR);
						detalleRecibo = getDetalleRecibo(numero, "PROCESADO");
						// System.out.println("-----------------aquiiiiiiiiiiiiiiiiiiii----------");
						// System.out.println(detalleRecibo.size());

						// pdf.setLblVersion(rb.getString("doc.Ver"));
						// pdf.setLblFechaDeAprobacion(rb.getString("doc.dateApprovedAvr"));
						// pdf.setLblFechaDeExpiracion(rb.getString("doc.dateExpireAvr"));
						// pdf.setLblPropietario(rb.getString("cbs.owner"));
						// pdf.setLblOrigen(rb.getString("cbs.origin"));
						// pdf.setLblSolicitado(rb.getString("imp.solicitante"));
						// pdf.setLblImpreso(rb.getString("imp.datePrint"));
						// pdf.setLblNumeroDeCopias(rb.getString("imp.numCopy"));
						// pdf.setLblSoloPropietario(rb.getString("sch.noSignature"));

						// DETALLE RECIBO
						// System.out.println("aquiiiiiiiiiiiiiiiiiiii----------");
						// System.out.println(detalleRecibo);
						// System.out.println(detalleRecibo.size());
						// System.out.println("aquiiiiiiiiiiiiiiiiiiii----------");
						if ((detalleRecibo != null && detalleRecibo.size() > 0)) {
							// System.out.println("aquiiiiiiiiiiiiiiiiiiii----------");
							pdf.setLblRecibo(new TituloDetalleRecibo("REEMBOLSO DE GASTOS EFECTUADOS POR SU CUENTA",
									"GASTOS COMUNES", "CÓDIGO", "DESCRIPCIÓN", "MONTO", "CUOTA PARTE Bs.", "IVA",
									"CUOTA PARTE $"));
							// pdf.setLblRecibo(new
							// TituloDetalleRecibo("1-CÓDIGO","2-DESCRIPCIÓN","3-MONTO","4-CUOTA PARTE
							// Bs.","5-IVA","6-CUOTA PARTE $"));
							pdf.setDetRecibo(cargarDetalleRecibo13(detalleRecibo));
							// Double valorActualFondo =0.0;
							Iterator ite = detalleRecibo.iterator();
							Double valorActualFondo = 0.0;
							String valorAnterior = "00000059";
							while (ite.hasNext()) {
								PdfReciboDetalleRequest obj = (PdfReciboDetalleRequest) ite.next();
								valorActualFondo = String.valueOf(obj.getRecibocodigo().trim())
										.equalsIgnoreCase("00000059") ? obj.getReciboMonto() : 0.0;
								valorAnterior = String.valueOf(obj.getRecibocodigo().trim());
								if (valorAnterior.equalsIgnoreCase("00000059")) {
									break;
								}

							}

							// fondoDeReserva =detalleRecibo.iterator()
							//
							ArrayList total = cargarTotalRecibo(detalleRecibo);
							Double total8 = 0.00;
							Double saldoTotalDolares = 0.00;
							Object total1 = total.get(0); // (String.valueOf(numero)
							Object total2 = total.get(1);
							Object total3 = total.get(2);
							Object total4 = total.get(3);
							Object total5 = total.get(4);
							Object total6 = total.get(5);
							Object total7 = total.get(6);// Double.parseDouble(reciboMonto)
							String saldoAnterioFondo = getFondoReserva(mesParm, anioParm);
							Double saldoTotalFondo = getFondoReservaDouble(mesParm, anioParm) + valorActualFondo;
							total8 = Double.parseDouble((String) total.get(7));
							saldoTotalDolares = total8 / tasa;
							saldoTotalDolaresSTR = String.format("%1$,.2f", saldoTotalDolares);
							// String letra = String.valueOf(convertirNumeroATexto((double) (total8));
							// String letra1 = String.valueOf(convertirNumeroATexto(234.87));
							// String letra2 = String.valueOf(convertirNumeroATexto(21.99));
							String valorActualFondoSTR = String.format("%1$,.2f", valorActualFondo);
							String saldoTotalFondoSTR = String.format("%1$,.2f", saldoTotalFondo);
							/*
							 * System.out.println("******************************ojbjeto--------------");
							 * System.out.println(total); System.out.println(total7);
							 * System.out.println(total8); System.out.println(total7);
							 * System.out.println(saldoAnterioFondo); System.out.println(valorActualFondo);
							 * System.out.println(saldoTotalFondo); System.out.println(valorActualFondoSTR);
							 * System.out.println("******************************ojbjeto--------------");
							 */
							// pdf.setLblRecibo1(new TituloDetalle("CÓDIGO","DESCRIPCIÓN","MONTO"));
							// ArrayList<E> totales = cargarTotalRecibo(detalleRecibo);
							pdf.setlblTotalRecibo(new TotalTablaDetalle("TOTAL GASTOS COMUNES", total1.toString(),
									total2.toString(), total3.toString(), total4.toString()));
							// pdf.setlblTotalRecibo(new TotalTablaDetalle());
							// pdf.setDetRecibo1(cargarTotalRecibo(totadetalleRecibo));
							pdf.setLblDistGasto(new TituloDetalleRecibo("1", "2", "3", "4", "5", "6"));
							ArrayList total25 = cargarDetalleTotalDistribucion(total);
							Double numeroRecibo = getRecibosPendientesDouble(mesParm, anioParm, contacto, local);
							String totalBolivaresLetras = convertirNumeroATexto(total8);
							/*
							 * System.out.println("********************total25*******************");
							 * System.out.println(total8); System.out.println(numeroRecibo);
							 * System.out.println(totalBolivaresLetras);
							 * System.out.println("********************total25*******************");
							 */
							// pdf.setlblTotalRecibo(new TotalTablaDetalle("TOTAL GASTOS
							// COMUNES",total1.toString(),total2.toString(),total3.toString(),total4.toString()));
							pdf.setDetDistGasto(total25);
							pdf.setlblTotalDistGasto(new TotalTablaDetalle("TOTAL GENERAL", total5.toString(),
									total6.toString(), " ", total3.toString(), total7.toString()));
							pdf.setLblTotalLetrasBolivares(totalBolivaresLetras);
							// cargarDetalleTotalDistribucion
							pdf.setlblFondoReserva(new TituloDetalleRecibo("FONDOS", " ", "SALDO ANTEROR", "APLCACION",
									"APORTE", "SALDO ACTUAL"));
							pdf.setlblTotalFondoReserva(
									new TotalTablaDetalle("FONDO DE RESERVA(E)", saldoAnterioFondo.toString(), "0,00",
											valorActualFondoSTR.toString(), saldoTotalFondoSTR.toString()));

							pdf.setLblEdoCta(new TituloDetalleRecibo("ESTADO DE CUENTA DEL PROPIETARIO:",
									"RECIBOS PENDIENTES", "TOTAL RECIBOS PENDIENTES", "RECIBO ACTUAL", "TOTAL DEUDA Bs",
									"TOTAL DEUDA $"));
							pdf.setlblTotalEdoCta(new TotalTablaDetalle("  ", " " + numeroRecibo.toString(), "0.00",
									total7.toString(), total7.toString(), saldoTotalDolaresSTR.toString()));

						}

						// Datos de la pagina

						pdf.setDatEncabezado1(HandlerParameters.PARAMETROS.getHeadImp1());
						pdf.setDatEncabezado2(HandlerParameters.PARAMETROS.getHeadImp2());
						pdf.setDatEncabezado3(HandlerParameters.PARAMETROS.getHeadImp3());

					} else {
						System.err.println("es nulo o blanco  numero ");
					}

					// mandamos correo al contacto
					boolean swtienequepasar = true;
					if (!unSoloMailMandar.containsKey(contacto)) {
						contactoTO = listContactos.get(String.valueOf(contacto));

						if (contactoTO != null) {
							// nombre = contactoTO.getNombre() + " " + contactoTO.getApellidos();
							nombre = contactoTO.getNombre();
							correo = contactoTO.getEmail();
							direccion = contactoTO.getDireccion();
							pdf.setLblDireccion("DIRECCIÓN");
							pdf.setDatDireccion(direccion);
							pdf.setDatAviso(numero);
							pdf.setDatNombre(nombre);

							/*
							 * System.out.println("****************if*************************");
							 */
							// System.out.println(correo);
							// System.out.println(direccion);
							// System.out.println("************************************");

							InformarPorMail Informarpormail = new InformarPorMail();
							Informarpormail.setNombre(nombre);
							Informarpormail.setCorreo(correo);
							Informarpormail.setId("0");// contacto
							correocontacto = correo;
							unSoloMailMandar.put(contacto, Informarpormail);
						}

					}
					// este else se esta evalundo
					else {
						InformarPorMail Informarpormail = (InformarPorMail) unSoloMailMandar.get(contacto);
						contactotxt = Informarpormail.getNombre();
						// System.out.println("***************el*********************");
						// System.out.println(contactotxt);
						// System.out.println("************************************");

					} // end if
						// ahora a mandar correos
					StringBuffer comentarios = new StringBuffer("");
					/*
					 * comentarios.append(rb.getString("scp.mail1")).append(sw>1 ?" Co-propietario: " : ":").append("*"+nombre.trim()+"*").append(salto).append(salto);
					if (sw> 1) {
						comentarios.append("Desde Condominio Dos Torres le informamos que presenta una deuda a la fecha de ").append(periodo).append(" Avisos de Cobro, que suman la cantidad de USD ").append(saldoTotalDolaresSTR).append(salto).append(salto);
						comentarios.append("el Aviso de cobro No ").append("*"+numero+"* ").append(salto).append("Ha sido Enviado a su email: ").append("*"+correo+"*" ).append(salto);
						comentarios.append("Lo invitamos a emitir el pago correspondiente, ");
						
						
					}
					 */
					comentarios.append(rb.getString("scp.mail1")).append(sw>1 ?" Co-propietario: " : ":").append(nombre).append(saltoM).append(saltoM);
					if (sw> 1) {
						comentarios.append("Desde Condominio Dos Torres le informamos que presenta una deuda a la fecha de ").append(periodo).append(" Avisos de Cobro, que suman la cantidad de USD ").append(saldoTotalDolaresSTR).append(saltoM).append(saltoM);
						comentarios.append("Lo invitamos a emitir el pago correspondiente, ");
						
						
					} else {
						comentarios.append(rb.getString("scp.mail2")).append(" ").append(periodo).append("").append(saltoM).append(saltoM);
						
						
					}
					comentarios.append(rb.getString("scp.mail3")).append(" ").append(saltoM);
					comentarios.append(rb.getString("scp.mail4")).append(" ").append(saltoM);
					comentarios.append(rb.getString("scp.mail5")).append(" ").append(saltoM);
					comentarios.append(rb.getString("scp.mail6")).append(" ").append(saltoM);
					comentarios.append(rb.getString("scp.mail7")).append(" ").append(saltoM);
					comentarios.append(rb.getString("scp.mail8")).append(" ").append(saltoM);
					comentarios.append(rb.getString("scp.mail9")).append(" ").append(saltoM);
					comentarios.append(rb.getString("scp.mail10")).append(" ").append(saltoM);
					comentarios.append(rb.getString("scp.mail11")).append(" ").append(saltoM).append(saltoM);
					comentarios.append(rb.getString("scp.mail12")).append(" ").append(saltoM);
					comentarios.append(rb.getString("scp.mail13")).append(" ").append(saltoM);
					comentarios.append("Usuario: ").append(contacto).append(" Clave:123456").append(saltoM);
					
					MailForm formaMail = new MailForm();
					String pathpdf = ToolsHTML.getPath();
					// String cpath = ToolsHTML.getPath();
					// String fileName =
					// path.concat("recibos").concat(File.separator)+"06Record.xls";
					String fileName = pathpdf.concat("recibos").concat(File.separator).concat("AvisoNo-")
							.concat(numero + ".pdf");
					// System.out.println("***************datos de archivos*********************");
					// System.out.println(pathpdf);
					// System.out.println(fileName);
					// System.out.println("************************************");

					formaMail.setFileName(fileName);
					formaMail.setFrom(HandlerParameters.PARAMETROS.getMailAccount());
					formaMail.setNameFrom(rb.getString("mail.system"));
					formaMail.setTo(correo);
					formaMail.setSubject(numero);
					formaMail.setMensaje(comentarios + " ");
					/*
					 * enviarEmailContactos(String titleMail, String user, String account, String
					 * email, String comments)
					 */
					// HandlerUtilDosTorres.enviarEmailContactos("titulo "+formaMail.getSubject(),
					// formaMail.getNameFrom(), formaMail.getFrom(), formaMail.getTo(),
					// formaMail.getMensaje());

					// incio try de enviar
					try {

						log.info("Iniciando creacion de pdfs: " + nameFilePdf);
						pdf.createPdf(nameFilePdf);
						log.info("Creada pdfs: para Enviar Al E-mail " + nameFilePdf);
						if (formaMail != null) {
							 SendMailTread mail = new SendMailTread(formaMail);
							 mail.start();
						}
					} catch (Exception ex) {
						log.debug("Exception");
						log.error(ex.getMessage());
						ex.printStackTrace();
					}
					// fin

				} // end while externo
				rs.close();

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setFinally(con, pst);
		}
	}


	//mandar whatsapp

	public static void mandarWhatsAppDarwin(String avisoPRM, String codcliPRM, String localPRM, String nombrePRM,
			String unNombrePRM, String unLocalPRM, String emisionFromPRM, String emisionToPRM, String periodoPRM, int sw) {
		String salida = "";
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String codcli = "V1715";
		String mm = "01";
		String yyyy = "2023";

		//// getUltimoPerido
		// unNombrePRM

		if (!ToolsHTML.isEmptyOrNull(unNombrePRM) && unNombrePRM.length() > 2) {
			codcliPRM = "";
			codcliPRM = unNombrePRM.trim();
		}
		if (!ToolsHTML.isEmptyOrNull(unLocalPRM) && unLocalPRM.length() > 2) {
			// if (!ToolsHTML.isEmptyOrNull(unLocal)) {
			localPRM = "";
			localPRM = unLocalPRM.trim();
		}

		try {
			Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"),
					DesigeConf.getProperty("country.Default"));
			ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);

			// query de envio de recibos
			StringBuilder sql = new StringBuilder(
					" SELECT  DISTINCT CODCLI, NOMCLI AS NOMBRE,NUMERO,EMITIDA,VENCE,MES,ANIO,NUMREPORTEZ,CEDRIF,ALICUOTA,TASA  ")
					.append("FROM CONDT.dbo.ENCDOCVENTA ").append(" WHERE TIPODOC = '").append(Constants.TIPO_DOC)
					.append("'");

			if (!ToolsHTML.isEmptyOrNull(codcliPRM)) {
				// System.out.println("------------------------------codcli-------------------------");
				// System.out.println(codcli);
				// System.out.println("------------------------------codcli-------------------------");
				 sql.append(" and CODCLI LIKE '%%%").append(codcliPRM).append("%%%' ");
				//sql.append(" and CODCLI LIKE '%%%").append(codcli).append("%%%' ");
			}

			// .append("' ").append("and CODCLI LIKE '%%%").append(codcliPRM).append("%%%'
			// ");
			// .append(" and mes LIKE '%%%").append(mesPRM).append("%%%' AND ANIO LIKE
			// '%%%").append(anioPRM).append("%%%' ");

			if (!ToolsHTML.isEmptyOrNull(avisoPRM)) {
				sql.append(" and NUMERO LIKE '%%%").append(avisoPRM).append("%%%' ");
			}

			if (!ToolsHTML.isEmptyOrNull(localPRM)) {
				// System.out.println("------------------------------local-------------------------");
				// System.out.println(localPRM);
				// System.out.println("------------------------------local-------------------------");

				sql.append("and NUMREPORTEZ LIKE '%%%").append(localPRM).append("%%%' ");
			}
			if (!ToolsHTML.isEmptyOrNull(nombrePRM)) {
				sql.append("and NOMCLI LIKE '%%%").append(nombrePRM).append("%%%' ");
			}
			

			if (sw>1) {
			
				sql.append("and STATUS LIKE '%%%").append("AVISO").append("%%%' ");
			}
			
			// inicio aqui
			if (!ToolsHTML.isEmptyOrNull(emisionFromPRM) && !ToolsHTML.isEmptyOrNull(emisionToPRM)
					&& emisionFromPRM.length() > 9 && emisionToPRM.length() > 9) {
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					sql.append(" AND (EMITIDA BETWEEN CONVERT(datetime, '" + emisionFromPRM + " 0:00:00 AM', 120) ");
					sql.append(" AND CONVERT(datetime, '" + emisionToPRM + " 11:59:59 PM', 120) ) ");

					break;
				case Constants.MANEJADOR_POSTGRES:
					sql.append(" AND (EMITIDA BETWEEN CAST('");
					sql.append(emisionFromPRM).append("' as timestamp)  ");
					sql.append(" AND CAST('").append(emisionToPRM).append("' as timestamp) ) ");

					break;
				case Constants.MANEJADOR_MYSQL:
					sql.append(" AND (EMITIDA BETWEEN TIMESTAMP('").append(emisionFromPRM).append("')  ");
					sql.append(" AND TIMESTAMP('").append(emisionToPRM).append("') ) ");

					break;
				}
			}

			if (!ToolsHTML.isEmptyOrNull(emisionFromPRM) && emisionFromPRM.length() > 9) {
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					sql.append(" AND (EMITIDA >= CONVERT(datetime, '" + emisionFromPRM + " 0:00:00 AM', 120) ) ");

					break;
				case Constants.MANEJADOR_POSTGRES:
					sql.append(" AND (EMITIDA >= CAST('").append(emisionFromPRM).append("' as timestamp) ) ");

					break;
				case Constants.MANEJADOR_MYSQL:
					sql.append(" AND (EMITIDA >= TIMESTAMP('").append(emisionFromPRM).append("') ) ");

					break;
				}
			}

			if (!ToolsHTML.isEmptyOrNull(emisionToPRM) && emisionToPRM.length() > 9) {
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					sql.append(" AND (EMITIDA <= CONVERT(datetime, '" + emisionToPRM + " 11:59:59 PM', 120) ) ");

					break;
				case Constants.MANEJADOR_POSTGRES:
					sql.append(" AND (EMITIDA <= CAST('").append(emisionToPRM).append("' as timestamp) ) ");

					break;
				case Constants.MANEJADOR_MYSQL:
					sql.append(" AND (EMITIDA <= TIMESTAMP('").append(emisionToPRM).append("') ) ");

					break;
				}
			}

			if (ToolsHTML.isEmptyOrNull(emisionToPRM)) {

				if (ToolsHTML.isEmptyOrNull(periodoPRM)) {
					String aniomes = getUltimoPerido();

					if (aniomes.length() > 5) {
						yyyy = aniomes.substring(0, 4);
						mm = aniomes.substring(4, 6);
						// System.out.println("--------------------aniomesyyyy------------------");
						// System.out.println(yyyy);
						// System.out.println(mm);
					}

					sql.append(" and MES = '").append(mm).append("' ");
					sql.append(" and ANIO = '").append(yyyy).append("' ");
				}

				if (!ToolsHTML.isEmptyOrNull(periodoPRM)) {

					if (periodoPRM.length() > 6) {
						mm = periodoPRM.substring(0, 2);
						yyyy = periodoPRM.substring(3, 7);
					}

					sql.append(" and MES = '").append(mm).append("' ");
					sql.append(" and ANIO = '").append(yyyy).append("' ");
				}

			}
			//// Ultimo

			sql.append(" GROUP by CODCLI,NOMCLI,NUMERO,EMITIDA,VENCE,MES,ANIO,NUMREPORTEZ,CEDRIF,ALICUOTA,TASA ")
					.append(" ORDER BY CODCLI ");
			// //System.out.println("[mandarMailsUsuariosdelSacop]"+sql.toString());

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			pst = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = pst.executeQuery();

			if (rs != null) {
				Hashtable unSoloMailMandar = new Hashtable();
				ArrayList dataUnSoloMailMandar = new ArrayList();

				StringTokenizer stk = null;
				StringTokenizer stk2 = null;
				String contacto = null;
				String contactotxt = null;
				String correocontacto = "";
				StringBuffer SolicitudInforma = new StringBuffer("");
				String correo = null;
				String usuario = null;
				String nombre = null;
				String nombreCli = null;
				String numero = null;
				String emision = null;
				String vencefc = null;
				String periodo = null;
				String mesParm = null;
				String anioParm = null;
				String local = null;
				String alicuota = null;
				String rif = null;
				Double tasa = null;
				String estado = "POR PAGAR";
				String direccion = "handler S/N COLOQUE EN LA BASEDATOS LA DIRECCION";
				String nameFilePdf = null;
				String fondoDeReserva = null;
				String telefono = null;
				String totalBolivaresLetras = null;
				String totalReciboBolivares = null;
				String saldoTotalDolaresSTR = null;

				log.info("inicio de crear pdf ");
				String path = ToolsHTML.getPath().concat("recibos").concat(File.separator);
				// String servidor = request.getScheme() + "://" +
				// ToolsHTML.getServerName(request) + ":" + request.getServerPort() +
				// request.getContextPath() + "/";

				// cargamos los usuario
				Map<String, ContactoTO> listContactos = HandlerUtilDosTorres.listarContactoAlls();
				ContactoTO contactoTO = null;
				Collection detalleRecibo = null;
				PdfPortadaRecibo pdf = new PdfPortadaRecibo();
				while (rs.next()) {
					stk = new StringTokenizer(rs.getString("CODCLI"), ",");
					// stk2 = new StringTokenizer(rs.getString("responsables"), ",");
					contacto = rs.getString("CODCLI") != null ? rs.getString("CODCLI") : "";
					numero = rs.getString("NUMERO") != null ? rs.getString("NUMERO").trim() : "";
					/// forma.setDateApproved(ToolsHTML.formatDateShow(!ToolsHTML.isEmptyOrNull(dateApprove)
					/// ? dateApprove : "", false));
					emision = rs.getString("EMITIDA") != null
							? ToolsHTML.formatDateShow(rs.getString("EMITIDA").toString(), false)
							: "";
					vencefc = rs.getString("VENCE") != null
							? ToolsHTML.formatDateShow(rs.getString("VENCE").toString(), false)
							: "";
					periodo = rs.getString("MES") != null ? rs.getString("MES").concat("/").concat(rs.getString("ANIO"))
							: "";
					mesParm = rs.getString("MES") != null ? rs.getString("MES") : "";
					anioParm = rs.getString("ANIO") != null ? rs.getString("ANIO") : "";
					local = rs.getString("NUMREPORTEZ") != null ? rs.getString("NUMREPORTEZ") : "";
					nombreCli = rs.getString("NOMBRE") != null ? rs.getString("NOMBRE") : "";
					rif = rs.getString("CEDRIF") != null ? rs.getString("CEDRIF") : "";
					alicuota = rs.getString("ALICUOTA") != null ? rs.getString("ALICUOTA").toString() : "";
					tasa = rs.getDouble("TASA");
					String tasaSTR = String.format("%1$,.2f", tasa);

					// contactoTO = listContactos.get(String.valueOf(contacto));
					// System.out.println("***********xxxxxxxxxxxxxxxxxxxxxxxxxx-------*****CONTATOto*************************");
					// System.out.println(listContactos.get(String.valueOf(contacto)));
					// System.out.println("***********xxxxxxxxxxxxxxxxxxxxxxxxxx-------*****CONTATOto*************************");
					contactoTO = listContactos.get(String.valueOf(contacto));
					// Hashtable unSoloMailMandar = new Hashtable();
					if (!listContactos.containsKey(contacto)) {
						// System.out.println("***********-------*****CONTATOto*************************");
						// System.out.println(contactoTO);
						// System.out.println(listContactos);
						System.out.println("***********-------*****CONTATOto*************************");
					}
					if (contactoTO != null) {
						// nombre = contactoTO.getNombre() + " " + contactoTO.getApellidos();
						nombre = contactoTO.getNombre();
						correo = contactoTO.getEmail();
						direccion = contactoTO.getDireccion();
						telefono =  contactoTO.getTelefono2();
						pdf.setLblDireccion("DIRECCIÓN");
						pdf.setDatDireccion(direccion);
						pdf.setDatAviso(numero);
						pdf.setDatNombre(nombre);

						 System.out.println("****************if*************************");
						 System.out.println(correo);
						 System.out.println(telefono);
						 System.out.println("************************************");
						InformarPorMail Informarpormail = new InformarPorMail();
						Informarpormail.setNombre(nombre);
						Informarpormail.setCorreo(correo);
						Informarpormail.setId("0");// contacto
						correocontacto = correo;
						// unSoloMailMandar.put(contacto, Informarpormail);
					}

					// dirrecion = rs.getString("NUMERO") != null ? rs.getString("NUMERO") : "";
					// aqui genero el pdf

					// !ToolsHTML.isEmptyOrNull(dateApprove)

					if (!ToolsHTML.isEmptyOrNull(numero)) {
						nameFilePdf = path.concat("AvisoNo-").concat(String.valueOf(numero)).concat(".pdf");
						log.info("inicio de nombre " + nameFilePdf + " path " + path);

						// cambio de datos a las etiquetas
						pdf.setLblCodigo(rb.getString("doc.number"));
						pdf.setLblAviso("Aviso");
						pdf.setLblNombre("CLIENTE");
						pdf.setLblDireccion("DIRECCIÓN");
						pdf.setLblEmision("EMISIÓN");
						pdf.setLblFechaDeVencimiento("VENCIMIENTO");
						pdf.setLblPeriodoRecibo("PERIODO");
						pdf.setLblLocal("LOCAL");
						pdf.setLblRIF("R.I.F");
						pdf.setLblAlicuota("ALICUOTA");
						// datos DATA
						pdf.setDatCodigo(contacto);
						pdf.setDatAviso(numero);
						pdf.setDatNombre(nombreCli);
						// pdf.setDatDireccion(direccion);

						pdf.setDatEmision(emision);
						pdf.setDatFechaDeVencimiento(vencefc);
						pdf.setDatPeriodoRecibo(periodo);
						pdf.setDatLocal(local);
						pdf.setDatRIF(rif);
						pdf.setDatAlicuota(alicuota);
						pdf.setDatTasaCambiaria(tasaSTR);
						detalleRecibo = getDetalleRecibo(numero, "PROCESADO");
						// System.out.println("-----------------aquiiiiiiiiiiiiiiiiiiii----------");
						// System.out.println(detalleRecibo.size());

						// pdf.setLblVersion(rb.getString("doc.Ver"));
						// pdf.setLblFechaDeAprobacion(rb.getString("doc.dateApprovedAvr"));
						// pdf.setLblFechaDeExpiracion(rb.getString("doc.dateExpireAvr"));
						// pdf.setLblPropietario(rb.getString("cbs.owner"));
						// pdf.setLblOrigen(rb.getString("cbs.origin"));
						// pdf.setLblSolicitado(rb.getString("imp.solicitante"));
						// pdf.setLblImpreso(rb.getString("imp.datePrint"));
						// pdf.setLblNumeroDeCopias(rb.getString("imp.numCopy"));
						// pdf.setLblSoloPropietario(rb.getString("sch.noSignature"));

						// DETALLE RECIBO
						// System.out.println("aquiiiiiiiiiiiiiiiiiiii----------");
						// System.out.println(detalleRecibo);
						// System.out.println(detalleRecibo.size());
						// System.out.println("aquiiiiiiiiiiiiiiiiiiii----------");
						if ((detalleRecibo != null && detalleRecibo.size() > 0)) {
							// System.out.println("aquiiiiiiiiiiiiiiiiiiii----------");
							pdf.setLblRecibo(new TituloDetalleRecibo("REEMBOLSO DE GASTOS EFECTUADOS POR SU CUENTA",
									"GASTOS COMUNES", "CÓDIGO", "DESCRIPCIÓN", "MONTO", "CUOTA PARTE Bs.", "IVA",
									"CUOTA PARTE $"));
							// pdf.setLblRecibo(new
							// TituloDetalleRecibo("1-CÓDIGO","2-DESCRIPCIÓN","3-MONTO","4-CUOTA PARTE
							// Bs.","5-IVA","6-CUOTA PARTE $"));
							pdf.setDetRecibo(cargarDetalleRecibo13(detalleRecibo));
							// Double valorActualFondo =0.0;
							Iterator ite = detalleRecibo.iterator();
							Double valorActualFondo = 0.0;
							String valorAnterior = "00000059";
							while (ite.hasNext()) {
								PdfReciboDetalleRequest obj = (PdfReciboDetalleRequest) ite.next();
								valorActualFondo = String.valueOf(obj.getRecibocodigo().trim())
										.equalsIgnoreCase("00000059") ? obj.getReciboMonto() : 0.0;
								valorAnterior = String.valueOf(obj.getRecibocodigo().trim());
								if (valorAnterior.equalsIgnoreCase("00000059")) {
									break;
								}

							}

							// fondoDeReserva =detalleRecibo.iterator()
							//
							ArrayList total = cargarTotalRecibo(detalleRecibo);
							Double total8 = 0.00;
							Double saldoTotalDolares = 0.00;
							Object total1 = total.get(0); // (String.valueOf(numero)
							Object total2 = total.get(1);
							Object total3 = total.get(2);
							Object total4 = total.get(3);
							Object total5 = total.get(4);
							Object total6 = total.get(5);
							Object total7 = total.get(6);//	Double.parseDouble(reciboMonto)
							totalReciboBolivares =total7.toString(); 
							String saldoAnterioFondo = getFondoReserva(mesParm, anioParm);
							Double saldoTotalFondo = getFondoReservaDouble(mesParm, anioParm) + valorActualFondo;
							total8 = Double.parseDouble((String) total.get(7));
							saldoTotalDolares = total8 / tasa;
							saldoTotalDolaresSTR = String.format("%1$,.2f", saldoTotalDolares);
							// String letra = String.valueOf(convertirNumeroATexto((double) (total8));
							// String letra1 = String.valueOf(convertirNumeroATexto(234.87));
							// String letra2 = String.valueOf(convertirNumeroATexto(21.99));
							String valorActualFondoSTR = String.format("%1$,.2f", valorActualFondo);
							String saldoTotalFondoSTR = String.format("%1$,.2f", saldoTotalFondo);
							/*
							 * System.out.println("******************************ojbjeto--------------");
							 * System.out.println(total); System.out.println(total7);
							 * System.out.println(total8); System.out.println(total7);
							 * System.out.println(saldoAnterioFondo); System.out.println(valorActualFondo);
							 * System.out.println(saldoTotalFondo); System.out.println(valorActualFondoSTR);
							 * System.out.println("******************************ojbjeto--------------");
							 */
							// pdf.setLblRecibo1(new TituloDetalle("CÓDIGO","DESCRIPCIÓN","MONTO"));
							// ArrayList<E> totales = cargarTotalRecibo(detalleRecibo);
							pdf.setlblTotalRecibo(new TotalTablaDetalle("TOTAL GASTOS COMUNES", total1.toString(),
									total2.toString(), total3.toString(), total4.toString()));
							// pdf.setlblTotalRecibo(new TotalTablaDetalle());
							// pdf.setDetRecibo1(cargarTotalRecibo(totadetalleRecibo));
							pdf.setLblDistGasto(new TituloDetalleRecibo("1", "2", "3", "4", "5", "6"));
							ArrayList total25 = cargarDetalleTotalDistribucion(total);
							Double numeroRecibo = getRecibosPendientesDouble(mesParm, anioParm, contacto, local);
							totalBolivaresLetras = convertirNumeroATexto(total8);
							/*
							 * System.out.println("********************total25*******************");
							 * System.out.println(total8); System.out.println(numeroRecibo);
							 * System.out.println(totalBolivaresLetras);
							 * System.out.println("********************total25*******************");
							 */
							// pdf.setlblTotalRecibo(new TotalTablaDetalle("TOTAL GASTOS
							// COMUNES",total1.toString(),total2.toString(),total3.toString(),total4.toString()));
							pdf.setDetDistGasto(total25);
							pdf.setlblTotalDistGasto(new TotalTablaDetalle("TOTAL GENERAL", total5.toString(),
									total6.toString(), " ", total3.toString(), total7.toString()));
							pdf.setLblTotalLetrasBolivares(totalBolivaresLetras);
							// cargarDetalleTotalDistribucion
							pdf.setlblFondoReserva(new TituloDetalleRecibo("FONDOS", " ", "SALDO ANTEROR", "APLCACION",
									"APORTE", "SALDO ACTUAL"));
							pdf.setlblTotalFondoReserva(
									new TotalTablaDetalle("FONDO DE RESERVA(E)", saldoAnterioFondo.toString(), "0,00",
											valorActualFondoSTR.toString(), saldoTotalFondoSTR.toString()));

							pdf.setLblEdoCta(new TituloDetalleRecibo("ESTADO DE CUENTA DEL PROPIETARIO:",
									"RECIBOS PENDIENTES", "TOTAL RECIBOS PENDIENTES", "RECIBO ACTUAL", "TOTAL DEUDA Bs",
									"TOTAL DEUDA $"));
							pdf.setlblTotalEdoCta(new TotalTablaDetalle("  ", " " + numeroRecibo.toString(), "0.00",
									total7.toString(), total7.toString(), saldoTotalDolaresSTR.toString()));

						}

						// Datos de la pagina

						pdf.setDatEncabezado1(HandlerParameters.PARAMETROS.getHeadImp1());
						pdf.setDatEncabezado2(HandlerParameters.PARAMETROS.getHeadImp2());
						pdf.setDatEncabezado3(HandlerParameters.PARAMETROS.getHeadImp3());

					} else {
						System.err.println("es nulo o blanco  numero ");
					}

					// mandamos correo al contacto
					boolean swtienequepasar = true;
					if (!unSoloMailMandar.containsKey(contacto)) {
						contactoTO = listContactos.get(String.valueOf(contacto));

						if (contactoTO != null) {
							// nombre = contactoTO.getNombre() + " " + contactoTO.getApellidos();
							nombre = contactoTO.getNombre();
							correo = contactoTO.getEmail();
							direccion = contactoTO.getDireccion();
							pdf.setLblDireccion("DIRECCIÓN");
							pdf.setDatDireccion(direccion);
							pdf.setDatAviso(numero);
							pdf.setDatNombre(nombre);

							/*
							 * System.out.println("****************if*************************");
							 */
							// System.out.println(correo);
							// System.out.println(direccion);
							// System.out.println("************************************");

							InformarPorMail Informarpormail = new InformarPorMail();
							Informarpormail.setNombre(nombre);
							Informarpormail.setCorreo(correo);
							Informarpormail.setId("0");// contacto
							correocontacto = correo;
							unSoloMailMandar.put(contacto, Informarpormail);
						}

					}
					// este else se esta evalundo
					else {
						InformarPorMail Informarpormail = (InformarPorMail) unSoloMailMandar.get(contacto);
						contactotxt = Informarpormail.getNombre();
						// System.out.println("***************el*********************");
						// System.out.println(contactotxt);
						// System.out.println("************************************");

					} // end if
						// whatsapp

					/*
					if (sw>1) {
					
						query.append("and STATUS LIKE '%%%").append("AVISO").append("%%%' ");
						1 Estimado Co-propietario:
						2 Desde Condominio Dos Torres le informamos que presenta una deuda a la fecha de _____ Avisos de Cobro, que suman la cantidad de USD _______
						3 Lo invitamos a emitir el pago correspondiente, los datos para el pago, son los siguientes:
						los mismo datos de la cuenta


					}
					*/
					
					StringBuffer comentarios = new StringBuffer("");
					comentarios.append(rb.getString("scp.mail1")).append(sw>1 ?" Co-propietario: " : ":").append("*"+nombre.trim()+"*").append(salto).append(salto);
					if (sw> 1) {
						comentarios.append("Desde Condominio Dos Torres le informamos que presenta una deuda a la fecha de ").append(periodo).append(" Avisos de Cobro, que suman la cantidad de USD ").append(saldoTotalDolaresSTR).append(salto).append(salto);
						comentarios.append("el Aviso de cobro No ").append("*"+numero+"* ").append(salto).append("Ha sido Enviado a su email: ").append("*"+correo+"*" ).append(salto);
						comentarios.append("Lo invitamos a emitir el pago correspondiente, ");
						
						
					} else {
						comentarios.append("Se Notifica que el Aviso de cobro No ").append("*"+numero+"*").append(salto).append("Emitido por los gastos de condominio del mes de: ").append(periodo).append(".").append(salto).append(salto);
						comentarios.append("Por un monto de Bs. ").append("*"+totalReciboBolivares.trim()+"*" ).append(salto);
						comentarios.append(textu+totalBolivaresLetras.trim()+" ").append(salto);
						comentarios.append("Tasa Bs ").append(tasa).append(salto);
						comentarios.append("Monto en $ ").append(saldoTotalDolaresSTR).append(salto).append(salto);
						comentarios.append("ha sido Enviado a su email: ").append("*"+correo+"*" ).append(salto).append(salto);
						
					}
					
					comentarios.append(rb.getString("scp.mail3")).append(" ").append(salto);
					comentarios.append(rb.getString("scp.mail4")).append(" ").append(salto);
					comentarios.append(rb.getString("scp.mail5")).append(" ").append(salto);
					comentarios.append(rb.getString("scp.mail6")).append(" ").append(salto);
					comentarios.append(rb.getString("scp.mail7")).append(" ").append(salto);
					comentarios.append(rb.getString("scp.mail8")).append(" ").append(salto);
					comentarios.append(rb.getString("scp.mail9")).append(" ").append(salto);
					comentarios.append(rb.getString("scp.mail10")).append(" ").append(salto);
					comentarios.append(rb.getString("scp.mail11")).append(" ").append(salto).append(salto);
					comentarios.append(rb.getString("scp.mail12")).append(" ").append(salto);
					comentarios.append(rb.getString("scp.mail13")).append(" ").append(salto);
					comentarios.append("`Usuario:").append(contacto.trim()+"`").append(salto);
					comentarios.append("`Clave: 123456`");

					MailForm formaMail = new MailForm();
					WhastAppForm formaWhastApp = new WhastAppForm();
					String pathpdf = ToolsHTML.getPath();
					// String cpath = ToolsHTML.getPath();
					// String fileName =
					// path.concat("recibos").concat(File.separator)+"06Record.xls";
					String fileName = pathpdf.concat("recibos").concat(File.separator).concat("AvisoNo-")
							.concat(numero + ".pdf");
					// System.out.println("***************datos de archivos*********************");
					
					// System.out.println(fileName);
					// System.out.println("************************************");

					formaMail.setFileName(fileName);
					formaMail.setFrom(HandlerParameters.PARAMETROS.getMailAccount());
					formaMail.setNameFrom(rb.getString("mail.system"));
					formaMail.setTo(correo);
					formaMail.setSubject(numero);
					formaMail.setMensaje(comentarios + " ");
					formaWhastApp.setTelefono(telefono);
					formaWhastApp.setMensaje(comentarios + " ");
					 System.out.println("***************datos de mensaje********************");
					 System.out.println(formaWhastApp.getMensaje());
					 System.out.println(formaWhastApp.getTelefono());
					 System.out.println("***************datos de mensaje********************");
					/*
					 * enviarEmailContactos(String titleMail, String user, String account, String
					 * email, String comments)
					 */
					// HandlerUtilDosTorres.enviarEmailContactos("titulo "+formaMail.getSubject(),
					// formaMail.getNameFrom(), formaMail.getFrom(), formaMail.getTo(),
					// formaMail.getMensaje());

					// incio try de enviar
					try {

						//log.info("Iniciando creacion de pdfs: " + nameFilePdf);
						//pdf.createPdf(nameFilePdf);
						//log.info("Creada pdfs para whas: " + nameFilePdf);
						log.info("Iniciando envio por Whasapp de la informacion: " + nameFilePdf);
						if (formaWhastApp != null) {
							EnviarWhastAppTread whastsapp = new EnviarWhastAppTread(formaWhastApp);
							 whastsapp.start();
						}
					} catch (Exception ex) {
						log.debug("Exception");
						log.error(ex.getMessage());
						ex.printStackTrace();
					}
					// fin

				} // end while externo
				rs.close();

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setFinally(con, pst);
		}
	}


	
	// buscar el detalle de recibo
	public static Collection getDetalleRecibo(String idDoc, String status) {
		ArrayList<PdfReciboDetalleRequest> result = new ArrayList<PdfReciboDetalleRequest>();
		PdfReciboDetalleRequest beanFinal = new PdfReciboDetalleRequest();
		String valorActual23 = null;
		/*
		 * Select min(a.CODART) as CODART , sum(a.costo), (select NOMBRE from
		 * CONDT.dbo.CONCTACON where CODIGO=e.ctapadre) NOMART, e.ctapadre from
		 * CONDT.dbo.rendocventa a left join CONDT.dbo.ctaarticulos b on
		 * a.codart=b.codigo left join CONDT.dbo.ctagruart d on b.codgruinv=d.codigo
		 * left join CONDT.dbo.conctacon e on b.codcon=e.codigo left join
		 * CONDT.dbo.ctaempaques c on a.idempaque=c.idrenglon where a.numdoc =
		 * '00000000361' group by ctapadre order by MIN(a.CODART) ;
		 * 
		 */
		try {
			StringBuilder sql = new StringBuilder(
					"Select max(a.CODART) as CODART ,(select NOMBRE from CONDT.dbo.CONCTACON where CODIGO=e.ctapadre) NOMART,")
					.append("SUM(a.COSTO) COSTO ,SUM(a.TOTAL) TOTAL ,SUM(a.IVA) IVA ,SUM(a.TOTAL_MM) TOTAL_MM,MAX(a.TIPOIVA) TIPOIVA, e.ctapadre ")
					.append("from CONDT.dbo.rendocventa a left join CONDT.dbo.ctaarticulos b on a.codart=b.codigo left join CONDT.dbo.ctagruart d on b.codgruinv=d.codigo left join CONDT.dbo.conctacon e on b.codcon=e.codigo left join CONDT.dbo.ctaempaques c on a.idempaque=c.idrenglon where a.numdoc = '")
					.append(idDoc).append("' ").append(" group by ctapadre ").append(" order by MIN(a.CODART) ");
			Vector<?> datos = JDBCUtil.doQueryVector(sql.toString(),
					Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				PdfReciboDetalleRequest bean = new PdfReciboDetalleRequest();
				// operties properties = (Properties) datos.elementAt(row);
				// String idVer = properties.getProperty("idDoc");
				valorActual23 = String.valueOf(properties.getProperty("CODART").trim());

				if (!valorActual23.equalsIgnoreCase("00000059")) {
					bean.setRecibocodigo(properties.getProperty("CODART"));
					bean.setReciboDescipcion(properties.getProperty("NOMART"));
					bean.setReciboMontoSTR(properties.getProperty("COSTO"));
					bean.setReciboCuotaParteSTR(properties.getProperty("TOTAL"));
					bean.setReciboIVASTR(properties.getProperty("IVA"));
					bean.setReciboCuotaParteDolaresSTR(properties.getProperty("TOTAL_MM"));
					bean.setReciboTipoIva(properties.getProperty("TIPOIVA"));
					result.add(bean);
				}
				if (valorActual23.equalsIgnoreCase("00000059")) {
					// valorAnterior.equalsIgnoreCase("00000059"))

					beanFinal.setRecibocodigo(properties.getProperty("CODART"));
					beanFinal.setReciboDescipcion(properties.getProperty("NOMART"));
					beanFinal.setReciboMontoSTR(properties.getProperty("COSTO"));
					beanFinal.setReciboCuotaParteSTR(properties.getProperty("TOTAL"));
					beanFinal.setReciboIVASTR(properties.getProperty("IVA"));
					beanFinal.setReciboCuotaParteDolaresSTR(properties.getProperty("TOTAL_MM"));
					beanFinal.setReciboTipoIva(properties.getProperty("TIPOIVA"));
				}

			}
			result.add(beanFinal);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}

		return result;
	}

	// buscar el saldo fondoReserva
	public static String getFondoReserva(String mesParm, String anioParm) {
		ArrayList<PdfReciboDetalleRequest> result = new ArrayList<PdfReciboDetalleRequest>();
		try {
			/*
			 * query.setLength(0); query.
			 * append("SELECT IDCONTACTO,ORIGEN,CODIGO,NOMBRE,TELEFONO1,TELEFONO2,FAX,CELULAR,"
			 * ); query.append(" CAST(DIRECCION AS VARCHAR(4000)) AS DIRECCION, ");
			 * query.append("EMAIL,EMAIL2,EMAIL3 ");
			 * query.append(" from CONDT.dbo.CTADIRECTORIO ");
			 */

			// (QENC.MES) =MES AND (QENC.ANIO)=ANIO
			// SELECT isnull(SUM(fondor),0) saldoant FROM CONDT.dbo.VCAFONDOSR WHERE
			// TRY_CONVERT(DATETIME,CONCAT(anio,'-',MES,'-','01'),120) <
			// TRY_CONVERT(DATETIME, '2023-03-01', 120)
			StringBuilder sql = new StringBuilder(
					"SELECT isnull(SUM(fondor),0) saldoant FROM CONDT.dbo.VCAFONDOSR fondo WHERE TRY_CONVERT(DATETIME,CONCAT(anio,'-',MES,'-','01'),120)  < TRY_CONVERT(DATETIME, '")
					// .append("ANIO" )
					// .append("-")
					// .append("MES")
					// .append("-01, 120 ) < TRY_CONVERT(DATETIME, ")
					// '2023-08-01', 120)");
					.append(anioParm).append("-").append(mesParm).append("-01', 120 ) ");
			// .append("' ");
			// .append(anioParm).append("' ");

			Vector<?> datos = JDBCUtil.doQueryVector(sql.toString(),
					Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				PdfReciboDetalleRequest bean = new PdfReciboDetalleRequest();
				bean.setReciboFondoReservaSTR(properties.getProperty("saldoant"));
				// bean.setReciboTipoIva(properties.getProperty("TIPOIVA"));
				result.add(bean);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return result.get(0).getReciboFondoReservaSTR();
	}

	public static Double getFondoReservaDouble(String mesParm, String anioParm) {
		ArrayList<PdfReciboDetalleRequest> result = new ArrayList<PdfReciboDetalleRequest>();
		try {
			/*
			 * query.setLength(0); query.
			 * append("SELECT IDCONTACTO,ORIGEN,CODIGO,NOMBRE,TELEFONO1,TELEFONO2,FAX,CELULAR,"
			 * ); query.append(" CAST(DIRECCION AS VARCHAR(4000)) AS DIRECCION, ");
			 * query.append("EMAIL,EMAIL2,EMAIL3 ");
			 * query.append(" from CONDT.dbo.CTADIRECTORIO ");
			 */

			// (QENC.MES) =MES AND (QENC.ANIO)=ANIO
			// SELECT isnull(SUM(fondor),0) saldoant FROM CONDT.dbo.VCAFONDOSR WHERE
			// TRY_CONVERT(DATETIME,CONCAT(anio,'-',MES,'-','01'),120) <
			// TRY_CONVERT(DATETIME, '2023-03-01', 120)
			StringBuilder sql = new StringBuilder(
					"SELECT isnull(SUM(fondor),0) saldoant FROM CONDT.dbo.VCAFONDOSR fondo WHERE TRY_CONVERT(DATETIME,CONCAT(anio,'-',MES,'-','01'),120)  < TRY_CONVERT(DATETIME, '")
					// .append("ANIO" )
					// .append("-")
					// .append("MES")
					// .append("-01, 120 ) < TRY_CONVERT(DATETIME, ")
					// '2023-08-01', 120)");
					.append(anioParm).append("-").append(mesParm).append("-01', 120 ) ");
			// .append("' ");
			// .append(anioParm).append("' ");

			Vector<?> datos = JDBCUtil.doQueryVector(sql.toString(),
					Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				PdfReciboDetalleRequest bean = new PdfReciboDetalleRequest();
				bean.setReciboFondoReservaSTR(properties.getProperty("saldoant"));
				// bean.setReciboTipoIva(properties.getProperty("TIPOIVA"));
				result.add(bean);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return result.get(0).getReciboFondoReserva();
	}

	private static ArrayList cargarDetalleRecibo13(Collection recibos) {
		ArrayList<String> lista = null;
		String tipoIva = "";
		String letra = "E";
		PdfReciboDetalleRequest recibo = null;
		if (recibos != null) {
			lista = new ArrayList<String>();
			Iterator ite = recibos.iterator();
			while (ite.hasNext()) {
				recibo = (PdfReciboDetalleRequest) ite.next();
				recibo.setReciboTipoIva(recibo.getReciboIVA() > 0 ? recibo.getReciboTipoIva() : "E");
				tipoIva = "(".concat(recibo.getReciboTipoIva().concat(")"));
				lista.add(recibo.getRecibocodigo());
				lista.add(recibo.getReciboDescipcion());
				// recibo.getReciboMontoSTR()
				lista.add(recibo.getReciboMontoSTR());
				lista.add(recibo.getReciboCuotaParteSTR());
				lista.add(recibo.getReciboIVASTR());
				lista.add(recibo.getReciboCuotaParteDolaresSTR().concat("    ").concat(tipoIva));
				// lista.add(recibo.getReciboIVA);

			}
			// return lista;
		}
		return lista;

	}

	private static ArrayList cargarTotalRecibo(Collection recibos) {
		ArrayList<String> lista = null;
		PdfReciboDetalleRequest recibo = null;
		double totalMonto = 0;
		double totalCuotaParte = 0;
		double totalCuotaParteConIVA = 0;
		double totalCuotaParteSinIVA = 0;
		double totalIVA = 0;
		double totalCuotaParteDolares = 0;
		double totalReciboGeneral = 0;

		String tipoIva = "--";
		if (recibos != null) {
			lista = new ArrayList<String>();
			Iterator ite = recibos.iterator();
			while (ite.hasNext()) {
				recibo = (PdfReciboDetalleRequest) ite.next();
				totalMonto = recibo.getReciboMonto() + totalMonto;
				totalCuotaParte = recibo.getReciboCuotaParte() + totalCuotaParte;
				if (recibo.getReciboIVA() == 0) {
					totalCuotaParteSinIVA = recibo.getReciboCuotaParte() + totalCuotaParteSinIVA;

				} else {
					totalCuotaParteConIVA = recibo.getReciboCuotaParte() + totalCuotaParteConIVA;

				}
				totalIVA = recibo.getReciboIVA() + totalIVA;
				totalCuotaParteDolares = recibo.getReciboCuotaParteDolares() + totalCuotaParteDolares;
				totalReciboGeneral = round(totalIVA + totalCuotaParte, 2);

			}
			recibo.setReciboMonto(totalMonto);
			recibo.setReciboCuotaParte(totalCuotaParte);
			recibo.setReciboCuotaParteConIVA(totalCuotaParteConIVA);
			recibo.setReciboCuotaParteSinIVA(totalCuotaParteSinIVA);
			recibo.setReciboIVA(totalIVA);
			recibo.setReciboCuotaParteDolares(totalCuotaParteDolares);
			recibo.setReciboTotalGeneral(totalReciboGeneral);
			lista.add(recibo.getReciboMontoSTR());
			lista.add(recibo.getReciboCuotaParteSTR());
			lista.add(recibo.getReciboIVASTR());
			lista.add(recibo.getReciboCuotaParteDolaresSTR().concat("     ").concat(tipoIva));
			lista.add(recibo.getReciboCuotaParteConIVASTR());
			lista.add(recibo.getReciboCuotaParteSinIVASTR());
			lista.add(recibo.getReciboTotalGeneralSTR());
			lista.add(recibo.getReciboTotalGeneral() + "");

		}
		return lista;

	}

	private static ArrayList cargarDetalleTotalDistribucion(ArrayList totales) {
		ArrayList<String> lista = null;
		String tipoIva = "16,00 %";

		lista = new ArrayList<String>();
		if (totales != null) {
			lista.add("Gastos Comunes");
			lista.add((String) totales.get(4));
			lista.add((String) totales.get(5));
			lista.add(tipoIva);
			lista.add((String) totales.get(2));
			lista.add((String) totales.get(6));

			lista.add("Gastos Individuales");
			lista.add("0,00");
			lista.add("0,00");
			lista.add("16,00 %");
			lista.add("0,00");
			lista.add("0,00");
		}

		return lista;

	}

	public static ArrayList loadDetalleRecibo(String idDoc, String statu) {
		// Collection result = null;
		ArrayList<String> result = null;
		if (!ToolsHTML.isEmptyOrNull(idDoc)) {
			try {
				result = (ArrayList<String>) getDetalleRecibo(idDoc, "PROCESADO");
			} catch (Exception ex) {
				log.error(ex.getMessage());
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static String convertirNumeroATexto(double total) {
		/*
		 * public ResponseEntity<String> get(String numero, String
		 * etiquetaEnteroSingular,String etiquetaEnteroPlural, String
		 * etiquetaFlotanteSingular,String etiquetaFlotantePlural, String
		 * etiquetaConector, boolean mayusculas) { logger.info("numeroLetra/?numero=" +
		 * numero + "&etiquetaEntero=" + etiquetaEnteroSingular +
		 * "&etiquetaFlotante="+etiquetaFlotanteSingular+
		 * "&etiquetaConector="+etiquetaConector+ "&mayusculas="+mayusculas);
		 */
		boolean mayusculas = true;
		NumeroLetras letritas = new NumeroLetras();
		String totald = String.valueOf(total);
		String resultado = letritas.Convertir(totald, "Bolivar", "Bolivares", "centimo", "centimos", "con", mayusculas);

		return "SON: " + resultado;
	}

	public static Double getRecibosPendientesDouble(String mesParm, String anioParm, String contactoParm,
			String localParm) {
		ArrayList<PdfReciboDetalleRequest> result = new ArrayList<PdfReciboDetalleRequest>();
		try {
			// select ISNULL(sum(total),0) tot,ISNULL(sum(total_mm),0) tot_mm ,count(numero)
			// nvenc from CONDT.dbo.encdocventa
			// where codcli='V064489409' and numreportez='L-601' and tipodoc='FP' and status
			// not in ('ANULADO') and statuscobro in ('PENDIENTE') ;

			// (QENC.MES) =MES AND (QENC.ANIO)=ANIO
			// SELECT isnull(SUM(fondor),0) saldoant FROM CONDT.dbo.VCAFONDOSR WHERE
			// TRY_CONVERT(DATETIME,CONCAT(anio,'-',MES,'-','01'),120) <
			// TRY_CONVERT(DATETIME, '2023-03-01', 120)
			StringBuilder sql = new StringBuilder(
					"SELECT isnull(count(numero),0) numeroRecibo from CONDT.dbo.encdocventa  WHERE TRY_CONVERT(DATETIME,CONCAT(anio,'-',MES,'-','01'),120)  < TRY_CONVERT(DATETIME, '")
					// .append("ANIO" )
					// .append("-")
					// .append("MES")
					// .append("-01, 120 ) < TRY_CONVERT(DATETIME, ")
					// '2023-08-01', 120)");
					.append(anioParm).append("-").append(mesParm)
					.append("-01', 120 ) and tipodoc='FP' and status not in ('ANULADO') and statuscobro in ('PENDIENTE') and ")
					.append("CODCLI='").append(contactoParm).append("' and numreportez='").append(localParm)
					.append("' ");

			Vector<?> datos = JDBCUtil.doQueryVector(sql.toString(),
					Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				PdfReciboDetalleRequest bean = new PdfReciboDetalleRequest();
				bean.setReciboFondoReservaSTR(properties.getProperty("numeroRecibo"));
				// bean.setReciboTipoIva(properties.getProperty("TIPOIVA"));
				result.add(bean);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return result.get(0).getReciboFondoReserva();
	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	/*
	 * SELECT max(CONCAT(anio, mes)) from CONDT.dbo.encdocventa WHERE tipodoc='FP'
	 * and status not in ('ANULADO') and statuscobro in ('PENDIENTE')
	 */

	public static String getUltimoPerido() {
		ArrayList<PdfReciboDetalleRequest> result = new ArrayList<PdfReciboDetalleRequest>();
		try {
			StringBuilder sql = new StringBuilder(
					"SELECT max(CONCAT(anio, mes)) as aniomes  from CONDT.dbo.encdocventa ")
					.append("WHERE tipodoc='FP' and status not in ('ANULADO') and statuscobro in ('PENDIENTE')");

			Vector<?> datos = JDBCUtil.doQueryVector(sql.toString(),
					Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				PdfReciboDetalleRequest bean = new PdfReciboDetalleRequest();
				bean.setReciboAnioMes(properties.getProperty("aniomes"));
				result.add(bean);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return result.get(0).getReciboAnioMes();
	}

	public String sendMessageWhasat(String url, WhastAppForm formaWhastApp) throws IOException {

		EnviarWhastAppTread whastApp = new EnviarWhastAppTread();
		whastApp.start();

		return "ok";
	}
}
