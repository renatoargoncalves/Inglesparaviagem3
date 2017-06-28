package com.sow.inglesparaviagem.model;

import android.content.Context;
import android.os.AsyncTask;

import com.sow.inglesparaviagem.R;
import com.sow.inglesparaviagem.classes.Category;
import com.sow.inglesparaviagem.classes.Log;
import com.sow.inglesparaviagem.events.OnLoadCategoriesCanceledEvent;
import com.sow.inglesparaviagem.events.OnLoadCategoriesEvent;
import com.sow.inglesparaviagem.events.OnLoadCategoriesFailEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


public class MainModelImpl implements MainModel {

    private static final String TAG = "MainActivity";

    @Override
    public void loadCategories(Context context) {
        new LoadCategoriesTask(context).execute((Void) null);
    }

    public class LoadCategoriesTask extends AsyncTask<Void, Void, Integer> {

        private Context mContext;
        private ArrayList<Category> mCategories;

        LoadCategoriesTask(Context context) {
            Log.w(TAG, "LoadPhrasesTask()");
            mContext = context;
            mCategories = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                Log.w(TAG, "LoadPhrasesTask.onPreExecute() ");
            } catch (Exception e) {
                Log.e(TAG, "LoadPhrasesTask.onPreExecute(): " + e.getMessage());
            }
        }


        @Override
        protected Integer doInBackground(Void... params) {
            Integer result = 0;
            try {
                Log.w(TAG, "LoadPhrasesTask().doInBackground()");

                mCategories.clear();

                String[] strCategories = mContext.getResources().getStringArray(R.array.categories);
                for (int i = 0; i < strCategories.length; i++) {
                    String[] category = strCategories[i].split("#");
                    mCategories.add(new Category(mContext.getResources().getIdentifier(category[0], "drawable", mContext.getPackageName()), category[1], category[0]));
                }
                result = 1;
                return result;
            } catch (Exception e) {
                Log.e(TAG, "LoadPhrasesTask.doInBackGround(): " + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(final Integer result) {
            Log.w(TAG, "LoadPhrasesTask().onPostExecute(): result: " + result);
            switch (result) {
                case 1:
                    Log.i(TAG, "LoadPhrasesTask().onPostExecute(): OnLoadCategoriesEvent()");
                    EventBus.getDefault().post(new OnLoadCategoriesEvent(mCategories));
                    break;
//                case 2:
//                    Log.i(TAG, "LoadPhrasesTask().onPostExecute(): user wrong password!");
//                    EventBus.getDefault().post(new OnLoadCategoriesEvent());
//                    break;
                default:
                    Log.i(TAG, "LoadPhrasesTask().onPostExecute(): OnLoadCategoriesFailEvent()");
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
