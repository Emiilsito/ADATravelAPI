package es.severo.travel_api.repository;

import es.severo.travel_api.domain.Booking;
import es.severo.travel_api.domain.BookingStatus;
import es.severo.travel_api.dto.FlightBookingCountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByFlightId(Long flightId);

    boolean existsBySeatIdAndFlightId(Long seatId, Long flightId);
    long countByPassengerId(Long passengerId);
    Optional<Booking> findByPassengerIdAndStatus(Long passengerId, BookingStatus status);
    Optional<Booking> findFirstByPassengerIdOrderByBookingDateTimeDesc(Long passengerId);

    // --- Actividad 2 ---
    @Query("SELECT new es.severo.travel_api.dto.FlightBookingCountDto(b.flight.flightNumber, b.flight.departureDate, COUNT(b)) " +
            "FROM Booking b WHERE b.flight.departureDate = :date GROUP BY b.flight.flightNumber, b.flight.departureDate")
    List<FlightBookingCountDto> countBookingsByFlightOnDate(@Param("date") LocalDate date);
}
