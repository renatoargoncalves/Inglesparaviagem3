package com.sow.inglesparaviagem.model;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.sow.inglesparaviagem.R;
import com.sow.inglesparaviagem.classes.Category;
import com.sow.inglesparaviagem.classes.Log;
import com.sow.inglesparaviagem.classes.Phrase;
import com.sow.inglesparaviagem.events.OnLoadCategoriesCanceledEvent;
import com.sow.inglesparaviagem.events.OnLoadCategoriesEvent;
import com.sow.inglesparaviagem.events.OnLoadCategoriesFailEvent;
import com.sow.inglesparaviagem.events.OnLoadFilteredPhrasesEvent;
import com.sow.inglesparaviagem.events.OnLoadFilteredPhrasesFailEvent;
import com.sow.inglesparaviagem.events.OnToogleSearchViewStateCanceledEvent;
import com.sow.inglesparaviagem.events.OnToogleSearchViewToGone;
import com.sow.inglesparaviagem.events.OnToogleSearchViewToVisible;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


public class MainModelImpl implements MainModel {

    private static final String TAG = "MainModelImpl";

    @Override
    public void loadCategories(Context context) {
        new LoadCategoriesTask(context).execute((Void) null);
    }

    @Override
    public void toogleSearchViewState(int visibility) {
        new ToogleSearchViewStateTask(visibility).execute((Void) null);
    }

    @Override
    public void filterPhrases(Context context, String query) {
        new FilterPhrasesTask(context, query).execute((Void) null);
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

    public class ToogleSearchViewStateTask extends AsyncTask<Void, Void, Integer> {

        private int mVisibility;

        ToogleSearchViewStateTask(int visibility) {
            Log.w(TAG, "ToogleSearchViewStateTask()");
            mVisibility = visibility;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                Log.w(TAG, "ToogleSearchViewStateTask.onPreExecute() ");
            } catch (Exception e) {
                Log.e(TAG, "ToogleSearchViewStateTask.onPreExecute(): " + e.getMessage());
            }
        }


        @Override
        protected Integer doInBackground(Void... params) {
            if (mVisibility == View.VISIBLE)
                return View.GONE;
            else
                return View.VISIBLE;
        }

        @Override
        protected void onPostExecute(final Integer visibility) {
            Log.w(TAG, "LoadPhrasesTask().onPostExecute(): visibility: " + visibility);
            switch (visibility) {
                case View.VISIBLE:
                    Log.i(TAG, "LoadPhrasesTask().onPostExecute(): OnToogleSearchViewStateEvent()");
                    EventBus.getDefault().post(new OnToogleSearchViewToVisible());
                    break;
                case View.GONE:
                    Log.i(TAG, "LoadPhrasesTask().onPostExecute(): OnToogleSearchViewStateEvent()");
                    EventBus.getDefault().post(new OnToogleSearchViewToGone());
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            EventBus.getDefault().post(new OnToogleSearchViewStateCanceledEvent());
        }
    }

    public class FilterPhrasesTask extends AsyncTask<Void, Void, Integer> {

        private Context mContext;
        private String mQuery;
        ArrayList<Phrase> mFilteredPhrases = new ArrayList<>();

        FilterPhrasesTask(Context context, String query) {
            Log.w(TAG, "FilterPhrasesTask()");
            mContext = context;
            mQuery = query;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                Log.w(TAG, "FilterPhrasesTask.onPreExecute() ");
            } catch (Exception e) {
                Log.e(TAG, "FilterPhrasesTask.onPreExecute(): " + e.getMessage());
            }
        }


        @Override
        protected Integer doInBackground(Void... params) {
            int result = 0;
            try {
                if (mQuery.equals("")) {
                    loadAllPhrases();
                    result = 1;
                } else {
                    loadPhrasesThatContain(mQuery);
                    result = 1;
                }
            } catch (Exception e) {
                result = 0;
            }
            return result;
        }

        private void loadPhrasesThatContain(String mQuery) {
            mFilteredPhrases.clear();
            String[] strPhrases = mContext.getResources().getStringArray(R.array.phrases);
            for (int i = 0; i < strPhrases.length; i++) {
                if(strPhrases[i].toLowerCase().contains(mQuery.toLowerCase())) {
                    mFilteredPhrases.add(new Phrase(strPhrases[i].split("#")[2], strPhrases[i].split("#")[1], strPhrases[i].split("#")[0]));
                }
            }
        }

        private void loadAllPhrases() {
            mFilteredPhrases.clear();
            String[] strPhrases = mContext.getResources().getStringArray(R.array.phrases);
            for (int i = 0; i < strPhrases.length; i++) {
                mFilteredPhrases.add(new Phrase(strPhrases[i].split("#")[2], strPhrases[i].split("#")[1], strPhrases[i].split("#")[0]));
            }
        }

        @Override
        protected void onPostExecute(final Integer result) {
            Log.w(TAG, "FilterPhrasesTask().onPostExecute(): result: " + result);
                EventBus.getDefault().post(new OnLoadFilteredPhrasesEvent(mFilteredPhrases));
        }

        @Override
        protected void onCancelled() {
            EventBus.getDefault().post(new OnLoadFilteredPhrasesFailEvent());
        }
    }

}
