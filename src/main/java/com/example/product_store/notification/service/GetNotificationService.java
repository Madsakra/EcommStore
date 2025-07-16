package com.example.product_store.notification.service;

import com.example.product_store.QueryBinder;
import com.example.product_store.notification.dto.NotificationDTO;
import com.example.product_store.notification.model.Notification;
import com.example.product_store.notification.repositories.NotificationRepository;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GetNotificationService implements QueryBinder<String, NotificationDTO> {
  private final NotificationRepository notificationRepository;
  private final Logger logger = LoggerFactory.getLogger(GetNotificationService.class);

  public GetNotificationService(
      NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  @Override
  public NotificationDTO execute(String jti) {
      // SERVICE WILL CHECK WHETHER THE ACCOUNT EXISTS
    Optional<Notification> notificationOptional =
        notificationRepository.findFirstByAdminIdOrderByCreatedAtDesc(jti);
    logger.info(notificationOptional.toString());
    return notificationOptional
        .map(NotificationDTO::new)
        .orElse(null); // Let controller decide how to handle absence
  }
}
