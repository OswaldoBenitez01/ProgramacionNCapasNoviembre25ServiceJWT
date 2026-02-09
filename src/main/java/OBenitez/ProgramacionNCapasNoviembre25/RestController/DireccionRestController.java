package OBenitez.ProgramacionNCapasNoviembre25.RestController;

import OBenitez.ProgramacionNCapasNoviembre25.JPA.Direccion;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Result;
import OBenitez.ProgramacionNCapasNoviembre25.Service.DireccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/direccion")
public class DireccionRestController {
    
    @Autowired
    private DireccionService direccionService;
    
    @PostMapping
    public ResponseEntity AddAddress(@RequestBody Direccion direccion){
        Result result = direccionService.AddAddress(direccion);
        return ResponseEntity.status(result.StatusCode).body(result);
    }
    
    @DeleteMapping("/{IdAddress}")
    public ResponseEntity DeleteAddress(@PathVariable("IdAddress") int IdAddress){
        Result result = direccionService.DeleteAddressById(IdAddress);
        return ResponseEntity.status(result.StatusCode).body(result);
    }
    
    @PutMapping
    public ResponseEntity UpdateAddress(@RequestBody Direccion direccion){
        Result result = direccionService.UpdateAddressById(direccion);
        return ResponseEntity.status(result.StatusCode).body(result);
    }
}
