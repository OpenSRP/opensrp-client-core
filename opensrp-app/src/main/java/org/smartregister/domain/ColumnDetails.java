package org.smartregister.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ColumnDetails {
    private String name;
    private String dataType;
    private String length;
    private String defaultValue;
}
