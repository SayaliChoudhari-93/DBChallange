package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import lombok.extern.slf4j.Slf4j;
import java.util.logging.Logger;

@Slf4j
public class EmailNotificationService implements NotificationService {

  @Override
  public void notifyAboutTransfer(Account account, String transferDescription) {
    //THIS METHOD SHOULD NOT BE CHANGED - ASSUME YOUR COLLEAGUE WILL IMPLEMENT IT

    Logger log = Logger.getLogger(EmailNotificationService.class.getName());
    log.info("Sending notification to owner of " + account.getAccountId() + " : " + transferDescription);
  }

}
