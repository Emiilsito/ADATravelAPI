package es.severo.travel_api.controller;

import es.severo.travel_api.dto.PassengerDto;
import es.severo.travel_api.dto.request.CreatePassengerRequest;
import es.severo.travel_api.dto.request.UpdatePassengerRequest;
import es.severo.travel_api.service.PassengerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Passengers", description = "Gestión de información de pasajeros")
@RestController
@RequestMapping("/api/passengers")
public class PassengerController {

    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @Operation(summary = "Listar todos", description = "Devuelve una lista simple de pasajeros.")
    @GetMapping
    public ResponseEntity<List<PassengerDto>> findAll() {
        return ResponseEntity.ok(passengerService.findAll());
    }

    @Operation(summary = "Modificar pasajero", description = "Actualiza los datos de un pasajero existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Actualizado"),
            @ApiResponse(responseCode = "409", description = "Conflicto con el número de documento")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PassengerDto> update(@PathVariable Long id, @RequestBody UpdatePassengerRequest req) {
        return ResponseEntity.ok(passengerService.update(id, req));
    }

    @Operation(summary = "Registrar pasajero", description = "Crea un nuevo pasajero comprobando que el DNI no esté repetido.")
    @PostMapping
    public ResponseEntity<PassengerDto> create(@Valid @RequestBody CreatePassengerRequest req) {
        PassengerDto created = passengerService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, "/api/passengers/" + created.id())
                .body(created);
    }

    @Operation(summary = "Paginación pasajeros", description = "Listado de pasajeros con soporte para filtros de página y ordenación.")
    @GetMapping("/page")
    public ResponseEntity<Page<PassengerDto>> getPassengersPage(
            @PageableDefault(page = 0, size = 8, sort = "documentNumber", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(passengerService.getPassengersPage(pageable));
    }

    @Operation(summary = "Buscar por documento", description = "Busca los datos de un pasajero por su DNI o Pasaporte.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe el documento")
    })
    @GetMapping("/doc/{documentNumber}")
    public ResponseEntity<PassengerDto> getByDoc(
            @Parameter(description = "DNI del pasajero", example = "12345678Z") @PathVariable String documentNumber) {
        return passengerService.findByDoc(documentNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}