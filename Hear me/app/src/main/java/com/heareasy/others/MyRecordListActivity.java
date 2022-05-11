package com.heareasy.others;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.heareasy.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class MyRecordListActivity extends AppCompatActivity {
    private static Context myContext;
    private ArrayAdapter<String> listAdapter;
    private ListView mainListView;
    private ArrayList<File> myFileList = new ArrayList();

    public class FileComparator implements Comparator<File> {
        public int compare(File file, File file2) {
            if (file.isDirectory() == file2.isDirectory()) {
                return file.getName().toLowerCase().compareTo(file2.getName().toLowerCase());
            }
            return file.isDirectory() ? -1 : 1;
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.record_listview);
        myContext = this;
        this.mainListView = (ListView) findViewById(R.id.mainListView);
    }



    public void onStart() {
        super.onStart();
        int i = 0;
        String[] strArr = new String[0];
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(Arrays.asList(strArr));
        this.listAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, arrayList);
        ArrayList arrayList2 = getmyfiles();
        this.myFileList = arrayList2;
        if (arrayList2 == null || arrayList2.size() <= 0) {
            Toast.makeText(getApplicationContext(), "No recorded file", Toast.LENGTH_SHORT).show();
        } else {
            while (i < this.myFileList.size()) {
                this.listAdapter.add(((File) this.myFileList.get(i)).getName());
                i++;
            }
        }
        this.mainListView.setAdapter(this.listAdapter);

    }

    public void onStop() {
        super.onStop();
    }

    public ArrayList<File> getmyfiles() {
        ArrayList arrayList = new ArrayList();
        File[] listFiles = myContext.getApplicationContext().getFilesDir().listFiles();
        if (listFiles != null && listFiles.length > 0) {
            int i = 0;
            while (i < listFiles.length) {
                if (!listFiles[i].isDirectory() && (listFiles[i].getName().toLowerCase().endsWith(".raw") || listFiles[i].getName().toLowerCase().endsWith(".wav"))) {
                    arrayList.add(listFiles[i]);
                }
                i++;
            }
        }
        mySort(arrayList);
        return arrayList;
    }

    private void mySort(ArrayList<File> arrayList) {
        Collections.sort(arrayList);
        runOnUiThread(new Runnable() {
            public void run() {
                MyRecordListActivity.this.listAdapter.notifyDataSetChanged();
            }
        });
    }
}
