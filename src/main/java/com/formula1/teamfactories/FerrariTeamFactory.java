package com.formula1.teamfactories;

import java.util.Arrays;

import com.formula1.factories.TeamFactory;
import com.formula1.teams.Team;

public class FerrariTeamFactory implements TeamFactory {
    @Override
    public Team createtTeam(int id_equipo) {
        return new Team(id_equipo, "Ferrari", "Italia", "Ferrari", Arrays.asList(5, 6));
    }
}
