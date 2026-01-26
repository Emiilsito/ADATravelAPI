package es.severo.travel_api.dto;

import java.util.List;

public record GroupBookingsResultDto(
        Long flightId,
        String groupRef,
        String contactEmail,
        List<BookingDto> createdBookings
) {}
