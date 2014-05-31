/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.edu.fema.controller;

import br.edu.fema.controller.exceptions.NonexistentEntityException;
import br.edu.fema.model.Automovel;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author dvillela
 */
public interface AutomovelJpa extends Serializable {

    void create(Automovel automovel);

    void destroy(Integer id) throws NonexistentEntityException;

    void edit(Automovel automovel) throws NonexistentEntityException, Exception;

    Automovel findAutomovel(Integer id);

    List<Automovel> findAutomovelEntities();

    List<Automovel> findAutomovelEntities(int maxResults, int firstResult);

    int getAutomovelCount();

    EntityManager getEntityManager();
    
}
