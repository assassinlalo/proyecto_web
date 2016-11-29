/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author lalo
 */
@Named(value = "master")
@SessionScoped
public class Master_MB implements Serializable {

    /**
     * Creates a new instance of master
     */
    public Master_MB() {
    }
    
    public void inicio() throws IOException{
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(ec.getRequestContextPath()+"/faces/index.xhtml");
    }
    
}
