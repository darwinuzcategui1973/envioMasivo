<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 sun.jdbc.rowset.CachedRowSet"
            	%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%
	ResourceBundle rb = ToolsHTML.getBundle(request);
	String color="";
%>
<html>
<head>
	<title><%=rb.getString("btn.docUnRead")%></title>
	<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
	<script>
	</script>
</head>
<body class="bodyInternas" style="margin:0;" onload="checkUser()">
<form name='forma' style="margin:0;">
<table width="100%">
	<caption class="titleBlack" style="padding:5px;">DETALLE DE VISUALIZACIÓN SEGUN LISTA DE DIVULGACIÓN</caption>
	<tr>
		<td class="td_titulo_C" colspan="2" style="border:1px solid #afafaf;">
			Nombre
		</td>
		<td class="td_titulo_C"  style="border:1px solid #afafaf;">
			N&uacute;mero
		</td>
		<td class="td_titulo_C"  style="border:1px solid #afafaf;">
			Versi&oacute;n
		</td>
		<td class="td_titulo_C" colspan="2"  style="border:1px solid #afafaf;">
			Propietario
		</td>
	</tr>
    <logic:present name="listDocUnRead">
        <%CachedRowSet crs = (CachedRowSet) request.getAttribute("listDocUnRead");
        int idDoc=0;
        boolean imp = true;
        boolean isFirst = true;
        while(crs.next()){
        	if(idDoc!=crs.getInt("numgen")) {
        		imp = true;
	        	color="#d4d4d4";
	        	isFirst=true;
        	} else {
        		imp = false;
	        	color="#efefef";
        	}
        if(imp){%>
			<tr>
				<td width="40%" colspan="2" class="td_gris_l" style="background-color:<%=color%>">
					<%=crs.getString("nameDocument")%>
				</td>
				<td width="15%" class="td_gris_l" style="background-color:<%=color%>">
					<%=crs.getString("prefix")==null?"":crs.getString("prefix").concat("-")%><%=crs.getString("number")%>
				</td>
				<td width="15%" class="td_gris_l" style="background-color:<%=color%>">
					<%=crs.getString("mayorVer")%>.<%=crs.getString("minorVer")%>
				</td>
				<td width="20%" colspan="2" class="td_gris_l" style="background-color:<%=color%>">
					<%=crs.getString("owner")%>
				</td>
			</tr>
		<%} else {
			if(isFirst){
				isFirst=false;%>
			<tr>
				<td class="td_gris_l"  >
					&nbsp;
				</td>
				<td class="td_gris_l"  style="border:1px solid #afafaf;">
					Usuario
				</td>
				<td class="td_gris_l"  style="border:1px solid #afafaf;">
					Primera Lectura
				</td>
				<td class="td_gris_l"  style="border:1px solid #afafaf;">
					Ultima Lectura
				</td>
				<td class="td_gris_l"  style="border:1px solid #afafaf;">
					Lecturas
				</td>
				<td class="td_gris_l"  style="border:1px solid #afafaf;">
					Descargas
				</td>
			</tr>
			<%}%>
			<tr>
				<td class="td_gris_l" width="25%"  >
					&nbsp;
				</td>
				<td class="td_gris_l"  >
					<%=crs.getString("nameUser")%>
				</td>
				<td class="td_gris_l"  >
					<%=ToolsHTML.formatDateToShow(crs.getString("registra"),true)%>
				</td>
				<td class="td_gris_l"  >
					<%=ToolsHTML.formatDateToShow(crs.getString("actualiza"),true)%>
				</td>
				<td class="td_gris_l"  >
					<%=crs.getInt("contador")%>
				</td>
				<td class="td_gris_l"  >
					<%=crs.getInt("descargas")%>
				</td>
			</tr>
		<%}%>
    <%	idDoc = crs.getInt("numgen");
    }%>
    </logic:present>
</table>
<hr style="width:95%">
</form>
</body>
</html>