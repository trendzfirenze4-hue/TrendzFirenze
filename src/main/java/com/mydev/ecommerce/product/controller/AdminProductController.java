
package com.mydev.ecommerce.product.controller;

import com.mydev.ecommerce.category.repository.CategoryRepository;
import com.mydev.ecommerce.common.service.FileStorageService;
import com.mydev.ecommerce.product.dto.ProductRequest;
import com.mydev.ecommerce.product.dto.ProductReviewRequest;
import com.mydev.ecommerce.product.model.Product;
import com.mydev.ecommerce.product.model.ProductImage;
import com.mydev.ecommerce.product.model.ProductReview;
import com.mydev.ecommerce.product.repository.ProductRepository;
import com.mydev.ecommerce.product.repository.ProductReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final ProductReviewRepository reviewRepo;
    private final FileStorageService fileStorageService;

    public AdminProductController(ProductRepository productRepo,
                                  CategoryRepository categoryRepo,
                                  ProductReviewRepository reviewRepo,
                                  FileStorageService fileStorageService) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        this.reviewRepo = reviewRepo;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public List<Product> list() {
        return productRepo.findAllAdminWithImages();
    }

    @GetMapping("/{id}")
    public Product one(@PathVariable Long id) {
        return productRepo.findAdminByIdWithImages(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

   




//     @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
// public Map<String, String> uploadImage(@RequestPart("file") MultipartFile file) throws IOException {
//     FileStorageService.UploadResult uploaded = fileStorageService.saveFile(file);

//     return Map.of(
//             "imageUrl", uploaded.imageUrl(),
//             "publicId", uploaded.publicId(),
//             "resourceType", uploaded.resourceType()
//     );
// }




@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public Map<String, String> uploadImage(@RequestPart("file") MultipartFile file) throws IOException {

    System.out.println("UPLOAD CONTROLLER HIT");
    System.out.println("FILE NAME = " + file.getOriginalFilename());
    System.out.println("FILE TYPE = " + file.getContentType());
    System.out.println("FILE SIZE = " + file.getSize());

    FileStorageService.UploadResult uploaded = fileStorageService.saveFile(file);

    return Map.of(
            "imageUrl", uploaded.imageUrl(),
            "publicId", uploaded.publicId(),
            "resourceType", uploaded.resourceType()
    );
}



    @PostMapping
    @Transactional
    public Product create(@Valid @RequestBody ProductRequest req) {
        var cat = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        validatePricing(req);

        Product p = new Product();
        p.setTitle(req.title());
        p.setDescription(req.description());
        p.setPriceInr(req.priceInr());
        p.setMrpInr(req.mrpInr());
        p.setStock(req.stock());
        p.setCategory(cat);
        p.setCreatedAt(OffsetDateTime.now());
        p.setActive(true);
        p.setDeleted(false);

        if (req.images() != null) {
            for (String url : req.images()) {
                ProductImage img = new ProductImage();
                img.setImageUrl(url);
                img.setCloudinaryPublicId(extractPublicId(url));
                img.setProduct(p);
                p.getImages().add(img);
            }
        }

        return productRepo.save(p);
    }

    @PutMapping("/{id}")
    @Transactional
    public Product update(@PathVariable Long id,
                          @Valid @RequestBody ProductRequest req) {

        Product p = productRepo.findAdminByIdWithImages(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        var cat = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        validatePricing(req);

        p.setTitle(req.title());
        p.setDescription(req.description());
        p.setPriceInr(req.priceInr());
        p.setMrpInr(req.mrpInr());
        p.setStock(req.stock());
        p.setCategory(cat);

        Set<String> newImageUrls = req.images() == null ? Set.of() : new LinkedHashSet<>(req.images());

        for (ProductImage oldImage : p.getImages()) {
            if (!newImageUrls.contains(oldImage.getImageUrl())) {
                fileStorageService.deleteFile(oldImage.getCloudinaryPublicId());
            }
        }

        p.getImages().clear();

        if (req.images() != null) {
            for (String url : req.images()) {
                ProductImage img = new ProductImage();
                img.setImageUrl(url);
                img.setCloudinaryPublicId(extractPublicId(url));
                img.setProduct(p);
                p.getImages().add(img);
            }
        }

        return productRepo.saveAndFlush(p);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void delete(@PathVariable Long id) {
        Product p = productRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        p.setActive(false);
        p.setDeleted(true);
        productRepo.save(p);
    }

    @PostMapping("/{id}/reviews")
    @Transactional
    public ProductReview addReview(@PathVariable Long id,
                                   @Valid @RequestBody ProductReviewRequest req) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        ProductReview review = new ProductReview();
        review.setProduct(product);
        review.setReviewerName(req.reviewerName());
        review.setRating(req.rating());
        review.setReviewText(req.reviewText());
        review.setFeatured(Boolean.TRUE.equals(req.featured()));

        return reviewRepo.save(review);
    }

    @PutMapping("/{productId}/reviews/{reviewId}")
    @Transactional
    public ProductReview updateReview(@PathVariable Long productId,
                                      @PathVariable Long reviewId,
                                      @Valid @RequestBody ProductReviewRequest req) {
        ProductReview review = reviewRepo.findByIdAndProductId(reviewId, productId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        review.setReviewerName(req.reviewerName());
        review.setRating(req.rating());
        review.setReviewText(req.reviewText());
        review.setFeatured(Boolean.TRUE.equals(req.featured()));

        return reviewRepo.save(review);
    }

    @DeleteMapping("/{productId}/reviews/{reviewId}")
    @Transactional
    public void deleteReview(@PathVariable Long productId,
                             @PathVariable Long reviewId) {
        ProductReview review = reviewRepo.findByIdAndProductId(reviewId, productId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        reviewRepo.delete(review);
    }

    private void validatePricing(ProductRequest req) {
        if (req.priceInr() == null || req.priceInr() <= 0) {
            throw new RuntimeException("Selling price must be greater than 0");
        }

        if (req.mrpInr() != null && req.mrpInr() <= 0) {
            throw new RuntimeException("MRP must be greater than 0");
        }

        if (req.mrpInr() != null && req.mrpInr() < req.priceInr()) {
            throw new RuntimeException("MRP must be greater than or equal to selling price");
        }
    }

    private String extractPublicId(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        int uploadIndex = imageUrl.indexOf("/upload/");
        if (uploadIndex < 0) {
            return null;
        }

        String path = imageUrl.substring(uploadIndex + "/upload/".length());

        path = path.replaceFirst("^v\\d+/", "");

        int lastDot = path.lastIndexOf('.');
        if (lastDot > 0) {
            path = path.substring(0, lastDot);
        }

        return path;
    }
}