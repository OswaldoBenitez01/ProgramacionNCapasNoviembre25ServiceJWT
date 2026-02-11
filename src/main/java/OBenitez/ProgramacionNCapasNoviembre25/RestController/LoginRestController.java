
package OBenitez.ProgramacionNCapasNoviembre25.RestController;

import OBenitez.ProgramacionNCapasNoviembre25.Component.JwtUtil;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Result;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario;
import OBenitez.ProgramacionNCapasNoviembre25.Service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@Tag(name = "API de autenticacion", description = "Operaciones de Log in y Log out")
public class LoginRestController {
    
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostMapping("/login")
    public ResponseEntity<String> logIn(@RequestBody Usuario usuarioLogin) {
        
        Result result = usuarioService.LogInUsuario(usuarioLogin.getUsername());
        if (!result.Correct || result.Object == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario o contraseña incorrectos");
        }

        Usuario usuarioDB = (Usuario) result.Object;

        boolean passwordOk = passwordEncoder.matches(
                usuarioLogin.getPassword(),
                usuarioDB.getPassword()
        );

        if (!passwordOk) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario o contraseña incorrectos");
        }

        String token = jwtUtil.generateToken(usuarioDB.getIdUsuario());
        return ResponseEntity.ok(token);
    }


            
}
