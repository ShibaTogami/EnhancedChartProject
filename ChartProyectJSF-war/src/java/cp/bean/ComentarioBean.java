/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cp.bean;

import cp.ejb.ComentarioFacade;
import cp.ejb.ProyectoFacade;
import cp.entity.Comentario;
import cp.entity.Proyecto;
import cp.entity.Usuario;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author nacho
 */
@ManagedBean
@SessionScoped
public class ComentarioBean {

    
    //@ManagedProperty (value="#{usuarioBean}")
    private UsuarioBean usuarioBean;
    
    //@ManagedProperty (value="#{proyectoBean}")
    private ProyectoBean proyectoBean;

    public ProyectoBean getProyectoBean() {
        return proyectoBean;
    }

    public String setProyectoBean(ProyectoBean proyectoBean, UsuarioBean usuarioBean) {
        this.proyectoBean = proyectoBean;
        this.setUsuarioBean(usuarioBean);
        return "anadirComentario";
    }
    
    @EJB
    private ProyectoFacade proyectoFacade;
    
    @EJB
    private ComentarioFacade comentarioFacade;
    
    protected Comentario comentario;
    protected Proyecto proyecto;
    protected Usuario usuario;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }
    

    public UsuarioBean getUsuarioBean() {
        return usuarioBean;
    }

    public void setUsuarioBean(UsuarioBean usuarioBean) {
        this.usuarioBean = usuarioBean;
    }

    public Comentario getComentario() {
        return comentario;
    }

    public void setComentario(Comentario comentario) {
        this.comentario = comentario;
    }
    /**
     * Creates a new instance of ComentarioBean
     */
    public ComentarioBean() {
    }
    
    @PostConstruct
    public void Init(){
        comentario = new Comentario();
    }
    
    public String doComentar(){
        comentario.setIdProyecto2(proyectoBean.proyectoSeleccionado);
        comentario.setNickname(usuarioBean.getUsuario());
        comentarioFacade.create(comentario);
        proyectoBean.proyectoSeleccionado.getComentarioCollection().add(comentario);
        comentario = new Comentario();
        return "proyecto";
    }
    
}
