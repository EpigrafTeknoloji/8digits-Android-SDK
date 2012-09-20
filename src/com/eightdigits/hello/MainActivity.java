package com.eightdigits.hello;

import com.eightdigits.sdk.EightDigitsClient;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

  public EightDigitsClient eightDigitsClient;
  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.eightDigitsClient = EightDigitsClient.createInstance(this, "http://demo1.8digits.com", "DJjAd2sj03");
        this.eightDigitsClient.authWithUsername("verisun", "hebelek");
        this.eightDigitsClient.newVisit("Yeni Ziyaret", "/home");
        this.eightDigitsClient.newEvent("event1", "value1");
        setContentView(R.layout.activity_main);
    }
    
    @Override
    protected void onRestart() {
      super.onRestart();
      this.eightDigitsClient.onRestart("Yeni ziyaret", "/home");
      this.eightDigitsClient.newEvent("event2", "value2");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
