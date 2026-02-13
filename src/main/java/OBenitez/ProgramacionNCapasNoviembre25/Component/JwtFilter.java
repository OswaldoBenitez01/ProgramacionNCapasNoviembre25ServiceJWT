
package OBenitez.ProgramacionNCapasNoviembre25.Component;

import OBenitez.ProgramacionNCapasNoviembre25.DAO.IUsuarioJPA;
import OBenitez.ProgramacionNCapasNoviembre25.JPA.Usuario;
import OBenitez.ProgramacionNCapasNoviembre25.Service.UserDetailJPAService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    UserDetailJPAService userDetailsService;
    @Autowired
    IUsuarioJPA usuarioRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            try {
                String token = header.substring(7);
                Integer idUsuario = jwtUtil.extractUserId(token);

                if (idUsuario != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
                    if (usuario != null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getUsername());
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (io.jsonwebtoken.ExpiredJwtException ex) {
                // Token expirado
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"error\": \"Token expirado\", \"message\": \"" + ex.getMessage() + "\"}");
                return;
            } catch (io.jsonwebtoken.SignatureException ex) {
                // Token inválido
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"error\": \"Token inválido\", \"message\": \"Firma del token no válida\"}");
                return;
            } catch (Exception ex) {
                // Otro error
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"error\": \"Error de autenticación\", \"message\": \"" + ex.getMessage() + "\"}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}

