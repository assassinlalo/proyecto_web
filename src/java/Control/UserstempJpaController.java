/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import Control.exceptions.NonexistentEntityException;
import Entidad.Userstemp;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author lalo
 */
public class UserstempJpaController implements Serializable {

    public UserstempJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Userstemp userstemp) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(userstemp);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Userstemp userstemp) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            userstemp = em.merge(userstemp);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = userstemp.getId();
                if (findUserstemp(id) == null) {
                    throw new NonexistentEntityException("The userstemp with id " + id + " no longer exists.");
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
            Userstemp userstemp;
            try {
                userstemp = em.getReference(Userstemp.class, id);
                userstemp.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userstemp with id " + id + " no longer exists.", enfe);
            }
            em.remove(userstemp);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Userstemp> findUserstempEntities() {
        return findUserstempEntities(true, -1, -1);
    }

    public List<Userstemp> findUserstempEntities(int maxResults, int firstResult) {
        return findUserstempEntities(false, maxResults, firstResult);
    }

    private List<Userstemp> findUserstempEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Userstemp.class));
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

    public Userstemp findUserstemp(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Userstemp.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserstempCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Userstemp> rt = cq.from(Userstemp.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    /*************************************************************** CREADOS ***************************************************************************************/
    public Userstemp findByEmail(String correo){
        EntityManager em = getEntityManager();
        Userstemp resultado = new Userstemp();
        List<Userstemp> results = em.createNamedQuery("Userstemp.findByCorreo",Userstemp.class).setParameter("correo", correo).getResultList();
        
        try{
            resultado = results.get(0);
        }catch(Exception ex){
            resultado = null;
            System.out.println("Ocurrio un error!!!!!!! EN el JPA Controller ");
        }
        
        return resultado;
    }
    
    public Userstemp findByPass(String password){
        EntityManager em = getEntityManager();
        
        List<Userstemp> results = em.createNamedQuery("Userstemp.findByPasswordTemp",Userstemp.class).setParameter("passwordTemp", password).getResultList();
        Userstemp resultado;
        try{
            resultado = results.get(0);
        }catch(Exception ex){
            resultado = null;
            System.out.println("Ocurrio un error!!!!!!! EN el JPA Controller o no existe el registro...");
        }
        
        return resultado;
    }
    
    public Userstemp findByHash(String hash){
        EntityManager em = getEntityManager();
        
        List<Userstemp> results = em.createNamedQuery("Userstemp.findByHash",Userstemp.class).setParameter("hash", hash).getResultList();
        Userstemp resultado;
        try{
            resultado = results.get(0);
        }catch(Exception ex){
            resultado = null;
            System.out.println("Ocurrio un error!!!!!!! EN el JPA Controller o no existe el registro...");
        }
        
        return resultado;
    }
    
}
