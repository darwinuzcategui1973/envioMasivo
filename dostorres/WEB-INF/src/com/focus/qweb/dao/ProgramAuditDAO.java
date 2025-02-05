package com.focus.qweb.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.desige.webDocuments.accion.forms.ProgramAudit;
import com.desige.webDocuments.persistent.managers.HandlerNorms;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.ProgramAuditTO;

import sun.jdbc.rowset.CachedRowSet;

/**
 * 
 * @author JRivero
 * 
 */
public class ProgramAuditDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	ProgramAuditTO ProgramAuditTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oProgramAuditTO) throws Exception {
		ProgramAuditTO = (ProgramAuditTO) oProgramAuditTO;

		if(ToolsHTML.parseInt(ProgramAuditTO.getIdProgramAudit())<=0) {
			int idProgramAudit = HandlerStruct.proximo("programaudit", "programaudit", "idProgramAudit");
			ProgramAuditTO.setIdProgramAudit(String.valueOf(idProgramAudit));
		}
		
		query.setLength(0);
		query.append("INSERT INTO programaudit (idProgramAudit,nameProgram,idNorm,idPersonResponsible,dateFrom,dateUntil,status,idNormCheck,dateCreated,idPersonCreator) ");
		query.append("VALUES(?,?,?,?,?,?,?,?,?,?)");

		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, 1);
		
		parametros = new ArrayList<Object>();
		parametros.add(ProgramAuditTO.getIdProgramAuditInt());
		parametros.add(ProgramAuditTO.getNameProgram());
		parametros.add(ProgramAuditTO.getIdNormInt());
		parametros.add(ProgramAuditTO.getIdPersonResponsibleInt());
		//parametros.add(new java.sql.Date(ProgramAuditTO.getDateCreatedDATE().getTime()));
		//parametros.add(new java.sql.Date(c.getTimeInMillis()));
		parametros.add(new java.sql.Date(ProgramAuditTO.getDateFromDATE().getTime()));
		parametros.add(new java.sql.Date(ProgramAuditTO.getDateUntilDATE().getTime()));
		parametros.add(ProgramAuditTO.getStatus()==null || ProgramAuditTO.getStatus().trim().isEmpty()?"B":ProgramAuditTO.getStatus()); // Borrador por defecto
		parametros.add(ProgramAuditTO.getIdNormCheck());
		parametros.add(new java.sql.Date(ProgramAuditTO.getDateCreatedDATE().getTime()));
		parametros.add(ProgramAuditTO.getIdPersonCreatorInt());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oProgramAuditTO) throws Exception {
		ProgramAuditTO = (ProgramAuditTO) oProgramAuditTO;

		query.setLength(0);
		query.append("UPDATE programaudit SET nameProgram=?,idNorm=?,idPersonResponsible=?,dateFrom=?, ");
		query.append("dateUntil=?,status=?,idNormCheck=?,dateCreated=?,idPersonCreator=? ");
		query.append("WHERE idProgramAudit=?");

		parametros = new ArrayList<Object>();
		parametros.add(ProgramAuditTO.getNameProgram());
		parametros.add(ProgramAuditTO.getIdNormInt());
		parametros.add(ProgramAuditTO.getIdPersonResponsibleInt());
		parametros.add(new java.sql.Date(ProgramAuditTO.getDateFromDATE().getTime()));
		parametros.add(new java.sql.Date(ProgramAuditTO.getDateUntilDATE().getTime()));
		parametros.add(ProgramAuditTO.getStatus()==null || ProgramAuditTO.getStatus().trim().isEmpty()?"B":ProgramAuditTO.getStatus()); // Borrador por defecto
		parametros.add(ProgramAuditTO.getIdNormCheck());
		parametros.add(new java.sql.Date(ProgramAuditTO.getDateCreatedDATE().getTime()));
		parametros.add(ProgramAuditTO.getIdPersonCreatorInt());

		parametros.add(ProgramAuditTO.getIdProgramAuditInt()); // primary key

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oProgramAuditTO) throws Exception {
		ProgramAuditTO = (ProgramAuditTO) oProgramAuditTO;

		query.setLength(0);
		query.append("DELETE FROM programaudit WHERE idProgramAudit=?");

		parametros = new ArrayList<Object>();
		parametros.add(ProgramAuditTO.getIdProgramAuditInt());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oProgramAuditTO) throws Exception {
		ProgramAuditTO = (ProgramAuditTO) oProgramAuditTO;

		query.setLength(0);
		query.append("SELECT * FROM programaudit WHERE idProgramAudit=").append(ProgramAuditTO.getIdProgramAuditInt());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, ProgramAuditTO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<ProgramAuditTO> listar() throws Exception {
		ArrayList<ProgramAuditTO> lista = new ArrayList<ProgramAuditTO>();
		query.setLength(0);
		
		//query.append("SELECT * FROM programaudit ORDER bY idProgramAudit");
		query.append("SELECT DISTINCT(A.idProgramAudit),A.nameProgram ,B.idNorm,A.idPersonResponsible,");
		query.append("A.dateFrom,A.dateUntil,A.status,");
		query.append("A.idNormCheck,A.dateCreated,A.idPersonCreator");
		query.append(" FROM programaudit A,planaudit B WHERE A.idProgramAudit =B.idProgramAudit");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			ProgramAuditTO = new ProgramAuditTO();
			load(crs, ProgramAuditTO);
			lista.add(ProgramAuditTO);
		}
		return lista;
	}
	
	/**
	 * 
	 */
	public ArrayList<ProgramAuditTO> listarActive() throws Exception {
		ArrayList<ProgramAuditTO> lista = new ArrayList<ProgramAuditTO>();

		query.setLength(0);
		query.append("SELECT * FROM programaudit ")
			//.append("WHERE DATE_FORMAT(now(),'%Y-%m-%d') BETWEEN dateFrom AND dateUntil ")
			//.append("AND status != 'C' ")
		    .append("WHERE status != 'C' ")
			.append("ORDER BY idProgramAudit");
			

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			ProgramAuditTO = new ProgramAuditTO();
			load(crs, ProgramAuditTO);
			lista.add(ProgramAuditTO);
		}
		return lista;
	}
	
	public ArrayList<ProgramAuditTO> listarActiveNormas(String normas) throws Exception {
		ArrayList<ProgramAuditTO> lista = new ArrayList<ProgramAuditTO>();
		
	  
		String s=HandlerNorms.getAllNormasPrincipalesPlanDeLosProgramasAuditoria(null,normas).toString();
		normas= s.replace("[", "(");
		normas= normas.replace("]", ")");
		
		query.setLength(0);
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			
			query.append("SELECT * FROM programaudit programa ")
			.append("WHERE EXISTS(SELECT 1 FROM planaudit WHERE programa.idProgramAudit = planaudit.idProgramAudit ");
			if (!ToolsHTML.isEmptyOrNull(normas)) {
				query.append(" AND  planaudit.idNorm IN  " ).append(normas).append( " )" ) ;
			}
			query.append(" AND programa.status != 'C' ")
			.append("ORDER BY programa.idProgramAudit");
			
			break;
		case Constants.MANEJADOR_POSTGRES:
			// MANEJADOR POSTGRES LO DEJO IGUAL MYSQL HAST QUE REALICE PRUEBA CON POGRESQL
			query.append("SELECT * FROM programaudit programa ")
			.append("WHERE EXISTS(SELECT 1 FROM planaudit as plan WHERE programa.idProgramAudit = plan.idProgramAudit ");
			if (!ToolsHTML.isEmptyOrNull(normas)) {
				query.append(" AND  plan.idNorm IN  " ).append(normas).append( " )" ) ;
			}
			query.append(" AND programa.status != 'C' ")
			.append("ORDER BY programa.idProgramAudit");
			
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append("SELECT * FROM programaudit programa ")
			.append("WHERE EXISTS(SELECT 1 FROM planaudit as plan WHERE programa.idProgramAudit = plan.idProgramAudit ");
			if (!ToolsHTML.isEmptyOrNull(normas)) {
				query.append(" AND  plan.idNorm IN  " ).append(normas).append( " )" ) ;
			}
			query.append(" AND programa.status != 'C' ")
			.append("ORDER BY programa.idProgramAudit");
			
			break;
		}
		
		/*
		if (!ToolsHTML.isEmptyOrNull(normas)) {
			query.append(" AND  plan.idNorm IN  " ).append(normas).append( " )" ) ;
		}
		query.append(" AND programa.status != 'C' ")
		.append("ORDER BY programa.idProgramAudit");
		*/

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			ProgramAuditTO = new ProgramAuditTO();
			load(crs, ProgramAuditTO);
			lista.add(ProgramAuditTO);
		}
		return lista;
	}
	
	public static int remove_Duplicate_Elements(int arr[], int n){  
        if (n==0 || n==1){  
            return n;  
        }  
        int[] tempA = new int[n];  
        int j = 0;  
        for (int i=0; i<n-1; i++){  
            if (arr[i] != arr[i+1]){  
                tempA[j++] = arr[i];  
            }  
         }  
        tempA[j++] = arr[n-1];      
        for (int i=0; i<j; i++){  
            arr[i] = tempA[i];  
        }  
        return j;  
    }  


	/**
	 * 
	 */
	public ArrayList<ProgramAuditTO> listarById(String id) throws Exception {
		ArrayList<ProgramAuditTO> lista = new ArrayList<ProgramAuditTO>();

		query.setLength(0);
		query.append("SELECT * FROM programaudit ");
		query.append("WHERE idProgramAudit ");
		if(id.indexOf(",")==0) {
			query.append("=").append(id).append(" ");
		} else {
			query.append(" IN (").append(id).append(") ");
		}
		query.append("ORDER BY dateFrom ");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			ProgramAuditTO = new ProgramAuditTO();
			load(crs, ProgramAuditTO);
			lista.add(ProgramAuditTO);
		}
		return lista;
	}

	/**
	 * 
	 */
	public ArrayList<ProgramAuditTO> listarProgramAuditAlls(String idNorm) throws Exception {
		ArrayList<ProgramAuditTO> lista = new ArrayList<ProgramAuditTO>();

		query.setLength(0);
		query.append("SELECT * FROM programaudit ");
		if (!ToolsHTML.isEmptyOrNull(idNorm)) {
			query.append("WHERE idNorm=").append(idNorm).append(" ");
		}
		query.append("ORDER BY dateFrom ");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			ProgramAuditTO = new ProgramAuditTO();
			load(crs, ProgramAuditTO);
			lista.add(ProgramAuditTO);
		}
		return lista;
	}


	/**
	 * 
	 */
	public Map<String,ProgramAuditTO> listarOrderProgramAuditAlls(String idNorm) throws Exception {
		Map<String,ProgramAuditTO> lista = new TreeMap<String,ProgramAuditTO>();
	    
		query.setLength(0);
		query.append("SELECT * FROM programaudit ");
		if (!ToolsHTML.isEmptyOrNull(idNorm)) {
			query.append("WHERE idNorm='").append(idNorm).append("' ");
		}
		query.append("ORDEr BY idNorm ");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			ProgramAuditTO = new ProgramAuditTO();
			load(crs, ProgramAuditTO);
			lista.put(ProgramAuditTO.getIdProgramAudit(),ProgramAuditTO);
		}
		return lista;
	}
	
	public synchronized static boolean delete(ProgramAudit forma)
			throws ApplicationExceptionChecked {
		try {
			if(!PlanAuditDAO.isProgramInPlan(forma.getIdProgramAudit())) {
				StringBuilder delete = new StringBuilder("DELETE FROM programaudit WHERE idProgramAudit = ").append(forma.getId());
				return JDBCUtil.doUpdate(delete.toString()) > 0;
			} else {
				throw new ApplicationExceptionChecked("E0141");
			}
		} catch (ApplicationExceptionChecked ae) {
			throw new ApplicationExceptionChecked("E0141");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void load(CachedRowSet crs, ProgramAuditTO ProgramAuditTO) throws SQLException {
		ProgramAuditTO.setIdProgramAudit(crs.getString("idProgramAudit"));
		ProgramAuditTO.setNameProgram(crs.getString("nameProgram"));
		ProgramAuditTO.setIdNorm(crs.getString("idNorm"));
		ProgramAuditTO.setIdPersonResponsible(crs.getString("idPersonResponsible"));
		ProgramAuditTO.setDateFrom(crs.getString("dateFrom"));
		ProgramAuditTO.setDateUntil(crs.getString("dateUntil"));
		ProgramAuditTO.setStatus(crs.getString("status"));
		ProgramAuditTO.setIdNormCheck(crs.getString("idNormCheck"));
		ProgramAuditTO.setDateCreated(crs.getString("dateCreated"));
		ProgramAuditTO.setIdPersonCreator(crs.getString("idPersonCreator"));
		
	}

}
