package com.db.awmd.challenge;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.AccountsTransactionalService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AccountsTransactionControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private AccountsTransactionalService transactionalService;

  @Autowired
  private AccountsService accountsService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void prepareMockMvc() {
    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
  }

  @Before
  public void createAccount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":2500}")).andExpect(status().isCreated());

    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-456\",\"balance\":1500}")).andExpect(status().isCreated());

  }

  @Test
  public void transferAmountHappyPath() throws Exception {
    this.mockMvc.perform(post("/v1/transactions").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"Id-456\",\"amount\":500}")).andExpect(status().isOk());
  }

  @Test
  public void transferAmountNegativeAmountExp() throws Exception {
    this.mockMvc.perform(post("/v1/transactions").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"Id-456\",\"amount\":-500}")).andExpect(status().isBadRequest());
  }

  @Test
  public void transferAmountInsufficientBalanceExp() throws Exception {
    this.mockMvc.perform(post("/v1/transactions").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"Id-456\",\"amount\":5000}")).andExpect(status().isNotAcceptable());
  }

  @Test
  public void transferAmountNoBody() throws Exception {
    this.mockMvc.perform(post("/v1/transactions").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
  }

  @Test
  public void transferAmountEmptyAccountId() throws Exception {
    this.mockMvc.perform(post("/v1/transactions").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountFromId\":\"\",\"accountToId\":\"\",\"amount\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void getAmountAfterTransaction() throws Exception {

    String IdFrom = "Id-123";
    String IdTo = "Id-456";

    this.transactionalService.transferMoneyToOtherAccounts(IdFrom, IdTo, new BigDecimal(500));

    BigDecimal afterTransBalanceFrom = this.accountsService.getAccount(IdFrom).getBalance();
    BigDecimal afterTransBalanceTo = this.accountsService.getAccount(IdTo).getBalance();

    //Verification After transaction
    assertThat(afterTransBalanceFrom).isEqualTo(new BigDecimal(2000));  // 2500 - 500 = 2000
    assertThat(afterTransBalanceTo).isEqualTo(new BigDecimal(2000));    // 1500 + 500 = 2000
  }

  @After    // This will be invoked after every test case.
  public void clearAccounts()
  {
    accountsService.getAccountsRepository().clearAccounts();
  }
}
