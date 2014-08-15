package com.TigerSun.Game;

import java.util.ArrayList;

import com.TigerSun.tictactoeadventure.util.Dbg;

/**
 * FileName: Layer.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Mar 5, 2014 11:10:38 PM
 */
public class Layer {
    public static final String MODULE = "LYR";
    public static final boolean DBG = false;

    public final ArrayList<Unit> units;

    public Layer(final int layerId, final int attrNum, final int unitNum, final boolean needThres) {
        units = new ArrayList<Unit>();
        for (int i = 0; i < unitNum; i++) {
            units.add(new Unit(layerId, i, attrNum, needThres));
        }
    }

    public Layer(final Layer l) {
        this.units = new ArrayList<Unit>();
        for (Unit u: l.units){
            this.units.add(new Unit(u));
        }
    }

    public ArrayList<Double> getV (final ArrayList<Double> attrs) {
        final ArrayList<Double> results = new ArrayList<Double>();
        for (Unit unit : units) {
            final double result = unit.getV(attrs);
            results.add(result);
        }
        return results;
    }

    public int size () {
        return units.size();
    }
    @Override
    public String toString(){
        return units.toString();
    }
}
