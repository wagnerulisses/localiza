/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author dvillela
 */
public class JPAUtil {

    //Padrão Singleton
    private JPAUtil() {
    }
    
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("LocacaoPU");

    public static EntityManagerFactory getEmf() {
        return emf;
    }
}
