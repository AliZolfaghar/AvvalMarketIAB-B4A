/*   1:    */ package anywheresoftware.b4a.inappbilling3;
/*   2:    */
/*   3:    */ //import android.R.string;
import anywheresoftware.b4a.BA;
/*   4:    */ //import anywheresoftware.b4a.BA.Hide;
/*   5:    */ //import anywheresoftware.b4a.BA.SharedProcessBA;
/*   6:    */ //import anywheresoftware.b4a.BA.ShortName;
/*   7:    */ //import anywheresoftware.b4a.BA.Version;
/*   8:    */ import anywheresoftware.b4a.objects.Inventory;
/*   9:    */ import anywheresoftware.b4a.objects.IabHelper;
/*  10:    */ //import anywheresoftware.b4a.objects.IbHelper.OnConsumeFinishedListener;
/*  11:    */ //import anywheresoftware.b4a.objects.IbHelper.OnIabPurchaseFinishedListener;
/*  12:    */ //import anywheresoftware.b4a.objects.IbHelper.OnIabSetupFinishedListener;
/*  13:    */ //import anywheresoftware.b4a.objects.IbHelper.QueryInventoryFinishedListener;
/*  14:    */ import anywheresoftware.b4a.objects.IabResult;
/*  15:    */ import anywheresoftware.b4a.objects.Purchase;
import anywheresoftware.b4a.objects.collections.Map;
/*  16:    */ //import anywheresoftware.b4a.objects.collections.Map.MyMap;
/*  17:    */ //import java.lang.ref.WeakReference;
/*  18:    */ import org.json.JSONException;
                import org.json.JSONObject;
/*  20:    */
/*  21:    */ @BA.ShortName("BillingManager3")
/*  22:    */ @BA.Version(1.2F)
/*  23:    */ public class BillingManager3
/*  24:    */ {
    /*  25:    */   private String eventName;
    /*  26:    */   private IabHelper helper;
    /*  27:    */
/*  28:    */   public void Initialize(final BA ba, String EventName, String PublicKey)
/*  29:    */   {
/*  30: 42 */     this.eventName = EventName.toLowerCase(BA.cul);
/*  31: 43 */     this.helper = new IabHelper(BA.applicationContext, PublicKey);
/*  32: 44 */     this.helper.startSetup(new IabHelper.OnIabSetupFinishedListener()
/*  33:    */     {
            /*  34:    */       public void onIabSetupFinished(IabResult result)
/*  35:    */       {
/*  36: 48 */         ba.raiseEvent(BillingManager3.this, BillingManager3.this.eventName + "_billingsupported", new Object[] { Boolean.valueOf(result.isSuccess()),
/*  37: 49 */           result.getMessage() == null ? "" : result.getMessage() });
/*  38:    */       }
/*  39:    */     });
/*  40:    */   }
    /*  41:    */
/*  42:    */   public void setDebugLogging(boolean v)
/*  43:    */   {
/*  44: 57 */     this.helper.enableDebugLogging(v);
/*  45:    */   }
    /*  46:    */
/*  47:    */   public boolean getSubscriptionsSupported()
/*  48:    */   {
/*  49: 63 */     return this.helper.subscriptionsSupported();
/*  50:    */   }
    /*  51:    */
    public String getAbout()
    {
        return "Rebuild For Avval Market in app billing by ( ALI ZOLFAGHAR )";
    }

    /*  52:    */   public void GetOwnedProducts(final BA ba)
/*  53:    */   {
/*  54: 70 */     IabHelper.QueryInventoryFinishedListener lis = new IabHelper.QueryInventoryFinishedListener()
/*  55:    */     {
            /*  56:    */       public void onQueryInventoryFinished(IabResult result, Inventory inv)
/*  57:    */       {
/*  58: 74 */         Map map = new Map();map.Initialize();
/*  59: 75 */         if ((inv != null) && (inv.mPurchaseMap != null)) {
/*  60: 76 */           ((Map.MyMap)map.getObject()).putAll(inv.mPurchaseMap);
/*  61:    */         }
/*  62: 78 */         ba.raiseEvent(BillingManager3.this, BillingManager3.this.eventName + "_ownedproducts", new Object[] { Boolean.valueOf(result.isSuccess()), map });
/*  63:    */       }
/*  64: 81 */     };
/*  65: 82 */     this.helper.queryInventoryAsync(lis);
/*  66:    */   }
    /*  67:    */
/*  68:    */   public void RequestPayment(final BA ba, String ProductId, String ProductType, String DeveloperPayload)
/*  69:    */   {
/*  70: 91 */     this.helper.launchPurchaseFlow(((BA)ba.sharedProcessBA.activityBA.get()).activity, ProductId, ProductType, 1,
/*  71: 92 */       new IabHelper.OnIabPurchaseFinishedListener()
/*  72:    */       {
                    /*  73:    */         public void onIabPurchaseFinished(IabResult result, Purchase info)
/*  74:    */         {
/*  75: 97 */           if (result.isFailure()) {
/*  76: 98 */             BA.LogError(result.getMessage());
/*  77:    */           }
/*  78: 99 */           ba.raiseEvent(BillingManager3.this, BillingManager3.this.eventName + "_purchasecompleted", new Object[] { Boolean.valueOf(result.isSuccess()), info });
/*  79:    */         }
/*  80:102 */       }, DeveloperPayload, ba);
/*  81:    */   }
    /*  82:    */
/*  83:    */   public void ConsumeProduct(final BA ba, Purchase Product)
/*  84:    */   {
/*  85:108 */     this.helper.consumeAsync(Product, new IabHelper.OnConsumeFinishedListener()
/*  86:    */     {
            /*  87:    */       public void onConsumeFinished(Purchase purchase, IabResult result)
/*  88:    */       {
/*  89:112 */         if (result.isFailure()) {
/*  90:113 */           BA.LogError(result.getMessage());
/*  91:    */         }
/*  92:114 */         ba.raiseEvent(BillingManager3.this, BillingManager3.this.eventName + "_productconsumed", new Object[] { Boolean.valueOf(result.isSuccess()), purchase });
/*  93:    */       }
/*  94:    */     });
/*  95:    */   }
    /*  96:    */


//
//



/* 193:    */ }


/* Location:           C:\Users\AZR\Downloads\InAppBilling3\InAppBilling3.jar
 * Qualified Name:     anywheresoftware.b4a.inappbilling3.BillingManager3
 * JD-Core Version:    0.7.0.1
 */