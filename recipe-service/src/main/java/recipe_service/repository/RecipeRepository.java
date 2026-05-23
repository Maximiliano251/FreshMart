package recipe_service.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import recipe_service.domain.Recipe;

import java.util.List;

public interface RecipeRepository
        extends MongoRepository<Recipe, String> {

    // busca por nombre ignorando mayúsculas
    List<Recipe> findByNombreContainingIgnoreCase(String nombre);

    // busca por categoría
    List<Recipe> findByCategoria(String categoria);

    // busca por tag
    List<Recipe> findByTagsContaining(String tag);

    // busca por dificultad
    List<Recipe> findByDificultad(String dificultad);

    // búsqueda combinada nombre + categoría
    @Query("{ 'nombre': { $regex: ?0, $options: 'i' }, 'categoria': ?1 }")
    List<Recipe> findByNombreAndCategoria(String nombre, String categoria);
}