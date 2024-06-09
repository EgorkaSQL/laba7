package server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import client.mycollection.Organization;
import server.commands.ServerCommand;
import server.dataBase.DataBaseManager;
import server.managers.CollectionManager;
import server.managers.ServerCommandManager;
import server.users.AuthenticationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerApp
{
    private static final int BUFFER_SIZE = 65535;
    private static final Logger LOGGER = LogManager.getLogger(ServerApp.class);
    private static ServerCommandManager commandManager;
    private static Hashtable<Long, Organization> organizations;

    public static void main(String[] args) throws IOException
    {
        LOGGER.info("Начало работы сервера...");
        organizations = new Hashtable<>();

        DataBaseManager databaseManager = new DataBaseManager();
        CollectionManager collectionManager = new CollectionManager(organizations);
        AuthenticationManager authManager = new AuthenticationManager(databaseManager);
        commandManager = new ServerCommandManager(collectionManager, authManager);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Сохранение состояния коллекции перед выходом...");
            collectionManager.save();
        }));

        int port = 1234;
        DatagramChannel channel = DatagramChannel.open();
        channel.bind(new InetSocketAddress(port));
        channel.configureBlocking(false);
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        LOGGER.info("Сервер запущен и прослушивает порт {}", port);

        ExecutorService requestExecutor = Executors.newCachedThreadPool();
        ExecutorService responseExecutor = Executors.newFixedThreadPool(10);

        while (true)
        {
            buffer.clear();
            SocketAddress clientAddress = channel.receive(buffer);

            if (clientAddress != null)
            {
                ByteBuffer bufferForThread = ByteBuffer.allocate(BUFFER_SIZE);
                bufferForThread.put(buffer.array());
                bufferForThread.flip();

                requestExecutor.execute(() -> {
                    try
                    {
                        byte[] data = new byte[bufferForThread.remaining()];
                        bufferForThread.get(data);

                        ServerCommand command = deserializeCommand(data);
                        String result = commandManager.executeCommand(command);

                        responseExecutor.execute(() -> {
                            ByteBuffer responseBuffer = ByteBuffer.allocate(BUFFER_SIZE);
                            responseBuffer.put(result.getBytes(StandardCharsets.UTF_8));
                            responseBuffer.flip();
                            try
                            {
                                channel.send(responseBuffer, clientAddress);
                                LOGGER.info("Отправлен ответ клиенту: {}", clientAddress);
                            }
                            catch (IOException e)
                            {
                                LOGGER.error("Ошибка при отправке ответа клиенту: " + e.getMessage());
                            }
                        });
                    }
                    catch (IOException | ClassNotFoundException e)
                    {
                        LOGGER.error("Ошибка при десериализации команды: " + e.getMessage());
                    }
                });
            }
        }
    }

    private static ServerCommand deserializeCommand(byte[] commandBytes) throws IOException, ClassNotFoundException
    {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(commandBytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream))
        {
            return (ServerCommand) objectInputStream.readObject();
        }
    }
}