package dsy1103.bibliotecaam.taller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TallerRequestDTO {

    @NotBlank(message = "El nombre del taller no puede estar vacio.")
    private String nombreTaller;

    @NotNull(message = "La fecha en la que se realizo el taller debe especificarse: Año-Mes-Dia (EJ: 1990-09-19)")
    private LocalDate fechaTaller;

    @NotNull(message = "El id del empleado a cargo debe estar presente.")
    private Long idEmpleado;

}
