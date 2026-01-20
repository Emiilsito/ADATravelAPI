package es.severo.travel_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record FlightBookingCountDto(
        @Schema(example = "IB1234") String flightNumber,
        @Schema(example = "2026-05-20") LocalDate departureDate,
        @Schema(example = "45") Long totalBookings
) {}
