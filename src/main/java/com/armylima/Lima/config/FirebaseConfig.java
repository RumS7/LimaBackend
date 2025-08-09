package com.armylima.Lima.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
@Service
public class FirebaseConfig {

    // --- ADD THIS LOGGER ---
    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${app.firebase-configuration-file}")
    private Resource serviceAccount;

    @PostConstruct
    public void initialize() {
        try {
            InputStream serviceAccountStream = serviceAccount.getInputStream();
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                // --- ADD THIS SUCCESS MESSAGE ---
                logger.info(">>>>>>>>>> Firebase has been initialized successfully. <<<<<<<<<<");
            }
        } catch (Exception e) {
            // --- ADD THIS ERROR MESSAGE ---
            logger.error(">>>>>>>>>> !!! FIREBASE INITIALIZATION FAILED !!! <<<<<<<<<<", e);
        }
    }
}