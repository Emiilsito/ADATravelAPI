package es.severo.travel_api.repository;

import es.severo.travel_api.domain.Flight;
import es.severo.travel_api.domain.FlightStatus;
import es.severo.travel_api.domain.BookingStatus;
import es.severo.travel_api.dto.AirlineFlightCountDto;
import es.severo.travel_api.dto.FlightBookingStatsDto;
import es.severo.travel_api.dto.FlightSearchResultDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
        SELECT new es.severo.travel_api.dto.FlightSearchResultDto(
            f.id, f.flightNumber, f.departureDate, f.departureTime,
            f.departureAirport.code, f.arrivalAirport.code, f.airline.code, f.status, f.basePrice
        )
        FROM Flight f
        WHERE (:from IS NULL OR f.departureAirport.code = :from)
        AND (:to IS NULL OR f.arrivalAirport.code = :to)
        AND (cast(:dateFrom as date) IS NULL OR f.departureDate >= :dateFrom)
        AND (cast(:dateTo as date) IS NULL OR f.departureDate <= :dateTo)
        AND (:minPrice IS NULL OR f.basePrice >= :minPrice)
        AND (:maxPrice IS NULL OR f.basePrice <= :maxPrice)
    """)
    Page<FlightSearchResultDto> searchFlights(
            @Param("from") String from,
            @Param("to") String to,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    @Query("""
        SELECT new es.severo.travel_api.dto.FlightBookingStatsDto(
            f.flightNumber, f.departureDate, f.departureAirport.code, f.arrivalAirport.code, f.airline.code, COUNT(b)
        )
        FROM Flight f JOIN f.bookings b
        WHERE f.departureDate = :date
        AND (:status IS NULL OR b.status = :status)
        GROUP BY f.id, f.flightNumber, f.departureDate, f.departureAirport.code, f.arrivalAirport.code, f.airline.code
        ORDER BY COUNT(b) DESC
    """)
    Page<FlightBookingStatsDto> findTopBookedFlights(
            @Param("date") LocalDate date,
            @Param("status") BookingStatus status,
            Pageable pageable
    );
}