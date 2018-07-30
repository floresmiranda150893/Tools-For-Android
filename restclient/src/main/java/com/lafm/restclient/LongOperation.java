package com.lafm.restclient;

import android.os.AsyncTask;

/**
 * Created by Luis FM on 26/02/2018.
 */

public class LongOperation<T> extends AsyncTask<Void, Void, T> {

    private ILongOperation<T> iLongOperation = null;

    private int code = 404;

    public LongOperation(int code, ILongOperation<T> iLongOperation){
        this.iLongOperation = iLongOperation;
        this.code = code;
    }

    @Override
    protected T doInBackground(Void... params) {
        return this.iLongOperation.doInBackground();
    }

    @Override
    protected void onPostExecute(T result) {
        this.iLongOperation.onPostExecute(this.code, result);
    }

    @Override
    protected void onPreExecute() {
        this.iLongOperation.onPreExecute();
    }

}