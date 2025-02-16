package com.ai.pj.service;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Service;

@Service
public class LogMarkerService {
    private final Marker expiredTokenMarker;
    private final Marker invalidTokenMarker;

    public LogMarkerService() {
        this.expiredTokenMarker = MarkerFactory.getMarker("EXPIRED_TOKEN");
        this.invalidTokenMarker =   MarkerFactory.getMarker("INVALID_TOKEN");
    }

    public Marker getExpiredTokenMarker() {
        return expiredTokenMarker;
    }

    public Marker getInvalidTokenMarker() {
        return invalidTokenMarker;
    }
}
