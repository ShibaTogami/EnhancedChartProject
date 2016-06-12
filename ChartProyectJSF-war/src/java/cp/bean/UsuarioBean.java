/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cp.bean;

import cp.ejb.UsuarioFacade;
import cp.entity.Proyecto;
import cp.entity.Usuario;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author shiba
 */
@ManagedBean (name = "usuarioBean")
@SessionScoped
public class UsuarioBean implements Serializable {

    @EJB
    private UsuarioFacade usuarioFacade;

    /**
     * Creates a new instance of UsuarioBean
     */
    
    protected Usuario usuario; //usuario Logueado
    protected String usuarioIntroducido; //usuario introducido en la web de log in o registro
    protected String passwordIntroducido; //pass introducido en la web de log in o en registro
    protected String passwordIntroducido2; //pass introducido en la web de log in o registro
    protected String emailIntroducido; //email introducidor en registro
    protected String emailIntroducido2; //repeticion del email para controlar errores
    protected String preguntaSecretaIntroducida; //pregunta de seguridad para recuperar contraseña en registro
    protected String respuestaSecretaIntroducida; //respuesta para recuperar contraseña en registor
    protected String errorLogin; //contendra el texto del error por el cual el login puede fallar
    protected String errorRegistro; //contendrá el texto a mostrar en caso de error al registrarse
    protected String errorRecuperar; //contendrá el texto del error por el que falló la recuperación de contraseña
    protected Proyecto proyectoSeleccionado; //proyecto que tiene seleccionado el usuario
    protected final String cadenaVacia=""; //cadena para usar en comparaciones
    
    public UsuarioBean() {
        errorLogin="";
        errorRegistro="";
        errorRecuperar="";
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getUsuarioIntroducido() {
        return usuarioIntroducido;
    }

    public void setUsuarioIntroducido(String usuarioIntroducido) {
        this.usuarioIntroducido = usuarioIntroducido;
    }

    public String getPasswordIntroducido() {
        return passwordIntroducido;
    }

    public void setPasswordIntroducido(String passwordIntroducido) {
        this.passwordIntroducido = passwordIntroducido;
    }

    public String getPasswordIntroducido2() {
        return passwordIntroducido2;
    }

    public void setPasswordIntroducido2(String passwordIntroducido2) {
        this.passwordIntroducido2 = passwordIntroducido2;
    }

    public String getEmailIntroducido() {
        return emailIntroducido;
    }

    public void setEmailIntroducido(String emailIntroducido) {
        this.emailIntroducido = emailIntroducido;
    }

    public String getEmailIntroducido2() {
        return emailIntroducido2;
    }

    public void setEmailIntroducido2(String emailIntroducido2) {
        this.emailIntroducido2 = emailIntroducido2;
    }

    public String getPreguntaSecretaIntroducida() {
        return preguntaSecretaIntroducida;
    }

    public void setPreguntaSecretaIntroducida(String preguntaSecretaIntroducida) {
        this.preguntaSecretaIntroducida = preguntaSecretaIntroducida;
    }

    public String getRespuestaSecretaIntroducida() {
        return respuestaSecretaIntroducida;
    }

    public void setRespuestaSecretaIntroducida(String respuestaSecretaIntroducida) {
        this.respuestaSecretaIntroducida = respuestaSecretaIntroducida;
    }

    public String doLoguear()
    {
        String redireccion = null;
        Usuario user = usuarioFacade.getUsuarioPorNickname(this.usuarioIntroducido);
        if (passwordMalicioso()) //si hay caracteres maliciosos en el password
        {
            errorLogin = "Error: La contraseña contiene caracteres no permitidos";
            redireccion = "index.xhtml";
        }
        else //comprobamos el pass con el del usuario recuperado de la BD
        {
            if (user==null) //si no se ha encontrado al usuario.
            {
                errorLogin="El usuario Introducido no existe";
                redireccion = "index.xhtml";
            }
            else
            {
                //si el usuario existe y los passwords coinciden
                if (user.getPassword().equals(this.passwordIntroducido))
                {
                    this.usuario = user;
                    redireccion = "principal.xhtml";
                }
                else //si no coincide
                {
                    errorLogin = "Error: La contraseña es incorrecta";
                    redireccion = "index.xhtml";
                }
            }
            //vaciamos datos irrelevantes. Obtenibles desde usuario.
            this.usuarioIntroducido=null;
            this.passwordIntroducido =null;
        }
        return redireccion;
    }
    
    public String doRegistrar() throws InterruptedException
    {
        String salida="";
        if (usuarioIntroducido.equals("") || emailIntroducido.equals("") || emailIntroducido2.equals("") 
           || passwordIntroducido.equals("") || passwordIntroducido2.equals("") || preguntaSecretaIntroducida.equals("") 
           || respuestaSecretaIntroducida.equals(""))
        {//comprobando campos null
            errorRegistro="Error: Todos los campos son obligatorios.";
            salida = "registro.xhtml";
        }
        else if (usuarioFacade.getUsuarioPorNickname(usuarioIntroducido)==null) //si el usuario es nuevo
        {    
            if (!passwordMalicioso()) //si el password no es malicioso
            {
                if (comprobarPassword()) //y coincide
                {
                    if (comprobarEmail()) //comprobamos el email y si coincide
                    {
                        //registramos
                        usuario = new Usuario(usuarioIntroducido,passwordIntroducido,new Date(),emailIntroducido);
                        usuario.setPregunta(preguntaSecretaIntroducida);
                        usuario.setRespuesta(respuestaSecretaIntroducida);
                        usuario.setUltimaConexion(new Date());
                        usuarioFacade.create(usuario);
                        //vaciar propiedades no necesarias con valores residuales
               
                        emailIntroducido=null;
                        preguntaSecretaIntroducida=null;
                        respuestaSecretaIntroducida=null;
                        passwordIntroducido2=null;
                        emailIntroducido2=null;

                        this.doLoguear();
                    }
                    else
                    {
                        //indicar error al usuario
                        errorRegistro="Error: El email Introducido no se ha repetido correctamente";
                        salida = "registro.xhtml";
                    }
                }
                else
                {
                    //indicar error al usuario
                    errorRegistro = "Error: Las constraseñas introducidas no coinciden";
                    salida = "registro.xhtml";
                }
            }
            else 
            {
                //indicar error al usuario
                errorRegistro = "Error: La contraseña introducida contiene caracteres no permitidos";
                salida = "registro.xhtml";
            }
        }
        else
        {
            errorRegistro = "Error: Ese usuario ya había sido registrado.";
            salida = "registro.xhtml";
        }
        return salida;
    }

    private boolean passwordMalicioso() { //metodo que comprueba la presencia de caracteres
        //maliciosos en el password introducido
        boolean salida = false; //empezamos dando por hecho que no los contiene
        for (char aux : passwordIntroducido.toCharArray()) {
            if (aux == '\'' || aux == '"' || aux == '+' || aux == '-' || aux == '*' || aux == '/' || aux == '%') {
                salida = true; //si se encuentra con algun caracter no permitido se actualiza la variable
            }
        }
        return salida;
    }

    private boolean comprobarPassword() {
        //metodo que comprueba que ambos passwords introducidos en el registro coinciden
        return passwordIntroducido.equals(passwordIntroducido2);
    }

    private boolean comprobarEmail() {
        return emailIntroducido.equals(emailIntroducido2);
    }

    public String getErrorLogin() {
        return errorLogin;
    }

    public void setErrorLogin(String errorLogin) {
        this.errorLogin = errorLogin;
    }

    public String getErrorRegistro() {
        return errorRegistro;
    }

    public void setErrorRegistro(String errorRegistro) {
        this.errorRegistro = errorRegistro;
    }

    public Proyecto getProyectoSeleccionado() {
        return proyectoSeleccionado;
    }

    public void setProyectoSeleccionado(Proyecto proyectoSeleccionado) {
        this.proyectoSeleccionado = proyectoSeleccionado;
    }
    
    public String doRecuperar()
    {
        String salida="recuperarPassword.xhtml";
        if (this.respuestaSecretaIntroducida.equals(usuario.getRespuesta()))
        {
            salida="crearNuevoPassword.xhtml";
        }
        else
        { //reseteamos campos y establecemos error
            usuarioIntroducido=null;
            respuestaSecretaIntroducida=null;
            errorRecuperar="Error: La respuesta secreta no es correcta.";
            usuario=null;
        }
        return salida;
    }
    
    public String doConsultar()
    {
        String salida="recuperarPassword.xhtml";
        usuario = usuarioFacade.getUsuarioPorNickname(usuarioIntroducido);
        if (usuario==null)
        {
            errorRecuperar="Error: El usuario introducido no está en la base de datos";
            usuarioIntroducido=null;
        }
        return salida;
    }

    public String getErrorRecuperar() {
        return errorRecuperar;
    }

    public void setErrorRecuperar(String errorRecuperar) {
        this.errorRecuperar = errorRecuperar;
    }
   
    public String doNuevaPassword()
    {
        String salida="principal.xhtml";
        if (!passwordMalicioso()) //si no hay caracteres maliciosos en el password
        {
            if (passwordIntroducido.equals(passwordIntroducido2)) //y las contraseñas coinciden
            {
                usuario.setPassword(passwordIntroducido);
                usuarioFacade.edit(usuario);
            }
            else
            {
                errorRecuperar="Error: Las contraseñas no coinciden.";
                salida="crearNuevoPassword.xhtml";
            }
        }
        else
        {
            errorRecuperar="Error: Esa contraseña contiene caracteres no válidos.";
            salida="crearNuevoPassword.xhtml";
        }
        return salida;
    }
    
    public void control(){
        if(usuario == null){
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(UsuarioBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //IMPORTANTE -- IMPORTANTE -- IMPORTANTE
    public String doLogout(){
        usuario = null;
        
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        Object session = externalContext.getSession(false);
        HttpSession httpSession = (HttpSession) session;
        httpSession.invalidate();
        
        return "/index.xhtml";    
    }

    public String getCadenaVacia() {
        return cadenaVacia;
    }

    
    
}
