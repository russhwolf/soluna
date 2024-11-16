package com.russhwolf.soluna

sealed interface EventResult<out TimeUnit : Any> {
    data class Value<TimeUnit : Any>(val time: TimeUnit) : EventResult<TimeUnit>
    data object TooHigh : EventResult<Nothing>
    data object TooLow : EventResult<Nothing>
    data object Error : EventResult<Nothing>
}

internal fun <A : Any, B : Any> EventResult<A>.map(
    transform: (A) -> B
): EventResult<B> =
    when (this) {
        is EventResult.Value -> EventResult.Value(transform(time))
        is EventResult.TooHigh -> EventResult.TooHigh
        is EventResult.TooLow -> EventResult.TooLow
        is EventResult.Error -> EventResult.Error
    }

val <TimeUnit : Any> EventResult<TimeUnit>.timeOrNull: TimeUnit?
    get() = when (this) {
        is EventResult.Value<TimeUnit> -> time
        is EventResult.TooHigh,
        is EventResult.TooLow,
        is EventResult.Error -> null
    }

sealed interface RiseSetResult<out TimeUnit : Any> {
    data class RiseThenSet<TimeUnit : Any>(
        override val riseTime: TimeUnit,
        override val setTime: TimeUnit
    ) : RiseSetResult<TimeUnit>, HasRise<TimeUnit>, HasSet<TimeUnit>

    data class SetThenRise<TimeUnit : Any>(
        override val setTime: TimeUnit,
        override val riseTime: TimeUnit
    ) : RiseSetResult<TimeUnit>, HasRise<TimeUnit>, HasSet<TimeUnit>

    data class RiseOnly<TimeUnit : Any>(override val riseTime: TimeUnit) : RiseSetResult<TimeUnit>, HasRise<TimeUnit>
    data class SetOnly<TimeUnit : Any>(override val setTime: TimeUnit) : RiseSetResult<TimeUnit>, HasSet<TimeUnit>
    data object UpAllDay : RiseSetResult<Nothing>
    data object DownAllDay : RiseSetResult<Nothing>
    data object Unknown : RiseSetResult<Nothing>

    sealed interface HasRise<TimeUnit : Any> : RiseSetResult<TimeUnit> {
        val riseTime: TimeUnit
    }

    sealed interface HasSet<TimeUnit : Any> : RiseSetResult<TimeUnit> {
        val setTime: TimeUnit
    }
}

internal fun <A : Any, B : Any> RiseSetResult<A>.map(
    transform: (A) -> B
): RiseSetResult<B> = when (this) {
    is RiseSetResult.RiseThenSet -> RiseSetResult.RiseThenSet(
        riseTime = transform(riseTime),
        setTime = transform(setTime)
    )

    is RiseSetResult.SetThenRise -> RiseSetResult.SetThenRise(
        setTime = transform(setTime),
        riseTime = transform(riseTime)
    )

    is RiseSetResult.RiseOnly -> RiseSetResult.RiseOnly(transform(riseTime))
    is RiseSetResult.SetOnly -> RiseSetResult.SetOnly(transform(setTime))
    RiseSetResult.DownAllDay -> RiseSetResult.DownAllDay
    RiseSetResult.UpAllDay -> RiseSetResult.UpAllDay
    RiseSetResult.Unknown -> RiseSetResult.Unknown
}

internal fun <TimeUnit : Comparable<TimeUnit>> combineResults(
    riseResult: EventResult<TimeUnit>,
    setResult: EventResult<TimeUnit>
): RiseSetResult<TimeUnit> {
    return when (riseResult) {
        is EventResult.Value -> when (setResult) {
            is EventResult.Value -> {
                if (riseResult.time < setResult.time) {
                    RiseSetResult.RiseThenSet(riseTime = riseResult.time, setTime = setResult.time)
                } else {
                    RiseSetResult.SetThenRise(riseTime = riseResult.time, setTime = setResult.time)
                }
            }

            EventResult.TooHigh,
            EventResult.TooLow,
            EventResult.Error -> RiseSetResult.RiseOnly(riseTime = riseResult.time)
        }

        EventResult.TooHigh -> when (setResult) {
            is EventResult.Value -> RiseSetResult.SetOnly(setTime = setResult.time)
            EventResult.TooHigh,
            EventResult.Error -> RiseSetResult.UpAllDay

            EventResult.TooLow -> RiseSetResult.Unknown
        }

        EventResult.TooLow -> when (setResult) {
            is EventResult.Value -> RiseSetResult.SetOnly(setTime = setResult.time)
            EventResult.TooLow,
            EventResult.Error -> RiseSetResult.DownAllDay

            EventResult.TooHigh -> RiseSetResult.Unknown
        }

        EventResult.Error -> when (setResult) {
            is EventResult.Value -> RiseSetResult.SetOnly(setTime = setResult.time)
            EventResult.TooLow -> RiseSetResult.DownAllDay
            EventResult.TooHigh -> RiseSetResult.UpAllDay
            EventResult.Error -> RiseSetResult.Unknown

        }
    }
}

val <TimeUnit : Any> RiseSetResult<TimeUnit>.riseOrNull: TimeUnit?
    get() = when (this) {
        is RiseSetResult.HasRise -> riseTime
        is RiseSetResult.SetOnly,
        is RiseSetResult.UpAllDay,
        is RiseSetResult.DownAllDay,
        is RiseSetResult.Unknown -> null
    }

val <TimeUnit : Any> RiseSetResult<TimeUnit>.setOrNull: TimeUnit?
    get() = when (this) {
        is RiseSetResult.HasSet -> setTime
        is RiseSetResult.RiseOnly,
        is RiseSetResult.UpAllDay,
        is RiseSetResult.DownAllDay,
        is RiseSetResult.Unknown -> null
    }
