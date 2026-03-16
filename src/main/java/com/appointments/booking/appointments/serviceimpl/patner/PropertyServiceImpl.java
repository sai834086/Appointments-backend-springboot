package com.appointments.booking.appointments.serviceimpl.patner;

import com.appointments.booking.appointments.exception.AlreadyExistsException;
import com.appointments.booking.appointments.exception.UnauthorizedAccessOrUnknownException;
import com.appointments.booking.appointments.mapStruct.patner.PropertyMapStruct;
import com.appointments.booking.appointments.model.appuser.AppUser;
import com.appointments.booking.appointments.model.enums.StatusEnum;
import com.appointments.booking.appointments.model.patner.PartnerUser;
import com.appointments.booking.appointments.model.patner.Property;
import com.appointments.booking.appointments.model.roles.Role;
import com.appointments.booking.appointments.payload.request.patner.propertyRequests.PropertyRegisterRequest;
import com.appointments.booking.appointments.payload.request.patner.propertyRequests.PropertyUpdateRequest;
import com.appointments.booking.appointments.payload.response.patner.propertyResponse.PropertyDetailsResponse;
import com.appointments.booking.appointments.repository.patner.PartnerUserRepository;
import com.appointments.booking.appointments.repository.patner.PropertyRepository;
import com.appointments.booking.appointments.repository.roles.RoleRepository;
import com.appointments.booking.appointments.repository.user.AppUserRepository;
import com.appointments.booking.appointments.service.patner.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyMapStruct propertyMapStruct;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    private final PartnerUserRepository partnerUserRepository;

    @Autowired
    public PropertyServiceImpl(PropertyRepository propertyRepository,
                               PropertyMapStruct propertyMapStruct,
                               AppUserRepository appUserRepository,
                               PasswordEncoder passwordEncoder,
                               RoleRepository roleRepository, PartnerUserRepository partnerUserRepository) {
        this.propertyRepository = propertyRepository;
        this.propertyMapStruct = propertyMapStruct;
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.partnerUserRepository = partnerUserRepository;

    }

    // ----------------------------------------------------------------
    // 1. ADD PROPERTY
    // ----------------------------------------------------------------
    @Override
    @Transactional
    public void addProperty(PropertyRegisterRequest dto, Long ownerId) {

        // Validation: Global check for property name uniqueness
        if(checkPropertyExists(dto.getPropertyName())) {
            throw new AlreadyExistsException("Property Name with this name already exists");
        }

        // Fetch the Owner (The logged-in Partner)
        AppUser owner = appUserRepository.findById(ownerId)
                .orElseThrow(() -> new UnauthorizedAccessOrUnknownException("Owner not found"));

        PartnerUser partner = partnerUserRepository.findByAppUser_UserId(ownerId)
                .orElseThrow(() -> new UnauthorizedAccessOrUnknownException("Partner profile not found for this user"));

        // Convert DTO to Entity
        Property property = propertyMapStruct.toEntity(dto);

        property.setPartnerUser(partner);

        // Logic: Who is the manager?
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            AppUser manager = appUserRepository.findByEmail(dto.getEmail())
                    .orElseGet(() -> {
                        AppUser newManager = new AppUser();
                        newManager.setFirstName(dto.getFirstName());
                        newManager.setLastName(dto.getLastName());
                        newManager.setEmail(dto.getEmail());
                        newManager.setPhoneNumber(dto.getPhoneNumber());
                        newManager.setPassword(passwordEncoder.encode(dto.getPassword()));

                        Role managerRole = roleRepository.findByRoleName("MANAGER")
                                .orElseThrow(() -> new RuntimeException("Role MANAGER not found"));
                        newManager.setRoles(Set.of(managerRole));

                        return appUserRepository.save(newManager);
                    });

            property.setManager(manager);
        } else {
            // No manager info? The Owner acts as the manager.
            property.setManager(owner);
        }

        // Finalize status and save
        if (property.getStatus() == null) {
            property.setStatus(StatusEnum.INACTIVE);
        }

        propertyRepository.save(property);
    }

    // ----------------------------------------------------------------
    // 2. GET ALL PROPERTIES FOR OWNER
    // ----------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<PropertyDetailsResponse> allPropertyDetails(Long userId) {

        // UPDATED: Traversing Property -> PartnerUser -> AppUser -> UserId
        List<Property> properties = propertyRepository.findByPartnerUser_AppUser_UserId(userId);

        if(properties.isEmpty()){
            throw new UnauthorizedAccessOrUnknownException("No properties found for this user");
        }
        return propertyMapStruct.toResponse(properties);
    }

    // ----------------------------------------------------------------
    // 3. UPDATE PROPERTY
    // ----------------------------------------------------------------
    @Override
    @Transactional
    public PropertyDetailsResponse updateProperty(PropertyUpdateRequest dto, Long propertyId, Long userId) {

        // UPDATED: Fetch Property AND verify ownership via PartnerUser
        Property existingProperty = propertyRepository.findByPropertyIdAndPartnerUser_AppUser_UserId(propertyId, userId)
                .orElseThrow(()-> new UnauthorizedAccessOrUnknownException("Property not found or you are not the owner"));

        // Only check for duplicates if the name is changing
        if (dto.getPropertyName() != null && !dto.getPropertyName().equals(existingProperty.getPropertyName())) {
            if (checkPropertyExists(dto.getPropertyName())) {
                throw new AlreadyExistsException("Property name already exists, choose a different name");
            }
        }

        // Update fields and save
        propertyMapStruct.updateEntityFromDto(dto, existingProperty);
        Property savedProperty = propertyRepository.save(existingProperty);

        return propertyMapStruct.toResponse(savedProperty);
    }

    // ----------------------------------------------------------------
    // 4. HELPERS
    // ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public boolean checkPropertyExists(String propertyName){
        return propertyRepository.findByPropertyName(propertyName) != null;
    }
}