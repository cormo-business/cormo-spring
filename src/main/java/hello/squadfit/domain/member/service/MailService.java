package hello.squadfit.domain.member.service;

import hello.squadfit.config.EmailConfig;
import hello.squadfit.domain.member.entity.Member;
import hello.squadfit.domain.member.request.EmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MailService {

    private final MemberService memberService;
    private final EmailConfig emailConfig;

    @Transactional
    public Boolean checkMail(Long userId, EmailRequest request){

        try{

            Member member = memberService.findOneByUserId(userId);

            String certificationNumber = getCertificationNumber();

            boolean isSuccess = emailConfig.sendCertificationMail(request.email(), certificationNumber);

            if(!isSuccess) return false;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return  true;
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
