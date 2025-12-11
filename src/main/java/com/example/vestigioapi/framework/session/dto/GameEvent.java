package com.example.vestigioapi.framework.session.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameEvent<T> {
    private String eventType;
    private T payload;
}
