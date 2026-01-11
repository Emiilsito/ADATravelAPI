package es.severo.travel_api.controller;

import es.severo.travel_api.domain.Flight;
import es.severo.travel_api.dto.FlightDto;
import es.severo.travel_api.dto.request.CreateFlightRequest;
import es.severo.travel_api.dto.request.UpdateFlightRequest;
import es.severo.travel_api.dto.request.UpdateStatusFlightRequest;
import es.severo.travel_api.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

/*
DTOs --> request, dto
Controller -->  @RestController
-GET -->
-POST
-PUT
-PATCH
-DELETE

Service --> @Service
Entidades --> anotaciones
Repository --> JPARepository
 */
@RestController
@RequestMapping("/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<FlightDto>> findAll() {
        return null;
    }

    //404, 200,
    //http://localhost:8081/api/flights/5
    @GetMapping("/{id}")
    public ResponseEntity<FlightDto> get(@PathVariable Long id) {
        FlightDto dto = flightService.findById(id);
        return ResponseEntity.ok(dto);
    }

    // POST /api/flights -> 201, 400, 404
    @PostMapping
    public ResponseEntity<FlightDto> create(@Valid @RequestBody CreateFlightRequest req) {
        FlightDto created = flightService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, "/flights/" + created.id())
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightDto> update(@PathVariable Long id, @Valid @RequestBody UpdateFlightRequest req) {
        return ResponseEntity.ok(flightService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        flightService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //200, 404, 400
    @PatchMapping("/{id}")
    public ResponseEntity<FlightDto> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusFlightRequest req
    ) {
        return ResponseEntity.ok(flightService.updateStatusFlight(id, req));
    }

    // GET /api/flights/search?from=MAD&to=BCN -> 200
    @GetMapping("/search")
    public ResponseEntity<List<FlightDto>> search(
            @RequestParam(required = false, defaultValue = "") String from,
            @RequestParam String to
    ) {
        List<FlightDto> results = flightService.searchByAirports(from, to);
        return ResponseEntity.ok(results);
    }
}
