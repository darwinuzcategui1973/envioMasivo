<!--
 * Title: copyRightDoc.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
  * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 18/07/2005 (NC) Creation </li>
 *      <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..
 </ul>
-->

<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.Users"%>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    Users usuario = (Users)session.getAttribute("user");
    String nameFile = request.getParameter("nameFile");
    int pos = nameFile.indexOf(".");
    while (nameFile.indexOf(".",pos + 1)>0) {
        pos = nameFile.indexOf(".",pos + 1);
    }
    String ext = nameFile.substring(pos+1).trim();
    String nameEnd = usuario.getUser() + "." + ext;
%>
<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 
<script language=javascript src="./estilo/funciones.js"></script>
<script languaje="javascript">
    function ApplyFile() {
        document.uupload.upFile();
    }

    function showMSG() {
        document.upploadData.target = "_parent";
        var fichero = document.upploadData.fileName.value;
        if(fichero.substring(fichero.length-4)=="html"){
			window.parent.doc.send();
			//alert(window.parent.doc.getMensaje());
			document.upploadData.contenido.value=window.parent.doc.getMensaje();
        }
        document.upploadData.submit();
    }
</script>

<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
</head>
<body class="bodyInternas">
    <table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
        <tr>
            <td class="td_vino" width="100%">
                <%=rb.getString("showDoc.noteTitle")%>
            </td>
        </tr>
        <tr>
            <td class="td_gris_l" width="100%">
                <center>
                    <%=rb.getString("showDoc.note")%>
                </center>
            </td>
        </tr>
        <tr>
            <td align="center">
                <input type="button" class="boton" value="<%=rb.getString("btn.applyChanges")%>" onclick="javascript:showMSG();" />
            </td>
        </tr>
    <form name="upploadData" method="post" action="updateDocCheckOut.do">
<%--        <input type="hidden" name="fileName" value="<%=request.getParameter("nameFile")%>"/>--%>
        <input type="hidden" name="fileName" value="<%=nameEnd%>"/>        
        <input type="hidden" name="idDocument" value="<%=request.getParameter("idDocument")%>"/>
        <input type="hidden" name="idVersion" value="<%=request.getParameter("numVersion")%>"/>
        <input type="hidden" name="idCheckOut" value="<%=request.getParameter("idCheckOut")%>"/>
<%--        <input type="hidden" name="isNewRegister" value="<%=request.getParameter("isNewRegister")%>"/>--%>
        <input type="hidden" name="idNodeSelected" value="<%=request.getParameter("idNodeSelected")%>"/>
        <input type="hidden" name="closeWindow" value="<%=request.getParameter("closeWindow")!=null?request.getParameter("closeWindow"):""%>"/>
        <input type="hidden" name="contenido" value=""/>        
    </form>
    </table>
</body>
</html>
