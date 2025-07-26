package dev.corusoft.eticketia.infrastructure.dtos.ticket.inbound;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.util.Date;
import org.springframework.web.multipart.MultipartFile;

/**
 * Parameters to scan a ticket using Mindee implementation.
 */
public record MindeeScanTicketInputDTO(
    @NotNull MultipartFile imageFile,
    @NotNull @PastOrPresent Date scanTimestamp
) {

}
