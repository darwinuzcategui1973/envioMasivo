package com.desige.webDocuments.dao;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.desige.webDocuments.bean.ExpedienteRequest;
import com.desige.webDocuments.enums.AudioFormatExtensionEnum;
import com.desige.webDocuments.enums.VideoFormatExtensionEnum;
import com.desige.webDocuments.files.facade.FilesFacade;
import com.desige.webDocuments.files.forms.ExpedienteDetalleForm;
import com.desige.webDocuments.files.forms.ExpedienteForm;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.FilesDocumentNotValidException;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.util.Archivo;

import sun.jdbc.rowset.CachedRowSet;

public class ExpedienteDetalleDAO implements Serializable {

	private CachedRowSet crs = null;
	private StringBuffer query = new StringBuffer();
	private StringBuffer update = new StringBuffer();
	private StringBuffer insert1 = new StringBuffer();
	private StringBuffer insert2 = new StringBuffer();

	private static int campos = 0;

	public ExpedienteDetalleDAO() {
	}

	public void save(ExpedienteForm files, String lista,Users usuario) throws Exception {
		
		query.setLength(0);
		ArrayList<Object> parametro = new ArrayList<Object>();
		TreeMap<Integer, ExpedienteDetalleForm> listAll = new TreeMap<Integer, ExpedienteDetalleForm>();
		TreeMap<Integer, ExpedienteDetalleForm> listSort = new TreeMap<Integer, ExpedienteDetalleForm>();
		String[] listDocument = lista.split(",");
		int act = 0;
		int orden = 10000;
		StringBuffer queryDoc = new StringBuffer("select a.orden,b.namedocument from expediente_detalle a, documents b where a.numgen=b.numgen and a.f1=? order by orden");
		CachedRowSet relacionadosOld = null;
		CachedRowSet relacionadosNew = null;
		StringBuffer comentarios = new StringBuffer();

		Map<String,String> listNameDocuments = HandlerDocuments.getNameFilesDocuments(listDocument);
		VideoFormatExtensionEnum[] videoExtesionEnum = VideoFormatExtensionEnum.values();
		AudioFormatExtensionEnum[] audioExtesionEnum = AudioFormatExtensionEnum.values();
		
		ExpedienteDetalleForm filesDetForm;
		boolean hacer = true;
		for (int i = 0; i < listDocument.length && !lista.trim().equals(""); i++) {
			
			filesDetForm = new ExpedienteDetalleForm();
			filesDetForm.setF1(files.getF1());
			filesDetForm.setNumgen(Integer.parseInt(listDocument[i]));
			filesDetForm.setOrden(orden++);
			
			hacer = true;
			if(listNameDocuments.containsKey(listDocument[i])) {
				String nameFile = listNameDocuments.get(listDocument[i]);
				for(VideoFormatExtensionEnum ext: videoExtesionEnum) {
					if(nameFile.toLowerCase().endsWith(ext.getValue())) {
						hacer=false;
						break;
					}
				}
				if(hacer) {
					for(AudioFormatExtensionEnum ext: audioExtesionEnum) {
						if(nameFile.toLowerCase().endsWith(ext.getValue())) {
							hacer=false;
							break;
						}
					}
				}
			}
			if(hacer) {
				listAll.put(filesDetForm.getNumgen(), filesDetForm);
			}
		}

		// consultamos los registros existentes
		parametro.add(files.getF1());
		query.setLength(0);
		query.append("select * from expediente_detalle where f1=?");
		crs = JDBCUtil.executeQuery(query, parametro, Thread.currentThread().getStackTrace()[1].getMethodName());
		
		//  consultamos los documentos relaciondos actuales
		//relacionadosOld = JDBCUtil.executeQuery(queryDoc, parametro);

		String sep = "";
		while (crs.next()) {
			if (listAll.containsKey(crs.getInt("f1"))) {
				filesDetForm = listAll.get(crs.getInt("f1"));
				filesDetForm.setOrden(crs.getInt("orden"));
			}
		}

		for (Iterator ite = listAll.keySet().iterator(); ite.hasNext();) {
			filesDetForm = listAll.get(ite.next());
			listSort.put(filesDetForm.getOrden(), filesDetForm);
		}

		Connection cone = null;
		try{
			cone = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			cone.setAutoCommit(false);

			query.setLength(0);
			query.append("delete from expediente_detalle where f1=?");
			parametro = new ArrayList<Object>();
			parametro.add(files.getF1());
			act = JDBCUtil.executeUpdate(query, parametro,cone);
	
			query.setLength(0);
			query.append("insert into expediente_detalle (f1,numgen,orden) values(?,?,?)");
			parametro.add(0);
			parametro.add(0);
	
			orden = 1;
			for (Iterator ite = listSort.keySet().iterator(); ite.hasNext();) {
				filesDetForm = listSort.get(ite.next());
				filesDetForm.setOrden(orden++);
	
				parametro.set(1, filesDetForm.getNumgen());
				parametro.set(2, filesDetForm.getOrden());
				act = JDBCUtil.executeUpdate(query, parametro,cone);
				if (act == 0)
					throw new Exception("No se puede agregar el registro");
			}
			
			parametro = new ArrayList<Object>();
			parametro.add(files.getF1());
			//  consultamos los documentos relaciondos que han sido agregados
			relacionadosNew = JDBCUtil.executeQuery(queryDoc, parametro, cone, Thread.currentThread().getStackTrace()[1].getMethodName());
			
			// establecemos la relacion de los documentos modificados
			comentarios.append("Documentos relacionados / related documents<br/><br/>");
			boolean entro=false;
			while(relacionadosNew.next()) {
				entro=true;
				comentarios.append("* (");
				comentarios.append(relacionadosNew.getString(1));
				comentarios.append(") ");
				comentarios.append(relacionadosNew.getString(2));
				comentarios.append("<br/>");
			}
			if(!entro) {
				comentarios.append("No hay documentos relacionados / There are no related documents");
			}
			
			FilesFacade.updateHistoryFiles(cone, files.getF1(), usuario.getUser(), null, "17", comentarios.toString(), new String[]{"0","0"});
			
			cone.commit();
		}catch(Exception e) {
			e.printStackTrace();
			if (cone != null) {
				cone.rollback();
			}
			throw e;
		} finally{
			if (cone != null) {
				cone.close();
			}
		}

	}
	
	
	public CachedRowSet findAllWithName(ExpedienteForm expDetForm) throws Exception {
		ArrayList<Object> parametro = new ArrayList<Object>();

		parametro.add(expDetForm.getF1());
		
		query.setLength(0);
		query.append("select b.nameFile, c.name, b.owner,d.data ");
		query.append("from expediente_detalle a, documents b, struct c, versiondoc d ");
		query.append("where a.numgen=b.numgen ");
		query.append("and b.idNode=c.idNode ");
		query.append("and d.numver=(select max(numver) from versiondoc where versiondoc.numdoc=a.numgen) "); 
		query.append("and a.f1=? ");

		return JDBCUtil.executeQuery(query,parametro, Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public List<ExpedienteRequest> findAllExpedientes(ExpedienteForm expDetForm) throws Exception {
		List<ExpedienteRequest> lista = new ArrayList<ExpedienteRequest>();
	
		CachedRowSet crs = findAll(expDetForm);
		while(crs.next()) {
			lista.add(new ExpedienteRequest(crs));
		}
		
		String nameFileCacheTemp;
		File cache;
		for(ExpedienteRequest item : lista) {
			nameFileCacheTemp = Archivo.getNameFileEncripted(null, "versiondocview", Integer.parseInt(item.getNumver()), null);
			cache = new File(nameFileCacheTemp);
			item.setPdfGenerated(cache.exists());
		}
		
		return lista;
	}

	public CachedRowSet findAll(ExpedienteForm expDetForm) throws Exception {
		ArrayList<Object> parametro = new ArrayList<Object>();

		parametro.add(expDetForm.getF1());
		
		query.setLength(0);
		query.append("SELECT a.numgen,b.namedocument,c.mayorver,c.minorver,b.prefix,b.number,");
		query.append("b.type,b.owner,a.orden,d.nombres,d.apellidos,e.typedoc,");
		query.append("b.statu,c.statu as statuVer,c.dateExpiresDrafts,c.dateExpires, b.nameFile, f.name AS folder, c.numver, ");
		query.append("b.toForFiles, b.active, ev.numver AS realVer ");
		query.append("FROM documents b, versiondoc c, person d, typedocuments e, struct f, ");
		query.append("expediente_detalle a LEFT OUTER JOIN  expediente_version ev ON ev.f1 = a.f1 AND a.numgen = ev.numgen AND ev.filesversion = (SELECT MAX(filesversion) from expediente_history where f1 = a.f1) ");
		query.append("WHERE a.numgen=b.numgen ");
		query.append("AND b.numgen=c.numdoc ");
		query.append("AND b.owner=d.nameUser ");
		query.append("AND ").append(JDBCUtil.getCastAsIntString("b.type")).append(" = e.idtypedoc ");
		query.append("AND b.idNode=f.idNode ");
		query.append("AND c.numver=(SELECT MAX(numver) FROM versiondoc WHERE versiondoc.numdoc=b.numgen AND active = ").append(JDBCUtil.getCastAsBitString("1")).append(") ");
		query.append("AND a.f1=? ");
		query.append("ORDER BY a.orden ");

		return JDBCUtil.executeQuery(query,parametro, Thread.currentThread().getStackTrace()[1].getMethodName());
	}
	
	
	public static boolean isConstainDocument(ExpedienteForm expDetForm) throws Exception {
		ArrayList<Object> parametro = new ArrayList<Object>();
		StringBuffer query = new StringBuffer();

		parametro.add(expDetForm.getF1());
		
		query.setLength(0);
		query.append("select count(*) from expediente_detalle where f1=? ");
		CachedRowSet crs = JDBCUtil.executeQuery(query,parametro, Thread.currentThread().getStackTrace()[1].getMethodName());
		crs.next();
		return crs.getInt(1)>0;
	}
	
	public static boolean isConstainDocumentEnabled(ExpedienteForm expDetForm) throws Exception {
		ArrayList<Object> parametro = new ArrayList<Object>();
		StringBuffer query = new StringBuffer();

		parametro.add(expDetForm.getF1());
		
		query.setLength(0);
		query.append("select count(*) from expediente_detalle a, documents b where a.numgen=b.numgen and b.toForFiles='0' and f1=?");
		CachedRowSet crs = JDBCUtil.executeQuery(query,parametro, Thread.currentThread().getStackTrace()[1].getMethodName());
		crs.next();
		return crs.getInt(1)>0;
	}

	/**
	 * 
	 * @param files Contiene el id del expediente a salvar
	 * @param usuario es el usuario que realiza la operacion
	 * @return true si la operacion de salvar es exitosa
	 */
	public ExpedienteDetalleForm saveVersion(ExpedienteForm files,Users usuario) throws FilesDocumentNotValidException {
		Connection cone = null;
		ArrayList<Object> parametro = null;
		StringBuffer comentarios = new StringBuffer();
		ExpedienteDAO oExpedienteDAO = new ExpedienteDAO();
		ExpedienteHistoryDAO oExpedienteHistoryDAO = new ExpedienteHistoryDAO();
		ExpedienteVersionDAO oExpedienteVersionDAO = new ExpedienteVersionDAO();
		try{
			// buscamos los documentos del expediente
			oExpedienteDAO.findById(files);
			
			CachedRowSet rel = findAll(files);
			
			if(rel.size()==0) {
				throw new FilesDocumentNotValidException("No hay documentos relacionados al expediente");
			}
			
			List<ExpedienteRequest> listDocuments = findAllExpedientes(files);
			listDocuments.forEach((doc) -> {
				if(doc.getStatuVer().equals(HandlerDocuments.docBorrador) || !doc.getStatuVer().equals(HandlerDocuments.docApproved) || !doc.getStatuVer().equals(doc.getStatu()) ) {
					throw new FilesDocumentNotValidException("El documento no esta aprobado para ser guardado en una version");
				}
				if(!doc.isActive()) {
					throw new FilesDocumentNotValidException("El documento no existe en el sistema");
				}
				if(!doc.isPdfGenerated()) {
					throw new FilesDocumentNotValidException("El documento no tiene pdf generado");
				}
				if(doc.getToForFiles()==null || doc.getToForFiles().equals("true") || doc.getToForFiles().equals("1") ) {
					throw new FilesDocumentNotValidException("El documento no esta marcado como expediente");
				}
			}); 
			

			// identificamos el numero de version
			files.setFilesVersion(oExpedienteVersionDAO.getNewVersion(files));
			Date fecha = new Date();
			
			// abrimos la conexion a la base de datos
			cone = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			cone.setAutoCommit(false);
			
			// almacenamos el expediente en la tabla historica
			oExpedienteHistoryDAO.save(files, usuario, cone);

			// lo almacenamos en la tabla de versiones
			ExpedienteDetalleForm exp = null;
			parametro = null;
			
			// Almacenamos la version
			while(rel.next()) {
				exp = new ExpedienteDetalleForm();
				exp.setF1(files.getF1());
				exp.setNumgen(rel.getInt("numgen"));
				exp.setOrden(rel.getInt("orden"));
				exp.setNumver(rel.getInt("numver"));
				exp.setFilesVersion(files.getFilesVersion());
				exp.setNameUser(usuario.getUser());
				exp.setOwnerFiles(files.getF3());
				exp.setDatePrint(fecha);

				oExpedienteVersionDAO.save(exp, usuario, cone);
			}
			
			comentarios.setLength(0);
			comentarios.append("Version guardada/Version save<br/>&nbsp;&nbsp;&nbsp;<b>- ");
			comentarios.append(files.getFilesVersion());
			comentarios.append(" -</b>");
			
			FilesFacade.updateHistoryFiles(cone, files.getF1(), usuario.getUser(), null, "18", comentarios.toString(), new String[]{"0","0"});
			
			cone.commit();
			return exp;
		}catch(FilesDocumentNotValidException e) {
			e.printStackTrace();
			if (cone != null) {
				try {
					cone.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			throw e;
		}catch(Exception e) {
			e.printStackTrace();
			if (cone != null) {
				try {
					cone.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} finally{
			if (cone != null) {
				try {
					cone.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	

}
