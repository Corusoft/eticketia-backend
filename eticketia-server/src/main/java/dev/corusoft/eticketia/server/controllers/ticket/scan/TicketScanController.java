package dev.corusoft.eticketia.server.controllers.ticket.scan;

import static dev.corusoft.eticketia.infrastructure.web.ApiPaths.TICKET_SCAN;

import dev.corusoft.eticketia.application.usecases.ticket.scan.ScanTicketInput;
import dev.corusoft.eticketia.application.usecases.ticket.scan.ScanTicketOutput;
import dev.corusoft.eticketia.domain.exceptions.DomainException;
import dev.corusoft.eticketia.infrastructure.api.ApiResponse;
import dev.corusoft.eticketia.infrastructure.api.ApiResponseBuilder;
import dev.corusoft.eticketia.infrastructure.security.EndpointSecurityConfigurer;
import dev.corusoft.eticketia.infrastructure.security.SecurityConstants;
import dev.corusoft.eticketia.infrastructure.usecases.ticket.scan.MindeeScanTicketUseCaseImpl;
import dev.corusoft.eticketia.infrastructure.web.ApiPaths;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequestMapping(TICKET_SCAN)
@RestController
@Lazy
@Log4j2
@RequiredArgsConstructor
public class TicketScanController implements EndpointSecurityConfigurer {

  private final MindeeScanTicketUseCaseImpl scanTicketUseCase;

  @Override
  public void secureEndpoints(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.authorizeHttpRequests(requests -> requests
        .requestMatchers(HttpMethod.POST, TICKET_SCAN).authenticated()
    );
  }

  @PostMapping(path = "",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<ApiResponse<ScanTicketOutput>> scanTicket(
      @RequestAttribute(SecurityConstants.USER_ID_ATTRIBUTE_NAME) String userId,
      @RequestParam("imageFile") MultipartFile imageFile,
      @RequestParam("scanTimestamp") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date scanTimestamp)
      throws DomainException {
    byte[] ticketImageBytes = getBytesFromImage(imageFile);
    ScanTicketInput input = new ScanTicketInput(ticketImageBytes, scanTimestamp, userId);

    ScanTicketOutput result = scanTicketUseCase.execute(input);

    // Build response
    ApiResponse<ScanTicketOutput> responseBody = ApiResponseBuilder.success(result);
    String newTicketUri = ApiPaths.TICKET_BY_ID_FORMAT.formatted(result.ticket().getId());
    URI resourceLocation = ServletUriComponentsBuilder.fromCurrentRequest()
        .path(newTicketUri)
        .buildAndExpand(result.ticket().getId())
        .toUri();

    return ResponseEntity
        .created(resourceLocation)
        .contentType(MediaType.APPLICATION_JSON)
        .body(responseBody);
  }

  private static byte[] getBytesFromImage(MultipartFile imageFile) {
    byte[] ticketImageBytes;
    try {
      ticketImageBytes = imageFile.getBytes();
    } catch (IOException e) {
      String message = "Could not extract bytes from ticket image";
      log.error(message);
      throw new IllegalArgumentException(message, e);
    }
    return ticketImageBytes;
  }

}
