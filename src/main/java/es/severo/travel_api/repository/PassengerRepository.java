package es.severo.travel_api.repository;

import es.severo.travel_api.domain.Flight;
import es.severo.travel_api.domain.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {

   boolean existsByDocumentNumberAndIdIsNot(String documentNumber, Long id);

    boolean existsByDocumentNumberIgnoreCase(String documentNumber);

    Optional<Passenger> findByDocumentNumberIgnoreCase(String documentNumber);
    boolean existsByIdNotAndDocumentNumberContainingIgnoreCase(Long id, String pattern);}
