package es.severo.travel_api.service;

import es.severo.travel_api.domain.Airport;
import es.severo.travel_api.dto.AirportDto;
import es.severo.travel_api.repository.AirportRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AirportService {
    private final AirportRepository airportRepository;

    public AirportService(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    @Transactional
    public List<AirportDto> findAll() {
        return airportRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public AirportDto findById(Long id) {
        return airportRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aeropuerto " + id + " no encontrado"));
    }

    private AirportDto toDto(Airport airport) {
        return new AirportDto(airport.getId(), airport.getCode(), airport.getName(), airport.getCity(), airport.getCountry());
    }
}
