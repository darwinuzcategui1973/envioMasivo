<!--
 * Title: copyRight.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
  * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 14/05/2005 (NC) Creation </li>
 *      <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..
 </ul>
-->

<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.persistent.managers.HandlerBD,
                 com.desige.webDocuments.utils.ToolsHTML"%>  
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
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

    function showMSG(formulario) {
	    <!-- TODO: CAMBIO PARA DOBLE VERSION -->
    	if (!validarExtensionDoble(formulario)) { 
    		return;
    	}
        document.upploadData.target = "_parent";
        document.upploadData.submit();
    }

   <!-- TODO: CAMBIO PARA DOBLE VERSION -->
    function validarExtensionDoble(forma) {
    	if(forma.nameFileParalelo.value.length!=0) {
    		return true;
    	}
    	cad = forma.fileName.value;
    	while(cad.indexOf(".")!=-1) {
    		cad = cad.substring(cad.indexOf(".")+1);
    	}
    	cad = "."+cad;
    	
    	if(extensionDouble.indexOf(cad)!=-1) {
    		alert('<%=rb.getString("err.insertViewFile")%>');
    		document.getElementById("paralelo").style.display="";
    		return false;
    	}
    	return true;
    }
    
    <!-- TODO: CAMBIO PARA DOBLE VERSION -->
    function requiereView(forma) {
    	cad = forma.fileName.value;
    	while(cad.indexOf(".")!=-1) {
    		cad = cad.substring(cad.indexOf(".")+1);
    	}
    	cad = "."+cad;

   		document.getElementById("paralelo").style.display="none";
    	if(extensionDouble.indexOf(cad)!=-1) {
    		document.getElementById("paralelo").style.display="";
    	}
    }
	<!-- TODO: CAMBIO PARA DOBLE VERSION -->
	var extensionDouble="";
	<%
	try {
		// validar o no el navegador - para este caso ie7
		String valor = HandlerParameters.PARAMETROS.getFileDoubleVersion();
		out.println("extensionDouble=\"".concat(valor==null?"":valor).concat("\";"));
	} catch (Exception e) {
		e.printStackTrace();
	}
	%>
</script>
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
</head>
<body class="bodyInternas">
<form name="upploadData" method="post" action="updateRegister.do" enctype="multipart/form-data">
    <table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
        <tr>
            <td class="td_vino" width="100%">
                <%=rb.getString("showDoc.noteTitle")%>
                **************************************************
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
				<table border="0">
		            <tr id="paralelo" style="display:none">
		                <td height="26" class="titleLeft" style="background: url(img/btn140.png); background-repeat: no-repeat" valign="middle">
		                    &nbsp;&nbsp;&nbsp;&nbsp;<%=rb.getString("doc.upFile2")%>&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;
		                </td>
		                <td class="td_gris_l">
		                    <input type="file" name="nameFileParalelo"/>
		                </td>
		            </tr>
		        </table>     
            </td>
        </tr>
        <tr>
            <td align="center">
                <input type="button" class="boton" value="<%=rb.getString("btn.applyChanges")%>" onclick="javascript:showMSG(this.form);" />
            </td>
        </tr>
        <input type="hidden" name="fileName" value="<%=request.getParameter("nameFile")%>"/>
        <input type="hidden" name="idDocument" value="<%=request.getParameter("idDocument")%>"/>
        <input type="hidden" name="numVer" value="<%=request.getParameter("idVersion")%>"/>
        <input type="hidden" name="isNewRegister" value="<%=request.getParameter("isNewRegister")%>"/>
        <input type="hidden" name="idNodeSelected" value="<%=request.getParameter("idNodeSelected")%>"/>
        <input type="hidden" name="closeWindow" value="<%=request.getParameter("closeWindow")!=null?request.getParameter("closeWindow"):""%>"/>
        <input type="hidden" name="idWF" value="<%=request.getParameter("idWF")%>"/>
    </table>
</form>
</body>
</html>
<script language="javascript">
requiereView(document.upploadData);
</script>
