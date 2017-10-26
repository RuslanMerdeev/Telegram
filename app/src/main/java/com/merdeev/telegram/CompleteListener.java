package com.merdeev.telegram;

/**
 * Ожидание уведомления о завершении выполнения задачи,
 * необходимо для запуска последовательности задач
 * @author R.Z.Merdeev
 */
interface CompleteListener {

    /**
     * При завершении выполнения задачи
     * @param cc источник вызова, объект класса
     * @param result результат, произвольные данные
     * @param type тип данных
     */
    void complete(Object cc, Object result, Class type);
}
