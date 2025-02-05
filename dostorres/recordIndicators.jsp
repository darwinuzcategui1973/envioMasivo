<!--**
 * Title: recordIndicators.jsp <br/>
 * Copyright: (c) 2007 Focus Consulting<br/>
 * @version WebDocuments v4.3
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 19-12-2007 (YSA) Creation </li>
 </ul>

 */-->

<%@page import="com.desige.webDocuments.persistent.managers.HandlerGrupo"%>
<%@page import="com.desige.webDocuments.seguridad.forms.SeguridadUserForm"%>
<%@page import="com.desige.webDocuments.utils.beans.Users"%>
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>

<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb,"initPage();");
    if (onLoad==null) {
        response.sendRedirect(rb.getString("href.logout"));
    }
    
    Users usuario = (Users)session.getAttribute("user");
    SeguridadUserForm forma = new SeguridadUserForm();
    SeguridadUserForm securityForGroup = new SeguridadUserForm();
    HandlerGrupo.getFieldUser(forma, "seguridadUser", true, usuario.getNameUser());
    HandlerGrupo.getFieldUser(securityForGroup, "seguridadGrupo", false, usuario.getIdGroup());
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="./estilo/funciones.js"></script>
<script type="text/javascript">
    function initPage() {
        forma = document.forms[0];
        if ((forma)&&(forma.nameDocument)) {
            forma.nameDocument.focus();
        }
    }

  	function generar() {
        if (!validateCheck()) {
            alert('<%=rb.getString("rcd.alertFilter")%>');
        } else {
            if (confirm("<%=rb.getString("areYouSure")%>")) {
                forma = document.getElementById("selection");
                
                var valores = "";
				var lista = forma.listIndicators;

				if (lista){
		            for (row = 0; row < lista.length; row++){
						var valor = lista[row];
						if (valor.checked && valor.value!=""){
							valores += valor.value + ","; 
						}
					}
				}
				
				if(valores.length>0){
					valores = valores.substring(0,valores.length-1);
				}

                window.opener.document.recordForm.indicatorsSelected.value = valores;
				if (window.opener.document.recordForm.btnIndicators) {
            		window.opener.document.recordForm.btnIndicators.disabled = true;
        		}
				if (window.opener.document.recordForm.btnViewCharges) {
            		window.opener.document.recordForm.btnViewCharges.disabled = true;
        		}
				if (window.opener.document.recordForm.btnViewUsers) {
            		window.opener.document.recordForm.btnViewUsers.disabled = true;
        		}
        		window.opener.generateReport();
                this.close();
            }
        }
    }

  	function validateCheck() {
  		var selection = document.getElementById("selection");
  		
  	    for (var i=0; i < selection.length;i++) {
  	    	if (selection.elements[i].type == "checkbox"){
	            if (selection.elements[i].checked) {
	                return true;
	            }
	        }
        }
        return false;
    }  
    
    function checkAll() {
	    var selection = document.getElementById("selection");
        if (selection.length > 1) {
            for (var row = 1; row < selection.length; row++) {
                if (selection.elements[row].value.length > 0) {
                    selection.elements[row].checked = selection.all.checked;
                }
            }
        }
    }
          
</script>
</head>
<body class="bodyInternas" <%=onLoad%>>
<form action="" name="selection" id="selection">
<table align="center" border="0" width="100%">
	<tr>
    	<td width="100%">
        	<table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
            	<tr>
                	<td class="td_title_bc" height="22 px" width="*">
            	        <%=rb.getString("rcd.titleFilter")%>
    	            </td>
        	    </tr>
        	</table>
		</td>
   	</tr>
    <tr>
       	<td height="4"></td>
    </tr>
    <tr>
      	<td valign="top">
        	<table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
            	<tr>
                	<td class='td_1' align="center" width="10">
	                	<input type="checkbox" name="all" onClick="javascript:checkAll();"/>
                    </td>
                    <td class='td_1' width="*"><%=rb.getString("rcd.varAlls")%></td>
                </tr>  
            	<tr>
                	<td class='td_0' align="center" width="10">
	                	<input name="listIndicators" type="checkbox" value="33">
                    </td>
                    <td class='td_0' width="*"><%=rb.getString("rcd.varDoc6")%></td>
                </tr>  
            	<!-- <tr>
                	<td class='td_1' align="center" width="25">
	                	<input name="listIndicators" type="checkbox" value="1">
                    </td>
                    <td class='td_1'><%=rb.getString("rcd.varDoc1")%></td>
                </tr>
            	<tr>
                	<td class='td_0' align="center">
	                	<input name="listIndicators" type="checkbox" value="2">
                    </td>
                    <td class='td_0'><%=rb.getString("rcd.varDoc2")%></td>
                </tr> -->
            	<tr>
                	<td class='td_1' align="center">
	                	<input name="listIndicators" type="checkbox" value="3">
                    </td>
                    <td class='td_1'><%=rb.getString("rcd.varDoc3")%></td>
                </tr>
            	<tr>
                	<td class='td_0' align="center">
	                	<input name="listIndicators" type="checkbox" value="4">
                    </td>
                    <td class='td_0'><%=rb.getString("rcd.varDoc4")%></td>
                </tr>
            	<tr>
                	<td class='td_1' align="center">
	                	<input name="listIndicators" type="checkbox" value="5">
                    </td>
                    <td class='td_1'><%=rb.getString("rcd.varDoc5")%></td>
                </tr>  
                                                                              
            	<tr>
                	<td class='td_0' align="center">
	                	<input name="listIndicators" type="checkbox" value="6">
                    </td>
                    <td class='td_0'><%=rb.getString("rcd.varFlow1")%></td>
                </tr>
            	<tr>
                	<td class='td_1' align="center">
	                	<input name="listIndicators" type="checkbox" value="7">
                    </td>
                    <td class='td_1'><%=rb.getString("rcd.varFlow2")%></td>
                </tr>  
            	<tr>
                	<td class='td_0' align="center">
	                	<input name="listIndicators" type="checkbox" value="8">
                    </td>
                    <td class='td_0'><%=rb.getString("rcd.varFlow3")%></td>
                </tr>
            	<tr>
                	<td class='td_1' align="center">
	                	<input name="listIndicators" type="checkbox" value="9">
                    </td>
                    <td class='td_1'><%=rb.getString("rcd.varFlow4")%></td>
                </tr>  
            	<tr>
                	<td class='td_0' align="center">
	                	<input name="listIndicators" type="checkbox" value="10">
                    </td>
                    <td class='td_0'><%=rb.getString("rcd.varFlow5")%></td>
                </tr>
            	<tr>
                	<td class='td_1' align="center">
	                	<input name="listIndicators" type="checkbox" value="11">
                    </td>
                    <td class='td_1'><%=rb.getString("rcd.varFlow6")%></td>
                </tr>  
            	<tr>
                	<td class='td_0' align="center">
	                	<input name="listIndicators" type="checkbox" value="12">
                    </td>
                    <td class='td_0'><%=rb.getString("rcd.varFlow7")%></td>
                </tr>
            	<tr>
                	<td class='td_1' align="center">
	                	<input name="listIndicators" type="checkbox" value="13">
                    </td>
                    <td class='td_1'><%=rb.getString("rcd.varFlow8")%></td>
                </tr>  
            	<tr>
                	<td class='td_0' align="center">
	                	<input name="listIndicators" type="checkbox" value="14">
                    </td>
                    <td class='td_0'><%=rb.getString("rcd.varFlow9")%></td>
                </tr>
            	<tr>
                	<td class='td_1' align="center">
	                	<input name="listIndicators" type="checkbox" value="15">
                    </td>
                    <td class='td_1'><%=rb.getString("rcd.varFlow10")%></td>
                </tr>  

                <%if (ToolsHTML.showFTP() && ToolsHTML.userCanSeeFlujos(forma, securityForGroup)) {%>
            	<tr>
                	<td class='td_0' align="center">
	                	<input name="listIndicators" type="checkbox" value="16">
                    </td>
                    <td class='td_0'><%=rb.getString("rcd.varFTP1")%></td>
                </tr>
            	<tr>
                	<td class='td_1' align="center">
	                	<input name="listIndicators" type="checkbox" value="17">
                    </td>
                    <td class='td_1'><%=rb.getString("rcd.varFTP2")%></td>
                </tr> 
            	<tr>
                	<td class='td_0' align="center">
	                	<input name="listIndicators" type="checkbox" value="18">
                    </td>
                    <td class='td_0'><%=rb.getString("rcd.varFTP3")%></td>
                </tr>
            	<tr>
                	<td class='td_1' align="center">
	                	<input name="listIndicators" type="checkbox" value="19">
                    </td>
                    <td class='td_1'><%=rb.getString("rcd.varFTP4")%></td>
                </tr> 
            	<tr>
                	<td class='td_0' align="center">
	                	<input name="listIndicators" type="checkbox" value="20">
                    </td>
                    <td class='td_0'><%=rb.getString("rcd.varFTP5")%></td>
                </tr>
            	<tr>
                	<td class='td_1' align="center">
	                	<input name="listIndicators" type="checkbox" value="21">
                    </td>
                    <td class='td_1'><%=rb.getString("rcd.varFTP6")%></td>
                </tr> 
                <%}%>
                
                <%if (ToolsHTML.showSACOP() && ToolsHTML.userCanSeeSacop(forma, securityForGroup)){ %>
            	<tr>
                	<td class='td_0' align="center">
	                	<input name="listIndicators" type="checkbox" value="22">
                    </td>
                    <td class='td_0'><%=rb.getString("rcd.varSacop1")%></td>
                </tr>
            	<tr>
                	<td class='td_1' align="center">
	                	<input name="listIndicators" type="checkbox" value="23">
                    </td>
                    <td class='td_1'><%=rb.getString("rcd.varSacop2")%></td>
                </tr> 
            	<tr>
                	<td class='td_0' align="center">
	                	<input name="listIndicators" type="checkbox" value="24">
                    </td>
                    <td class='td_0'><%=rb.getString("rcd.varSacop3")%></td>
                </tr>
            	<tr>
                	<td class='td_1' align="center">
	                	<input name="listIndicators" type="checkbox" value="25">
                    </td>
                    <td class='td_1'><%=rb.getString("rcd.varSacop4")%></td>
                </tr> 
                <%}%>
                
            	<tr>
                	<td class='td_0' align="center">
	                	<input name="listIndicators" type="checkbox" value="26">
                    </td>
                    <td class='td_0'><%=rb.getString("rcd.varPrint1")%></td>
                </tr>
            	<!-- <tr>
                	<td class='td_1' align="center">
	                	<input name="listIndicators" type="checkbox" value="27">
                    </td>
                    <td class='td_1'><%=rb.getString("rcd.varPrint2")%></td>
                </tr> 
            	<tr>
                	<td class='td_0' align="center">
	                	<input name="listIndicators" type="checkbox" value="28">
                    </td>
                    <td class='td_0'><%=rb.getString("rcd.varPrint3")%></td>
                </tr>
            	<tr>
                	<td class='td_1' align="center">
	                	<input name="listIndicators" type="checkbox" value="29">
                    </td>
                    <td class='td_1'><%=rb.getString("rcd.varPrint4")%></td>
                </tr> --> 
            	<tr>
                	<td class='td_1' align="center">
	                	<input name="listIndicators" type="checkbox" value="30">
                    </td>
                    <td class='td_1'><%=rb.getString("rcd.varPrint5")%></td>
                </tr>
            	<tr>
                	<td class='td_0' align="center">
	                	<input name="listIndicators" type="checkbox" value="31">
                    </td>
                    <td class='td_0'><%=rb.getString("rcd.varPrint6")%></td>
                </tr> 
            	<tr>
                	<td class='td_1' align="center">
	                	<input name="listIndicators" type="checkbox" value="32" >
                    </td>
                    <td class='td_1'><%=rb.getString("rcd.varPrint7")%></td>
                </tr>
			</table>                            	
       	</td>
    </tr>
<logic:present name="securityRecord">
	<logic:equal name="securityRecord" property="toGenerate"  value="1">
    <tr>
      	<td width="100%" align="center">
           	<br>
           	<input type="button" name="btnGenerate" onclick="javascript:generar(this.form);" value='<%=rb.getString("rcd.generateReport")%>' class="boton">              
       	</td>
    </tr> 		          
	</logic:equal>
</logic:present>
    
</table>
</form>
</body>
</html>
