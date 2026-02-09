package OBenitez.ProgramacionNCapasNoviembre25.Service;

import OBenitez.ProgramacionNCapasNoviembre25.DAO.IColonia;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Colonia;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Result;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColoniaService {
    @Autowired
    private IColonia coloniaRepository;
    
    public Result GetColoniasByMunicipio(int idMunicipio){
        Result result = new Result();
        
        try {
            List<Colonia> colonias = coloniaRepository.findByMunicipioIdMunicipio(idMunicipio);
            
            if (colonias == null || colonias.isEmpty()) {
                result.Correct = false;
                result.ErrorMessage = "No se encontraron colonias para este municipio";
                result.StatusCode = 404;
                return result;
            }
            
            result.Objects = new ArrayList<>(colonias);
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
