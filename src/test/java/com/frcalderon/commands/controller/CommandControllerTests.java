package com.frcalderon.commands.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frcalderon.commands.controller.dto.CommandProductRequest;
import com.frcalderon.commands.controller.dto.CommandRequest;
import com.frcalderon.commands.controller.dto.CommandResponse;
import com.frcalderon.commands.model.Command;
import com.frcalderon.commands.model.CommandProduct;
import com.frcalderon.commands.service.CommandService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = CommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class CommandControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommandService commandService;

    @Autowired
    private ObjectMapper objectMapper;

    private Command command;

    private CommandRequest commandRequest;

    private CommandResponse commandResponse;

    @BeforeEach
    void setUp() {
        List<CommandProduct> products = Collections.singletonList(
                CommandProduct.builder()
                        .productId(1L)
                        .productName("Lotus Cheesecake")
                        .quantity(2)
                        .unitPrice(5.5)
                        .totalPrice(11.0)
                        .command(command)
                        .build()
        );

        command = Command.builder()
                .id(1L)
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.TO_DO)
                .price(11.0)
                .products(products)
                .build();

        commandRequest = CommandRequest.builder()
                .deliveryDate("15-06-2023")
                .commandProductList(
                        Collections.singletonList(
                                CommandProductRequest.builder()
                                        .productId(1L)
                                        .productName("Lotus Cheesecake")
                                        .quantity(2)
                                        .price(5.5)
                                        .build()
                        )
                )
                .build();

        commandResponse = CommandResponse.builder()
                .id(1L)
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.TO_DO)
                .price(11.0)
                .products(products)
                .build();
    }

    @Test
    public void CommandController_GetAllCommands_ReturnListOfCommandResponseAndOk() throws Exception {
        List<Command> commandResponseList = Collections.singletonList(command);

        when(commandService.getAllCommands()).thenReturn(commandResponseList);

        ResultActions response = mockMvc.perform(get("/commands")
                .contentType(MediaType.APPLICATION_JSON)
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.size()",
                        CoreMatchers.is(commandResponseList.size())
                ));
    }

    @Test
    public void CommandController_GetCommand_ReturnCommandResponseAndOk() throws Exception {
        Long commandId = 1L;
        when(commandService.getCommand(commandId)).thenReturn(command);

        ResultActions response = mockMvc.perform(get("/commands/1")
                .contentType(MediaType.APPLICATION_JSON)
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.id",
                        CoreMatchers.is(commandResponse.getId().intValue())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.deliveryDate",
                        CoreMatchers.is(commandResponse.getDeliveryDate().toString())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.status",
                        CoreMatchers.is(commandResponse.getStatus())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.price",
                        CoreMatchers.is(commandResponse.getPrice())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.products.size()",
                        CoreMatchers.is(commandResponse.getProducts().size())
                ));
    }

    @Test
    public void CommandController_CreateCommand_ReturnCommandResponseAndCreated() throws Exception {
        when(commandService.createCommand(commandRequest)).thenReturn(command);

        ResultActions response = mockMvc.perform(post("/commands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commandRequest))
        );

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.id",
                        CoreMatchers.is(commandResponse.getId().intValue())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.deliveryDate",
                        CoreMatchers.is(commandResponse.getDeliveryDate().toString())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.status",
                        CoreMatchers.is(commandResponse.getStatus())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.price",
                        CoreMatchers.is(commandResponse.getPrice())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.products.size()",
                        CoreMatchers.is(commandResponse.getProducts().size())
                ));
    }

    @Test
    public void CommandController_UpdateCommand_ReturnCommandResponseAndOk() throws Exception {
        Long commandId = 1L;
        when(commandService.updateCommand(commandId, commandRequest)).thenReturn(command);

        ResultActions response = mockMvc.perform(put("/commands/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commandRequest))
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.id",
                        CoreMatchers.is(commandResponse.getId().intValue())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.deliveryDate",
                        CoreMatchers.is(commandResponse.getDeliveryDate().toString())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.status",
                        CoreMatchers.is(commandResponse.getStatus())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.price",
                        CoreMatchers.is(commandResponse.getPrice())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.products.size()",
                        CoreMatchers.is(commandResponse.getProducts().size())
                ));
    }

    @Test
    public void CommandController_DeleteCommand_ReturnNoContent() throws Exception {
        Long commandId = 1L;
        doNothing().when(commandService).deleteCommand(commandId);

        ResultActions response = mockMvc.perform(delete("/commands/1")
                .contentType(MediaType.APPLICATION_JSON)
        );

        response.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void CommandController_CommandToDo_ReturnCommandResponseAndOk() throws Exception {
        Long commandId = 1L;
        Command commandToDo = Command.builder()
                .id(1L)
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.TO_DO)
                .price(11.0)
                .build();

        when(commandService.commandToDo(commandId)).thenReturn(commandToDo);

        ResultActions response = mockMvc.perform(put("/commands/todo/1")
                .contentType(MediaType.APPLICATION_JSON)
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.status",
                        CoreMatchers.is(Command.TO_DO)
                ));
    }

    @Test
    public void CommandController_CommandInProgress_ReturnCommandResponseAndOk() throws Exception {
        Long commandId = 1L;
        Command commandInProgress = Command.builder()
                .id(1L)
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.IN_PROGRESS)
                .price(11.0)
                .build();

        when(commandService.commandInProgress(commandId)).thenReturn(commandInProgress);

        ResultActions response = mockMvc.perform(put("/commands/inprogress/1")
                .contentType(MediaType.APPLICATION_JSON)
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.status",
                        CoreMatchers.is(Command.IN_PROGRESS)
                ));
    }

    @Test
    public void CommandController_CommandPrepared_ReturnCommandResponseAndOk() throws Exception {
        Long commandId = 1L;
        Command commandPrepared = Command.builder()
                .id(1L)
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.PREPARED)
                .price(11.0)
                .build();

        when(commandService.commandPrepared(commandId)).thenReturn(commandPrepared);

        ResultActions response = mockMvc.perform(put("/commands/prepared/1")
                .contentType(MediaType.APPLICATION_JSON)
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.status",
                        CoreMatchers.is(Command.PREPARED)
                ));
    }

    @Test
    public void CommandController_CommandDelivered_ReturnCommandResponseAndOk() throws Exception {
        Long commandId = 1L;
        Command commandDelivered = Command.builder()
                .id(1L)
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.DELIVERED)
                .price(11.0)
                .build();

        when(commandService.commandDelivered(commandId)).thenReturn(commandDelivered);

        ResultActions response = mockMvc.perform(put("/commands/delivered/1")
                .contentType(MediaType.APPLICATION_JSON)
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.status",
                        CoreMatchers.is(Command.DELIVERED)
                ));
    }

    @Test
    public void CommandController_CommandCancelled_ReturnCommandResponseAndOk() throws Exception {
        Long commandId = 1L;
        Command commandCancelled = Command.builder()
                .id(1L)
                .deliveryDate(LocalDate.now().plusDays(5))
                .status(Command.CANCELLED)
                .price(11.0)
                .build();

        when(commandService.commandCancelled(commandId)).thenReturn(commandCancelled);

        ResultActions response = mockMvc.perform(put("/commands/cancelled/1")
                .contentType(MediaType.APPLICATION_JSON)
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.status",
                        CoreMatchers.is(Command.CANCELLED)
                ));
    }
}
