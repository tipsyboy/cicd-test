package com.dabom.report.service;

import com.dabom.boardcomment.exception.BoardCommentException;
import com.dabom.boardcomment.exception.BoardCommentExceptionType;
import com.dabom.boardcomment.model.entity.BoardComment;
import com.dabom.boardcomment.repository.BoardCommentRepository;
import com.dabom.channelboard.exception.ChannelBoardException;
import com.dabom.channelboard.exception.ChannelBoardExceptionType;
import com.dabom.channelboard.model.entity.ChannelBoard;
import com.dabom.channelboard.repositroy.ChannelBoardRepository;
import com.dabom.member.exception.MemberException;
import com.dabom.member.exception.MemberExceptionType;
import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.report.model.dto.ReportRegisterDto;
import com.dabom.report.model.entity.Report;
import com.dabom.report.repository.ReportRepository;
import com.dabom.video.exception.VideoException;
import com.dabom.video.exception.VideoExceptionType;
import com.dabom.video.model.Video;
import com.dabom.video.repository.VideoRepository;
import com.dabom.videocomment.model.entity.VideoComment;
import com.dabom.videocomment.repository.VideoCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final VideoRepository videoRepository;
    private final VideoCommentRepository videoCommentRepository;
    private final ChannelBoardRepository channelBoardRepository;
    private final BoardCommentRepository boardCommentRepository;

    @Transactional
    public void registerReport(ReportRegisterDto<Integer> reportDto, Integer reporterMemberIdx) {

        Member reporter = memberRepository.findById(reporterMemberIdx)
                .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Report.ReportBuilder reportBuilder = Report.builder()
                .reason(reportDto.getReason())
                .member(reporter)
                .reportcount(0);

        switch (reportDto.getTargetType()) {
            case "VIDEO":
                Video video = videoRepository.findById(reportDto.getTargetId())
                        .orElseThrow(() -> new VideoException(VideoExceptionType.VIDEO_NOT_FOUND));
                reportBuilder.video(video);
                break;
            case "VIDEO_COMMENT":
                VideoComment videoComment = videoCommentRepository.findById(reportDto.getTargetId())
                        .orElseThrow(() -> new IllegalArgumentException("Video comment not found with ID: " + reportDto.getTargetId()));
                reportBuilder.videoComment(videoComment);
                break;
            case "CHANNEL_BOARD":
                ChannelBoard channelBoard = channelBoardRepository.findById(reportDto.getTargetId())
                        .orElseThrow(() -> new ChannelBoardException(ChannelBoardExceptionType.BOARD_NOT_FOUND));
                reportBuilder.channelBoard(channelBoard);
                break;
            case "BOARD_COMMENT":
                BoardComment boardComment = boardCommentRepository.findById(reportDto.getTargetId())
                        .orElseThrow(() -> new BoardCommentException(BoardCommentExceptionType.BOARD_NOT_FOUND_FOR_COMMENT));
                reportBuilder.boardComment(boardComment);
                break;
            default:
                throw new IllegalArgumentException("Invalid report target type: " + reportDto.getTargetType());
        }

        reportRepository.save(reportBuilder.build());
    }
}