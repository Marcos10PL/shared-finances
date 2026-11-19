package pl.us.shared_finances.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.us.shared_finances.entity.Membership;
import pl.us.shared_finances.entity.enums.WalletRole;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    // członkostwo konkretnej osoby w konkretnym portfelu
    Optional<Membership> findByWalletIdAndUserId(Long walletId, Long userId);

    // wszystkie portfele usera
    List<Membership> findByUserId(Long userId);

    // członkowie portfela
    List<Membership> findByWalletId(Long walletId);

    // właściciel portfela, żeby w zaproszeniu pokazać, kto zaprasza
    Optional<Membership> findByWalletIdAndRole(Long walletId, WalletRole role);

    // czy ta osoba już jest w portfelu
    boolean existsByWalletIdAndUserId(Long walletId, Long userId);
}