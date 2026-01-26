package es.severo.travel_api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateGroupBookingsRequest(
        @NotBlank(message = "Debe de haber un grupo de referencia")
        String groupRef,

        @Email(message = "Formato invalido de email")
        String contactEmail,

        @NotNull
        @Size(min = 2, max = 6, message = "Los grupos de pasajeros tienen que ser entre 2 y 6")
        @Valid
        List<PassengerSeatRequest> passengers
) {}
