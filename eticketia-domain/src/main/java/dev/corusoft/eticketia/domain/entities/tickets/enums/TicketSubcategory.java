package dev.corusoft.eticketia.domain.entities.tickets.enums;

public enum TicketSubcategory {
    PLANE,
    TAXI,
    TRAIN,
    RESTAURANT,
    SHOPPING,
    GROCERIES,
    CULTURAL,
    ELECTRONICS,
    OFFICE_SUPPLIES,
    MICROMOBILITY,
    CAR_RENTAL,
    PUBLIC,
    DELIVERY,
    OTHER,
    UNKNOWN
    ;

    /**
     * Returns the enum constant of this type with the specified name.
     */
    public static TicketSubcategory fromString(String name) {
        try {
            return TicketSubcategory.valueOf(name.strip().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
