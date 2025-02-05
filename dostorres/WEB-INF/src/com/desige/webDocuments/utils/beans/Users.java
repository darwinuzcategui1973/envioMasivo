package com.desige.webDocuments.utils.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.desige.webDocuments.area.forms.Area;
import com.desige.webDocuments.cargo.forms.Cargo;
import com.desige.webDocuments.sacop.forms.plantilla1;

/**
 * Title: Users.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>23/03/2004 (NC) Creation </li>
 *      <li>06/06/2004 (NC) Addee new field idLanguaje</li>
 </ul>
 */

public class Users extends SuperBeans implements Serializable {
    static final long serialVersionUID = -8971762053013052919L;
    private String user;
    private String clave;
    private String pass;
    private String nameUser;
    private String namePerson;
    private String email;
    private int numRecord;
    private String language;
    private String country;
    private String idLanguaje;
    private String idGroup;
    private String lastTime;
    private long idPerson;
    private boolean especialSession = false;
    private Date lastDatePass;
    private Date lastDatePassEdit;
    private String lastEdit;
    private int edit;
    private String lastLogin;
    private Area area;
    private Cargo cargo;
    private UserRecord userRecord;
    private int modo = 0;
    private int lado = 0;
    private int ppp = 0;
    private int panel = 0;
    private int separar = 0;
    private int pagina = 0;
    private int minimo = 0;
    private int typeDocuments = 0;
    private int ownerTypeDoc = 0;
    private int idNodeDigital = 0;
    private int idNodeService = 0;
    private int typesetter = 0;
    private int checker = 0;
    private String lote;
    private String correlativo;
    private String javaWebStart;
    private ArrayList consecutivo = new ArrayList();
	//ydavila Ticket 001-00-003023
	private String respsolcambio;
	private String respsolelimin;
	private String respsolimpres;
    private String id;
    
    private plantilla1 planillaSacop = null; 
    
    /**
     * Este atributo nos permite indicar si deseamos abrir un nuevo frame, luego de que
     * el usuario realize la accion de login.
     * 
     * Aplica para los casos en los que se desea ver un documento, a trav�s del link del correo
     * pero el usuario no tiene ninguna sesion activa en el sistema.
     * 
     */
    private String actionToInvokeAfterLogin;
    
    public Users(){
        setEspecialSession(false);
    }

    public Users(String user,String pass,String email){
        setUser(user);
        setPass(pass);
        setEmail(email);
        setEspecialSession(false);
    }
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String user) {
        this.nameUser = user;
    }

    public String getNamePerson() {
        return namePerson;
    }

    public void setNamePerson(String namePerson) {
        this.namePerson = namePerson;
    }

    public int getNumRecord() {
        return numRecord;
    }

    public void setNumRecord(int numRecord) {
        this.numRecord = numRecord;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIdLanguaje() {
        return idLanguaje;
    }

    public void setIdLanguaje(String idLanguaje) {
        this.idLanguaje = idLanguaje;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }
    
	//ydavila Ticket 001-00-003023

	public String getrespsolcambio() {
		return respsolcambio;
	}
	public String getrespsolelimin() {
		return respsolcambio;
	}
	public String getrespsolimpres() {
		return respsolcambio;
	}
	public void setrespsolcambio(String respsolcambio) {
		this.respsolcambio = respsolcambio;
	}
	public void setrespsolelimin(String respsolelimin) {
		this.respsolelimin = respsolelimin;
	}
	public void setrespsolimpres(String respsolimpres) {
		this.respsolimpres = respsolimpres;
	}

	
    public plantilla1 getPlanillaSacop() {
		return planillaSacop;
	}

	public void setPlanillaSacop(plantilla1 planillaSacop) {
		this.planillaSacop = planillaSacop;
	}

	public String toString() {
        StringBuffer result = new StringBuffer(100);
        result.append("user: ").append(getUser()).append(" lastTime: ").append(getLastTime());
        result.append(" typesetter: ").append(getTypesetter()).append(" checker: ").append(getChecker());
        result.append(" ownertypedoc: ").append(getOwnerTypeDoc()).append(" typedocuments: ").append(getTypeDocuments());
        result.append(" idnodedigital: ").append(getIdNodeDigital()).append(" lote: ").append(getLote());
        result.append(" idNodeService: ").append(getIdNodeService());
        result.append(" idGroup: ").append(getIdGroup());
        
        return result.toString();
    }

    public long getIdPerson() {
        return idPerson;
    }

    public void setIdPerson(long idPerson) {
        this.idPerson = idPerson;
    }

    public boolean isEspecialSession() {
        return especialSession;
    }

    public void setEspecialSession(boolean especialSession) {
        this.especialSession = especialSession;
    }

    public Date getLastDatePass() {
        return lastDatePass;
    }

    public void setLastDatePass(Date lastDatePass) {
        this.lastDatePass = lastDatePass;
    }

    public Date getLastDatePassEdit() {
        return lastDatePassEdit;
    }

    public void setLastDatePassEdit(Date lastDatePassEdit) {
        this.lastDatePassEdit = lastDatePassEdit;
    }

    public int getEdit() {
        return edit;
    }

    public void setEdit(int edit) {
        this.edit = edit;
    }

    public String getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(String lastEdit) {
        this.lastEdit = lastEdit;
    }
    
    public Area getArea(){
    	return area;
    }
    
    public void setArea(Area area){
    	this.area = area;
    }

    public Cargo getCargo(){
    	return cargo;
    }
    
    public void setCargo(Cargo cargo){
    	this.cargo = cargo;
    }

	public UserRecord getUserRecord() {
		return userRecord;
	}

	public void setUserRecord(UserRecord userRecord) {
		this.userRecord = userRecord;
	}

	public int getLado() {
		return lado;
	}

	public void setLado(int lado) {
		this.lado = lado;
	}

	public int getModo() {
		return modo;
	}

	public void setModo(int modo) {
		this.modo = modo;
	}

	public int getPanel() {
		return panel;
	}

	public void setPanel(int panel) {
		this.panel = panel;
	}

	public int getPpp() {
		return ppp==0?300:ppp;
	}

	public void setPpp(int ppp) {
		this.ppp = ppp;
	}

	public int getMinimo() {
		return minimo==0?770:minimo;
	}

	public void setMinimo(int minimo) {
		this.minimo = minimo;
	}

	public int getPagina() {
		return pagina;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
	}

	public int getSeparar() {
		return separar;
	}

	public void setSeparar(int separar) {
		this.separar = separar;
	}

	public int getTypeDocuments() {
		return typeDocuments;
	}

	public void setTypeDocuments(int typeDocuments) {
		this.typeDocuments = typeDocuments;
	}

	public int getChecker() {
		return checker;
	}

	public void setChecker(int checker) {
		this.checker = checker;
	}

	public int getIdNodeDigital() {
		return idNodeDigital;
	}

	public void setIdNodeDigital(int idNodeDigital) {
		this.idNodeDigital = idNodeDigital;
	}
	
	public int getIdNodeService() {
		return idNodeService;
	}

	public void setIdNodeService (int idNodeService) {
		this.idNodeService = idNodeService;
	}


	public int getOwnerTypeDoc() {
		return ownerTypeDoc;
	}

	public void setOwnerTypeDoc(int ownerTypeDoc) {
		this.ownerTypeDoc = ownerTypeDoc;
	}

	public int getTypesetter() {
		return typesetter;
	}

	public void setTypesetter(int typesetter) {
		this.typesetter = typesetter;
	}

	public String getLote() {
		return lote;
	}

	public void setLote(String lote) {
		this.lote = lote;
	}

	public String getCorrelativo() {
		return correlativo;
	}

	public void setCorrelativo(String correlativo) {
		this.correlativo = correlativo;
	}

	public ArrayList getConsecutivo() {
		return consecutivo;
	}

	public void setConsecutivo(ArrayList consecutivo) {
		this.consecutivo = consecutivo;
	}
	
	public String getActionToInvokeAfterLogin() {
		return actionToInvokeAfterLogin;
	}
	
	public void setActionToInvokeAfterLogin(String actionToInvokeAfterLogin) {
		this.actionToInvokeAfterLogin = actionToInvokeAfterLogin;
	}

	public String getJavaWebStart() {
		return javaWebStart;
	}

	public void setJavaWebStart(String javaWebStart) {
		this.javaWebStart = javaWebStart;
	}
	
}
