package com.frcalderon.commands.exceptions;

public class CommandCouldNotBeUpdatedException extends RuntimeException {

    public CommandCouldNotBeUpdatedException() {
        super("Command could not be updated");
    }
}
