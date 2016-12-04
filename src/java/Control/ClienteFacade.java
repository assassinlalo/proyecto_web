/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import Entidad.Clientes;
import Entidad.Usuarios;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author lalo
 */
public class ClienteFacade {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("ProyectoUnidad4PU");
    ClientesJpaController empJPA = new ClientesJpaController(emf);
    Clientes emp;
    
    UsuariosJpaController userJpa = new UsuariosJpaController(emf);
    
    public boolean crearCliente(ClientePojo empPojo,String user){
        boolean result;
        Usuarios usuario;
        emp = new Clientes();
        emp.setApellidoMaterno(empPojo.getApellidoMaterno());
        emp.setApellidoPaterno(empPojo.getApellidoPaterno());
        emp.setNombre(empPojo.getNombre());
        emp.setCorreoE(empPojo.getCorreoE());
        emp.setNombreFoto(empPojo.getNombreFoto());
        try{
            usuario = userJpa.findByUser(user);
            emp.setUid(usuario);
            empJPA.create(emp);
            result = true;
        }catch(Exception ex){
            result = false;
        }
        return result;
    }
    
    public ClientePojo getCliente(String correo){
        Usuarios usuario = userJpa.findByUser(correo);
        Clientes cliente = empJPA.findByUid(usuario);
        ClientePojo pojo = new ClientePojo();
        if(cliente != null){
            pojo.setApellidoMaterno(cliente.getApellidoMaterno());
            pojo.setApellidoPaterno(cliente.getApellidoPaterno());
            pojo.setCorreoE(cliente.getCorreoE());
            pojo.setIdEmpleado(cliente.getIdCliente());
            pojo.setIdUser(usuario.getUid());
            pojo.setNombre(cliente.getNombre());
            pojo.setNombreFoto(cliente.getNombreFoto());
        }else{
            pojo = null;
        }
        return pojo;
    }
}
