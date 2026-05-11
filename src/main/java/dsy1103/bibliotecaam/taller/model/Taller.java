package dsy1103.bibliotecaam.taller.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "taller")
public class Taller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTaller;

    @Column(nullable = false,length = 30)
    private String nombreTaller;

    @Column(nullable = false)
    private LocalDate fechaTaller;

    /*
    AQUI VA: idEmpleado
     */
}
