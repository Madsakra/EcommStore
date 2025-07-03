package com.example.product_store.notification;

import com.example.product_store.notification.dto.NotificationDTO;
import com.example.product_store.notification.service.GetNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "Admin Notification Management",
    description = "APIs for managing admin's notification for orders")
@RestController
@RequestMapping("/admin")
public class NotificationController {

  private final GetNotificationService getNotificationService;

  public NotificationController(GetNotificationService getNotificationService) {
    this.getNotificationService = getNotificationService;
  }

  @Operation(
      summary = "Get Notification for orders sold",
      description =
          "Get notification for orders sold to customers. Only usable by admins",
      security = @SecurityRequirement(name = "bearerAuth"))
  @GetMapping("/notification")
  public ResponseEntity<NotificationDTO> getNotification() {

    NotificationDTO notificationDTO = getNotificationService.execute(null);

    if (notificationDTO == null) {
      return ResponseEntity.noContent().build(); // 204 No Content
    }

    return ResponseEntity.ok(notificationDTO); // 200 OK with body
  }
}
