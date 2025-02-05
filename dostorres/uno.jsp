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
   <head>
   </head>
   <body>
         <%
              String sql_query=ToolsHTML.getAttribute(request,"queryReporte")!=null?(String)ToolsHTML.getAttribute(request,"queryReporte"):"";
              out.println("sql_query="+sql_query);                 
         %>
   </body>
</html>
