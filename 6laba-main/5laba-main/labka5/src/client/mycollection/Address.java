package client.mycollection;

import java.io.Serializable;

public class Address implements Checker, Serializable
{

    private String zipCode; //Длина строки не должна быть больше 27, Поле не может быть null
    private Location town; //Поле может быть null

    public Address(String zipCode, Location town)
    {
        this.zipCode = zipCode;
        this.town = town;
    }

    @Override
    public void check()
    {
        if(zipCode == null)
        {
            throw new IllegalArgumentException("ZipCode не может быть null");
        }

        if(zipCode.length() >= 27)
        {
            throw new IllegalArgumentException("Длинна строки ZipCode не может быть больше 27");
        }
    }

    public String getZipCode()
    {
        return this.zipCode;
    }

    public Location getLocation()
    {
        return this.town;
    }

    @Override
    public String toString()
    {
        return "Address{" +
                "zipCode = '" + zipCode + '\'' +
                ", town = " + town +
                '}';
    }
}