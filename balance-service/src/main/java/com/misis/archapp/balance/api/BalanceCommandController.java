package com.misis.archapp.balance.api;

import com.misis.archapp.balance.command.*;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/balances")
public class BalanceCommandController {

    private final CommandGateway gateway;

    public BalanceCommandController(CommandGateway gateway) {
        this.gateway = gateway;
    }

    @PostMapping("/{userId}/create")
    public CompletableFuture<String> create(
        @PathVariable UUID userId
    ) {
        return gateway.send(new CreateBalanceCommand(userId));
    }

    @PostMapping("/{userId}/credit")
    public CompletableFuture<Void> credit(
        @PathVariable UUID userId,
        @RequestBody BigDecimal amount
    ) {
        return gateway.send(new CreditBalanceCommand(userId, amount));
    }

    @PostMapping("/{userId}/debit")
    public CompletableFuture<Void> debit(
        @PathVariable UUID userId,
        @RequestBody BigDecimal amount
    ) {
        return gateway.send(new DebitBalanceCommand(userId, amount));
    }
}