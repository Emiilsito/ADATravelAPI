package es.severo.travel_api.service;

import es.severo.travel_api.domain.*;
import es.severo.travel_api.dto.*;
import es.severo.travel_api.dto.request.CreateGroupBookingsRequest;
import es.severo.travel_api.dto.request.PassengerSeatRequest;
import es.severo.travel_api.dto.request.PatchBookingStatusRequest;
import es.severo.travel_api.repository.BookingRepository;
import es.severo.travel_api.repository.FlightRepository;
import es.severo.travel_api.repository.PassengerRepository;
import es.severo.travel_api.repository.SeatRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;
    private final SeatRepository seatRepository;

    public BookingService(BookingRepository bookingRepository,
                          FlightRepository flightRepository,
                          PassengerRepository passengerRepository,
                          SeatRepository seatRepository) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.passengerRepository = passengerRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public void delete(Long id) {
        if(!bookingRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La reserva no existe");
        }
        bookingRepository.deleteById(id);
    }

    @Transactional
    public BookingDto updateStatus(Long id, PatchBookingStatusRequest req) {
        Booking b = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        try {
            b.setStatus(BookingStatus.valueOf(req.status().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado no válido");
        }

        Booking s = bookingRepository.save(b);
        return toDto(s);
    }

    @Transactional
    public GroupBookingsResultDto createGroupBooking(Long flightId, CreateGroupBookingsRequest request) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Flight not found"));

        if (flight.getStatus() == FlightStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot book a cancelled flight");
        }

        List<BookingDto> savedBookingsDtos = new ArrayList<>();

        for (PassengerSeatRequest pRequest : request.passengers()) {
            Passenger passenger = passengerRepository.findById(pRequest.passengerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger " + pRequest.passengerId() + " not found"));

            Seat seat = seatRepository.findByFlightIdAndSeatNumber(flightId, pRequest.seatNumber())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat " + pRequest.seatNumber() + " not found on this flight"));

            if (bookingRepository.existsByFlightIdAndPassengerIdAndStatusNot(flightId, passenger.getId(), BookingStatus.CANCELLED)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Passenger " + passenger.getId() + " already has a booking");
            }

            if (bookingRepository.isSeatOccupied(flightId, pRequest.seatNumber())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Seat " + pRequest.seatNumber() + " is already occupied");
            }

            Booking booking = new Booking();
            booking.setFlight(flight);
            booking.setPassenger(passenger);
            booking.setSeat(seat);
            booking.setBookingDateTime(LocalDateTime.now());
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setPricePaid(flight.getBasePrice().add(new BigDecimal("100")));

            String locator = request.groupRef() + "-" + passenger.getId() + "-" + seat.getSeatNumber();
            booking.setLocator(locator);

            Booking saved = bookingRepository.save(booking);
            savedBookingsDtos.add(toDto(saved));
        }

        return new GroupBookingsResultDto(flightId, request.groupRef(), request.contactEmail(), savedBookingsDtos);
    }

    @Transactional(readOnly = true)
    public Page<BookingDto> getBookingsPage(Pageable pageable) {
        return bookingRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<List<FlightBookingCountDto>> getBookingsByDate(LocalDate date) {
        List<FlightBookingCountDto> report = bookingRepository.countBookingsByFlightOnDate(date);
        return report.isEmpty() ? Optional.empty() : Optional.of(report);
    }

    private BookingDto toDto(Booking b) {
        Long seatId = (b.getSeat() != null) ? b.getSeat().getId() : null;

        return new BookingDto(
                b.getId(),
                b.getLocator(),
                b.getBookingDateTime(),
                b.getPricePaid(),
                b.getStatus(),
                b.getPassenger().getId(),
                b.getFlight().getId(),
                seatId
        );
    }
}