package es.severo.travel_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PassengerSeatRequest(
        @NotNull(message = "El id del pasajero es obligatorio")
        Long passengerId,

        @NotBlank
        @Pattern(regexp = "^[0-9]{1,2}[A-F]$", message = "Formato de asiento invalido (e.g. 12A)")
        String seatNumber
) {}
