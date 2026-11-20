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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MembershipIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ApiHelper api;

    private TestUser anna;
    private TestUser bartek;
    private Long walletId;

    @BeforeEach
    void setUp() throws Exception {
        api = new ApiHelper(mockMvc);
        anna = api.register("Anna");
        walletId = api.createWallet(anna, "Mieszkanie");
        bartek = api.addToWallet(anna, walletId, "Bartek", "MEMBER");
    }

    // zmiana roli

    @Test
    void zmianaRoli_200_iDzialaOdRazuBezNowegoTokena() throws Exception {
        changeRole(anna, bartek.id(), "GUEST")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("GUEST"));

        mockMvc.perform(get("/api/wallets/" + walletId).header("Authorization", bartek.bearer()))
                .andExpect(jsonPath("$.role").value("GUEST"));
    }

    @Test
    void zmianaRoliNaOwner_400_INVALID_ROLE() throws Exception {
        changeRole(anna, bartek.id(), "OWNER")
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("INVALID_ROLE"));
    }

    @Test
    void zmianaRoliWlascicielki_400_OWNER_PROTECTED() throws Exception {
        changeRole(anna, anna.id(), "GUEST")
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("OWNER_PROTECTED"));
    }

    @Test
    void zmianaRoliPrzezCzlonka_403_NOT_OWNER() throws Exception {
        TestUser ola = api.addToWallet(anna, walletId, "Ola", "MEMBER");

        changeRole(bartek, ola.id(), "GUEST")
                .andExpect(status().isForbidden())
                .andExpect(status().reason("NOT_OWNER"));
    }

    @Test
    void zmianaRoliOsobySpozaPortfela_404_MEMBER_NOT_FOUND() throws Exception {
        TestUser obcy = api.register("Obcy");

        changeRole(anna, obcy.id(), "GUEST")
                .andExpect(status().isNotFound())
                .andExpect(status().reason("MEMBER_NOT_FOUND"));
    }

    // usuwanie członka

    @Test
    void usuniecieCzlonka_204_iTraciDostep() throws Exception {
        mockMvc.perform(delete("/api/wallets/" + walletId + "/members/" + bartek.id())
                        .header("Authorization", anna.bearer()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/wallets/" + walletId).header("Authorization", bartek.bearer()))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/wallets/" + walletId).header("Authorization", anna.bearer()))
                .andExpect(jsonPath("$.members.length()").value(1));
    }

    @Test
    void usuniecieWlascicielki_400_OWNER_PROTECTED() throws Exception {
        mockMvc.perform(delete("/api/wallets/" + walletId + "/members/" + anna.id())
                        .header("Authorization", anna.bearer()))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("OWNER_PROTECTED"));
    }

    // opuszczanie

    @Test
    void opuszczenie_czlonek_204() throws Exception {
        mockMvc.perform(post("/api/wallets/" + walletId + "/leave").header("Authorization", bartek.bearer()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/wallets/" + walletId).header("Authorization", bartek.bearer()))
                .andExpect(status().isNotFound());
    }

    @Test
    void opuszczenie_wlascicielka_400_OWNER_CANNOT_LEAVE() throws Exception {
        mockMvc.perform(post("/api/wallets/" + walletId + "/leave").header("Authorization", anna.bearer()))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("OWNER_CANNOT_LEAVE"));
    }

    @Test
    void opuszczenie_obcy_404() throws Exception {
        TestUser obcy = api.register("Obcy");

        mockMvc.perform(post("/api/wallets/" + walletId + "/leave").header("Authorization", obcy.bearer()))
                .andExpect(status().isNotFound());
    }

    private ResultActions changeRole(TestUser by, Long memberId, String role) throws Exception {
        return mockMvc.perform(put("/api/wallets/" + walletId + "/members/" + memberId + "/role")
                .header("Authorization", by.bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "role": "%s" }
                        """.formatted(role)));
    }
}