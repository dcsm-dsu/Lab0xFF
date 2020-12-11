package com.dmoracco;

import java.util.ArrayList;
import java.util.Collections;

public class PersistantArrayList {
    public ArrayList<Integer> list;

    public void set(ArrayList<Integer> newList){
        this.list = (ArrayList) newList.clone();
    }
}
