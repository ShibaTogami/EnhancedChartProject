/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cp.ejb;

import cp.entity.Mensaje;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext; 

/**
 *
 * @author shiba
 */
@Stateless
public class MensajeFacade extends AbstractFacade<Mensaje> {

    @PersistenceContext(unitName = "persistence")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MensajeFacade() {
        super(Mensaje.class);
    }
    
}
