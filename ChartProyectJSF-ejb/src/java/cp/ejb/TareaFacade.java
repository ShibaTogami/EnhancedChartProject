/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cp.ejb;

import cp.entity.Tarea;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext; 
import javax.persistence.Query;

/**
 *
 * @author shiba
 */
@Stateless
public class TareaFacade extends AbstractFacade<Tarea> {

    @PersistenceContext(unitName = "persistence")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TareaFacade() {
        super(Tarea.class);
    }
    
    public List<Tarea> findByEstado(String estado, BigDecimal numProyecto) {
        Query q;
        
        q = em.createNamedQuery("Tarea.findByEstadoYProyecto");
        q.setParameter("estado", estado);
        q.setParameter("idProyecto", numProyecto);
        
        return q.getResultList();
    }
    
}
