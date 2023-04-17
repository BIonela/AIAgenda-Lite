package com.example.aiagenda.util

enum class AuthenticationStatus {
    SUCCESS,
    ERROR,
    USER_EXISTS,
    NO_INTERNET_CONNECTION,
    EMAIL_NOT_FOUND,
    WRONG_PASSWORD_OR_EMAIL_INVALID,
    ANOTHER_EXCEPTION,
    EMAIL_INVALID,
    TOO_MANY_REQUESTS
}

enum class ValidationError {
    LOADING,
    FIRST_NAME_IS_EMPTY,
    LAST_NAME_IS_EMPTY,
    EMAIL_IS_EMPTY,
    EMAIL_NOT_VALID,
    PASSWORD_IS_EMPTY,
    PASSWORD_SHORT,
    YEAR_NOT_SELECTED
}

enum class UiStatus {
    LOADING,
    SUCCESS,
    ERROR
}