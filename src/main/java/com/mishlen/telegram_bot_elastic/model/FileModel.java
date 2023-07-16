package com.mishlen.telegram_bot_elastic.model;

import lombok.Data;

@Data
public class FileModel {
    String application;
    String level;
    String env;
    String value;
    String date;
}
