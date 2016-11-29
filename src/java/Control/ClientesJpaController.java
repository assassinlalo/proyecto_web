/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import Control.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidad.Usuarios;
import Entidad.Citas;
import Entidad.Clientes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author lalo
 */
public class ClientesJpaController implements Serializable {

    public ClientesJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Clientes clientes) {
        if (clientes.getCitasCollection() == null) {
            clientes.setCitasCollection(new ArrayList<Citas>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuarios uid = clientes.getUid();
            if (uid != null) {
                uid = em.getReference(uid.getClass(), uid.getUid());
                clientes.setUid(uid);
            }
            Collection<Citas> attachedCitasCollection = new ArrayList<Citas>();
            for (Citas citasCollectionCitasToAttach : clientes.getCitasCollection()) {
                citasCollectionCitasToAttach = em.getReference(citasCollectionCitasToAttach.getClass(), citasCollectionCitasToAttach.getIdCita());
                attachedCitasCollection.add(citasCollectionCitasToAttach);
            }
            clientes.setCitasCollection(attachedCitasCollection);
            em.persist(clientes);
            if (uid != null) {
                uid.getClientesCollection().add(clientes);
                uid = em.merge(uid);
            }
            for (Citas citasCollectionCitas : clientes.getCitasCollection()) {
                Clientes oldIdClienteOfCitasCollectionCitas = citasCollectionCitas.getIdCliente();
                citasCollectionCitas.setIdCliente(clientes);
                citasCollectionCitas = em.merge(citasCollectionCitas);
                if (oldIdClienteOfCitasCollectionCitas != null) {
                    oldIdClienteOfCitasCollectionCitas.getCitasCollection().remove(citasCollectionCitas);
                    oldIdClienteOfCitasCollectionCitas = em.merge(oldIdClienteOfCitasCollectionCitas);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Clientes clientes) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Clientes persistentClientes = em.find(Clientes.class, clientes.getIdCliente());
            Usuarios uidOld = persistentClientes.getUid();
            Usuarios uidNew = clientes.getUid();
            Collection<Citas> citasCollectionOld = persistentClientes.getCitasCollection();
            Collection<Citas> citasCollectionNew = clientes.getCitasCollection();
            if (uidNew != null) {
                uidNew = em.getReference(uidNew.getClass(), uidNew.getUid());
                clientes.setUid(uidNew);
            }
            Collection<Citas> attachedCitasCollectionNew = new ArrayList<Citas>();
            for (Citas citasCollectionNewCitasToAttach : citasCollectionNew) {
                citasCollectionNewCitasToAttach = em.getReference(citasCollectionNewCitasToAttach.getClass(), citasCollectionNewCitasToAttach.getIdCita());
                attachedCitasCollectionNew.add(citasCollectionNewCitasToAttach);
            }
            citasCollectionNew = attachedCitasCollectionNew;
            clientes.setCitasCollection(citasCollectionNew);
            clientes = em.merge(clientes);
            if (uidOld != null && !uidOld.equals(uidNew)) {
                uidOld.getClientesCollection().remove(clientes);
                uidOld = em.merge(uidOld);
            }
            if (uidNew != null && !uidNew.equals(uidOld)) {
                uidNew.getClientesCollection().add(clientes);
                uidNew = em.merge(uidNew);
            }
            for (Citas citasCollectionOldCitas : citasCollectionOld) {
                if (!citasCollectionNew.contains(citasCollectionOldCitas)) {
                    citasCollectionOldCitas.setIdCliente(null);
                    citasCollectionOldCitas = em.merge(citasCollectionOldCitas);
                }
            }
            for (Citas citasCollectionNewCitas : citasCollectionNew) {
                if (!citasCollectionOld.contains(citasCollectionNewCitas)) {
                    Clientes oldIdClienteOfCitasCollectionNewCitas = citasCollectionNewCitas.getIdCliente();
                    citasCollectionNewCitas.setIdCliente(clientes);
                    citasCollectionNewCitas = em.merge(citasCollectionNewCitas);
                    if (oldIdClienteOfCitasCollectionNewCitas != null && !oldIdClienteOfCitasCollectionNewCitas.equals(clientes)) {
                        oldIdClienteOfCitasCollectionNewCitas.getCitasCollection().remove(citasCollectionNewCitas);
                        oldIdClienteOfCitasCollectionNewCitas = em.merge(oldIdClienteOfCitasCollectionNewCitas);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = clientes.getIdCliente();
                if (findClientes(id) == null) {
                    throw new NonexistentEntityException("The clientes with id " + id + " no longer exists.");
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
            Clientes clientes;
            try {
                clientes = em.getReference(Clientes.class, id);
                clientes.getIdCliente();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The clientes with id " + id + " no longer exists.", enfe);
            }
            Usuarios uid = clientes.getUid();
            if (uid != null) {
                uid.getClientesCollection().remove(clientes);
                uid = em.merge(uid);
            }
            Collection<Citas> citasCollection = clientes.getCitasCollection();
            for (Citas citasCollectionCitas : citasCollection) {
                citasCollectionCitas.setIdCliente(null);
                citasCollectionCitas = em.merge(citasCollectionCitas);
            }
            em.remove(clientes);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Clientes> findClientesEntities() {
        return findClientesEntities(true, -1, -1);
    }

    public List<Clientes> findClientesEntities(int maxResults, int firstResult) {
        return findClientesEntities(false, maxResults, firstResult);
    }

    private List<Clientes> findClientesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Clientes.class));
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

    public Clientes findClientes(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Clientes.class, id);
        } finally {
            em.close();
        }
    }

    public int getClientesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Clientes> rt = cq.from(Clientes.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
