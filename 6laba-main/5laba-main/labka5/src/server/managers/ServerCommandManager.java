package server.managers;

import server.commands.ServerCommand;
import server.dataBase.DataBaseManager;
import server.users.User;
import server.users.AuthenticationManager;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServerCommandManager
{
    private final CollectionManager collectionManager;
    private final AuthenticationManager authManager;
    private int commandCounter = 0;
    private final Map<Integer, String> commandHistory = new LinkedHashMap<>();

    public ServerCommandManager(CollectionManager collectionManager, AuthenticationManager authManager)
    {
        this.collectionManager = collectionManager;
        this.authManager = authManager;
    }

    private void addToHistory(String commandName)
    {
        if (commandCounter >= 13)
        {
            Integer firstKey = commandHistory.keySet().iterator().next();
            commandHistory.remove(firstKey);
        }
        commandHistory.put(commandCounter++, commandName);
    }

    public String executeCommand(ServerCommand command)
    {
        AuthenticationManager authManager = new AuthenticationManager(new DataBaseManager());
        User user = authManager.login(command.getUsername(), command.getPassword());
        if (user == null)
        {
            return "Ошибка аутентикации. Пожалуйста, попробуйте снова.";
        }

        this.collectionManager.setCurrentUser(user);

        this.addToHistory(command.getClass().getSimpleName());
        return command.execute(collectionManager);
    }

    public String registerUser(String username, String password)
    {
        User newUser = authManager.register(username, password);
        if (newUser != null)
        {
            return "Регистрация прошла успешно.";
        }
        else
        {
            return "Ошибка регистрации. Пользователь с этим именем уже существует.";
        }
    }

    public String loginUser(String username, String password)
    {
        User user = authManager.login(username, password);
        if (user != null)
        {
            collectionManager.setCurrentUser(user);
            return "Успешный вход.";
        }
        else
        {
            return "Ошибка входа. Проверьте введенное имя пользователя и пароль.";
        }
    }

    public Map<Integer, String> getCommandHistory()
    {
        return commandHistory;
    }
}