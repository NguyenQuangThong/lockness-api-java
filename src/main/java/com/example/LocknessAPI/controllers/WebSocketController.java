package com.example.LocknessAPI.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/hello")
    @SendTo("/topic/messages") // Broadcasts message to "/topic/messages"
    public String processMessage(String message) {
        return "Server received: " + message;
    }
}
