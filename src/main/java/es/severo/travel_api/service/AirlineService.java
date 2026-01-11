package es.severo.travel_api.service;

import es.severo.travel_api.domain.Airline;
import es.severo.travel_api.dto.AirlineDto;
import es.severo.travel_api.repository.AirlineRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AirlineService {

    private final AirlineRepository airlineRepository;

    public AirlineService(AirlineRepository airlineRepository) {
        this.airlineRepository = airlineRepository;
    }

    @Transactional
    public List<AirlineDto> findAll(){
        return airlineRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public AirlineDto findById(Long id){
        return airlineRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aerolinea " + id + " no encontrada."));
    }

    private AirlineDto toDto(Airline airline){
        return new AirlineDto(airline.getId(), airline.getCode(), airline.getName());
    }
}
