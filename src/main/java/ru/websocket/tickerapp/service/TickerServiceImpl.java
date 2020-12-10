package ru.websocket.tickerapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.websocket.tickerapp.model.Ticker;
import ru.websocket.tickerapp.repository.TickerListService;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class TickerServiceImpl implements TickerService {

    private LinkedList<Ticker> tempTickers = new LinkedList<>();
    private int updateSecs = 0;

    @Autowired
    TickerListService tickerListService;

    @Override
    public Ticker parseTicker(String message) {
        log.debug("Parsing message: " + message);
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
        return ticker;
    }

    public void saveTicker(Ticker ticker){
        if(compareToLast(ticker)){
            updateSecs = 0;
            tickerListService.save(ticker);
            log.debug("Saving updated ticker: " + "ask:" + ticker.getAsk() + " , bid:" + ticker.getBid());
            System.out.println("ask:" + ticker.getAsk() + " , bid:" + ticker.getBid());
        }
        tempTickers.add(ticker);
        if (tempTickers.size() > 3) {
            tempTickers.removeFirst();
        }
    }

    private boolean compareToLast(Ticker ticker){
        if (tempTickers.size() > 0) {
            Ticker tempTicker = tempTickers.getLast();
            log.debug("Tickers compareTo result: " + tempTicker.compareTo(ticker));
            if (tempTicker.compareTo(ticker) != 0) {
                return true;
            }
        } else {
            return true;
        }
        System.out.println("No updates for " + ++updateSecs + " secs");
        return false;
    }
}
