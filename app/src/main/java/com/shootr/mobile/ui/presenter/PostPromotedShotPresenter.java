package com.shootr.mobile.ui.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import com.android.billingclient.api.SkuDetails;
import com.shootr.mobile.billing.BillingManager;
import com.shootr.mobile.billing.BillingUpdatesListener;
import com.shootr.mobile.billing.ProductQueryListener;
import com.shootr.mobile.data.api.exception.ErrorInfo;
import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.data.dagger.ApplicationContext;
import com.shootr.mobile.domain.bus.EventReceived;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.AcceptPromotedTermsInteractor;
import com.shootr.mobile.domain.interactor.GetCachedPromotedTiers;
import com.shootr.mobile.domain.interactor.GetPromotedTermsInteractor;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.PostNewPrivateMessageInteractor;
import com.shootr.mobile.domain.interactor.VerifyReceiptInteractor;
import com.shootr.mobile.domain.interactor.shot.IncrementReplyCountShotInteractor;
import com.shootr.mobile.domain.interactor.shot.PostNewPromotedShotAsReplyInteractor;
import com.shootr.mobile.domain.interactor.shot.PostNewPromotedShotInStreamInteractor;
import com.shootr.mobile.domain.interactor.user.GetMentionedPeopleInteractor;
import com.shootr.mobile.domain.model.ErrorSocketMessage;
import com.shootr.mobile.domain.model.NewItemSocketMessage;
import com.shootr.mobile.domain.model.PromotedReceipt;
import com.shootr.mobile.domain.model.PromotedTermsSocketMessage;
import com.shootr.mobile.domain.model.PromotedTier;
import com.shootr.mobile.domain.model.Searchable;
import com.shootr.mobile.domain.model.SearchableType;
import com.shootr.mobile.domain.model.SocketMessage;
import com.shootr.mobile.domain.model.user.PromotedTiers;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.task.events.CommunicationErrorEvent;
import com.shootr.mobile.task.events.ConnectionNotAvailableEvent;
import com.shootr.mobile.ui.model.PromotedTermsModel;
import com.shootr.mobile.ui.model.PromotedTierModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.PromotedTierModelMapper;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.PostPromotedShotView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class PostPromotedShotPresenter implements Presenter, BillingUpdatesListener, EventReceived.Receiver {

  private static final int MAX_LENGTH = 140;
  private static final int MAX_MESSAGE_LENGTH = 5000;

  private static final int PREPARING_SHOT = 0;
  private static final int VERIFIYING_RECEIPT = 1;
  private static final int SENDING_SHOT = 2;
  private static final int CONSUMING_PRODUCT = 3;

  private static final int ERROR = -1;

  private final Bus bus;
  private final ErrorMessageFactory errorMessageFactory;
  private final PostNewPromotedShotInStreamInteractor postNewPromotedShotInStreamInteractor;
  private final PostNewPromotedShotAsReplyInteractor postNewShotAsReplyInteractor;
  private final PostNewPrivateMessageInteractor postNewPrivateMessageInteractor;
  private final GetMentionedPeopleInteractor getMentionedPeopleInteractor;
  private final IncrementReplyCountShotInteractor incrementReplyCountShotInteractor;
  private final VerifyReceiptInteractor verifyReceiptInteractor;
  private final GetPromotedTermsInteractor getPromotedTermsInteractor;
  private final AcceptPromotedTermsInteractor acceptPromotedTermsInteractor;
  private final GetCachedPromotedTiers getCachedPromotedTiers;
  private final UserModelMapper userModelMapper;
  private final PromotedTierModelMapper promotedTierModelMapper;
  private final Context context;

  private PostPromotedShotView view;
  private File selectedImageFile;
  private String shotCommentToSend;
  private String currentTextWritten = "";
  private boolean isReply;
  private String replyParentId;
  private boolean isInitialized = false;
  private Integer wordPosition;
  private String[] words;
  private String idTargetUser;
  private int maxLength = MAX_LENGTH;
  private HashMap<String, PromotedTierModel> promotedTiersModel = new HashMap<>();
  private ArrayList<PromotedTierModel> auxPromoted = new ArrayList<>();
  private BillingManager billingManager;
  private Activity activity;
  private int currentTierIndex;
  private boolean isValidatingReceipt = false;
  private String textToSend;
  private int sendingState;
  private PromotedReceipt currentPromotedReceipt;
  private List<PromotedReceipt> pendingReceipts;
  private boolean isFreeShot;
  private String idStream;
  private String currentPurchaseToken;
  private PromotedTermsModel promotedTermsModel;
  private boolean promotedTermsAccepted;

  @Inject public PostPromotedShotPresenter(@Main Bus bus, ErrorMessageFactory errorMessageFactory,
      PostNewPromotedShotInStreamInteractor postNewShotInStreamInteractor,
      PostNewPromotedShotAsReplyInteractor postNewShotAsReplyInteractor,
      PostNewPrivateMessageInteractor postNewMessageInteractor,
      GetMentionedPeopleInteractor getMentionedPeopleInteractor,
      IncrementReplyCountShotInteractor incrementReplyCountShotInteractor,
      VerifyReceiptInteractor verifyReceiptInteractor,
      GetPromotedTermsInteractor getPromotedTermsInteractor,
      AcceptPromotedTermsInteractor acceptPromotedTermsInteractor,
      GetCachedPromotedTiers getCachedPromotedTiers, UserModelMapper userModelMapper,
      PromotedTierModelMapper promotedTierModelMapper, @ApplicationContext Context context) {
    this.bus = bus;
    this.errorMessageFactory = errorMessageFactory;
    this.postNewPromotedShotInStreamInteractor = postNewShotInStreamInteractor;
    this.postNewShotAsReplyInteractor = postNewShotAsReplyInteractor;
    this.postNewPrivateMessageInteractor = postNewMessageInteractor;
    this.getMentionedPeopleInteractor = getMentionedPeopleInteractor;
    this.incrementReplyCountShotInteractor = incrementReplyCountShotInteractor;
    this.verifyReceiptInteractor = verifyReceiptInteractor;
    this.getPromotedTermsInteractor = getPromotedTermsInteractor;
    this.acceptPromotedTermsInteractor = acceptPromotedTermsInteractor;
    this.getCachedPromotedTiers = getCachedPromotedTiers;
    this.userModelMapper = userModelMapper;
    this.promotedTierModelMapper = promotedTierModelMapper;
    this.context = context;
  }

  protected void setView(PostPromotedShotView postNewShotView) {
    this.view = postNewShotView;
    this.isInitialized = true;
  }

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  public void initializeAsNewShot(PostPromotedShotView postNewShotView, String idStream) {
    this.setView(postNewShotView);
    initializeBillingManager();
    this.idStream = idStream;
    getPromotedTerms(idStream);
  }

  public void initializeAsReply(PostPromotedShotView postNewShotView, String replyParentId,
      String replyToUsername, String idStream) {
    this.setView(postNewShotView);
    if (replyParentId == null || replyToUsername == null) {
      throw new IllegalArgumentException(
          String.format("Invalid reply data: parentId=%d username=%s", replyParentId,
              replyToUsername));
    }
    this.isReply = true;
    this.replyParentId = replyParentId;
    this.idStream = idStream;
    this.view.showReplyToUsername(replyToUsername);
    getPromotedTerms(idStream);
    initializeBillingManager();
  }

  private void getPromotedTerms(String idStream) {
    getPromotedTermsInteractor.getPromotedTerms(idStream, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        /* no-op */
      }
    });
  }

  private void initializeBillingManager() {
    billingManager = new BillingManager(activity, this);
  }

  private void getCachedTiers() {
    getCachedPromotedTiers.getPromotedTiers(new Interactor.Callback<PromotedTiers>() {
      @Override public void onLoaded(PromotedTiers promotedTiers) {
        for (PromotedTier promotedTier : promotedTiers.getData()) {
          promotedTiersModel.put(promotedTier.getProductId(),
              promotedTierModelMapper.transform(promotedTier));
        }
        getStorePrices();

        setupPendingReceipts(promotedTiers);
      }
    });
  }

  private void setupPendingReceipts(PromotedTiers promotedTiers) {
    pendingReceipts = promotedTiers.getPendingReceipts();
    if (!pendingReceipts.isEmpty()) {
      view.showPendingReceiptsAlert();
    }
  }

  private void getStorePrices() {
    ArrayList<String> productId = new ArrayList<>();

    productId.addAll(promotedTiersModel.keySet());

    billingManager.getProductsDetails(productId, new ProductQueryListener() {
      @Override public void onQueryCompleted(List<SkuDetails> skuDetailsList) {
        for (SkuDetails skuDetails : skuDetailsList) {
          if (promotedTiersModel.containsKey(skuDetails.getSku())) {
            promotedTiersModel.get(skuDetails.getSku()).setDisplayPrice(skuDetails.getPrice());
            auxPromoted.add(promotedTiersModel.get(skuDetails.getSku()));
          }
        }

        if (!auxPromoted.isEmpty()) {
          Collections.sort(auxPromoted, new PromotedTierModel.PromotedComparator());
          currentTierIndex = 0;
          maxLength = (int) auxPromoted.get(currentTierIndex).getBenefits().getLenght();
          updateCharCounter(currentTextWritten);
          view.updateUi(auxPromoted.get(currentTierIndex));
          checkForPendingReceipts(currentTierIndex);
        }

        view.setMaxRange(auxPromoted.size() - 1);
      }

      @Override public void onError() {
        view.disableSendButton();
      }
    });
  }

  public void acceptPromotedTerms() {
    acceptPromotedTermsInteractor.accepPromotedTerms(idStream, promotedTermsModel.getVersion(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        promotedTermsAccepted = true;
        initPromotedShot(textToSend);
      }
    });
  }

  public void textChanged(String currentText) {
    currentTextWritten = filterText(currentText);
    updateCharCounter(currentTextWritten);
    updateSendButonEnabled(currentTextWritten);
  }

  public void choosePhotoFromGallery() {
    view.choosePhotoFromGallery();
  }

  public void takePhotoFromCamera() {
    view.takePhotoFromCamera();
  }

  public void selectImage(File selectedImageFile) {
    if (selectedImageFile != null && selectedImageFile.exists()) {
      view.showImagePreview(selectedImageFile);
      this.selectedImageFile = selectedImageFile;
      updateSendButonEnabled(currentTextWritten);
    } else {
      Timber.w("Tried to set invalid file as image: %s", selectedImageFile);
    }
  }

  public void removeImage() {
    selectedImageFile = null;
    view.hideImagePreview();
    updateSendButonEnabled(currentTextWritten);
  }

  public void initPromotedShot(String text) {
    sendingState = PREPARING_SHOT;
    textToSend = text;
    if (promotedTermsAccepted && !shouldShowPromotedTerms()) {
    if (isFreeShot && currentPromotedReceipt != null) {
      sendShot(textToSend);
    } else {
      billingManager.initiatePurchaseFlow(auxPromoted.get(currentTierIndex).getProductId());
    }
    } else {
      view.showPromotedTerms();
    }
  }

  private boolean shouldShowPromotedTerms() {
    if (promotedTermsModel != null && !promotedTermsModel.getTerms().isEmpty()) {
      return true;
    }
    promotedTermsAccepted = true;
    return false;
  }

  public void sendShot(String text) {
    sendingState = SENDING_SHOT;
    view.hideKeyboard();
    shotCommentToSend = filterText(text);

    if (canSendShot(shotCommentToSend)) {
      this.showLoading();
      startSendingShot();
    } else {
      Timber.w("Tried to closeSocket shot empty or too big: %s", shotCommentToSend);
    }
  }

  public void sendMessage(String text) {
    view.hideKeyboard();
    shotCommentToSend = filterText(text);

    if (canSendShot(shotCommentToSend)) {
      this.showLoading();
      if (idTargetUser != null) {
        postMessage(idTargetUser);
      }
    } else {
      Timber.w("Tried to closeSocket shot empty or too big: %s", shotCommentToSend);
    }
  }

  private boolean canSendShot(String filteredText) {
    return (hasImage() && isLessThanMaxLength(filteredText)) || isNotEmptyAndLessThanMaxLenght(
        filteredText);
  }

  private boolean hasImage() {
    return selectedImageFile != null;
  }

  private void startSendingShot() {
    if (!isReply) {
      postStreamShot();
    } else {
      postReplyShot();
    }
  }

  private void postReplyShot() {
    postNewShotAsReplyInteractor.postNewPromotedShotAsReply(shotCommentToSend, selectedImageFile,
        replyParentId, currentPromotedReceipt.getData(), idStream, new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            onShotSending();
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            onShotError();
          }
        });
  }

  private void incrementParentReplyCount() {
    incrementReplyCountShotInteractor.incrementReplyCount(replyParentId,
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
                /* no-op */
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
                /* no-op */
          }
        });
  }

  private void postMessage(String idTargetUser) {
    postNewPrivateMessageInteractor.postNewPrivateMessage(shotCommentToSend, selectedImageFile,
        idTargetUser, new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            onShotSending();
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            onShotError();
          }
        });
  }

  private void postStreamShot() {
    postNewPromotedShotInStreamInteractor.postNewPromotedShotInStream(shotCommentToSend,
        selectedImageFile, currentPromotedReceipt.getData(), new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            onShotSending();
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            onShotError();
          }
        });
  }

  private void onShotSending() {
    view.setResultOk();
    view.closeScreen();
  }

  private void onShotError() {
    view.showError(errorMessageFactory.getCommunicationErrorMessage());
    this.hideLoading();
  }

  private void updateCharCounter(String filteredText) {
    int remainingLength = maxLength - filteredText.length();
    view.setRemainingCharactersCount(remainingLength);

    boolean isValidShot = remainingLength > 0;
    if (isValidShot) {
      view.setRemainingCharactersColorValid();
    } else {
      view.setRemainingCharactersColorInvalid();
    }
  }

  private void updateSendButonEnabled(String filteredText) {
    if (canSendShot(filteredText)) {
      view.enableSendButton();
    } else {
      view.disableSendButton();
    }
  }

  private boolean isNotEmptyAndLessThanMaxLenght(String text) {
    return text.length() > 0 && isLessThanMaxLength(text);
  }

  private boolean isLessThanMaxLength(String text) {
    return text.length() <= maxLength;
  }

  private String filterText(String originalText) {
    String trimmed = originalText.trim();
    while (trimmed.contains("\n\n\n")) {
      trimmed = trimmed.replace("\n\n\n", "\n\n");
    }
    return trimmed;
  }

  @Subscribe public void onCommunicationError(CommunicationErrorEvent event) {
    this.hideLoading();
    view.showError(errorMessageFactory.getCommunicationErrorMessage());
  }

  @Subscribe public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
    this.hideLoading();
    view.showError(errorMessageFactory.getConnectionNotAvailableMessage());
  }

  public void navigateBack() {
    if (hasEnteredData()) {
      view.showDiscardAlert();
    } else {
      view.performBackPressed();
    }
  }

  public void confirmDiscard() {
    view.performBackPressed();
  }

  private boolean hasEnteredData() {
    return ((currentTextWritten != null) && !currentTextWritten.isEmpty()) || (selectedImageFile
        != null);
  }

  private void showLoading() {
    view.showLoading();
    view.hideSendButton();
    view.disableSendButton();
  }

  private void hideLoading() {
    view.hideLoading();
    view.enableSendButton();
    view.showSendButton();
  }

  public boolean isInitialized() {
    return isInitialized;
  }

  public File getSelectedImageFile() {
    return selectedImageFile;
  }

  public void autocompleteMention(String username, String[] words, Integer wordPosition) {
    this.words = words;
    this.wordPosition = wordPosition;
    String extractedUsername = username.substring(1);
    if (extractedUsername.length() >= 1) {
      loadMentions(extractedUsername);
    }
  }

  private void loadMentions(String extractedUsername) {
    getMentionedPeopleInteractor.searchItems(extractedUsername,
        new Interactor.Callback<List<Searchable>>() {
          @Override public void onLoaded(List<Searchable> searchables) {
            List<UserModel> mentionSuggestions = filterMentions(searchables);
            if (!mentionSuggestions.isEmpty()) {
              Collections.sort(mentionSuggestions, new UserModel.MentionComparator());
              view.showMentionSuggestions();
              view.hideImageContainer();
              view.renderMentionSuggestions(mentionSuggestions);
            } else {
              view.hideMentionSuggestions();
              showImage();
            }
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
                          /* no-op */
          }
        });
  }

  @NonNull private List<UserModel> filterMentions(List<Searchable> searchables) {
    List<UserModel> mentionSuggestions = new ArrayList<>();
    for (Searchable searchable : searchables) {
      if (searchable.getSearchableType().equals(SearchableType.USER)) {
        mentionSuggestions.add(userModelMapper.transform((User) searchable));
      }
    }
    return mentionSuggestions;
  }

  public void onMentionClicked(UserModel user) {
    String shotComment = mountShotComment(user);
    view.mentionUser(shotComment);
    view.hideMentionSuggestions();
    view.setCursorToEndOfText();
    showImage();
  }

  @NonNull public String mountShotComment(UserModel user) {
    String shotComment = "";
    Integer position = 0;
    for (String word : words) {
      if (equals(position, wordPosition)) {
        shotComment += "@" + user.getUsername() + " ";
      } else {
        shotComment += word + " ";
      }
      position++;
    }
    return shotComment;
  }

  private boolean equals(Integer a, Integer b) {
    return a != null && a.equals(b);
  }

  public void onStopMentioning() {
    view.hideMentionSuggestions();
    showImage();
  }

  public void showImage() {
    if (hasImage()) {
      view.showImageContainer();
    }
  }

  @Override public void resume() {
    bus.register(this);
  }

  @Override public void pause() {
    bus.unregister(this);
  }

  public void destroy() {
    billingManager.destroy();
  }

  public void onTierChanged(int tier) {
    currentPromotedReceipt = null;
    isFreeShot = false;
    view.setBuyButtonText();
    if (!auxPromoted.isEmpty() && tier < auxPromoted.size()) {
      maxLength = (int) auxPromoted.get(tier).getBenefits().getLenght();
      updateCharCounter(currentTextWritten);
      view.updateUi(auxPromoted.get(tier));
      currentTierIndex = tier;
      checkForPendingReceipts(tier);
      updateSendButonEnabled(currentTextWritten);
    }
  }

  private void checkForPendingReceipts(int tier) {
    for (PromotedReceipt pendingReceipt : pendingReceipts) {
      if (pendingReceipt.getProductId().equals(auxPromoted.get(tier).getProductId())) {
        isFreeShot = true;
        currentPromotedReceipt = pendingReceipt;
        view.setFreeButtonText();
        break;
      }
    }
  }

  @Override public void onBillingSetupFinished() {
    getCachedTiers();
  }

  @Override public void verifyPurchase(String purchaseInfo, String purchaseToken) {
    sendingState = VERIFIYING_RECEIPT;
    currentPurchaseToken = purchaseToken;
    verifyReceiptInteractor.verifyReceipt(purchaseInfo, new Interactor.Callback<Boolean>() {
      @Override public void onLoaded(Boolean aBoolean) {
        /* no-op */
      }
    });
  }

  @Override public void onConsumeFinished(String purchaseToken) {
    if (sendingState == VERIFIYING_RECEIPT) {
      currentPurchaseToken = null;
      sendShot(textToSend);
    }
  }

  @Override public void onError() {
    sendingState = ERROR;
    view.disableSendButton();
  }

  @Subscribe @Override public void onEvent(EventReceived.Event event) {
    if (event.getMessage().getEventType().equals(SocketMessage.NEW_ITEM_DATA)) {
      NewItemSocketMessage newItemSocketMessage = (NewItemSocketMessage) event.getMessage();

      if (newItemSocketMessage.getData().getItem() instanceof PromotedReceipt) {
        if (sendingState == VERIFIYING_RECEIPT) {
          currentPromotedReceipt = (PromotedReceipt) newItemSocketMessage.getData().getItem();
          billingManager.consumeProduct(currentPurchaseToken);
        }
      }
    } else if (event.getMessage().getEventType().equals(SocketMessage.ERROR)) {
      ErrorSocketMessage errorSocketMessage = (ErrorSocketMessage) event.getMessage();
      if (errorSocketMessage.getData().getErrorCode() == ErrorInfo.CODE_INVALID_RECEIPT) {
        sendingState = ERROR;
        view.showReceiptError();
      } else if (errorSocketMessage.getData().getErrorCode() == ErrorInfo.CODE_INVALID_SHOT_RECEIPT) {
        sendingState = ERROR;
        view.showReceiptErrorSnedingShot();
      }
    } else if (event.getMessage().getEventType().equals(SocketMessage.PROMOTED_TERMS)) {
      if (promotedTermsModel == null) {
        PromotedTermsSocketMessage promotedTermsSocketMessage = (PromotedTermsSocketMessage) event.getMessage();
        promotedTermsModel = new PromotedTermsModel();
        promotedTermsModel.setTerms((promotedTermsSocketMessage.getData()).getTerms());
        promotedTermsModel.setVersion((promotedTermsSocketMessage.getData()).getVersion());
      }
    }
  }
}

