package com.dabom.playlist.service;

import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.playlist.model.dto.AddVideoDto;
import com.dabom.playlist.model.dto.PlaylistInnerDto;
import com.dabom.playlist.model.dto.PlaylistRegisterDto;
import com.dabom.playlist.model.dto.PlaylistUpdateDto;
import com.dabom.playlist.model.dto.playlistResponseDto;
import com.dabom.playlist.model.entity.Playlist;
import com.dabom.playlist.model.entity.PlaylistItem;
import com.dabom.playlist.repository.PlaylistRepository;
import com.dabom.playlist.repository.playlistItemRepository;
import com.dabom.video.model.Video;
import com.dabom.video.repository.VideoRepository;
import com.dabom.playlist.exception.PlaylistException;
import com.dabom.playlist.exception.PlaylistExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final playlistItemRepository playlistItemRepository;
    private final MemberRepository memberRepository;
    private final VideoRepository videoRepository;


    @Transactional
    public Integer register(PlaylistRegisterDto dto, Integer memberIdx) {

        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new PlaylistException(PlaylistExceptionType.MEMBER_NOT_FOUND));

        Playlist playlist = Playlist.builder()
                .playlistTitle(dto.getPlaylistTitle())
                .member(member)
                .build();
        return playlistRepository.save(playlist).getIdx();
    }

    @Transactional
    public void add(AddVideoDto dto, Integer memberIdx) {
        Playlist playlist = playlistRepository.findById(dto.getPlaylistIdx())
                .orElseThrow(() -> new PlaylistException(PlaylistExceptionType.PLAYLIST_NOT_FOUND));

        Video video = videoRepository.findById(dto.getVideoIdx())
                .orElseThrow(() -> new PlaylistException(PlaylistExceptionType.VIDEO_NOT_FOUND));

        if (!playlist.getMember().getIdx().equals(memberIdx)) {
            throw new PlaylistException(PlaylistExceptionType.NO_PERMISSION_ADD_VIDEO);

        }

        if (playlistItemRepository.existsByPlaylistAndVideo(playlist, video)) {
            throw new PlaylistException(PlaylistExceptionType.VIDEO_ALREADY_IN_PLAYLIST);
        }

        playlistItemRepository.save(new PlaylistItem(playlist, video));
    }

    @Transactional
    public void delete(Integer playlistIdx, Integer memberIdx) {
        Playlist playlist = playlistRepository.findById(playlistIdx)
                .orElseThrow(() -> new PlaylistException(PlaylistExceptionType.PLAYLIST_NOT_FOUND));

        if (!playlist.getMember().getIdx().equals(memberIdx)) {
            throw new PlaylistException(PlaylistExceptionType.NO_PERMISSION_DELETE_PLAYLIST);
        }
        List<PlaylistItem> itemsToDelete = playlistItemRepository.findAllByPlaylist(playlist);

        playlistItemRepository.deleteAll(itemsToDelete);

        playlistRepository.delete(playlist);
    }

    public List<playlistResponseDto> list(Integer memberidx) {
        Member member = memberRepository.findById(memberidx)
                .orElseThrow(() -> new PlaylistException(PlaylistExceptionType.MEMBER_NOT_FOUND));

        List<Playlist> playlists = playlistRepository.findAllByMember(member);

        return playlists.stream()
                .map(playlistResponseDto::from)
                .toList();
    }

    @Transactional
    public void update(PlaylistUpdateDto dto, Integer playlistIdx, Integer memberIdx) {
        Playlist entity = playlistRepository.findById(playlistIdx)
                .orElseThrow(() -> new PlaylistException(PlaylistExceptionType.PLAYLIST_NOT_FOUND));

        if (!entity.getMember().getIdx().equals(memberIdx)) {
            throw new PlaylistException(PlaylistExceptionType.NO_PERMISSION_UPDATE_PLAYLIST);
        }

        entity.setPlaylistTitle(dto.getPlaylistTitle());
        playlistRepository.save(entity);
    }

    public PlaylistInnerDto getPlaylistDetails(Integer playlistIdx, Integer memberIdx) {
        Playlist playlist = playlistRepository.findById(playlistIdx)
                .orElseThrow(() -> new PlaylistException(PlaylistExceptionType.PLAYLIST_NOT_FOUND));

        if (!playlist.getMember().getIdx().equals(memberIdx)) {
            throw new PlaylistException(PlaylistExceptionType.NO_PERMISSION_VIEW_PLAYLIST);
        }

        List<Video> videos = playlistItemRepository.findVideosByPlaylist(playlist);

        return PlaylistInnerDto.from(playlist, videos);
    }
}


