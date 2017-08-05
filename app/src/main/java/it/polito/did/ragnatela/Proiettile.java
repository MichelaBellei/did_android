package it.polito.did.ragnatela;


public class Proiettile {
    int tirante;
    int pos1; //posizioni proiettile doppio
    int pos2;
    int lifespan;
    boolean alive;
    boolean toCheck;
    int step;

    public boolean isAlive() {
        return alive;
    }

    public boolean isChecked() {
        return toCheck;
    }

    public void kill() {
        this.alive = false;
    }

    public Proiettile(int ramo) {
        if(ramo == 1) {
            tirante = 1;
            pos1 = 0;
            pos2 = 51;
            lifespan = 25;
        } else if(ramo == 2) {
            tirante = 2;
            pos1 = 52;
            pos2 = 185;
            lifespan = 118;
        } else if(ramo == 3) {
            tirante = 3;
            pos1 = 186;
            pos2 = 317;
            lifespan = 251;
        } else if(ramo == 4) {
            tirante = 4;
            pos1 = 318;
            pos2 = 423;
            lifespan = 370;
        } else if(ramo ==5) {
            tirante = 5;
            pos1 = 424;
            pos2 = 521;
            lifespan = 472;
        }
        step = 1;
        toCheck = false;
        alive = true;

    }


    public int getTirante() {
        return tirante;
    }

    public int getPos1() { return pos1; }

    public int getPos2() {
        return pos2;
    }
/*
    public void setPos1(int pos) {
        this.pos1 = pos;
    }

    public void setPos2(int pos) {
        this.pos2 = pos;
    }
*/
    public void setToCheck(boolean toCheck) {
        this.toCheck = toCheck;
    }

    public void update() {
        if(isAlive()) {
            this.pos1 = this.pos1 + step;
            this.pos2 = this.pos2 - step;

            if ((this.pos1 >= lifespan) || (this.pos2 <= lifespan)) {
                toCheck = true;
                kill();
            }
        }
    }

}