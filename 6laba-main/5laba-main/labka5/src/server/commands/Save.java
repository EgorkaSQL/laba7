package server.commands;

import server.managers.CollectionManager;

public class Save extends ServerCommand
{
    public Save(String username, String password)
    {
        super(null, null, username, password);
    }

    @Override
    public String execute(CollectionManager collectionManager)
    {
        collectionManager.save();
        return "Коллекция сохранена.";
    }
}