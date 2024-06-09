package server.commands;

import client.mycollection.Organization;
import server.managers.CollectionManager;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class RemoveGreater extends ServerCommand
{
    public RemoveGreater(Long id, String username, String password)
    {
        super(id, null, username, password);
    }

    @Override
    public String execute(CollectionManager collectionManager)
    {
        Long id = getId();
        int removedCount = 0;
        Hashtable<Long, Organization> organizations = collectionManager.getOrganizations();
        Set<Long> keys = new HashSet<>(organizations.keySet());
        for (Long orgId : keys)
        {
            if (orgId > id)
            {
                organizations.remove(orgId);
                removedCount++;
            }
        }
        return "Элементы с ID больше, чем " + id + ", удалены из коллекции. Всего удалено " + removedCount + " элементов.";
    }
}