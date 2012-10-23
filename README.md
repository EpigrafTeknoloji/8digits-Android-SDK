# 8digits.com Android SDK

## Usage

### Adding SDK To Your Project
You can add 8digits Android SDK to your project by adding 8digits-sdk.jar file to your project.

### Getting client instance
You should create new instance of client in your main activity.

```java
this.eightDigitsClient = EightDigitsClient.createInstance(this, "<API_URL>", "<TRACKING_CODE>");
```

8Digits client is a singleton class, if you want to get instance of class in other activities, you should type code below in your activity's onCreate event.

```java
this.eightDigitsClient = EightDigitsClient.getInstance();
```

### Creating Auth token (Creating session)
After getting instance of client, you should create `authToken` to make requests. To create `authToken`

```java
this.eightDigitsClient.authWithUsername("<USERNAME>", "<PASSWORD>");
```

Creating `authToken` in your main activity is enough. You don't have to call this method in other activities.

### Creating New Visit
After creating your session (Creating auth token), you should call newVisit method which creates sessionCode and hitCode. 8digits SDK will associate `hitCode` and `sessionCode` with your view to use later when sending events. 

```java
this.eightDigitsClient.newVisit("<Title of Visit>", "<Path>");
```

### Creating New Hit
If you want to create new hitCode you can call ```newScreen``` method of client.

```java
int hitCode = this.eightDigitsClient.newScreen("<Screen Name>", "<Screen Path>");
```

### Re-creating Hit
Every time user navigate to your activity, you need create new hitCode. Doing this is so easy in 8digits SDK. You just need to call ```onRestart``` method of client in your Activity's onRestart method. You can see example usage below.

```java
@Override
    protected void onRestart() {
      super.onRestart();
      this.eightDigitsClient.onRestart("<Title of Visit>", "<Path>");
}
```

### Creating New Event
To create a new event, you can use ```newEvent``` method. 8digits SDK automatically adds hitCode to your new event request. SDK, sends events to the server asynchronously and does not affect your application's user experience.

```java
this.eightDigitsClient.newEvent("<Event Key>", "<Event Value>");
```

### Getting User Score
You can get user badges with ```score``` method. This method just takes one callback argument. Callback is a instance of ``EightDigitsResultListener`` class. You can see example below.

```java
this.eightDigitsClient.score(new EightDigitsResultListener() {
        
        @Override
        public void handleResult(JSONObject result) {
          // TODO Auto-generated method stub
          
        }
      });
```

You need to control ``error`` key in handleResult method. If result has error key, this means your api request is failed.

### Getting User Badges
You can get user badges with ```badges``` method. This method just takes one callback argument. Callback is a instance of ``EightDigitsResultListener`` class. You can call this method how you call score method. You can see example above.


### Ending Hit
You should end user hits in your activities onDestroy method. You can do this by calling, ``endScreen`` method. You don't need to send any parameter to this method. 

```java
this.eightDigitsClient.endScreen();
```

### Ending Visit
You should end user visit. You should call this method only in your main activity's onDestroy method. 

```java
this.eightDigitsClient.endVisit();
```

### Author

Gurkan Oluc (@grkn) <gurkan@8digits.com>






