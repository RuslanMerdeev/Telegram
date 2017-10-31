package com.merdeev.telegram;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pengrad.telegrambot.request.GetUpdates;

public class MainActivity extends Context implements View.OnClickListener{

    private String app_name;
    private Button btnCreate, btnKill;
    private MyBot myBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            app_name = getResources().getString(R.string.app_name);

            // Создается трейсер
            new Trace(app_name, "log.txt");

            Trace.save("mainActivity: onCreate");

            myBot = MyBot.create();//SimpleBot.create(this);

            btnCreate = (Button) findViewById(R.id.btnCreate);
            btnCreate.setOnClickListener(this);

            btnKill = (Button) findViewById(R.id.btnKill);
            btnKill.setOnClickListener(this);
        }
        // Выводится трейс для исключения
        catch (Exception e) {
            Trace.showException(this, e, "mainActivity: onCreate:");
        }
    }

    /**
     * Определяет источник вызова и
     * обрабатывает данные {@link CompleteListener}
     * @param cc источник вызова, объект класса
     * @param result результат, произвольные данные
     * @param type тип данных
     */
    @Override
    public void complete(Object cc, Object result, Class type) {
        Trace.save("mainActivity: complete");

        try {
            // Проверяется, что источника вызова нет
            if (cc == null) {
                Trace.save("mainActivity: complete: сс: null");
                Dialog.showError(this);
                return;
            }

            // Проверяется, что результата нет
            if (result == null) {
                Trace.save("mainActivity: complete: result: null");
                Dialog.showError(this);
                return;
            }

            // Проверяется, что типа результата нет
            if (type == null) {
                Trace.save("mainActivity: complete: type: null");
                Dialog.showError(this);
                return;
            }

            // Проверяется, что источник - объект класса SimpleBot
            if (cc instanceof SimpleBot) {
                Trace.save("mainActivity: complete: SimpleBot");
            }
            // Проверяется, что источник - объект класса Dialog
            else if (cc instanceof Dialog) {
                Trace.save("mainActivity: complete: Dialog");

                // Проверяется, что тип результата массив идентификаторов
                if (type == int[].class) {
                    // Определяется диалог и кнопка
                    int dialog = ((int[])result)[0];
                    int button = ((int[])result)[1];

                    // Проверяется, что это диалог ошибки
                    if (dialog == Dialog.DIALOG_ERROR){
                        Trace.save("mainActivity: complete: Dialog: error");

                        switch (button) {
                            // Проверяется, что нажималась позитивная кнопка
                            case android.app.Dialog.BUTTON_POSITIVE:
                                Trace.save("mainActivity: complete: Dialog: error: positive");
                                finish();
                                break;

                            // Проверяется, что произошел cancel
                            case Dialog.BUTTON_CANCEL:
                                Trace.save("mainActivity: complete: Dialog: error: cancel");
                                finish();
                                break;

                            default:
                                Trace.save("mainActivity: complete: Dialog: error: unknown button");
                                Dialog.showError(this);
                                break;
                        }
                    }
                    // Проверяется, что это диалог прогресса
                    else if (dialog == Dialog.DIALOG_PROGRESS) {
                        Trace.save("mainActivity: complete: Dialog: progress");

                        switch (button) {
                            // Проверяется, что произошел cancel
                            case Dialog.BUTTON_CANCEL:
                                Trace.save("mainActivity: complete: Dialog: progress: cancel");
                                break;

                            default:
                                Trace.save("mainActivity: complete: Dialog: progress: unknown button");
                                Dialog.showError(this);
                                break;
                        }
                    }
                    else {
                        Trace.save("mainActivity: complete: Dialog: unknown dialog");
                        Dialog.showError(this);
                    }
                } else {
                    Trace.save("mainActivity: complete: Dialog: unknown type");
                    Dialog.showError(this);
                }
            } else {
                Trace.save("mainActivity: complete: unknown cc");
                Dialog.showError(this);
            }
        }
        // Выводится трейс для исключения
        catch (Exception e) {
            Trace.showException(this, e, "mainActivity: complete:");
        }
    }

    @Override
    public void onClick(View view) {
        Trace.save("mainActivity: onClick");

        try {
            switch (view.getId()) {
                case R.id.btnCreate:
//                    SimpleBot.create(this);
                    if (myBot != null) myBot.kill();
                    myBot = MyBot.create();
                    break;

                case R.id.btnKill:
//                    SimpleBot.kill(this);
                    if (myBot != null) myBot.kill();
                    break;
            }
        }
        // Выводится трейс для исключения
        catch (Exception e) {
            Trace.showException(this, e, "mainActivity: onClick:");
        }
    }
}
