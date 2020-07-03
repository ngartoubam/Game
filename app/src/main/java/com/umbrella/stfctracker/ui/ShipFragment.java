package com.umbrella.stfctracker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.umbrella.stfctracker.databinding.FragShipsBinding;

public class ShipFragment extends Fragment {
    private FragShipsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragShipsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
