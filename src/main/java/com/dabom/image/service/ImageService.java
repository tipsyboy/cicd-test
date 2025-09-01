package com.dabom.image.service;

import com.dabom.image.exception.ImageException;
import com.dabom.image.model.dto.ImageUploadResponseDto;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {

    public ImageUploadResponseDto uploadSingleImage(MultipartFile file, String directory) throws ImageException;
    public List<ImageUploadResponseDto> uploadMultipleImages(List<MultipartFile> files, String directory) throws ImageException;

    public void deleteImage(@RequestParam Integer idx) throws ImageException;

    String find(Integer idx) throws ImageException;
}