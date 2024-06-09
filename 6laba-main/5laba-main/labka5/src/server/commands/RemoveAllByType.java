package server.commands;

import client.mycollection.OrganizationType;
import server.managers.CollectionManager;

public class RemoveAllByType extends ServerCommand
{
    public RemoveAllByType(OrganizationType type, String username, String password)
    {
        super(null, type, username, password);
    }

    @Override
    public String execute(CollectionManager collectionManager)
    {
        OrganizationType type = (OrganizationType) getArgument();
        return collectionManager.removeAllByType(type);
    }
}
