package com.desige.webDocuments.users.forms;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperActionForm;


/**
 * Title: LoginUser.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>23/03/2004 (NC) Creation </li>
 </ul>
 */


public class LoginUser extends SuperActionForm implements Serializable {
    static final long serialVersionUID = -4543694925489706834L;
    private String clave;
    private String nuevaclave;
    private String repclave;
    private String user;
    private String nombres;
    private String lengthPass;
    private String apellidos;
    private String email;
    private String grupo;
    private String cargo;
    private long idPerson;
    private String useremplazo;
    private String area;
    private int idNodeService;
   
    private boolean cambiocargo;
    public boolean getCambioCargo() {
        return cambiocargo;
    }
    public void setCambioCargo(boolean cambioCargo) {
        this.cambiocargo = cambioCargo;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

     public String getUseremplazo() {
        return useremplazo;
  }
  public void setUseremplazo(String useremplazo) {
        this.useremplazo = useremplazo;
  }

    public LoginUser(){

    }

    public LoginUser(String user,String clave){
        setUser(user);
        setClave(clave);
    }

    public String getClave() {
        return clave;
    }
    public void setClave(String clave) {
        this.clave = clave;
    }
  public String getUser() {
        return user;
  }
  public void setUser(String user) {
        this.user = user;
  }
    public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest request) {
        ResourceBundle rb = ToolsHTML.getBundle(request);
        if (!ToolsHTML.checkValue(this.user)){
        	request.getSession().setAttribute("error",rb.getString("notUser"));
        }
        if (!ToolsHTML.checkValue(this.clave)){
        	request.getSession().setAttribute("error,",rb.getString("notPass"));
            actionMapping.findForward("error");
        }
        return null;
    }
    public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public void cleanForm(){
        setClave("");
        setUser("");
        setGrupo("");
        setEmail("");
        setApellidos("");
        setNombres("");
        setCargo("-1");
    }

    public String getNuevaclave() {
        return nuevaclave;
    }

    public void setNuevaclave(String nuevaclave) {
        this.nuevaclave = nuevaclave;
    }

    public String getRepclave() {
        return repclave;
    }

    public void setRepclave(String repclave) {
        this.repclave = repclave;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
    public String getLengthPass() {
        return lengthPass;
    }

    public void setLengthPass(String lengthPass) {
        this.lengthPass = lengthPass;
    }

    public long getIdPerson() {
        return idPerson;
    }

    public void setIdPerson(long idPerson) {
        this.idPerson = idPerson;
    }
    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
    
    public int getIdNodeService() {
        return idNodeService;
    }

    public void setIdNodeService(int idNodeService) {
        this.idNodeService = idNodeService;
    }
}
