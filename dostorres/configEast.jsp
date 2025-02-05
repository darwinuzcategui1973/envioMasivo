<!-- /**
 * Title: configEast.jsp<br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 */ -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.Constants,
                 com.desige.webDocuments.utils.DesigeConf"%>
 
 <%
    ResourceBundle rb = ToolsHTML.getBundle(request);
 
	//request.getSession().setAttribute(Constants.MODULO_ACTIVO, Constants.MODULO_ADMINISTRACION_TECNICA);
 %>
 
<html>
    <head>
        <meta content="MSHTML 6.00.2800.1106" name=generator/>
        <link href="estilo/estilo.css" rel="stylesheet" type="text/css"/>
        <link rel="StyleSheet" href="css/dtree.css" type="text/css" />

        <script type="text/javascript" src="script/dtree.js"></script>
        <script>
			var d = '';  // tree
			
        	var titulo = new Array();
        	titulo[0]='';
        	titulo[1]='<%=rb.getString("param.logSettings")%>';
           	titulo[2]='<%=rb.getString("param.mailSetting")%>';
        	<%--ydavila Ticket 001-00-003023 --%>        	
        	<%--titulo[5]='<%=rb.getString("param.confFTP")%>';--%>
        	
        	function ir(nodo) {
        		window.parent.frames['configCenter'].ir(nodo);
        		window.parent.frames['configNorth'].setTitulo(titulo[nodo]);
        	}
        	
        </script>
	</head>
<body class="bodyInternas" style="margin:0px;">
<table cellspacing="0" cellpadding="0"  style="border:0px solid #afafaf;padding:5px" width="100%" height="100%">
<tr>
	<td valign="top" style="textDecoration:none;">
			<div class="item">
				<script type="text/javascript">
				<!--
				d = new dTree('d');
				d.add(0,-1,'<%=rb.getString("admin.config2")%>');   //1
				d.add(1,0,'<%=rb.getString("param.logSettings2")%>','javascript:ir(1)'); //1
				d.add(3,0,'<%=rb.getString("param.mailSetting2")%>','javascript:ir(3)'); //3
				<%-- ydavila Ticket 001-00-003023 --%>
				<%-- d.add(5,0,'<%=rb.getString("param.confFTP2")%>'<%=ToolsHTML.showFTP()?",'javascript:ir(5)'":""%>); //5--%>
				
			
				
				/*
				d.add(1,0,'Node 1','default.html');
				d.add(2,0,'Node 2','default.html');
				d.add(3,1,'Node 1.1','default.html');
				d.add(4,0,'Node 3','default.html');
				d.add(5,3,'Node 1.1.1','default.html');
				d.add(6,5,'Node 1.1.1.1','default.html');
				d.add(7,0,'Node 4','default.html');
				d.add(8,1,'Node 1.2','default.html');
				d.add(9,0,'My Pictures','default.html','Pictures I\'ve taken over the years','','','img/imgfolder.gif');
				d.add(10,9,'The trip to Iceland','default.html','Pictures of Gullfoss and Geysir');
				d.add(11,9,'Mom\'s birthday','default.html');
				d.add(12,0,'Recycle Bin','default.html','','','img/trash.gif');
				*/
				document.write(d);
				//-->
				</script>
			</div>
	</td>
</tr>
</table>
</body>
</html>
