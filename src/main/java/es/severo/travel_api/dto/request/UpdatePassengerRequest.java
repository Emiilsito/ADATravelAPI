package es.severo.travel_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePassengerRequest(
        @NotNull @NotBlank String documentNumber,
        String gender
) {
}
