package TIG055st2014.mailmaster;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;

public class SaveDraftFragment extends DialogFragment implements DialogInterface.OnClickListener {
 
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Cancel Compose");
        alertDialogBuilder.setMessage("Do you wish to save a draft?");
        //null should be your on click listener
        alertDialogBuilder.setPositiveButton("Yes", this);
        alertDialogBuilder.setNegativeButton("No", this);
        alertDialogBuilder.setCancelable(false);
        return alertDialogBuilder.create();
    }

	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
		ComposeActivity ca = (ComposeActivity) getActivity();
		if(which == DialogInterface.BUTTON_POSITIVE){
			ca.save = true;
		}
		else{
			ca.save = false;
		}
		ca.dialogResult();
	}
}
