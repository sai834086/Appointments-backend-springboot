package com.appointments.booking.appointments.model.patner;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "services")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Services {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "service_name", nullable = false, length = 45)
    private String serviceName;

    @Column(name = "each_appointment_time_in_minus", nullable = false, length = 10)
    private short eachServiceTimeInMinus;

    @Column(name="service_fee")
    private Double serviceFee;

    @Column(name = "description", length = 500)
    private String description;

    @ManyToMany(mappedBy = "services", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("services")
    private List<Employee> employees;

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

}
