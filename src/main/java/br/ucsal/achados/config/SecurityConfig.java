package br.ucsal.achados.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/** Só o responsável faz login; a consulta e o pedido de interesse dos alunos são públicos. */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	public static final String PAPEL_RESPONSAVEL = "RESPONSAVEL";

	@Bean
	SecurityFilterChain filtros(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/admin/login").permitAll()
						.requestMatchers("/admin/**").hasRole(PAPEL_RESPONSAVEL)
						.anyRequest().permitAll())
				.formLogin(form -> form
						.loginPage("/admin/login")
						.loginProcessingUrl("/admin/login")
						.defaultSuccessUrl("/admin", true)
						.failureUrl("/admin/login?erro"))
				.logout(logout -> logout
						.logoutUrl("/admin/logout")
						.logoutSuccessUrl("/"))
				.headers(headers -> headers.contentSecurityPolicy(csp -> csp.policyDirectives(
						"default-src 'self'; img-src 'self' data:; style-src 'self'; form-action 'self'; frame-ancestors 'none'")));
		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	UserDetailsService usuarios(AchadosProperties props, PasswordEncoder encoder) {
		return new InMemoryUserDetailsManager(User.withUsername(props.admin().usuario())
				.password(encoder.encode(props.admin().senha()))
				.roles(PAPEL_RESPONSAVEL)
				.build());
	}
}
