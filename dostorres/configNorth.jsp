<!-- /**
 * Title: vacio.jsp<br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simon Rodríguez (SR)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>Nothing</li>
 </ul>
 */ -->
 <%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.DesigeConf"%>
 
 <%
    ResourceBundle rb = ToolsHTML.getBundle(request);
	String path = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath()) + "/";
 %>
 
<html>
    <head>
        <meta content="MSHTML 6.00.2800.1106" name=generator/>
        <link href="estilo/estilo.css" rel="stylesheet" type="text/css"/>
        <style>
			.titleLeftBlack {
				font-family: Sans-serif, Helvetica, Arial, Verdana;
				font-weight:bold;
				font-size: 9pt;
				color: #000000;
				text-align: left;
				vertical-align: middle;
				padding-left:10pt;
			}        
        </style>
        <script>
        	function setTitulo(titulo) {
        		document.getElementById("titulo").innerHTML=titulo; 
        	}
        	
            var winWidth  = 600;
            var winHeight = 800;
            function setDimensionScreen() {
                if (screen.availWidth) {
                    winWidth = screen.availWidth;
                }
                if (screen.availHeight){
                    winHeight = screen.availHeight - 100;
                }
            }

            function abrirVentana(pagina,width,height,nameWin) {
            	setDimensionScreen();
                //alert('Mostrando a: ' + pagina);
                var hWnd = null;
                var left = getPosition(winWidth,width);
                var top = getPosition(winHeight,height);
                hWnd = window.open("<%=path%>"+pagina, nameWin, "resizable=yes,scrollbars=yes,statusbar=yes,width="+width+",height="+height+",left="+left+",top="+top);
            }
            function getPosition(totalValue, value) {
            	var calculo = (totalValue - value) / 2;
            	if (calculo < 0) {
            		return 0;
            	}
            	return calculo;
            }

        </script>
	</head>
<body class="bodyInternas" style="margin:20px;background:#c2d2e0;">
<table cellspacing="0" cellpadding="0"  style="border:0px solid #afafaf;" width="100%" height="100%" bgcolor="#c2d2e0">
<tr>
	<td width="99%" id="titulo" class="titleLeftBlack" ><%=rb.getString("admin.config2")%></td>
	<td ><img src="icons/help.png" class="helpBTN" alt='Mostrar Ayuda' onclick="abrirVentana('download/Manual_del_Administrador_configuracion_tecnica.pdf',800,800,'help')"></td>
</tr>
</table>
</body>
</html>
