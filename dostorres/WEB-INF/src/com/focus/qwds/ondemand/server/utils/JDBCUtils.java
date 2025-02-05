package com.focus.qwds.ondemand.server.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.focus.qwds.ondemand.server.excepciones.ErrorDeAplicaqcion;

public class JDBCUtils {
	
	private static Properties configuracion = null;
	private static DataSource ds = null;
        
	
	static{
		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
                        //Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JDBCUtils.configuracion = new Properties();    	

                /*
                // La peticion de conexion a traves de jdbc sin datasource esta deshabilitada donde esta se utiliza, por lo que
                // no hace falta cargar el archivo de configuracion Conf.xml
		try {
			JDBCUtils.configuracion.loadFromXML(new FileInputStream("com/focus/qwds/ondemand/server/configuracion/Conf.xml"));			
		} catch (InvalidPropertiesFormatException e) {	
			e.printStackTrace();
		} catch (FileNotFoundException e) {		
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
                */
    	
	}
	
	public static Connection getConnection() throws SQLException, ErrorDeAplicaqcion {
            if (ds == null){
            Context ctx;
                try {
                    ctx = new InitialContext();

                    ds = (DataSource) ctx.lookup("java:comp/env/jdbc/WebDocs");

                } catch (NamingException ex) {
                   throw new ErrorDeAplicaqcion(ex);
                }
            }
            
            return ds.getConnection();
                
            //return DriverManager.getConnection(JDBCUtils.configuracion.getProperty("url"),JDBCUtils.configuracion.getProperty("usuario"), JDBCUtils.configuracion.getProperty("password"));
	}
}
