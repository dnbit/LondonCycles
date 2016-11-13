
package com.dnbitstudio.londoncycles.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class BikePoint {

    @SerializedName("$type")
    @Expose
    private String $type;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("commonName")
    @Expose
    private String commonName;
    @SerializedName("placeType")
    @Expose
    private String placeType;
    @SerializedName("additionalProperties")
    @Expose
    private List<AdditionalProperty> additionalProperties = new ArrayList<AdditionalProperty>();
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lon")
    @Expose
    private double lon;

    /**
     * No args constructor for use in serialization
     */
    public BikePoint() {
    }

    /**
     *
     * @param id
     * @param commonName
     * @param lon
     * @param additionalProperties
     * @param $type
     * @param placeType
     * @param lat
     * @param url
     */
    public BikePoint(String $type, String id, String url, String commonName, String placeType, List<AdditionalProperty> additionalProperties, double lat, double lon) {
        this.$type = $type;
        this.id = id;
        this.url = url;
        this.commonName = commonName;
        this.placeType = placeType;
        this.additionalProperties = additionalProperties;
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * @return The $type
     */
    public String get$type() {
        return $type;
    }

    /**
     * @param $type The $type
     */
    public void set$type(String $type) {
        this.$type = $type;
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return The commonName
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * @param commonName The commonName
     */
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    /**
     * @return The placeType
     */
    public String getPlaceType() {
        return placeType;
    }

    /**
     * @param placeType The placeType
     */
    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

    /**
     * @return The additionalProperties
     */
    public List<AdditionalProperty> getAdditionalProperties() {
        return additionalProperties;
    }

    /**
     * @param additionalProperties The additionalProperties
     */
    public void setAdditionalProperties(List<AdditionalProperty> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    /**
     * @return The lat
     */
    public double getLat() {
        return lat;
    }

    /**
     * @param lat The lat
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * @return The lon
     */
    public double getLon() {
        return lon;
    }

    /**
     * @param lon The lon
     */
    public void setLon(double lon) {
        this.lon = lon;
    }
}
