package ru.websocket.tickerapp.model;

import lombok.Data;

@Data
public class Ticker implements Comparable<Ticker>{
    private double bid;
    private double ask;
    private double bidSize;
    private double askSize;
    private String marketName;

    @Override
    public int compareTo(Ticker o) {
        return (int) ((this.ask+this.bid)-(o.getAsk()+o.getBid()));
    }
}