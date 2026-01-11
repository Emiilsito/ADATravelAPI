package es.severo.travel_api.repository;

import es.severo.travel_api.domain.Airline;
import es.severo.travel_api.domain.Airport;
import es.severo.travel_api.domain.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight> findByDepartureAirportCodeAndArrivalAirportCode(String from, String to);

    boolean existsByFlightNumberAndDepartureDateAndIdNot(String flightNumber, LocalDate departureDate, Long id);
}
