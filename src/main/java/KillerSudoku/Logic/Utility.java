package KillerSudoku.Logic;

public class Utility { // generate random getallen
    public static int rand(int max){
        return rand(0, max);
    }

    public static int rand(int min, int max){
        return (int)(Math.random() * 1000000) % (max + 1 - min) + min;
    }
}
