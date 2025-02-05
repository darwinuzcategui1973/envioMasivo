<!-- perfil.jsp -->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.persistent.managers.HandlerParameters,
                 com.desige.webDocuments.utils.ToolsHTML"%>
                 
<%@ page import="com.desige.webDocuments.persistent.managers.HandlerBD" %>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
	if(request.getAttribute("visible")==null || !String.valueOf(request.getAttribute("visible")).equals("true") ) {
		//response.sendRedirect("loadPerfil.do");
	}

    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    Users usuario = (Users)session.getAttribute("user");
    String ok = (String) ToolsHTML.getAttributeSession(session, "usuario", false);
    String info = (String) ToolsHTML.getAttribute(request, "info", true);
    
    if (ToolsHTML.checkValue(ok)) {
        onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
        onLoad.append(ok).append("'");
    } else {
        String link = rb.getString("href.logout");
        response.sendRedirect(link + "?target=parent");
    }
    if (ToolsHTML.checkValue(info)) {
        onLoad.append(";alert('").append(info).append("')");
    }
    if (onLoad.length() > 0) {
        onLoad.append("\"");
    }
    ToolsHTML.clearSession(session, "application.profile");
    String styleTxt = "width: 300px; height: 19px";
    String numGo = request.getParameter("numGo");
    if (ToolsHTML.isEmptyOrNull(numGo)) {
        numGo = "1";
    }

    //Luis Cisneros 28/02/11
    //Validacion de correo opcional
    boolean validateEmail = String.valueOf(HandlerParameters.PARAMETROS.getValidateEmail()).equals("0");


%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language="JavaScript">
    function validarEntero(valor) {
        //intento convertir a entero.
        //si era un entero no le afecta, si no lo era lo intenta convertir
        valor = parseInt(valor)
        //Compruebo si es un valor numérico
        if (isNaN(valor)) {
            //entonces (no es numero) devuelvo el valor cadena vacia
            return false;
        } else {
            //En caso contrario (Si era un número) devuelvo el valor
            return true;
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
        if (forma.apellidos && trim(forma.apellidos.value).length == 0) {
            alert("<%=rb.getString("err.notApe")%>");
            return false;
        }
        if (forma.nombres && trim(forma.nombres.value).length == 0) {
            alert("<%=rb.getString("err.notName")%>");
            return false;
        }
        if (forma.email && trim(forma.email.value).length == 0) {
            alert("<%=rb.getString("err.notMail")%>");
            return false;
        }


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
        
        if (forma.numRecordPages && (forma.numRecordPages.value.length == 0 || !validarEntero(forma.numRecordPages.value))) {
            alert("<%=rb.getString("user.notNumberReg")%>");
            return false;
        }
        <logic:present name="mustChange" scope="session">
            if (forma.repclave.value == forma.clave.value) {
                alert('<%=rb.getString("user.mustChange")%>');
                return false;
            }
        </logic:present>
        return true;
    }

	String.prototype.trim = function() { return this.replace(/^\s*|\s*$/g,""); };

    function salvar() {
    	document.perfil.clave.value = document.perfil.clave.value.trim();
    	document.perfil.clavenueva.value = document.perfil.clavenueva.value.trim();
        if (document.perfil.clave.value.length>0) {
            if  (document.perfil.clave.value==document.perfil.clavenueva.value){
                if (document.perfil.clave.value.length >= parseInt(document.perfil.lengthPass.value)) {
                    if (validar(document.perfil)) {
                        document.perfil.submit();
                    }
                } else {
                    alert ("<%=rb.getString("clave.lenght")%> " + document.perfil.lengthPass.value);
                }
            }else{
                  alert ("<%=rb.getString("clave.clavedif")%>");
            }
        } else {
            alert ("<%=rb.getString("clave.clavevacia")%>");
        }
    }

    function editField(pages,input,value,forma) {
        var hWnd = null;
        hWnd = window.open(pages+"?input="+input+"&value="+value+"&nameForma="+forma.name,"Catalogo","width=600,height=350,resizable=yes,scrollbars=yes,left=212,top=250");
        if ((document.window != null) && (!hWnd.opener)){
            hWnd.opener = document.window;
        }
    }

    function showHome() {
        <logic:notPresent name="primeravez" scope="session">
            //history.back();
            location.href = "loadAllWF.do?goTo=reload";
        </logic:notPresent>
        <logic:present name="primeravez" scope="session">
            <logic:equal value="true" name="primeravez">
                location.href = "loadAllWF.do?goTo=reload";  //Luis Cisneros 12/03/07; Cuando Cancela debe ir a principal. 
            </logic:equal>
            <logic:notEqual value="true" name="primeravez">
                history.back();
            </logic:notEqual>
            <%
                session.removeAttribute("primeravez");
            %>
        </logic:present>        
    }

    function loadLinks() {
        forma = document.forms[0];
        forma.action = "loadUrls.do";
        forma.submit();
    }

</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>
<table cellSpacing="1" cellPadding="0" align="center" border="0" width="100%"  height="100%" >
	<tr>
		<td width="<%=ToolsHTML.ANCHO_MENU%>px"  valign="top" align="left" >
			<table cellSpacing="1" cellPadding="2"  border="0" width="<%=ToolsHTML.ANCHO_MENU%>px"  height="100%" style="border-collapse:collapse;border-color:#ofofof;border:1px solid #efefef">
				<tr>
					<td height="20px" class="ppalBoton" onmouseover="this.className='ppalBoton ppalBoton2'" onmouseout="this.className='ppalBoton'">
						<table border="0" cellspacing="0" cellpadding="2">
							<tr>
								<td class="ppalTextBold" width="100%">
									<%=rb.getString("enl.perfil")%>
								</td>
								<td>
								    &nbsp;
								</td>
								<td>
								    <span ><img src="images/floppy.gif" width="17" height="17" title="<%=rb.getString("btn.save")%>" onClick="javascript:salvar();"></span>&nbsp;
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td height="200px" bgcolor="white" valign="top" background="menu-images/backgd.jpg">
					
						<table class="ppalText" style="cursor:pointer;" cellspacing="5">
							<tr onclick="ver('DocPen')">
								<td><img src="menu-images/doc.gif"/></td>
								<td>&nbsp;Ficha Personal</td>
							</tr>
							<tr onclick="ver('DocPen')">
								<td><img src="menu-images/doc.gif"/></td>
								<td>&nbsp;Enlaces</td>
							</tr>
						</table>
					
					</td>
				</tr>
						<%if (usuario!=null) {
                            out.println(ToolsHTML.printMenu(rb,usuario.getUser(),usuario.getIdGroup(),true,request));
                        }%>
			</table>
		</td>
		<td align="center" valign="top">
	        <html:form action="editPerfil">
            <html:hidden property="id"/>
            <html:hidden property="lengthPass"/>
			<table cellspacing="0" cellpadding="3" border="0" width="100%">
				<tr>
					<td colspan="2" width="100%">
                        <table class="clsTableTitle" width="100%" cellSpacing="0" cellPadding="0" align=center border=0>
                            <tbody>
                                <tr>
                                    <td class="td_title_bc" height="21">
                                    <logic:present name="primeravez" scope="session">
                                        <%=rb.getString("enl.perfilseg")%>
                                     </logic:present>
                                     <logic:notPresent scope="session" name="primeravez">
                                        <%=rb.getString("enl.perfil")%>
                                     </logic:notPresent>
                                    <br><br></td>
                                </tr>
                            </tbody>
                        </table>
					</td>
				</td>
				<tr>
					<td width="50%"><!-- ini primera columna -->
				  		<fieldset>
				  		<legend class="etiqueta">Datos Personales</legend>

						<table width="100%" border="0" cellspacing="3" cellpadding="3">
						    <tr>
						        <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="26" valign="middle" style="width:250px">
									<%=rb.getString("login.user")%>:
						        </td>
						        <td width="45%" valign="middle" >
									<font class="txtRigth">
									<bean:write name="perfil" property="user" />
									</font>
						        </td>
						        <td width="*">&nbsp;        </td>
						    </tr>
						    <tr>
						        <td valign="middle" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="26">
									<%=rb.getString("user.ape")%>:
						        </td>
						        <td valign="top">
									<html:text property="apellidos" size="30" styleClass="classText" style="<%=styleTxt%>"/>
						        </td>
						        <td>&nbsp;        </td>
						    </tr>
						    <tr>
						        <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="26">
									<%=rb.getString("user.name")%>:
						        </td>
						        <td><html:text property="nombres" size="30" styleClass="classText" style="<%=styleTxt%>"/>        </td>
						        <td>&nbsp;        </td>
						    </tr>
						    <tr>
						        <td valign="middle" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="26">
									<%=rb.getString("user.cargo")%>:
						        </td>
						        <td>
									<html:text property="cargo" size="30" disabled="true" readonly="true" styleClass="classText" style="<%=styleTxt%>"/>
									<html:hidden property="cargo"/>   <%-- Luis Cisneros 12/03/07, El Cargo no es editable --%>
						        </td>
						        <td>
									&nbsp;
						        </td>
						    </tr>
						    <tr>
						        <td valign="middle" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="26">
									<%=rb.getString("user.email")%>:
						        </td>
						        <td>
									<html:text property="email" size="30" styleClass="classText" style="<%=styleTxt%>"/>
						        </td>
						        <td>
									&nbsp;
						        </td>
						    </tr>
			                <tr>
			                    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="23">
			                        <%=rb.getString("user.phone")%>:
			                     </td>
			                    <td><html:text property="telefono" size="12" styleClass="classText"/></td>
			                    <td>&nbsp;</td>
			                </tr>
						</table>

						</fieldset>
					</td>
					<td width="50%" rowspan="2" valign="top"><!-- ini primera columna -->
				  		<fieldset>
				  		<legend class="etiqueta">Configuraci&oacute;n</legend>
				  		
							<table cellSpacing="3" cellPadding="3" align="center" border="0" width="100%">
								<tr>
								    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="23">
									<%=rb.getString("user.languaje")%>:
								     </td>
								    <td>
									<logic:present name="idiomas" scope="request">
									    <html:select property="idioma" styleClass="classText">
										<logic:iterate id="idio" name="idiomas" type="com.desige.webDocuments.utils.beans.Search">
										    <html:option value="<%=idio.getId()%>">
											<%=idio.getDescript()%>
										    </html:option>
										</logic:iterate>
									    </html:select>
									</logic:present>
								    </td>
								    <td>&nbsp;</td>
								</tr>
								<tr>
								    <td class="titleLeft" style="background: url(img/btn120x2.png); background-repeat: no-repeat" height="23">
									<%=rb.getString("user.area")%>:
								     </td>
								    <td>
							
									<logic:present name="tblareas" scope="request">
									    <%String selecArea="";%>
									     <select name="area" disabled size="1" style="width:210px;" styleClass="classText">
										    <logic:iterate id="idio" name="tblareas" type="com.desige.webDocuments.utils.beans.Search">
											<logic:equal name="perfil" property="area" value="<%=idio.getId()%>">
											     <option value="<%=idio.getId()%>" selected="true"><%=idio.getDescript()%></option>
											</logic:equal>
											<logic:notEqual name="perfil" property="area" value="<%=idio.getId()%>">
											     <option value="<%=idio.getId()%>"><%=idio.getDescript()%></option>
											</logic:notEqual>
							
										    </logic:iterate>
									    </select>
									</logic:present>
							
							
								    </td>
								    <td>&nbsp;</td>
								</tr>
								<tr>
								    <td class="titleLeft" style="background: url(img/btn120x2.png); background-repeat: no-repeat" height="42px">
									<%=rb.getString("user.numRecord")%>:
								     </td>
								    <td>
									<html:text property="numRecordPages" size="12" styleClass="classText"/>
								    </td>
								    <td>&nbsp;</td>
								</tr>
								<tr>
								    <td class="titleLeft" style="background: url(img/btn120x2.png); background-repeat: no-repeat" height="42px">
									<%=rb.getString("user.eln")%>:
								     </td>
								    <td>
									<!--<a href="loadUrls.do" class="ahref_b" target="_self">-->
									<a href="javascript:loadLinks();" class="ahref_b" target="_self">
									    <%=rb.getString("user.links")%>
									</a>
								    </td>
								    <td>&nbsp;</td>
								</tr>
								<!-- 02 de agosto 2005 inicio -->
								<tr>
								<td height="42" class="titleLeft" style="background: url(img/btn120x2.png); background-repeat: no-repeat" valign="middle">
								    <%=rb.getString("clave.nueva")%>
								</td>
								<td>
								    <html:password property="clave"  size="50" styleClass="classText"/>
								    <html:hidden  property="repclave"/>
								</td>
								</tr>
								<tr>
								<td height="42" class="titleLeft" style="background: url(img/btn120x2.png); background-repeat: no-repeat" valign="middle">
								    <%=rb.getString("clave.rep_nueva")%>
								</td>
								<td><html:password property="clavenueva" size="50" styleClass="classText"/>
								</td>
							    </tr>
							</table>

						</fieldset>
					</td>
				</tr>
				<tr>
					<td width="50%" valign="top"><!-- ini primera columna -->
				  		<fieldset>
				  		<legend class="etiqueta">Ubicaci&oacute;n Geografica</legend>

						<table width="100%" border="0" cellspacing="3" cellpadding="3">
						    <tr>
						        <td class="titleLeft" style="background: url(img/btn120x4.png); background-repeat: no-repeat" height="23" valign="middle" >
									<%=rb.getString("user.dir")%>:
						        </td>
						        <td>
									<html:textarea property="direccion" cols="30" rows="4" styleClass="classText" style="width: 300px"/>
						        </td>
						        <td>&nbsp;        </td>
						    </tr>
						    <tr>
						        <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="23">
									<%=rb.getString("user.ciudad")%>:
						        </td>
						        <td>
									<html:hidden property="ciudad"/>
									<html:text property="nameCity" size="30" readonly="true" styleClass="classText"/>
									<input type="button" class="boton" value="..." onClick="javascript:editField('loadCity.do','ciudad','nameCity',this.form);" style="width:20px;"/>
						        </td>
						        <td>&nbsp;        </td>
						    </tr>
						    <tr>
						        <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="22">
									<%=rb.getString("user.edo")%>:
						        </td>
						        <td>
									<html:hidden property="estado"/>
									<html:text property="nameState" size="30" readonly="true" styleClass="classText"/>
									<input type="button" class="boton" value="..." onClick="javascript:editField('loadState.do','estado','nameState',this.form);" style="width:20px;" />
						        </td>
						        <td>&nbsp;        </td>
						    </tr>
			                <tr>
			                    <td valign="top" class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="22px"  style="width:250px">
			                        <%=rb.getString("user.pais")%>:
			                     </td>
			                    <td>
			                        <html:hidden property="pais"/>
			                        <html:text property="namePais" size="30" readonly="true" styleClass="classText"/>
			                        <input type="button" class="boton" value="..." onClick="javascript:editField('loadCountries.do','pais','namePais',this.form);" style="width:20px;" />
			                    </td>
			                    <td>&nbsp;</td>
			                </tr>
							<tr>
							    <td class="titleLeft" style="background: url(img/btn120.png); background-repeat: no-repeat" height="23">
								<%=rb.getString("user.zip")%>:
							     </td>
							    <td><html:text property="zip" size="12" styleClass="classText"/></td>
							    <td>&nbsp;</td>
							</tr>
						</table>
						
						</fieldset>
					</td><!-- fin primera columna -->
			    </tr>
			</table>
	        </html:form>
	        
		</td>
    </tr>
</table>
</body>
</html>
