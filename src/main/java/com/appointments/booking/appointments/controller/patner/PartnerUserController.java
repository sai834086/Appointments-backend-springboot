package com.appointments.booking.appointments.controller.patner;

import com.appointments.booking.appointments.exception.InvalidException;
import com.appointments.booking.appointments.exception.UnauthorizedAccessOrUnknownException;
import com.appointments.booking.appointments.model.appuser.AppUser;
import com.appointments.booking.appointments.payload.request.patner.partnerRequests.PartnerUpdateRequest;
import com.appointments.booking.appointments.payload.request.patner.partnerRequests.PartnerUserLoginRequest;
import com.appointments.booking.appointments.payload.request.patner.partnerRequests.PartnerUserSignUpRequest;
import com.appointments.booking.appointments.payload.response.patner.partnerResponse.PartnerProfileResponse;
import com.appointments.booking.appointments.payload.response.ApiResponse;
import com.appointments.booking.appointments.repository.user.AppUserRepository;
import com.appointments.booking.appointments.security.JwtUserDetails;
import com.appointments.booking.appointments.security.JwtUtil;
import com.appointments.booking.appointments.service.patner.PartnerUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class PartnerUserController {

    private final PartnerUserService partnerUserService;


    @Autowired
    public PartnerUserController(PartnerUserService partnerUserService) {
        this.partnerUserService = partnerUserService;

    }

    @PostMapping("/partnerUser/register")
    public ResponseEntity<ApiResponse<Map<String,Object>>> partnerUserSignUp(@Valid @RequestBody PartnerUserSignUpRequest partnerUser){
        partnerUserService.saveUser(partnerUser);
        return ResponseEntity.ok(new ApiResponse<>(true, "success"));
    }


    @PatchMapping("/partnerUser/profileUpdate/{partnerId}")
    public ResponseEntity<?> updatePartner(
            @PathVariable Long partnerId,
            @RequestBody PartnerUpdateRequest request) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long id = jwtUserDetails.getId();

        if(!partnerId.equals(id)){
            throw new UnauthorizedAccessOrUnknownException("Unauthorized, No access");
        }
        partnerUserService.updatePartner(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true,"update success full"));
    }
    @GetMapping("/partnerUser/getPartnerProfile")
    public ResponseEntity<ApiResponse<Map<String,Object>>> getPartnerProfile(@AuthenticationPrincipal JwtUserDetails jwtUserDetails){

        PartnerProfileResponse partnerProfile = partnerUserService.partnerUserDetails(jwtUserDetails.getId());
        Map<String,Object> payload = new HashMap<>();
        payload.put("partnerUserProfile",partnerProfile);
        return ResponseEntity.ok(new ApiResponse<>(true,"update success full",payload));
    }
}
