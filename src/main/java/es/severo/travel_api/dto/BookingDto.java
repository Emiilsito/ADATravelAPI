package es.severo.travel_api.dto;

import es.severo.travel_api.domain.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookingDto(
        Long id,
        String locator,
        LocalDateTime bookingDateTime,
        BigDecimal pricePaid,
        BookingStatus status,
        Long passengerId,
        Long flightId,
        Long seatId
) {
}
