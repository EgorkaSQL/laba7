package server.commands;

import server.managers.CollectionManager;

public class Help extends ServerCommand
{
    public Help(String username, String password)
    {
        super(null, null, username, password);
    }

    @Override
    public String execute(CollectionManager collectionManager)
    {
        return collectionManager.help();
    }
}