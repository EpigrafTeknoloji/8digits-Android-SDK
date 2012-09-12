package com.eightdigits.sdk;

import org.json.JSONObject;

public interface EightDigitsAsyncResultListener {
  public void handleError(JSONObject result);
}
