<!-- /**
 * Title: editDocument.jsp<br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 18/07/2005 Creación </li>
 </ul>
 */ -->
<%@ page import="com.desige.webDocuments.utils.ToolsHTML"%>
<%
    String parameters = ToolsHTML.parseParameters(request);
    String file = "viewDocToEdit.jsp" + parameters;
    String pie = "copyRightDoc.jsp" + parameters;
%>
<html>
<head>
<title>

</title>
<jsp:include page="meta.jsp" /> 
</head>

<script language="JavaScript">
    function descargar() {
        window.opener.setNull();
    }
</script>

<frameset rows="*,120" cols="*" frameborder="NO" border="0" framespacing="0" onUnload="javascript:descargar();">
    <frame id="topEdit" src="<%=file%>" name="doc" scrolling="YES" noresize/>
    <frame id="footEdit" src="<%=pie%>" name="info">
</frameset>
<noframes>
</noframes>
</html>
