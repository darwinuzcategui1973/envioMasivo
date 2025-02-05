package com.desige.webDocuments.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.dao.ConfExpedienteDAO;
import com.desige.webDocuments.files.forms.FilesForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 03/04/2006
 * Time: 11:29:28 AM
 * To change this template use File | Settings | File Templates.
 */


public class CrearReporteFiles extends SuperAction {
//    private static ArrayList elementos = ToolsHTML.getProperties("visor.OpenBrowser");
//    private POIFSFileSystem fs;
    public String error;
    private static Logger log = LoggerFactory.getLogger(CrearReporteFiles.class.getName());
    private ResourceBundle rb = null;
    private String dateSystem = ToolsHTML.sdf.format(new Date());
    private Users usuario = null;
    
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        
        try {
			usuario = getUserSession();

			Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
    		rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);        	
        	
            int modeloReporte=2;

            generarExcelModelo2(request,response);

            return null;
        } catch(Exception e) {
           log.error(e.getMessage());
            e.printStackTrace();
        } finally {
        }
        return goError();
    }

    private String getQuery(HttpServletRequest request) throws ApplicationExceptionChecked {
    	return request.getSession().getAttribute("queryFilesReport").toString();
    }

    public void generarExcelModelo2(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String path = ToolsHTML.getPath().concat("estilo");
        String nameFile = path.concat(File.separator).concat("FormatoModeloFiles.xls");
        response.setContentType("application/vnd.ms-excel");
        InputStream inputfile = new FileInputStream(nameFile);
        HSSFWorkbook workbook = new HSSFWorkbook(inputfile,true);
        HSSFSheet sheet = workbook.getSheetAt(0);

        HSSFCellStyle style = null;
        
        // colunas seleccionadas por el usuario
		String columnUser = HandlerBD.getOptionFiles((int)(usuario.getIdPerson()));
        
        // DAO de configuracion
        ConfExpedienteDAO conf = new ConfExpedienteDAO();
        ArrayList lista = (ArrayList)conf.findAll();
        FilesForm obj;
        
        try {
            HSSFRow fil = sheet.getRow(4);

            if (fil!=null) {
            	HSSFCell cell = null;
	                cell = fil.getCell((short)0);
	                if (cell!=null) {
	                	style = cell.getCellStyle();
	                }
	            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        
        //Se Escribe la Información en el Documento
        String sql_query = getQuery(request);
        log.debug("sql_query: " + sql_query);
        Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
        PreparedStatement ps = con.prepareStatement(JDBCUtil.replaceCastMysql(sql_query));
        ResultSet rs = ps.executeQuery();
        int fila = 5;
        HSSFRow row;
        HSSFCell celda;
        
        //Estableciendo Título del Reporte según el Idioma del Usuario :D
        //Fila 2
        row = sheet.getRow(1);
        if (row!=null) {
            //Columna 2 titulo
            celda = row.getCell((short)0);
            celda.setCellValue(getMessage("files.titleSearch") +  " "  + " " + ToolsHTML.sdfShowWithoutHour.format(new java.util.Date()));
        }

        //Títulos de la Tabla
        int cont=0;
        row = sheet.getRow(4);
        for(int i=0; i<lista.size(); i++) {
        	obj=(FilesForm)lista.get(i);
        	if(columnUser.indexOf(",".concat(obj.getId().replace("f", "")).concat(","))!=-1) {
	        	HSSFCell col = row.createCell((short)cont++);
	            //row.getCell((short)i).setCellValue(obj.getEtiqueta(usuario.getLanguage()));
	            if (style!=null) {
	            	col.setCellStyle(style);
	            }
	        	col.setCellValue(obj.getEtiqueta(usuario.getLanguage()));
        	}
        }

        while (rs.next()) {
            row = sheet.createRow((short)fila);
            cont=0;
            for(int i=0; i<lista.size(); i++) {
            	obj=(FilesForm)lista.get(i);
            	if(columnUser.indexOf(",".concat(obj.getId().replace("f", "")).concat(","))!=-1) {
	            	celda = row.createCell((short)cont++);
	                //if (style!=null) {
	                //    celda.setCellStyle(style);
	                //}
	            	switch(obj.getTipo()) {
	            	case FilesForm.TYPE_STRING:
	                    celda.setCellValue(ToolsHTML.isEmptyOrNull(rs.getString(obj.getId()),"").trim());
	            		break;
	            	case FilesForm.TYPE_NUMERIC:
	                    celda.setCellValue(new Integer(ToolsHTML.isEmptyOrNull(rs.getString(obj.getId()),"0").replaceAll("\\.","")));
	            		break;
	            	case FilesForm.TYPE_DATE:
	            		try {
		            		HSSFDataFormat df = workbook.createDataFormat();
		            		HSSFCellStyle cs2 = workbook.createCellStyle();
		            		cs2.setDataFormat(df.getFormat("d/m/yy"));
		            		cs2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		            		celda.setCellStyle(cs2);
		                    celda.setCellValue(rs.getDate((obj.getId())));
	            		} catch(java.lang.IllegalArgumentException ex) {
	                        celda.setCellValue(ToolsHTML.isEmptyOrNull(rs.getString(obj.getId()),"").trim());
	            		}
	                    //HSSFDateUtil.
	            		break;
	            	}
            	}
            }

            fila++;
        }
        
        while (false && rs.next()) {
            row = sheet.createRow((short)fila);
            //Valor y estilo si Aplica en la Columna # 1
            celda = row.createCell((short)0);
            if (style!=null) {
                celda.setCellStyle(style);
            }
            celda.setCellValue(rs.getString("nameDocument"));
            //Valor y estilo si Aplica en la Columna # 2
            celda = row.createCell((short)1);
            if (style!=null) {
                celda.setCellStyle(style);
            }
            celda.setCellValue(rs.getString("typeDoc"));
            //Valor y estilo si Aplica en la Columna # 3
            celda = row.createCell((short)2);
            if (style!=null) {
                celda.setCellStyle(style);
            }
            String correlativo = ToolsHTML.isEmptyOrNull(rs.getString("prefix"),"").concat(ToolsHTML.isEmptyOrNull(rs.getString("number"),""));
            if(!ToolsHTML.isEmptyOrNull(correlativo)){
            	celda.setCellValue(correlativo);
            }else{
            	if(!ToolsHTML.isEmptyOrNull(rs.getString("number"))){
                	celda.setCellValue(rs.getString("number"));
            	}else{
            		celda.setCellValue(rs.getString("prefix"));
            	}
            }

            //Valor y estilo si Aplica en la Columna # 4
            String mayorver="0";
            String minorver="0";
            try {
            	mayorver=rs.getString("MayorVer")!=null?rs.getString("MayorVer").trim():"";
            	minorver=rs.getString("MinorVer")!=null?rs.getString("MinorVer").trim():"";
            } catch(java.sql.SQLException e) {
            	
            }
            celda = row.createCell((short)3);
            if (style!=null) {
                celda.setCellStyle(style);
            }
            celda.setCellValue(mayorver+"."+minorver);

            //Valor y estilo si Aplica en la Columna # 5
            celda = row.createCell((short)4);
            if (style!=null) {
                celda.setCellStyle(style);
            }
            celda.setCellValue(ToolsHTML.sdfShowWithoutHour.format(rs.getTimestamp("dateCreation")));

            //Valor y estilo si Aplica en la Columna # 6
            celda = row.createCell((short)5);
            String statu = ToolsHTML.getStatusDocumento(rs.getString("statu"), rs.getString("statuVer"),rb,dateSystem, rs.getString("dateExpiresDrafts"), rs.getString("dateExpires") ) ;
            if (!ToolsHTML.isEmptyOrNull(statu)) {
                if (style!=null) {
                    celda.setCellStyle(style);
                }
                celda.setCellValue(statu);
            } else {
                celda.setCellValue("");
            }


            //Valor y estilo si Aplica en la Columna # 7
            celda = row.createCell((short)6);
            if (style!=null) {
                celda.setCellStyle(style);
            }
            //celda.setCellValue(getCargo(rs.getString("idPerson")));
            celda.setCellValue(rs.getString("nombre"));


            fila++;
        }
        //Enviando el Archivo al Cliente

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment; filename=ListaDocumentos.xls");
        response.setHeader("content-transfer-encoding", "binary");
        OutputStream out = response.getOutputStream();
        workbook.write(out);
        
        out.flush();
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


}
