package com.desige.webDocuments.excel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;

public class excel   {



        private POIFSFileSystem fs;
        public String error;

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        excel xls = new excel();
       // xls.generarExcel("c:\\");
        //System.out.println("Generado con exito la hoja de excel");


    }
    public void generarExcel(String path,ResultSet rs){
        try{
            Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"),DesigeConf.getProperty("country.Default"));
            ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",defaultLocale);
            //  String ruta = path + File.separator + nameFile;
            //String path = ToolsHTML.getPath().concat("tmp"); // \\tmp
            excel excel1 = new excel();
            HSSFWorkbook wb = new HSSFWorkbook();
            FileOutputStream fileOut = new FileOutputStream(path+"\\copia2.xls");
            HSSFSheet sheet = wb.createSheet(rb.getString("lst.maestra"));
            HSSFSheet sheet2 = wb.createSheet("Hoja2");
            HSSFRow row;
            HSSFCell cell;
            wb.write(fileOut);
            fs =  new POIFSFileSystem(new FileInputStream(path+"\\copia2.xls"));
            wb = new HSSFWorkbook(fs);
            excel1.encabezado( wb ,sheet);
            sheet = wb.getSheetAt(0);
            sheet2 = wb.getSheetAt(1);
            //book.connect();
            //rs=book.viewbooks();
            float acumularbolivares=0;
            float acumulardolares=0;
            short fila=7;
            short column=0;
            while (rs.next()){
                 row=sheet.createRow((short)fila);
                 row.createCell((short)column).setCellValue("Nro" );

                 column +=1;
                 row=sheet.createRow((short)fila);
                 row.createCell((short)column).setCellValue("contrato" );

                 column +=1;
                 row=sheet.createRow((short)fila);
                 row.createCell((short)column).setCellValue("concepto" );

                  column +=1;
                  row=sheet.createRow((short)fila);
                  row.createCell((short)column).setCellValue("codigocontable");


                 column +=1;
                 row=sheet.createRow((short)fila);
                 row.createCell((short)column).setCellValue("fecha_factura".substring(8, 10) + "/" + "fecha_factura".substring(5, 7) + "/" + "fecha_factura".substring(0, 4) );

                 column +=1;
                 row=sheet.createRow((short)fila);
                 row.createCell((short)column).setCellValue("dolares");

                 column +=1;
                 row=sheet.createRow((short)fila);
                 row.createCell((short)column).setCellValue("Cambio");

                 column +=1;
                 row=sheet.createRow((short)fila);
                 row.createCell((short)column).setCellValue(Float.parseFloat("10.9") * ((float)10.8) );
                 //acumulardolares= acumulardolares + Float.parseFloat(rs.getString("dolares"));
                 //acumularbolivares= acumularbolivares + (Float.parseFloat(rs.getString("dolares")) * Float.parseFloat( rs.getString("Cambio")));

                 column +=1;
                 row=sheet.createRow((short)fila);
                 row.createCell((short)column).setCellValue("nombre" );

                 column +=1;
                 row=sheet.createRow((short)fila);
                 row.createCell((short)column).setCellValue("status" );

                 //if (rs.getString("FECHA_PAGO") !=null) {
                     column +=1;
                     row=sheet.createRow((short)fila);
                     row.createCell((short)column).setCellValue("Fecha");
                 //}

                 //if (rs.getString("ANIO_CANC") !=null) {
                     column +=1;
                     row=sheet.createRow((short)fila);
                     row.createCell((short)column).setCellValue("ANIO_CANC" );
                 //}
                 column =0;
                 fila +=1;
                 //System.out.println("Fila="+fila +" Columna="+column);
            }
            HSSFCellStyle cellStyle = wb.createCellStyle();
            HSSFCellStyle cellStyle2 = wb.createCellStyle();
            //row.createCell((short)2).setCellValue("TOTAL FACTURADO EN EL AÑO");
            row=sheet.createRow((short)fila);
            cell = row.createCell((short)2);
            cell.setCellValue("");
            excel1.azul_celda(cellStyle, cell);

            cell = row.createCell((short)5);
            cell.setCellValue(acumulardolares);
            excel1.orange_celda(cellStyle2, cell);

            //row.createCell((short)5).setCellValue( acumulardolares);

            cell = row.createCell((short)7);
            cell.setCellValue(acumularbolivares);
            excel1.orange_celda(cellStyle2, cell);
            //row.createCell((short)7).setCellValue(acumularbolivares);
            //book.disconnect();
           //FileOutputStream
           fileOut = new FileOutputStream(path+"\\copia2.xls");
           wb.write(fileOut);


        }catch(Exception e){
               e.printStackTrace();
        }

    }

    private void encabezado(HSSFWorkbook wb ,HSSFSheet sheet)throws Exception{
        excel xls = new excel();
       try{
          Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"),DesigeConf.getProperty("country.Default"));
          ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",defaultLocale);
          HSSFCell cell;
          HSSFRow row;
          HSSFCellStyle cellStyle = wb.createCellStyle();
          HSSFCellStyle cellStyle2 = wb.createCellStyle();

          sheet = wb.getSheetAt(0);


           row=sheet.createRow((short)1);
           cell = row.createCell((short)1);
           row.setHeight( (short) 300 );
           sheet.setColumnWidth( (short) 1, (short)13000 );
           sheet.setColumnWidth( (short) 2, (short)9000 );
           sheet.setColumnWidth( (short) 3, (short)5000 );
           sheet.setColumnWidth( (short) 4, (short)5000 );
           sheet.setColumnWidth( (short) 6, (short)5000 );
           sheet.setColumnWidth( (short) 8, (short)9000 );
           sheet.setColumnWidth( (short) 9, (short)5000 );
           sheet.setColumnWidth( (short) 10, (short)7000 );
           sheet.setColumnWidth( (short) 11, (short)5000 );
           cell.setCellValue(ToolsHTML.getLicenceOwner());
           xls.azul_celda(cellStyle,cell);

           row=sheet.createRow((short)2);
           cell = row.createCell((short)1);
           cell.setCellValue(rb.getString("pb.title"));
           xls.azul_celda(cellStyle,cell);

           row=sheet.createRow((short)3);
           cell = row.createCell((short)0);
           cell.setCellValue("Fecha:");
           xls.azul_celda(cellStyle,cell);
           //row=sheet.createRow((short)3);
           //row.createCell((short)0).setCellValue("Fecha:");



           cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
           row=sheet.createRow((short)3);
           cell = row.createCell((short)2);
           cell.setCellValue(new Date());
           xls.azul_celda(cellStyle,cell);


           row=sheet.createRow((short)4);
           cell = row.createCell((short)0);
           cell.setCellValue(rb.getString("cbs.name"));
           xls.rojo_celda(cellStyle2,cell);
           //row=sheet.createRow((short)4);
           //row.createCell((short)0).setCellValue("Nro:");


           row=sheet.createRow((short)4);
           cell = row.createCell((short)1);
           cell.setCellValue(rb.getString("doc.type"));
           xls.rojo_celda(cellStyle2,cell);
           //row=sheet.createRow((short)4);
           //row.createCell((short)1).setCellValue("Contrato");

           row=sheet.createRow((short)4);
           cell = row.createCell((short)2);
           cell.setCellValue(rb.getString("doc.number"));
           xls.rojo_celda(cellStyle2,cell);
           //row=sheet.createRow((short)4);
           //row.createCell((short)2).setCellValue("Concepto");


           row=sheet.createRow((short)4);
           cell = row.createCell((short)3);
           cell.setCellValue(rb.getString("showDoc.Ver"));
           xls.rojo_celda(cellStyle2,cell);
          // row=sheet.createRow((short)4);
          // row.createCell((short)3).setCellValue("N");

           row=sheet.createRow((short)4);
           cell = row.createCell((short)4);
           cell.setCellValue(rb.getString("cbs.owner"));
           xls.rojo_celda(cellStyle2,cell);
          // row=sheet.createRow((short)4);
          // row.createCell((short)4).setCellValue("FECHA FACTURA");

/*
           row=sheet.createRow((short)4);
           cell = row.createCell((short)5);
           cell.setCellValue(rb.getString("cbs.owner"));
           xls.rojo_celda(cellStyle2,cell);
       //    row=sheet.createRow((short)4);
        //   row.createCell((short)5).setCellValue("Dolares");

           row=sheet.createRow((short)4);
           cell = row.createCell((short)6);
           cell.setCellValue("Tipo Cambio");
           xls.rojo_celda(cellStyle2,cell);
           //row=sheet.createRow((short)4);
           //row.createCell((short)6).setCellValue("TIPO CAMBIO");


           row=sheet.createRow((short)4);
           cell = row.createCell((short)7);
           cell.setCellValue("Bolivares");
           xls.rojo_celda(cellStyle2,cell);
           //row=sheet.createRow((short)4);
           //row.createCell((short)7).setCellValue("Bolivares");

           row=sheet.createRow((short)4);
           cell = row.createCell((short)8);
           cell.setCellValue("Institución");
           xls.rojo_celda(cellStyle2,cell);
           //row=sheet.createRow((short)4);
           //row.createCell((short)8).setCellValue("Nombre");

           row=sheet.createRow((short)4);
           cell = row.createCell((short)9);
           cell.setCellValue("Status");
           xls.rojo_celda(cellStyle2,cell);
           //row=sheet.createRow((short)4);
           //row.createCell((short)9).setCellValue("Status");
           row=sheet.createRow((short)4);
           cell = row.createCell((short)10);
           cell.setCellValue("Fecha de Pago");
           xls.rojo_celda(cellStyle2,cell);

           row=sheet.createRow((short)4);
           cell = row.createCell((short)11);
           cell.setCellValue("Año de Pago");
           xls.rojo_celda(cellStyle2,cell);
*/
       }
       catch(Exception e){
           error="error: " + e;
           throw new Exception(error);
       }
    }
private void azul_celda(HSSFCellStyle cellStyle,HSSFCell cell){
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);

        cellStyle.setBottomBorderColor(HSSFColor.BLUE.index);
        cellStyle.setLeftBorderColor(HSSFColor.BLUE.index);
        cellStyle.setRightBorderColor(HSSFColor.BLUE.index);
        cellStyle.setTopBorderColor(HSSFColor.BLUE.index);
        cell.setCellStyle(cellStyle);
}

private void orange_celda(HSSFCellStyle cellStyle,HSSFCell cell){
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);

        cellStyle.setBottomBorderColor(HSSFColor.ORANGE.index);
        cellStyle.setLeftBorderColor(HSSFColor.ORANGE.index);
        cellStyle.setRightBorderColor(HSSFColor.ORANGE.index);
        cellStyle.setTopBorderColor(HSSFColor.ORANGE.index);
        cell.setCellStyle(cellStyle);
}

private void rojo_celda(HSSFCellStyle cellStyle,HSSFCell cell){
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);

        cellStyle.setBottomBorderColor(HSSFColor.RED.index);
        cellStyle.setLeftBorderColor(HSSFColor.RED.index);
        cellStyle.setRightBorderColor(HSSFColor.RED.index);
        cellStyle.setTopBorderColor(HSSFColor.RED.index);
        cell.setCellStyle(cellStyle);
}

}
