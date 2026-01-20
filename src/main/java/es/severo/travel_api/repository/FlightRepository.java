package es.severo.travel_api.repository;

import es.severo.travel_api.domain.Airline;
import es.severo.travel_api.domain.Airport;
import es.severo.travel_api.domain.Flight;
import es.severo.travel_api.domain.FlightStatus;
import es.severo.travel_api.dto.AirlineFlightCountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight> findByDepartureAirportCodeAndArrivalAirportCode(String from, String to);

    boolean existsByFlightNumberAndDepartureDateAndIdNot(String flightNumber, LocalDate departureDate, Long id);

    Optional<Flight> findByStatusAndDepartureDate(FlightStatus status, LocalDate departureDate);

    Optional<Flight> findByDepartureAirportCode(String code);

    Optional<Flight> findByAirlineCodeOrderByDepartureDateAscDepartureTimeAsc(String code);

    Optional<Flight> findTop5ByDepartureAirportCodeAndArrivalAirportCodeAndDepartureDateOrderByBasePriceAsc(
            String depCode, String arrCode, LocalDate date);

    long countByAirlineCode(String code);

    @Query("SELECT new es.severo.travel_api.dto.AirlineFlightCountDto(f.airline.code, f.airline.name, COUNT(f)) " +
            "FROM Flight f GROUP BY f.airline.code, f.airline.name")
    List<AirlineFlightCountDto> findAirlineFlightStats();
}
