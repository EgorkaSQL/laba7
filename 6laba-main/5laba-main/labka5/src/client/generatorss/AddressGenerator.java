package client.generatorss;

import client.mycollection.*;

public class AddressGenerator extends AbstractGenerator
{
    private LocationGenerator locationGenerator = new LocationGenerator();

    public Address createAddressFromUserInput()
    {
        String zipCode = getValidatedString("Введите почтовый индекс: ", 27);

        Location location = locationGenerator.createLocationFromUserInput();

        return new Address(zipCode, location);
    }
}