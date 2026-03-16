package com.appointments.booking.appointments.controller.user;

import com.appointments.booking.appointments.payload.request.user.UserLoginRequest;
import com.appointments.booking.appointments.payload.request.user.UserSignUpRequest;
import com.appointments.booking.appointments.payload.request.user.UserUpdateRequest;
import com.appointments.booking.appointments.payload.response.ApiResponse;
import com.appointments.booking.appointments.payload.response.user.AllPartnerUsersToAppUsers;
import com.appointments.booking.appointments.payload.response.user.AppUserProfileResponse;
import com.appointments.booking.appointments.repository.user.AppUserRepository;
import com.appointments.booking.appointments.security.JwtUserDetails;
import com.appointments.booking.appointments.security.JwtUtil;
import com.appointments.booking.appointments.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class UserController {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final JwtUtil jwtUtil;


    @Autowired
    public UserController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    //User Sign Up ---------------------------------------------
    @PostMapping("/registerUser")
    public ResponseEntity<ApiResponse<Map<String,Object>>> saveUserRequest(@Valid @RequestBody UserSignUpRequest userDTO){
        userService.saveUser(userDTO);
        return ResponseEntity.ok(new ApiResponse<>(true,"sign up success full"));
    }

    // User Login ----------------------------------------------------
    @PostMapping("appUser/login")
    public ResponseEntity<ApiResponse<Map<String,Object>>> loginUserRequest(@Valid @RequestBody UserLoginRequest userDTO){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDTO.getUserName(),
                        userDTO.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Long id = userService.getAppUserId(userDTO.getUserName());
        String jwt = jwtUtil.generateToken(userDetails, id);

        Map<String,Object> payload = new HashMap<>();
        payload.put("token",jwt);

        return ResponseEntity.ok(new ApiResponse<>(true,"Login success full",payload));
    }

    @GetMapping("/appUser/userDetails")
    public ResponseEntity<ApiResponse<Map<String,Object>>> getUserDetails(@AuthenticationPrincipal JwtUserDetails jwtUserDetails){

        AppUserProfileResponse appUserDetails = userService.getUserDetails(jwtUserDetails.getId());

        Map<String,Object> payload = new HashMap<>();
        payload.put("Profile", appUserDetails);

        return ResponseEntity.ok(new ApiResponse<>(true,"success",payload));

    }

    @PatchMapping("/appUser/update")
    public ResponseEntity<?> updateProfile(
            @RequestBody UserUpdateRequest request,
            @AuthenticationPrincipal JwtUserDetails userDetails) {

        userService.updateUserDetails(userDetails.getId(), request);

        return ResponseEntity.ok(new ApiResponse<>(true, "Profile updated successfully"));
    }

}