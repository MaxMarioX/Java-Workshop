package pl.coderslab;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.lang3.ArrayUtils;

public class Main {

    //Lista zadań pobranych z pliku
    static List<String> TaskList = new ArrayList<>();
    static String[][] Tasks;

    public static void main(String[] args)
    {
        //Tekst główny w menu
        String text = "Please select an option: ";
        //Lista dostępnych opcji w menu
        String[] options = {"add","remove","list","exit"};
        //Plik
        String fName = "/home/mariusz/tasks.csv";
        //Co wybrał użytkownik
        String option;
        //Zapisujemy wynik sprawdzenia przez metodę CheckOption
        int uOption;

        Scanner GetOption = new Scanner(System.in);

        //Lista wczytanych task'ów
        Tasks = LoadData(fName);

        //Warunkiem wyjścia z programu jest wpisanie słowa exit
        do {
            //Wyświetlamy Menu
            DrawMenu(text,options);

            //Znak zachęty
            System.out.print("> ");

            //Pobieramy dane od użytkownika
            option = GetOption.next();

            //Sprawdzamy co wybrał użytkownik
            if((uOption = CheckOption(options,option)) != -1) {
                if (uOption == 0) //add
                    AddTask();

                else if (uOption == 1) //remove
                    DeleteTask(Tasks);

                else if (uOption == 2) //list
                    ListTasks(Tasks);

                else if(uOption == (option.length() - 1)) //exit (przy założeniu, że exit zawsze będzie ostatnim elementem)
                    SaveToFile(fName,Tasks);
            }
            else {
                System.out.println(ConsoleColors.RED + "Option [" + option + "] doesn't exist! Press ENTER to try again." + ConsoleColors.RESET);
            }
        }
        while(true);
    }

    //Metoda zapisująca dane zadania do pliku
    public static void SaveToFile(String FileName, String[][] tasks)
    {
        try(FileWriter fileWriter = new FileWriter(FileName, true))
        {
            String t = "";
            for(int a = 0; a < tasks.length; a++) {
                for (int b = 0; b < tasks[a].length; b++) {
                    t += tasks[a][b] + ",";
                    if(b == tasks[a].length - 1) { //Pozbywamy się przecinka na końcu wiersza
                        t = t.substring(0,t.length() - 1);
                    }
                }
                fileWriter.write(t + "\n");
                t = "";
            }
        } catch (IOException e)
        {
            System.out.println("Wystąpił błąd zapisu do pliku: " + e.getMessage());
        }
        System.exit(0);
    }

    //Metoda usuwająca zadanie z listy
    public static void DeleteTask(String[][] tasks)
    {
        int task;
        char choice;

        //Wyświetlamy zawartość listy
        for(int a = 0; a < tasks.length; a++)
        {
            System.out.print("[" + a + "] ");
            for(int b = 0; b < tasks[a].length; b++)
                System.out.print(tasks[a][b] + " ");
            System.out.println("");
        }

        do {
            Scanner getData = new Scanner(System.in);
            Scanner getChoice = new Scanner(System.in);

            System.out.print("\n\nType in task number to remove: ");
            task = getData.nextInt();

            if((task >= 0) && (task < tasks.length)) {
                Tasks = ArrayUtils.remove(tasks,task);
                System.out.println("Task [" + task + "] has been removed.");
            }
            else {
                System.out.println("Wrong task number!");
            }

            System.out.print("Would you like to remove next task? [y/n]: ");
            choice = getChoice.next().charAt(0);

        } while(choice != 'n');
    }

    //Metoda listująca zadanie
    public static void ListTasks(String[][] list)
    {
        System.out.println("Task list:");

        for(int a = 0; a < list.length; a++)
        {
            System.out.print("[" + a + "] ");
            for(int b = 0; b < list[a].length; b++)
                System.out.print(list[a][b] + " ");
            System.out.println("");
        }

        System.out.println("\nPress ENTER to continue...");

        try {
            System.in.read();
        } catch (Exception e)
        {}
    }

    //Metoda dodająca zadania do listy
    public static void AddTask()
    {
        String[] TaskData = new String[3];
        char choice;

        do {
            Scanner getData = new Scanner(System.in);
            Scanner getChoice = new Scanner(System.in);

            System.out.print("Describe task: \n> ");
            TaskData[0] = getData.nextLine();

            System.out.print("Type in date task: \n> ");
            TaskData[1] = getData.nextLine();

            System.out.print("Is it important task [true/false]: \n> ");
            TaskData[2] = getData.nextLine();

            Tasks = Arrays.copyOf(Tasks,Tasks.length + 1);
            Tasks[Tasks.length - 1] = new String[3];

            for(int a = 0; a < 3; a++)
                Tasks[Tasks.length - 1][a] = TaskData[a];

            System.out.print("Would you like to add next task? [y/n]: ");
            choice = getChoice.next().charAt(0);

        } while(choice != 'n');
    }

    //Metoda wczytująca dane z pliku task.csv. Po wczytaniu danych, zwraca listę task'ów
    public static String[][] LoadData(String FileName)
    {
        Path path = Paths.get(FileName);
        List<String> LoadTasks = new ArrayList<>();
        String[][] TaskTable = null;
        String[] task;

        //Sprawdzamy czy plik istnieje
        if(!Files.exists(path))
        {
            System.out.println("Nie można odnaleźć pliku " + FileName + ". ");
            System.exit(0);
        }
        //Odczytujemy dane z pliku
        try(Scanner scan = new Scanner(new File(FileName))) {
            while(scan.hasNextLine())
                LoadTasks.add(scan.nextLine());

            //Przygotowujemy macierz [liczba wierszy][liczba kolumn]
            //Jeżeli w pliku były już dane
            if(LoadTasks.size() != 0) {
                TaskTable = new String[LoadTasks.size()][LoadTasks.get(0).split(",").length];

                //Zapisujemy dane do macierzy
                for(int a = 0; a < LoadTasks.size(); a++)
                {
                    task = LoadTasks.get(a).split(",");

                    for(int b = 0; b < task.length; b++)
                        TaskTable[a][b] = task[b];
                }
            } else { //W przypadku gdy plik był pusty
                TaskTable = new String[1][3];
            }
        } catch (FileNotFoundException e)
        {
            System.out.println("Nie można odczytać pliku " + FileName);
        }

        return TaskTable;
    }

    //Metoda sprawdzająca czy użytkownik wybrał jedną z dostępnych opcji - argumenty (tablica opcji, opcja wybrana przez usera)
    public static int CheckOption(String[] AvailableOptions, String UserChoice)
    {
        int opt = -1; //domyślnie zwraca błąd

        //Iterujemy po tablicy AvailableOptions
        for(int a = 0; a < AvailableOptions.length; a++)
        {
            //Sprawdzamy czy użytkownik podał coś z dostępnych opcji
            if(AvailableOptions[a].equals(UserChoice))
            {
                //Jeżeli tak, zapisujemy numer indeksu z opcją i przerywamy działanie pętli
                opt = a;
                break;
            }
        }

        //Zwracamy rezultat weryfikacji
        return opt;
    }

    //Metoda rysująca menu na ekranie - argumenty (Tekst główny, tablica opcji)
    public static void DrawMenu(String MainText, String[] AvailableOptions)
    {
        //Menu jest elastyczne. Dopasowuje się do zmiennej MainText.
        //Ale text.length musi być większe od każdego indeksu AvailableOptions

        //Zestaw znaków, którymi metoda będzie rysować
        char ch_1 = '+';
        char ch_2 = '-';
        char ch_3 = '|';

        //Obliczamy rozmiar tekstu głównego
        int size_text = MainText.length();

        //Tablica rozmiarów nazw opcji AvailableOptions
        //Numer indeksu 'size_opt' odpowiada numerowi indeksu 'AvailableOptions'
        int[] size_opt = new int[AvailableOptions.length];

        //Obliczamy rozmiary dla każdej nazwy opcji z AvailableOptions
        for(int a = 0; a < AvailableOptions.length; a++)
            size_opt[a] = MainText.length() - AvailableOptions[a].length();

        //Najpierw wyświetlamy tekst główny
        //Tekst główny
        System.out.print(ch_1);
        for(int a = 0; a < size_text; a++)
            System.out.print(ch_2);
        System.out.println(ch_1);

        System.out.print(ch_3);
        System.out.print(ConsoleColors.BLUE + MainText + ConsoleColors.RESET);
        for(int a = 0; a < size_text - MainText.length(); a++)
            System.out.print(" ");
        System.out.println(ch_3);

        System.out.print(ch_1);
        for(int a = 0; a < size_text; a++)
            System.out.print(ch_2);
        System.out.println(ch_1);

        //Teraz wyświetlamy dostępne opcje z tablicy 'AvailableOptions'
        for(int a = 0; a < AvailableOptions.length; a++)
        {
            System.out.print(ch_3);
            System.out.print(AvailableOptions[a]);

            for(int b = 0; b < size_opt[a]; b++)
                System.out.print(" ");

            System.out.println(ch_3);
        }

        //Po wyświetleniu wszystkich opcji, rysujemy końcową linię
        System.out.print(ch_1);
        for(int a = 0; a < size_text; a++)
            System.out.print(ch_2);
        System.out.println(ch_1);

        System.out.println("");
    }
}