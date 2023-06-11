package com.frcalderon.commands.repository;

import com.frcalderon.commands.model.UpdateStock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UpdateStockRepositoryTests {

    @Autowired
    private UpdateStockRepository updateStockRepository;

    @Test
    public void UpdateStockRepository_FindById_ReturnUpdateStock() {
        UpdateStock updateStock = UpdateStock.builder()
                .request("this is a request")
                .uri("this is a uri")
                .sent(false)
                .build();

        updateStockRepository.save(updateStock);

        UpdateStock foundUpdateStock = updateStockRepository.findById(updateStock.getId()).get();

        Assertions.assertThat(foundUpdateStock).isNotNull();
    }

    @Test
    public void UpdateStockRepository_Save_ReturnSavedUpdateStock() {
        UpdateStock updateStock = UpdateStock.builder()
                .request("this is a request")
                .uri("this is a uri")
                .sent(false)
                .build();

        UpdateStock savedUpdateStock = updateStockRepository.save(updateStock);

        Assertions.assertThat(updateStock).isNotNull();
        Assertions.assertThat(savedUpdateStock.getId()).isGreaterThan(0);
    }

    @Test
    public void UpdateStockRepository_Update_ReturnUpdateStockNotNull() {
        UpdateStock updateStock = UpdateStock.builder()
                .request("this is a request")
                .uri("this is a uri")
                .sent(false)
                .build();

        updateStockRepository.save(updateStock);

        UpdateStock savedUpdateStock = updateStockRepository.findById(updateStock.getId()).get();
        savedUpdateStock.setSent(true);

        UpdateStock updatedUpdateStock = updateStockRepository.save(savedUpdateStock);

        Assertions.assertThat(updatedUpdateStock.isSent()).isTrue();
    }

    @Test
    public void UpdateStockRepository_GetBySent_ReturnMoreThanOneUpdateStock() {
        UpdateStock updateStock1 = UpdateStock.builder()
                .request("this is a request")
                .uri("this is a uri")
                .sent(false)
                .build();

        UpdateStock updateStock2 = UpdateStock.builder()
                .request("this is a request")
                .uri("this is a uri")
                .sent(false)
                .build();

        UpdateStock updateStock3 = UpdateStock.builder()
                .request("this is a request")
                .uri("this is a uri")
                .sent(true)
                .build();

        UpdateStock updateStock4 = UpdateStock.builder()
                .request("this is a request")
                .uri("this is a uri")
                .sent(true)
                .build();

        updateStockRepository.save(updateStock1);
        updateStockRepository.save(updateStock2);
        updateStockRepository.save(updateStock3);
        updateStockRepository.save(updateStock4);

        Assertions.assertThat(updateStock1.getId()).isGreaterThan(0);
        Assertions.assertThat(updateStock2.getId()).isGreaterThan(0);
        Assertions.assertThat(updateStock3.getId()).isGreaterThan(0);
        Assertions.assertThat(updateStock4.getId()).isGreaterThan(0);

        List<UpdateStock> updateStockList = updateStockRepository.findAllBySent(false);

        Assertions.assertThat(updateStockList).isNotNull();
        Assertions.assertThat(updateStockList.size()).isEqualTo(2);
    }
}
