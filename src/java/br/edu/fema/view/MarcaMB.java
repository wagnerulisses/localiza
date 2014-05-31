/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.edu.fema.view;

import br.edu.fema.controller.MarcaJpa;
import br.edu.fema.controller.MarcaJpaController;
import br.edu.fema.model.Marca;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import util.JPAUtil;

/**
 *
 * @author Damasceno
 */
@ManagedBean
//@RequestScoped    // apagamos esse  ..
@ViewScoped  // colocamos esse...
public class MarcaMB {
    
    private List<Marca> marcas;
    private Marca marca;

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public List<Marca> getMarcas() {
        // Ler as marca do banco de dados
        
        MarcaJpa marcaJpa = new MarcaJpaController(JPAUtil.getEmf());
        this.marcas = marcaJpa.findMarcaEntities();
        return marcas;
    }

    public void setMarcas(List<Marca> marcas) {
        this.marcas = marcas;
    }
    
    
    /**
     * Creates a new instance of MarcaMB
     */
    public MarcaMB() {
        this.marca = new Marca();
       
        
    }
    
    public void save(Marca marca){
        MarcaJpa marcaJpa = new MarcaJpaController(JPAUtil.getEmf());
        marcaJpa.create(marca);
        this.marca = new Marca();
    
    }
    
}
