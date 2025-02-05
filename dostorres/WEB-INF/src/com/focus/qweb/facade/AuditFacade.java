package com.focus.qweb.facade;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import sun.jdbc.rowset.CachedRowSet;

import javax.servlet.http.HttpServletRequest;

import com.desige.webDocuments.parameters.forms.BaseParametersForm;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.utils.IDDBFactorySql;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.utils.mail.SendMail;
import com.focus.qweb.dao.AuditDAO;
import com.focus.qweb.dao.NormCeroDAO;
import com.focus.qweb.dao.PlanAuditDAO;
import com.focus.qweb.dao.ProgramAuditDAO;
import com.focus.qweb.dao.TransactionDAO;
import com.focus.qweb.to.AuditTO;
import com.focus.qweb.to.NormCeroAuditTO;
import com.focus.qweb.to.PlanAuditTO;
import com.focus.qweb.to.ProgramAuditTO;
import com.focus.qweb.to.TransactionTO;
import com.focus.request.NormAuditRequest;
import com.focus.request.PlanAuditRequest;
import com.focus.request.ProgramAuditRequest;

public class AuditFacade {

	private HttpServletRequest request = null;

	public AuditFacade(HttpServletRequest request) {
		this.request = request;
	}

	public static void insertarAuditFacade(ResourceBundle rb, String idTransaction, String detalle, long idUsuario, String nameUser, String valorInicial, String valorFinal, String terminal) {
		try {
			final TransactionTO oTransactionTO = new TransactionTO();
			TransactionDAO oTransactionDAO = new TransactionDAO();
			final String nameUserRegister = nameUser;
			final String textMail = rb.getString("transaction.title.mailText");
			final String nameUserNow = nameUser;

			oTransactionTO.setIdTransaction(Integer.parseInt(idTransaction));
			if (oTransactionDAO.cargar(oTransactionTO)) {

				if (oTransactionTO.getTraSave() == 1) {
					AuditTO oAuditTO = new AuditTO();
					AuditDAO oAuditDAO = new AuditDAO();

					oAuditTO.setIdTransaction(idTransaction);
					oAuditTO.setDetalle(detalle);
					oAuditTO.setIdUsuario(String.valueOf(idUsuario));
					oAuditTO.setValorInicial(valorInicial);
					oAuditTO.setValorFinal(valorFinal);
					oAuditTO.setTerminal(terminal);

					oAuditDAO.insertar(oAuditTO);
				}
				if (oTransactionTO.getTraNotify() == 1) {
					(new Thread(new Runnable() {
						public void run() {
 						    //sendMail(String smtpMail, String from, String nameUser,String to, String cc, String asunto, String texto) throws Exception {
							try {
								StringBuilder text = new StringBuilder();
								text.append(textMail.replaceAll("<username>",nameUserNow));
								
								
								BaseParametersForm form = new BaseParametersForm();
								HandlerParameters.load(form);
								SendMail.sendMail(form.getSmtpMail(), form.getMailAccount(), nameUserRegister, form.getMailAccount(), null, oTransactionTO.getTraDescrip() , text.toString());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					})).start();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insertarAuditFacade(AuditTO oAuditTO) {
		try {
			AuditDAO oAuditDAO = new AuditDAO();
			oAuditDAO.insertar(oAuditTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<AuditTO> listarAuditFacade() throws Exception {
		ArrayList<AuditTO> lista = null;
		AuditDAO oAuditDAO = new AuditDAO();

		lista = oAuditDAO.listar();

		return lista;
	}
	
	public List<ProgramAuditRequest> listarProgramFacade(boolean isPlanActive) {
		List<ProgramAuditRequest> list = new ArrayList<ProgramAuditRequest>();
		ProgramAuditDAO oProgramAuditDAO = new ProgramAuditDAO();
		try {
			List<ProgramAuditTO> registros = isPlanActive?oProgramAuditDAO.listarActive():oProgramAuditDAO.listar();
			
			ProgramAuditTO pro;
			for(int i=0; i<registros.size(); i++) {
				pro = registros.get(i);
				list.add(new ProgramAuditRequest(pro.getIdProgramAudit(), pro.getNameProgram(), pro.getIdNorm()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List<ProgramAuditRequest> listarProgramFacadeId(boolean isPlanActive,String normas) {
		List<ProgramAuditRequest> list = new ArrayList<ProgramAuditRequest>();
		ProgramAuditDAO oProgramAuditDAO = new ProgramAuditDAO();
		try {
			List<ProgramAuditTO> registros = isPlanActive?oProgramAuditDAO.listarActiveNormas(normas):oProgramAuditDAO.listar();
			
			ProgramAuditTO pro;
			for(int i=0; i<registros.size(); i++) {
				pro = registros.get(i);
				list.add(new ProgramAuditRequest(pro.getIdProgramAudit(), pro.getNameProgram(), pro.getIdNorm()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<PlanAuditRequest> listarPlanFacade() {
		List<PlanAuditRequest> list = new ArrayList<PlanAuditRequest>();
		PlanAuditDAO oPlanAuditDAO = new PlanAuditDAO();
		try {
			List<PlanAuditTO> registros = oPlanAuditDAO.listar();
			
			PlanAuditTO pro;
			for(int i=0; i<registros.size(); i++) {
				pro = registros.get(i);
				list.add(new PlanAuditRequest(pro.getIdPlanAudit(), pro.getNamePlan(), pro.getIdProgramAudit(), pro.getIdNorm()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List<PlanAuditRequest> listarPlanFacadeId(String normas) {
		List<PlanAuditRequest> list = new ArrayList<PlanAuditRequest>();
		PlanAuditDAO oPlanAuditDAO = new PlanAuditDAO();
		try {
			List<PlanAuditTO> registros = oPlanAuditDAO.listarByIdNorma(normas);
			
			PlanAuditTO pro;
			for(int i=0; i<registros.size(); i++) {
				pro = registros.get(i);
				list.add(new PlanAuditRequest(pro.getIdPlanAudit(), pro.getNamePlan(), pro.getIdProgramAudit(), pro.getIdNorm()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	//NormCeroDAO
	public List<NormAuditRequest> listarNormaCeroFacade() {
		List<NormAuditRequest> list = new ArrayList<NormAuditRequest>();
		NormCeroDAO oNormCeroDAO = new NormCeroDAO();
		/*
		try {
			List<NormCeroAuditTO> registros = oNormCeroDAO.listar();
			
			NormCeroAuditTO pro;
			for(int i=0; i<registros.size(); i++) {
				pro = registros.get(i);
				list.add(new NormAuditRequest(pro.getIdNormAudit(), pro.getNameNorma(), pro.getIdNormIndiceCero()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		return list;
		
	}
	
	public static void insertarCambiosAuditFacade(ResourceBundle rb, Users user, CachedRowSet parametersOld, CachedRowSet parameterNew, String ipUser) {
		try {
			
			if( parametersOld.next() && parameterNew.next() ) {
				Iterator<String> ite = TransactionTO.LIST_FIELDS_PARAMETERS.keySet().iterator();
				StringBuilder initValue = new StringBuilder();
				StringBuilder finalValue = new StringBuilder();
				String oldValue = null;
				String newValue = null;
				
				while(ite.hasNext()) {
					String fieldName = ite.next();
					
					try {
						oldValue = String.valueOf(parametersOld.getString(fieldName));
						newValue = String.valueOf(parameterNew.getString(fieldName));
					} catch(Exception e) {
						continue;
					}
					
					if(!oldValue.equals(newValue)) {
						System.out.println(fieldName);
						
						initValue.setLength(0);
						finalValue.setLength(0);
						
						if(fieldName.equals("mail_auth_password")) {
							initValue.append("[").append(fieldName).append("] ").append(oldValue.substring(0,Math.min(2,oldValue.length())).concat("**********"));
							finalValue.append(newValue.substring(0,Math.min(2,oldValue.length())).concat("**********"));
						} else {
							initValue.append("[").append(fieldName).append("] ").append(oldValue);
							finalValue.append(newValue);
						}
						
						AuditFacade.insertarAuditFacade(rb, TransactionTO.LIST_FIELDS_PARAMETERS.get(fieldName), "", user.getIdPerson(), user.getNameUser(), 
								initValue.toString(), finalValue.toString(), ipUser );

					}
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

}
