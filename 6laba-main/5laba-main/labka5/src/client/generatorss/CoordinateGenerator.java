package client.generatorss;

import client.mycollection.Coordinates;

public class CoordinateGenerator extends AbstractGenerator
{
    public Coordinates createCoordinatesFromUserInput()
    {
        int x = getValidatedInt("Введите координаты X: ", null, 583);
        float y = getValidatedFloat("Введите координаты Y: ", -744f, null);

        return new Coordinates(x, y);
    }
}
