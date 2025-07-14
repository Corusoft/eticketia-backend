package dev.corusoft.eticketia.domain.entities.tickets.enums;

public enum TicketCategory {
    TOLL,
    FOOD,
    PARKING,
    TRANSPORT,
    ACCOMMODATION,
    GASOLINE,
    TELECOM,
    MISCELLANEOUS,
    SOFTWARE,
    SHOPPING,
    ENERGY,
    UNKNOWN
    ;

    /**
     * Returns the enum constant of this type with the specified name.
     */
    public static TicketCategory fromString(String name) {
        try {
            return TicketCategory.valueOf(name.strip().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

}
