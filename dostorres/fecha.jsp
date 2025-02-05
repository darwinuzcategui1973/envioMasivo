<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.TimeZone" %>


<%

StringBuffer cad = new StringBuffer();

//Locale l = new Locale("es", "VE");
Locale l = new Locale("en", "US");
//Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Venezuela"));
Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/La_Paz"));

//Locale l = new Locale("es","MX");
//Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Mexico_City"),l);

cad.append(cal.get(Calendar.DATE));
cad.append("-");
cad.append(cal.get(Calendar.MONTH)+1);
cad.append("-");
cad.append(cal.get(Calendar.YEAR)); 
cad.append(" ");
cad.append(cal.get(Calendar.HOUR_OF_DAY));
cad.append(":");
cad.append(cal.get(Calendar.MINUTE));
cad.append(":");
cad.append(cal.get(Calendar.SECOND));
out.println("FECHA: " + cal.getTimeZone().getDisplayName() + " -> "+ cad.toString());
out.println("<br/>");
out.println(new java.util.Date());
%>

Prueba de la hora