package com.desige.webDocuments.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: RelacionadosReport.java <br/>
 * Copyright: (c) 2007 Focus Consulting C.A. <br/>
 * @author Ing. Nelson Crespo (NC)
 * @version QwebDocuments v4.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 * </ul>
 */
public class RelacionadosReport extends SuperAction {
    static Logger log = LoggerFactory.getLogger(RelacionadosReport.class.getName());
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        try {
            String idDoc = getParameter("numDoc");
            //System.out.println("idDoc " + idDoc);
            if (!ToolsHTML.isEmptyOrNull(idDoc)) {
                createReport(idDoc,response,request);
                return goSucces();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Error: ",ex);
        }
        return goError();
    }

    private void createReport(String idDoc,HttpServletResponse response,HttpServletRequest request) throws Exception {
        String query = HandlerDocuments.getQueryRelations(idDoc,null);
        String path = ToolsHTML.getPath().concat("estilo");
        String nameFile = path.concat(File.separator).concat("Relacionados.xls");
        response.setContentType("application/vnd.ms-excel");
        InputStream inputfile = new FileInputStream(nameFile);
        HSSFWorkbook workbook = new HSSFWorkbook(inputfile,true);
        HSSFSheet sheet = workbook.getSheetAt(0);
        //Para el Manejo de Estilos de la Hoja
        HSSFCellStyle style1 = null;
        HSSFCellStyle style2 = null;
        HSSFCellStyle style3 = null;
        HSSFCellStyle style4 = null;
        HSSFCellStyle style5 = null;
        try {
            HSSFRow fil = sheet.getRow(12);
            if (fil!=null) {
                HSSFCell cell = fil.getCell((short)0);
                if (cell!=null) {
                    style1 = cell.getCellStyle();
                }
                cell = fil.getCell((short)1);
                if (cell!=null) {
                    style2 = cell.getCellStyle();
                }
                cell = fil.getCell((short)2);
                if (cell!=null) {
                    style3 = cell.getCellStyle();
                }
//                cell = fil.getCell((short)3);
//                if (cell!=null) {
//                    style4 = cell.getCellStyle();
//                }
//                cell = fil.getCell((short)4);
//                if (cell!=null) {
//                    style5 = cell.getCellStyle();
//                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
        PreparedStatement ps = con.prepareStatement(JDBCUtil.replaceCastMysql(query));
        ResultSet rs = ps.executeQuery();
        int fila = 12;
        HSSFRow row;
        HSSFCell celda;
        //Estableciendo Título del Reporte según el Idioma del Usuario :D
        BaseDocumentForm forma = new BaseDocumentForm();
        forma.setIdDocument(idDoc);
        forma.setNumberGen(idDoc);
        HandlerStruct.loadDocument(forma,true,false,null, request);
        row = sheet.getRow(1);
        if (row!=null) {
            celda = row.getCell((short)0);
            StringBuffer msg = new StringBuffer(getMessage("doc.links")).append(" ").append(getMessage("doc.toDoc")).append(" ");
            msg.append(forma.getPrefix()).append(forma.getNumber()).append(" ").append(getMessage("showDoc.Ver"));
            msg.append(" ").append(forma.getMayorVer()).append(".").append(forma.getMinorVer()).append(" ");
            msg.append(forma.getNameDocument());
            celda.setCellValue(msg.toString());
        }
        //Títulos de la Tabla
        row = sheet.getRow(11);
        row.getCell((short)0).setCellValue(getMessage("doc.number"));
        row.getCell((short)1).setCellValue(getMessage("cbs.name"));
        row.getCell((short)2).setCellValue(getMessage("showDoc.Ver"));
//        row.getCell((short)3).setCellValue(getMessage("doc.type"));
//        row.getCell((short)4).setCellValue(getMessage("cbs.owner"));
        String number;
        while (rs.next()) {
            //System.out.println("WHILE...");
            number = null;
            row = sheet.createRow((short)fila);
            //Valor y estilo si Aplica en la Columna # 1
            celda = row.createCell((short)0);
            if (style1!=null) {
                celda.setCellStyle(style1);
            }
            String prefijo = rs.getString("prefix");
            String numero = rs.getString("number");
            if (prefijo==null || prefijo.toUpperCase().equals("NULL")) prefijo = ""; 
            if (numero==null || numero.toUpperCase().equals("NULL")) numero = "";
            number = prefijo + numero;
            celda.setCellValue(number);
            //Valor y estilo si Aplica en la Columna # 2
            celda = row.createCell((short)1);
            if (style2!=null) {
                celda.setCellStyle(style2);
            }
            celda.setCellValue(rs.getString("nameDocument"));
            //Valor y estilo si Aplica en la Columna # 3
//            celda = row.createCell((short)2);
//            if (style3!=null) {
//                celda.setCellStyle(style3);
//            }
//            celda.setCellValue(rs.getString("correlativo"));

            //Valor y estilo si Aplica en la Columna # 3
            String mayorver=rs.getString("MayorVer")!=null?rs.getString("MayorVer").trim():"";
            String minorver=rs.getString("MinorVer")!=null?rs.getString("MinorVer").trim():"";
            celda = row.createCell((short)2);
            if (style3!=null) {
                celda.setCellStyle(style3);
            }
            celda.setCellValue(mayorver+"."+minorver);
            //Valor y estilo si Aplica en la Columna # 5
//            celda = row.createCell((short)3);
//            if (style5!=null) {
//                celda.setCellStyle(style4);
//            }
//            celda.setCellValue(rs.getString("Nombre"));
            fila++;
        }

        //Enviando el Archivo al Cliente
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment; filename=ListaRelacionados.xls");
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
            if (inputfile!=null) {
                inputfile.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
