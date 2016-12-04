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
import java.io.IOException;
import javax.inject.Named;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author lalo
 */
@Named(value = "login")
@RequestScoped
public class Login_index_MB implements Serializable {

    private ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();

    private UserTempFacade facade = new UserTempFacade();
    private UsuarioFacade facadeUser = new UsuarioFacade();
    private ClienteFacade clienteFacade = new ClienteFacade();
    private RolesFacade rolesFacade = new RolesFacade();
    private UserTempPojo usuarioTemp = new UserTempPojo();

    private Integer id;
    private String login;
    private String pwd;
    private String hash;
    private String correo;
    private static boolean activado = false;
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

    public String preRegistrar() {
        session = nuevaSession();
        session.setAttribute("preRegistro", true);
        sessionInactiva(120);
        String cadena = "/pre_registro?faces-redirect=true";
        return cadena;
    }

    private void sessionInactiva(int tpoActivo) {
        session.setMaxInactiveInterval(tpoActivo);
    }

    public String submit() throws IOException {
        String pagina=null;
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest requestCreden = (HttpServletRequest) ec.getRequest();
        if (hash == null) {
            if ((login != null) || pwd != null) {
                userPojo = facadeUser.buscaUsuario(correo, desencriptaPwdTemp(pwd));
                if (userPojo != null) {
//                    try {
//                        // Consulta Roles
//                        requestCreden.login(correo, pwd);
//
//                        if (requestCreden.isUserInRole("cliente")) {
//                            ClientePojo cliente = clienteFacade.getCliente(userPojo.getUnombre());
//                            // reedirigir a la pagina correpondiente segun el tipo de usuario. 
//                            // Hacer una consulta a los roles... 
//                            session = (HttpSession) ec.getSession(true);
//                            session.setAttribute("Cliente", true);
//                            session.setAttribute("Correo", userPojo.getUnombre());
//                            session.setAttribute("pojoCliente", cliente);
//                            // Deberia buscar al usuario por su uid y sacar sus datos... para ponerlos en la sesión.
//                            ec.redirect(ec.getRequestContextPath() + "/faces/Cliente/agendar_cita.xhtml");
//                        } else if (requestCreden.isUserInRole("admin")) {
//                            session = (HttpSession) ec.getSession(true);
//                            session.setAttribute("Admin", true);
//                            ec.redirect(ec.getRequestContextPath() + "/faces/Admin/ver_citas.xhtml");
//                        } else {
//                            context.addMessage(null, new FacesMessage("credenciales insuficientes"));
//                            pagina = "index";
//                        }
//                    } catch (ServletException ex) {
//                        Logger.getLogger(Login_index_MB.class.getName()).log(Level.SEVERE, null, ex);
//                    }

                    String rol = rolesFacade.obtenerRol(correo);
                    if (rol != null) {
                        if (rol.equals("cliente")) {
                            ClientePojo cliente = clienteFacade.getCliente(userPojo.getUnombre());
                            // reedirigir a la pagina correpondiente segun el tipo de usuario. 
                            // Hacer una consulta a los roles... 
                            session = (HttpSession) ec.getSession(true);
                            session.setAttribute("Cliente", true);
                            session.setAttribute("Correo", userPojo.getUnombre());
                            session.setAttribute("pojoCliente", cliente);
                            // Deberia buscar al usuario por su uid y sacar sus datos... para ponerlos en la sesión.
                            ec.redirect(ec.getRequestContextPath() + "/faces/Cliente/agendar_cita.xhtml");
                        } else {
                            session = (HttpSession) ec.getSession(true);
                            session.setAttribute("Admin", true);
                            ec.redirect(ec.getRequestContextPath() + "/faces/Admin/ver_citas.xhtml");
                        }
                    } else {
                        context.addMessage("growlMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "No tienes privilegios para acceder"));
                    }
                } else {
                    context.addMessage("growlMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Los datos no son válidos", "El usuario: " + login
                            + ", no existe, por favor registrate."));
                }
            } else {
                context.addMessage("growlMensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Los datos no son válidos", "Error"));
            }

        } else {
//            if((login != null) && pwd != null){
            usuarioTemp = facade.buscaUsuario(correo);
//            if (login.equals(correo)) {
            if (usuarioTemp != null) {
                System.out.println("Contraseña recibida: " + pwd + "\n Correo Recibido: " + correo);
                if (usuarioTemp.getPasswordTemp().equals(desencriptaPwdTemp(pwd)) && usuarioTemp.getHash().equals(hash)) {
                    cambiaActivo(usuarioTemp);
                    String ap = usuarioTemp.getAp();
                    String am = usuarioTemp.getAm();
                    String nom = usuarioTemp.getNombre();
                    String co = usuarioTemp.getCorreo();
                    try {
                        session = nuevaSession();
                        HttpServletRequest request = (HttpServletRequest) ec.getRequest();
                        System.out.println("User Agents: \n" + request.getHeader("user-agent"));
                        //Este segmento de código permite obtener la ip del usuario que está con la sessión activa.
                        //Es necesario verificar en todo momento que la sesión, la ip y el usuario no cambien de parámetro.
                        String ip = request.getHeader("X-FORWARDED-FOR");
                        if (ip != null) {
                            ip = ip.replaceFirst(",.*", "");
                        } else {
                            ip = request.getRemoteAddr();
                        }

                        session.setAttribute("ap", ap);
                        session.setAttribute("am", am);
                        session.setAttribute("nom", nom);
                        session.setAttribute("co", co);
                        session.setAttribute("status", activado);

                        System.out.println("Me voy al alta......");
                        ec.redirect(ec.getRequestContextPath() + "/faces/Nuevo/registro.xhtml");
                    } catch (IOException ex) {
                        System.out.println("Ocurrio un error al redirigir a la pagina de registro... ");
                    }
                } else {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Contraseña invalida."));
                }
                //context.addMessage("growlMensaje", new FacesMessage(FacesMessage.SEVERITY_INFO,"Hola","Los datos que se reciben son: "+correo+"   y "+hash));
//                UserTempPojo usuario = null;
//                try {
//                    usuario = facade.consultarPreRegsitro(hash, pwd);
//                } catch (Exception ex) {
//
//                }
//                if (usuario != null) {
//                    try {
//                        // Cambiar a activo en 1 a la tabla... 
//                        session = (HttpSession) ec.getSession(false);
//                        session.setAttribute("activado", true);
//                        String parametros = "?ap=" + usuario.getAp() + "&am=" + usuario.getAm() + "&nom=" + usuario.getNombre() + "&co=" + usuario.getCorreo();
//                        ec.redirect(ec.getRequestContextPath() + "/faces/Nuevo/registro.xhtml" + parametros);
//                    } catch (IOException ex) {
//                        System.out.println("Ocurrio un error al redirigir a la pagina de registro... ");
//                    }
//                } else {
//                    context.addMessage("growlMensaje", new FacesMessage(FacesMessage.SEVERITY_INFO, "Hola", "Los datos no coinciden con la BD. "
//                            + "Por favor intenta acceder de nuevo. "));
//                }
            } else {
                context.addMessage("growlMensaje", new FacesMessage(FacesMessage.SEVERITY_INFO, "Hola", "El correo introducido no coincide... "
                        + "intentalo de nuevo"));
            }
//            }else{
//                context.addMessage("growlMensaje", new FacesMessage(FacesMessage.SEVERITY_INFO,"Hola","Debes introducir datos para poder acceder. "
//                            + "intentalo de nuevo"));
//            }
        }
        return pagina;
    }

    private void cambiaActivo(UserTempPojo user) {
        user.setActivo(1);
        activado = facade.editarActivoUsuario(user);
    }

    private HttpSession nuevaSession() {
        HttpSession session = (HttpSession) ec.getSession(false);
        System.out.println("Session nueva " + session.isNew());
        System.out.println("id de session " + session.getId());
        session.invalidate();
        session = (HttpSession) ec.getSession(true);
        System.out.println("Session nueva " + session.isNew());
        System.out.println("id de session " + session.getId());
        return session;
    }

    private String desencriptaPwdTemp(String numAlea) {
        String cadena = null;
        MessageDigest digest;
        byte[] hash;
        int l;
        StringBuffer hexString = new StringBuffer();
        try {
            digest = MessageDigest.getInstance("SHA-256");
            try {
                hash = digest.digest(numAlea.getBytes("UTF-8"));
                l = hash.length;
                for (int i = 0; i < l; i++) {
                    String hex = Integer.toHexString(0xff & hash[i]);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);

                }
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Login_index_MB.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            cadena = hexString.toString();

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Login_index_MB.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("CADENA.......... \n" + cadena);
        return cadena;
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
