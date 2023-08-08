package org.baggle.infra.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FcmConfig {
    @Bean
    FirebaseApp firebaseApp() throws IOException{
        ClassPathResource resource = new ClassPathResource("firebase/ssang-1a9ab-firebase-adminsdk-7nen2-2c1210a093.json");
        InputStream refreshToken = resource.getInputStream();
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(refreshToken))
                .build();
//        FirebaseApp firebaseApp = null;
//        List<FirebaseApp> firebaseAppList = FirebaseApp.getApps();
//        if(firebaseAppList != null && !firebaseAppList.isEmpty())
//            for(FirebaseApp app: firebaseAppList)
//                if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME))
//                    firebaseApp = app;
//                else{
//                    FirebaseOptions options = FirebaseOptions.builder()
//                            .setCredentials(GoogleCredentials.fromStream(refreshToken))
//                            .build();
//                    firebaseApp = FirebaseApp.initializeApp(options);
//                }
        return FirebaseApp.initializeApp(options);
    }
    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException{
        return FirebaseMessaging.getInstance(firebaseApp());
    }
}
