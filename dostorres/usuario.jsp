<%@ page contentType="text/html; charset=iso-8859-1" language="java" import="java.sql.*,
                                                                             java.util.ResourceBundle,
                                                                             com.desige.webDocuments.utils.ToolsHTML,
                                                                             com.desige.webDocuments.utils.beans.SuperActionForm,
                                                                             com.desige.webDocuments.persistent.managers.HandlerParameters,
                                                                             com.desige.webDocuments.utils.beans.SuperAction,
                                                                             com.desige.webDocuments.parameters.forms.BaseParametersForm,
                                                                             com.desige.webDocuments.utils.DesigeConf,
                                                                             com.desige.webDocuments.utils.beans.Users"%>
<%@ page import="com.desige.webDocuments.persistent.managers.HandlerBD" %>
<%@ page import="com.desige.webDocuments.utils.Constants" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!-- usuario.jsp -->
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    String info = (String) request.getAttribute("info");
    if (ToolsHTML.checkValue(info)) {
        onLoad.append(" onLoad=\"alert('").append(info).append("')\"");
    }
    String cmd = (String) request.getAttribute("cmd");
    if (cmd == null) {
        cmd = SuperActionForm.cmdLoad;
    }
    pageContext.setAttribute("cmd", cmd);
    String valorCargo = "";
    Users usuario = (Users) session.getAttribute("user");
    String logueado = usuario.getUser();
    String areaFiltro = (String) request.getAttribute("areaFiltro");
    String nameNodeService = (String) request.getAttribute("nameNodeService");
    String idRout = (String) request.getAttribute("idRout");
    String idNodeService = (String) request.getAttribute("idNodeService");
    if (ToolsHTML.isEmptyOrNull(areaFiltro)) {
        areaFiltro = "-1";
    }

    //Luis Cisneros 28/02/11
    //Validacion de correo opcional
    boolean validateEmail = String.valueOf(HandlerParameters.PARAMETROS.getValidateEmail()).equals("0");

%>
<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language=javascript src="./estilo/funciones.js"></script>

<script language="JavaScript">
//	function validar() {
//        if (document.login.docSearch.value.length>0) {
//            alert('<%=rb.getString("err.notDocumentSearch")%>');
//            return false;
//        }
//        //Validando el Formato de Correo
//        if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(valor)) {
//
//        }
//        document.login.submit();
//	}

    function botones(forma) {
        if (forma) {
            if (forma.btnOK) {
                forma.btnOK.disabled = true;
            }
            if (forma.btnCancel) {
                forma.btnCancel.disabled = true;
            }
        }
    }

    function salvar(forma) {

        var strArea='<%=areaFiltro%>';
        var strCargo=<bean:write name="newUser" property="cargo"/>;
	
        
        forma.idNodeService.value=forma.idRout.value;
        if (forma.idNodeService.value==1) {
        	
        	//alert("Usuario no posee seguridad o no se puede utilizar el node principal");
        	alert ("<%=rb.getString("user.msjNodePrincipal")%>");

            forma.idNodeService.value=0;
        	
        }
        
        
        
        

        if (strCargo!=-1) {
            if ((strCargo)==(document.newUser.cargo.value)) {
                if ( validar(forma)){
                    //no remplazamos el usuario ni sus documentos
                    forma.cancelUser.disabled = true;
                    forma.saveUser.disabled = true;

                    forma.submit();
                 }
            } else {
                var str = forma.useremplazo.value;
                if ((forma.useremplazo.value!=null)&& (str.length>0) ) {
                    //validamos si hay cambio de area
                    if (strArea==document.newUser.area.value) {
                        if ((strCargo)!=(document.newUser.cargo.value)) {
                            //es opcional en caso que cambiemos de cargo y no de area preguntar al usuario
                            //si desea pasar los documentos flujos seguridad a otro usuario
                            if (confirm("<%=rb.getString("crg.seguroremplazo")%>")) {
                                if ( validar(forma)) {
                                    forma.cambiocargo.value = true;//(1==1);                                  
                                    forma.cancelUser.disabled = true;
                                    forma.saveUser.disabled = true;

                                    forma.submit();
                                }

                            } else {
                                if ( validar(forma)) {
                                    forma.cambiocargo.value = (1==3);//false
                                    forma.cancelUser.disabled = true;
                                    forma.saveUser.disabled = true;

                                    forma.submit();
                                }
                            }
                        }
                    } else {
                        if ( validar(forma)) {
                            //en caso que cambiemos de  area , se cambia de seguridda, documentos, flujos.
                            forma.cambiocargo.value = (1==1);
                            forma.cancelUser.disabled = true;
                            forma.saveUser.disabled = true;
                            forma.submit();
                        }
                    }
                } else {
                    alert("<%=rb.getString("user.cambiocargo")%>");
                }
            }
        } else {
            if ( validar(forma)) {
                //Si vas a insertar, el newUser.cargo me viene -1..
                forma.cancelUser.disabled = true;
                forma.saveUser.disabled = true;
                // forma.btndelete.disabled=true;
                forma.submit();
               // forma.btndelete.disabled=true;
            }
        }
   }

      function trim (cadena) {

        for(i=0; i<cadena.length; )
        {
            if(cadena.charAt(i)==" ")
                cadena=cadena.substring(i+1, cadena.length);
            else
                break;
        }

        for(i=cadena.length-1; i>=0; i=cadena.length-1)
        {
            if(cadena.charAt(i)==" ")
                cadena=cadena.substring(0,i);
            else
                break;
        }

        return cadena;
    }

    function validar(forma) {
    	if(forma.user.value.toLowerCase()=='<%=Constants.ID_USER_TEMPORAL%>') {
    		//forma.user.value=forma.user.value.toLowerCase();
    		//alert('Usuario Reservado para Qwebdocuments, ingrese otro Id de usuario');
    		alert ("<%=rb.getString("user.msjIdReservadoUsuario")%>");
    		return false;
    	}
        if (trim(forma.user.value).length==0 || trim(forma.nombres.value).length==0 || trim(forma.apellidos.value).length==0
            || trim(forma.cargo.value).length==0 || trim(forma.clave.value).length==0 || trim(forma.email.value).length==0) {
            alert ("<%=rb.getString("user.mensaje")%>");
            return false;
        }
        //Validando el Formato del Nombre de Usuario
        if (!(/^\w+$/.test(forma.user.value))) {
            alert ("<%=rb.getString("user.badNameUser")%>");
            return false;
        }


        //Validando el Formato del Correo
        //Validando el Formato del Correo
        <% if(String.valueOf(HandlerParameters.PARAMETROS.getValidateEmail()).equals("0")) {%>

	        if (!(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(forma.email.value))) {
	            alert ("<%=rb.getString("E0099")%>");
            	return false;
	        }
        	var dominioCorreo = forma.email.value.substring(forma.email.value.indexOf("@")+1);
        	//Validando el dominio de correo
        	<% if(HandlerParameters.PARAMETROS.getDomainEmail()!=null && !HandlerParameters.PARAMETROS.getDomainEmail().trim().equals("")) {
	        	String[]  dominios = HandlerParameters.PARAMETROS.getDomainEmail().trim().split(",");
	        	StringBuffer sb = new StringBuffer();
	        	String sep = "";
        		for(int item=0; item < dominios.length; item++) {
        			if(!dominios[item].trim().equals("")) {
        				sb.append(sep);
        				sb.append("dominioCorreo=='");
        				sb.append(dominios[item].trim());
        				sb.append("'");
        				sep = " || ";
					}        		
        		}
        		if(sb.toString().length()>0) {%>
        			if(!(<%=sb.toString()%>)) {
        				alert("<%=rb.getString("err.NotValidDomain")%>");
        				return false;
        			}
        		<%}
        	}%>
        
        <%}%>
        
        if ((forma.lengthPass.value!=null)||(forma.lengthPass.value!='null')) {
            if (forma.clave.value.length >= parseInt(forma.lengthPass.value)) {
                if (forma.btnOK) {
                    forma.btnOK.disabled = true;
                }
                if (forma.btnCancel) {
                    forma.btnCancel.disabled = true;
                }
                return true;
                //forma.submit();  //Luis Cisneros 14-03-07, este submit esta fuera de contexto, este m'etodo no debe enviar nada al servidor, solo validar
            } else {
                alert ("<%=rb.getString("clave.lenght")%> " + forma.lengthPass.value);
                return false;
            }
        }
        //return true;
    }

    function cancelar(form) {
        <%
            boolean asign = false;
        %>
        <logic:equal value="loadToEdit" name="nextPage" >
             location.href="loadUsersToEdit.do?loadUser=true"
            <%
                asign = true;
            %>
        </logic:equal>
        <logic:equal value="showAllAcounts" name="nextPage" >
            location.href = "newUserGrupo.do?cmd=load";
            <%
                asign = true;
            %>
        </logic:equal>
        <%
            if (!asign) {
        %>
            location.href = "administracionMain.do";
        <%}%>
    }

    function editField(pages,input,value,forma) {
    	
    
        var hWnd = null;
        
    
        if (newUser.grupo.value=="1"  && newUser.grupo.value!=null  ){
        	//alert("No se puede Cargar Usuario Viewer");
        	alert("<%=rb.getString("user.msjViewerNodeService")%>");
        	
        } else {
        	
        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.name +"&selecIdGroup="+newUser.grupo.value+"&selecUser="+newUser.user.value,"Catalogo","width=400,height=300,resizable=yes,scrollbars=yes,left=300,top=250");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
        
        }
    }
    function updateCheck(check,field){
        if (check.checked){
            field.value = "0";
        } else{
            field.value = 1;
        }
    }
    function refreshButton(forma){
        forma.lengthDocNumber.disabled = !forma.docNumberGenerator[1].checked;
        forma.resetDocNumber.disabled = !forma.docNumberGenerator[1].checked;

        if (forma.docNumberGenerator[1].checked){
            forma.lengthDocNumber.focus();
        }
    }
    
    function youAreSure(form){
        var str = form.useremplazo.value;
        if ((form.useremplazo.value!=null)&& (str.length>0) ){
        	if (confirm("<%=rb.getString("areYouSure")%>")) {
        		form.cmd.value = "<%=SuperActionForm.cmdDelete%>";
                form.cancelUser.disabled=true;
                form.saveUser.disabled=true;
                form.btndelete.disabled=true;
                
                //desaparecemos el boton de borrado
                if(document.getElementById('spanBtnDelete') != null){
                    document.getElementById('spanBtnDelete').innerHTML  = "<img src='images/cargando.gif' border='0' />";
                }

                form.submit();
            }
        }else{
        	alert("<%=rb.getString("user.elimerror")%>");
        }
     }
    
     function getDatos(){
        document.newUser.reload.value="<%=SuperActionForm.cmdReLoad%>"
        document.newUser.action = "newUser.do"
        //alert(document.newUser.cmd.value + ' nextPage='+document.newUser.nextPage.value );
        document.newUser.submit();
        // SuperActionForm.cmdLoad;
        //document.newUser.idarea.value;
    }
    
    function clear(valor) {
    	if(valor!=null){
   			document.newUser.area.value=valor;
	   		getDatos();
    	}
    }

    function selectUser(forma,obj,combo) {
		if(combo==0) {
			document.getElementById('useremplazo').value=obj.value;
			forma.useremplazoAll.selectedIndex=-1;
		} else {
			// buscamos si no esta en el otro combo
			var noPreguntar = false;
			for(var x=0;x<forma.useremplazoArea.options.length;x++) {
				if(forma.useremplazoArea.options[x].value==obj.value){
					noPreguntar=true;
					break;
				}
			}
			if(noPreguntar || confirm("Esta seguro que desea sustituir el usuario por otro que no pertenece a la misma area?")) {
				document.getElementById('useremplazo').value=obj.value;
				forma.useremplazoArea.selectedIndex=-1;
			} else {
				forma.useremplazoAll.selectedIndex=-1;
				document.getElementById('useremplazo').value='';
			}
		}
    }
</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
    <logic:present name="datosGrupos" scope="session" >
        <html:form action="/newUser.do">
            <input type="hidden"  name ="reload" value="">

            <html:hidden property="cambiocargo" value="false"/>
            <html:hidden property="cmd" />
            <html:hidden property="lengthPass" />
            <input type="hidden" name="nextPage" value="<%=request.getAttribute("nextPage")%>" />
            <table width="100%" border="0" align="center">
                <!--<tr>
                    <td colspan="2" class="pagesTitle">
                        <%=rb.getString("user.title")%>
                    </td>
                </tr>-->
                <tr>
                    <td colspan="2">
                        <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                            <tbody>
                                <tr>
                                    <td class="td_title_bc">
                                        <%=rb.getString("user.title")%>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr>
                <%
                    if (!cmd.equalsIgnoreCase(SuperActionForm.cmdEdit)) { %>
                        <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" width="16%" valign="middle">
                            <%=rb.getString("user.usuario")%>
                        </td>
                        <td width="*" class="td_gris_l">
                            <html:text property="user" size="30" styleClass="classText"/>
                        </td>
                <%
                    } else {
                %>
                    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" width="16%" valign="middle">
                        <%=rb.getString("user.usuario")%>
                    </td>
                    <td class="td_gris_l">
                        <bean:write name="newUser" property="user"/>
                    </td>
                    <html:hidden property="user" />
                <% } %>
                </tr>
                <tr>
                    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                        <%=rb.getString("user.nombre")%>
                    </td>
                    <td class="td_gris_l">
                        <html:text property="nombres" size="30" styleClass="classText"/>
                    </td>
                </tr>
                <tr>
                    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                        <%=rb.getString("user.apellido")%>
                    </td>
                    <td class="td_gris_l">
                        <html:text property="apellidos" size="30" styleClass="classText"/>
                    </td>
                </tr>
                 <!--<tr>-->
                    <!--<td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">-->
                        <%--<%=rb.getString("user.cargo")%>--%>
                    <!--</td>-->
                    <!--<td class="td_gris_l">-->
                        <%--<html:text property="cargo" size="30" styleClass="classText"/>--%>
                    <!--</td>-->
                <!--</tr>-->
                <tr>
                    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                        <%=rb.getString("user.area")%>
                     </td>
                    <td>
                       <%String selecArea="";%>

                       <logic:notEqual name="newUser" property="user" value='admin'>
                        <!--en caso que el usuario no sea el mismo , queda habilitado    -->
                       <logic:notEqual name="newUser" property="user" value="<%=logueado%>">
                            <logic:present name="tblareas" scope="request">
                             <select name="area"   size="1" onchange="getDatos();" style="width:210px;"  class="classText">
                                    <logic:iterate id="idio" name="tblareas" type="com.desige.webDocuments.utils.beans.Search">
                                        <logic:equal name="newUser" property="area" value="<%=idio.getId()%>">
                                             <option value="<%=idio.getId()%>" selected="true"><%=idio.getDescript()%></option>
                                        </logic:equal>
                                        <logic:notEqual name="newUser" property="area" value="<%=idio.getId()%>">
                                             <option value="<%=idio.getId()%>"><%=idio.getDescript()%></option>
                                        </logic:notEqual>

                                    </logic:iterate>
                            </select> 
                            <input type="button" class="boton" value="..." onClick="abrirVentanaSinResize('loadCargo.do',700,500)" style="margin-bottom:5px;width:20px;" />
                        </logic:present>
                        </logic:notEqual>
                        </logic:notEqual>
                       <!--en caso que el usuario  sea el mismo , qeda deshabilitado    -->
                       <logic:equal name="newUser" property="user" value='<%=logueado%>'>
                           <logic:present name="tblareas" scope="request">
                             <select name="area" DISABLED READONLY  size="1" onchange="getDatos();" style="width:210px;"  class="classText">
                                    <logic:iterate id="idio" name="tblareas" type="com.desige.webDocuments.utils.beans.Search">
                                        <logic:equal name="newUser" property="area" value="<%=idio.getId()%>">
                                             <option value="<%=idio.getId()%>" selected="true"><%=idio.getDescript()%></option>
                                        </logic:equal>
                                        <logic:notEqual name="newUser" property="area" value="<%=idio.getId()%>">
                                             <option value="<%=idio.getId()%>"><%=idio.getDescript()%></option>
                                        </logic:notEqual>
                                    </logic:iterate>
                            </select>
                        </logic:present>
                       </logic:equal>

                        
                       <!--en caso que el usuario  sea el mismo , qeda deshabilitado    -->
                       <!-- comentamos aqui esto -->
                       <!--
                       <logic:equal name="newUser" property="user" value='admin'>
                           <logic:present name="tblareas" scope="request">
                             <select name="area" DISABLED READONLY  size="1" onchange="getDatos();" style="width:210px;"  styleClass="classText">
                                    <logic:iterate id="idio" name="tblareas" type="com.desige.webDocuments.utils.beans.Search">
                                        <logic:equal name="newUser" property="area" value="<%=idio.getId()%>">
                                             <option value="<%=idio.getId()%>" selected="true"><%=idio.getDescript()%></option>
                                        </logic:equal>
                                        <logic:notEqual name="newUser" property="area" value="<%=idio.getId()%>">
                                             <option value="<%=idio.getId()%>"><%=idio.getDescript()%></option>
                                        </logic:notEqual>
                                    </logic:iterate>
                            </select>
                        </logic:present>
                       </logic:equal>
                       -->
                    </td>
                    <td>&nbsp;</td>
                </tr>
                 <tr>
                    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                        <%=rb.getString("user.cargo")%>
                    </td>
                    <td class="td_gris_l">

                    <!--en caso que el usuario no sea el mismo , qheda habilitado    -->
                   <logic:notEqual name="newUser" property="user" value="<%=logueado%>">
                     <html:select disabled="false"  property="cargo" styleClass="classText">
                     <logic:present name="tblcargos" scope="request">
                        <!--inicializamos variablepara verificar si cambioo de cargo -->
                      <logic:iterate id="idio" name="tblcargos" scope="request" type="com.desige.webDocuments.utils.beans.Search">
                            <logic:equal name="newUser" property="cargo" value="<%=idio.getId()%>">
                                  <option value="<%=idio.getId()%>" selected="true"><%=idio.getDescript()%></option>

                            </logic:equal>
                            <logic:notEqual name="newUser" property="cargo" value="<%=idio.getId()%>">
                                  <option value="<%=idio.getId()%>"><%=idio.getDescript()%></option>
                            </logic:notEqual>
                       </logic:iterate>
                     </logic:present>
                    </html:select>
                    <input type="button" class="boton" value="..." onClick="abrirVentanaSinResize('loadCargo.do',700,500)" style="margin-bottom:5px;width:20px;" />
                   </logic:notEqual>


                  <!--en caso que el usuario  sea el mismo , qeda deshabilitado    -->
                   <logic:equal name="newUser" property="user" value='<%=logueado%>'>
                      <html:select disabled="true"  property="cargo">
                    <logic:present name="tblcargos" scope="request">
                        <!--inicializamos variablepara verificar si cambioo de cargo -->
                      <logic:iterate id="idio" name="tblcargos" scope="request" type="com.desige.webDocuments.utils.beans.Search">
                            <logic:equal name="newUser" property="cargo" value="<%=idio.getId()%>">
                                  <option value="<%=idio.getId()%>" selected="true"><%=idio.getDescript()%></option>

                            </logic:equal>
                            <logic:notEqual name="newUser" property="cargo" value="<%=idio.getId()%>">
                                  <option value="<%=idio.getId()%>"><%=idio.getDescript()%></option>
                            </logic:notEqual>
                       </logic:iterate>
                    </logic:present>           
                    </html:select>
                   </logic:equal>


                   </td>

                </tr>
                <tr>
                    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                        <%=rb.getString("user.clave")%>
                    </td>
                    <td class="td_gris_l">
                        <html:password property="clave" size="30" styleClass="classText" />
                        <html:hidden  property="repclave"/>
                    </td>
                </tr>
                <tr>
                    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                        <%=rb.getString("user.email")%>
                    </td>
                    <td class="td_gris_l">
                        <html:text property="email" size="30" styleClass="classText"/>
                    </td>
                </tr>
                <tr>
                  <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                        <%=rb.getString("user.grupo")%></td>
                    <td class="td_gris_l">
                        <logic:notEqual name="newUser" property="user" value='admin'>
                            <html:select property="grupo" styleClass="classText" style="width:198px;">
                                <logic:iterate id="idGrupos" name="datosGrupos" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                    <html:option value="<%=idGrupos.getId()%>" >
                                        <%=idGrupos.getDescript()%>
                                    </html:option>
                                </logic:iterate>
                            </html:select>
		                    <input type="button" class="boton" value="..." onClick="abrirVentanaSinResize('newGrupo.do',700,500)" style="margin-bottom:5px;width:20px;" />
                        </logic:notEqual>
                        <logic:equal name="newUser" property="user" value='admin'>
                            <html:select disabled="true"  property="grupo" styleClass="classText" style="width:198px;">
                                <logic:iterate id="idGrupos" name="datosGrupos" scope="session" type="com.desige.webDocuments.utils.beans.Search">
                                    <html:option value="<%=idGrupos.getId()%>" >
                                        <%=idGrupos.getDescript()%>
                                    </html:option>
                                </logic:iterate>
                            </html:select>
		                    <input type="button" class="boton" value="..." onClick="abrirVentanaSinResize('loadCargo.do',700,500)" style="margin-bottom:5px;width:20px;" />
                        </logic:equal>
                    </td>
                </tr>
                    <logic:notEqual name="newUser" property="user" value=''>
                 <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                         <%=rb.getString("user.nodeService")%></td>
                        <td class="td_gris_l">
                       
                
		                            <table cellpadding="0" cellspacing="0" border="0">
		                            <tr>
		                            	<td>
							               <!--   <input type="text"   name="nameRout"  style="width:155px;height: 19px" value=""/> -->
							                <!--  <input type="hidden" name="idRout" value=""/> -->
							                <input type="text" name="nameRout"  style="width:155px;height: 19px" value="<%=request.getAttribute("nameNodeService")!=null?request.getAttribute("nameNodeService"):""%>"/>
							                <input type="hidden" name="idRout" value="<%=request.getAttribute("idNodeService")!=null?request.getAttribute("idNodeService"):""%>" />
							                <input type="hidden" name="idNodeService" value="0"/>
		                            	</td>
		                            	<td>
							                &nbsp;<input type="button" class="boton" value=" ... " onClick="javascript:editField('loadAllStruct.do?toSelectValue=true','estado','nameState',this.form);" style="width:20px;height:19px" />
		                            	</td>
		                            </tr>
		                          
		                            </table>
		                                 </logic:notEqual>


                <tr>
                    <td colspan="2" align="center">
                        <%
                            String disableBtns = "";
                            String idAdmon = DesigeConf.getProperty("application.admon");
                            //System.out.println("idAdmon = " + idAdmon);
                        %>
                        <logic:notEqual value='<%=idAdmon.trim()%>' name='user' property='idGroup'>
                        <%
                            disableBtns = "disabled";
                        %>
                        </logic:notEqual>
                        <input type="button" class="boton" value="<%=rb.getString("btn.save")%>" name="saveUser" onClick="salvar(this.form);" <%=disableBtns%> />
                        &nbsp;
                        <input type="button" class="boton" value="<%=rb.getString("btn.cancel")%>"  name="cancelUser" onclick="cancelar(this.form)" />
                    </td>
                </tr>
                <!-- 08 AGOSTO 2005 INICIO -->
                <logic:equal value="<%=SuperActionForm.cmdEdit%>" name="cmd" >
                                <tr>
                                  <td>&nbsp;</td>
                                </tr>
                                <tr>
                                  <td colspan="2">
                                         <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                                             <tbody>
                                                 <tr>
                                                     <td class="td_title_bc">
                                                      <%=rb.getString("user.elim1")%> <bean:write name="newUser" property="user" /> <%=rb.getString("user.elim2")%> 
                                                      <%-- ydavila Ticket 001-00-003023--%>
                                                      <br><%=rb.getString("user.elim4")%>  
                                                     </td>
                                                 </tr>
                                             </tbody>
                                         </table>
                                     </td>
                                 </tr>

                <tr>
                    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" width="16%" valign="middle">
                        <%=rb.getString("user.usuario")%>
                    </td>
                    <td class="td_gris_l">
                        <bean:write name="newUser" property="user"/>
                    </td>
                </tr>
                <tr>
	                 <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat">
	                     <%=rb.getString("user.sustpor")%>
	                 </td>
	                 <td rowspan="4" valign="top"  style="background: url(img/btn120.gi2f); background-repeat: no-repeat">
	                 	<input type="hidden" id="useremplazo" name="useremplazo">
	                    <select name="useremplazoArea" size="5" multiple style="width:300px;" class="classText" onchange="selectUser(this.form,this,0)">
	                        <logic:present name="usuarios" scope="request">
	                            <logic:iterate id="bean" name="usuarios" scope="request" type="com.desige.webDocuments.utils.beans.Search">
	                                <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
	                            </logic:iterate>
	                        </logic:present>
	                   </select>
	                </td>
                </tr>
                 <tr>
                   <td rowspan="3" class="td_gris_l" style="text-align:center" >Lista de usuarios del area</td>
                   <td>&nbsp;</td>
                 </tr>
                 <tr>
                   <td>&nbsp;</td>
                 </tr>
                 <tr>
                   <td>&nbsp;</td>
                 </tr>
                 
                <tr>
                  <td colspan="2">
                         <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="2" align=center border=0>
                             <tbody>
                                 <tr>
                                     <td class="td_title_bc">
                                        <%=rb.getString("user.elim1")%> <bean:write name="newUser" property="user" /> <%=rb.getString("user.elim2")%>
                                        <%-- ydavila Ticket 001-00-003023--%>
                                        <br><%=rb.getString("user.elim4")%>  
                                     </td>
                                 </tr>
                             </tbody>
                         </table>
                     </td>
                 </tr>

                <tr>
	                 <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat">
	                     <%=rb.getString("user.sustpor")%>
	                 </td>
	                 <td  rowspan="4" valign="top"  style="background: url(img/btn120.gi2f); background-repeat: no-repeat">
	                    <select name="useremplazoAll" size="5" multiple style="width:300px;" class="classText" onchange="selectUser(this.form,this,1)">
	                        <logic:present name="usuariosAll" scope="request">
	                            <logic:iterate id="bean" name="usuariosAll" scope="request" type="com.desige.webDocuments.utils.beans.Search">
	                                <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
	                            </logic:iterate>
	                        </logic:present>
	                   </select>
	                </td>
                </tr>
                 <tr>
                   <td rowspan="3" class="td_gris_l" style="text-align:center" >Lista general de usuarios<br/>(Todas las areas)</td>
                   <td>&nbsp;</td>
                 </tr>
                 <tr>
                   <td>&nbsp;</td>
                 </tr>
                 <tr>
                   <td>&nbsp;</td>
                 </tr>

                 <tr>
                   <td colspan="2" style="height:2pt;" bgcolor="gray"></td>
                 </tr>
                 
                 <tr>
                   <td colspan="2">&nbsp;</td>
                 </tr>

                <!-- 08 AGOSTO 2005 FIN -->
                 <tr>
                    <td colspan="2" align="center">
                        <logic:notEqual value='<%=idAdmon.trim()%>' name='user' property='idGroup'>
                        <%
                          disableBtns = "disabled";
                        %>
                        </logic:notEqual>


                          <!-- Si es el mismo usuario que esta logueado, el sistema no lo deja eliminar.-->
                         <!-- comentamos esto -->
                         <!-- 
                         <logic:equal name="newUser" property="user" value='<%=logueado%>'>
                            <span id="spanBtnDelete">
                                <input type="button" class="boton" value="<%=rb.getString("btn.delete")%>" name="btndelete" onclick="youAreSure(this.form);" disabled="true" />
                            </span>
                         </logic:equal>
                         -->
                          <logic:equal name="newUser" property="user" value='admin'>
                            <input type="button" class="boton" value="<%=rb.getString("btn.delete")%>" name="btndelete" onclick="youAreSure(this.form);" disabled="true" />
                          </logic:equal>
                          <logic:notEqual name="newUser" property="user" value='admin'>
                              <logic:notEqual name="newUser" property="user" value="<%=logueado%>">
                                <span id="spanBtnDelete">
                                    <input type="button" class="boton" value="<%=rb.getString("btn.delete")%>" name="btndelete" onclick="youAreSure(this.form);" <%=disableBtns%> />
                                </span>
                              </logic:notEqual>
                          </logic:notEqual>
                    </td>
                </tr>
           </logic:equal>
            </table>
        </html:form>
</logic:present>
<logic:notPresent scope="session" name="datosGrupos">
  <%=rb.getString("grupo.cargaron")%>
</logic:notPresent>
</body>
</html>