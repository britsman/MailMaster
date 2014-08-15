package TIG055st2014.mailmaster.Fragments;

import TIG055st2014.mailmaster.R;
import TIG055st2014.mailmaster.Activities.ComposeActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/* M@ilMaster Multi-Account Email Client
Copyright (C) 2014 Eric Britsman & Khaled Alnawasreh

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License 
Version 2 only; as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

Contact Info: eric_britsman@hotmail.com / khaled.nawasreh@gmail.com
*/

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
