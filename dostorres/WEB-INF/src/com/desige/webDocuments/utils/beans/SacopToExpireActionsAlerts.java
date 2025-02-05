package com.desige.webDocuments.utils.beans;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.parameters.forms.BaseParametersForm;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.mail.SendMail;

/**
 * Clase para gestionar el envio de alertas de acciones de SACOPs que estan proximas a vencer.
 * 
 * @author frojas
 *
 */
public class SacopToExpireActionsAlerts extends TimerTask{
	private static final Logger log = LoggerFactory.getLogger(SacopToExpireActionsAlerts.class);
	private static final String query = "SELECT sacop.sacopnum, sacop.respblearea, sacop.solicitudinforma, accion.accion, p.email, p.nombres, p.apellidos"
			+ " FROM person p, tbl_planillasacop1 sacop, tbl_planillasacopaccion accion, tbl_sacopaccionporpersona axp"
			+ " WHERE sacop.idplanillasacop1 = accion.idplanillasacop1"
			+ " AND accion.idplanillasacopaccion = axp.idplanillasacopaccion"
			+ " AND p.idperson = axp.idperson"
			+ " AND sacop.estado = 3"
			+ " AND axp.firmo = 0"
			+ " AND sacop.active  = 1"
			+ (Constants.MANEJADOR_ACTUAL == Constants.MANEJADOR_MSSQL ? " AND CONVERT(datetime,accion.fecha,120) > ?" : " AND accion.fecha > ?")
			+ (Constants.MANEJADOR_ACTUAL == Constants.MANEJADOR_MSSQL ? " AND CONVERT(datetime,accion.fecha,120) < ?" : " AND accion.fecha < ?");
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		log.info("Inicio de envio de alertas de acciones SACOPS que estan por expirar");
		try {
			Calendar cal = Calendar.getInstance();
			
			if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
					|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
				log.info("Es fin de semana, no disparamos las alertas");
			} else {
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				cal.set(Calendar.MILLISECOND, 999);
				cal.add(Calendar.DAY_OF_MONTH, -1);
				
				BaseParametersForm form = new BaseParametersForm();
				HandlerParameters.load(form);
				
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				ps = con.prepareStatement(JDBCUtil.replaceCastMysql(query));
				switch(Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					ps.setDate(1, new Date(cal.getTimeInMillis()));
					break;
				case Constants.MANEJADOR_POSTGRES:
					ps.setTimestamp(1, new Timestamp(cal.getTimeInMillis()));
					break;
				case Constants.MANEJADOR_MYSQL:
					ps.setTimestamp(1, new Timestamp(cal.getTimeInMillis()));
					break;
				}
				
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				cal.add(Calendar.DAY_OF_MONTH, form.getDiasAlertaAccionesSacopPorVencer() + 2);
				switch(Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					ps.setDate(2, new Date(cal.getTimeInMillis()));
					break;
				case Constants.MANEJADOR_POSTGRES:
					ps.setTimestamp(2, new Timestamp(cal.getTimeInMillis()));
					break;
				case Constants.MANEJADOR_MYSQL:
					ps.setTimestamp(2, new Timestamp(cal.getTimeInMillis()));
					break;
				}
				rs = ps.executeQuery();
				
				while (rs.next()){
					//tengo una acción que según la configuración de dias está por vencer.
					//armo el texto del mensaje y ubico los usuarios que recibiran dicho correo
					ResourceBundle bundle = ToolsHTML.getBundle(null);
					String textMail = MessageFormat.format(bundle.getString("scp.mail.abouttoexpireactions.text"),
							new Object[]{rs.getString(6) + " " + rs.getString(7), rs.getString(1), rs.getString(4)});
					textMail += form.getMsgSacopAccionesPorVencer();
					
					String subject = MessageFormat.format(bundle.getString("scp.mail.abouttoexpireactions.subject"),
							new Object[]{rs.getString(4), rs.getString(1)});
					
					String[] ccArray = rs.getString(3).split(",");
					String cc = "";
					Users responsable = HandlerDBUser.load(rs.getInt(2), true);
					if(! ToolsHTML.isEmptyOrNull(responsable.getEmail())){
						cc = responsable.getEmail();
					}
					
					for (String ccId : ccArray) {
						Users tmp = HandlerDBUser.load(Long.parseLong(ccId), true);
						if(! ToolsHTML.isEmptyOrNull(tmp.getEmail())){
							if(! "".equals(cc)){
								cc += ";";
							}
							cc += tmp.getEmail();
						}
					}
					
					SendMail.sendMail(form.getSmtpMail(),
							form.getMailAccount(),
							responsable.getNamePerson(),
							rs.getString(5),
							cc,
							subject,
							textMail);
					
					log.info("Enviado mail de accion por expirar a [" 
							+ (rs.getString(6) + " " + rs.getString(7)) + "/" + rs.getString(5) + "]"
							+ ", (sacop/accion) (" + rs.getString(1) + "/" + rs.getString(4) + ")");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Error: " + e.getLocalizedMessage(), e);
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
			try {
				ps.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
			try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
		log.info("Fin de envio de alertas de acciones SACOPS que estan por expirar");
	}
}
