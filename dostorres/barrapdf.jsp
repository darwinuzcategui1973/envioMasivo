<%@ page import="com.desige.webDocuments.utils.beans.Users,
				java.util.ResourceBundle,
				com.desige.webDocuments.utils.ToolsHTML,
				com.desige.webDocuments.persistent.managers.HandlerDocuments"%>
<%
    Users usuario = (Users)session.getAttribute("user");
	StringBuffer b = new StringBuffer();
	b.append("http://").append(request.getServerName()).append(":").append(request.getServerPort()).append(request.getContextPath());
	
	String name = HandlerDocuments.getNameDocumentFromId(request.getParameter("idDocument"));
	boolean isExpediente = request.getParameter("isExpediente")!=null && request.getParameter("isExpediente").equals("true");
	boolean isImage = false;
	String ext = "";
	if(name!=null) {
		isImage = (name.toLowerCase().endsWith("jpg") || name.toLowerCase().endsWith("jpeg") || name.toLowerCase().endsWith("gif"));
		if(name.indexOf(".")>=0) {
			ext = name.substring(name.indexOf("."));
		}
	}
    ResourceBundle rb = ToolsHTML.getBundle(request);
%>
<html> 
<head> 
<script language="javascript" src="./estilo/funciones.js"></script>

<script language="javascript">
var obj=null;
var http = false;

function firmantes() {
	showPaginaFirmantes(<%=usuario.getIdPerson()%>,<%=request.getParameter("idDocument")%>,<%=request.getParameter("idVersion")%>);
}

function imprimir() {
	document.getElementById("btnImprimir").style.display="none";
	//alert("firstPage=<%=request.getParameter("firstPage")%>");
	obj = window.open("print.jsp?nameFile=<%=b.toString()%>/tmp/job<%=usuario.getIdPerson()%>_<%=request.getParameter("idDocument")%>_<%=request.getParameter("idVersion")%><%=isImage?ext:".pdf"%>&item=<%=session.getAttribute("numberCopies")%>&info=Prepare la impresora&idDocument=<%=request.getParameter("idDocument")%>&idVersion=<%=request.getParameter("idVersion")%>&isExpediente=<%=isExpediente%>&firstPage=<%=request.getParameter("firstPage")%>&idPerson=<%=usuario.getIdPerson()%>","blanco","toolsbar=no,scrollbar=no,width=460,height=400");
}
function cerrarObj() {
	http = null;
	if(obj){
		try {
			obj.close();
		}catch(e){}
	}
	if(navigator.appName == "Microsoft Internet Explorer") {
	  http = new ActiveXObject("Microsoft.XMLHTTP");
	} else {
	  http = new XMLHttpRequest();
	}
	http.open("POST", "updateImpresionAjax.do?idDocument=<%=request.getParameter("idDocument")%>&idVersion=<%=request.getParameter("idVersion")%>&eliminarFile=true");
	http.onreadystatechange=function() {
	  try {
		  if(http.readyState == 4) {
			setTimeout("window.close()",300);
		  }
	  } catch(e){
	  }
	}
	http.send(null);
	pause(1000);	
}

function hideButton() {
	document.getElementById("btnImprimir").style.display="none";
	if(document.getElementById("btnFirmantes")!=null){
		document.getElementById("btnFirmantes").style.display="none";
	}
	try { 
		window.opener.hideBtnImp();
	} catch(e){}
	window.parent.hideBtnImp();
	
}

function pause(millis) {
	var date = new Date();
	var curDate = null;

	do { curDate = new Date(); }
	while(curDate-date < millis);
}

function main() {
	//alert(window.parent.document.getElementById("barrapdf").style.zIndex);
	window.parent.document.getElementById("barrapdf").style.zIndex=20000;
	//alert(window.parent.document.getElementById("barrapdf").style.zIndex);
	
}

//setTimeout("main()",5000);
</script>

</head>
<body id='cuerpo' onUnload="cerrarObj()" oncontextmenu="return false" onkeydown="return false" style="background: url(img/barraSup2.png); background-repeat: no-repeat">
<table border="0"  cellspacing="2" width="100%" cellpadding="2" style="position:absolute;left:0px;top:0px;" >
<tr><td align="right">
<%if(!isExpediente){%>
<input type="button" id="btnFirmantes" value="<%=rb.getString("showDoc.Firmantes")%>" onclick="firmantes()">
<%}%>
<%if(session.getAttribute("numberCopies")!=null){%>
<input style="display:none;" type="button" id="btnImprimir" value="<%=rb.getString("btn.print")%>" onclick="imprimir()">
<%}%>
<%session.removeAttribute("numberCopies");%>
</td></tr>
</table>
<script language="javascript">
document.oncontextmenu = function(){return false;}
</script>
</body>
</html>
