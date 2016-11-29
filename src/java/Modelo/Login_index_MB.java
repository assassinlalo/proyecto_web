/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Control.UserTempFacade;
import Control.UserTempPojo;
import Control.UsuarioFacade;
import Control.UsuarioPojo;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author lalo
 */
@Named(value = "login")
@SessionScoped
public class Login_index_MB implements Serializable {

    private UserTempFacade facade  = new UserTempFacade();
    private UsuarioFacade facadeUser = new UsuarioFacade();
    private Integer id;
    private String login;
    private String pwd;
    private String hash;
    private String correo;
    
    private UsuarioPojo userPojo;
    HttpSession session;
    /**
     * Creates a new instance of Login_index_MB
     */
    public Login_index_MB() {
    }
    
    // Slider
    
    private List<String> images;
     
    @PostConstruct
    public void init() {
        images = new ArrayList<String>();
        for (int i = 1; i <= 2; i++) {
            images.add("slide_" + i + ".jpg");
        }
    }
    public void preRegistro() throws IOException{
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(ec.getRequestContextPath()+"/faces/views/pre_registro.xhtml");
    }
    
    public void submit() throws IOException{
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        FacesContext context = FacesContext.getCurrentInstance();
//        HttpServletRequest request = (HttpServletRequest) ec.getRequest();
        if(correo == null && hash == null){
            if((login != null) || pwd != null){
                userPojo = facadeUser.buscaUsuario(login, pwd);
                if(userPojo != null){
                    //reedirigir a la pagina correpondiente segun el tipo de usuario. 
                    //ec.redirect(ec.getRequestContextPath()+"/faces/Nuevo/registro.xhtml");
                }else{
                    context.addMessage("growlMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR,"Los datos no son válidos","El usuario: "+login+
                            ", no existe, por favor registrate."));
                }
            }else{
                context.addMessage("growlMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR,"Los datos no son válidos","Error"));
            }
        }else{
            if((login != null) && pwd != null){
                if(login.equals(correo)){
                    System.out.println("Contraseña recibida: "+pwd+"\n Correo Recibido: "+correo);
                    //context.addMessage("growlMensaje", new FacesMessage(FacesMessage.SEVERITY_INFO,"Hola","Los datos que se reciben son: "+correo+"   y "+hash));
                    UserTempPojo usuario = null;
                    try{
                        usuario = facade.consultarPreRegsitro(hash, pwd);
                    }catch(Exception ex){
                        
                    }
                    if(usuario != null){
                        try{
                            // Cambiar a activo en 1 a la tabla... 
                            String parametros = "?ap="+usuario.getAp()+"&am="+usuario.getAm()+"&nom="+usuario.getNombre()+"&co="+usuario.getCorreo();
                            ec.redirect(ec.getRequestContextPath()+"/faces/Nuevo/registro.xhtml"+parametros);
                        }catch(IOException ex){
                            System.out.println("Ocurrio un error al redirigir a la pagina de registro... ");
                        }
                    }else{
                        context.addMessage("growlMensaje", new FacesMessage(FacesMessage.SEVERITY_INFO,"Hola","Los datos no coinciden con la BD. "
                                + "Por favor intenta acceder de nuevo. "));
                    }
                }else{
                    context.addMessage("growlMensaje", new FacesMessage(FacesMessage.SEVERITY_INFO,"Hola","El correo introducido no coincide... "
                            + "intentalo de nuevo"));
                }
            }else{
                context.addMessage("growlMensaje", new FacesMessage(FacesMessage.SEVERITY_INFO,"Hola","Debes introducir datos para poder acceder. "
                            + "intentalo de nuevo"));
            }
        }
    }
 
    public List<String> getImages() {
        return images;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the login
     */
    public String getLogin() {
        return login;
    }

    /**
     * @param login the login to set
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * @return the pwd
     */
    public String getPwd() {
        return pwd;
    }

    /**
     * @param pwd the pwd to set
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @param hash the hash to set
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * @return the correo
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * @param correo the correo to set
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
