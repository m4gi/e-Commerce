package com.team.ecommerce.controller.admin;

import com.team.ecommerce.entity.Category;
import com.team.ecommerce.entity.FieldDetail;
import com.team.ecommerce.entity.History;
import com.team.ecommerce.entity.Product;
import com.team.ecommerce.service.CategoryService;
import com.team.ecommerce.service.HistotyService;
import com.team.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Controller
@RequestMapping("/admin/storage")
public class StorageController {

    @Autowired
    private ProductService productService;

    @Autowired
    private HistotyService histotyService;

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("")
    public String listAll(Model model) {
        model.addAttribute("products", productService.getAll());
        return "admin/storage/manage";
    }

    // Saving History And Change Product Quantity//
    @RequestMapping("import/{id}")
    public String imp(Model model, @PathVariable Integer id) {
        model.addAttribute("product", productService.get(id));
        return "admin/storage/import";
    }

    @RequestMapping("export/{id}")
    public String exp(Model model, @PathVariable Integer id) {
        model.addAttribute("product", productService.get(id));
        return "admin/storage/export";
    }

    @RequestMapping(value = "impSave", method = RequestMethod.POST)
    public String impSave(@ModelAttribute("Product") Product product,
                          @RequestParam("changeQuantity") String changeQuanity,
                          @RequestParam("histotyDescription") String histotyDescription) {
        int temp = 0;
        try {
            temp = Integer.parseInt(changeQuanity);
            if (temp < 0) {
                System.out.println("false");
            }
            else {
                History history = new History();
                history.setDescription(histotyDescription);
                history.setDate(new Date());
                history.setProduct(product);
                history.setQuantity(temp);
                product.setQuantity(product.getQuantity() + temp);
                histotyService.save(history);
                productService.save(product);
            }
        } catch (Exception ignored) {
        }
        return "redirect:/admin/storage";
    }

    @RequestMapping(value = "expSave", method = RequestMethod.POST)
    public String empSave(@ModelAttribute("Product") Product product,
                          @RequestParam("changeQuantity") String changeQuanity,
                          @RequestParam("histotyDescription") String histotyDescription) {
        int temp = 0;
        try {
            temp = Integer.parseInt(changeQuanity);
            if (temp > product.getQuantity()) System.out.println("false");
            else {
                History history = new History();
                history.setDescription(histotyDescription);
                history.setDate(new Date());
                history.setProduct(product);
                history.setQuantity(-temp);
                product.setQuantity(product.getQuantity() - temp);
                histotyService.save(history);
                productService.save(product);
            }
        } catch (Exception ignored) {
        }
        return "redirect:/admin/storage";
    }

    public class TextBoxValidator implements Validator {
        @Override
        public boolean supports(Class<?> clazz) {
            return false;
        }

        @Override
        public void validate(Object target, Errors errors) {
            ValidationUtils.rejectIfEmptyOrWhitespace(
                    errors, "username", "required.username");
        }
    }

}