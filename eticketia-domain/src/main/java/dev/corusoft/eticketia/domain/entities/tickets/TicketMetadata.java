package dev.corusoft.eticketia.domain.entities.tickets;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketMetadata {

  private String rawOcrResponse;
  private Date scanTimestamp;
  private String uploadedImageUri;
}
