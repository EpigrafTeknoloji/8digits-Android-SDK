package com.eightdigits.sdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.eightdigits.sdk.exceptions.EightDigitsApiException;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

public class EightDigitsClient {

  private Activity                       activity;
  private String                         urlPrefix;
  private String                         trackingCode;
  private String                         visitorCode;
  private String                         authToken;
  private String                         sessionCode;
  private String                         username;
  private String                         password;
  private EightDigitsAsyncResultListener asyncResultListener;

  private static final String            TAG                 = "EightDigitsSDK";
  public static boolean                  BREAK_RESPONSE_LOOP = false;
  public static String                   LAST_API_RESPONSE   = "";
  public static EightDigitsApiException  LAST_EXCEPTION      = null;
  public static EightDigitsClient        instance            = null;

  public static synchronized EightDigitsClient getInstance() {
    if (instance != null)
      return instance;
    return null;
  }

  public static synchronized EightDigitsClient createInstance(
      Activity activity, String urlPrefix, String trackingCode) {

    if (instance == null)
      instance = new EightDigitsClient(activity, urlPrefix, trackingCode);

    return instance;
  }

  private EightDigitsClient(Activity activity, String urlPrefix,
      String trackingCode) {
    this.setUrlPrefix(urlPrefix);
    this.setTrackingCode(trackingCode);
    this.setVisitorCode(UUID.randomUUID().toString());
    this.setActivity(activity);

  }

  /**
   * Authenticates client with username and password, returns authToken for api
   * calls. If api call fails, returns null.
   * 
   * @param username Your 8digits username
   * @param password Your 8digits password
   * @return
   */
  public void authWithUsername(String username, String password)
      throws EightDigitsApiException {

    this.setUsername(username);
    this.setPassword(password);

    Map<String, String> params = new HashMap<String, String>(2);
    params.put("username", this.getUsername());
    params.put("password", this.getPassword());

    JSONObject response = this.syncApiRequest("/api/auth", params);

    try {
      String authToken = response.getJSONObject("data").getString("authToken");
      this.setAuthToken(authToken);
    } catch (JSONException e) {
      logError(e.getMessage());
    }
  }

  /**
   * Call this method when your application Auth token should be created for
   * using this method. API returns hitCode and sessionCode. Method sets
   * sessionCode for later use, returns hitCode to you. If API call fails method
   * returns null.
   * 
   * @param title Title of your visit.
   * @param path Path for your application. Example : /home, /list
   * @return Returns hitCode for other events on screen.
   */
  public String newVisit(String title, String path)
      throws EightDigitsApiException {
    String hitCode = null;

    int systemVersion = android.os.Build.VERSION.SDK_INT;
    // String systemName = "SystemName";
    String model = "Linux";
    String userAgent = "Mozilla/5.0 ("
        + model
        + "; U; "
        + "Android "
        + android.os.Build.VERSION.RELEASE
        + "; "
        + android.os.Build.MODEL;
        
    if(systemVersion >= 10) {
      userAgent += " "
          + android.os.Build.SERIAL;
    }
        userAgent += " like Mac OS X; en-us) AppleWebKit (KHTML, like Gecko) Mobile/8A293 Safari";

    DisplayMetrics metrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
    int width = metrics.widthPixels;
    int height = metrics.heightPixels;
    String language = Locale.getDefault().getDisplayLanguage();

    Map<String, String> params = new HashMap<String, String>();
    params.put("authToken", this.getAuthToken());
    params.put("trackingCode", this.getTrackingCode());
    params.put("visitorCode", this.getVisitorCode());
    params.put("pageTitle", title);
    params.put("path", path);
    params.put("screenWidth", new Integer(width).toString());
    params.put("screenHeight", new Integer(height).toString());
    params.put("color", "24");
    params.put("acceptLang", language);
    params.put("flashVersion", "0.0.0");
    params.put("javaEnabled", "false");
    params.put("userAgent", userAgent);
    params.put("device", android.os.Build.MANUFACTURER);
    params.put("vendor", android.os.Build.BRAND);
    params.put("model", android.os.Build.MODEL);

    try {
      JSONObject response = this.syncApiRequest("/api/visit/create", params);
      JSONObject data = response.getJSONObject("data");
      hitCode = data.getString("hitCode");
      this.setSessionCode(data.getString("sessionCode"));
      return hitCode;
    } catch (JSONException e) {
      logError(e.getMessage());
    }

    return hitCode;
  }

  /**
   * You should call newScreen method in every activity except main activity.
   * (because newVisit method creates hitCode). Auth token should be created for
   * using this method.
   * 
   * @param title Title of your activity
   * @param path Path for your application. Example : /home, /list
   * @return
   */
  public String newScreen(String title, String path)
      throws EightDigitsApiException {
    String hitCode = null;
    Map<String, String> params = new HashMap<String, String>();
    params.put("authToken", this.getAuthToken());
    params.put("trackingCode", this.getTrackingCode());
    params.put("visitorCode", this.getVisitorCode());
    params.put("sessionCode", this.getSessionCode());
    params.put("pageTitle", title);
    params.put("path", path);

    try {
      JSONObject response = this.syncApiRequest("/api/hit/create", params);
      JSONObject data = response.getJSONObject("data");
      hitCode = data.getString("hitCode");
    } catch (JSONException e) {
      logError(e.getMessage());
    }
    return hitCode;
  }

  /**
   * Creates new event. This method makes async request to app. Auth token and
   * hitCode should be created for using this method.
   * 
   * @param key Key of your event
   * @param value Value of your event
   * @param hitCode hitCode for activity which you get from newScreen or new
   *          Visit method.
   */
  public void newEvent(String key, String value, String hitCode) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("authToken", this.getAuthToken());
    params.put("trackingCode", this.getTrackingCode());
    params.put("visitorCode", this.getVisitorCode());
    params.put("sessionCode", this.getSessionCode());
    params.put("hitCode", hitCode);
    params.put("key", key);
    params.put("value", value);

    this.asyncApiRequest("/api/event/create", params);
  }

  /**
   * Returns score for visitor. Auth token should be created for using this
   * method.
   * 
   * @return
   */
  public Integer score() throws EightDigitsApiException {
    Integer score = null;
    Map<String, String> params = new HashMap<String, String>();
    params.put("authToken", this.getAuthToken());
    params.put("trackingCode", this.getTrackingCode());
    params.put("visitorCode", this.getVisitorCode());

    try {
      JSONObject response = this.syncApiRequest("/api/visitor/score", params);
      JSONObject data = response.getJSONObject("data");
      score = data.getInt("score");
    } catch (JSONException e) {
      logError(e.getMessage());
    }

    return score;
  }

  /**
   * Returns badges of current user. If no badges found returns null. Auth token
   * should be created for using this method.
   * 
   * @return List of badge id's
   */
  public List<String> badges() throws EightDigitsApiException {
    List<String> badges = null;

    Map<String, String> params = new HashMap<String, String>();
    params.put("authToken", this.getAuthToken());
    params.put("trackingCode", this.getTrackingCode());
    params.put("visitorCode", this.getVisitorCode());

    try {
      JSONObject response = this.syncApiRequest("/api/visitor/badges", params);
      JSONObject data = response.getJSONObject("data");
      JSONArray badgesAsJsonArray = data.getJSONArray("badges");
      badges = this.convertJsonArrayToArrayList(badgesAsJsonArray);
    } catch (JSONException e) {
      logError(e.getMessage());
    }

    return badges;
  }

  /**
   * End screen hit. You should call this method in onDestroy of activity. This
   * method makes async request to api so does not return result. Auth token and
   * hitCode should be created for using this method.
   * 
   * @param hitCode hitCode for activity which you get from newScreen or new
   *          Visit method.
   */
  public void endScreen(String hitCode) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("authToken", this.getAuthToken());
    params.put("trackingCode", this.getTrackingCode());
    params.put("visitorCode", this.getVisitorCode());
    params.put("sessionCode", this.getSessionCode());
    params.put("hitCode", hitCode);
    this.asyncApiRequest("/api/hit/end", params);
  }

  /**
   * End visit of user. You should call this method when user close your
   * application. Makes async api request. Auth token and sessionCode should be
   * created for using this method.
   */
  public void endVisit() {
    Map<String, String> params = new HashMap<String, String>();
    params.put("authToken", this.getAuthToken());
    params.put("trackingCode", this.getTrackingCode());
    params.put("visitorCode", this.getVisitorCode());
    params.put("sessionCode", this.getSessionCode());
    this.asyncApiRequest("/api/visit/end", params);
  }
  
  /**
   * Fills content of ImageView with badge image for given badge ID
   * 
   * @param iv      ImageView object for displaying image
   * @param badgeId Id of badge. You can find this ids in result of badges method.
   */
  public void badgeImage(ImageView iv, String badgeId) {
    String imageUrl = this.getUrlPrefix() + "/api/badge/image/" + badgeId;
    new DownloadImageTask(iv).execute(imageUrl);
  }
  
  private void asyncApiRequest(String path, Map<String, String> params) {
    try {
      this.api(path, params, true);
    } catch (EightDigitsApiException e) {
      logError(e.getMessage());
    }

  }

  private JSONObject syncApiRequest(String path, Map<String, String> params)
      throws EightDigitsApiException {
    return this.api(path, params, false);
  }

  /**
   * Main API request method
   * 
   * @param path Path of requested method
   * @param params Map of params
   * @return
   */
  private JSONObject api(String path, Map<String, String> params,
      boolean isAsyncRequest) throws EightDigitsApiException {
    log("========================");
    log(path);

    this.resetRequestInfo(isAsyncRequest);

    String url = this.getUrlPrefix() + path;

    boolean tryAgain = true;
    JSONObject response = null;
    int tryCount = 0;

    while (tryAgain) {
      
      if(tryCount > 5)
        throw new EightDigitsApiException(-503, "Error occured. Try Again");
      
      List<NameValuePair> pairs = this.generatePairsFromParams(params);

      Runnable r = new EightDigitsApiRequest(url, pairs, isAsyncRequest,
          this.getAsyncResultListener());
      new Thread(r).start();

      if (!isAsyncRequest) {
        try {
          while (!EightDigitsClient.BREAK_RESPONSE_LOOP) {
            Thread.sleep(100);
          }
        } catch (InterruptedException e) {
          throw new EightDigitsApiException(-503, "Error occured. Try Again");
        }

        if (EightDigitsClient.LAST_EXCEPTION != null) {
          EightDigitsApiException lastException = EightDigitsClient.LAST_EXCEPTION;
          throw lastException;
        }

        try {
          response = this.parse(EightDigitsClient.LAST_API_RESPONSE);
          tryAgain = false;
        } catch (EightDigitsApiException e) {
          if (e.getErrorCode() == -1) {
            log(e.getMessage());
            params.put("authToken", this.getAuthToken());
            try {
              Thread.sleep(100);
            } catch (InterruptedException e2) {
              logError(e2.getMessage());
            }
            tryCount++;
          } else {
            throw e;
          }
        }
      } else {
        tryAgain = false;
      }
    }
    return response;
  }

  private void resetRequestInfo(boolean isAsyncRequest) {

    if (!isAsyncRequest) {
      EightDigitsClient.LAST_EXCEPTION = null;
      EightDigitsClient.BREAK_RESPONSE_LOOP = false;
      EightDigitsClient.LAST_API_RESPONSE = "";
    }
  }

  private JSONObject parse(String response) throws EightDigitsApiException {
    JSONObject jsonObject = null;
    try {

      if (response.trim().length() == 0) {
        throw new EightDigitsApiException(-504, "API returned empty result.");
      }

      jsonObject = new JSONObject(response);

      if (jsonObject.getJSONObject("result").getInt("code") > 0) {
        Integer code = jsonObject.getJSONObject("result").getInt("code");
        String message = jsonObject.getJSONObject("result")
            .getString("message");
        throw new EightDigitsApiException(code, message);
      } else if (jsonObject.getJSONObject("result").getInt("code") == -1) {
        this.authWithUsername(this.getUsername(), this.getPassword());
        throw new EightDigitsApiException(-501,
            "Auth token is expired. Getting new one..");
      }
    } catch (JSONException e) {
      throw new EightDigitsApiException(-502,
          "Problem occured at JSON parsing.");
    }
    return jsonObject;
  }

  private List<String> convertJsonArrayToArrayList(JSONArray jsonArray) {
    List<String> resultList = new ArrayList<String>();

    try {
      for (int i = 0; i < jsonArray.length(); i++) {
        resultList.add(jsonArray.get(i).toString());
      }
    } catch (JSONException e) {
      logError(e.getMessage());
    }
    return resultList;
  }

  /**
   * Generates key-value pairs for Http Request entity
   * 
   * @param params
   * @return
   */
  private List<NameValuePair> generatePairsFromParams(Map<String, String> params) {
    List<NameValuePair> pairs = new ArrayList<NameValuePair>();

    for (Map.Entry<String, String> entry : params.entrySet()) {
      log("Request Param = " + entry.getKey() + " = " + entry.getValue());
      pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
    }
    return pairs;
  }
  
  public static void logError(String message) {
    Log.e(TAG, message);
  }

  public static void log(String message) {
    Log.d(TAG, message);
  }

  public Activity getActivity() {
    return activity;
  }

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
  
  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }

  public String getSessionCode() {
    return sessionCode;
  }

  public void setSessionCode(String sessionCode) {
    this.sessionCode = sessionCode;
  }

  public String getUrlPrefix() {
    return urlPrefix;
  }

  public void setUrlPrefix(String urlPrefix) {
    this.urlPrefix = urlPrefix;
  }

  public String getTrackingCode() {
    return trackingCode;
  }

  public void setTrackingCode(String trackingCode) {
    this.trackingCode = trackingCode;
  }

  public String getVisitorCode() {
    return visitorCode;
  }

  public void setVisitorCode(String visitorCode) {
    this.visitorCode = visitorCode;
  }

  public EightDigitsAsyncResultListener getAsyncResultListener() {
    return asyncResultListener;
  }

  public void setAsyncResultListener(
      EightDigitsAsyncResultListener asyncResultListener) {
    this.asyncResultListener = asyncResultListener;
  }

}
