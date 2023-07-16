package com.mishlen.telegram_bot_elastic.service;

import com.mishlen.telegram_bot_elastic.config.BotConfig;
import lombok.AllArgsConstructor;
import com.mishlen.telegram_bot_elastic.model.FileModel;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        FileModel fileModel = new FileModel();
        String response = "";

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                default:
                    try {
                        response = FileService.searchLog(messageText, fileModel);
                    } catch (IOException e) {
                        sendMessage(chatId, emptyResponse());
                    } catch (ParseException e) {
                        throw new RuntimeException("Unable to parse date");
                    }
                    if (response.equals("[]")) response = emptyResponse();
                    sendMessage(chatId, response);
            }
        }
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Привет, " + name + "!" + "\n" +
                "Для поиска укажите поля и значения отбора в JSON формате." + "\n" +
                "Пример:" + "\n" + getExample();
        sendMessage(chatId, answer);
    }

    private String emptyResponse() {
        return "По указанным отборам данные не найдены." + "\n" +
                "Попробуйте исправить текст запроса." + "\n" +
                "Пример:" + "\n" + getExample();
    }

    private String getExample() {
        return "{\"application\": \"qw_auth\",\n" +
                " \"level\": \"error\",\n" +
                " \"env\": \"prod\",\n" +
                " \"beginDate\": \"1683111757304\",\n" +
                " \"endDate\": \"1683111777304\"\n" +
                "}";
    }
    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }
}
