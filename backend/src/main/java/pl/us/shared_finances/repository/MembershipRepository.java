package pl.us.shared_finances.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.us.shared_finances.entity.Membership;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    // członkostwo konkretnej osoby w konkretnym portfelu
    Optional<Membership> findByWalletIdAndUserId(Long walletId, Long userId);

    // wszystkie portfele usera
    List<Membership> findByUserId(Long userId);

    // członkowie portfela (lista "members" w odpowiedzi)
    List<Membership> findByWalletId(Long walletId);
}
