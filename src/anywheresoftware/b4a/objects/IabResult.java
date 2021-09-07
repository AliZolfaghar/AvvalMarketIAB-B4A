package anywheresoftware.b4a.objects;
import anywheresoftware.b4a.BA;
import  anywheresoftware.b4a.BA.Hide;

@BA.Hide
public class IabResult {
    int mResponse;
    String mMessage;

    public IabResult(int response, String message) {
        this.mResponse = response;
        if (message == null || message.trim().length() == 0) {
            this.mMessage = IabHelper.getResponseDesc(response);
        }
        else {
            this.mMessage = message + " (response: " + IabHelper.getResponseDesc(response) + ")";
        }
    }
    public int getResponse() { return this.mResponse; }
    public String getMessage() { return this.mMessage; }
    public boolean isSuccess() { return this.mResponse == IabHelper.BILLING_RESPONSE_RESULT_OK; }
    public boolean isFailure() { return !isSuccess(); }
    public String toString() { return "IabResult: " + getMessage(); }
}

