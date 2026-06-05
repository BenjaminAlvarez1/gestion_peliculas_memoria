package cl.usm.gestionPeliculasMemoria.services;

import cl.usm.gestionPeliculasMemoria.entities.Pelicula;
import cl.usm.gestionPeliculasMemoria.repositories.PeliculasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PeliculasServiceImplTest {

    @Mock
    private PeliculasRepository peliculasRepository;

    @InjectMocks
    private PeliculasServiceImpl peliculasService;

    private Pelicula peliculaOrigen;
    private Pelicula peliculaSecundaria;

    @BeforeEach
    void setUp() {
        peliculaOrigen = new Pelicula("1", "Inception", "Christopher Nolan", null, null);
        peliculaSecundaria = new Pelicula("2", "Interstellar", "Christopher Nolan", null, null);
    }

    @Test
    void testCreatePelicula_Success() {
        when(peliculasRepository.insert(any(Pelicula.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pelicula peliculaGuardada = peliculasService.createPelicula(peliculaOrigen);

        assertNotNull(peliculaGuardada, "La pelicula creada no deberia ser nula");
        assertNotNull(peliculaGuardada.getTokenDescarga(), "El token de descarga no deberia ser nulo");
        assertEquals(10, peliculaGuardada.getTokenDescarga().length(), "El token de descarga debe tener 10 caracteres");
        verify(peliculasRepository, times(1)).insert(peliculaOrigen);
    }

    @Test
    void testCreatePelicula_ExceptionHandling() {
        when(peliculasRepository.insert(any(Pelicula.class))).thenThrow(new RuntimeException("Error en la base de datos"));

        Pelicula intentoGuardado = peliculasService.createPelicula(peliculaOrigen);

        assertNull(intentoGuardado, "Si ocurre una excepcion al crear, el servicio deberia retornar null");
        verify(peliculasRepository, times(1)).insert(peliculaOrigen);
    }

    @Test
    void testGetAll_Success() {
        List<Pelicula> listaPeliculasEsperadas = Arrays.asList(peliculaOrigen, peliculaSecundaria);
        when(peliculasRepository.findAll()).thenReturn(listaPeliculasEsperadas);

        List<Pelicula> listaObtenida = peliculasService.getAll();

        assertNotNull(listaObtenida);
        assertEquals(2, listaObtenida.size(), "Deberia retornar la lista completa de peliculas");
        verify(peliculasRepository, times(1)).findAll();
    }

    @Test
    void testGetAll_EmptyList() {
        when(peliculasRepository.findAll()).thenReturn(Collections.emptyList());

        List<Pelicula> listaObtenida = peliculasService.getAll();

        assertNotNull(listaObtenida);
        assertTrue(listaObtenida.isEmpty(), "Si no hay peliculas, deberia retornar una lista vacia");
        verify(peliculasRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Success() {
        when(peliculasRepository.findById("1")).thenReturn(peliculaOrigen);

        Pelicula peliculaEncontrada = peliculasService.findById("1");

        assertNotNull(peliculaEncontrada, "Deberia encontrar la pelicula con el ID especificado");
        assertEquals("1", peliculaEncontrada.getId());
        assertEquals("Inception", peliculaEncontrada.getTitulo());
        verify(peliculasRepository, times(1)).findById("1");
    }

    @Test
    void testFindById_NotFound() {
        when(peliculasRepository.findById("99")).thenReturn(null);

        Pelicula peliculaEncontrada = peliculasService.findById("99");

        assertNull(peliculaEncontrada, "Si no encuentra el ID, deberia retornar null");
        verify(peliculasRepository, times(1)).findById("99");
    }

    @Test
    void testFilter_MatchById() {
        when(peliculasRepository.findAll()).thenReturn(Arrays.asList(peliculaOrigen, peliculaSecundaria));

        List<Pelicula> peliculasFiltradas = peliculasService.filter("1");

        assertEquals(1, peliculasFiltradas.size(), "Deberia filtrar correctamente por ID");
        assertEquals("1", peliculasFiltradas.get(0).getId());
    }

    @Test
    void testFilter_MatchByTituloCaseInsensitive() {
        when(peliculasRepository.findAll()).thenReturn(Arrays.asList(peliculaOrigen, peliculaSecundaria));

        List<Pelicula> peliculasFiltradas = peliculasService.filter("inCEP"); 

        assertEquals(1, peliculasFiltradas.size(), "Deberia filtrar correctamente ignorando mayusculas y minusculas");
        assertEquals("Inception", peliculasFiltradas.get(0).getTitulo());
    }

    @Test
    void testFilter_NoMatch() {
        when(peliculasRepository.findAll()).thenReturn(Arrays.asList(peliculaOrigen, peliculaSecundaria));

        List<Pelicula> peliculasFiltradas = peliculasService.filter("Avatar");

        assertNotNull(peliculasFiltradas);
        assertTrue(peliculasFiltradas.isEmpty(), "Si no coincide nada, la lista deberia estar vacia");
    }
}
