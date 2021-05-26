package org.smartregister.domain.jsonmapping;

import org.smartregister.domain.jsonmapping.util.LocationTree;
import org.smartregister.domain.jsonmapping.util.TeamMember;

import java.util.List;
import java.util.Set;

/**
 * Created by keyman on 2/28/2018.
 */

public class LoginResponseData {
    public User user;
    public Time time;
    public LocationTree locations;
    public TeamMember team;
    public List<String> jurisdictions;
    public Set<String> jurisdictionIds;
}
