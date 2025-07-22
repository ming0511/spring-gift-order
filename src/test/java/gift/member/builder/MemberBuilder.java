package gift.member.builder;

import gift.member.Role;
import gift.member.entity.Member;

public class MemberBuilder {

    private String email = "default@email.com";
    private String password = "default";
    private String name = "user";
    private Role role = Role.USER;

    private MemberBuilder() {

    }

    public static MemberBuilder aMember() {
        return new MemberBuilder();
    }

    public MemberBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public MemberBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public MemberBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public MemberBuilder withRole(Role role) {
        this.role = role;
        return this;
    }

    public Member build() {
        return new Member(email, password, name, role);
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }
}
