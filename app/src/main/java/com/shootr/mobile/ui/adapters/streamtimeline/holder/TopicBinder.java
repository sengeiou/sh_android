package com.shootr.mobile.ui.adapters.streamtimeline.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.ahamed.multiviewadapter.BaseViewHolder;
import com.ahamed.multiviewadapter.ItemBinder;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.model.PrintableModel;
import com.shootr.mobile.ui.model.TopicModel;
import com.shootr.mobile.ui.widgets.BaseMessageTextView;

public class TopicBinder extends ItemBinder<TopicModel, TopicBinder.TopicViewHolder> {

  private final View.OnClickListener onTopicClick;

  public TopicBinder(View.OnClickListener onTopicClick) {
    this.onTopicClick = onTopicClick;
  }

  @Override public TopicViewHolder create(LayoutInflater inflater, ViewGroup parent) {
    View view = inflater.inflate(R.layout.item_topic, parent, false);
    return new TopicViewHolder(view);
  }

  @Override public void bind(TopicViewHolder holder, TopicModel item) {
    holder.topicTextView.setText(item.getComment());
    holder.topicTextView.setOnUrlClickListener(new OnUrlClickListener() {
      @Override public void onClick() {
        //TODO sendPinMessageOpenlinkAnalythics();
      }
    });
    holder.topicTextView.setBaseMessageModel(item);
    holder.topicTextView.addLinks();
  }

  @Override public boolean canBindData(Object item) {
    return item instanceof TopicModel && ((TopicModel) item).getTimelineGroup().equals(
        PrintableModel.PINNED_GROUP);
  }

  public static class TopicViewHolder extends BaseViewHolder<TopicModel> {

    BaseMessageTextView topicTextView;
    RelativeLayout container;

    public TopicViewHolder(View itemView) {
      super(itemView);
      topicTextView = (BaseMessageTextView) itemView.findViewById(R.id.timeline_message);
      container = (RelativeLayout) itemView.findViewById(R.id.container);
    }
  }

}
