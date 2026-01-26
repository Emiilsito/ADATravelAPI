package es.severo.travel_api.controller;

import es.severo.travel_api.dto.FlightBookingStatsDto;
import es.severo.travel_api.domain.BookingStatus;
import es.severo.travel_api.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/reports")
@Tag(name = "Reports", description = "Analytics and statistics")
public class ReportController {

    private final FlightService flightService;

    public ReportController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/flights/top-booked")
    @Operation(summary = "Ranking de vuelos por reservas", description = "Devuelve vuelos ordenados por número de reservas para una fecha dada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reporte generado"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    public ResponseEntity<Page<FlightBookingStatsDto>> getTopBookedFlights(
            @Parameter(description = "Fecha del vuelo", example = "2026-02-01", required = true)
            @RequestParam LocalDate departureDate,

            @Parameter(description = "Filtrar por estado de reserva (opcional)", example = "CONFIRMED")
            @RequestParam(required = false) BookingStatus status,

            @Parameter(hidden = true) Pageable pageable
    ) {
        return ResponseEntity.ok(flightService.getTopBookedFlights(departureDate, status, pageable));
    }
}
