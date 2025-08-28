package com.dabom.playlist.service;

import com.dabom.member.model.entity.Member;
import com.dabom.member.repository.MemberRepository;
import com.dabom.playlist.model.dto.AddVideoDto;
import com.dabom.playlist.model.dto.PlaylistRegisterDto;
import com.dabom.playlist.model.dto.PlaylistUpdateDto;
import com.dabom.playlist.model.dto.playlistResponseDto;
import com.dabom.playlist.model.entity.Playlist;
import com.dabom.playlist.model.entity.PlaylistItem;
import com.dabom.playlist.repository.PlaylistRepository;
import com.dabom.playlist.repository.playlistItemRepository;
import com.dabom.video.model.Video;
import com.dabom.video.repository.VideoRepository;
import jakarta.persistence.EntityNotFoundException;
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
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다: " + memberIdx));

        Playlist playlist = Playlist.builder()
                .playlistTitle(dto.getPlaylistTitle())
                .member(member)
                .build();
        return playlistRepository.save(playlist).getIdx();
    }

    public void add(AddVideoDto dto, Integer memberIdx) {
        Playlist playlist = playlistRepository.findById(dto.getPlaylistIdx())
                .orElseThrow(() -> new EntityNotFoundException("플레이리스트를 찾을 수 없습니다" + memberIdx));

        Video video = videoRepository.findById(dto.getVideoIdx())
                .orElseThrow(() -> new EntityNotFoundException("비디오를 찾을 수 없습니다"));

        if (!playlist.getMember().getIdx().equals(memberIdx)) {
            throw new SecurityException("영상을 추가 할 수 없습니다");

        }

        if (playlistItemRepository.existsByPlaylistAndVideo(playlist, video)) {
            throw new IllegalStateException("이미 플레이리스트에 추가된 영상입니다.");
        }

        playlistItemRepository.save(new PlaylistItem(playlist, video));
    }


    public void delete(Integer playlistIdx, Integer memberIdx) {
        Playlist playlist = playlistRepository.findById(playlistIdx)
                .orElseThrow(() -> new EntityNotFoundException("플레이리스트를 찾을 수 없습니다."));

        if (!playlist.getMember().getIdx().equals(memberIdx)) {
            throw new SecurityException("플레이리스트를 삭제할 권한이 없습니다.");
        }
        List<PlaylistItem> itemsToDelete = playlistItemRepository.findAllByPlaylist(playlist);

        playlistItemRepository.deleteAll(itemsToDelete);

        playlistRepository.delete(playlist);
    }

    public List<playlistResponseDto> list(Integer memberidx) {
        Member member = memberRepository.findById(memberidx)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다: " + memberidx));

        List<Playlist> playlists = playlistRepository.findAllByMember(member);

        return playlists.stream()
                .map(playlistResponseDto::from)
                .toList();
    }

    public Integer update(PlaylistUpdateDto dto, Integer playlistIdx) {
        Playlist entity = playlistRepository.findById(playlistIdx)
                .orElseThrow(() -> new EntityNotFoundException("플레이 리스트를 찾을 수 없습니다: " + playlistIdx));

        dto.toEntity(entity);
        playlistRepository.save(entity);

        return playlistRepository.save(entity).getIdx();
    }
}
