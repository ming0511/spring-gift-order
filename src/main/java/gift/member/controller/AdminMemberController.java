package gift.member.controller;

import gift.member.dto.AdminMemberCreateRequestDto;
import gift.member.dto.AdminMemberGetResponseDto;
import gift.member.dto.AdminMemberUpdateRequestDto;
import gift.member.dto.MemberCreateCommand;
import gift.member.dto.MemberUpdateCommand;
import gift.member.service.MemberService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/members")
public class AdminMemberController {

    private final MemberService memberService;

    public AdminMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 추가
    @GetMapping("/create")
    public String createMemberPage() {
        return "member/create-member";
    }

    @PostMapping("/create")
    public String createMember(
        @Valid @ModelAttribute AdminMemberCreateRequestDto requestDto,
        BindingResult bindingResult, Model model
    ) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "member/create-member";
        }

        MemberCreateCommand dto = new MemberCreateCommand(requestDto.email(), requestDto.password(),
            requestDto.name(), requestDto.role());

        try {
            memberService.saveMember(dto);
            return "redirect:/admin/members";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/create-member";
        }
    }

    // 조회
    @GetMapping
    public String getMembersPage(Model model) {

        List<AdminMemberGetResponseDto> members = memberService.findAllMembers();
        model.addAttribute("members", members);
        return "member/members";
    }

    @GetMapping("/search")
    public String getMemberById(@RequestParam Long memberId, Model model) {

        try {
            AdminMemberGetResponseDto member = memberService.findMemberById(memberId);
            model.addAttribute("members", List.of(member));
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }

        return "member/members";
    }

    // 수정
    @GetMapping("/update/{memberId}")
    public String updateMemberPage(@PathVariable Long memberId, Model model) {

        try {
            AdminMemberGetResponseDto member = memberService.findMemberById(memberId);
            model.addAttribute("member", member);
            return "member/update-member";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/members";
        }
    }

    @PostMapping("/update/{memberId}")
    public String updateMemberById(@PathVariable Long memberId,
        @Valid @ModelAttribute AdminMemberUpdateRequestDto requestDto,
        BindingResult bindingResult,
        Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute("memberUpdateRequestDto", requestDto);
            return "member/update-member";
        }

        MemberUpdateCommand dto = new MemberUpdateCommand(requestDto.email(), requestDto.password(),
            requestDto.name(), requestDto.role());

        try {
            memberService.updateMember(memberId, dto);
            return "redirect:/admin/members";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/update-member";
        }
    }

    // 삭제
    @PostMapping("/delete/{memberId}")
    public String deleteMemberById(@PathVariable Long memberId, Model model) {

        try {
            memberService.deleteMember(memberId);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/members";
    }

}
