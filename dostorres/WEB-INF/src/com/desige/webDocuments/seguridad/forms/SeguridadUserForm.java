package com.desige.webDocuments.seguridad.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: SeguridadUserForm.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Roger Rodríguez
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li> 09/08/2004 (RR) Creation </li>
 *      <li> 09/08/2004 (SR) sE CREA EL BEAN  isDocument </li>
 *      <li> 21/04/2006 (NC) Add field search in the security profile user and group </li>
 *      <li> 30/06/2006 (NC) New constructor add </li>
 </ul>
 */
public class SeguridadUserForm extends SuperActionForm implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 702405853326967238L;
	
	private String nameUser;
    private String Nombres;                               
    private String mail;
    private String idGrupo;
    private String nombreGrupo;
    private String descripcionGrupo;
    private int estructura;
    private int flujos;
    private int mensajes;
    private int administracion;
    private int perfil;
    private int sacop;
    private int search;
    private int toImpresion;
    private long idPerson;
    private String isDocument;
    private int record;
    private int files;
    private int digital;
    
    public int getRecord() {
        return record;
	 }
	 
    public void setRecord(int record) {
        this.record= record;
	 }
 
    public int getSacop() {
            return sacop;
     }
     public void setSacop(int sacop) {
            this.sacop= sacop;
     }

    public int getToImpresion() {
            return toImpresion;
        }
        public void setToImpresion(int toImpresion) {
            this.toImpresion = toImpresion;
        }

    public void setIsDocument(String  isDocument) {
        this.isDocument = isDocument;
    }

    public String  getIsDocument() {
        return isDocument;
    }

    public SeguridadUserForm() {
        setEstructura(1);
        setPerfil(1);
        setAdministracion(1);
        setFlujos(1);
        setMensajes(1);
        setSacop(1);
        setSearch(1);
        setToImpresion(1);
        setRecord(1);
        setFiles(1);
    }
    
    public SeguridadUserForm(int valor) {
        setEstructura(valor);
        setPerfil(valor);
        setAdministracion(valor);
        setFlujos(valor);
        setMensajes(valor);
        setSacop(valor);
        setSearch(valor);
        setToImpresion(valor);
        setRecord(valor);
        setFiles(valor);
    }
    

    public SeguridadUserForm(String nameUser,boolean isGroupHerency) {
        setNameUser(nameUser);
        if (isGroupHerency) {
            setEstructura(2);
            //setPerfil(2);
            setAdministracion(2);
            setFlujos(2);
            setMensajes(2);
            setSacop(2);
            setSearch(2);
            setToImpresion(2);
            setRecord(2);
            setFiles(2);
            setDigital(2);
        } 
        
        setPerfil(0);
    }

    public void cleanForm(){
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(String idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getDescripcionGrupo() {
        return descripcionGrupo;
    }

    public void setDescripcionGrupo(String descripcionGrupo) {
        this.descripcionGrupo = descripcionGrupo;
    }

    public int getEstructura() {
        return estructura;
    }

    public void setEstructura(int estructura) {
        this.estructura = estructura;
    }

    public int getFlujos() {
        return flujos;
    }

    public void setFlujos(int flujos) {
        this.flujos = flujos;
    }

    public int getMensajes() {
        return mensajes;
    }

    public void setMensajes(int mensajes) {
        this.mensajes = mensajes;
    }

    public int getAdministracion() {
        return administracion;
    }

    public void setAdministracion(int administracion) {
        this.administracion = administracion;
    }

    public int getPerfil() {
        return perfil;
    }

    public void setPerfil(int perfil) {
        this.perfil = perfil;
    }

    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }

    public String getNombres() {
        return Nombres;
    }

    public void setNombres(String nombres) {
        Nombres = nombres;
    }


    public long getIdPerson() {
        return idPerson;
    }

    public void setIdPerson(long idPerson) {
        this.idPerson = idPerson;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o instanceof SeguridadUserForm) {
            SeguridadUserForm oParam = (SeguridadUserForm)o;
            if (!ToolsHTML.isEmptyOrNull(oParam.getNameUser())) {
                if (oParam.getNameUser().equalsIgnoreCase(this.getNameUser())) {
                    return true;
                }
            } else {
                if (oParam.getIdGrupo().equalsIgnoreCase(this.getIdGrupo())) {
                    return true;
                }
            }
        }
        return false;
    }

    public int hashCode() {
    	if(this.getIdGrupo() == null){
    		return super.hashCode();
    	} else {
    		return this.getIdGrupo().hashCode();
    	}
    }

    public int getSearch() {
        return search;
    }

    public void setSearch(int search) {
        this.search = search;
    }

	public int getFiles() {
		return files;
	}

	public void setFiles(int files) {
		this.files = files;
	}

	public int getDigital() {
		return digital;
	}

	public void setDigital(int digital) {
		this.digital = digital;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SeguridadUserForm [nameUser=").append(nameUser)
				.append(", Nombres=").append(Nombres).append(", mail=")
				.append(mail).append(", idGrupo=").append(idGrupo)
				.append(", nombreGrupo=").append(nombreGrupo)
				.append(", descripcionGrupo=").append(descripcionGrupo)
				.append(", estructura=").append(estructura).append(", flujos=")
				.append(flujos).append(", mensajes=").append(mensajes)
				.append(", administracion=").append(administracion)
				.append(", perfil=").append(perfil).append(", sacop=")
				.append(sacop).append(", search=").append(search)
				.append(", toImpresion=").append(toImpresion)
				.append(", idPerson=").append(idPerson).append(", isDocument=")
				.append(isDocument).append(", record=").append(record)
				.append(", files=").append(files).append(", digital=")
				.append(digital).append("]");
		return builder.toString();
	}
    
    
}
