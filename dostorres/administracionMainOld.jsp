<!--
/**
 * Title: administracionMainOld.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelsón Crespo.(NC)
 * @author Ing. Simon Rodriguez.(SR)
 * @version WebDocuments v3.0
 * <br/>
 *          Changes:<br/>
 * <ul>
 *          <li> 25/04/2006 (SR) Se sustituyo /WebDocuments/.. por ./..
 * </ul>
 */ 
 -->

<%@ page contentType="text/html; charset=iso-8859-1"
 language="java" import="java.util.ResourceBundle,
                         com.desige.webDocuments.utils.ToolsHTML,
                         com.desige.webDocuments.persistent.managers.HandlerGrupo,
                         com.desige.webDocuments.utils.beans.Users,
                         com.desige.webDocuments.utils.DesigeConf"%>
<%@ page import="com.desige.webDocuments.persistent.managers.HandlerBD" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%
    if(request.getAttribute("visible")==null || !String.valueOf(request.getAttribute("visible")).equals("true") ) {
    	response.sendRedirect("administracion.do");
    }

    Users usuario = (Users) session.getAttribute("user");
    if (usuario==null) {
        response.sendRedirect("errorSession.jsp");
    }
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
    String mails = HandlerGrupo.getEmailGrupos(null);
    
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<style>
.fondoOscuro{
	top:0px;
	left:0px;
	margin: 0px auto;
	background-color: #000000;
	position: absolute;
	opacity: 0.6;
	-moz-opacity: 0.6;
	-khtml-opacity: 0.6;
	filter: alpha(opacity=60);
	z-index:50;	
}
#caja2 {
	color:white;
	font-weight:bold;
	font-family: Sans-serif, Helvetica, Arial, Verdana ;
	font-size:12px;
	position: absolute;
	z-index:100;	
	border:1px solid white;
}
#caja3 {
	margin: 0px auto;
	background-color: #ffffff;
	position: absolute;
	opacity: 0.6;
	-moz-opacity: 0.6;
	-khtml-opacity: 0.6;
	filter: alpha(opacity=60);
	z-index:60;	
}
p.negro {color: #000;}
	
	
}
</style>

<script language=javascript src="./estilo/funciones.js"></script>
<script language="JavaScript">
	
	function clear(){
		window.parent.clear();
	}
	
    setDimensionScreen();

    function abrirVent(pagina,width,height) {
            var hWnd = null;
            var left = getPosition(winWidth,width);
            var top = getPosition(winHeight,height);

            hWnd = window.open(pagina, "WebDocuments", "resizable=yes,scrollbars=yes,statusbar=yes,width="+width+",height="+height+",left="+left+",top="+top);
    }

	function validar(){
        if (document.login.docSearch.value.length>0){
            alert('<%=rb.getString("err.notDocumentSearch")%>');
        }else{
		    document.login.submit();
        }
	}
    function salvar(){
        document.perfil.submit();
    }

	function administrarCancelar(){
		document.getElementById("caja").style.display="none";
		document.getElementById("caja2").style.display="none";
		document.getElementById("caja3").style.display="none";
	}

	function administrar(){

		medidas = getWindowXY();
		ancho=medidas[0]-150;
		largo=medidas[1]-100;

    	var frm = document.getElementById("frmConfig");
    	frm.style.width='';
    	frm.style.height='';

    
    	var obj = document.getElementById("caja");
    	obj.style.display="";
    	obj.style.width=medidas[0]+"px";
    	obj.style.height=medidas[1]+"px";
    	
    	var obj2 = document.getElementById("caja2");
    	obj2.style.display="";
    	obj2.style.left=((medidas[0]-ancho)/2)+"px";
    	obj2.style.top=((medidas[1]-largo)/2)+"px";
    	obj2.style.width=ancho+"px";
    	obj2.style.height=largo+"px";
    	obj2.className="bodyInternas";
    	//obj2.style.backgroundColor="gray";
    	//obj2.style.backgroundImage.src=url("img/fondoGris2.jpg");
    	
    	var marco = document.getElementById("caja3");
    	margen=10;
    	marco.style.display="";
    	marco.style.left=((medidas[0]-ancho)/2)-margen+"px";
    	marco.style.top=((medidas[1]-largo)/2)-margen+"px";
    	marco.style.width=ancho+(margen*2)+"px";
    	marco.style.height=largo+(margen*2)+"px";
    	
    	
    	var obj3 = document.getElementById("frmConfig");
    	obj3.style.left="0px";
    	obj3.style.top="0px";
    	//obj3.width=obj2.style.width;
    	obj3.width="100%";
    	obj3.height="100%";
    	
    }
    
    function cancelarArea() {
    	var obj = document.getElementById("caja");
    	obj.style.display="none";
    	var obj2 = document.getElementById("caja2");
    	obj2.style.display="none";
    }
	
</script>
<style>
div.iconoPanel {
    float: left;
    overflow: hidden;
    padding: 5px;;
    width: 138px;
    text-align: center;
    
}
div.iconoImage {
    text-align: center;
    width: 138px;
}
div.iconoTexto {
	font-family:Tahoma,Helvetica,Verdana,Serif;
    text-align: center;
    color: #4f4f4f;
    font-size:9pt;
}

</style>
</head>

<body class="bodyInternas2" >
    <logic:present name="user" scope="session">
<table width="100%" border="1" height="100%">
    <tr>
        <td colspan="2">
            <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border=0>
                <tbody>
                    <tr>
                        <td class="td_title_bc" width="*" height="18">
				            <%=rb.getString("enl.Admin")%>
                        </td>
                    </tr>
                </tbody>
            </table>
        </td>
    </tr>
    <tr>
        <td class="td_title_bc barraBlue"><%=rb.getString("admin.title1")%></td>
        <td class="td_title_bc barraBlue"><%=rb.getString("admin.title2")%></td>
    </tr>
    <tr>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td colspan="2">
        	<div class="iconoPanel">
        		<div class="iconoImage"><img src="img/user33_2.jpg"></div>
        		<div class="iconoTexto">
					<a href="newUserGrupo.do" onclick="javascript:window.parent.hacer('opc_1');" class="ahreMenu">
		                <%=rb.getString("admin.cuentas")%>
		            </a>
        		</div>
        	</div>
        	<div class="iconoPanel">
        		<div class="iconoImage"><img src="img/user33_2.jpg"></div>
        		<div class="iconoTexto">
					<a href="newUserGrupo.do" onclick="javascript:window.parent.hacer('opc_1');" class="ahreMenu">
		                <%=rb.getString("admin.cuentas")%>
		            </a>
        		</div>
        	</div>
        	<div class="iconoPanel">
        		<div class="iconoImage"><img src="img/user33_2.jpg"></div>
        		<div class="iconoTexto">
					<a href="newUserGrupo.do" onclick="javascript:window.parent.hacer('opc_1');" class="ahreMenu">
		                <%=rb.getString("admin.cuentas")%>
		            </a>
        		</div>
        	</div>
        	<div class="iconoPanel">
        		<div class="iconoImage"><img src="img/user33_2.jpg"></div>
        		<div class="iconoTexto">
					<a href="newUserGrupo.do" onclick="javascript:window.parent.hacer('opc_1');" class="ahreMenu">
		                <%=rb.getString("admin.cuentas")%>
		            </a>
        		</div>
        	</div>
        	<div class="iconoPanel">
        		<div class="iconoImage"><img src="img/user33_2.jpg"></div>
        		<div class="iconoTexto">
					<a href="newUserGrupo.do" onclick="javascript:window.parent.hacer('opc_1');" class="ahreMenu">
		                <%=rb.getString("admin.cuentas")%>
		            </a>
        		</div>
        	</div>
        	<div class="iconoPanel">
        		<div class="iconoImage"><img src="img/user33_2.jpg"></div>
        		<div class="iconoTexto">
					<a href="newUserGrupo.do" onclick="javascript:window.parent.hacer('opc_1');" class="ahreMenu">
		                <%=rb.getString("admin.cuentas")%>
		            </a>
        		</div>
        	</div>
        	<div class="iconoPanel">
        		<div class="iconoImage"><img src="img/user33_2.jpg"></div>
        		<div class="iconoTexto">
					<a href="newUserGrupo.do" onclick="javascript:window.parent.hacer('opc_1');" class="ahreMenu">
		                <%=rb.getString("admin.cuentas")%>
		            </a>
        		</div>
        	</div>
        	<div class="iconoPanel">
        		<div class="iconoImage"><img src="img/user33_2.jpg"></div>
        		<div class="iconoTexto">
					<a href="newUserGrupo.do" onclick="javascript:window.parent.hacer('opc_1');" class="ahreMenu">
		                <%=rb.getString("admin.cuentas")%>
		            </a>
        		</div>
        	</div>
        	<div class="iconoPanel">
        		<div class="iconoImage"><img src="img/user33_2.jpg"></div>
        		<div class="iconoTexto">
					<a href="newUserGrupo.do" onclick="javascript:window.parent.hacer('opc_1');" class="ahreMenu">
		                <%=rb.getString("admin.cuentas")%>
		            </a>
        		</div>
        	</div>
        	<div class="iconoPanel">
        		<div class="iconoImage"><img src="img/user33_2.jpg"></div>
        		<div class="iconoTexto">
					<a href="newUserGrupo.do" onclick="javascript:window.parent.hacer('opc_1');" class="ahreMenu">
		                <%=rb.getString("admin.cuentas")%>
		            </a>
        		</div>
        	</div>
        	<div class="iconoPanel">
        		<div class="iconoImage"><img src="img/user33_2.jpg"></div>
        		<div class="iconoTexto">
					<a href="newUserGrupo.do" onclick="javascript:window.parent.hacer('opc_1');" class="ahreMenu">
		                <%=rb.getString("admin.cuentas")%>
		            </a>
        		</div>
        	</div>
        	<div class="iconoPanel">
        		<div class="iconoImage"><img src="img/user33_2.jpg"></div>
        		<div class="iconoTexto">
					<a href="newUserGrupo.do" onclick="javascript:window.parent.hacer('opc_1');" class="ahreMenu">
		                <%=rb.getString("admin.cuentas")%>
		            </a>
        		</div>
        	</div>
        	<div class="iconoPanel">
        		<div class="iconoImage"><img src="img/user33_2.jpg"></div>
        		<div class="iconoTexto">
					<a href="newUserGrupo.do" onclick="javascript:window.parent.hacer('opc_1');" class="ahreMenu">
		                <%=rb.getString("admin.cuentas")%>
		            </a>
        		</div>
        	</div>
        </td>
    </tr>
    <tr>
        <td>
            <img src="img/user33_2.jpg">
            <a href="newUserGrupo.do" onclick="javascript:window.parent.hacer('opc_1');" class="ahreMenu">
                <%=rb.getString("admin.cuentas")%>
            </a>
        </td>
        <td>
            <img src="img/parameter33.jpg">
            <a href="#" onclick="administrar()" class="ahreMenu" >
                <%=rb.getString("admin.config")%>
            </a>
        </td>
    </tr>
    <tr>
        <td >
        	<img src="img/addUser33.jpg">
            <a href="newUser.do" onclick="javascript:window.parent.hacer('opc_3');" class="ahreMenu">
                <%=rb.getString("admin.usuario")%>
            </a>
        </td>
        <td >
            <img src="img/sendMail33.jpg">
            	<%if(!ToolsHTML.isNotifyEmail()){%>
	                <a href="#" onclick="javascript:alert('<%=rb.getString("E0131")%>')" class="ahreMenu">
	                    <%=rb.getString("admin.mensaje")%>
	                </a>
                <%}else{%>
	                <a href="loadMailAll.do"<%=mails%> onclick="javascript:window.parent.hacer('opc_4');" class="ahreMenu">
	                    <%=rb.getString("admin.mensaje")%>
	                </a>
                <%}%>
        </td>
    </tr>
    <tr>
        <td >
            <img src="img/addGroup33.jpg">
            <a href="newGrupo.do" onclick="javascript:window.parent.hacer('opc_5');" class="ahreMenu">
                <%=rb.getString("admin.grupo")%>
            </a>
        </td>
        <td>
            <img src="img/normas33.jpg">
            <a href="javascript:abrirVent('loadNorms.do',700,500)" onclick="javascript:window.parent.hacer('opc_6');" class="ahreMenu">
                <%=rb.getString("norms.update")%>  
            </a>
        </td>
    </tr>
    <tr>
        <td class="ahreMenu">
            <img src="img/candado33.jpg">
            <a href="segUserGrupo.do" onclick="javascript:window.parent.hacer('opc_7');" class="ahreMenu">
                <%=rb.getString("admin.seguridad")%>
            </a>
        </td>
        <td>
            <img src="img/typeDoc33.jpg">
            <a href="javascript:abrirVentana('loadTypeDoc.do',700,500)" onclick="javascript:window.parent.hacer('opc_8');" class="ahreMenu">
                <%=rb.getString("typeDoc.update")%>
            </a>
        </td>
    </tr>
    <tr>
        <td class="ahreMenu">
            <img src="img/passUser33.jpg">
                <a href="clave.do" onclick="javascript:window.parent.hacer('opc_9');" class="ahreMenu">
                <%=rb.getString("admin.clave")%>
                </a>
        </td>
         <td>
            <img src="img/areas33.jpg">
            <a href="javascript:abrirVentana('loadArea.do',700,500)" onclick="javascript:window.parent.hacer('opc_10');" class="ahreMenu">
                <%=rb.getString("admin.areas")%>
            </a>
        </td>
    </tr>
        <tr>
            <td class="ahreMenu">
                <img src="img/editUser33.jpg">
                <a href="loadUsersToEdit.do?loadUser=true" onclick="javascript:window.parent.hacer('opc_11');" class="ahreMenu">
                    <%=rb.getString("admin.changeUserGroup")%>
                </a>
            </td>
            <td>
                <img src="img/cargos33.jpg">
                <a href="javascript:abrirVentanaSinResize('loadCargo.do',700,500)" onclick="javascript:window.parent.hacer('opc_12');" class="ahreMenu">
                    <%=rb.getString("admin.cargos")%>
                </a>
            </td>
        </tr>        
        <tr>
            <td class="ahreMenu">
                <img src="img/editGroup33.jpg">
                <a href="loadUsersToEdit.do?goTo=loadGroups" onclick="javascript:window.parent.hacer('opc_13');" class="ahreMenu">
                    <%=rb.getString("admin.changeGroup")%>
                </a>
            </td>
            <td>
               <%-- Luis Cisneros 19/04/07 Mostrar opciones de SACOP --%>
               <%=ToolsHTML.menuSACOP(application, rb)%>   
            </td>
        </tr>
    <!-- SIMON 15 DE JULIO 2005 INCIO -->
        <tr>
            <td class="ahreMenu">
                <img src="img/editSession33.jpg">
                <a href="loadUsersToSession.do?goTo=loadGroups" onclick="javascript:window.parent.hacer('opc_15');" class="ahreMenu">
                   <%=rb.getString("session.admin")%>
                </a>
            </td>
            <td>
               <%if(ToolsHTML.showFTP()){%>
               <%=ToolsHTML.menuFTP(application, rb)%>
               <%}%>
            </td>
        </tr>
    <tr>
        <td></td>
            <td>
               <%=ToolsHTML.menuInTouch(application, rb)%>            
            </td>
    </tr>
    
    <% 
    	String sMigracion = (String)DesigeConf.getProperty("migracionOn");
    	String sMigracionValidacion = (String)DesigeConf.getProperty("migracionValidacionOn");
      	if(sMigracionValidacion!=null && "1".equals(sMigracionValidacion)){
    %>         
    <tr>
        <% if (ToolsHTML.showFiles()) { %>
        <td>
           <img src="img/normas.gif">
           <a href="filesNew.do" onclick="javascript:window.parent.hacer('opc_19');" class="ahreMenu">
           	<%=rb.getString("files.configuration")%>
           </a>   
        </td>
        <%} else {%>
        <td>&nbsp;</td>
        <%}%>
        <td>
           <img src="images/excel.gif">
           <a href="importar.do" onclick="javascript:window.parent.hacer('opc_17');" class="ahreMenu">
	           	<%=rb.getString("migr.title")%>
		   </a>   
        </td>
    </tr>    
	<%	}else{
			if(sMigracion!=null && "1".equals(sMigracion)){
	%>
    <tr>
        <% if (ToolsHTML.showFiles()) { %>
        <td>
           <img src="img/normas.gif">
           <a href="filesNew.do" onclick="javascript:window.parent.hacer('opc_19');" class="ahreMenu">
           	<%=rb.getString("files.configuration")%>
           </a>   
        </td>
        <%} else {%>
        <td>&nbsp;</td>
        <%}%>
        <td>
           <img src="images/excel.gif">
           <a href="migracion.do" onclick="javascript:window.parent.hacer('opc_17');" class="ahreMenu">
           	<%=rb.getString("migr.title")%>
           </a>   
        </td>
    </tr>    
	<%	    }
	    }
	%>
    <tr>
        <td>
           <img src="img/Filecab.jpg">
           <a href="documentNew.do" onclick="javascript:window.parent.hacer('opc_20');" class="ahreMenu">
           	<%=rb.getString("document.configuration")%>
           </a>   
        </td>
        <td>&nbsp;
        </td>
    </tr>    
<!-- SIMON 15 DE JULIO 2005 FIN -->
</table>
    </logic:present>
<logic:notPresent name="user" scope="session">
    <script language="JavaScript">
        location.href = "inicio.do";
    </script>
</logic:notPresent>
<div id="caja" class="fondoOscuro" style="display:none;">
</div>
<div id="caja2" style="position:absolute;top:0;left:0;">
	<iframe id="frmConfig" name="frmConfig" src="" frameborder="no" style="width:0px;height:0px;"></iframe>
</div>
<div id="caja3" style="display:none;">
</div>
</body>
</html>
<script language="JavaScript">
	try {
		window.parent.clear();
	} catch(e){}
</script>
<script>
	fade=0;
	fadeie=0;
	function fadein(){
		if(fade<=1){
			if(navigator.appName=="Microsoft Internet Explorer"){
				document.body.style.filter="alpha(opacity="+fadeie+")";
			}else{
				document.body.style.opacity=fade;
			}
			fadeie=fadeie+1;
			fade=fade+0.10;
			setTimeout("fadein()",1);
		}
	}
	function fadeout(){
		if(fade>=0.9 | fade>=0) {
			if(navigator.appName=="Microsoft Internet Explorer"){
				document.body.style.filter="alpha(opacity="+fadeie+")";
			}else{
				document.body.style.opacity=fade;
			}
			fade=fade-0.01;
			fadeie=fadeie-1;
			setTimeout("fadeout()",50);
		}
	}
</script>
<script language="javascript" event="onload" for="window">
	window.open("marcoConfig.jsp","frmConfig");
	try{
		eval(<%=onLoad.replaceAll("onLoad=","")%>);
	}catch(e){}
	//fadein();
</script>
