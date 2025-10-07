package net.runith.runithstats.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

@UtilityClass
public final class TimeFormatter {

    public static String formatSince(Instant instant, Instant since) {
        return format(Duration.between(instant, since).getSeconds());
    }

    public static @NotNull String format(long seconds) {
        long years = seconds / (365L * 24 * 60 * 60);
        long months = seconds / (30L * 24 * 60 * 60);
        long days = seconds / (24 * 60 * 60);
        long hours = seconds / (60 * 60);
        long minutes = seconds / 60;

        if (seconds < 60) {
            return seconds + "s";
        } else if (minutes < 60) {
            long sec = seconds % 60;
            return sec == 0 ? minutes + "m " :  minutes + "m " + sec + "s";
        } else if (hours < 24) {
            long min = (seconds % (60 * 60)) / 60;
            return min == 0 ? hours + "h " :  hours + "h " + min + "m";
        } else if (days < 30) {
            long hr = (seconds % (24 * 60 * 60)) / (60 * 60);
            return hr == 0 ? days + "d " :  days + "d " + hr + "h";
        } else if (months < 12) {
            long d = (seconds % (30L * 24 * 60 * 60)) / (24 * 60 * 60);
            return months + "m " + d + "d";
        } else {
            long d = (seconds % (365L * 24 * 60 * 60)) / (24 * 60 * 60);
            return years + "y " + d + "d";
        }
    }
}