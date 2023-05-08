package org.example;


import org.example.MyException.*;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.Scanner;

public class Main {
    public static void main (String[] args) throws Exception {
        Scanner vvod = new Scanner(System.in);
        obrabotka instryment = new obrabotka();
        int kol_voStr = 0;
        String[] DataBase;
        String filePath = "airports.csv", kol_voStrok;

        //считает кол-во строк
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                kol_voStr = kol_voStr + 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //создает указатели для использования файла
        DataBase = (instryment.Ykazat(kol_voStr,filePath));
        //сортирует базу
        Arrays.sort(DataBase);
        //переменная для определения повтора
        char povtor;
        do {

            System.out.println("Введите фильтр в формате \"column[<номер столбца>]<операция><условие>\" в одну строчку.");
            System.out.println("Если условие текстовое значение записать в \'\' если цифровое то без \'\'.");
            System.out.println("Фильтры соеденяются с помощью & и ||.");
            //ввод фильтра
            String filter = vvod.nextLine().replace(" ","");
            if(filter.equals("!quit")){
                System.exit(0);
            }

            System.out.println("Введите начало имени Аэропорта без ковычек в одну строчку");
            //ввод поиска по 2 столбцу
            String poiscIf = "\"" + vvod.nextLine();
            if(poiscIf.equals("!quit")){
                System.exit(0);
            }

            try {
                //начало отсчета поиска
                long elapsed;
                long start = System.currentTimeMillis();
                //создает временну базу данных
                String[] tempDataBase = DataBase;
                //создает базу данных по имени аэропорта
                tempDataBase = instryment.poisc(tempDataBase, poiscIf);
                //проводит поиск данных по фильтру и вывод данных так же возвращает кол-во найденх строк
                kol_voStrok = instryment.vivod(filter, tempDataBase, filePath);
                //конец отсчета поиска в среднем из 1000 поисков  на поиск по фильтру и поиску примера уходило 90-100 мс
                long finish = System.currentTimeMillis();
                elapsed = finish - start;



                System.out.println("Количество найденых строк: " + kol_voStrok+" Время, затраченое на поиск: "+elapsed+" мс");
                System.out.println("Для повтора поиска введите y:");
                try {
                    povtor = vvod.nextLine().charAt(0);
                } catch (StringIndexOutOfBoundsException e) {
                    povtor = 'n';
                }


            }catch (ErrorFilterNotIfExceprion e) {
                System.out.println("В фильтре ну узано условие");
                povtor='y';
            }catch (FormatFiltraException e) {
                System.out.println("В фильтре ошибка формата условия");
                povtor='y';
            }catch (ErrorColumn e) {
                System.out.println("Неверно указана колонка");
                povtor='y';
            } catch (ErrorPoiskStolb e) {
                System.out.println("Неверно указан поиск по второй колонке");
                povtor='y';
            }catch (ErrorTipIfAndTipcolumnExctption e) {
                System.out.println("Неверный тип данных в условии для поиска в колонке " + e.toString().charAt(e.toString().length()-1));
                povtor='y';
            }catch (EmptyStackException e) {
                System.out.println("Неверный фильтр");
                povtor='y';
            }catch (ErrorPraamPoisk e) {
                System.out.println("Не указан параметр поиска Аэропорта");
                povtor='y';
            }
        }while(povtor=='y'||povtor=='Y');

    }
}
