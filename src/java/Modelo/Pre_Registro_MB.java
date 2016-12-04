/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Control.UserTempFacade;
import Control.UserTempPojo;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;

/**
 *
 * @author lalo
 */
@Named(value = "pre_registro")
@RequestScoped
public class Pre_Registro_MB implements Serializable {

    private UserTempFacade facade = new UserTempFacade();
    private ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
    private UserTempPojo userPojo;

    private String ap;
    private String am;
    private String nombre;
    private String correo;

    FacesContext context;
    HttpSession session;
    @Resource(name = "mail/micorreo")
    private javax.mail.Session mailSession;

    /**
     * Creates a new instance of Pre_Registro_MB
     */
    public Pre_Registro_MB() {
    }

    public void validaPagina() throws IOException {
        System.out.println("Entre a valida pagina pre-registro desde el index");
        session = (HttpSession) ec.getSession(false);
        if (session != null) {
            //si se trata de abrir directamente con el url en la pesta;a del navegador
            if (session.getAttribute("preRegistro") == null ) {
                ec.redirect(ec.getRequestContextPath() + "/faces/index.xhtml");
            }
            if(!(boolean) session.getAttribute("preRegistro")){
                ec.redirect(ec.getRequestContextPath() + "/faces/index.xhtml");
            }
        } else {
//            context.addMessage("growlMessage", new FacesMessage("Error", "Su sessión permaneció inactiva, realice su resgistro de nuevo."));
//            ec.getFlash().setKeepMessages(true);
            ec.redirect(ec.getRequestContextPath() + "/faces/index.xhtml");
        }
    }

    public void preRegistro() throws IOException {
        userPojo = facade.buscaUsuario(correo);
        if (userPojo == null) {
            userPojo = new UserTempPojo();
            String hash = generaHash();
            String numAleatorio = numAleatorio();
            String pwd = generaPwdTemp(numAleatorio);

            userPojo.setNombre(getNombre());
            userPojo.setAm(getAm());
            userPojo.setAp(getAp());
            userPojo.setCorreo(getCorreo());
            userPojo.setActivo(0);
            userPojo.setEstado(0);
            userPojo.setHash(hash);
            userPojo.setPasswordTemp(pwd);

            String mensaje[] = facade.createPreRegistro(userPojo);
            context = FacesContext.getCurrentInstance();

            if (mensaje[0].equals("0")) {
                enviarCorreo(getCorreo(), numAleatorio, hash);
                context.addMessage("growlMessage", new FacesMessage("Exitoso", mensaje[1]));
                ec.getFlash().setKeepMessages(true);
                ec.redirect(ec.getRequestContextPath() + "/faces/index.xhtml");
            } else {
                context.addMessage("growlMessage", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", mensaje[1]));
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Ya existe un registro con el correo ingresado"));
        }

    }

    public String generaHash() {
        String cadena = null;
        Random rd = new Random();
        int numAleatorio = (int) (rd.nextDouble() * 100000);
        String numAlea = Integer.toString(numAleatorio);
        MessageDigest digest;
        byte[] hash;
        int l = 0;
        StringBuffer hexString = new StringBuffer();
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(numAlea.getBytes());
            hash = digest.digest();
            l = hash.length;
            for (int i = 0; i < l; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            cadena = hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Pre_Registro_MB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cadena;
    }

    private String numAleatorio() {
        SecureRandom random = new SecureRandom();
        int numAleatorio = random.nextInt(500000) + 5000;
        String numAlea = Integer.toString(numAleatorio);
        return numAlea;
    }

    private String generaPwdTemp(String numAlea) {
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
                Logger.getLogger(Pre_Registro_MB.class.getName()).log(Level.SEVERE, null, ex);
            }
            cadena = hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Pre_Registro_MB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cadena;
    }

    private void enviarCorreo(String to, String pwd, String hash) {
        String subject = "Confirmación de pre-registro para nuevo cliente de: VIDAEstética";
        String msg = to;
        String msg1 = pwd;
        String msg2 = "http://localhost:8080/ProyectoUnidad4/faces/index.xhtml?email=" + to + "&hash=" + hash;
        String template = "Gracias por pre-registrarse. \n"
                + "Tu cuenta ha sido creada, podrás acceder a ella haciendo clic "
                + "en la siguiente liga de abajo y usando las siguientes credenciales: \n"
                + "_______________________________________________________________\n"
                + "\n"
                + "    usuario: " + "%s\n"
                + "    contraseña: " + "%s\n"
                + ">_______________________________________________________________\n"
                + "\n"
                + "Por favor haz click en la siguiente liga para activar y acceder a tu cuenta\n"
                + "%s.\n";
        String body = String.format(template, msg, msg1, msg2);
        System.out.println("Body: \n" + body);
        MimeMessage message = new MimeMessage(mailSession);
        try {
            message.setFrom(new InternetAddress(mailSession.getProperty("mail.from")));
            InternetAddress[] address = {new InternetAddress(to)};
            message.setRecipients(Message.RecipientType.TO, address);
            message.setSubject(subject);
            message.setSentDate(new Date());
            message.setText(body);
            Transport.send(message);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * @return the ap
     */
    public String getAp() {
        return ap;
    }

    /**
     * @param ap the ap to set
     */
    public void setAp(String ap) {
        this.ap = ap;
    }

    /**
     * @return the am
     */
    public String getAm() {
        return am;
    }

    /**
     * @param am the am to set
     */
    public void setAm(String am) {
        this.am = am;
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
