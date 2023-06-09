// IXRPAccountService.aidl
package com.lushtechnology.zpass;

// Declare any non-default types here with import statements

interface IXRPAccountService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    String getAccountAddress();
    long getAccountValue();
    void pay(String receiverAdress, long amount);
}