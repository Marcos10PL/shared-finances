package pl.us.shared_finances.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.us.shared_finances.dto.request.CategoryRenameRequest;
import pl.us.shared_finances.dto.request.CategoryRequest;
import pl.us.shared_finances.dto.response.CategoryResponse;
import pl.us.shared_finances.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> getMyCategories(@AuthenticationPrincipal Long userId) {
        return categoryService.getMyCategories(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(@AuthenticationPrincipal Long userId,
                                           @Valid @RequestBody CategoryRequest request) {
        return categoryService.create(userId, request);
    }

    @PutMapping("/{categoryId}")
    public CategoryResponse renameCategory(@PathVariable Long categoryId,
                                           @AuthenticationPrincipal Long userId,
                                           @Valid @RequestBody CategoryRenameRequest request) {
        return categoryService.rename(categoryId, userId, request);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long categoryId,
                               @AuthenticationPrincipal Long userId) {
        categoryService.delete(categoryId, userId);
    }
}
