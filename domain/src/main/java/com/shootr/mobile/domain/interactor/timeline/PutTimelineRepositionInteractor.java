package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.TimelineReposition;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import javax.inject.Inject;

public class PutTimelineRepositionInteractor implements Interactor {

  //region Dependencies
  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final StreamRepository streamRepository;
  private String idStream;
  private String idFilter;
  private Object item;
  private int offset;

  @Inject public PutTimelineRepositionInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local StreamRepository timelineRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.streamRepository = timelineRepository;
  }
  //endregion

  public void putTimelineReposition(String idStream, String idFilter, Object item, int offset) {
    this.idStream = idStream;
    this.idFilter = idFilter;
    this.item = item;
    this.offset = offset;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    TimelineReposition timelineReposition = new TimelineReposition();
    timelineReposition.setFirstItem(item);
    timelineReposition.setOffset(offset);
    streamRepository.putTimelineReposition(timelineReposition, idStream, idFilter);
  }
}
