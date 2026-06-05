package cl.usm.gestionPeliculasMemoria.repositories;

import cl.usm.gestionPeliculasMemoria.entities.Pelicula;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PeliculasRepositoryImplTest {

    private PeliculasRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new PeliculasRepositoryImpl();
    }

    @Test
    void testInsert_Success() {
        Pelicula nuevaPelicula = new Pelicula("1", "Inception", "Nolan", null, null);

        Pelicula peliculaRegistrada = repository.insert(nuevaPelicula);

        assertNotNull(peliculaRegistrada);
        assertEquals("1", peliculaRegistrada.getId());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void testInsert_NullId_ThrowsException() {
        Pelicula peliculaInvalida = new Pelicula(null, "Inception", "Nolan", null, null);

        IllegalArgumentException excepcionObtenida = assertThrows(IllegalArgumentException.class, () -> {
            repository.insert(peliculaInvalida);
        });
        assertEquals("El ID de la pelicula no puede ser nulo", excepcionObtenida.getMessage());
    }

    @Test
    void testInsert_DuplicateId_ThrowsException() {
        Pelicula primeraPelicula = new Pelicula("1", "Inception", "Nolan", null, null);
        Pelicula peliculaRepetida = new Pelicula("1", "Interstellar", "Nolan", null, null);
        repository.insert(primeraPelicula);

        IllegalArgumentException excepcionObtenida = assertThrows(IllegalArgumentException.class, () -> {
            repository.insert(peliculaRepetida); 
        });
        assertEquals("La pelicula con ID 1 ya existe", excepcionObtenida.getMessage());
    }

    @Test
    void testFindAll_Success() {
        repository.insert(new Pelicula("1", "Inception", "Nolan", null, null));
        repository.insert(new Pelicula("2", "Interstellar", "Nolan", null, null));

        List<Pelicula> peliculasTotales = repository.findAll();

        assertEquals(2, peliculasTotales.size());
    }

    @Test
    void testFindAll_Empty() {
        List<Pelicula> peliculasTotales = repository.findAll();

        assertTrue(peliculasTotales.isEmpty());
    }

    @Test
    void testFindById_Success() {
        Pelicula peliculaBuscada = new Pelicula("1", "Inception", "Nolan", null, null);
        repository.insert(peliculaBuscada);

        Pelicula peliculaEncontrada = repository.findById("1");

        assertNotNull(peliculaEncontrada);
        assertEquals("1", peliculaEncontrada.getId());
        assertEquals("Inception", peliculaEncontrada.getTitulo());
    }

    @Test
    void testFindById_NotFound() {
        repository.insert(new Pelicula("1", "Inception", "Nolan", null, null));

        Pelicula peliculaEncontrada = repository.findById("2"); 

        assertNull(peliculaEncontrada);
    }

    @Test
    void testFindById_NullId() {
        repository.insert(new Pelicula("1", "Inception", "Nolan", null, null));

        Pelicula peliculaEncontrada = repository.findById(null);

        assertNull(peliculaEncontrada);
    }
}
