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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InvitationIntegrationTest {

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
        bartek = api.register("Bartek");
        walletId = api.createWallet(anna, "Mieszkanie");
    }

    // wysyłanie

    @Test
    void zaproszenie_201_iWidoczneUWlascicielki() throws Exception {
        invite(anna, bartek.email(), "MEMBER")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(bartek.email()))
                .andExpect(jsonPath("$.role").value("MEMBER"))
                .andExpect(jsonPath("$.ownerId").value(anna.id()))
                .andExpect(jsonPath("$.ownerName").value("Anna"));

        mockMvc.perform(get("/api/wallets/" + walletId).header("Authorization", anna.bearer()))
                .andExpect(jsonPath("$.invitations.length()").value(1))
                .andExpect(jsonPath("$.invitations[0].email").value(bartek.email()));
    }

    @Test
    void drugieZaproszenie_409_ALREADY_INVITED() throws Exception {
        api.invite(anna, walletId, bartek, "MEMBER");

        invite(anna, bartek.email(), "MEMBER")
                .andExpect(status().isConflict())
                .andExpect(status().reason("ALREADY_INVITED"));
    }

    @Test
    void zaproszenieOdCzlonka_403_NOT_OWNER() throws Exception {
        api.accept(bartek, api.invite(anna, walletId, bartek, "MEMBER"));
        TestUser ola = api.register("Ola");

        invite(bartek, ola.email(), "GUEST")
                .andExpect(status().isForbidden())
                .andExpect(status().reason("NOT_OWNER"));
    }

    // moje zaproszenia, przyjęcie, odrzucenie

    @Test
    void mojeZaproszenia_pokazujaKtoIDokadZaprasza() throws Exception {
        api.invite(anna, walletId, bartek, "GUEST");

        mockMvc.perform(get("/api/invitations").header("Authorization", bartek.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].ownerId").value(anna.id()))
                .andExpect(jsonPath("$[0].ownerName").value("Anna"))
                .andExpect(jsonPath("$[0].walletId").value(walletId))
                .andExpect(jsonPath("$[0].walletName").value("Mieszkanie"))
                .andExpect(jsonPath("$[0].role").value("GUEST"));
    }

    @Test
    void przyjecie_204_portfelPojawiaSieZRolaZZaproszenia() throws Exception {
        Long invitationId = api.invite(anna, walletId, bartek, "GUEST");

        api.accept(bartek, invitationId);

        mockMvc.perform(get("/api/wallets/" + walletId).header("Authorization", bartek.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("GUEST"))
                .andExpect(jsonPath("$.members.length()").value(2));

        mockMvc.perform(get("/api/invitations").header("Authorization", bartek.bearer()))
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    void odrzucenie_204_iMoznaZaprosicPonownie() throws Exception {
        Long invitationId = api.invite(anna, walletId, bartek, "MEMBER");

        mockMvc.perform(post("/api/invitations/" + invitationId + "/reject")
                        .header("Authorization", bartek.bearer()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/wallets/" + walletId).header("Authorization", anna.bearer()))
                .andExpect(jsonPath("$.invitations.length()").value(0));

        invite(anna, bartek.email(), "GUEST")
                .andExpect(status().isCreated());
    }

    private ResultActions invite(TestUser from, String email, String role) throws Exception {
        return mockMvc.perform(post("/api/wallets/" + walletId + "/invitations")
                .header("Authorization", from.bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "email": "%s", "role": "%s" }
                        """.formatted(email, role)));
    }
}