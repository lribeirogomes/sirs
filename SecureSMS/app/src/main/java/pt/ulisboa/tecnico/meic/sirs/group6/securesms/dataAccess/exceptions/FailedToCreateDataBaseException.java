package pt.ulisboa.tecnico.meic.sirs.group6.securesms.dataAccess.exceptions;

/**
 * Created by joao on 11/14/15.
 */
public class FailedToCreateDataBaseException extends Exception{
    public FailedToCreateDataBaseException(){
        super("Failed to create database.");
    }
}
