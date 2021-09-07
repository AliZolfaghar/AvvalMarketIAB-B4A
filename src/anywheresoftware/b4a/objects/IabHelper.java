/*   1:    */ package anywheresoftware.b4a.objects;
/*   2:    */ 
/*   3:    */ import android.app.Activity;
/*   4:    */ import android.app.PendingIntent;
/*   5:    */ import android.content.ComponentName;
/*   6:    */ import android.content.Context;
/*   7:    */ import android.content.Intent;
/*   7:    */ import android.content.IntentSender;
/*   8:    */ //import android.content.IntentSender.SendIntentException;
/*   9:    */ import android.content.ServiceConnection;
/*  10:    */ //import android.content.pm.PackageManager;
/*  11:    */ import android.os.Bundle;
/*  12:    */ import android.os.Handler;
/*  13:    */ import android.os.IBinder;
/*  14:    */ import android.os.RemoteException;
/*  15:    */ import android.text.TextUtils;
/*  16:    */ import anywheresoftware.b4a.BA;
/*  17:    */ //import anywheresoftware.b4a.BA.Hide;
/*  18:    */ //import anywheresoftware.b4a.BA.SharedProcessBA;
/*  19:    */ import anywheresoftware.b4a.IOnActivityResult;
/*  19:    */ import anywheresoftware.b4a.inappbilling3.BillingManager3;
/*  20:    */ //import anywheresoftware.b4a.inappbilling3.BillingManager3.Prchase;
                import  anywheresoftware.b4a.objects.Purchase;

/*  21:    */ import com.android.vending.billing.IInAppBillingService;
/*  22:    */ //import com.android.vending.billing.IInAppBillingService.Stub;
/*  23:    */ import java.lang.reflect.Field;
/*  24:    */ import java.util.ArrayList;
/*  25:    */ import java.util.List;
                import org.json.JSONException;
/*  27:    */ 
/*  28:    */ @BA.Hide
/*  29:    */ public class IabHelper
/*  30:    */ {
    /*  31: 82 */   boolean mDebugLog = false;
    /*  32: 83 */   String mDebugTag = "IabHelper";
    /*  33: 86 */   boolean mSetupDone = false;
    /*  34: 89 */   boolean mSubscriptionsSupported = false;
    /*  35: 93 */   boolean mAsyncInProgress = false;
    /*  36: 97 */   String mAsyncOperation = "";
    /*  37:    */   Context mContext;
    /*  38:    */   IInAppBillingService mService;
    /*  39:    */   ServiceConnection mServiceConn;
    /*  40:    */   int mRequestCode;
    /*  41:    */   String mPurchasingItemType;
    /*  42:113 */   String mSignatureBase64 = null;
    /*  43:    */   public static final int BILLING_RESPONSE_RESULT_OK = 0;
    /*  44:    */   public static final int BILLING_RESPONSE_RESULT_USER_CANCELED = 1;
    /*  45:    */   public static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
    /*  46:    */   public static final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
    /*  47:    */   public static final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5;
    /*  48:    */   public static final int BILLING_RESPONSE_RESULT_ERROR = 6;
    /*  49:    */   public static final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
    /*  50:    */   public static final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8;
    /*  51:    */   public static final int IABHELPER_ERROR_BASE = -1000;
    /*  52:    */   public static final int IABHELPER_REMOTE_EXCEPTION = -1001;
    /*  53:    */   public static final int IABHELPER_BAD_RESPONSE = -1002;
    /*  54:    */   public static final int IABHELPER_VERIFICATION_FAILED = -1003;
    /*  55:    */   public static final int IABHELPER_SEND_INTENT_FAILED = -1004;
    /*  56:    */   public static final int IABHELPER_USER_CANCELLED = -1005;
    /*  57:    */   public static final int IABHELPER_UNKNOWN_PURCHASE_RESPONSE = -1006;
    /*  58:    */   public static final int IABHELPER_MISSING_TOKEN = -1007;
    /*  59:    */   public static final int IABHELPER_UNKNOWN_ERROR = -1008;
    /*  60:    */   public static final int IABHELPER_SUBSCRIPTIONS_NOT_AVAILABLE = -1009;
    /*  61:    */   public static final int IABHELPER_INVALID_CONSUMPTION = -1010;
    /*  62:    */   public static final String RESPONSE_CODE = "RESPONSE_CODE";
    /*  63:    */   public static final String RESPONSE_GET_SKU_DETAILS_LIST = "DETAILS_LIST";
    /*  64:    */   public static final String RESPONSE_BUY_INTENT = "BUY_INTENT";
    /*  65:    */   public static final String RESPONSE_INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
    /*  66:    */   public static final String RESPONSE_INAPP_SIGNATURE = "INAPP_DATA_SIGNATURE";
    //                                                                     INAPP_DATA_SIGNATURE
    /*  67:    */   public static final String RESPONSE_INAPP_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
    /*  68:    */   public static final String RESPONSE_INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
    /*  69:    */   public static final String RESPONSE_INAPP_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";
    /*  70:    */   public static final String INAPP_CONTINUATION_TOKEN = "INAPP_CONTINUATION_TOKEN";
    /*  71:    */   public static final String ITEM_TYPE_INAPP = "inapp";
    /*  72:    */   public static final String ITEM_TYPE_SUBS = "subs";
    /*  73:    */   public static final String GET_SKU_DETAILS_ITEM_LIST = "ITEM_ID_LIST";
    /*  74:    */   public static final String GET_SKU_DETAILS_ITEM_TYPE_LIST = "ITEM_TYPE_LIST";
    /*  75:    */   private IOnActivityResult ion;
    /*  76:    */   OnIabPurchaseFinishedListener mPurchaseListener;
    /*  77:    */
/*  78:    */   public IabHelper(Context ctx, String base64PublicKey)
/*  79:    */   {
/*  80:170 */     this.mContext = ctx.getApplicationContext();
/*  81:171 */     this.mSignatureBase64 = base64PublicKey;
/*  82:172 */     logDebug("IAB helper created.");
/*  83:    */   }
    /*  84:    */
/*  85:    */   public void enableDebugLogging(boolean enable, String tag)
/*  86:    */   {
/*  87:179 */     this.mDebugLog = enable;
/*  88:180 */     this.mDebugTag = tag;
/*  89:    */   }
    /*  90:    */
/*  91:    */   public void enableDebugLogging(boolean enable)
/*  92:    */   {
/*  93:184 */     this.mDebugLog = enable;
/*  94:    */   }
    /*  95:    */
/*  96:    */   public void startSetup(final OnIabSetupFinishedListener listener)
/*  97:    */   {
/*  98:209 */     if (this.mSetupDone) {
/*  99:209 */       throw new IllegalStateException("IAB helper is already set up.");
/* 100:    */     }
/* 101:212 */     logDebug("Starting in-app billing setup.");
/* 102:213 */     this.mServiceConn = new ServiceConnection()
/* 103:    */     {
            /* 104:    */       public void onServiceDisconnected(ComponentName name)
/* 105:    */       {
/* 106:216 */         IabHelper.this.logDebug("Billing service disconnected.");
/* 107:217 */         IabHelper.this.mService = null;
/* 108:    */       }
            /* 109:    */
/* 110:    */       public void onServiceConnected(ComponentName name, IBinder service)
/* 111:    */       {
/* 112:222 */         IabHelper.this.logDebug("Billing service connected.");
/* 113:223 */         IabHelper.this.mService = IInAppBillingService.Stub.asInterface(service);
/* 114:224 */         String packageName = IabHelper.this.mContext.getPackageName();
/* 115:    */         try
/* 116:    */         {
/* 117:226 */           IabHelper.this.logDebug("Checking for in-app billing 3 support.");
/* 118:    */           
/* 119:    */ 
/* 120:229 */           int response = IabHelper.this.mService.isBillingSupported(3, packageName, "inapp");
/* 121:230 */           if (response != 0)
/* 122:    */           {
/* 123:231 */             if (listener != null) {
/* 124:232 */               listener.onIabSetupFinished(new IabResult(response, "Error checking for billing v3 support."));
/* 125:    */             }
/* 126:235 */             IabHelper.this.mSubscriptionsSupported = false;
/* 127:236 */             return;
/* 128:    */           }
/* 129:238 */           IabHelper.this.logDebug("In-app billing version 3 supported for " + packageName);
/* 130:    */           
/* 131:    */ 
/* 132:241 */           response = IabHelper.this.mService.isBillingSupported(3, packageName, "subs");
/* 133:242 */           if (response == 0)
/* 134:    */           {
/* 135:243 */             IabHelper.this.logDebug("Subscriptions AVAILABLE.");
/* 136:244 */             IabHelper.this.mSubscriptionsSupported = true;
/* 137:    */           }
/* 138:    */           else
/* 139:    */           {
/* 140:247 */             IabHelper.this.logDebug("Subscriptions NOT AVAILABLE. Response: " + response);
/* 141:    */           }
/* 142:250 */           IabHelper.this.mSetupDone = true;
/* 143:    */         }
/* 144:    */         catch (RemoteException e)
/* 145:    */         {
/* 146:253 */           if (listener != null) {
/* 147:254 */             listener.onIabSetupFinished(new IabResult(-1001, 
/* 148:255 */               "RemoteException while setting up in-app billing."));
/* 149:    */           }
/* 150:257 */           e.printStackTrace();
/* 151:258 */           return;
/* 152:    */         }
/* 153:261 */         if (listener != null) {
/* 154:262 */           listener.onIabSetupFinished(new IabResult(0, "Setup successful."));
/* 155:    */         }
/* 156:    */       }
/* 157:266 */     };



        Intent serviceIntent = new Intent("com.hrm.android.market.billing.InAppBillingService.BIND");
/* 158:267 */     //Intent serviceIntent = new Intent("ir.cafebazaar.pardakht.InAppBillingService.BIND");
        serviceIntent.setPackage("com.hrm.android.market");
/* 159:268 */     //serviceIntent.setPackage("com.farsitel.bazaar");
        //serviceIntent.setPackage("com.android.vending");
/* 160:269 */     if (!this.mContext.getPackageManager().queryIntentServices(serviceIntent, 0).isEmpty()) {
/* 161:271 */       this.mContext.bindService(serviceIntent, this.mServiceConn, 1);
/* 162:275 */     } else if (listener != null) {
/* 163:276 */       listener.onIabSetupFinished(
/* 164:277 */         new IabResult(3, 
/* 165:278 */         "Billing service unavailable on device."));
/* 166:    */     }
/* 167:    */   }
    /* 168:    */
/* 169:    */   public void dispose()
/* 170:    */   {
/* 171:290 */     logDebug("Disposing.");
/* 172:291 */     this.mSetupDone = false;
/* 173:292 */     if (this.mServiceConn != null)
/* 174:    */     {
/* 175:293 */       logDebug("Unbinding from service.");
/* 176:294 */       if (this.mContext != null) {
/* 177:294 */         this.mContext.unbindService(this.mServiceConn);
/* 178:    */       }
/* 179:295 */       this.mServiceConn = null;
/* 180:296 */       this.mService = null;
/* 181:297 */       this.mPurchaseListener = null;
/* 182:    */     }
/* 183:    */   }
    /* 184:    */
/* 185:    */   public boolean subscriptionsSupported()
/* 186:    */   {
/* 187:303 */     return this.mSubscriptionsSupported;
/* 188:    */   }
    /* 189:    */
/* 190:    */   public void launchPurchaseFlow(Activity act, String sku, String itemType, int requestCode, OnIabPurchaseFinishedListener listener, String extraData, BA ba)
/* 191:    */   {
/* 192:349 */     checkSetupDone("launchPurchaseFlow");
/* 193:350 */     flagStartAsync("launchPurchaseFlow");
/* 194:353 */     if ((itemType.equals("subs")) && (!this.mSubscriptionsSupported))
/* 195:    */     {
/* 196:354 */       IabResult r = new IabResult(-1009, 
/* 197:355 */         "Subscriptions are not available.");
/* 198:356 */       if (listener != null) {
/* 199:356 */         listener.onIabPurchaseFinished(r, null);
/* 200:    */       }
/* 201:357 */       flagEndAsync();
/* 202:358 */       return;
/* 203:    */     }
/* 204:    */     try
/* 205:    */     {
/* 206:362 */       logDebug("Constructing buy intent for " + sku + ", item type: " + itemType);
/* 207:363 */       Bundle buyIntentBundle = this.mService.getBuyIntent(3, this.mContext.getPackageName(), sku, itemType, extraData);
/* 208:364 */       int response = getResponseCodeFromBundle(buyIntentBundle);
/* 209:365 */       if (response != 0)
/* 210:    */       {
/* 211:366 */         logError("Unable to buy item, Error response: " + getResponseDesc(response));
/* 212:    */         
/* 213:368 */         IabResult result = new IabResult(response, "Unable to buy item");
/* 214:369 */         if (listener != null) {
/* 215:369 */           listener.onIabPurchaseFinished(result, null);
/* 216:    */         }
/* 217:370 */         flagEndAsync();
/* 218:371 */         return;
/* 219:    */       }
/* 220:374 */       PendingIntent pendingIntent = (PendingIntent)buyIntentBundle.getParcelable("BUY_INTENT");
/* 221:375 */       this.mRequestCode = requestCode;
/* 222:376 */       this.mPurchaseListener = listener;
/* 223:377 */       this.mPurchasingItemType = itemType;
/* 224:378 */       this.ion = new IOnActivityResult()
/* 225:    */       {
                /* 226:    */         public void ResultArrived(int resultCode, Intent intent)
/* 227:    */         {
/* 228:382 */           IabHelper.this.handleActivityResult(IabHelper.this.mRequestCode, resultCode, intent);
/* 229:    */         }
/* 230:    */       };
/* 231:    */       try
/* 232:    */       {
/* 233:388 */         ba.startActivityForResult(this.ion, null);
/* 234:    */       }
/* 235:    */       catch (NullPointerException localNullPointerException) {}
/* 236:392 */       BA.SharedProcessBA sba = ba.sharedProcessBA;
/* 237:    */       try
/* 238:    */       {
/* 239:394 */         Field f = BA.SharedProcessBA.class.getDeclaredField("onActivityResultCode");
/* 240:395 */         f.setAccessible(true);
/* 241:396 */         this.mRequestCode = (f.getInt(sba) - 1);
/* 242:397 */         BA.Log("requestCode = " + this.mRequestCode);
/* 243:    */       }
/* 244:    */       catch (Exception e)
/* 245:    */       {
/* 246:399 */         throw new RuntimeException(e);
/* 247:    */       }
/* 248:401 */       act.startIntentSenderForResult(pendingIntent.getIntentSender(), 
/* 249:402 */         this.mRequestCode, new Intent(), 
/* 250:403 */         Integer.valueOf(0).intValue(), Integer.valueOf(0).intValue(), 
/* 251:404 */         Integer.valueOf(0).intValue());
/* 252:    */     }
/* 253:    */     catch (IntentSender.SendIntentException e)
/* 254:    */     {
/* 255:408 */       logError("SendIntentException while launching purchase flow for sku " + sku);
/* 256:409 */       e.printStackTrace();
/* 257:    */       
/* 258:411 */       IabResult result = new IabResult(-1004, "Failed to send intent.");
/* 259:412 */       if (listener != null) {
/* 260:412 */         listener.onIabPurchaseFinished(result, null);
/* 261:    */       }
/* 262:413 */       flagEndAsync();
/* 263:    */     }
/* 264:    */     catch (RemoteException e)
/* 265:    */     {
/* 266:416 */       logError("RemoteException while launching purchase flow for sku " + sku);
/* 267:417 */       e.printStackTrace();
/* 268:    */       
/* 269:419 */       IabResult result = new IabResult(-1001, "Remote exception while starting purchase flow");
/* 270:420 */       if (listener != null) {
/* 271:420 */         listener.onIabPurchaseFinished(result, null);
/* 272:    */       }
/* 273:421 */       flagEndAsync();
/* 274:    */     }
/* 275:    */   }
    /* 276:    */
/* 277:    */   public boolean handleActivityResult(int requestCode, int resultCode, Intent data)
/* 278:    */   {
/* 279:440 */     BA.Log("Arrived: " + requestCode + ", " + this.mRequestCode);
/* 280:441 */     if (requestCode != this.mRequestCode) {
/* 281:441 */       return false;
/* 282:    */     }
/* 283:443 */     checkSetupDone("handleActivityResult");
/* 284:    */     
/* 285:    */ 
/* 286:446 */     flagEndAsync();
/* 287:448 */     if (data == null)
/* 288:    */     {
/* 289:449 */       logError("Null data in IAB activity result.");
/* 290:450 */       IabResult result = new IabResult(-1002, "Null data in IAB result");
/* 291:451 */       if (this.mPurchaseListener != null) {
/* 292:451 */         this.mPurchaseListener.onIabPurchaseFinished(result, null);
/* 293:    */       }
/* 294:452 */       return true;
/* 295:    */     }
/* 296:455 */     int responseCode = getResponseCodeFromIntent(data);
/* 297:456 */     String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
/* 298:457 */     String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
                    BA.Log("azr - place 0 : intent_Data : " + data.toString());
                    BA.Log("azr - place 0 : intent_Data : " + data.getDataString());

/* 299:459 */     if ((resultCode == -1) && (responseCode == 0))
/* 300:    */     {
/* 301:460 */       logDebug("Successful resultcode from purchase activity.");
/* 302:461 */       logDebug("Purchase data: " + purchaseData);
/* 303:462 */       logDebug("Data signature: " + dataSignature);
/* 304:463 */       logDebug("Extras: " + data.getExtras());
/* 305:464 */       logDebug("Expected item type: " + this.mPurchasingItemType);
/* 306:466 */       if ((purchaseData == null) || (dataSignature == null))
/* 307:    */       {
/* 308:467 */         logError("BUG: either purchaseData or dataSignature is null.");
/* 309:468 */         logDebug("Extras: " + data.getExtras().toString());
/* 310:469 */         IabResult result = new IabResult(-1008, "IAB returned null purchaseData or dataSignature");
/* 311:470 */         if (this.mPurchaseListener != null) {
/* 312:470 */           this.mPurchaseListener.onIabPurchaseFinished(result, null);
/* 313:    */         }
/* 314:471 */         return true;
/* 315:    */       }
/* 316:474 */       Purchase purchase = null;
/* 317:    */       try
/* 318:    */       {
/* 319:476 */         purchase = new Purchase(this.mPurchasingItemType, purchaseData, dataSignature);
/* 320:477 */         String sku = purchase.getProductId();
                        BA.Log("azr - place 1 : purchaseData : " + purchaseData);
                        BA.Log("azr - place 1 : dataSignature : " + dataSignature);
/* 321:480 */         if (!Security.verifyPurchase(this.mSignatureBase64, purchaseData, dataSignature))
/* 322:    */         {
/* 323:481 */           logError("Purchase signature verification FAILED for sku " + sku);
/* 324:482 */           IabResult result = new IabResult(-1003, "Signature verification failed for Product ID " + sku);
/* 325:483 */           if (this.mPurchaseListener != null) {
/* 326:483 */             this.mPurchaseListener.onIabPurchaseFinished(result, purchase);
/* 327:    */           }
/* 328:484 */           return true;
/* 329:    */         }
/* 330:486 */         logDebug("Purchase signature successfully verified.");
/* 331:    */       }
/* 332:    */       catch (JSONException e)
/* 333:    */       {
/* 334:489 */         logError("Failed to parse purchase data.");
/* 335:490 */         e.printStackTrace();
/* 336:491 */         IabResult result = new IabResult(-1002, "Failed to parse purchase data.");
/* 337:492 */         if (this.mPurchaseListener != null) {
/* 338:492 */           this.mPurchaseListener.onIabPurchaseFinished(result, null);
/* 339:    */         }
/* 340:493 */         return true;
/* 341:    */       }
/* 342:496 */       if (this.mPurchaseListener != null) {
/* 343:497 */         this.mPurchaseListener.onIabPurchaseFinished(new IabResult(0, "Success"), purchase);
/* 344:    */       }
/* 345:    */     }
/* 346:500 */     else if (resultCode == -1)
/* 347:    */     {
/* 348:502 */       logDebug("Result code was OK but in-app billing response was not OK: " + getResponseDesc(responseCode));
/* 349:503 */       if (this.mPurchaseListener != null)
/* 350:    */       {
/* 351:504 */         IabResult result = new IabResult(responseCode, "Problem purchashing item.");
/* 352:505 */         this.mPurchaseListener.onIabPurchaseFinished(result, null);
/* 353:    */       }
/* 354:    */     }
/* 355:508 */     else if (resultCode == 0)
/* 356:    */     {
/* 357:509 */       logDebug("Purchase canceled - Response: " + getResponseDesc(responseCode));
/* 358:510 */       IabResult result = new IabResult(-1005, "User canceled.");
/* 359:511 */       if (this.mPurchaseListener != null) {
/* 360:511 */         this.mPurchaseListener.onIabPurchaseFinished(result, null);
/* 361:    */       }
/* 362:    */     }
/* 363:    */     else
/* 364:    */     {
/* 365:514 */       logError("Purchase failed. Result code: " + Integer.toString(resultCode) + 
/* 366:515 */         ". Response: " + getResponseDesc(responseCode));
/* 367:516 */       IabResult result = new IabResult(-1006, "Unknown purchase response.");
/* 368:517 */       if (this.mPurchaseListener != null) {
/* 369:517 */         this.mPurchaseListener.onIabPurchaseFinished(result, null);
/* 370:    */       }
/* 371:    */     }
/* 372:519 */     return true;
/* 373:    */   }
    /* 374:    */
/* 375:    */   public Inventory queryInventory(boolean querySkuDetails, List<String> moreSkus)
/* 376:    */     throws IabException
/* 377:    */   {
/* 378:523 */     return queryInventory(querySkuDetails, moreSkus, null);
/* 379:    */   }
    /* 380:    */
/* 381:    */   public Inventory queryInventory(boolean querySkuDetails, List<String> moreItemSkus, List<String> moreSubsSkus)
/* 382:    */     throws IabException
/* 383:    */   {
/* 384:541 */     checkSetupDone("queryInventory");
/* 385:    */     try
/* 386:    */     {
/* 387:543 */       Inventory inv = new Inventory();
/* 388:544 */       int r = queryPurchases(inv, "inapp");
/* 389:545 */       if (r != 0) {
/* 390:546 */         throw new IabException(r, "Error refreshing inventory (querying owned items).");
/* 391:    */       }
/* 392:549 */       if (querySkuDetails)
/* 393:    */       {
/* 394:550 */         r = querySkuDetails("inapp", inv, moreItemSkus);
/* 395:551 */         if (r != 0) {
/* 396:552 */           throw new IabException(r, "Error refreshing inventory (querying prices of items).");
/* 397:    */         }
/* 398:    */       }
/* 399:557 */       if (this.mSubscriptionsSupported)
/* 400:    */       {
/* 401:558 */         r = queryPurchases(inv, "subs");
/* 402:559 */         if (r != 0) {
/* 403:560 */           throw new IabException(r, "Error refreshing inventory (querying owned subscriptions).");
/* 404:    */         }
/* 405:563 */         if (querySkuDetails)
/* 406:    */         {
/* 407:564 */           r = querySkuDetails("subs", inv, moreItemSkus);
/* 408:565 */           if (r != 0) {
/* 409:566 */             throw new IabException(r, "Error refreshing inventory (querying prices of subscriptions).");
/* 410:    */           }
/* 411:    */         }
/* 412:    */       }
/* 413:571 */       return inv;
/* 414:    */     }
/* 415:    */     catch (RemoteException e)
/* 416:    */     {
/* 417:574 */       throw new IabException(-1001, "Remote exception while refreshing inventory.", e);
/* 418:    */     }
/* 419:    */     catch (JSONException e)
/* 420:    */     {
/* 421:577 */       throw new IabException(-1002, "Error parsing JSON response while refreshing inventory.", e);
/* 422:    */     }
/* 423:    */   }
    /* 424:    */
/* 425:    */   public void queryInventoryAsync(final boolean querySkuDetails, final List<String> moreSkus, final QueryInventoryFinishedListener listener)
/* 426:    */   {
/* 427:608 */     final Handler handler = new Handler();
/* 428:609 */     checkSetupDone("queryInventory");
/* 429:610 */     flagStartAsync("refresh inventory");
/* 430:611 */     new Thread(new Runnable()
/* 431:    */     {
            /* 432:    */       public void run()
/* 433:    */       {
/* 434:613 */         IabResult result = new IabResult(0, "Inventory refresh successful.");
/* 435:614 */         Inventory inv = null;
/* 436:    */         try
/* 437:    */         {
/* 438:616 */           inv = IabHelper.this.queryInventory(querySkuDetails, moreSkus);
/* 439:    */         }
/* 440:    */         catch (IabException ex)
/* 441:    */         {
/* 442:619 */           result = ex.getResult();
/* 443:    */         }
/* 444:622 */         IabHelper.this.flagEndAsync();
/* 445:    */         
/* 446:624 */         final IabResult result_f = result;
/* 447:625 */         final Inventory inv_f = inv;
/* 448:626 */         handler.post(new Runnable()
/* 449:    */         {
                    /* 450:    */           public void run()
/* 451:    */           {
/* 452:628 */             listener.onQueryInventoryFinished(result_f, inv_f);
/* 453:    */           }
/* 454:    */         });
/* 455:    */       }
/* 456:    */     })
/* 457:    */     
/* 458:    */ 
/* 459:    */ 
/* 460:    */ 
/* 461:    */ 
/* 462:    */ 
/* 463:    */ 
/* 464:    */ 
/* 465:    */ 
/* 466:    */ 
/* 467:    */ 
/* 468:    */ 
/* 469:    */ 
/* 470:    */ 
/* 471:    */ 
/* 472:    */ 
/* 473:    */ 
/* 474:    */ 
/* 475:    */ 
/* 476:    */ 
/* 477:632 */       .start();
/* 478:    */   }
    /* 479:    */
/* 480:    */   public void queryInventoryAsync(QueryInventoryFinishedListener listener)
/* 481:    */   {
/* 482:636 */     queryInventoryAsync(true, null, listener);
/* 483:    */   }
    /* 484:    */
/* 485:    */   public void queryInventoryAsync(boolean querySkuDetails, QueryInventoryFinishedListener listener)
/* 486:    */   {
/* 487:640 */     queryInventoryAsync(querySkuDetails, null, listener);
/* 488:    */   }
    /* 489:    */
/* 490:    */   void consume(Purchase itemInfo)
/* 491:    */     throws IabException
/* 492:    */   {
/* 493:654 */     checkSetupDone("consume");
/* 494:656 */     if (!itemInfo.mItemType.equals("inapp")) {
/* 495:657 */       throw new IabException(-1010, 
/* 496:658 */         "Items of type '" + itemInfo.mItemType + "' can't be consumed.");
/* 497:    */     }
/* 498:    */     try
/* 499:    */     {
/* 500:662 */       String token = itemInfo.getToken();
/* 501:663 */       String sku = itemInfo.getProductId();
/* 502:664 */       if ((token == null) || (token.equals("")))
/* 503:    */       {
/* 504:665 */         logError("Can't consume " + sku + ". No token.");
/* 505:666 */         throw new IabException(-1007, "PurchaseInfo is missing token for Product ID: " + 
/* 506:667 */           sku + " " + itemInfo);
/* 507:    */       }
/* 508:670 */       logDebug("Consuming sku: " + sku + ", token: " + token);
/* 509:671 */       int response = this.mService.consumePurchase(3, this.mContext.getPackageName(), token);
/* 510:672 */       if (response == 0)
/* 511:    */       {
/* 512:673 */         logDebug("Successfully consumed sku: " + sku);
/* 513:    */       }
/* 514:    */       else
/* 515:    */       {
/* 516:676 */         logDebug("Error consuming consuming sku " + sku + ". " + getResponseDesc(response));
/* 517:677 */         throw new IabException(response, "Error consuming sku " + sku);
/* 518:    */       }
/* 519:    */     }
/* 520:    */     catch (RemoteException e)
/* 521:    */     {
/* 522:681 */       throw new IabException(-1001, "Remote exception while consuming. PurchaseInfo: " + itemInfo, e);
/* 523:    */     }
/* 524:    */   }
    /* 525:    */
/* 526:    */   public void consumeAsync(Purchase purchase, OnConsumeFinishedListener listener)
/* 527:    */   {
/* 528:721 */     checkSetupDone("consume");
/* 529:722 */     List<Purchase> purchases = new ArrayList<Purchase>();
/* 530:723 */     purchases.add(purchase);
/* 531:724 */     consumeAsyncInternal(purchases, listener, null);
/* 532:    */   }
    /* 533:    */
/* 534:    */   public void consumeAsync(List<Purchase> purchases, OnConsumeMultiFinishedListener listener)
/* 535:    */   {
/* 536:733 */     checkSetupDone("consume");
/* 537:734 */     consumeAsyncInternal(purchases, null, listener);
/* 538:    */   }
    /* 539:    */
/* 540:    */   public static String getResponseDesc(int code)
/* 541:    */   {
/* 542:745 */     String[] iab_msgs = "0:OK/1:User Canceled/2:Unknown/3:Billing Unavailable/4:Item unavailable/5:Developer Error/6:Error/7:Item Already Owned/8:Item not owned"
/* 543:    */     
/* 544:    */ 
/* 545:748 */       .split("/");
/* 546:749 */     String[] iabhelper_msgs = "0:OK/-1001:Remote exception during initialization/-1002:Bad response received/-1003:Purchase signature verification failed/-1004:Send intent failed/-1005:User cancelled/-1006:Unknown purchase response/-1007:Missing token/-1008:Unknown error/-1009:Subscriptions not available/-1010:Invalid consumption attempt"
/* 547:    */     
/* 548:    */ 
/* 549:    */ 
/* 550:    */ 
/* 551:    */ 
/* 552:    */ 
/* 553:    */ 
/* 554:    */ 
/* 555:758 */       .split("/");
/* 556:760 */     if (code <= -1000)
/* 557:    */     {
/* 558:761 */       int index = -1000 - code;
/* 559:762 */       if ((index >= 0) && (index < iabhelper_msgs.length)) {
/* 560:762 */         return iabhelper_msgs[index];
/* 561:    */       }
/* 562:763 */       return String.valueOf(code) + ":Unknown IAB Helper Error";
/* 563:    */     }
/* 564:765 */     if ((code < 0) || (code >= iab_msgs.length)) {
/* 565:766 */       return String.valueOf(code) + ":Unknown";
/* 566:    */     }
/* 567:768 */     return iab_msgs[code];
/* 568:    */   }
    /* 569:    */
/* 570:    */   void checkSetupDone(String operation)
/* 571:    */   {
/* 572:774 */     if (!this.mSetupDone)
/* 573:    */     {
/* 574:775 */       logError("Illegal state for operation (" + operation + "): IAB helper is not set up.");
/* 575:776 */       throw new IllegalStateException("IAB helper is not set up. Can't perform operation: " + operation);
/* 576:    */     }
/* 577:    */   }
    /* 578:    */
/* 579:    */   int getResponseCodeFromBundle(Bundle b)
/* 580:    */   {
/* 581:782 */     Object o = b.get("RESPONSE_CODE");
/* 582:783 */     if (o == null)
/* 583:    */     {
/* 584:784 */       logDebug("Bundle with null response code, assuming OK (known issue)");
/* 585:785 */       return 0;
/* 586:    */     }
/* 587:787 */     if ((o instanceof Integer)) {
/* 588:787 */       return ((Integer)o).intValue();
/* 589:    */     }
/* 590:788 */     if ((o instanceof Long)) {
/* 591:788 */       return (int)((Long)o).longValue();
/* 592:    */     }
/* 593:790 */     logError("Unexpected type for bundle response code.");
/* 594:791 */     logError(o.getClass().getName());
/* 595:792 */     throw new RuntimeException("Unexpected type for bundle response code: " + o.getClass().getName());
/* 596:    */   }
    /* 597:    */
/* 598:    */   int getResponseCodeFromIntent(Intent i)
/* 599:    */   {
/* 600:798 */     Object o = i.getExtras().get("RESPONSE_CODE");
/* 601:799 */     if (o == null)
/* 602:    */     {
/* 603:800 */       logError("Intent with no response code, assuming OK (known issue)");
/* 604:801 */       return 0;
/* 605:    */     }
/* 606:803 */     if ((o instanceof Integer)) {
/* 607:803 */       return ((Integer)o).intValue();
/* 608:    */     }
/* 609:804 */     if ((o instanceof Long)) {
/* 610:804 */       return (int)((Long)o).longValue();
/* 611:    */     }
/* 612:806 */     logError("Unexpected type for intent response code.");
/* 613:807 */     logError(o.getClass().getName());
/* 614:808 */     throw new RuntimeException("Unexpected type for intent response code: " + o.getClass().getName());
/* 615:    */   }
    /* 616:    */
/* 617:    */   void flagStartAsync(String operation)
/* 618:    */   {
/* 619:813 */     if (this.mAsyncInProgress) {
/* 620:814 */       throw new IllegalStateException("Can't start async operation (" + operation + ") because another async operation(" + this.mAsyncOperation + ") is in progress.");
/* 621:    */     }
/* 622:815 */     this.mAsyncOperation = operation;
/* 623:816 */     this.mAsyncInProgress = true;
/* 624:817 */     logDebug("Starting async operation: " + operation);
/* 625:    */   }
    /* 626:    */
/* 627:    */   void flagEndAsync()
/* 628:    */   {
/* 629:821 */     logDebug("Ending async operation: " + this.mAsyncOperation);
/* 630:822 */     this.mAsyncOperation = "";
/* 631:823 */     this.mAsyncInProgress = false;
/* 632:    */   }
    /* 633:    */
/* 634:    */   int queryPurchases(Inventory inv, String itemType)
/* 635:    */     throws JSONException, RemoteException, IabException
/* 636:    */   {
/* 637:829 */     logDebug("Querying owned items, item type: " + itemType);
/* 638:830 */     logDebug("Package name: " + this.mContext.getPackageName());
/* 639:831 */     boolean verificationFailed = false;
/* 640:832 */     String continueToken = null;
/* 641:    */     do
/* 642:    */     {
/* 643:835 */       logDebug("Calling getPurchases with continuation token: " + continueToken);
/* 644:836 */       Bundle ownedItems = this.mService.getPurchases(3, this.mContext.getPackageName(), 
/* 645:837 */         itemType, continueToken);
/* 646:    */       
/* 647:839 */       int response = getResponseCodeFromBundle(ownedItems);
/* 648:840 */       logDebug("Owned items response: " + String.valueOf(response));
/* 649:841 */       if (response != 0)
/* 650:    */       {
/* 651:842 */         logDebug("getPurchases() failed: " + getResponseDesc(response));
/* 652:843 */         return response;
/* 653:    */       }
/* 654:845 */       if ((!ownedItems.containsKey("INAPP_PURCHASE_ITEM_LIST")) || 
/* 655:846 */         (!ownedItems.containsKey("INAPP_PURCHASE_DATA_LIST")) || 
/* 656:847 */         (!ownedItems.containsKey("INAPP_DATA_SIGNATURE_LIST")))
/* 657:    */       {
/* 658:848 */         logError("Bundle returned from getPurchases() doesn't contain required fields.");
/* 659:849 */         return -1002;
/* 660:    */       }
/* 661:852 */       ArrayList<String> ownedSkus = ownedItems.getStringArrayList(
/* 662:853 */         "INAPP_PURCHASE_ITEM_LIST");
/* 663:854 */       ArrayList<String> purchaseDataList = ownedItems.getStringArrayList(
/* 664:855 */         "INAPP_PURCHASE_DATA_LIST");
/* 665:856 */       ArrayList<String> signatureList = ownedItems.getStringArrayList(
/* 666:857 */         "INAPP_DATA_SIGNATURE_LIST");
/* 667:859 */       for (int i = 0; i < purchaseDataList.size(); i++)
/* 668:    */       {
/* 669:860 */         String purchaseData = (String)purchaseDataList.get(i);
/* 670:861 */         String signature = (String)signatureList.get(i);
/* 671:862 */         String sku = (String)ownedSkus.get(i);
                        BA.Log("azr - place 2  : purchaseData : "  + purchaseData);
                        BA.Log("azr - place 2  : signature : "  + signature);

/* 672:863 */         if (Security.verifyPurchase(this.mSignatureBase64, purchaseData, signature))
/* 673:    */         {
/* 674:864 */           logDebug("Sku is owned: " + sku);
/* 675:865 */           Purchase purchase = new Purchase(itemType, purchaseData, signature);
/* 676:867 */           if (TextUtils.isEmpty(purchase.getToken()))
/* 677:    */           {
/* 678:868 */             logWarn("BUG: empty/null token!");
/* 679:869 */             logDebug("Purchase data: " + purchaseData);
/* 680:    */           }
/* 681:873 */           inv.addPurchase(purchase);
/* 682:    */         }
/* 683:    */         else
/* 684:    */         {
/* 685:876 */           logWarn("Purchase signature verification **FAILED**. Not adding item.");
/* 686:877 */           logDebug("   Purchase data: " + purchaseData);
/* 687:878 */           logDebug("   Signature: " + signature);

                    BA.Log("azr - place 3  : purchaseData : "  + purchaseData);
                    BA.Log("azr - place 3  : signature : "  + signature);

/* 688:879 */           if (itemType.equals("inapp"))
/* 689:    */           {
/* 690:880 */             Purchase purchase = new Purchase(itemType, purchaseData, signature);
/* 691:881 */             consume(purchase);
/* 692:    */           }
/* 693:883 */           verificationFailed = true;
/* 694:    */         }
/* 695:    */       }
/* 696:887 */       continueToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");
/* 697:888 */       logDebug("Continuation token: " + continueToken);
/* 698:834 */     } while (!
/* 753:889 */       TextUtils.isEmpty(continueToken));
/* 754:891 */     return verificationFailed ? -1003 : 0;
/* 755:    */   }
    /* 756:    */
/* 757:    */   int querySkuDetails(String itemType, Inventory inv, List<String> moreSkus)
/* 758:    */     throws RemoteException, JSONException
/* 759:    */   {
/* 760:896 */     logDebug("Querying SKU details.");
/* 761:897 */     ArrayList<String> skuList = new ArrayList<String>();
/* 762:898 */     skuList.addAll(inv.getAllOwnedSkus(itemType));
/* 763:899 */     if (moreSkus != null) {
/* 764:899 */       skuList.addAll(moreSkus);
/* 765:    */     }
/* 766:901 */     if (skuList.size() == 0)
/* 767:    */     {
/* 768:902 */       logDebug("queryPrices: nothing to do because there are no SKUs.");
/* 769:903 */       return 0;
/* 770:    */     }
/* 771:906 */     Bundle querySkus = new Bundle();
/* 772:907 */     querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
/* 773:908 */     Bundle skuDetails = this.mService.getSkuDetails(3, this.mContext.getPackageName(), 
/* 774:909 */       itemType, querySkus);
/* 775:911 */     if (!skuDetails.containsKey("DETAILS_LIST"))
/* 776:    */     {
/* 777:912 */       int response = getResponseCodeFromBundle(skuDetails);
/* 778:913 */       if (response != 0)
/* 779:    */       {
/* 780:914 */         logDebug("getSkuDetails() failed: " + getResponseDesc(response));
/* 781:915 */         return response;
/* 782:    */       }
/* 783:918 */       logError("getSkuDetails() returned a bundle with neither an error nor a detail list.");
/* 784:919 */       return -1002;
/* 785:    */     }
/* 786:923 */     ArrayList<String> responseList = skuDetails.getStringArrayList(
/* 787:924 */       "DETAILS_LIST");
/* 788:926 */     for (String thisResponse : responseList)
/* 789:    */     {
/* 790:927 */       SkuDetails d = new SkuDetails(itemType, thisResponse);
/* 791:928 */       logDebug("Got sku details: " + d);
/* 792:929 */       inv.addSkuDetails(d);
/* 793:    */     }
/* 794:931 */     return 0;
/* 795:    */   }
    /* 796:    */
/* 797:    */   void consumeAsyncInternal(final List<Purchase> purchases, final OnConsumeFinishedListener singleListener, final OnConsumeMultiFinishedListener multiListener)
/* 798:    */   {
/* 799:938 */     final Handler handler = new Handler();
/* 800:939 */     flagStartAsync("consume");
/* 801:940 */     new Thread(new Runnable()
/* 802:    */     {
            /* 803:    */       public void run()
/* 804:    */       {
/* 805:942 */         final List<IabResult> results = new ArrayList<IabResult>();
/* 806:943 */         for (Purchase purchase : purchases) {
/* 807:    */           try
/* 808:    */           {
/* 809:945 */             IabHelper.this.consume(purchase);
/* 810:946 */             results.add(new IabResult(0, "Successful consume of Product Id " + purchase.getProductId()));
/* 811:    */           }
/* 812:    */           catch (IabException ex)
/* 813:    */           {
/* 814:949 */             results.add(ex.getResult());
/* 815:    */           }
/* 816:    */         }
/* 817:953 */         IabHelper.this.flagEndAsync();
/* 818:954 */         if (singleListener != null) {
/* 819:955 */           handler.post(new Runnable()
/* 820:    */           {
                        /* 821:    */             public void run()
/* 822:    */             {
/* 823:957 */               singleListener.onConsumeFinished((Purchase) purchases.get(0), (IabResult)results.get(0));
/* 824:    */             }
/* 825:    */           });
/* 826:    */         }
/* 827:961 */         if (multiListener != null) {
/* 828:962 */           handler.post(new Runnable()
/* 829:    */           {
                        /* 830:    */             public void run()
/* 831:    */             {
/* 832:964 */               multiListener.onConsumeMultiFinished(purchases, results);
/* 833:    */             }
/* 834:    */           });
/* 835:    */         }
/* 836:    */       }
/* 837:    */     })
/* 838:    */     
/* 839:    */ 
/* 840:    */ 
/* 841:    */ 
/* 842:    */ 
/* 843:    */ 
/* 844:    */ 
/* 845:    */ 
/* 846:    */ 
/* 847:    */ 
/* 848:    */ 
/* 849:    */ 
/* 850:    */ 
/* 851:    */ 
/* 852:    */ 
/* 853:    */ 
/* 854:    */ 
/* 855:    */ 
/* 856:    */ 
/* 857:    */ 
/* 858:    */ 
/* 859:    */ 
/* 860:    */ 
/* 861:    */ 
/* 862:    */ 
/* 863:    */ 
/* 864:    */ 
/* 865:    */ 
/* 866:969 */       .start();
/* 867:    */   }
    /* 868:    */
/* 869:    */   void logDebug(String msg)
/* 870:    */   {
/* 871:973 */     if (this.mDebugLog) {
/* 872:973 */       BA.Log(msg);
/* 873:    */     }
/* 874:    */   }
    /* 875:    */
/* 876:    */   void logError(String msg)
/* 877:    */   {
/* 878:977 */     if (this.mDebugLog) {
/* 879:977 */       BA.LogError(msg);
/* 880:    */     }
/* 881:    */   }
    /* 882:    */
/* 883:    */   void logWarn(String msg)
/* 884:    */   {
/* 885:981 */     if (this.mDebugLog) {
/* 886:981 */       BA.Log(msg);
/* 887:    */     }
/* 888:    */   }
    /* 889:    */
/* 890:    */   public static abstract interface OnConsumeFinishedListener
/* 891:    */   {
        /* 892:    */     public abstract void onConsumeFinished(Purchase paramPrchase, IabResult paramIabResult);
/* 893:    */   }
    /* 894:    */
/* 895:    */   public static abstract interface OnConsumeMultiFinishedListener
/* 896:    */   {
        /* 897:    */     public abstract void onConsumeMultiFinished(List<Purchase> paramList, List<IabResult> paramList1);
/* 898:    */   }
    /* 899:    */
/* 900:    */   public static abstract interface OnIabPurchaseFinishedListener
/* 901:    */   {
        /* 902:    */     public abstract void onIabPurchaseFinished(IabResult paramIabResult, Purchase paramPrchase);
/* 903:    */   }
    /* 904:    */
/* 905:    */   public static abstract interface OnIabSetupFinishedListener
/* 906:    */   {
        /* 907:    */     public abstract void onIabSetupFinished(IabResult paramIabResult);
/* 908:    */   }
    /* 909:    */
/* 910:    */   public static abstract interface QueryInventoryFinishedListener
/* 911:    */   {
        /* 912:    */     public abstract void onQueryInventoryFinished(IabResult paramIabResult, Inventory paramInventory);
/* 913:    */   }
/* 914:    */ }


/* Location:           C:\Users\AZR\Downloads\InAppBilling3\InAppBilling3.jar
 * Qualified Name:     anywheresoftware.b4a.objects.IabHelper
 * JD-Core Version:    0.7.0.1
 */