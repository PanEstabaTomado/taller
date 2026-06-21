package dsy1103.bibliotecaam.taller;

import dsy1103.bibliotecaam.taller.assembler.TallerModelAssembler;
import dsy1103.bibliotecaam.taller.controller.TallerController;
import dsy1103.bibliotecaam.taller.dto.TallerRequestDTO;
import dsy1103.bibliotecaam.taller.dto.TallerResponseDTO;
import dsy1103.bibliotecaam.taller.service.TallerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TallerController.class)
@ActiveProfiles("test")
@Import(TallerModelAssembler.class)
@DisplayName("Tests Unitarios - TallerController")
class TallerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private TallerService tallerService;

    @Test
    @DisplayName("GIVEN: Existen talleres WHEN: GET /api/bibliotecaam/taller THEN: Retorna 200 OK y la lista HAL-JSON")
    void shouldReturnTodosLosTalleres() throws Exception {
        TallerResponseDTO t1 = new TallerResponseDTO(1L, "Taller de Lectura", LocalDate.now(), 10L);
        TallerResponseDTO t2 = new TallerResponseDTO(2L, "Taller de Poesía", LocalDate.now(), 11L);
        List<TallerResponseDTO> lista = Arrays.asList(t1, t2);

        Mockito.when(tallerService.obtenerTodas()).thenReturn(lista);

        mockMvc.perform(get("/api/bibliotecaam/taller")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.tallerResponseDTOList.length()").value(2))
                .andExpect(jsonPath("$._embedded.tallerResponseDTOList[0].idTaller").value(1L))
                .andExpect(jsonPath("$._embedded.tallerResponseDTOList[0].nombreTaller").value("Taller de Lectura"));
    }

    @Test
    @DisplayName("GIVEN: ID válido WHEN: GET /api/bibliotecaam/taller/{id} THEN: Retorna 200 OK y el taller")
    void shouldReturnTallerById() throws Exception {
        Long id = 1L;
        TallerResponseDTO mockResponse = new TallerResponseDTO(id, "Taller de Historia", LocalDate.now(), 10L);

        Mockito.when(tallerService.obtenerPorId(id)).thenReturn(Optional.of(mockResponse));

        mockMvc.perform(get("/api/bibliotecaam/taller/{id}", id)
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTaller").value(id))
                .andExpect(jsonPath("$.nombreTaller").value("Taller de Historia"));
    }

    @Test
    @DisplayName("GIVEN: ID inexistente WHEN: GET /api/bibliotecaam/taller/{id} THEN: Retorna 404 Not Found")
    void shouldReturnNotFoundWhenTallerDoesNotExist() throws Exception {
        Long id = 99L;
        Mockito.when(tallerService.obtenerPorId(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/bibliotecaam/taller/{id}", id)
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GIVEN: Nombre válido WHEN: GET /api/bibliotecaam/taller/nombre/{nombre} THEN: Retorna 200 OK y la lista")
    void shouldReturnTalleresByNombre() throws Exception {
        String nombre = "Lectura";
        TallerResponseDTO mockResponse = new TallerResponseDTO(1L, "Taller de Lectura", LocalDate.now(), 10L);
        List<TallerResponseDTO> lista = Arrays.asList(mockResponse);

        Mockito.when(tallerService.obtenerPorNombre(nombre)).thenReturn(lista);

        mockMvc.perform(get("/api/bibliotecaam/taller/nombre/{nombre}", nombre)
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.tallerResponseDTOList[0].nombreTaller").value("Taller de Lectura"));
    }

    @Test
    @DisplayName("GIVEN: Request y Token válidos WHEN: POST /api/bibliotecaam/taller THEN: Retorna 201 Created")
    void shouldCreateTaller() throws Exception {
        TallerRequestDTO request = new TallerRequestDTO("Taller de Computación", LocalDate.of(2026, 6, 21), 12L);
        TallerResponseDTO mockResponse = new TallerResponseDTO(1L, "Taller de Computación", LocalDate.of(2026, 6, 21), 12L);
        String token = "Bearer token-de-prueba";

        Mockito.when(tallerService.guardar(any(TallerRequestDTO.class), eq(token))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/bibliotecaam/taller")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idTaller").value(1L))
                .andExpect(jsonPath("$.nombreTaller").value("Taller de Computación"))
                .andExpect(jsonPath("$.fechaTaller").value("2026-06-21"));
    }

    @Test
    @DisplayName("GIVEN: ID, Request y Token válidos WHEN: PUT /api/bibliotecaam/taller/{id} THEN: Retorna 200 OK")
    void shouldUpdateTaller() throws Exception {
        Long id = 1L;
        TallerRequestDTO request = new TallerRequestDTO("Taller Actualizado", LocalDate.now(), 15L);
        TallerResponseDTO mockResponse = new TallerResponseDTO(id, "Taller Actualizado", LocalDate.now(), 15L);
        String token = "Bearer token-de-prueba";

        Mockito.when(tallerService.actualizar(eq(id), any(TallerRequestDTO.class), eq(token))).thenReturn(Optional.of(mockResponse));

        mockMvc.perform(put("/api/bibliotecaam/taller/{id}", id)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreTaller").value("Taller Actualizado"))
                .andExpect(jsonPath("$.idEmpleado").value(15L));
    }

    @Test
    @DisplayName("GIVEN: ID válido WHEN: DELETE /api/bibliotecaam/taller/{id} THEN: Retorna 204 No Content")
    void shouldDeleteTaller() throws Exception {
        Long id = 1L;
        TallerResponseDTO mockResponse = new TallerResponseDTO(id, "Taller a Borrar", LocalDate.now(), 10L);

        Mockito.when(tallerService.obtenerPorId(id)).thenReturn(Optional.of(mockResponse));
        Mockito.doNothing().when(tallerService).eliminar(id);

        mockMvc.perform(delete("/api/bibliotecaam/taller/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GIVEN: ID inexistente WHEN: DELETE /api/bibliotecaam/taller/{id} THEN: Retorna 204 No Content y Body de error")
    void shouldReturnNoContentWithBodyWhenTallerDoesNotExistOnDelete() throws Exception {
        Long id = 99L;
        Mockito.when(tallerService.obtenerPorId(id)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/bibliotecaam/taller/{id}", id))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.['¡ERROR! ']").value("¡El Taller con id 99 no fue encontrado!"));
    }
}