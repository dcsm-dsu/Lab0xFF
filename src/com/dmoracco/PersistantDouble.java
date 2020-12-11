package com.dmoracco;

public class PersistantDouble {
    public double value;

    PersistantDouble(){
        this.value = 0;
    }
    PersistantDouble(double input){
        this.value = input;
    }

    public PersistantDouble add(double input){
        this.value = this.value + input;
        return this;
    }
}
