package client.mycollection;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Comparator;

public class Organization implements Serializable, Checker, Comparable<Organization>
{
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    private String name; //Поле не может быть null, Строка не может быть пустой

    private Coordinates coordinates; //Поле не может быть null

    private LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    private int annualTurnover; //Значение поля должно быть больше 0

    private OrganizationType type; //Поле может быть null

    private Address officialAddress; //Поле не может быть null

    private String createdBy;

    public Organization(String name, Coordinates coordinates, int annualTurnover, OrganizationType type, Address officialAddress)
    {
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = LocalDate.now();
        this.annualTurnover = annualTurnover;
        this.type = type;
        this.officialAddress = officialAddress;
    }

    @Override
    public int compareTo(Organization other)
    {
        return Integer.compare(this.annualTurnover, other.annualTurnover);
    }

    @Override
    public void check()
    {
        if(name == null)
        {
            throw new IllegalArgumentException("Поле name у организации не может быть null");
        }
        if(name.isEmpty())
        {
            throw new IllegalArgumentException("Поле name не может быть пустым");
        }
        if(coordinates == null)
        {
            throw new IllegalArgumentException("Поле coordinates не может быть null");
        }
        if(annualTurnover <= 0)
        {
            throw new IllegalArgumentException("Поле annualTurnover должно быть больше 0");
        }
        if(officialAddress == null)
        {
            throw new IllegalArgumentException("Поле officialAddress не может быть null");
        }
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public int getAnnualTurnover()
    {
        return annualTurnover;
    }

    public OrganizationType getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public Coordinates getCoordinates()
    {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates)
    {
        this.coordinates = coordinates;
    }

    public Address getOfficialAddress()
    {
        return officialAddress;
    }

    public LocalDate getCreationDate()
    {
        return creationDate;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setAnnualTurnover(int annualTurnover)
    {
        this.annualTurnover = annualTurnover;
    }

    public void setType(OrganizationType type)
    {
        this.type = type;
    }

    public void setOfficialAddress(Address officialAddress)
    {
        this.officialAddress = officialAddress;
    }

    @Override
    public String toString()
    {
        return "Organization{" +
                "id = " + id +
                ", name = '" + name + '\'' +
                ", coordinates = " + coordinates +
                ", creationDate = " + creationDate +
                ", annualTurnover = " + annualTurnover +
                ", type = " + type +
                ", officialAddress = " + officialAddress +
                '}';
    }
}