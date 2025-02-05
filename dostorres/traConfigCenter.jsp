<!-- /**
 * Title: configCenter.jsp<br/>
 * Copyright: (c) 2009 Focus Consulting C.A.<br/>
 */ -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.focus.qweb.to.TransactionTO,
                 java.util.Enumeration,
                 java.util.Map" %>
<%
ResourceBundle rb = ToolsHTML.getBundle(request);

Map<Integer,TransactionTO> lista = (Map)session.getAttribute("listTransactionSession");
%>
<html>
    <head>
        <meta content="MSHTML 6.00.2800.1106" name=generator/>
        <meta http-equiv="content-type" content="text/html; utf-8">
        <link href="estilo/estilo.css" rel="stylesheet" type="text/css"/>
        <script>

        	function save(id,obj,save,notify) {
		    	respuesta = "false";
				if(navigator.appName == "Microsoft Internet Explorer") {
				  http = new ActiveXObject("Microsoft.XMLHTTP");
				} else {
				  http = new XMLHttpRequest();
				}
				http.open("POST", "markCheckTransactionAjax.do?id="+id+"&value="+(obj.checked?1:0)+"&save="+save+"&notify="+notify);
				http.onreadystatechange=function() {
				  if(http.readyState == 4) {
					  // hecho
				  }
				}
				http.send(null);
		    }
        	
        </script>
	</head>
<body class="bodyInternas" style="margin:0px;">
<table width="100%">
	<%
	String color0 = "#efefef";
	String color1 = "#efefdf";
	String space = "";
	String spaceTab = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
	String color = color0;
	TransactionTO tra = null;
	int cont = 0;
	int key = 0;
	String sufijo = "";
	boolean isPri = false;
	for(Map.Entry<Integer,TransactionTO> element : lista.entrySet()) {
		color = color.equals(color0)?color1:color0;
		key = element.getKey();
		tra = element.getValue();
		
		space = "";
		if(tra.getTraLevel()>1) {
			for(int i=0; i<tra.getTraLevel();i++) {
				space += spaceTab;
			}
		}
		cont++;
		%>
		<tr class="td_gris_l" style="background-color:<%=color %>">
			<td ><%=space%>[<%=element.getKey() %>] <%=ToolsHTML.replaceAcentos(tra.getTraDescrip()) %></td>
		<%if(tra.getTraActivate()==1){%>
			<td width="10%"><input type="checkbox" name="traSave<%=element.getKey() %>" value="<%=element.getKey() %>" <%=tra.getTraSave()==1?"checked":""%> onclick="save(<%=element.getKey()%>,this,true,false)"/></td>
			<td width="10%"><input type="checkbox" name="traNotify<%=element.getKey() %>" value="<%=element.getKey() %>"  <%=tra.getTraNotify()==1?"checked":""%> onclick="save(<%=element.getKey()%>,this,false,true)" /></td>
		<%} else {%>
			<td width="10%">&nbsp;</td>
			<td width="10%">&nbsp;</td>
		<%}%>
		</tr>
	<%}%>
</table>
</body>
</html>
