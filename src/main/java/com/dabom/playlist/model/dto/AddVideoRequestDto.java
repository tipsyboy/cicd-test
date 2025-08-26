package com.dabom.playlist.model.dto;

import lombok.Getter;
import lombok.Setter;

// 어떤 플레이리스트에 어떤 동영상을 추가 할건지

@Getter
@Setter
public class AddVideoRequestDto {
    private Integer playlistIdx;
    private Integer videoIdx;
}
