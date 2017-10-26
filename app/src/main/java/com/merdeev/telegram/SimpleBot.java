package com.merdeev.telegram;


import android.os.Handler;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

import java.io.IOException;
import java.util.List;

public class SimpleBot extends TelegramBot implements UpdatesListener {

    /** Контекст */
    private Context context;

    /** Бот */
    private static volatile SimpleBot bot = null;

    private static Handler handler;

    static void create(Context context) throws Exception {
        if (bot == null) {
            synchronized (SimpleBot.class) {
                if ( bot == null ) {
                    bot = new SimpleBot(context, "474700806:AAGhyAWhW2-dK_VFxU7r4jJ0OkjgCR759kc");
                }
            }
        }
    }

    static void kill(Context context) throws Exception {
        if (bot != null) {
            synchronized (SimpleBot.class) {
                if ( bot != null ) {
//                    bot.removeGetUpdatesListener();
                    bot = null;
                    handler = null;
                }
            }
        }
    }

    private SimpleBot(Context context, String token) {
        super(token);
        this.context = context;
//        this.setUpdatesListener(this);
        handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                Trace.save("handler: handleMessage");
                if (handler != null) {
                    getUpdates(new GetUpdates());
                    handler.sendEmptyMessageDelayed(0, 5000);
                }
            }
        };
        handler.sendEmptyMessageDelayed(0, 500);
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

    private void getUpdates(GetUpdates request) {
        if (bot == null) return;

        bot.execute(request, new Callback<GetUpdates, GetUpdatesResponse>() {
            @Override
            public void onResponse(GetUpdates request, GetUpdatesResponse response) {

                if (!response.isOk() || response.updates() == null || response.updates().size() <= 0) {
                    return;
                }

                List<Update> updates = response.updates();
                int lastConfirmedUpdate = process(updates);

                if (lastConfirmedUpdate != CONFIRMED_UPDATES_NONE) {
                    int offset = lastConfirmedUpdate == CONFIRMED_UPDATES_ALL
                            ? lastUpdateId(updates) + 1
                            : lastConfirmedUpdate + 1;
                    request = request.offset(offset);
                }
                getUpdates(request);
            }

            @Override
            public void onFailure(GetUpdates request, IOException e) {

            }
        });
    }

    private int lastUpdateId(List<Update> updates) {
        return updates.get(updates.size() - 1).updateId();
    }
}
