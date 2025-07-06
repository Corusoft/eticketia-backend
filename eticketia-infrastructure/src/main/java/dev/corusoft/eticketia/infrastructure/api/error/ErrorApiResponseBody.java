package dev.corusoft.eticketia.infrastructure.api.error;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.corusoft.eticketia.infrastructure.api.ApiResponseBody;
import java.util.List;
import lombok.Builder;

@Builder
public record ErrorApiResponseBody(
    int statusCode,
    String status,
    @JsonInclude(NON_NULL) String message,
    @JsonInclude(NON_NULL) String debugMessage,
    @JsonInclude(NON_NULL) List<? extends ApiErrorDetails> errors
)
    implements ApiResponseBody {

}
