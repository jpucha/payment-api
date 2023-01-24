package ec.com.peigo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/clientes").hasRole("ADMIN")
            .antMatchers("/api/cuentas")
            .hasRole("CUSTOMER")
            .antMatchers("/api/")
            .hasRole("ADMIN")
            .antMatchers("/api/paymentTransaction")
            .hasRole("CUSTOMER")
            .antMatchers("/api/authentication")
            .hasRole("ADMIN")
            .antMatchers("/api/user")
            .hasRole("ADMIN")
            .antMatchers("/hello")
            .anonymous()
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return httpSecurity.build();
    }

}
