package es.severo.travel_api.controller;

import es.severo.travel_api.dto.AirlineFlightCountDto;
import es.severo.travel_api.dto.FlightDto;
import es.severo.travel_api.dto.request.CreateFlightRequest;
import es.severo.travel_api.dto.request.UpdateFlightRequest;
import es.severo.travel_api.dto.request.UpdateStatusFlightRequest;
import es.severo.travel_api.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Flights", description = "Gestión de vuelos y estadísticas")
@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
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

    @Operation(summary = "Búsqueda por aeropuertos", description = "Filtra vuelos por códigos IATA de origen y destino.")
    @GetMapping("/search")
    public ResponseEntity<List<FlightDto>> search(
            @Parameter(description = "IATA Origen", example = "MAD") @RequestParam(required = false, defaultValue = "") String from,
            @Parameter(description = "IATA Destino", example = "BCN") @RequestParam String to) {
        return ResponseEntity.ok(flightService.searchByAirports(from, to));
    }

    @Operation(summary = "Estadísticas aerolíneas", description = "Total de vuelos operados por cada aerolínea.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Éxito"),
            @ApiResponse(responseCode = "204", description = "Sin datos")
    })
    @GetMapping("/statistics/airlines")
    public ResponseEntity<List<AirlineFlightCountDto>> getAirlineStats() {
        return flightService.getAirlineStats()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}