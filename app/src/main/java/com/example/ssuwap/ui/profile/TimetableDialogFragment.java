package com.example.ssuwap.ui.profile;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.ssuwap.R;

public class TimetableDialogFragment extends DialogFragment {

    public interface TimetableDialogListener {
        void onTimetableSelected(String year, String semester);
    }

    private final TimetableDialogListener listener;

    public TimetableDialogFragment(TimetableDialogListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_timetable_selector);

        NumberPicker yearPicker = dialog.findViewById(R.id.year_picker);
        NumberPicker semesterPicker = dialog.findViewById(R.id.semester_picker);
        Button btnSave = dialog.findViewById(R.id.btn_save_timetable);

        yearPicker.setMinValue(1);
        yearPicker.setMaxValue(4);
        yearPicker.setValue(2);

        semesterPicker.setMinValue(1);
        semesterPicker.setMaxValue(2);
        semesterPicker.setValue(2);

        btnSave.setOnClickListener(v -> {
            String selectedYear = String.valueOf(yearPicker.getValue());
            String selectedSemester = String.valueOf(semesterPicker.getValue());

            if (listener != null) {
                listener.onTimetableSelected(selectedYear, selectedSemester);
            }
            dismiss();
        });

        return dialog;
    }
}