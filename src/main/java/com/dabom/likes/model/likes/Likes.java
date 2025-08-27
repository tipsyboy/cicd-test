package com.dabom.likes.model.likes;

import com.dabom.boardcomment.model.entity.BoardComment;
import com.dabom.channelboard.model.entity.ChannelBoard;
import com.dabom.member.model.entity.Member;
import com.dabom.video.model.Video;
import com.dabom.videocomment.model.entity.VideoComment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    private Member channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_comment_idx")
    private VideoComment videoComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channelBoard_idx")
    private ChannelBoard channelBoard ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boardComment_idx")
    private BoardComment boardComment;


}
