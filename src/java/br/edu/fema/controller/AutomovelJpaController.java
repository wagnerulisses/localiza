/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.edu.fema.controller;

import br.edu.fema.controller.exceptions.NonexistentEntityException;
import br.edu.fema.model.Automovel;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import br.edu.fema.model.Modelo;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author dvillela
 */
public class AutomovelJpaController implements AutomovelJpa {

    public AutomovelJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    @Override
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public void create(Automovel automovel) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Modelo modeloId = automovel.getModeloId();
            if (modeloId != null) {
                modeloId = em.getReference(modeloId.getClass(), modeloId.getId());
                automovel.setModeloId(modeloId);
            }
            em.persist(automovel);
            if (modeloId != null) {
                modeloId.getAutomovelList().add(automovel);
                modeloId = em.merge(modeloId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void edit(Automovel automovel) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Automovel persistentAutomovel = em.find(Automovel.class, automovel.getId());
            Modelo modeloIdOld = persistentAutomovel.getModeloId();
            Modelo modeloIdNew = automovel.getModeloId();
            if (modeloIdNew != null) {
                modeloIdNew = em.getReference(modeloIdNew.getClass(), modeloIdNew.getId());
                automovel.setModeloId(modeloIdNew);
            }
            automovel = em.merge(automovel);
            if (modeloIdOld != null && !modeloIdOld.equals(modeloIdNew)) {
                modeloIdOld.getAutomovelList().remove(automovel);
                modeloIdOld = em.merge(modeloIdOld);
            }
            if (modeloIdNew != null && !modeloIdNew.equals(modeloIdOld)) {
                modeloIdNew.getAutomovelList().add(automovel);
                modeloIdNew = em.merge(modeloIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = automovel.getId();
                if (findAutomovel(id) == null) {
                    throw new NonexistentEntityException("The automovel with id " + id + " no longer exists.");
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
    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Automovel automovel;
            try {
                automovel = em.getReference(Automovel.class, id);
                automovel.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The automovel with id " + id + " no longer exists.", enfe);
            }
            Modelo modeloId = automovel.getModeloId();
            if (modeloId != null) {
                modeloId.getAutomovelList().remove(automovel);
                modeloId = em.merge(modeloId);
            }
            em.remove(automovel);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public List<Automovel> findAutomovelEntities() {
        return findAutomovelEntities(true, -1, -1);
    }

    @Override
    public List<Automovel> findAutomovelEntities(int maxResults, int firstResult) {
        return findAutomovelEntities(false, maxResults, firstResult);
    }

    private List<Automovel> findAutomovelEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Automovel.class));
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
    public Automovel findAutomovel(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Automovel.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public int getAutomovelCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Automovel> rt = cq.from(Automovel.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
