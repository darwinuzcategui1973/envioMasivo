<%@ page import="com.desige.webDocuments.persistent.managers.HandlerBD,
				 com.desige.webDocuments.utils.Constants,
				 com.desige.webDocuments.utils.ToolsHTML,
				 java.util.ResourceBundle,
				 com.desige.webDocuments.utils.beans.Users"%>
<!-- /**
 * Title: optionSearch.jsp<br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>Nothing</li>
 </ul>
 */ -->
<%
 ResourceBundle rb = ToolsHTML.getBundle(request);
 String opt = HandlerBD.getOptionSearch((int)((Users)session.getAttribute("user")).getIdPerson());
 
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
				http.open("POST", "markOptionAjax.do?campo=<%=Constants.COLUMN_SEARCH_NAME%>&columna="+obj.value);
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
<%--ydavila Ticket 001-00-003265
<input type="checkbox" value="A" onclick="sele(this)" <%=opt.indexOf("A")!=-1?"checked":""%> > <%=rb.getString("cbs.name")%><br/>
<input type="checkbox" value="B" onclick="sele(this)" <%=opt.indexOf("B")!=-1?"checked":""%> > <%=rb.getString("cbs.ubicacion")%><br/>
<input type="checkbox" value="C" onclick="sele(this)" <%=opt.indexOf("C")!=-1?"checked":""%> > <%=rb.getString("doc.type")%><br/>
<input type="checkbox" value="D" onclick="sele(this)" <%=opt.indexOf("D")!=-1?"checked":""%> > <%=rb.getString("doc.number")%><br/>
<input type="checkbox" value="E" onclick="sele(this)" <%=opt.indexOf("E")!=-1?"checked":""%> > <%=rb.getString("doc.normISO")%><br/>
<input type="checkbox" value="F" onclick="sele(this)" <%=opt.indexOf("F")!=-1?"checked":""%> > <%=rb.getString("cbs.owner")%><br/>
<input type="checkbox" value="G" onclick="sele(this)" <%=opt.indexOf("G")!=-1?"checked":""%> > <%=rb.getString("showDoc.create")%><br/>
<input type="checkbox" value="H" onclick="sele(this)" <%=opt.indexOf("H")!=-1?"checked":""%> > <%=rb.getString("doc.date")%><br/>
<input type="checkbox" value="I" onclick="sele(this)" <%=opt.indexOf("I")!=-1?"checked":""%> > <%=rb.getString("doc.firmpending")%><br/>
--%>

<%--ydavila Ticket 001-00-003265--%>
<%--<input type="checkbox" value="A" onclick="sele(this)" <%=opt.indexOf("A")!=-1?"checked":""%> > <%=rb.getString("doc.numberLM")%><br/>--%>
<input type="hidden" value="A" > 
<input type="checkbox" value="B" onclick="sele(this)" <%=opt.indexOf("B")!=-1?"checked":""%> > <%=rb.getString("doc.typeLM")%><br/>
<input type="checkbox" value="C" onclick="sele(this)" <%=opt.indexOf("C")!=-1?"checked":""%> > <%=rb.getString("cbs.nameLM")%><br/>
<%--
<input type="checkbox" value="D" onclick="sele(this)" <%=opt.indexOf("D")!=-1?"checked":""%> > <%=rb.getString("cbs.commentLM")%><br/>
--%>
<input type="checkbox" value="D" onclick="sele(this)" <%=opt.indexOf("D")!=-1?"checked":""%> > <%=rb.getString("doc.normISO")%><br/>
<input type="checkbox" value="E" onclick="sele(this)" <%=opt.indexOf("E")!=-1?"checked":""%> > <%=rb.getString("cbs.owner")%><br/>
<input type="checkbox" value="F" onclick="sele(this)" <%=opt.indexOf("F")!=-1?"checked":""%> > <%=rb.getString("showDoc.create")%><br/>
<input type="checkbox" value="G" onclick="sele(this)" <%=opt.indexOf("G")!=-1?"checked":""%> > <%=rb.getString("doc.status")%><br/>
<input type="checkbox" value="H" onclick="sele(this)" <%=opt.indexOf("H")!=-1?"checked":""%> > <%=rb.getString("doc.firmpending")%><br/>
<%--ydavila Ticket 001-00-003265--%>
</form>
</body>
</html>
<script language="javascript">
	if(!document.all){
		try {
			document.getElementById("icono").className='none';
		} catch(e){}
	}
	function opcion() {
		try {
			window.parent.document.getElementById("opt").className='menutip enlace_imagen';
			window.parent.document.getElementById("x").style.display="none";
		} catch(e){}
	}
	
</script>