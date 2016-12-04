/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.IOException;
import javax.inject.Named;
import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author lalo
 */
@Named(value = "master")
@RequestScoped
public class Master_MB implements Serializable {

    HttpSession session;
    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();

    /**
     * Creates a new instance of master
     */
    public Master_MB() {
    }
    
    public void cerrar_sesion() throws IOException {
        session = (HttpSession) ec.getSession(false);
        session.invalidate();
        ec.redirect(ec.getRequestContextPath() + "/faces/index.xhtml");
    }

}
