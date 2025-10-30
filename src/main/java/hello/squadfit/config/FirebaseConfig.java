package hello.squadfit.config;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

    @Value("${app.fcm.project-id}")
    private String projectId;

    @Value("${app.fcm.credentials-path}")
    private String credPath; // todo: 이건 -> $env:GOOGLE_APPLICATION_CREDENTIALS="C:\secrets\firebase-sa.json"

    @PostConstruct
    public void init() throws Exception {
        if (FirebaseApp.getApps().isEmpty()) {
            var res = new org.springframework.core.io.DefaultResourceLoader().getResource(credPath);
            try (var is = res.getInputStream()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(is))
                        .setProjectId(projectId)
                        .build();

                FirebaseApp.initializeApp(options);
            }
        }
    }
}