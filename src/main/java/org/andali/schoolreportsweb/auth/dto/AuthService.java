package org.andali.schoolreportsweb.auth.dto;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.school.School;
import org.andali.schoolreportsweb.security.CustomUserDetailsService;
import org.andali.schoolreportsweb.security.JwtService;
import org.andali.schoolreportsweb.security.SchoolUserDetails;
import org.andali.schoolreportsweb.user.User;
import org.andali.schoolreportsweb.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow();

        UserDetails userDetails = userDetailsService.loadUserByUsername(
                loginRequest.email()
        );

        String token = jwtService.generateToken(userDetails);

        AuthenticatedUser authenticatedUser =
                new AuthenticatedUser(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole(),
                        user.getSchool() != null ? user.getSchool().getId() : null,
                        user.getSchool() != null ? user.getSchool().getName() : null

                );
        return new LoginResponse(
                token,
                authenticatedUser
        );
    }
}
