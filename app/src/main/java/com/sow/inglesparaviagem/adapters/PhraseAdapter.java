package com.sow.inglesparaviagem.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sow.inglesparaviagem.R;
import com.sow.inglesparaviagem.classes.Phrase;

import java.util.ArrayList;

public class PhraseAdapter extends RecyclerView.Adapter<PhraseAdapter.ViewHolder> {

    private final static String TAG = "PhraseAdapter";
    private Context context;
    private ArrayList<Phrase> mPhrases = new ArrayList<>();
    private OnItemClickListener mItemClickListener;

    public PhraseAdapter(Context context, ArrayList<Phrase> phrases, boolean search) {
        this.context = context;
        mPhrases = phrases;
    }

    @Override
    public PhraseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_phrase, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PhraseAdapter.ViewHolder holder, final int position) {
        holder.textView_port.setText(mPhrases.get(position).getPort());
        holder.textView_eng.setText(mPhrases.get(position).getEng());
    }

    @Override
    public int getItemCount() {
        return mPhrases.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView_port, textView_eng;
        LinearLayout linearLayout_phrase;

        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout_phrase = (LinearLayout) itemView.findViewById(R.id.linearLayout_phrase);
            textView_port = (TextView) itemView.findViewById(R.id.textView_speak_port);
            textView_eng = (TextView) itemView.findViewById(R.id.textView_speak_eng);
            linearLayout_phrase.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getPosition());
//                Toast.makeText(context, "clicado", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

}
