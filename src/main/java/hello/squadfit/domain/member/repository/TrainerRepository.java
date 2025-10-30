package hello.squadfit.domain.member.repository;

import hello.squadfit.domain.member.entity.Trainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

//    List<Trainer> findAllByName(String trainerName);

    Trainer findOneByUserEntity_Id(Long userEntityId);

    Page<Trainer> findAllByName(String trainerName, PageRequest of);
}
