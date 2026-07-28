package com.appointments.booking.appointments.model.notification;

/**
 * Kinds of activity a partner gets notified about.
 *
 * <p>The frontend maps each value to an icon and a colour tone, so adding a
 * value here means adding a matching entry in the partner notifications page.
 */
public enum PartnerNotificationType {

    /** A customer booked an appointment at one of the partner's properties. */
    APPOINTMENT_BOOKED,

    /** A customer cancelled an appointment. */
    APPOINTMENT_CANCELLED,

    /** A property switched to ACTIVE or INACTIVE. */
    PROPERTY_STATUS_CHANGED,

    /** A property's details (name, address) were edited. */
    PROPERTY_UPDATED,

    /** A manager was assigned to, or removed from, a property. */
    MANAGER_CHANGED,

    /** An employee was added to a property. */
    EMPLOYEE_ADDED,

    /** An employee's weekly availability was updated. */
    EMPLOYEE_AVAILABILITY_UPDATED
}
