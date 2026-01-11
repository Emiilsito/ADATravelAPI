package es.severo.travel_api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
        name = "flights",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"flight_number", "departure_date"})
        }
)
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_number", nullable = false, length = 10)
    private String flightNumber;

    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    @Column(name = "arrival_date", nullable = false)
    private LocalDate arrivalDate;

    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "base_price", precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FlightStatus status;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "airline_id", nullable = false)
    @ToString.Exclude
    private Airline airline;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_airport_id", nullable = false)
    @ToString.Exclude
    private Airport departureAirport;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "arrival_airport_id", nullable = false)
    @ToString.Exclude
    private Airport arrivalAirport;
}