package ru.websocket.tickerapp.service;

import ru.websocket.tickerapp.model.Ticker;

public interface TickerService {
    Ticker parseTicker(String message);
    void saveTicker(Ticker ticker);
}
