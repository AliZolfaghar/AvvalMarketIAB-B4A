/*  1:   */ package anywheresoftware.b4a.objects;
/*  2:   */
/*  3:   */ import anywheresoftware.b4a.BA;
/*  3:   */ //import anywheresoftware.b4a.BA.Hide;
/*  3:   */ import anywheresoftware.b4a.inappbilling3.BillingManager3;
/*  4:   */ //import anywheresoftware.b4a.inappbilling3.BillingManager3.Prchase;
/*  4:   */ import anywheresoftware.b4a.objects.Purchase;
/*  5:   */ import java.util.ArrayList;
/*  6:   */ import java.util.HashMap;
/*  7:   */ import java.util.List;
import java.util.Map;
/*  9:   */
/* 10:   */ @BA.Hide
/* 11:   */ public class Inventory
/* 12:   */ {
    /* 13:32 */   public Map<String, SkuDetails> mSkuMap = new HashMap<String, SkuDetails>();
    /* 14:33 */   public Map<String, Purchase> mPurchaseMap = new HashMap<String, Purchase>();
    /* 15:   */
/* 16:   */   public SkuDetails getSkuDetails(String sku)
/* 17:   */   {
/* 18:39 */     return (SkuDetails)this.mSkuMap.get(sku);
/* 19:   */   }
    /* 20:   */
/* 21:   */   public Purchase getPurchase(String sku)
/* 22:   */   {
/* 23:44 */     return (Purchase) this.mPurchaseMap.get(sku);
/* 24:   */   }
    /* 25:   */
/* 26:   */   public boolean hasPurchase(String sku)
/* 27:   */   {
/* 28:49 */     return this.mPurchaseMap.containsKey(sku);
/* 29:   */   }
    /* 30:   */
/* 31:   */   public boolean hasDetails(String sku)
/* 32:   */   {
/* 33:54 */     return this.mSkuMap.containsKey(sku);
/* 34:   */   }
    /* 35:   */
/* 36:   */   public void erasePurchase(String sku)
/* 37:   */   {
/* 38:66 */     if (this.mPurchaseMap.containsKey(sku)) {
/* 39:66 */       this.mPurchaseMap.remove(sku);
/* 40:   */     }
/* 41:   */   }
    /* 42:   */
/* 43:   */   List<String> getAllOwnedSkus()
/* 44:   */   {
/* 45:71 */     return new ArrayList<String>(this.mPurchaseMap.keySet());
/* 46:   */   }
    /* 47:   */
/* 48:   */   List<String> getAllOwnedSkus(String itemType)
/* 49:   */   {
/* 50:76 */     List<String> result = new ArrayList<String>();
/* 51:77 */     for (Purchase p : this.mPurchaseMap.values()) {
/* 52:78 */       if (p.getItemType().equals(itemType)) {
/* 53:78 */         result.add(p.getProductId());
/* 54:   */       }
/* 55:   */     }
/* 56:80 */     return result;
/* 57:   */   }
    /* 58:   */
/* 59:   */   List<Purchase> getAllPurchases()
/* 60:   */   {
/* 61:85 */     return new ArrayList<Purchase>(this.mPurchaseMap.values());
/* 62:   */   }
    /* 63:   */
/* 64:   */   void addSkuDetails(SkuDetails d)
/* 65:   */   {
/* 66:89 */     this.mSkuMap.put(d.getSku(), d);
/* 67:   */   }
    /* 68:   */
/* 69:   */   void addPurchase(Purchase p)
/* 70:   */   {
/* 71:93 */     this.mPurchaseMap.put(p.getProductId(), p);
/* 72:   */   }
/* 73:   */ }


/* Location:           C:\Users\AZR\Downloads\InAppBilling3\InAppBilling3.jar
 * Qualified Name:     anywheresoftware.b4a.objects.IBnventory
 * JD-Core Version:    0.7.0.1
 */