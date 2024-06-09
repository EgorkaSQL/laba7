package client.mycollection;

import java.io.Serializable;

public class Location implements Checker, Serializable {
    private int x;
    private float y;

    private String name; //Длина строки не должна быть больше 255, Поле может быть null

    public Location(int x, float y, String name)
    {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    @Override
    public void check()
    {
        if(name.length() > 255)
        {
            throw new IllegalArgumentException("Поле name в Location не может быть больше 255");
        }
    }

    public String getName()
    {
        return this.name;
    }

    public Integer getX()
    {
        return this.x;
    }

    public Float getY()
    {
        return this.y;
    }

    @Override
    public String toString()
    {
        return "Location{" +
                "x = " + x +
                ", y = " + y +
                ", name = '" + name + '\'' +
                '}';
    }
}