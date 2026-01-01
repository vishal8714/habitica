package com.xarrier.databaseapp.Exceptions;

public class UsernameAlreadyExistsException extends RuntimeException{

    public UsernameAlreadyExistsException() {
        super("Username is already taken");
    }
}
