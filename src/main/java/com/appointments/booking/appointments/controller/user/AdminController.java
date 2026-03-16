package com.appointments.booking.appointments.controller.user;

import com.appointments.booking.appointments.payload.request.admin.RegisterRequest;
import com.appointments.booking.appointments.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/create-admin")
    public ResponseEntity<?> createNewAdmin(@RequestBody RegisterRequest request) {
        // You reuse your normal registration logic, but FORCE the role to ADMIN
        userService.registerAdmin(request);
        return ResponseEntity.ok("New Admin created successfully.");
    }
}
