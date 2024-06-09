package client.generatorss;
import java.util.Scanner;

public abstract class AbstractGenerator
{
    protected Scanner scanner = new Scanner(System.in);

    protected String getValidatedString(String prompt, Integer maxLength)
    {
        String input;
        do
        {
            System.out.print(prompt);
            input = scanner.nextLine();

            if (maxLength != null && input.length() > maxLength)
            {
                System.out.println("Слишком длинный запрос. Попробуйте еще раз.");
            }
            else if (input.isEmpty())
            {
                System.out.println("Запрос пуст. Попробуйте еще раз.");
            }
        }
        while ((maxLength != null && input.length() > maxLength) || input.isEmpty());

        return input;
    }

    protected int getValidatedInt(String prompt, Integer min, Integer max)
    {
        while (true)
        {
            try
            {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine());

                if ((min != null && value < min) || (max != null && value > max))
                {
                    throw new NumberFormatException();
                }

                return value;
            }
            catch (NumberFormatException e)
            {
                String errorMessage = "Неверный ввод. Пожалуйста, введите целое число ";
                if (min != null)
                {
                    errorMessage += "больше или равные " + min;
                    if (max != null)
                    {
                        errorMessage += " и ";
                    }
                }
                if (max != null)
                {
                    errorMessage += "меньше или равные " + max;
                }
                System.out.println(errorMessage);
            }
        }
    }

    protected float getValidatedFloat(String prompt, Float min, Float max)
    {
        while (true)
        {
            try
            {
                System.out.print(prompt);
                float value = Float.parseFloat(scanner.nextLine());

                if ((min != null && value < min) || (max != null && value > max))
                {
                    throw new NumberFormatException();
                }

                return value;
            }
            catch (NumberFormatException e)
            {
                String errorMessage = "Неверный ввод. Пожалуйста, введите десятичное число ";
                if (min != null)
                {
                    errorMessage += "больше, чем " + min;
                    if (max != null)
                    {
                        errorMessage += " и ";
                    }
                }
                if (max != null)
                {
                    errorMessage += "меньше, чем " + max;
                }
                System.out.println(errorMessage);
            }
        }
    }
}