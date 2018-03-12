package com.github.tututelegrambot;

/**
 * Created by maxtar on 06.03.18.
 */

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class TutuTelegramBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage sendMessage = new SendMessage().setChatId(update.getMessage().getChatId()).setText("Hello!");
            if (update.getMessage().getText().equals("/markup")) {
                SendMessage message = new SendMessage()
                        .setChatId(update.getMessage().getChatId())
                        .setText("Here is your keyboard");
                // Create ReplyKeyboardMarkup object
                ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                // Create the keyboard (list of keyboard rows)
                List<KeyboardRow> keyboard = new ArrayList<>();
                // Create a keyboard row
                KeyboardRow row = new KeyboardRow();
//                // Set each button, you can also use KeyboardButton objects if you need something else than text
//                row.add("Row 1 Button 1");
//                row.add("Row 1 Button 2");
//                row.add("Row 1 Button 3");
//                // Add the first row to the keyboard
//                keyboard.add(row);
//                // Create another keyboard row
//                row = new KeyboardRow();
//                // Set each button for the second line
//                row.add("Row 2 Button 1");
//                row.add("Row 2 Button 2");
//                row.add("Row 2 Button 3");
//                // Add the second row to the keyboard
//                keyboard.add(row);

                KeyboardButton geoButton = new KeyboardButton("Get Location");
                geoButton.setRequestLocation(true);

                // Set the keyboard to the markup
                row.add(geoButton);
                keyboard.add(row);
                keyboardMarkup.setKeyboard(keyboard);
                // Add it to the message
                message.setReplyMarkup(keyboardMarkup);
                try {
                    sendMessage(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (update.getMessage().hasLocation()) {
                System.out.println("HELLO");
                System.out.println(update.getMessage());
                sendMessage = new SendMessage().setChatId(update.getMessage().getChatId()).setText(update.getMessage().getLocation().toString());
            }
//            SendMessage message = new SendMessage()
//                    .setChatId(update.getMessage().getChatId())
//                    .setText(update.getMessage().getText());
//            SendMessage sendMessage = new SendMessage().setChatId(update.getMessage().getChatId()).setText("Hello!");
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "botName";
    }

    @Override
    public String getBotToken() {
        return "botToken";
    }
}
