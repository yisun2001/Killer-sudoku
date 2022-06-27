package KillerSudoku.GameState;

import java.io.Serializable;
import java.util.Arrays;

public class PlayerCell implements Serializable{
    // Het vertegenwoordigt één cel, de toestand, het aantal en de kandidaten
    public enum State{
        CANDIDATES, FINALIZED // finalized = niet te wijzigen
    }

    private boolean[] candidates;
    private State state;
    private byte num;

    public PlayerCell(){
        super();

        state = State.CANDIDATES;
        candidates = new boolean[9];
        for(int i = 0; i < 9; i++)
            candidates[i] = false;
        num = 0;
    }

    public State getState(){
        return state;
    }


    public byte getNum(){
        return num;
    }

    public boolean isCandidate(byte i){ // Is de kandidaat getoond
        return candidates[i-1];
    }

    public void clearCandidates(){
        if(state == State.CANDIDATES)
            Arrays.fill(candidates, false);
    }

    public void setNum(byte n){ // Zet het nummer op de cel
        num = n;
        clearCandidates();
        state = State.FINALIZED; // niet te wijzigen
    }

}
