<!--
 * Title: reasigneUserFlexFlow.jsp <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simón Rodriguez.(SR)
  * @version WebDocuments v4.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 14/05/2005 (NC) Se eliminaron líneas de código comentadas. </li>
 *      <li> 03/08/2005 (SR) Se muestra la version del documento. </li>
 </ul>
-->
<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.SuperActionForm,
                 java.util.Hashtable,
                 com.desige.webDocuments.structured.forms.BaseStructForm,
                 com.desige.webDocuments.persistent.managers.HandlerWorkFlows"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ page language="java" %>
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String onLoad = ToolsHTML.getMensajePages(request,rb);
    if (onLoad==null){
        response.sendRedirect(rb.getString("href.logout"));
    }
    String cmd = (String)session.getAttribute("cmd");
    if (cmd==null){
        cmd = SuperActionForm.cmdLoad;
    }
    String nodeActive = (String)session.getAttribute("nodeActive");
    pageContext.setAttribute("cmd",cmd);

   	String idDocument = (String)session.getAttribute("idDocumentReject");
	String idWorkFlow = (String)session.getAttribute("idWorkFlowReject");
 	String numVersion = (String)session.getAttribute("numVersionReject");
  	String idFlexFlow = (String)session.getAttribute("idFlexFlowReject");
   	String comments = (String)session.getAttribute("commentsReject");
	String result = (String)session.getAttribute("resultReject");
   	String statu = (String)session.getAttribute("statuReject");
  	String idMovement = (String)session.getAttribute("idMovementReject");
   	String row =  (String)session.getAttribute("rowReject");
%>
<html>
<head>
<title><%=rb.getString("principal.title")%></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">

<script language="JavaScript">
	function cerrar(){
		//alert(window.parent.document.location);
		//window.parent.location
		//window.parent.document.location="/loadWF.do?isFlexFlow=true";
		//window.parent.opener.location.reload();
		window.parent.opener.location.href = window.parent.opener.location.href;
		window.close();
	}
	
    function aceptar(forma) {
    	if (validateRadio(forma)){
	    	//Si Se selecciono la opcion de devolver al generador entonces se sigue el curso normal
	    	if(forma.idAct.value=="0"){
	    		reinicioFTP(forma);
	    	}else{
	    	 	if (forma.btnOK) {
            		forma.btnOK.disabled = true;
        		}
	    		forma.action="reasigneFlexFlow.do";
		        forma.target = "_parent";
		        forma.submit();
				//window.close();        
			}
		}
    }
    
    function reinicioFTP(forma) {
        //forma.action="responseFlexFlow.do";
        //forma.target = "_parent";
        //forma.submit();
		//window.close();
    }

    function showCharge(userName,charge) {
	    charge = charge.replace(/\[/gi,"-").replace(/\]/gi,"-");
        window.open("showCharge.jsp?userName="+userName+"&charge="+charge, "WebDocuments", "resizable=yes,scrollbars=yes,statusbar=yes,width=300,height=200,left=410,top=250");
    }

   function validateRadio(forma) {
        for (var i=1; i < forma.length;i++) {
            if (forma.elements[i].checked){
                valor = forma.elements[i].value;
                indice = valor.indexOf(",");
                forma.typeReasigne.value = valor.substring(0,indice);
                valor = valor.substring(indice+1,valor.length);
                indice = valor.indexOf(",");
                forma.idAct.value = valor.substring(0,indice);
                forma.idUser.value = valor.substring(indice+1,valor.length);
                return true;
            }
        }
        if(forma.idAct.value==""){
        	alert("<%=rb.getString("msg.selectOption")%>");
        	return false;
        }
    }
    
</script>
</head>

<body class="bodyInternas" <%=onLoad%>>
<logic:present name="recargar" scope="session">
	<logic:equal name="recargar" value="1">
	<script language="JavaScript">
		cerrar();
	</script>	
	</logic:equal>
</logic:present>

<logic:present name="flujosReject" scope="session">
<html:form action="reasigneFlexFlow.do" >
	<html:hidden property="result" value="<%=result%>"/>
	<html:hidden property="row" value="<%=row%>"/>
	<html:hidden property="statu" value="<%=statu%>"/>
	<html:hidden property="idMovement" value="<%=idMovement%>"/>
	<html:hidden property="numVersion" value="<%=numVersion%>"/>
	<html:hidden property="idDocument" value="<%=idDocument%>"/>
	<html:hidden property="idWorkFlow" value="<%=idWorkFlow%>"/>
	<html:hidden property="idFlexFlow" value="<%=idFlexFlow%>"/>
	<html:hidden property="isFlexFlow" value="true"/>
	<html:textarea property="commentsUser" style="display:none" value="<%=comments%>"/>
	<html:hidden property="idUser" value=""/>
	<html:hidden property="typeReasigne" value=""/>
	<html:hidden property="idAct" value=""/>
	
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr class="clsTableTitle">
		<td width="100%" class="td_title_bc">
			<%=rb.getString("wf.titleReasigneUserActivity")%>
		</td>
	</tr>
	<tr>
		<td class="td_gris_l">
			<div align="center">
			<br>
			<%=rb.getString("wf.reasigneUserActivity")%>
			</div>
		</td>
	</tr>
	<tr>
		<td height="10"></td>
	</tr>	
	<!--<tr>
		<td class="td_gris_l">
			<input name="selection" type="radio" value="0,0,0"/><%=rb.getString("wf.reasigneUserOwner")%>
		</td>
	</tr>
	<tr>
		<td height="10"></td>
	</tr>-->
	</table>	
	
    <logic:iterate id="doc" name="flujosReject" scope="session" indexId="indice" type="com.desige.webDocuments.workFlows.forms.DataWorkFlowForm" >
        <table align=center border=0 width="100%">
            <tr>
                <td valign="top" colspan="4">
                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                        <tbody>
                            <tr>
                            	<%
                            	boolean sec = false;
                            	if (doc.getSequential()!=null && doc.getSequential().equals("1")) {
                            		if(doc.getIdWorkFlow()!=null && idWorkFlow.compareTo(doc.getIdWorkFlow())>=0 
                            		&& doc.getStatu()!=null && (HandlerWorkFlows.wfuPending.equals(doc.getStatu()) || HandlerWorkFlows.wfuAcepted.equals(doc.getStatu()) )){
                            	%>
                                <td height="22" width="10" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat" valign="middle">
                                    <input name="selection" type="radio" value="1,<%=doc.getIdWorkFlow()%>,0"/>
                                </td>
                            	<%
                            		}
                            	}else{
                            		sec = true;
                            	}
                            	%>
                                <td height="22" class="titleCenter" style="background: url(img/BgComments.gif); background-repeat: no-repeat" valign="middle">
                                    <%=doc.getNameWF()%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            
            <tr>
                <td valign="top" colspan="4" >
                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                        <tr>
                            <td width="5" class="td_title_bc"></td>
                            <td width="35%" class="td_title_bc">
                                <%=rb.getString("wf.participantion")%>
                            </td>
                            <td width="18%" class="td_title_bc">
                                <%=rb.getString("wf.statu")%>
                            </td>
                            <td class="td_title_bc" width="18%">
                                <%=rb.getString("wf.result")%>
                            </td>
                            <td class="td_title_bc" width="29%">
                                <%=rb.getString("wf.dateDoc")%>
                            </td>
                        </tr>
                    </table>

                        <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">

                            <logic:iterate id="participation" name="doc" property="users" type="com.desige.webDocuments.workFlows.forms.ParticipationForm">
                                <tr>
                                	<td width="5" class="td_title_bc">
                                		<%if (sec && doc.getIdWorkFlow()!=null && idWorkFlow.compareTo(doc.getIdWorkFlow())>=0 && HandlerWorkFlows.wfuAcepted.equals(participation.getStatu()+"")){%>
                                		<input name="selection" type="radio" value="2,<%=doc.getIdWorkFlow()%>,<%=participation.getRow()%>"/>
                                		<%}%>
                                	</td>
                                    <td class="td_gris_l" width="35%">
                                        <logic:present name="showCharge" scope="session">
                                            <a href="javascript:showCharge('<%=participation.getNameUser()%>','<%=participation.getCharge()%>')" class="ahref_b">
                                                <bean:write name="participation" property="charge" />
                                            </a>
                                        </logic:present>
                                        <logic:notPresent name="showCharge" scope="session">
                                            <a href="javascript:showCharge('<%=participation.getNameUser()%>','<%=participation.getCharge()%>')" class="ahref_b">
                                                <bean:write name="participation" property="nameUser" />
                                            </a>
                                        </logic:notPresent>
                                    </td>
                                    <td class="td_gris_l" width="18%">
                                        <p align="center">
                                            <%=rb.getString("wf.statuUserWF"+participation.getStatu())%>
                                        </p>
                                    </td>
                                    <td class="td_gris_l" width="18%">
                                        <p align="center">
                                            <%=rb.getString("wf.resultUserWF"+participation.getResult())%>
                                        </p>
                                    </td>
                                    <td class="td_gris_l" width="29%">
                                        <bean:write name="participation" property="dateReply" />
                                    </td>
                                </tr>
                            </logic:iterate>                           
                        </table>
                </td>
            </tr>
            
            <tr>
                <td colspan="4">
                    &nbsp;
                </td>
            </tr>
        </table>
    </logic:iterate>
	
	<table align=center border=0 width="100%">
    	<tr>
            <td>
                &nbsp;    
            </td>
        </tr>
        <tr>
            <td>
                <center>
                    <input type="button" class="boton" value="<%=rb.getString("btn.ok")%>" class="boton" name="btnOK" onClick="javascript:aceptar(this.form);"/>
                </center>
            </td>
        </tr>
    </table>
    
</html:form>
</logic:present>
<logic:notPresent name="flujosReject">
	<div class="td_gris_l">
	<div align="center">
	<br><br>
	<%=rb.getString("err.notActivities")%>
	<br><br><br>
	<input type="button" class="boton" value="<%=rb.getString("btn.close")%>" name="btnClose" onClick="javascript:window.close();"/>
    </div></div>
</logic:notPresent> 

</body>
</html>
