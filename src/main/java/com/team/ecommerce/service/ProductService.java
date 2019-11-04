package com.team.ecommerce.service;

import com.team.ecommerce.entity.Field;
import com.team.ecommerce.entity.FieldDetail;
import com.team.ecommerce.entity.Product;
import com.team.ecommerce.repository.CategoryRepository;
import com.team.ecommerce.repository.FieldDetailRepository;
import com.team.ecommerce.repository.FieldRepository;
import com.team.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private FieldRepository fieldRepository;
    @Autowired
    private FieldDetailRepository fieldDetailRepository;

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product get(int id) {
        return productRepository.getOne(id);
    }

    public void save(Product product) {
        productRepository.save(product);
        product.getFieldDetails().forEach(fieldDetailRepository::save);
    }

    public void delete(int id) {
        fieldDetailRepository.getAllByProduct(productRepository.getOne(id)).forEach(fieldDetailRepository::delete);
        productRepository.deleteById(id);
    }

    public void saveProduct(String name, String category, Long price, Long discount, Integer quantity, String image, String description, Map<String, String> fieldDetails) {
        Product product = new Product();
        product.setImage(image);
        product.setName(name);
        product.setCategory(categoryRepository.getFirstByCategory(category));
        product.setPrice(price);
        product.setDiscount(discount);
        product.setQuantity(quantity);
        product.setDescription(description);
        product = productRepository.save(product);

        for (Map.Entry<String, String> fd : fieldDetails.entrySet()) {
            FieldDetail fieldDetail = new FieldDetail();
            Field field = fieldRepository.getFirstByCategory_CategoryAndField(category, fd.getKey());

            fieldDetail.setField(field);
            fieldDetail.setDetail(fd.getValue());
            fieldDetail.setProduct(product);
            fieldDetailRepository.save(fieldDetail);
        }
        reindexProduct(product.getId());
    }

    public void editProduct(Product product, Map<String, String> fieldDetails) {
        Product p = productRepository.getOne(product.getId());
        p.setImage(product.getImage());
        p.setName(product.getName());
        p.setPrice(product.getPrice());
        p.setDiscount(product.getDiscount());
        p.setDescription(product.getDescription());

        p.getFieldDetails().forEach(fd -> {
            fd.setDetail(fieldDetails.get(fd.getField().getField()));
        });
        p = productRepository.save(p);
        reindexProduct(p.getId());
    }

    private void reindexProduct(int productId) {
        productRepository.save(get(productId));
    }
}
