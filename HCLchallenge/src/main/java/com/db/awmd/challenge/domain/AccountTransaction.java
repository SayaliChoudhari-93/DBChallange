package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class AccountTransaction {

  @NotNull
  @NotEmpty
  private final String accountFromId;

  @NotNull
  @NotEmpty
  private final String accountToId;

  @NotNull
  @Min(value = 0, message = "Initial balance must be positive.")
  private BigDecimal amount;

  @JsonCreator
  public AccountTransaction(@JsonProperty("accountFromId") String accountFromId,
                            @JsonProperty("accountToId") String accountToId,
                            @JsonProperty("amount") BigDecimal amount) {
    this.accountFromId = accountFromId;
    this.accountToId = accountToId;
    this.amount = amount;

  }

  public String getAccountFromId() {
    return accountFromId;
  }

  public String getAccountToId() {
    return accountToId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

}
