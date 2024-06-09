package server.commands;

import client.mycollection.Organization;
import server.managers.CollectionManager;

public class ReplaceIfGreater extends ServerCommand
{
    public ReplaceIfGreater(Long id, Organization organization, String username, String password)
    {
        super(id, organization, username, password);
    }

    @Override
    public String execute(CollectionManager collectionManager)
    {
        Organization newOrganization = (Organization) getArgument();
        String result;
        if (collectionManager.containsId(getId()))
        {
            Organization existingOrganization = collectionManager.getOrganizations().get(getId());
            if (newOrganization.compareTo(existingOrganization) > 0)
            {
                newOrganization.setId(getId());
                collectionManager.update(getId(), newOrganization);
                result = "Значение по ключу " + getId() + " было заменено на новое.";
            }
            else
            {
                result = "Новое значение не больше старого. Замена не произведена.";
            }
        }
        else
        {
            result = "Нет элемента с ID " + getId() + " в коллекции.";
        }
        return result;
    }
}