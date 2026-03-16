package com.appointments.booking.appointments.controller.patner;

import com.appointments.booking.appointments.model.patner.PartnerUser;
import com.appointments.booking.appointments.payload.request.patner.employeeRequests.EmployeeRegisterRequest;
import com.appointments.booking.appointments.payload.request.patner.employeeRequests.EmployeeUpdateRequest;
import com.appointments.booking.appointments.payload.response.ApiResponse;
import com.appointments.booking.appointments.payload.response.patner.employeeResponse.EmployeeResponseWithServices;
import com.appointments.booking.appointments.repository.patner.PartnerUserRepository;
import com.appointments.booking.appointments.security.JwtUserDetails;
import com.appointments.booking.appointments.service.patner.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class EmployeeController {

    private final EmployeeService employeeService;

    private final PartnerUserRepository partnerUserRepository;

    public EmployeeController(EmployeeService employeeService, PartnerUserRepository partnerUserRepository) {
        this.employeeService = employeeService;
        this.partnerUserRepository = partnerUserRepository;
    }


    @PostMapping("/partnerUser/registerEmployee/{propertyId}")
    public ResponseEntity<ApiResponse<Map<String,Object>>> registerEmployee(@Valid @RequestBody EmployeeRegisterRequest dto, @PathVariable Long propertyId){

        JwtUserDetails user = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long partnerId = user.getId();

         employeeService.saveEmployee(dto,propertyId,partnerId);

         return ResponseEntity.ok(new ApiResponse<>(true, "employee added successfully"));
    }

    @GetMapping("/partnerUser/getEmployeesWithServices/{propertyId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEmployeesWithServices(@PathVariable("propertyId") Long propertyId){
        JwtUserDetails user = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long partnerId = user.getId();
        List<EmployeeResponseWithServices> employees = employeeService.getAllEmployeesWithServices(propertyId,partnerId);
        Map<String,Object> payload = new HashMap<>();
        payload.put("allEmployeeDetails", employees);
        return ResponseEntity.ok(new ApiResponse<>(true, "success", payload));
    }

    @GetMapping("/partnerUser/getEmployees/{propertyId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEmployees(@PathVariable("propertyId") Long propertyId){
        JwtUserDetails user = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long partnerId = user.getId();
        List<EmployeeResponseWithServices> employees = employeeService.getAllEmployeesWithServices(propertyId,partnerId);
        Map<String,Object> payload = new HashMap<>();
        payload.put("allEmployeeDetails", employees);
        return ResponseEntity.ok(new ApiResponse<>(true, "success", payload));
    }

    @PatchMapping("/partnerUser/updateEmployee/{propertyId}/{employeeId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateEmployee(@Valid @RequestBody EmployeeUpdateRequest dto,
                                                                           @PathVariable("propertyId") Long propertyId,
                                                                           @PathVariable("employeeId") Long employeeId){
        JwtUserDetails user = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long partnerId = user.getId();
        employeeService.updateEmployee(dto, employeeId, propertyId, partnerId);

        return ResponseEntity.ok(new ApiResponse<>(true, "Employee Updated"));
    }
    @DeleteMapping("partnerUser/deleteEmployee/{employeeId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteEmployee(@PathVariable("employeeId") Long employeeId){
        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long partnerId = jwtUserDetails.getId();

        employeeService.deleteEmployeeById(employeeId, partnerId);

        return ResponseEntity.ok(new ApiResponse<>(true, "Employee Deleted"));
    }

    @PutMapping("/partnerUser/addServicesToEmployee/{employeeId}/{propertyId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addServicesToEmployee(@PathVariable Long employeeId,
                                                                                  @PathVariable Long propertyId,
                                                                                  @RequestBody List<Long> serviceIds,
                                                                                  @AuthenticationPrincipal JwtUserDetails userDetails){


        // 3. Call your service
        employeeService.addServicesToEmployee(employeeId, serviceIds, propertyId, userDetails.getId());

        return ResponseEntity.ok(new ApiResponse<>(true, "Services added to employee"));
    }

    @DeleteMapping("/partnerUser/removeServiceFromEmployee/{propertyId}/{employeeId}/{serviceId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> removeServiceFromEmployee(
            @PathVariable Long propertyId,
            @PathVariable Long employeeId,
            @PathVariable Long serviceId,
            @AuthenticationPrincipal JwtUserDetails userDetails) {


        // 2. Execute Removal
        employeeService.removeServiceFromEmployee(employeeId, serviceId, propertyId, userDetails.getId());

        return ResponseEntity.ok(new ApiResponse<>(true, "Service removed from employee"));
    }

}
