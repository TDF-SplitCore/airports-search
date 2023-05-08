package  org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Calculator {

    public static String Calculator1(String formula) throws Exception {
        List<String>list = opredPoryadka(formula);

        return calcResult(list);
    }

    private static String calcResult(List<String> list) throws Exception{
        //рассчитывает все операции в порядке приоритета
        Stack<String> charStack = new Stack<String>();
        for (String str : list) {
            if (indexZnak().contains(str)) {
                String data1 = charStack.pop();
                String data2 = charStack.pop();
                charStack.push(cal(str.charAt(0),data2,data1));
            } else {
                charStack.push(str);
            }
        }

        return charStack.peek();
    }
    private static List<String> indexZnak() {
        List<String> Znak = new ArrayList<String>(0);
        Znak.add("|");
        Znak.add("&");
        Znak.add("(");
        Znak.add(")");
        Znak.add("#");
        return Znak;
    }
    private static String cal(char c,String a,String b) throws Exception{
        //проводит операцию & ||
        switch(c){
            case '&':
                if((a.charAt(0)=='1'&b.charAt(0)=='1')|(a.charAt(0)=='0'&b.charAt(0)=='0')){
                    return "1";
                }else{
                    return "0";
                }
            case '|':
                if(a.charAt(0)=='1'|b.charAt(0)=='1'){
                    return "1";
                }else{
                    return "0";
                }
            default:
                throw new Exception("XX");
        }
    }

    private static List<String> opredPoryadka(String val) {
        List<String> result = new ArrayList<String>();
        Stack<String> charStack = new Stack<String>();
        //указывает окончание строк
        charStack.push("#");
        val = val + "#";
        //указывает порядок приоритета
        int[][] dir = {
                {1,0,0,1,1},
                {1,1,0,1,1},
                {0,0,0,2,-1},
                {1,1,-1,1,0},
                {0,0,0,-1,2}
        };
        List<String> test = new ArrayList<String>();

        //предача данных в лист
        for(int i = 0;i<val.length();i++){
            test.add(Character.toString(val.charAt(i)));
        }
//проходит все значения
        for (int i = 0 ,size = test.size(); i < size ; i++) {
            String str = test.get(i);
            //определяет цифра это или строка
            try {
                //если цифра то добваляет в лист
                Double.valueOf(str);
                result.add(str);
            } catch (NumberFormatException e) {
                //определяет индексы
                int x = indexZnak().indexOf(str);
                int y = indexZnak().indexOf(charStack.peek());
                //определяет действие в заивисимотси от приоритета в массиве dir
                if (dir[y][x] == 0) {
                    charStack.push(str);
                } else if (dir[y][x] == 1) {
                    result.add(charStack.pop());
                    i --;
                } else if (dir[y][x] == 2 && (x == 3 && y == 2 )) {
                    charStack.pop();
                } else if (dir[y][x] == 2 && (x == 4 && y == 4 )) {
                    break;
                } else {
                    break;
                }
            }
        }
        return result;
    }

}
