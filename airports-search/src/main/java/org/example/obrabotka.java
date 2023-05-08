package org.example;

import org.example.MyException.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class obrabotka {
    //создает указатели для использования файла
    public String[] Ykazat(int kol_voStr,String filePath) {
        int indexStr = 0;
        String[] DataBase;
        String line;
        //Принимает и обрабатывает данные из файла
        try (BufferedReader reader1 = new BufferedReader(new FileReader(filePath))) {
            //обявляет массив для хранения указателей
            DataBase = new String[kol_voStr];
            //перечитывает весь файл для создания указателя
            for (int i = 0; i < kol_voStr; i++) {
                DataBase[i] = "";
                line = reader1.readLine();
                int w = 0;
                int gf = 0;
                for (int q = 0; q < line.length(); q++) {
                    if (w==0){
                        gf++;
                    }
                    if (line.charAt(q) == ',') {

                        w++;

                        if (w == 2) {
                            break;
                        }
                    }
                    if (gf<=q) {
                        DataBase[i] = DataBase[i] + line.charAt(q);
                    }

                }
                if(i==0){
                    DataBase[i] = DataBase[i] + "," + 0;
                }else{
                    DataBase[i] = DataBase[i] + "," + indexStr;
                }
                indexStr = indexStr + line.length()+1;
                gf=0;
            }
            return DataBase;
        } catch (IOException e) {
            e.printStackTrace();
            DataBase = new String[1];
            return DataBase;
        }

    }
    //определяет значения по каждому фильтру
    private String columnZnach(String filter, String string) throws Exception{
        int indexColomn,endIfColumn=0;
        String  ifColumn= "",nextIfColumn;
        // определяет начала фильтра
        indexColomn = filter.indexOf("column[");
        //проверяет есть ли вообще условия? если нет условия то возвращает "null"
        if(filter.length()==0){
            return "netznach";
        }else if(indexColomn == -1) {
            throw new ErrorFilterNotIfExceprion("");
        }
        //удаляет данные до фильтра
        filter = filter.substring(indexColomn);
        //определяет конец фильтра
        endIfColumn = endIf(filter.indexOf("&"),filter.indexOf("|"),filter.indexOf(")"));
        //определяет последний ли фильтр найден, если найден последний то вызывает определение условия и возвращает
        if(endIfColumn== -1){
            return columnTrueFalse(filter,string);
        }else if(filter.indexOf("column[", endIfColumn) == -1){
            return columnTrueFalse(filter.substring(0,endIfColumn),string);
        }
        //запоминает текущий фильтр
        ifColumn =  filter.substring(0,endIfColumn);
        //определяет значение текущего фильтра
        ifColumn = columnTrueFalse(ifColumn,string);
        //вызывает этот же метод для определения последующее фильтра
        nextIfColumn=columnZnach(filter.substring(endIfColumn),string);
        //Присоединяет к текущему фильтру данные о предыдущем
        ifColumn = ifColumn  + nextIfColumn;
        //возвращает итоги условий по переданным сюда фильтрам
        return ifColumn;

    }
    //проводит поиск в базе по назавнию аэропорта
    public String[] poisc(String[] DataBase, String filter)throws Exception{
        int lowDiapazon=0,maxDiapazon=0;
        //пределяет есть ли параметр поиска
        if(filter.length()<2){
            throw new ErrorPraamPoisk("");
        }
        //определяет в каком диапазоне требуемые
        boolean d=false;
        for(int i= 0; i<DataBase.length;i++){
            if(DataBase[i].toLowerCase().startsWith(filter.toLowerCase())){
                d=true;
            }else{
                if(d){
                    maxDiapazon = i;
                    break;
                }
                lowDiapazon++;
            }

        }
        //определяет нашлись ли данные поиска
        if(maxDiapazon==0){
            throw new ErrorPoiskStolb("");
        }
        //обьявляет новый массив
        String[] newMass = new String[maxDiapazon-lowDiapazon];
        //перемещает найденные значения в новый массив
        for(int i=0; i<newMass.length;i++ ){
            newMass[i]=DataBase[lowDiapazon+i];
        }
        //определяет положение данного параметра в файле
        int indexStr=0;
        for (int i = 0; i<newMass.length;i++){
            int e=1;
            d = false;
            newMass[i]=newMass[i]+',';
            for(int j = 0; j<newMass[i].length();j++){
                if (newMass[i].charAt(j)==','){
                    d=true;
                }else{
                    newMass[i]=newMass[i]+newMass[i].charAt(j);
                    e++;
                }
                if (d){
                    newMass[i]= newMass[i].substring(j+1);
                    if(indexStr<newMass[i].length()-e){
                        indexStr=newMass[i].length()-e;
                    }
                    break;
                }
            }
        }
        //добавляет 0 вначале индекса строчки
        for(int i=0; i<newMass.length;i++){
            int e=0;
            for(int j=1; j<newMass[i].length();j++){
                if(newMass[i].charAt(j)==','){
                    break;
                }else{
                    e++;
                }
            }
            while(e<indexStr-1){
                newMass[i]="0"+newMass[i];
                e++;
            }
        }
        Arrays.sort(newMass);

        return newMass;

    }
    //Определяет значение переданной колонке
    private String columnTrueFalse(String ifColomn, String stroka)throws Exception{
        String Column, znachIf, oper,operTemp,itog = "";
        //определяет по какой колонке выполняеться фильтр
        if(!ifColomn.contains("]")){
            throw new FormatFiltraException("");
        }
        Column =  ifColomn.substring(7,ifColomn.indexOf("]"));
        //находит данные по нужной колонке
        for(int b=1;b<Integer.parseInt (Column);b++){
            stroka=stroka.substring(stroka.indexOf(',')+1);
        }
        //удаляет в конце запятую кроме последнего столбца
        if(Integer.parseInt (Column)!=14) {
            stroka = stroka.substring(0, stroka.indexOf(','));
        }
        //определяет операцию
        oper = ifColomn.substring(ifColomn.indexOf("]")+1,ifColomn.indexOf("]")+2);
        //проверяет операцию являеться ли она не равно
        //если является то ставит знак '!'
        //если не является то оставляет знак
        if(oper.charAt(0) == '<'){
            operTemp = ifColomn.substring(ifColomn.indexOf("]")+2,ifColomn.indexOf("]")+3);
            if( operTemp.charAt(0) == '>'){
                oper="!";
            }
        }
        //определяен значение из фильтра
        if(oper.charAt(0) == '!'){
            znachIf = ifColomn.substring(ifColomn.indexOf("]")+3);
        }else{
            znachIf = ifColomn.substring(ifColomn.indexOf("]")+2);
        }
        if (stroka.charAt(0)=='\"'|| stroka.charAt(0)=='\\'){
            if (znachIf.charAt(0)=='\''){
                //метод определения строк
                itog =sravStrok(znachIf,oper,stroka);

            }else {
                throw new ErrorTipIfAndTipcolumnExctption("");
            }
        }else{
            //определения чисел
            if (znachIf.charAt(0)=='\''){

                throw new ErrorTipIfAndTipcolumnExctption(Column);

            }else {
                itog = sravStrok(Float.parseFloat(znachIf), oper, stroka);
            }
        }
        //определение строковое решение или числовое


        return itog;
    }
    //удаляет кавычки
    private String DeletKavichki(String stroka){
        if (stroka.contains("\"")) {
            stroka = stroka.substring(stroka.indexOf("\"") + 1);
            if (stroka.contains("\"")) {
                stroka = stroka.substring(0, stroka.indexOf("\""));
            }
        }else{
            if (stroka.contains("'")) {
                stroka = stroka.substring(stroka.indexOf("'") + 1);
                if (stroka.contains("'")) {
                    stroka = stroka.substring(0, stroka.indexOf("'"));
                }
            }
        }

        return stroka;
    }
    //подставляет значение в фильтр
    private String podstZnach(String znach,String filter){
        for(int i = 0; i<znach.length();i++){
            int firstColumn, endColumn;
            firstColumn = filter.indexOf("column[");

            endColumn = endIf(filter.indexOf("&", firstColumn),filter.indexOf("|", firstColumn),filter.indexOf(")", firstColumn));
            if (endColumn != -1){
                filter = filter.substring(0,firstColumn)+ znach.charAt(i)+ filter.substring(endColumn);
            }else{
                for(int j = filter.indexOf("column[") +7 ; j<filter.length();j++){
                    if (filter.charAt(j) == ')'){
                        filter = filter.substring(0,firstColumn)+ znach.charAt(i)+ filter.substring(j);
                        break;
                    }else{
                        if (j==filter.length()-1){
                            filter = filter.substring(0,firstColumn)+ znach.charAt(i);
                            break;
                        }
                    }
                }
            }
        }
        return filter;
    }
    //определяет последующий символ в строке & | )
    private int endIf(int indexAnd , int indexOR,int indexScob ){
        int end=-1;
        if (indexAnd<0){
            if(indexScob<indexOR){
                if(indexScob==-1){
                    end=indexOR;
                }else {
                    end = indexScob;
                }
            }else if(indexScob>indexOR){
                if(indexOR==-1){
                    end = indexScob;

                }else {
                    end=indexOR;
                }
            }
        }else if(indexOR <0){
            if(indexAnd<indexScob){
                if(indexAnd==-1){
                    end=indexScob;
                }else {
                    end = indexAnd;
                }
            }else if(indexAnd>indexScob){
                if(indexScob==-1){
                    end = indexAnd;

                }else {
                    end=indexScob;
                }
            }
        }else if(indexScob<0){
            if(indexAnd<indexOR){
                if(indexAnd==-1){
                    end=indexOR;
                }else {
                    end = indexAnd;
                }
            }else if(indexAnd>indexOR){
                if(indexOR==-1){
                    end = indexAnd;

                }else {
                    end=indexOR;
                }
            }
        }else{
            int min = 0 ;
            if (indexOR<indexAnd){
                end = Math.min(indexOR, indexScob);
            }else{
                end = Math.min(indexAnd, indexScob);
            }
        }
        if(indexOR==-1 & indexAnd==-1&indexScob ==-1){
            end = -1;
        }
        return end;
    }
    //определение условия числовых типов
    private String sravStrok(float znachIf, String oper, String znachStroka){
        Float znachStrokaF = Float.valueOf(znachStroka);
        switch(oper.charAt(0)){
            case ('<'):
                if(znachIf>znachStrokaF){
                    return "1";
                }else{
                    return "0";
                }
            case '>':
                if(znachIf<znachStrokaF){
                    return "1";
                }else{
                    return "0";
                }
            case '!':
                if(znachIf!=znachStrokaF){
                    return "1";
                }else{
                    return "0";
                }
            case '=':
                if(znachIf==znachStrokaF){
                    return "1";
                }else{
                    return "0";
                }
        }
        return "";
    }
    //определение условия строковых типов типов
    private String sravStrok(String znachIf, String oper, String znachStroka){
        znachIf = DeletKavichki(znachIf);
        znachStroka = DeletKavichki(znachStroka);
        switch(oper.charAt(0)){
            case ('<'):
                if(znachIf.length()>znachStroka.length()){
                    return "1";
                }else{
                    return "0";
                }
            case '>':
                if(znachIf.length()<znachStroka.length()){
                    return "1";
                }else{
                    return "0";
                }
            case '!':
                if(znachIf.equalsIgnoreCase(znachStroka)){
                    return "0";
                }else{
                    return "1";
                }
            case '=':
                if(znachIf.equalsIgnoreCase(znachStroka)){
                    return "1";
                }else{
                    return "0";
                }
        }
        return "";
    }
    //вывод информации
    public String vivod(String filter, String[] dataBase,String filePath) throws Exception{
        int tempPropysk = 0,schethikVivoda=0;
        int pozic= 0;String stroka = "",tempMet,tempOr = "";
        for (int i = 0 ; i<filter.length();i++){
            if(filter.charAt(i)=='|'){
                if(filter.charAt(i+1)=='|'){
                    tempOr = tempOr + filter.charAt(i);
                }
            }else {
                tempOr = tempOr + filter.charAt(i);
            }
        }
        filter = tempOr;

        for(int i = 0;i<dataBase.length;i++){
            String temp = dataBase[i];
            try{
                pozic = Integer.parseInt(dataBase[i].substring(0,dataBase[i].indexOf(',')));
                try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                    reader.skip(pozic);
                    stroka = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }catch (NumberFormatException e ){
                throw new ErrorColumn("");
            }

            tempMet =  columnZnach(filter,stroka);
            if(tempMet.equals("null")){
                schethikVivoda++;
                System.out.println(temp.substring(temp.indexOf(',')+1)+"[" + stroka+"]");
            }else {
                tempMet = podstZnach(tempMet, filter);

                tempMet = Calculator.Calculator1(tempMet);


                if (tempMet.charAt(0) == '1') {
                    schethikVivoda++;
                    System.out.println(temp.substring(temp.indexOf(',') + 1) + "[" + stroka + "]");
                }
            }



        }
        return String.valueOf(schethikVivoda);
    }
}
