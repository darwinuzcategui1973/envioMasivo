<table width="100%" border="0" cellspacing="2" cellpadding="2">
  <tr bgcolor="#000099" align="center"> 
    <td colspan="2"><font face="Verdana, Arial, Helvetica, sans-serif" size="3" color="#FFFFFF"><b>Valores 
      del request </b></font></td>
  </tr>
  <%
  
  java.util.Enumeration parametros = request.getParameterNames();
  
  while (parametros.hasMoreElements()) {
  
  String nombre = (String)parametros.nextElement();
  String valor = request.getParameter(nombre);
  %>
  
    
  <tr bgcolor="#EFE0F8"> 
    <td width="47%"><font face="Verdana, Arial, Helvetica, sans-serif" size="1"><%=nombre%></font></td>
    <td width="53%" bgcolor="#EFE0F8"><font face="Verdana, Arial, Helvetica, sans-serif" size="1"><%=valor%></font></td>
  </tr>
  <%}%>
  <tr bgcolor="#000099" align="center"> 
    <td colspan="2"><font face="Verdana, Arial, Helvetica, sans-serif" size="3" color="#FFFFFF"><b>Valores 
      de la session</b></font></td>
  </tr>
  <%
  java.util.Enumeration sesiones = session.getAttributeNames();

  while (sesiones.hasMoreElements()) {
  
  String nombreSesion = (String)sesiones.nextElement();
  Object valorSesion = session.getAttribute(nombreSesion);
  %>
  
  <tr bgcolor="#EFE0F8"> 
    <td width="47%"><font face="Verdana, Arial, Helvetica, sans-serif" size="1"><%=nombreSesion%></font></td>
    <td width="53%"><font face="Verdana, Arial, Helvetica, sans-serif" size="1"><%=valorSesion%></font></td>
  </tr>
  <%}%>
  <br/>
  <tr bgcolor="#000099" align="center">
    <td colspan="2"><font face="Verdana, Arial, Helvetica, sans-serif" size="3" color="#FFFFFF"><b>Valores
      de la Cabecera</b></font></td>
  </tr>
  <%
  java.util.Enumeration head = request.getHeaderNames();

  while (head.hasMoreElements()) {

  String nombreSesion = (String)head.nextElement();
  Object valorSesion = request.getHeader(nombreSesion);// session.getAttribute(nombreSesion);
  %>

  <tr bgcolor="#EFE0F8">
    <td width="47%"><font face="Verdana, Arial, Helvetica, sans-serif" size="1"><%=nombreSesion%></font></td>
    <td width="53%"><font face="Verdana, Arial, Helvetica, sans-serif" size="1"><%=valorSesion%></font></td>
  </tr>
  <%}%>

  <tr bgcolor="#000099" align="center">
    <td colspan="2"><font face="Verdana, Arial, Helvetica, sans-serif"><b><font color="#FFFFFF">Valores
      del application (ServletContext)</font></b></font></td>
  </tr>
  <%
  java.util.Enumeration apps = application.getAttributeNames();
  
  while (apps.hasMoreElements()) {
  
  String nombreApp = (String)apps.nextElement();
  Object valorApp = application.getAttribute(nombreApp);
  %>
  
  <tr bgcolor="#EFE0F8"> 
    <td width="47%"><font face="Verdana, Arial, Helvetica, sans-serif" size="1"><%=nombreApp%></font></td>
    <td width="53%"><font face="Verdana, Arial, Helvetica, sans-serif" size="1"><%=valorApp%></font></td>
  </tr>
  <%}%>

</table>
