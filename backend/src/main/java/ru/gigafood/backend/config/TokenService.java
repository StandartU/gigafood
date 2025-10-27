package ru.gigafood.backend.config;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import ru.gigafood.backend.entity.User;
import ru.gigafood.backend.repository.UserRepository;

import com.nimbusds.jwt.SignedJWT;

import jakarta.servlet.http.HttpServletRequest;
import ru.gigafood.backend.entity.CustomUsrDetails;

public class TokenService  {
	
        private final JwtEncoder jwtEncoder;

        public TokenService(JwtEncoder jwtEncoder) {
                super();
                this.jwtEncoder = jwtEncoder;
        }

    @Autowired
    private UserRepository userRepository;
    
    public String generateAccessToken(CustomUsrDetails usrDetails) {
        Instant now = Instant.now();
        String scope = usrDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(2, ChronoUnit.MINUTES))
                .subject(usrDetails.getUsername())
                .claim("scope", scope)
                .build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String generateRefreshToken(CustomUsrDetails usrDetails) {
        Instant now = Instant.now();
        String scope = usrDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(10, ChronoUnit.MINUTES))
                .subject(usrDetails.getUsername())
                .claim("scope", scope)
                .build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
    
    public String parseToken(String token) {
    	try {
			SignedJWT decodedJWT = SignedJWT.parse(token);
			String subject = decodedJWT.getJWTClaimsSet().getSubject();
			return subject;
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return null;
    }

    public User getUserByRequest(HttpServletRequest httpRequest) {
        String headerAuth = httpRequest.getHeader("Authorization");		 
        String accessToken = headerAuth.substring(7, headerAuth.length());

        String email = parseToken(accessToken);
	User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return user;
    }
}
