package com.appointments.booking.appointments.serviceimpl.patner;

import com.appointments.booking.appointments.exception.AlreadyExistsException;
import com.appointments.booking.appointments.exception.UnauthorizedAccessOrUnknownException;
import com.appointments.booking.appointments.mapStruct.patner.PartnerUserMapStruct;
import com.appointments.booking.appointments.model.appuser.AppUser;
import com.appointments.booking.appointments.model.enums.VerificationEnum;
import com.appointments.booking.appointments.model.enums.StatusEnum;
import com.appointments.booking.appointments.model.patner.PartnerUser;
import com.appointments.booking.appointments.model.patner.Property;
import com.appointments.booking.appointments.model.roles.Role;
import com.appointments.booking.appointments.payload.request.patner.partnerRequests.PartnerUpdateRequest;
import com.appointments.booking.appointments.payload.request.patner.partnerRequests.PartnerUserSignUpRequest;
import com.appointments.booking.appointments.payload.response.patner.partnerResponse.PartnerProfileResponse;
import com.appointments.booking.appointments.repository.patner.PartnerUserRepository;
import com.appointments.booking.appointments.repository.patner.PropertyRepository;
import com.appointments.booking.appointments.repository.roles.RoleRepository;
import com.appointments.booking.appointments.repository.user.AppUserRepository;
import com.appointments.booking.appointments.service.patner.PartnerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;

@Service
@Qualifier("partnerAuthentication")
public class PartnerUserServiceImpl implements PartnerUserService {

    private final PasswordEncoder passwordEncoder;
    private final PartnerUserRepository partnerUserRepository;
    private final PartnerUserMapStruct partnerUserMapStruct;
    private final RoleRepository roleRepository;
    private final PropertyRepository propertyRepository;
    private final AppUserRepository appUserRepository;

    @Autowired
    public PartnerUserServiceImpl(PasswordEncoder passwordEncoder,
                                  PartnerUserRepository partnerUserRepository,
                                  PartnerUserMapStruct partnerUserMapStruct,
                                  RoleRepository roleRepository,
                                  PropertyRepository propertyRepository,
                                  AppUserRepository appUserRepository) {
        this.passwordEncoder = passwordEncoder;
        this.partnerUserRepository = partnerUserRepository;
        this.partnerUserMapStruct = partnerUserMapStruct;
        this.roleRepository = roleRepository;
        this.propertyRepository = propertyRepository;
        this.appUserRepository = appUserRepository;
    }

    @Override
    @Transactional
    public void saveUser(PartnerUserSignUpRequest signUpRequest) {
        if(checkBusinessNameExits(signUpRequest.getBusinessName())){
            throw new AlreadyExistsException("Business name already exists.");
        }
        if (appUserRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new AlreadyExistsException("Email already in use.");
        }

        AppUser appUser = new AppUser();
        appUser.setFirstName(signUpRequest.getFirstName());
        appUser.setLastName(signUpRequest.getLastName());
        appUser.setEmail(signUpRequest.getEmail());
        appUser.setPhoneNumber(signUpRequest.getPhoneNumber());
        appUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        Role role = roleRepository.findByRoleName("PARTNER")
                .orElseThrow(() -> new UnauthorizedAccessOrUnknownException("ROLE 'PARTNER' NOT DEFINED"));
        appUser.setRoles(Collections.singleton(role));

        AppUser savedUser = appUserRepository.save(appUser);

        PartnerUser partnerProfile = partnerUserMapStruct.toEntity(signUpRequest);
        partnerProfile.setAppUser(savedUser);
        partnerProfile.setIsVerified(VerificationEnum.UNVERIFIED);
        partnerProfile.setStatus(StatusEnum.INACTIVE);

        partnerUserRepository.save(partnerProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerProfileResponse partnerUserDetails(String userName){
        AppUser appUser = checkUserExists(userName);
        PartnerUser partnerProfile = appUser.getPartnerUser();

        if(partnerProfile == null) {
            throw new UnauthorizedAccessOrUnknownException("Partner Profile does not exist.");
        }
        return partnerUserMapStruct.toDTO(partnerProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerProfileResponse partnerUserDetails(Long id) {
        PartnerUser partnerUser = partnerUserRepository.findByAppUser_UserId(id)
                .orElseThrow(() -> new UnauthorizedAccessOrUnknownException("Partner not found"));
        return partnerUserMapStruct.toDTO(partnerUser);
    }

    @Override
    @Transactional
    public void updatePartner(Long partnerId, PartnerUpdateRequest request) {
        PartnerUser partner = partnerUserRepository.findById(partnerId)
                .orElseThrow(() -> new UnauthorizedAccessOrUnknownException("Partner user not found"));

        AppUser appUser = partner.getAppUser();

        if (request.getFirstName() != null) appUser.setFirstName(request.getFirstName());
        if (request.getLastName() != null) appUser.setLastName(request.getLastName());

        // Update Phone/Email with uniqueness checks
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(appUser.getPhoneNumber())) {
            if (appUserRepository.existsByPhoneNumber(request.getPhoneNumber())) throw new AlreadyExistsException("Phone in use");
            appUser.setPhoneNumber(request.getPhoneNumber());
        }

        partnerUserRepository.save(partner);
    }

    @Transactional(readOnly = true)
    public boolean checkStatusInProperty(Long partnerId){
        // FIXED: partnerId is the PartnerUser primary key.
        // We get the AppUser ID to find properties owned by this partner.
        PartnerUser partner = partnerUserRepository.findById(partnerId)
                .orElseThrow(() -> new UnauthorizedAccessOrUnknownException("Partner not found"));

        Long appUserId = partner.getAppUser().getUserId();

        // Use the updated repository method traversing Property -> PartnerUser -> AppUser
        List<Property> properties = propertyRepository.findByPartnerUser_AppUser_UserId(appUserId);

        return properties.stream()
                .anyMatch(p -> StatusEnum.ACTIVE.equals(p.getStatus()));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userVerified(String userName){
        AppUser appUser = checkUserExists(userName);
        PartnerUser partnerUser = appUser.getPartnerUser();
        return partnerUser != null && partnerUser.getIsVerified() == VerificationEnum.VERIFIED;
    }

    public AppUser checkUserExists(String loginInput) {
        return appUserRepository.findByEmailOrPhoneNumber(loginInput, loginInput)
                .orElseThrow(() -> new UnauthorizedAccessOrUnknownException("Invalid Username or Password"));
    }

    public boolean checkBusinessNameExits(String businessName){
        return partnerUserRepository.existsByBusinessName(businessName);
    }
}