package es.severo.travel_api.service;

import es.severo.travel_api.domain.Booking;
import es.severo.travel_api.domain.BookingStatus;
import es.severo.travel_api.dto.BookingDto;
import es.severo.travel_api.dto.request.PatchBookingStatusRequest;
import es.severo.travel_api.repository.BookingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
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

        return new BookingDto(s.getId(), s.getLocator(), s.getBookingDateTime(),
                s.getPricePaid(), s.getStatus(), s.getPassenger().getId(),
                s.getFlight().getId(), s.getSeat().getId());
    }

    private BookingDto toDto(Booking b) {
        return new BookingDto(
                b.getId(), b.getLocator(), b.getBookingDateTime(), b.getPricePaid(), b.getStatus(),
                b.getPassenger().getId(), b.getFlight().getId(), b.getSeat().getId()
        );
    }

    @Transactional(readOnly = true)
    public Page<BookingDto> getBookingsPage(Pageable pageable) {
        return bookingRepository.findAll(pageable).map(this::toDto);
    }
}
