package TIG055st2014.mailmaster.Activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import javax.activation.DataSource;

import TIG055st2014.mailmaster.R;
import TIG055st2014.mailmaster.HelpClasses.AppVariablesSingleton;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

/* M@ilMaster Multi-Account Email Client
Copyright (C) 2014 Eric Britsman & Khaled Alnawasreh
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General 
Public License as published by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the 
implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
for more details. You should have received a copy of the GNU General Public License along with this program.  
If not, see <http://www.gnu.org/licenses/>.

Contact Info: eric_britsman@hotmail.com / khaled.nawasreh@gmail.com
*/

/**
 * Activity containing the list of the currently selected email's attachments
 * (if any). They can be downloaded to the phone. Depending on fileformat they
 * will also be displayed in an imageview after the download is complete. 
 */
public class AttachmentsActivity extends Activity implements
AdapterView.OnItemClickListener {

	private ListView listView;
	public ArrayList<String> fileNames;
	public ArrayList<DataSource> files;
	private boolean hasAttachments;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attachments);
		getActionBar().setDisplayShowHomeEnabled(false);
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance(); 
		fileNames = new ArrayList<String>();
		files = new ArrayList<DataSource>();
		fileNames.addAll(apv.getFileNames());
		files.addAll(apv.getFiles());
		hasAttachments = true;
		if (fileNames.size() == 0) {
			hasAttachments = false;

			fileNames.add(getApplicationContext().
					getResources().getString(R.string.no_attachments));
		}
		listView = (ListView) findViewById(R.id.attachment_list);
		listView.setClickable(true);
		listView.setOnItemClickListener(this);
		listView.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
				R.layout.simple_attach_item, R.id.simple_attachtext, fileNames));
	}
	@Override
	protected void onStart() {
		super.onStart();
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance(); 
		//In case app has been left on attachments screen untouched for about 30 min
		//Basically if OS destroys the AppVariablesSingleton instance.
		if(apv.getEmail() == null){
			startActivity(new Intent("TIG055st2014.mailmaster.Activities.MailFolderActivity"));
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View newDef, int position,
			long id) {
		// Download/open clicked on item
		if(hasAttachments){
			try {
				String temp[] = fileNames.get(position).split("/");
				DownloadTask dt = new DownloadTask(temp[temp.length-1], files.get(position), getApplicationContext());
				dt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * AsyncTask used to download attachments and mark them as media (so they can be found by the gallery app).
	 * This also allows the user to use the downloaded file as an attachment when composing/replying.
	 */
	private class DownloadTask extends AsyncTask<Void, Void, Void>{

		private String name;
		private DataSource source;
		private Context context;
		private boolean downloaded;

		private DownloadTask(String n, DataSource d, Context c){
			name = n;
			source = d; 
			context = c;
		}

		@Override
		protected Void doInBackground(Void... v) {
			try {

				downloaded = false;
				File SDCardRoot = Environment.getExternalStorageDirectory();
				File dir = new File(SDCardRoot, "/M@ilMaster");
				if(!dir.exists()){
					dir.mkdir();
				}
				File target = new File(dir, name);
				target.canWrite();
				target.setWritable(true);
				Log.d("Filepath", target.getPath());
				FileOutputStream fos = new FileOutputStream(target);
				InputStream is = source.getInputStream();
				byte[] buffer = new byte[1024];
				int len1 = 0;
				while ((len1 = is.read(buffer)) != -1) {
					Log.d("File", "Writing");
					fos.write(buffer, 0, len1);
				}	
				fos.close();
				is.close();
				//based on http://stackoverflow.com/questions/5250515/how-to-update-the-android-media-database
				//this code is needed to get file to appear in gallery app.
				if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)){
					Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
					String mCurrentFilePath = "file://" + target.getPath(); 
					File file = new File(mCurrentFilePath);
					Uri contentUri = Uri.fromFile(file);
					mediaScanIntent.setData(contentUri);
					sendBroadcast(mediaScanIntent);
				}
				else{
					sendBroadcast (new Intent(Intent.ACTION_MEDIA_MOUNTED, 
							Uri.parse("file://" + Environment.getExternalStorageDirectory())));
				}
				downloaded = true;
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}   	
		/**
		 * Tries to display file if download was successful and file is of a supported type.
		 */
		@Override
		protected void onPostExecute(Void v){
			if(downloaded){
				Toast toast = Toast.makeText(context,
						getApplicationContext().getResources()
						.getString(R.string.toast_attdownload)+ " " +  name + "!", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
				toast.show();
				//based on http://developer.android.com/guide/appendix/media-formats.html
				// these are the ImageView supported file types.
				if(name.endsWith("jpg") || name.endsWith("png")
						|| name.endsWith("gif")
						|| name.endsWith("bmp")
						|| name.endsWith("webp")){
					String imagePath = Environment.getExternalStorageDirectory()
							.toString() + "/" + name;
					ImageView my_image = (ImageView) findViewById(R.id.my_image);
					my_image.setImageDrawable(Drawable.createFromPath(imagePath));

				}
			}
			else{
				Toast toast = Toast.makeText(context,
						getApplicationContext().getResources()
						.getString(R.string.toast_attdownload1)+" "+ name + "!", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
				toast.show();
			}
		}
	}
}

