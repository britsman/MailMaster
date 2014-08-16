package TIG055st2014.mailmaster.Activities;

import TIG055st2014.mailmaster.R;
import TIG055st2014.mailmaster.R.layout;
import TIG055st2014.mailmaster.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class GPLInfoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gplinfo);
	}
	@Override
	protected void onStart(){
		super.onStart();
		TextView tv = (TextView) findViewById(R.id.text_gpl_info);
		if(getIntent().getStringExtra("type").equals("conditions")){
			tv.setText("M@ilMaster Multi-Account Email Client\nCopyright (C) 2014 Eric Britsman & Khaled Alnawasreh\n" +
		               "This program is free software; you can redistribute it and/or modify it under the terms of " +
					   "the GNU General Public License Version 2 only; as published by the Free Software Foundation.\n" +
		               "This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; " +
					   "without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. " +
		               "See the GNU General Public License for more details.\nYou should have received a copy of " +
					   "the GNU General Public License along with this program; if not, write to the Free Software " +
					   "Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.");
}
		else{
			tv.setText("NO WARRANTY" + "\n\n" + "BECAUSE THE PROGRAM IS LICENSED FREE OF CHARGE, THERE IS NO WARRANTY FOR THE PROGRAM," + 
		               " TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER" + 
					   " PARTIES PROVIDE THE PROGRAM \"AS IS\" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT " +
		               "LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE " +
					   "QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE, YOU ASSUME THE COST OF ALL " +
		               "NECESSARY SERVICING, REPAIR OR CORRECTION.\n" + "IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN WRITING" +
					   " WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MAY MODIFY AND/OR REDISTRIBUTE THE PROGRAM AS PERMITTED ABOVE, BE " +
		               " LIABLE TO YOU FOR DAMAGES, INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE" +
					   " OR INABILITY TO USE THE PROGRAM (INCLUDING BUT NOT LIMITED TO LOSS OF DATA OR DATA BEING RENDERED INACCURATE OR LOSSES" +
		               " SUSTAINED BY YOU OR THIRD PARTIES OR A FAILURE OF THE PROGRAM TO OPERATE WITH ANY OTHER PROGRAMS), EVEN IF SUCH HOLDER" + 
					   " OR OTHER PARTY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.");
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gplinfo, menu);
		return true;
	}

}
