/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import Entidad.Userstemp;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author lalo
 */
public class UserTempFacade {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("ProyectoUnidad4PU");
    UserstempJpaController userJPA = new UserstempJpaController(emf);
    Userstemp user;
    
    public String[] createPreRegistro(UserTempPojo usPojo){
        String mensaje[] = new String[2];
        user = new Userstemp();
        if(userJPA.findByEmail(usPojo.getCorreo()) == null){
            user.setNombre(usPojo.getNombre());
            user.setActivo(usPojo.getActivo());
            user.setAm(usPojo.getAm());
            user.setAp(usPojo.getAp());
            user.setCorreo(usPojo.getCorreo());
            user.setEstado(usPojo.getEstado());
            user.setHash(usPojo.getHash());
            user.setId(usPojo.getId());
            user.setPasswordTemp(usPojo.getPasswordTemp());
            try{
                userJPA.create(user);
                mensaje[0] = "0";
                mensaje[1] = "Usuario con el correo "+usPojo.getCorreo()+", creado exitosamente. ¡Se te enviará un correo! Por favor revisalo y "
                        + "sigue las instrucciones. ";
            }catch(Exception ex){
                mensaje[0] = "-1";
                mensaje[1] = "Usuario con el correo "+usPojo.getCorreo()+", no se pudo crear exitosamente.";
            }
        }else{
            mensaje[0] = "-1";
            mensaje[1] = "Usuario con el correo "+usPojo.getCorreo()+", ya existe, por favor registra un correo distinto. ";
        }
        return mensaje;
    }
    
    public UserTempPojo consultarPreRegsitro(String hash, String pass){
        UserTempPojo usuarioReturn = null;
        // Buscar una mejor forma de hacerlo. 
        if(userJPA.findByPass(pass) != null){ // busca por contraseña
             Userstemp user = userJPA.findByHash(hash); // busca por hash
            if(user != null){
                usuarioReturn = new UserTempPojo();
                 usuarioReturn.setNombre(user.getNombre());
                 usuarioReturn.setAm(user.getAm());
                 usuarioReturn.setAp(user.getAp());
                 usuarioReturn.setCorreo(user.getCorreo());
                 usuarioReturn.setActivo(1);
            }
        }
        return usuarioReturn;
    }
    
    public String[] consultarParaRegistrar(String correo, String ap, String am, String nombre){
        String mensaje[] = new String[2];
        boolean coincide;
        boolean coincide2;
        boolean coincide3;
        try{
            Userstemp usTemp = userJPA.findByEmail(correo);
            if(usTemp != null){
                coincide = (ap.equals(usTemp.getAp()));
                coincide2 = (am.equals(usTemp.getAm()));
                coincide3 = (nombre.equals(usTemp.getNombre()));
                if(coincide && coincide2 && coincide3){
                    mensaje[0] = "0";
                    mensaje[1] = "Los datos del usuario con el correo "+correo+", coinciden con los de la BD, puede continuar. ";
                }
            }else{
                mensaje[0] = "-1";
                mensaje[1] = "Los datos del usuario con el correo "+correo+", no coinciden con la BD. ";
            }
        }catch(Exception ex){
            mensaje[0] = "-1";
            mensaje[1] = "Ocurrio un error al querer procesar los datos. Intenta más tarde. ";
        }
        
        return mensaje;
    }
}
