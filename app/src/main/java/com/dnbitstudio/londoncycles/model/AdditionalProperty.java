
package com.dnbitstudio.londoncycles.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class AdditionalProperty {

    @SerializedName("$type")
    @Expose
    private String $type;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("sourceSystemKey")
    @Expose
    private String sourceSystemKey;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("modified")
    @Expose
    private String modified;

    /**
     * No args constructor for use in serialization
     */
    public AdditionalProperty() {
    }

    /**
     *
     * @param category
     * @param sourceSystemKey
     * @param value
     * @param $type
     * @param key
     * @param modified
     */
    public AdditionalProperty(String $type, String category, String key, String sourceSystemKey, String value, String modified) {
        this.$type = $type;
        this.category = category;
        this.key = key;
        this.sourceSystemKey = sourceSystemKey;
        this.value = value;
        this.modified = modified;
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
     * @return The category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category The category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return The key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key The key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return The sourceSystemKey
     */
    public String getSourceSystemKey() {
        return sourceSystemKey;
    }

    /**
     * @param sourceSystemKey The sourceSystemKey
     */
    public void setSourceSystemKey(String sourceSystemKey) {
        this.sourceSystemKey = sourceSystemKey;
    }

    /**
     * @return The value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value The value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return The modified
     */
    public String getModified() {
        return modified;
    }

    /**
     * @param modified The modified
     */
    public void setModified(String modified) {
        this.modified = modified;
    }
}
