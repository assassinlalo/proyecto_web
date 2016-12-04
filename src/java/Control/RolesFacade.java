/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import Entidad.Roles;
import Entidad.Usuarios;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author lalo
 */
public class RolesFacade {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("ProyectoUnidad4PU");
    RolesJpaController jpaRoles = new RolesJpaController(emf);
    UsuariosJpaController jpaUser = new UsuariosJpaController(emf);
    Roles rol = new Roles();
    
    public String obtenerRol(String correo){
        String rolLogin;
        try{
            rol = jpaRoles.findRoles(correo);
            rolLogin = rol.getRol();
        }catch(Exception ex){
            rolLogin = null;
        }
        return rolLogin;
    }
    
    public boolean crearRegistroRolUser(String user, String rolUser, int uid){
        boolean creado;
        try{
            Usuarios usuario = jpaUser.findUsuarios(uid);
            rol.setRol(rolUser);
            rol.setUsuario(user);
            rol.setUid(usuario);
            jpaRoles.create(rol);
            creado = true;
        }catch(Exception ex){
            creado = false;
        }
        return creado;
    }
    
    
}
