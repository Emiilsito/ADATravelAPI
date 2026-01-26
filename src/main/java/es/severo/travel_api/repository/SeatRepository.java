package es.severo.travel_api.repository;

import es.severo.travel_api.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    Optional<Seat> findByFlightIdAndSeatNumber(Long flightId, String seatNumber);
}
