package server.commands;

import server.managers.CollectionManager;

public class Clear extends ServerCommand
{
    public Clear(String username, String password)
    {
        super(null, null, username, password);
    }

    @Override
    public String execute(CollectionManager collectionManager)
    {
        return collectionManager.clear();
    }
}