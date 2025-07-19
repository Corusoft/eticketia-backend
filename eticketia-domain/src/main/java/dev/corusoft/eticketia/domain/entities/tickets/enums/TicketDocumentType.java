package dev.corusoft.eticketia.domain.entities.tickets.enums;

public enum TicketDocumentType {
  EXPENSE_RECEIPT,
  CREDIT_CARD_RECEIPT,
  UNKNOWN;

  /**
   * Returns the enum constant of this type with the specified name.
   */
  public static TicketDocumentType fromString(String name) {
    try {
      return TicketDocumentType.valueOf(name.strip().toUpperCase());
    } catch (IllegalArgumentException e) {
      return UNKNOWN;
    }
  }
}
