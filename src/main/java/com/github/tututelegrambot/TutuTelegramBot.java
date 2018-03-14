package com.github.tututelegrambot;

/**
 * Created by maxtar on 06.03.18.
 */

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Location;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class TutuTelegramBot extends TelegramLongPollingBot {

    private final static String iKnow = "Я точно знаю,\nкуда хочу";
    private final static String iDontKnow = "Я не знаю точно,\nкуда хочу";
    private static int branchIndicator = 0;
    private static String destination;
    private static String latLon;
    private final static int ROW_SIZE = 40;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            SendMessage sendMessage = new SendMessage().setChatId(update.getMessage().getChatId());
            if (message != null && message.hasText()) {
                switch (branchIndicator) {
                    case 0:
                        switch (message.getText()) {
                            case "/start":
                                break;
                            case "/help":
                                break;
                            case iKnow:
                                caseIKnow(sendMessage);
                                break;
                            case iDontKnow:
                                caseIDontKnow(sendMessage);
                                break;
                            default:
                                requestLocation(sendMessage);
                                break;
                        }
                        break;
                    case 1:
                        firstCase(message, sendMessage);
                        break;
                    case 2:
                        break;
                    default:
                        break;
                }
            } else if (message != null && message.hasLocation()) {
                Location location = message.getLocation();
                latLon = location.getLatitude().toString() + "," + location.getLongitude().toString();
                defaultFunction(sendMessage);
            } else {
                sendMessage.setText("Извини, я понимаю только текст");
                sendMsg(sendMessage);
            }
        }
    }

    public void startFunction() {

    }

    public void helpFunction() {

    }

    private void defaultFunction(SendMessage sendMessage) {
        // Set text is mandatory (required)
        sendMessage.setText("Выберите вариант.");
        // Set keyboard
        // todo maybe relocate to separate function
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        // Make keyboard oneTime
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        // List of keyboard rows
        List<KeyboardRow> keyboard = new ArrayList<>();
        // Keyboard row
        KeyboardRow row = new KeyboardRow();
        // Add buttons to row
        row.add(iKnow);
        row.add(iDontKnow);
        // Add row to keyboard
        keyboard.add(row);
        replyKeyboardMarkup.setKeyboard(keyboard);
        sendMsg(sendMessage);
    }

    private void caseIKnow(SendMessage sendMessage) {
        branchIndicator = 1;
        sendMessage.setText("Куда пойдём?")
                .setReplyMarkup(new ReplyKeyboardRemove());
        sendMsg(sendMessage);
    }

    private void requestLocation(SendMessage sendMessage) {
        sendMessage.setText("Сначала мне нужно знать, где Вы.");
        // todo maybe relocate to separate function
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        KeyboardButton requestLocationButton = new KeyboardButton("Поделиться местоположением");
        requestLocationButton.setRequestLocation(true);
        row.add(requestLocationButton);
        keyboard.add(row);
        replyKeyboardMarkup.setKeyboard(keyboard);
        sendMsg(sendMessage);
    }

    private void caseIDontKnow(SendMessage sendMessage) {
        branchIndicator = 2;
        sendMessage.setText("В разработке")
                .setReplyMarkup(new ReplyKeyboardRemove());
        sendMsg(sendMessage);
    }

    private void firstCase(Message message, SendMessage sendMessage) {
        destination = message.getText();
        if (latLon != null) {
            try {
                // TODO add try again button
                List<String> places = JsonHandler.autocomplete(destination, latLon);
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();
                for (int i = 0; i < places.size(); i++) {
                    List<InlineKeyboardButton> row = new ArrayList<>();
                    String place;
                    if (places.get(i).length() < ROW_SIZE) {
                        place = Utils.addNewLineSymbols(places.get(i));
                    } else {
                        place = places.get(i);
                    }
                    row.add(new InlineKeyboardButton(place).setCallbackData(Integer.toString(i)));
                    // TODO SPLIT TEXT ON SEVERAL ROWS WITH \n
                    keyboardButtons.add(row);
                }
                inlineKeyboardMarkup.setKeyboard(keyboardButtons);
                sendMessage.setText("Позволь, я уточню.");
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                sendMsg(sendMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // TODO
        }
    }

    private void sendMsg(SendMessage sendMessage) {
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
