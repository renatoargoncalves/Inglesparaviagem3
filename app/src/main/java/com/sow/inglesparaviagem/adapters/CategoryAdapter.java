package com.sow.inglesparaviagem.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sow.inglesparaviagem.R;
import com.sow.inglesparaviagem.classes.Category;
import com.sow.inglesparaviagem.classes.Log;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final static String TAG = "CategoryAdapter";
    private Context mContext;
    private ArrayList<Category> mCategories = new ArrayList<>();
    private OnItemClickListener mItemClickListener;


    public CategoryAdapter(Context context, ArrayList<Category> categories) {
        Log.w(TAG, "CategoryAdapter()");
        mContext = context;
        mCategories = categories;
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoryAdapter.ViewHolder holder, final int position) {
        holder.imageView_category.setImageResource(mCategories.get(position).getImage());
        holder.textView_title.setText(mCategories.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView_category;
        TextView textView_title;
        RelativeLayout relativeLayout_category;

        public ViewHolder(View itemView) {
            super(itemView);
            relativeLayout_category = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_category);
            imageView_category = (ImageView) itemView.findViewById(R.id.imageView_category);
            textView_title = (TextView) itemView.findViewById(R.id.textView_title);
            relativeLayout_category.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.w(TAG, "CategoryAdapter.onClick()");
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getPosition());
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
