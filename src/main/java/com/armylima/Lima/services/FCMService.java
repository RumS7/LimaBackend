package com.armylima.Lima.services;


import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

@Service
public class FCMService {

    public void sendNotification(String token, String title, String body) {
        String bannerImageUrl = "https://i.imgur.com/your-image-link.png";

        // 2. Build the standard notification for all platforms.
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        // 3. Build an Android-specific configuration to include the image.
        AndroidNotification androidNotification = AndroidNotification.builder()
                .setImage(bannerImageUrl)
                .build();

        AndroidConfig androidConfig = AndroidConfig.builder()
                .setNotification(androidNotification)
                .build();

        // 4. Build the final message, including the Android-specific config.
        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .setAndroidConfig(androidConfig) // Attach the config with the image
                
                .build();

        // --- END OF NEW LOGIC ---

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            System.err.println("Failed to send FCM message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

//https://i.postimg.cc/XNdGRGZX/alert.png
