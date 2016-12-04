/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import Control.exceptions.NonexistentEntityException;
import Entidad.Citas;
import Entidad.Clientes;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author lalo
 */
public class CitaFacade {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("ProyectoUnidad4PU");
    CitasJpaController jpaCita = new CitasJpaController(emf);
    
    ClientesJpaController CliCita = new ClientesJpaController(emf);
    
    Citas cita;
    
    private ClienteFacade facadeC= new ClienteFacade();
    private Clientes cliente;
    
     public List<CitaPojo> getAllCitas(){
          
        List<CitaPojo> citasP = new ArrayList();
         
        List<Citas> citas = jpaCita.findCitasEntities();
        for(int i=0; i< citas.size();i++){
            cliente= new Clientes();
            cliente = CliCita.findClientes(citas.get(i).getIdCliente().getIdCliente());
            CitaPojo pojo = new CitaPojo();
            pojo.setIdCita(citas.get(i).getIdCita());
            pojo.setFecha(citas.get(i).getFecha());
            pojo.setObservaciones(citas.get(i).getObservaciones());
            pojo.setHora(citas.get(i).getHora());
            pojo.setTipo(citas.get(i).getTipo());
            pojo.setNombre(cliente.getNombre()+" "+cliente.getApellidoPaterno()+" "+cliente.getApellidoMaterno());
            citasP.add(pojo);
            
        }
         
         return citasP;
      }
    
    public boolean reservarCita(CitaPojo citaPoj, String correo){
        boolean result = false;
        Clientes cliente;
        cliente = CliCita.findByEmail(correo);
        if(cliente != null){
            cita = new Citas();
            cita.setFecha(citaPoj.getFecha());
            cita.setObservaciones(citaPoj.getObservaciones());
            cita.setTipo(citaPoj.getTipo());
            cita.setHora(citaPoj.getHora());
            cita.setIdCliente(cliente);
            try{
                jpaCita.create(cita);
                result = true;
            }catch(Exception ex){
            }
        }
        return result;
    }
    
    public boolean existeCita(String fecha, String hora){
        boolean result;
        result = jpaCita.yaExisteHora(fecha, hora);
        return result;
    }
    public boolean diaCompleto(String fecha){
        boolean result;
        int citasHoy = jpaCita.cuantasHayFecha(fecha);
        result = (citasHoy >= 3);
        return result;
    }
    
      public boolean deleteC(int id) throws Exception{
          boolean elimino;
          try{
              jpaCita.destroy(id);
              elimino = true;
          }catch( NonexistentEntityException ex){
              elimino = false;
          }
          return elimino;
      }
}
