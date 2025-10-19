package ru.gigafood.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import ru.gigafood.backend.service.CustomUsrDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private RsaProperties rsaKeys;
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
    public UserDetailsService customUserDetailsService() {
        return new CustomUsrDetailsService();
    }

	@Bean
	TokenService tokenService() {
		return new TokenService(jwtEncoder());
	}

	@Bean
	public AuthenticationManager authManager() {
		var authProvider = new DaoAuthenticationProvider(customUserDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
	}


	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
            .csrf(csrf -> csrf.disable())
			.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/gigafood/api/v1/login").permitAll()
				.requestMatchers("/gigafood/api/v1/signup").permitAll()
                .requestMatchers("/gigafood/api/v1/token/refresh").permitAll()
				.requestMatchers("/gigafood/api/v1/admin").hasAuthority("SCOPE_adm")
				.requestMatchers("/gigafood/api/v1/user").hasAuthority("SCOPE_usr")
				.anyRequest().authenticated()
			)
        	.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        	.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .build();
	}   

	@Bean
	public JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
	}

    @Bean
	public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
		JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwkSource);
	}
}
