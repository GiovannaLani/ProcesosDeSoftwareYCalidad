package com.spq.vinted.service;

import com.spq.vinted.model.Ad;
import com.spq.vinted.repository.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class AdService {

    @Autowired
    private AdRepository adRepository;

    public List<Ad> getAllAds() {
        return adRepository.findAll();
    }

    public Ad getAdById(long id) {
        return adRepository.findById(id).orElseThrow(() -> new RuntimeException("Ad not found"));
    }

    public Ad saveAd(Ad ad) {
        return adRepository.save(ad);
    }

    public String uploadAdImage(MultipartFile imageFile) throws IOException {
        String uploadDir = "uploads/ads/";

        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        String uniqueFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();

        Path filePath = Paths.get(uploadDir).resolve(uniqueFileName).toAbsolutePath();

        Files.copy(imageFile.getInputStream(), filePath);

        return uniqueFileName;
    }

    public void uploadAdImage(long adId, MultipartFile imageFile) throws IOException {
        String uploadDir = "uploads/ads/";
        Ad ad = adRepository.findById(adId).orElseThrow(() -> new RuntimeException("Ad not found"));

        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        String uniqueFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
        Path filePath = Paths.get(uploadDir).resolve(uniqueFileName).toAbsolutePath();

        Files.copy(imageFile.getInputStream(), filePath);

        ad.setImageUrl(uniqueFileName);
        adRepository.save(ad);
    }
}