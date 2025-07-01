package com.example.product_store.notification.service;

import com.example.product_store.QueryBinder;
import com.example.product_store.authentication.model.Account;
import com.example.product_store.authentication.service.RetrieveAccountService;
import com.example.product_store.notification.dto.NotificationDTO;
import com.example.product_store.notification.model.Notification;
import com.example.product_store.notification.repositories.NotificationRepository;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GetNotificationService implements QueryBinder<Void, NotificationDTO> {
  private final NotificationRepository notificationRepository;
  private final RetrieveAccountService retrieveAccountService;
  private final Logger logger = LoggerFactory.getLogger(GetNotificationService.class);

  public GetNotificationService(
      NotificationRepository notificationRepository,
      RetrieveAccountService retrieveAccountService) {
    this.notificationRepository = notificationRepository;
    this.retrieveAccountService = retrieveAccountService;
  }

  @Override
  public NotificationDTO execute(Void input) {
      // SERVICE WILL CHECK WHETHER THE ACCOUNT EXISTS
    Account account = retrieveAccountService.execute(null);
    Optional<Notification> notificationOptional =
        notificationRepository.findFirstByAdminIdOrderByCreatedAtDesc(account.getId());
    logger.info(notificationOptional.toString());
    return notificationOptional
        .map(NotificationDTO::new)
        .orElse(null); // Let controller decide how to handle absence
  }
}
