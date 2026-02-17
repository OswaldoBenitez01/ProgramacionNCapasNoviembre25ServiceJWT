package OBenitez.ProgramacionNCapasNoviembre25.Service;

import OBenitez.ProgramacionNCapasNoviembre25.DAO.IColonia;
import OBenitez.ProgramacionNCapasNoviembre25.DAO.IDireccion;
import OBenitez.ProgramacionNCapasNoviembre25.DAO.IUsuarioJPA;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Colonia;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Direccion;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Result;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private IUsuarioJPA usuarioRepository;
    @Autowired
    private IDireccion direccionRepository;
    @Autowired
    private IColonia coloniaRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SeguridadControllerService seguridadService;

    // ======================= GET ======================
    public Result GetAll() {
    Result result = new Result();

        try {
            // Obtener usuario autenticado
            Result resultUsuario = seguridadService.obtenerUsuarioAutenticado();
            if (!resultUsuario.Correct) {
                return resultUsuario;
            }

            // Verificar que sea administrativo
            Authentication auth = seguridadService.obtenerAuthentication();
            if (!seguridadService.esAdministrativo(auth)) {
                result.Correct = false;
                result.ErrorMessage = "No tienes permisos para consultar todos los usuarios";
                result.StatusCode = 403;
                return result;
            }

            List<Usuario> usuarios = usuarioRepository.findAllByOrderByIdUsuarioAsc();

            result.Correct = true;
            result.StatusCode = 200;
            result.Objects = new ArrayList<>(usuarios); // puede ir vacía sin problema

        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = "Error interno del servidor: " + ex.getLocalizedMessage();
            result.ex = ex;
            result.StatusCode = 500;
        }
        return result;
    }


    public Result BusquedaAbierta(Usuario usuario) {
        Result result = new Result();
        
        try {
            // Obtener usuario autenticado
            Result resultUsuario = seguridadService.obtenerUsuarioAutenticado();
            if (!resultUsuario.Correct) {
                return resultUsuario;
            }
            
            // Verificar que sea administrativo
            Authentication auth = seguridadService.obtenerAuthentication();
            if (!seguridadService.esAdministrativo(auth)) {
                result.Correct = false;
                result.ErrorMessage = "No tienes permisos para realizar búsquedas de usuarios";
                result.StatusCode = 403;
                return result;
            }
            
            String nombre = (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) ? null : usuario.getNombre();
            String apellidoPaterno = (usuario.getApellidoPaterno() == null || usuario.getApellidoPaterno().trim().isEmpty()) ? null : usuario.getApellidoPaterno();
            String apellidoMaterno = (usuario.getApellidoMaterno() == null || usuario.getApellidoMaterno().trim().isEmpty()) ? null : usuario.getApellidoMaterno();
            Integer idRol = (usuario.getRol() == null || usuario.getRol().getIdRol() == null) ? null : usuario.getRol().getIdRol();
        
            List<Usuario> usuarios = usuarioRepository.busquedaAbierta(
                    nombre,
                    apellidoPaterno,
                    apellidoMaterno,
                    idRol
            );
            
            if (usuarios.isEmpty()) {
                result.Correct = false;
                result.ErrorMessage = "No se encontraron usuarios";
                result.Objects = new ArrayList<>();
                result.StatusCode = 200;
                return result;
            }
            
            result.Objects = new ArrayList<>(usuarios);
            result.Correct = true;
            result.StatusCode = 200;
            
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = "Error interno del servidor: " + ex.getLocalizedMessage();
            result.ex = ex;
            result.StatusCode = 500;
        }
        return result;
    }

    public Result GetById(Integer idUsuario) {
        Result result = new Result();

        try {
            // Validar ID
            if (idUsuario == null || idUsuario <= 0) {
                result.Correct = false;
                result.ErrorMessage = "Debe proporcionar un ID de usuario válido";
                result.StatusCode = 400;
                return result;
            }
            
            // Obtener usuario autenticado
            Result resultUsuario = seguridadService.obtenerUsuarioAutenticado();
            if (!resultUsuario.Correct) {
                return resultUsuario;
            }
            
            Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);
            if (!usuario.isPresent()) {
                result.Correct = false;
                result.ErrorMessage = "Usuario no encontrado";
                result.StatusCode = 404;
                return result;
            }
            
            // Verificar permisos (propio o admin)
            Result resultPermiso = seguridadService.verificarPermisoRecurso(
                idUsuario,
                "No tienes permisos para consultar este usuario"
            );
            if (!resultPermiso.Correct) {
                return resultPermiso;
            }
            
            result.Object = usuario.get();
            result.Correct = true;
            result.StatusCode = 200;
            
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = "Error interno del servidor: " + ex.getLocalizedMessage();
            result.ex = ex;
            result.StatusCode = 500;
        }
        return result;
    }

    public Result LogInUsuario(String username) {
        Result result = new Result();

        try {
            Usuario usuario = usuarioRepository.findByUsername(username);
            if (usuario == null || usuario.getIdUsuario() == 0) {
                result.Correct = false;
                result.ErrorMessage = "Usuario no encontrado";
                result.StatusCode = 404;
                return result;
            }
            
            result.Object = usuario;
            result.Correct = true;
            result.StatusCode = 200;
            
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = "Error interno del servidor: " + ex.getLocalizedMessage();
            result.ex = ex;
            result.StatusCode = 500;
        }
        return result;
    }

    // ===================== ADD ====================
    public Result Add(Usuario usuario) {
        Result result = new Result();
        
        try {
            // Validar estructur
            if (usuario == null) {
                result.Correct = false;
                result.ErrorMessage = "Debe proporcionar un usuario válido";
                result.StatusCode = 400;
                return result;
            }
            
            // Obtener usuario autenticado
            Result resultUsuario = seguridadService.obtenerUsuarioAutenticado();
            if (!resultUsuario.Correct) {
                return resultUsuario;
            }
            
            // Verificar que sea administrativo
            Authentication auth = seguridadService.obtenerAuthentication();
            if (!seguridadService.esAdministrativo(auth)) {
                result.Correct = false;
                result.ErrorMessage = "No tienes permisos para crear usuarios";
                result.StatusCode = 403;
                return result;
            }
            
            usuario.setIdUsuario(null);
            
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            }

            if (usuario.getDirecciones() != null && !usuario.getDirecciones().isEmpty()) {
                for (Direccion direccion : usuario.getDirecciones()) {
                    direccion.setUsuario(usuario);
                    if (direccion.colonia != null && direccion.colonia.getIdColonia() != 0) {
                        Colonia coloniadb = coloniaRepository.findById(direccion.getColonia().getIdColonia()).orElse(null);
                        direccion.setColonia(coloniadb);
                    }
                }
            }
            
            Usuario saved = usuarioRepository.save(usuario);
            result.Object = saved;
            result.Correct = true;
            result.StatusCode = 201;
            
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = "Error al crear usuario: " + ex.getLocalizedMessage();
            result.ex = ex;
            result.StatusCode = 400;
        }
        return result;
    }

    public Result AddAll(List<Usuario> usuarios) {
        Result result = new Result();

        try {
            // Validar lista
            if (usuarios == null || usuarios.isEmpty()) {
                result.Correct = false;
                result.ErrorMessage = "Debe proporcionar una lista de usuarios válida";
                result.StatusCode = 400;
                return result;
            }
            
            // Obtener usuario autenticado
            Result resultUsuario = seguridadService.obtenerUsuarioAutenticado();
            if (!resultUsuario.Correct) {
                return resultUsuario;
            }
            
            // Verificar que sea administrativo
            Authentication auth = seguridadService.obtenerAuthentication();
            if (!seguridadService.esAdministrativo(auth)) {
                result.Correct = false;
                result.ErrorMessage = "No tienes permisos para crear múltiples usuarios";
                result.StatusCode = 403;
                return result;
            }
            
            for (Usuario usuarioNuevo : usuarios) {
                Direccion direccionNueva = usuarioNuevo.getDirecciones().get(0);
                usuarioNuevo.setDirecciones(null);
                usuarioNuevo.setStatus(1);
                usuarioRepository.saveAndFlush(usuarioNuevo);

                direccionNueva.setUsuario(new Usuario());
                direccionNueva.getUsuario().setIdUsuario(usuarioNuevo.getIdUsuario());
                direccionRepository.save(direccionNueva);
            }
            
            result.Correct = true;
            result.StatusCode = 201;
            
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = "Error al crear usuarios: " + ex.getLocalizedMessage();
            result.ex = ex;
            result.StatusCode = 400;
        }
        return result;
    }

    // ===================== UPDATE ====================
    public Result UpdateUser(Usuario usuario) {
        Result result = new Result();
        
        try {
            // Validar estructura básica
            if (usuario == null || usuario.getIdUsuario() == null || usuario.getIdUsuario() <= 0) {
                result.Correct = false;
                result.ErrorMessage = "Debe proporcionar un ID de usuario válido";
                result.StatusCode = 400;
                return result;
            }
            
            // Obtener usuario autenticado
            Result resultUsuario = seguridadService.obtenerUsuarioAutenticado();
            if (!resultUsuario.Correct) {
                return resultUsuario;
            }
            
            // Buscar usuario existente
            Optional<Usuario> usuarioDB = usuarioRepository.findById(usuario.getIdUsuario());
            if (usuarioDB.isEmpty()) {
                result.Correct = false;
                result.ErrorMessage = "Usuario no encontrado";
                result.StatusCode = 404;
                return result;
            }
            
            // Verificar permisos (propio o admin)
            Result resultPermiso = seguridadService.verificarPermisoRecurso(
                usuario.getIdUsuario(),
                "No tienes permisos para modificar este usuario"
            );
            if (!resultPermiso.Correct) {
                return resultPermiso;
            }

            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            } else {
                usuario.setPassword(usuarioDB.get().getPassword());
            }

            usuario.setDirecciones(usuarioDB.get().getDirecciones());
            usuario.setImagen(usuarioDB.get().getImagen());
            
            Usuario updated = usuarioRepository.save(usuario);
            result.Object = updated;
            result.Correct = true;
            result.StatusCode = 200;
            
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = "Error al actualizar usuario: " + ex.getLocalizedMessage();
            result.ex = ex;
            result.StatusCode = 400;
        }
        return result;
    }

    public Result UpdateStatus(Integer idUsuario, Integer status) {
        Result result = new Result();
        
        try {
            // Validar parámetros
            if (idUsuario == null || idUsuario <= 0) {
                result.Correct = false;
                result.ErrorMessage = "Debe proporcionar un ID de usuario válido";
                result.StatusCode = 400;
                return result;
            }
            
            if (status == null) {
                result.Correct = false;
                result.ErrorMessage = "Debe proporcionar un status válido";
                result.StatusCode = 400;
                return result;
            }
            
            // Obtener usuario autenticado
            Result resultUsuario = seguridadService.obtenerUsuarioAutenticado();
            if (!resultUsuario.Correct) {
                return resultUsuario;
            }
            
            // Verificar que sea administrativo (solo admins pueden cambiar status)
            Authentication auth = seguridadService.obtenerAuthentication();
            if (!seguridadService.esAdministrativo(auth)) {
                result.Correct = false;
                result.ErrorMessage = "No tienes permisos para cambiar el estado de usuarios";
                result.StatusCode = 403;
                return result;
            }
            
            // Buscar usuario
            Optional<Usuario> usuarioDB = usuarioRepository.findById(idUsuario);
            if (usuarioDB.isEmpty()) {
                result.Correct = false;
                result.ErrorMessage = "Usuario no encontrado";
                result.StatusCode = 404;
                return result;
            }
            
            usuarioDB.get().setStatus(status);
            usuarioRepository.save(usuarioDB.get());
            
            result.Correct = true;
            result.StatusCode = 200;
            
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = "Error al actualizar estado: " + ex.getLocalizedMessage();
            result.ex = ex;
            result.StatusCode = 400;
        }
        return result;
    }

    public Result UpdatePhoto(Integer idUsuario, String foto) {
        Result result = new Result();

        try {
            // Validar parámetros
            if (idUsuario == null || idUsuario <= 0) {
                result.Correct = false;
                result.ErrorMessage = "Debe proporcionar un ID de usuario válido";
                result.StatusCode = 400;
                return result;
            }
            
            if (foto == null || foto.trim().isEmpty()) {
                result.Correct = false;
                result.ErrorMessage = "Debe proporcionar una foto válida";
                result.StatusCode = 400;
                return result;
            }
            
            // Obtener usuario autenticado
            Result resultUsuario = seguridadService.obtenerUsuarioAutenticado();
            if (!resultUsuario.Correct) {
                return resultUsuario;
            }
            
            // Buscar usuario
            Optional<Usuario> usuarioDB = usuarioRepository.findById(idUsuario);
            if (usuarioDB.isEmpty()) {
                result.Correct = false;
                result.ErrorMessage = "Usuario no encontrado";
                result.StatusCode = 404;
                return result;
            }
            
            // Verificar permisos (propio o admin)
            Result resultPermiso = seguridadService.verificarPermisoRecurso(
                idUsuario,
                "No tienes permisos para modificar la foto de este usuario"
            );
            if (!resultPermiso.Correct) {
                return resultPermiso;
            }

            usuarioDB.get().setImagen(foto);
            usuarioRepository.save(usuarioDB.get());
            
            result.Correct = true;
            result.StatusCode = 200;
            
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = "Error al actualizar foto: " + ex.getLocalizedMessage();
            result.ex = ex;
            result.StatusCode = 400;
        }
        return result;
    }

    // ===================== DELETE ====================
    public Result DeleteById(Integer idUsuario) {
        Result result = new Result();
        
        try {
            // Validar ID
            if (idUsuario == null || idUsuario <= 0) {
                result.Correct = false;
                result.ErrorMessage = "Debe proporcionar un ID de usuario válido";
                result.StatusCode = 400;
                return result;
            }
            
            // Obtener usuario autenticado
            Result resultUsuario = seguridadService.obtenerUsuarioAutenticado();
            if (!resultUsuario.Correct) {
                return resultUsuario;
            }
            
            Usuario usuarioJwt = (Usuario) resultUsuario.Object;
            
            // No permitir que un usuario se elimine a sí mismo
            if (usuarioJwt.getIdUsuario().equals(idUsuario)) {
                result.Correct = false;
                result.ErrorMessage = "No puedes eliminar tu propio usuario";
                result.StatusCode = 403;
                return result;
            }
            
            // Verificar que sea administrativo (solo admins pueden eliminar)
            Authentication auth = seguridadService.obtenerAuthentication();
            if (!seguridadService.esAdministrativo(auth)) {
                result.Correct = false;
                result.ErrorMessage = "No tienes permisos para eliminar usuarios";
                result.StatusCode = 403;
                return result;
            }
            
            // Buscar usuario a eliminar
            Optional<Usuario> usuarioDB = usuarioRepository.findById(idUsuario);
            if (usuarioDB.isEmpty()) {
                result.Correct = false;
                result.ErrorMessage = "Usuario no encontrado";
                result.StatusCode = 404;
                return result;
            }

            usuarioRepository.deleteById(idUsuario);
            
            result.Correct = true;
            result.StatusCode = 200;
            
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = "Error al eliminar usuario: " + ex.getLocalizedMessage();
            result.ex = ex;
            result.StatusCode = 500;
        }
        return result;
    }
}
