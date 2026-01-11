package es.severo.travel_api.dto;

import es.severo.travel_api.domain.FlightStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record FlightDto(
        Long id,
        String flightNumber,
        LocalDate departureDate,
        LocalDate arrivalDate,
        LocalTime departureTime,
        LocalTime arrivalTime,
        Integer durationMinutes,
        BigDecimal basePrice,
        FlightStatus status,
        String airlineCode,
        String departureAirportCode,
        String arrivalAirportCode
) {
}
