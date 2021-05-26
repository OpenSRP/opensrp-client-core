package org.smartregister.domain.jsonmapping;

import com.google.gson.annotations.SerializedName;

/**
 * Created by keyman on 2/21/2018.
 */

public class Column {
    public String column_name;
    public String type;
    @SerializedName("data_type")
    public String dataType;
    @SerializedName("source_format")
    public String sourceFormat;
    @SerializedName("save_format")
    public String saveFormat;
    public JsonMapping json_mapping;
}
