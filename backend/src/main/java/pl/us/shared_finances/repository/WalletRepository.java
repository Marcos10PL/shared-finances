package pl.us.shared_finances.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.us.shared_finances.entity.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
