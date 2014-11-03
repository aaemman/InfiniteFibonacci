package com.iheartradio.infinitefibonacci.callback;

/**
 * Created by AlexanderEmmanuel on 2014-11-02.
 * This is a generic callback interface meant to be used in combination with an AsyncTask.
 */
public interface TaskCallback<T> {

	public abstract void onCallback(T params);
}
