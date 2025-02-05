<%@page import="java.io.OutputStream"%>
<%@page import="com.desige.webDocuments.utils.ToolsHTML"%>
<%@page import="com.desige.webDocuments.persistent.utils.JDBCUtil"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>

<%!
public void borrarPermisoUsuario(String idPerson, String idStruct){
	final String query = "UPDATE PermissionStructUser SET active = 0 WHERE idperson = ? AND idstruct = ?";
	
    Connection con = null;
    PreparedStatement ps = null;
    
    try {
        //
        con = JDBCUtil.getConnection("ajusteSeguridad.jsp");
        ps = con.prepareStatement(query);
        ps.setString(1, idPerson);
        ps.setString(2, idStruct);
        
        ps.execute();
    } catch(Exception e){
        e.printStackTrace();
    } finally{
        try {
            ps.close();
        } catch(Exception e){
            /**/
        }
        
        try {
            con.close();
        } catch(Exception e){
            /**/
        }
    }
}

public void ajusteSeguridad(JspWriter out, String node, String prevPath, String idPerson){
	final String query = "SELECT idnode, name FROM struct WHERE idnodeparent = ? ORDER BY LOWER(name)";
	Connection con = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	
	try {
		//
		con = JDBCUtil.getConnection("ajusteSeguridad");
		ps = con.prepareStatement(query);
		ps.setString(1, node);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			out.println("<h3>" + prevPath + "/" + rs.getString(2) + "</h3>");
			//debemos borrar el permiso de este usuario
			borrarPermisoUsuario(idPerson, rs.getString(1));
			ajusteSeguridad(out, rs.getString(1), prevPath + "/" + rs.getString(2), idPerson);
		}
		
		borrarPermisoUsuario(idPerson, node);
		out.println("<h3>" + prevPath + "</h3>");
        //ajusteSeguridad(rs.getString(1), prevPath + "/" + rs.getString(2), idPerson);
        
	} catch(Exception e){
		e.printStackTrace();
	} finally{
		try {
            rs.close();
        } catch(Exception e){
            /**/
        }
        
        try {
            ps.close();
        } catch(Exception e){
            /**/
        }
        
        try {
            con.close();
        } catch(Exception e){
            /**/
        }
	}
}
%>

<%
String query = null;
Connection con = null;
PreparedStatement ps = null;
ResultSet rs = null;
%>
<html>
<head>
    <title>Ajuste de seguridad forzado</title>
</head>
<body>
    <form action="ajusteSeguridad.jsp" method="post">
        <table align="center">
            <tr>
                <td>Id del nodo en la estructura:</td>
                <td><input type="text" name="nodo" /></td>
            </tr>
            <tr>
                <td>Id del usuario:</td>
                <td>
                    <select name="usuario">
	                <%
	                try {
	                	query = "SELECT idperson, apellidos, nombres FROM person ORDER BY LOWER(apellidos)";
	                    con = JDBCUtil.getConnection("ajusteSeguridad.jsp");
	                    ps = con.prepareStatement(query);
	                    rs = ps.executeQuery();
	                    
	                    while(rs.next()){
	                %>
	                   <option value="<%= rs.getInt(1) %>"><%=rs.getString(2) + ", " + rs.getString(3)%></option>
	                <%
	                    }
	                } catch (Exception e){
	                	e.printStackTrace();
	                } finally {
	                	try {
	                		rs.close();
	                	} catch(Exception e){
	                		/**/
	                	}
	                	
	                	try {
                            ps.close();
                        } catch(Exception e){
                            /**/
                        }
	                	
	                	try {
                            con.close();
                        } catch(Exception e){
                            /**/
                        }
	                }
	                %>
                    </select>
                </td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <input type="submit" name="submit" value="Enviar" />
                </td>
            </tr>
        </table>
    </form>
    
<%
if(request.getParameter("submit") != null){
	if(ToolsHTML.isEmptyOrNull(request.getParameter("usuario"))
			|| ToolsHTML.isEmptyOrNull(request.getParameter("nodo"))){
		out.println("Debe indicar el id del nodo raiz y el usuario al cual se le removera la seguridad.");
	} else {
		ajusteSeguridad(out, request.getParameter("nodo"), "", request.getParameter("usuario"));
	}
}
%>
</body>
</html>