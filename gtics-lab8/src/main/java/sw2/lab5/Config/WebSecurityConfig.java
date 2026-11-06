package sw2.lab5.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/processlogin")
                .failureUrl("/loginForm?error")
                .defaultSuccessUrl("/redirectByRol", true)
                .permitAll();

        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/post")
                .deleteCookies("JSESSIONID", "SESSION")
                .invalidateHttpSession(true);

        http.sessionManagement()
                .sessionFixation().migrateSession();

        http.exceptionHandling()
                .accessDeniedHandler((request, response, e) ->
                        response.sendRedirect(request.getContextPath() + "/post?denegado"));

        http.authorizeRequests()
                .antMatchers("/user/list", "/user/editar", "/user/actualizar", "/user/estado").hasAuthority("admin")
                .antMatchers("/user", "/user/guardar").hasAnyAuthority("admin", "user")
                .antMatchers("/post/create", "/post/edit", "/post/guardar", "/post/borrar").hasAnyAuthority("admin", "user")
                .antMatchers("/amigos", "/amigos/**").hasAnyAuthority("admin", "user")
                .antMatchers("/redirectByRol").authenticated()
                .anyRequest().permitAll();

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery("SELECT email, password, active FROM user WHERE email = ?");
        manager.setAuthoritiesByUsernameQuery("SELECT u.email, r.role_name FROM user u " +
                "INNER JOIN role r ON u.id_role = r.id_role " +
                "WHERE u.email = ? AND u.active = 1");
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
