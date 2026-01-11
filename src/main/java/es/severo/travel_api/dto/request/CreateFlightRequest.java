package es.severo.travel_api.dto.request;

import es.severo.travel_api.domain.FlightStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateFlightRequest(
        @NotNull @NotBlank String flightNumber,
        @NotNull LocalDate departureDate,
        @NotNull LocalDate arrivalDate,
        @NotNull LocalTime departureTime,
        @NotNull LocalTime arrivalTime,
        @Positive Integer durationMinutes,
        @DecimalMin("0.0") BigDecimal basePrice,
        @NotNull FlightStatus status,
        @NotNull String airlineCode,
        @NotNull String departureAirportCode,
        @NotNull String arrivalAirportCode
) {
}
