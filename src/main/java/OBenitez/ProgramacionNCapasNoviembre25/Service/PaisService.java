package OBenitez.ProgramacionNCapasNoviembre25.Service;

import OBenitez.ProgramacionNCapasNoviembre25.DAO.IPais;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Pais;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Result;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaisService {
    @Autowired
    private IPais paisRepository;
    
    public Result GetAll(){
        Result result = new Result();
        
        try {
            List<Pais> paises = paisRepository.findAll();
            if (paises.isEmpty()) {
                result.Correct = false;
                result.ErrorMessage = "No se encontraron paises";
                result.Objects = new ArrayList<>();
                result.StatusCode = 404;
                return result;
            }
            result.Objects = new ArrayList<>(paises);
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
