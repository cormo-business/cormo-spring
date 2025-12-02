package hello.squadfit.domain.certification.service;

import hello.squadfit.config.EmailConfig;
import hello.squadfit.domain.certification.repository.EmailRepository;
import hello.squadfit.domain.certification.request.CheckedEmailRequest;
import hello.squadfit.domain.certification.request.SendEmailRequest;
import hello.squadfit.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailService {

    private final MemberService memberService;
    private final EmailConfig emailConfig;
    private final EmailRepository emailRepository;

    @Transactional
    public Boolean sendEmail(SendEmailRequest request){

        try{

            String certificationNumber = getCertificationNumber();

            boolean isSuccess = emailConfig.sendCertificationMail(request.email(), certificationNumber);

            if(!isSuccess) return false;

            // 레디스 저장하기
            emailRepository.save(request.email(), certificationNumber,  Duration.ofMinutes(10));

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }


        return  true;
    }

    public Boolean checkedEmail(CheckedEmailRequest request) {
        String correctCode = emailRepository.find(request.email());

        log.info("저장된 키: {}",correctCode);

        if(correctCode == null){
            return false;
        }
        if(correctCode.equals(request.code())){
            return true;
        }else{
            return false;
        }
    }

    // 랜덤 번호 만들기
    private String getCertificationNumber() {
        String certificationNumber = "";
        for(int count = 0; count < 4; count++){
            certificationNumber += (int) (Math.random() * 10);
        }

        return certificationNumber;
    }


}
