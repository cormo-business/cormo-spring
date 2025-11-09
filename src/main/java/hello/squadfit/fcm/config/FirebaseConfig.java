package hello.squadfit.fcm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws Exception {
        if (FirebaseApp.getApps().isEmpty()) {
            String b64 = System.getenv("FIREBASE_SA_B64");
            if (b64 == null || b64.isBlank()) {
                throw new IllegalStateException("❌ FIREBASE_SA_B64 환경변수가 설정되지 않았습니다!");
            }

            byte[] decoded = Base64.getDecoder().decode(b64);
            try (InputStream in = new ByteArrayInputStream(decoded)) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(in))
                        .build();
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase initialized successfully from Base64 env.");
            }
        }
    }
}
