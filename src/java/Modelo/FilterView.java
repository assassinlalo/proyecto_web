/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Control.CitaFacade;
import Control.CitaPojo;
import Entidad.Citas;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpSession;

/**
 *
 * @author David
 */
@Named(value = "dtFilterView")
@RequestScoped
public class FilterView implements Serializable {
     
    private List<Citas> citas;
    private Citas appointment;
     int id;
    private List<Citas> filteredCitas;
    private HtmlDataTable htmlDataTable;
    private CitaPojo selectedLista; 
    private List<CitaPojo> citasP;
    
    HttpSession session;
     
     FacesContext   context = FacesContext.getCurrentInstance();
    @ManagedProperty("#{citaList}")
    private CitaFacade list = new CitaFacade();
 
    @PostConstruct
    public void init() {
        if(list.getAllCitas() != null){
            setCitasP(list.getAllCitas());
        }
    }
    
    public void validaPagina() throws IOException{
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        
        HttpSession session = (HttpSession) ec.getSession(false);
        if(session.getAttribute("Admin") == null){
            ec.redirect(ec.getRequestContextPath()+"/faces/index.xhtml");
        }
        if(!(boolean) session.getAttribute("Admin")){
            ec.redirect(ec.getRequestContextPath()+"/faces/index.xhtml");
        }
    }
      


    public void deleteCita() throws Exception{
        System.out.println("Hola");
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
         if (selectedLista.getIdCita() == 0) {
            context.addMessage("growlMessage", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Seleccione una cita"));
        } else if (list.deleteC(selectedLista.getIdCita())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Se elimino correctamente!"));
            session = (HttpSession) ec.getSession(false);
            session.setAttribute("Admin", true);
            ec.getFlash().setKeepMessages(true);
            ec.redirect(ec.getRequestContextPath()+"/faces/Admin/ver_citas.xhtml");
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar el registro."));
        }
     }
    
     public void setSelected(ValueChangeEvent event) {
         
//         int index = getHtmlDataTable().getRowIndex(); 
         
         appointment=(Citas)getHtmlDataTable().getRowData();
         id=appointment.getIdCita();
         System.out.println(id);
    }
     
     public List<CitaPojo> getCitasP() {
        return citasP;
    }

    /**
     * @param citasP the citasP to set
     */
    public void setCitasP(List<CitaPojo> citasP) {
        this.citasP = citasP;
    }
     
    public List<Citas> getCars() {
        return getCitas();
    }
 
    public List<Citas> getFilteredCars() {
        return getFilteredCitas();
    }
 
    public void setFilteredCars(List<Citas> filteredCars) {
        this.setFilteredCitas(filteredCars);
    }

    /**
     * @return the citas
     */
    public List<Citas> getCitas() {
        return citas;
    }

    /**
     * @param citas the citas to set
     */
    public void setCitas(List<Citas> citas) {
        this.citas = citas;
    }

    /**
     * @return the filteredCitas
     */
    public List<Citas> getFilteredCitas() {
        return filteredCitas;
    }

    /**
     * @param filteredCitas the filteredCitas to set
     */
    public void setFilteredCitas(List<Citas> filteredCitas) {
        this.filteredCitas = filteredCitas;
    }

    /**
     * @return the htmlDataTable
     */
    public HtmlDataTable getHtmlDataTable() {
        return htmlDataTable;
    }

    /**
     * @param htmlDataTable the htmlDataTable to set
     */
    public void setHtmlDataTable(HtmlDataTable htmlDataTable) {
        this.htmlDataTable = htmlDataTable;
    }

    /**
     * @return the selectedLista
     */
    public CitaPojo getSelectedLista() {
        return selectedLista;
    }

    /**
     * @param selectedLista the selectedLista to set
     */
    public void setSelectedLista(CitaPojo selectedLista) {
        this.selectedLista = selectedLista;
    }
 
   
}