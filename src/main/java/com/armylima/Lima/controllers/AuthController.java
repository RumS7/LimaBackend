package com.armylima.Lima.controllers;

import com.armylima.Lima.auth.CustomUserDetails;
import com.armylima.Lima.dto.LoginRequest;
import com.armylima.Lima.dto.Rank;
import com.armylima.Lima.dto.RegisterDTO;
import com.armylima.Lima.entities.UserInfo;
import com.armylima.Lima.repositories.UserRepository;
import com.armylima.Lima.services.JWTService;
import com.armylima.Lima.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    private JWTService jwtService;

    private final PasswordEncoder encoder;
    private final UserService userService;

    @Value("${KING.secretkey}")
    private String KingKey;

    @Value("${KNIGHT.secretkey}")
    private String KnightKey;

    @Value("${QUEEN.secretkey}")
    private String QueenKey;

    public AuthController(UserRepository userRepo, PasswordEncoder encoder,
                          UserService userService, JWTService jwtService) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.userService= userService;
        this.jwtService = jwtService;
    }


    @PostMapping("/signup")
    public ResponseEntity<String> register(@RequestBody RegisterDTO dto) {
        if (userRepo.existsByEmail(dto.email())) {
            return ResponseEntity.status(409).body("Email already registered");
        }
        if (userRepo.existsByArmyId(dto.armyId())) {
            return ResponseEntity.status(409).body("ArmyId already registered");
        }

        // --- THIS IS THE CORRECTED LOGIC ---
        if (dto.rank() == Rank.KING) {
            if (dto.secretKey()==null || !dto.secretKey().equals(KingKey)) { // Use .equals() for string comparison
                return ResponseEntity.status(409).body("Incorrect KING  key");
            }
        } else if (dto.rank() == Rank.KNIGHT) {
            if (dto.secretKey()==null || !dto.secretKey().equals(KnightKey)) { // Use .equals() for string comparison
                return ResponseEntity.status(409).body("Incorrect KNIGHT key");
            }
        }else if (dto.rank() == Rank.QUEEN) {
            if (dto.secretKey()==null || !dto.secretKey().equals(QueenKey)) { // Use .equals() for string comparison
                return ResponseEntity.status(409).body("Incorrect QUEEN key");
            }
        }
        // --- END OF CORRECTION ---

        userService.registerUser(dto);
        return ResponseEntity.ok("User registered successfully");
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getArmyId(), request.getPassword())
        );
        UserInfo user = userRepo.findByArmyId(request.getArmyId()).orElseThrow();
        String jwtToken = jwtService.generateToken(new CustomUserDetails(user));
        // Corrected to return "rank" to match the frontend model
        return ResponseEntity.ok(Map.of("token", jwtToken, "rank", user.getRank().name()));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        req.logout();
        return ResponseEntity.ok("Logged out");
    }
}

