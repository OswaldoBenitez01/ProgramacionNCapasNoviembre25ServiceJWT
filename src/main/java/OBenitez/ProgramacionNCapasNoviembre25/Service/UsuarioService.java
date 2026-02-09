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

    // ======================= GET ======================
    public Result GetAll() {
        Result result = new Result();

        try {
            List<Usuario> usuarios = usuarioRepository.findAllByOrderByIdUsuarioAsc();
            if (usuarios.isEmpty()) {
                result.Correct = false;
                result.ErrorMessage = "No se encontraron usuarios";
                result.Objects = new ArrayList<>();
                result.StatusCode = 404;
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

    public Result BusquedaAbierta(Usuario usuario) {
        Result result = new Result();
        try {
            String nombre = (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) ? null : usuario.getNombre();
            String apellidoPaterno = (usuario.getApellidoPaterno() == null || usuario.getApellidoPaterno().trim().isEmpty()) ? null : usuario.getApellidoPaterno();
            String apellidoMaterno = (usuario.getApellidoMaterno() == null || usuario.getApellidoMaterno().trim().isEmpty()) ? null : usuario.getApellidoMaterno();
            Integer idRol = (usuario.getRol() == null || usuario.getRol().getIdRol() == null) ? null : usuario.getRol().getIdRol();

            List<Usuario> usuarios = usuarioRepository.busquedaAbierta(nombre, apellidoPaterno, apellidoMaterno, idRol);

            if (usuarios.isEmpty()) {
                result.Correct = false;
                result.ErrorMessage = "No se encontraron usuarios";
                result.Objects = new ArrayList<>();
                result.StatusCode = 404;
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

    public Result GetById(Integer IdUsuario) {
        Result result = new Result();

        try {
            Optional<Usuario> usuario = usuarioRepository.findById(IdUsuario);
            if (!usuario.isPresent()) {
                result.Correct = false;
                result.ErrorMessage = "Usuario no encontrado";
                result.StatusCode = 404;
                return result;
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
        usuario.setIdUsuario(null);
        
        try {
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
            Optional<Usuario> usuarioDB = usuarioRepository.findById(usuario.getIdUsuario());
            if (usuarioDB.isEmpty()) {
                result.Correct = false;
                result.ErrorMessage = "Usuario no encontrado";
                result.StatusCode = 404;
                return result;
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

    public Result UpdateStatus(Integer IdUsuario, Integer Status) {
        Result result = new Result();
        try {
            Optional<Usuario> usuarioDB = usuarioRepository.findById(IdUsuario);
            if (usuarioDB.isEmpty()) {
                result.Correct = false;
                result.ErrorMessage = "Usuario no encontrado";
                result.StatusCode = 404;
                return result;
            }
            usuarioDB.get().setStatus(Status);
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

    public Result UpdatePhoto(Integer IdUsuario, String Foto) {
        Result result = new Result();

        try {
            Optional<Usuario> usuarioDB = usuarioRepository.findById(IdUsuario);
            if (usuarioDB.isEmpty()) {
                result.Correct = false;
                result.ErrorMessage = "Usuario no encontrado";
                result.StatusCode = 404;
                return result;
            }

            usuarioDB.get().setImagen(Foto);
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
    public Result DeleteById(Integer IdUsuario) {
        Result result = new Result();
        try {
            Optional<Usuario> usuarioDB = usuarioRepository.findById(IdUsuario);
            if (usuarioDB.isEmpty()) {
                result.Correct = false;
                result.ErrorMessage = "Usuario no encontrado";
                result.StatusCode = 404;
                return result;
            }

            usuarioRepository.deleteById(IdUsuario);
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
