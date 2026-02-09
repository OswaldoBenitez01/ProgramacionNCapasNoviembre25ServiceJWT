
package OBenitez.ProgramacionNCapasNoviembre25.RestController;

import OBenitez.ProgramacionNCapasNoviembre25.JPA.Result;
import OBenitez.ProgramacionNCapasNoviembre25.Service.ColoniaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/colonia")
public class ColoniaRestController {
    @Autowired
    private ColoniaService coloniaService;
    
    @GetMapping("municipio/{IdMunicipio}")
    public ResponseEntity GetColoniasByMunicipio(@PathVariable int IdMunicipio){
        Result result = coloniaService.GetColoniasByMunicipio(IdMunicipio);
        return ResponseEntity.status(result.StatusCode).body(result);
    }
}
