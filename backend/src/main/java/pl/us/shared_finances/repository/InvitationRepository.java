package pl.us.shared_finances.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.us.shared_finances.entity.Invitation;
import pl.us.shared_finances.entity.enums.InvitationStatus;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    List<Invitation> findByWalletIdAndStatus(Long walletId, InvitationStatus status);
}