/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Control.UserTempFacade;
import Control.UserTempPojo;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author lalo
 */
@Named(value = "pre_registro")
@SessionScoped
public class Pre_Registro_MB implements Serializable {
    
    private UserTempFacade facade  = new UserTempFacade();
    
    private String ap;
    private String am;
    private String nombre;
    private String correo;
    
    FacesContext context;
    
    @Resource(name = "mail/micorreo")
    private javax.mail.Session mailSession;
    
    /**
     * Creates a new instance of Pre_Registro_MB
     */
    public Pre_Registro_MB() {
    }
    
    public void preRegistro(){
        UserTempPojo usPojo = new UserTempPojo();
        
        String hash = generaHash();
        String numAl = numAleatorio();
        String pwd = generaPwdTemp(numAl);
        
        usPojo.setNombre(getNombre());
        usPojo.setAm(getAm());
        usPojo.setAp(getAp());
        usPojo.setCorreo(getCorreo());
        usPojo.setActivo(0);
        usPojo.setEstado(0);
        usPojo.setHash(hash);
        usPojo.setPasswordTemp(pwd);
        String mensaje[] = facade.createPreRegistro(usPojo);
        context = FacesContext.getCurrentInstance();
        if(mensaje[0].equals("0")){
            enviarCorreo(getCorreo(), pwd, hash); 
            context.addMessage("growlMessage", new FacesMessage("Exitoso",  mensaje[1]) );
            // Redirigir a index
        }else{
            context.addMessage("growlMessage", new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error",  mensaje[1]) );
        }
    }
    
    private String generaHash(){
        String cadena = null;
        Random rd = new Random();
        int numAleatorio = (int)(rd.nextDouble()*1000000);
        String numAlea = Integer.toString(numAleatorio);
        MessageDigest digest;
        byte[] hash;
        int l;
        StringBuffer hexString = new StringBuffer();
        try{
            digest = MessageDigest.getInstance("MD5");
            digest.update(numAlea.getBytes());
            hash = digest.digest();
            l = hash.length;
            for(int i=0; i<l; i++){
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1){
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            cadena = hexString.toString();
        }catch(NoSuchAlgorithmException ex){}
        return cadena;
    }
    private String numAleatorio(){
        SecureRandom random = new SecureRandom();
        int numAleatorio = random.nextInt(500000)+5000;
        String numAlea = Integer.toString(numAleatorio);
        return numAlea;
    }
    private String generaPwdTemp(String numAlea){
        String cadena = null;
        MessageDigest digest;
        byte[] hash;
        int l;
        StringBuffer hexString = new StringBuffer();
        try{
            digest = MessageDigest.getInstance("SHA-256");
            try{
                hash = digest.digest(numAlea.getBytes("UTF-8"));
                l = hash.length;
                for(int i=0; i<l; i++){
                    String hex = Integer.toHexString(0xff & hash[i]);
                    if(hex.length()== 1){
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }
            }catch(UnsupportedEncodingException ex){}
            cadena = hexString.toString().substring(0, 8);
        }catch(NoSuchAlgorithmException ex){}
        return cadena;
    }
    
    private void enviarCorreo(String to, String pwd, String hash){
        String subject = "Confirmación de pre-registro para nuevo cliente de: VIDAEstética";
        String msg = to;
        String msg1 = pwd;
        String msg2 = "http://localhost:8080/ProyectoUnidad4/faces/index.xhtml?email="+to+"&hash="+hash;
        String template = "Gracias por pre-registrarse. \n"
                +"Tu cuenta ha sido creada, podrás acceder a ella haciendo clic "
                +"en la siguiente liga de abajo y usando las siguientes credenciales: \n"
                +"_______________________________________________________________\n"
                +"\n"
                +"    usuario: "+"%s\n"
                +"    contraseña: "+"%s\n"
                +">_______________________________________________________________\n"
                +"\n"
                +"Por favor haz click en la siguiente liga para activar y acceder a tu cuenta\n"
                +"%s.\n";
        String body = String.format(template, msg,msg1,msg2);
        System.out.println("Body: \n"+body);
        MimeMessage message = new MimeMessage(mailSession);
        try{
            message.setFrom(new InternetAddress(mailSession.getProperty("mail.from")));
            InternetAddress[] address = {new InternetAddress(to)};
            message.setRecipients(Message.RecipientType.TO, address);
            message.setSubject(subject);
            message.setSentDate(new Date());
            message.setText(body);
            Transport.send(message);
        }catch(MessagingException ex){
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
