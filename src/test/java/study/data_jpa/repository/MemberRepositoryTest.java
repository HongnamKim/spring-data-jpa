package study.data_jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
  @Autowired private MemberRepository memberRepository;
  @Autowired private TeamRepository teamRepository;
  @Autowired private EntityManager em;

  @Test
  public void testMember() {
    Member member = new Member("memberA");
    Member savedMember = memberRepository.save(member);

    Member findMember = memberRepository.findById(savedMember.getId()).get();

    assertThat(findMember.getId()).isEqualTo(savedMember.getId());
    assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());
    assertThat(findMember).isEqualTo(member);
  }

  @Test
  public void basicCRUD() {
    Member member1 = new Member("member1");
    Member member2 = new Member("member2");
    memberRepository.save(member1);
    memberRepository.save(member2);

    // 단건 조회 검증
    Member findMember1 = memberRepository.findById(member1.getId()).get();
    Member findMember2 = memberRepository.findById(member2.getId()).get();
    assertThat(findMember1).isEqualTo(member1);
    assertThat(findMember2).isEqualTo(member2);

    List<Member> all = memberRepository.findAll();
    assertThat(all.size()).isEqualTo(2);

    long count = memberRepository.count();
    assertThat(count).isEqualTo(2);

    memberRepository.delete(member1);
    memberRepository.delete(member2);

    long deletedCount = memberRepository.count();
    assertThat(deletedCount).isEqualTo(0);
  }

  @Test
  public void namedQuery() {
    Member m1 = new Member("AAA", 10);
    Member m2 = new Member("BBB", 20);
    memberRepository.save(m1);
    memberRepository.save(m2);

    List<Member> result = memberRepository.findByUsername("AAA");
    Member findMember = result.getFirst();
    assertThat(findMember.getUsername()).isEqualTo("AAA");
  }

  @Test
  public void testQuery() {
    Member m1 = new Member("AAA", 10);
    Member m2 = new Member("BBB", 20);
    memberRepository.save(m1);
    memberRepository.save(m2);

    List<Member> result = memberRepository.findUser("AAA", 10);
    assertThat(result.getFirst()).isEqualTo(m1);
  }

  @Test
  public void findUsernameList() {
    Member m1 = new Member("AAA", 10);
    Member m2 = new Member("BBB", 20);
    memberRepository.save(m1);
    memberRepository.save(m2);

    List<String> result = memberRepository.findUsernameList();
    for (String name : result) {
      System.out.println("name = " + name);
    }
    assertThat(result.size()).isEqualTo(2);
  }

  @Test
  public void findMemberDto() {
    Team team = new Team("teamA");
    teamRepository.save(team);

    Member m1 = new Member("AAA", 10);
    m1.setTeam(team);
    memberRepository.save(m1);

    List<MemberDto> memberDto = memberRepository.findMemberDto();
    System.out.println("memberDto.getFirst() = " + memberDto.getFirst());
  }

  @Test
  public void findByUsernames() {
    Member m1 = new Member("AAA", 10);
    Member m2 = new Member("BBB", 20);
    memberRepository.save(m1);
    memberRepository.save(m2);

    List<Member> result = memberRepository.findByUsernames(List.of("AAA", "BBB"));
    for (Member member : result) {
      System.out.println("member = " + member);
    }
    assertThat(result.size()).isEqualTo(2);
  }

  @Test
  public void paging() {
    // given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 10));
    memberRepository.save(new Member("member3", 10));
    memberRepository.save(new Member("member4", 10));
    memberRepository.save(new Member("member5", 10));

    int age = 10;
    // int offset = 0;
    // int limit = 3;

    PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

    // when
    Page<Member> page = memberRepository.findByAge(age, pageRequest);
    Page<MemberDto> response =
        page.map(member -> new MemberDto(member.getId(), member.getUsername(), null)); // DTO 로 변환
    // long totalCount = memberRepository.totalCount(age);

    // then
    List<Member> content = page.getContent();

    long totalElements = page.getTotalElements();

    assertThat(content.size()).isEqualTo(3);
    assertThat(page.getTotalElements()).isEqualTo(5);
    assertThat(page.getNumber()).isEqualTo(0);
    assertThat(page.getTotalPages()).isEqualTo(2);
    assertThat(page.isFirst()).isTrue();
    assertThat(page.hasNext()).isTrue();
  }

  @Test
  public void bulkUpdate() throws Exception {
    // given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 19));
    memberRepository.save(new Member("member3", 20));
    memberRepository.save(new Member("member4", 21));
    memberRepository.save(new Member("member5", 40));
    // when
    int resultCount = memberRepository.bulkAgePlus(20);

    List<Member> result = memberRepository.findByUsername("member5");
    Member member5 = result.getFirst();
    System.out.println("member5 = " + member5); // 벌크 수정 시 영속성 컨텍스트를 무시하고 업데이트가 실행됨
    // findByUsername 으로 조회 시 DB 조회는 하지만(PK로 조회가 아니므로) 영속성 컨텍스트의 1차 캐시 값을 사용하기 때문에
    // 업데이트가 반영되지 않은 age = 40 으로 남아있음.
    // @Modifying(clearAutomatically = true) 로 벌크성 수정 시 영속성 컨텍스트를 초기

    // then
    assertThat(resultCount).isEqualTo(3);
  }

  @Test
  public void findMemberLazy() {
    // given
    // member1 -> teamA
    // member2 -> teamB

    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    teamRepository.save(teamA);
    teamRepository.save(teamB);

    Member member1 = new Member("member1", 10, teamA);
    Member member2 = new Member("member2", 10, teamB);
    memberRepository.save(member1);
    memberRepository.save(member2);

    em.flush(); // db 반영
    em.clear(); // 컨텍스트 초기화

    // when
    List<Member> members = memberRepository.findAll();

    for (Member member : members) {
      System.out.println("member = " + member.getUsername());
      System.out.println(
          "member.getTeam().getClass() = " + member.getTeam().getClass()); // 실제 객체가 아닌 프록시 객체
      System.out.println("member.team = " + member.getTeam()); // team 을 지연 로딩으로 조회 -> N+1 문제 발생
    }
  }

  @Test
  public void findMemberFetchJoin() {
    // given
    // member1 -> teamA
    // member2 -> teamB

    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    teamRepository.save(teamA);
    teamRepository.save(teamB);

    Member member1 = new Member("member1", 10, teamA);
    Member member2 = new Member("member2", 10, teamB);
    memberRepository.save(member1);
    memberRepository.save(member2);

    em.flush(); // db 반영
    em.clear(); // 컨텍스트 초기화

    // when
    // List<Member> members = memberRepository.findMemberFetchJoin();
    // List<Member> members = memberRepository.findAll();
    List<Member> members = memberRepository.findEntityGraphByUsername("member1");

    for (Member member : members) {
      System.out.println("member = " + member.getUsername());
      System.out.println(
          "member.getTeam().getClass() = "
              + member.getTeam().getClass()); // 프록시로 채워져 있지 않고 실제 객체로 채워져 있음
      System.out.println(
          "member.team = "
              + member.getTeam()); // Member 조회 시 team 을 fetch join 으로 같이 가져오므로 추가 쿼리 없음
    }
  }
}
