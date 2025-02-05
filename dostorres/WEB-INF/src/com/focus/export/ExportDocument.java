package com.focus.export;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.focus.filesystem.FileSystem;

public class ExportDocument {
	
	
	
	public void export() {
		Connection cone = null;
		FileSystem fs = new FileSystem();
		try {
			//Class.forName("org.postgresql.Driver").newInstance();
			Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();

			//cone = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test", "qwebdocuments", "qwebdocuments");
			cone = DriverManager.getConnection("jdbc:jtds:sqlserver://localhost:1433/vicson", "qwebdocuments", "qwebdocuments");
			

			PreparedStatement pst = null;
			
			StringBuffer sb = new StringBuffer();
			
			// documentos IS - Instructivos de Sistemas para vicson
			sb.setLength(0);
			sb.append("select numVer,nameFile ");
			sb.append("from documents a, versiondoc b ");
			sb.append("where a.numgen=b.numdoc  ");
			sb.append("and a.versionPublic=b.numver ");
			sb.append("and type=66 and (number like '%.1' or number not like '%.%') ");

			fs.vaciarTablaFromQuery(cone, "C:/vicson/IS/", sb.toString() , "versiondoc", "numver", "data");
			
			
			// documentos Formatos para vicson
			sb.setLength(0);
			sb.append(" select versionPublic,nameFile from documents where type=7 and versionpublic>0 ");
			fs.vaciarTablaFromQuery(cone, "C:/vicson/FORMATOS/", sb.toString() , "versiondoc", "numver", "data");
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cone != null) {
				try {
					cone.close();
				} catch (SQLException e) {
				}
			}
		}
		
	}

	public static void main(String[] args) {
		ExportDocument exp = new ExportDocument();
		exp.export();
	}
	
}
