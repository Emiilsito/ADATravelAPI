package es.severo.travel_api.service;

//Beans, IoC, DI
//Beans --> objeto, instancia
//IoC --> Inversion of Control -->
//DI --> Dependency Inyection
//ApplicationContext

import es.severo.travel_api.domain.Airline;
import es.severo.travel_api.domain.Airport;
import es.severo.travel_api.domain.Flight;
import es.severo.travel_api.domain.FlightStatus;
import es.severo.travel_api.dto.AirlineFlightCountDto;
import es.severo.travel_api.dto.FlightDto;
import es.severo.travel_api.dto.request.CreateFlightRequest;
import es.severo.travel_api.dto.request.UpdateFlightRequest;
import es.severo.travel_api.dto.request.UpdateStatusFlightRequest;
import es.severo.travel_api.repository.AirlineRepository;
import es.severo.travel_api.repository.AirportRepository;
import es.severo.travel_api.repository.BookingRepository;
import es.severo.travel_api.repository.FlightRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;
    private final BookingRepository bookingRepository;

    public FlightService(FlightRepository flightRepository, AirlineRepository airlineRepository, AirportRepository airportRepository, BookingRepository bookingRepository) {
        this.flightRepository = flightRepository;
        this.airlineRepository = airlineRepository;
        this.airportRepository = airportRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public List<FlightDto> findAll() {
        List<Flight> flights = flightRepository.findAll();
        return flights.stream().map(flight -> toDto(flight)).toList();
    }

    @Transactional
    public FlightDto updateStatusFlight(Long id, UpdateStatusFlightRequest req) {
        if (req == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cuerpo no puede estar vacío");
        }
        Flight f = flightRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"El vuelo " + id + " no existe"));
        f.setStatus(req.status());
        //flightRepository.save(f);
        return toDto(f);
    }

    @Transactional
    public FlightDto create(CreateFlightRequest req) {
        if (req == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cuerpo no puede estar vacío");
        }
        Airline airline = airlineRepository
                .findByCode(req.airlineCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"La aerolínea " + req.airlineCode() + " no existe"));

        Airport departure = airportRepository.findByCode(req.departureAirportCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"El aeropuerto de origen " + req.departureAirportCode() + " no existe"));

        Airport arrival = airportRepository.findByCode(req.arrivalAirportCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"El aeropuerto de llegada " + req.arrivalAirportCode() + " no existe"));
        Flight f = new Flight();
        f.setFlightNumber(req.flightNumber());
        f.setArrivalDate(req.arrivalDate());
        f.setDepartureDate(req.departureDate());
        f.setDepartureTime(req.departureTime());
        f.setArrivalTime(req.arrivalTime());
        f.setDurationMinutes(req.durationMinutes());
        f.setStatus(req.status());
        f.setBasePrice(req.basePrice());
        f.setAirline(airline);
        f.setArrivalAirport(arrival);
        f.setDepartureAirport(departure);

        Flight saved = flightRepository.save(f);
        return toDto(saved);
    }

    @Transactional
    public FlightDto update(Long id, UpdateFlightRequest req) {
        Flight flight = flightRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Comprobar maestros (404)
        Airline airline = airlineRepository.findById(req.airlineId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aerolínea no existe"));
        Airport dep = airportRepository.findById(req.departureAirportId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Origen no existe"));
        Airport arr = airportRepository.findById(req.arrivalAirportId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Destino no existe"));

        // Comprobar duplicado (409)
        if (flightRepository.existsByFlightNumberAndDepartureDateAndIdNot(req.flightNumber(), req.departureDate(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Conflicto de vuelo");
        }

        flight.setFlightNumber(req.flightNumber());
        flight.setDepartureDate(req.departureDate());
        flight.setArrivalDate(req.arrivalDate());
        flight.setDepartureTime(req.departureTime());
        flight.setArrivalTime(req.arrivalTime());
        flight.setBasePrice(req.basePrice());
        flight.setStatus(FlightStatus.valueOf(req.status()));
        flight.setAirline(airline);
        flight.setDepartureAirport(dep);
        flight.setArrivalAirport(arr);

        Flight s = flightRepository.save(flight);
        return new FlightDto(s.getId(), s.getFlightNumber(), s.getDepartureDate(), s.getArrivalDate(), s.getDepartureTime(), s.getArrivalTime(), s.getDurationMinutes(), s.getBasePrice(), s.getStatus(), s.getAirline().getCode(), s.getDepartureAirport().getCode(), s.getArrivalAirport().getCode());
    }

    @Transactional(readOnly = true)
    public FlightDto findById(Long id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El id es nulo");
        }
        return flightRepository.findById(id)
                .map(flight -> toDto(flight))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el vuelo " + id));
    }

    @Transactional
    public void delete(Long id) {
        if (!flightRepository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (bookingRepository.existsByFlightId(id)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Vuelo con reservas");
        flightRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<FlightDto> searchByAirports(String from, String to) {
        List<Flight> flights = flightRepository.findByDepartureAirportCodeAndArrivalAirportCode(from, to);

        return flights.stream()
                .map(flight -> toDto(flight))
                .toList();
    }

    private FlightDto toDto(Flight flight) {
        return new FlightDto(flight.getId(),
                flight.getFlightNumber(), flight.getDepartureDate(), flight.getArrivalDate(),
                flight.getDepartureTime(), flight.getArrivalTime(),flight.getDurationMinutes(),
                flight.getBasePrice(), flight.getStatus(),
                flight.getAirline().getCode(),
                flight.getDepartureAirport().getCode(), flight.getArrivalAirport().getCode());
    }

    @Transactional(readOnly = true)
    public Optional<List<AirlineFlightCountDto>> getAirlineStats() {
        List<AirlineFlightCountDto> stats = flightRepository.findAirlineFlightStats();
        return stats.isEmpty() ? Optional.empty() : Optional.of(stats);
    }


}
