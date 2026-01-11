package es.severo.travel_api.repository;

import es.severo.travel_api.domain.Flight;
import es.severo.travel_api.domain.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {

   boolean existsByDocumentNumberAndIdIsNot(String documentNumber, Long id);

    boolean existsByDocumentNumberIgnoreCase(String documentNumber);
}
