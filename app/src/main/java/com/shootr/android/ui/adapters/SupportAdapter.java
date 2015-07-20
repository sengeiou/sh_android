package com.shootr.android.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import java.util.List;

public class SupportAdapter extends RecyclerView.Adapter<SupportAdapter.ViewHolder> {

    private Context context;
    private List<String> dataset;

    public SupportAdapter(Context context, List<String> supportList) {
        this.context = context;
        this.dataset = supportList;
    }

    @Override public SupportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater
          .from(parent.getContext())
          .inflate(R.layout.support_text_layout, parent, false);
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        holder.text = dataset.get(position);
        holder.bindText();
    }

    @Override public int getItemCount() {
        return dataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public String text;

        @Bind(R.id.support_text) TextView textView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, itemView);
            this.view = v;
        }

        public void bindText(){
            this.textView.setText(text);
        }
    }
}
