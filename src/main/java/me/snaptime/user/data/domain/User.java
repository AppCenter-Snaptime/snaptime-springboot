package me.snaptime.user.data.domain;


import io.micrometer.core.annotation.Counted;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.common.domain.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long Id;

    @Column(name = "user_name",nullable = false)
    private String name;

    @Column(name = "user_logninId",nullable = false, unique = true)
    private String longinId;

    @Column(name = "user_password",nullable = false)
    private String password;

    @Column(name = "user_email",nullable = false)
    private String email;

    @Column(name = "user_birthDay",nullable = false)
    private String birthDay;

    @OneToOne(mappedBy = "userId",cascade = CascadeType.ALL)
    private ProfilePhoto profilePhoto;

    @Builder
    protected User(Long Id, String name,String loginId,String password, String email, String birthDay){
        this.Id = Id;
        this.name = name;
        this.longinId = loginId;
        this.password =password;
        this.email = email;
        this.birthDay = birthDay;
    }


}
