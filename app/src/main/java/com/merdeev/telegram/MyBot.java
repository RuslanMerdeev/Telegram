package com.merdeev.telegram;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by r.merdeev on 30.10.2017.
 */

public class MyBot implements Runnable {

    private Boolean stop;

    private int offset = 0;

    private static final String TOKEN = "474700806:AAGhyAWhW2-dK_VFxU7r4jJ0OkjgCR759kc";
    private static final int USER = 477473163;

    static MyBot create() {
        MyBot myBot = new MyBot();
        new Thread(myBot).start();
        return myBot;
    }

    void kill() {
        stop = true;
    }

    MyBot(){
        stop = false;
    }

    @Override

    public void run() {
        Trace.save("myBot: run");
        while (!stop) {
            try {
                Trace.save("myBot: run: offset: " + offset);
                String[][] data = getUpdates(offset+1);

                Trace.save("myBot: run: data: " + data.length);
                if (data != null) {
                    Trace.save("myBot: run: 1");
                    for (int i=0; i<data.length; i++) {
                        int update_id = Integer.parseInt(data[i][0]);
                        int user_id = Integer.parseInt(data[i][1]);
                        if (user_id == USER) {
                            int chat_id = Integer.parseInt(data[i][2]);
                            String text = data[i][3];
                            sendMessage(chat_id, text);
                        }
                        if ( update_id > offset) offset = update_id;
                    }
                }

                Thread.sleep(5000);
            }
            // Выводится трейс для исключения
            catch (Exception e) {
                Trace.save("myBot " + e.getClass() + ": " + e.getMessage());
                StackTraceElement[] el = e.getStackTrace();
                for (StackTraceElement i : el) {
                    Trace.save(i.getFileName() + ": " + i.getLineNumber() + ": " + i.getMethodName());
                }
            }
        }
    }

    private String[][] getUpdates(int offset) throws Exception {
        Trace.save("myBot: getUpdates");
        // Открывается соединение
        URLConnection uc = new URL("https://api.telegram.org/bot" + TOKEN + "/getUpdates?offset=" + offset).openConnection();

        String answer = toString(uc);

        Trace.save("myBot: answer:\n" + answer);

        if ( !findOk(answer) ) return null;

        String[] text = splitText(answer);
        return splitData(text);
    }

    private String toString(URLConnection uc) throws Exception {
        Trace.save("myBot: toString");

        int contentLength = uc.getContentLength();
        InputStream in = new BufferedInputStream(uc.getInputStream());
        byte[] data = new byte[contentLength];
        int bytesRead;
        int offset = 0;
        while (offset < contentLength) {
            bytesRead = in.read(data, offset, data.length - offset);
            if (bytesRead == -1)
                break;
            offset += bytesRead;
        }
        in.close();

        if (offset != contentLength) {
            throw new IOException("Only read " + offset
                    + " bytes; Expected " + contentLength + " bytes");
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(data);

        // Возвращается строка нужной кодировки
        return stream.toString("Cp1251");
    }

    private Boolean findOk (String text) throws Exception {
        if (text.substring(text.indexOf("\"ok\":")+5, text.indexOf(",")).matches("true") ) return true;
        return false;
    }

    private String[] splitText (String text) throws Exception {
        Trace.save("myBot: splitText");
        String temp = text.substring(text.indexOf("[") + 1, text.lastIndexOf("]"));
        if (!temp.matches("")) {
            temp = text.substring(text.indexOf("\"update_id\":") + 12, text.lastIndexOf("\"}}"));
            return temp.split("\"\\}\\},\\{\"update_id\":");
        }
        return new String[0];
    }

    private String[][] splitData (String[] text) throws Exception {
        String[][] data = new String[text.length][4];
        for (int i=0; i<text.length; i++) {
            data[i][0] = text[i].substring(0,text[i].indexOf(","));
            String temp = text[i].substring(text[i].indexOf("\"id\":") + 5);
            data[i][1] = temp.substring(0,temp.indexOf(","));
            temp = temp.substring(temp.indexOf("\"id\":") + 5);
            data[i][2] = temp.substring(0,temp.indexOf(","));
            data[i][3] = temp.substring(temp.indexOf("\"text\":\"") + 8);
        }

        return data;
    }

    private void sendMessage(int chat_id, String text) throws Exception {
        String answer = configAnswer(text);
        URLConnection uc = new URL("https://api.telegram.org/bot" + TOKEN + "/sendMessage?chat_id=" + chat_id + "&text=" + answer).openConnection();
        answer = toString(uc);

        if ( !findOk(answer) ) return;
    }

    private String configAnswer (String text) {
        return "ok";
    }

}
