package com.focus.qweb.to;

import com.desige.webDocuments.accion.forms.ActionRecommended;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoTO;

/**
 * Created by IntelliJ IDEA.
 * User: Administrador
 * Date: 20/03/2006
 * Time: 10:29:06 AM
 * To change this template use File | Settings | File Templates.
 */

public class ActionRecommendedTO  implements ObjetoTO {
	
	private static final long serialVersionUID = 1L;

	private String idActionRecommended;
    private String descActionRecommended;
    private String idRegisterClass;
    
    public ActionRecommendedTO() {
    	
    }

    public ActionRecommendedTO(ActionRecommended p) {
    	this.setIdActionRecommended(String.valueOf(p.getIdActionRecommended()));
    	this.setDescActionRecommended(p.getDescActionRecommended());
    	this.setIdRegisterClass(String.valueOf(p.getIdRegisterClass()));
    }

    public String getIdActionRecommended() {
		return idActionRecommended;
	}
    public int getIdActionRecommendedInt() {
		return ToolsHTML.parseInt(idActionRecommended);
	}

	public void setIdActionRecommended(String idActionRecommended) {
		this.idActionRecommended = idActionRecommended;
	}

	public String getDescActionRecommended() {
		return descActionRecommended;
	}

	public void setDescActionRecommended(String descActionRecommended) {
		this.descActionRecommended = descActionRecommended;
	}

	public String getIdRegisterClass() {
		return idRegisterClass;
	}
	
	public int getIdRegisterClassInt() {
		return ToolsHTML.parseInt(idRegisterClass);
	}
	

	public void setIdRegisterClass(String idRegisterClass) {
		this.idRegisterClass = idRegisterClass;
	}

}