package ru.yandex.practicum.filmorate.Exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String s) {
        super(s);
    }
}
