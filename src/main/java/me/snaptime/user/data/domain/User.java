package me.snaptime.user.data.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.common.domain.BaseTimeEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseTimeEntity implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_name",nullable = false)
    private String name;

    @Column(name = "user_loginId",nullable = false, unique = true)
    private String loginId;

    //메서드의 프로퍼티를 JSON 직렬화에서 제외하도록 지정합니다.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "user_password",nullable = false)
    private String password;

    @Column(name = "user_email",nullable = false)
    private String email;

    @Column(name = "user_birthDay",nullable = false)
    private String birthDay;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private ProfilePhoto profilePhoto;

    //연관관계가 아닌 값 타입(value type)을 사용할 때 사용된다.
    //엔티티 내에 List,Set,Map과 같은 컬렉션이 있는 경우 해당 컬렉션의 요소가,
    //별도의 테이블에 저장 될 필요가 있을 때 사용한다.
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @Builder
    protected User(String name,String loginId,String password, String email, String birthDay, List<String> roles){
        this.name = name;
        this.loginId = loginId;
        this.password =password;
        this.email = email;
        this.birthDay = birthDay;
        this.roles = roles;
    }

    public void updateUserName(String name) { this.name = name;}
    public void updateUserLoginId(String loginId) { this.loginId = loginId;}
    public void updateUserEmail(String email) { this.email = email;}
    public void updateUserBirthDay(String birthDay) { this.birthDay = birthDay;}


    //UserDetails 인터페이스를 구현한 클래스에서 사용자의 권한을 반환하는 메서드
    //Spring Security에서 사용자의 권한은 GrantedAuthority 객체의 컬렉션으로 표현
    //GrantedAuthority는 사용자가 가지고 있는 권한을 나타내는 인터페이스이며, 대표적으로 SimpleGrantedAuthority 클래스가 이를 구현
    //해당 메서드는 roles 리스트에 저장된 각 권한을 SimpleGrantedAuthority로 매핑하고, 그 결과를 컬렉션으로 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    // 사용자의 loginId를 반환하는 메서드,
    //일반적으로 외부에 노출되어도 되는 정보이기 때문에 JsonProperty.Access.WRITE_ONLY 가 필요하지 않다.
    @Override
    public String getUsername() {
        return this.loginId;
    }

    //사용자 계정의 만료 여부
    //false를 반환하면 사용자 계정이 만료되었다는 뜻
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //사용자 계정이 잠겨있는지 여부
    //false를 반환하면 계정이 잠겨있다는 뜻
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //사용자의 자격증명(패스워드 등) 만료 여부
    //false를 반환하면 자격 증명이 만료되었다는 뜻
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //사용자의 계정 활성화 여부
    //false를 반환하면 계정이 비활성화 되었다는 뜻
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isEnabled() {
        return true;
    }
}
