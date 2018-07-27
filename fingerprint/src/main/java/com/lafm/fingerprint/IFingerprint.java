package com.lafm.fingerprint;

/**
 * Created by Chris on 17/04/2018.
 */

public interface IFingerprint {

    void onAuthenticationStart();

    void onAuthenticationSucceeded();

    void onAuthenticationFailed();

    void onAuthenticationHelp(String message);

}
