package com.social.network.auth_service.entity;

public enum OutboxStatus {
    DLQ,
    SENT,
    SEND_FAILED
}
