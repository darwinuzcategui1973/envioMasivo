<!--
 * Title: getDate.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Simón Rodriguez.
 * @author Ing. Nelson Crespo.
 * @version WebDocuments v3.0
 * <br/>
 *       Changes:<br/>

 *      <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..
 * </ul>
-->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>

<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
%>
<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/popcalendar2.js"></script>
<script type="text/javascript" src="./estilo/fechas.js"></script>
<script type="text/javascript">
    function Aceptar(forma){
        if (validarFecha(forma.date.value)){
            opener.document.<%=request.getParameter("nameForm")%>.<%=request.getParameter("field")%>.value = forma.date.value;
            cancelar(forma);
        } else {
            alert("<%=rb.getString("wf.badDate")%>");
        }
    }

    function cancelar(form){
        top.window.close();
    }

</script>
</head>

<body>
<center>
<br/>
<form name="getDate" >
    <table cellSpacing=0 cellPadding=2 align=center border=0 width="95%">
        <tr>
            <td class="titleLeft" style="display:none">
                <%=request.getParameter("text")!=null?rb.getString(request.getParameter("text")):rb.getString("wf.dateExpire")%>
            </td>
            <td style="display:none1">&nbsp;
                <input type="text" id="txtfecha" name="date" readonly="true" maxlength="10" size="12" value="<%=request.getParameter("valueForm")!=null?request.getParameter("valueForm"):""%>" style="left:0px;top:-23px;position:absolute;"/>
                <div style="display:none">
                <input type="button" onclick='popUpCalendar(document.getElementById("txtfecha"), getDate.date, getDate.initialDay, getDate.initialMonth, getDate.initialYear, "yyyy-mm-dd")' value='>>' class="boton" style="width:20px;">
                <input type=button onclick='getDate.date.value=""; getDate.initialDay.value="";  getDate.initialMonth.value="";  getDate.initialYear.value=""; ' value='<%=rb.getString("btn.clear")%>' class="boton">
                <input type=hidden name="initialYear" value='<%= ((request.getParameter("initialYear") != null) ? request.getParameter("initialYear") : "")%>'>
                <input type=hidden name="initialMonth" value='<%= ((request.getParameter("initialMonth") != null) ? request.getParameter("initialMonth") : "")%>'>
                <input type=hidden name="initialDay" value='<%= ((request.getParameter("initialDay") != null) ? request.getParameter("initialDay") : "")%>'>
                </div>
            </td>
        </tr>
        <tr style="display:none">
            <td colspan="2" align="center" >
                <input type="button" class="boton" value="<%=rb.getString("btn.ok")%>" onClick="javascript:Aceptar(this.form);"/>
                    &nbsp;
                <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:cancelar();"/>
            </td>
        </tr>
    </table>
</form>
</center>
</body>
</html>
    <script type="text/javascript" event="onload" for="window">
		setTimeout('popUpCalendar(document.getElementById("txtfecha"), document.getDate.date, document.getDate.initialDay, document.getDate.initialMonth, document.getDate.initialYear, "yyyy-mm-dd")',300);
    </script>
