/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Control.ClienteFacade;
import Control.ClientePojo;
import Control.RolesFacade;
import Control.UserTempFacade;
import Control.UserTempPojo;
import Control.UsuarioFacade;
import Control.UsuarioPojo;
import java.io.File;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.stream.FileImageOutputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.primefaces.event.CaptureEvent;

/**
 *
 * @author lalo
 */
@Named(value = "nuevo")
@RequestScoped
public class Nuevo_MB implements Serializable {

    private ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String nombre;
    private String correoE;
    private String foto;
    private String usuario;
    private String contrasena;
    private String confContrasena;
    private boolean verFoto=false;
    private UserTempFacade userTempfacade = new UserTempFacade();
    private UsuarioFacade userFacade = new UsuarioFacade();
    private ClienteFacade clienteFacade = new ClienteFacade();
    private RolesFacade rolesFacade = new RolesFacade();

    /**
     * Creates a new instance of Nuevo_MB
     */
    public Nuevo_MB() {
    }

    FacesContext context;

    public void validaPagina() throws IOException {

        HttpSession session = (HttpSession) ec.getSession(false);
//        if (session != null) {
            if (session.getAttribute("status") == null) {
                ec.redirect(ec.getRequestContextPath() + "/faces/index.xhtml");
            }
            if (!(boolean) session.getAttribute("status")) {
                ec.redirect(ec.getRequestContextPath() + "/faces/index.xhtml");
            } else {
                this.apellidoPaterno = (String) session.getAttribute("ap");
                this.apellidoMaterno = (String) session.getAttribute("am");
                this.nombre = (String) session.getAttribute("nom");
                this.correoE = (String) session.getAttribute("co");
            }
//        } else {
//            ec.redirect(ec.getRequestContextPath() + "/faces/index.xhtml");
//        }
    }

    public void registrar() throws IOException {
        context = FacesContext.getCurrentInstance();
        String[] response = userTempfacade.consultarParaRegistrar(correoE, apellidoPaterno, apellidoMaterno, nombre);
        if (response[0].equals("0")) {//success
            //context.addMessage("growlMessage", new FacesMessage(FacesMessage.SEVERITY_INFO,"Exitoso",  response[1]) );
            if (contrasena.equals(confContrasena)) {
                UsuarioPojo user = userFacade.buscaUsuario(correoE, pwdSecure(contrasena));
                if (user == null) {
                    UsuarioPojo userCreado = new UsuarioPojo();
                    userCreado.setUnombre(correoE);
                    contrasena = pwdSecure(contrasena);
                    userCreado.setUcontrasenia(contrasena);
                    boolean creoUsuario = userFacade.crearUsuario(userCreado);
                    if (creoUsuario) {
                        userCreado = userFacade.buscaUsuario(correoE, contrasena);
                        if (userCreado != null) {
                            //tendria que mandar a un facade de empleado para poder crearlo alla...
                            ClientePojo empPojo = new ClientePojo();
                            empPojo.setNombreFoto(foto);
                            empPojo.setApellidoMaterno(apellidoMaterno);
                            empPojo.setApellidoPaterno(apellidoPaterno);
                            empPojo.setCorreoE(correoE);
                            empPojo.setNombre(nombre);
                            if (clienteFacade.crearCliente(empPojo, correoE)) {
                                // Aqui falta actualizar la tabla de empleados temporales para poner el estado en 1
                                if (cambiaEstado(userTempfacade.buscaUsuario(correoE))) {
                                    // Esto para poder reedirigir y mostrar el mensaje de usuario creado.
                                    if (rolesFacade.crearRegistroRolUser(correoE, "cliente", userCreado.getUid())) {
                                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "El cliente ha sido creado", ""));
                                        ec.getFlash().setKeepMessages(true);
//                                        HttpSession session = (HttpSession) ec.getSession(true);
                                        //Aqui se estaria reedirigiendo...
                                        ec.redirect(ec.getRequestContextPath() + "/faces/index.xhtml");
                                    } else {
                                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo crear el empleado, intenta más tarde"));
                                    }
                                } else {
                                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo crear el empleado, intenta más tarde"));
                                }

                            } else {
                                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo crear el empleado, intenta más tarde"));
                            }
                        }
                    } else {
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo crear el usuario, intenta más tarde"));
                    }
                } else {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe un usuario dado de alta con el correo: " + correoE));
                }
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Las contraseñas ingresadas no coinciden, por favor verificalas."));
            }
        } else { //Error
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", response[1]));
        }
    }

    private boolean cambiaEstado(UserTempPojo user) {
        user.setEstado(1);
        return userTempfacade.editarEstadoUsuario(user);
    }

    private String pwdSecure(String password) {
        String cadena = null;
        MessageDigest digest;
        byte[] hash;
        int l;
        StringBuffer hexString = new StringBuffer();
        try {
            digest = MessageDigest.getInstance("SHA-256");
            try {
                hash = digest.digest(password.getBytes("UTF-8"));
                l = hash.length;
                for (int i = 0; i < l; i++) {
                    String hex = Integer.toHexString(0xff & hash[i]);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Pre_Registro_MB.class.getName()).log(Level.SEVERE, null, ex);
            }
            cadena = hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Pre_Registro_MB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cadena;
    }

    public void oncapture(CaptureEvent captureEvent) {
        foto = getRandomImageName();
        final byte[] datos = captureEvent.getData();
//        String path = "C:\\Users\\sony\\Documents\\NetBeansProjects\\Seguridad Web\\Unidad III\\proyecto_web\\web" + File.separator + "resources" + File.separator
//                + "fotos" + File.separator + foto + ".png";
        ServletContext servletContext = (ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext();
        String newFileName = servletContext.getRealPath("") + File.separator + "resources" + File.separator +
                "fotos"+ File.separator + foto+ ".png";
        System.out.println(newFileName + "\n");

        FileImageOutputStream imageOutput;
//        FileImageOutputStream outputStream;
        try {
            imageOutput = new FileImageOutputStream(new File(newFileName));
            imageOutput.write(datos, 0, datos.length);
            imageOutput.close();
            verFoto=true;
        } catch (IOException e) {
            throw new FacesException("Error in writing captured image.", e);
        }
        System.out.println(foto);
    }

    private String getRandomImageName() {
        int i = (int) (Math.random() * 10000000);
        return String.valueOf(i);
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

    public boolean isVerFoto() {
        return verFoto;
    }

    public void setVerFoto(boolean verFoto) {
        this.verFoto = verFoto;
    }

}
