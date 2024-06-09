package server.commands;

import server.managers.CollectionManager;
import server.managers.ServerCommandManager;

public class History extends ServerCommand
{

    private ServerCommandManager commandManager;

    public History(ServerCommandManager commandManager, String username, String password)
    {
        super(null, null, username, password);
        this.commandManager = commandManager;
    }

    @Override
    public String execute(CollectionManager collectionManager)
    {
        StringBuilder result = new StringBuilder();
        result.append("Последние ")
                .append(Math.min(commandManager.getCommandHistory().size(), 13))
                .append(" команд:\n");
        for (String commandHistory : commandManager.getCommandHistory().values())
        {
            result.append(commandHistory).append("\n");
        }
        return result.toString();
    }
}