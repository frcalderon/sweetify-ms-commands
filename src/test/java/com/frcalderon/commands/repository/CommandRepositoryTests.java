package com.frcalderon.commands.repository;

import com.frcalderon.commands.model.Command;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CommandRepositoryTests {

    @Autowired
    private CommandRepository commandRepository;

    @Test
    public void CommandRepository_GetAll_ReturnMoreThanOneCommand() {
        Command command1 = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.TO_DO)
                .price(10.5)
                .build();

        Command command2 = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(10))
                .status(Command.CANCELLED)
                .price(20.5)
                .build();

        commandRepository.save(command1);
        commandRepository.save(command2);

        List<Command> commandList = commandRepository.findAll();

        Assertions.assertThat(commandList).isNotNull();
        Assertions.assertThat(commandList.size()).isEqualTo(2);
    }

    @Test
    public void CommandRepository_FindById_ReturnCommand() {
        Command command = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.TO_DO)
                .price(10.5)
                .build();

        commandRepository.save(command);

        Command foundCommand = commandRepository.findById(command.getId()).get();

        Assertions.assertThat(foundCommand).isNotNull();
    }

    @Test
    public void CommandRepository_Save_ReturnSavedCommand() {
        Command command = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.TO_DO)
                .price(10.5)
                .build();

        Command savedCommand = commandRepository.save(command);

        Assertions.assertThat(savedCommand).isNotNull();
        Assertions.assertThat(savedCommand.getId()).isGreaterThan(0);
    }

    @Test
    public void CommandRepository_Update_ReturnCommandNotNull() {
        Command command = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.TO_DO)
                .price(10.5)
                .build();

        commandRepository.save(command);

        Command savedCommand = commandRepository.findById(command.getId()).get();
        savedCommand.setDeliveryDate(LocalDate.now().plusDays(10));
        savedCommand.setStatus(Command.CANCELLED);
        savedCommand.setPrice(20.75);

        Command updatedCommand = commandRepository.save(savedCommand);

        Assertions.assertThat(updatedCommand.getDeliveryDate()).isEqualTo(savedCommand.getDeliveryDate());
        Assertions.assertThat(updatedCommand.getStatus()).isEqualTo(savedCommand.getStatus());
        Assertions.assertThat(updatedCommand.getPrice()).isEqualTo(savedCommand.getPrice());
    }

    @Test
    public void CommandRepository_Delete_ReturnCommandIsEmpty() {
        Command command = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.TO_DO)
                .price(10.5)
                .build();

        commandRepository.save(command);

        commandRepository.deleteById(command.getId());

        Optional<Command> deletedCommand = commandRepository.findById(command.getId());

        Assertions.assertThat(deletedCommand).isEmpty();
    }
}
