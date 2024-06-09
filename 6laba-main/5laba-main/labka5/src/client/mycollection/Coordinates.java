package client.mycollection;

import java.io.Serializable;

public class Coordinates implements Checker, Serializable {

    private Integer x; //Максимальное значение поля: 583, Поле не может быть null
    private Float y; //Значение поля должно быть больше -744, Поле не может быть null

    public Coordinates(Integer x, Float y)
    {
        this.x = x;
        this.y = y;
    }

    public Integer getX()
    {
        return x;
    }

    public Float getY()
    {
        return y;
    }

    @Override
    public void check()
    {
        if(x > 583)
        {
            throw new IllegalArgumentException("Значение X не может быть больше 583");
        }
        if(x == null)
        {
            throw new IllegalArgumentException("Поле X не может быть null");
        }
        if(y <= -744)
        {
            throw new IllegalArgumentException("Значение Y должно быть больше -744");
        }
        if(y == null)
        {
            throw new IllegalArgumentException("Поле Y не может быть null");
        }
    }

    @Override
    public String toString()
    {
        return "x = " + x +
                " ; " + "y = " + y;
    }
}