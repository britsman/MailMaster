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

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class AttachmentsActivity extends Activity implements
		AdapterView.OnItemClickListener {

	private ListView listView;
	public ArrayList<String> fileNames;
	public ArrayList<DataSource> files;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attachments);
		DisplayEmail d = DisplayEmail.getInstance();
		fileNames = d.getAttachments();
		files = d.getFiles();
		System.out.println("AAAAAAAAAAAAAAAAAAAAAA" + fileNames);
		System.out.println("BBBBBBBBBBBBBBBBBBBBBBB" + fileNames.size());

		if (fileNames.size() == 0) {
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
		try {
			DownloadTask dt = new DownloadTask(fileNames.get(position), files.get(position));
			dt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    private class DownloadTask extends AsyncTask<Void, Void, Void>{
    	
    	private String name;
    	private DataSource source;
    	
    	private DownloadTask(String n, DataSource d){
    		name = n;
    		source = d; 
    	}
    	
    	@Override
    	protected Void doInBackground(Void... v) {
			try {
				File SDCardRoot = Environment.getExternalStorageDirectory();
				File target = new File(SDCardRoot, name);
				target.canWrite();
				target.setWritable(true);
				
				Log.d("absolute", target.getAbsolutePath());
				Log.d("normal", target.getPath());
				
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
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}   	
    }
}
