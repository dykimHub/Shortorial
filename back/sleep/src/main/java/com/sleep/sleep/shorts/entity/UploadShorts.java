package com.sleep.sleep.shorts.entity;

import com.sleep.sleep.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "upload_shorts")
@Entity
public class UploadShorts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int uploadNo;

    @ManyToOne
    @JoinColumn(name = "member_no")
    private Member memberIndex;
    private String uploadUrl;
    private String uploadTitle;
    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadDate;
    @Column(nullable = true)
    private String youtubeUrl;


//    @OneToOne
//    @JoinColumn(name = "sns_no")
//    private UploadSns snsNo;

    @Builder
    public UploadShorts(Member memberIndex, String uploadUrl, String uploadTitle) {
        if (memberIndex != null) {
            this.memberIndex = memberIndex;
        }
        this.uploadUrl = uploadUrl;
        this.uploadTitle = uploadTitle;
        this.uploadDate = LocalDateTime.now(); // 현재 날짜와 시간 할당
    }

    public void update(String newTitle, String newUrl) {
        this.uploadTitle = newTitle;
        this.uploadUrl = newUrl;
    }

    public void putYoutubeUrl(String url){
        this.youtubeUrl=url;
    }

    @PrePersist
    protected void onCreate() {
        this.uploadDate = LocalDateTime.now();
    }

}
