package com.flipdeal.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.flipdeal.demo.dto.ProductRequest;
import com.flipdeal.demo.dto.ProductResponse;
import com.flipdeal.demo.dto.ProductUpdateReq;
import com.flipdeal.demo.entity.ProductEntity;
import com.flipdeal.demo.repository.ProductRepository;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductServiceImpl {

    @Autowired
    private S3Client s3Client;
    @Autowired
    private ProductRepository productRepository;

    @Value("${aws.s3.bucketname}")
    private String bucketName;

    public String uploadFile(MultipartFile file) {
        log.info("ProductServiceImpl: uploadFile called for file: {}", file.getOriginalFilename());
        String filenameExtension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        String key = UUID.randomUUID().toString() + "." + filenameExtension;
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .acl("public-read")
                    .contentType(file.getContentType())
                    .build();
            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            if (response.sdkHttpResponse().isSuccessful()) {
                log.info("File uploaded successfully: {}", key);
                return "https://" + bucketName + ".s3.amazonaws.com/" + key;
            } else {
                log.error("File upload failed for: {}", key);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed");
            }
        } catch (IOException ex) {
            log.error("An error occurred while uploading the file: {}", file.getOriginalFilename());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occured while uploading the file");
        }
    }

    public ProductResponse addProduct(ProductRequest request, MultipartFile file) {
        log.info("ProductServiceImpl: addProduct called for product: {}", request.getName());
        ProductEntity newProductEntity = convertToEntity(request);
        String imageUrl = uploadFile(file);
        newProductEntity.setImageUrl(imageUrl);
        newProductEntity = productRepository.save(newProductEntity);
        log.info("Product saved with id: {}", newProductEntity.getId());
        return convertToResponse(newProductEntity);
    }

    public List<ProductResponse> readProducts() {
        log.info("ProductServiceImpl: readProducts called");
        List<ProductEntity> databaseEntries = productRepository.findAll();
        return databaseEntries.stream().map(object -> convertToResponse(object)).collect(Collectors.toList());
    }

    public ProductResponse readProduct(UUID id) {
        log.info("ProductServiceImpl: readProduct called for id: {}", id);
        ProductEntity existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found for the id:" + id));
        return convertToResponse(existingProduct);
    }

    public boolean deleteFile(String filename) {
        log.info("ProductServiceImpl: deleteFile called for filename: {}", filename);
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
        log.info("File deleted: {}", filename);
        return true;
    }

    public void deleteProduct(UUID id) {
        log.info("ProductServiceImpl: deleteProduct called for id: {}", id);
        ProductResponse response = readProduct(id);
        String imageUrl = response.getImageUrl();
        String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        boolean isFileDelete = deleteFile(filename);
        if (isFileDelete) {
            productRepository.deleteById(response.getId());
            log.info("Product deleted with id: {}", response.getId());
        }
    }

    public List<ProductResponse> searchProducts(String keyword) {
        log.info("ProductServiceImpl: searchProducts called with keyword: {}", keyword);
        List<ProductEntity> matchedProducts = productRepository.searchByKeyword(keyword);
        return matchedProducts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getProductsByCategory(String category) {
        log.info("ProductServiceImpl: getProductsByCategory called with category: {}", category);
        List<ProductEntity> entities = productRepository.findByCategoryIgnoreCase(category);
        System.out.println("Filtering category: " + category);
        productRepository.findByCategoryIgnoreCase(category)
                .forEach(p -> System.out.println("Matched Product Category: " + p.getCategory()));

        return entities.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public ProductResponse updateProductAvailability(UUID id, Boolean isAvailable) {
        log.info("ProductServiceImpl: updateProductAvailability called for id: {}, isAvailable: {}", id, isAvailable);
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setIsAvailable(isAvailable);
        productRepository.save(product);
        log.info("Product availability updated for id: {}", id);
        return convertToResponse(product);
    }

    public ProductUpdateReq updateProduct(UUID id, ProductUpdateReq productUpdateReq) {
        log.info("ProductServiceImpl: updateProduct called for id: {}", id);
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        product.setName(productUpdateReq.getName());
        product.setDescription(productUpdateReq.getDescription());
        product.setPrice(productUpdateReq.getPrice());
        productRepository.save(product);
        log.info("Product updated for id: {}", id);
        return convertToUpdateResponse(product);
    }

    public List<ProductResponse> searchProductsByCategory(String keyword, String category) {
        log.info("ProductServiceImpl: searchProductsByCategory called with keyword: {}, category: {}", keyword,
                category);
        List<ProductEntity> matched = productRepository.searchByKeywordAndCategory(keyword, category);
        return matched.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private ProductUpdateReq convertToUpdateResponse(ProductEntity productEntity) {
        return ProductUpdateReq.builder()
                .id(productEntity.getId())
                .name(productEntity.getName())
                .description(productEntity.getDescription())
                .price(productEntity.getPrice())
                .build();
    }

    private ProductEntity convertToEntity(ProductRequest request) {
        return ProductEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isAvailable(true)
                .category(request.getCategory())
                .price(request.getPrice())
                .build();

    }

    private ProductResponse convertToResponse(ProductEntity entity) {
        return ProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .isAvailable(entity.getIsAvailable())
                .category(entity.getCategory())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .build();
    }

}
