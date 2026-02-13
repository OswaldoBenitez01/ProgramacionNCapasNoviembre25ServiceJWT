package OBenitez.ProgramacionNCapasNoviembre25.Service;

import OBenitez.ProgramacionNCapasNoviembre25.DAO.IUsuarioJPA;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SeguridadControllerService {
    @Autowired
    private IUsuarioJPA usuarioRepository;

    public boolean tieneRol(Authentication auth, String rol) {
        String rolBuscado = "ROLE_" + rol;
        for (GrantedAuthority authority : auth.getAuthorities()) {
            if (rolBuscado.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
    

    public boolean esAdministrativo(Authentication auth) {
        return tieneRol(auth, "Director") || tieneRol(auth, "Administrador");
    }
    

    public boolean esPropioOAdmin(int idUsuarioActivo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (esAdministrativo(auth)) {
            return true;
        }
        
        String email = auth.getName();
        Usuario usuarioDB = usuarioRepository.findByEmail(email);
        return usuarioDB != null && usuarioDB.getIdUsuario() == idUsuarioActivo;
    }
    
    public Result obtenerUsuarioAutenticado() {
        Result result = new Result();
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            if (auth == null || auth.getName() == null) {
                result.Correct = false;
                result.ErrorMessage = "No se encontró autenticación válida";
                result.StatusCode = 401;
                return result;
            }
            
            String email = auth.getName();
            Usuario usuario = usuarioRepository.findByEmail(email);
            
            if (usuario == null) {
                result.Correct = false;
                result.ErrorMessage = "Usuario no autenticado";
                result.StatusCode = 401;
                return result;
            }
            
            result.Correct = true;
            result.Object = usuario;
            result.StatusCode = 200;
            
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = "Error al obtener usuario autenticado: " + ex.getLocalizedMessage();
            result.ex = ex;
            result.StatusCode = 500;
        }
        
        return result;
    }
    

    public Result verificarPermisoRecurso(int idUsuarioPropietario, String mensajeError) {
        Result result = new Result();
        
        // Obtener usuario autenticado
        Result resultUsuario = obtenerUsuarioAutenticado();
        if (!resultUsuario.Correct) {
            return resultUsuario;
        }
        
        Usuario usuarioJwt = (Usuario) resultUsuario.Object;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Verificar si es administrativo
        boolean esAdmin = esAdministrativo(auth);
        
        // Verificar si es propietario o admin
        if (!esAdmin && !usuarioJwt.getIdUsuario().equals(idUsuarioPropietario)) {
            result.Correct = false;
            result.ErrorMessage = mensajeError != null ? mensajeError : "No tienes permisos para realizar esta acción";
            result.StatusCode = 403;
            return result;
        }
        
        result.Correct = true;
        result.Object = usuarioJwt;
        result.StatusCode = 200;
        return result;
    }
    

    public Authentication obtenerAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
