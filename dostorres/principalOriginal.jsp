<!-- principalOriginal.jsp --> 
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.Users,
                 com.desige.webDocuments.persistent.managers.HandlerDocuments"%>
<%@ page import="org.apache.struts.taglib.html.Constants"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>

<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    StringBuffer onLoad = new StringBuffer("");
    String ok = (String)session.getAttribute("usuario");
    
    int MAXIMO = 4;
	int cont=0;
    

    //Luis Cisneros 12/03/07 Para mostrar informaci'on de info en la pestana ppal
    String info = (String) ToolsHTML.getAttribute(request, "info", true);
    //Fin 12/03/07

    if ((ok != null) && (ok.compareTo("") != 0)) {
        onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
        onLoad.append(ok);
        onLoad.append("'");

        //Luis Cisneros 12/03/07 Para mostrar informaci'on de info en la pestana ppal
        if (ToolsHTML.checkValue(info)) {
            onLoad.append(";alert('").append(info).append("');");
        }
        //Fin 12/03/07
        
        onLoad.append("\"");
    } else{
        response.sendRedirect(rb.getString("href.logout"));
    }


    
    ToolsHTML.clearSession(session,"application.ppal");
    String tipoImpresion=HandlerDocuments.TypeDocumentsImpresion;
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
	<!-- variables globales -->
	var http;
	

	function showDocument(idDoc){
		var forma = document.getElementById("selection");
		forma.idDocument.value = idDoc;
		forma.action = "showDataDocument.do";
		forma.submit();
	}
	function validar(){
        if (document.login.docSearch.value.length>0){
            alert('<%=rb.getString("err.notDocumentSearch")%>');
        }else{
		    document.login.submit();
        }
	}

    function showWF(idWorkFlow,row,owner,isFlexFlow){
    	var forma = document.getElementById("selection");
        forma.idWorkFlow.value = idWorkFlow;
        forma.row.value = row;
        forma.owner.value = owner;
        forma.isFlexFlow.value = isFlexFlow;
        forma.showStruct.value = 'false';
        forma.submit();
    }
    
	function ajaxobj() {
		try {
			_ajaxobj = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			try {
				_ajaxobj = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (E) {
				_ajaxobj = false;
			}
		}
		if (!_ajaxobj && typeof XMLHttpRequest!='undefined') {
			_ajaxobj = new XMLHttpRequest();
		}
		return _ajaxobj;
	}    
	
    function llenarCuadrante(cuadro) {
    	if(1==1 || cuadro=="docCheckOuts") {
			ajax = ajaxobj();
			ajax.open("POST", "fillMainAjax.do?cuadrante="+cuadro, true);
			ajax.onreadystatechange=function() {
				if (ajax.readyState == 4) {
					var arr = eval(ajax.responseText);
					
					var box = document.getElementById(cuadro);
					while (box.firstChild) {
					  box.removeChild(box.firstChild);
					}
					document.getElementById(cuadro+"Ver").style.display="none";
					
					for(var x=0;x<arr.length;x++) {
					    var tr    = document.createElement('TR');
					    var img    = document.createElement('IMG');
					    var ref    = document.createElement('a');
					    var td0   = document.createElement('TD');
					    var td1   = document.createElement('TD');
					    var td2   = document.createElement('TD');
					    var td3   = document.createElement('TD');
	
						img.src = arr[x].img; 
						ref.value = arr[x].action;
						ref.href = "#";
						ref.onclick = function() {
							//alert(this.value);
							eval(this.value);
						}
						ref.appendChild(document.createTextNode(arr[x].col2));
						
						ref.className = "ahref_b";
					    td1.className = "txt_b";
					    td2.className = "txt_b";
					    td3.className = "txt_b";
					    
					    td0.appendChild(img);
					    td1.appendChild(document.createTextNode(arr[x].col1));
					    td2.appendChild(ref);
					    td3.appendChild(document.createTextNode(arr[x].col3));
	
					    tr.appendChild(td0);
					    tr.appendChild(td1);
					    tr.appendChild(td2);
					    tr.appendChild(td3);
					    
					    box.appendChild(tr);
				    }
					
				}
			}
			ajax.send(null);    		
    	}
    }
    
function deleterow(node)
{
// Obtain a reference to the containing tr. Use a while loop
// so the function can be called by passing any node contained by
// the tr node.
var tr = node.parentNode;
while (tr.tagName.toLowerCase() != "tr")
tr = tr.parentNode;

// Remove the tr node and all children.
tr.parentNode.removeChild(tr);
}    
</script>
</head>

<body class="bodyInternas" <%=onLoad.toString()%>>

<table cellSpacing=0 cellPadding=2 align=center border=0 width="100%">
    <form name="selection" id="selection" action="loadWF.do">
        <input type="hidden" name="idWorkFlow" value="" />
        <input type="hidden" name="row" value="" />
        <input type="hidden" name="idMovement" value="" />
		<input type="hidden" name="idDocument" value="" />
        <input type="hidden" name="owner" value="" />
        <input type="hidden" name="showStruct" value="true"/>
        <input type="hidden" name="isFlexFlow" value="" />
        <input type="hidden" name="origen" value="principal" />

    </form>

	<tr>
    	<td valign="top" width="50%">
			<table width="100%" border="0" cellSpacing="0" cellPadding="0">
                <tr>
			        <td>
                        <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                            <tbody>
                                <tr>
                                    <td class="td_title_bc" height="15">
                                        <%=rb.getString("doc.pending")%>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                        <!-- Documentos Bloqueados (Check Out)-->
		  		        <table  width="100%" border="0" cellspacing="1" cellpadding="1" class="borderWindowPrincipal">
              		        <tr>
                                <td class="td_titulo_C" colspan="2"><%=rb.getString("doc.Ver")%></td>
                                <td class="td_titulo_C" width="50%"><%=rb.getString("doc.name")%></td>
                                <td class="td_titulo_C" width="36%"><%=rb.getString("doc.dateCheckOut")%></td>
              		        </tr>
		  		        	<tbody id="docCheckOuts">
                              <logic:present name="docCheckOuts" scope="session" >
                               	<%cont=0;%>
                                <logic:iterate id="docCheck" name="docCheckOuts" type="com.desige.webDocuments.document.forms.DocumentsCheckOutsBean" scope="session" >
                                	<%if(++cont>MAXIMO) break;%>
                                    <tr >
                                        <td>
                                            <img src="img/chequeado.gif">
                                        </td>
                                        <td class="txt_b">
                                            <%=docCheck.getMayorVer()+"."+docCheck.getMinorVer()%>
                                        </td>
                                        <td class="txt_b">
                                            <a href="javascript:showDocument('<%=docCheck.getIdDocument()%>')" class="ahref_b">
                                                <%=docCheck.getNameDocument()+" " + docCheck.getPrefix()+docCheck.getNumber()%>
                                            </a>
                                        </td>
                                        <td class="txt_b">
                                            <%=docCheck.getDateCheckOut()%>
                                        </td>
                                    </tr>
                                </logic:iterate>
                            </logic:present>
		  		        	</tbody>                            
            	        </table>
			        </td>
                    <td class="tablaLeftWindow" width="1%">&nbsp;</td>
                </tr>
                <tr id="docCheckOutsVer">
                    <td  width="99%" align="right">
                    	<table cellpadding="0" cellspacing="0" width="98%" >
                    		<tr>
                    			<td class="tablaLineaWindow" align="right">
			                    	<a onclick="llenarCuadrante('docCheckOuts')" style="cursor:pointer">
			                    		<font face="Tahoma" size="1"><b>Ver todos&nbsp;</b></font>
			                    	</a>
                    			</td>
                    			<td width="3%">
			                    	&nbsp;
                    			</td>
                    		</tr>
                    	</table>
                    </td>
                    <td class="tablaLeftWindow" width="1%">&nbsp;</td>
                </tr>
                <tr>
                    <td class="tablaBottomWindow" width="99%">&nbsp;</td>
                    <td class="tablaCorner4Window" width="1%">&nbsp;</td>
                </tr>
                <tr>
                    <td>
                        <!-- Documentos Vencidos -->
                        <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                            <tbody>
                                <tr>
                                    <td class="td_title_bc" height="15">
                                        <%=rb.getString("doc.docVenc")%>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
			            <table width="100%" border="0" cellspacing="1" class="borderWindowPrincipal" cellspacing="1">
                            <tr>
                                <td class="td_titulo_C" colspan="2"><%=rb.getString("doc.Ver")%></td>
                                <td class="td_titulo_C" width="50%"><%=rb.getString("doc.name")%></td>
                                <td class="td_titulo_C" width="36%"><%=rb.getString("doc.dateDocExp")%></td>
                            </tr>
                            <logic:present name="docExpires" scope="session" >
                                <logic:iterate id="docExp" name="docExpires" type="com.desige.webDocuments.document.forms.DocumentsCheckOutsBean" scope="session" >
                                    <tr>
                                        <td>
                                            <img src="img/docVenc.gif">
                                        </td>
                                        <td class="txt_b">
                                            <%=docExp.getMayorVer()+"."+docExp.getMinorVer()%>
                                        </td>
                                        <td class="txt_b">
                                            <a href="javascript:showDocument('<%=docExp.getIdDocument()%>')" class="ahref_b">
                                                <%=docExp.getNameDocument()+" "+docExp.getPrefix()+docExp.getNumber()%>
                                            </a>
                                        </td>
                                        <td class="txt_b">
                                            <%=docExp.getDateCheckOut()%>
                                        </td>
                                    </tr>
                                </logic:iterate>
                            </logic:present>
                        </table>
		            </td>
                    <td class="tablaLeftWindow" width="1%">&nbsp;</td>
                </tr>
                <tr>
                    <td  width="99%" align="right">
                    	<table cellpadding="0" cellspacing="0" width="98%" >
                    		<tr>
                    			<td class="tablaLineaWindow" align="right">
			                    	<font face="Tahoma" size="1"><b>Ver todos&nbsp;</b></font>
                    			</td>
                    			<td width="3%">
			                    	&nbsp;
                    			</td>
                    		</tr>
                    	</table>
                    </td>
                    <td class="tablaLeftWindow" width="1%">&nbsp;</td>
                </tr>
                <tr>
                    <td class="tablaBottomWindow" width="99%">&nbsp;</td>
                    <td class="tablaCorner4Window" width="1%">&nbsp;</td>
                </tr>
            </table>
	</td>
	<td width="*" valign="top">
		<table width="100%" border="0" cellSpacing="0" cellPadding="0">
                <tr>
                    <td>
                        <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                            <tbody>
                                <tr>
                                    <td class="td_title_bc" height="15">
                                        <%=rb.getString("doc.workFlowReq")%>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                        <!-- Flujos Pendientes -->
                        <table width="100%" border="0" cellspacing="1" class="borderWindowPrincipal">
                            <tr>
                                <td class="td_titulo_C" colspan="2" width="18%">
                                    <%=rb.getString("doc.Ver")%>
                                </td>
                                <td class="td_titulo_C" width="50%">
                                    <%=rb.getString("doc.name")%>
                                </td>
                                <td class="td_titulo_C" width="36%">
                                    <%=rb.getString("doc.type")%>
                                </td>
                                <!--<td class="td_titulo_C" width="35%">
                                    <%=rb.getString("doc.dateWFExp")%>
                                </td>-->
                            </tr>
		  		        	<tbody id="wfPendings">
                            <logic:present name="wfPendings" scope="session">
                               	<%cont=0;%>
                                <logic:iterate id="wfReq" name="wfPendings" type="com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm" scope="session" >
                                	<%if(++cont>MAXIMO) break;%>
                                    <tr>
                                        <td class="txt_b" >
                                            <logic:equal name="wfReq" property="typeDOC" value="<%=tipoImpresion%>">
                                                   <img src="icons/printer.png" border="0">
                                            </logic:equal>
                                            <logic:notEqual name="wfReq" property="typeDOC" value="<%=tipoImpresion%>">
                                                    <img src="img/pendings.gif"  border="0">
                                            </logic:notEqual>
										</td>
                                        <td class="txt_b" >
                                                <%=wfReq.getIdVersion()%>
										</td>
                                        <td>
                                            <a href="javascript:showWF('<%=wfReq.getIdWorkFlow()%>','<%=wfReq.getRow()%>',<%=wfReq.isOwner()%>,<%=wfReq.isFlexFlow()%>)" class="ahref_b">
                                                 <logic:notEqual name="wfReq" property="typeDOC" value="<%=tipoImpresion%>">
                                                    <%=wfReq.getNameDocument()+" " +wfReq.getPrefix()+wfReq.getNumber()%>
                                                 </logic:notEqual>
                                                 <logic:equal name="wfReq" property="typeDOC" value="<%=tipoImpresion%>">
                                                   <%=wfReq.getNameDocument()%>
                                                </logic:equal>
                                            </a>
                                        </td>
                                        <td class="txt_b">
                                            <%=wfReq.getNameWorkFlow()%>
<%--                                            <%=rb.getString("wf.type"+wfReq.getTypeWF())%>--%>
                                        </td>
                                     <!--   <td class="txt_b">
                                            <%=wfReq.getDateExpire()%>
                                        </td>-->
                                    </tr>
                                </logic:iterate>
                            </logic:present>
		  		        	</tbody>                            
                        </table>
                    </td>
                    <td class="tablaLeftWindow" width="1%">&nbsp;</td>
                </tr>
                <tr id="wfPendingsVer">
                    <td  width="99%" align="right">
                    	<table cellpadding="0" cellspacing="0" width="98%" >
                    		<tr>
                    			<td class="tablaLineaWindow" align="right">
			                    	<a onclick="llenarCuadrante('wfPendings')" style="cursor:pointer">
			                    		<font face="Tahoma" size="1"><b>Ver todos&nbsp;</b></font>
			                    	</a>
                    			</td>
                    			<td width="3%">
			                    	&nbsp;
                    			</td>
                    		</tr>
                    	</table>
                    </td>
                    <td class="tablaLeftWindow" width="1%">&nbsp;</td>
                </tr>
                <tr>
                    <td class="tablaBottomWindow" width="99%">&nbsp;</td>
                    <td class="tablaCorner4Window" width="1%">&nbsp;</td>
                </tr>
                <tr>
                    <td>
                        <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                            <tbody>
                                <tr>
                                    <td class="td_title_bc" height="15">
                                        <%=rb.getString("doc.workFlowExpired")%>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                        <!-- Flujos Vencidos -->
                        <table width="100%" border="0" cellspacing="1" class="borderWindowPrincipal">
                            <tr>
                                <td class="td_titulo_C" width="18%">
                                    <%=rb.getString("doc.Ver")%>
                                </td>
                                <td class="td_titulo_C" width="35%">
                                    <%=rb.getString("doc.name")%>
                                </td>
                                <td class="td_titulo_C" width="12%">
                                    <%=rb.getString("doc.type")%>
                                </td>
                                <td class="td_titulo_C" width="35%">
                                    <%=rb.getString("doc.dateWFExpired")%>
                                </td>
                            </tr>
                            <logic:present name="wfExpires" scope="session" >
                                <logic:iterate id="wfReq" name="wfExpires" type="com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm" scope="session" >
                                    <tr>
                                        <td>
                                            <img src="img/pendings.gif">
                                            <%=wfReq.getIdVersion()%>
                                        </td>
                                        <td>
                                            <a href="javascript:showWF('<%=wfReq.getIdWorkFlow()%>','<%=wfReq.getRow()%>',<%=wfReq.isOwner()%>,<%=wfReq.isFlexFlow()%>)" class="ahref_b">
                                                <%=wfReq.getNameDocument()+" " +wfReq.getPrefix()+wfReq.getNumber()%>
                                            </a>
                                        </td>
                                        <td class="txt_b">
                                            <%=wfReq.getNameWorkFlow()%>
                                        </td>
                                        <td class="txt_b">
                                            <%=wfReq.getDateExpire()%>
                                        </td>
                                    </tr>
                                </logic:iterate>
                            </logic:present>
                        </table>
                     
                    </td>
                    <td class="tablaLeftWindow" width="1%">&nbsp;</td>
                </tr>
                <tr>
                    <td  width="99%" align="right">
                    	<table cellpadding="0" cellspacing="0" width="98%" >
                    		<tr>
                    			<td class="tablaLineaWindow" align="right">
			                    	<font face="Tahoma" size="1"><b>Ver todos&nbsp;</b></font>
                    			</td>
                    			<td width="3%">
			                    	&nbsp;
                    			</td>
                    		</tr>
                    	</table>
                    </td>
                    <td class="tablaLeftWindow" width="1%">&nbsp;</td>
                </tr>

                <tr>
                    <td class="tablaBottomWindow" width="99%">&nbsp;</td>
                    <td class="tablaCorner4Window" width="1%">&nbsp;</td>
                </tr>

                <!-- Flujos EXPIRADOS al originador, 16/07/2008-->
                <tr>
                    <td>
                        <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                            <tbody>
                                <tr>
                                    <td class="td_title_bc" height="15">
                                        <%=rb.getString("doc.workFlowExpiredOwner")%>
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table width="100%" border="0" cellspacing="1" class="borderWindowPrincipal">
                            <tr>
                                <td class="td_titulo_C" width="18%">
                                    <%=rb.getString("doc.Ver")%>
                                </td>
                                <td class="td_titulo_C" width="35%">
                                    <%=rb.getString("doc.name")%>
                                </td>
                                <td class="td_titulo_C" width="12%">
                                    <%=rb.getString("doc.type")%>
                                </td>
                                <td class="td_titulo_C" width="35%">
                                    <%=rb.getString("doc.dateWFExpired")%>
                                </td>
                            </tr>
                            <logic:present name="wfExpiresOwner" scope="session" >
                                    <logic:iterate id="wfReq" name="wfExpiresOwner" type="com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm" scope="session" >
                                    <tr>
                                        <td>
                                            <img src="img/pendings.gif">
                                            <%=wfReq.getIdVersion()%>
                                        </td>
                                        <td>
                                            <a href="javascript:showWF('<%=wfReq.getIdWorkFlow()%>','<%=wfReq.getRow()%>',<%=wfReq.isOwner()%>,<%=wfReq.isFlexFlow()%>)" class="ahref_b">
                                                <%=wfReq.getNameDocument()+" " +wfReq.getPrefix()+wfReq.getNumber()%>
                                            </a>
                                        </td>
                                        <td class="txt_b">
                                            <%=wfReq.getNameWorkFlow()%>
                                        </td>
                                        <td class="txt_b">
                                            <%=wfReq.getDateExpire()%>
                                        </td>
                                    </tr>
                                    </logic:iterate>
                            </logic:present>
                        </table>

                    </td>
                    <td class="tablaLeftWindow" width="1%">&nbsp;</td>
                </tr>
                <tr>
                    <td  width="99%" align="right">
                    	<table cellpadding="0" cellspacing="0" width="98%" >
                    		<tr>
                    			<td class="tablaLineaWindow" align="right">
			                    	<font face="Tahoma" size="1"><b>Ver todos&nbsp;</b></font>
                    			</td>
                    			<td width="3%">
			                    	&nbsp;
                    			</td>
                    		</tr>
                    	</table>
                    </td>
                    <td class="tablaLeftWindow" width="1%">&nbsp;</td>
                </tr>
                <tr>
                    <td class="tablaBottomWindow" width="99%">&nbsp;</td>
                    <td class="tablaCorner4Window" width="1%">&nbsp;</td>
                </tr>
                

                <!-- Flujos CANCELADOS  Luis Cisneros, 01/03/2007-->
                <tr>
                    <td>
                        <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                            <tbody>
                                <tr>
                                    <td class="td_title_bc" height="15">
                                        <%=rb.getString("doc.worFlowCanceled")%>
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <table width="100%" border="0" cellspacing="1" class="borderWindowPrincipal">
                            <tr>
                                <td class="td_titulo_C" width="18%">
                                    <%=rb.getString("doc.Ver")%>
                                </td>
                                <td class="td_titulo_C" width="35%">
                                    <%=rb.getString("doc.name")%>
                                </td>
                                <td class="td_titulo_C" width="12%">
                                    <%=rb.getString("doc.type")%>
                                </td>
                                <td class="td_titulo_C" width="35%">
                                    <%=rb.getString("doc.dateWFCanceled")%>
                                </td>
                            </tr>
                            <logic:present name="wfCanceled" scope="session" >
                                    <logic:iterate id="wfReq" name="wfCanceled" type="com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm" scope="session" >
                                           <tr>
                                        <td>
                                            <img src="img/pendings.gif">
                                            <%=wfReq.getIdVersion()%>
                                        </td>
                                        <td>
                                            <a href="javascript:showWF('<%=wfReq.getIdWorkFlow()%>','<%=wfReq.getRow()%>',<%=wfReq.isOwner()%>,<%=wfReq.isFlexFlow()%>)" class="ahref_b">
                                                <%=wfReq.getNameDocument()+" " +wfReq.getPrefix()+wfReq.getNumber()%>
                                            </a>
                                        </td>
                                        <td class="txt_b">
                                            <%=rb.getString("wf.type"+wfReq.getTypeWF())%>
                                        </td>
                                        <td class="txt_b">
                                            <%=wfReq.getDateCompleted()%>
                                        </td>
                                    </tr>
                                    </logic:iterate>
                            </logic:present>
                        </table>

                    </td>
                    <td class="tablaLeftWindow" width="1%">&nbsp;</td>
                </tr>
                <tr>
                    <td  width="99%" align="right">
                    	<table cellpadding="0" cellspacing="0" width="98%" >
                    		<tr>
                    			<td class="tablaLineaWindow" align="right">
			                    	<font face="Tahoma" size="1"><b>Ver todos&nbsp;</b></font>
                    			</td>
                    			<td width="3%">
			                    	&nbsp;
                    			</td>
                    		</tr>
                    	</table>
                    </td>
                    <td class="tablaLeftWindow" width="1%">&nbsp;</td>
                </tr>
                <tr>
                    <td class="tablaBottomWindow" width="99%">&nbsp;</td>
                    <td class="tablaCorner4Window" width="1%">&nbsp;</td>
                </tr>
            </table>
	</td>
  </tr>
 </table>
</body>
</html>
