package com.dabom.video.model.dto;


import com.dabom.video.model.VideoTag;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoMetadataRequestDto {

    private Integer idx;
    private String title;
    private String description;

    @JsonProperty("isPublic")
    private boolean isPublic;

    private VideoTag videoTag;
}
