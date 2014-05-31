/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.edu.fema.controller;

import br.edu.fema.controller.exceptions.IllegalOrphanException;
import br.edu.fema.controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import br.edu.fema.model.Marca;
import br.edu.fema.model.Automovel;
import br.edu.fema.model.Modelo;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author dvillela
 */
public class ModeloJpaController implements ModeloJpa {

    public ModeloJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    @Override
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public void create(Modelo modelo) {
        if (modelo.getAutomovelList() == null) {
            modelo.setAutomovelList(new ArrayList<Automovel>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Marca marcaId = modelo.getMarcaId();
            if (marcaId != null) {
                marcaId = em.getReference(marcaId.getClass(), marcaId.getId());
                modelo.setMarcaId(marcaId);
            }
            List<Automovel> attachedAutomovelList = new ArrayList<Automovel>();
            for (Automovel automovelListAutomovelToAttach : modelo.getAutomovelList()) {
                automovelListAutomovelToAttach = em.getReference(automovelListAutomovelToAttach.getClass(), automovelListAutomovelToAttach.getId());
                attachedAutomovelList.add(automovelListAutomovelToAttach);
            }
            modelo.setAutomovelList(attachedAutomovelList);
            em.persist(modelo);
            if (marcaId != null) {
                marcaId.getModeloList().add(modelo);
                marcaId = em.merge(marcaId);
            }
            for (Automovel automovelListAutomovel : modelo.getAutomovelList()) {
                Modelo oldModeloIdOfAutomovelListAutomovel = automovelListAutomovel.getModeloId();
                automovelListAutomovel.setModeloId(modelo);
                automovelListAutomovel = em.merge(automovelListAutomovel);
                if (oldModeloIdOfAutomovelListAutomovel != null) {
                    oldModeloIdOfAutomovelListAutomovel.getAutomovelList().remove(automovelListAutomovel);
                    oldModeloIdOfAutomovelListAutomovel = em.merge(oldModeloIdOfAutomovelListAutomovel);
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
    public void edit(Modelo modelo) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Modelo persistentModelo = em.find(Modelo.class, modelo.getId());
            Marca marcaIdOld = persistentModelo.getMarcaId();
            Marca marcaIdNew = modelo.getMarcaId();
            List<Automovel> automovelListOld = persistentModelo.getAutomovelList();
            List<Automovel> automovelListNew = modelo.getAutomovelList();
            List<String> illegalOrphanMessages = null;
            for (Automovel automovelListOldAutomovel : automovelListOld) {
                if (!automovelListNew.contains(automovelListOldAutomovel)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Automovel " + automovelListOldAutomovel + " since its modeloId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (marcaIdNew != null) {
                marcaIdNew = em.getReference(marcaIdNew.getClass(), marcaIdNew.getId());
                modelo.setMarcaId(marcaIdNew);
            }
            List<Automovel> attachedAutomovelListNew = new ArrayList<Automovel>();
            for (Automovel automovelListNewAutomovelToAttach : automovelListNew) {
                automovelListNewAutomovelToAttach = em.getReference(automovelListNewAutomovelToAttach.getClass(), automovelListNewAutomovelToAttach.getId());
                attachedAutomovelListNew.add(automovelListNewAutomovelToAttach);
            }
            automovelListNew = attachedAutomovelListNew;
            modelo.setAutomovelList(automovelListNew);
            modelo = em.merge(modelo);
            if (marcaIdOld != null && !marcaIdOld.equals(marcaIdNew)) {
                marcaIdOld.getModeloList().remove(modelo);
                marcaIdOld = em.merge(marcaIdOld);
            }
            if (marcaIdNew != null && !marcaIdNew.equals(marcaIdOld)) {
                marcaIdNew.getModeloList().add(modelo);
                marcaIdNew = em.merge(marcaIdNew);
            }
            for (Automovel automovelListNewAutomovel : automovelListNew) {
                if (!automovelListOld.contains(automovelListNewAutomovel)) {
                    Modelo oldModeloIdOfAutomovelListNewAutomovel = automovelListNewAutomovel.getModeloId();
                    automovelListNewAutomovel.setModeloId(modelo);
                    automovelListNewAutomovel = em.merge(automovelListNewAutomovel);
                    if (oldModeloIdOfAutomovelListNewAutomovel != null && !oldModeloIdOfAutomovelListNewAutomovel.equals(modelo)) {
                        oldModeloIdOfAutomovelListNewAutomovel.getAutomovelList().remove(automovelListNewAutomovel);
                        oldModeloIdOfAutomovelListNewAutomovel = em.merge(oldModeloIdOfAutomovelListNewAutomovel);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = modelo.getId();
                if (findModelo(id) == null) {
                    throw new NonexistentEntityException("The modelo with id " + id + " no longer exists.");
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
            Modelo modelo;
            try {
                modelo = em.getReference(Modelo.class, id);
                modelo.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The modelo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Automovel> automovelListOrphanCheck = modelo.getAutomovelList();
            for (Automovel automovelListOrphanCheckAutomovel : automovelListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Modelo (" + modelo + ") cannot be destroyed since the Automovel " + automovelListOrphanCheckAutomovel + " in its automovelList field has a non-nullable modeloId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Marca marcaId = modelo.getMarcaId();
            if (marcaId != null) {
                marcaId.getModeloList().remove(modelo);
                marcaId = em.merge(marcaId);
            }
            em.remove(modelo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public List<Modelo> findModeloEntities() {
        return findModeloEntities(true, -1, -1);
    }

    @Override
    public List<Modelo> findModeloEntities(int maxResults, int firstResult) {
        return findModeloEntities(false, maxResults, firstResult);
    }

    private List<Modelo> findModeloEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Modelo.class));
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
    public Modelo findModelo(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Modelo.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public int getModeloCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Modelo> rt = cq.from(Modelo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
