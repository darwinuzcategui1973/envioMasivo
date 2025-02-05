<!-- Title: filesRegisterBase.jsp -->
<%@ page import="com.desige.webDocuments.files.forms.ExpedienteForm,
	             com.desige.webDocuments.utils.beans.Users,
	             java.util.Collection,
	             com.desige.webDocuments.persistent.managers.HandlerDBUser"%>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%
Users usuario = (Users)session.getAttribute("user");
Collection users = HandlerDBUser.getAllUsers();
pageContext.setAttribute("usuarios",users);
%>
<html>
    <head>
        <meta content="MSHTML 6.00.2800.1106" name=generator/>
        <link href="estilo/estilo.css" rel="stylesheet" type="text/css"/>
		<script type="text/javascript" src="estilo/funciones.js"></script>
		<script type="text/javascript" src="estilo/Calendario_2.js"></script>
		<script type="text/javascript">
			function isValido(obj,msg){
				if((typeof obj.length)=='undefined'){
					error=false;
					marcado=(obj.type=='radio' || obj.type=='checkbox');
					error = (marcado && obj.checked?true:error);
					error = (!marcado && trim(obj.value)==''?true:error);
					
					if(error){
						alert(msg+" es requerido ");
						obj.value='';
						return false;
					}
				} else {
					if(obj.type=="select-one" && (typeof obj.value)!='undefined' && trim(obj.value)!='') {
						return true;
					} else {
						for(var x=0; x<obj.length; x++) {
							if(obj[x].checked) {
								return true;
							}
						}
					}
					alert(msg+" es requerido ");
					return false;
				}
				return true;
			}
			
			function isEmpty(obj){
				vacio=false;
				if((typeof obj.length)=='undefined'){
					vacio=false;
					marcado=(obj.type=='radio' || obj.type=='checkbox');
					vacio = (marcado && !obj.checked?true:vacio);
					vacio = (!marcado && trim(obj.value)!=''?true:vacio);

				} else {
					for(var x=0; x<obj.length; x++) {
						if(obj[x].checked){
							vacio=false;
						}
					}
				}
				return vacio;
			}
			

			function isItemValid(obj,item,objRequerido,msg){
				for(var x=0; x<obj.length; x++) {
					if(obj[x].value==item && (obj[x].type=="select-one"?obj[x].selected:obj[x].checked) ){
						if(!isValido(objRequerido,msg,objRequerido.type)) { 
							try{objRequerido.focus()}catch(e){try{objRequerido[0].focus()}catch(e){}}; 
							return false;
						}
					}
				}
				return true;
			}

			function save(){
				<!--validacion-->
				
				document.forms[0].submit();
			}
			
			function cancelar() {
				document.forms[0].action="filesView.do";
				document.forms[0].submit();
			}
			
			function llenar(tag) {
				var obj = eval('document.forms[0].'+tag);
				var lista = eval('document.forms[0].'+tag+'_');
				var sep = '';
				
				obj.value='';
				for(var x=0;x<lista.length;x++){
					if(lista[x].checked){
						obj.value += sep + lista[x].value;
						sep = ',';
					}
				}
			}
			
			function marcar(tag) {
				var obj = eval('document.forms[0].'+tag);
				var lista = eval('document.forms[0].'+tag+'_');
				var sep = '';
				
				var valor = (""+obj.value).split(",");
				
				for(var x=0;x<lista.length;x++){
					for(var k=0;k<valor.length;k++){
						if(lista[x].value==valor[k]){
							lista[x].checked=true;
						}
					}
				}
			}
		</script>
	</head>
<body class="bodyInternas">
<html:form action="/filesRegisterSave" method="POST">
<!--codigo-->
</html:form>
<center>
<input type="button" class="boton" onclick="save()" value="<bean:message key="btn.save"/>" >
<input type="button" class="boton" onclick="cancelar()" value="<bean:message key="btn.back"/>" >
</center>
</body>
</html>
<script type="text/javascript"">
	<!--finalCodeJS-->
</script>
