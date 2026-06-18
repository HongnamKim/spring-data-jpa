package study.data_jpa.entity;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class MemberTest {
  @PersistenceContext EntityManager em;

  @Test
  @Transactional
  @Rollback(false)
  public void testEntity() {
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    em.persist(teamA);
    em.persist(teamB);

    Member member1 = new Member("member1", 10, teamA);
    Member member2 = new Member("member2", 20, teamA);
    Member member3 = new Member("member3", 30, teamB);
    Member member4 = new Member("member4", 40, teamB);
    em.persist(member1);
    em.persist(member2);
    em.persist(member3);
    em.persist(member4);

    // 초기화
    em.flush(); // DB 에 쿼리 날리기
    em.clear(); // 영속성 컨텍스트 캐시 날리기 -> 이후 조회 시 DB 조회하기 위함

    List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();

    for (Member member : members) {
      System.out.println("member = " + member);
      System.out.println("-> member.team=" + member.getTeam());
    }
  }
}
