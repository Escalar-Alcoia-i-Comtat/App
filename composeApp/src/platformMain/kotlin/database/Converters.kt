package database

import androidx.room.TypeConverter
import data.generic.Builder
import data.generic.ExternalTrack
import data.generic.LatLng
import data.generic.PitchInfo
import data.generic.Point
import kotlinx.datetime.Instant
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

object Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.fromEpochMilliseconds(it) }
    }

    @TypeConverter
    fun instantToTimestamp(instant: Instant?): Long? {
        return instant?.toEpochMilliseconds()
    }


    @TypeConverter
    fun fromLatLng(value: String?): LatLng? {
        return value?.let(LatLng::valueOf)
    }

    @TypeConverter
    fun toLatLng(value: LatLng?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun fromUuid(value: String?): Uuid? {
        return value?.let(Uuid::parse)
    }

    @TypeConverter
    fun toUuid(value: Uuid?): String? {
        return value?.toString()
    }


    @TypeConverter
    fun fromPointList(value: String?): List<Point>? {
        return value?.let { Json.decodeFromString(ListSerializer(Point.serializer()), it) }
    }

    @TypeConverter
    fun toPointList(value: List<Point>?): String? {
        return value?.let { Json.encodeToString(ListSerializer(Point.serializer()), it) }
    }


    @TypeConverter
    fun fromExternalTrackList(value: String?): List<ExternalTrack>? {
        return value?.let { Json.decodeFromString(ListSerializer(ExternalTrack.serializer()), it) }
    }

    @TypeConverter
    fun toExternalTrackList(value: List<ExternalTrack>?): String? {
        return value?.let { Json.encodeToString(ListSerializer(ExternalTrack.serializer()), it) }
    }


    @TypeConverter
    fun fromPitchInfoList(value: String?): List<PitchInfo>? {
        return value?.let { Json.decodeFromString(ListSerializer(PitchInfo.serializer()), it) }
    }

    @TypeConverter
    fun toPitchInfoList(value: List<PitchInfo>?): String? {
        return value?.let { Json.encodeToString(ListSerializer(PitchInfo.serializer()), it) }
    }


    @TypeConverter
    fun fromBuilderList(value: String?): List<Builder>? {
        return value?.let { Json.decodeFromString(ListSerializer(Builder.serializer()), it) }
    }

    @TypeConverter
    fun toBuilderList(value: List<Builder>?): String? {
        return value?.let { Json.encodeToString(ListSerializer(Builder.serializer()), it) }
    }


    @TypeConverter
    fun fromBuilder(value: String?): Builder? {
        return value?.let { Json.decodeFromString(Builder.serializer(), it) }
    }

    @TypeConverter
    fun toBuilder(value: Builder?): String? {
        return value?.let { Json.encodeToString(Builder.serializer(), it) }
    }


    @TypeConverter
    fun fromUuidList(value: String?): List<Uuid>? {
        return value?.split(',')?.map(Uuid::parse)
    }

    @TypeConverter
    fun toUuidList(value: List<Uuid>?): String? {
        return value?.joinToString(",")
    }
}
