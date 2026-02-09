package com.fortytwo.demeter.analytics.dto;

import java.time.Instant;

public record DateRangeRequest(Instant from, Instant to) {}
