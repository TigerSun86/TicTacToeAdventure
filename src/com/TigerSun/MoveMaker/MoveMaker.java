package com.TigerSun.MoveMaker;

import com.TigerSun.Game.GameProblem;
import com.TigerSun.Game.Record;

public interface MoveMaker {
    /** @return Action made by the MoveMaker. */
    Object makeMove (GameProblem gameProblem, Record record);
}
