package com.frcalderon.commands.exceptions;

public class CommandNotFoundException extends RuntimeException {

    public CommandNotFoundException() {
        super("Command not found");
    }
}
