package OBenitez.ProgramacionNCapasNoviembre25.Service;

import OBenitez.ProgramacionNCapasNoviembre25.DAO.IColonia;
import OBenitez.ProgramacionNCapasNoviembre25.DAO.IDireccion;
import OBenitez.ProgramacionNCapasNoviembre25.DAO.IUsuarioJPA;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Colonia;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Direccion;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Result;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class DireccionService {
    @Autowired
    private IColonia coloniaRepository;
    @Autowired
    private IDireccion direccionRepository;
    @Autowired
    private IUsuarioJPA usuarioRepository;
    
    public Result AddAddress(Direccion direccion) {
        Result result = new Result();
        
        // Obtener usuario del JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usernameJwt = auth.getName();

        Usuario usuarioJwt = usuarioRepository.findByEmail(usernameJwt); // o como busques
        if (!usuarioJwt.getIdUsuario().equals(direccion.usuario.getIdUsuario())) {
            result.Correct = false;
            result.ErrorMessage = "No puedes modificar otro usuario";
            result.StatusCode = 403;
            return result;
        }
        
        try {  
            if (direccion == null || direccion.usuario == null || direccion.usuario.getIdUsuario() == null) {
                result.Correct = false;
                result.ErrorMessage = "La direccion debe incluir un usuario valido";
                result.StatusCode = 400;
                return result;
            }
            
            Optional<Usuario> usuarioDB = usuarioRepository.findById(direccion.usuario.getIdUsuario());
            if (usuarioDB.isEmpty()) {
                result.Correct = false;
                result.ErrorMessage = "Usuario no encontrado";
                result.StatusCode = 404;
                return result;
            }
            
            if (direccion.colonia != null && direccion.colonia.getIdColonia() != 0) {
                Optional<Colonia> coloniaDB = coloniaRepository.findById(direccion.colonia.getIdColonia());
                if (coloniaDB.isPresent()) {
                    direccion.setColonia(coloniaDB.get());
                }
            }
            
            direccion.setUsuario(usuarioDB.get());
            direccionRepository.save(direccion);
            result.Object = direccion;
            result.Correct = true;
            result.StatusCode = 201;
        } catch (Exception ex) {
            result.Correct = false;
            result.ErrorMessage = "Error interno del servidor: " + ex.getLocalizedMessage();
            result.ex = ex;
            result.StatusCode = 500;
        }
        return result;
    }
    
    public Result UpdateAddressById(Direccion direccion) {
        Result result = new Result();
        
        // Obtener usuario del JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usernameJwt = auth.getName();

        Usuario usuarioJwt = usuarioRepository.findByEmail(usernameJwt); // o como busques
        if (!usuarioJwt.getIdUsuario().equals(direccion.usuario.getIdUsuario())) {
            result.Correct = false;
            result.ErrorMessage = "No puedes modificar otro usuario";
            result.StatusCode = 403;
            return result;
        }
        
        try {
            Optional<Direccion> direccionDB = direccionRepository.findById(direccion.getIdDireccion());
            
            if (direccionDB.isEmpty()) {
                result.Correct = false;
                result.ErrorMessage = "Direccion no encontrada";
                result.StatusCode = 404;
                return result;
            }
            
            direccionDB.get().setCalle(direccion.getCalle());
            direccionDB.get().setNumeroInterior(direccion.getNumeroInterior());
            direccionDB.get().setNumeroExterior(direccion.getNumeroExterior());
            if (direccion.colonia != null && direccion.colonia.getIdColonia() != 0) {
                Optional<Colonia> coloniaDB = coloniaRepository.findById(direccion.colonia.getIdColonia());
                if (coloniaDB.isPresent()) {
                    direccionDB.get().setColonia(coloniaDB.get());
                }
            }
            
            direccionRepository.save(direccionDB.get());
            result.Object = direccionDB.get();
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
    
    public Result DeleteAddressById(int IdDireccion) {
        Result result = new Result();
        
        try {
            Optional<Direccion> direccionDB = direccionRepository.findById(IdDireccion);
            
            if (direccionDB.isEmpty()) {
                result.Correct = false;
                result.ErrorMessage = "Direccion no encontrada";
                result.StatusCode = 404;
                return result;
            }
            
            direccionRepository.deleteById(IdDireccion);
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
}
