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
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
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
    protected String errorEditar; //contendra el texto del error si se edita mal
    protected String mostrarEmail2;
    protected String dirImagen;
    protected String dirCuenta;
    
    //propiedades de control de errores del registro en ajax
    protected String nicknameNuevo;
    private final String USUARIO_NO_CONSULTADO = "no consultado";
    protected String color;
    protected String nombreIcono;
    private final String PASSWORD_NO_COMPROBADO = "no comprobado";
    protected String passwordComprobado;
    protected String colorPass;
    protected String iconoPass;
    private final String COLOR_BUENO = "#4ab8e8";
    private final String COLOR_MALO = "darkred";
    private final String ICONO_CHECK = "fa-check-circle";
    private final String ICONO_ERROR = "fa-times";
    protected String emailComprobado;
    private final String EMAIL_NO_COMPROBADO = "email no comprobado";
    protected String colorMail;
    protected String iconoMail;
    
    
    public UsuarioBean() {
        errorLogin = "";
        errorRegistro = "";
        errorRecuperar = "";
        errorEditar = "";
        mostrarEmail2 = "false";
        color = "grey";
        nombreIcono = "";
        passwordComprobado = PASSWORD_NO_COMPROBADO;
        colorPass = "grey";
        iconoPass = "";
        emailComprobado = EMAIL_NO_COMPROBADO;
        colorMail = "grey";
        iconoMail = "";
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

    public String getDirCuenta() {
        return dirCuenta;
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

    public String getErrorEditar() {
        return errorEditar;
    }

    public void setErrorEditar(String errorEditar) {
        this.errorEditar = errorEditar;
    }

    public String getMostrarEmail2() {
        return mostrarEmail2;
    }

    public void setMostrarEmail2(String mostrarEmail2) {
        this.mostrarEmail2 = mostrarEmail2;
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
        String redireccion;
        Usuario user = usuarioFacade.getUsuarioPorNickname(this.usuarioIntroducido);
        if (passwordMalicioso()) //si hay caracteres maliciosos en el password
        {
            errorLogin = "La contraseña contiene caracteres no permitidos";
            redireccion = "index.xhtml";
        }
        else //comprobamos el pass con el del usuario recuperado de la BD
        {
            if (user==null) //si no se ha encontrado al usuario.
            {
                errorLogin="El usuario introducido no existe";
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
                    errorLogin = "La contraseña es incorrecta";
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
        String salida;
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
                        salida = "principal.xhtml";
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

    public void comprobarPasswordAjax() {
        //metodo que comprueba que ambos passwords introducidos en el registro coinciden
        passwordComprobado = "comprobado";
        boolean resultado = passwordIntroducido.equals(passwordIntroducido2);
        if (resultado) //si coinciden
        {
            colorPass = COLOR_BUENO;
            iconoPass = ICONO_CHECK;
        }
        else //si no
        {
            colorPass = COLOR_MALO;
            iconoPass = ICONO_ERROR;
        }
    }
    
    private boolean comprobarPassword() {
        //metodo que comprueba que ambos passwords introducidos en el registro coinciden
        return passwordIntroducido.equals(passwordIntroducido2);
    }

    private boolean comprobarEmail() {
        return emailIntroducido.equals(emailIntroducido2);
    }
    
    public void comprobarEmailAjax() {
        emailComprobado = "comprobado";
        boolean resultado = emailIntroducido.equals(emailIntroducido2);
        if (resultado)
        {
            colorMail = COLOR_BUENO;
            iconoMail = ICONO_CHECK;
        }
        else
        {
            colorMail = COLOR_MALO;
            iconoMail = ICONO_ERROR;
        }
    }

    public String getDirImagen() {
        return dirImagen;
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
    
    public void enventoEmail(AjaxBehaviorEvent e){
        mostrarEmail2 = "true";
    }
    
    public void eventoUsuario(AjaxBehaviorEvent e){
        Usuario user = usuarioFacade.getUsuarioPorNickname(this.usuarioIntroducido);
        if(usuarioIntroducido.equals("")){
            errorLogin = "No has introducido usuario";
        }else if(user == null){
            errorLogin = "El usuario introducido no existe";
        }else{
           errorLogin = ""; 
        }
    }
    
    public void eventoPassword(AjaxBehaviorEvent e){
        Usuario user = usuarioFacade.getUsuarioPorNickname(this.usuarioIntroducido);
        if(usuarioIntroducido.equals("")){
            errorLogin = "No has introducido usuario";
        }else if(user == null){
            errorLogin = "El usuario introducido no existe";
        }else if (passwordMalicioso()){
            errorLogin = "La contraseña contiene caracteres no permitidos";
        }else{
           errorLogin = ""; 
        }
    }
    
    public String irEditarPass(){
        mostrarEmail2 = "false";
        errorEditar = "";
        return "/crearNuevoPassword.xhtml";
    }

    public String doEditar(){
        control();
        mostrarEmail2 = "false";
        errorEditar = "";
        String emailTemporal = usuarioFacade.getUsuarioPorNickname(usuario.getNickname()).getEmail();
        String retorno = "/editarPerfil.xhtml";     
        if(!usuario.getEmail().equals("") && usuario.getEmail() != null){
            if(!usuario.getPregunta().equals("") && usuario.getPregunta() != null){
                if(!usuario.getRespuesta().equals("") && usuario.getRespuesta() != null){
                    if(emailIntroducido2 == null){
                        usuario.setEmail(emailTemporal);
                        usuarioFacade.edit(usuario);    //El usuario solo se modifica si todo es correcto
                        retorno = "/perfil.xhtml";
                    }else if((emailIntroducido2.equals("") && emailTemporal.equals(usuario.getEmail())) || emailIntroducido2.equals(usuario.getEmail())){
                        usuarioFacade.edit(usuario);    //El usuario solo se modifica si todo es correcto
                        retorno = "/perfil.xhtml";
                    }else{
                        errorEditar = "Email introducido no valido";
                    }
                }else{
                    errorEditar = "Respuesta introducida no valida";
                }
            }else{
                errorEditar = "Pregunta introducida no valida";
            }
        }else{
            errorEditar = "Email introducido no valido";
        }
        emailIntroducido2 = "";
        usuario = usuarioFacade.getUsuarioPorNickname(usuario.getNickname());   //Recargamos el usuario
        return retorno;
    }
    
    public void direccionDeAvatar(){
        String email = usuario.getEmail();
        email = email.replace(" ", "");     //Elimino los espacios
        email = email.toLowerCase();        //Pongo todas las letras en minusculas
        //El tratamiento que ha recibido la cadena email es por exigencias de gravatar
        //--Todo debe estar en minusculas y sin espacios--
        String hash = hashEmail(email);     //Hago el hash
        dirImagen = "https://www.gravatar.com/avatar/" + hash + ".jpg?size=150";
    }
    
    public void direccionDeCuenta(){
        String email = usuario.getEmail();
        email = email.replace(" ", "");     //Elimino los espacios
        email = email.toLowerCase();        //Pongo todas las letras en minusculas
        //El tratamiento que ha recibido la cadena email es por exigencias de gravatar
        //--Todo debe estar en minusculas y sin espacios--
        String hash = hashEmail(email);     //Hago el hash
        dirCuenta = "https://www.gravatar.com/" + hash;
    }
    
    private String hashEmail(String entrada){
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");

            try {
                md5.update(entrada.getBytes("UTF-8"));

                byte[] digest = md5.digest();
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < digest.length; i++) {
                    sb.append(String.format("%02x", digest[i]));
                }
                String hash = sb.toString();

                return hash;
                
            } catch (UnsupportedEncodingException ex) {
                
            }
        } catch (NoSuchAlgorithmException ex) {
            
        }
        return "";
    }
    
    public void comprobarNombreUsuario ()
    {
        if (usuarioFacade.find(usuarioIntroducido)!=null)
        {
            color=COLOR_MALO;
            nombreIcono = ICONO_ERROR;
        }
        else
        {
            color=COLOR_BUENO;
            nombreIcono=ICONO_CHECK;
        }
    }

    public String getNicknameNuevo() {
        return nicknameNuevo;
    }

    public void setNicknameNuevo(String nicknameNuevo) {
        this.nicknameNuevo = nicknameNuevo;
    }

    public String getColor() {
        return color;
    }

    public String getNombreIcono() {
        return nombreIcono;
    }

    public String getUSUARIO_NO_CONSULTADO() {
        return USUARIO_NO_CONSULTADO;
    }

    public String getPASSWORD_NO_COMPROBADO() {
        return PASSWORD_NO_COMPROBADO;
    }

    public String getPasswordComprobado() {
        return passwordComprobado;
    }

    public String getColorPass() {
        return colorPass;
    }

    public String getIconoPass() {
        return iconoPass;
    }

    public String getCOLOR_BUENO() {
        return COLOR_BUENO;
    }

    public String getCOLOR_MALO() {
        return COLOR_MALO;
    }

    public String getICONO_CHECK() {
        return ICONO_CHECK;
    }

    public String getICONO_ERROR() {
        return ICONO_ERROR;
    }

    public String getEmailComprobado() {
        return emailComprobado;
    }



    public String getColorMail() {
        return colorMail;
    }

    public String getEMAIL_NO_COMPROBADO() {
        return EMAIL_NO_COMPROBADO;
    }



    public String getIconoMail() {
        return iconoMail;
    }


    
}
