/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import Control.exceptions.NonexistentEntityException;
import Entidad.Citas;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidad.Clientes;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author lalo
 */
public class CitasJpaController implements Serializable {

    public CitasJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Citas citas) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Clientes idCliente = citas.getIdCliente();
            if (idCliente != null) {
                idCliente = em.getReference(idCliente.getClass(), idCliente.getIdCliente());
                citas.setIdCliente(idCliente);
            }
            em.persist(citas);
            if (idCliente != null) {
                idCliente.getCitasCollection().add(citas);
                idCliente = em.merge(idCliente);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Citas citas) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Citas persistentCitas = em.find(Citas.class, citas.getIdCita());
            Clientes idClienteOld = persistentCitas.getIdCliente();
            Clientes idClienteNew = citas.getIdCliente();
            if (idClienteNew != null) {
                idClienteNew = em.getReference(idClienteNew.getClass(), idClienteNew.getIdCliente());
                citas.setIdCliente(idClienteNew);
            }
            citas = em.merge(citas);
            if (idClienteOld != null && !idClienteOld.equals(idClienteNew)) {
                idClienteOld.getCitasCollection().remove(citas);
                idClienteOld = em.merge(idClienteOld);
            }
            if (idClienteNew != null && !idClienteNew.equals(idClienteOld)) {
                idClienteNew.getCitasCollection().add(citas);
                idClienteNew = em.merge(idClienteNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = citas.getIdCita();
                if (findCitas(id) == null) {
                    throw new NonexistentEntityException("The citas with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Citas citas;
            try {
                citas = em.getReference(Citas.class, id);
                citas.getIdCita();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The citas with id " + id + " no longer exists.", enfe);
            }
            Clientes idCliente = citas.getIdCliente();
            if (idCliente != null) {
                idCliente.getCitasCollection().remove(citas);
                idCliente = em.merge(idCliente);
            }
            em.remove(citas);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Citas> findCitasEntities() {
        return findCitasEntities(true, -1, -1);
    }

    public List<Citas> findCitasEntities(int maxResults, int firstResult) {
        return findCitasEntities(false, maxResults, firstResult);
    }

    private List<Citas> findCitasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Citas.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Citas findCitas(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Citas.class, id);
        } finally {
            em.close();
        }
    }

    public int getCitasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Citas> rt = cq.from(Citas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    /*************************************************************** CREADOS ***************************************************************************************/
    
    public boolean yaExisteHora(String fecha, String hora){
        EntityManager em = getEntityManager();
        boolean ExisteCita = false;
        List<Citas> results = em.createNamedQuery("Citas.findByFecha",Citas.class).setParameter("fecha", fecha).getResultList();
        try{
            for (Citas itera : results) {
                if(itera.getHora().equals(hora)){
                    ExisteCita = true;
                }
            }
        }catch(Exception ex){
            ExisteCita = true;
            System.out.println("Ocurrio un error!!!!!!! EN el JPA Controller ");
        }
        return ExisteCita;
    }
    
    public int cuantasHayFecha(String fecha){
        int cont;
        EntityManager em = getEntityManager();
        List<Citas> results = em.createNamedQuery("Citas.findByFecha",Citas.class).setParameter("fecha", fecha).getResultList();
        cont = results.size();
        return cont;
    }
}
