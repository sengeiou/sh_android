package com.shootr.mobile.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.holders.ActionViewHolder;
import com.shootr.mobile.ui.adapters.holders.AllParticipantsViewHolder;
import com.shootr.mobile.ui.adapters.holders.FollowTextViewHolder;
import com.shootr.mobile.ui.adapters.holders.SeparatorViewHolder;
import com.shootr.mobile.ui.adapters.holders.SwitchViewHolder;
import com.shootr.mobile.ui.adapters.holders.WatcherViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnFollowUnfollowListener;
import com.shootr.mobile.ui.adapters.listeners.OnFollowUnfollowStreamListener;
import com.shootr.mobile.ui.adapters.listeners.OnUserClickListener;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.shootr.mobile.domain.utils.Preconditions.checkPositionIndex;

public class StreamDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private static final int TYPE_AUTHOR = 1;
  private static final int TYPE_CONTRIBUTOR = 2;
  private static final int TYPE_MEDIA = 3;
  private static final int TYPE_MUTE = 4;
  private static final int TYPE_PARTICIPANTS_TITLE = 5;
  private static final int TYPE_PARTICIPANT = 6;
  private static final int TYPE_DESCRIPTION = 7;
  private static final int TYPE_ALL_PARTICIPANTS = 8;
  private static final int TYPE_FOLLOWERS = 9;

  private static final int EXTRA_ITEMS_ABOVE_PARTICIPANTS = 7;

  private final View.OnClickListener onAuthorClickListener;
  private final View.OnClickListener onContributorsClickListener;
  private final View.OnClickListener onMediaClickListener;
  private final View.OnClickListener onAllParticipantsClickListener;
  private final View.OnClickListener onFollowersClickListener;
  private final OnUserClickListener onUserClickListener;
  private final OnFollowUnfollowListener onFollowUnfollowListener;
  private final ImageLoader imageLoader;
  private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
  private final OnFollowUnfollowStreamListener onFollowUnfollowStreamListener;
  private final NumberFormatUtil numberFormatUtil;
  private ActionViewHolder authorViewHolder;
  private ActionViewHolder mediaViewHolder;
  private ActionViewHolder contributorViewHolder;
  private ActionViewHolder followersViewHolder;
  private SwitchViewHolder muteViewHolder;
  private SeparatorViewHolder separatorViewHolder;
  private AllParticipantsViewHolder allParticipantsViewHolder;

  private List<UserModel> participants = Collections.emptyList();
  private FollowTextViewHolder descriptionViewHolder;

  private String authorName;
  private String description;
  private int contributorsNumber;
  private int followersNumber;
  private boolean isCurrentUserStreamAuthor;

  private final Set<String> keepFollowButtonIds = new HashSet<>();
  private boolean isAllParticipantsVisible = false;
  private boolean isMuted;
  private boolean isFollowing;
  private StreamModel stream;

  public StreamDetailAdapter(ImageLoader imageLoader, View.OnClickListener onAuthorClickListener,
      View.OnClickListener onContributorsClickListener, View.OnClickListener onMediaClickListener,
      View.OnClickListener onFollowersClickListener, CompoundButton.OnCheckedChangeListener onCheckedChangeListener,
      View.OnClickListener onAllParticipantsClickListener, OnUserClickListener onUserClickListener,
      OnFollowUnfollowListener onFollowUnfollowListener,
      OnFollowUnfollowStreamListener onFollowUnfollowStreamListener,
      NumberFormatUtil numberFormatUtil) {
    this.onAuthorClickListener = onAuthorClickListener;
    this.onContributorsClickListener = onContributorsClickListener;
    this.onMediaClickListener = onMediaClickListener;
    this.onFollowersClickListener = onFollowersClickListener;
    this.onCheckedChangeListener = onCheckedChangeListener;
    this.onAllParticipantsClickListener = onAllParticipantsClickListener;
    this.onUserClickListener = onUserClickListener;
    this.imageLoader = imageLoader;
    this.onFollowUnfollowListener = onFollowUnfollowListener;
    this.onFollowUnfollowStreamListener = onFollowUnfollowStreamListener;
    this.numberFormatUtil = numberFormatUtil;
  }

  public void setAuthorName(String authorName) {
    this.authorName = authorName;
    if (authorViewHolder != null) {
      authorViewHolder.setName(authorName);
    }
  }

  public void setStream(StreamModel stream) {
    this.stream = stream;
    if (descriptionViewHolder != null) {
      descriptionViewHolder.setStream(stream);
    }
  }

  public void setDescription(String description) {
    this.description = description;
    if (descriptionViewHolder != null) {
      descriptionViewHolder.setText(description);
    }
  }

  public void showAllParticipants() {
    isAllParticipantsVisible = true;
    if (allParticipantsViewHolder != null) {
      allParticipantsViewHolder.setVisible(true);
    }
  }

  @Override public int getItemViewType(int position) {
    if (position == getItemCount() - 1) {
      return TYPE_ALL_PARTICIPANTS;
    }
    switch (position) {
      case 0:
        return TYPE_DESCRIPTION;
      case 1:
        return TYPE_FOLLOWERS;
      case 2:
        return TYPE_AUTHOR;
      case 3:
        return TYPE_CONTRIBUTOR;
      case 4:
        return TYPE_MEDIA;
      case 5:
        return TYPE_MUTE;
      case 6:
        return TYPE_PARTICIPANTS_TITLE;
      default:
        return TYPE_PARTICIPANT;
    }
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v;
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    switch (viewType) {
      case TYPE_DESCRIPTION:
        return setupDescriptionViewHolder(parent, inflater);
      case TYPE_AUTHOR:
        return setupAuthorViewHolder(parent, inflater);
      case TYPE_CONTRIBUTOR:
        return setupContributorViewHolder(parent, inflater);
      case TYPE_MEDIA:
        return setupMediaViewHolder(parent, inflater);
      case TYPE_MUTE:
        return setupMuteViewHolder(parent, inflater);
      case TYPE_PARTICIPANTS_TITLE:
        return setupSeparatorViewHolder(parent, inflater);
      case TYPE_PARTICIPANT:
        return setupParticipantViewHolder(parent, inflater);
      case TYPE_ALL_PARTICIPANTS:
        return setupAllParticipantsViewHolder(parent, inflater);
      case TYPE_FOLLOWERS:
        return setupFollowersViewHolder(parent, inflater);
      default:
        throw new IllegalStateException("No holder declared for view type " + viewType);
    }
  }

  private RecyclerView.ViewHolder setupAllParticipantsViewHolder(ViewGroup parent,
      LayoutInflater inflater) {
    View v;
    if (allParticipantsViewHolder == null) {
      v = inflater.inflate(R.layout.include_all_participants_button, parent, false);
      allParticipantsViewHolder = new AllParticipantsViewHolder(v);
      v.setOnClickListener(onAllParticipantsClickListener);
    }
    return allParticipantsViewHolder;
  }

  @NonNull private RecyclerView.ViewHolder setupParticipantViewHolder(ViewGroup parent,
      LayoutInflater inflater) {
    View v;
    v = inflater.inflate(R.layout.item_list_stream_watcher, parent, false);
    return new WatcherViewHolder(v, onUserClickListener, imageLoader, onFollowUnfollowListener,
        keepFollowButtonIds);
  }

  @NonNull private RecyclerView.ViewHolder setupSeparatorViewHolder(ViewGroup parent,
      LayoutInflater inflater) {
    View v;
    if (separatorViewHolder == null) {
      v = inflater.inflate(R.layout.stream_detail_separator, parent, false);
      separatorViewHolder = new SeparatorViewHolder(v, numberFormatUtil);
    }
    return separatorViewHolder;
  }

  @NonNull
  private RecyclerView.ViewHolder setupMuteViewHolder(ViewGroup parent, LayoutInflater inflater) {
    View v;
    if (muteViewHolder == null) {
      v = inflater.inflate(R.layout.item_mute_switch, parent, false);
      muteViewHolder = new SwitchViewHolder(v);
    }
    return muteViewHolder;
  }

  @NonNull
  private RecyclerView.ViewHolder setupMediaViewHolder(ViewGroup parent, LayoutInflater inflater) {
    View v;
    if (mediaViewHolder == null) {
      v = inflater.inflate(R.layout.item_menu_action, parent, false);
      v.setOnClickListener(onMediaClickListener);
      mediaViewHolder = new ActionViewHolder(v);
    }
    return mediaViewHolder;
  }

  @NonNull
  private RecyclerView.ViewHolder setupAuthorViewHolder(ViewGroup parent, LayoutInflater inflater) {
    View v;
    if (authorViewHolder == null) {
      v = inflater.inflate(R.layout.item_menu_action, parent, false);
      v.setOnClickListener(onAuthorClickListener);
      authorViewHolder = new ActionViewHolder(v);
    }
    return authorViewHolder;
  }

  @NonNull private RecyclerView.ViewHolder setupDescriptionViewHolder(ViewGroup parent,
      LayoutInflater inflater) {
    View v;
    if (descriptionViewHolder == null) {
      v = inflater.inflate(R.layout.item_menu_follow_text, parent, false);
      descriptionViewHolder = new FollowTextViewHolder(v, onFollowUnfollowStreamListener);
    }
    return descriptionViewHolder;
  }

  private RecyclerView.ViewHolder setupContributorViewHolder(ViewGroup parent,
      LayoutInflater inflater) {
    View v;
    if (contributorViewHolder == null) {
      v = inflater.inflate(R.layout.item_menu_action, parent, false);
      v.setOnClickListener(onContributorsClickListener);
      contributorViewHolder = new ActionViewHolder(v);
    }
    return contributorViewHolder;
  }

  private RecyclerView.ViewHolder setupFollowersViewHolder(ViewGroup parent,
      LayoutInflater inflater) {
    View v;
    if (followersViewHolder == null) {
      v = inflater.inflate(R.layout.item_menu_action, parent, false);
      v.setOnClickListener(onFollowersClickListener);
      followersViewHolder = new ActionViewHolder(v);
    }
    return followersViewHolder;
  }


  @Override public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
    switch (getItemViewType(position)) {
      case TYPE_DESCRIPTION:
        descriptionViewHolder.setText(description);
        descriptionViewHolder.setStream(stream);
        descriptionViewHolder.setupFollowButtonListener();
        descriptionViewHolder.setFollowing(isFollowing);
        break;
      case TYPE_AUTHOR:
        authorViewHolder.setIcon(R.drawable.ic_stream_author_24_gray50);
        authorViewHolder.setName(authorName);
        authorViewHolder.showAdminMark();
        break;
      case TYPE_CONTRIBUTOR:
        contributorViewHolder.setIcon(R.drawable.ic_people_outline);
        contributorViewHolder.setName(R.string.title_activity_contributors);
        contributorViewHolder.setNumber(contributorsNumber);
        if (contributorsNumber == 0 && !isCurrentUserStreamAuthor) {
          contributorViewHolder.disable();
        } else {
          contributorViewHolder.enable();
        }
        break;
      case TYPE_FOLLOWERS:
        followersViewHolder.setIcon(R.drawable.ic_contributors);
        followersViewHolder.setName(R.string.stream_followers);
        followersViewHolder.setNumber(followersNumber);
        if (followersNumber == 0) {
          followersViewHolder.disable();
        } else {
          followersViewHolder.enable();
        }
        break;
      case TYPE_MEDIA:
        mediaViewHolder.setIcon(R.drawable.ic_action_stream_gallery_gray_24);
        mediaViewHolder.setName(R.string.stream_detail_media);
        break;
      case TYPE_MUTE:
        muteViewHolder.setName(R.string.stream_detail_mute);
        muteViewHolder.setMuteSwitch(onCheckedChangeListener);
        muteViewHolder.setMuteStatus(isMuted);
        break;
      case TYPE_PARTICIPANT:
        UserModel user = participants.get(participantPosition(position));
        ((WatcherViewHolder) viewHolder).bind(user);
        break;
      case TYPE_ALL_PARTICIPANTS:
        allParticipantsViewHolder.setVisible(isAllParticipantsVisible);
        break;
      case TYPE_PARTICIPANTS_TITLE:
        ((SeparatorViewHolder) viewHolder).showTitle();
        break;
      default:
    }
  }

  @Override public int getItemCount() {
    return EXTRA_ITEMS_ABOVE_PARTICIPANTS + participants.size() + 1;
  }

  private int participantPosition(int adapterPosition) {
    int userItemPosition = adapterPosition - EXTRA_ITEMS_ABOVE_PARTICIPANTS;
    checkPositionIndex(userItemPosition, participants.size());
    return userItemPosition;
  }

  public void setParticipants(List<UserModel> watchers) {
    this.participants = watchers;
  }

  public void setMuteStatus(Boolean isChecked) {
    this.isMuted = isChecked;
    if (muteViewHolder != null) {
      muteViewHolder.setMuteStatus(isChecked);
    }
  }

  public void setFollowing(Boolean isFollowing) {
    this.isFollowing = isFollowing;
    if (descriptionViewHolder != null) {
      descriptionViewHolder.setFollowing(isFollowing);
    }
  }

  public void setContributorsNumber(Integer contributorsNumber, boolean isCurrentUserStreamAuthor) {
    this.contributorsNumber = contributorsNumber;
    this.isCurrentUserStreamAuthor = isCurrentUserStreamAuthor;
    if (contributorViewHolder != null) {
      contributorViewHolder.setNumber(contributorsNumber);
    }
  }

  public void setFollowersNumber(Integer followersNumber) {
    this.followersNumber = followersNumber;
    if (followersViewHolder != null) {
      followersViewHolder.setNumber(followersNumber);
    }
  }

  public void setFollowingNumber(Integer numberOfFollowing, Integer totalWatchers) {
    if (separatorViewHolder != null) {
      separatorViewHolder.setFollowingNumber(numberOfFollowing, totalWatchers);
    }
  }

  public void disableContributors(boolean isCurrentUserStreamAuthor) {
    this.isCurrentUserStreamAuthor = isCurrentUserStreamAuthor;
    if (contributorViewHolder != null) {
      contributorViewHolder.disable();
    }
  }

  public void setButtonFollowingState(boolean b) {
    descriptionViewHolder.setFollowing(b);
  }
}
