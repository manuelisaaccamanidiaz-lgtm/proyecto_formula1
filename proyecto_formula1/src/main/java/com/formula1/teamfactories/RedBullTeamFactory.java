package com.formula1.teamfactories;

import java.util.Arrays;

import com.formula1.factories.TeamFactory;
import com.formula1.teams.Team;

public class RedBullTeamFactory implements TeamFactory {
    @Override
    public Team createtTeam(int id_equipo) {
        return new Team(id_equipo, "Red Bull Racing", "Austria", "Honda", Arrays.asList(1, 2));
    }
}
