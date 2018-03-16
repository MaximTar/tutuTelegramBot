package com.github.tututelegrambot;

/**
 * Created by maxtar on 06.03.18.
 */

import org.json.JSONObject;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendLocation;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Location;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class TutuTelegramBot extends TelegramLongPollingBot {

    private final static String iKnow = "Я точно знаю,\nкуда хочу";
    private final static String iDontKnow = "Я не знаю точно,\nкуда хочу";
    private final static String walking = "Пешком";
    private final static String transit = "Общественным транспортом";
    private final static String stepByStep = "Посмотреть маршрут по шагам";
    private final static String changeMode = "Сменить способ передвижения";
    private int branchIndicator = 0;
    private String destination;
    private String latLon;
    private List<String> places = new ArrayList<>();
    private JSONObject route = null;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            SendMessage sendMessage = new SendMessage().setChatId(update.getMessage().getChatId());
            if (message != null) {
                switch (branchIndicator) {
                    case 0:
                        if (message.hasText()) {
                            switch (message.getText()) {
                                case "/start":
                                    startFunction();
                                    break;
                                case "/help":
                                    helpFunction();
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
                        } else if (message.hasLocation()) {
                            Location location = message.getLocation();
                            latLon = location.getLatitude().toString() + "," + location.getLongitude().toString();
                            defaultFunction(sendMessage);
                        } else {
                            // TODO
                        }
                        break;
                    case 1:
                        if (message.hasText()) {
                            if (places.contains(message.getText())) {
                                branchIndicator = 3;
                                destination = message.getText();
                                walkingOrTransit(message, sendMessage);
                            } else {
                                firstCase(message, sendMessage);
                            }
                        } else {
                            // TODO
                        }
                        break;
                    case 2:
                        if (message.hasText()) {
                            secondCase(message, sendMessage);
                        } else {
                            // TODO
                        }
                        break;
                    case 3:
                        if (message.hasText()) {
                            if (Objects.equals(message.getText(), walking)
                                    || Objects.equals(message.getText(), transit)) {
                                thirdCase(message, sendMessage);
                            } else if (Objects.equals(message.getText(), changeMode)) {
                                walkingOrTransit(message, sendMessage);
                            } else if (Objects.equals(message.getText(), stepByStep)) {
                                showRouteStepByStep(sendMessage);
                            }
                        } else {
                            // TODO
                        }
                        break;
                    default:
                        break;
                }
            } else {
                sendMessage.setText("Извини, я понимаю только текст.");
                sendMsg(sendMessage);
            }
        }
    }

    private void startFunction() {
        // TODO
    }

    private void helpFunction() {
        // TODO
    }

    private void defaultFunction(SendMessage sendMessage) {
        sendMessage.setText("Выберите вариант.");
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(iKnow);
        row.add(iDontKnow);
        keyboard.add(row);
        ReplyKeyboardMarkup replyKeyboardMarkup = createOneTimeReplyKeyboardMarkup(keyboard);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
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
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        KeyboardButton requestLocationButton = new KeyboardButton("Поделиться местоположением");
        requestLocationButton.setRequestLocation(true);
        row.add(requestLocationButton);
        keyboard.add(row);
        ReplyKeyboardMarkup replyKeyboardMarkup = createOneTimeReplyKeyboardMarkup(keyboard);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        sendMsg(sendMessage);
    }

    private void caseIDontKnow(SendMessage sendMessage) {
        branchIndicator = 2;
        sendMessage.setText("Какие пожелания?") // todo change text
                .setReplyMarkup(new ReplyKeyboardRemove());
        sendMsg(sendMessage);
    }

    private void firstCase(Message message, SendMessage sendMessage) {
        destination = message.getText();
        if (latLon != null) {
            try {
                places = GoogleApiHandler.autocomplete(destination, latLon);
                List<KeyboardRow> keyboard = new ArrayList<>();
                for (String place : places) {
                    KeyboardRow row = new KeyboardRow();
                    row.add(place);
                    keyboard.add(row);
                }
                ReplyKeyboardMarkup replyKeyboardMarkup = createOneTimeReplyKeyboardMarkup(keyboard);
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
                // TODO add try again button
                sendMessage.setText("Позвольте, я уточню.");
                sendMsg(sendMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // TODO
        }
    }

    private void secondCase(Message message, SendMessage sendMessage) {
        String keyword = message.getText();
        if (latLon != null) {
            try {
                List<String> placesIds = GoogleApiHandler.nearbySearch(keyword, latLon);
                List<KeyboardRow> keyboard = new ArrayList<>();
                for (String placeId : placesIds) {
                    KeyboardRow row = new KeyboardRow();
                    String place = GoogleApiHandler.placeDetailsById(placeId);
                    row.add(place);
                    keyboard.add(row);
                }
                ReplyKeyboardMarkup replyKeyboardMarkup = createOneTimeReplyKeyboardMarkup(keyboard);
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
                sendMessage.setText("Поблизости найдены следующие места:");
                sendMsg(sendMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void thirdCase(Message message, SendMessage sendMessage) {
        String mode = message.getText();
        if (latLon != null) {
            try {
                if (Objects.equals(mode, walking)) {
                    mode = "walking";
                } else if (Objects.equals(mode, transit)) {
                    mode = "transit";
                } else {
                    // TODO
                }
                route = GoogleApiHandler.createRoute(destination, latLon, mode);
                JSONObject location = route.getJSONArray("routes").getJSONObject(0)
                        .getJSONArray("legs").getJSONObject(0);
                final String distance = location.getJSONObject("distance").getString("text");
                final String duration = location.getJSONObject("duration").getString("text");
                List<KeyboardRow> keyboard = new ArrayList<>();
                KeyboardRow row = new KeyboardRow();
                row.add(stepByStep);
                row.add(changeMode);
                keyboard.add(row);
                ReplyKeyboardMarkup replyKeyboardMarkup = createOneTimeReplyKeyboardMarkup(keyboard);
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
                sendMessage.setText("Длительность маршрута: " + distance + " или " + duration +
                        " Желаете посмотреть маршрут по шагам или сменить способ передвижения?");
                sendMsg(sendMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // TODO
        }
    }

    private void walkingOrTransit(Message message, SendMessage sendMessage) {
        sendMessage.setText("Пойдете пешком или воспользуетесь общественным транспортом?");
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(walking);
        row.add(transit);
        keyboard.add(row);
        ReplyKeyboardMarkup replyKeyboardMarkup = createOneTimeReplyKeyboardMarkup(keyboard);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        sendMsg(sendMessage);
        try {
            float[] location = GoogleApiHandler.geocoding(destination);
            SendLocation sendLocation = new SendLocation(location[0], location[1]).setChatId(message.getChatId());
            sendMsg(sendLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showRouteStepByStep(SendMessage sendMessage) {
        List<String> steps = Utils.getStepsFromRoute(route);
        String msg = "";
        for (int i = 0; i < steps.size(); i++) {
            if (i == steps.size() - 1) {
                msg += steps.get(i);
            } else {
                msg += steps.get(i) + "\n";
            }
        }
        sendMessage.setText(msg);
        sendMsg(sendMessage);
    }

    private void sendMsg(BotApiMethod<Message> sendMsg) {
        try {
            execute(sendMsg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private static ReplyKeyboardMarkup createOneTimeReplyKeyboardMarkup(List<KeyboardRow> keyboard) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
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
