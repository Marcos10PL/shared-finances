package pl.us.shared_finances.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.us.shared_finances.entity.Invitation;
import pl.us.shared_finances.entity.enums.InvitationStatus;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    // oczekujące zaproszenia danego portfela
    List<Invitation> findByWalletIdAndStatus(Long walletId, InvitationStatus status);

    // moje zaproszenia
    List<Invitation> findByInviteeIdAndStatus(Long inviteeId, InvitationStatus status);

    // czy ta osoba ma już oczekujące zaproszenie do tego portfela
    boolean existsByWalletIdAndInviteeIdAndStatus(Long walletId, Long inviteeId, InvitationStatus status);
}