package com.eightdigits.sdk;

import java.net.UnknownHostException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONObject;

import com.eightdigits.sdk.exceptions.EightDigitsApiException;

import android.net.http.AndroidHttpClient;

public class EightDigitsApiRequest implements Runnable {

  public String              url;
  public List<NameValuePair> pairs;
  public boolean             isAsyncRequest;
  public EightDigitsAsyncResultListener errorListener;

  public EightDigitsApiRequest(String url, List<NameValuePair> pairs,
      boolean isAsyncRequest, EightDigitsAsyncResultListener errorListener) {
    this.url = url;
    this.pairs = pairs;
    this.isAsyncRequest = isAsyncRequest;
    this.errorListener = errorListener;
  }

  public void run() {
    try {
      AndroidHttpClient client = AndroidHttpClient.newInstance("8Digits");
      EightDigitsClient.log(this.url);
      HttpPost post = new HttpPost(this.url);
      post.setEntity(new UrlEncodedFormEntity(this.pairs));

      ResponseHandler<String> responseHandler = new BasicResponseHandler();
      String response = client.execute(post, responseHandler);
      client.close();

      if (!this.isAsyncRequest) {
        EightDigitsClient.log(this.url + " = Sync API Request Response = "
            + response);
        EightDigitsClient.LAST_API_RESPONSE = response;
        EightDigitsClient.BREAK_RESPONSE_LOOP = true;
      } else {
        if(this.errorListener != null) {
          this.errorListener.handleError(new JSONObject(response));
        }
        
        EightDigitsClient.log(this.url + " = Async API Request Response = "
            + response);
      }
      EightDigitsClient.log("-------------------------");

    }
    catch (UnknownHostException e) {
      if(!this.isAsyncRequest) {
        EightDigitsClient.BREAK_RESPONSE_LOOP = true;
        EightDigitsClient.LAST_EXCEPTION = new EightDigitsApiException(-500, "Invalid API address");
      } else {
        EightDigitsClient.logError(e.getMessage());
      }
      
    }
    catch (Exception e) {
      System.err.println(e);
    }
  }

}
