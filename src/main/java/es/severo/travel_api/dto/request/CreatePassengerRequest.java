package es.severo.travel_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePassengerRequest(
        @NotBlank @Size(min = 5, max = 20) String documentNumber,
        @Size(max = 1) String gender
) {}
