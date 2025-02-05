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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

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

/**
 * Created by IntelliJ IDEA. User: srodriguez Date: 03/04/2006 Time: 11:29:28 AM
 * To change this template use File | Settings | File Templates.
 */

public class CrearReporteSearch extends SuperAction {
	// private static ArrayList elementos =
	// ToolsHTML.getProperties("visor.OpenBrowser");
	// private POIFSFileSystem fs;
	private StringBuilder criterio = new StringBuilder();
	public String error;
	private static Logger log = LoggerFactory.getLogger(CrearReporteSearch.class
			.getName());
	private ResourceBundle rb = null;
	private String dateSystem = ToolsHTML.sdf.format(new Date());
	private HttpServletRequest request;
	private HashMap<Integer, String> mapCargo;

	/**
     * 
     */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);

		try {
			Locale defaultLocale = new Locale(
					DesigeConf.getProperty("language.Default"),
					DesigeConf.getProperty("country.Default"));
			rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);

			// int modeloReporte = 2;

			this.request = request;
			generarExcelModelo2(request, response);

			return null;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
		}
		return goError();
	}

	/**
	 * 
	 * @param label
	 * @param value
	 */
	private void addCriterio(String label, String value) {
		if (value != null && value.trim().length() > 0
				&& !value.trim().equals("0")) {
			criterio.append(criterio.length() > 8 ? " - " : "").append(label)
					.append("=[").append(value).append("]");
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
	 * @param id
	 * @return
	 */
	private String getTypesDocuments(String id) {
		if (request.getSession().getAttribute("typesDocuments") != null) {
			Collection cole = (Collection) request.getSession().getAttribute(
					"typesDocuments");
			for (Iterator ite = cole.iterator(); ite.hasNext();) {
				Search obj = (Search) ite.next();
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

			for (Iterator ite = cole.iterator(); ite.hasNext();) {
				Search obj = (Search) ite.next();
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
	private String getQuery(HttpServletRequest request)
			throws ApplicationExceptionChecked {
		try {
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
			String expiredTo = request.getParameter("expiredToHIDDEN");
			String expiredFrom = request.getParameter("expiredFromHIDDEN");
			BaseDocumentForm doc = null;

			criterio.setLength(0);
			criterio.append("Filtros:");
			addCriterio("Estatus", getTypeStatuSession(TypesStatus));
			addCriterio("Propietario", propietario);
			addCriterio("Version", version);
			addCriterio("Palabras", keys);
			addCriterio("Nombre", name);
			addCriterio("Numero", number);
			addCriterio("Tipo de Documento", getTypesDocuments(typeDoc));
			addCriterio("Propietario", owner);
			addCriterio("Prefijo", prefix);
			addCriterio("Norma ISO", normISO);
			// addCriterio("Publicado",publicDoc);
			addCriterio("Carpeta: ", idRout);
			addCriterio("Ruta: ", nameRout);

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

			doc = new BaseDocumentForm();

			// los datos para los nombre de los campos
			ConfDocumentoDAO oConfDocumentoDAO = new ConfDocumentoDAO();
			ArrayList lista = (ArrayList) oConfDocumentoDAO.findAll();
			if (lista != null) {
				doc.setListaCampos(lista);
				for (int i = 0; i < lista.size(); i++) {
					DocumentForm obj = (DocumentForm) lista.get(i);
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

		return request.getSession().getAttribute("querySearchReport")
				.toString();
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void generarExcelModelo2(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String path = ToolsHTML.getPath().concat("estilo");
		String nameFile = path.concat(File.separator).concat("FormatoModeloSearch.xls");
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
		HSSFCellStyle style9 = null;
		//ydavila Ticket 001-00-003265
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
				//ydavila Ticket 001-00-003265
				cell = fil.getCell((short) 8);
				if (cell != null) {
					style9 = cell.getCellStyle();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// Se Escribe la Informaci�n en el Documento
		Collection<Search> colNorma = HandlerNorms.getAllNorms();
		String sql_query = getQuery(request);
		fixCriterioForNorma(colNorma);

		int fila = 5;
		HSSFRow row;
		HSSFCell celda;
		// Estableciendo T�tulo del Reporte seg�n el Idioma del Usuario :D
		// Fila 2
		row = sheet.getRow(1);
		if (row != null) {
			// Columna 2 titulo
			celda = row.getCell((short) 0);
			celda.setCellValue(getMessage("pb.titleSearch") + " "
					+ getMessage("pb.to") + " "
					+ ToolsHTML.sdfShowWithoutHour.format(new java.util.Date()));
		}

		// Criterios seleccionados del reporte

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
		/*row = sheet.getRow(4);
		row.getCell((short) 0).setCellValue(getMessage("rep.tituloDoc"));
		//ydavila Ticket 001-00-003265
		//row.getCell((short) 1).setCellValue(getMessage("doc.type"));
		row.getCell((short) 1).setCellValue(getMessage("doc.typeLM"));
		//ydavila Ticket 001-00-003265
		//row.getCell((short) 2).setCellValue(getMessage("rep.codigo"));
		row.getCell((short) 2).setCellValue(getMessage("doc.numberLM"));
		//ydavila Ticket 001-00-003265
		//row.getCell((short) 3).setCellValue(getMessage("showDoc.Ver"));
		row.getCell((short) 3).setCellValue(getMessage("showDoc.VerLM"));
		row.getCell((short) 4).setCellValue(getMessage("showDoc.create"));
		row.getCell((short) 5).setCellValue(getMessage("showDoc.status"));
		row.getCell((short) 6).setCellValue(getMessage("sch.owner"));
		row.getCell((short) 7).setCellValue("Norma");
		//ydavila Ticket 001-00-003265
		row.getCell((short) 8).setCellValue("Comentarios");*/
		//ydavila Ticket 001-00-003265
		row = sheet.getRow(4);
		row.getCell((short) 0).setCellValue(getMessage("doc.numberLM"));
		//row.getCell((short) 3).setCellValue(getMessage("showDoc.Ver"));
		row.getCell((short) 1).setCellValue(getMessage("showDoc.VerLM"));
		row.getCell((short) 2).setCellValue(getMessage("showDoc.create"));
		//row.getCell((short) 1).setCellValue(getMessage("doc.type"));
		row.getCell((short) 3).setCellValue(getMessage("doc.typeLM"));
		row.getCell((short) 4).setCellValue(getMessage("rep.tituloDoc"));
		row.getCell((short) 5).setCellValue("Comentarios");
		//row.getCell((short) 2).setCellValue(getMessage("rep.codigo"));
		row.getCell((short) 6).setCellValue(getMessage("showDoc.status"));
		row.getCell((short) 7).setCellValue(getMessage("sch.owner"));
		row.getCell((short) 8).setCellValue("Norma");
		log.debug("sql_query: " + sql_query);
		
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			ps = con.prepareStatement(JDBCUtil.replaceCastMysql(sql_query));
			rs = ps.executeQuery();
	
			Collection documents = (Collection) request.getSession().getAttribute(
					"searchDocs");
			StringBuilder sbNorma = new StringBuilder("");
	
			while (rs.next()) {
				// Chequea si el documento esta seleccionado o no ya que la consulta
				// se trae todos los documentos (pesimo!!!!)
	
				if (!existDocument(documents, rs.getLong("numgen"))) {
					continue;
				}
	
				row = sheet.createRow((short) fila);
				// Valor y estilo si Aplica en la Columna # 1
				celda = row.createCell((short) 0);
				if (style1 != null) {
					celda.setCellStyle(style1);
				}
				String correlativo = ToolsHTML.isEmptyOrNull(
						rs.getString("prefix"), "").concat(
						ToolsHTML.isEmptyOrNull(rs.getString("number"), ""));
				if (!ToolsHTML.isEmptyOrNull(correlativo)) {
					celda.setCellValue(correlativo);
				} else {
					if (!ToolsHTML.isEmptyOrNull(rs.getString("number"))) {
						celda.setCellValue(rs.getString("number"));
					} else {
						celda.setCellValue(rs.getString("prefix"));
					}
				}
				// Valor y estilo si Aplica en la Columna Versi�n
							String mayorver = "0";
							String minorver = "0";
							try {
								mayorver = rs.getString("MayorVer") != null ? rs.getString(
										"MayorVer").trim() : "";
								minorver = rs.getString("MinorVer") != null ? rs.getString(
										"MinorVer").trim() : "";
							} catch (java.sql.SQLException e) {
	
							}
							celda = row.createCell((short) 1);
							if (style2 != null) {
								celda.setCellStyle(style2);
							}
							celda.setCellValue(mayorver + "." + minorver);
							// Valor y estilo si Aplica en la Columna  
							celda = row.createCell((short) 2);
							if (style3 != null) {
								celda.setCellStyle(style3);
							}
							celda.setCellValue(ToolsHTML.sdfShowWithoutHour.format(rs
									.getTimestamp("dateCreation")));
	
							// Valor y estilo si Aplica en la Columna  
							celda = row.createCell((short) 3);
	
							celda.setCellValue(rs.getString("typeDoc"));
							// Valor y estilo si Aplica en la Columna # 3
							celda = row.createCell((short) 4);
							if (style4 != null) {
								celda.setCellStyle(style4);
							}
							
							celda.setCellValue(rs.getString("nameDocument"));
							// Valor y estilo si Aplica en la Columna #Nombre
							celda = row.createCell((short) 5);
							if (style2 != null) {
								celda.setCellStyle(style2);
							}
	
							String estatus=request.getParameter("TypesStatus");
							if (request.getParameter("TypesStatus").equals("5")) {
								estatus="Aprobado";
							}else{
								estatus="Borrador";
							}
							//ydavila Ticket 001-00-003265
							celda.setCellValue(HandlerDocuments.getDescriptCommentExcel1(con, rs.getString("numgen"),estatus));
							celda = row.createCell((short) 6);
							
							String statu = ToolsHTML.getStatusDocumento(rs.getString("statu"),
									rs.getString("statuVer"), rb, dateSystem,
									rs.getString("dateExpiresDrafts"),
									rs.getString("dateExpires"));
							if (!ToolsHTML.isEmptyOrNull(statu)) {
								if (style6 != null) {
									celda.setCellStyle(style6);
								}
								celda.setCellValue(statu);
							} else {
								celda.setCellValue("");
							}
							celda = row.createCell((short) 7);
	
							// Valor y estilo si Aplica en la Columna # 7
							//celda = row.createCell((short) 6);
							if (style5 != null) {
								celda.setCellStyle(style5);
							}
							celda.setCellValue(rs.getString("nombre"));
	
							celda = row.createCell((short) 8);
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
	
	
							fila++;
	
	
			}
			// Enviando el Archivo al Cliente
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition",
					"attachment; filename=ListaDocumentos.xls");
			response.setHeader("content-transfer-encoding", "binary");
			OutputStream out = response.getOutputStream();
			workbook.write(out);
			out.flush();
			out.close();
		} finally {
			JDBCUtil.closeQuietly(con, ps, rs);
		}
	}
}
