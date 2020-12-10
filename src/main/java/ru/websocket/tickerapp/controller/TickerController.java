package ru.websocket.tickerapp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.websocket.tickerapp.configuration.WebSocketClientEndpoint;
import ru.websocket.tickerapp.model.Ticker;
import ru.websocket.tickerapp.repository.TickerListService;
import ru.websocket.tickerapp.service.TickerServiceImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;


@Slf4j
@Controller
public class TickerController {

    @Autowired
    TickerListService tickerListService;

    @Autowired
    TickerServiceImpl tickerService;

    private static URI URL = null;

    static {
        try {
            URL = new URI("wss://ftx.com/ws/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static final String ping = "{\"op\" : \"ping\"}";


    @GetMapping({"/tickers", "/", ""})
    @SendTo("/topic/tickers")
    public ModelAndView establishConnection(@RequestParam(name = "market", required = false) String market) {
        ModelAndView mv = new ModelAndView("index");

        // open websocket
        final WebSocketClientEndpoint clientEndPoint = new WebSocketClientEndpoint(URL);
        ArrayList<Ticker> tickers = tickerListService.findAll();
        LinkedList<Ticker> tempTickers = new LinkedList<>();
        mv.addObject("tickers", tickers);

        //add listener
        clientEndPoint.addMessageHandler(new WebSocketClientEndpoint.MessageHandler() {
            @Override
            public void handleMessage(String message) {
                try {
                    Thread.sleep(1000);
                    //parsing data
                    if (message.contains("data")) {
                        Ticker ticker = tickerService.parseTicker(message);
                        ticker.setMarketName(market);
                        tickerService.saveTicker(ticker);
                    } else{
                        log.debug("Data not found in string: " + message);
                    }
                } catch (InterruptedException e) {
                    log.error(e.toString());
                }
            }
        });

        // send message to websocket
        if (market != null) {
            String fullQuery = "{\"op\" : \"subscribe\", \"channel\" : \"ticker\", \"market\" : \"" + market + "\"}";
            clientEndPoint.sendMessage(fullQuery);
        } else {
            log.debug("Empty market field");
        }

        return mv;
    }


}
