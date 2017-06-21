package com.sow.inglesparaviagem.model;

import android.content.Context;
import android.os.AsyncTask;

import com.sow.inglesparaviagem.classes.Log;
import com.sow.inglesparaviagem.events.OnLoadCategoriesCanceledEvent;
import com.sow.inglesparaviagem.events.OnLoadCategoriesEvent;
import com.sow.inglesparaviagem.events.OnLoadCategoriesFailEvent;

import org.greenrobot.eventbus.EventBus;


public class MainModelImpl implements MainModel {

    private static final String TAG = "UserLoginModelImpl";

    @Override
    public void loadCategories(Context context) {
        new LoadCategoriesTask(context).execute((Void) null);
    }

    public class LoadCategoriesTask extends AsyncTask<Void, Void, Integer> {

        private static final String TAG = "LoadCategoriesTask";
        private Context mContext;

        LoadCategoriesTask(Context context) {
            Log.w(TAG, "LoadCategoriesTask()");
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                Log.w(TAG, "LoadCategoriesTask.onPreExecute() ");

            } catch (Exception e) {
                Log.e(TAG, "LoadCategoriesTask.onPreExecute(): " + e.getMessage());
            }

        }


        @Override
        protected Integer doInBackground(Void... params) {
            Integer result = 0;
            try {
                Log.w(TAG, "LoadCategoriesTask().doInBackground()");

                return result;
            } catch (Exception e) {
                Log.e(TAG, "LoadCategoriesTask.doInBackGround(): " + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(final Integer result) {
            Log.w(TAG, "LoadCategoriesTask().onPostExecute(): result: " + result);
            switch (result) {
                case 1:
                    Log.i(TAG, "LoadCategoriesTask().onPostExecute(): OnLoadCategoriesEvent()");
                    EventBus.getDefault().post(new OnLoadCategoriesEvent());
                    break;
//                case 2:
//                    Log.i(TAG, "LoadCategoriesTask().onPostExecute(): user wrong password!");
//                    EventBus.getDefault().post(new OnLoadCategoriesEvent());
//                    break;
                default:
                    Log.i(TAG, "LoadCategoriesTask().onPostExecute(): OnLoadCategoriesFailEvent()");
                    EventBus.getDefault().post(new OnLoadCategoriesFailEvent());
                    break;

            }
        }

        @Override
        protected void onCancelled() {
            EventBus.getDefault().post(new OnLoadCategoriesCanceledEvent());
        }
    }

}
