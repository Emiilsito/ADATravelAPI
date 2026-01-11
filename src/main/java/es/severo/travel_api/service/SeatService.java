package es.severo.travel_api.service;

import es.severo.travel_api.domain.Seat;
import es.severo.travel_api.dto.SeatDto;
import es.severo.travel_api.repository.SeatRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SeatService {
    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Transactional
    public SeatDto findById(Long id){
        return seatRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asiento " + id + " no encontrado."));
    }

    private SeatDto toDto(Seat seat) {
        return new SeatDto(seat.getId(), seat.getSeatNumber(), seat.getCabinClass(), seat.getFlight().getId());
    }
}
