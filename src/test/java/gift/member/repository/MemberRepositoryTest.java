package gift.member.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.member.builder.MemberBuilder;
import gift.member.entity.Member;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void saveMember() {
        Member expected = MemberBuilder.aMember().build();
        Member actual = memberRepository.save(expected);
        assertAll(
            () -> assertThat(actual.getMemberId()).isNotNull(),
            () -> assertThat(actual.getEmail()).isEqualTo(expected.getEmail()),
            () -> assertThat(actual.getPassword()).isEqualTo(expected.getPassword()),
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getRole()).isEqualTo(expected.getRole())
        );
    }

    @Test
    void findMemberByEmail() {
        String expected = "one@email.com";
        memberRepository.save(MemberBuilder.aMember().withEmail(expected).build());

        String actual = memberRepository.findByEmail(expected).get().getEmail();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAllMembers() {
        memberRepository.save(MemberBuilder.aMember().withEmail("one@email.com").build());
        memberRepository.save(MemberBuilder.aMember().withEmail("two@email.com").build());
        memberRepository.save(MemberBuilder.aMember().withEmail("three@email.com").build());

        List<Member> memberList = memberRepository.findAll();

        assertThat(memberList).hasSize(3);
    }

    @Test
    void findMemberById() {
        Member expected = MemberBuilder.aMember().build();
        Member savedMember = memberRepository.save(expected);

        Member actual = memberRepository.findById(savedMember.getMemberId()).get();
        assertAll(
            () -> assertThat(actual.getMemberId()).isNotNull(),
            () -> assertThat(actual.getEmail()).isEqualTo(expected.getEmail()),
            () -> assertThat(actual.getPassword()).isEqualTo(expected.getPassword()),
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getRole()).isEqualTo(expected.getRole())
        );
    }

    @Test
    void updateMember() {
    }

    @Test
    void deleteMember() {
        Member expected = MemberBuilder.aMember().build();
        Member savedMember = memberRepository.save(expected);

        memberRepository.delete(savedMember);

        assertThat(memberRepository.findById(savedMember.getMemberId())).isEmpty();
    }

    @Test
    void existsByEmail() {
        Member expected = MemberBuilder.aMember().build();
        Member savedMember = memberRepository.save(expected);

        assertThat(memberRepository.existsByEmail(savedMember.getEmail())).isTrue();
    }
}
