
package OBenitez.ProgramacionNCapasNoviembre25.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    
    private Key llave = Keys.hmacShaKeyFor("OswaldoBenitezCamachoRisosuIT2025".getBytes());
    
    public String generateToken(Integer idUsuario){
        return Jwts.builder()
                .setSubject(String.valueOf(idUsuario))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(llave)
                .compact();
    }
    
    
    public Integer extractUserId(String token){
        String subject = Jwts
                .parser()
                .setSigningKey(llave)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return Integer.parseInt(subject);
    }
    
//    public String extractUsername(String token){
//        return Jwts
//                .parser()
//                .setSigningKey(llave)
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }
}
