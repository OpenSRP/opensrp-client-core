package org.smartregister.dto;

import java.io.Serializable;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by samuelgithengi on 9/16/20.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserAssignmentDTO implements Serializable {

    private Set<Long> organizationIds;

    private Set<String> jurisdictions;

    private Set<String> plans;

    private boolean isRemoved;
}
