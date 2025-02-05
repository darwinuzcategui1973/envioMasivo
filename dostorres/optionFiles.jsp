<%@ page import="com.desige.webDocuments.persistent.managers.HandlerBD,
				 com.desige.webDocuments.files.forms.FilesForm,
				 com.desige.webDocuments.utils.Constants,
				 com.desige.webDocuments.utils.ToolsHTML,
				 java.util.ResourceBundle,
				 java.util.ArrayList,
				 com.desige.webDocuments.utils.beans.Users"%>
<!-- /**
 * Title: optionFiles.jsp<br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>Nothing</li>
 </ul>
 */ -->
<%
 ResourceBundle rb = ToolsHTML.getBundle(request);
 String opt = HandlerBD.getOptionFiles((int)((Users)session.getAttribute("user")).getIdPerson());
 
 ArrayList conf = (ArrayList)session.getAttribute("conf");
 Users usuario = (Users)session.getAttribute("user");
 
%>
<html>
    <head>
        <meta content="MSHTML 6.00.2800.1106" name=generator/>
        <link href="estilo/estilo.css" rel="stylesheet" type="text/css"/>
		<script language="javascript">
		
			var http = false;
		    
			function isIE7(){
				return (navigator.appVersion.indexOf("MSIE 7")>0?true:false);
			}
			
			function sele(obj){
				if(navigator.appName == "Microsoft Internet Explorer") {
				  http = new ActiveXObject("Microsoft.XMLHTTP");
				} else {
				  http = new XMLHttpRequest();
				}
				http.open("POST", "markOptionAjax.do?campo=<%=Constants.COLUMN_FILES_NAME%>&columna=,"+obj.value+",");
				http.onreadystatechange=function() {
				  if(http.readyState == 4) {
				  	parent.visualizar(http.responseText);
				  }
				}
				http.send(null);
		    }
		</script>
	</head>
<body class="bodyInternas ppalText">
<form name="opciones"> 
<br/>
<table cellspacing="0" cellpadding="0" border="0" width="100%" >
	<tr>
		<td class="ppalText" style="padding:0 0px 0 0px" align="left"><b>&nbsp;<%=usuario.getLanguage().equals("es")?"Columnas":"Column"%></b></td>
		<td class="ppalText" id="iconox" style="padding:0 0px 0 0px" align="right"><a href="#" style="cursor:pointer" onclick="opcion()"><%=usuario.getLanguage().equals("es")?"Cerrar":"Close"%></a></td>
	</tr>
</table>	
<%
FilesForm obj;
for(int i=0; i<conf.size();i++){
	obj=(FilesForm)conf.get(i);%>
	<input type="checkbox" value="<%=obj.getId().replaceAll("f","")%>" onclick="sele(this)" <%=opt.indexOf(","+String.valueOf(obj.getId().replaceAll("f",""))+",")!=-1?"checked":""%> ><%=obj.getEtiqueta(usuario.getLanguage())%><br/>
<%}%>
</form>
</body>
</html>
<script language="javascript">
	if(!document.all){
		document.getElementById("icono").className='none';
	}
	function opcion() {
		window.parent.document.getElementById("opt").className='menutip enlace_imagen';
		window.parent.document.getElementById("x").style.display="none";
	}
	
</script>