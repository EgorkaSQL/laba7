package server.commands;

import client.mycollection.Organization;
import server.managers.CollectionManager;

public class Update extends ServerCommand
{
    public Update(Long id, Organization organization, String username, String password)
    {
        super(id, organization, username, password);
    }

    @Override
    public String execute(CollectionManager collectionManager)
    {
        if (getArgument() == null) {
            if (collectionManager.getOrganizations().containsKey(getId()))
            {
                return "ID существует";
            }
            else
            {
                return "ID не существует";
            }
        }
        else
        {
            Organization organization = (Organization) getArgument();
            collectionManager.update(getId(), organization);
            return "Элемент с ID " + getId() + " был обновлен.";
        }
    }
}