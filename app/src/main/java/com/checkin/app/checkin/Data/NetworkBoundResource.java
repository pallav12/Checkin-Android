package com.checkin.app.checkin.Data;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

// ResultType: Type for the Resource data
// RequestType: Type for the API response
public abstract class NetworkBoundResource<ResultType, RequestType> {
    private final static String TAG = NetworkBoundResource.class.getSimpleName();
    private final MediatorLiveData<Resource<ResultType>> mResult = new MediatorLiveData<>();
    private Object val;

    @MainThread
    public NetworkBoundResource() {
        mResult.setValue(Resource.Companion.loading(null));
        if (shouldUseLocalDb()) {
            final LiveData<ResultType> dbSource = loadFromDb();
            mResult.addSource(dbSource, data -> {
                mResult.removeSource(dbSource);
                if (shouldFetch(data)) {
                    fetchFromNetwork(dbSource);
                } else {
                    mResult.addSource(dbSource,
                            (ResultType newData) -> mResult.setValue(Resource.Companion.successCached(newData)));
                }
            });
        } else {
            fetchFromNetwork(null);
        }
    }

    private void fetchFromNetwork(LiveData<ResultType> dbSource) {
        LiveData<ApiResponse<RequestType>> apiResponse = createCall();
        final boolean useDb = shouldUseLocalDb() && dbSource != null;
        if (useDb) {
            // we re-attach dbSource as a new source,
            // it will dispatch its latest value quickly
            mResult.addSource(dbSource, newData -> mResult.setValue(Resource.Companion.loading(newData)));
        }
        mResult.addSource(apiResponse, response -> {
            mResult.removeSource(apiResponse);
            if (useDb) {
                mResult.removeSource(dbSource);
            }
            if (response != null) {
                Resource<RequestType> resource = Resource.Companion.createResource(response);
                if (useDb)
                    saveResultAndReInit(resource);
                else
                    postResultDirectly(resource);
                if (!response.isSuccessful()) {
                    onFetchFailed(response);
                    if (useDb) {
                        mResult.addSource(dbSource, newData -> mResult.setValue(Resource.Companion.error(resource.getMessage(), newData)));
                    }
                }
            }
        });
    }

    @MainThread
    private void saveResultAndReInit(Resource<RequestType> resource) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                saveCallResult(resource.getData());
                return null;
            }

            @Override
            protected void onPostExecute(Void param) {
                // we specially request a new live data,
                // otherwise we will get immediately last cached value,
                // which may not be updated with latest results received from network.
                mResult.addSource(loadFromDb(),
                        newData -> mResult.setValue(Resource.Companion.success(newData)));
            }
        }.execute();
    }

    // Called in case no Database interaction needed.
    @MainThread
    protected void postResultDirectly(Resource<RequestType> resource) {
        RequestType data = resource.getData();
        Log.e(TAG, resource.getStatus().name());
        try {
            mResult.setValue(Resource.Companion.cloneResource(resource, (ResultType) data));
        } catch (ClassCastException e) {
            Log.e(TAG, "Invalid Resource Data type.");
        }
    }

    // Called with the data in the database to decide whether it should be
    // fetched from the network.
    @MainThread
    protected boolean shouldFetch(ResultType data) {
        return true;
    }

    protected abstract boolean shouldUseLocalDb();

    // Called to create the API call.
    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();

    // Called to get the cached data from the database
    @MainThread
    protected LiveData<ResultType> loadFromDb() {
        return null;
    }

    // Called to save the result of the API response into the database
    @WorkerThread
    protected abstract void saveCallResult(RequestType data);

    // Called when the fetch fails. The child class may want to reset components
    // like rate limiter.
    @MainThread
    protected void onFetchFailed(ApiResponse<RequestType> response) {
    }

    // returns a LiveData that represents the resource, implemented
    // in the base class.
    public LiveData<Resource<ResultType>> getAsLiveData() {
        return mResult;
    }

    protected Object getVal() {
        return val;
    }

    protected void setVal(Object val) {
        this.val = val;
    }
}
