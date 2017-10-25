package ru.oleg.rsoi.remoteservice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RemoteRsoiService<Request, Response> {
    /**
     * Создаёт полный URL для удалённого сервиса с заданным окончанием
     * Пример: getUrl(/movie/{id}) -> http://localhost:8082/movie/{id}
     * @param postfix окончание url
     * @return url для связи с сервисом
     */
    String getUrl(String postfix);

    /**
     * Запрос на создание объекта
     */
    Response create(Request request, String postfix);

    /**
     * Запрос на поиск объекта по идентификатору
     */
    Response find(int id, String postfix);

    /**
     * Запрос на поиск всех объектов по идентификатору
     */
    List<Response> findAll(int id, String postfix);

    /**
     * Запрос на поиск всех объектов постранично
     */
    Page<Response> findAllPaged(Pageable page, String postfix);

    /**
     *  Запрос на обновление объекта
     */
    void update(int id, Request request, String postfix);

    /**
     * Запрсо на удаление объекта
     */
    void delete(int id, String postfix);

    /**
     * Запрос, существует ли объект
     */
    boolean exists(int id, String postfix);


}
