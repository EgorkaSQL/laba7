package server.commands;

import server.managers.CollectionManager;

public class Exit extends ServerCommand
{
    public Exit(String username, String password)
    {
        super(null, null, username, password);
    }

    @Override
    public String execute(CollectionManager collectionManager)
    {
        return "Завершение работы!";
    }
}