package dsy1103.bibliotecaam.taller;

import dsy1103.bibliotecaam.taller.dto.TallerRequestDTO;
import dsy1103.bibliotecaam.taller.dto.TallerResponseDTO;
import dsy1103.bibliotecaam.taller.model.Taller;
import dsy1103.bibliotecaam.taller.repository.TallerRepository;
import dsy1103.bibliotecaam.taller.service.TallerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(classes = TallerService.class)
@ActiveProfiles("test")
@DisplayName("Tests Unitarios - TallerService")
class TallerServiceTest {

    @Autowired
    private TallerService tallerService;

    @MockitoBean
    private TallerRepository tallerRepository;

    @MockitoBean
    private WebClient webClient;

    // Mocks para la interfaz fluida de WebClient
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;
    private WebClient.ResponseSpec responseSpecMock;

    private final String mockToken = "Bearer token-valido";

    @BeforeEach
    void setUp() {
        requestHeadersUriSpecMock = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        requestHeadersSpecMock = Mockito.mock(WebClient.RequestHeadersSpec.class);
        responseSpecMock = Mockito.mock(WebClient.ResponseSpec.class);
    }

    @SuppressWarnings("unchecked")
    private void mockWebClientSuccess(WebClient webClientMock, String uri, Object id, String token) {
        Mockito.when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        Mockito.when(requestHeadersUriSpecMock.uri(eq(uri), eq(id))).thenReturn(requestHeadersSpecMock);
        Mockito.when(requestHeadersSpecMock.header("Authorization", token)).thenReturn(requestHeadersSpecMock);
        Mockito.when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        Mockito.when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.just("OK"));
    }

    @SuppressWarnings("unchecked")
    private void mockWebClientException(WebClient webClientMock, String uri, Object id, String token, Throwable exception) {
        Mockito.when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        Mockito.when(requestHeadersUriSpecMock.uri(eq(uri), eq(id))).thenReturn(requestHeadersSpecMock);
        Mockito.when(requestHeadersSpecMock.header("Authorization", token)).thenReturn(requestHeadersSpecMock);
        Mockito.when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        Mockito.when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.error(exception));
    }

    @Test
    @DisplayName("GIVEN: Existen talleres WHEN: obtenerTodas THEN: Retorna la lista completa de DTOs")
    void shouldReturnAllTalleres() {
        List<Taller> mockList = Arrays.asList(
                new Taller(1L, "Taller Java", LocalDate.now(), 10L),
                new Taller(2L, "Taller Spring", LocalDate.now(), 11L)
        );
        Mockito.when(tallerRepository.findAll()).thenReturn(mockList);

        List<TallerResponseDTO> resultado = tallerService.obtenerTodas();

        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getIdTaller());
        assertEquals("Taller Java", resultado.get(0).getNombreTaller());
    }

    @Test
    @DisplayName("GIVEN: Existe taller WHEN: obtenerPorId THEN: Retorna el DTO correspondiente")
    void shouldReturnTallerById() {
        Long id = 1L;
        Taller taller = new Taller(id, "Taller Git", LocalDate.now(), 10L);
        Mockito.when(tallerRepository.findById(id)).thenReturn(Optional.of(taller));

        Optional<TallerResponseDTO> resultado = tallerService.obtenerPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getIdTaller());
        assertEquals("Taller Git", resultado.get().getNombreTaller());
    }

    @Test
    @DisplayName("GIVEN: Request y Token válidos WHEN: guardar THEN: Valida remotamente al empleado y guarda el taller")
    void shouldSaveTallerSuccessfully() {
        TallerRequestDTO request = new TallerRequestDTO("Taller SQL", LocalDate.now(), 10L);
        Taller tallerGuardado = new Taller(100L, "Taller SQL", LocalDate.now(), 10L);

        mockWebClientSuccess(webClient, "/api/bibliotecaam/empleado/{id}", 10L, mockToken);
        Mockito.when(tallerRepository.save(any(Taller.class))).thenReturn(tallerGuardado);

        TallerResponseDTO resultado = tallerService.guardar(request, mockToken);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getIdTaller());
        assertEquals("Taller SQL", resultado.getNombreTaller());
    }

    @Test
    @DisplayName("GIVEN: Empleado inexistente WHEN: guardar THEN: Lanza RuntimeException")
    void shouldThrowExceptionWhenEmpleadoNotFound() {
        TallerRequestDTO request = new TallerRequestDTO("Taller Fallido", LocalDate.now(), 99L);

        WebClientResponseException notFoundException = Mockito.mock(WebClientResponseException.NotFound.class);
        mockWebClientException(webClient, "/api/bibliotecaam/empleado/{id}", 99L, mockToken, notFoundException);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> tallerService.guardar(request, mockToken));
        assertTrue(exception.getMessage().contains("El empleado con el id 99 no existe"));
        Mockito.verify(tallerRepository, Mockito.never()).save(any(Taller.class));
    }

    @Test
    @DisplayName("GIVEN: ID, Request y Token válidos WHEN: actualizar THEN: Modifica el taller existente")
    void shouldUpdateTallerSuccessfully() {
        Long id = 1L;
        Taller existente = new Taller(id, "Taller Viejo", LocalDate.now(), 10L);
        TallerRequestDTO request = new TallerRequestDTO("Taller Nuevo", LocalDate.now(), 10L);
        Taller modificado = new Taller(id, "Taller Nuevo", LocalDate.now(), 10L);

        Mockito.when(tallerRepository.findById(id)).thenReturn(Optional.of(existente));
        mockWebClientSuccess(webClient, "/api/bibliotecaam/empleado/{id}", 10L, mockToken);
        Mockito.when(tallerRepository.save(any(Taller.class))).thenReturn(modificado);

        Optional<TallerResponseDTO> resultado = tallerService.actualizar(id, request, mockToken);

        assertTrue(resultado.isPresent());
        assertEquals("Taller Nuevo", resultado.get().getNombreTaller());
    }

    @Test
    @DisplayName("GIVEN: ID válido WHEN: eliminar THEN: Borra el registro en el repositorio")
    void shouldDeleteTaller() {
        Long id = 1L;
        Mockito.doNothing().when(tallerRepository).deleteById(id);

        assertDoesNotThrow(() -> tallerService.eliminar(id));
        Mockito.verify(tallerRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    @DisplayName("GIVEN: Nombre existente WHEN: obtenerPorNombre THEN: Retorna los talleres filtrados")
    void shouldReturnTalleresByNombre() {
        String nombre = "Docker";
        List<Taller> mockList = Arrays.asList(new Taller(1L, "Taller Docker", LocalDate.now(), 12L));
        Mockito.when(tallerRepository.findByNombreTaller(nombre)).thenReturn(mockList);

        List<TallerResponseDTO> resultado = tallerService.obtenerPorNombre(nombre);

        assertEquals(1, resultado.size());
        assertEquals("Taller Docker", resultado.get(0).getNombreTaller());
    }
}