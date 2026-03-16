package com.appointments.booking.appointments.service.patner;

import com.appointments.booking.appointments.payload.request.patner.partnerRequests.PartnerUpdateRequest;
import com.appointments.booking.appointments.payload.request.patner.partnerRequests.PartnerUserSignUpRequest;
import com.appointments.booking.appointments.payload.response.patner.partnerResponse.PartnerProfileResponse;
import com.appointments.booking.appointments.payload.response.patner.propertyResponse.PropertyDetailsResponse;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface PartnerUserService {
    void saveUser(PartnerUserSignUpRequest signUpRequest);
    PartnerProfileResponse partnerUserDetails(String userName);
    boolean userVerified(String userName);
    void updatePartner(Long id, PartnerUpdateRequest request);
    PartnerProfileResponse partnerUserDetails(Long id);
}
