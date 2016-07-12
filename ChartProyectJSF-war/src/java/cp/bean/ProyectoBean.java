/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cp.bean;

import cp.ejb.ComentarioFacade;
import cp.ejb.ProyectoFacade;
import cp.ejb.TareaFacade;
import cp.ejb.UsuarioFacade;
import cp.entity.Comentario;
import cp.entity.Proyecto;
import cp.entity.Tarea;
import cp.entity.Usuario;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

/**
 *
 * @author shiba
 */
@ManagedBean(name="proyectoBean")
@SessionScoped
public class ProyectoBean implements Serializable {
    @ManagedProperty(value="#{usuarioBean}")
    private UsuarioBean usuarioBean;

    
    @EJB
    private ProyectoFacade proyectoFacade;
    @EJB
    private UsuarioFacade usuarioFacade;
    @EJB
    private ComentarioFacade comentarioFacade;
    @EJB
    private TareaFacade tareaFacade;
    
    protected Proyecto proyectoSeleccionado;
    protected String usuarioBuscar;
    protected List<Usuario> usuariosEncontrados;
    protected List<Usuario> seleccion;
    protected String comentario;
    protected int num;

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
    

    public UsuarioBean getUsuarioBean() {
        return usuarioBean;
    }

    public void setUsuarioBean(UsuarioBean usuarioBean) {
        this.usuarioBean = usuarioBean;
    }
    
    public List<Usuario> getSeleccion() {
        return seleccion;
    }

    public void setSeleccion(List<Usuario> seleccion) {
        this.seleccion = seleccion;
    }

    public List<Usuario> getUsuariosEncontrados() {
        return usuariosEncontrados;
    }

    public void setUsuariosEncontrados(List<Usuario> usuariosEncontrados) {
        this.usuariosEncontrados = usuariosEncontrados;
    }

    public String getUsuarioBuscar() {
        return usuarioBuscar;
    }

    

    public void setUsuarioBuscar(String usuarioBuscar) {
        this.usuarioBuscar = usuarioBuscar;
    }
    
    public int aumentar() {
        num++;
        return num;
    }
    
    /**
     * Creates a new instance of ProyectoBean
     */
    
    @PostConstruct
    public void Init(){
        seleccion = new ArrayList<Usuario>();
        this.num = 1;
    }
    
    public ProyectoBean() {
    }

    public Proyecto getProyectoSeleccionado() {
        return proyectoSeleccionado;
    }

    public void setProyectoSeleccionado(BigDecimal idProyecto) {
        this.proyectoSeleccionado = proyectoFacade.findByIdProyecto(idProyecto);
    }
     
    public String buscarUsuarios(){
        setUsuariosEncontrados(usuarioFacade.getUsuarioPorNicknameParecido(usuarioBuscar));
        List<Usuario> aux = new ArrayList<Usuario>();
        for(Usuario us : usuariosEncontrados){
            for(Usuario u : proyectoSeleccionado.getUsuarioCollection()){
                if(us.getNickname().equals(u.getNickname())){
                    aux.add(u);
                    break;
                }
            }
        }
        usuariosEncontrados.removeAll(aux);
        if(usuariosEncontrados == null){
            return "error";
        }
        else{
            
        return "busqueda";
        }
    }
    public String doActualizar(){
        proyectoFacade.edit(proyectoSeleccionado);
        Comentario comment = new Comentario();
        comment.setTexto("el usuario " + usuarioBean.usuario.getNickname()+ " actualizó el proyecto");
        comment.setIdProyecto1(proyectoSeleccionado);
        comment.setNickname(usuarioBean.usuario);
        comentarioFacade.create(comment);
        proyectoSeleccionado.getComentarioCollection1().add(comment);
        seleccion = new ArrayList();
        return "proyecto";
        
    }
    
    public String doComentar(){
        Comentario comment = new Comentario();
        comment.setTexto(comentario);
        comentario = "";
        comment.setIdProyecto2(proyectoSeleccionado);
        comment.setNickname(usuarioBean.getUsuario());
        comentarioFacade.create(comment);
        proyectoSeleccionado.getComentarioCollection().add(comment);
        return "proyecto";
    }
    
    public String doEliminar(){
        for(Comentario comment : proyectoSeleccionado.getComentarioCollection()){
            comentarioFacade.remove(comment);
        }
        for(Comentario comment : proyectoSeleccionado.getComentarioCollection1()){
            comentarioFacade.remove(comment);
        }
        for(Tarea tarea : proyectoSeleccionado.getTareaCollection()){
            tareaFacade.remove(tarea);
        }
        List<Usuario> usuarios = (List<Usuario>) proyectoSeleccionado.getUsuarioCollection();
        for(Usuario u : usuarios){
            u.getProyectoCollection().remove(proyectoSeleccionado);
        }
        this.usuarioBean.usuario.getProyectoCollection().remove(proyectoSeleccionado);
        this.usuarioBean.usuario.getProyectoCollection1().remove(proyectoSeleccionado);
        proyectoFacade.remove(proyectoSeleccionado);
        return "principal";
    }
    
    public String doTerminar(){
        proyectoSeleccionado.setEstado("FINALIZADO");
        Comentario comment = new Comentario();
        comment.setTexto("el usuario " + usuarioBean.usuario.getNickname()+ " finalizó el proyecto");
        comment.setIdProyecto1(proyectoSeleccionado);
        comment.setNickname(usuarioBean.usuario);
        comentarioFacade.create(comment);
        proyectoSeleccionado.getComentarioCollection1().add(comment);
        proyectoSeleccionado.setNombre(proyectoSeleccionado.getNombre() + " (FINALIZADO)");
        proyectoFacade.edit(proyectoSeleccionado);
        List<Proyecto> proyectos = (List<Proyecto>) usuarioBean.usuario.getProyectoCollection();
            for(int i = 0; i < proyectos.size(); i++){
                if(proyectos.get(i).getIdProyecto().equals(proyectoSeleccionado.getIdProyecto())){
                    proyectos.get(i).setNombre(proyectoSeleccionado.getNombre());
                    usuarioBean.usuario.setProyectoCollection(proyectos);
                    break;
                }
            }
        return "principal";
    }
    
    public boolean doRenderizar(){
        boolean res = true;
            if(proyectoSeleccionado.getEstado().equals("FINALIZADO") || !usuarioBean.usuario.equals(proyectoSeleccionado.getLider())){
                res = false;
            }
        return res;
    }
    
    public boolean doRenderizar2(){
        boolean res = true;
            if(proyectoSeleccionado.getEstado().equals("FINALIZADO")){
                res = false;
            }
        return res;
    }
    
    
    public String añadirSeleccion(Usuario usuario){
        seleccion.add(usuario);
        usuariosEncontrados.remove(usuario);
        return "busqueda";
    }
    
    public String actualizar(){
        for(Usuario usuario: seleccion){
            proyectoSeleccionado.getUsuarioCollection().add(usuario);
            usuario.getProyectoCollection().add(proyectoSeleccionado);
        }
        Comentario comment = new Comentario();
        comment.setTexto("el usuario " + usuarioBean.usuario.getNickname()+ " actualizó el proyecto");
        comment.setIdProyecto1(proyectoSeleccionado);
        comment.setNickname(usuarioBean.usuario);
        comentarioFacade.create(comment);
        proyectoSeleccionado.getComentarioCollection1().add(comment);
        proyectoFacade.edit(proyectoSeleccionado);
        
        return "proyecto";
    }
 
    public String doMostrar(BigDecimal idProyecto, UsuarioBean usuarioBean) {
        this.setProyectoSeleccionado(idProyecto);
        this.setUsuarioBean(usuarioBean);
        return "proyecto";
    }

    public void comentarioTareaCreada(){
        Comentario comment = new Comentario();
        comment.setTexto("el usuario " + usuarioBean.usuario.getNickname()+ " creó una Tarea");
        comment.setIdProyecto1(proyectoSeleccionado);
        comment.setNickname(usuarioBean.usuario);
        comentarioFacade.create(comment);
        proyectoSeleccionado.getComentarioCollection1().add(comment);
    }
    
    public void comentarioTareaEditada(){
        Comentario comment = new Comentario();
        comment.setTexto("el usuario " + usuarioBean.usuario.getNickname()+ " editó una Tarea");
        comment.setIdProyecto1(proyectoSeleccionado);
        comment.setNickname(usuarioBean.usuario);
        comentarioFacade.create(comment);
        proyectoSeleccionado.getComentarioCollection1().add(comment);
    }
    
    public void comentarioTareaEliminada(){
        Comentario comment = new Comentario();
        comment.setTexto("el usuario " + usuarioBean.usuario.getNickname()+ " eliminó una Tarea");
        comment.setIdProyecto1(proyectoSeleccionado);
        comment.setNickname(usuarioBean.usuario);
        comentarioFacade.create(comment);
        proyectoSeleccionado.getComentarioCollection1().add(comment);
    }
    
}