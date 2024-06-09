package client.generatorss;

import client.mycollection.*;

public class OrganizationGeneratorr
{
    private final AbstractGenerator stringGenerator = new AbstractGenerator() { };
    private final AddressGenerator addressGenerator = new AddressGenerator();
    private final CoordinateGenerator coordinatesGenerator = new CoordinateGenerator();
    private final OrganizationTypeGenerator organizationTypeGenerator = new OrganizationTypeGenerator();

    public Organization createOrganizationFromUserInput()
    {
        System.out.println("\nВведите данные для создания новой организации:");

        String name = stringGenerator.getValidatedString("Название: ", 200);
        Coordinates coordinates = coordinatesGenerator.createCoordinatesFromUserInput();
        int annualTurnover = stringGenerator.getValidatedInt("Годовой оборот: ", 0, null);
        OrganizationType organizationType = organizationTypeGenerator.inputOrganizationType();
        Address address = addressGenerator.createAddressFromUserInput();

        return new Organization(name, coordinates, annualTurnover, organizationType, address);
    }
}