package com.kakaoseventeen.dogwalking.review.domain;

import com.kakaoseventeen.dogwalking.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "REVIEW_tb")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(nullable = false)
    private String reviewContent;

    @Column(nullable = false)
    private boolean reviewEval1;
    @Column(nullable = false)
    private boolean reviewEval2;
    @Column(nullable = false)
    private boolean reviewEval3;
    @Column(nullable = false)
    private boolean reviewEval4;

    @Column(nullable = false)
    private Integer dogBowl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member reviewerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member receiverId;

    @Column(nullable = false)
    private boolean isReceiverDogOwner;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;


}
