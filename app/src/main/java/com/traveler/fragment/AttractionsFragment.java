package com.traveler.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.traveler.Extra;
import com.traveler.R;
import com.traveler.activity.PlaceDetailActivity;
import com.traveler.adapters.PlaceItemAdapter;
import com.traveler.http.TravelerIoFacade;
import com.traveler.http.TravelerIoFacadeImpl;
import com.traveler.listeners.RecyclerItemClickListener;
import com.traveler.models.events.AttractionsErrorEvent;
import com.traveler.models.google.Place;
import com.traveler.models.google.PlaceItemsResponse;
import com.traveler.models.google.PlaceType;
import com.traveler.views.ProgressView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * @author vgrec, created on 11/3/14.
 */
public class AttractionsFragment extends Fragment {

    public static final String KEY_PLACE_TYPE = "KEY_PLACE_TYPE";
    private final int NUMBER_OF_ITEMS_PER_PAGE = 20;

    private PlaceItemAdapter adapter;
    private PlaceType placeType;
    private List<Place> places = new ArrayList<Place>();
    private int lastResultsSize;
    private String nextPageToken = "";
    /**
     * Whether the data we are viewing is first, or gathered by subsequently requests to onLoadMore.
     */
    private boolean dataLoadedFirstTime;

    @InjectView(R.id.attractions_list)
    RecyclerView recyclerView;

    @InjectView(R.id.progress_view)
    ProgressView progressView;

    public static AttractionsFragment newInstance(PlaceType placeType) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_PLACE_TYPE, placeType);
        AttractionsFragment fragment = new AttractionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public AttractionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        placeType = (PlaceType) getArguments().getSerializable(KEY_PLACE_TYPE);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attractions, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (adapter == null) {
            adapter = new PlaceItemAdapter(places);
            downloadPlaces();
        }

        adapter.setListener(new PlaceItemAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (lastResultsSize >= NUMBER_OF_ITEMS_PER_PAGE) {
                    downloadPlaces();
                }
            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        openPlaceDetailsActivity(position);
                    }
                })
        );
        recyclerView.setAdapter(adapter);

        progressView.setTryAgainClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPlaces();
            }
        });
    }

    private void openPlaceDetailsActivity(int position) {
        Intent intent = new Intent(getActivity(), PlaceDetailActivity.class);
        intent.putExtra(Extra.PLACE_ID, places.get(position).getPlaceId());
        startActivity(intent);
    }

    private void downloadPlaces() {
        showTaskInProgress();
        TravelerIoFacade ioFacade = new TravelerIoFacadeImpl(getActivity());
        ioFacade.getPlaces(placeType, nextPageToken);
    }

    // On attractions received
    public void onEvent(PlaceItemsResponse result) {
        if (result != null) {
            if (result.getPlaceType() != placeType) {
                return;
            }
            hideTaskCompleted();
            nextPageToken = result.getNextPageToken();
            lastResultsSize = result.getPlaces().size();
            places.addAll(result.getPlaces());
            adapter.notifyDataSetChanged();
        }
    }

    public void onEvent(AttractionsErrorEvent error) {
        adapter.setTaskInProgress(false);
        progressView.showError();
    }

    private void showTaskInProgress() {
        adapter.setTaskInProgress(true);
        if (!dataLoadedFirstTime) {
            progressView.show();
        } else {
            if (getActivity() != null) {
                getActivity().setProgressBarIndeterminateVisibility(true);
            }
        }
    }

    private void hideTaskCompleted() {
        adapter.setTaskInProgress(false);
        if (!dataLoadedFirstTime) {
            progressView.hide();
            dataLoadedFirstTime = true;
        } else {
            if (getActivity() != null) {
                getActivity().setProgressBarIndeterminateVisibility(false);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
