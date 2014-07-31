package TIG055st2014.mailmaster;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Folder;
import javax.mail.Message;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
		DisplayEmail d = DisplayEmail.getInstance(); 
		fileNames = d.getAttachments();
		files = d.getFiles();
		hasAttachments = true;
		if (fileNames.size() == 0) {
			hasAttachments = false;
			fileNames.add("This email contains no attachments");
		}
		listView = (ListView) findViewById(R.id.attachment_list);
		listView.setClickable(true);
		listView.setOnItemClickListener(this);
		listView.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_list_item_1, fileNames));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View newDef, int position,
			long id) {
		// Download/open clicked on item?
		if(hasAttachments){
			try {
				DownloadTask dt = new DownloadTask(fileNames.get(position), files.get(position), getApplicationContext());
				dt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
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
				File target = new File(SDCardRoot, name);
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
				//based on http://stackoverflow.com/questions/21258221/how-to-create-an-app-image-folder-to-show-in-android-gallery
				//this code is needed to get file to appear in gallery app.
			    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
			    String mCurrentPhotoPath = "file:" + target.getAbsolutePath(); 
			    File file = new File(mCurrentPhotoPath);
			    Uri contentUri = Uri.fromFile(file);
			    mediaScanIntent.setData(contentUri);
			    sendBroadcast(mediaScanIntent);
			    downloaded = true;
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}   	
    	@Override
    	protected void onPostExecute(Void v){
    		if(downloaded){
				Toast toast = Toast.makeText(context,
            			"Succesfully downloaded " + name + "!", Toast.LENGTH_SHORT);
            	toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
            	toast.show();
    		}
    		else{
				Toast toast = Toast.makeText(context,
            			"Failed to download " + name + "!", Toast.LENGTH_SHORT);
            	toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
            	toast.show();
    		}
    	}
    }
}
