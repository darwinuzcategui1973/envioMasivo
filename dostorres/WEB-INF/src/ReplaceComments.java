

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.desige.webDocuments.persistent.utils.JDBCUtil;

public class ReplaceComments {

	public ReplaceComments() {

	}

	public void start() {
		Connection cone = null;
		try {
			//Class.forName("org.postgresql.Driver").newInstance();
			Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();

			//cone = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test", "qwebdocuments", "qwebdocuments");
			cone = DriverManager.getConnection("jdbc:jtds:sqlserver://SRVBD05:1433/qwebdocuments", "qwebdocuments", "qwebdocuments");
			//cone = DriverManager.getConnection("jdbc:jtds:sqlserver://ccsfocus05:1433/qweb45_protocolo", "qwebdocuments", "qwebdocuments");

			PreparedStatement pst = null;
			
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT comments,idworkflow FROM workflows");
			
			pst = cone.prepareStatement(JDBCUtil.replaceCastMysql(sb.toString()));
			ResultSet rs = pst.executeQuery();
			
			pst = cone.prepareStatement(JDBCUtil.replaceCastMysql("UPDATE workflows SET comments=? WHERE idworkflow=?"));
			
			String coment;
			System.out.println("Convirtiendo los comentarios de la tabla workflows");
			while(rs.next()) {
				System.out.println(rs.getInt(2));
				coment=rs.getString(1).replaceAll("http://vargaslims", "http://webapp").replaceAll("http://VARGASLIMS", "http://webapp");
				//coment=rs.getString(1).replaceAll("http://ccsfocus03", "http://webapp");
				pst.setString(1,coment);
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
		ReplaceComments t = new ReplaceComments();
		t.start();
	}

}
