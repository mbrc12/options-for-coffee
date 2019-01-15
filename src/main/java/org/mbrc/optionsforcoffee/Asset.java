package org.mbrc.optionsforcoffee;

public interface Asset {

    double getCurrentValue();

    void evolve(Object caller);

    String getAssetType();
}
