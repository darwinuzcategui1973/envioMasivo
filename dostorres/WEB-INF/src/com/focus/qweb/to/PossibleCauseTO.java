package com.focus.qweb.to;

import com.desige.webDocuments.causa.forms.PossibleCause;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoTO;

/**
 * Created by IntelliJ IDEA.
 * User: Administrador
 * Date: 20/03/2006
 * Time: 10:29:06 AM
 * To change this template use File | Settings | File Templates.
 */

public class PossibleCauseTO  implements ObjetoTO {
	
	private static final long serialVersionUID = 1L;

	private String idPossibleCause;
    private String descPossibleCause;
    
    public PossibleCauseTO() {
    	
    }

    public PossibleCauseTO(PossibleCause p) {
    	this.setIdPossibleCause(String.valueOf(p.getIdPossibleCause()));
    	this.setDescPossibleCause(p.getDescPossibleCause());
    }

    public String getIdPossibleCause() {
		return idPossibleCause;
	}

    public int getIdPossibleCauseInt() {
		return ToolsHTML.parseInt(idPossibleCause);
	}

    public void setIdPossibleCause(String idPossibleCause) {
		this.idPossibleCause = idPossibleCause;
	}

	public String getDescPossibleCause() {
		return descPossibleCause;
	}

	public void setDescPossibleCause(String descPossibleCause) {
		this.descPossibleCause = descPossibleCause;
	}

	
}