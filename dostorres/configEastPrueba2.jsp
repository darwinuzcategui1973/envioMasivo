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
        	titulo[2]='<%=rb.getString("param.cargosacop")%>';
        	titulo[3]='<%=rb.getString("param.mailSetting")%>';
        	titulo[4]='<%=rb.getString("param.cbsSetting")%>';
        	<%--ydavila Ticket 001-00-003023 --%>        	
        	<%--titulo[5]='<%=rb.getString("param.confFTP")%>';--%>
        	titulo[5]='<%=rb.getString("param.confFT")%>';
        	titulo[6]='<%=rb.getString("param.confPrint")%>';
        	titulo[7]='<%=rb.getString("param.confSystem")%>';
        	titulo[8]='<%=rb.getString("param.msg")%>';
        	titulo[9]='<%=rb.getString("param.msg")%>';
        	titulo[10]='<%=rb.getString("param.msg")%>';
        	titulo[11]='<%=rb.getString("param.msg")%>';
        	titulo[12]='<%=rb.getString("param.msg")%>';
        	titulo[13]='<%=rb.getString("param.msg")%>';
        	titulo[14]='<%=rb.getString("param.msg")%>';
        	titulo[15]='<%=rb.getString("param.msg")%>';
            
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
				d.add(2,0,'<%=rb.getString("param.cargosacop2")%>'<%=ToolsHTML.showSACOP()?",'javascript:ir(2)'":""%>); //2
				d.add(3,0,'<%=rb.getString("param.mailSetting2")%>','javascript:ir(3)'); //3
				d.add(4,0,'<%=rb.getString("param.cbsSetting2")%>','javascript:ir(4)'); //4
				<%-- ydavila Ticket 001-00-003023 --%>
				<%-- d.add(5,0,'<%=rb.getString("param.confFTP2")%>'<%=ToolsHTML.showFTP()?",'javascript:ir(5)'":""%>); //5--%>
				d.add(5,0,'<%=rb.getString("enl.workFlow")%>'<%=ToolsHTML.showFTP()?",'javascript:ir(5)'":""%>); //5
				d.add(6,0,'<%=rb.getString("param.confPrint2")%>','javascript:ir(6)'); //6
				d.add(7,0,'<%=rb.getString("param.confSystem2")%>','javascript:ir(7)'); //7
				d.add(8,0,'<%=rb.getString("param.msg")%>','#'); //8
			        d.add(81,8,'<%=rb.getString("param.nodo.documento")%>','#'); //9
                        d.add(811,81,'<%=rb.getString("param.doc.expirado")%>','javascript:ir(8)'); //10
                        d.add(812,81,'<%=rb.getString("param.doc.borrador")%>','javascript:ir(9)'); //11
			        d.add(82,8,'<%=rb.getString("param.nodo.flujo")%>','#'); //12
			            d.add(821,82,'<%=rb.getString("param.wf.expirado")%>','javascript:ir(10)'); //13
                        d.add(822,82,'<%=rb.getString("param.wf.revision")%>','javascript:ir(11)'); //14
                        d.add(823,82,'<%=rb.getString("param.wf.aprobado")%>','javascript:ir(12)'); //15
                        d.add(824,82,'<%=rb.getString("param.wf.cancelado")%>','javascript:ir(13)'); //16
                    d.add(83,8,'<%=rb.getString("param.cargosacop2")%>','<%=ToolsHTML.showSACOP() ? "#" : ""%>'); //17
                        d.add(831,83,'<%=rb.getString("scp.acciones.por.vencer")%>','<%=ToolsHTML.showSACOP() ? "javascript:ir(14)" : ""%>'); //18
                        d.add(832,83,'<%=rb.getString("scp.acciones.vencidas")%>','<%=ToolsHTML.showSACOP() ? "javascript:ir(15)" : ""%>'); //19
                
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
