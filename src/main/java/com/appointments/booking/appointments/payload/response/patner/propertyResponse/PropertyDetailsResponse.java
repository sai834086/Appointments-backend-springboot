package com.appointments.booking.appointments.payload.response.patner.propertyResponse;

import com.appointments.booking.appointments.model.enums.StatusEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class PropertyDetailsResponse {

        private Long propertyId;

        private String firstName;

        private String lastName;

        private String email;

        private String phoneNumber;

        private String propertyName;

        private String buildingNo;

        private String street;

        private String city;

        private String state;

        private String country;

        @Enumerated(EnumType.STRING)
        private StatusEnum status;

        private String zipCode;
}
