package es.severo.travel_api.controller;

import es.severo.travel_api.domain.Passenger;
import es.severo.travel_api.dto.PassengerDto;
import es.severo.travel_api.dto.request.CreatePassengerRequest;
import es.severo.travel_api.dto.request.UpdatePassengerRequest;
import es.severo.travel_api.service.PassengerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/passengers")
public class PassengerController {

    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @GetMapping
    public ResponseEntity<List<PassengerDto>> findAll() {
        return ResponseEntity.ok(passengerService.findAll());
    }

    //200,400,404,409(CONFLICT)
    @PutMapping("/{id}")
    public ResponseEntity<PassengerDto> update(
            @PathVariable Long id,
            @RequestBody UpdatePassengerRequest req
            ) {
        return ResponseEntity.ok(passengerService.update(id, req));
    }

    @PostMapping
    public ResponseEntity<PassengerDto> create(@Valid @RequestBody CreatePassengerRequest req) {
        PassengerDto created = passengerService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, "/passengers/" + created.id())
                .body(created);
    }
}
