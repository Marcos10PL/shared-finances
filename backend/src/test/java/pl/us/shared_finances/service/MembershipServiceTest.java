package pl.us.shared_finances.service;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pl.us.shared_finances.dto.request.ChangeRoleRequest;
import pl.us.shared_finances.dto.response.MemberResponse;
import pl.us.shared_finances.entity.Membership;
import pl.us.shared_finances.entity.User;
import pl.us.shared_finances.entity.enums.WalletRole;
import pl.us.shared_finances.repository.MembershipRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembershipServiceTest {

    private static final Long WALLET_ID = 1L;
    private static final Long OWNER_ID = 10L;
    private static final Long MEMBER_ID = 20L;

    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private WalletPermissionService permissionService;

    @InjectMocks
    private MembershipService membershipService;

    @Test
    void wlascicielNieMozeOpuscicPortfela() {
        Membership owner = membership(OWNER_ID, WalletRole.OWNER);
        when(permissionService.requireMember(WALLET_ID, OWNER_ID)).thenReturn(owner);

        assertError(() -> membershipService.leaveWallet(WALLET_ID, OWNER_ID), "OWNER_CANNOT_LEAVE");
        verify(membershipRepository, never()).delete(any());
    }

    @Test
    void czlonekMozeOpuscicPortfel() {
        Membership member = membership(MEMBER_ID, WalletRole.MEMBER);
        when(permissionService.requireMember(WALLET_ID, MEMBER_ID)).thenReturn(member);

        membershipService.leaveWallet(WALLET_ID, MEMBER_ID);

        verify(membershipRepository).delete(member);
    }

    @Test
    void nieMoznaNadacRoliWlasciciela() {
        ChangeRoleRequest request = new ChangeRoleRequest(WalletRole.OWNER);

        assertError(() -> membershipService.changeRole(WALLET_ID, OWNER_ID, MEMBER_ID, request), "INVALID_ROLE");
        verify(membershipRepository, never()).save(any());
    }

    @Test
    void nieMoznaZmienicRoliWlasciciela() {
        Membership owner = membership(OWNER_ID, WalletRole.OWNER);
        when(membershipRepository.findByWalletIdAndUserId(WALLET_ID, OWNER_ID)).thenReturn(Optional.of(owner));
        ChangeRoleRequest request = new ChangeRoleRequest(WalletRole.GUEST);

        assertError(() -> membershipService.changeRole(WALLET_ID, OWNER_ID, OWNER_ID, request), "OWNER_PROTECTED");
        verify(membershipRepository, never()).save(any());
    }

    @Test
    void zmianaRoliZapisujeNowaRole() {
        Membership member = membership(MEMBER_ID, WalletRole.MEMBER);
        when(membershipRepository.findByWalletIdAndUserId(WALLET_ID, MEMBER_ID)).thenReturn(Optional.of(member));

        MemberResponse result = membershipService.changeRole(WALLET_ID, OWNER_ID, MEMBER_ID,
                new ChangeRoleRequest(WalletRole.GUEST));

        assertThat(result.role()).isEqualTo(WalletRole.GUEST);
        assertThat(member.getRole()).isEqualTo(WalletRole.GUEST);
        verify(membershipRepository).save(member);
    }

    @Test
    void zmianaRoliOsobySpozaPortfela() {
        when(membershipRepository.findByWalletIdAndUserId(WALLET_ID, MEMBER_ID)).thenReturn(Optional.empty());
        ChangeRoleRequest request = new ChangeRoleRequest(WalletRole.GUEST);

        assertError(() -> membershipService.changeRole(WALLET_ID, OWNER_ID, MEMBER_ID, request), "MEMBER_NOT_FOUND");
    }

    @Test
    void bezUprawnienNicSieNieZmienia() {
        when(permissionService.requireOwner(WALLET_ID, MEMBER_ID))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "NOT_OWNER"));
        ChangeRoleRequest request = new ChangeRoleRequest(WalletRole.GUEST);

        assertError(() -> membershipService.changeRole(WALLET_ID, MEMBER_ID, OWNER_ID, request), "NOT_OWNER");
        verifyNoInteractions(membershipRepository);
    }

    @Test
    void nieMoznaUsunacWlasciciela() {
        Membership owner = membership(OWNER_ID, WalletRole.OWNER);
        when(membershipRepository.findByWalletIdAndUserId(WALLET_ID, OWNER_ID)).thenReturn(Optional.of(owner));

        assertError(() -> membershipService.removeMember(WALLET_ID, OWNER_ID, OWNER_ID), "OWNER_PROTECTED");
        verify(membershipRepository, never()).delete(any());
    }

    // pomocnicze

    private static Membership membership(Long userId, WalletRole role) {
        User user = new User();
        user.setId(userId);
        user.setDisplayName("User " + userId);
        user.setEmail("user" + userId + "@test.pl");

        Membership membership = new Membership();
        membership.setUser(user);
        membership.setRole(role);
        return membership;
    }

    private static void assertError(ThrowingCallable call, String code) {
        assertThatThrownBy(call)
                .isInstanceOf(ResponseStatusException.class)
                .extracting("reason")
                .isEqualTo(code);
    }
}