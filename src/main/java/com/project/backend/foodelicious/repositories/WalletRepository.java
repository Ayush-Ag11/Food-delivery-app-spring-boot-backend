package com.project.backend.foodelicious.repositories;

import com.project.backend.foodelicious.entities.User;
import com.project.backend.foodelicious.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUser(User user);
}
