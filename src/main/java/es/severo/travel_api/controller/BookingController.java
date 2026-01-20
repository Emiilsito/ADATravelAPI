package es.severo.travel_api.controller;

import es.severo.travel_api.dto.BookingDto;
import es.severo.travel_api.dto.FlightBookingCountDto;
import es.severo.travel_api.dto.request.PatchBookingStatusRequest;
import es.severo.travel_api.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Bookings", description = "Gestión de reservas de vuelos")
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "Eliminar reserva", description = "Borra una reserva del sistema por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reserva eliminada"),
            @ApiResponse(responseCode = "404", description = "No se encuentra la reserva")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID de la reserva") @PathVariable Long id) {
        bookingService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Actualizar estado", description = "Cambia el estado de una reserva (CONFIRMED, CANCELLED, etc).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "400", description = "Estado no válido"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingDto> updateStatus(@PathVariable Long id, @Valid @RequestBody PatchBookingStatusRequest req) {
        return ResponseEntity.ok(bookingService.updateStatus(id, req));
    }

    @Operation(summary = "Listado paginado", description = "Devuelve las reservas por páginas (máximo 30 por página).")
    @GetMapping("/page")
    public ResponseEntity<Page<BookingDto>> getBookingsPage(
            @PageableDefault(size = 6, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        if (pageable.getPageSize() > 30) {
            pageable = PageRequest.of(pageable.getPageNumber(), 30, pageable.getSort());
        }

        return ResponseEntity.ok(bookingService.getBookingsPage(pageable));
    }

    @Operation(summary = "Informe diario", description = "Número de reservas por vuelo en una fecha concreta.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Informe generado"),
            @ApiResponse(responseCode = "404", description = "Sin datos para esa fecha")
    })
    @GetMapping("/reports/daily-bookings")
    public ResponseEntity<List<FlightBookingCountDto>> getReportByDate(
            @Parameter(description = "Fecha de salida", example = "2026-05-20") @RequestParam LocalDate date) {
        return bookingService.getBookingsByDate(date)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}