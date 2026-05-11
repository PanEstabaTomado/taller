package dsy1103.bibliotecaam.taller.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TallerResponseDTO {

    private Long idTaller;

    private String nombreTaller;

    private LocalDate fechaTaller;
    /*
    private Long idEmpleado;
     */
}
