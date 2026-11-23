package pl.us.shared_finances.service;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pl.us.shared_finances.dto.request.CategoryRenameRequest;
import pl.us.shared_finances.dto.request.CategoryRequest;
import pl.us.shared_finances.dto.response.CategoryResponse;
import pl.us.shared_finances.entity.Category;
import pl.us.shared_finances.entity.User;
import pl.us.shared_finances.entity.enums.CategoryType;
import pl.us.shared_finances.repository.CategoryRepository;
import pl.us.shared_finances.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    private static final Long USER_ID = 10L;
    private static final Long OTHER_USER_ID = 20L;
    private static final Long CATEGORY_ID = 100L;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    // lista

    @Test
    void listaZawieraDomyslneIWlasne() {
        Category domyslna = category(1L, "Jedzenie", CategoryType.EXPENSE, null);
        Category wlasna = category(2L, "Kot", CategoryType.EXPENSE, user(USER_ID));
        when(categoryRepository.findByUserIsNullOrUserIdOrderByNameAsc(USER_ID)).thenReturn(List.of(domyslna, wlasna));

        List<CategoryResponse> result = categoryService.getMyCategories(USER_ID);

        assertThat(result).extracting(CategoryResponse::name).containsExactly("Jedzenie", "Kot");
        assertThat(result.get(0).userId()).isNull();
        assertThat(result.get(1).userId()).isEqualTo(USER_ID);
    }

    // tworzenie

    @Test
    void tworzenieZapisujePrzycietaNazweIAutora() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user(USER_ID)));

        CategoryResponse result = categoryService.create(USER_ID, new CategoryRequest("  Kot  ", CategoryType.EXPENSE));

        ArgumentCaptor<Category> saved = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(saved.capture());
        assertThat(saved.getValue().getName()).isEqualTo("Kot");
        assertThat(saved.getValue().getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(saved.getValue().getUser().getId()).isEqualTo(USER_ID);

        assertThat(result.name()).isEqualTo("Kot");
        assertThat(result.userId()).isEqualTo(USER_ID);
    }

    @Test
    void tworzenieDuplikatuWlasnejKategorii_CATEGORY_EXISTS() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user(USER_ID)));
        when(categoryRepository.existsByNameIgnoreCaseAndTypeAndUserId("Kot", CategoryType.EXPENSE, USER_ID))
                .thenReturn(true);

        assertError(() -> categoryService.create(USER_ID, new CategoryRequest("Kot", CategoryType.EXPENSE)),
                HttpStatus.CONFLICT, "CATEGORY_EXISTS");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void tworzenieDuplikatuDomyslnejKategorii_CATEGORY_EXISTS() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user(USER_ID)));
        when(categoryRepository.existsByNameIgnoreCaseAndTypeAndUserIsNull("Jedzenie", CategoryType.EXPENSE))
                .thenReturn(true);

        assertError(() -> categoryService.create(USER_ID, new CategoryRequest("Jedzenie", CategoryType.EXPENSE)),
                HttpStatus.CONFLICT, "CATEGORY_EXISTS");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void tworzenieBezIstniejacegoUsera_USER_NOT_FOUND() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertError(() -> categoryService.create(USER_ID, new CategoryRequest("Kot", CategoryType.EXPENSE)),
                HttpStatus.UNAUTHORIZED, "USER_NOT_FOUND");
        verifyNoInteractions(categoryRepository);
    }

    // zmiana nazwy

    @Test
    void zmianaNazwyWlasnejKategorii() {
        Category kot = category(CATEGORY_ID, "Kot", CategoryType.EXPENSE, user(USER_ID));
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(kot));

        CategoryResponse result = categoryService.rename(CATEGORY_ID, USER_ID, new CategoryRenameRequest(" Zwierzęta "));

        assertThat(result.name()).isEqualTo("Zwierzęta");
        assertThat(result.type()).isEqualTo(CategoryType.EXPENSE);
        assertThat(kot.getName()).isEqualTo("Zwierzęta");
        verify(categoryRepository).save(kot);
    }

    @Test
    void zmianaNazwyNaZajeta_CATEGORY_EXISTS() {
        Category kot = category(CATEGORY_ID, "Kot", CategoryType.EXPENSE, user(USER_ID));
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(kot));
        when(categoryRepository.existsByNameIgnoreCaseAndTypeAndUserIdAndIdNot("Pies", CategoryType.EXPENSE, USER_ID, CATEGORY_ID))
                .thenReturn(true);

        assertError(() -> categoryService.rename(CATEGORY_ID, USER_ID, new CategoryRenameRequest("Pies")),
                HttpStatus.CONFLICT, "CATEGORY_EXISTS");
        assertThat(kot.getName()).isEqualTo("Kot");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void zmianaNazwyDomyslnej_DEFAULT_CATEGORY_READ_ONLY() {
        Category domyslna = category(CATEGORY_ID, "Jedzenie", CategoryType.EXPENSE, null);
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(domyslna));

        assertError(() -> categoryService.rename(CATEGORY_ID, USER_ID, new CategoryRenameRequest("Żarcie")),
                HttpStatus.FORBIDDEN, "DEFAULT_CATEGORY_READ_ONLY");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void zmianaNazwyCudzej_CATEGORY_NOT_FOUND() {
        Category cudza = category(CATEGORY_ID, "Kot", CategoryType.EXPENSE, user(OTHER_USER_ID));
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(cudza));

        assertError(() -> categoryService.rename(CATEGORY_ID, USER_ID, new CategoryRenameRequest("Mój kot")),
                HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void zmianaNazwyNieistniejacej_CATEGORY_NOT_FOUND() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        assertError(() -> categoryService.rename(CATEGORY_ID, USER_ID, new CategoryRenameRequest("Kot")),
                HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND");
    }

    // usuwanie

    @Test
    void usuniecieWlasnejKategorii() {
        Category kot = category(CATEGORY_ID, "Kot", CategoryType.EXPENSE, user(USER_ID));
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(kot));

        categoryService.delete(CATEGORY_ID, USER_ID);

        verify(categoryRepository).delete(kot);
    }

    @Test
    void usuniecieDomyslnej_DEFAULT_CATEGORY_READ_ONLY() {
        Category domyslna = category(CATEGORY_ID, "Jedzenie", CategoryType.EXPENSE, null);
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(domyslna));

        assertError(() -> categoryService.delete(CATEGORY_ID, USER_ID),
                HttpStatus.FORBIDDEN, "DEFAULT_CATEGORY_READ_ONLY");
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void usuniecieCudzej_CATEGORY_NOT_FOUND() {
        Category cudza = category(CATEGORY_ID, "Kot", CategoryType.EXPENSE, user(OTHER_USER_ID));
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(cudza));

        assertError(() -> categoryService.delete(CATEGORY_ID, USER_ID),
                HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND");
        verify(categoryRepository, never()).delete(any());
    }

    // pomocnicze

    private static User user(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private static Category category(Long id, String name, CategoryType type, User owner) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setType(type);
        category.setUser(owner);
        return category;
    }

    private static void assertError(ThrowingCallable call, HttpStatus status, String code) {
        assertThatThrownBy(call)
                .isInstanceOfSatisfying(ResponseStatusException.class, e -> {
                    assertThat(e.getStatusCode()).isEqualTo(status);
                    assertThat(e.getReason()).isEqualTo(code);
                });
    }
}
