package pl.us.shared_finances.service;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import pl.us.shared_finances.dto.request.InvitationRequest;
import pl.us.shared_finances.dto.response.InvitationResponse;
import pl.us.shared_finances.entity.Invitation;
import pl.us.shared_finances.entity.Membership;
import pl.us.shared_finances.entity.User;
import pl.us.shared_finances.entity.Wallet;
import pl.us.shared_finances.entity.enums.InvitationStatus;
import pl.us.shared_finances.entity.enums.WalletRole;
import pl.us.shared_finances.repository.InvitationRepository;
import pl.us.shared_finances.repository.MembershipRepository;
import pl.us.shared_finances.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvitationServiceTest {

    private static final Long WALLET_ID = 1L;
    private static final Long INVITATION_ID = 5L;

    @Mock
    private InvitationRepository invitationRepository;

    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletPermissionService permissionService;

    @InjectMocks
    private InvitationService invitationService;

    private final User anna = user(10L, "Anna", "anna@test.pl");     // właściciel
    private final User bartek = user(20L, "Bartek", "bartek@test.pl"); // zapraszany

    @Test
    void zaproszenieSamegoSiebie() {
        when(userRepository.findByEmail("anna@test.pl")).thenReturn(Optional.of(anna));

        assertError(() -> invitationService.invite(WALLET_ID, anna.getId(),
                new InvitationRequest("anna@test.pl", WalletRole.MEMBER)), "SELF");
        verify(invitationRepository, never()).save(any());
    }

    @Test
    void zaproszenieNaEmailBezKonta() {
        when(userRepository.findByEmail("nikt@test.pl")).thenReturn(Optional.empty());

        assertError(() -> invitationService.invite(WALLET_ID, anna.getId(),
                new InvitationRequest("nikt@test.pl", WalletRole.MEMBER)), "NO_ACCOUNT");
    }

    @Test
    void zaproszenieOsobyKtoraJuzJestWPortfelu() {
        when(userRepository.findByEmail("bartek@test.pl")).thenReturn(Optional.of(bartek));
        when(membershipRepository.existsByWalletIdAndUserId(WALLET_ID, bartek.getId())).thenReturn(true);

        assertError(() -> invitationService.invite(WALLET_ID, anna.getId(),
                new InvitationRequest("bartek@test.pl", WalletRole.MEMBER)), "ALREADY_MEMBER");
    }

    @Test
    void drugieOczekujaceZaproszenie() {
        when(userRepository.findByEmail("bartek@test.pl")).thenReturn(Optional.of(bartek));
        when(invitationRepository.existsByWalletIdAndInviteeIdAndStatus(WALLET_ID, bartek.getId(), InvitationStatus.PENDING))
                .thenReturn(true);

        assertError(() -> invitationService.invite(WALLET_ID, anna.getId(),
                new InvitationRequest("bartek@test.pl", WalletRole.MEMBER)), "ALREADY_INVITED");
    }

    @Test
    void zaproszenieZRolaWlasciciela() {
        assertError(() -> invitationService.invite(WALLET_ID, anna.getId(),
                new InvitationRequest("bartek@test.pl", WalletRole.OWNER)), "INVALID_ROLE");
        verifyNoInteractions(userRepository);
    }

    @Test
    void emailJestNormalizowanyIZaproszenieZapisane() {
        // given
        Wallet wallet = wallet();
        Membership ownerMembership = membership(wallet, anna, WalletRole.OWNER);
        when(permissionService.requireOwner(WALLET_ID, anna.getId())).thenReturn(ownerMembership);

        when(userRepository.findByEmail("bartek@test.pl")).thenReturn(Optional.of(bartek));
        when(membershipRepository.findByWalletIdAndRole(WALLET_ID, WalletRole.OWNER)).thenReturn(Optional.of(ownerMembership));

        // when
        InvitationResponse result = invitationService.invite(WALLET_ID, anna.getId(),
                new InvitationRequest("  Bartek@Test.PL ", WalletRole.GUEST));

        // then
        assertThat(result.email()).isEqualTo("bartek@test.pl");
        assertThat(result.role()).isEqualTo(WalletRole.GUEST);
        assertThat(result.ownerId()).isEqualTo(anna.getId());
        assertThat(result.ownerName()).isEqualTo("Anna");
        verify(invitationRepository).save(any(Invitation.class));
    }

    @Test
    void przyjecieCudzegoZaproszenia() {
        Invitation invitation = invitation(bartek, InvitationStatus.PENDING, WalletRole.MEMBER);
        when(invitationRepository.findById(INVITATION_ID)).thenReturn(Optional.of(invitation));

        // ktoś inny niż Bartek próbuje przyjąć jego zaproszenie
        assertError(() -> invitationService.accept(INVITATION_ID, 30L), "INVITATION_NOT_FOUND");
        verify(membershipRepository, never()).save(any());
    }

    @Test
    void przyjecieJuzRozpatrzonegoZaproszenia() {
        Invitation invitation = invitation(bartek, InvitationStatus.ACCEPTED, WalletRole.MEMBER);
        when(invitationRepository.findById(INVITATION_ID)).thenReturn(Optional.of(invitation));

        assertError(() -> invitationService.accept(INVITATION_ID, bartek.getId()), "INVITATION_ALREADY_ANSWERED");
    }

    @Test
    void przyjecieTworzyCzlonkostwoZRolaZZaproszenia() {
        // given
        Invitation invitation = invitation(bartek, InvitationStatus.PENDING, WalletRole.GUEST);
        when(invitationRepository.findById(INVITATION_ID)).thenReturn(Optional.of(invitation));

        // when
        invitationService.accept(INVITATION_ID, bartek.getId());

        // then
        ArgumentCaptor<Membership> captor = ArgumentCaptor.forClass(Membership.class);
        verify(membershipRepository).save(captor.capture());
        assertThat(captor.getValue().getUser()).isEqualTo(bartek);
        assertThat(captor.getValue().getRole()).isEqualTo(WalletRole.GUEST);
        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.ACCEPTED);
    }

    @Test
    void odrzucenieZmieniaStatusINieTworzyCzlonkostwa() {
        Invitation invitation = invitation(bartek, InvitationStatus.PENDING, WalletRole.MEMBER);
        when(invitationRepository.findById(INVITATION_ID)).thenReturn(Optional.of(invitation));

        invitationService.reject(INVITATION_ID, bartek.getId());

        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.REJECTED);
        verify(membershipRepository, never()).save(any());
    }

    // pomocnicze

    private static User user(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setDisplayName(name);
        user.setEmail(email);
        return user;
    }

    private static Wallet wallet() {
        Wallet wallet = new Wallet();
        wallet.setId(WALLET_ID);
        wallet.setName("Mieszkanie");
        return wallet;
    }

    private static Membership membership(Wallet wallet, User user, WalletRole role) {
        Membership membership = new Membership();
        membership.setWallet(wallet);
        membership.setUser(user);
        membership.setRole(role);
        return membership;
    }

    private static Invitation invitation(User invitee, InvitationStatus status, WalletRole role) {
        Invitation invitation = new Invitation();
        invitation.setId(INVITATION_ID);
        invitation.setWallet(wallet());
        invitation.setInvitee(invitee);
        invitation.setStatus(status);
        invitation.setRole(role);
        return invitation;
    }

    private static void assertError(ThrowingCallable call, String code) {
        assertThatThrownBy(call)
                .isInstanceOf(ResponseStatusException.class)
                .extracting("reason")
                .isEqualTo(code);
    }
}