package com.reservemate.reserve_mate_backend.user.domain;

import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE user_id = ?")
@Where(clause = "deleted = false")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "profile_image")
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Builder(toBuilder = true)
    public User(
            String name,
            String email,
            String password,
            String phone,
            String profileImage,
            UserRole role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.profileImage = profileImage;
        this.role = role != null ? role : UserRole.ROLE_USER;
    }

    public void updateProfile(String name, String phone, String profileImage) {
        this.name = name != null ? name : this.name;
        this.phone = phone != null ? phone : this.phone;
        this.profileImage = profileImage != null ? profileImage : this.profileImage;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
