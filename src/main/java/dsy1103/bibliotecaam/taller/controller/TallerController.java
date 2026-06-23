package dsy1103.bibliotecaam.taller.controller;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import dsy1103.bibliotecaam.taller.assembler.TallerModelAssembler;
import dsy1103.bibliotecaam.taller.dto.TallerRequestDTO;
import dsy1103.bibliotecaam.taller.dto.TallerResponseDTO;
import dsy1103.bibliotecaam.taller.model.Taller;
import dsy1103.bibliotecaam.taller.service.TallerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/bibliotecaam/taller")
@RequiredArgsConstructor
@Tag(name = "Talleres", description = "Lista de talleres de la biblioteca")
public class TallerController {
    private final TallerService tallerService;
    @Autowired
    private TallerModelAssembler assembler;

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @ArraySchema(schema = @Schema(implementation = Taller.class))
    @Operation(summary = "Obtener los datos de todos los talleres.", description = "Esta opcion retornara los datos de todos los talleres en la Base de Datos.")
    public ResponseEntity<CollectionModel<EntityModel<TallerResponseDTO>>> obtenerTodos(){
        List<EntityModel<TallerResponseDTO>> talleres = tallerService.obtenerTodas().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(talleres,
                linkTo(methodOn(TallerController.class).obtenerTodos()).withSelfRel()));
    }
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Obtener los datos por el id del taller", description = "Se retornara el taller que coincida con el id ingresado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "¡Taller encontrado con exito!"),
            @ApiResponse(responseCode = "404",description = "ERROR: ¡El id del taller ingresado no existe!")
    })
    public ResponseEntity<EntityModel<TallerResponseDTO>> obtenerPorId(@PathVariable Long id){
        return tallerService.obtenerPorId(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombre}")
    @ArraySchema(schema = @Schema(implementation = Taller.class))
    @Operation(summary = "Obtener los talleres por el nombre", description = "Se retornaran los talleres que coincidan con el nombre ingresado.")
    public ResponseEntity<CollectionModel<EntityModel<TallerResponseDTO>>> obtenerPorISBN(@PathVariable String nombre){
        List<EntityModel<TallerResponseDTO>> talleres = tallerService.obtenerPorNombre(nombre).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(talleres,
                linkTo(methodOn(TallerController.class).obtenerPorISBN(nombre)).withSelfRel()));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo taller en la Base de Datos", description = "Se creara y guardaran los datos de un nuevo taller creado en la Base de Datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "¡Taller actualizado con exito!",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Taller.class))),
            @ApiResponse(responseCode = "400",description = "Puede que no se este comunicando con la tabla Donacion/Faltan parametros.")
    })
    private ResponseEntity<EntityModel<TallerResponseDTO>> guardar(@Valid @RequestBody TallerRequestDTO dto, @RequestHeader("Authorization") String token){
        TallerResponseDTO nuevoTaller = tallerService.guardar(dto, token);
        return ResponseEntity.status(201).body(assembler.toModel(nuevoTaller));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un taller de la Base de Datos", description = "Se actualizaran los datos de un taller ingresando su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "¡Taller actualizado con exito!",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Taller.class))),
            @ApiResponse(responseCode = "404",description = "ERROR: ¡El id del taller ingresado no existe!"),
            @ApiResponse(responseCode = "400",description = "Puede que no se este comunicando con la tabla taller/Faltan parametros.")
    })
    public ResponseEntity<TallerResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody TallerRequestDTO dto,
            @RequestHeader("Authorization") String token) {
        return tallerService.actualizar(id, dto, token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        if (tallerService.obtenerPorId(id).isEmpty()){
            Map<String, String> borrado = new LinkedHashMap<>();
            borrado.put("¡ERROR! ", "¡El Taller con id "+id+" no fue encontrado!");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(borrado);
        }else {
            tallerService.eliminar(id);
            Map<String, String> borrado = new LinkedHashMap<>();
            borrado.put("¡EXITO! ", "¡El Taller fue eliminado con exito!");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(borrado);
        }
    }


}
