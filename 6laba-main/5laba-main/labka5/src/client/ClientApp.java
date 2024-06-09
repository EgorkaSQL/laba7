package client;

import client.generatorss.OrganizationGeneratorr;
import client.mycollection.Organization;

import client.mycollection.OrganizationType;
import server.commands.*;
import server.dataBase.DataBaseManager;
import server.managers.CollectionManager;
import server.managers.ServerCommandManager;
import server.users.AuthenticationManager;

import java.io.*;
import java.net.*;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

public class ClientApp
{
    private static final int BUFFER_SIZE = 65535;
    private static final int SERVER_PORT = 1234;
    private static final String SERVER_ADDRESS = "localhost";
    private static final int TIMEOUT = 2500;

    private static final Scanner scanner = new Scanner(System.in);

    private static AtomicLong currentId = new AtomicLong(0);
    private static List<String> commandHistory = new ArrayList<>();

    public static void main(String[] args)
    {
        try (DatagramChannel channel = DatagramChannel.open())
        {
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(TIMEOUT);

            InetAddress address = InetAddress.getByName(SERVER_ADDRESS);
            OrganizationGeneratorr orgGenerator = new OrganizationGeneratorr();

            System.out.print("Введите имя пользователя: ");
            String username = scanner.nextLine().trim();
            System.out.print("Введите пароль пользователя: ");
            String password = scanner.nextLine().trim();

            ServerCommandManager serverCommandManager = new ServerCommandManager(new CollectionManager(new Hashtable<>()), new AuthenticationManager(new DataBaseManager()));

            System.out.println("Вы уже зарегистрированы в системе? (Yes/No): ");
            String answer = scanner.nextLine().trim();
            if (answer.equalsIgnoreCase("Yes")) {
                String loginResult = serverCommandManager.loginUser(username, password);
                if (loginResult.startsWith("Ошибка")) {
                    System.out.println("Ошибка входа, проверьте введённые данные и перезапустите клиента.");
                    return;
                }
            } else {
                String registerResult = serverCommandManager.registerUser(username, password);
                if (registerResult.startsWith("Ошибка")) {
                    System.out.println("Ошибка регистрации, проверьте введённые данные и перезапустите клиента.");
                    return;
                } else {
                    System.out.println("Успешная регистрация, ваш аккаунт создан.");
                }
            }

            while (true)
            {
                System.out.print("Введите команду: ");
                String commandName = scanner.nextLine().trim();
                Long id = currentId.incrementAndGet();

                if (commandName.equals("insert"))
                {
                    Organization org = orgGenerator.createOrganizationFromUserInput();
                    Insert command = new Insert(id, org, username, password);
                    sendCommand(command, channel, address);
                    commandHistory.add(commandName);
                }
                else if (commandName.equals("update"))
                {
                    System.out.print("Введите ID организации для обновления: ");
                    Long updateId = Long.parseLong(scanner.nextLine().trim());

                    ServerCommand command = new Update(updateId, null, username, password);
                    String response = sendCommand(command, channel, address);

                    if (response.equals("ID существует"))
                    {
                        System.out.println("Введите данные для обновления:");
                        Organization org = orgGenerator.createOrganizationFromUserInput();
                        org.setId(updateId);
                        command = new Update(updateId, org, username, password); // передаем username и password
                        sendCommand(command, channel, address);
                    }
                    else if (response.equals("ID не существует"))
                    {
                        System.out.println("Организации с ID " + updateId + " не существует. Обновление невозможно.");
                    }

                    commandHistory.add(commandName);
                }
                else if (commandName.equals("remove_key"))
                {
                    System.out.print("Введите ID элемента для удаления: ");
                    id = Long.parseLong(scanner.nextLine().trim());
                    RemoveKey command = new RemoveKey(id, username, password);
                    sendCommand(command, channel, address);
                    commandHistory.add(commandName);
                }
                else if (commandName.equals("show"))
                {
                    ServerCommand command = new Show(username, password);
                    sendCommand(command, channel, address);
                    commandHistory.add(commandName);
                }
                else if (commandName.equals("replace_if_greater"))
                {
                    System.out.print("Введите идентификатор организации для замены: ");
                    id = Long.parseLong(scanner.nextLine().trim());
                    System.out.println("Введите данные организации для сравнения:");
                    Organization organization = orgGenerator.createOrganizationFromUserInput();
                    ReplaceIfGreater command = new ReplaceIfGreater(id, organization, username, password);
                    sendCommand(command, channel, address);
                    commandHistory.add(commandName);
                }
                else if (commandName.equals("remove_greater"))
                {
                    System.out.print("Введите идентификатор для сравнения: ");
                    id = Long.parseLong(scanner.nextLine().trim());
                    RemoveGreater command = new RemoveGreater(id, username, password);
                    sendCommand(command, channel, address);
                    commandHistory.add(commandName);
                }
                else if (commandName.equals("remove_all_by_type"))
                {
                    System.out.print("Введите тип организации для удаления: ");
                    OrganizationType type = OrganizationType.valueOf(scanner.nextLine().toUpperCase().trim());
                    RemoveAllByType command = new RemoveAllByType(type, username, password);
                    sendCommand(command, channel, address);
                    commandHistory.add(commandName);
                }
                else if (commandName.equals("info"))
                {
                    Info command = new Info(username, password);
                    sendCommand(command, channel, address);
                    commandHistory.add(commandName);
                }
                else if (commandName.equals("history"))
                {
                    int historySize = Math.min(commandHistory.size(), 13);
                    System.out.println("Последние " + historySize + " команд(ы):");
                    commandHistory.subList(commandHistory.size() - historySize, commandHistory.size())
                            .forEach(System.out::println);
                    commandHistory.add(commandName);
                }
                else if (commandName.equals("help"))
                {
                    Help command = new Help(username, password);
                    sendCommand(command, channel, address);
                    commandHistory.add(commandName);
                }
                else if (commandName.equals("filter_by_annual_turnover"))
                {
                    System.out.print("Введите годовой оборот для фильтрации: ");
                    Integer annualTurnover = Integer.parseInt(scanner.nextLine().trim());
                    FilterByAnnualTurnover command = new FilterByAnnualTurnover(annualTurnover, username, password);
                    sendCommand(command, channel, address);
                    commandHistory.add(commandName);
                }
                else if (commandName.equals("exit"))
                {
                    Exit command = new Exit(username, password);
                    sendCommand(command, channel, address);
                    System.out.println("Завершение работы клиента...");
                    System.exit(0);
                }
                else if (commandName.equals("count_less_than_annual_turnover"))
                {
                    System.out.print("Введите годовой оборот для сравнения: ");
                    Integer annualTurnover = Integer.parseInt(scanner.nextLine().trim());
                    CountLessThanAnnualTurnover command = new CountLessThanAnnualTurnover(annualTurnover, username, password);
                    sendCommand(command, channel, address);
                    commandHistory.add(commandName);
                }
                else if (commandName.equals("clear"))
                {
                    Clear command = new Clear(username, password);
                    sendCommand(command, channel, address);
                    commandHistory.add(commandName);
                }
                else if (commandName.equals("execute_script"))
                {
                    System.out.print("Введите имя файла: ");
                    String fileName = scanner.nextLine().trim();
                    try (Scanner fileScanner = new Scanner(new File(fileName)))
                    {
                        while (fileScanner.hasNextLine())
                        {
                            String fileCommand = fileScanner.nextLine().trim();
                            String[] fileCommandParts = fileCommand.split(" ", 2);
                            String fileCommandName = fileCommandParts[0];
                            String fileCommandArgs = fileCommandParts.length > 1 ? fileCommandParts[1] : null;

                            if (fileCommandName.equals("insert"))
                            {
                                Organization org = orgGenerator.createOrganizationFromUserInput();
                                ServerCommand command = new Insert(Long.parseLong(fileCommandArgs), org, username, password); // добавлен username, password
                                sendCommand(command, channel, address);
                            }
                            else if (fileCommandName.equals("update"))
                            {
                                Long updateId = Long.parseLong(fileCommandArgs);
                                ServerCommand command = new Update(updateId, null, username, password); // добавлен username, password
                                String response = sendCommand(command, channel, address);
                                if (response.equals("ID существует"))
                                {
                                    System.out.println("Введите данные для обновления:");
                                    Organization org = orgGenerator.createOrganizationFromUserInput();
                                    command = new Update(updateId, org, username, password); // добавлен username, password
                                    sendCommand(command, channel, address);
                                }
                                else if (response.equals("ID не существует"))
                                {
                                    System.out.println("Организации с ID " + updateId + " не существует. Обновление невозможно.");
                                }
                            }
                        }
                    }
                    catch (FileNotFoundException e)
                    {
                        System.out.println("Файл " + fileName + " не найден.");
                    }
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("Произошла ошибка при взаимодействии с сервером: " + e.getMessage());
        }
    }

    private static byte[] serializeCommand(ServerCommand command) throws IOException
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(command);
        objectOutputStream.flush();
        return byteArrayOutputStream.toByteArray();
    }

    private static final int MAX_RETRIES = 3;

    private static String sendCommand(ServerCommand command, DatagramChannel channel, InetAddress address) throws IOException
    {
        byte[] commandBytes = serializeCommand(command);
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(commandBytes, commandBytes.length, address, SERVER_PORT);
        int retries = 0;
        while (retries < MAX_RETRIES)
        {
            try
            {
                socket.send(packet);

                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                socket.setSoTimeout(TIMEOUT);
                socket.receive(responsePacket);

                String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
                System.out.println("Ответ сервера:\n" + response);
                return response.trim();
            }
            catch (SocketTimeoutException e)
            {
                System.out.println("Сервер не отвечает, попытка " + (retries + 1) + " из " + MAX_RETRIES);
                retries++;
            }
            catch (IOException e)
            {
                System.out.println("Произошла ошибка при взаимодействии с сервером: " + e.getMessage());
                throw e;
            }
        }
        return "";
    }
}