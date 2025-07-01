package com.example.product_store.notification.service;

import com.example.product_store.notification.model.Notification;
import com.example.product_store.notification.repositories.NotificationRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CreateNotificationService {

  private final NotificationRepository notificationRepository;
  public static final Logger logger = LoggerFactory.getLogger(CreateNotificationService.class);
  List<Notification> notifications = new ArrayList<>();

  public CreateNotificationService(NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  public Void execute(
      Map<String, BigDecimal> purchasesMap, String batchOrderId, String clientId) {
    for (Map.Entry<String, BigDecimal> entry : purchasesMap.entrySet()) {
      String adminId = entry.getKey();
      BigDecimal totalCost = entry.getValue();
      String orderStatus = "Success";
      Notification notification =
          new Notification(
              null,
              adminId,
              batchOrderId,
              clientId,
              totalCost,
              LocalDateTime.now(),
              orderStatus);
      notifications.add(notification);

      logger.info(
          "Notification to admin {}: Customer {} just purchased goods of {}. Payment status -> {}",
          adminId,
          clientId,
          totalCost,
          orderStatus);
    }

    notificationRepository.saveAll(notifications);

    return null;
  }
}
