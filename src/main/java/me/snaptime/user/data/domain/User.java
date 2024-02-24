package me.snaptime.user.data.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.common.domain.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long Id;

    @Column(name = "user_name",nullable = false)
    private String name;

    @Column(name = "user_loginId",nullable = false, unique = true)
    private String loginId;

    @Column(name = "user_password",nullable = false)
    private String password;

    @Column(name = "user_email",nullable = false)
    private String email;

    @Column(name = "user_birthDay",nullable = false)
    private String birthDay;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private ProfilePhoto profilePhoto;

    @Builder
    protected User(Long Id, String name,String loginId,String password, String email, String birthDay){
        this.Id = Id;
        this.name = name;
        this.loginId = loginId;
        this.password =password;
        this.email = email;
        this.birthDay = birthDay;
    }

    public void updateUserName(String name) { this.name = name;}
    public void updateUserLoginId(String loginId) { this.loginId = loginId;}
    public void updateUserEmail(String email) { this.email = email;}
    public void updateUserBirthDay(String birthDay) { this.birthDay = birthDay;}

}
