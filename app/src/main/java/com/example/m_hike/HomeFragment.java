package com.example.m_hike;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment implements SelectListener, androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener {

    RecyclerView hikeRecycler;
    MHikeDatabase db;
    LiveData<List<Hike>> hikeList;
    LiveData<List<Hike>> filteredHikeList;
    Helper helper;
    Button resetBtn;
    SearchView searchInput;
    Spinner advancedSearch;
    ExecutorService executors = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        helper = new Helper(getParentFragmentManager());
        db = MHikeDatabase.getInstance(requireActivity().getApplicationContext());
        hikeRecycler = requireView().findViewById(R.id.hikeRecycler);
        resetBtn = requireView().findViewById(R.id.resetBtn);
        searchInput = requireView().findViewById(R.id.searchInput);
        advancedSearch = requireView().findViewById(R.id.advancedSearch);

        hikeList = db.hikeDao().getAllHikes();
        hikeRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        HikeAdapter adapter = new HikeAdapter(requireContext(), this);
        hikeRecycler.setAdapter(adapter);
        hikeList.observe(getViewLifecycleOwner(), adapter::setHikeList);

        resetBtn.setOnClickListener(this::showPopup);

        searchInput.clearFocus();
        searchInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query != null) {
                    String searchQuery = "%" + query + "%";
                    switch (advancedSearch.getSelectedItemPosition()) {
                        case 0:
                            //Search by name
                            filteredHikeList = db.hikeDao().getHikeByName(searchQuery);
                            break;
                        case 1:
                            //Search by location
                            filteredHikeList = db.hikeDao().getHikeByLocation(searchQuery);
                            break;
                        case 2:
                            //Search by length
                            filteredHikeList = db.hikeDao().getHikeByLength(searchQuery);
                            break;
                        case 3:
                            //Search by date
                            filteredHikeList = db.hikeDao().getHikeByDate(searchQuery);
                            break;
                        case 4:
                            //Search by observation type
                            LiveData<List<Integer>> hikeIdList = db.observationDao().getHikeIdByObservationType(query);
                            hikeIdList.observe(getViewLifecycleOwner(), integers -> {
                                filteredHikeList = db.hikeDao().getHikesById(integers);
                            });
                            break;
                        default:
                            //Search by name
                            filteredHikeList = db.hikeDao().getHikeByName(searchQuery);
                            break;
                    }
                    if(filteredHikeList != null) {
                        filteredHikeList.observe(getViewLifecycleOwner(), adapter::setHikeList);
                    }
                    resetBtn.setVisibility(View.GONE);
                } else {
                    hikeList.observe(getViewLifecycleOwner(), adapter::setHikeList);
                    resetBtn.setVisibility(View.VISIBLE);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if(query != null && !query.equals("")) {
                    String searchQuery = "%" + query + "%";
                    switch (advancedSearch.getSelectedItemPosition()) {
                        case 0:
                            //Search by name
                            filteredHikeList = db.hikeDao().getHikeByName(searchQuery);
                            break;
                        case 1:
                            //Search by location
                            filteredHikeList = db.hikeDao().getHikeByLocation(searchQuery);
                            break;
                        case 2:
                            //Search by length
                            filteredHikeList = db.hikeDao().getHikeByLength(searchQuery);
                            break;
                        case 3:
                            //Search by date
                            filteredHikeList = db.hikeDao().getHikeByDate(searchQuery);
                            break;
                        case 4:
                            //Search by observation type
                            LiveData<List<Integer>> hikeIdList = db.observationDao().getHikeIdByObservationType(searchQuery);
                            hikeIdList.observe(getViewLifecycleOwner(), integers -> {
                                filteredHikeList = db.hikeDao().getHikesById(integers);
                            });
                            break;
                        default:
                            //Search by name
                            filteredHikeList = db.hikeDao().getHikeByName(searchQuery);
                            break;
                    }
                    if(filteredHikeList != null) {
                        filteredHikeList.observe(getViewLifecycleOwner(), adapter::setHikeList);
                    }
                    resetBtn.setVisibility(View.GONE);
                } else {
                    resetBtn.setVisibility(View.VISIBLE);
                    hikeList.observe(getViewLifecycleOwner(), adapter::setHikeList);
                }
                return true;
            }
        });

        ArrayAdapter<String> advancedSearchAdapter = new ArrayAdapter<>(requireContext().getApplicationContext(), R.layout.drop_down_item, getResources().getStringArray(R.array.advanced_search));
        advancedSearch.setAdapter(advancedSearchAdapter);
    }

    @Override
    public void onItemClicked(Hike hike) {
        Bundle bundle = new Bundle();
        bundle.putInt("hike_id", hike.getHikeId());
        helper.replaceFragment(new HikeFragment(), bundle, "hike");
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(requireContext(), v);
        popup.setOnMenuItemClickListener(this::onMenuItemClick);
        popup.inflate(R.menu.confirmation_reset);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.yesItem) {
            resetDatabase();
            Toast.makeText(requireContext(), "Successfully reset database", Toast.LENGTH_SHORT).show();
            return true;
        } else return item.getItemId() == R.id.noItem;
    }

    private void resetDatabase() {
        Runnable resetDatabase = () -> db.hikeDao().resetDatabase();
        executors.execute(resetDatabase);
    }
}