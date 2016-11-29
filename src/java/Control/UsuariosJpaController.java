/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import Control.exceptions.IllegalOrphanException;
import Control.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidad.Clientes;
import Entidad.Usuarios;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author lalo
 */
public class UsuariosJpaController implements Serializable {

    public UsuariosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuarios usuarios) {
        if (usuarios.getClientesCollection() == null) {
            usuarios.setClientesCollection(new ArrayList<Clientes>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Clientes> attachedClientesCollection = new ArrayList<Clientes>();
            for (Clientes clientesCollectionClientesToAttach : usuarios.getClientesCollection()) {
                clientesCollectionClientesToAttach = em.getReference(clientesCollectionClientesToAttach.getClass(), clientesCollectionClientesToAttach.getIdCliente());
                attachedClientesCollection.add(clientesCollectionClientesToAttach);
            }
            usuarios.setClientesCollection(attachedClientesCollection);
            em.persist(usuarios);
            for (Clientes clientesCollectionClientes : usuarios.getClientesCollection()) {
                Usuarios oldUidOfClientesCollectionClientes = clientesCollectionClientes.getUid();
                clientesCollectionClientes.setUid(usuarios);
                clientesCollectionClientes = em.merge(clientesCollectionClientes);
                if (oldUidOfClientesCollectionClientes != null) {
                    oldUidOfClientesCollectionClientes.getClientesCollection().remove(clientesCollectionClientes);
                    oldUidOfClientesCollectionClientes = em.merge(oldUidOfClientesCollectionClientes);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuarios usuarios) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuarios persistentUsuarios = em.find(Usuarios.class, usuarios.getUid());
            Collection<Clientes> clientesCollectionOld = persistentUsuarios.getClientesCollection();
            Collection<Clientes> clientesCollectionNew = usuarios.getClientesCollection();
            List<String> illegalOrphanMessages = null;
            for (Clientes clientesCollectionOldClientes : clientesCollectionOld) {
                if (!clientesCollectionNew.contains(clientesCollectionOldClientes)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Clientes " + clientesCollectionOldClientes + " since its uid field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Clientes> attachedClientesCollectionNew = new ArrayList<Clientes>();
            for (Clientes clientesCollectionNewClientesToAttach : clientesCollectionNew) {
                clientesCollectionNewClientesToAttach = em.getReference(clientesCollectionNewClientesToAttach.getClass(), clientesCollectionNewClientesToAttach.getIdCliente());
                attachedClientesCollectionNew.add(clientesCollectionNewClientesToAttach);
            }
            clientesCollectionNew = attachedClientesCollectionNew;
            usuarios.setClientesCollection(clientesCollectionNew);
            usuarios = em.merge(usuarios);
            for (Clientes clientesCollectionNewClientes : clientesCollectionNew) {
                if (!clientesCollectionOld.contains(clientesCollectionNewClientes)) {
                    Usuarios oldUidOfClientesCollectionNewClientes = clientesCollectionNewClientes.getUid();
                    clientesCollectionNewClientes.setUid(usuarios);
                    clientesCollectionNewClientes = em.merge(clientesCollectionNewClientes);
                    if (oldUidOfClientesCollectionNewClientes != null && !oldUidOfClientesCollectionNewClientes.equals(usuarios)) {
                        oldUidOfClientesCollectionNewClientes.getClientesCollection().remove(clientesCollectionNewClientes);
                        oldUidOfClientesCollectionNewClientes = em.merge(oldUidOfClientesCollectionNewClientes);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = usuarios.getUid();
                if (findUsuarios(id) == null) {
                    throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuarios usuarios;
            try {
                usuarios = em.getReference(Usuarios.class, id);
                usuarios.getUid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Clientes> clientesCollectionOrphanCheck = usuarios.getClientesCollection();
            for (Clientes clientesCollectionOrphanCheckClientes : clientesCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuarios (" + usuarios + ") cannot be destroyed since the Clientes " + clientesCollectionOrphanCheckClientes + " in its clientesCollection field has a non-nullable uid field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(usuarios);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuarios> findUsuariosEntities() {
        return findUsuariosEntities(true, -1, -1);
    }

    public List<Usuarios> findUsuariosEntities(int maxResults, int firstResult) {
        return findUsuariosEntities(false, maxResults, firstResult);
    }

    private List<Usuarios> findUsuariosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuarios.class));
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

    public Usuarios findUsuarios(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuarios.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuariosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuarios> rt = cq.from(Usuarios.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public Usuarios findByUser(String username){
        EntityManager em = getEntityManager();
        Usuarios resultado = new Usuarios();
        List<Usuarios> results = em.createNamedQuery("Usuarios.findByUnombre",Usuarios.class).setParameter("unombre", username).getResultList();
        try{
            resultado = results.get(0);
        }catch(Exception ex){
            resultado = null;
            System.out.println("Ocurrio un error!!!!!!! EN el JPA Controller o no existe el registro...");
        }
        
        return resultado;
    }
}
