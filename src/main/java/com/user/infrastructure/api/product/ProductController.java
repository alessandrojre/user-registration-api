package com.user.infrastructure.api.product;

import com.user.application.product.dto.ProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping
    public ResponseEntity<List<ProductResponse>> list() {
        var products = List.of(
                new ProductResponse("P001", "Laptop Macbook"),
                new ProductResponse("P002", "Laptop HP"),
                new ProductResponse("P003", "Teclado xyz")
        );
        return ResponseEntity.ok(products);
    }
}
