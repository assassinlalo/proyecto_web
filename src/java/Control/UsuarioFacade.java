/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import Entidad.Usuarios;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author lalo
 */
public class UsuarioFacade {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("ProyectoUnidad4PU");
    UsuariosJpaController userJpa = new UsuariosJpaController(emf);
    Usuarios usuario;
    
    public boolean crearUsuario(UsuarioPojo userPojo){
        usuario = new Usuarios();
        llenaDatosUsuario(userPojo,usuario);
        System.out.println("Me voy a Jpa a crear usuario");
        boolean creado;
        try{
            userJpa.create(usuario);
            creado = true;
        }catch(Exception ex){
            creado = false;
        }
        return creado;
    }
    
    private void llenaDatosUsuario(UsuarioPojo userPojo, Usuarios usuario){
        usuario.setUnombre(userPojo.getUnombre());
        usuario.setUcontrasenia(userPojo.getUcontrasenia());
    }
    
    public UsuarioPojo buscaUsuario(String unombre, String ucontrasenia){
        UsuarioPojo userPojo;
        boolean valido;
        usuario = new Usuarios();
        usuario = userJpa.findByUser(unombre);
        if(usuario != null){
            valido = validaUsuario(usuario,ucontrasenia);
            if(valido){
                userPojo = ConsigueDatosUsuario(usuario);
            }else{
                userPojo = null;
            }
        }else{
            userPojo = null;
        }
        return userPojo;
    }
    private boolean validaUsuario(Usuarios user, String pwd){
        String actPwd;
        actPwd = user.getUcontrasenia();
        if(actPwd.equals(pwd)){
            return true;
        }else{
            return false;
        }
    }
    
    private UsuarioPojo ConsigueDatosUsuario(Usuarios usuario){
        UsuarioPojo userPojo = new UsuarioPojo();
        userPojo.setUid(usuario.getUid());
        userPojo.setUnombre(usuario.getUnombre());
        userPojo.setUcontrasenia(usuario.getUcontrasenia());
        return userPojo;
    }
    
}
