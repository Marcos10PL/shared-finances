package pl.us.shared_finances.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.us.shared_finances.dto.request.CategoryRenameRequest;
import pl.us.shared_finances.dto.request.CategoryRequest;
import pl.us.shared_finances.dto.response.CategoryResponse;
import pl.us.shared_finances.entity.Category;
import pl.us.shared_finances.entity.User;
import pl.us.shared_finances.repository.CategoryRepository;
import pl.us.shared_finances.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // GET /api/categories
    @Transactional(readOnly = true)
    public List<CategoryResponse> getMyCategories(Long userId) {
        List<CategoryResponse> result = new ArrayList<>();

        for (Category category : categoryRepository.findByUserIsNullOrUserIdOrderByNameAsc(userId)) {
            result.add(toResponse(category));
        }
        return result;
    }

    // POST /api/categories
    @Transactional
    public CategoryResponse create(Long userId, CategoryRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "USER_NOT_FOUND"));

        String name = request.name().trim();

        // nie dublujemy ani swoich, ani domyślnych kategorii
        if (categoryRepository.existsByNameIgnoreCaseAndTypeAndUserId(name, request.type(), userId)
                || categoryRepository.existsByNameIgnoreCaseAndTypeAndUserIsNull(name, request.type())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CATEGORY_EXISTS");
        }

        Category category = new Category();
        category.setName(name);
        category.setType(request.type());
        category.setUser(user);
        categoryRepository.save(category);

        return toResponse(category);
    }

    // PUT /api/categories/{categoryId}
    @Transactional
    public CategoryResponse rename(Long categoryId, Long userId, CategoryRenameRequest request) {
        Category category = requireOwnCategory(categoryId, userId);

        String name = request.name().trim();

        if (categoryRepository.existsByNameIgnoreCaseAndTypeAndUserIdAndIdNot(name, category.getType(), userId, categoryId)
                || categoryRepository.existsByNameIgnoreCaseAndTypeAndUserIsNull(name, category.getType())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CATEGORY_EXISTS");
        }

        category.setName(name);
        categoryRepository.save(category);

        return toResponse(category);
    }

    // DELETE /api/categories/{categoryId}
    @Transactional
    public void delete(Long categoryId, Long userId) {
        Category category = requireOwnCategory(categoryId, userId);

        // TODO: po dodaniu transakcji odrzucać usunięcie używanej kategorii albo nwm co jeszcze:
        // if (transactionRepository.existsByCategoryId(categoryId)) {
        //     throw new ResponseStatusException(HttpStatus.CONFLICT, "CATEGORY_IN_USE");
        // }

        categoryRepository.delete(category);
    }

    // cudza kategoria wygląda jak nieistniejąca, domyślnej nie można zmieniać
    private Category requireOwnCategory(Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND"));

        if (category.getUser() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "DEFAULT_CATEGORY_READ_ONLY");
        }
        if (!category.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND");
        }
        return category;
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getUser() == null ? null : category.getUser().getId()
        );
    }
}
