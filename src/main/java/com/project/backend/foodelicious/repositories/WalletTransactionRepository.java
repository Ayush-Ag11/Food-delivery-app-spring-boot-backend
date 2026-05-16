package com.project.backend.foodelicious.repositories;

import com.project.backend.foodelicious.entities.Order;
import com.project.backend.foodelicious.entities.Wallet;
import com.project.backend.foodelicious.entities.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> findByWallet(Wallet wallet);

    List<WalletTransaction> findByOrder(Order order);
}
