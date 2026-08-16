package com.formula1.factories;

import com.formula1.pilots.Pilot;

public interface PilotFactory {
    Pilot createPilot(int id_piloto);
}
