package es.severo.travel_api.dto.request;

import jakarta.validation.constraints.NotNull;

public record PatchBookingStatusRequest(
        @NotNull String status
) {}
