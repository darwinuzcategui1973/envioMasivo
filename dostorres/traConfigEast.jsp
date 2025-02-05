<!-- /**
 * Title: traConfigEast.jsp<br/>
 * Copyright: (c) 2019 Focus Consulting C.A.<br/>
 */ -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.DesigeConf"%>
 
 <%
    ResourceBundle rb = ToolsHTML.getBundle(request);
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
        	titulo[1]='<%=rb.getString("transaction.option01")%>';
        	titulo[2]='<%=rb.getString("transaction.option02")%>';
        	titulo[3]='<%=rb.getString("transaction.option03")%>';
        	titulo[4]='<%=rb.getString("transaction.option04")%>';
        	titulo[5]='<%=rb.getString("transaction.option05")%>';
        	titulo[6]='<%=rb.getString("transaction.option06")%>';
        	titulo[7]='<%=rb.getString("transaction.option07")%>';
        	titulo[8]='<%=rb.getString("transaction.option08")%>';
        	titulo[9]='<%=rb.getString("transaction.option09")%>';
        	titulo[10]='<%=rb.getString("transaction.option10")%>';
        	titulo[11]='<%=rb.getString("transaction.option11")%>';
        	titulo[12]='<%=rb.getString("transaction.option12")%>';
            
        	function ir(nodo) {

        		//window.parent.frames['configCenter'].ir(nodo);
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
				d.add(0,-1,'<%=rb.getString("transaction.option")%>');   //1
				d.add(1,0,'<%=rb.getString("transaction.option01")%>','javascript:ir(1)'); //1
				d.add(2,0,'<%=rb.getString("transaction.option02")%>','javascript:ir(2)'); //2
				d.add(3,0,'<%=rb.getString("transaction.option03")%>','javascript:ir(3)'); //3
				d.add(4,0,'<%=rb.getString("transaction.option04")%>','javascript:ir(4)'); //4
				d.add(5,0,'<%=rb.getString("transaction.option05")%>','javascript:ir(5)'); //5
				d.add(6,0,'<%=rb.getString("transaction.option06")%>','javascript:ir(6)'); //6
				d.add(7,0,'<%=rb.getString("transaction.option07")%>','javascript:ir(7)'); //7
				d.add(8,0,'<%=rb.getString("transaction.option08")%>','javascript:ir(8)'); //8
				d.add(9,0,'<%=rb.getString("transaction.option09")%>','javascript:ir(9)'); //9
				d.add(10,0,'<%=rb.getString("transaction.option10")%>','javascript:ir(10)'); //10
				d.add(11,0,'<%=rb.getString("transaction.option11")%>','javascript:ir(11)'); //11
				d.add(12,0,'<%=rb.getString("transaction.option12")%>','javascript:ir(12)'); //12
				document.write(d);
				//-->
				</script>
			</div>
	</td>
</tr>
</table>
</body>
</html>
