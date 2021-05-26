/**
 *
 */
package org.smartregister.domain.jsonmapping.util;

import java.util.HashSet;
import java.util.Set;

public class TeamMember {

    public String identifier;

    public String teamMemberId;

    public Set<TeamLocation> locations = new HashSet<>();

    public Team team;

    public String uuid;

}
