package TIG055st2014.mailmaster;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AttachmentsActivity extends Activity implements AdapterView.OnItemClickListener{
	
    private ListView listView;
    public ArrayList<String> fileNames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attachments);
		getActionBar().setDisplayShowHomeEnabled(false);
		DisplayEmail d = DisplayEmail.getInstance(); 
		fileNames = d.getAttachments();
		if(fileNames.size() == 0){
			fileNames.add("This email contains no attachments");
		}
		listView = (ListView) findViewById(R.id.attachment_list);
        listView.setClickable(true);
        listView.setOnItemClickListener(this);
        listView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), 
        											 android.R.layout.simple_list_item_1, fileNames));
	}
    @Override
    public void onItemClick(AdapterView<?> parent, View newDef, int position, long id) {
    	//Download/open clicked on item?
    }
}
