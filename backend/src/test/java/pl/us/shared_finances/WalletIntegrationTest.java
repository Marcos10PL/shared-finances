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
import pl.us.shared_finances.repository.MembershipRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WalletIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MembershipRepository membershipRepository;

    private ApiHelper api;

    @BeforeEach
    void setUp() {
        api = new ApiHelper(mockMvc);
    }

    @Test
    void rejestracjaZakladaPierwszyPortfel() throws Exception {
        TestUser anna = api.register("anna");

        String json = ApiHelper.body(mockMvc.perform(get("/api/wallets").header("Authorization", anna.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].role").value("OWNER"))
                .andReturn());

        String name = JsonPath.read(json, "$[0].name");
        assertThat(name).isEqualTo("Mój portfel");
    }

    @Test
    void nowyPortfel_201_wlascicielJedynymCzlonkiem() throws Exception {
        TestUser anna = api.register("anna");

        mockMvc.perform(post("/api/wallets")
                        .header("Authorization", anna.bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nameJson("Mieszkanie")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Mieszkanie"))
                .andExpect(jsonPath("$.role").value("OWNER"))
                .andExpect(jsonPath("$.members.length()").value(1))
                .andExpect(jsonPath("$.members[0].id").value(anna.id()))
                .andExpect(jsonPath("$.invitations.length()").value(0))
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.monthlyLimit").value(nullValue()));
    }

    @Test
    void nowyPortfel_pustaNazwa_400() throws Exception {
        TestUser anna = api.register("anna");

        mockMvc.perform(post("/api/wallets")
                        .header("Authorization", anna.bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nameJson("")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void nowyPortfel_nazwa50Znakow_201_a51Znakow_400() throws Exception {
        TestUser anna = api.register("anna");

        mockMvc.perform(post("/api/wallets")
                        .header("Authorization", anna.bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nameJson("a".repeat(50))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/wallets")
                        .header("Authorization", anna.bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nameJson("a".repeat(51))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void zmianaNazwy_wlasciciel_200() throws Exception {
        TestUser anna = api.register("anna");
        Long walletId = api.createWallet(anna, "Mieszkanie");

        mockMvc.perform(put("/api/wallets/" + walletId)
                        .header("Authorization", anna.bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nameJson("Mieszkanie Krakowska")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mieszkanie Krakowska"));
    }

    @Test
    void zmianaNazwy_czlonek_403_NOT_OWNER() throws Exception {
        TestUser anna = api.register("anna");
        Long walletId = api.createWallet(anna, "Mieszkanie");
        TestUser bartek = api.addToWallet(anna, walletId, "bartek", "MEMBER");

        mockMvc.perform(put("/api/wallets/" + walletId)
                        .header("Authorization", bartek.bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nameJson("Przejęty")))
                .andExpect(status().isForbidden())
                .andExpect(status().reason("NOT_OWNER"));
    }

    @Test
    void obcyNieWidziPortfela_404_WALLET_NOT_FOUND() throws Exception {
        TestUser anna = api.register("anna");
        TestUser obcy = api.register("obcy");
        Long walletId = api.createWallet(anna, "Mieszkanie");

        mockMvc.perform(get("/api/wallets/" + walletId).header("Authorization", obcy.bearer()))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("WALLET_NOT_FOUND"));
    }

    @Test
    void usuniecie_204_portfelZnikaRazemZCzlonkostwami() throws Exception {
        TestUser anna = api.register("anna");
        Long walletId = api.createWallet(anna, "Mieszkanie");
        TestUser bartek = api.addToWallet(anna, walletId, "bartek", "MEMBER");

        mockMvc.perform(delete("/api/wallets/" + walletId).header("Authorization", anna.bearer()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/wallets/" + walletId).header("Authorization", bartek.bearer()))
                .andExpect(status().isNotFound());

        assertThat(membershipRepository.findByWalletId(walletId)).isEmpty();
    }

    @Test
    void usuniecie_gosc_403() throws Exception {
        TestUser anna = api.register("anna");
        Long walletId = api.createWallet(anna, "Mieszkanie");
        TestUser gosc = api.addToWallet(anna, walletId, "gosc", "GUEST");

        mockMvc.perform(delete("/api/wallets/" + walletId).header("Authorization", gosc.bearer()))
                .andExpect(status().isForbidden());
    }

    @Test
    void czlonekWidziCzlonkowAleNieZaproszenia() throws Exception {
        TestUser anna = api.register("anna");
        Long walletId = api.createWallet(anna, "Mieszkanie");
        TestUser bartek = api.addToWallet(anna, walletId, "bartek", "MEMBER");
        api.invite(anna, walletId, api.register("ola"), "GUEST");

        mockMvc.perform(get("/api/wallets/" + walletId).header("Authorization", bartek.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("MEMBER"))
                .andExpect(jsonPath("$.members.length()").value(2))
                .andExpect(jsonPath("$.invitations.length()").value(0));
    }

    private static String nameJson(String name) {
        return """
                { "name": "%s" }
                """.formatted(name);
    }
}