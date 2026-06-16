package study.data_jpa.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.entity.Member;

@SpringBootTest
@Transactional
// @Rollback(false)
class MemberJpaRepositoryTest {

  @Autowired private MemberJpaRepository memberJpaRepository;

  @Test
  public void testMember() {
    Member member = new Member("memberA");
    Member savedMember = memberJpaRepository.save(member);

    Member findMember = memberJpaRepository.find(savedMember.getId());

    assertThat(findMember.getId()).isEqualTo(savedMember.getId());
    assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());
    assertThat(findMember).isEqualTo(member);
  }
}
