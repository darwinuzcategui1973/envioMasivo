<!--
/**
 * Title: administracionMainNew.jsp <br/>
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
    height:90px;
    text-align: center;
    
}
div.iconoImage {
	border:0px solid red;
    text-align: center;
    width: 138px;
}
div.iconoTexto {
	border:0px solid blue;
	font-family:Tahoma,Helvetica,Verdana,Serif;
    text-align: center;
    color: #4f4f4f;
    font-size:9pt;
}
.activo {
	border-left  :1px solid #efefef;
	border-top   :1px solid #efefef;
	border-right :1px solid #afafaf;
	border-bottom:1px solid #afafaf;
}
.inactivo {
	background-color:;
}
</style>
</head>

<body class="bodyInternas2" >
    <logic:present name="user" scope="session">
<table width="100%" border="0" height="100%">
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
        <td colspan="2" valign="top">
        	<div>
        	<div class="iconoPanel" onmouseover="this.className='iconoPanel activo'" onmouseout="this.className='iconoPanel inactivo'" >
        		<div class="iconoImage"><img src="img/user33_2.jpg"></div>
        		<div class="iconoTexto">
					<a href="newUserGrupo.do" onclick="javascript:window.parent.hacer('opc_1');" class="ahreMenu">
		                <%=rb.getString("admin.cuentas")%>
		            </a>
        		</div>
        	</div>

        	<div class="iconoPanel" onmouseover="this.className='iconoPanel activo'" onmouseout="this.className='iconoPanel inactivo'" >
        		<div class="iconoImage"><img src="img/parameter33.jpg"></div>
        		<div class="iconoTexto">
		            <a href="#" onclick="administrar()" class="ahreMenu" >
		                <%=rb.getString("admin.config")%>
		            </a>
        		</div>
        	</div>

        	<div class="iconoPanel" onmouseover="this.className='iconoPanel activo'" onmouseout="this.className='iconoPanel inactivo'" >
        		<div class="iconoImage"><img src="img/addUser33.jpg"></div>
        		<div class="iconoTexto">
		            <a href="newUser.do" onclick="javascript:window.parent.hacer('opc_3');" class="ahreMenu">
		                <%=rb.getString("admin.usuario")%>
		            </a>
        		</div>
        	</div>

        	<div class="iconoPanel" onmouseover="this.className='iconoPanel activo'" onmouseout="this.className='iconoPanel inactivo'" >
        		<div class="iconoImage"><img src="img/sendMail33.jpg"></div>
        		<div class="iconoTexto">
	            	<%if(!ToolsHTML.isNotifyEmail()){%>
		                <a href="#" onclick="javascript:alert('<%=rb.getString("E0131")%>')" class="ahreMenu">
		                    <%=rb.getString("admin.mensaje")%>
		                </a>
	                <%}else{%>
		                <a href="loadMailAll.do"<%=mails%> onclick="javascript:window.parent.hacer('opc_4');" class="ahreMenu">
		                    <%=rb.getString("admin.mensaje")%>
		                </a>
	                <%}%>
        		</div>
        	</div>

        	<div class="iconoPanel" onmouseover="this.className='iconoPanel activo'" onmouseout="this.className='iconoPanel inactivo'" >
        		<div class="iconoImage"><img src="img/addGroup33.jpg"></div>
        		<div class="iconoTexto">
		            <a href="newGrupo.do" onclick="javascript:window.parent.hacer('opc_5');" class="ahreMenu">
		                <%=rb.getString("admin.grupo")%>
		            </a>
        		</div>
        	</div>

        	<div class="iconoPanel" onmouseover="this.className='iconoPanel activo'" onmouseout="this.className='iconoPanel inactivo'" >
        		<div class="iconoImage"><img src="img/normas33.jpg"></div>
        		<div class="iconoTexto">
		            <a href="javascript:abrirVent('loadNorms.do',700,500)" onclick="javascript:window.parent.hacer('opc_6');" class="ahreMenu">
		                <%=rb.getString("norms.update")%>  
		            </a>
        		</div>
        	</div>

        	<div class="iconoPanel" onmouseover="this.className='iconoPanel activo'" onmouseout="this.className='iconoPanel inactivo'" >
        		<div class="iconoImage"><img src="img/candado33.jpg"></div>
        		<div class="iconoTexto">
		            <a href="segUserGrupo.do" onclick="javascript:window.parent.hacer('opc_7');" class="ahreMenu">
		                <%=rb.getString("admin.seguridad")%>
		            </a>
        		</div>
        	</div>
        	
        	<div class="iconoPanel" onmouseover="this.className='iconoPanel activo'" onmouseout="this.className='iconoPanel inactivo'" >
        		<div class="iconoImage"><img src="img/typeDoc33.jpg"></div>
        		<div class="iconoTexto">
		            <a href="javascript:abrirVentana('loadTypeDoc.do',700,500)" onclick="javascript:window.parent.hacer('opc_8');" class="ahreMenu">
		                <%=rb.getString("typeDoc.update")%>
		            </a>
        		</div>
        	</div>

        	<div class="iconoPanel" onmouseover="this.className='iconoPanel activo'" onmouseout="this.className='iconoPanel inactivo'" >
        		<div class="iconoImage"><img src="img/passUser33.jpg"></div>
        		<div class="iconoTexto">
	                <a href="clave.do" onclick="javascript:window.parent.hacer('opc_9');" class="ahreMenu">
	                <%=rb.getString("admin.clave")%>
	                </a>
        		</div>
        	</div>

        	<div class="iconoPanel" onmouseover="this.className='iconoPanel activo'" onmouseout="this.className='iconoPanel inactivo'" >
        		<div class="iconoImage"><img src="img/areas33.jpg"></div>
        		<div class="iconoTexto">
		            <a href="javascript:abrirVentana('loadArea.do',700,500)" onclick="javascript:window.parent.hacer('opc_10');" class="ahreMenu">
		                <%=rb.getString("admin.areas")%>
		            </a>
        		</div>
        	</div>

        	<div class="iconoPanel" onmouseover="this.className='iconoPanel activo'" onmouseout="this.className='iconoPanel inactivo'" >
        		<div class="iconoImage"><img src="img/editUser33.jpg"></div>
        		<div class="iconoTexto">
	                <a href="loadUsersToEdit.do?loadUser=true" onclick="javascript:window.parent.hacer('opc_11');" class="ahreMenu">
	                    <%=rb.getString("admin.changeUserGroup")%>
	                </a>
        		</div>
        	</div>

        	<div class="iconoPanel" onmouseover="this.className='iconoPanel activo'" onmouseout="this.className='iconoPanel inactivo'" >
        		<div class="iconoImage"><img src="img/cargos33.jpg"></div>
        		<div class="iconoTexto">
	                <a href="javascript:abrirVentanaSinResize('loadCargo.do',700,500)" onclick="javascript:window.parent.hacer('opc_12');" class="ahreMenu">
	                    <%=rb.getString("admin.cargos")%>
	                </a>
        		</div>
        	</div>

        	<div class="iconoPanel" onmouseover="this.className='iconoPanel activo'" onmouseout="this.className='iconoPanel inactivo'" >
        		<div class="iconoImage"><img src="img/editGroup33.jpg"></div>
        		<div class="iconoTexto">
	                <a href="loadUsersToEdit.do?goTo=loadGroups" onclick="javascript:window.parent.hacer('opc_13');" class="ahreMenu">
	                    <%=rb.getString("admin.changeGroup")%>
	                </a>
        		</div>
        	</div>
        	
        	<div class="iconoPanel" onmouseover="this.className='iconoPanel activo'" onmouseout="this.className='iconoPanel inactivo'" >
        		<div class="iconoImage"><img src="img/editSession33.jpg"></div>
        		<div class="iconoTexto">
	                <a href="loadUsersToSession.do?goTo=loadGroups" onclick="javascript:window.parent.hacer('opc_15');" class="ahreMenu">
	                   <%=rb.getString("session.admin")%>
	                </a>
        		</div>
        	</div>

	        <% if (ToolsHTML.showFiles()) { %>
        	<div class="iconoPanel" onmouseover="this.className='iconoPanel activo'" onmouseout="this.className='iconoPanel inactivo'" >
        		<div class="iconoImage"><img src="img/normas.jpg"></div>
        		<div class="iconoTexto">
		           <a href="filesNew.do" onclick="javascript:window.parent.hacer('opc_19');" class="ahreMenu">
		           	<%=rb.getString("files.configuration")%>
		           </a>   
        		</div>
        	</div>
	        <%}%>
        	
		    <%String sMigracion = (String)DesigeConf.getProperty("migracionOn");
	    	String sMigracionValidacion = (String)DesigeConf.getProperty("migracionValidacionOn");
	      	if(sMigracionValidacion!=null && "1".equals(sMigracionValidacion)){%>         
	        	<div class="iconoPanel" onmouseover="this.className='iconoPanel activo'" onmouseout="this.className='iconoPanel inactivo'" >
	        		<div class="iconoImage"><img src="images/excel.gif"></div>
	        		<div class="iconoTexto">
			           <a href="importar.do" onclick="javascript:window.parent.hacer('opc_17');" class="ahreMenu">
				           	<%=rb.getString("migr.title")%>
					   </a>   
	        		</div>
	        	</div>
			<%}else{ %>        	
				<%if(sMigracion!=null && "1".equals(sMigracion)){%>
	        	<div class="iconoPanel" onmouseover="this.className='iconoPanel activo'" onmouseout="this.className='iconoPanel inactivo'" >
	        		<div class="iconoImage"><img src="images/excel.gif"></div>
	        		<div class="iconoTexto">
			           <a href="migracion.do" onclick="javascript:window.parent.hacer('opc_17');" class="ahreMenu">
			           	<%=rb.getString("migr.title")%>
			           </a>   
	        		</div>
	        	</div>
		        <%}%>
	        <%}%>
        	
        	<div class="iconoPanel" onmouseover="this.className='iconoPanel activo'" onmouseout="this.className='iconoPanel inactivo'" >
        		<div class="iconoImage"><img src="img/Filecab.jpg"></div>
        		<div class="iconoTexto">
		           <a href="documentNew.do" onclick="javascript:window.parent.hacer('opc_20');" class="ahreMenu">
		           	<%=rb.getString("document.configuration")%>
		           </a>   
        		</div>
        	</div>

			<%if(ToolsHTML.showSACOP()){%>
        	<div class="iconoPanel" onmouseover="this.className='iconoPanel activo'" onmouseout="this.className='iconoPanel inactivo'" >
        		<div class="iconoImage"><img src="img/sacop33.jpg"></div>
        		<div class="iconoTexto">
				   <a href="javascript:abrirVentana('LoadTitulosPlanillasSacop.do',700,500)" onclick="javascript:window.parent.hacer('opc_14');"  class="ahreMenu">
		           	<%=rb.getString("admin.scpplanillas")%>
		           </a>   
        		</div>
        	</div>
			<%}%>

           <%if(ToolsHTML.showFTP()){%>
        	<div class="iconoPanel" onmouseover="this.className='iconoPanel activo'" onmouseout="this.className='iconoPanel inactivo'" >
        		<div class="iconoImage"><img src="img/parametrico33.jpg"></div>
        		<div class="iconoTexto">
				   <a href="createAct.do" onclick="javascript:window.parent.hacer('opc_16');"  class="ahreMenu">
		           	<%=rb.getString("admin.activities")%>
		           </a>   
        		</div>
        	</div>
           <%}%>


        	</div>
        </td>
    </tr>

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
