package com.desige.webDocuments.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.dao.ConfDocumentoDAO;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.files.forms.DocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerNorms;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Created by IntelliJ IDEA. User: srodriguez Date: 03/04/2006 Time: 11:29:28 AM
 * To change this template use File | Settings | File Templates.
 */

public class CrearReporte extends SuperAction {
	// private static ArrayList elementos =
	// ToolsHTML.getProperties("visor.OpenBrowser");
	// private POIFSFileSystem fs;
	public String error;
	private StringBuilder criterio = new StringBuilder("");
	private HttpServletRequest request;
	private HashMap<Integer, String> mapCargo;

	static Logger log = LoggerFactory.getLogger(CrearReporte.class.getName());

	/**
	 * 
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);
		try {
			Users usuario = getUserSession();
			// Luis Cisneros
			// 25/04/07
			// Modelos de Reportes Lista Maestra
			// La version 2 fue pedida por fama de america
			String strModeloReporte = DesigeConf.getProperty("modeloReporte");

			int modeloReporte = 0;

			try {
				modeloReporte = Integer.parseInt(strModeloReporte);
			} catch (Exception e) {
				e.printStackTrace();
				;
				log.debug("ERROR EN EL NUMERO DE MODELO DE REPORTE");
			}

			this.request = request;

			switch (modeloReporte) {
			case 1:
				generarExcel(usuario, request, response);
				break;
			case 2:
				generarExcelModelo2(usuario, request, response);
				break;

			default:
				generarExcel(usuario, request, response);
			}

			return null; //goSucces();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
		}
		return goError();
	}

	private String getCargo(Connection con, String idPerson) {
		String result = "?";
		try {
			// Chequea si esta en el cache de cargos
			Integer id = Integer.valueOf(idPerson);
			if (mapCargo.containsKey(id)) {
				result = mapCargo.get(id);
			} else {
				StringBuilder sql = new StringBuilder("SELECT tbl_cargo.cargo ")
						.append("FROM tbl_cargo ")
						.append("INNER JOIN person ON tbl_cargo.idcargo = ").append(JDBCUtil.getCastAsIntString("person.cargo")).append(" ")
						.append("WHERE person.idperson =").append(idPerson);

				result = JDBCUtil.getScalar(con, sql.toString());
				mapCargo.put(id, result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 
	 * @param label
	 * @param value
	 */
	private void addCriterio(String label, String value) {
		if (value != null && value.trim().length() > 0
				&& !value.trim().equals("0")) {
			criterio.append(criterio.length() > 8 ? " - " : "");
			criterio.append(label);
			criterio.append("=[");
			criterio.append(value);
			criterio.append("]");
		}
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	private String getTypesDocuments(String id) {
		if (request.getSession().getAttribute("typesDocuments") != null) {
			Collection cole = (Collection) request.getSession().getAttribute(
					"typesDocuments");
			Iterator ite = cole.iterator();
			Search obj = null;
			while (ite.hasNext()) {
				obj = (Search) ite.next();
				if (obj.getId().equals(id)) {
					return obj.getDescript();
				}
			}
		}
		return id;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	private String getTypeStatuSession(String id) {
		if (request.getSession().getAttribute("TypeStatuSession") != null) {
			Collection cole = (Collection) request.getSession().getAttribute(
					"TypeStatuSession");
			Iterator ite = cole.iterator();
			Search obj = null;
			while (ite.hasNext()) {
				obj = (Search) ite.next();
				if (obj.getId().equals(id)) {
					return obj.getDescript();
				}
			}
		}
		return id;
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	private String getQuery(Users usuario, HttpServletRequest request)
			throws ApplicationExceptionChecked {
		String TypesStatus = request.getParameter("TypesStatus");
		String propietario = request.getParameter("propietario");
		String version = request.getParameter("version");
		String keys = request.getParameter("keysDoc");
		String name = request.getParameter("nombre");
		String number = request.getParameter("number");
		String typeDoc = request.getParameter("typeDocument");
		String owner = request.getParameter("owner");
		String prefix = request.getParameter("prefix");
		String normISO = request.getParameter("normISO");
		String publicDoc = request.getParameter("public");
		String idRout = request.getParameter("idRout");
		String nameRout = request.getParameter("nameRout");
		String approvedTo = request.getParameter("approvedToHIDDEN");
		String approvedFrom = request.getParameter("approvedFromHIDDEN");
		String expiredTo = request.getParameter("expiredToHIDDEN");
		String expiredFrom = request.getParameter("expiredFromHIDDEN");
		String comment = request.getParameter("comment");
		String idProgramAudit = request.getParameter("idProgramAudit");
		String idPlanAudit = request.getParameter("idPlanAudit");
		BaseDocumentForm doc = null;

		criterio.setLength(0);
		criterio.append("Filtros:");
		addCriterio("Estatus", getTypeStatuSession(TypesStatus));
		addCriterio("Propietario", propietario);
		addCriterio("Versi�n", version);
		addCriterio("Palabras Clave", keys);
		addCriterio("T�tulo del Documento", name);
		addCriterio("Documento N�", number);
		addCriterio("Tipo de Documento", getTypesDocuments(typeDoc));
		addCriterio("Propietario", owner);
		addCriterio("Prefijo", prefix);
		addCriterio("Norma ISO", normISO);
		// addCriterio("Publicado",publicDoc);
		addCriterio("Carpeta: ", idRout);
		addCriterio("Ruta: ", nameRout);
		addCriterio("comment", comment);
		addCriterio("idProgramAudit", idProgramAudit);
		addCriterio("idPlanAudit", idPlanAudit);

		log.debug("TypesStatus: " + TypesStatus);
		log.debug("propietario: " + propietario);
		log.debug("version: " + version);
		log.debug("keys: " + keys);
		log.debug("name: " + name);
		log.debug("number: " + number);
		log.debug("typeDoc: " + typeDoc);
		log.debug("owner: " + owner);
		log.debug("prefix: " + prefix);
		log.debug("normISO: " + normISO);
		log.debug("publicDoc: " + publicDoc);
		log.debug("idRout: " + idRout);
		log.debug("nameRout: " + nameRout);
		log.debug("comment: " + comment);
		try {
			doc = new BaseDocumentForm();

			// los datos para los nombre de los campos
			ConfDocumentoDAO oConfDocumentoDAO = new ConfDocumentoDAO();
			ArrayList lista;
			lista = (ArrayList) oConfDocumentoDAO.findAll();
			if (lista != null) {
				doc.setListaCampos(lista);
				DocumentForm obj = null;
				for (int i = 0; i < lista.size(); i++) {
					obj = (DocumentForm) lista.get(i);
					doc.set(obj.getId().toUpperCase(),
							request.getParameter(obj.getId()));
					addCriterio(obj.getEtiqueta02(),
							request.getParameter(obj.getId()));
				}
			}
			request.getSession().setAttribute("confDocument", lista);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HandlerDocuments hd = new HandlerDocuments();
		String orderBy = hd.getOrdenPublicados(getParameter("ordenVersion"),
				getParameter("ordenNombre"), getParameter("ordenTypeDocument"),
				getParameter("ordenNumber"), getParameter("ordenPropietario"),
				getParameter("ordenApproved"));

		
		Connection conn = null;
		String query = null; 
		try {
			conn = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			query = hd.getQueryPublicados(conn, propietario, typeDoc, number, prefix,
					version, keys, name, usuario, orderBy, idRout, expiredFrom,
					expiredTo, approvedFrom, approvedTo, doc, null, null,comment, idProgramAudit, idPlanAudit);
		} finally {
			if(conn!=null) {
				try {
					conn.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return query;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void generarExcel(Users usuario, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String path = ToolsHTML.getPath().concat("estilo");
		String nameFile = path.concat(File.separator).concat("Formato.xls");
		response.setContentType("application/vnd.ms-excel");
		InputStream inputfile = new FileInputStream(nameFile);
		HSSFWorkbook workbook = new HSSFWorkbook(inputfile, true);
		HSSFSheet sheet = workbook.getSheetAt(0);

		HSSFCellStyle style1 = null;
		HSSFCellStyle style2 = null;
		HSSFCellStyle style3 = null;
		HSSFCellStyle style4 = null;
		HSSFCellStyle style5 = null;
		HSSFCellStyle style6 = null;
		HSSFCellStyle style7 = null;
		try {
			HSSFRow fil = sheet.getRow(12);
			if (fil != null) {
				HSSFCell cell = fil.getCell((short) 0);
				if (cell != null) {
					style1 = cell.getCellStyle();
				}
				cell = fil.getCell((short) 1);
				if (cell != null) {
					style2 = cell.getCellStyle();
				}
				cell = fil.getCell((short) 2);
				if (cell != null) {
					style3 = cell.getCellStyle();
				}
				cell = fil.getCell((short) 3);
				if (cell != null) {
					style4 = cell.getCellStyle();
				}
				cell = fil.getCell((short) 4);
				if (cell != null) {
					style5 = cell.getCellStyle();
				}
				cell = fil.getCell((short) 5);
				if (cell != null) {
					style6 = cell.getCellStyle();
				}
				cell = fil.getCell((short) 6);
				if (cell != null) {
					style7 = cell.getCellStyle();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// Se Escribe la Informaci�n en el Documento
		String sql_query = getQuery(usuario, request);
		log.debug("sql_query: " + sql_query);
		Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		PreparedStatement ps = con.prepareStatement(JDBCUtil.replaceCastMysql(sql_query));
		ResultSet rs = ps.executeQuery();
		int fila = 12;
		HSSFRow row;
		HSSFCell celda;
		// Estableciendo T�tulo del Reporte seg�n el Idioma del Usuario :D
		row = sheet.getRow(1);
		if (row != null) {
			celda = row.getCell((short) 0);
			celda.setCellValue(getMessage("pb.title") + " "
					+ getMessage("pb.to") + " "
					+ ToolsHTML.sdfShowWithoutHour.format(new java.util.Date()));
		}
		// Estableciendo los Par�metros de B�squeda
		// Nombre del Documento
		String paramValue = getParameter("nombre");
		row = sheet.getRow(3);
		if (row != null) {
			// T�tulo de la Celda
			celda = row.getCell((short) 3);
			celda.setCellValue(getMessage("pb.nameDoc") + ":");
			if (!ToolsHTML.isEmptyOrNull(paramValue)) {
				// valor de la Celda
				celda = row.getCell((short) 4);
				celda.setCellValue(paramValue);
			}
		}
		// Consecutivo del Documento
		paramValue = getParameter("number");
		row = sheet.getRow(4);
		if (row != null) {
			// T�tulo de la Celda
			celda = row.getCell((short) 3);
			celda.setCellValue(getMessage("pb.number") + ":");
			if (!ToolsHTML.isEmptyOrNull(paramValue)) {
				// valor de la Celda
				celda = row.getCell((short) 4);
				celda.setCellValue(paramValue);
			}
		}
		// Consecutivo del Documento
		paramValue = getParameter("version");
		row = sheet.getRow(5);
		if (row != null) {
			// T�tulo de la Celda
			celda = row.getCell((short) 3);
			celda.setCellValue(getMessage("pb.version") + ":");
			if (!ToolsHTML.isEmptyOrNull(paramValue)) {
				// valor de la Celda
				celda = row.getCell((short) 4);
				celda.setCellValue(paramValue);
			}
		}

		// Propietario del Documento
		paramValue = getParameter("nameOwner");
		row = sheet.getRow(6);
		if (row != null) {
			// T�tulo de la Celda
			celda = row.getCell((short) 3);
			celda.setCellValue(getMessage("pb.owner") + ":");
			if (!ToolsHTML.isEmptyOrNull(paramValue)) {
				// valor de la Celda
				celda = row.getCell((short) 4);
				celda.setCellValue(paramValue);
			}
		}
		// Tipo de Documento
		paramValue = getParameter("nameTypeDoc");
		row = sheet.getRow(7);
		if (row != null) {
			// T�tulo de la Celda
			celda = row.getCell((short) 3);
			celda.setCellValue(getMessage("pb.typeDoc") + ":");
			if (!ToolsHTML.isEmptyOrNull(paramValue)) {
				// valor de la Celda
				celda = row.getCell((short) 4);
				celda.setCellValue(paramValue);
			}
		}
		// Estructura
		paramValue = getParameter("nameRout");
		row = sheet.getRow(8);
		if (row != null) {
			// T�tulo de la Celda
			celda = row.getCell((short) 3);
			celda.setCellValue(getMessage("pb.folder") + ":");
			if (!ToolsHTML.isEmptyOrNull(paramValue)) {
				// valor de la Celda
				celda = row.getCell((short) 4);
				celda.setCellValue(paramValue);
			}
		}
		// Palabras Claves
		paramValue = getParameter("keysDoc");
		row = sheet.getRow(9);
		if (row != null) {
			// T�tulo de la Celda
			celda = row.getCell((short) 3);
			celda.setCellValue(getMessage("pb.keys") + ":");
			if (!ToolsHTML.isEmptyOrNull(paramValue)) {
				// valor de la Celda
				celda = row.getCell((short) 4);
				celda.setCellValue(paramValue);
			}
		}
		// T�tulos de la Tabla
		row = sheet.getRow(11);
		row.getCell((short) 0).setCellValue(getMessage("cbs.name"));
		row.getCell((short) 1).setCellValue(getMessage("doc.type"));
		row.getCell((short) 2).setCellValue(getMessage("doc.number"));
		row.getCell((short) 3).setCellValue(getMessage("showDoc.Ver"));
		row.getCell((short) 4).setCellValue(getMessage("cbs.owner"));
		row.getCell((short) 5).setCellValue(getMessage("showDoc.approved"));
		row.getCell((short) 6).setCellValue(getMessage("showDoc.create"));

		while (rs.next()) {
			row = sheet.createRow((short) fila);
			// Valor y estilo si Aplica en la Columna # 1
			celda = row.createCell((short) 0);
			if (style1 != null) {
				celda.setCellStyle(style1);
			}
			celda.setCellValue(rs.getString("nameDocument"));
			// Valor y estilo si Aplica en la Columna # 2
			celda = row.createCell((short) 1);
			if (style2 != null) {
				celda.setCellStyle(style2);
			}
			celda.setCellValue(rs.getString("typeDoc"));
			// Valor y estilo si Aplica en la Columna # 3
			celda = row.createCell((short) 2);
			if (style3 != null) {
				celda.setCellStyle(style3);
			}

			String correlativo = rs.getString("correlativo");
			if (!ToolsHTML.isEmptyOrNull(correlativo)) {
				celda.setCellValue(correlativo);
			} else {
				if (!ToolsHTML.isEmptyOrNull(rs.getString("number"))) {
					celda.setCellValue(rs.getString("number"));
				} else {
					celda.setCellValue(rs.getString("prefix"));
				}
			}

			// Valor y estilo si Aplica en la Columna # 4
			String mayorver = rs.getString("MayorVer") != null ? rs.getString(
					"MayorVer").trim() : "";
			String minorver = rs.getString("MinorVer") != null ? rs.getString(
					"MinorVer").trim() : "";
			celda = row.createCell((short) 3);
			if (style4 != null) {
				celda.setCellStyle(style4);
			}
			celda.setCellValue(mayorver + "." + minorver);
			// Valor y estilo si Aplica en la Columna # 5
			celda = row.createCell((short) 4);
			if (style5 != null) {
				celda.setCellStyle(style5);
			}
			celda.setCellValue(rs.getString("Nombre"));
			// Valor y estilo si Aplica en la Columna # 6
			celda = row.createCell((short) 5);
			String fecha = rs.getString("dateApproved");
			if (!ToolsHTML.isEmptyOrNull(fecha)) {
				if (style6 != null) {
					celda.setCellStyle(style6);
				}
				celda.setCellValue(ToolsHTML.formatDateShow(
						rs.getString("dateApproved"), false));
			} else {
				celda.setCellValue("");
			}
			// Fecha de Creaci�n del Documento
			// fecha = rs.getString("dateCreation");
			// if (!ToolsHTML.isEmptyOrNull(fecha)) {
			// Valor y estilo si Aplica en la Columna # 7
			celda = row.createCell((short) 6);
			if (style7 != null) {
				celda.setCellStyle(style7);
			}
			// date = ToolsHTML.sdfShowConvert.parse(value);
			// dateCreation = ToolsHTML.sdfShow.format(date);

			celda.setCellValue(ToolsHTML.sdfShowWithoutHour.format(rs
					.getTimestamp("dateCreation")));
			// celda.setCellValue(ToolsHTML.formatDateShow(rs.getString("dateApproved"),false));
			// } else {
			// celda.setCellValue("");
			// }
			fila++;
		}
		// Enviando el Archivo al Cliente

		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition",
				"attachment; filename=ListaMaestra.xls");
		response.setHeader("content-transfer-encoding", "binary");
		OutputStream out = response.getOutputStream();
		workbook.write(out);
		out.close();
		try {
			if (con != null) {
				con.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Busca la descripcion de la norma en la coleccion de las mismas
	 * 
	 * @param colNorma
	 * @param idNorma
	 * @return
	 */
	private String getNormaDesc(Collection<Search> colNorma,
			final String idNorma) {
		Search search = (Search) CollectionUtils.find(colNorma,
				new Predicate() {
					public boolean evaluate(Object obj) {
						Search search = (Search) obj;
						if (search.getId().equalsIgnoreCase(idNorma)) {
							return true;
						} else {
							return false;
						}
					}
				});

		return (search != null ? search.getDescript() : "");
	}

	/**
	 * Retirna truer si el documento esta seleccionado en pantalla
	 * 
	 * @param documents
	 * @param idDocument
	 * @return
	 */
	private boolean existDocument(Collection documents, long idDocument) {

		final String id = String.valueOf(idDocument);
		BaseDocumentForm bdf = (BaseDocumentForm) CollectionUtils.find(
				documents, new Predicate() {
					public boolean evaluate(Object obj) {
						BaseDocumentForm doc = (BaseDocumentForm) obj;
						return id.equalsIgnoreCase(doc.getIdDocument());
					}
				});

		return (bdf != null);
	}

	/**
	 * Muestar descripcion de la norma en el titulo
	 * 
	 * @param colNorma
	 */
	private void fixCriterioForNorma(Collection<Search> colNorma) {
		String tempString = criterio.toString();
		int pos = tempString.indexOf("Norma ISO");
		if (pos != -1) {
			pos = tempString.indexOf("[", pos + 1);
			if (pos != -1) {
				tempString = tempString.substring(pos + 1);
				pos = tempString.indexOf("]");
				if (pos != -1) {
					tempString = tempString.substring(0, pos);
				}
				String descNorma = "[".concat(
						getNormaDesc(colNorma, tempString)).concat("]");

				tempString = "[".concat(tempString).concat("]");
				pos = criterio.indexOf(tempString);
				criterio.replace(pos, pos + tempString.length(), descNorma);
			}
		}
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void generarExcelModelo2(Users usuario, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String path = ToolsHTML.getPath().concat("estilo");
		String nameFile = path.concat(File.separator).concat(
				"FormatoModelo2.xls");
		response.setContentType("application/vnd.ms-excel");
		InputStream inputfile = new FileInputStream(nameFile);
		HSSFWorkbook workbook = new HSSFWorkbook(inputfile, true);
		HSSFSheet sheet = workbook.getSheetAt(0);

		HSSFCellStyle style1 = null;
		HSSFCellStyle style2 = null;
		HSSFCellStyle style3 = null;
		HSSFCellStyle style4 = null;
		HSSFCellStyle style5 = null;
		HSSFCellStyle style6 = null;
		HSSFCellStyle style7 = null;
		HSSFCellStyle style8 = null;

		try {
			HSSFRow fil = sheet.getRow(5);
			if (fil != null) {
				HSSFCell cell = fil.getCell((short) 0);
				if (cell != null) {
					style1 = cell.getCellStyle();
				}
				cell = fil.getCell((short) 1);
				if (cell != null) {
					style2 = cell.getCellStyle();
				}
				cell = fil.getCell((short) 2);
				if (cell != null) {
					style3 = cell.getCellStyle();
				}
				cell = fil.getCell((short) 3);
				if (cell != null) {
					style4 = cell.getCellStyle();
				}
				cell = fil.getCell((short) 4);
				if (cell != null) {
					style5 = cell.getCellStyle();
				}
				cell = fil.getCell((short) 5);
				if (cell != null) {
					style6 = cell.getCellStyle();
				}
				cell = fil.getCell((short) 6);
				if (cell != null) {
					style7 = cell.getCellStyle();
				}
				cell = fil.getCell((short) 7);
				if (cell != null) {
					style8 = cell.getCellStyle();
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// Se Escribe la Informaci�n en el Documento
		// Se Escribe la Informaci�n en el Documento
		Collection<Search> colNorma = HandlerNorms.getAllNorms();
		String sql_query = getQuery(usuario, request);
		fixCriterioForNorma(colNorma);
		log.debug("sql_query: " + sql_query);

		int fila = 5;
		HSSFRow row;
		HSSFRow row2;
		HSSFCell celda;
		// Estableciendo T�tulo del Reporte seg�n el Idioma del Usuario :D
		// Fila 2
		row = sheet.getRow(1);
		if (row != null) {
			// Columna 2 titulo
			//ydavila Ticket 001-00-003265
			//celda = row.getCell((short) 0);
			celda = row.getCell((short) 0);
			celda.setCellValue(getMessage("pb.title") + " "
					+ getMessage("pb.to") + " "
					+ ToolsHTML.sdfShowWithoutHour.format(new java.util.Date()));
		}

		// Criterios seleccionados del reporte

		int cont = 0;
		row = sheet.getRow(2);
		HSSFCell col = row.getCell((short) 0);
		col.setCellValue(criterio.toString());

		// Estableciendo los Par�metros de B�squeda
		// Estructura
		String paramValue = getParameter("nameRout");
		log.debug(this.getClass().getSimpleName()
				+ "[generarExcelModelo2: getParameter(\"nameRout\")]"
				+ paramValue);
		if (row != null) {
			// Ruta
			celda = row.getCell((short) 3);
			if (!ToolsHTML.isEmptyOrNull(paramValue)) {
				String ruta[] = paramValue.split("\\\\");
				celda.setCellValue(ruta[ruta.length - 1]);
			} else {
				HSSFCellStyle blanco = sheet.getRow((short) 0)
						.getCell((short) 2).getCellStyle();

				row.createCell((short) 3);
				row.getCell((short) 3).setCellStyle(blanco);
				row.createCell((short) 4);
				row.getCell((short) 5).setCellStyle(blanco);

				row.createCell((short) 5);
				row.getCell((short) 5).setCellStyle(blanco);
				row.createCell((short) 6);
				row.getCell((short) 6).setCellStyle(blanco);
			}
		}

		// T�tulos de la Tabla
		row = sheet.getRow(4);
//ydavila Ticket 001-00-003265
	/*	row.getCell((short) 0).setCellValue(getMessage("rep.tituloDoc"));
		row.getCell((short) 1).setCellValue(getMessage("doc.type"));
		row.getCell((short) 2).setCellValue(getMessage("rep.codigo"));
		row.getCell((short) 3).setCellValue(getMessage("showDoc.Ver"));
		row.getCell((short) 4).setCellValue(getMessage("showDoc.create"));
		row.getCell((short) 5).setCellValue(getMessage("showDoc.approved"));
		row.getCell((short) 6).setCellValue(getMessage("user.cargo"));
		row.getCell((short) 7).setCellValue("Norma");
   */
		row.getCell((short) 0).setCellValue(getMessage("doc.numberLM"));
		row.getCell((short) 1).setCellValue(getMessage("showDoc.VerLM"));
		row.getCell((short) 2).setCellValue(getMessage("showDoc.approvedLM"));
		row.getCell((short) 3).setCellValue(getMessage("doc.typeLM"));
		row.getCell((short) 4).setCellValue(getMessage("cbs.nameLM"));
		row.getCell((short) 5).setCellValue("Comentarios");
		row.getCell((short) 6).setCellValue(getMessage("showDoc.create"));
		row.getCell((short) 7).setCellValue(getMessage("user.cargo"));
		row.getCell((short) 8).setCellValue("Norma");

//ydavila Ticket 001-00-003265		
		
		Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		PreparedStatement ps = con.prepareStatement(JDBCUtil.replaceCastMysql(sql_query));
		ResultSet rs = ps.executeQuery();

		Collection documents = (Collection) request.getSession().getAttribute(
				"published");
		StringBuilder sbNorma = new StringBuilder("");
		mapCargo = new HashMap<Integer, String>();

		while (rs.next()) {
			// Chequea si el documento esta seleccionado o no ya que la consulta
			// se trae todos los documentos (pesimo!!!!)
			if (!existDocument(documents, rs.getLong("numgen"))) {
				continue;
			}

			row = sheet.createRow((short) fila);
//ydavila Ticket 001-00-003265					
			/*// Valor y estilo si Aplica en la Columna # 1
			celda = row.createCell((short) 0);
			if (style1 != null) {
				celda.setCellStyle(style1);
			}
			celda.setCellValue(rs.getString("nameDocument"));
			// Valor y estilo si Aplica en la Columna # 2
			celda = row.createCell((short) 1);
			if (style2 != null) {
				celda.setCellStyle(style2);
			}
			celda.setCellValue(rs.getString("typeDoc"));
			// Valor y estilo si Aplica en la Columna # 3
			celda = row.createCell((short) 2);
			if (style3 != null) {
				celda.setCellStyle(style3);
			}

			String correlativo = rs.getString("correlativo");
			if (!ToolsHTML.isEmptyOrNull(correlativo)) {
				celda.setCellValue(correlativo);
			} else {
				if (!ToolsHTML.isEmptyOrNull(rs.getString("number"))) {
					celda.setCellValue(rs.getString("number"));
				} else {
					celda.setCellValue(rs.getString("prefix"));
				}
			}

			// Valor y estilo si Aplica en la Columna # 4
			String mayorver = rs.getString("MayorVer") != null ? rs.getString(
					"MayorVer").trim() : "";
			String minorver = rs.getString("MinorVer") != null ? rs.getString(
					"MinorVer").trim() : "";
			celda = row.createCell((short) 3);
			if (style4 != null) {
				celda.setCellStyle(style4);
			}
			celda.setCellValue(mayorver + "." + minorver);

			// Valor y estilo si Aplica en la Columna # 5
			celda = row.createCell((short) 4);
			if (style7 != null) {
				celda.setCellStyle(style7);
			}
			celda.setCellValue(ToolsHTML.sdfShowWithoutHour.format(rs
					.getTimestamp("dateCreation")));

			// Valor y estilo si Aplica en la Columna # 6
			celda = row.createCell((short) 5);
			String fecha = rs.getString("dateApproved");
			if (!ToolsHTML.isEmptyOrNull(fecha)) {
				if (style6 != null) {
					celda.setCellStyle(style6);
				}
				celda.setCellValue(ToolsHTML.formatDateShow(
						rs.getString("dateApproved"), false));
			} else {
				celda.setCellValue("");
			}

			// Valor y estilo si Aplica en la Columna # 7
			celda = row.createCell((short) 6);
			if (style5 != null) {
				celda.setCellStyle(style5);
			}
			celda.setCellValue(getCargo(rs.getString("idPerson")));

			celda = row.createCell((short) 7);
			if (style7 != null) {
				style7.setWrapText(true);
				celda.setCellStyle(style7);
			}
			String normISO = rs.getString("normiso");
			if (normISO != null) {
				normISO = normISO.trim();
				String[] normArray = normISO.split(",");
				for (String idNorma : normArray) {
					String tempString = getNormaDesc(colNorma, idNorma);
					if (tempString.length() > 0) {
						sbNorma.append("* ").append(tempString).append("\n\r");
					}
				}
			}
			celda.setCellValue(sbNorma.toString());
			sbNorma.setLength(0);
*/
			// Valor y estilo si Aplica en la Columna # 1
						celda = row.createCell((short) 0);
						if (style1 != null) {
							celda.setCellStyle(style1);
						}
						String correlativo = rs.getString("correlativo");
						if (!ToolsHTML.isEmptyOrNull(correlativo)) {
							celda.setCellValue(correlativo);
						} else {
							if (!ToolsHTML.isEmptyOrNull(rs.getString("number"))) {
								celda.setCellValue(rs.getString("number"));
							} else {
								celda.setCellValue(rs.getString("prefix"));
							}
						}

				// Valor y estilo si Aplica en la Columna # 2
						String mayorver = rs.getString("MayorVer") != null ? rs.getString(
								"MayorVer").trim() : "";
						String minorver = rs.getString("MinorVer") != null ? rs.getString(
								"MinorVer").trim() : "";
						celda = row.createCell((short) 1);
						if (style2 != null) {
							celda.setCellStyle(style2);
						}
						celda.setCellValue(mayorver + "." + minorver);

				// Valor y estilo si Aplica en la Columna # 3
						celda = row.createCell((short) 2);
						String fecha = rs.getString("dateApproved");
						if (!ToolsHTML.isEmptyOrNull(fecha)) {
							if (style3 != null) {
								celda.setCellStyle(style3);
							}
							celda.setCellValue(ToolsHTML.formatDateShow(
									rs.getString("dateApproved"), false));
						} else {
							celda.setCellValue("");
						}

				// Valor y estilo si Aplica en la Columna # 4
						celda = row.createCell((short) 3);
						if (style4 != null) {
							celda.setCellStyle(style4);
						}
						celda.setCellValue(rs.getString("typeDoc"));
		
				// Valor y estilo si Aplica en la Columna # 5
						celda = row.createCell((short) 4);
						if (style5 != null) {
							celda.setCellStyle(style5);
						}
						celda.setCellValue(rs.getString("nameDocument"));
							
				//ydavila Ticket 001-00-003265	 Se agrega salida para columna Comentarios
						celda = row.createCell((short) 5);
						if (style7 != null) {
							celda.setCellStyle(style7);
						}
						/*ydavila
						 * Connection conn1 = null;
						PreparedStatement pst1 = null;
						ResultSet rs1 = null;
						StringBuffer sb = new StringBuffer();
						conn1 = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
						String type="5";
						sb.append("SELECT comments FROM historydocs WHERE idnode = ")
						        .append(rs.getString("numgen"))
								.append(" and type = '").append(type).append("'")
						        .append(" order by idhistory desc limit 1");
						pst1 = conn1.prepareStatement(sb.toString());
						System.out.println("rs= "+rs);
						System.out.println("documents= "+documents + "number= "+rs.getString("number") + "idnode= "+rs.getString("idnode"));
						rs1 = pst1.executeQuery();
						String comment= "";
						while (rs1.next()) {
							//Collection history = HandlerStruct.getHistoryDoc(idNode);
							comment = rs1.getString("comments");
							if (ToolsHTML.isEmptyOrNull(comment)) {
								celda.setCellValue("");
							} else {
								//ydavila Ticket 001-00-003265	 Se agrega salida para columna Comentarios
								String comment2=HandlerDocuments.getDescriptCommentExcel(comment);
								// ydavila	Se filtra im�genes para Excel
								comment=comment2;
								String imagen="<img";
								if (comment.contains(imagen)) {
									ResourceBundle rb = ResourceBundle.getBundle("lst.longcomment");
											comment=rb.toString();
								}
								celda.setCellValue(comment);
							}
						}*/
				// Valor y estilo si Aplica en la Columna Comentarios (#6)		
					/*	celda = row.createCell((short) 5);
						if (style7 != null) {
							celda.setCellStyle(style7);
						}
						celda.setCellValue(rs.getString("comments"));
					*/			
								
				//ydavila 
						String estatus="Aprobado";
						String comment2=HandlerDocuments.getDescriptCommentExcel(con, rs.getString("numgen"),estatus);
						celda.setCellValue(comment2);		
								
				// Valor y estilo si Aplica en la Columna # 7
						celda = row.createCell((short) 6);
						if (style2 != null) {
							celda.setCellStyle(style2);
						}
						celda.setCellValue(ToolsHTML.sdfShowWithoutHour.format(rs
								.getTimestamp("dateCreation")));

				// Valor y estilo si Aplica en la Columna # 8
						celda = row.createCell((short) 7);
						if (style5 != null) {
							celda.setCellStyle(style5);
						}
						celda.setCellValue(getCargo(con, rs.getString("idPerson")));

						celda = row.createCell((short) 8);
						if (style7 != null) {
						//	style7.setWrapText(true);
						//	celda.setCellStyle(style7);
						}
						String normISO = rs.getString("normiso");
						if (normISO != null) {
							normISO = normISO.trim();
							String[] normArray = normISO.split(",");
							for (String idNorma : normArray) {
								String tempString = getNormaDesc(colNorma, idNorma);
								if (tempString.length() > 0) {
									sbNorma.append("* ").append(tempString).append("\n\r");
								}
							}
						}
						celda.setCellValue(sbNorma.toString());
						sbNorma.setLength(0);
			
			fila++;
		}
		JDBCUtil.closeQuietly(con, ps, rs);

		// Enviando el Archivo al Cliente

		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition",
				"attachment; filename=ListaMaestra.xls");
		response.setHeader("content-transfer-encoding", "binary");
		OutputStream out = response.getOutputStream();
		workbook.write(out);
		out.close();
	}


	
	
	// public void generarExcel(HttpServletRequest request,HttpServletResponse
	// response){
	// try{
	// Locale defaultLocale = new
	// Locale(DesigeConf.getProperty("language.Default"),DesigeConf.getProperty("country.Default"));
	// ResourceBundle rb =
	// ResourceBundle.getBundle("LoginBundle",defaultLocale);
	// String path = ToolsHTML.getPath().concat("tmp"); //
	// \\tmp
	// String ruta = path + File.separator;
	// String sql_query=
	// ToolsHTML.getAttribute(request,"queryReporte")!=null?(String)ToolsHTML.getAttribute(request,"queryReporte"):"";
	// Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	// PreparedStatement ps = con.prepareStatement(sql_query);
	// ResultSet rs=null;
	// rs=ps.executeQuery();
	// CrearReporte excel1 = new CrearReporte();
	// HSSFWorkbook wb = new HSSFWorkbook();
	// FileOutputStream fileOut = new
	// FileOutputStream(ruta+rb.getString("lst.archivoreporte"));
	// HSSFSheet sheet = wb.createSheet(rb.getString("lst.maestra"));
	// HSSFSheet sheet2 = wb.createSheet("Hoja2");
	// HSSFRow row;
	// HSSFCell cell;
	// wb.write(fileOut);
	// fs = new POIFSFileSystem(new
	// FileInputStream(ruta+rb.getString("lst.archivoreporte")));
	// wb = new HSSFWorkbook(fs);
	// excel1.encabezado( wb ,sheet);
	// sheet = wb.getSheetAt(0);
	// sheet2 = wb.getSheetAt(1);
	// //book.connect();
	// //rs=book.viewbooks();
	// float acumularbolivares=0;
	// float acumulardolares=0;
	// int fila=7;
	// int column=0;
	// while (rs.next()){
	// row=sheet.createRow((short)fila);
	// row.createCell((short)column).setCellValue(rs.getString("nameDocument"));
	//
	// column +=1;
	// row=sheet.createRow((short)fila);
	// row.createCell((short)column).setCellValue(rs.getString("typeDoc"));
	//
	// column +=1;
	// row=sheet.createRow((short)fila);
	// row.createCell((short)column).setCellValue(rs.getString("correlativo") );
	//
	// column +=1;
	// row=sheet.createRow((short)fila);
	// String
	// mayorver=rs.getString("MayorVer")!=null?rs.getString("MayorVer").trim():"";
	// String
	// minorver=rs.getString("MinorVer")!=null?rs.getString("MinorVer").trim():"";
	// row.createCell((short)column).setCellValue(mayorver+"."+minorver);
	//
	//
	// column +=1;
	// row=sheet.createRow((short)fila);
	// row.createCell((short)column).setCellValue(rs.getString("Nombre"));
	//
	// column +=1;
	// row=sheet.createRow((short)fila);
	// if (!ToolsHTML.isEmptyOrNull(rs.getString("dateApproved"))) {
	// row.createCell((short)column).setCellValue(ToolsHTML.formatDateShow(rs.getString("dateApproved"),false));
	// } else {
	// row.createCell((short)column).setCellValue("");
	// }
	//
	// column =0;
	// fila +=1;
	// //System.out.println("Fila="+fila +" Columna="+column);
	// }
	// fileOut = new FileOutputStream(ruta+rb.getString("lst.archivoreporte"));
	// wb.write(fileOut);
	// fileOut.close();
	//
	//
	// if (processFile(response,request,path,rb)) {
	// //System.out.println("Todo fine");
	// }
	// if (con!=null){
	// con.close();
	// }
	// if (ps!=null){
	// ps.close();
	// }
	// if(rs!=null){
	// rs.close();
	// }
	//
	//
	// }catch(Exception e){
	// e.printStackTrace();
	// }
	//
	// }

	// private static synchronized boolean processFile(HttpServletResponse
	// response,HttpServletRequest request,String path
	// , ResourceBundle rb){
	// String extension =
	// ShowDocument.getExtensionFile(rb.getString("lst.archivoreporte"));
	// response.setContentType("application/vnd.ms-excel");
	// InputStream in = null;
	// OutputStream out = null;
	//
	//
	// String initServer =
	// ToolsHTML.getServerPath(ToolsHTML.getServerName(),request.getServerPort(),request.getContextPath());
	// //String fileURL = initServer + "/tmp/" + forma.getNameFile();
	// String fileURL = initServer + "/" + DesigeConf.getProperty("folderTmp") +
	// "/" + rb.getString("lst.archivoreporte");
	//
	// boolean isVisioDoc = elementos.contains(extension);
	// try {
	//
	// boolean onlyRead = request.getParameter("downFile")==null?true:false;
	// //System.out.println("onlyRead = " + onlyRead);
	// if ((!isVisioDoc)||(!onlyRead)) {
	// //if(onlyRead) {
	// // response.setHeader("Content-disposition", "inline;open; filename=\"" +
	// "copia2.xls" + "\"");
	// // } else {
	// response.setHeader("Content-disposition", "attachment; filename=\"" +
	// rb.getString("lst.archivoreporte") + "\"");
	// // }
	// response.setHeader("content-transfer-encoding", "binary");
	// //System.out.println("path=" + path);
	//
	// //System.out.println("path completo="+path + File.separator +
	// rb.getString("lst.archivoreporte"));
	// File fichero = new File(path + File.separator +
	// rb.getString("lst.archivoreporte"));
	// // File fichero = new
	// File("C:\\Java\\Tomcat5.0\\webapps\\WebDocuments\\tmp\\otro.xls");
	//
	// //System.out.println("fichero.getAbsolutePath()="+fichero.getAbsolutePath());
	// //File fichero = new File(path + File.separator + forma.getNameFile());
	// response.setHeader("Content-Length",""+fichero.length());
	// response.setHeader("Content-Type","application/vnd.ms-excel");
	// //System.out.println("bytes del Archivo = " + fichero.length());
	// in = new FileInputStream(fichero);
	// out = response.getOutputStream();
	// int bytesRead = 0;
	// byte buffer[] = new byte[8192];
	// while((bytesRead = in.read(buffer, 0, 8192)) != -1) {
	//
	// //System.out.println("2006-04-03  2 37 pm");
	// out.write(buffer, 0, bytesRead);
	// response.flushBuffer();
	// }
	// ShowDocument.closeIOs(in,out);
	// try {
	// //System.out.println("DELETE");
	// fichero.delete();
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// return true;
	// } else {
	// response.setHeader("Cache-control","no-cache");
	// response.setHeader("Pragma","no-cache");
	// response.setDateHeader ("Expires", 0);
	// response.sendRedirect(fileURL);
	// return true;
	// }
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// } finally {
	// ShowDocument.closeIOs(in,out);
	// }
	// return false;
	// }

	// private void encabezado(HSSFWorkbook wb , HSSFSheet sheet)throws
	// Exception{
	// CrearReporte xls = new CrearReporte();
	// try{
	// Locale defaultLocale = new
	// Locale(DesigeConf.getProperty("language.Default"),DesigeConf.getProperty("country.Default"));
	// ResourceBundle rb =
	// ResourceBundle.getBundle("LoginBundle",defaultLocale);
	// HSSFCell cell;
	// HSSFRow row;
	// HSSFCellStyle cellStyle = wb.createCellStyle();
	// HSSFCellStyle cellStyle2 = wb.createCellStyle();
	//
	// sheet = wb.getSheetAt(0);
	// row=sheet.createRow((short)1);
	// cell = row.createCell((short)2);
	// row.setHeight( (short) 300 );
	// sheet.setColumnWidth( (short) 0, (short)13000 );
	// sheet.setColumnWidth( (short) 1, (short)13000 );
	// sheet.setColumnWidth( (short) 2, (short)5000 );
	// sheet.setColumnWidth( (short) 3, (short)5000 );
	// sheet.setColumnWidth( (short) 4, (short)5000 );
	// sheet.setColumnWidth( (short) 5, (short)8000 );
	// sheet.setColumnWidth( (short) 6, (short)5000 );
	// sheet.setColumnWidth( (short) 8, (short)5000 );
	// sheet.setColumnWidth( (short) 9, (short)5000 );
	// sheet.setColumnWidth( (short) 10, (short)5000 );
	// sheet.setColumnWidth( (short) 11, (short)5000 );
	// cell = row.createCell((short)1);
	// cell.setCellValue(rb.getString("lic.propietario1"));
	// xls.azul_celda(cellStyle,cell);
	//
	// row=sheet.createRow((short)2);
	// cell = row.createCell((short)0);
	// cell.setCellValue(rb.getString("pb.title"));
	// // xls.azul_celda(cellStyle,cell);
	//
	// /* row=sheet.createRow((short)3);
	// cell = row.createCell((short)0);
	// cell.setCellValue("Fecha:");
	// xls.azul_celda(cellStyle,cell);*/
	// //row=sheet.createRow((short)3);
	// //row.createCell((short)0).setCellValue("Fecha:");
	//
	//
	//
	// cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
	// row=sheet.createRow((short)3);
	// cell = row.createCell((short)0);
	// Date fecha = new Date();
	//
	// cell.setCellValue(rb.getString("scp.fecha")+":"+ToolsHTML.sdfShowWithoutHour.format(fecha));
	// // xls.azul_celda(cellStyle,cell);
	//
	//
	// row=sheet.createRow((short)5);
	// cell = row.createCell((short)0);
	// cell.setCellValue(rb.getString("cbs.name"));
	// xls.rojo_celda(cellStyle2,cell);
	// //row=sheet.createRow((short)4);
	// //row.createCell((short)0).setCellValue("Nro:");
	//
	//
	// row=sheet.createRow((short)5);
	// cell = row.createCell((short)1);
	// cell.setCellValue(rb.getString("doc.type"));
	// xls.rojo_celda(cellStyle2,cell);
	// //row=sheet.createRow((short)4);
	// //row.createCell((short)1).setCellValue("Contrato");
	//
	// row=sheet.createRow((short)5);
	// cell = row.createCell((short)2);
	// cell.setCellValue(rb.getString("doc.number"));
	// xls.rojo_celda(cellStyle2,cell);
	// //row=sheet.createRow((short)4);
	// //row.createCell((short)2).setCellValue("Concepto");
	//
	//
	// row=sheet.createRow((short)5);
	// cell = row.createCell((short)3);
	// cell.setCellValue(rb.getString("showDoc.Ver"));
	// xls.rojo_celda(cellStyle2,cell);
	// // row=sheet.createRow((short)4);
	// // row.createCell((short)3).setCellValue("N");
	//
	// row=sheet.createRow((short)5);
	// cell = row.createCell((short)4);
	// cell.setCellValue(rb.getString("cbs.owner"));
	// xls.rojo_celda(cellStyle2,cell);
	//
	// row=sheet.createRow((short)5);
	// cell = row.createCell((short)5);
	// cell.setCellValue(rb.getString("showDoc.approved"));
	// xls.rojo_celda(cellStyle2,cell);
	// // row=sheet.createRow((short)4);
	// // row.createCell((short)4).setCellValue("FECHA FACTURA");
	//
	// /*
	// row=sheet.createRow((short)4);
	// cell = row.createCell((short)5);
	// cell.setCellValue(rb.getString("cbs.owner"));
	// xls.rojo_celda(cellStyle2,cell);
	// // row=sheet.createRow((short)4);
	// // row.createCell((short)5).setCellValue("Dolares");
	//
	// row=sheet.createRow((short)4);
	// cell = row.createCell((short)6);
	// cell.setCellValue("Tipo Cambio");
	// xls.rojo_celda(cellStyle2,cell);
	// //row=sheet.createRow((short)4);
	// //row.createCell((short)6).setCellValue("TIPO CAMBIO");
	//
	//
	// row=sheet.createRow((short)4);
	// cell = row.createCell((short)7);
	// cell.setCellValue("Bolivares");
	// xls.rojo_celda(cellStyle2,cell);
	// //row=sheet.createRow((short)4);
	// //row.createCell((short)7).setCellValue("Bolivares");
	//
	// row=sheet.createRow((short)4);
	// cell = row.createCell((short)8);
	// cell.setCellValue("Instituci�n");
	// xls.rojo_celda(cellStyle2,cell);
	// //row=sheet.createRow((short)4);
	// //row.createCell((short)8).setCellValue("Nombre");
	//
	// row=sheet.createRow((short)4);
	// cell = row.createCell((short)9);
	// cell.setCellValue("Status");
	// xls.rojo_celda(cellStyle2,cell);
	// //row=sheet.createRow((short)4);
	// //row.createCell((short)9).setCellValue("Status");
	// row=sheet.createRow((short)4);
	// cell = row.createCell((short)10);
	// cell.setCellValue("Fecha de Pago");
	// xls.rojo_celda(cellStyle2,cell);
	//
	// row=sheet.createRow((short)4);
	// cell = row.createCell((short)11);
	// cell.setCellValue("A�o de Pago");
	// xls.rojo_celda(cellStyle2,cell);
	// */
	// }
	// catch(Exception e){
	// error="error: " + e;
	// throw new Exception(error);
	// }
	// }

	// private void azul_celda(HSSFCellStyle cellStyle,HSSFCell cell){
	// cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	// cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	// cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
	// cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
	//
	// cellStyle.setBottomBorderColor(HSSFColor.BLUE.index);
	// cellStyle.setLeftBorderColor(HSSFColor.BLUE.index);
	// cellStyle.setRightBorderColor(HSSFColor.BLUE.index);
	// cellStyle.setTopBorderColor(HSSFColor.BLUE.index);
	// cell.setCellStyle(cellStyle);
	// }
	//
	// private void orange_celda(HSSFCellStyle cellStyle,HSSFCell cell){
	// cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	// cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	// cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
	// cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
	//
	// cellStyle.setBottomBorderColor(HSSFColor.ORANGE.index);
	// cellStyle.setLeftBorderColor(HSSFColor.ORANGE.index);
	// cellStyle.setRightBorderColor(HSSFColor.ORANGE.index);
	// cellStyle.setTopBorderColor(HSSFColor.ORANGE.index);
	// cell.setCellStyle(cellStyle);
	// }
	//
	// private void rojo_celda(HSSFCellStyle cellStyle,HSSFCell cell){
	// cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	// cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	// cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
	// cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
	//
	// cellStyle.setBottomBorderColor(HSSFColor.RED.index);
	// cellStyle.setLeftBorderColor(HSSFColor.RED.index);
	// cellStyle.setRightBorderColor(HSSFColor.RED.index);
	// cellStyle.setTopBorderColor(HSSFColor.RED.index);
	// cell.setCellStyle(cellStyle);
	// }
	//
	//
	//
	//

}
