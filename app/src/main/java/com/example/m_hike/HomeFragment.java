package com.example.m_hike;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    RecyclerView hikeRecycler;
    MHikeDatabase db;
    ExecutorService executors = Executors.newSingleThreadExecutor();
    List<Hike> hikeList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hikeRecycler = requireView().findViewById(R.id.hikeRecycler);
        db = MHikeDatabase.getInstance(requireActivity().getApplicationContext());
    }

    private void getAllHikes() {
        Runnable getAllHikes = () -> hikeList = db.hikeDao().getAllHikes();
        executors.execute(getAllHikes);
    }
}