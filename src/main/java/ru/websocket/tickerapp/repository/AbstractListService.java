package ru.websocket.tickerapp.repository;

import java.util.ArrayList;


public class AbstractListService<T> {

    protected ArrayList<T> list = new ArrayList<>();

    public ArrayList<T> findAll(){
        return list;
    }

    public void save(T object){
        list.add(object);
    };
}
