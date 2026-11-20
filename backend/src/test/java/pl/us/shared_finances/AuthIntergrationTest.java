package pl.us.shared_finances;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.us.shared_finances.ApiHelper.TestUser;
import pl.us.shared_finances.entity.User;
import pl.us.shared_finances.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @SpringBootTest = cała aplikacja, @AutoConfigureMockMvc = MockMvc do wysyłania żądań (w Boot 4 trzeba to dopisać)
@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private ApiHelper api;

    @BeforeEach
    void setUp() {
        api = new ApiHelper(mockMvc);
    }

    // rejestracja

    @Test
    void rejestracja_201_zwracaTokenIUzytkownika_emailMalymiLiterami() throws Exception {
        String email = ApiHelper.uniqueEmail("anna");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson("Anna", email.toUpperCase(), "haslo1234")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.displayName").value("Anna"))
                .andExpect(jsonPath("$.user.email").value(email));
    }

    @Test
    void rejestracja_hasloZapisaneJakoSkrotBcrypt() throws Exception {
        TestUser anna = api.register("anna");

        User saved = userRepository.findByEmail(anna.email()).orElseThrow();

        assertThat(saved.getPasswordHash()).startsWith("$2");
        assertThat(saved.getPasswordHash()).isNotEqualTo(ApiHelper.PASSWORD);
    }

    @Test
    void rejestracja_tenSamEmail_409_EMAIL_TAKEN() throws Exception {
        TestUser anna = api.register("anna");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson("Anna", anna.email().toUpperCase(), "haslo1234")))
                .andExpect(status().isConflict())
                .andExpect(status().reason("EMAIL_TAKEN"));
    }

    @Test
    void rejestracja_haslo7Znakow_400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson("Anna", ApiHelper.uniqueEmail("anna"), "1234567")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejestracja_haslo8Znakow_201() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson("Anna", ApiHelper.uniqueEmail("anna"), "12345678")))
                .andExpect(status().isCreated());
    }

    @Test
    void rejestracja_zlyFormatEmaila_400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson("Anna", "anna-bez-malpy.pl", "haslo1234")))
                .andExpect(status().isBadRequest());
    }

    // logowanie

    @Test
    void logowanie_poprawneDane_200() throws Exception {
        TestUser anna = api.register("anna");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(anna.email(), ApiHelper.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.id").value(anna.id()));
    }

    @Test
    void logowanie_zleHaslo_401_INVALID_CREDENTIALS() throws Exception {
        TestUser anna = api.register("anna");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(anna.email(), "zle-haslo")))
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("INVALID_CREDENTIALS"));
    }

    @Test
    void logowanie_nieistniejacyEmail_401_tenSamKod() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(ApiHelper.uniqueEmail("nikt"), "haslo1234")))
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("INVALID_CREDENTIALS"));
    }

    // /me i token

    @Test
    void me_zTokenem_200() throws Exception {
        TestUser anna = api.register("anna");

        mockMvc.perform(get("/api/auth/me").header("Authorization", anna.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(anna.id()))
                .andExpect(jsonPath("$.email").value(anna.email()));
    }

    @Test
    void me_bezTokena_401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_zmienionyToken_401() throws Exception {
        TestUser anna = api.register("anna");
        String[] parts = anna.token().split("\\.");
        String tampered = parts[0] + ".f" + parts[1].substring(1) + "." + parts[2];

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + tampered))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenZLogowaniaDzialaTakJakZRejestracji() throws Exception {
        TestUser anna = api.register("anna");

        String json = ApiHelper.body(mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(anna.email(), ApiHelper.PASSWORD)))
                .andReturn());
        String token = JsonPath.read(json, "$.token");

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    // pomocnicze

    private static String registerJson(String name, String email, String password) {
        return """
                { "displayName": "%s", "email": "%s", "password": "%s" }
                """.formatted(name, email, password);
    }

    private static String loginJson(String email, String password) {
        return """
                { "email": "%s", "password": "%s" }
                """.formatted(email, password);
    }
}
