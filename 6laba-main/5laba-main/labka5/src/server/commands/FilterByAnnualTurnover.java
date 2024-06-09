package server.commands;

import server.managers.CollectionManager;

public class FilterByAnnualTurnover extends ServerCommand
{
    public FilterByAnnualTurnover(Integer annualTurnover, String username, String password)
    {
        super(null, annualTurnover, username, password);
    }

    @Override
    public String execute(CollectionManager collectionManager)
    {
        Integer annualTurnover = (Integer) getArgument();
        return collectionManager.filterByAnnualTurnover(annualTurnover);
    }
}