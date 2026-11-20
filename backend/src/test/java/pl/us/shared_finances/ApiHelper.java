package pl.us.shared_finances;

import com.jayway.jsonpath.JsonPath;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiHelper {

    public static final String PASSWORD = "haslo1234";

    private final MockMvc mockMvc;

    public ApiHelper(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public record TestUser(Long id, String email, String token) {
        public String bearer() {
            return "Bearer " + token;
        }
    }

    public static String uniqueEmail(String name) {
        return name + "-" + UUID.randomUUID() + "@test.pl";
    }

    public TestUser register(String name) throws Exception {
        String email = uniqueEmail(name).toLowerCase();

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "displayName": "%s", "email": "%s", "password": "%s" }
                                """.formatted(name, email, PASSWORD)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = body(result);
        return new TestUser(readLong(json, "$.user.id"), email, JsonPath.read(json, "$.token"));
    }

    public Long createWallet(TestUser owner, String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/wallets")
                        .header("Authorization", owner.bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "%s" }
                                """.formatted(name)))
                .andExpect(status().isCreated())
                .andReturn();

        return readLong(body(result), "$.id");
    }

    public Long invite(TestUser owner, Long walletId, TestUser invitee, String role) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/wallets/" + walletId + "/invitations")
                        .header("Authorization", owner.bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "%s", "role": "%s" }
                                """.formatted(invitee.email(), role)))
                .andExpect(status().isCreated())
                .andReturn();

        return readLong(body(result), "$.id");
    }

    public void accept(TestUser invitee, Long invitationId) throws Exception {
        mockMvc.perform(post("/api/invitations/" + invitationId + "/accept")
                        .header("Authorization", invitee.bearer()))
                .andExpect(status().isNoContent());
    }

    public TestUser addToWallet(TestUser owner, Long walletId, String name, String role) throws Exception {
        TestUser member = register(name);
        Long invitationId = invite(owner, walletId, member, role);
        accept(member, invitationId);
        return member;
    }

    public static String body(MvcResult result) throws Exception {
        return result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    public static Long readLong(String json, String path) {
        Number number = JsonPath.read(json, path);
        return number.longValue();
    }
}