package hello.squadfit.config;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailConfig {

    @Value("${spring.mail.username}")
    private String fromAddress;
    private final JavaMailSender javaMailSender;

    // 제목
    private final String SUBJECT = "[늘액션] 인증메일";

    public boolean sendCertificationMail(String email, String certificationNumber){

        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            String htmlContent = getCertificationMessage(certificationNumber);

            messageHelper.setFrom(fromAddress);
            messageHelper.setTo(email);
            messageHelper.setSubject(SUBJECT);
            messageHelper.setText(htmlContent, true);

            javaMailSender.send(message);

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private String getCertificationMessage(String certificationNumber){

        String certificationMessage = "";
        certificationMessage += "<h1 style='text-align'>"+SUBJECT+"</h1>";
        certificationMessage += "<h3 style='text-align>인증코드 : <strong style='font-size: 32px; letter-spacing: 8px'>" + certificationNumber + "</strong></h3>";
        return certificationMessage;
    }

}
