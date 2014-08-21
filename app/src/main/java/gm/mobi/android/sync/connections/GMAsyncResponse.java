package gm.mobi.android.sync.connections;

/**
 * Created by InmaculadaAlcon on 21/08/2014.
 */
public interface GMAsyncResponse {
    void onResponseSuccess(int callId, Long numItems, Long numTotalItems, Long offset);
    void onResponseFailure(int callId);
    void onResponseSuccess(int callId);
    void lockScreen();
    void unLockScreen();
}
