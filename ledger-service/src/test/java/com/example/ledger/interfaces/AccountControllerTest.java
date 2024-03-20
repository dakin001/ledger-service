package com.example.ledger.interfaces;

import com.example.ledger.application.command.service.impl.AccountServiceImpl;
import com.example.ledger.application.query.BalanceQueryDto;
import com.example.ledger.application.query.PageResult;
import com.example.ledger.application.query.QueryService;
import com.example.ledger.application.query.WalletProjection;
import com.example.ledger.domain.account.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {
    @Mock
    AccountServiceImpl accountService;
    @Mock
    QueryService queryService;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        var controller = new AccountController(accountService, queryService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void open_success() throws Exception {
        Account account = new Account();
        when(accountService.open(any())).thenReturn(account);

        this.mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated());

        verify(accountService, times(1)).open(any());
    }

    @Test
    void close_success() throws Exception {
        this.mockMvc.perform(delete("/accounts/ABCDE001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());

        verify(accountService, times(1)).close(any());
    }

    @Test
    void getHistory_success() throws Exception {
        var argCaptor = ArgumentCaptor.forClass(BalanceQueryDto.class);
        when(queryService.getHistory(argCaptor.capture())).thenReturn(new PageResult<>(List.of(new WalletProjection())));

        this.mockMvc.perform(get("/accounts/ABCDE001/wallets/CURRENCY/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        BalanceQueryDto queryDto = argCaptor.getValue();
        assertEquals("ABCDE001", queryDto.getAccountNumber());
        assertEquals("CURRENCY", queryDto.getWalletType());
    }
}