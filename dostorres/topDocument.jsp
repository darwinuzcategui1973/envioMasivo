<!--/**
 * Title: topDocument.jsp <br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @author Simón Rodriguez (SR)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 16/06/2005 (SR) Se invalida la tecla ctrl en java script para imprimir en la forma </li>
 *      <li> 02/12/2005 (NC) Código para eliminar el contenido del portapapeles
 *      <li> 30/06/2006 (NC) Cambios para correcto formato de los documentos a Mostrar </li>
 *      <li> 14/06/2007 (YS) Se muestran revisores de un documento (firmasRevisores) </li>
 *      <li> 04/08/2008 (YS) Se muestra elaborador de un documento (firmaElaborador) </li>
 * <ul>
 */-->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.DesigeConf,
                 com.desige.webDocuments.persistent.managers.HandlerParameters,
                 com.desige.webDocuments.document.actions.loadsolicitudImpresion,
                 com.desige.webDocuments.persistent.managers.HandlerBD,
                 com.desige.webDocuments.utils.Constants,
                 com.desige.webDocuments.document.forms.BaseDocumentForm,
                 com.desige.webDocuments.persistent.managers.HandlerDocuments"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String cmd = (String)session.getAttribute("cmd");
    if (cmd==null) {
        cmd = SuperActionForm.cmdLoad;
    }
    pageContext.setAttribute("cmd",cmd);
    String nameFile = request.getParameter("nameFile")!=null?request.getParameter("nameFile"):"";
    boolean isPDFFormat = false;
    if (nameFile!=null) {
        int pos = nameFile.indexOf(".");
        if (pos > 0) {
            String ext = nameFile.substring(pos+1);
            if ("pdf".equalsIgnoreCase(ext)) {
                isPDFFormat = true;
            }
        }
    }
    BaseDocumentForm forma = (BaseDocumentForm)session.getAttribute("showDocumentI");
    
    String showAllInfo = DesigeConf.getProperty("showAllInfo");
    String idDocumentptr = (String)session.getAttribute("idDocument");
    String idVersion = String.valueOf(forma.getNumVer());
    String idUserptr = (String)session.getAttribute("idUser");
    boolean printFree = false;
    boolean isPrintable = false;
    
	String sHeadImp1 = HandlerParameters.PARAMETROS.getHeadImp1();
	String sHeadImp2 = HandlerParameters.PARAMETROS.getHeadImp2();
	String sHeadImp3 = HandlerParameters.PARAMETROS.getHeadImp3();	

%>
<html>
<head>
<title><%=rb.getString("principal.title")%> </title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<style>
<!--
.skin0 {
	position:absolute;
	width:120px;
	border:1px solid white;
	background-color:#3366FF;
	font-family:Verdana, Arial, Helvetica;
	font-size: 9pt;
	line-height:15px;
	text-align: center;
	cursor:default;
	visibility:hidden;
	background-image: url(img/btn120.png);
	background-repeat: no-repeat;
	height: 20px;
	font-weight: bold;
	color: #FFFFFF;
}
.menuitems {
   padding-left:10px;
   padding-right:10px;
}
-->
</style>

<!-- inicio special style sheet for printing -->
<style media="print">
	.noprint     { display: none }
</style>
<script language="javascript">
	<%
	boolean fileNotOpenSave = false;
	try {
		// validar o no el navegador - para este caso ie7
		String valor = String.valueOf(HandlerParameters.PARAMETROS.getFileNotOpenSave());
		fileNotOpenSave = valor.equals("1") || valor.equals("");
	} catch (Exception e) {
		e.printStackTrace();
	}
	
    if(fileNotOpenSave) {%>
		var idInterval = setInterval("window.parent.focus()",50);
	<%} else {%>
		//var idInterval = setInterval("window.parent.focus()",3000);
	<%}%>
</script>
<script defer>
	function isIE7(){
		return (navigator.appVersion.indexOf("MSIE 7")>0?true:false);
	}

	function setInstallStyles(fOK) {
		document.getElementById("installFailure").runtimeStyle.display = fOK ? "none" : "block";
		document.getElementById("installOK").runtimeStyle.display = fOK ? "block" : "none";
	}
	function okInstall() {
		setInstallStyles(true);
		//setInterval("window.parent.focus()",50);
	}
	function noInstall() {
		setInstallStyles(false);
       	parent.document.getElementById('ventana').rows='100%,*';
       	parent.document.getElementById('ventana').scrolling="no";
       	parent.document.getElementById('ventana').border="0";
       	parent.document.getElementById('cabezera').border="0";
	}
	
	function viewinit() {
		  if (!factory.object) {
		  	noInstall();
		  } else {
			  okInstall();
			  //factory.printing.header = "MeadCo's ScriptX: Basic printing example";
			  //factory.printing.footer = "The de facto standard for advanced web-based printing";
			  //factory.printing.portrait = false;
			  //factory.printing.disableUI = true;  // avanced
			  //factory.printing.copies = 2;  // avanced
		  }
	}
</script>
<!-- fin special style sheet for printing -->

<script language="JavaScript">

    //setInterval("window.parent.focus()",50);

	var key = new Array();
	key['p'] = "No imprimirás.";
	key['c'] = "No copiarás.";
	key['f'] = "No buscarás.";
	
	function getKey(keyStroke) {
		if(isIE7()){
			return;			
		}
		if (window.event.ctrlKey) {
			isNetscape=(document.layers);
			eventChooser = (isNetscape) ? keyStroke.which : event.keyCode;
			which = String.fromCharCode(eventChooser).toLowerCase();
			//for (var i in key) if (which == i)
				//alert(key);
			alert("<%=rb.getString("doc.printNotValid")%>");
		}
	}
	document.onkeydown = getKey;

    function showCharge(userName,charge) {
        window.open("showCharge.jsp?userName="+userName+"&charge="+charge, "_blank", "resizable=yes,scrollbars=yes,statusbar=yes,width=300,height=200,left=410,top=250");
    }

    function limpiarPortaPapeles() {
        <%
            if ("0".equalsIgnoreCase(DesigeConf.getProperty("enableCAP"))) {
        %>
        		// habilitar estas dos lineas
				if(!isIE7()){
                	window.clipboardData.clearData();
	                window.setTimeout("limpiarPortaPapeles()", 1000, "Javascript");
				}
        <% } %>
    }
    <logic:present name="printFree" scope="session">
        <%
            printFree = true;
            session.removeAttribute("printFree");
        %>
    </logic:present>
    
    function imprimir() {
       <% isPrintable = HandlerDocuments.isDocumentStatuVersionPrintable(Integer.parseInt(forma.getIdDocument()),forma.getNumVer());
       if (isPrintable && (printFree || loadsolicitudImpresion.existepermisoImpresion(idDocumentptr,idUserptr)) ) {
            if (isPDFFormat) {%>
                alert("voy a imprimir es pdf");
                //window.parent.documento.print();
            <%} else {%>
	             if (document.onkeydown==undefined) {
            }else{
                 //permite colocar el documento como ya impreso, actualiza el documento de tal manera que tenga que pedir
                 //nuevamente la solicitud en caso de querer volver a imprimir
                 document.impresionRealizada.submit();
            }
            <%--<logic:present name="imprimir" scope="session">--%>
//                //Imprimir el Documento
//                //esto permite al usuario imprimir solo una vez.
//                window.parent.focus();
//                window.parent.documento.print();
//                window.parent.close()
            <%--</logic:present>    --%>
            <% }%>

         <%}else{ %>
              alert('<%=rb.getString("doc.notApprovedToPrint")%>');
        <% } %>
    }
</script>
<script language="JavaScript1.2">
   var menuskin=0
   var display_url=0

   function showmenuie5() {
      var rightedge=document.body.clientWidth-event.clientX
      var bottomedge=document.body.clientHeight-event.clientY
      if (rightedge < ie5menu.offsetWidth)
         ie5menu.style.left=document.body.scrollLeft+event.clientX-ie5menu.offsetWidth
      else
         ie5menu.style.left=document.body.scrollLeft+event.clientX
         if (bottomedge<ie5menu.offsetHeight)
            ie5menu.style.top=document.body.scrollTop+event.clientY-ie5menu.offsetHeight
         else
            ie5menu.style.top=document.body.scrollTop+event.clientY
            ie5menu.style.visibility="visible"
            return false
    }

    function hidemenuie5() {
       ie5menu.style.visibility="hidden"
    }

    function highlightie5() {
       if (event.srcElement.className=="menuitems") {
          event.srcElement.style.backgroundColor="highlight"
          event.srcElement.style.color="white"
          if (display_url==1)
             window.status=event.srcElement.url
       }
    }

    function lowlightie5() {
       if (event.srcElement.className=="menuitems") {
          event.srcElement.style.backgroundColor=""
          event.srcElement.style.color="black"
          window.status=''
       }
    }

    function jumptoie5() {
       if (event.srcElement.className=="menuitems") {
          if (event.srcElement.getAttribute("target")!=null)
             window.open(event.srcElement.url,event.srcElement.getAttribute("target"))
          else
             window.location=event.srcElement.url
       }
    }
    
    function main() {
		window.setTimeout('limpiarPortaPapeles()',1000,'javascript');
		viewinit();
    }
    
</script>
</head>
<body class="bodyInternas" oncontextmenu="return false" onkeydown="return false" onload="main()">
<!-- ini MeadCo ScriptX Control -->
<object id="factory" style="display:none" viewastext 
classid="clsid:1663ED61-23EB-11D2-B92F-008048FDD814" 
codebase="http://192.168.10.36:8085/qwebds4/smsx.cab#Version=6,3,435,20">
</object>
<!-- fin MeadCo ScriptX Control -->
<div id="installFailure" style="display:none">
	<h2>Aparece que ScriptX no esta disponible o ha fallado en la instalacion.</h2>
	<p><b>¿Ha aparecido la barra de informacion?</b></p>
	<p>Si usted usa Internet Explorer 6 o superior, Ventanas XP SP2 o superior entonces la barra de Información de Explorador puede aparecer tratando de instalar ScriptX, esto puede parecerse a esto:</p>
	<p style="padding: 4px"><img src="img/infobar.gif"></p>
	<p>Si la barra ha aparecido, sigue las instrucciones: haz click sobre la barra y luego selecciona <i> Instalar el Control de ActiveX...</i></p>
	<p>Haga click <a href="smsx.exe">aqui</a> para instalar el ActiveX manualmente. Si desea mayor informaci&oacute;n visite <a target="_blank" href="http://www.meadroid.com/scriptx/indexdocs.asp">http://www.meadroid.com/scriptx/indexdocs.asp</a></p>
	<p><b>¿Usted es un administrador?</b></p>
	<p>ScriptX es un código que requiere que usted tenga derechos de administrador sobre su máquina para instalarlo satisfactoriamente.</p>
	<p>Si usted no es un administrador, por favor póngase en contacto con su administrador y solicite instalar el ScriptX para usted.</p>
</div>
<span id="installOK">
<!--[if IE]>
   <span id="ie5menu" class="skin0" onMouseover="highlightie5()" onMouseout="lowlightie5()" onClick="jumptoie5()">
      <a href="javascript:imprimir();" class="titleLeft"><%=rb.getString("btn.print")%></a>
   </span>
<![endif]-->
<logic:notEqual name="printOK" value="true">
<form name="impresionRealizada" action="loadsolicitudImpresion.do">
    <input type=hidden name="cmd" value="actualizarImprimir">
    <input type=hidden name="idDocumentptr" value="<%=idDocumentptr%>">
    <input type=hidden name="idUserptr" value="<%=idUserptr%>">
    <input type=hidden name="idDocument" value="<%=idDocumentptr%>">
    <input type=hidden name="printFree" value="<%=printFree%>">    
    <logic:present name="showDocumentI" >
        <input type=hidden name="numVer" value="<bean:write name='showDocumentI' property='numVer'/>">
        <input type=hidden name="idVersion" value="<bean:write name='showDocumentI' property='numVer'/>">
    </logic:present>
</form>
</logic:notEqual>
<logic:present name="showDocumentI" >
    <bean:define id ="doc" name="showDocumentI" type="com.desige.webDocuments.document.forms.BaseDocumentForm" scope="session"/>
    <logic:present name="isOwner" scope="session">
        <bean:define id="dataWF" name="isOwner" scope="session" type="com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm" />
    </logic:present>

        <logic:notEqual name="doc" value="6"  property="statu">
            <% if((request.getParameter("imprimir")!=null)&&(request.getParameter("imprimir").equalsIgnoreCase("1"))){%>
                <script language="JavaScript1.2">
                    if (document.all&&window.print) {
                        if (menuskin==0)
                            ie5menu.className="skin0"
                        else
                            ie5menu.className="skin1"
                        document.oncontextmenu=showmenuie5
                        document.body.onclick=hidemenuie5
                    }
                </script>
            <%}%>
        </logic:notEqual>
        <logic:present name="printOK" scope="session">
        <table align=center border=0 width="100%">
            <tr>
            	<td class="td_gris_r" width="15%"><img src="img/logos/empresa.gif" border="0"><br><br></td>
            	<td class="td_gris_c" width="55%"><b><%=sHeadImp1%></b></td>
            	<td class="td_gris_c" width="30%"><%=sHeadImp2%><br><%=sHeadImp3%></td>
        	</tr>
        </logic:present>
        <%
            if (Constants.permissionSt.equalsIgnoreCase(showAllInfo)) {
        %>
        <table align=center border=0 width="100%">
            <tr>
                <td width="10%" class="titleLeft2" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.number")%>:
                </td>
                <td class="td_gris_l">
                    <%=doc.getPrefix() + doc.getNumber() %>
                
	            	<logic:present name="documentoActiveFactory" scope="session">
	                 <!--<td width="20%" class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
	                     <%=rb.getString("enl.logout")%>:
	                 </td>-->
                     [
                     <a href="logoutActiveFactory.do" class="ahrefLogout" target="_parent">
                     <%=rb.getString("enl.logout")%>
                     </a>
                     ]
                	</logic:present>
          		</td>
            </tr>
            <tr>
                  <td class="titleLeft2" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                    <font class="titleLeft">
                        <%=rb.getString("cbs.name")%>:
                    </font>
                </td>
                <td class="td_gris_l"  colspan="7">
                    <%=doc.getNameDocument()%>
                </td>
            </tr>
            <tr>
                <td class="titleLeft2" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                    <font class="titleLeft">
                        <%=rb.getString("doc.Ver")%>:
                    </font>
                </td>
                <td class="td_gris_l">
                    <bean:write name="doc" property="mayorVer"/>.<bean:write name="doc" property="minorVer"/>
                </td>
                <td width="15%" class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.dateApprovedAvr")%>:
                </td>
                <td class="td_gris_l">
                    <%=doc.getDateApproved() %>
                </td>
            <!--</tr>-->

            <!--<tr>-->
                <td width="15%" class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("doc.dateExpireAvr")%>:
                </td>
                <td class="td_gris_l">
                    <%=doc.getDateExpires() %>
                </td>

                <td width="10%" class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                    <%=rb.getString("cbs.owner")%>:
                </td>
                <td class="td_gris_l">
                    <%=doc.getNameOwner() %>
                </td>
            </tr>       
        </table>
        <% } else { %>
                <table align=center border=0 width="100%">
                    <tr>
                        <td width="25%" class="titleLeft2" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                            &nbsp;<%=rb.getString("doc.number")%>:
                        </td>
                        <td class="td_gris_l" width="*">
                            <%=doc.getPrefix() + doc.getNumber() %>
                        </td>
                    </tr>
                    <tr>
                        <td class="titleLeft2" width="25%" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                            <font class="titleLeft2">
                                &nbsp;<%=rb.getString("cbs.name")%>:
                            </font>
                        </td>
                        <td class="td_gris_l" width="*">
                            <%=doc.getNameDocument()%>
                           	<logic:present name="documentoActiveFactory" scope="session">
	                    	&nbsp;&nbsp;&nbsp;
	                      	[
	                        <a href="logoutActiveFactory.do" class="ahrefLogout" target="_parent">
	                        <%=rb.getString("enl.logout")%>
	                        </a>
	                        ]
	                     	</logic:present>
	                     </td>
                    </tr>
                    
		            <tr>
		                <td width="25%" class="titleLeft2" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
		                    <font class="titleLeft">
		                        &nbsp;<%=rb.getString("doc.Ver")%>:
		                    </font>
		                </td>
		                <td class="td_gris_l">
		                    <bean:write name="doc" property="mayorVer"/>.<bean:write name="doc" property="minorVer"/>
		                </td>
					</tr>
					<tr>		                
		                <td width="25%" class="titleLeft2" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
		                    &nbsp;<%=rb.getString("doc.dateApproved")%>:
		                </td>
		                <td class="td_gris_l">
		                    <%=doc.getDateApproved() %>
		                </td>
					</tr>
					<tr>			            
		                <td width="25%" class="titleLeft2" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
		                    &nbsp;<%=rb.getString("doc.dateExpire")%>:
		                </td>
		                <td class="td_gris_l">
		                    <%=doc.getDateExpires() %>
		                </td>
		           	</tr>
		           	
					<tr>
		                <td width="25%" class="titleLeft2" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
		                    &nbsp;<%=rb.getString("cbs.owner")%>:
		                </td>
		                <td class="td_gris_l">
		                    <%=doc.getNameOwner() %>  <!--jairo-->
		                </td>
		            </tr>                          
                </table>
        <% }%>
    </logic:present>
    <table align=left border=0 width="100%">
    <tr>
        <td>
    <logic:present name="printOK" scope="session">
        <logic:present name="dataSolicitud" scope="session">
            <table align=left border=0 width="100%">
                <tr>
                    <td width="25%" class="titleLeft2" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                        <%=rb.getString("imp.solicitante")%>:
                    </td>
                    <td class="td_gris_l">
                        <bean:write name="dataSolicitud" property="nameSolicitante"/>
                    </td>
                </tr>
                <tr>
                    <td width="25%" class="titleLeft2" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                        <%=rb.getString("imp.datePrint")%>:
                    </td>
                    <td class="td_gris_l">
                        <%=ToolsHTML.sdfShow.format(new java.util.Date())%>
                    </td>
                </tr>
                <tr>
                    <td width="25%" class="titleLeft2" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                        <%=rb.getString("imp.numCopy")%>:
                    </td>
                    <td class="td_gris_l">
                        <bean:write name="dataSolicitud" property="copias"/>
                    </td>
                </tr>
            <!--</table>-->
                <!--<tr>
                    <td colspan="2" height="22" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat" valign="middle">
                        <%=rb.getString("imp.comentarios")%>
                    </td>
                </tr>-->
                <tr>
                    <%--<td width="20%" class="titleLeft" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">--%>
                        <%--<%=rb.getString("imp.comentarios")%>:--%>
                    <!--</td>-->
                    <td class="td_gris_l" colspan="2">
                        <bean:define id="solicitud" name="dataSolicitud" scope="session" type="com.desige.webDocuments.document.forms.frmsolicitudImpresion"/>
                        <%=solicitud.getComments()%>
                    </td>
                </tr>
            </table>
            <%
                session.removeAttribute("dataSolicitud");
            %>
        </logic:present>
    </logic:present>

    </td>
</tr>

<tr>     
    <td>
    
    <!-- YSA  04/08/08 -->
    <!-- Se muestra el elaborador -->
    <logic:present name="viewCreator" scope="session">
    <logic:present name="firmaElaborador" scope="session">
    <br>
	<table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
        <tr>     
            <td height=1></td>
            <td height=1></td>
        </tr> 
        <tr>     
            <td width="*" class="td_titulo_C2" colspan="2" align="center">
                <b><%=rb.getString("imp.elaborado")%></b>
            </td>
        </tr>  
        <tr>
            <td width="50%" class="td_titulo_C">
            	 <logic:present name="nameCharge" scope="session">
                 <logic:equal name="nameCharge" scope="session" value="0">
                    <%=rb.getString("charge.userName")%>
                 </logic:equal>
                 <logic:equal name="nameCharge" scope="session" value="1">
                 	<%=rb.getString("charge.charge")%>
                 </logic:equal>
                 <logic:equal name="nameCharge" scope="session" value="2">
                 	<%=rb.getString("charge.nameCharge")%>
                 </logic:equal>
                 </logic:present>
                 
                 <logic:notPresent name="nameCharge" scope="session">
                    <%=rb.getString("charge.userName")%>
                 </logic:notPresent>
            </td>
            <td width="*" class="td_titulo_C">
                <%=rb.getString("doc.dateTimeMake")%>
            </td>
        </tr>
       <bean:define id="firmaCreador" name="firmaElaborador" scope="session" type="com.desige.webDocuments.document.forms.BeanFirmsDoc"/>
            <tr>
                <td class="td_gris_l">
                 	 <logic:present name="nameCharge" scope="session">
	                 <logic:equal name="nameCharge" scope="session" value="0">
	                    <%=firmaCreador.getNameUser()%>
	                 </logic:equal>
	                 <logic:equal name="nameCharge" scope="session" value="1">
	                 	<%=firmaCreador.getCharge()%>
	                 </logic:equal>
	                 <logic:equal name="nameCharge" scope="session" value="2">
	                    <%=firmaCreador.getNameUser()%><br>(<%=firmaCreador.getCharge()%>)
	                 </logic:equal>
	                 </logic:present>
	                  
	                 <logic:notPresent name="nameCharge" scope="session">
	                    <%=firmaCreador.getNameUser()%>
	                 </logic:notPresent>                
                </td>
                <td class="td_gris_c" valign="top">
	                <%=firmaCreador.getDateReplied()%>
                </td>
            </tr>
	</table>
    <br>
    </logic:present>
    </logic:present>
  </td>
</tr>

<tr>
    <td>
    <!-- Se muestran los colaboradores -->
    <logic:present name="viewCollaborator" scope="session">
    <logic:present name="firmasColaboradores" scope="session">
	<table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
        <tr>     
            <td height=1></td>
            <td height=1></td>
        </tr> 
        <tr>     
            <td width="*" class="td_titulo_C2" colspan="2" align="center">
                <%=rb.getString("imp.collaborator")%>
            </td>
        </tr>            
        <tr>
            <td width="50%" class="td_titulo_C">
            	 <logic:present name="nameCharge" scope="session">
                 <logic:equal name="nameCharge" scope="session" value="0">
                    <%=rb.getString("charge.userName")%>
                 </logic:equal>
                 <logic:equal name="nameCharge" scope="session" value="1">
                 	<%=rb.getString("charge.charge")%>
                 </logic:equal>
                 <logic:equal name="nameCharge" scope="session" value="2">
                 	<%=rb.getString("charge.nameCharge")%>
                 </logic:equal>
                 </logic:present>
                 
                 <logic:notPresent name="nameCharge" scope="session">
                    <%=rb.getString("charge.userName")%>
                 </logic:notPresent>
            </td>
            <td width="*" class="td_titulo_C">
                <%=rb.getString("doc.dateTimeFirm")%>
            </td>
        </tr>
       <logic:iterate id="participation" name="firmasColaboradores" type="com.desige.webDocuments.document.forms.BeanFirmsDoc" scope="session">
            <tr>
                <td class="td_gris_l">
                 	 <logic:present name="nameCharge" scope="session">
	                 <logic:equal name="nameCharge" scope="session" value="0">
	                    <%=participation.getNameUser()%>
	                 </logic:equal>
	                 <logic:equal name="nameCharge" scope="session" value="1">
	                 	<%=participation.getCharge()%>
	                 </logic:equal>
	                 <logic:equal name="nameCharge" scope="session" value="2">
	                    <%=participation.getNameUser()%><br>(<%=participation.getCharge()%>)
	                 </logic:equal>
	                 </logic:present>
	                  
	                 <logic:notPresent name="nameCharge" scope="session">
	                    <%=participation.getNameUser()%>
	                 </logic:notPresent>                
                </td>
                <td class="td_gris_c" valign="top">
	                <%=participation.getDateReplied()%>
                </td>
            </tr>
        </logic:iterate>
	</table>        
    <br>
    </logic:present>
    </logic:present>
    </td>
</tr>


<tr>
    <td>
    <!-- YSA  15/06/07 -->
    <!-- Se muestran los revisores -->
    <logic:present name="viewReaders" scope="session">
    <logic:present name="firmasRevisores" scope="session">
	<table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
        <tr>     
            <td height=1></td>
            <td height=1></td>
        </tr> 
        <tr>     
            <td width="*" class="td_titulo_C2" colspan="2" align="center">
                <%=rb.getString("imp.revisado")%>
            </td>
        </tr>            
        <tr>
            <td width="50%" class="td_titulo_C">
            	 <logic:present name="nameCharge" scope="session">
                 <logic:equal name="nameCharge" scope="session" value="0">
                    <%=rb.getString("charge.userName")%>
                 </logic:equal>
                 <logic:equal name="nameCharge" scope="session" value="1">
                 	<%=rb.getString("charge.charge")%>
                 </logic:equal>
                 <logic:equal name="nameCharge" scope="session" value="2">
                 	<%=rb.getString("charge.nameCharge")%>
                 </logic:equal>
                 </logic:present>
                 
                 <logic:notPresent name="nameCharge" scope="session">
                    <%=rb.getString("charge.userName")%>
                 </logic:notPresent>
            </td>
            <td width="*" class="td_titulo_C">
                <%=rb.getString("doc.dateTimeFirm")%>
            </td>
        </tr>
       <logic:iterate id="participation" name="firmasRevisores" type="com.desige.webDocuments.document.forms.BeanFirmsDoc" scope="session">
            <tr>
                <td class="td_gris_l">
                 	 <logic:present name="nameCharge" scope="session">
	                 <logic:equal name="nameCharge" scope="session" value="0">
	                    <%=participation.getNameUser()%>
	                 </logic:equal>
	                 <logic:equal name="nameCharge" scope="session" value="1">
	                 	<%=participation.getCharge()%>
	                 </logic:equal>
	                 <logic:equal name="nameCharge" scope="session" value="2">
	                    <%=participation.getNameUser()%><br>(<%=participation.getCharge()%>)
	                 </logic:equal>
	                 </logic:present>
	                  
	                 <logic:notPresent name="nameCharge" scope="session">
	                    <%=participation.getNameUser()%>
	                 </logic:notPresent>                
                </td>
                <td class="td_gris_c" valign="top">
	                <%=participation.getDateReplied()%>
                </td>
            </tr>
        </logic:iterate>
	</table>        
    <br>
    </logic:present>
    </logic:present>
    </td>
</tr>

<tr>     
    <td>
    <logic:present name="firmas" scope="session">
        <table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
            <tr>     
                <td height=1></td>
                <td height=1></td>
            </tr>   
            <tr>     
                <td width="*" class="td_titulo_C2" colspan="2" align="center">
                    <%=rb.getString("imp.aprobado")%>
                </td>
            </tr>           
            <tr>
                <td width="50%" class="td_titulo_C">
                	 <logic:present name="nameCharge" scope="session">
	                 <logic:equal name="nameCharge" scope="session" value="0">
	                    <%=rb.getString("charge.userName")%>
	                 </logic:equal>
	                 <logic:equal name="nameCharge" scope="session" value="1">
	                 	<%=rb.getString("charge.charge")%>
	                 </logic:equal>
	                 <logic:equal name="nameCharge" scope="session" value="2">
	                 	<%=rb.getString("charge.nameCharge")%>
	                 </logic:equal>
	                 </logic:present>
	                 
	                 <logic:notPresent name="nameCharge" scope="session">
	                    <%=rb.getString("charge.userName")%>
	                 </logic:notPresent>
                </td>
                <td width="*" class="td_titulo_C">
                    <%=rb.getString("doc.dateTimeFirm")%>
                </td>
            </tr>
            <logic:iterate id="participation" name="firmas" type="com.desige.webDocuments.document.forms.BeanFirmsDoc" scope="session">
                <tr>
                    <td class="td_gris_l">
	                 	 <logic:present name="nameCharge" scope="session">
		                 <logic:equal name="nameCharge" scope="session" value="0">
		                    <%=participation.getNameUser()%>
		                 </logic:equal>
		                 <logic:equal name="nameCharge" scope="session" value="1">
		                 	<%=participation.getCharge()%>
		                 </logic:equal>
		                 <logic:equal name="nameCharge" scope="session" value="2">
		                    <%=participation.getNameUser()%><br>(<%=participation.getCharge()%>)
		                 </logic:equal>
		                 </logic:present>
		                  
		                 <logic:notPresent name="nameCharge" scope="session">
		                    <%=participation.getNameUser()%>
		                 </logic:notPresent>
		                 <%=participation.getEditor()!=null && participation.getEditor().equals("1")?"<span style='color:blue'>("+rb.getString("doc.editor")+")</span>":participation.getEditor()%>
                    </td>
                    <td class="td_gris_c" valign="top">
	                    <%=participation.getDateReplied()%>
                    </td>
                </tr>
            </logic:iterate>
        </table>
    </logic:present>
    <logic:notPresent name="firmas" scope="session">
        <!-- <table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
            <tr>
                <td class="td_orange_l_b" width="5%">
                    <center><%=rb.getString("sch.noSignature")%></center>
                </td>
            </tr>
        </table> -->
	    <logic:present name="firmaPropietario" scope="session">
	        <table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
	            <tr>     
	                <td height=1></td>
	                <td height=1></td>
	            </tr>   
	            <tr>     
	                <td width="*" class="td_titulo_C2" colspan="2" align="center">
	                    <%=rb.getString("imp.aprobado")%>
	                </td>
	            </tr>           
	            <tr>
	                <td width="50%" class="td_titulo_C">
	                	 <logic:present name="nameCharge" scope="session">
		                 <logic:equal name="nameCharge" scope="session" value="0">
		                    <%=rb.getString("charge.userName")%>
		                 </logic:equal>
		                 <logic:equal name="nameCharge" scope="session" value="1">
		                 	<%=rb.getString("charge.charge")%>
		                 </logic:equal>
		                 <logic:equal name="nameCharge" scope="session" value="2">
		                 	<%=rb.getString("charge.nameCharge")%>
		                 </logic:equal>
		                 </logic:present>
		                 
		                 <logic:notPresent name="nameCharge" scope="session">
		                    <%=rb.getString("charge.userName")%>
		                 </logic:notPresent>
	                </td>
	                <td width="*" class="td_titulo_C">
	                    <%=rb.getString("doc.dateTimeFirm")%>
	                </td>
	            </tr>
	            <logic:iterate id="participation" name="firmaPropietario" type="com.desige.webDocuments.document.forms.BeanFirmsDoc" scope="session">
	                <tr>
	                    <td class="td_gris_l">
		                 	 <logic:present name="nameCharge" scope="session">
			                 <logic:equal name="nameCharge" scope="session" value="0">
			                    <%=participation.getNameUser()%>
			                 </logic:equal>
			                 <logic:equal name="nameCharge" scope="session" value="1">
			                 	<%=participation.getCharge()%>
			                 </logic:equal>
			                 <logic:equal name="nameCharge" scope="session" value="2">
			                    <%=participation.getNameUser()%><br>(<%=participation.getCharge()%>)
			                 </logic:equal>
			                 </logic:present>
			                  
			                 <logic:notPresent name="nameCharge" scope="session">
			                    <%=participation.getNameUser()%>
			                 </logic:notPresent>                         
	                    </td>
	                    <td class="td_gris_c" valign="top">
		                    <%=participation.getDateReplied()%>
	                    </td>
	                </tr>
	            </logic:iterate>
	        </table>	    
	    
	    </logic:present>
    </logic:notPresent>
    
    </td>
</tr>
</table>
    <script language="JavaScript">
    	var impresas = 0;
    
		function pause(millis) {
			var date = new Date();
			var curDate = null;
		
			do { curDate = new Date(); }
			while(curDate-date < millis);
		}
		
		
        <logic:present name="printOK" scope="session">
        	<% int numberCopies = Integer.parseInt(String.valueOf(session.getAttribute("numberCopies"))); %>
        	
			function hacer() {
				try {
					factory.printing.Print(false,window.parent.top);
					//return;
					impresas++;
				} catch(e) {
					//alert("<%=rb.getString("doc.printError")%>");
				}
				if ( impresas < <%=numberCopies%> ) { 
					setTimeout("hacer()",5000);
				} else {
					setTimeout("alert('<%=rb.getString("doc.printFinish")%>')",5000);
				}
			}
        
            //Imprimir el Documento
            //esto permite al usuario imprimir solo una vez.
            //window.parent.documento.print(); // version vieja de print

            window.parent.focus();
        	parent.document.getElementById('ventana').rows='100%,*';
        	parent.document.getElementById('ventana').scrolling="no";
        	parent.document.getElementById('ventana').border="0";
        	parent.document.getElementById('cabezera').border="0";
        	
        	try{
	        	clearInterval(idInterval);
        	} catch(e){
        	}
        	
        	factory.printing.PageSetup();

			mensaje='<%=rb.getString("doc.printStart").replaceAll("PARAMETRO1",String.valueOf(numberCopies))%>';
			alert(mensaje);
			//factory.printing.Print(false,window.parent.frames[0]);
			hacer();
			
            <%
                session.removeAttribute("printOK");
                session.removeAttribute("numberCopies");
            %>
			//window.close();

        </logic:present>
    </script>
</span>

</body>
</html>


