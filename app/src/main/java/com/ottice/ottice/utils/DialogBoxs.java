package com.ottice.ottice.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.ottice.ottice.R;

import java.util.Calendar;
import java.util.Date;

/**
 * TODO: Add a class header comment!
 */
public class DialogBoxs {

    private Context context;
    private ProgressDialog prgDialog;

    public DialogBoxs(Context context) {
        this.context = context;
    }

    public void networkEnablingDialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.network_not_enabled)
                .setMessage(R.string.open_location_settings)
                .setPositiveButton(R.string.turn_on,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void showProgressBarDialog(){
        // setting progress_bar bar dialog
        prgDialog = new ProgressDialog(context);
        try {
            prgDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        prgDialog.setContentView(R.layout.progress_bar);
        prgDialog.setCancelable(false);
    }

    public void hideProgressBar(){
        if(prgDialog != null){
            prgDialog.hide();
        }
    }


    public void showSearchHelp(){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.search_help_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.9f;
        window.setAttributes(lp);

        Button gotIt = (Button) dialog.findViewById(R.id.searchHintButton);
        gotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showFilterHelp();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }


    private void showFilterHelp(){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.filter_help_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.9f;
        window.setAttributes(lp);


        Button gotIt = (Button) dialog.findViewById(R.id.filterGotItButton);
        gotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showStartTimeSelectionHelp();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }


    private void showStartTimeSelectionHelp(){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.start_time_help_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.9f;
        window.setAttributes(lp);


        Button gotIt = (Button) dialog.findViewById(R.id.startTimeGotItButton);
        TextView helpDate = (TextView) dialog.findViewById(R.id.helpDate);
        TextView helpTime = (TextView) dialog.findViewById(R.id.helpTime);

        Date date = Calendar.getInstance().getTime();
        helpDate.setText(Common.showingDateFormat.format(date));
        helpTime.setText(Common.showingTimeFormat.format(date));

        gotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                PrefrencesClass.savePreferenceBoolean(context, Common.DASHBOARD_FIRST_TIME_VISITED, true);
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }
}
