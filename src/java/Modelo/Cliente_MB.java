/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Control.CitaFacade;
import Control.CitaPojo;
import Control.ClientePojo;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author lalo
 */
@Named(value = "cliente")
@SessionScoped
public class Cliente_MB implements Serializable {
    private Date today = new Date();
    
    private String fecha;
    private String observaciones;
    private String tipo;
    private String hora;
    
    private Date fechaForm;
    private Date horaForm;
    
    private ClientePojo clientePojo = new ClientePojo();
    HttpSession session;
    
    private final CitaFacade facadeCita = new CitaFacade();
    /**
     * Creates a new instance of Cliente_MB
     */
    public Cliente_MB() {
    }
    
    public void creaCita(){
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        FacesContext context = FacesContext.getCurrentInstance();
        CitaPojo citaPojo = new CitaPojo();
        fecha = new SimpleDateFormat("dd/MM/yyyy").format(fechaForm);
        citaPojo.setFecha(fecha);
        hora = new SimpleDateFormat("HH:mm").format(horaForm);
        citaPojo.setHora(hora);
        citaPojo.setObservaciones(observaciones);
        citaPojo.setTipo(tipo);
        session = (HttpSession) ec.getSession(false);
        if((String)session.getAttribute("Correo") != null){
            if(!facadeCita.diaCompleto(fecha)){
                if(!facadeCita.existeCita(fecha,hora)){
                    boolean creada = facadeCita.reservarCita(citaPojo, (String)session.getAttribute("Correo"));
                    if(creada){
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Exito","Se creo correctamente su cita:\n El día: "+fecha+"\n"
                                + "a la hora: "+hora));
                    }else{
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Exito","No se creo correctamente su cita:\n El día: "+fecha+"\n"
                                + "a la hora: "+hora));
                    }
                }else{
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error","Existe una cita para ese horario, por favor elige otro. "));
                }
            }else{
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error","Se llego al limite de 3 citas por día, intenta venir otro día. :) "));
            }
        }else{
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error","No se pudo validar la sesión por favor accede de nuevo. "));
        }
        
    }
    
    public void validaPagina() throws IOException{
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        
        session = (HttpSession) ec.getSession(false);
        if(session.getAttribute("Cliente") == null){
            ec.redirect(ec.getRequestContextPath()+"/faces/index.xhtml");
        }
        if(!(boolean) session.getAttribute("Cliente")){
            ec.redirect(ec.getRequestContextPath()+"/faces/index.xhtml");
        }
        
        setClientePojo((ClientePojo)session.getAttribute("pojoCliente"));
    }
    
    
    /**
     * @return the fecha
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the today
     */
    public Date getToday() {
        return today;
    }

    /**
     * @param today the today to set
     */
    public void setToday(Date today) {
        this.today = today;
    }

    /**
     * @return the observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * @param observaciones the observaciones to set
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the hora
     */
    public String getHora() {
        return hora;
    }

    /**
     * @param hora the hora to set
     */
    public void setHora(String hora) {
        this.hora = hora;
    }

    /**
     * @return the fechaForm
     */
    public Date getFechaForm() {
        return fechaForm;
    }

    /**
     * @param fechaForm the fechaForm to set
     */
    public void setFechaForm(Date fechaForm) {
        this.fechaForm = fechaForm;
    }

    /**
     * @return the horaForm
     */
    public Date getHoraForm() {
        return horaForm;
    }

    /**
     * @param horaForm the horaForm to set
     */
    public void setHoraForm(Date horaForm) {
        this.horaForm = horaForm;
    }

    /**
     * @return the clientePojo
     */
    public ClientePojo getClientePojo() {
        return clientePojo;
    }

    /**
     * @param clientePojo the clientePojo to set
     */
    public void setClientePojo(ClientePojo clientePojo) {
        this.clientePojo = clientePojo;
    }
    
}
