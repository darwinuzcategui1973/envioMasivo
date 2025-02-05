<%@ page import="java.util.ResourceBundle,
                 com.desige.webDocuments.utils.ToolsHTML,
                 com.desige.webDocuments.utils.beans.Users"%>
<%@ page import="com.desige.webDocuments.utils.beans.PaginPage"%>
<%@ page import="com.desige.webDocuments.utils.beans.SuperActionForm"%>
<%@ page import="java.util.Hashtable"%>
<%@ page import="com.desige.webDocuments.document.actions.loadsolicitudImpresion"%>
<%@ page import="java.util.Enumeration"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ page language="java" %>
<!--/**
 * Title: showSolicitudImpresion.jsp <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
  * @author Ing. Simon Rodr�guez (SR)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 * </ul>
 */-->
<%
    ResourceBundle rb = ToolsHTML.getBundle(request);
    String info=(String) request.getSession().getAttribute("info");;
    if (info==null){
    	info=(String) request.getAttribute("info");
    }
    ////System.out.println(info+"=info.........ahora si....salio 2007-05-23..............");
    
    
    String onLoad = ToolsHTML.getMensajePages(request,rb);
  

    //System.out.println("info="+info);
    //System.out.println(onLoad);
    //System.out.println("...........................");
//    String idDoc = request.getParameter("idDocument")!=null?request.getParameter("idDocument"):"vacio";


    String comentariosImp = request.getParameter("comentariosImp")!=null?request.getParameter("comentariosImp"):"";
    String docRelations = request.getParameter("docRelations")!=null?request.getParameter("docRelations"):"";
    String copiasRelations = request.getParameter("copiasRelations")!=null?request.getParameter("copiasRelations"):"";
    Hashtable existeCopias = loadsolicitudImpresion.copiasControladas(docRelations,copiasRelations);
    //copiasControlada.containsKey()
    
    String from = null;
    String size = null;
    Users users = null;
    PaginPage Pagingbean = null;
%>
<html>
<head>
<title></title>
<jsp:include page="meta.jsp" /> 
<link href="estilo/estilo.css" rel="stylesheet" type="text/css">
<script language=javascript src="./estilo/funciones.js"></script>
<script language="JavaScript">
   <logic:present name="cerrarventana" scope="request">
       <%
       if (info!=null){%>
    	   alert('<%=info%>');
           window.close();
           //top.window.close();
     <%  }
       %>
        </logic:present>
    //TODO cerrar en Solicitud de impresion
      
               
      
    
 function contarItemsSelecteds(lista){
		if (lista){
			var items = lista.length;
			var valor;
			var cont = 0;
            var row = 0;
			for (row = 0; row < items; row++){
				var valor = lista[row];
				if (valor.selected){
					cont++;
				}
			}
			return cont;
		}
		return 0;
	}

    function numerico(valor){
             cad = valor.toString();
             if ((cad.length>0)&&(cad!="0")){
                 for (var i=0; i<cad.length; i++) {
                      var caracter = cad.charAt(i);
                      if (caracter<"0" || caracter>"9")
                          return false;
                  }
              }else{
                  return false;
              }
    return true;
    }

    function closeWin() {
        top.window.close();
    }
    
    function deleteText(objeto){
    	if(objeto.value.length>=200)
	    	objeto.value = objeto.value.substring(0,199);
    }
        
    function salvar(){
    	 
        if(document.solicitudimpresion.comentariosImp.value.length>200){
	        document.solicitudimpresion.comentariosImp.value = document.solicitudimpresion.comentariosImp.value.substring(0,200);
        }
        document.solicitudimpresion.mensaje.value = document.solicitudimpresion.comentariosImp.value;
            
     if (estaControlado()){
              if (validateCheck()){
                botones("false");
                document.solicitudimpresion.action="loadsolicitudImpresion.do";
                document.solicitudimpresion.submit();
                //top.window.close();
                cmd="";
                return true;
              }else{
                      alert('<%=rb.getString("imp.selecelemlista")%>');
                      return false;
              }
      }else{
            botones("false");
            document.solicitudimpresion.controlada.value="no";
            document.solicitudimpresion.action="loadsolicitudImpresion.do";
            document.solicitudimpresion.submit();
            //top.window.close();
             cmd="";
            return true;
      }
    }

    
    function botones(condicion) {
         if (condicion=="false"){
         	document.getElementById("botones").style.display="none";
        }else if (condicion!="false"){
         	document.getElementById("botones").style.display="";
        }
    }

   function estaControlado() {
       var sw=false;
         for (var i=0; i < document.solicitudimpresion.length;i++) {
             var  elemento=document.solicitudimpresion.elements[i];
                 if (elemento.type == "checkbox"){
                    if (document.solicitudimpresion.elements[i].disabled){
                        i +=document.solicitudimpresion.length;
                        sw=false;
                    }else{
                        i +=document.solicitudimpresion.length;
                        sw=true;
                    }
                 }
          }
       return sw;
   }


    function deshabilitar(condicion) {
            for (var i=0; i < document.solicitudimpresion.length;i++) {
                var  elemento=document.solicitudimpresion.elements[i];
                if (condicion==true){
                    if (elemento.type == "checkbox"){
                       document.solicitudimpresion.elements[i].disabled = true;
                    }
                    if (elemento.type == "text"){
                        document.solicitudimpresion.elements[i].disabled = true;
                    }
                }else if (condicion==false){
                    if (elemento.type == "checkbox"){
                       document.solicitudimpresion.elements[i].disabled = false;
                    }
                    if (elemento.type == "text"){
                        document.solicitudimpresion.elements[i].disabled =false;
                    }
                }
             }
    }
    function disparandoSeleccion(valo){
    var valor=valo;
            if (valor==1){
                deshabilitar(true);
             }else{
               deshabilitar(false);
            }
    }
  function validateCheck() {
        var set = false;
        var valueSelect = false;
        seleccionados= document.solicitudimpresion.docRelations.value;
        copias=document.solicitudimpresion.copiasRelations.value;
        //procesamos  checkbox
        var usersSelected='usersSelected';
        var cop='copias';
        for (var i=0; i < document.solicitudimpresion.length;i++) {
             msg1='';
             var checkbox=document.solicitudimpresion.elements[i];
             if (document.solicitudimpresion.elements[i].checked){
                if (document.solicitudimpresion.elements[i].value!='0'){
                    msg1 = document.solicitudimpresion.elements[i].value;
                    var numero=checkbox.name.substring(checkbox.name.length,checkbox.name.length-(checkbox.name.length-usersSelected.length));
                    //buscamos el  texto
                   for (var j=0; j < document.solicitudimpresion.length;j++) {
                         msg2='';
                         var nomcopia=document.solicitudimpresion.elements[j];
                         var numcopia=nomcopia.name.substring(nomcopia.name.length,nomcopia.name.length-(nomcopia.name.length-cop.length));
                          if (document.solicitudimpresion.elements[j].type=="text"){
                           if (numcopia==numero){
                             //j=document.solicitudimpresion.length+1;
                                if (document.solicitudimpresion.elements[j].value!='0'){
                                         if (numerico(document.solicitudimpresion.elements[j].value)){
                                                      msg2 =document.solicitudimpresion.elements[j].value;
                                                      if ((msg2!='')||(document.solicitudimpresion.copiasRelations.value.length > 0)) {
                                                            copias+= "," + msg2;
                                                            seleccionados+= "," + msg1;
                                                            valueSelect=true;
                                                        }
                                          }else{

                                             alert('<%=rb.getString("imp.numcopiarazonable")%>');
                                         }

                                     }

                                  }
                             }
                       }
                   }
              }
           }
	       if (valueSelect){
	       	   var selec = seleccionados.split(",");
	       	   var copia = copias.split(",");
	       	   
	       	   var sel2 = new Array();
	       	   var cop2 = new Array();
	       	   var found = -1;
	       	   for(var x=0;x<selec.length;x++) {
				   found = -1;
				   //alert("sel2.length="+sel2.length+" -- "+sel2);
		       	   for(var k=0;k<sel2.length;k++) {
		       	   	  //alert(selec[x]+"=="+sel2[k]);
	       	   		  if(selec[x]==sel2[k]){
	       	   		  	found = k;
	       	   		  	break;
	       	   		  }
	       	   	   }
	       	   	   if((typeof selec[x]!='undefined') && selec[x]!='') {
	       	   	   	  var ind = found<0?sel2.length:found;
	       	   	      sel2[ind] = selec[x];
	       	   	      cop2[ind] = copia[x];
	       	   	   }
	       	   }
	       	   seleccionados = sel2.join(",");
	       	   copias = cop2.join(",");

	           document.solicitudimpresion.docRelations.value=seleccionados;
	           document.solicitudimpresion.copiasRelations.value=copias;
	             
	           document.findForm.docRelations.value=seleccionados;
	           document.findForm.copiasRelations.value=copias;
	           
	           document.formPagingPage.docRelations.value=seleccionados;
	           document.formPagingPage.copiasRelations.value=copias;
           }
           return valueSelect;
    }

   function desmarcarCheck(copia1,checkBox1) {
       seleccionados=document.solicitudimpresion.docRelations.value;
       copias=document.solicitudimpresion.copiasRelations.value;
  	   var selec = seleccionados.split(",");
   	   var copia = copias.split(",");
   	   if(seleccionados.length==0){
   	      selec = new Array();
   	      copia = new Array();
   	   }
    	   
   	   var found = -1;
   	   for(var x=0;x<selec.length;x++) {
		   found = -1;
  	   	   if(selec[x]==checkBox1.value){
      		  found = x;
      	   }
       	   if(found>=0) {
       	      selec.splice(found,1);
       	      copia.splice(found,1);
       	      break;
       	   }
       }
       	   
   	   if(checkBox1.checked) {
   	      selec[selec.length] = checkBox1.value;

		  var cp = 1;   	      
   	      try {
   	      	cp = parseInt(copia1.value); 
   	      	cp = (cp>0?cp:1);
   	      }catch(e){
   	      	cp = 1;
   	      }
		  copia1.value = cp;
   	      copia[copia.length] = copia1.value;
   	   } else {
		  copia1.value = 0;
   	   }
    	   
  	   seleccionados = selec.join(",");
   	   copias = copia.join(",");
    
       document.solicitudimpresion.docRelations.value=seleccionados;
       document.solicitudimpresion.copiasRelations.value=copias;
             
       document.findForm.docRelations.value=seleccionados;
       document.findForm.copiasRelations.value=copias;
           
       document.formPagingPage.docRelations.value=seleccionados;
       document.formPagingPage.copiasRelations.value=copias;

  }
  
  
  function paging_OnClick(pageFrom){
        document.formPagingPage.from.value = pageFrom;
        validateCheck();
        document.formPagingPage.docRelations.value = document.solicitudimpresion.docRelations.value;
        document.formPagingPage.copiasRelations.value = document.solicitudimpresion.copiasRelations.value;
        document.formPagingPage.namePropietario.value = document.solicitudimpresion.namePropietario.value;
        document.formPagingPage.nameResponsable.value = document.solicitudimpresion.nameResponsable.value;
        document.formPagingPage.idDocument.value = document.solicitudimpresion.idDocument.value;
        document.formPagingPage.idVersion.value = document.solicitudimpresion.idVersion.value;
        document.formPagingPage.controlada.value = document.solicitudimpresion.controlada.value;
        document.formPagingPage.cmd.value = document.solicitudimpresion.cmd.value;
        document.formPagingPage.solicitante.value = document.solicitudimpresion.solicitante.value;
    	document.formPagingPage.comentariosImp.value = document.solicitudimpresion.comentariosImp.value;
        document.formPagingPage.submit();
    }

 function find_OnClick(){ 
        validateCheck();
        findForm.docRelations.value = document.solicitudimpresion.docRelations.value;
        findForm.copiasRelations.value = document.solicitudimpresion.copiasRelations.value;
        findForm.namePropietario.value = document.solicitudimpresion.namePropietario.value;
        findForm.nameResponsable.value = document.solicitudimpresion.nameResponsable.value;
        findForm.idDocument.value = document.solicitudimpresion.idDocument.value;
        findForm.idVersion.value = document.solicitudimpresion.idVersion.value;
        findForm.controlada.value = document.solicitudimpresion.controlada.value;
        findForm.cmd.value = document.solicitudimpresion.cmd.value;
        findForm.solicitante.value = document.solicitudimpresion.solicitante.value;
        findForm.submit();
    }

	function mostrarObj(layer){
		if(layer.style.display!='')
			layer.style.display = '';
	}
	
	function ocultarObj(layer){
		if(layer.style.display!='none')
			layer.style.display = 'none';
	}
   
    function toggleDiv(gn)  {
		//var g = document.all(gn);
		var g = document.getElementById(gn);
		
		if( g.style.display == "none")
			g.style.display = "";
		else
			g.style.display = "none";
    }	
	
</script>
</head>
<logic:notPresent name="cerrarventana" scope="request">
<body class="bodyInternas" <%=onLoad%>>

<%--<form name="solicitudimpresion" action ="loadsolicitudImpresion.do">--%>
    <center>
        <table align=center border=0 width="100%">
            <tr>
                <td colspan="2">
                    <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                        <tbody>
                            <tr>
                                <td class="td_title_bc" height="21">
                                      <%=rb.getString("cbs.opcimpresion")%>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            <tr>
           </tr>
           <tr>
              <td class="td_gris_l">
                <logic:present name="datossolicitante" scope="session">
                   <bean:define id ="solicitaprint" name="datossolicitante" type="com.desige.webDocuments.users.forms.LoginUser" scope="session"/>
                    <b><%=rb.getString("imp.requeridoPor")%>:</b> <bean:write name="solicitaprint" property="cargo"/></br>
                </logic:present>
              </td>
           </tr>
           
           <tr>
                <td colspan="2">
                </td>
           </tr>
           <tr>
           <td width="40%">
           <table cellSpacing=0 cellPadding=0 align=center border=0 width="100%">
                      <tr>
                          <td colspan="4" valign="top">
                             <table class="clsTableTitle" width="100%" cellSpacing=0 cellPadding=2 align=center border=0>
                              <tbody>
                                    <tr>
                                       <td class="td_title_bc" width="45%"align="center"></td>
                                       <td width="5%">Destinatarios</td>
                                       <td width="*" align="center"></td>
                                     </tr>
                               </tbody>
                              </table>
                            </td>
                        </tr>

                      <tr>
                              <td>
                                  <form name="findForm" action ="filtroSessionUsuarios.do">
                                          <input type="hidden" name="from"  value="">
                                          <input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
                                          <input type="hidden" name="docRelations"  value="<%=docRelations%>">
                                          <input type="hidden" name="copiasRelations"  value="<%=copiasRelations%>">
                                          <input type=hidden name="namePropietario" value="<%=request.getParameter("namePropietario")%>">
                                          <input type=hidden name="nameResponsable" value="<%=request.getParameter("nameResponsable")%>">
                                          <input type=hidden name="idDocument" value="<%=request.getParameter("idDocument")!=null?request.getParameter("idDocument"):"0"%>"/>
                                          <input type=hidden name="idVersion" value="<%=request.getParameter("idVersion")!=null?request.getParameter("idVersion"):"0"%>"/>
                                          <input type=hidden name="controlada" value="si"/>
                                          <logic:present name="datossolicitante" scope="session">
                                             <bean:define id ="solicitaprint" name="datossolicitante" type="com.desige.webDocuments.users.forms.LoginUser" scope="session"/>
                                             <input type=hidden name="solicitante" value="<bean:write name="solicitaprint" property="idPerson"/>">
                                          </logic:present>
                                      
                                      <table>
                                        <tr>
                                            <logic:present name="showCharge" scope="session">
                                                <td class="titleLeft" width="36%" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                                                    <%=rb.getString("user.cargo")%>  
                                                 </td>
                                                 <td>
                                                     <input type="text" name="nombreCargo" id="nombreCargo"/>
                                                 </td>
                                            </logic:present>
                                            <logic:notPresent name="showCharge" scope="session">
                                                <td class="titleLeft" width="36%" height="26" style="background: url(img/btn120.png); background-repeat: no-repeat" valign="middle">
                                                    <%=rb.getString("user.ape")%>  
                                                 </td>
                                                 <td>
                                                     <input type="text" name="apellidoNombre" id="apellidoNombre"/>
                                                 </td>
                                            </logic:notPresent>              
                                            <td>
                                                <input class="boton" type="button" onclick="find_OnClick()" name="Buscar" value="Buscar"/>
                                            </td>
                                         </tr>
                                      </table>
                                 </form>
                              </td>
                      </tr>

                      <logic:present name="noHayUsuarios" scope="request">
                          <tr>
                              <td colspan="4" >
                                <div style="background-color:red;color:white;font-family: Arial, Helvetica, sans-serif;padding: 3px;margin-bottom: 12px;">
                                    <%=rb.getString("doc.busqfallida")%>
                                </div>
                              </td>
                          </tr>
                      </logic:present>
                      <tr>
                        <td width="40%">
                            <center>


                                <logic:present name="usuarios" scope="session">

                                 <% from = request.getParameter("from");
                                    size = (String)session.getAttribute("size");
                                    users = (Users)session.getAttribute("user");
                                    Pagingbean = ToolsHTML.getBeanPaginPage(from,Integer.parseInt(size),String.valueOf(users.getNumRecord()));
                                 %>
                    <!--             simons:< % =docRelations%>   <br>
                                 copias:< % =copiasRelations%>-->

                                 <%--<form name="selection">--%>
                                 <form name="solicitudimpresion" action ="loadsolicitudImpresion.do">
                                         <input type="hidden" name="docRelations" value="<%=docRelations%>"/>
                                         <input type="hidden" name="copiasRelations" value="<%=copiasRelations%>"/>
                                         <input type=hidden name="namePropietario" value="<%=request.getParameter("namePropietario")%>">
                                         <input type=hidden name="nameResponsable" value="<%=request.getParameter("nameResponsable")%>">
                                         <input type=hidden name="idDocument" value="<%=request.getParameter("idDocument")!=null?request.getParameter("idDocument"):"0"%>"/>
                                         <input type=hidden name="idVersion" value="<%=request.getParameter("idVersion")!=null?request.getParameter("idVersion"):"0"%>"/>
                                         <input type=hidden name="controlada" value="si"/>
                                         <input type=hidden name="cmd" value="<%=SuperActionForm.cmdLoad%>"/>
                                         <input type="hidden" name="mensaje" style="display:none" value="">
                                         <logic:present name="datossolicitante" scope="session">
                                             <bean:define id ="solicitaprint" name="datossolicitante" type="com.desige.webDocuments.users.forms.LoginUser" scope="session"/>
                                             <input type=hidden name="solicitante" value="<bean:write name="solicitaprint" property="idPerson"/>">
                                         </logic:present>

           		
							           <tr>
							           		<td class="td_gris_l" colspan="2">
							                <span style="cursor: hand;" onclick='toggleDiv("comentario"); if(comentario.style.display == "none"){ commentsImg.src="menu-images/mas.gif";} else { commentsImg.src="menu-images/menos.gif";}'>
							                    <img id='commentsImg' src='menu-images/mas.gif' />
							                    <%=rb.getString("imp.comentarios")%>
							                </span>
							                </td>
							           </tr>
							                      
							           <tr style="display:none" id="comentario">
							                <td colspan="2">
												<textarea cols="50" rows="3" name="comentariosImp" onkeypress="javascript:deleteText(this)"><%=comentariosImp%></textarea>
							                </td>
							           </tr>
						                <script language="javascript">
						                    document.getElementById("comentario").style.display = "none";
						                </script>
							           <tr>
							           		<td class="td_gris_l" colspan="2"><br></td>
							           </tr>

                                       <tr>
                                              <td align= "center">
                                                        <%=ToolsHTML.getRadioButton("perfil",0,0,"javascript:disparandoSeleccion('0');")%> <%=rb.getString("imp.controlada0")%>
                                              </td>
                                               <td align= "center">
                                                        <%=ToolsHTML.getRadioButton("perfil",0,1,"javascript:disparandoSeleccion('1');")%> <%=rb.getString("imp.nocontrolada0")%>
                                               </td>
                                       </tr>
                                       <tr><td>Usuarios</td><td>Copias</td></tr>
                                       <tr><td></td></tr>
                                       <tr><td></td></tr>
                                       <tr><td></td></tr>

                                                <%int i =Integer.parseInt(Pagingbean.getDesde());%>
                                                   <logic:iterate id="bean" name="usuarios" indexId="ind" offset="<%=Pagingbean.getDesde()%>" length="<%=Pagingbean.getCuantos()%>"
                                                           type="com.desige.webDocuments.utils.beans.Search" scope="session">
                                                            <%
                                                                String checked="";
                                                                String valor="0";
                                                                if (bean.getId()!=null){
                                                                    String key=bean.getId();
                                                                    boolean chequed=false;
                                                                    if (existeCopias.get(key)!=null){
                                                                       valor=(String)existeCopias.get(key);
                                                                    }
                                                                }
                                                                if (!"0".equalsIgnoreCase(valor)){
                                                                   checked="checked";
                                                                }
                                                            %>
                                                            <tr>
                                                               <td class="td_gris_l">
                                                                   <logic:present name="showCharge" scope="session">
                                                                   <%out.println("<input name=usersSelected"+i+" onClick='javascript:desmarcarCheck(copias"+i+",usersSelected"+i+");'"+ " type=checkbox value="+bean.getId() + " " + checked + ">");%>
                                                                   <%=bean.getAditionalInfo()%>
                                                                   </logic:present>
                                                                   <logic:notPresent name="showCharge" scope="session">

                                                                          <%out.println("<input name=usersSelected"+i+" onClick='javascript:desmarcarCheck(copias"+i+",usersSelected"+i+");'"+ " type=checkbox value="+bean.getId() + " " + checked + ">");%>
                                                                          <%=bean.getDescript()%>
                                                                   </logic:notPresent>
                                                               </td>
                                                                <td width="40%">
                                                                       <%out.println("<input name=copias"+i+" type=text value="+valor+" onChange='javascript:desmarcarCheck(copias"+i+",usersSelected"+i+");' >");%>
                                                                </td>

                                                            </tr>
                                                     <%++i;%>
                                                     </logic:iterate>
                                            <%
                                                if (size.compareTo("0") > 0) {
                                            %>
                                                    <tr>
                                                        <td align="center" colspan="2">
                                                            <!-- Inicio de Paginaci�n -->
                                                            <table class="paginador">
                                                              <tr>
                                                                <td align="center" colspan="4">
                                                                  <br>
                                                                  <font size="1" color="#000000">
                                                                    <%=rb.getString("pagin.title")+ " "%>
                                                                    <%=Integer.parseInt(Pagingbean.getPages())+1%>
                                                                    <%=rb.getString("pagin.of")%>
                                                                    <%=Pagingbean.getNumPages()%>
                                                                  </font>
                                                                </td>
                                                              </tr>
                                                              <tr>
                                                                  <td align="center"> <img src="img/First.gif" width="24" height="24"
                                                                       class="GraphicButton" title="<%=rb.getString("pagin.First")%>"
                                                                       onclick="paging_OnClick(0)">
                                                                  </td>
                                                                  <td align="center"> <img src="img/left.gif" width="24" height="24"
                                                                       class="GraphicButton" title="<%=rb.getString("pagin.Previous")%>"
                                                                        onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())-1)%>)">
                                                                  </td>
                                                                  <td align="center"> <img src="img/right.gif" width="24" height="24"
                                                                       class="GraphicButton" title="<%=rb.getString("pagin.next")%>"
                                                                       onclick="paging_OnClick(<%=(Integer.parseInt(Pagingbean.getPages())+1)%>)">
                                                                  </td>
                                                                  <td align="center"> <img src="img/End.gif" width="24" height="24"
                                                                       class="GraphicButton" title="<%=rb.getString("pagin.Last")%>"
                                                                       onclick="paging_OnClick(<%=Pagingbean.getNumPages()%>)">
                                                                  </td>
                                                              </tr>
                                                            </table>
                                                            <!-- Fin de Paginaci�n -->
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td colspan="2" class="td_gris_l">
                                                            &nbsp;
                                                        </td>
                                                    </tr>
                                                <%
                                                    }
                                                %>

                                      <tr id="botones">
                                          <td colspan="2" align="center" >
												<input type="button" class="boton" name="btnOK" value="<%=rb.getString("btn.send")%>" onClick="javascript:salvar();" >
												&nbsp;
												<input type="button" class="boton" name="btnCancel" value="<%=rb.getString("btn.cancel")%>" onClick="javascript:closeWin();" >
                                           </td>
                                       </tr>
                                        </form>
                                        <form name="formPagingPage" method="post" action="showSolicitudImpresion.jsp">
                                              <input type="hidden" name="from"  value="">
                                              <input type="hidden" name="cmd"  value="<%=SuperActionForm.cmdLoad%>">
                                              <input type="hidden" name="docRelations"  value="<%=docRelations%>">
                                              <input type="hidden" name="copiasRelations"  value="<%=copiasRelations%>">
                                              <input type=hidden name="namePropietario" value="<%=request.getParameter("namePropietario")%>">
                                              <input type=hidden name="nameResponsable" value="<%=request.getParameter("nameResponsable")%>">
                                              <input type=hidden name="idDocument" value="<%=request.getParameter("idDocument")!=null?request.getParameter("idDocument"):"0"%>"/>
                                              <input type=hidden name="idVersion" value="<%=request.getParameter("idVersion")!=null?request.getParameter("idVersion"):"0"%>"/>
                                              <input type=hidden name="controlada" value="si"/>
                                              <input type=hidden name="comentariosImp" value="<%=comentariosImp%>"/>
                                              <logic:present name="datossolicitante" scope="session">
                                                 <bean:define id ="solicitaprint" name="datossolicitante" type="com.desige.webDocuments.users.forms.LoginUser" scope="session"/>
                                                 <input type=hidden name="solicitante" value="<bean:write name="solicitaprint" property="idPerson"/>">
                                              </logic:present>
                                         </form>

                               </logic:present>
                             </center>
                        </td>
                   </table>
                </td>

           <tr><td></td></tr>
           <tr><td></td></tr>
           <tr><td></td></tr>
           <tr><td></td></tr>
           <tr><td></td></tr>
           <tr><td></td></tr>
           <tr><td></td></tr>

        </table>
    </center>
<%--</form>--%>
</body>
</logic:notPresent>
</html>
<script language="javascript">
<%

%>
</script>
