package es.severo.travel_api.dto.request;

import es.severo.travel_api.domain.FlightStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusFlightRequest(@NotNull FlightStatus status) {
}
