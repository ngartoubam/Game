package com.umbrella.stfctracker.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.umbrella.stfctracker.Adapters.BuiltShipRecyclerViewAdapter;
import com.umbrella.stfctracker.Database.Models.BuiltShipViewModel;
import com.umbrella.stfctracker.R;
import com.umbrella.stfctracker.databinding.FragShipsBinding;

import static com.umbrella.stfctracker.Adapters.BuiltShipRecyclerViewAdapter.*;

public class ShipFragment extends Fragment {
    private FragShipsBinding binding;

    private BuiltShipRecyclerViewAdapter mAdapter;
    private BuiltShipViewModel mViewModel;

    private boolean tierDownActivated = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragShipsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpRecyclerView();
        setUpLiveData();

        binding.fragShipsAction.setLongClickable(true);
        binding.fragShipsAction.setOnClickListener(v -> {
            if (tierDownActivated) {
                tierDownActivated = false;

                v.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_fab_back));
            } else {
                Navigation.findNavController(requireView()).navigate(ShipFragmentDirections.shipsToAddShip());
            }
        });
        binding.fragShipsAction.setOnLongClickListener(v -> {
            if (!tierDownActivated) {
                tierDownActivated = true;

                v.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_fab));
            }
            return true;
        });
    }

    private void setUpRecyclerView() {
        mViewModel = new ViewModelProvider(this).get(BuiltShipViewModel.class);

        ItemPressedListener pressedListener = builtShip -> {
            if (tierDownActivated) {
                if (builtShip.getCurrentTier() == 1) {
                    Toast.makeText(requireContext(), getString(R.string.ship_tierDown_warning, builtShip.getName()), Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle(getString(R.string.ship_tierDown_confirmation_title, builtShip.getName()));
                    builder.setMessage(getString(R.string.ship_tierDown_confirmation_description, builtShip.getName()));

                    builder.setPositiveButton("YES", (((dialog, which) -> {
                        mViewModel.onTierDown(builtShip);
                        Toast.makeText(requireContext(), getString(R.string.ship_tierDown_confirmation, builtShip.getName()), Toast.LENGTH_SHORT).show();
                    })));
                    builder.setNegativeButton("NO", (((dialog, which) -> dialog.cancel())));

                    builder.create().show();
                }
            } else {
                Navigation.findNavController(requireView()).navigate(ShipFragmentDirections.shipsToShipDetails(builtShip));
            }
        };

        ItemScrapListener scrapListener = builtShip -> mViewModel.onScrap(builtShip);

        mAdapter = new BuiltShipRecyclerViewAdapter(requireActivity().getApplication(), pressedListener, scrapListener);
        binding.fragShipsGrid.setAdapter(mAdapter);
        binding.fragShipsGrid.setHasFixedSize(true);
    }

    private void setUpLiveData() {
        mViewModel.getAllBuiltShipsLive().observe(getViewLifecycleOwner(), builtShips ->
                mAdapter.setBuiltShips(builtShips));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}