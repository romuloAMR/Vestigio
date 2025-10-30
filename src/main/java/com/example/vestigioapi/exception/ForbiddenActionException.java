package com.example.vestigioapi.exception;

public class ForbiddenActionException extends RuntimeException{
    public ForbiddenActionException(){
        super();
    }
    public ForbiddenActionException(String message){
        super(message);
    }
}
