package com.spq.vinted.service;

import com.spq.vinted.model.Ad;
import com.spq.vinted.repository.AdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdServiceTest {

    @InjectMocks
    private AdService adService;

    @Mock
    private AdRepository adRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAds_ReturnsAdsList() {
        List<Ad> ads = Arrays.asList(new Ad(), new Ad());
        when(adRepository.findAll()).thenReturn(ads);

        List<Ad> result = adService.getAllAds();

        assertEquals(2, result.size());
        verify(adRepository).findAll();
    }

    @Test
    void testGetAdById_ExistingId_ReturnsAd() {
        Ad ad = new Ad();
        when(adRepository.findById(1L)).thenReturn(Optional.of(ad));

        Ad result = adService.getAdById(1L);

        assertNotNull(result);
        verify(adRepository).findById(1L);
    }

    @Test
    void testGetAdById_NotFound_ThrowsException() {
        when(adRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            adService.getAdById(1L);
        });

        assertEquals("Ad not found", ex.getMessage());
    }

    @Test
    void testSaveAd_SavesAndReturnsAd() {
        Ad ad = new Ad();
        when(adRepository.save(ad)).thenReturn(ad);

        Ad result = adService.saveAd(ad);

        assertEquals(ad, result);
        verify(adRepository).save(ad);
    }

    @Test
    void testUploadAdImage_ReturnsUniqueFileName() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                "dummy content".getBytes()
        );

        String fileName = adService.uploadAdImage(multipartFile);

        assertTrue(fileName.contains("image.jpg"));
    }

    @Test
    void testUploadAdImage_WithAdId_SavesImageUrl() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                "dummy content".getBytes()
        );

        Ad ad = new Ad();
        when(adRepository.findById(1L)).thenReturn(Optional.of(ad));
        when(adRepository.save(any())).thenReturn(ad);

        adService.uploadAdImage(1L, multipartFile);

        assertNotNull(ad.getImageUrl());
        assertTrue(ad.getImageUrl().contains("image.jpg"));
        verify(adRepository).save(ad);
    }

    @Test
    void testUploadAdImage_WithAdId_AdNotFound_ThrowsException() {
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                "dummy content".getBytes()
        );

        when(adRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            adService.uploadAdImage(1L, multipartFile);
        });

        assertEquals("Ad not found", ex.getMessage());
    }

}

