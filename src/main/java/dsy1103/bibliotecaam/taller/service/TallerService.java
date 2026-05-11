package dsy1103.bibliotecaam.taller.service;

import dsy1103.bibliotecaam.taller.dto.TallerRequestDTO;
import dsy1103.bibliotecaam.taller.dto.TallerResponseDTO;
import dsy1103.bibliotecaam.taller.model.Taller;
import dsy1103.bibliotecaam.taller.repository.TallerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TallerService {
    private final TallerRepository tallerRepository;

    private TallerResponseDTO mapToDOTO(Taller taller){
        return new TallerResponseDTO(
                taller.getIdTaller(),
                taller.getNombreTaller(),
                taller.getFechaTaller()
        );
    }
    /*
    private void validarEmpleado(Long idEmpleado) {
        try {
            webClient.get()
                    .uri("/api/empleado/{id}", idEmpleado)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info(">>> Empleado {} validado correctamente (WebClient)", idEmpleado);

        } catch (WebClientResponseException.NotFound e) {
            throw new RuntimeException(
                    "El empleado con el id " + idEmpleado + " no existe en la tabla Empleado.");
        } catch (Exception e) {
            throw new RuntimeException(
                    "No se puede conectar con empleado: " + e.getMessage());
        }
    }
     */




    /*
     * * ---------------------- C R U D ---------------------
     */

    public List<TallerResponseDTO> obtenerTodas() {
        return tallerRepository.findAll().stream()
                .map(this::mapToDOTO).collect(Collectors.toList());
    }

    public Optional<TallerResponseDTO> obtenerPorId(Long id) {
        return tallerRepository.findById(id).map(this::mapToDOTO);
    }

    public TallerResponseDTO guardar(TallerRequestDTO dto) {
        // validarEmpleado(dto.getIdEmpleado());
        Taller t = new Taller(
                null,
                dto.getNombreTaller(),
                dto.getFechaTaller());
        // dto.getIdEmpleado());
        return mapToDOTO(tallerRepository.save(t));
    }

    public Optional<TallerResponseDTO> actualizar(Long id, TallerRequestDTO dto) {
        return tallerRepository.findById(id).map(existente -> {
            //validarEspecialidad(dto.getEspecialidadId());
            existente.setNombreTaller(dto.getNombreTaller());
            existente.setFechaTaller(dto.getFechaTaller());
            //existente.setIdEmpleado(dto.getIdEmpleado));
            return mapToDOTO(tallerRepository.save(existente));
        });
    }

    public void eliminar(Long id) {
        tallerRepository.deleteById(id);
    }

    // ------------------------------------------------------------------------
    // * * Funciones Extras * *
    // ------------------------------------------------------------------------
    public List<TallerResponseDTO> obtenerPorNombre(String nombre) {
        return tallerRepository.findByNombreTaller(nombre).stream()
                .map(this::mapToDOTO).collect(Collectors.toList());
    }
}
