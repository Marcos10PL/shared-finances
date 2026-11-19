package pl.us.shared_finances.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.us.shared_finances.entity.Category;
import pl.us.shared_finances.entity.enums.CategoryType;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // kategorie domyślne + własne kategorie usera
    List<Category> findByUserIsNullOrUserIdOrderByNameAsc(Long userId);

    // czy user ma już własną kategorię o tej nazwie i typie
    boolean existsByNameIgnoreCaseAndTypeAndUserId(String name, CategoryType type, Long userId);

    // to samo przy zmianie nazwy, z pominięciem edytowanej kategorii
    boolean existsByNameIgnoreCaseAndTypeAndUserIdAndIdNot(String name, CategoryType type, Long userId, Long id);

    // czy istnieje kategoria domyślna o tej nazwie i typie
    boolean existsByNameIgnoreCaseAndTypeAndUserIsNull(String name, CategoryType type);
}
