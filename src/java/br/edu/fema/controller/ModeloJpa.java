/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.edu.fema.controller;

import br.edu.fema.controller.exceptions.IllegalOrphanException;
import br.edu.fema.controller.exceptions.NonexistentEntityException;
import br.edu.fema.model.Modelo;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author dvillela
 */
public interface ModeloJpa extends Serializable {

    void create(Modelo modelo);

    void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException;

    void edit(Modelo modelo) throws IllegalOrphanException, NonexistentEntityException, Exception;

    Modelo findModelo(Integer id);

    List<Modelo> findModeloEntities();

    List<Modelo> findModeloEntities(int maxResults, int firstResult);

    EntityManager getEntityManager();

    int getModeloCount();
    
}
