package es.severo.travel_api.controller;

import es.severo.travel_api.dto.*;
import es.severo.travel_api.dto.request.CreateFlightRequest;
import es.severo.travel_api.dto.request.CreateGroupBookingsRequest;
import es.severo.travel_api.dto.request.UpdateFlightRequest;
import es.severo.travel_api.dto.request.UpdateStatusFlightRequest;
import es.severo.travel_api.service.BookingService;
import es.severo.travel_api.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Flights", description = "Gestión de vuelos y estadísticas")
@RestController
@RequestMapping("/flights")
public class FlightController {

    private final FlightService flightService;
    private final BookingService bookingService;

    public FlightController(FlightService flightService, BookingService bookingService) {
        this.flightService = flightService;
        this.bookingService = bookingService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<FlightDto>> findAll() {
        return ResponseEntity.ok(flightService.findAll());
    }

    @Operation(summary = "Obtener vuelo por ID", description = "Devuelve los datos de un vuelo específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vuelo encontrado"),
            @ApiResponse(responseCode = "404", description = "Vuelo no existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FlightDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(flightService.findById(id));
    }

    @Operation(summary = "Crear vuelo", description = "Registra un nuevo vuelo en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vuelo creado"),
            @ApiResponse(responseCode = "404", description = "Aerolínea o aeropuerto no existen")
    })
    @PostMapping
    public ResponseEntity<FlightDto> create(@Valid @RequestBody CreateFlightRequest req) {
        FlightDto created = flightService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, "/api/flights/" + created.id())
                .body(created);
    }

    @Operation(summary = "Actualizar vuelo", description = "Reemplaza todos los datos de un vuelo.")
    @PutMapping("/{id}")
    public ResponseEntity<FlightDto> update(@PathVariable Long id, @Valid @RequestBody UpdateFlightRequest req) {
        return ResponseEntity.ok(flightService.update(id, req));
    }

    @Operation(summary = "Borrar vuelo", description = "Elimina un vuelo si no tiene reservas asociadas.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Eliminado"),
            @ApiResponse(responseCode = "409", description = "No se puede borrar: tiene reservas")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        flightService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cambiar estado vuelo", description = "Modifica solo el estado operacional del vuelo.")
    @PatchMapping("/{id}")
    public ResponseEntity<FlightDto> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusFlightRequest req) {
        return ResponseEntity.ok(flightService.updateStatusFlight(id, req));
    }

    @Operation(summary = "Búsqueda avanzada de vuelos", description = "Buscador con filtros y paginación.")
    @GetMapping("/search")
    public ResponseEntity<Page<FlightSearchResultDto>> search(
            @Parameter(description = "Origin IATA code", example = "ALC")
            @RequestParam(required = false) @Pattern(regexp = "[A-Z]{3}") String from,

            @Parameter(description = "Destination IATA code", example = "BCN")
            @RequestParam(required = false) @Pattern(regexp = "[A-Z]{3}") String to,

            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(required = false) @Min(0) BigDecimal minPrice,
            @RequestParam(required = false) @Min(0) BigDecimal maxPrice,

            @Parameter(hidden = true) Pageable pageable
    ) {
        return ResponseEntity.ok(flightService.search(from, to, dateFrom, dateTo, minPrice, maxPrice, pageable));
    }

    @Operation(summary = "Crear reservas de grupo", description = "Crea reservas para un grupo de 2 a 6 pasajeros en un vuelo.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reservas creadas"),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflicto en reserva")
    })
    @PostMapping("/{flightId}/bookings/group")
    public ResponseEntity<GroupBookingsResultDto> createGroupBooking(
            @PathVariable Long flightId,
            @Valid @RequestBody CreateGroupBookingsRequest request) {

        GroupBookingsResultDto result = bookingService.createGroupBooking(flightId, request);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/flights/{id}/bookings/group/{ref}")
                .buildAndExpand(flightId, result.groupRef())
                .toUri();

        return ResponseEntity.created(location).body(result);
    }

    @Operation(summary = "Estadísticas aerolíneas", description = "Total de vuelos operados por cada aerolínea.")
    @GetMapping("/statistics/airlines")
    public ResponseEntity<List<AirlineFlightCountDto>> getAirlineStats() {
        return flightService.getAirlineStats()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}