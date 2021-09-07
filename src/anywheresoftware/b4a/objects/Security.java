/* Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package anywheresoftware.b4a.objects;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
/*   5:    */ import android.widget.Toast;
import anywheresoftware.b4a.BA;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import static java.security.AccessController.getContext;


/**
 * Security-related methods. For a secure implementation, all of this code
 * should be implemented on a server that communicates with the
 * application on the device. For the sake of simplicity and clarity of this
 * example, this code is included here and is executed on the device. If you
 * must verify the purchases on the phone, you should obfuscate this code to
 * make it harder for an attacker to replace the code with stubs that treat all
 * purchases as verified.
 */
@BA.Hide
public class Security {
    private static final String TAG = "IABUtil/Security";
    private static final String KEY_FACTORY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

    /**
     * Verifies that the data was signed with the given signature, and returns
     * the verified purchase. The data is in JSON format and signed
     * with a private key. The data also contains the (@link PurchaseState )
     * and product ID of the purchase.
     * @param base64PublicKey the base64-encoded public key to use for verifying.
     * @param signedData the signed JSON string (signed, not encrypted)
     * @param signature the signature for the data, signed with the private key
     */
    public static boolean verifyPurchase(String base64PublicKey, String signedData, String signature) {

        BA.Log("AZR : verifyPurchase");
        BA.Log("AZR : base64PublicKey : " + base64PublicKey);
        BA.Log("AZR : signedData : " + signedData);
        BA.Log("AZR : signature : " + signature);

        if (TextUtils.isEmpty(signedData) || TextUtils.isEmpty(base64PublicKey) || TextUtils.isEmpty(signature)) {
            Log.e(TAG, "Purchase verification failed: missing data.");
            return false;
        }

        PublicKey key = Security.generatePublicKey(base64PublicKey);

        // String myData = "\"test\"";
        String correct_signedData = "{\"autoRenewing\":#autoRenewing#,\"developerPayload\":\"#developerPayload#\",\"orderId\":#orderId#,\"packageName\":\"#packageName#\",\"productId\":\"#productId#\",\"purchaseState\":#purchaseState#,\"purchaseTime\":\"#purchaseTime#\",\"token\":\"#token#\"}";

        // get signData as json
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(signedData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // autoRenewing
        try {
            correct_signedData = correct_signedData.replace("#autoRenewing#",jsonObj.getString("autoRenewing"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // developerPayload
        try {
            correct_signedData = correct_signedData.replace("#developerPayload#",jsonObj.getString("developerPayload"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // orderId
        try {
            correct_signedData = correct_signedData.replace("#orderId#",jsonObj.getString("orderId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // packageName
        try {
            correct_signedData = correct_signedData.replace("#packageName#",jsonObj.getString("packageName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // productId
        try {
            correct_signedData = correct_signedData.replace("#productId#",jsonObj.getString("productId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // purchaseState
        try {
            correct_signedData = correct_signedData.replace("#purchaseState#",jsonObj.getString("purchaseState"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // purchaseTime
        try {
            correct_signedData = correct_signedData.replace("#purchaseTime#",jsonObj.getString("purchaseTime"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // token
        try {
            correct_signedData = correct_signedData.replace("#token#",jsonObj.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        BA.Log("-------AZ_JSON_CORRECTION : " + correct_signedData);

        // use correct_signData instead of original signData
        //return Security.verify(key, signedData, signature);
        return Security.verify(key, correct_signedData, signature);
    }

    /**
     * Generates a PublicKey instance from a string containing the
     * Base64-encoded public key.
     *
     * @param encodedPublicKey Base64-encoded public key
     * @throws IllegalArgumentException if encodedPublicKey is invalid
     */
    public static PublicKey generatePublicKey(String encodedPublicKey) {
        try {
            byte[] decodedKey = Base64.decode(encodedPublicKey, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
            return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            Log.e(TAG, "Invalid key specification.");
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Verifies that the signature from the server matches the computed
     * signature on the data.  Returns true if the data is correctly signed.
     *
     * @param publicKey public key associated with the developer account
     * @param signedData signed data from server
     * @param signature server signature
     * @return true if the data and signature match
     */
    public static boolean verify(PublicKey publicKey, String signedData, String signature) {
        BA.Log("AZR : verify---");
        BA.Log("AZR : base64PublicKey : " + publicKey);
        BA.Log("AZR : signedData : " + signedData);
        BA.Log("AZR : signature : " + signature);


        byte[] signatureBytes;
        try {
            signatureBytes = Base64.decode(signature, Base64.DEFAULT);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Base64 decoding failed.");
            return false;
        }
        try {
            Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
            sig.initVerify(publicKey);
            sig.update(signedData.getBytes());
            BA.Log("AZ_SIG_Check");
            BA.Log("AZ_SIG_publicKey : "  + publicKey);

            if (!sig.verify(signatureBytes)) {
                Log.e(TAG, "Signature verification failed.");
                BA.Log("AZ_SIG_ERR");
                return false;
            }
            return true;
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "NoSuchAlgorithmException.");
        } catch (InvalidKeyException e) {
            Log.e(TAG, "Invalid key specification.");
        } catch (SignatureException e) {
            Log.e(TAG, "Signature exception.");
        }
        return false;
    }
}
