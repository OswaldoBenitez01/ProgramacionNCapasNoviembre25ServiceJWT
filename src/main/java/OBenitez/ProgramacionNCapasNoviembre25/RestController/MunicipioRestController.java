package OBenitez.ProgramacionNCapasNoviembre25.RestController;

import OBenitez.ProgramacionNCapasNoviembre25.JPA.Result;
import OBenitez.ProgramacionNCapasNoviembre25.Service.MunicipioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/municipio")
public class MunicipioRestController {
    @Autowired
    private MunicipioService municipioService;
    
    @GetMapping("estado/{IdEstado}")
    public ResponseEntity GetMunicipiosByEstado(@PathVariable int IdEstado){
        Result result = municipioService.GetMunicipiosByEstado(IdEstado);
        return ResponseEntity.status(result.StatusCode).body(result);
    }
}
