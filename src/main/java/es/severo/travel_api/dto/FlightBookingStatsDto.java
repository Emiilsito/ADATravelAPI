package es.severo.travel_api.dto;

import java.time.LocalDate;

public record FlightBookingStatsDto(
        String flightNumber,
        LocalDate departureDate,
        String fromIata,
        String toIata,
        String airlineCode,
        Long totalBookings
) {}
