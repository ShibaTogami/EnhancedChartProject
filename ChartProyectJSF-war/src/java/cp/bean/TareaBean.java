/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cp.bean;

import cp.ejb.ProyectoFacade;
import cp.ejb.TareaFacade;
import cp.entity.Proyecto;
import cp.entity.Tarea;
import cp.entity.TareaPK;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author rocio
 */
@ManagedBean
@SessionScoped
public class TareaBean {

    
    private ProyectoBean proyectoBean;
    private UsuarioBean usuarioBean;
    @EJB
    private TareaFacade tareaFacade;
    @EJB
    private ProyectoFacade proyectoFacade;

    protected Tarea tarea;
    protected Proyecto proyecto;

    @PostConstruct
    public void init() {
        this.tarea = new Tarea();
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    /*
     * Creates a new instance of TareaBean
     */
    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public TareaBean() {
    }

    public Tarea getTarea() {
        return tarea;
    }

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
    }

    public ProyectoBean getProyectoBean() {
        return proyectoBean;
    }

    public void setProyectoBean(ProyectoBean proyectoBean) {
        this.proyectoBean = proyectoBean; 
    }

    public UsuarioBean getUsuarioBean() {
        return usuarioBean;
    }

    public void setUsuarioBean(UsuarioBean usuarioBean) {
        this.usuarioBean = usuarioBean;
    }
    
    

    public String doEncontrarTarea(Tarea tarea, ProyectoBean pb) {
        this.setTarea(tarea);
        this.setProyectoBean(pb);
        this.setProyecto(this.proyectoBean.proyectoSeleccionado);
        return "tarea";
    }

    public String getNombreTarea() {
        return tarea.getNombre();
    }

    public String doEditar() {
        return "nuevaTarea";
    }

    public String doBorrar() {
        this.tareaFacade.remove(tarea);
        this.proyectoBean.proyectoSeleccionado.getTareaCollection().remove(tarea);
        this.proyectoFacade.edit(this.proyectoBean.proyectoSeleccionado);
        return "proyecto";
    }

    public String doGuardar() {
        if (tarea.getTareaPK().getIdTarea() != null) {
            this.tareaFacade.edit(tarea);
        } else {
            Date d = new Date();
            tarea.setFechaInicio(d);
            this.tareaFacade.create(tarea);
            this.proyecto.getTareaCollection().add(tarea);
            this.proyectoFacade.edit(proyecto);
        }
        return "tarea";
    }
    
    public String setProyectoUsuarioBean(ProyectoBean pb, UsuarioBean ub){
        this.init();
        this.setProyectoBean(pb);
        this.setUsuarioBean(ub);
        this.proyecto = proyectoBean.proyectoSeleccionado;
        TareaPK tpk = new TareaPK();
        tpk.setIdProyecto(proyecto.getIdProyecto().toBigInteger());
        tarea.setTareaPK(tpk);
        return "nuevaTarea";
    }
}
