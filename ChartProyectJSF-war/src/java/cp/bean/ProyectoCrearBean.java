/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cp.bean;

import cp.ejb.ProyectoFacade;
import cp.ejb.UsuarioFacade;
import cp.entity.Proyecto;
import cp.entity.Usuario;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;

/**
 *
 * @author pablo
 */
@ManagedBean
@SessionScoped
public class ProyectoCrearBean {

    @EJB
    private UsuarioFacade usuarioFacade;

    @EJB
    private ProyectoFacade proyectoFacade;
    
    protected List<Usuario> participantes;
    protected String participanteNuevo;
    protected String error;
    protected boolean errorActivo;
    protected Proyecto proyecto;
    protected List<Usuario> todosLosUsuarios;
    

    
    /**
     * Creates a new instance of ProyectoCrearBean
     */
    public ProyectoCrearBean() {
    }
    
    
    @PostConstruct
    public void init() {
        proyecto = new Proyecto();
        proyecto.setDescripcion("Introduzca una descripcion para los nuevos participantes.");
        this.participantes = new ArrayList();
        error="";
        errorActivo = false;
        todosLosUsuarios = usuarioFacade.findAll();
    }

    public boolean isErrorActivo() {
        return errorActivo;
    }

    public void setErrorActivo(boolean errorActivo) {
        this.errorActivo = errorActivo;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
    
    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public List<Usuario> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<Usuario> participantes) {
        this.participantes = participantes;
    }

    public String getParticipanteNuevo() {
        return participanteNuevo;
    }

    public void setParticipanteNuevo(String participanteNuevo) {
        this.participanteNuevo = participanteNuevo;
    }
    
    public String doAñadirParticipante(Usuario lider) {
        Usuario usuario = usuarioFacade.getUsuarioPorNickname(participanteNuevo);
        //PREGUNTAR EDU CÓMO HACER LO DE AJAX Y EL ERROR
        if(participanteNuevo.equals("")) {
            error = "Campo vacío.";
            errorActivo = true;
        } else if(usuario == null) {
            error = "El usuario introducido no existe.";
            errorActivo = true;
        } else if(usuario.equals(lider)) {
            error = "El líder ya formar parte de los participantes.";
            errorActivo = true;
        } else if(participantes.contains(usuario)) {
            error = "El usuario introducido ya forma parte de los participantes.";
            errorActivo = true;
        } else {
            this.participantes.add(usuario);
            error = "";
            errorActivo = false;
        }
        this.participanteNuevo = "";
        return "nuevoProyecto"; 
    }
    
   
    
    public String doCancelar() {
        this.init();
        return "principal";
    }
    
    public String doEliminarParticipante(Usuario usuario) {
        if(participantes.contains(usuario)) {
            participantes.remove(usuario);
        }
        return "nuevoProyecto";
    }
    
    public String doCrear(Usuario lider) {
        Date date = new Date();
        proyecto.setEstado("En Proceso");
        proyecto.setLider(lider);
        participantes.add(lider);
        proyecto.setUsuarioCollection(participantes);
        proyecto.setFechaInicio(date);
        if(proyecto.getNombre().equals("")) {
            proyecto.setNombre("[Sin nombre]");
        }
        if(proyecto.getDescripcion().equals("")) {
            proyecto.setDescripcion(("[Sin descripción]"));
        }
        proyectoFacade.create(proyecto);
    
        for (Usuario participante: participantes) {
            List<Proyecto> proyectosUsuarioParticipa = (List<Proyecto>) participante.getProyectoCollection();
            if(proyectosUsuarioParticipa == null) {
                proyectosUsuarioParticipa = new ArrayList();
            }
            proyectosUsuarioParticipa.add(proyecto); //Atento a cuando haces esto
            usuarioFacade.edit(participante);
        }  
        
        List<Proyecto> proyectosUsuarioLidera = (List<Proyecto>) lider.getProyectoCollection1();
        if(proyectosUsuarioLidera == null) {
            proyectosUsuarioLidera = new ArrayList();
        }
        proyectosUsuarioLidera.add(proyecto);
        usuarioFacade.edit(lider);
        
        
        //Meter en proyectos en los que lidera     ??????Seguro? Por lo visto se ha metido ya.
        //Añadir proyecto en los participantes, y fechaInicio
        //Arreglar el tema del ID
        this.init();
        return "principal";
    }

    public List<Usuario> getTodosLosUsuarios() {
        return todosLosUsuarios;
    }
    
    public List<Usuario> pillarUsuarios (String consulta)
    {
        consulta = consulta.toLowerCase();
        List<Usuario> listaFiltrada = new ArrayList<>();
        for (Usuario auxiliar : todosLosUsuarios)
        {
            if (auxiliar.getNickname().toLowerCase().contains(consulta))
            {
                listaFiltrada.add(auxiliar);
            }
        }
        return listaFiltrada;
    }
}
