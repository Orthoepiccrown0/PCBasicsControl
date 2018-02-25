package com.pcbasics.epiccrown.pcbasiccontrol;
import android.widget.Toast;

import mobi.upod.timedurationpicker.TimeDurationPickerDialogFragment;
import mobi.upod.timedurationpicker.TimeDurationPicker;

public class PickerDialogFragment extends TimeDurationPickerDialogFragment {

    @Override
    protected long getInitialDuration() {
        return 15 * 60 * 1000;
    }


    @Override
    protected int setTimeUnits() {
        return TimeDurationPicker.HH_MM;
    }



    @Override
    public void onDurationSet(TimeDurationPicker view, long duration) {
        Control.timeToWait = duration/1000 +"";
    }
}