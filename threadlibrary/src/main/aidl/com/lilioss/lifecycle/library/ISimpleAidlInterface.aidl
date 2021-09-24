// ISimpleAidlInterface.aidl
package com.lilioss.lifecycle.library;

// Declare any non-default types here with import statements

interface ISimpleAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    String basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    ParcelFileDescriptor shareFile();
}