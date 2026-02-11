package OBenitez.ProgramacionNCapasNoviembre25.Configuration;

import OBenitez.ProgramacionNCapasNoviembre25.Component.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {
    
    @Autowired
    private JwtFilter jwtFilter;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(config -> config
                // PÚBLICO
                .requestMatchers("/api/auth/login").permitAll()

//                // CATÁLOGOS PÚBLICOS
//                .requestMatchers("/api/rol").authenticated()
//                .requestMatchers("/api/pais").authenticated()
//                .requestMatchers("/api/estado/pais/{IdPais}").authenticated()
//                .requestMatchers("/api/municipio/estado/{IdEstado}").authenticated()
//                .requestMatchers("/api/colonia/municipio/{IdMunicipio}").authenticated()
//
//                //ADMIN/DIRECTOR
//                .requestMatchers("/api/usuario").hasAnyRole("Director", "Administrador(a)")
//                .requestMatchers("/api/usuario/busqueda").hasAnyRole("Director", "Administrador(a)")
//                .requestMatchers("/api/usuario/{IdUsuario}").authenticated()
//
//                //USUARIO
//                .requestMatchers(HttpMethod.PUT, "/api/usuario").authenticated()
//                .requestMatchers("/api/direccion").authenticated()

                // Todo lo demás AUTHENTICATED
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

   
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin("http://localhost:8081");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
}
