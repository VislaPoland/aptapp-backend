package com.creatix.message;

public class MessageDeliveryException extends Exception {

    public MessageDeliveryException() {
    }

    public MessageDeliveryException(String s) {
        super(s);
    }

    public MessageDeliveryException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public MessageDeliveryException(Throwable throwable) {
        super(throwable);
    }

    public MessageDeliveryException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
