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
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author dvillela
 */
public interface MarcaJpa extends Serializable {

    void create(Marca marca);

    void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException;

    void edit(Marca marca) throws IllegalOrphanException, NonexistentEntityException, Exception;

    Marca findMarca(Integer id);

    List<Marca> findMarcaEntities();

    List<Marca> findMarcaEntities(int maxResults, int firstResult);

    EntityManager getEntityManager();

    int getMarcaCount();
    
}
