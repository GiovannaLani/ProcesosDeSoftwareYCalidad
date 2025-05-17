package com.spq.vinted.controller;

import com.spq.vinted.dto.AdDTO;
import com.spq.vinted.model.Ad;
import com.spq.vinted.service.AdService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/ads")
public class AdController {

    @Autowired
    private AdService adService;

    @GetMapping
    public ResponseEntity<List<Ad>> getAllAds() {
        try {
            List<Ad> ads = adService.getAllAds();
            return ResponseEntity.ok(ads);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ad> getAdById(@PathVariable long id) {
        try {
            Ad ad = adService.getAdById(id);
            if (ad == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(ad);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getAdImage(@PathVariable String filename) throws MalformedURLException {
        Path imagePath = Paths.get("uploads/ads").resolve(filename).toAbsolutePath();
        Resource resource = new UrlResource(imagePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/adData")
    public ResponseEntity<Long> uploadAdData(
            @RequestParam("token") long token,
            @RequestBody AdDTO adDTO) {
        Ad ad = new Ad(adDTO.getTitle(), adDTO.getDescription(), null); 
        Ad savedAd = adService.saveAd(ad);

        if (savedAd == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(savedAd.getId());
    }

    @PutMapping(value = "/adImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateAdImage(
            @RequestParam("adId") long adId,
            @RequestPart("image") MultipartFile imageFile) {

        try {
            adService.uploadAdImage(adId, imageFile);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if ("Ad not found".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}