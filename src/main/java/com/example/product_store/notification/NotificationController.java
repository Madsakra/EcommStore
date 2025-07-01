package com.example.product_store.notification;

import com.example.product_store.notification.dto.NotificationDTO;
import com.example.product_store.notification.service.GetNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class NotificationController {

  private final GetNotificationService getNotificationService;


  public NotificationController(GetNotificationService getNotificationService) {
    this.getNotificationService = getNotificationService;
  }

  @GetMapping("/notification")
  public ResponseEntity<NotificationDTO> getNotification() {

    NotificationDTO notificationDTO = getNotificationService.execute(null);

    if (notificationDTO == null) {
      return ResponseEntity.noContent().build(); // 204 No Content
    }

    return ResponseEntity.ok(notificationDTO); // 200 OK with body
  }
}
