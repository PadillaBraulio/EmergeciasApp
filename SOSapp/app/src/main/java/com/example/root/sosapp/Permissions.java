package com.example.root.sosapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.security.Permission;

/**
 * Created by root on 11/07/16.
 */
public class Permissions {
    public static int i = 0;
    public static void requestPermission(AppCompatActivity activity, int requestId,
                                         String permission) {

        i = i + 1;
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            // Display a dialog with rationale.
            Permissions.RationaleDialog.newInstance(requestId, permission)
                    .show(activity.getSupportFragmentManager(), "dialog");
        } else {
            // Location permission has not been granted yet, request it.
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestId);

        }
    }

    /**
     * Checks if the result contains a {@link PackageManager#PERMISSION_GRANTED} result for a
     * permission from a runtime permissions request.
     *
     * @see android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback
     */
    public static boolean isPermissionGranted(String[] grantPermissions, int[] grantResults,
                                              String permission) {
        for (int i = 0; i < grantPermissions.length; i++) {
            if (permission.equals(grantPermissions[i])) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }

    /**
     * A dialog that displays a permission denied message.
     */
    public static class PermissionDeniedDialog extends android.support.v4.app.DialogFragment {


        private static final String ARGUMENT_PERMISSION_NAME = "PERMISION";
        private String permission;


        /**
         * Creates a new instance of this dialog and optionally finishes the calling Activity
         * when the 'Ok' button is clicked.
         */
        public static PermissionDeniedDialog newInstance(String permission) {
            Bundle arguments = new Bundle();
            arguments.putString(ARGUMENT_PERMISSION_NAME,permission);
            PermissionDeniedDialog dialog = new PermissionDeniedDialog();
            dialog.setArguments(arguments);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            permission = getArguments().getString(ARGUMENT_PERMISSION_NAME);
            return new AlertDialog.Builder(getActivity())
                    .setMessage(Utilities.getPermissionDenied(permission))
                    .setPositiveButton(android.R.string.ok, null)
                    .create();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            Toast.makeText(getActivity(), Utilities.getAdviceWrong(permission),
                    Toast.LENGTH_SHORT).show();
            if(permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)){
               // getActivity().finish();

            }

        }

    }

    /**
     * A dialog that explains the use of the location permission and requests the necessary
     * permission.
     * <p>
     * The activity should implement
     * {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}
     * to handle permit or denial of this permission request.
     */
    public static class RationaleDialog extends android.support.v4.app.DialogFragment {

        private static final String ARGUMENT_PERMISSION_REQUEST_CODE = "requestCode";

        private static final String ARGUMENT_PERMISSION_NAME = "PERMISION";

        private String permission = "";
        private boolean showmessage =  true;
        /**
         * Creates a new instance of a dialog displaying the rationale for the use of the location
         * permission.
         * <p>
         * The permission is requested after clicking 'ok'.
         *
         * @param requestCode    Id of the request that is used to request the permission. It is
         *                       returned to the
         */
        public static RationaleDialog newInstance(int requestCode, String permission) {
            Bundle arguments = new Bundle();
            arguments.putInt(ARGUMENT_PERMISSION_REQUEST_CODE, requestCode);
            arguments.putString(ARGUMENT_PERMISSION_NAME,permission);
            RationaleDialog dialog = new RationaleDialog();
            dialog.setArguments(arguments);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle arguments = getArguments();
            final int requestCode = arguments.getInt(ARGUMENT_PERMISSION_REQUEST_CODE);
            permission = arguments.getString(ARGUMENT_PERMISSION_NAME);

            return new AlertDialog.Builder(getActivity())
                    .setMessage(Utilities.getPermissionRationale(permission))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // After click on Ok, request the permission.
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{permission},
                                    requestCode);
                            showmessage = false;
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            if(showmessage) {
                Toast.makeText(getActivity(),
                        Utilities.getAdviceWrong(permission),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


    public static class EnableMobileDataDialog extends android.support.v4.app.DialogFragment {

        private static final String ARGUMENT_PERMISSION_REQUEST_CODE = "requestCode";

        private static final String ARGUMENT_PERMISSION_NAME = "PERMISION";

        private String permission = "";
        private boolean showmessage =  true;
        /**
         * Creates a new instance of a dialog displaying the rationale for the use of the location
         * permission.
         * <p>
         * The permission is requested after clicking 'ok'.
         *
         * @param requestCode    Id of the request that is used to request the permission. It is
         *                       returned to the
         */
        public static RationaleDialog newInstance(int requestCode, String permission) {
            Bundle arguments = new Bundle();
            arguments.putInt(ARGUMENT_PERMISSION_REQUEST_CODE, requestCode);
            arguments.putString(ARGUMENT_PERMISSION_NAME,permission);
            RationaleDialog dialog = new RationaleDialog();
            dialog.setArguments(arguments);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle arguments = getArguments();
            final int requestCode = arguments.getInt(ARGUMENT_PERMISSION_REQUEST_CODE);
            permission = arguments.getString(ARGUMENT_PERMISSION_NAME);

            return new AlertDialog.Builder(getActivity())
                    .setMessage(Utilities.getPermissionRationale(permission))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // After click on Ok, request the permission.
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
        }

    }



    public static abstract class Utilities{

        public static int getPermissionDenied(String permission){
            switch (permission){
                case Manifest.permission.SEND_SMS:{
                    return R.string.sendms_permission_denied;
                }
                case Manifest.permission.ACCESS_FINE_LOCATION:{
                    return R.string.location_permission_denied;
                }
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:{
                    return R.string.write_permission_denied;
                }
            }
            return -1;
        }


        public static int getPermissionRationale(String permission){
            switch (permission){
                case Manifest.permission.SEND_SMS:{
                    return R.string.permission_rationale_sendms;
                }
                case Manifest.permission.ACCESS_FINE_LOCATION:{
                    return R.string.permission_rationale_location;
                }
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:{
                    return R.string.write_permission_rationale;
                }
            }
            return -1;
        }
        public static int getAdviceWrong(String permission){
            switch (permission){
                case Manifest.permission.SEND_SMS:{
                    return R.string.sendms_advice_wrong;
                }
                case Manifest.permission.ACCESS_FINE_LOCATION:{
                    return R.string.location_advice_wrong;
                }
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:{
                    return R.string.write_advice_wrong;
                }
            }
            return -1;
        }
    }
}
