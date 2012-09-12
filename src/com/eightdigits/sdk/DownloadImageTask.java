package com.eightdigits.sdk;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
  
  private ImageView imageView;
  
  public DownloadImageTask(ImageView iv) {
    this.imageView = iv;
  }
  
  @Override
  protected Bitmap doInBackground(String... params) {
    String imageUrl = params[0];
    Bitmap decodedBitmap = null;
    
    try {
      InputStream in = new java.net.URL(imageUrl).openStream();
      decodedBitmap = BitmapFactory.decodeStream(in);
    } catch (Exception e) {
      // TODO: handle exception
    }
    return decodedBitmap;
  }
  
  @Override
  protected void onPostExecute(Bitmap result) {
    
    if(this.imageView == null)
      return;
    
    this.imageView.setImageBitmap(result);
  }
  

}
