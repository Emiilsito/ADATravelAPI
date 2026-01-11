package es.severo.travel_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateFlightRequest(
        @NotBlank String flightNumber,
        @NotNull LocalDate departureDate,
        @NotNull LocalDate arrivalDate,
        @NotNull LocalTime departureTime,
        @NotNull LocalTime arrivalTime,
        @PositiveOrZero BigDecimal basePrice,
        @NotBlank String status,
        @NotNull @Positive Long airlineId,
        @NotNull @Positive Long departureAirportId,
        @NotNull @Positive Long arrivalAirportId
) {}
