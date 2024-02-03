package me.snaptime.user.data.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name",nullable = false)
    private String name;

    @Column(name="user_loginId",nullable = false,unique = true)
    private String loginId;

    @Column(name = "user_password",nullable = false)
    private String password;

    @Column(name = "user_email",nullable = false)
    private String email;

    @Column(name = "user_birthDay",nullable = false)
    private String birthDay;

    //일대일관계에서 대상 테이블에 외래 키를 저장하는 단방향 관계는 JPA에서 지원하지 않는다.
    @OneToOne(mappedBy = "user",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private ProfilePhoto profilePhoto;

//  @OneToOne(mappedBy = "user",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
//  private Snap snap;


    @Builder
    protected User(Long userId,String name, String loginId, String password, String email,String birthDay)
    {
        this.userId = userId;
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.birthDay = birthDay;
    }
}
