package dev.corusoft.eticketia.domain.entities.tickets.enums;

public enum DocumentType {
    EXPENSE_RECEIPT,
    CREDIT_CARD_RECEIPT,
    UNKNOWN
    ;

    /**
     * Returns the enum constant of this type with the specified name.
     */
    public static DocumentType fromString(String name) {
        try {
            return DocumentType.valueOf(name.strip().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
