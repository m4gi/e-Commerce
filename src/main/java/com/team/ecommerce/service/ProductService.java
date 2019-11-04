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
    }

    public void delete(int id) {
        productRepository.deleteById(id);
    }

    public void createProduct(String name, String category, Long price, Long discount, Integer quantity, String image, Map<String, String> fieldDetails) {
        Product product = new Product();
        product.setImage(image);
        product.setName(name);
        product.setCategory(categoryRepository.getFirstByCategory(category));
        product.setPrice(price);
        product.setDiscount(discount);
        product.setQuantity(quantity);
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

    private void reindexProduct(int productId){
        productRepository.save(get(productId));
    }
}
