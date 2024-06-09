package server.commands;

import server.managers.CollectionManager;

public class Show extends ServerCommand
{
    public Show(String username, String password)
    {
        super(null, null, username, password);
    }

    @Override
    public String execute(CollectionManager collectionManager)
    {
        return collectionManager.show();
    }
}