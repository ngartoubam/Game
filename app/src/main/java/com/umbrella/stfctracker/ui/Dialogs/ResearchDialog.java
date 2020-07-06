package com.umbrella.stfctracker.ui.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;

import com.umbrella.stfctracker.CustomComponents.InformationLabel;
import com.umbrella.stfctracker.CustomComponents.ResourceAmount;
import com.umbrella.stfctracker.CustomComponents.ResourceMaterialAmount;
import com.umbrella.stfctracker.DataTypes.ResourceMaterial;
import com.umbrella.stfctracker.Database.Data.DataFunctions;
import com.umbrella.stfctracker.Database.DatabaseClient;
import com.umbrella.stfctracker.Database.Entities.Level;
import com.umbrella.stfctracker.Database.Entities.Research;
import com.umbrella.stfctracker.R;
import com.umbrella.stfctracker.Structures.CumulativeBonus;
import com.umbrella.stfctracker.Structures.Data;
import com.umbrella.stfctracker.Structures.TimeDisplay;
import com.umbrella.stfctracker.databinding.DialogResearchBinding;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

public class ResearchDialog extends DialogFragment {
    private DialogResearchBinding binding;

    private CumulativeBonus cumulativeBonus = CumulativeBonus.getInstance();

    private Research research;
    private MutableLiveData<Level> observableLevel;

    private int currentIndex;

    private boolean isUpgradeable = true;

    //Empty constructor needed for dialogs.
    public ResearchDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(ResearchDialog.STYLE_NORMAL, R.style.FullScreen);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        if (window == null) return;
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setDimAmount(0.8f);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogResearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ResearchDialogArgs args = ResearchDialogArgs.fromBundle(requireArguments());
        research = args.getResearch();
        observableLevel = new MutableLiveData<>(args.getResearch().getLevels()
                .get(research.getUnlockedLevel() != research.getLevels().size() //Not max level then ...
                    ? research.getUnlockedLevel() //Next level
                    : research.getUnlockedLevel() - 1)); //Current unlocked level

        currentIndex = (research.getUnlockedLevel() != research.getLevels().size())
                ? research.getUnlockedLevel()
                : research.getUnlockedLevel() - 1;

        fillBaseData(args.getResearch());
        setUpObservers();
    }

    private void fillBaseData(Research research) {
        binding.dialogResearchTitleName.setText(research.getName());
        binding.dialogResearchImg.setImageDrawable(DataFunctions.decodeDrawable(getResources(), research.getImage()));
        binding.dialogResearchInfo.setText(research.getDescription());

        binding.dialogResearchCloseButton.setOnClickListener(listener -> dismiss());
        binding.dialogResearchPrevious.setOnClickListener(onPrevious ->
            observableLevel.setValue(research.getLevels().get(--currentIndex))
        );
        binding.dialogResearchNext.setOnClickListener(onNext ->
                observableLevel.setValue(research.getLevels().get(++currentIndex))
        );
        binding.dialogResearchStartButton.setOnClickListener(upgrade -> {
            binding.dialogResearchStartButton.setClickable(false);

            DatabaseClient.dbWriteExecutor.execute(() -> {
                CumulativeBonus cBonus = CumulativeBonus.getInstance();
                cBonus.setValue(research.getBonus(), research.getLevels().get(research.getUnlockedLevel()).getBonusA());

                research.setUnlockedLevel(research.getUnlockedLevel() + 1);

                DatabaseClient.getInstance(requireContext()).daoResearch().levelUp(research);

                observableLevel.postValue(research.getUnlockedLevel() != research.getLevels().size()
                        ? research.getLevels().get(++currentIndex)
                        : research.getLevels().get(currentIndex));
            });
        });
    }

    private void setUpObservers() {
        observableLevel.observe(getViewLifecycleOwner(), level -> {
            //Get all views of @{ResourceMaterialAmount, ResourceAmount}
            LinkedList<ResourceMaterialAmount> rma = new LinkedList<>(Arrays.asList(binding.dialogResearchMaterialA, binding.dialogResearchMaterialB, binding.dialogResearchMaterialC, binding.dialogResearchMaterialD));
            LinkedList<ResourceAmount> ra = new LinkedList<>(Arrays.asList(binding.dialogResearchAmountA, binding.dialogResearchAmountB));

            isUpgradeable = (research.getUnlockedLevel() != research.getLevels().size() // Not max level
                    && level.getRequiredOperationsLevel() <= Data.getInstance().getOperationsLevel() // Seen level is accessible with current Ops level
                    && level.getLevel() - 1 == research.getUnlockedLevel()); // Looking at next level that can be upgraded

            //Note: UpgradeLayout does not vanish as with Buildings because otherwise the values can't be viewed anymore.
            binding.dialogResearchStartButton.setUsable(isUpgradeable);

            binding.dialogResearchPrevious.setVisibility(currentIndex != 0 ? View.VISIBLE : View.INVISIBLE);
            binding.dialogResearchNext.setVisibility(currentIndex != (research.getLevels().size() - 1) ? View.VISIBLE : View.INVISIBLE);

            //Show current unlocked level in Progressbar.
            binding.dialogResearchLevel.setValue(research.getUnlockedLevel(), research.getLevels().size());

            binding.dialogResearchStartButton.setTime(cumulativeBonus.applyBonus(level.getUpgradeTime(), cumulativeBonus.getResearchSpeedBonus()));

            //Set current shown level.
            binding.dialogResearchCurrentLevelOnChange.setVisibility(research.getUnlockedLevel() == level.getLevel() ? View.INVISIBLE : View.VISIBLE);
            binding.dialogResearchCurrentLevelOnChange.setText(getResources().getString(R.string.currentLevel, level.getLevel()));

            binding.dialogResearchBonusValue.setText(getString(R.string.percentage, level.getBonusA()));
            binding.dialogResearchArrowUp.setVisibility(isUpgradeable ? View.VISIBLE : View.INVISIBLE);
            binding.dialogResearchBonusLayout.setVisibility(level.getBonusA() != -1 ? View.VISIBLE : View.INVISIBLE);

            //Get all materials/resources of the level.
            LinkedList<ResourceMaterial> popMaterials = (level.getMaterials() != null ? level.getMaterials() : new LinkedList<>());
            LinkedList<ResourceMaterial> popResources = (level.getResources() != null ? level.getResources() : new LinkedList<>());

            //Set details of material.
            popMaterials.forEach(resourceMaterial -> {
                ResourceMaterialAmount item = rma.pop();

                item.setValue(Long.valueOf(resourceMaterial.getValue()).intValue());
                item.setRarity(resourceMaterial.getRarity());
                item.setMaterial(resourceMaterial.getMaterial());
                item.setGrade(resourceMaterial.getGrade());
                item.setNeeded(true);
            });

            popResources.forEach(resourceMaterial -> {
                ResourceAmount item = ra.pop();

                item.setValue(cumulativeBonus.applyBonus(resourceMaterial.getValue(), cumulativeBonus.getResearchBaseCostEfficiencyBonus()));
                item.setMaterial(resourceMaterial.getMaterial());
                item.setNeeded(true);

                item.setOnClickListener(view -> {
                    InformationLabel label = new InformationLabel(requireContext());
                    label.setValue(item.getValue());
                    label.setLocation(view, 20, 20);

                    binding.dialogResearchHolder.addView(label);
                });
            });

            //Set all residual views to NOT needed.
            rma.forEach(item -> item.setNeeded(false));
            ra.forEach(item -> item.setNeeded(false));
        });
    }
}
