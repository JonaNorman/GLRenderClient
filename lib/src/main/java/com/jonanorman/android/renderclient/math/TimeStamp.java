package com.jonanorman.android.renderclient.math;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TimeStamp {

    private TimeUnit timeUnit;
    private long duration;


    public static final TimeStamp MIN_VALUE = new TimeStamp(TimeUnit.NANOSECONDS) {
        @Override
        public void setDuration(long duration) {
            throw new RuntimeException("TimeStamp MinValue can not setDuration");
        }

        @Override
        public void setTimeStamp(TimeStamp timeStamp) {
            throw new RuntimeException("TimeStamp MinValue can not setTimeStamp");
        }
    };

    public static final TimeStamp MAX_VALUE = new TimeStamp(TimeUnit.NANOSECONDS, Long.MAX_VALUE) {
        @Override
        public void setDuration(long duration) {
            throw new RuntimeException("TimeStamp MaxValue can not setDuration");
        }

        @Override
        public void setTimeStamp(TimeStamp timeStamp) {
            throw new RuntimeException("TimeStamp MaxValue can not setTimeStamp");
        }
    };

    public static final TimeStamp MATCH_PARENT_VALUE = new TimeStamp(TimeUnit.NANOSECONDS, -1) {
        @Override
        public void setDuration(long duration) {
            throw new RuntimeException("TimeStamp MatchParentValue can not setDuration");
        }

        @Override
        public void setTimeStamp(TimeStamp timeStamp) {
            throw new RuntimeException("TimeStamp MatchParentValue can not setTimeStamp");
        }
    };

    private TimeStamp(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    private TimeStamp(TimeUnit timeUnit, long duration) {
        this.timeUnit = timeUnit;
        this.duration = duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public long toNanos() {
        return timeUnit.toNanos(duration);
    }

    public long toMicros() {
        return timeUnit.toMicros(duration);
    }

    public long toMillis() {
        return timeUnit.toMillis(duration);
    }

    public long toSeconds() {
        return timeUnit.toSeconds(duration);
    }

    public float toSecondsFloatValue() {
        return toNanos() / 1000000000f;
    }

    public long toMinutes() {
        return timeUnit.toMinutes(duration);
    }

    public long toHours() {
        return timeUnit.toMinutes(duration);
    }

    public long toDays() {
        return timeUnit.toMinutes(duration);
    }


    public void setDuration(TimeStamp timeStamp) {
        if (timeUnit == TimeUnit.NANOSECONDS) {
            setDuration(timeStamp.toNanos());
        } else if (timeUnit == TimeUnit.MICROSECONDS) {
            setDuration(timeStamp.toMicros());
        } else if (timeUnit == TimeUnit.MILLISECONDS) {
            setDuration(timeStamp.toMillis());
        } else if (timeUnit == TimeUnit.SECONDS) {
            setDuration(timeStamp.toSeconds());
        } else if (timeUnit == TimeUnit.MINUTES) {
            setDuration(timeStamp.toMinutes());
        } else if (timeUnit == TimeUnit.HOURS) {
            setDuration(timeStamp.toHours());
        } else if (timeUnit == TimeUnit.DAYS) {
            setDuration(timeStamp.toHours());
        }
    }


    public void setTimeStamp(TimeStamp timeStamp) {
        timeUnit = timeStamp.timeUnit;
        duration = timeStamp.duration;
    }

    public static TimeStamp of(TimeUnit timeUnit) {
        return new TimeStamp(timeUnit);
    }

    public static TimeStamp of(TimeStamp stamp) {
        TimeStamp timeStamp = TimeStamp.ofNanos(0);
        timeStamp.setTimeStamp(stamp);
        return timeStamp;
    }

    public static TimeStamp of(TimeUnit timeUnit, long duration) {
        TimeStamp timeDuration = new TimeStamp(timeUnit);
        timeDuration.setDuration(duration);
        return timeDuration;
    }

    public static TimeStamp ofNanos(long duration) {
        return of(TimeUnit.NANOSECONDS, duration);
    }

    public static TimeStamp ofMicros(long duration) {
        return of(TimeUnit.MICROSECONDS, duration);
    }

    public static TimeStamp ofMills(long duration) {
        return of(TimeUnit.MILLISECONDS, duration);
    }

    public static TimeStamp ofSeconds(long duration) {
        return of(TimeUnit.SECONDS, duration);
    }

    public static TimeStamp ofMinutes(long duration) {
        return of(TimeUnit.MINUTES, duration);
    }


    public static TimeStamp ofHours(long duration) {
        return of(TimeUnit.HOURS, duration);
    }

    public static TimeStamp ofDays(long duration) {
        return of(TimeUnit.DAYS, duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeStamp)) return false;
        TimeStamp timeStamp = (TimeStamp) o;
        return toNanos() == timeStamp.toNanos();
    }

    @Override
    public int hashCode() {
        return Objects.hash(TimeUnit.NANOSECONDS, toNanos());
    }
}
