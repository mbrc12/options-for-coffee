package org.mbrc.optionsforcoffee;

public class SignedAsset implements Asset {

    Asset asset;
    InteractionManager.Direction direction;

    SignedAsset(Asset asset, InteractionManager.Direction direction) {
        this.asset = asset;
        this.direction = direction;
    }

    @Override
    public double getCurrentValue() {
        int dirMultiplier = (direction == InteractionManager.Direction.LONG) ? 1 : -1;
        return asset.getCurrentValue() * dirMultiplier;
    }

    @Override
    public void evolve(Object caller) {
        asset.evolve(caller);
    }

    @Override
    public String getAssetType() {
        if (direction == InteractionManager.Direction.SHORT) {
            return "Short " + asset.getAssetType();
        } else {
            return "Long " + asset.getAssetType();
        }
    }
}
