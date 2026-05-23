package recipe_service.service;


import lombok.RequiredArgsConstructor;
import recipe_service.domain.Recipe;
import recipe_service.repository.RecipeRepository;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public List<Recipe> getAll() {
        return recipeRepository.findAll();
    }

    public Optional<Recipe> getById(String id) {
        return recipeRepository.findById(id);
    }

    public List<Recipe> search(String query) {
        return recipeRepository.findByNombreContainingIgnoreCase(query);
    }

    public List<Recipe> getByCategory(String categoria) {
        return recipeRepository.findByCategoria(categoria);
    }

    public List<Recipe> getByTag(String tag) {
        return recipeRepository.findByTagsContaining(tag);
    }

    public Recipe create(Recipe recipe) {
        recipe.setCreatedAt(LocalDateTime.now());
        recipe.setUpdatedAt(LocalDateTime.now());
        return recipeRepository.save(recipe);
    }

    public Optional<Recipe> update(String id, Recipe updated) {
        return recipeRepository.findById(id).map(existing -> {
            existing.setNombre(updated.getNombre());
            existing.setCategoria(updated.getCategoria());
            existing.setDificultad(updated.getDificultad());
            existing.setTiempoMinutos(updated.getTiempoMinutos());
            existing.setPorciones(updated.getPorciones());
            existing.setIngredientes(updated.getIngredientes());
            existing.setPasos(updated.getPasos());
            existing.setTags(updated.getTags());
            existing.setUpdatedAt(LocalDateTime.now());
            return recipeRepository.save(existing);
        });
    }

    public boolean delete(String id) {
        if (recipeRepository.existsById(id)) {
            recipeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}