package com.example.vestigioapi.core.common.util;

public class ValidationParam {
    public static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=_])(?=\\S+$).{8,}$";
    public static final int PASSWORD_MIN = 8;
}
