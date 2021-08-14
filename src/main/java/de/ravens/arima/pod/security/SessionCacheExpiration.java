package de.ravens.arima.pod.security;

import org.ehcache.expiry.ExpiryPolicy;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

public class SessionCacheExpiration implements ExpiryPolicy<String, CustomDiscordUserPrincipal> {

    @Override
    public Duration getExpiryForCreation(String s, CustomDiscordUserPrincipal principal) {
        DateTimeFormatter parser2 = ISODateTimeFormat.dateTime();
        return Duration.between(Instant.now(), Instant.ofEpochMilli(parser2.parseDateTime(principal.getExpires()).getMillis()));
    }

    @Override
    public Duration getExpiryForAccess(String s, Supplier<? extends CustomDiscordUserPrincipal> supplier) {
        return null;
    }

    @Override
    public Duration getExpiryForUpdate(String s, Supplier<? extends CustomDiscordUserPrincipal> supplier, CustomDiscordUserPrincipal customDiscordUserPrincipal) {
        return null;
    }
}
