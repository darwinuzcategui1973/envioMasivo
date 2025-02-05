package com.focus.custom.delsur;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimerTask;

import javax.sql.DataSource;

import oracle.jdbc.OracleTypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.focus.custom.delsur.dto.CreditoDTO;
import com.focus.custom.delsur.dto.RecaudoCreditoDTO;
import com.focus.custom.delsur.util.CustomProperties;
import com.focus.custom.delsur.util.DelSurStructuraUtil;
import com.focus.custom.delsur.util.DelSurStructMailUtil;

class DelSurBatchProcessTask extends TimerTask {
	private static final Logger log = LoggerFactory.getLogger(DelSurBatchProcessTask.class);
	private DataSource dataSource;
	private CustomProperties props;
	private String carpetaParaDigitalizados;
	private boolean runInDemand = false;
	
	/**
	 * 
	 * @param dataSource
	 * @param props
	 * @param carpetaParaDigitalizados
	 */
	public DelSurBatchProcessTask(DataSource dataSource, CustomProperties props,
			String carpetaParaDigitalizados){
		this(dataSource, props, carpetaParaDigitalizados, false);
	}
	/**
	 * 
	 * @param dataSource
	 * @param props
	 * @param carpetaParaDigitalizados
	 * @param runInDemand
	 */
	public DelSurBatchProcessTask(DataSource dataSource, CustomProperties props,
			String carpetaParaDigitalizados, boolean runInDemand) {
		// TODO Auto-generated constructor stub
		this.dataSource = dataSource;
		this.props = props;
		this.carpetaParaDigitalizados = carpetaParaDigitalizados;
		this.runInDemand = runInDemand;
	}
	
	/**
	 * 
	 * @throws SQLException
	 */
	private final List<CreditoDTO> readFromAbanksDataBase() throws SQLException{
		log.info("Ejecución de job en demanda = " + runInDemand);
		runInDemand = false;
		
		final String query = "CALL ws_k_interface.ws_p_get_infSolicitud(?,?,?"
				+ (runInDemand ? ",?" : "") + ")";
		final int resultSetIndex = runInDemand ? 4 : 3;
		List<CreditoDTO> result = null;
		
		Connection conn = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		
		try {
			conn = dataSource.getConnection();
			callStmt = conn.prepareCall(query);
			
			callStmt.registerOutParameter(resultSetIndex, OracleTypes.CURSOR);
		    callStmt.setInt(1, 5);
		    callStmt.setInt(2, 2);
		    if(runInDemand){
		    	//estado de los recaudos a traerse en demanda
		    	callStmt.setString(3, "C,N");
		    }
		    callStmt.execute();
		    
		    //return the result set
		    rs = (ResultSet) callStmt.getObject(resultSetIndex);
		    while (rs.next()) {
		    	CreditoDTO temp = new CreditoDTO();
		    	
		    	if(result == null){
		    		result = new LinkedList<CreditoDTO>();
		    	}
		    	
		    	temp.setCodigoRegion(rs.getInt(1));
	    		temp.setNombreRegion(rs.getString(2));
	    		temp.setCodigoAgencia(rs.getInt(3));
	    		temp.setNombreAgencia(rs.getString(4));
	    		temp.setCodigoProducto(rs.getInt(5));
	    		temp.setNombreProducto(rs.getString(6));
	    		temp.setCodigoModalidadProducto(rs.getInt(7));
	    		temp.setNombreModalidadProducto(rs.getString(8));
	    		temp.setNumeroSolicitud(rs.getInt(9));
	    		temp.setCodigoUsuarioAbanks(rs.getString(10));
	    		temp.setCodigoTipoBanca(rs.getInt(11));
	    		temp.setNombreTipoBanca(rs.getString(12));
	    		temp.setNumeroIdentificacionCliente(rs.getString(13));
	    		temp.setCodigoCliente(rs.getInt(14));
	    		temp.setNombreSolicitante(rs.getString(15));
	    		temp.setApellidoSolicitante(rs.getString(16));
	    		temp.setCodigoGerenteAbanks(rs.getString(17));
	    		Calendar fechaSolicitud = Calendar.getInstance();
	    		fechaSolicitud.setTimeInMillis(rs.getTimestamp(18).getTime());
	    		temp.setFechaSolicitud(fechaSolicitud);
		    	
	    		List<RecaudoCreditoDTO> recaudosList = null;
	    		String detalleRecaudos = rs.getString(19);
	    		if(detalleRecaudos != null && !"".equals(detalleRecaudos)){
	    			RecaudoCreditoDTO tmp = null;
	    			StringTokenizer recaudos = new StringTokenizer(detalleRecaudos, "|");
	    			
	    			while (recaudos.hasMoreElements()) {
	    				if(recaudosList == null){
	    					recaudosList = new LinkedList<RecaudoCreditoDTO>();
	    				}
	    				
						StringTokenizer values = new StringTokenizer(recaudos.nextToken(), "->");
						
						tmp = new RecaudoCreditoDTO();
						tmp.setCodigoRecaudo(Integer.parseInt(values.nextToken()));
						tmp.setCodigoDocumento(Integer.parseInt(values.nextToken()));
						tmp.setNombreDocumento(values.nextToken());
						
						recaudosList.add(tmp);
					}
	    		}
	    		
	    		temp.setListadoRecaudos(recaudosList);
		    	result.add(temp);
		    	log.info("Registro consultado: " + temp);
		    }
		    
		    log.info("Finalizada preparacion de objetos con la informacion del core");
		} catch (Exception e) {
			// TODO: handle exception
			//log.error("Timestamp: " + rs.getString(18));
			log.error("Error ejecutando consumo de PKG del core bancario"
					+ (runInDemand ? " (en demanda)" : "")
					+ ".El error fue: " + e.getLocalizedMessage(), e);
			DelSurStructMailUtil.sendAbanksPKGError(props.getMailSupportAccounts(),
					e.getLocalizedMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
			try {
			    callStmt.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
			try {
			    conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
		return result;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		log.info("+ Iniciando proceso batch");
		
		try {
			//nos conectamos al core para traer los registros
			List<CreditoDTO> creditos = readFromAbanksDataBase();
			
			//creamos la localidad principal si no existe
			int creditLocationId = DelSurStructuraUtil.crearLocalidadCredito(props);
			if(creditLocationId > 0){
				log.info("Localidad principal creada de manera exitosa");
				
				//con los registros obtenidos, creamos el arbol de la estructura
				DelSurStructuraUtil.procesarNodosYRecaudos(creditos,
						props,
						creditLocationId,
						carpetaParaDigitalizados);
				
				//una vez creado el arbol
				//procedemos a cargar los documentos como digitalizados (importar)
			} else {
				log.error("No pudo ser creada o referenciada la localidad de los expedientes de creditos. "
						+ " Debemos detener el proceso");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Se produjo un error durante la ejecucion del proceso batch"
					+ ". El error fue: " + e.getMessage(), e);
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(cal.getTimeInMillis() + props.getDelayEjecucionesBatch());
		
		log.info("Proxima ejecucion del proceso batch sera aproximadamente el: " + cal.getTime());
		log.info("- Finalizado proceso batch");
	}
}
