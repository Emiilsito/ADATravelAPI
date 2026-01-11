package es.severo.travel_api.dto;

public record SeatDto(Long id, String seatNumber, String cabinClass, Long flightId) {
}
