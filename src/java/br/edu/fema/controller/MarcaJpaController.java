/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.edu.fema.controller;

import br.edu.fema.controller.exceptions.IllegalOrphanException;
import br.edu.fema.controller.exceptions.NonexistentEntityException;
import br.edu.fema.model.Marca;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import br.edu.fema.model.Modelo;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author dvillela
 */
public class MarcaJpaController implements MarcaJpa {

    public MarcaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    @Override
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public void create(Marca marca) {
        if (marca.getModeloList() == null) {
            marca.setModeloList(new ArrayList<Modelo>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Modelo> attachedModeloList = new ArrayList<Modelo>();
            for (Modelo modeloListModeloToAttach : marca.getModeloList()) {
                modeloListModeloToAttach = em.getReference(modeloListModeloToAttach.getClass(), modeloListModeloToAttach.getId());
                attachedModeloList.add(modeloListModeloToAttach);
            }
            marca.setModeloList(attachedModeloList);
            em.persist(marca);
            for (Modelo modeloListModelo : marca.getModeloList()) {
                Marca oldMarcaIdOfModeloListModelo = modeloListModelo.getMarcaId();
                modeloListModelo.setMarcaId(marca);
                modeloListModelo = em.merge(modeloListModelo);
                if (oldMarcaIdOfModeloListModelo != null) {
                    oldMarcaIdOfModeloListModelo.getModeloList().remove(modeloListModelo);
                    oldMarcaIdOfModeloListModelo = em.merge(oldMarcaIdOfModeloListModelo);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void edit(Marca marca) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Marca persistentMarca = em.find(Marca.class, marca.getId());
            List<Modelo> modeloListOld = persistentMarca.getModeloList();
            List<Modelo> modeloListNew = marca.getModeloList();
            List<String> illegalOrphanMessages = null;
            for (Modelo modeloListOldModelo : modeloListOld) {
                if (!modeloListNew.contains(modeloListOldModelo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Modelo " + modeloListOldModelo + " since its marcaId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Modelo> attachedModeloListNew = new ArrayList<Modelo>();
            for (Modelo modeloListNewModeloToAttach : modeloListNew) {
                modeloListNewModeloToAttach = em.getReference(modeloListNewModeloToAttach.getClass(), modeloListNewModeloToAttach.getId());
                attachedModeloListNew.add(modeloListNewModeloToAttach);
            }
            modeloListNew = attachedModeloListNew;
            marca.setModeloList(modeloListNew);
            marca = em.merge(marca);
            for (Modelo modeloListNewModelo : modeloListNew) {
                if (!modeloListOld.contains(modeloListNewModelo)) {
                    Marca oldMarcaIdOfModeloListNewModelo = modeloListNewModelo.getMarcaId();
                    modeloListNewModelo.setMarcaId(marca);
                    modeloListNewModelo = em.merge(modeloListNewModelo);
                    if (oldMarcaIdOfModeloListNewModelo != null && !oldMarcaIdOfModeloListNewModelo.equals(marca)) {
                        oldMarcaIdOfModeloListNewModelo.getModeloList().remove(modeloListNewModelo);
                        oldMarcaIdOfModeloListNewModelo = em.merge(oldMarcaIdOfModeloListNewModelo);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = marca.getId();
                if (findMarca(id) == null) {
                    throw new NonexistentEntityException("The marca with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Marca marca;
            try {
                marca = em.getReference(Marca.class, id);
                marca.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The marca with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Modelo> modeloListOrphanCheck = marca.getModeloList();
            for (Modelo modeloListOrphanCheckModelo : modeloListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Marca (" + marca + ") cannot be destroyed since the Modelo " + modeloListOrphanCheckModelo + " in its modeloList field has a non-nullable marcaId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(marca);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public List<Marca> findMarcaEntities() {
        return findMarcaEntities(true, -1, -1);
    }

    @Override
    public List<Marca> findMarcaEntities(int maxResults, int firstResult) {
        return findMarcaEntities(false, maxResults, firstResult);
    }

    private List<Marca> findMarcaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Marca.class));
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

    @Override
    public Marca findMarca(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Marca.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public int getMarcaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Marca> rt = cq.from(Marca.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
