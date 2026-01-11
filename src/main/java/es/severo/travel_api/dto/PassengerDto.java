package es.severo.travel_api.dto;

public record PassengerDto(
        Long id,
        String documentNumber,
        String gender
) {
}
