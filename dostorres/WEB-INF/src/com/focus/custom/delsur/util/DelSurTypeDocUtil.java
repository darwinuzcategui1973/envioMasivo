package com.focus.custom.delsur.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.typeDocuments.forms.TypeDocumentsForm;
import com.desige.webDocuments.utils.beans.SuperActionForm;

public class DelSurTypeDocUtil {
	private static final Logger log = LoggerFactory.getLogger(DelSurTypeDocUtil.class);
	
	/**
	 * 
	 * @param forma
	 * @return
	 */
	private static int existeTypeDoc(TypeDocumentsForm forma){
		int docTypeId = -1;
	    Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try{
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			StringBuilder sql = new StringBuilder("");
			sql.append("SELECT idtypedoc FROM typedocuments WHERE TypeDoc = ?");
			
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			st.setString(1, forma.getName().trim());
			
			rs = st.executeQuery();
			if (rs.next()){
				docTypeId = rs.getInt(1);
			}
		} catch(Exception e) {
			log.error(String.valueOf(e.getCause()), e);
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				st.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
		return docTypeId;
	}
	
	/**
	 * 
	 * @param nombreTipoDocumento
	 * @return
	 */
	public static int getTypeDocId(String nombreTipoDocumento,
			String codigoDocumento){
		//verificamos si existe
		TypeDocumentsForm forma = new TypeDocumentsForm();
		forma.setCmd(SuperActionForm.cmdInsert);
		forma.setFirstPage((byte) 1);
		forma.setId("");
		forma.setMonthsDeadDocs("3");
		forma.setMonthsExpireDocs("3");
		forma.setMonthsExpireDrafts("3");
		forma.setName(nombreTipoDocumento);
		forma.setPublicDoc((byte) 0);
		forma.setSendToFlexWF((byte) 1);
		forma.setUnitTimeDead("M");
		forma.setUnitTimeExpire("M");
		forma.setUnitTimeExpireDrafts("M");
		forma.setEquivalencia(codigoDocumento);
		forma.setGenerateRequestSacop(1);
		
		int docTypeId = existeTypeDoc(forma);
		if(docTypeId < 1){
			//el documento no existe
			//debemos crearlo
			HandlerTypeDoc.insert(forma);
			docTypeId = Integer.parseInt(forma.getId());
		}
		
		forma.setId(Integer.toString(docTypeId));
		
		return docTypeId;
	}
}
