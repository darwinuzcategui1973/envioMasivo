<%@ page import="com.desige.webDocuments.persistent.managers.HandlerBD,
				 com.desige.webDocuments.utils.Constants,
				 com.desige.webDocuments.utils.ToolsHTML,
				 java.util.ResourceBundle,
				 com.desige.webDocuments.utils.beans.Users"%>
<!-- /**
 * Title: optionSacop.jsp<br/>
 * Copyright: (c) 2024 Focus Consulting C.A.<br/>
 * @author Darwin Uzcategui
  * @version WebDocuments v5.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>Nothing</li>
 </ul>
 */ -->
<%
 ResourceBundle rb = ToolsHTML.getBundle(request);
   String opt = HandlerBD.getOptionSacop    ((int)((Users)session.getAttribute("user")).getIdPerson());
 //String opt = HandlerBD.getOptionPublished((int)((Users)session.getAttribute("user")).getIdPerson());
 
  Users usuario = (Users)session.getAttribute("user");
 
%>
<html>
    <head>
        <meta content="MSHTML 6.00.2800.1106" name=generator/>
        <link href="estilo/estilo.css" rel="stylesheet" type="text/css"/>
		<script language="javascript">
		
			var http = false;
		    
			function sele(obj){
				if(navigator.appName == "Microsoft Internet Explorer") {
				  http = new ActiveXObject("Microsoft.XMLHTTP");
				} else {
				  http = new XMLHttpRequest();
				}
				
				var url = "markOptionAjax.do?campo=<%=Constants.COLUMN_SACOP_NAME%>&columna="+obj.value;
				http.open("POST", url);
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

<%--<input type="hidden"   value="A" > --%>
<%--<input type="checkbox" value="B" onclick="sele(this)" <%=opt.indexOf("B")!=-1?"checked":""%> > <%=rb.getString("showDoc.VerLM")%><br/>--%>
<input type="checkbox" value="A" onclick="sele(this)" <%=opt.indexOf("A")!=-1?"checked":""%> > <%=rb.getString("scp.num")%><br/>
<input type="checkbox" value="B" onclick="sele(this)" <%=opt.indexOf("B")!=-1?"checked":""%> > <%=rb.getString("scp.fechae")%><br/>
<input type="checkbox" value="C" onclick="sele(this)" <%=opt.indexOf("C")!=-1?"checked":""%> > <%=rb.getString("scp.applicant")%><br/>
<input type="checkbox" value="D" onclick="sele(this)" <%=opt.indexOf("D")!=-1?"checked":""%> > <%=rb.getString("scp.emisor")%><br/>
<input type="checkbox" value="E" onclick="sele(this)" <%=opt.indexOf("E")!=-1?"checked":""%> > <%=rb.getString("scp.responsable")%><br/>
<input type="checkbox" value="F" onclick="sele(this)" <%=opt.indexOf("F")!=-1?"checked":""%> > <%=rb.getString("scp.orgsacop")%><br/>
<input type="checkbox" value="G" onclick="sele(this)" <%=opt.indexOf("G")!=-1?"checked":""%> > <%=rb.getString("requestSacop.origen")%><br/>
<input type="checkbox" value="H" onclick="sele(this)" <%=opt.indexOf("H")!=-1?"checked":""%> > <%=rb.getString("scp.fechastimada")%><br/>
<input type="checkbox" value="Z" onclick="sele(this)" <%=opt.indexOf("Z")!=-1?"checked":""%> > <%=rb.getString("scp.fechacierre")%><br/>
<input type="checkbox" value="I" onclick="sele(this)" <%=opt.indexOf("I")!=-1?"checked":""%> > <%=rb.getString("scp.estado")%><br/>
<input type="checkbox" value="J" onclick="sele(this)" <%=opt.indexOf("J")!=-1?"checked":""%> > <%=rb.getString("scp.tracking")%><br/>
<input type="hidden" value="K" >
</form>
</body>
</html>
<script language="javascript">
	if(!document.all){
		if(document.getElementById("icono"))document.getElementById("icono").className='none';
	}
	function opcion() {
		window.parent.document.getElementById("opt").className='menutip enlace_imagen';
		window.parent.document.getElementById("x").style.display="none";
	}
	
</script>