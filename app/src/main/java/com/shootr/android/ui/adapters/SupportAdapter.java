package com.shootr.android.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.activities.SupportActivity;
import com.shootr.android.util.VersionUtils;
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
        ViewHolder vh = new ViewHolder(layoutView, context);
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
        private Context context;

        @Bind(R.id.support_text) TextView textView;
        @Bind(R.id.support_version_number) TextView versionNumber;
        @Bind(R.id.profile_support_container) View supportContainer;

        public ViewHolder(View v, Context context) {
            super(v);
            ButterKnife.bind(this, itemView);
            this.view = v;
            this.context = context;
        }

        public void bindText(){
            this.textView.setText(text);
            switch (text) {
                case SupportActivity.FAQ:
                    setupFAQContainer();
                    break;
                case SupportActivity.PRIVACY_POLICY:
                    setupPrivacyPolicyContainer();
                    break;
                case SupportActivity.TERMS_OF_SERVICE:
                    setupTermsOfServiceContainer();
                    break;
                case SupportActivity.VERSION:
                    setupVersionContainer();
                    break;
            }
        }

        private void setupVersionContainer() {
            supportContainer.setClickable(false);
            versionNumber.setVisibility(View.VISIBLE);
            versionNumber.setText(String.valueOf(VersionUtils.getVersionName(context.getApplicationContext())));
        }

        private void setupTermsOfServiceContainer() {
            supportContainer.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    //TODO Real implementation
                }
            });
        }

        private void setupPrivacyPolicyContainer() {
            supportContainer.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    //TODO Real implementation
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void setupFAQContainer() {
            supportContainer.setClickable(false);
            textView.setTextColor(context.getResources().getColor(R.color.gray_60));
        }
    }

}
