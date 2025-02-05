<!-- /**
 * Title: loadDocument.jsp<br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Jairo Rivero
 * @version WebDocuments v3.0
 */ -->
<%@ page import="com.desige.webDocuments.utils.ToolsHTML"%>
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.Users"%>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    Users usuario = (Users)session.getAttribute("user");
    String nameFile = request.getParameter("nameFile");
    int pos = nameFile.indexOf(".");
    while (nameFile.indexOf(".", pos + 1) > 0) {
        pos = nameFile.indexOf(".",pos + 1);
    }
    String ext = nameFile.substring(pos+1).trim();
    String nameEnd = usuario.getUser() + "." + ext;
    boolean showAppletDigitalizar = (ext != null && "pdf".equals(ext.toLowerCase()) ? true : false);
%>

<html>
<head>
<title>

</title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
	String.prototype.trim = function() { return this.replace(/^\s*|\s*$/g,""); };
	
    function showMSG() {
    	var formulario = document.getElementById("upploadData");
    	
    	if(formulario.fileNameNew.value.length!=0) {
    		formulario.isFileScanned.value = "false";
    	}
    
    	if (formulario.isFileScanned.value == "false" && !validarExtensionDoble(formulario)) { 
    		return;
    	}
    	
		with(formulario) {
			if(isFileScanned.value == "false") {
				var cad1 = fileName.value.substring(fileName.value.lastIndexOf(".")+1);
				var cad2 = fileNameNew.value.substring(fileNameNew.value.lastIndexOf(".")+1);
				
				if(cad1.toLowerCase() != cad2.toLowerCase()) {
					alert("El archivo debe ser de tipo \""+cad1+"\"");
					return;
				}
			}
			
        	target = "_parent";
        	submit();
        }
    }
    
    function validarExtensionDoble(forma) {
    	if(forma.fileNameNew.value.length==0) {
    		return false;
    	}
    	cad = forma.fileNameNew.value;
    	if(isDoblePunto(cad)) {
    		alert("Los archivos a cargar deben poseer solo una extensión");
    		return false;
    	}
    	while(cad.indexOf(".")!=-1) {
    		cad = cad.substring(cad.indexOf(".")+1);

    	}
    	cad = "."+cad;
    	
    	return true;
    }
    
    function isDoblePunto(name) {
    	name = name.replace(/\\/g,"/");
    	var arr = name.split("/");
    	name = arr[arr.length-1];
    	var nPunto = 0;
    	while(name.indexOf(".")!=-1 && nPunto<2) {
    		nPunto++;
    		name = name.substring(name.indexOf(".")+1);
    	}
		return (nPunto!=1);
    }
    
</script>
</head>
<body>
<form name="upploadData" id="upploadData" method="post" action="updateDocCheckOut.do" enctype="multipart/form-data">
<input type="hidden" name="fileName" value="<%=nameEnd%>"/>        
<input type="hidden" name="idDocument" value="<%=request.getParameter("idDocument")%>"/>
<input type="hidden" name="idCheckOut" value="<%=request.getParameter("idCheckOut")%>"/>
<input type="hidden" name="idNodeSelected" value="<%=request.getParameter("idNodeSelected")%>"/>
<input type="hidden" name="closeWindow" value="<%=request.getParameter("closeWindow")!=null?request.getParameter("closeWindow"):""%>"/>
<input type="hidden" name="isFileScanned" value="false"/>
<input type="hidden" name="idNodeSelected" value="null"/>
<input type="hidden" name="closeWindow" value="true"/>

<table width="103%" height="107%" border="0" style="position:absolute;left:0px;top:0px" cellspacing="0" cellpadding="0">
<tr>
	<td  bgcolor="white" height="80%" align="center">
	    <table cellSpacing="0" cellPadding="2" align="center" border="0" >
	        <tr>
	            <td class="td_gris_l" style="text-align:center">
	               <%--ydavila <b>El tipo de archivo esta configurado como no editable.</br>(extensi&oacute;n <%=nameEnd.substring(nameEnd.indexOf(".")+1)%>) </b></br>
	                </br>
	                Para versionar este tipo de documento debe subir un nuevo archivo con los cambios,</br>
	                puede seleccionar un archivo existente o subirlo directamente desde el Scanner</br></br></br>
	                </br>
	                </br>
	                por favor seleccione el nuevo archivo y luego presione <%=rb.getString("btn.applyChanges")%>.
	               --%>
	                <b><%=rb.getString("loaddoclinea1")%></br>(<%=rb.getString("loaddoclinea2")%>
	                <%=nameEnd.substring(nameEnd.indexOf(".")+1)%>) </b></br>	        
	                </br>
	                <%=rb.getString("loaddoclinea3")%></br>
	                <%=rb.getString("loaddoclinea4")%>
	                </br></br></br>
	                </br>
	                </br>
	                <%=rb.getString("loaddoclinea5")%> <%=rb.getString("btn.applyChanges")%>.
	            </td>
	        </tr>
	        <tr>
	            <td  width="100%" align="center" >
	            	<input type="file" name="fileNameNew" size="80">
	            </td>
	        </tr>
	        <tr>
	        	<td>
	        	&nbsp;
	        	</td>
	        </tr>
	        <tr>
	            <td class="td_gris_l" style="text-align:center;display:<%= showAppletDigitalizar ? ";" : "none;" %>"><br/>
	                <%=rb.getString("loaddoclinea5")%> <%=rb.getString("btn.applyChanges")%>.
	            </td>
	        </tr>
	        <tr>
	        	<td align="center" style="display:<%= showAppletDigitalizar ? ";" : "none;" %>">
	        	    <iframe name="scanner" id="scanner" src="<%= showAppletDigitalizar ? "digitalizar.jsp" : "" %>" style="height:22px;width:235px;" FRAMEBORDER="0" scrolling="NO" scroll="no"></iframe>
	        	</td>
	        </tr>
	    </table>
	</td>
</tr>
<tr>
	<td class="bodyInternas" valign="top" >
	    <table cellSpacing="10" cellPadding=2 align=center border="0" width="100%" >
	        <tr>
	            <td class="td_vino" width="100%">
	                Copyright
	            </td>
	        </tr>
	        <tr>
	            <td class="td_gris_l" width="100%">
	                <center>
	                    <%=rb.getString("archprotegido")%>
	                </center>
	            </td>
	        </tr>
	        <tr>
	            <td align="center">
	                <input type="button" class="boton" value="<%=rb.getString("btn.applyChanges")%>" onclick="javascript:showMSG();" />
	            </td>
	        </tr>
	    </table>
	</td>
</tr>
</table>
</form>
</body>
</html>
