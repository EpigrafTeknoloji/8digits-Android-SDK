# 8digits.com Android SDK

## Usage

### Getting client instance
You should create new instance of client in your main activity.

```java
this.eightDigitsClient = EightDigitsClient.createInstance(this, "<API_URL>", "<TRACKING_URL>");
```

8Digits client is a singleton class, if you want to get instance of class in other activities, you should type code below in your activity's onCreate event.

```java
this.eightDigitsClient = EightDigitsClient.getInstance();
```

### Creating Auth token (Creating session)
After getting instance of client, you should create auth token to make requests. To create auth token

```java
this.eightDigitsClient.authWithUsername("<USERNAME>", "<PASSWORD>");
```

Creating auth token in your main activity is enough. You don't have to call this method in other activities.

### Creating New Visit
After creating your session (Creating auth token), you should call newVisit method which creates sessionCode and hitCode. You will use hitCode when you create event for activity. To track what is happening in your activity, client associates event with hitCode.

```java
int hitCode = this.eightDigitsClient.newVisit("<Title of Visit>", "<Path>");
```

### Creating New Hit
If you want to create new hitCode you can call ```newScreen``` method of client.

```java
int hitCode = this.eightDigitsClient.newScreen("<Screen Name>", "<Screen Path>");
```

### Creating New Event
To create a new event, you can use ```newEvent``` method. You should pass hitCode for creating a new event. Client sends new events to
API asynchronously. So there is no return value.

```java
this.eightDigitsClient.newEvent("<Event Key>", "<Event Value>", this.hitCode);
```

### Creating Listener
8Digits client is not handling results of asynchronous api calls. If you want to get results of this calls, you can set a listener for them. To do this,
```java
this.eightDigitsClient.setAsyncResultListener(new AsyncResultListener());
```
You can see a example of listener in ```com.eightdigits.hello``` package. ```AsyncResultListener``` class is a listener.


### Example and Import

You can see example usages of this methods in this application. To import SDK to your application, you can use 8digits-sdk.jar file.

### Author

Gurkan Oluc (@grkn) <gurkan@8digits.com>






