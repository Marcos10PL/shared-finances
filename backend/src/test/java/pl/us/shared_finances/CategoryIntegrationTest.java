package pl.us.shared_finances;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pl.us.shared_finances.ApiHelper.TestUser;
import pl.us.shared_finances.entity.Category;
import pl.us.shared_finances.entity.enums.CategoryType;
import pl.us.shared_finances.repository.CategoryRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    private ApiHelper api;

    private TestUser anna;
    private TestUser bartek;

    @BeforeEach
    void setUp() throws Exception {
        api = new ApiHelper(mockMvc);
        anna = api.register("Anna");
        bartek = api.register("Bartek");
    }

    // tworzenie

    @Test
    void nowaKategoria_201_iWidocznaNaLiscie() throws Exception {
        create(anna, "  Kot  ", "EXPENSE")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Kot"))
                .andExpect(jsonPath("$.type").value("EXPENSE"))
                .andExpect(jsonPath("$.userId").value(anna.id()));

        list(anna).andExpect(jsonPath("$[*].name", hasItem("Kot")));
    }

    @Test
    void cudzychKategoriiNieWidac() throws Exception {
        createAndGetId(anna, "Kot Anny", "EXPENSE");

        list(bartek).andExpect(jsonPath("$[*].name", not(hasItem("Kot Anny"))));
    }

    @Test
    void domyslneKategorieWidziKazdy() throws Exception {
        String name = uniqueName("Jedzenie");
        saveDefault(name, CategoryType.EXPENSE);

        list(anna).andExpect(jsonPath("$[?(@.name == '%s')].userId".formatted(name), hasItem((Object) null)));
        list(bartek).andExpect(jsonPath("$[*].name", hasItem(name)));
    }

    @Test
    void duplikatNazwy_409_bezWzgleduNaWielkoscLiter() throws Exception {
        createAndGetId(anna, "Kot", "EXPENSE");

        create(anna, "KOT", "EXPENSE")
                .andExpect(status().isConflict())
                .andExpect(status().reason("CATEGORY_EXISTS"));
    }

    @Test
    void duplikatDomyslnej_409() throws Exception {
        String name = uniqueName("Paliwo");
        saveDefault(name, CategoryType.EXPENSE);

        create(anna, name, "EXPENSE")
                .andExpect(status().isConflict())
                .andExpect(status().reason("CATEGORY_EXISTS"));
    }

    @Test
    void taSamaNazwaInnyTyp_201() throws Exception {
        createAndGetId(anna, "Prezenty", "EXPENSE");

        create(anna, "Prezenty", "INCOME").andExpect(status().isCreated());
    }

    @Test
    void taSamaNazwaURoznychUserow_201() throws Exception {
        createAndGetId(anna, "Kot", "EXPENSE");

        create(bartek, "Kot", "EXPENSE").andExpect(status().isCreated());
    }

    @Test
    void niepoprawneDane_400() throws Exception {
        create(anna, "", "EXPENSE").andExpect(status().isBadRequest());
        create(anna, "x".repeat(51), "EXPENSE").andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", anna.bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"Kot\" }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bezTokena_401() throws Exception {
        mockMvc.perform(get("/api/categories")).andExpect(status().isUnauthorized());
    }

    // zmiana nazwy

    @Test
    void zmianaNazwy_200_typBezZmian() throws Exception {
        Long id = createAndGetId(anna, "Kot", "EXPENSE");

        rename(anna, id, "Zwierzęta")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Zwierzęta"))
                .andExpect(jsonPath("$.type").value("EXPENSE"));

        assertThat(categoryRepository.findById(id)).get().extracting(Category::getName).isEqualTo("Zwierzęta");
    }

    @Test
    void zmianaNazwyNaTeSama_200() throws Exception {
        Long id = createAndGetId(anna, "Kot", "EXPENSE");

        rename(anna, id, "Kot").andExpect(status().isOk());
    }

    @Test
    void zmianaNazwyNaZajeta_409() throws Exception {
        createAndGetId(anna, "Pies", "EXPENSE");
        Long id = createAndGetId(anna, "Kot", "EXPENSE");

        rename(anna, id, "pies")
                .andExpect(status().isConflict())
                .andExpect(status().reason("CATEGORY_EXISTS"));
    }

    @Test
    void zmianaNazwyCudzej_404() throws Exception {
        Long id = createAndGetId(anna, "Kot", "EXPENSE");

        rename(bartek, id, "Mój kot")
                .andExpect(status().isNotFound())
                .andExpect(status().reason("CATEGORY_NOT_FOUND"));
    }

    @Test
    void zmianaNazwyDomyslnej_403() throws Exception {
        Long id = saveDefault(uniqueName("Zakupy"), CategoryType.EXPENSE);

        rename(anna, id, "Moje zakupy")
                .andExpect(status().isForbidden())
                .andExpect(status().reason("DEFAULT_CATEGORY_READ_ONLY"));
    }

    // usuwanie

    @Test
    void usuniecie_204_iZnikaZBazy() throws Exception {
        Long id = createAndGetId(anna, "Kot", "EXPENSE");

        remove(anna, id).andExpect(status().isNoContent());

        assertThat(categoryRepository.findById(id)).isEmpty();
    }

    @Test
    void usuniecieCudzej_404_iZostajeWBazie() throws Exception {
        Long id = createAndGetId(anna, "Kot", "EXPENSE");

        remove(bartek, id).andExpect(status().isNotFound());

        assertThat(categoryRepository.findById(id)).isPresent();
    }

    @Test
    void usuniecieDomyslnej_403() throws Exception {
        Long id = saveDefault(uniqueName("Rachunki"), CategoryType.EXPENSE);

        remove(anna, id)
                .andExpect(status().isForbidden())
                .andExpect(status().reason("DEFAULT_CATEGORY_READ_ONLY"));
    }

    @Test
    void usuniecieNieistniejacej_404() throws Exception {
        remove(anna, 999_999L).andExpect(status().isNotFound());
    }

    // pomocnicze

    private ResultActions list(TestUser user) throws Exception {
        return mockMvc.perform(get("/api/categories").header("Authorization", user.bearer()))
                .andExpect(status().isOk());
    }

    private ResultActions create(TestUser user, String name, String type) throws Exception {
        return mockMvc.perform(post("/api/categories")
                .header("Authorization", user.bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "name": "%s", "type": "%s" }
                        """.formatted(name, type)));
    }

    private Long createAndGetId(TestUser user, String name, String type) throws Exception {
        String json = ApiHelper.body(create(user, name, type).andExpect(status().isCreated()).andReturn());
        return ApiHelper.readLong(json, "$.id");
    }

    private ResultActions rename(TestUser user, Long id, String name) throws Exception {
        return mockMvc.perform(put("/api/categories/" + id)
                .header("Authorization", user.bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "name": "%s" }
                        """.formatted(name)));
    }

    private ResultActions remove(TestUser user, Long id) throws Exception {
        return mockMvc.perform(delete("/api/categories/" + id).header("Authorization", user.bearer()));
    }

    // kategorie domyślne nie mają endpointu, wstawiamy je prosto do bazy;
    // baza H2 jest wspólna dla wszystkich klas testowych, stąd unikalne nazwy
    private Long saveDefault(String name, CategoryType type) {
        Category category = new Category();
        category.setName(name);
        category.setType(type);
        return categoryRepository.save(category).getId();
    }

    private static String uniqueName(String base) {
        return base + " " + UUID.randomUUID().toString().substring(0, 8);
    }
}
