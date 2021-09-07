package anywheresoftware.b4a.objects;

import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.Hide;
import org.json.JSONException;
import org.json.JSONObject;

@BA.Hide

public class SkuDetails {
    private final String mItemType;
    private final String mSku;
    private final String mType;
    private final String mPrice;
    private final long mPriceAmountMicros;
    private final String mPriceCurrencyCode;
    private final String mTitle;
    private final String mDescription;
    private final String mJson;

    public SkuDetails(String jsonSkuDetails) throws JSONException {
        this(IabHelper.ITEM_TYPE_INAPP, jsonSkuDetails);
    }

    public SkuDetails(String itemType, String jsonSkuDetails) throws JSONException {
        this.mItemType = itemType;
        this.mJson = jsonSkuDetails;
        JSONObject o = new JSONObject(this.mJson);
        this.mSku = o.optString("productId");
        this.mType = o.optString("type");
        this.mPrice = o.optString("price");
        this.mTitle = o.optString("title");
        this.mDescription = o.optString("description");
        this.mPriceAmountMicros = o.optLong("price_amount_micros");
        this.mPriceCurrencyCode = o.optString("price_currency_code");
    }

    public String getSku() { return this.mSku; }
    public String getType() { return this.mType; }
    public String getPrice() { return this.mPrice; }
    public long getPriceAmountMicros() { return this.mPriceAmountMicros; }
    public String getPriceCurrencyCode() { return this.mPriceCurrencyCode; }
    public String getTitle() { return this.mTitle; }
    public String getDescription() { return this.mDescription; }

    @Override
    public String toString() {
        return "SkuDetails:" + this.mJson;
    }
}
