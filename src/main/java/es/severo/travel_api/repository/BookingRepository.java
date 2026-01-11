package es.severo.travel_api.repository;

import es.severo.travel_api.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByFlightId(Long flightId);
}
