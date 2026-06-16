package study.data_jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.entity.Member;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
  @Autowired private MemberRepository memberRepository;

  @Test
  public void testMember() {
    Member member = new Member("memberA");
    Member savedMember = memberRepository.save(member);

    Member findMember = memberRepository.findById(savedMember.getId()).get();

    assertThat(findMember.getId()).isEqualTo(savedMember.getId());
    assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());
    assertThat(findMember).isEqualTo(member);
  }
}
