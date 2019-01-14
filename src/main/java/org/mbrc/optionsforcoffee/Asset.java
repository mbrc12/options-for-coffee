package org.mbrc.optionsforcoffee;

public interface Asset {

    public double getCurrentValue();

    public void evolve(Object caller);

    public String getAssetType();
}
