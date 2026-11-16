package com.misis.archapp.balance.query.projection;

import com.misis.archapp.balance.event.*;
import com.misis.archapp.balance.query.*;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.Instant;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
public class BalanceProjection {

    private final BalanceViewRepository viewRepo;
    private final BalanceHistoryRepository historyRepo;

    public BalanceProjection(
        BalanceViewRepository viewRepo,
        BalanceHistoryRepository historyRepo
    ) {
        this.viewRepo = viewRepo;
        this.historyRepo = historyRepo;
    }

    @EventHandler
    public void on(BalanceCreatedEvent event) {
        BalanceView view = new BalanceView()
            .setUserId(event.userId())
            .setBalance(BigDecimal.ZERO);

        viewRepo.save(view);
        saveHistory(event.userId(), "CREATE", BigDecimal.ZERO);
    }

    @EventHandler
    public void on(BalanceCreditedEvent event) {
        BalanceView view = viewRepo.findById(event.userId())
            .orElseThrow(EntityNotFoundException::new);

        view.setBalance(view.getBalance().add(event.amount()));
        viewRepo.save(view);

        saveHistory(event.userId(), "CREDIT", event.amount());
    }

    @EventHandler
    public void on(BalanceDebitedEvent event) {
        BalanceView view = viewRepo.findById(event.userId())
            .orElseThrow(EntityNotFoundException::new);

        view.setBalance(view.getBalance().subtract(event.amount()));
        viewRepo.save(view);

        saveHistory(event.userId(), "DEBIT", event.amount());
    }

    private void saveHistory(
        java.util.UUID userId,
        String type,
        BigDecimal amount
    ) {
        BalanceHistoryEntry entry = new BalanceHistoryEntry()
            .setUserId(userId)
            .setType(type)
            .setAmount(amount)
            .setTimestamp(Instant.now());

        historyRepo.save(entry);
    }
}