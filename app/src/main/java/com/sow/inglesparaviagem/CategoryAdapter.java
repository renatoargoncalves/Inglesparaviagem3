package com.sow.inglesparaviagem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CategoryProvider.Category> categoryArrayList = new ArrayList<>();
    private OnItemClickListener mItemClickListener;

    public CategoryAdapter(Context context) {
        this.context = context;
        categoryArrayList = new CategoryProvider(context).getCategoryArrayList();
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_category, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoryAdapter.ViewHolder holder, final int position) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), categoryArrayList.get(position).getImage_id());

//        Palette p = createPaletteSync(bitmap);
        holder.imageView_category.setImageBitmap(bitmap);
        holder.textView_title.setText(categoryArrayList.get(position).getTitle());
        holder.textView_subtitle.setText(categoryArrayList.get(position).getSubtitle());
//        holder.linearLayout_categoryPanel.setBackgroundColor(p.getMutedColor(0x000000));
    }

    public Palette createPaletteSync(Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        return p;
    }


    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView_category;
        TextView textView_title, textView_subtitle;
        LinearLayout linearLayout_categoryPanel;
        RelativeLayout relativeLayout_category;

        public ViewHolder(View itemView) {
            super(itemView);
            relativeLayout_category = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_category);
            imageView_category = (ImageView) itemView.findViewById(R.id.imageView_category);
            textView_title = (TextView) itemView.findViewById(R.id.textView_title);
            textView_subtitle = (TextView) itemView.findViewById(R.id.textView_subtitle);
//            linearLayout_categoryPanel = (LinearLayout) itemView.findViewById(R.id.linearLayout_categoryPanel);
            relativeLayout_category.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
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
