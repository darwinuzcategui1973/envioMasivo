<%@ page import="net.sf.jasperreports.engine.*" %>

<%@ page import="net.sf.jasperreports.engine.design.*" %>

<%@ page import="net.sf.jasperreports.engine.data.*"%>

<%@ page import="net.sf.jasperreports.engine.export.*"%>

<%@ page import="net.sf.jasperreports.engine.util.*"%>

<%@ page import="net.sf.jasperreports.view.*"%>

<%@ page import="net.sf.jasperreports.view.save.*"%>
<%@ page contentType="text/html; charset=iso-8859-1" language="java"
            import="java.sql.*,
                    com.desige.webDocuments.utils.ToolsHTML,
                    java.util.ResourceBundle,
                    com.desige.webDocuments.persistent.utils.JDBCUtil" %>
<%@ page import="java.sql.*"%>

<%@ page import="java.util.*" %>

<%@ page import="java.io.*" %>

<html>

<head><title>Generando Reporte Nomina...............</title>
<jsp:include page="meta.jsp" /> 

 <%
            //System.out.println("Comienza el Reporte----------------------------");
 %>



<script language="JavaScript">

function regresar(){

            history.back();

}

function error(){

            alert("Error encontrando procesando el periodo: " + periodo);

            history.back();

}

</script>

<head>

<%

try{

            ResultSet rs = null;

            Statement st = null;

            String sql = null;

            Connection conn = JDBCUtil.getConnection("reportelistamaestra.jsp");

            /////////////////////////////////////////////

            //System.out.println("********Compilamos listamaestra.jrxml OK********");

            System.setProperty(

                        "jasper.reports.compile.class.path",

                        application.getRealPath("/WEB-INF/lib/jasperreports-1.1.0.jar") +

                        System.getProperty("path.separator") +

                        application.getRealPath("/WEB-INF/classes/")

                        );

            //System.out.println("*****Cargamos el jasperreports-1.1.0.jar OK*********");

            System.setProperty(

                        "jasper.reports.compile.temp",

                        application.getRealPath("/reports/")

                        );

            JasperCompileManager.compileReportToFile(application.getRealPath("/reportes/listamaestra.jrxml"));



            //System.out.println("******Fin de la Compilamos el archivos***********");

            /////////////////////////////////////////////


//carpetadeweb/reportes/simons.jasper
            File reportFile = new File(application.getRealPath("/reportes/listamaestra.jasper"));

            String sql_query=ToolsHTML.getAttribute(request,"queryReporte")!=null?(String)ToolsHTML.getAttribute(request,"queryReporte"):"";


           out.println("sql_query="+sql_query);

            Map parameters = new HashMap();

            parameters.put("sql_query", sql_query);


            byte[] bytes =

                        JasperRunManager.runReportToPdf(

                                   reportFile.getPath(),

                                   parameters,

                                   conn

                                   );


            if(conn!=null){
               conn.close();
            }
            response.setContentType("application/pdf");

            response.setContentLength(bytes.length);

            ServletOutputStream ouputStream = response.getOutputStream();

            ouputStream.write(bytes, 0, bytes.length);

            ouputStream.flush();

            ouputStream.close();


            //System.out.println("jasperPDF OK..............");

            ///////////////////////////////////////////////////////////////

            //System.out.println("Fin del reporte pago_reporte_nomina.............");
                                   }catch (JRException e)
                                   {
                                       //System.out.println("Error:" +e.getMessage());
                                   }

                                   catch (Exception e)

                                   {

                                   e.printStackTrace();

                                   //System.out.println("Error2:" +e.getMessage());

                                   }

%>

<body>

</body>

</html>



