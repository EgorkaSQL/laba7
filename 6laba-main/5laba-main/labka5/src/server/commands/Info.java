package server.commands;

import server.managers.CollectionManager;

public class Info extends ServerCommand
{
    public Info(String username, String password)
    {
        super(null, null, username, password);
    }

    @Override
    public String execute(CollectionManager collectionManager)
    {
        return collectionManager.info();
    }
}