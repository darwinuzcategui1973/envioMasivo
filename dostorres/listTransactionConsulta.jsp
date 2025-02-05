<!-- listTransactionConsulta.jsp -->
<%@ page import="java.util.ResourceBundle,
				 com.desige.webDocuments.files.forms.DocumentForm,
				 java.util.ArrayList,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.persistent.managers.HandlerDBUser,
                 com.desige.webDocuments.persistent.managers.HandlerNorms,
	             com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 com.desige.webDocuments.utils.beans.PaginPage,
                 com.desige.webDocuments.utils.Constants,
                 com.desige.webDocuments.persistent.managers.HandlerBD,
                 com.desige.webDocuments.persistent.managers.HandlerNorms,
				 com.desige.webDocuments.document.forms.BaseDocumentForm,
				 com.desige.webDocuments.persistent.managers.HandlerDocuments,
				 com.desige.webDocuments.utils.beans.Search,
				 java.sql.Connection,
				 sun.jdbc.rowset.CachedRowSet,
                 java.util.Collection"%>
                 
<%@ page import="com.desige.webDocuments.persistent.managers.HandlerBD" %>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
	if(request.getAttribute("visible")==null || !String.valueOf(request.getAttribute("visible")).equals("true") ) {
		//response.sendRedirect("administracionMain.do");
	}

    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    Users usuario = (Users)session.getAttribute("user");
    String ok = (String) ToolsHTML.getAttributeSession(session, "usuario", false);
    String info = (String) ToolsHTML.getAttribute(request, "info", true);

	int nameWidth=60;
	int lineas=Constants.LINEAS_POR_PAGINA;
	int pintada=0;
	
    String propietario = ToolsHTML.getAttribute(request,"propietario")!=null?(String)ToolsHTML.getAttribute(request,"propietario"):"";

%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">

<script type="text/javascript" src="./estilo/popcalendar2.js?sdf"></script>
<script type="text/javascript" src="./estilo/funciones.js"></script>
<script>
function getDateExpire(dateField,nameForm,dateValue,text){
    //window.open("getDate.jsp?field=" + dateField + "&nameForm="+nameForm+"&valueForm="+dateValue+"&text="+text,"","width=350,height=300,resizable=no,scrollbars=yes,left=300,top=250");
    window.open("getDate.jsp?field=" + dateField + "&nameForm="+nameForm+"&valueForm="+dateValue+"&text="+text,"","width=275,height=170,resizable=no,scrollbars=yes,left=60,top=250");
    
}
function ordenar(orderBy) {

    forma = document.search;
    forma.orderBy.value = orderBy;

    forma.expiredFromHIDDEN.value = forma.expiredFrom.value;
    forma.expiredToHIDDEN.value = forma.expiredTo.value;

    forma.submit();
}

</script>
</head>
<body class="bodyInternas" >
<table cellSpacing="1" cellPadding="0" align="center" border="0" width="100%"  height="100%" >
	<tr>
		<td width="<%=ToolsHTML.ANCHO_MENU%>px" valign="top" align="left" >
			<table cellSpacing="0" cellPadding="2"  border="0" width="<%=ToolsHTML.ANCHO_MENU%>px"  height="100%" style="/*border-collapse:collapse;*/border-color:#ofofof;border:1px solid #efefef">
				<tr>
					<td height="30px" class="ppalBoton" onmouseover="this.className='ppalBoton ppalBoton2'" onmouseout="this.className='ppalBoton'">
						<table border="0" cellspacing="0" cellpadding="2">
							<tr>
								<td>
									<a id="opt" href="#" class="menutip enlace_imagen">
									<img src="images/rightDark.gif" border="0" onClick="" >
								    </a>
								</td>
								<td class="ppalTextBold" width="100%">
									<%=rb.getString("enl.transactions")%>
								</td>
								<td>
								    &nbsp;
								</td>
								<td>
								    <span ><img src="icons/find.png" title="<%=rb.getString("btn.search")%>" onClick="javascript:ordenar();"></span>&nbsp;
								</td>
								<td class="none">
									<table border="0" cellspacing="1" cellpadding="0">
										<tr>
											<td height="1px">
										    <div id="flechaArriba" style="display:none"><img src="images/up3.gif"></div>
										    <div id="flechaAbajo" style="display:none"><img src="images/down3.gif"></div>
										    </td>
										</tr>
									</table>
								</td>
								<td>
								    &nbsp;<span id="signoMenos" style="display:none"><img src="images/replegar.gif" title="<%=rb.getString("seeLess")%>" onClick="javascript:more('none');"></span>&nbsp;
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<form name="search" method="post" action="listTransactionConsulta.do" style="margin:0;">
					    <input type="hidden" name="more" value="<%=session.getAttribute("more")%>"/>
					    <input type="hidden" name="accion" value="1"/>
					    <input type="hidden" name="orderBy" value=""/>
					    <input type="hidden" name="pages" value=""/>
					    <input id="public" name="public"   type="hidden" value="2"/>
					    
					<td id="panelFiltro" height="99%" bgcolor="white" valign="top" background="menu-images/backgd.png" class="scrollEnabled">
						<div id="listaOpcion" style="height:100%" class="scrollEnabled" >
						<table class="ppalText" style="cursor:pointer;" cellspacing="0" border="0" valign="top">

							<tr>
								<td>
					                <%=rb.getString("sch.from")%>
		                            <table cellpadding="0" cellspacing="0" border="0" style="margin:0px;">
		                            <tr>
		                            	<td>
							                <input type="hidden" name="expiredFromHIDDEN" value=""></input>
							                <input type="text" id="expiredFrom" name="expiredFrom" readonly disabled maxlength="10" style="width:140px;height: 19px" value="<%=request.getAttribute("expiredFromHIDDEN") == null ?"":request.getAttribute("expiredFromHIDDEN")%>" />
		                            	</td>
		                            	<td>
		                            		<a href="#" onclick='showCalendar(document.getElementById("expiredFrom"),"calendarioExpiredFrom")' ><img src="images/calendario2.gif" border="0" ></a>
		                            	</td>
		                            	<td>
		                            		<a href='#' onclick='document.search.expiredFromHIDDEN.value=""; document.search.expiredFrom.value="";' value='<%=rb.getString("btn.clear")%>' ><img src="images/eraser.gif" border="0" alt='<%=rb.getString("btn.clear")%>' text='<%=rb.getString("btn.clear")%>'></a>
		                            	</td>
		                            </tr>
		                            </table>
		                            <span id="calendarioExpiredFrom"></span>
								</td>
							</tr>
							
							<tr>
								<td>
		                            <%=rb.getString("sch.expiresTo")%>
		                            <table cellpadding="0" cellspacing="0" border="0" style="margin:0px;">
		                            <tr>
		                            	<td>
				                            <input type="hidden" name="expiredToHIDDEN" value=""></input>
				                            <input type="text" id="expiredTo" name="expiredTo" readonly disabled maxlength="10" style="width:140px;height: 19px"  value="<%=request.getAttribute("expiredToHIDDEN") == null ?"":request.getAttribute("expiredToHIDDEN")%>" />
		                            	</td>
		                            	<td>
		                            		<a href="#" onclick='showCalendar(document.getElementById("expiredTo"),"calendarioExpiredTo")' ><img src="images/calendario2.gif" border="0" ></a>
		                            	</td>
		                            	<td>
		                            		<a href='#' onclick='document.search.expiredToHIDDEN.value=""; document.search.expiredTo.value="";' value='<%=rb.getString("btn.clear")%>' ><img src="images/eraser.gif" border="0" alt='<%=rb.getString("btn.clear")%>' text='<%=rb.getString("btn.clear")%>'></a>
		                            	</td>
		                            </tr>
		                            </table>
		                            <span id="calendarioExpiredTo"></span>
								</td>
							</tr>

							<tr>
								<td>
									<%=rb.getString("user.title")%><br/> 
					                <select name="propietario" size="1" style="width:180px; height: 19px" styleClass="classText">
					                    <logic:present name="allUsers" scope="session">
					                        <option value="">&nbsp;</option>
					                        <logic:iterate id="bean" name="allUsers" scope="session" type="com.desige.webDocuments.utils.beans.Search">
					                            <logic:equal value="<%=propietario%>" property="id" name="bean" >
					                                <option value="<%=bean.getId()%>" selected="true"><%=bean.getDescript()%></option>
					                            </logic:equal>
					                            <logic:notEqual value="<%=propietario%>" property="id" name="bean" >
					                                <logic:notEqual value="<%=propietario%>" property="id" name="bean" >
					                                    <option value="<%=bean.getId()%>"><%=bean.getDescript()%></option>
					                                </logic:notEqual>
					                            </logic:notEqual>
					                        </logic:iterate>
					                    </logic:present>
					                </select>
					                <logic:notPresent name="allUsers" scope="session">
					                    <%=rb.getString("E0006")%>
					                </logic:notPresent>
					                <input type="hidden" name="nameOwner" value=""/>
								</td>
							</tr>

						</table>
						</div>
						</form>
					</td>
				</tr>
			</table>
		</td>
		
		
		
		<td align="center" valign="top">
		    
		    
		    <table align=center border="0" width="100%"  height="96%" cellspacing="0" cellpadding="0">
		        <tr>
		            <td><!--titulos-->
		                <table align="center" border="0" cellspacing="1" cellpadding="2" width="100%" height="100%">
		                    <tr >

	                        <td id="COL_A" class="td_orange_l_b barraBlue" width="10%" style="height:14px" nowrap>
                                <%=rb.getString("trasaction.title.date")%><a href="javascript:ordenar('date');"><img border=0 src="img/<%=ToolsHTML.orden(session,"date")?"asc":"desc"%>.gif"/></a>
                            </td>
	                        <td id="COL_B" class="td_orange_l_b barraBlue" width="8%" style="height:14px" nowrap>
                            	<%=rb.getString("trasaction.title.nameUser")%> <a href="javascript:ordenar('nameUser');"><img border=0 src="img/<%=ToolsHTML.orden(session,"nameUser")?"asc":"desc"%>.gif"/></a>
                            </td>
                            <td id="COL_C" class="td_orange_l_b barraBlue" width="18%" style="height:14px" nowrap>
	                            <%=rb.getString("trasaction.title.descrip")%> <a href="javascript:ordenar('descrip');"><img border=0 src="img/<%=ToolsHTML.orden(session,"descrip")?"asc":"desc"%>.gif"/></a>                            
	                        </td>
	                        <td id="COL_D" class="td_orange_l_b barraBlue" width="15%" style="height:14px" nowrap>
	                            <%=rb.getString("trasaction.title.valueStart")%><a href="javascript:ordenar('valueStart');"><img border=0 src="img/<%=ToolsHTML.orden(session,"valueStart")?"asc":"desc"%>.gif"/></a>
	                        </td>
	                        <td id="COL_E" class="td_orange_l_b barraBlue" width="15%" style="height:14px" nowrap>
	                            <%=rb.getString("trasaction.title.valueEnd")%><a href="javascript:ordenar('valueEnd');"><img border=0 src="img/<%=ToolsHTML.orden(session,"valueEnd")?"asc":"desc"%>.gif"/></a>
	                        </td>
	                        <td id="COL_F" class="td_orange_l_b barraBlue" width="4%" style="height:14px" nowrap>
	                            <%=rb.getString("trasaction.title.ip")%><a href="javascript:ordenar('ip');"><img border=0 src="img/<%=ToolsHTML.orden(session,"ip")?"asc":"desc"%>.gif"/></a>
	                        </td>
		                    <logic:present name="listTransaction" scope="request">
								<%
								CachedRowSet lista = (CachedRowSet) request.getAttribute("listTransaction");
								int cont=0;
								int indice=0;
								if(lista!=null) {
								while(lista.next()) {
									pintada++;%>		                    
		                        <tr class='fondo_<%=cont==0?cont++:cont--%>'>
	                                 <td id="LIN_A<%=indice%>" class="td_gris_l" nowrap>
                                        <%=ToolsHTML.formatDateShow(lista.getString("fecha"),true)%>
                                     </td>
                                     
		                            <td id="LIN_B<%=indice%>" class="td_gris_l" nowrap>
                                        <%=lista.getString("nameUser")%>
	                                </td>

		                            <td id="LIN_C<%=indice%>" class="td_gris_l" nowrap>
                                        <%=ToolsHTML.replaceAcentos(lista.getString("tra_descrip"))%>
	                                </td>

		                            <td id="LIN_D<%=indice%>" class="td_gris_l" nowrap>
                                        <%=lista.getString("valor_inicial")%>
	                                </td>

		                            <td id="LIN_E<%=indice%>" class="td_gris_l" nowrap>
                                        <%=lista.getString("valor_final")%>
	                                </td>

		                            <td id="LIN_F<%=indice%>" class="td_gris_l" nowrap>
                                        <%=lista.getString("terminal")%>
	                                </td>
	                            </tr>
	                            <%indice++;
	                            }
								
		                        for(pintada++;pintada<=lineas;pintada++){%>
	                        	<tr>
	                        		<td colspan="6">&nbsp;</td>
	                        	</tr>
		                        <%}
								
	                            }%>
		                    </logic:present>
		                </table>
		            </td>
		        </tr>
		    </table>

     </tr>
</table>
<div id="caja" class="fondoOscuro" style="display:none;">
</div>
<div id="caja2" style="position:absolute;top:0;left:0;">
	<iframe id="frmUbicacion" name="frmUbicacion" src="searchDocumentUbicacion.jsp" frameborder="no" style="width:0px;height:0px;"></iframe>
</div>
<div id="caja4" style="position:absolute;top:0;left:0;">
	<iframe id="frmPublicEraser" name="frmPublicEraser" src="searchDocumentPublic.jsp" frameborder="no" style="width:0px;height:0px;"></iframe>
</div>
<div id="caja3" style="display:none;">
</div>   
</body>
</html>
