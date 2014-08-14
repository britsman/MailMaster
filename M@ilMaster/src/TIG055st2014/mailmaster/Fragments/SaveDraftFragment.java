package TIG055st2014.mailmaster.Fragments;

import TIG055st2014.mailmaster.R;
import TIG055st2014.mailmaster.Activities.ComposeActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Fragment used to provide dialog for querying user if they wish to save the email
 * they are composing as a draft.
 */
public class SaveDraftFragment extends DialogFragment implements DialogInterface.OnClickListener {
 
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        
      //reading from the resource file depending on which language is selected
        String messageTitle = getResources().getString(R.string.message_title);
        String dialogmessage = getResources().getString(R.string.message_dia);
		alertDialogBuilder.setTitle( messageTitle);
        alertDialogBuilder.setMessage(dialogmessage);
        //null should be your on click listener
        
        String yes = getResources().getString(R.string.yes_answer);
        String no = getResources().getString(R.string.no_answer);
        alertDialogBuilder.setPositiveButton(yes, this);
        alertDialogBuilder.setNegativeButton(no, this);
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