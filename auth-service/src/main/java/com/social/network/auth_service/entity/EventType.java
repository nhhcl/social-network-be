package com.social.network.auth_service.entity;

import lombok.Getter;

@Getter
public enum EventType {
    CREATE_USER("create_user");
    private final String value;

    EventType(String value) {
        this.value = value;
    }

}
