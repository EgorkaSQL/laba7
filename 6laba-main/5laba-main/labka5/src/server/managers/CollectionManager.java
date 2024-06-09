package server.managers;

import client.mycollection.*;
import server.dataBase.DataBaseManager;
import server.users.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class CollectionManager
{
    private Hashtable<Long, Organization> organizations;
    private LocalDate creationDate;
    private User currentUser;

    public CollectionManager(Hashtable<Long, Organization> organizations)
    {
        this.organizations = organizations;
        this.creationDate = LocalDate.now();
        loadData();
    }

    public void setCurrentUser(User user)
    {
        this.currentUser = user;
    }

    public User getCurrentUser()
    {
        return this.currentUser;
    }

    private void loadData()
    {
        DataBaseManager databaseManager = new DataBaseManager();
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM organizations"))
        {

            while (resultSet.next())
            {
                long id = resultSet.getLong("id");
                String name = resultSet.getString("name");

                Integer x = resultSet.getInt("coordinates_x");
                Float y = resultSet.getFloat("coordinates_y");
                Coordinates coordinates = new Coordinates(x, y);

                int annualTurnover = resultSet.getInt("annual_turnover");

                OrganizationType type = OrganizationType.valueOf(resultSet.getString("type"));

                String zipCode = resultSet.getString("official_address_zip_code");
                Integer locationX = resultSet.getInt("official_address_town_x");
                Float locationY = resultSet.getFloat("official_address_town_y");
                String locationName = resultSet.getString("official_address_town_name");
                Location location = new Location(locationX, locationY, locationName);
                Address officialAddress = new Address(zipCode, location);

                Organization org = new Organization(name, coordinates, annualTurnover, type, officialAddress);
                org.setId(id);
                organizations.put(id, org);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error loading data from the database", e);
        }
    }

    public boolean containsId(long id)
    {
        return organizations.containsKey(id);
    }

    public String help()
    {
        return ("Введите одну из следующих команд:\n" +
                "help : вывести справку по доступным командам\n" +
                "info: вывести в стандартный поток вывода информацию о коллекции\n" +
                "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                "insert null {element} : добавить новый элемент с заданным ключом\n" +
                "update id {element} : обновить значение элемента коллекции, id которого равен заданному\n" +
                "remove_key null : удалить элемент из коллекции по его ключу\n" +
                "clear : очистить коллекцию\n" +
                "execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
                "exit : завершить программу (без сохранения в файл)\n" +
                "remove_greater {element} : удалить из коллекции все элементы, превышающие заданный\n" +
                "history : вывести последние 13 команд (без их аргументов)\n" +
                "replace_if_greater null {element} : заменить значение по ключу, если новое значение больше старого\n" +
                "remove_all_by_type type : удалить из коллекции все элементы, значение поля type которого эквивалентно заданному\n" +
                "count_less_than_annual_turnover annualTurnover : вывести количество элементов, значение поля annualTurnover которых меньше заданного\n" +
                "filter_by_annual_turnover annualTurnover : вывести элементы, значение поля annualTurnover которых равно заданному");
    }

    public Hashtable<Long, Organization> getOrganizations()
    {
        return organizations;
    }

    public synchronized void save()
    {
        DataBaseManager databaseManager = new DataBaseManager();
        Connection connection;

        try
        {
            connection = databaseManager.getConnection();

            String updateSql = "UPDATE organizations SET name = ?, coordinates_x = ?, coordinates_y = ?, " +
                    "annual_turnover = ?, type = ?, official_address_zip_code = ?, official_address_town_name = ?, official_address_town_x = ?, official_address_town_y = ?, created_by=?, creation_date = ? WHERE id = ?";

            String insertSql = "INSERT INTO organizations (name, coordinates_x, coordinates_y, annual_turnover, type, official_address_zip_code, official_address_town_name, official_address_town_x, official_address_town_y, created_by, creation_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            for (Map.Entry<Long, Organization> entry : organizations.entrySet())
            {
                Long id = entry.getKey();
                Organization org = entry.getValue();

                PreparedStatement selectStatement = connection.prepareStatement(
                        "SELECT * FROM organizations WHERE id = ?");
                selectStatement.setLong(1, id);
                ResultSet resultSet = selectStatement.executeQuery();

                if (resultSet.next())
                {
                    PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                    setUpdateStatementParameters(updateStatement, id, org);
                    updateStatement.executeUpdate();
                }
                else
                {
                    PreparedStatement insertStatement = connection.prepareStatement(insertSql);
                    setInsertStatementParameters(insertStatement, org);
                    insertStatement.executeUpdate();
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error saving data to the database", e);
        }
    }

    private void setInsertStatementParameters(PreparedStatement preparedStatement, Organization org) throws SQLException
    {
        preparedStatement.setString(1, org.getName());
        preparedStatement.setInt(2, org.getCoordinates().getX());
        preparedStatement.setFloat(3, org.getCoordinates().getY());
        preparedStatement.setInt(4, org.getAnnualTurnover());
        preparedStatement.setString(5, org.getType().name());
        preparedStatement.setString(6, org.getOfficialAddress().getZipCode());
        preparedStatement.setString(7, org.getOfficialAddress().getLocation().getName());
        preparedStatement.setInt(8, org.getOfficialAddress().getLocation().getX());
        preparedStatement.setFloat(9, org.getOfficialAddress().getLocation().getY());
        preparedStatement.setString(10, org.getCreatedBy());
        preparedStatement.setDate(11, java.sql.Date.valueOf(org.getCreationDate()));
    }

    private void setUpdateStatementParameters(PreparedStatement preparedStatement, Long id, Organization org) throws SQLException
    {
        preparedStatement.setString(1, org.getName());
        preparedStatement.setInt(2, org.getCoordinates().getX());
        preparedStatement.setFloat(3, org.getCoordinates().getY());
        preparedStatement.setInt(4, org.getAnnualTurnover());
        preparedStatement.setString(5, org.getType().name());
        preparedStatement.setString(6, org.getOfficialAddress().getZipCode());
        preparedStatement.setString(7, org.getOfficialAddress().getLocation().getName());
        preparedStatement.setInt(8, org.getOfficialAddress().getLocation().getX());
        preparedStatement.setFloat(9, org.getOfficialAddress().getLocation().getY());
        preparedStatement.setString(10, org.getCreatedBy());
        preparedStatement.setDate(11, java.sql.Date.valueOf(org.getCreationDate()));
        preparedStatement.setLong(12, id);
    }

    public synchronized String info()
    {
        return ("Тип коллекции: " + organizations.getClass().getName() + "\nДата инициализации: " + creationDate + "\nКоличество элементов: " + organizations.size());
    }

    public synchronized String show()
    {
        if (organizations.isEmpty())
        {
            return "Коллекция пуста.";
        }
        else
        {
            return organizations.values().stream()
                    .sorted(Comparator.comparing(Organization::getName))
                    .map(Organization::toString)
                    .collect(Collectors.joining("\n"));
        }
    }

    public synchronized String insert(Organization organization)
    {
        organization.setCreatedBy(getCurrentUser().getUsername());

        DataBaseManager databaseManager = new DataBaseManager();
        try (Connection connection = databaseManager.getConnection())
        {
            String sql = "INSERT INTO organizations (name, coordinates_x, coordinates_y, annual_turnover, type, official_address_zip_code, official_address_town_name, official_address_town_x, official_address_town_y, created_by, creation_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, organization.getName());
            preparedStatement.setInt(2, organization.getCoordinates().getX());
            preparedStatement.setFloat(3, organization.getCoordinates().getY());
            preparedStatement.setInt(4, organization.getAnnualTurnover());
            preparedStatement.setString(5, organization.getType().name());
            preparedStatement.setString(6, organization.getOfficialAddress().getZipCode());
            preparedStatement.setString(7, organization.getOfficialAddress().getLocation().getName());
            preparedStatement.setInt(8, organization.getOfficialAddress().getLocation().getX());
            preparedStatement.setFloat(9, organization.getOfficialAddress().getLocation().getY());
            preparedStatement.setString(10, organization.getCreatedBy());
            preparedStatement.setDate(11, java.sql.Date.valueOf(organization.getCreationDate()));

            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next())
            {
                organization.setId(rs.getInt(1));

                long id = organization.getId();
                organizations.put(id, organization);

                return "Добавлен элемент с ID " + id;
            }
            else
            {
                return "Ошибка при добавлении элемента";
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return "Ошибка при добавлении элемента: " + e.getMessage();
        }
    }

    public synchronized String update(Long id, Organization organization)
    {
        if (organizations.containsKey(id))
        {
            Organization existingOrg = organizations.get(id);

            String created_by = existingOrg.getCreatedBy();
            if (created_by != null && !created_by.equals(getCurrentUser().getUsername()))
            {
                return "У вас нет прав на изменение этого элемента!";
            }

            DataBaseManager databaseManager = new DataBaseManager();
            try (Connection connection = databaseManager.getConnection())
            {
                String sql = "UPDATE organizations SET name = ?, coordinates_x = ?, coordinates_y = ?, annual_turnover = ?, type = ?, official_address_zip_code = ?, official_address_town_name = ?, official_address_town_x = ?, official_address_town_y = ?, created_by = ?, creation_date = ? WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setString(1, organization.getName());
                preparedStatement.setInt(2, organization.getCoordinates().getX());
                preparedStatement.setFloat(3, organization.getCoordinates().getY());
                preparedStatement.setInt(4, organization.getAnnualTurnover());
                preparedStatement.setString(5, organization.getType().name());
                preparedStatement.setString(6, organization.getOfficialAddress().getZipCode());
                preparedStatement.setString(7, organization.getOfficialAddress().getLocation().getName());
                preparedStatement.setInt(8, organization.getOfficialAddress().getLocation().getX());
                preparedStatement.setFloat(9, organization.getOfficialAddress().getLocation().getY());
                preparedStatement.setString(10, organization.getCreatedBy());
                preparedStatement.setDate(11, java.sql.Date.valueOf(LocalDate.now()));
                preparedStatement.setLong(12, id);

                int updatedRows = preparedStatement.executeUpdate();

                if (updatedRows > 0)
                {
                    existingOrg.setName(organization.getName());
                    existingOrg.setCoordinates(organization.getCoordinates());
                    existingOrg.setAnnualTurnover(organization.getAnnualTurnover());
                    existingOrg.setType(organization.getType());
                    existingOrg.setOfficialAddress(organization.getOfficialAddress());

                    return "Элемент с ID " + id + " был обновлен.";
                }
                else
                {
                    return "Произошла ошибка при обновлении элемента с ID " + id;
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                return "Ошибка при обновлении элемента с ID " + id + ": " + e.getMessage();
            }
        }
        else
        {
            return "Элемент с ID " + id + " не существует. Обновление невозможно.";
        }
    }

    public synchronized String removeKey(Long id)
    {
        if (organizations.containsKey(id))
        {
            Organization existingOrg = organizations.get(id);

            if (!existingOrg.getCreatedBy().equals(getCurrentUser().getUsername()))
            {
                return "У вас нет прав на удаление этого элемента!";
            }

            DataBaseManager databaseManager = new DataBaseManager();
            try (Connection connection = databaseManager.getConnection())
            {
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM organizations WHERE id = ?");
                preparedStatement.setLong(1, id);
                int deletedRows = preparedStatement.executeUpdate();

                if (deletedRows > 0)
                {
                    organizations.remove(id);
                    return ("Элемент с ID " + id + " удален.");
                }
                else
                {
                    return ("Произошла ошибка при удалении элемента с ID " + id);
                }
            }
            catch (SQLException e)
            {
                throw new RuntimeException("Error removing entry from the database", e);
            }
        }
        else
        {
            return ("Элемент с ID " + id + " не найден. Удаление невозможно.");
        }
    }

    public synchronized String clear()
    {
        DataBaseManager databaseManager = new DataBaseManager();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM organizations"))
        {
            preparedStatement.executeUpdate();

            organizations.clear();

            return ("Коллекция была очищена.");
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error removing all entries from the database", e);
        }
    }

    public synchronized String removeAllByType(OrganizationType type)
    {
        int initialSize = organizations.size();

        DataBaseManager databaseManager = new DataBaseManager();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM organizations WHERE type = ?"))
        {
            preparedStatement.setString(1, type.name());
            preparedStatement.executeUpdate();

            organizations.values().removeIf(org -> org.getType().equals(type));

            int newSize = organizations.size();

            if (initialSize > newSize)
            {
                return ("Были удалены элементы. Размер коллекции уменьшился на " + (initialSize - newSize));
            }
            else
            {
                return ("Удаление элементов не произошло. Размер коллекции не изменился.");
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error removing entries from the database", e);
        }
    }

    public synchronized int countLessThanAnnualTurnover(int annualTurnover)
    {
        return (int) organizations.values().stream()
                .filter(org -> org.getAnnualTurnover() < annualTurnover)
                .count();
    }

    public synchronized String filterByAnnualTurnover(int annualTurnover)
    {
        return organizations.values().stream()
                .filter(org -> org.getAnnualTurnover() == annualTurnover)
                .map(Organization::toString)
                .collect(Collectors.joining("\n"));
    }
}