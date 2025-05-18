package com.spq.vinted.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.spq.vinted.dto.AdDTO;
import com.spq.vinted.model.Ad;
import com.spq.vinted.service.AdService;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdControllerTest {

    @Mock
    private AdService adService;

    @InjectMocks
    private AdController adController;

    private final Ad validAd = new Ad("Test Title", "Test Description");
    private final AdDTO validAdDTO = new AdDTO("Test Title", "Test Description");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validAd.setId(1L);
    }

    @Test
    void getAllAds_Success() {
        List<Ad> mockAds = Arrays.asList(
            new Ad("Ad 1", "Desc 1"),
            new Ad("Ad 2", "Desc 2")
        );
        
        when(adService.getAllAds()).thenReturn(mockAds);
        
        ResponseEntity<List<Ad>> response = adController.getAllAds();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void getAllAds_InternalError() {
        when(adService.getAllAds()).thenThrow(new RuntimeException("Database error"));
        
        ResponseEntity<List<Ad>> response = adController.getAllAds();
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }


    @Test
    void getAdById_Found() {
        when(adService.getAdById(1L)).thenReturn(validAd);
        
        ResponseEntity<Ad> response = adController.getAdById(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Title", response.getBody().getTitle());
    }

    @Test
    void getAdById_NotFound() {
        when(adService.getAdById(99L)).thenReturn(null);
        
        ResponseEntity<Ad> response = adController.getAdById(99L);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getAdById_InternalError() {
        when(adService.getAdById(1L)).thenThrow(new RuntimeException("Service error"));
        
        ResponseEntity<Ad> response = adController.getAdById(1L);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }


    // @Test
    // void getAdImage_Success() throws Exception {
    //     Path imagePath = Paths.get("uploads/ads/test.jpg").toAbsolutePath();
    //     UrlResource resource = new UrlResource(imagePath.toUri());
        
    //     ResponseEntity<Resource> response = adController.getAdImage("test.jpg");
        
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    //     assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
    // }

    @Test
    void getAdImage_NotFound() throws Exception {
        ResponseEntity<Resource> response = adController.getAdImage("nonexistent.jpg");
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    void uploadAdData_Success() {
        when(adService.saveAd(any(Ad.class))).thenReturn(validAd);
        
        ResponseEntity<Long> response = adController.uploadAdData(123L, validAdDTO);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody());
    }

    @Test
    void uploadAdData_Failure() {
        when(adService.saveAd(any(Ad.class))).thenReturn(null);
        
        ResponseEntity<Long> response = adController.uploadAdData(123L, validAdDTO);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    @Test
    void updateAdImage_Success() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);
        
        ResponseEntity<Void> response = adController.updateAdImage(1L, mockFile);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(adService).uploadAdImage(1L, mockFile);
    }

    @Test
    void updateAdImage_NotFound() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);
        doThrow(new RuntimeException("Ad not found")).when(adService).uploadAdImage(99L, mockFile);
        
        ResponseEntity<Void> response = adController.updateAdImage(99L, mockFile);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateAdImage_InternalError() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);
        doThrow(new IOException("Storage error")).when(adService).uploadAdImage(1L, mockFile);
        
        ResponseEntity<Void> response = adController.updateAdImage(1L, mockFile);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
