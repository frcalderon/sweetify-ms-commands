package com.frcalderon.commands.service;

import com.frcalderon.commands.model.UpdateStock;
import com.frcalderon.commands.repository.UpdateStockRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Data
public class UpdateStockService {

    @Value("${ms-commands.ms-products.base-url:http://localhost:8082/products/}")
    private String PRODUCTS_BASE_URL;

    @Autowired
    private UpdateStockRepository updateStockRepository;

    public void sendRequestToUpdateStock() {
        List<UpdateStock> updateStockList = updateStockRepository.findAllBySent(false);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        for (UpdateStock updateStock : updateStockList) {
            HttpEntity<String> request = new HttpEntity<>(updateStock.getRequest(), headers);
            String url = PRODUCTS_BASE_URL + updateStock.getUri();
            System.out.println(url);

            try {
                System.out.println("Sending request to update products: " + updateStock.getRequest());
                ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    updateStock.setSent(true);
                    updateStockRepository.save(updateStock);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
