package it.polito.did.ragnatela;

public class Anelli {
    int anello;
    int start;
    int stop;
    int step1, step2, step3, step4, step5;

    public Anelli(int num){
        anello=num;
    }

    public void setAnello(int num){
        anello=num;
    }

    public void setStep(int anello){
        if(anello==3){
            start=791;
            stop=1071;
            step1=52;
            step2=62;
            step3=70;
            step4=58;
            step5=38;
        }
        if(anello==2){
            start=613;
            stop=790;
            step1=39;
            step2=33;
            step3=43;
            step4=36;
            step5=26;
        }
        if(anello==1){
            start=522;
            stop=612;
            step1=21;
            step2=15;
            step3=19;
            step4=21;
            step5=14;
        }
    }
    public int getAnello(){return anello;}
    public int getStart(){return start;}
    public int getStop(){return stop;}
    public int getStep1(){return step1;}
    public int getStep2(){return step2;}
    public int getStep3(){return step3;}
    public int getStep4(){return step4;}
    public int getStep5(){return step5;}
}
