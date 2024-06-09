package server.commands;

import server.managers.CollectionManager;

public class RemoveKey extends ServerCommand
{
    public RemoveKey(Long id, String username, String password)
    {
        super(id, null, username, password);
    }

    @Override
    public String execute(CollectionManager collectionManager)
    {
        return collectionManager.removeKey(getId());
    }
}
