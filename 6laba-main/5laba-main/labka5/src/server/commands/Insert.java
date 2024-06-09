package server.commands;

import client.mycollection.Organization;
import server.managers.CollectionManager;

public class Insert extends ServerCommand
{
    public Insert(Long id, Organization organization, String username, String password)
    {
        super(id, organization, username, password);
        organization.setCreatedBy(username);
    }

    @Override
    public String execute(CollectionManager collectionManager)
    {
        Organization organization = (Organization) getArgument();
        return collectionManager.insert(organization);
    }
}