/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import br.edu.fema.controller.MarcaJpa;
import br.edu.fema.controller.MarcaJpaController;
import br.edu.fema.model.Marca;
import javax.swing.JOptionPane;
    
/**
 *
 * @author dvillela
 */
public class TesteMarcaJpa {

    // Classe executável
    public static void main(String[] args) {
        // Criar uma nova marca
        Marca m = new Marca();
        m.setNome("Ford");
        MarcaJpa mJpa = new MarcaJpaController(JPAUtil.getEmf());
        try {
            mJpa.create(m);
            JOptionPane.showMessageDialog(null, "Marca salva, id=" + m.getId());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro 1 -" + ex.getMessage());
        }

        // Leitura de marca
        Marca mLida = mJpa.findMarca(m.getId());
        JOptionPane.showMessageDialog(null, "Marca lida, id=" + mLida.getId()
                + "\nNome= " + mLida.getNome());

        // Alteração de marca
        try {
            mLida.setNome("Novo");
            mJpa.edit(mLida);
            JOptionPane.showMessageDialog(null, "Marca alterada, id=" + mLida.getId()
                    + "\nNome= " + mLida.getNome());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro 2 -" + ex.getMessage());
        }
        
        // Exclusao de marca
        try {
            mJpa.destroy(mLida.getId());
            JOptionPane.showMessageDialog(null, "Marca excluida, id=" + mLida.getId()
                    + "\nNome= " + mLida.getNome());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro 3 -" + ex.getMessage());
        }
    }
}
