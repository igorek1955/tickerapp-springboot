package ru.websocket.tickerapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.websocket.tickerapp.model.Ticker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class TickerServiceImplTest {

    public static String message = "{\"channel\": \"ticker\", \"market\": \"BTC-PERP\", \"type\": \"update\", \"data\":" +
            " {\"bid\": 18340.0, \"ask\": 18340.5, \"bidSize\": 0.1212, \"askSize\": 0.9768, \"last\": 18339.5, \"time\": 1607537729.9330084}}";

    @Test
    void parseTicker() {
        String[] firstSplit = message.split("data\":");
        String[] secondSplit = firstSplit[1].split(",");
        Ticker ticker = new Ticker();
        for (String s1 : secondSplit) {
            Matcher matcher = Pattern.compile("[0-9]*\\.?[0-9]+").matcher(s1);
            if (s1.contains("\"bid\"")) {
                if (matcher.find()) {
                    double bid = Double.parseDouble(matcher.group());
                    ticker.setBid(bid);
                }
            } else if (s1.contains("\"ask\"")) {
                if (matcher.find()) {
                    double ask = Double.parseDouble(matcher.group());
                    ticker.setAsk(ask);
                }
            }
        }
        assertEquals(ticker.getBid(), 18340.0);
        assertEquals(ticker.getAsk(), 18340.5);
    }
}