package dev.corusoft.eticketia.infrastructure.services.mindee;

import com.mindee.parsing.standard.AmountField;
import com.mindee.parsing.standard.ClassificationField;
import com.mindee.parsing.standard.DateField;
import com.mindee.parsing.standard.LocaleField;
import com.mindee.parsing.standard.StringField;
import com.mindee.parsing.standard.TaxField;
import com.mindee.parsing.standard.Taxes;
import com.mindee.product.receipt.ReceiptV5Document;
import com.mindee.product.receipt.ReceiptV5LineItem;
import dev.corusoft.eticketia.domain.entities.tickets.TicketLineItem;
import dev.corusoft.eticketia.domain.entities.tickets.TicketTax;
import dev.corusoft.eticketia.domain.entities.tickets.enums.TicketCategory;
import dev.corusoft.eticketia.domain.entities.tickets.enums.TicketDocumentType;
import dev.corusoft.eticketia.domain.entities.tickets.enums.TicketSubcategory;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Adapter class to convert objects from the Mindee API to domain entities.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MindeeAdapter {

  public static TicketCategory toTicketCategory(ClassificationField category) {
    return TicketCategory.fromString(category.getValue());
  }

  public static TicketSubcategory toTicketSubcategory(ClassificationField category) {
    return TicketSubcategory.fromString(category.getValue());
  }

  public static TicketDocumentType toTicketDocumentType(ClassificationField documentType) {
    return TicketDocumentType.fromString(documentType.getValue());
  }

  public static String toDate(DateField dateField) {
    if (dateField.isEmpty()) {
      return null;
    }

    return DateTimeFormatter.ISO_LOCAL_DATE.format(dateField.getValue());
  }

  public static LocalTime toTime(StringField timeField) {
    if (timeField.isEmpty()) {
      return null;
    }

    return LocalTime.parse(timeField.getValue(), DateTimeFormatter.ISO_TIME);
  }

  public static List<TicketLineItem> toLineItemsList(Collection<ReceiptV5LineItem> lineItems) {
    return lineItems.stream().map(MindeeAdapter::toLineItem).toList();
  }

  public static TicketLineItem toLineItem(ReceiptV5LineItem lineItem) {
    return TicketLineItem.builder()
        .description(lineItem.getDescription())
        .quantity(lineItem.getQuantity())
        .totalAmount(lineItem.getTotalAmount())
        .unitPrice(lineItem.getUnitPrice())
        .build();
  }

  public static List<TicketTax> toTicketTaxList(Taxes taxes) {
    return taxes.stream().map(MindeeAdapter::toTicketTax).toList();
  }

  public static TicketTax toTicketTax(TaxField tax) {
    return TicketTax.builder()
        .value(tax.getValue())
        .code(tax.getCode())
        .rate(tax.getRate())
        .base(tax.getBase())
        .build();
  }

  public static BigDecimal toBigDecimal(AmountField ammountField) {
    if (ammountField.isEmpty()) {
      return null;
    }

    return BigDecimal.valueOf(ammountField.getValue());
  }

  public static Currency toCurrency(ReceiptV5Document document) {
    Locale locale = toLocale(document.getLocale());
    if (locale != null) {
      return Currency.getInstance(locale);
    }

    return null;
  }

  public static Locale toLocale(LocaleField localeField) {
    if (localeField.isEmpty()) {
      return null;
    }
    if (localeField.getLanguage() != null) {
      return Locale.of(localeField.getCountry(), localeField.getLanguage());
    }

    return Locale.of(localeField.getCountry());
  }

}
