package com.merdeev.telegram;


import android.util.Log;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.List;

public class SimpleBot extends TelegramBot implements UpdatesListener {

    /** Контекст */
    private Context context;

    /** Бот */
    private static SimpleBot bot;

    static void create(Context context) throws Exception {
        bot = new SimpleBot(context, "474700806:AAGhyAWhW2-dK_VFxU7r4jJ0OkjgCR759kc");
    }

    public SimpleBot(Context context, String token) {
        super(token);
        this.context = context;
        this.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        Trace.save("simpleBot: process");
        for (Update i : updates) {
            Message message = i.message();
            if (message != null && message.text().length() > 0) {
                bot.execute(new SendMessage(message.chat().id(), "ok").replyMarkup(new ReplyKeyboardMarkup(new String[]{message.text()})));
                Trace.save("simpleBot: process: message: \"" + message.text() + "\"; answer: \"" + "ok" + "\"");
            }
            else {
                Trace.save("simpleBot: process: message: null or empty");
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
