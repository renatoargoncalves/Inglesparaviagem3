package com.sow.inglesparaviagem.model;

import android.content.Context;
import android.os.AsyncTask;

import com.sow.inglesparaviagem.R;
import com.sow.inglesparaviagem.classes.Log;
import com.sow.inglesparaviagem.classes.Phrase;
import com.sow.inglesparaviagem.events.OnLoadCategoriesCanceledEvent;
import com.sow.inglesparaviagem.events.OnLoadPhrasesEvent;
import com.sow.inglesparaviagem.events.OnLoadPhrasesFailEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


public class CategoryModelImpl implements CategoryModel {

    private static final String TAG = "CategoryModelImpl";

    @Override
    public void loadPhrases(Context context, String category) {
        new LoadPhrasesTask(context, category).execute((Void) null);
    }

    public class LoadPhrasesTask extends AsyncTask<Void, Void, Integer> {

        private static final String TAG = "LoadPhrasesTask";
        private Context mContext;
        private String mCategory;
        private ArrayList<Phrase> mPhrases = new ArrayList<>();

        LoadPhrasesTask(Context context, String category) {
            Log.w(TAG, "LoadPhrasesTask()");
            mContext = context;
            mCategory = category;
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
                Log.w(TAG, "LoadPhrasesTask().doInBackground(): categoryId: " + mCategory);

                mPhrases.clear();

                String[] strPhrases = mContext.getResources().getStringArray(R.array.phrases);
                for (int i = 0; i < strPhrases.length; i++) {
                    String[] phrase = strPhrases[i].split("#");
                    if(phrase[0].equals(mCategory))
                        mPhrases.add(new Phrase(phrase[2], phrase[1], phrase[0]));
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
                    Log.i(TAG, "LoadPhrasesTask().onPostExecute(): OnLoadPhrasesEvent()");
                    EventBus.getDefault().post(new OnLoadPhrasesEvent(mPhrases));
                    break;
//                case 2:
//                    Log.i(TAG, "LoadPhrasesTask().onPostExecute(): user wrong password!");
//                    EventBus.getDefault().post(new OnLoadCategoriesEvent());
//                    break;
                default:
                    Log.i(TAG, "LoadPhrasesTask().onPostExecute(): OnLoadPhrasesFailEvent()");
                    EventBus.getDefault().post(new OnLoadPhrasesFailEvent());
                    break;

            }
        }

        @Override
        protected void onCancelled() {
            EventBus.getDefault().post(new OnLoadCategoriesCanceledEvent());
        }
    }

}
