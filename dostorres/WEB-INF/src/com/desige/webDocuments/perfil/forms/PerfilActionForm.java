package com.desige.webDocuments.perfil.forms;

import java.io.Serializable;

import com.desige.webDocuments.cargo.forms.Cargo;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.to.PersonTO;

/**
 * Title: PerfilActionForm.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simón Rodríguez. (SR)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>28/03/2004 (NC) Creation </li>
 *      <li>02/08/2005 (SR) Se creo bean repclave </li>
 *      <li> 22/11/2005 (NC) Added selected field </li>
 </ul>
 */
public class PerfilActionForm extends SuperActionForm implements Serializable {
    private Long id;
    private String user;
    private String clave;
    private String clavenueva;
    private String repclave;
    private String idGrupo;
    private String apellidos;
    private String nombres;
    private String cargo;
    private String email;
    private String direccion;
    private String ciudad;
    private String estado;
    private String pais;
    private String zip;
    private String telefono;
    private String ext;
    private String idioma;
    private String namePais;
    private String numRecordPages;
    private String nameCity;
    private String nameState;
    private String lengthPass;
    private String area;
    private boolean selected;
    private Cargo dataCargo;
    private Integer modo = 0;
    private Integer lado = 0;
    private Integer ppp = 0;
    private Integer panel = 0;
    private Integer idNodeService = 0; 
    
    public PerfilActionForm() {
    	
    }

    public PerfilActionForm(PersonTO oPersonTO) {
    	if(oPersonTO!=null) {
	        this.id = Long.parseLong(oPersonTO.getId()==null?"0":oPersonTO.getId());
	        this.user = oPersonTO.getNameUser();
	        this.clave = oPersonTO.getClave();
	        this.clavenueva = oPersonTO.getClave();
	        this.repclave = oPersonTO.getClave();
	        this.idGrupo = oPersonTO.getIdGrupo();
	        this.apellidos = oPersonTO.getApellidos();
	        this.nombres = oPersonTO.getNombres();
	        this.cargo = oPersonTO.getCargo();
	        this.email = oPersonTO.getEmail();
	        this.direccion = oPersonTO.getDireccion();
	        this.ciudad = oPersonTO.getCiudad();
	        this.estado = oPersonTO.getEstado();
	        this.pais = oPersonTO.getCodPais();
	        this.zip = oPersonTO.getCodigoPostal();
	        this.telefono = oPersonTO.getTlf();
	        this.ext = "";
	        this.idioma = oPersonTO.getIdLanguage();
	        this.namePais = oPersonTO.getCodPais();
	        this.numRecordPages = oPersonTO.getNumRecordPages();
	        this.nameCity = oPersonTO.getCiudad();
	        this.nameState = oPersonTO.getEstado();
	        this.lengthPass = "10"; 
	        this.area = oPersonTO.getIdarea();
	        this.selected = false;
	        //private Cargo dataCargo;
	        this.modo = ToolsHTML.parseInt(oPersonTO.getModo());
	        this.lado = ToolsHTML.parseInt(oPersonTO.getLado());
	        this.ppp = ToolsHTML.parseInt(oPersonTO.getPpp());
	        this.panel = ToolsHTML.parseInt(oPersonTO.getPanel());
	        this.idNodeService = ToolsHTML.parseInt(oPersonTO.getIdNodeService());
	        
    	}
    }
    
    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

     public String getRepclave() {
        return repclave;
    }

    public void setRepclave(String repclave) {
        this.repclave = repclave;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getTelefono() {
        return telefono!=null?telefono.trim():telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono!=null?telefono.trim():telefono;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getNamePais() {
        return namePais;
    }

    public void setNamePais(String namePais) {
        this.namePais = namePais;
    }

    public String getNumRecordPages() {
        return numRecordPages;
    }

    public void setNumRecordPages(String numRecordPages) {
        this.numRecordPages = numRecordPages;
    }

    public String getNameCity() {
        return nameCity;
    }

    public void setNameCity(String nameCity) {
        this.nameCity = nameCity;
    }

    public String getNameState() {
        return nameState;
    }

    public void setNameState(String nameState) {
        this.nameState = nameState;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(String idGrupo) {
        this.idGrupo = idGrupo;
    }
    public void cleanForm(){
        setClave("");
        setUser("");
        setClavenueva("");
        }

    public String getClavenueva() {
        return clavenueva;
    }

    public void setClavenueva(String clavenueva) {
        this.clavenueva = clavenueva;
    }
    public String getLengthPass() {
        return lengthPass;
    }

    public void setLengthPass(String lengthPass) {
        this.lengthPass = lengthPass;
    }

    public String getCargo() {
        if (dataCargo!=null) {
            return dataCargo.getCargo();
        }
        return this.cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
    public boolean equals(Object object) {
        if (!(object instanceof PerfilActionForm)) {
            return false;
        }
        PerfilActionForm user = (PerfilActionForm) object;
        return (user.getId().equals(this.getId()));
    }

    public int hashCode() {
        return this.getId().hashCode();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Cargo getDataCargo() {
        return dataCargo;
    }

    public void setDataCargo(Cargo dataCargo) {
        this.dataCargo = dataCargo;
    }

    public String toString() {
        return this.getNombres() + "_" + this.getApellidos();// + "_" + this.getCargo();
    }

	public Integer getLado() {
		return lado;
	}

	public void setLado(Integer lado) {
		this.lado = lado;
	}

	public Integer getModo() {
		return modo;
	}

	public void setModo(Integer modo) {
		this.modo = modo;
	}

	public Integer getPanel() {
		return panel;
	}

	public void setPanel(Integer panel) {
		this.panel = panel;
	}

	public Integer getPpp() {
		return ppp;
	}

	public void setPpp(Integer ppp) {
		this.ppp = ppp;
	}
	
	
	
	public Integer getIdNodeService() {
		return idNodeService;
	}

	public void setIdNodeService(Integer idNodeService) {
		this.idNodeService = idNodeService;
	}


}
