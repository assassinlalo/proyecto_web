/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Control.ClienteFacade;
import Control.ClientePojo;
import Control.UserTempFacade;
import Control.UsuarioFacade;
import Control.UsuarioPojo;
import java.io.File;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.stream.FileImageOutputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.primefaces.event.CaptureEvent;

/**
 *
 * @author lalo
 */
@Named(value = "nuevo")
@SessionScoped
public class Nuevo_MB implements Serializable {
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String nombre;
    private String correoE;
    private String foto;
    private String usuario;
    private String contrasena;
    private String confContrasena;
    
    private UserTempFacade userTempfacade  = new UserTempFacade();
    private UsuarioFacade userFacade = new UsuarioFacade();
    private ClienteFacade clienteFacade = new ClienteFacade();
    /**
     * Creates a new instance of Nuevo_MB
     */
    public Nuevo_MB() {
    }
    
    FacesContext context;

    public void registrar() throws IOException{
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        context = FacesContext.getCurrentInstance();
        String[] response = userTempfacade.consultarParaRegistrar(correoE, apellidoPaterno, apellidoMaterno, nombre);
        if(response[0].equals("0")){//success
            context.addMessage("growlMessage", new FacesMessage(FacesMessage.SEVERITY_INFO,"Exitoso",  response[1]) );
            if(contrasena.equals(confContrasena)){
                UsuarioPojo user;
                user = userFacade.buscaUsuario(correoE, contrasena);
                if(user == null){
                    UsuarioPojo userCreado = new UsuarioPojo();
                    userCreado.setUnombre(correoE);
                    userCreado.setUcontrasenia(contrasena);
                    boolean creoUsuario = userFacade.crearUsuario(userCreado);
                    if(creoUsuario){
                        userCreado = userFacade.buscaUsuario(correoE, contrasena);
                        if(userCreado != null){
                            //tendria que mandar a un facade de empleado para poder crearlo alla...
                            ClientePojo empPojo = new ClientePojo();
                            empPojo.setApellidoMaterno(apellidoMaterno);
                            empPojo.setApellidoPaterno(apellidoPaterno);
                            empPojo.setCorreoE(correoE);
                            empPojo.setNombre(nombre);
                            empPojo.setNombreFoto(foto);
                            if(clienteFacade.crearCliente(empPojo, correoE)){
                                // Aqui falta actualizar la tabla de empleados temporales para poner el estado en 1
                                // Esto para poder reedirigir y mostrar el mensaje de usuario creado.
                                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"El empleado ha sido creado",  "") );
                                ec.getFlash().setKeepMessages(true);
                                HttpSession session = (HttpSession) ec.getSession(true);
                                //Aqui se estaria reedirigiendo...
                                ec.redirect(ec.getRequestContextPath()+"/faces/index.xhtml");
                            }else{
                                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error",  "No se pudo crear el empleado, intenta más tarde") );
                            }
                        }
                    }else{
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error",  "No se pudo crear el usuario, intenta más tarde") );
                    }
                }else{
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error",  "Ya existe un usuario con el correo: "+correoE));
                }
            }else{
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error",  "Las contraseñas ingresadas no coinciden, por favor verificalas.") );
            }
        }else{ //Error
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error",  response[1]) );
        }
    }
     private String getRandomImageName() {
        int i = (int) (Math.random() * 10000000);
         
        return String.valueOf(i);
    }
    public void oncapture(CaptureEvent captureEvent) {
        foto = getRandomImageName();
        final byte[] datos = captureEvent.getData();
        
        ServletContext servletContext = (ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext();
        String newFileName = servletContext.getRealPath("") + File.separator + "resources" + File.separator +
                "fotos"+ File.separator + foto+ ".png";
         System.out.println(newFileName +"\n");
         
        FileImageOutputStream imageOutput;
//        FileImageOutputStream outputStream;
        try {
            imageOutput = new FileImageOutputStream(new File(newFileName));
            imageOutput.write(datos, 0, datos.length);
            imageOutput.close();
        }
        catch(IOException e) {
            throw new FacesException("Error in writing captured image.", e);
        }
    }

    /**
     * @return the apellidoPaterno
     */
    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    /**
     * @param apellidoPaterno the apellidoPaterno to set
     */
    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    /**
     * @return the apellidoMaterno
     */
    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    /**
     * @param apellidoMaterno the apellidoMaterno to set
     */
    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the correoE
     */
    public String getCorreoE() {
        return correoE;
    }

    /**
     * @param correoE the correoE to set
     */
    public void setCorreoE(String correoE) {
        this.correoE = correoE;
    }

    /**
     * @return the foto
     */
    public String getFoto() {
        return foto;
    }

    /**
     * @param foto the foto to set
     */
    public void setFoto(String foto) {
        this.foto = foto;
    }

    /**
     * @return the usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * @return the contrasena
     */
    public String getContrasena() {
        return contrasena;
    }

    /**
     * @param contrasena the contrasena to set
     */
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /**
     * @return the confContrasena
     */
    public String getConfContrasena() {
        return confContrasena;
    }

    /**
     * @param confContrasena the confContrasena to set
     */
    public void setConfContrasena(String confContrasena) {
        this.confContrasena = confContrasena;
    }
    
}
