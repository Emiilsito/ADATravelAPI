package es.severo.travel_api.service;

import es.severo.travel_api.domain.Passenger;
import es.severo.travel_api.dto.PassengerDto;
import es.severo.travel_api.dto.request.CreatePassengerRequest;
import es.severo.travel_api.dto.request.UpdatePassengerRequest;
import es.severo.travel_api.repository.PassengerRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class PassengerService {
    private final PassengerRepository passengerRepository;

    public PassengerService(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    @Transactional
    public PassengerDto create(CreatePassengerRequest req) {
        if (passengerRepository.existsByDocumentNumberIgnoreCase(req.documentNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El DNI ya existe");
        }

        Passenger p = new Passenger();
        p.setDocumentNumber(req.documentNumber());
        p.setGender(req.gender());

        Passenger saved = passengerRepository.save(p);

        return new PassengerDto(saved.getId(), saved.getDocumentNumber(), saved.getGender());
    }

    @Transactional
    public PassengerDto update(Long id, UpdatePassengerRequest req) {
        Passenger p = passengerRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "")
        );

        if (passengerRepository
                .existsByDocumentNumberAndIdIsNot(req.documentNumber(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Ese dni ya existe");
        }
        p.setDocumentNumber(req.documentNumber());
        p.setGender(req.gender());
        return toDto(p);
    }

    private PassengerDto toDto(Passenger passenger) {
        return new PassengerDto(passenger.getId(),
                passenger.getDocumentNumber(),
                passenger.getGender());
    }

    @Transactional
    public List<PassengerDto> findAll() {
        return passengerRepository.findAll().stream()
                .map(p -> new PassengerDto(p.getId(), p.getDocumentNumber(), p.getGender()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<PassengerDto> getPassengersPage(Pageable pageable) {
        Page<Passenger> page = passengerRepository.findAll(pageable);

        return page.map(this::toDto);
    }
}
