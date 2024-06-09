package client.generatorss;

import client.mycollection.OrganizationType;

import java.util.Scanner;

public class OrganizationTypeGenerator extends AbstractGenerator
{
    private Scanner scanner = new Scanner(System.in);

    public OrganizationType inputOrganizationType()
    {
        while (true)
        {
            try
            {
                System.out.println("Введите тип огранизации (Выбрать один из предложенных):");
                for (OrganizationType type : OrganizationType.values())
                {
                    System.out.println("- " + type.name());
                }
                System.out.print("Ваш выбор: ");
                String orgType = scanner.nextLine();

                return OrganizationType.valueOf(orgType.toUpperCase());
            }
            catch (IllegalArgumentException e)
            {
                System.out.println("Неверный ввод. Введите одно из предложенных значений.");
            }
        }
    }
}
