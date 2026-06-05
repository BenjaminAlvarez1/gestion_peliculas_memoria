package cl.usm.gestionPeliculasMemoria.controllers;

import cl.usm.gestionPeliculasMemoria.entities.Comentario;
import cl.usm.gestionPeliculasMemoria.entities.Pelicula;
import cl.usm.gestionPeliculasMemoria.services.PeliculasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PeliculasControllerTest {

    @Mock
    private PeliculasService peliculasService;

    @InjectMocks
    private PeliculasController peliculasController;

    private Pelicula peliculaPrueba;

    @BeforeEach
    void setUp() {
        Comentario[] comentariosPrueba = {new Comentario("usuario1", "Buenisima")};
        peliculaPrueba = new Pelicula("1", "Inception", "Nolan", "token123", comentariosPrueba);
    }

    @Test
    void testGetAll_Success() {
        when(peliculasService.getAll()).thenReturn(Arrays.asList(peliculaPrueba));

        ResponseEntity<List<Pelicula>> respuestaEndpoint = peliculasController.getAll(null);

        assertEquals(HttpStatus.OK, respuestaEndpoint.getStatusCode());
        assertNotNull(respuestaEndpoint.getBody());
        assertEquals(1, respuestaEndpoint.getBody().size());
        assertEquals("1", respuestaEndpoint.getBody().get(0).getId());
        
        verify(peliculasService, times(1)).getAll();
    }

    @Test
    void testGetAll_WithQuery_Success() {
        when(peliculasService.filter("Incep")).thenReturn(Arrays.asList(peliculaPrueba));

        ResponseEntity<List<Pelicula>> respuestaEndpoint = peliculasController.getAll("Incep");

        assertEquals(HttpStatus.OK, respuestaEndpoint.getStatusCode());
        assertNotNull(respuestaEndpoint.getBody());
        assertEquals(1, respuestaEndpoint.getBody().size());
        assertEquals("Inception", respuestaEndpoint.getBody().get(0).getTitulo());
        
        verify(peliculasService, times(1)).filter("Incep");
    }

    @Test
    void testGetAll_Exception_InternalServerError() {
        when(peliculasService.getAll()).thenThrow(new RuntimeException("Error inesperado"));

        ResponseEntity<List<Pelicula>> respuestaEndpoint = peliculasController.getAll(null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuestaEndpoint.getStatusCode());
        
        verify(peliculasService, times(1)).getAll();
    }

    @Test
    void testCreatePelicula_Success() {
        when(peliculasService.createPelicula(any(Pelicula.class))).thenReturn(peliculaPrueba);

        ResponseEntity<?> respuestaEndpoint = peliculasController.createPelicula(peliculaPrueba);

        assertEquals(HttpStatus.OK, respuestaEndpoint.getStatusCode());
        assertNotNull(respuestaEndpoint.getBody());
        assertTrue(respuestaEndpoint.getBody() instanceof Pelicula);
        
        Pelicula peliculaDeserealizada = (Pelicula) respuestaEndpoint.getBody();
        assertEquals("1", peliculaDeserealizada.getId());
        assertEquals("Inception", peliculaDeserealizada.getTitulo());
        assertEquals("Nolan", peliculaDeserealizada.getDirector());
        assertEquals("token123", peliculaDeserealizada.getTokenDescarga());
        
        verify(peliculasService, times(1)).createPelicula(peliculaPrueba);
    }

    @Test
    void testCreatePelicula_Failure() {
        when(peliculasService.createPelicula(any(Pelicula.class))).thenReturn(null);

        ResponseEntity<?> respuestaEndpoint = peliculasController.createPelicula(peliculaPrueba);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuestaEndpoint.getStatusCode());
        
        verify(peliculasService, times(1)).createPelicula(peliculaPrueba);
    }

    @Test
    void testFindById_Success() {
        when(peliculasService.findById("1")).thenReturn(peliculaPrueba);

        ResponseEntity<Pelicula> respuestaEndpoint = peliculasController.findById("1");

        assertEquals(HttpStatus.OK, respuestaEndpoint.getStatusCode());
        assertNotNull(respuestaEndpoint.getBody());
        assertEquals("1", respuestaEndpoint.getBody().getId());
        
        verify(peliculasService, times(1)).findById("1");
    }

    @Test
    void testFindById_NotFound() {
        when(peliculasService.findById("2")).thenReturn(null);

        ResponseEntity<Pelicula> respuestaEndpoint = peliculasController.findById("2");

        assertEquals(HttpStatus.NOT_FOUND, respuestaEndpoint.getStatusCode());
        
        verify(peliculasService, times(1)).findById("2");
    }

    @Test
    void testFindById_Exception_InternalServerError() {
        when(peliculasService.findById("1")).thenThrow(new RuntimeException("Error inesperado"));

        ResponseEntity<Pelicula> respuestaEndpoint = peliculasController.findById("1");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuestaEndpoint.getStatusCode());
        
        verify(peliculasService, times(1)).findById("1");
    }

    @Test
    void testGetComentarios_Success() {
        when(peliculasService.findById("1")).thenReturn(peliculaPrueba);

        ResponseEntity<?> respuestaEndpoint = peliculasController.getComentarios("1");

        assertEquals(HttpStatus.OK, respuestaEndpoint.getStatusCode());
        assertNotNull(respuestaEndpoint.getBody());
        assertTrue(respuestaEndpoint.getBody() instanceof Comentario[]);
        Comentario[] arregloComentarios = (Comentario[]) respuestaEndpoint.getBody();
        assertEquals(1, arregloComentarios.length);
        assertEquals("usuario1", arregloComentarios[0].getUsuario());
        assertEquals("Buenisima", arregloComentarios[0].getComentario());
        
        verify(peliculasService, times(1)).findById("1");
    }

    @Test
    void testGetComentarios_NotFound() {
        when(peliculasService.findById("2")).thenReturn(null);

        ResponseEntity<?> respuestaEndpoint = peliculasController.getComentarios("2");

        assertEquals(HttpStatus.NOT_FOUND, respuestaEndpoint.getStatusCode());
        
        verify(peliculasService, times(1)).findById("2");
    }

    @Test
    void testGetComentarios_Exception_InternalServerError() {
        when(peliculasService.findById("1")).thenThrow(new RuntimeException("Error inesperado"));

        ResponseEntity<?> respuestaEndpoint = peliculasController.getComentarios("1");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuestaEndpoint.getStatusCode());
        
        verify(peliculasService, times(1)).findById("1");
    }
}
