package com.mydev.ecommerce.bulkorder.service;

import com.mydev.ecommerce.bulkorder.dto.BulkOrderInquiryRequest;
import com.mydev.ecommerce.bulkorder.dto.BulkOrderInquiryResponse;
import com.mydev.ecommerce.bulkorder.dto.BulkOrderStatusUpdateRequest;
import com.mydev.ecommerce.bulkorder.model.BulkOrderInquiry;
import com.mydev.ecommerce.bulkorder.repository.BulkOrderInquiryRepository;
import com.mydev.ecommerce.product.model.Product;
import com.mydev.ecommerce.product.model.ProductImage;
import com.mydev.ecommerce.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BulkOrderInquiryService {

    private final BulkOrderInquiryRepository bulkOrderInquiryRepository;
    private final ProductRepository productRepository;

    public BulkOrderInquiryService(BulkOrderInquiryRepository bulkOrderInquiryRepository,
                                   ProductRepository productRepository) {
        this.bulkOrderInquiryRepository = bulkOrderInquiryRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public BulkOrderInquiryResponse create(BulkOrderInquiryRequest req) {
        Product product = productRepository.findById(req.productId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (!product.isActive() || product.isDeleted()) {
            throw new EntityNotFoundException("Product not available");
        }

        BulkOrderInquiry inquiry = new BulkOrderInquiry();
        inquiry.setProduct(product);
        inquiry.setCustomerName(req.customerName().trim());
        inquiry.setEmail(req.email().trim());
        inquiry.setPhone(req.phone().trim());
        inquiry.setCompanyName(req.companyName() == null ? null : req.companyName().trim());
        inquiry.setQuantity(req.quantity());
        inquiry.setMessage(req.message() == null ? null : req.message().trim());

        BulkOrderInquiry saved = bulkOrderInquiryRepository.save(inquiry);
        return map(saved);
    }

    @Transactional(readOnly = true)
    public List<BulkOrderInquiryResponse> getAll() {
        return bulkOrderInquiryRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::map)
                .toList();
    }

    @Transactional(readOnly = true)
    public BulkOrderInquiryResponse getOne(Long id) {
        BulkOrderInquiry inquiry = bulkOrderInquiryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bulk inquiry not found"));
        return map(inquiry);
    }

    @Transactional
    public BulkOrderInquiryResponse updateStatus(Long id, BulkOrderStatusUpdateRequest req) {
        BulkOrderInquiry inquiry = bulkOrderInquiryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bulk inquiry not found"));

        inquiry.setStatus(req.status());
        return map(bulkOrderInquiryRepository.save(inquiry));
    }

    private BulkOrderInquiryResponse map(BulkOrderInquiry inquiry) {
        Product product = inquiry.getProduct();

        String firstImage = null;
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            ProductImage img = product.getImages().get(0);
            firstImage = img != null ? img.getImageUrl() : null;
        }

        return new BulkOrderInquiryResponse(
                inquiry.getId(),
                product.getId(),
                product.getTitle(),
                firstImage,
                product.getPriceInr(),
                inquiry.getCustomerName(),
                inquiry.getEmail(),
                inquiry.getPhone(),
                inquiry.getCompanyName(),
                inquiry.getQuantity(),
                inquiry.getMessage(),
                inquiry.getStatus(),
                inquiry.getCreatedAt()
        );
    }
}