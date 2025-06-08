package org.cosmetic.com.service.impl;

import lombok.AllArgsConstructor;
import org.cosmetic.com.model.ImageUrl;
import org.cosmetic.com.repository.ImageUrlRepository;
import org.cosmetic.com.service.ImgUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class ImgUrlServiceImpl implements ImgUrlService {

    private final ImageUrlRepository imageUrlRepository;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    @Value(
"${cloudflare.r2.accountId}"
    )
    private String accountId;

    private final S3Client s3Client;

    @Autowired
    public ImgUrlServiceImpl(ImageUrlRepository imageUrlRepository, S3Client s3Client) {
        this.imageUrlRepository = imageUrlRepository;
        this.s3Client = s3Client;
    }

    @Override
    public ImageUrl saveImageUrl(ImageUrl imageUrl) {
        return imageUrlRepository.save(imageUrl);
    }

    @Override
    public ImageUrl getImageUrlById(Long id) {
        return imageUrlRepository.findById(id).orElse(null);
    }

    @Override
    public List<ImageUrl> getAllImageUrls() {
        return imageUrlRepository.findAll();
    }

    @Override
    public void deleteImageUrl(Long id) {
        imageUrlRepository.deleteById(id);
    }

    @Override
    public String saveImageInS2(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return "https://" + accountId + ".r2.cloudflarestorage.com/" + bucket + "/" + fileName;
    }
}