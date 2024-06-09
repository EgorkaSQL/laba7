package client.generatorss;

import client.mycollection.Location;

public class LocationGenerator extends AbstractGenerator
{
    public Location createLocationFromUserInput()
    {
        int x = getValidatedInt("Введите координаты города X: ", null, null);
        float y = getValidatedFloat("Введите координаты города Y: ", null, null);
        String name = getValidatedString("Введите название города: ", 255);
        return new Location(x, y, name);
    }
}