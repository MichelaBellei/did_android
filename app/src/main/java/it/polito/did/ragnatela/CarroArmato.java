package it.polito.did.ragnatela;

public class CarroArmato {
    int score;

    public CarroArmato() {
        score = 0;
    }

    public int getScore() { return score; }

    public void hit() {
        score -= 5;
    }

    public void upScore() {
        score += 10;
    }
}