package gift.member.service;

import gift.exception.member.EmailAlreadyExistsException;
import gift.exception.member.MemberNotFoundException;
import gift.member.dto.AdminMemberGetResponseDto;
import gift.member.dto.MemberCreateCommand;
import gift.member.dto.MemberUpdateCommand;
import gift.member.dto.RegisterCommand;
import gift.member.dto.TokenResponseDto;
import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import gift.member.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public TokenResponseDto registerMember(RegisterCommand dto) {
        if (memberRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException("이미 사용 중인 이메일입니다.");
        }

        Member member = new Member(dto.email(), dto.password(), dto.name(), dto.role());

        Member savedMember = memberRepository.save(member);

        String token = new JwtTokenProvider().generateToken(savedMember.getMemberId(),
            savedMember.getName(),
            savedMember.getRole());

        return new TokenResponseDto(token);
    }

    @Override
    public void findMemberByEmail(String email) {

        try {
            memberRepository.findByEmail(email);
        } catch (EmptyResultDataAccessException e) {
            throw new MemberNotFoundException("이메일이 존재하지 않습니다. email =" + email);
        }
    }

    @Override
    public void saveMember(MemberCreateCommand dto) {

        Member member = new Member(dto.email(), dto.password(), dto.name(), dto.role());

        memberRepository.save(member);
    }

    @Override
    public List<AdminMemberGetResponseDto> findAllMembers() {
        List<Member> members = memberRepository.findAll();

        return members.stream()
            .map(member -> new AdminMemberGetResponseDto(
                member.getMemberId(),
                member.getEmail(),
                member.getPassword(),
                member.getName(),
                member.getRole()
            ))
            .collect(Collectors.toList());
    }

    @Override
    public AdminMemberGetResponseDto findMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        return new AdminMemberGetResponseDto(member.getMemberId(), member.getEmail(),
            member.getPassword(), member.getName(), member.getRole());
    }

    @Override
    @Transactional
    public void updateMember(Long memberId, MemberUpdateCommand dto) {

        Member member = new Member(memberId, dto.email(), dto.password(), dto.name(), dto.role());

        update(memberId, member);
    }

    @Override
    public void deleteMember(Long memberId) {
        memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        memberRepository.deleteById(memberId);
    }

    public void update(Long id, Member member) {
        Member foundMember = memberRepository.findById(id)
            .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        foundMember.changeEmail(member.getEmail());
        foundMember.changePassword(member.getPassword());
        foundMember.rename(member.getName());
        foundMember.assignRole(member.getRole());
    }
}
