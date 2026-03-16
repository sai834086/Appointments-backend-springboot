package com.appointments.booking.appointments.controller.patner;

import com.appointments.booking.appointments.exception.InvalidException;
import com.appointments.booking.appointments.exception.UnauthorizedAccessOrUnknownException;
import com.appointments.booking.appointments.model.appuser.AppUser;
import com.appointments.booking.appointments.payload.request.patner.partnerRequests.PartnerUserLoginRequest;
import com.appointments.booking.appointments.payload.response.ApiResponse;
import com.appointments.booking.appointments.repository.user.AppUserRepository;
import com.appointments.booking.appointments.security.JwtUtil;
import com.appointments.booking.appointments.service.patner.PartnerUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class LoginController {

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final AppUserRepository appUserRepository;

    private final PartnerUserService partnerUserService;

    public LoginController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, AppUserRepository appUserRepository, PartnerUserService partnerUserService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.appUserRepository = appUserRepository;
        this.partnerUserService = partnerUserService;
    }

    @PostMapping("/partnerUser/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> partnerUserLogin(@Valid @RequestBody PartnerUserLoginRequest partnerDTO) {

        // 1. Authenticate (Checks Password)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        partnerDTO.getUserName(),
                        partnerDTO.getPassword())
        );

        // 2. Get User Details
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 3. VALIDATE ROLE MATCH
        // Check if the user's actual authorities contain the role requested in the DTO
        boolean roleMatches = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(partnerDTO.getRole()));

        if (!roleMatches) {
            // If the user exists but does not have the specific role requested
            throw new InvalidException("You don't have access with role: " + partnerDTO.getRole());
        }

        // 4. Check Verification Status
        if (!partnerUserService.userVerified(partnerDTO.getUserName())) {
            throw new InvalidException("Account verification is in progress.");
        }

        // 5. Retrieve AppUser Entity
        AppUser appUser = appUserRepository.findByEmail(partnerDTO.getUserName())
                .orElseThrow(() -> new UnauthorizedAccessOrUnknownException("User not found"));

        // 6. Generate Token
        String jwt = jwtUtil.generateToken(userDetails, appUser.getUserId());

        // 7. Build Payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("token", jwt);
        payload.put("type", "Bearer");

        // We can confidently use the DTO role now because we verified it exists in the user's authorities
        payload.put("role", partnerDTO.getRole());

        payload.put("username", userDetails.getUsername());
        payload.put("userId", appUser.getUserId());
        payload.put("firstName", appUser.getFirstName());
        payload.put("lastName", appUser.getLastName());

        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", payload));
    }
}
