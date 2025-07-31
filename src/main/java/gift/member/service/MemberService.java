package gift.member.service;

import gift.member.dto.AdminMemberGetResponseDto;
import gift.member.dto.MemberCreateCommand;
import gift.member.dto.MemberUpdateCommand;
import gift.member.dto.RegisterCommand;
import gift.member.dto.TokenResponseDto;
import gift.member.entity.Member;
import java.util.List;

public interface MemberService {

    TokenResponseDto registerMember(RegisterCommand dto);

    void findMemberByEmail(String email);

    void saveMember(MemberCreateCommand dto);

    List<AdminMemberGetResponseDto> findAllMembers();

    Member findMemberById(Long memberId);

    void updateMember(Long memberId, MemberUpdateCommand dto);

    void deleteMember(Long memberId);
}
