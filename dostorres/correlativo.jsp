<!-- Title: correlativo.jsp -->
<%@ page import="com.desige.webDocuments.utils.ToolsHTML,java.util.ArrayList"%>
<% 
ArrayList numeracion = (ArrayList)request.getAttribute("NUMERACION_DIGITAL");

ArrayList<String> lista1 = new ArrayList<String>();
ArrayList<String> lista2 = new ArrayList<String>();
ArrayList<String> lista3 = new ArrayList<String>();

numeracion = (numeracion==null?new ArrayList():numeracion);
String inicio = request.getAttribute("inicio")==null?request.getParameter("inicio"):request.getAttribute("inicio").toString();
String ultimo = request.getAttribute("ultimo")==null?request.getParameter("ultimo"):request.getAttribute("ultimo").toString();
int ini = 1;
int fin = 1;
int longitud = inicio.length();
try{ini = Integer.parseInt(inicio);} catch(Exception e){ini = 1;}
try{fin = Integer.parseInt(ultimo); } catch(Exception e){fin = ini+10;}

int col = (fin+1)-ini;
col = col<3 ? 3 : col;
while((col%3)!=0){
	col++;
}
col=col/3;
for(int i=ini;i<=fin;i++){
	if(lista1.size()<col) {
		lista1.add(ToolsHTML.zero(i,longitud));
	} else if(lista2.size()<col) {
		lista2.add(ToolsHTML.zero(i,longitud));
	} else {
		lista3.add(ToolsHTML.zero(i,longitud));
	}
}
while(lista2.size()<lista1.size()) {lista2.add("");}
while(lista3.size()<lista1.size()) {lista3.add("");}
%>
<html>
    <head>
        <link href="estilo/estilo.css" rel="stylesheet" type="text/css"/>
        <style>
        	.red{
        		color:red;
        	}
        </style>
        <script>
        	function recargar(){
        		var ini = parseInt(document.forma.inicio.value);
        		var fin = parseInt(document.forma.ultimo.value);
        		var res = fin-ini;
        		if(fin<=ini){
        			alert('El numero hasta debe ser mayor a desde');
        			return;
        		}
        		if(res>500){
        			alert('No puede indicar mas de 500 numeros');
        			return;
        		}
        		var url = document.location.href;
        		var pos = url.indexOf("?");
        		var cad = url.substring(0,pos);
        		cad += "?inicio=<%=inicio%>";
        		cad += "&ultimo="+document.forma.ultimo.value;
				document.location.href=cad;
        	}
        	
        	function aplicar() {
        		document.forma.guardar.value="true";
        		document.forma.action="./correlativo.do";
        		document.forma.submit();
        	}
        	
        	function main() {
        		<%if(request.getAttribute("MENSAJE")!=null && !request.getAttribute("MENSAJE").equals("")){%>
        			alert("<%=request.getAttribute("MENSAJE")%>");
        			window.close();
        		<%}%>
        	}
        </script>
	</head>
<body class="bodyInternas" onload="main()">
<form name="forma" action="#">
<input type="hidden" name="guardar" value="false" />
<center>
<div style="border:1px solid navy;padding:5;">
Desde: <input type="text" name="inicio" maxlength="20" size="20" value="<%=inicio%>" readonly style="border:0px;"/>&nbsp;&nbsp;
Hasta: <input type="text" name="ultimo" maxlength="20" size="20" value="<%=fin%>"/>&nbsp;&nbsp;
<input type="button" value="Actualizar" onclick="recargar()">&nbsp;&nbsp;<br/><br/>
<input type="button" value="Guardar Secuencia Seleccionada" onclick="aplicar()" class="groovybutton">
</div>
<%if(request.getAttribute("MENSAJE")==null || request.getAttribute("MENSAJE").equals("")){%>
	<table width="80%">
		
		<caption style="color:red;"><%=request.getAttribute("ERROR")!=null?request.getAttribute("ERROR"):""%></caption>
		<tbody id="tabla">
		<%for(int i=0;i<lista1.size();i++){%>
		<tr>
			<td class="<%=numeracion.contains(lista1.get(i))?"red":""%>">
				<input type="checkbox" name="codigo" value="<%=lista1.get(i)%>" <%=numeracion.contains(lista1.get(i))?"disabled":"checked"%> > 
				<%=lista1.get(i)%>
			</td>
	
			<td class="<%=numeracion.contains(lista2.get(i))?"red":""%>">
				<%if(!lista2.get(i).equals("")){%>
				<input type="checkbox" name="codigo" value="<%=lista2.get(i)%>" <%=numeracion.contains(lista2.get(i))?"disabled":"checked"%> > 
				<%}%>
				<%=lista2.get(i)%>
			</td>
	
			<td class="<%=numeracion.contains(lista3.get(i))?"red":""%>">
				<%if(!lista3.get(i).equals("")){%>
				<input type="checkbox" name="codigo" value="<%=lista3.get(i)%>" <%=numeracion.contains(lista3.get(i))?"disabled":"checked"%> > 
				<%}%>
				<%=lista3.get(i)%>
			</td>
		</tr>
		<%}%>
		</tbody>
	</table>
<%}%>
<hr/>
</center>
</form>
</body>
</html>
