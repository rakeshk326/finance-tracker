package com.personal.finance_tracker.controller;

import com.personal.finance_tracker.dto.account.AccountResponseDTO;
import com.personal.finance_tracker.dto.account.CreateAccountRequestDTO;
import com.personal.finance_tracker.dto.account.UpdateAccountRequestDTO;
import com.personal.finance_tracker.service.AccountService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    AccountService accountService;

    @GetMapping("")
    public ResponseEntity<List<AccountResponseDTO>> getAccounts() {
        return ResponseEntity.ok(accountService.getAccounts());
    }

    @PostMapping("")
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody @Valid CreateAccountRequestDTO createAccountRequestDTO) {
        return ResponseEntity.ok(accountService.createAccount(createAccountRequestDTO));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> updateAccount(@PathVariable UUID id, @RequestBody UpdateAccountRequestDTO updateAccountRequestDTO) {
        return ResponseEntity.ok().body(accountService.updateAccount(id, updateAccountRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}