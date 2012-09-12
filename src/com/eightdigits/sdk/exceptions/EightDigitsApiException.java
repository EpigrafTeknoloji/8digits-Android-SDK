package com.eightdigits.sdk.exceptions;

public class EightDigitsApiException extends Exception {

  int ErrorCode;

  public EightDigitsApiException() {
    // TODO Auto-generated constructor stub
  }

  public EightDigitsApiException(Integer errorCode, String message) {
    super(message);
    this.setErrorCode(errorCode);
  }

  public int getErrorCode() {
    return ErrorCode;
  }

  public void setErrorCode(int errorCode) {
    ErrorCode = errorCode;
  }

}
