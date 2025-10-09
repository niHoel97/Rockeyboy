package no.uio.ifi.in2000.team47.rocketboy.model.grib

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GribResponse(
    @SerialName("id") val id : String,
    @SerialName("type") val type : String,
    @SerialName("domain") val domain : Domain,
    @SerialName("parameters") val parameters : Parameters,
    @SerialName("ranges") val ranges : Ranges
)

@Serializable
data class Domain (
    @SerialName("type") val type : String,
    @SerialName("domainType") val domainType : String,
    @SerialName("axes") val axes : Axes,
    @SerialName("referencing") val referencing : List<Referencing>
)

@Serializable
data class Axes (
    @SerialName("x") val x : X,
    @SerialName("y") val y : Y,
    @SerialName("z") val z : Z,
    @SerialName("t") val t : T
)

@Serializable
data class X (
    @SerialName("values") val values : List<Double>
)

@Serializable
data class Y (
    @SerialName("values") val values : List<Double>
)

@Serializable
data class Z (
    @SerialName("values") val values : List<Double>
)

@Serializable
data class T (
    @SerialName("values") val values : List<String>
)

@Serializable
data class Referencing (
    @SerialName("coordinates") val coordinates : List<String>,
    @SerialName("system") val system : System
)

@Serializable
data class System (
    @SerialName("type") val type : String,
    @SerialName("id") val id : String? = null,
    @SerialName("cs") val cs : Cs? = null,
    @SerialName("calendar") val calendar : String? = null
)

@Serializable
data class Cs(
    @SerialName("csAxes") val csAxes : List<CsAxes>
)

@Serializable
data class CsAxes (
    @SerialName("name") val name : Name,
    @SerialName("direction") val direction : String,
    @SerialName("unit") val unit : UnitCs
)

@Serializable
data class Name (
    @SerialName("en") val en : String
)

@Serializable
data class UnitCs (
    @SerialName("symbol") val symbol : String
)

@Serializable
data class Parameters (
    @SerialName("temperature") val temperature : ParamsTemperature,
    @SerialName("wind_from_direction") val wind_from_direction : ParamsWindFromDirection,
    @SerialName("wind_speed") val wind_speed : ParamsWindSpeed
    
)

@Serializable
data class ParamsTemperature (
    @SerialName("type") val type : String,
    @SerialName("id") val id : String,
    @SerialName("label") val label : Label,
    @SerialName("observedProperty") val observedProperty : ObservedProperty,
    @SerialName("unit") val unit : Unit
)

@Serializable
data class Temperature (
    @SerialName("type") val type : String,
    @SerialName("dataType") val dataType : String,
    @SerialName("axisNames") val axisNames : List<String>,
    @SerialName("shape") val shape : List<Int>,
    @SerialName("values") val values : List<Double>
)

@Serializable
data class ParamsWindFromDirection (
    @SerialName("type") val type : String,
    @SerialName("id") val id : String,
    @SerialName("label") val label : Label,
    @SerialName("observedProperty") val observedProperty : ObservedProperty,
    @SerialName("unit") val unit : Unit
)

@Serializable
data class WindFromDirection (
    @SerialName("type") val type : String,
    @SerialName("dataType") val dataType : String,
    @SerialName("axisNames") val axisNames : List<String>,
    @SerialName("shape") val shape : List<Int>,
    @SerialName("values") val values : List<Double>
)

@Serializable
data class ParamsWindSpeed (
    @SerialName("type") val type : String,
    @SerialName("id") val id : String,
    @SerialName("label") val label : Label,
    @SerialName("observedProperty") val observedProperty : ObservedProperty,
    @SerialName("unit") val unit : Unit
)

@Serializable
data class WindSpeed (
    @SerialName("type") val type : String,
    @SerialName("dataType") val dataType : String,
    @SerialName("axisNames") val axisNames : List<String>,
    @SerialName("shape") val shape : List<Int>,
    @SerialName("values") val values : List<Double>
)

@Serializable
data class Ranges (
    @SerialName("temperature") val temperature : Temperature,
    @SerialName("wind_from_direction") val wind_from_direction : WindFromDirection,
    @SerialName("wind_speed") val wind_speed : WindSpeed
)

@Serializable
data class Label (
    @SerialName("en") val en : String
)

@Serializable
data class ObservedProperty (
    @SerialName("id") val id : String,
    @SerialName("label") val label : Label
)

@Serializable
data class Unit (
    @SerialName("id") val id : String,
    @SerialName("label") val label : Label,
    @SerialName("symbol") val symbol : String
)



