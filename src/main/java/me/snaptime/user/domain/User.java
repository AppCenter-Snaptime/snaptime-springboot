package me.snaptime.user.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.common.BaseTimeEntity;
import me.snaptime.profilePhoto.domain.ProfilePhoto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
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
    private Long userId;

    @Column(name = "user_name",nullable = false)
    private String name;

    @Column(name = "user_nick_name")
    private String nickName;

    @Column(name = "user_email",nullable = false, unique = true)
    private String email;

    //메서드의 프로퍼티를 JSON 직렬화에서 제외하도록 지정합니다.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "user_password",nullable = false)
    private String password;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_photo_id")
    private ProfilePhoto profilePhoto;

    @Column(name = "user_penalty", nullable = false)
    private int penalty;

    @Column(name = "ban_end_time")
    private LocalDateTime banEndTime;

    //연관관계가 아닌 값 타입(value type)을 사용할 때 사용된다.
    //엔티티 내에 List,Set,Map과 같은 컬렉션이 있는 경우 해당 컬렉션의 요소가,
    //별도의 테이블에 저장 될 필요가 있을 때 사용한다.
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @Builder
    protected User(String name,String nickName, String email, String password,  List<String> roles, ProfilePhoto profilePhoto){
        this.name = name;
        this.nickName = nickName;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.profilePhoto = profilePhoto;
    }

    public void updateUserName(String name) { this.name = name;}
    public void updateNickName(String nickName){this.nickName = nickName;}
    public void updateUserPassword(String password){this.password = password;}
    public void updateAdminAuth(){this.roles = List.of("ROLE_ADMIN");}
    public void updateBenUserAuth(){this.roles=List.of("ROLE_BEN");}
    public void addPenalty(int penaltyPoint){this.penalty = this.penalty + penaltyPoint;}
    public void clearPenalty(){this.penalty = 0;}

    public void setBanEndTime(LocalDateTime endTime) {
        this.banEndTime = endTime;
    }

    public boolean isBanPeriodOver() {
        return this.banEndTime != null && LocalDateTime.now().isAfter(this.banEndTime);
    }

    public void restoreRole() {
        this.roles.clear(); // 기존 역할 제거
        this.roles = List.of("ROLE_USER");
        this.banEndTime = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
    @Override
    public String getUsername() {
        return this.email;
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
