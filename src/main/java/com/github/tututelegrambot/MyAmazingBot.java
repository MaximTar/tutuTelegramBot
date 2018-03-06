package com.github.tututelegrambot;

/**
 * Created by maxtar on 06.03.18.
 */

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class MyAmazingBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId())
                    .setText(update.getMessage().getText());
            SendMessage sendMessage = new SendMessage().setChatId(update.getMessage().getChatId()).setText("HellO!");
            try {
                execute(sendMessage); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "tutudevelopbot";
    }

    @Override
    public String getBotToken() {
        return "517271389:AAHQ7c4wnya3w3aOSrR84bU2wgSmNk2aV94";
    }
}
