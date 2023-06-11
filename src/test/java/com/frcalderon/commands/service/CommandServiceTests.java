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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class CommandServiceTests {

    @Mock
    private UpdateStockRepository updateStockRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CommandRepository commandRepository;

    @Mock
    private CommandProductRepository commandProductRepository;

    @InjectMocks
    private CommandService commandService;

    private AutoCloseable closeable;

    private Command command;

    private List<Command> commandList;

    private CommandProduct commandProduct;

    private List<CommandProduct> commandProductList;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        command = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.TO_DO)
                .price(10.5)
                .build();

        commandProduct = CommandProduct.builder()
                .productId(1L)
                .productName("Lotus Cheesecake")
                .quantity(2)
                .unitPrice(5.25)
                .totalPrice(10.5)
                .build();

        commandProductList = new ArrayList<>();
        commandProductList.add(commandProduct);

        command.setProducts(commandProductList);

        commandList = new ArrayList<>();
        commandList.add(command);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void CommandService_GetAll_ReturnCommandsList() {
        when(commandRepository.findAll()).thenReturn(commandList);

        List<Command> result = commandService.getAllCommands();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(command, result.get(0));

        verify(commandRepository, times(1)).findAll();
    }

    @Test
    public void CommandService_FindById_ReturnCommand() {
        when(commandRepository.findById(1L)).thenReturn(Optional.of(command));

        Command result = commandService.getCommand(1L);

        Assertions.assertEquals(command, result);

        verify(commandRepository, times(1)).findById(1L);
    }

    @Test
    public void CommandService_FindById_ReturnCommandNotFoundException() {
        when(commandRepository.findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(CommandNotFoundException.class, () -> commandService.getCommand(2L));

        verify(commandRepository, times(1)).findById(2L);
    }

    @Test
    public void CommandService_Create_ReturnCommand() throws JsonProcessingException {
        CommandRequest request = CommandRequest.builder()
                .deliveryDate("05-06-2023")
                .commandProductList(Collections.singletonList(
                        CommandProductRequest.builder()
                                .productId(1L)
                                .productName("Oreo Cheesecake")
                                .quantity(2)
                                .price(7.5)
                                .build()
                ))
                .build();

        Command newCommand = Command.builder()
                .id(2L)
                .deliveryDate(LocalDate.parse("05-06-2023", Utils.localDateTimeFormatter()))
                .status(Command.TO_DO)
                .price(15.0)
                .products(commandProductList)
                .build();

        CommandProduct newCommandProduct = CommandProduct.builder()
                .productId(1L)
                .productName("Oreo Cheesecake")
                .quantity(2)
                .unitPrice(7.5)
                .totalPrice(15.0)
                .build();

        UpdateStock updateStock = UpdateStock.builder()
                .request(Collections.singletonList(
                                UpdateStockRequest.builder()
                                        .productId(commandProduct.getProductId())
                                        .stock(commandProduct.getQuantity())
                                        .build()
                        ).toString()
                )
                .uri("this is a uri")
                .sent(false)
                .build();

        when(objectMapper.writeValueAsString(any(String.class))).thenReturn("request");
        when(updateStockRepository.save(any(UpdateStock.class))).thenReturn(updateStock);
        when(commandRepository.save(any(Command.class))).thenReturn(newCommand);
        when(commandProductRepository.save(any(CommandProduct.class))).thenReturn(newCommandProduct);
        when(commandRepository.findById(2L)).thenReturn(Optional.of(newCommand));

        Command result = commandService.createCommand(request);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getDeliveryDate().isEqual(LocalDate.parse("05-06-2023", Utils.localDateTimeFormatter())));
        Assertions.assertEquals(Command.TO_DO, result.getStatus());
        Assertions.assertEquals(15.0, result.getPrice());

        verify(commandRepository, times(2)).save(any(Command.class));
        verify(commandProductRepository, times(1)).save(any(CommandProduct.class));
        verify(updateStockRepository, times(1)).save(any(UpdateStock.class));
    }

    @Test
    public void CommandService_Update_ReturnCommand() throws JsonProcessingException {
        CommandRequest request = CommandRequest.builder()
                .deliveryDate("15-06-2023")
                .commandProductList(Collections.singletonList(
                        CommandProductRequest.builder()
                                .productId(1L)
                                .productName("Oreo Cheesecake")
                                .quantity(1)
                                .price(7.5)
                                .build()
                ))
                .build();

        Command updatedCommand = Command.builder()
                .id(1L)
                .deliveryDate(LocalDate.parse("15-06-2023", Utils.localDateTimeFormatter()))
                .status(Command.TO_DO)
                .price(7.5)
                .products(commandProductList)
                .build();

        CommandProduct updatedCommandProduct = CommandProduct.builder()
                .productId(1L)
                .productName("Oreo Cheesecake")
                .quantity(1)
                .unitPrice(7.5)
                .totalPrice(7.5)
                .build();

        UpdateStock updateStock = UpdateStock.builder()
                .request(Collections.singletonList(
                                UpdateStockRequest.builder()
                                        .productId(commandProduct.getProductId())
                                        .stock(commandProduct.getQuantity())
                                        .build()
                        ).toString()
                )
                .uri("this is a uri")
                .sent(false)
                .build();

        when(commandRepository.findById(1L)).thenReturn(Optional.of(updatedCommand));
        when(objectMapper.writeValueAsString(any(String.class))).thenReturn("request");
        when(updateStockRepository.save(any(UpdateStock.class))).thenReturn(updateStock);
        doNothing().when(commandProductRepository).deleteByCommandId(any(Long.class));
        when(commandProductRepository.save(any(CommandProduct.class))).thenReturn(updatedCommandProduct);
        when(commandRepository.save(any(Command.class))).thenReturn(updatedCommand);


        Command result = commandService.updateCommand(1L, request);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getDeliveryDate().isEqual(LocalDate.parse("15-06-2023", Utils.localDateTimeFormatter())));
        Assertions.assertEquals(Command.TO_DO, result.getStatus());
        Assertions.assertEquals(7.5, result.getPrice());

        verify(commandRepository, times(2)).findById(1L);
        verify(commandProductRepository, times(1)).deleteByCommandId(any(Long.class));
        verify(commandProductRepository, times(1)).save(any(CommandProduct.class));
        verify(commandRepository, times(1)).save(any(Command.class));
        verify(updateStockRepository, times(2)).save(any(UpdateStock.class));
    }

    @Test
    public void CommandService_Update_ReturnCommandNotFoundException() {
        CommandRequest request = CommandRequest.builder()
                .deliveryDate("15-06-2023")
                .commandProductList(Collections.singletonList(
                        CommandProductRequest.builder()
                                .productId(1L)
                                .productName("Oreo Cheesecake")
                                .quantity(1)
                                .price(7.5)
                                .build()
                ))
                .build();

        when(commandRepository.findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(CommandNotFoundException.class, () -> commandService.updateCommand(2L, request));

        verify(commandRepository, times(1)).findById(2L);
    }

    @Test
    public void CommandService_Delete_ReturnVoid() throws JsonProcessingException {
        UpdateStock updateStock = UpdateStock.builder()
                .request(Collections.singletonList(
                                UpdateStockRequest.builder()
                                        .productId(commandProduct.getProductId())
                                        .stock(commandProduct.getQuantity())
                                        .build()
                        ).toString()
                )
                .uri("this is a uri")
                .sent(false)
                .build();

        when(commandRepository.findById(1L)).thenReturn(Optional.of(command));
        when(objectMapper.writeValueAsString(any(String.class))).thenReturn("request");
        when(updateStockRepository.save(any(UpdateStock.class))).thenReturn(updateStock);

        commandService.deleteCommand(1L);

        verify(commandRepository, times(1)).findById(1L);
        verify(commandRepository, times(1)).deleteById(1L);
        verify(updateStockRepository, times(1)).save(any(UpdateStock.class));
    }

    @Test
    public void CommandService_Delete_ReturnCommandNotFoundException() {
        when(commandRepository.findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(CommandNotFoundException.class, () -> commandService.deleteCommand(2L));

        verify(commandRepository, times(1)).findById(2L);
        verify(commandRepository, times(0)).deleteById(2L);
        verify(updateStockRepository, times(0)).save(any(UpdateStock.class));
    }

    @Test
    public void CommandService_CommandCancelled_ReturnCommand() throws JsonProcessingException {
        Command commandCancelled = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.CANCELLED)
                .price(10.5)
                .products(commandProductList)
                .build();

        UpdateStock updateStock = UpdateStock.builder()
                .request(Collections.singletonList(
                                UpdateStockRequest.builder()
                                        .productId(commandProduct.getProductId())
                                        .stock(commandProduct.getQuantity())
                                        .build()
                        ).toString()
                )
                .uri("this is a uri")
                .sent(false)
                .build();

        when(commandRepository.findById(1L)).thenReturn(Optional.of(commandCancelled));
        when(objectMapper.writeValueAsString(any(String.class))).thenReturn("request");
        when(updateStockRepository.save(any(UpdateStock.class))).thenReturn(updateStock);
        when(commandRepository.save(any(Command.class))).thenReturn(commandCancelled);

        Command result = commandService.commandCancelled(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(Command.CANCELLED, result.getStatus());

        verify(commandRepository, times(1)).findById(1L);
        verify(commandRepository, times(1)).save(any(Command.class));
        verify(updateStockRepository, times(1)).save(any(UpdateStock.class));
    }

    @Test
    public void CommandService_CommandCancelled_ReturnCommandNotFoundException() {
        when(commandRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(CommandNotFoundException.class, () -> commandService.commandCancelled(1L));

        verify(commandRepository, times(1)).findById(1L);
        verify(commandRepository, times(0)).save(any(Command.class));
        verify(updateStockRepository, times(0)).save(any(UpdateStock.class));
    }

    @Test
    public void CommandService_CommandToDo_ReturnCommand() throws JsonProcessingException {
        Command commandCancelled = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.CANCELLED)
                .price(10.5)
                .products(commandProductList)
                .build();

        Command commandToDo = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.TO_DO)
                .price(10.5)
                .products(commandProductList)
                .build();

        UpdateStock updateStock = UpdateStock.builder()
                .request(Collections.singletonList(
                                UpdateStockRequest.builder()
                                        .productId(commandProduct.getProductId())
                                        .stock(commandProduct.getQuantity())
                                        .build()
                        ).toString()
                )
                .uri("this is a uri")
                .sent(false)
                .build();

        when(commandRepository.findById(1L)).thenReturn(Optional.of(commandCancelled));
        when(objectMapper.writeValueAsString(any(String.class))).thenReturn("request");
        when(updateStockRepository.save(any(UpdateStock.class))).thenReturn(updateStock);
        when(commandRepository.save(any(Command.class))).thenReturn(commandToDo);

        Command result = commandService.commandToDo(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(Command.TO_DO, result.getStatus());

        verify(commandRepository, times(1)).findById(1L);
        verify(commandRepository, times(1)).save(any(Command.class));
        verify(updateStockRepository, times(1)).save(any(UpdateStock.class));
    }

    @Test
    public void CommandService_CommandToDo_ReturnCommandNotFoundException() {
        when(commandRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(CommandNotFoundException.class, () -> commandService.commandToDo(1L));

        verify(commandRepository, times(1)).findById(1L);
        verify(commandRepository, times(0)).save(any(Command.class));
        verify(updateStockRepository, times(0)).save(any(UpdateStock.class));
    }

    @Test
    public void CommandService_CommandToDo_ReturnCommandCouldNotBeUpdatedException() {
        Command commandToDo = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.TO_DO)
                .price(10.5)
                .build();

        when(commandRepository.findById(1L)).thenReturn(Optional.of(commandToDo));

        Assertions.assertThrows(CommandCouldNotBeUpdatedException.class, () -> commandService.commandToDo(1L));

        verify(commandRepository, times(1)).findById(1L);
        verify(commandRepository, times(0)).save(any(Command.class));
        verify(updateStockRepository, times(0)).save(any(UpdateStock.class));
    }

    @Test
    public void CommandService_CommandInProgress_ReturnCommand() {
        Command commandToDo = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.TO_DO)
                .price(10.5)
                .build();

        Command commandInProgress = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.IN_PROGRESS)
                .price(10.5)
                .build();

        when(commandRepository.findById(1L)).thenReturn(Optional.of(commandToDo));
        when(commandRepository.save(any(Command.class))).thenReturn(commandInProgress);

        Command result = commandService.commandInProgress(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(Command.IN_PROGRESS, result.getStatus());

        verify(commandRepository, times(1)).findById(1L);
        verify(commandRepository, times(1)).save(any(Command.class));
    }

    @Test
    public void CommandService_CommandInProgress_ReturnCommandNotFoundException() {
        when(commandRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(CommandNotFoundException.class, () -> commandService.commandInProgress(1L));

        verify(commandRepository, times(1)).findById(1L);
        verify(commandRepository, times(0)).save(any(Command.class));
    }

    @Test
    public void CommandService_CommandInProgress_ReturnCommandCouldNotBeUpdatedException() {
        Command commandInProgress = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.IN_PROGRESS)
                .price(10.5)
                .build();

        when(commandRepository.findById(1L)).thenReturn(Optional.of(commandInProgress));

        Assertions.assertThrows(CommandCouldNotBeUpdatedException.class, () -> commandService.commandInProgress(1L));

        verify(commandRepository, times(1)).findById(1L);
        verify(commandRepository, times(0)).save(any(Command.class));
    }

    @Test
    public void CommandService_CommandPrepared_ReturnCommand() {
        Command commandInProgress = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.IN_PROGRESS)
                .price(10.5)
                .build();

        Command commandPrepared = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.PREPARED)
                .price(10.5)
                .build();

        when(commandRepository.findById(1L)).thenReturn(Optional.of(commandInProgress));
        when(commandRepository.save(any(Command.class))).thenReturn(commandPrepared);

        Command result = commandService.commandPrepared(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(Command.PREPARED, result.getStatus());

        verify(commandRepository, times(1)).findById(1L);
        verify(commandRepository, times(1)).save(any(Command.class));
    }

    @Test
    public void CommandService_CommandPrepared_ReturnCommandNotFoundException() {
        when(commandRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(CommandNotFoundException.class, () -> commandService.commandPrepared(1L));

        verify(commandRepository, times(1)).findById(1L);
        verify(commandRepository, times(0)).save(any(Command.class));
    }

    @Test
    public void CommandService_CommandPrepared_ReturnCommandCouldNotBeUpdatedException() {
        Command commandInProgress = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.PREPARED)
                .price(10.5)
                .build();

        when(commandRepository.findById(1L)).thenReturn(Optional.of(commandInProgress));

        Assertions.assertThrows(CommandCouldNotBeUpdatedException.class, () -> commandService.commandPrepared(1L));

        verify(commandRepository, times(1)).findById(1L);
        verify(commandRepository, times(0)).save(any(Command.class));
    }

    @Test
    public void CommandService_CommandDelivered_ReturnCommand() {
        Command commandPrepared = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.PREPARED)
                .price(10.5)
                .build();

        Command commandDelivered = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.DELIVERED)
                .price(10.5)
                .build();

        when(commandRepository.findById(1L)).thenReturn(Optional.of(commandPrepared));
        when(commandRepository.save(any(Command.class))).thenReturn(commandDelivered);

        Command result = commandService.commandDelivered(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(Command.DELIVERED, result.getStatus());

        verify(commandRepository, times(1)).findById(1L);
        verify(commandRepository, times(1)).save(any(Command.class));
    }

    @Test
    public void CommandService_CommandDelivered_ReturnCommandNotFoundException() {
        when(commandRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(CommandNotFoundException.class, () -> commandService.commandDelivered(1L));

        verify(commandRepository, times(1)).findById(1L);
        verify(commandRepository, times(0)).save(any(Command.class));
    }

    @Test
    public void CommandService_CommandDelivered_ReturnCommandCouldNotBeUpdatedException() {
        Command commandInProgress = Command.builder()
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.DELIVERED)
                .price(10.5)
                .build();

        when(commandRepository.findById(1L)).thenReturn(Optional.of(commandInProgress));

        Assertions.assertThrows(CommandCouldNotBeUpdatedException.class, () -> commandService.commandDelivered(1L));

        verify(commandRepository, times(1)).findById(1L);
        verify(commandRepository, times(0)).save(any(Command.class));
    }
}