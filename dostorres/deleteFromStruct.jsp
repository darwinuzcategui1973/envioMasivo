<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.HashMap"%>
<%@page import="java.util.LinkedList"%>
<%@page import="java.util.List"%>
<%@page import="com.desige.webDocuments.persistent.utils.JDBCUtil"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.util.Map"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Sección de Utilidades de Focus</title>
<script type="text/javascript">
    function alertInvalidKey(){
    	alert("Clave de ejecución inválida");
    }
    
    function doCheckTree(formulario){
    	formulario.process.value = "2";
    	formulario.submit();
    }
    
    function doDeleteTree(formulario){
        formulario.process.value = "3";
        formulario.submit();
    }
</script>
</head>
<body>
<%!
    private void getInfoNodes(String idNodo, List<String> levelInfo, List<String> idsInfo, int level){
       Connection con = null;
       PreparedStatement ps = null;
       ResultSet rs = null;
       final String query = "SELECT idnode, name FROM struct WHERE idnodeparent=? ORDER BY idnode";
       final int replySpaces = 8;
       
	   try{
		    con = JDBCUtil.getConnection("deleteFromStruct.jsp");
		    ps = con.prepareStatement(query);
		    ps.setString(1, idNodo);
		    
		    rs = ps.executeQuery();
		    while(rs.next()){
		    	idsInfo.add(rs.getString(1));
		    	
		    	String textLine = "";
		    	for(int i = 0; i < level * replySpaces; i++){
		    		textLine += "&nbsp;";
		    	}
		    	textLine += (rs.getString(2) + "<br />");
		    	levelInfo.add(textLine);
		    	
		    	getInfoNodes(rs.getString(1), levelInfo, idsInfo, level+1);
		    }
       } catch(Exception e){
           e.printStackTrace();
       } finally {
            try{
            	rs.close();
            } catch(Exception e){
            	
            }
            
            try{
                ps.close();
            } catch(Exception e){
                
            }
            
            try{
                con.close();
            } catch(Exception e){
                
            }
       }
	}

	private void deleteNodes(String idsToDelete){
		Connection con = null;
	    PreparedStatement ps = null;
	    final String query1 = "DELETE FROM documents WHERE idnode IN (" + idsToDelete + ")";
	    final String query2 = "DELETE FROM struct WHERE idnode IN (" + idsToDelete + ")";
	    
	    try{
	    	con = JDBCUtil.getConnection("deleteFromStruct.jsp");
	        ps = con.prepareStatement(query1);
	        
	        //eliminados los documentos, ahora eliminamos la estructura
	        ps = con.prepareStatement(query2);
	        ps.execute();
	    } catch(Exception e){
	        e.printStackTrace();
	    } finally {
	        try{
	            ps.close();
	        } catch(Exception e){
	            
	        }
	        
	        try{
	            con.close();
	        } catch(Exception e){
	            
	        }
	    }
	}
%>
<%
String result = "";
String listadoNodos = "";
String idNodo = "";
boolean claveValida = false;

if(request.getParameter("process") != null){
	if("*F0cusvision91*".equals(request.getParameter("clave"))){
		claveValida = true;
	}
	
	if("2".equals(request.getParameter("process")) && claveValida){
	    //se desea consultar el arbol a eliminar primero
	    List<String> idsNodes = new LinkedList<String>();
	    result = request.getParameter("idNodo");
	    idNodo = request.getParameter("idNodo");
	    
	    List<String> levelNodes = new LinkedList<String>();
	    levelNodes.add("Nodo Padre <br />");
	    
	    getInfoNodes(request.getParameter("idNodo"),
	            levelNodes,
	            idsNodes,
	            1);
	    
	    for(String nodeId : idsNodes){
	        result += "," + nodeId;
	    }
	    
	    for(String nodeName : levelNodes){
	        listadoNodos += nodeName;
	    }
	} else if("3".equals(request.getParameter("process"))){
	    deleteNodes(request.getParameter("ids"));
	}
}
%>
<form action="deleteFromStruct.jsp" method="post">
    <input type="hidden" name="process" value="1" />
    <input type="hidden" name="ids" value="<%=result%>" />
    
	<table align="center" border="1">
	    <tr>
	        <td>Id del nodo de la estructura a eliminar (solo números): </td>
	        <td><input type="text" name="idNodo" value="<%=idNodo%>"/> </td>
	    </tr>
	    <tr>
	        <td> Clave de ejecuci&oacute;n: </td>
            <td><input type="password" name="clave" value=""/></td>
        </tr>
        <tr>
            <td colspan="2" align="center">
                <input type="button" value="Verificar Arbol" onclick="doCheckTree(this.form)"/>
            </td>
        </tr>
	</table>
	
	<br />
	
	<table align="center">
	   <tr>
           <td align="center">
               <h1>Listado de los nodos involucrados: </h1>
           </td>
       </tr>
       <%
           if(! claveValida){
       %>
           <tr>
               <td>
                   <script type="text/javascript">
                       alertInvalidKey();
                   </script>
                   <h1>Clave de Ejecuci&oacute;n Inv&aacute;lida</h1>
               </td>
           </tr>
       <%
           } else {
       %>
	       <tr>
	           <td>
	               <%= listadoNodos %>
	           </td>
	       </tr>
	       <%
           if(! "".equals(listadoNodos)){
	       %>
	           <tr>
	               <td>
	                   <input type="button" value="Eliminar Arbol (y registros de Documentos)" onclick="doDeleteTree(this.form)"/>
	               </td>
	           </tr>
	       <%
	           }
	       }
       %>
	</table>
</form>
</body>
</html>