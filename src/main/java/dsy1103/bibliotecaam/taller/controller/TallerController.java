package dsy1103.bibliotecaam.taller.controller;

import dsy1103.bibliotecaam.taller.dto.TallerRequestDTO;
import dsy1103.bibliotecaam.taller.dto.TallerResponseDTO;
import dsy1103.bibliotecaam.taller.service.TallerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bibliotecaam/taller")
@RequiredArgsConstructor
public class TallerController {
    private final TallerService tallerService;

    @GetMapping
    public ResponseEntity<List<TallerResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(tallerService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TallerResponseDTO> obtenerPorId(@PathVariable Long id) {
        return tallerService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<TallerResponseDTO>> obtenerTallerPorNombre(@PathVariable String nombre) {
        if (tallerService.obtenerPorNombre(nombre).isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tallerService.obtenerPorNombre(nombre));
    }

    @PostMapping
    private ResponseEntity<TallerResponseDTO> guardar(@Valid @RequestBody TallerRequestDTO dto, @RequestHeader("Authorization") String token){
        return ResponseEntity.status(201).body(tallerService.guardar(dto, token));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TallerResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody TallerRequestDTO dto,
            @RequestHeader("Authorization") String token) {
        return tallerService.actualizar(id, dto, token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        tallerService.eliminar(id);
        Map<String, String> borrado = new LinkedHashMap<>();
        borrado.put("¡EXITO! ","¡El Taller fue eliminado con exito!");
        tallerService.eliminar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(borrado);
    }


}
