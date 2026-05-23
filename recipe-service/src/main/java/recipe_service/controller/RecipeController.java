package recipe_service.controller;


import lombok.RequiredArgsConstructor;

import recipe_service.domain.Recipe;
import recipe_service.service.RecipeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    // GET /api/recipes
    @GetMapping
    public ResponseEntity<List<Recipe>> getAll() {
        return ResponseEntity.ok(recipeService.getAll());
    }

    // GET /api/recipes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getById(@PathVariable String id) {
        return recipeService.getById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/recipes/search?q=pie
    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> search(
            @RequestParam String q) {
        return ResponseEntity.ok(recipeService.search(q));
    }

    // GET /api/recipes/category/postres
    @GetMapping("/category/{categoria}")
    public ResponseEntity<List<Recipe>> getByCategory(
            @PathVariable String categoria) {
        return ResponseEntity.ok(recipeService.getByCategory(categoria));
    }

    // GET /api/recipes/tag/sin_horno
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<Recipe>> getByTag(
            @PathVariable String tag) {
        return ResponseEntity.ok(recipeService.getByTag(tag));
    }

    // POST /api/recipes
    @PostMapping
    public ResponseEntity<Recipe> create(
            @RequestBody Recipe recipe) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(recipeService.create(recipe));
    }

    // PUT /api/recipes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> update(
            @PathVariable String id,
            @RequestBody Recipe recipe) {
        return recipeService.update(id, recipe)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/recipes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        return recipeService.delete(id)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.notFound().build();
    }
}