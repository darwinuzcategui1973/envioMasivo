

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.desige.webDocuments.persistent.utils.JDBCUtil;

public class ReplaceNamFiles {

	public ReplaceNamFiles() {

	}

	public void start() {
		Connection cone = null;
		try {
			//Class.forName("org.postgresql.Driver").newInstance();
			Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();

			//cone = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test", "qwebdocuments", "qwebdocuments");
			cone = DriverManager.getConnection("jdbc:jtds:sqlserver://localhost:1433/produccion", "qwebdocuments", "qwebdocuments");

			PreparedStatement pst = null;
			
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT nameFile,numgen FROM documents");
			
			pst = cone.prepareStatement(JDBCUtil.replaceCastMysql(sb.toString()));
			ResultSet rs = pst.executeQuery();
			
			pst = cone.prepareStatement(JDBCUtil.replaceCastMysql("UPDATE documents SET nameFile=? WHERE numgen=?"));
			
			String name;
			System.out.println("Convirtiendo los comentarios de la tabla workflows");
			while(rs.next()) {
				System.out.println(rs.getInt(2));
				name=rs.getString(1).replaceAll("http://vargaslims", "http://webapp").replaceAll("http://VARGASLIMS", "http://webapp");
				//coment=rs.getString(1).replaceAll("http://ccsfocus03", "http://webapp");
				pst.setString(1,name);
				pst.setInt(2,rs.getInt(2));
				
				pst.execute();
			}

			System.out.println("Ejecutado sin error");

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
		//ReplaceNamFiles t = new ReplaceNamFiles();
		//t.start();
		
		String cad = "Metaco_nazol. c%re&-ma (50 Mg.).doc";
		String pat = "";
		System.out.println(cad.replace(pat, cad));
	}

}
