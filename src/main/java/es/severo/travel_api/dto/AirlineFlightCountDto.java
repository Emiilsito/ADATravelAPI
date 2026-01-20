package es.severo.travel_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AirlineFlightCountDto(
        @Schema(example = "IB") String airlineCode,
        @Schema(example = "Iberia") String airlineName,
        @Schema(example = "150") Long totalFlights
) {}
