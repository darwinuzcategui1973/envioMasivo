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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.managers.HandlerSacop;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.sacop.actions.LoadSacop;
import com.desige.webDocuments.sacop.forms.Plantilla1BD;
import com.desige.webDocuments.sacop.forms.plantilla1;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qweb.dao.ActionRecommendedDAO;
import com.focus.qweb.to.ActionRecommendedTO;
import com.focus.util.PerfilAdministrador;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 26/05/2006
 * Time: 03:07:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class CrearReporteSacop extends SuperAction {
	private static final Logger log = LoggerFactory.getLogger(CrearReporte.class.getName());
	private static ArrayList<String> elementos = ToolsHTML.getProperties("visor.OpenBrowser");
	private POIFSFileSystem fs;
	public String error;
	
	public ActionForward execute(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		super.init(mapping,form,request,response);
		try {
			if (ToolsHTML.isEmptyOrNull(request.getParameter("conjuntosacop"))){
				generarExcel(request,response);
			}else{
				String variasSacop=request.getParameter("conjuntosacop")!=null?request.getParameter("conjuntosacop"):"";
				generarExcelDetalle(request,response,variasSacop.trim().toString());
			}
			
			return null;
		} catch(Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return goError();
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void generarExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String path = ToolsHTML.getPath();
		path = path.concat("estilo");
		String nameFile = path.concat(File.separator).concat("FormatoSacop.xls");
		response.setContentType("application/vnd.ms-excel");
		InputStream inputfile = new FileInputStream(nameFile);
		HSSFWorkbook workbook = new HSSFWorkbook(inputfile,true);
		HSSFSheet sheet = workbook.getSheetAt(0);
		ActionRecommendedDAO action = new ActionRecommendedDAO();
		Map<String,ActionRecommendedTO> listaAction = action.listarOrderActionRecommendedAlls("");

		HSSFCellStyle style1 = null;
		HSSFCellStyle style2 = null;
		HSSFCellStyle style3 = null;
		HSSFCellStyle style4 = null;
		HSSFCellStyle style5 = null;
		HSSFCellStyle style6 = null;
		HSSFCellStyle style7 = null;
		HSSFCellStyle style8 = null;
		HSSFCellStyle style9 = null;
		HSSFCellStyle style10 = null;
		HSSFCellStyle style11 = null;
		HSSFCellStyle style12 = null;
		HSSFCellStyle styleTextoAjustado = null;
		
		short celdaIndex = 0;
		try {
			HSSFRow fil = sheet.getRow(14);
			if (fil!=null) {
				HSSFCell cell = fil.getCell((short) celdaIndex++);
				if (cell!=null) {
					style1 = cell.getCellStyle();
				}
				cell = fil.getCell((short) celdaIndex++);
				if (cell!=null) {
					style2 = cell.getCellStyle();
				}
				cell = fil.getCell((short) celdaIndex++);
				if (cell!=null) {
					style3 = cell.getCellStyle();
				}
				cell = fil.getCell((short) celdaIndex++);
				if (cell!=null) {
					style4 = cell.getCellStyle();
				}
				cell = fil.getCell((short) celdaIndex++);
				if (cell!=null) {
					style5 = cell.getCellStyle();
				}
				cell = fil.getCell((short) celdaIndex++);
				if (cell!=null) {
					style6 = cell.getCellStyle();
				}
				cell = fil.getCell((short) celdaIndex++);
				if (cell!=null) {
					style7 = cell.getCellStyle();
				}
				cell = fil.getCell((short) celdaIndex++);
				if (cell!=null) {
					style8 = cell.getCellStyle();
				}
				cell = fil.getCell((short) celdaIndex++);
				if (cell!=null) {
					style9 = cell.getCellStyle();
				}
				cell = fil.getCell((short) celdaIndex++);
				if (cell!=null) {
					style10 = cell.getCellStyle();
				}
				cell = fil.getCell((short) celdaIndex++);
				if (cell!=null) {
					style11 = cell.getCellStyle();
				}
				cell = fil.getCell((short) celdaIndex++);
				if (cell!=null) {
					style12 = cell.getCellStyle();
				}
				cell = fil.getCell((short) 18);
				if (cell!=null) {
					styleTextoAjustado = cell.getCellStyle();
				}
			}
		} catch (Exception ex) {
			log.error("Error: " + ex.getLocalizedMessage(), ex);
		}
		
		long t0 = System.currentTimeMillis();
		
		//Se Escribe la Informaci�n en el Documento
		Users usuario = getUserSession();
		boolean userIsLikeAnAdmin = PerfilAdministrador.userIsInAdminGroup(usuario);
		
		// se sustituye este query por el que viene del filtro y que esta en una variable de session
		/*
		StringBuffer sql_query = new StringBuffer(100);
		sql_query.append("SELECT tbl_s.idplanillasacop1, tbl_s.sacopnum, em.nombres AS enombre, em.apellidos AS eapellidos, ");
		sql_query.append("re.nombres AS rnombre, re.apellidos AS rapellidos, ");
		sql_query.append("tbl_area.area, tbl_s.estado, tbl_tp.titulosplanillas, tbl_s.fechaemision, ");
		sql_query.append("tbl_s.descripcion, procesosafectados, eliminarcausaraiz, fechaWhenDiscovered, tbl_s.correcpreven ");
		sql_query.append(", tbl_c.descripcion AS clasificacion, tbl_s.fechareal, tbl_s.noconformidades, tbl_s.accionesrecomendadas ");
		sql_query.append(", tbl_s.fecha_verificacion, tbl_s.fecha_cierre ");
		sql_query.append("FROM tbl_planillasacop1 tbl_s left join tbl_clasificacionplanillassacop tbl_c on tbl_c.id = tbl_s.clasificacion ");
		sql_query.append("left outer join person re on re.idperson = tbl_s.respblearea,");
		sql_query.append("person em, tbl_area tbl_area, tbl_cargo tbl_cargo, tbl_titulosplanillassacop tbl_tp "); 
		sql_query.append("WHERE em.idperson=tbl_s.emisor ");
		sql_query.append("AND tbl_cargo.idcargo = tbl_s.usernotificado ");
		sql_query.append("AND tbl_area.idarea = tbl_cargo.idarea ");
		sql_query.append("AND tbl_s.origensacop=tbl_tp.numtitulosplanillas ");
		sql_query.append("AND tbl_s.active=1 ");//solo las sacop activas van al reporte
		
		//observamos los filtros provenientes de la pagina
		//vemos de que bandeja viene
		String bandeja = request.getParameter("bandejaReporte");
		if("pendiente".equals(bandeja)){
			sql_query.append("AND (tbl_s.estado='").append(LoadSacop.edoPendienteVerifSeg).append("'");
			sql_query.append(" OR tbl_s.estado='").append(LoadSacop.edoVerificacion).append("'");
			sql_query.append(" OR tbl_s.estado='").append(LoadSacop.edoVerificado).append("'");
			sql_query.append(" OR tbl_s.estado='").append(LoadSacop.edoEnEjecucion).append("'");
			sql_query.append(" OR tbl_s.estado='").append(LoadSacop.edoEmitida).append("'");
			sql_query.append(" OR tbl_s.estado='").append(LoadSacop.edoBorrador).append("'");
			sql_query.append(" OR tbl_s.estado='").append(LoadSacop.edoAprobado).append("') ");
		} else if("rechazado".equals(bandeja)){
			sql_query.append("AND tbl_s.estado='").append(LoadSacop.edoRechazado).append("' ");
		} else if("cerrado".equals(bandeja)){
			sql_query.append("AND tbl_s.estado='").append(LoadSacop.edoCerrado).append("' ");
		} else if("buscar".equals(bandeja)){
			String extraFilters = "";
			if(request.getParameter("idSacopReporte") != null && ! "".equals(request.getParameter("idSacopReporte"))){
				extraFilters += "AND tbl_s.sacopnum LIKE UPPER('%" + request.getParameter("idSacopReporte").toUpperCase() + "%') ";
			}
			if(request.getParameter("emisorSacopReporte") != null && ! "".equals(request.getParameter("emisorSacopReporte"))){
				extraFilters += "AND em.nameuser = '" + request.getParameter("emisorSacopReporte") + "' ";
			}
			if(request.getParameter("responsableSacopReporte") != null && ! "".equals(request.getParameter("responsableSacopReporte"))){
				extraFilters += "AND re.nameuser = '" + request.getParameter("responsableSacopReporte") + "' ";
			}
			if(request.getParameter("areaResponsableSacopReporte") != null && ! "".equals(request.getParameter("areaResponsableSacopReporte"))){
				extraFilters += "AND tbl_area.idarea = '" + request.getParameter("areaResponsableSacopReporte") + "' ";
			}
			if(request.getParameter("efectivaSacopReporte") != null && ! "".equals(request.getParameter("efectivaSacopReporte"))){
				extraFilters += "AND tbl_s.eliminarcausaraiz = '" + request.getParameter("efectivaSacopReporte") + "' ";
			}
			if(request.getParameter("estadoSacopReporte") != null && ! "".equals(request.getParameter("estadoSacopReporte"))){
				extraFilters += "AND tbl_s.estado = " + request.getParameter("estadoSacopReporte");
			}
			if(request.getParameter("fechaEmisionSacopReporte") != null && ! "".equals(request.getParameter("fechaEmisionSacopReporte"))){
				String emision = ToolsHTML.date.format(ToolsHTML.sdfShowWithoutHour.parse(request.getParameter("fechaEmisionSacopReporte")));
				extraFilters += "AND tbl_s.fechaemision >= '" + emision + " 00:00:00' " ;
				extraFilters += "AND tbl_s.fechaemision <= '" + emision + " 23:59:59' " ;
			}
			
			sql_query.append(extraFilters);
		} else {
			//no tenemos ninguna bandeja en el formulario
			//no observamos nada entonces
			sql_query.append(" AND 1 = 0 ");
		}
		
		if(! userIsLikeAnAdmin){
			//como el usuario no pertenece al grupo de administradores, solo podra ver las sacops en las que este relacionado
			log.info("El usuario " + usuario.getNameUser() + ", no es del grupo administrador, se le filtraran las sacops.");
			sql_query.append(" AND (em.idperson = ").append(usuario.getIdPerson());
			sql_query.append("    OR re.idperson = ").append(usuario.getIdPerson());
			sql_query.append("    OR tbl_s.solicitudinforma = '").append(usuario.getIdPerson()).append("'");
			sql_query.append("    OR tbl_s.solicitudinforma LIKE '%,").append(usuario.getIdPerson()).append("'");
			sql_query.append("    OR tbl_s.solicitudinforma LIKE '").append(usuario.getIdPerson()).append(",%'");
			sql_query.append("    OR tbl_s.solicitudinforma LIKE '%,").append(usuario.getIdPerson()).append(",%') ");
		}
		
		sql_query.append(" ORDER BY tbl_s.estado, tbl_s.origensacop, tbl_s.fechaemision");
		//System.out.println(sql_query);

		Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		//PreparedStatement ps = con.prepareStatement(JDBCUtil.replaceCastMysql(sql_query.toString()));
		*/
		
		Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		// Ejecutamos el query que esta en memoria si esta no ejecutamos nada
		String query = (String)request.getSession().getAttribute("LAST_QUERY_SEARCH_SACOP");

		ResultSet rs = null;
		PreparedStatement ps = null;
		if(query!=null && query.trim().length()>0) {
			ps = con.prepareStatement(JDBCUtil.replaceCastMysql(query));
			rs = ps.executeQuery();
		}
		
		log.info("Query del reporte ejecutado en " + (System.currentTimeMillis() - t0) + " ms.");
		
		int fila = 14;
		HSSFRow row;
		HSSFCell celda;
		//Estableciendo T�tulo del Reporte seg�n el Idioma del Usuario :D
		row = sheet.getRow(1);
		if (row!=null) {
			celda = row.getCell((short)0);
			String value = getMessage("scp.listado") +  " " + getMessage("pb.to") + " "	+ ToolsHTML.sdfShowWithoutHour.format(new java.util.Date());
			// se sustituyen todos los valueOf( por new StringValue( ya que generar error en el ex
			//celda.setCellValue(valueOf(value));
			celda.setCellValue(valueOf(value));
		}
		
		//T�tulos de la Tabla
		row = sheet.getRow(fila -1);
		celdaIndex = 0;
		
		row.getCell((short) celdaIndex++).setCellValue(valueOf(getMessage("scp.num")));
		row.getCell((short) celdaIndex++).setCellValue(valueOf(getMessage("scp.mailc2")));
		row.getCell((short) celdaIndex++).setCellValue(valueOf(getMessage("scp.areafectada")));
		row.getCell((short) celdaIndex++).setCellValue(valueOf(getMessage("scp.estado")));
		row.getCell((short) celdaIndex++).setCellValue(valueOf(getMessage("scp.num")));
		row.getCell((short) celdaIndex++).setCellValue(valueOf(getMessage("scp.emisor")));
		row.getCell((short) celdaIndex++).setCellValue(valueOf(getMessage("scp.responsable")));
		row.getCell((short) celdaIndex++).setCellValue(valueOf(getMessage("scp.orgsacop")));
		row.getCell((short) celdaIndex++).setCellValue(valueOf(getMessage("scp.fechae")));
		row.getCell((short) celdaIndex++).setCellValue(valueOf(getMessage("scp.fechaWhenDiscovered")));
		row.getCell((short) celdaIndex++).setCellValue(valueOf(getMessage("scp.fueefectiva")));
		row.getCell((short) celdaIndex++).setCellValue(valueOf(getMessage("scp.desc")));
		row.getCell((short) celdaIndex++).setCellValue(valueOf(getMessage("scp.prcafectados")));
		row.getCell((short) celdaIndex++).setCellValue(valueOf(getMessage("scp.clasificacionsacop")));
		row.getCell((short) celdaIndex++).setCellValue(valueOf(getMessage("scp.fechareal")));
		row.getCell((short) celdaIndex++).setCellValue(valueOf(getMessage("scp.fechaverificacion")));
		row.getCell((short) celdaIndex++).setCellValue(valueOf(getMessage("scp.fechacierre")));
		row.getCell((short) celdaIndex++).setCellValue(valueOf(getMessage("scp.noconformidades")));
		row.getCell((short) celdaIndex++).setCellValue(valueOf(getMessage("scp.acrecomendadas")));
		row.getCell((short) celdaIndex++).setCellValue(valueOf(getMessage("scp.acciones")));
		
		int cont = 0;
		int contBorrador = 0;
		int contEmitido  = 0;
		int contAprobado = 0;
		int contEnEjecucion = 0;
		int edoVerificacion = 0;
		int edoVerificado = 0;
		int edoCerrado = 0;
		int edoRechazado = 0;
		celdaIndex = 0;
		
		Hashtable<String,String> unicaPlanilla = new Hashtable<String,String>();
		String planillasacop = null;
		while (rs!=null && rs.next()) {
			
			planillasacop = rs.getString("idplanillasacop1") != null ? rs.getString("idplanillasacop1") : "";
			if (unicaPlanilla.containsKey(planillasacop)) {
				continue;
			}
			unicaPlanilla.put(planillasacop, "usuarioLoad");
			
			celdaIndex = 0;
			row = sheet.createRow((short)fila);
			//Valor y estilo si Aplica en la Columna # 1
			celda = row.createCell((short) celdaIndex++);
			if (style1!=null) {
				celda.setCellStyle(style1);
			}
			cont++;
			celda.setCellValue(cont);
			
			//tipo de SACOP
			celda = row.createCell((short) celdaIndex++);
			if (style1!=null) {
				celda.setCellStyle(style1);
			}
			
			try{
				celda.setCellValue(valueOf(getMessage("scp.actionType".concat(rs.getString("correcpreven")))));
			}catch(Exception e){
				celda.setCellValue(valueOf(rs.getString("correcpreven")));
			}
			
			
			//Valor y estilo si Aplica en la Columna # 2
			celda = row.createCell((short) celdaIndex++);
			if (style2!=null) {
				celda.setCellStyle(style2);
			}
			celda.setCellValue(valueOf(rs.getString("area") != null ? rs.getString("area").trim() : ""));
			
			//Valor y estilo si Aplica en la Columna # 3
			celda = row.createCell((short) celdaIndex++);
			if (style3!=null) {
				celda.setCellStyle(style3);
			}
			String estado = rs.getString("estado");
			celda.setCellValue(valueOf(obtenerEdoBorrador(rs.getString("estado"))));

			if (LoadSacop.edoBorrador.equalsIgnoreCase(estado)) {
				++contBorrador;
			} else if (LoadSacop.edoEmitida.equalsIgnoreCase(estado)) {
				++contEmitido;
			} else  if (LoadSacop.edoAprobado.equalsIgnoreCase(estado)) {
				++contAprobado;
			} else if (LoadSacop.edoEnEjecucion.equalsIgnoreCase(estado)) {
				++contEnEjecucion;
			} else if (LoadSacop.edoPendienteVerifSeg.equalsIgnoreCase(estado)) {

			} else if (LoadSacop.edoVerificacion.equalsIgnoreCase(estado)) {
				++edoVerificacion;
			} else if (LoadSacop.edoVerificado.equalsIgnoreCase(estado)) {
				++edoVerificado;
			} else if (LoadSacop.edoCerrado.equalsIgnoreCase(estado)) {
				++edoCerrado;
			} else if (LoadSacop.edoRechazado.equalsIgnoreCase(estado)) {
				++edoRechazado;
			}

			//Valor y estilo si Aplica en la Columna # 4
			celda = row.createCell((short) celdaIndex++);
			if (style4!=null) {
				celda.setCellStyle(style4);
			}
			celda.setCellValue(valueOf(rs.getString("sacopnum")));
			//Valor y estilo si Aplica en la Columna # 5
			celda = row.createCell((short) celdaIndex++);
			if (style5!=null) {
				celda.setCellStyle(style5);
			}
			String emisornom  = rs.getString("enombre")!=null?rs.getString("enombre"):"";
			String emisornom1 = rs.getString("eapellidos")!=null?rs.getString("eapellidos"):"";
			celda.setCellValue(valueOf(emisornom + " " + emisornom1));
			//Valor y estilo si Aplica en la Columna # 6
			celda = row.createCell((short) celdaIndex++);
			String responsablenom=rs.getString("rnombre")!=null?rs.getString("rnombre"):"";
			String responsablenom1=rs.getString("rapellidos")!=null?rs.getString("rapellidos"):"";
			if (style6!=null) {
				celda.setCellStyle(style6);
			}
			celda.setCellValue(valueOf(responsablenom + " " + responsablenom1));

			//Valor y estilo si Aplica en la Columna # 7
			celda = row.createCell((short) celdaIndex++);
			if (style7!=null) {
				celda.setCellStyle(style7);
			}
			celda.setCellValue(valueOf(rs.getString("titulosplanillas")));

			//Valor y estilo si Aplica en la Columna # 8
			celda = row.createCell((short) celdaIndex++);
			if (style8!=null) {
				celda.setCellStyle(style8);
			}
			celda.setCellValue(valueOf(ToolsHTML.formatDateShow(rs.getString("fechaemision"), false)));

			//Valor y estilo si Aplica en la Columna # 9
			celda = row.createCell((short) celdaIndex++);
			if (style9!=null) {
				celda.setCellStyle(style9);
			}
			celda.setCellValue(valueOf(ToolsHTML.formatDateShow(rs.getString("fechaWhenDiscovered"), false)));
			
			//Valor y estilo si Aplica en la Columna # 10
			celda = row.createCell((short) celdaIndex++);
			if (style10!=null) {
				celda.setCellStyle(style10);
			}
			if("0".equals(rs.getString("eliminarcausaraiz"))){
				celda.setCellValue(valueOf(getMessage("scp.si")));
			}else if("1".equals(rs.getString("eliminarcausaraiz"))) {
				celda.setCellValue(valueOf(getMessage("scp.no")));
			}else {
				celda.setCellValue(valueOf(getMessage("scp.pendiente")));
			}
			
			//Valor y estilo si Aplica en la Columna # 11
			celda = row.createCell((short) celdaIndex++);
			if (style11!=null) {
				celda.setCellStyle(style11);
			}
			celda.setCellValue(valueOf(rs.getString("descripcion")));
			
			Collection<Search> procesosSacop = HandlerProcesosSacop.getProcesosSacop(rs.getString("procesosafectados"));
			Iterator<Search> it = procesosSacop.iterator();
			StringBuffer proceso = new StringBuffer("");
			while(it.hasNext()) {
				Search bean = it.next();
				proceso.append(bean.getDescript());
				if (it.hasNext()){
					proceso.append(",").append("  ");
				} else {
					proceso.append("");
				}
			}
			//Valor y estilo si Aplica en la Columna # 12
			celda = row.createCell((short) celdaIndex++);
			if (style12!=null) {
				celda.setCellStyle(style12);
			}
			celda.setCellValue(valueOf(proceso.toString()));
			
			//Valor y estilo si Aplica en la Columna # 12
			celda = row.createCell((short) celdaIndex++);
			if (style12!=null) {
				celda.setCellStyle(style12);
			}
			celda.setCellValue(valueOf(rs.getString("clasificacion")));
			
			celda = row.createCell((short) celdaIndex++);
			if (style12!=null) {
				celda.setCellStyle(style12);
			}
			celda.setCellValue(valueOf(rs.getString("fechareal")));
			
			celda = row.createCell((short) celdaIndex++);
			if (style12!=null) {
				celda.setCellStyle(style12);
			}
			celda.setCellValue(valueOf(rs.getString("fecha_verificacion")));
			
			celda = row.createCell((short) celdaIndex++);
			if (style12!=null) {
				celda.setCellStyle(style12);
			}
			celda.setCellValue(valueOf(rs.getString("fecha_cierre")));
			
			celda = row.createCell((short) celdaIndex++);
			if (styleTextoAjustado!=null) {
				celda.setCellStyle(styleTextoAjustado);
			}
			celda.setCellValue(valueOf(rs.getString("noconformidades")));
			
			celda = row.createCell((short) celdaIndex++);
			if (styleTextoAjustado!=null) {
				celda.setCellStyle(styleTextoAjustado);
			}
			celda.setCellValue(valueOf(getAccionesrecomendadasString(listaAction, rs.getString("accionesrecomendadas"))));
			
			celda = row.createCell((short) celdaIndex++);
			if (styleTextoAjustado!=null) {
				celda.setCellStyle(styleTextoAjustado);
			}
			celda.setCellValue(valueOf(getDetalleAcciones(rs.getInt("idplanillasacop1"))));
			
			fila++;
		}
		//Estableciendo Estad�sticas
		//Borrador
		int rowCounter = 4;
		row = sheet.getRow(rowCounter);
		short indexCountValue = 7;
		row.getCell(indexCountValue).setCellValue(contBorrador);
		//Emitido
		row = sheet.getRow(++rowCounter);
		row.getCell(indexCountValue).setCellValue(contEmitido);
		//Aprobado
		row = sheet.getRow(++rowCounter);
		row.getCell(indexCountValue).setCellValue(contAprobado);
		//verificado
		row = sheet.getRow(++rowCounter);
		row.getCell(indexCountValue).setCellValue(edoVerificado);
		//Verificaci�n
		row = sheet.getRow(++rowCounter);
		row.getCell(indexCountValue).setCellValue(edoVerificacion);
		//En Ejecuci�n
		row = sheet.getRow(++rowCounter);
		row.getCell(indexCountValue).setCellValue(contEnEjecucion);
		//Cerrado
		row = sheet.getRow(++rowCounter);
		row.getCell(indexCountValue).setCellValue(edoCerrado);
		//Rechazado
		row = sheet.getRow(++rowCounter);
		row.getCell(indexCountValue).setCellValue(edoRechazado);
		//Enviando el Archivo al Cliente
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment; filename=reporteSACOP.xls");
		response.setHeader("content-transfer-encoding", "binary");
		OutputStream out = response.getOutputStream();
		workbook.write(out);
		out.close();
		try {
			if (con!=null) {
				con.close();
			}
			if (ps!=null){
				ps.close();
			}
			if(rs!=null){
				rs.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	public String getAccionesrecomendadasString(Map<String,ActionRecommendedTO> lista, String accionesrecomendadas) {
		StringBuffer sb = new StringBuffer();
		
		if(accionesrecomendadas!=null) {
			String[] arr = accionesrecomendadas.split(",");
			String sep = "";
			String coma = ",";
			
			for (int i = 0; i < arr.length; i++) {
				if(lista.containsKey(arr[i])) {
					sb.append(sep).append(lista.get(arr[i]).getDescActionRecommended());
					sep = coma;
				}
			}
		}
		
		return sb.toString();
	}
	

	/**
	 * 
	 * @param request
	 * @param response
	 * @param conjuntosacop
	 * @throws Exception
	 */
	public void generarExcelDetalle(HttpServletRequest request,HttpServletResponse response,String conjuntosacop) throws Exception {
		Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"),DesigeConf.getProperty("country.Default"));
		ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",defaultLocale);
		String path = ToolsHTML.getPath().concat("estilo");
		String nameFile = path.concat(File.separator).concat("DetallesFormatoSacop.xls");
		response.setContentType("application/vnd.ms-excel");
		InputStream inputfile = new FileInputStream(nameFile);
		HSSFWorkbook workbook = new HSSFWorkbook(inputfile,true);
		boolean showCharge=false;
		plantilla1 forma = new plantilla1();
		String usuarioVerificacion="";
		StringTokenizer cuantasSacop = new StringTokenizer(conjuntosacop.toString(),",;:");
		int fila=15;
		int hoja=0;
		ActionRecommendedDAO action = new ActionRecommendedDAO();
		Map<String,ActionRecommendedTO> listaAction = action.listarOrderActionRecommendedAlls("");

		while(cuantasSacop.hasMoreTokens()){
			String codigoSacop=(String)cuantasSacop.nextToken();
			HSSFSheet sheet = workbook.getSheetAt(hoja);
			workbook.setSheetName(hoja,codigoSacop);
			HSSFCellStyle style1 = null;
			HSSFCellStyle style2 = null;
			try {
				HSSFRow fil = sheet.getRow(15);
				if (fil!=null) {
					HSSFCell cell = fil.getCell((short)1);
					if (cell!=null) {
						style1 = cell.getCellStyle();
					}
					cell = fil.getCell((short)2);
					if (cell!=null) {
						style2 = cell.getCellStyle();
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			fila = 15;
			HSSFRow row;
			HSSFCell celda;
			row = sheet.getRow(1);
			if (row!=null) {
				celda = row.getCell((short)0);
				celda.setCellValue(valueOf(getMessage("scp.num") +  " " 
						+ codigoSacop + " " + ToolsHTML.sdfShowWithoutHour.format(new java.util.Date())));
			}
			boolean esAuditoria_o_Wonderware=false;
			Collection responsable = HandlerProcesosSacop.getInfResponsable("idplanillasacop1",codigoSacop,false,true,esAuditoria_o_Wonderware);

			Iterator it = responsable.iterator();
			if(it.hasNext()){
				Plantilla1BD obj = (Plantilla1BD) it.next();
				String id=obj.getProcesosafectados();
				String fechaEmision=ToolsHTML.sdfShowWithoutHour.format(obj.getFechaemision());
				Collection procesosSacop = HandlerProcesosSacop.getProcesosSacop(id);
				Collection norms =null;
				if (!ToolsHTML.isEmptyOrNull(obj.getRequisitosaplicable())){
					norms = HandlerProcesosSacop.getRequisitosAplicableSacop("idNorm",obj.getRequisitosaplicable());
				}
				Collection usuarios = HandlerDBUser.getAllUsersFilter(null,showCharge);
				Collection usuarios1 =HandlerProcesosSacop.getSolicitudinforma("idPerson",obj.getSolicitudinforma());// HandlerDBUser.getAllUsersFilter(null);
				plantilla1 formaResponsable = new plantilla1();
				usuarioVerificacion=String.valueOf(obj.getEmisor());
				forma.setSacopnum(obj.getSacopnum());
				forma.setUser(HandlerDocuments.getField("nameUser","person","idperson",String.valueOf(obj.getEmisor()),"=",1,Thread.currentThread().getStackTrace()[1].getMethodName()));
				forma.setUsernotificadotxt(obj.getUsernotificadotxt());
				HandlerSacop.load(forma);
				formaResponsable.setUser(HandlerDocuments.getField("nameUser","person","idperson",String.valueOf(obj.getRespblearea()),"=",1,Thread.currentThread().getStackTrace()[1].getMethodName()));
				String areaAfectada=HandlerProcesosSacop.findAreaByUsuario(String.valueOf(obj.getUsernotificado()));
				formaResponsable.setAreafectadatxt(areaAfectada);
				HandlerSacop.load(formaResponsable);
				formaResponsable.setAccionobservaciontxt(HandlerProcesosSacop.obtenerDatosResponsablesAccionesSacop(String.valueOf(obj.getIdplanillasacop1())));
				formaResponsable.setAccionobservaciontxt(formaResponsable.getAccionobservaciontxt()+" "+" "+rb.getString("scp.observacion")+": "+obj.getComntresponsablecerrar());
				formaResponsable.setRechazoapruebo(obj.getRechazoapruebo());
				formaResponsable.setNumerodeaccion(request.getParameter("tomarAcciones"));
				formaResponsable.setNoaceptada(obj.getNoaceptada());
				formaResponsable.setEdodelsacop(String.valueOf(obj.getEstado()));
				formaResponsable.setEdodelsacoptxt(obtenerEdoBorrador(formaResponsable.getEdodelsacop()));
				formaResponsable.setOrigensacop(String.valueOf(obj.getOrigensacop()));
				formaResponsable.setOrigensacoptxt(obtenerOrigenSacop(formaResponsable.getOrigensacop()));
				formaResponsable.setCorrecpreven(obj.getCorrecpreven());
				formaResponsable.setDescripcion(obj.getDescripcion());
				formaResponsable.setCausasnoconformidad(obj.getCausasnoconformidad());
				formaResponsable.setAccionrecomendada(getAccionesrecomendadasString(listaAction, obj.getAccionesrecomendadas()));
				formaResponsable.setIdplanillasacop1(String.valueOf(obj.getIdplanillasacop1()));
				formaResponsable.setAccionobservacion(obj.getAccionobservacion());
				if (!ToolsHTML.isEmptyOrNull(obj.getFechaEstimada().toString())){
					formaResponsable.setFechaEstimada(ToolsHTML.sdfShowWithoutHour.format(ToolsHTML.date.parse(obj.getFechaEstimada().toString())));
				}
				if (!ToolsHTML.isEmptyOrNull(obj.getFechaReal().toString())){
					formaResponsable.setFechaReal(ToolsHTML.sdfShowWithoutHour.format(ToolsHTML.date.parse(obj.getFechaReal().toString())));
				}
				formaResponsable.setAccionesEstablecidas(obj.getAccionesEstablecidas());
				formaResponsable.setAccionesEstablecidastxt(obj.getAccionesEstablecidastxt());
				formaResponsable.setEliminarcausaraiz(obj.getEliminarcausaraiz());
				formaResponsable.setEliminarcausaraiztxt(obj.getEliminarcausaraiztxt());
				String nom=".";
				if (formaResponsable.getNombres()!=null){
					nom=formaResponsable.getNombres()+"";
				}
				String apell="";
				if (!ToolsHTML.isEmptyOrNull(formaResponsable.getApellidos())){
					apell=formaResponsable.getApellidos()+"";
				}
				if  (nom!=null)
					//System.out.println("nom = " + nom);
				if  (apell!=null){}
				nom +=" "+apell;
				sheet = workbook.getSheetAt(hoja);
				hoja++;
				row = sheet.getRow(fila++);
				celda = row.createCell((short)1);
				if (style1!=null) {
					celda.setCellStyle(style1);
				}
				celda.setCellValue(valueOf(nom));
				row = sheet.getRow(fila++);
				celda = row.createCell((short)1);
				if (style1!=null) {
					celda.setCellStyle(style1);
				}
				celda.setCellValue(valueOf(formaResponsable.getEdodelsacoptxt()));
				celda = row.createCell((short)2);
				if (style2!=null) {
					celda.setCellStyle(style2);
				}
				celda.setCellValue(valueOf(""));
				row = sheet.getRow(fila++);
				celda = row.createCell((short)1);
				if (style1!=null) {
					celda.setCellStyle(style1);
				}
				celda.setCellValue(valueOf(fechaEmision));
				celda = row.createCell((short)2);
				if (style2!=null) {
					celda.setCellStyle(style2);
				}
				celda.setCellValue(valueOf(""));
				row = sheet.getRow(fila++);
				celda = row.createCell((short)1);
				if (style1!=null) {
					celda.setCellStyle(style1);
				}
				celda.setCellValue(valueOf(formaResponsable.getOrigensacoptxt()));
				celda = row.createCell((short)2);
				if (style2!=null) {
					celda.setCellStyle(style2);
				}
				celda.setCellValue(valueOf(""));
				row = sheet.getRow(fila++);
				celda = row.createCell((short)1);
				if (style1!=null) {
					celda.setCellStyle(style1);
				}
				celda.setCellValue(valueOf(formaResponsable.getNombres()+" "+formaResponsable.getApellidos()));
				celda = row.createCell((short)2);
				if (style2!=null) {
					celda.setCellStyle(style2);
				}
				celda.setCellValue(valueOf(""));
				row = sheet.getRow(fila++);
				celda = row.createCell((short)1);
				if (style1!=null) {
					celda.setCellStyle(style1);
				}
				celda.setCellValue(valueOf(formaResponsable.getAreafectadatxt()));
				celda = row.createCell((short)2);
				if (style2!=null) {
					celda.setCellStyle(style2);
				}
				celda.setCellValue(valueOf(""));
				row = sheet.getRow(fila++);
				celda = row.createCell((short)1);
				if (style1!=null) {
					celda.setCellStyle(style1);
				}
				celda.setCellValue(valueOf(formaResponsable.getFechaEstimada()));
				celda = row.createCell((short)2);
				if (style2!=null) {
					celda.setCellStyle(style2);
				}
				celda.setCellValue(valueOf(""));
				row = sheet.getRow(fila++);
				celda = row.createCell((short)1);
				if (style1!=null) {
					celda.setCellStyle(style1);
				}
				celda.setCellValue(valueOf(formaResponsable.getFechaReal()));
				celda = row.createCell((short)2);
				if (style2!=null) {
					celda.setCellStyle(style2);
				}
				celda.setCellValue(valueOf(""));
				row = sheet.getRow(fila++);
				celda = row.createCell((short)1);
				if (style1!=null) {
					celda.setCellStyle(style1);
				}
				celda.setCellValue(valueOf(formaResponsable.getDescripcion()));
				celda = row.createCell((short)2);
				if (style2!=null) {
					celda.setCellStyle(style2);
				}
				celda.setCellValue(valueOf(""));
				row = sheet.getRow(fila++);
				celda = row.createCell((short)1);
				if (style1!=null) {
					celda.setCellStyle(style1);
				}
				celda.setCellValue(valueOf(formaResponsable.getCausasnoconformidad()));
				celda = row.createCell((short)2);
				if (style2!=null) {
					celda.setCellStyle(style2);
				}
				celda.setCellValue(valueOf(""));
				row = sheet.getRow(fila++);
				celda = row.createCell((short)1);
				if (style1!=null) {
					celda.setCellStyle(style1);
				}
				celda.setCellValue(valueOf(formaResponsable.getAccionrecomendada()));
				celda = row.createCell((short)2);
				if (style2!=null) {
					celda.setCellStyle(style2);
				}
				celda.setCellValue(valueOf(""));
				StringBuffer informa_auser=new StringBuffer("");
				Iterator<Search> informa_a =usuarios1.iterator();
				while(informa_a.hasNext()){
					Search sh = informa_a.next();
					informa_auser.append(sh.getDescript()).append("(").append(sh.getAditionalInfo()).append(")");
					if (informa_a.hasNext()){
						informa_auser.append(",");
					}
				}
				row = sheet.getRow(fila++);
				celda = row.createCell((short)1);
				if (style1!=null) {
					celda.setCellStyle(style1);
				}
				celda.setCellValue(valueOf(informa_auser.toString()));
				celda = row.createCell((short)2);
				if (style2!=null) {
					celda.setCellStyle(style2);
				}
				celda.setCellValue(valueOf(""));
				StringBuffer requisitos_aplica=new StringBuffer("");
				Iterator requisitos_aplicaA=null;
				if (norms!=null){
					requisitos_aplicaA =norms.iterator();
					while(requisitos_aplicaA.hasNext()){
						Search sh = (Search)requisitos_aplicaA.next();
						requisitos_aplica.append(sh.getDescript());
						if (requisitos_aplicaA.hasNext()){
							requisitos_aplica.append(",");
						}
					}
				}
				row = sheet.getRow(fila++);
				celda = row.createCell((short)1);
				if (style1!=null) {
					celda.setCellStyle(style1);
				}
				celda.setCellValue(valueOf(requisitos_aplica.toString()));
				celda = row.createCell((short)2);
				if (style2!=null) {
					celda.setCellStyle(style2);
				}
				celda.setCellValue(valueOf(""));
				Iterator procesosSacopA=null;
				StringBuffer procesosSacop_str=new StringBuffer("");
				if(procesosSacop!=null){
					procesosSacopA =procesosSacop.iterator();
					while(procesosSacopA.hasNext()){
						Search sh = (Search)procesosSacopA.next();
						procesosSacop_str.append(sh.getDescript());
						if (procesosSacopA.hasNext()){
							procesosSacop_str.append(",");
						}
					}
				}
				row = sheet.getRow(fila++);
				celda = row.createCell((short)1);
				if (style1!=null) {
					celda.setCellStyle(style1);
				}
				celda.setCellValue(valueOf(procesosSacop_str.toString()));
				celda = row.createCell((short)2);
				if (style2!=null) {
					celda.setCellStyle(style2);
				}
				celda.setCellValue(valueOf(""));
				row = sheet.getRow(fila++);
				celda = row.createCell((short)1);
				if (style1!=null) {
					celda.setCellStyle(style1);
				}
				celda.setCellValue(valueOf(formaResponsable.getAccionobservacion()));
				celda = row.createCell((short)2);
				if (style2!=null) {
					celda.setCellStyle(style2);
				}
				celda.setCellValue(valueOf(""));
				row = sheet.getRow(fila++);
				celda = row.createCell((short)1);
				if (style1!=null) {
					celda.setCellStyle(style1);
				}
				celda.setCellValue(valueOf(formaResponsable.getAccionobservaciontxt()+""));
				celda = row.createCell((short)2);
				if (style2!=null) {
					celda.setCellStyle(style2);
				}
				celda.setCellValue(valueOf(""));
				String acionStablecida="";
				if ("1".equalsIgnoreCase(formaResponsable.getAccionesEstablecidas())){
					acionStablecida=rb.getString("scp.no");
				}else if ("0".equalsIgnoreCase(formaResponsable.getAccionesEstablecidas())){
					acionStablecida=rb.getString("scp.si");
				}
				row = sheet.getRow(fila++);
				celda = row.createCell((short)1);
				if (style1!=null) {
					celda.setCellStyle(style1);
				}
				celda.setCellValue(valueOf(acionStablecida));
				celda = row.createCell((short)2);
				if (style2!=null) {
					celda.setCellStyle(style2);
				}
				celda.setCellValue(valueOf(formaResponsable.getAccionesEstablecidastxt()));
				String eliminarCausaRaiz="";
				if ("1".equalsIgnoreCase(formaResponsable.getEliminarcausaraiz())){
					eliminarCausaRaiz=rb.getString("scp.no");
				}else  if ("0".equalsIgnoreCase(formaResponsable.getEliminarcausaraiz())){
					eliminarCausaRaiz=rb.getString("scp.si");
				}
				row = sheet.getRow(fila++);
				celda = row.createCell((short)1);
				if (style1!=null) {
					celda.setCellStyle(style1);
				}
				celda.setCellValue(valueOf(eliminarCausaRaiz));
				celda = row.createCell((short)2);
				if (style2!=null) {
					celda.setCellStyle(style2);
				}
				celda.setCellValue(valueOf(formaResponsable.getEliminarcausaraiztxt()));
			}
		}
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment; filename=reporteSACOP.xls");
		response.setHeader("content-transfer-encoding", "binary");
		OutputStream out = response.getOutputStream();
		workbook.write(out);
		out.close();
	}
	
	/**
	 * 
	 * @param orig
	 * @return
	 */
	private String obtenerOrigenSacop(String orig){
		String salida = HandlerProcesosSacop.getOrigen(orig);
		return salida;
	}
	
	
	/**
	 * 
	 * @param edo
	 * @return
	 */
	public String obtenerEdoBorrador(String edo){
		String salida=null;
		//         Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"),DesigeConf.getProperty("country.Default"));
		//         ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",defaultLocale);
		if (LoadSacop.edoBorrador.equalsIgnoreCase(edo)) {
			return getMessage("scp.borrador");
		} else if (LoadSacop.edoEmitida.equalsIgnoreCase(edo)) {
			return getMessage("scp.emitido");
		} else if (LoadSacop.edoAprobado.equalsIgnoreCase(edo)) {
			return getMessage("scp.aprobado1");
		} else if (LoadSacop.edoEnEjecucion.equalsIgnoreCase(edo)) {
			return getMessage("scp.inejecucion");
		} else if (LoadSacop.edoPendienteVerifSeg.equalsIgnoreCase(edo)) {
			return getMessage("scp.pendiente");
		} else if (LoadSacop.edoVerificacion.equalsIgnoreCase(edo)) {
			return getMessage("scp.verificacion");
		} else if (LoadSacop.edoCerrado.equalsIgnoreCase(edo)) {
			return getMessage("scp.cerrado1");
		} else if (LoadSacop.edoRechazado.equalsIgnoreCase(edo)) {
			return getMessage("scp.rechazado1");
		}
		return salida;
	}
	
	/**
	 * 
	 * @param sacopId
	 * @return
	 */
	private String getDetalleAcciones(int sacopId){
		String acciones = "";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			String query = "SELECT tbl_sa.accion, tbl_sa.fecha AS fechaMaxima, p.nombres, p.apellidos, tbl_axp.fecha as fechaRespuesta, tbl_axp.comentario, tbl_axp.firmo "
					+ "FROM person p, tbl_planillasacopaccion tbl_sa, tbl_sacopaccionporpersona AS tbl_axp "
					+ "WHERE tbl_axp.idplanillasacopaccion = tbl_sa.idplanillasacopaccion "
					+ "AND p.idperson = tbl_axp.idperson "
					+ "AND tbl_sa.idplanillasacop1 = " + sacopId;
			
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			ps = con.prepareStatement(JDBCUtil.replaceCastMysql(query));
			rs = ps.executeQuery();
			
			while(rs.next()){
				acciones += getMessage("scp.accion") + ": " + rs.getString("accion") + ", ";
				acciones += getMessage("scp.fechaculminacion") + ": " + rs.getString("fechaMaxima") + ", ";
				acciones += getMessage("scp.responsable") + ": " + rs.getString("nombres") + rs.getString("apellidos") + ", ";
				if("1".equals(rs.getString("firmo"))){
					acciones += getMessage("scp.accfirmada") + ": " + rs.getString("fechaRespuesta") + ", ";
					acciones += getMessage("scp.comm") + ": " + rs.getString("comentario") + ".";
				} else {
					acciones += getMessage("scp.accnofirmada");
				}
				
				acciones += "/n\n";
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Error: " + e.getLocalizedMessage(), e);
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
			try {
				ps.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
			try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
		return acciones;
	}
	
	private String valueOf(String valor) {
		return valor==null?" ":valor;
	}
}
