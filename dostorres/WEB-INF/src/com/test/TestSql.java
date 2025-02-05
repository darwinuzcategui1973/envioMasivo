package com.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.desige.webDocuments.persistent.utils.JDBCUtil;

public class TestSql {

	public TestSql() {

	}

	public void start() {
		Connection cone = null;
		try {
			//Class.forName("org.postgresql.Driver").newInstance();
			//cone = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test", "qwebdocuments", "qwebdocuments");

			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			cone = DriverManager.getConnection("jdbc:mysql://localhost:3306/qwebdocuments?useOldAliasMetadataBehavior=true&serverTimezone=America/Caracas", "qwebdocuments", "qwebdocuments");

			PreparedStatement pst = null;
			
			StringBuffer sb = new StringBuffer();
			sb.append("");
			
//			sb.append("INSERT INTO location (IdNode,MajorID,MinorID,AllowUserWF,Secuential,Conditional,AutomaticNotified, ");
//			sb.append("showCharge,typePrefix,timeDocVenc,txttimeDocVenc,HistAprob,cantExpDoc,unitExpDoc,vijenToprint,");
//			sb.append("checkvijenToprint,checkborradorCorrelativo)");
//			sb.append("VALUES (?,?,?,CAST(? AS bit),CAST(? AS bit),CAST(? AS bit),CAST(? AS bit),CAST(? AS bit),CAST(? AS bit),CAST(? AS bit),?,?,?,?,?,CAST(? AS bit),CAST(? AS bit))");

			
			pst = cone.prepareStatement(JDBCUtil.replaceCastMysql("select * from parameters"));
			//pst = cone.prepareStatement("insert into temporal values(?,20)");
			//pst = cone.prepareStatement("update tbl_area set area=?, activea=1 where idarea=20");
			
			//pst = cone.prepareStatement(sb.toString());

			//pst.setInt(1,(byte)1);
			//pst.setString(1,"1");

			pst.execute();

			//System.out.println("Ejecutado sin error");

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
		TestSql t = new TestSql();
		t.start();
	}

}
