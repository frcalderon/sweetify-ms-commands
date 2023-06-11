package com.frcalderon.commands.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.frcalderon.commands.controller.dto.CommandRequest;
import com.frcalderon.commands.controller.dto.CommandResponse;
import com.frcalderon.commands.model.Command;
import com.frcalderon.commands.service.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/commands")
public class CommandController {

    @Autowired
    private CommandService commandService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommandResponse> getAllCommands() {
        List<Command> commands = this.commandService.getAllCommands();
        return commands.stream().map(CommandResponse::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommandResponse getCommand(@PathVariable Long id) {
        Command command = this.commandService.getCommand(id);
        return new CommandResponse(command);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommandResponse createCommand(@RequestBody CommandRequest commandRequest) throws JsonProcessingException {
        Command command = this.commandService.createCommand(commandRequest);
        return new CommandResponse(command);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommandResponse updateCommand(@PathVariable Long id, @RequestBody CommandRequest commandRequest) throws JsonProcessingException {
        Command command = this.commandService.updateCommand(id, commandRequest);
        return new CommandResponse(command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommand(@PathVariable Long id) throws JsonProcessingException {
        this.commandService.deleteCommand(id);
    }

    @PutMapping("/todo/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommandResponse toDoCommand(@PathVariable Long id) throws JsonProcessingException {
        Command command = this.commandService.commandToDo(id);
        return new CommandResponse(command);
    }

    @PutMapping("/inprogress/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommandResponse inProgressCommand(@PathVariable Long id) {
        Command command = this.commandService.commandInProgress(id);
        return new CommandResponse(command);
    }

    @PutMapping("/prepared/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommandResponse preparedCommand(@PathVariable Long id) {
        Command command = this.commandService.commandPrepared(id);
        return new CommandResponse(command);
    }

    @PutMapping("/delivered/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommandResponse deliveredCommand(@PathVariable Long id) {
        Command command = this.commandService.commandDelivered(id);
        return new CommandResponse(command);
    }

    @PutMapping("/cancelled/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommandResponse cancelledCommand(@PathVariable Long id) throws JsonProcessingException {
        Command command = this.commandService.commandCancelled(id);
        return new CommandResponse(command);
    }
}
