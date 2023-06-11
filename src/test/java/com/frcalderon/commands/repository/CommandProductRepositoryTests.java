package com.frcalderon.commands.repository;

import com.frcalderon.commands.model.Command;
import com.frcalderon.commands.model.CommandProduct;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CommandProductRepositoryTests {

    @Autowired
    private CommandProductRepository commandProductRepository;

    @Autowired
    private CommandRepository commandRepository;

    @Test
    public void ProductIngredientRepository_Save_ReturnSavedProductIngredient() {
        Command command = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.TO_DO)
                .price(10.5)
                .build();

        commandRepository.save(command);

        Assertions.assertThat(command.getId()).isGreaterThan(0);

        CommandProduct commandProduct = CommandProduct.builder()
                .productId(1L)
                .productName("Lotus Cheesecake")
                .quantity(2)
                .unitPrice(5.25)
                .totalPrice(10.5)
                .build();

        CommandProduct savedCommandProduct = commandProductRepository.save(commandProduct);

        Assertions.assertThat(savedCommandProduct).isNotNull();
        Assertions.assertThat(savedCommandProduct.getId()).isGreaterThan(0);
    }

    @Test
    public void CommandProductRepository_Delete_ReturnCommandProductIsEmpty_CommandIsNotRemoved() {
        Command command = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.TO_DO)
                .price(10.5)
                .build();

        commandRepository.save(command);

        Assertions.assertThat(command.getId()).isGreaterThan(0);

        CommandProduct commandProduct = CommandProduct.builder()
                .command(command)
                .productId(1L)
                .productName("Lotus Cheesecake")
                .quantity(2)
                .unitPrice(5.25)
                .totalPrice(10.5)
                .build();

        CommandProduct savedCommandProduct = commandProductRepository.save(commandProduct);

        Assertions.assertThat(savedCommandProduct).isNotNull();
        Assertions.assertThat(savedCommandProduct.getId()).isGreaterThan(0);

        commandProductRepository.deleteById(savedCommandProduct.getId());

        Optional<CommandProduct> deletedCommandProduct = commandProductRepository.findById(savedCommandProduct.getId());
        Assertions.assertThat(deletedCommandProduct).isEmpty();

        Optional<Command> deletedCommand = commandRepository.findById(command.getId());
        Assertions.assertThat(deletedCommand).isNotEmpty();
    }

    @Test
    public void CommandProductRepository_DeleteByCommand_ReturnCommandProductIsEmpty_CommandIsNotRemoved() {
        Command command = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.TO_DO)
                .price(10.5)
                .build();

        commandRepository.save(command);

        Assertions.assertThat(command.getId()).isGreaterThan(0);

        CommandProduct commandProduct = CommandProduct.builder()
                .command(command)
                .productId(1L)
                .productName("Lotus Cheesecake")
                .quantity(2)
                .unitPrice(5.25)
                .totalPrice(10.5)
                .build();

        CommandProduct savedCommandProduct = commandProductRepository.save(commandProduct);

        Assertions.assertThat(savedCommandProduct).isNotNull();
        Assertions.assertThat(savedCommandProduct.getId()).isGreaterThan(0);

        commandProductRepository.deleteByCommandId(command.getId());

        Optional<CommandProduct> deletedCommandProduct = commandProductRepository.findById(savedCommandProduct.getId());
        Assertions.assertThat(deletedCommandProduct).isEmpty();

        Optional<Command> deletedCommand = commandRepository.findById(command.getId());
        Assertions.assertThat(deletedCommand).isNotEmpty();
    }
}
