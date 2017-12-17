package models;

public class Contract {
    public enum AssetType {
        SPADE,
        HEART,
        DIAMOND,
        CLUB,
        ALL_ASSET,
        WITHOUT_ASSET,
        CHECK,
        TEST
    }

    public AssetType mAsset;
    public int mValue;
    public String mOwnerName;
    public boolean mIsCoinched;
    public boolean mIsOverCoinched;

    // Empty constructor for serialization
    public Contract() {

    }

    public Contract(AssetType asset, int value) {
        mAsset = asset;
        mValue = value;
    }
}
