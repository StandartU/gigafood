package ru.gigafood.backend.service;

import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import ru.gigafood.backend.config.TokenService;
import ru.gigafood.backend.dto.AuthDto;
import ru.gigafood.backend.entity.CustomUsrDetails;
import ru.gigafood.backend.entity.Role;
import ru.gigafood.backend.entity.User;
import ru.gigafood.backend.repository.RoleRepository;
import ru.gigafood.backend.repository.UserRepository;

@Service
public class authService {
    @Autowired
	private TokenService tokenService;

    @Autowired
	private AuthenticationManager authManager;

    @Autowired
	private CustomUsrDetailsService usrDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
		
		UsernamePasswordAuthenticationToken authenticationToken = 
				new UsernamePasswordAuthenticationToken(request.username(), request.password());
		Authentication auth = authManager.authenticate(authenticationToken);
		
		CustomUsrDetails user = (CustomUsrDetails) usrDetailsService.loadUserByUsername(request.username());
		String access_token = tokenService.generateAccessToken(user);
		String refresh_token = tokenService.generateRefreshToken(user);
		
		return new AuthDto.LoginResponse("User with email = "+ request.username() + " successfully logined!"
				
				, access_token, refresh_token);
	}

    public AuthDto.RefreshTokenResponse refresh(HttpServletRequest request) {
		 String headerAuth = request.getHeader("Authorization");		 
		 String refreshToken = headerAuth.substring(7, headerAuth.length());
		
		String email = tokenService.parseToken(refreshToken);
		CustomUsrDetails user = (CustomUsrDetails) usrDetailsService.loadUserByUsername(email);
		String access_token = tokenService.generateAccessToken(user);
		String refresh_token = tokenService.generateRefreshToken(user);
		
		return new AuthDto.RefreshTokenResponse(access_token, refresh_token);
	}

    public AuthDto.SingupResponse signup(AuthDto.SingupRequest request) {
        if (userRepository.existsByEmail(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with this email already exists");
        }

        User user = new User();
        user.setEmail(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        Role userRole = roleRepository.findByRoleName("usr")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRoles(Set.of(userRole));
        user.setCreatedAt(new Date());
        userRepository.save(user);


        return new AuthDto.SingupResponse(
                "User registered successfully"
        );
    }
}
