package dev.corusoft.eticketia.domain.entities.tickets;

import java.util.Date;

public class TicketMetadata {
    private String rawOcrResponse;
    private Date scanTimestamp;
    private String uploadedImageUri;
    private String uploadedImageTimestamp;
    private TicketProcessingState status;
}
