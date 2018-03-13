package com.github.tututelegrambot;

/**
 * Created by maxtar on 06.03.18.
 */

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Location;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TutuTelegramBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage = new SendMessage().setChatId(update.getMessage().getChatId()); //.setText("Hello!");
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().equals("/markup")) {
                    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                    List<KeyboardRow> keyboard = new ArrayList<>();
                    KeyboardRow row = new KeyboardRow();
                    KeyboardButton geoButton = new KeyboardButton("Get Location");
                    geoButton.setRequestLocation(true);

                    row.add(geoButton);
                    keyboard.add(row);
                    keyboardMarkup.setKeyboard(keyboard);
                    sendMessage.setReplyMarkup(keyboardMarkup);
                } else {
                    // TODO
                }
            }
            else if (update.getMessage().hasLocation()) {
                // TODO CHECK THAT IT IS USER LOCATION, NOT THE OTHER ONE
                Location location = update.getMessage().getLocation();
                String latLon = location.getLatitude().toString() + "," + location.getLongitude().toString();
                try {
                    List<String> places = JsonHandler.autocomplete("парк культуры", latLon);
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();
                    for (int i = 0; i < places.size(); i++) {
                        List<InlineKeyboardButton> row = new ArrayList<>();
//                        row.add(new InlineKeyboardButton(place).setUrl(place));
                        row.add(new InlineKeyboardButton(places.get(i)).setCallbackData(Integer.toString(i)));
                        keyboardButtons.add(row);
                    }
                    inlineKeyboardMarkup.setKeyboard(keyboardButtons);
                    sendMessage.setText("Давай уточним");
                    sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println(update);
                System.out.println(update.getMessage());
            }
        }
        else {
            // TODO
        }
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
