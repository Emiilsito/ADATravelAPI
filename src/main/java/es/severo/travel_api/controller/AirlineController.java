package es.severo.travel_api.controller;

import es.severo.travel_api.dto.AirlineDto;
import es.severo.travel_api.service.AirlineService;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/airlines")
public class AirlineController {
    private final AirlineService airlineService;

    public AirlineController(AirlineService airlineService) {
        this.airlineService = airlineService;
    }

    @GetMapping("/sorted")
    public List<AirlineDto> getAirlinesSorted(Sort sort) {
        return airlineService.getAirlinesSorted(sort);
    }


}
