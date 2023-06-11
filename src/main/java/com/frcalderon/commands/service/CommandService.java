package com.frcalderon.commands.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frcalderon.commands.controller.dto.CommandProductRequest;
import com.frcalderon.commands.controller.dto.CommandRequest;
import com.frcalderon.commands.exceptions.CommandCouldNotBeUpdatedException;
import com.frcalderon.commands.exceptions.CommandNotFoundException;
import com.frcalderon.commands.model.Command;
import com.frcalderon.commands.model.CommandProduct;
import com.frcalderon.commands.model.UpdateStock;
import com.frcalderon.commands.model.UpdateStockRequest;
import com.frcalderon.commands.repository.CommandProductRepository;
import com.frcalderon.commands.repository.CommandRepository;
import com.frcalderon.commands.repository.UpdateStockRepository;
import com.frcalderon.commands.utils.Utils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CommandService {

    @Value("${ms-commands.ms-products.add-stock-uri:stock/add}")
    private String ADD_STOCK_URI;

    @Value("${ms-stock.ms-products.consume-stock-uri:stock/consume}")
    private String CONSUME_STOCK_URI;

    @Autowired
    private CommandRepository commandRepository;

    @Autowired
    private CommandProductRepository commandProductRepository;

    @Autowired
    private UpdateStockRepository updateStockRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public List<Command> getAllCommands() {
        return this.commandRepository.findAll();
    }

    public Command getCommand(Long id) {
        return commandRepository.findById(id)
                .orElseThrow(CommandNotFoundException::new);
    }

    public Command createCommand(CommandRequest commandRequest) throws JsonProcessingException {
        Command command = Command.builder()
                .deliveryDate(LocalDate.parse(commandRequest.getDeliveryDate(), Utils.localDateTimeFormatter()))
                .status(Command.TO_DO)
                .price(0.0)
                .build();

        Command savedCommand = commandRepository.save(command);

        double commandPrice = 0.0;
        List<UpdateStockRequest> updateStockRequests = new ArrayList<>();
        for (CommandProductRequest product : commandRequest.getCommandProductList()) {
            double productTotalPrice = product.getQuantity() * product.getPrice();
            commandProductRepository.save(
                    CommandProduct.builder()
                            .command(savedCommand)
                            .productId(product.getProductId())
                            .productName(product.getProductName())
                            .quantity(product.getQuantity())
                            .unitPrice(product.getPrice())
                            .totalPrice(productTotalPrice)
                            .build()
            );

            commandPrice = commandPrice + productTotalPrice;

            updateStockRequests.add(UpdateStockRequest.builder()
                    .productId(product.getProductId())
                    .stock(product.getQuantity())
                    .build()
            );
        }

        savedCommand.setPrice(commandPrice);
        commandRepository.save(savedCommand);

        UpdateStock updateStock = UpdateStock.builder()
                .request(objectMapper.writeValueAsString(updateStockRequests))
                .uri(CONSUME_STOCK_URI)
                .sent(false)
                .build();
        updateStockRepository.save(updateStock);

        return commandRepository.findById(savedCommand.getId()).get();
    }

    @Transactional
    public Command updateCommand(Long id, CommandRequest commandRequest) throws JsonProcessingException {
        Command commandToUpdate = commandRepository.findById(id)
                .orElseThrow(CommandNotFoundException::new);

        List<UpdateStockRequest> updateStockRequestsToAdd = new ArrayList<>();
        for (CommandProduct product : commandToUpdate.getProducts()) {
            updateStockRequestsToAdd.add(UpdateStockRequest.builder()
                    .productId(product.getProductId())
                    .stock(product.getQuantity())
                    .build()
            );
        }

        UpdateStock updateStockToAdd = UpdateStock.builder()
                .request(objectMapper.writeValueAsString(updateStockRequestsToAdd))
                .uri(ADD_STOCK_URI)
                .sent(false)
                .build();
        updateStockRepository.save(updateStockToAdd);

        commandProductRepository.deleteByCommandId(id);

        double commandPrice = 0.0;
        List<UpdateStockRequest> updateStockRequestsToConsume = new ArrayList<>();
        for (CommandProductRequest product : commandRequest.getCommandProductList()) {
            double productTotalPrice = product.getQuantity() * product.getPrice();
            commandProductRepository.save(
                    CommandProduct.builder()
                            .command(commandToUpdate)
                            .productId(product.getProductId())
                            .productName(product.getProductName())
                            .quantity(product.getQuantity())
                            .unitPrice(product.getPrice())
                            .totalPrice(productTotalPrice)
                            .build()
            );

            commandPrice = commandPrice + productTotalPrice;

            updateStockRequestsToConsume.add(UpdateStockRequest.builder()
                    .productId(product.getProductId())
                    .stock(product.getQuantity())
                    .build()
            );
        }

        commandToUpdate.setDeliveryDate(LocalDate.parse(commandRequest.getDeliveryDate(), Utils.localDateTimeFormatter()));
        commandToUpdate.setPrice(commandPrice);

        commandRepository.save(commandToUpdate);

        UpdateStock updateStockToConsume = UpdateStock.builder()
                .request(objectMapper.writeValueAsString(updateStockRequestsToConsume))
                .uri(CONSUME_STOCK_URI)
                .sent(false)
                .build();
        updateStockRepository.save(updateStockToConsume);

        return commandRepository.findById(commandToUpdate.getId()).get();
    }

    @Transactional
    public void deleteCommand(Long id) throws JsonProcessingException {
        Command commandToDelete = commandRepository.findById(id)
                .orElseThrow(CommandNotFoundException::new);

        List<UpdateStockRequest> updateStockRequestsToAdd = new ArrayList<>();
        for (CommandProduct product : commandToDelete.getProducts()) {
            updateStockRequestsToAdd.add(UpdateStockRequest.builder()
                    .productId(product.getProductId())
                    .stock(product.getQuantity())
                    .build()
            );
        }

        UpdateStock updateStockToAdd = UpdateStock.builder()
                .request(objectMapper.writeValueAsString(updateStockRequestsToAdd))
                .uri(ADD_STOCK_URI)
                .sent(false)
                .build();
        updateStockRepository.save(updateStockToAdd);

        commandProductRepository.deleteByCommandId(id);

        commandRepository.deleteById(id);
    }

    public Command commandCancelled(Long id) throws JsonProcessingException {
        Command commandToUpdate = commandRepository.findById(id)
                .orElseThrow(CommandNotFoundException::new);

        List<UpdateStockRequest> updateStockRequestsToAdd = new ArrayList<>();
        for (CommandProduct product : commandToUpdate.getProducts()) {
            updateStockRequestsToAdd.add(UpdateStockRequest.builder()
                    .productId(product.getProductId())
                    .stock(product.getQuantity())
                    .build()
            );
        }

        UpdateStock updateStockToAdd = UpdateStock.builder()
                .request(objectMapper.writeValueAsString(updateStockRequestsToAdd))
                .uri(ADD_STOCK_URI)
                .sent(false)
                .build();
        updateStockRepository.save(updateStockToAdd);

        commandToUpdate.setStatus(Command.CANCELLED);

        return commandRepository.save(commandToUpdate);
    }

    public Command commandToDo(Long id) throws JsonProcessingException {
        Command commandToUpdate = commandRepository.findById(id)
                .orElseThrow(CommandNotFoundException::new);

        if (!Objects.equals(commandToUpdate.getStatus(), Command.CANCELLED)) {
            throw new CommandCouldNotBeUpdatedException();
        }

        List<UpdateStockRequest> updateStockRequestsToConsume = new ArrayList<>();
        for (CommandProduct product : commandToUpdate.getProducts()) {
            updateStockRequestsToConsume.add(UpdateStockRequest.builder()
                    .productId(product.getProductId())
                    .stock(product.getQuantity())
                    .build()
            );
        }

        UpdateStock updateStockToAdd = UpdateStock.builder()
                .request(objectMapper.writeValueAsString(updateStockRequestsToConsume))
                .uri(CONSUME_STOCK_URI)
                .sent(false)
                .build();
        updateStockRepository.save(updateStockToAdd);

        commandToUpdate.setStatus(Command.TO_DO);

        return commandRepository.save(commandToUpdate);
    }

    public Command commandInProgress(Long id) {
        Command commandToUpdate = commandRepository.findById(id)
                .orElseThrow(CommandNotFoundException::new);

        if (!Objects.equals(commandToUpdate.getStatus(), Command.TO_DO)) {
            throw new CommandCouldNotBeUpdatedException();
        }

        commandToUpdate.setStatus(Command.IN_PROGRESS);

        return commandRepository.save(commandToUpdate);
    }

    public Command commandPrepared(Long id) {
        Command commandToUpdate = commandRepository.findById(id)
                .orElseThrow(CommandNotFoundException::new);

        if (!Objects.equals(commandToUpdate.getStatus(), Command.IN_PROGRESS)) {
            throw new CommandCouldNotBeUpdatedException();
        }

        commandToUpdate.setStatus(Command.PREPARED);

        return commandRepository.save(commandToUpdate);
    }

    public Command commandDelivered(Long id) {
        Command commandToUpdate = commandRepository.findById(id)
                .orElseThrow(CommandNotFoundException::new);

        if (!Objects.equals(commandToUpdate.getStatus(), Command.PREPARED)) {
            throw new CommandCouldNotBeUpdatedException();
        }

        commandToUpdate.setStatus(Command.DELIVERED);

        return commandRepository.save(commandToUpdate);
    }
}
