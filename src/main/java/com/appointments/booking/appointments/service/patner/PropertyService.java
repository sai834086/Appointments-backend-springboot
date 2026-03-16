package com.appointments.booking.appointments.service.patner;

import com.appointments.booking.appointments.model.patner.Property;
import com.appointments.booking.appointments.payload.request.patner.propertyRequests.PropertyRegisterRequest;
import com.appointments.booking.appointments.payload.request.patner.propertyRequests.PropertyUpdateRequest;
import com.appointments.booking.appointments.payload.response.patner.propertyResponse.PropertyDetailsResponse;

import java.util.List;

public interface PropertyService {
    void addProperty(PropertyRegisterRequest dto, Long partnerId);
    List<PropertyDetailsResponse> allPropertyDetails(Long partnerId);
    PropertyDetailsResponse updateProperty(PropertyUpdateRequest dto,Long id, Long partnerId);
}
