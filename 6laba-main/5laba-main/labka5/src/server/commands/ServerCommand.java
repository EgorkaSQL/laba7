package server.commands;

import server.managers.CollectionManager;

import java.io.Serializable;

public abstract class ServerCommand implements Serializable
{
    protected Long id;
    protected Serializable argument;

    protected String username;
    protected String password;

    protected ServerCommand(Long id, Serializable argument, String username, String password)
    {
        this.argument = argument;
        this.id = id;

        this.username = username;
        this.password = password;
    }

    public Long getId()
    {
        return id;
    }

    public Serializable getArgument()
    {
        return argument;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public abstract String execute(CollectionManager collectionManager);
}