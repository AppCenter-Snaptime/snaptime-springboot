package me.snaptime.snap.data.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.common.domain.BaseTimeEntity;

@Entity
@Getter
@Table(name = "photo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Photo extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String fileType;

    @Builder
    protected Photo(Long id, String fileName, String filePath, String fileType) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
    }
}
