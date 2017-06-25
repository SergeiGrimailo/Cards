package com.gmail.sgrimailo.utils.file_explorer;

import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gmail.sgrimailo.cards.BuildConfig;
import com.gmail.sgrimailo.cards.R;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class FileExplorerActivity extends AppCompatActivity {

    private static final boolean D = BuildConfig.DEBUG;
    private static final String TAG = FileExplorerActivity.class.getSimpleName();

    public static final String EXTRA_FOLDER = String.format("%s.%s",
            FileExplorerActivity.class.getName(), "EXTRA_FOLDER");
    public static final String EXTRA_FILENAME = String.format("%s.%s",
            FileExplorerActivity.class.getName(), "EXTRA_FILENAME");

    private Comparator<File> fileComparator = new Comparator<File>() {
        @Override
        public int compare(File file1, File file2) {
            if (file1.isDirectory() ^ file2.isDirectory()) {
                return file1.isDirectory() ? -1 : 1;
            } else {
                return file1.getName().compareTo(file2.getName());
            }
        }
    };

    private ListView mFilesList;
    private File mCurrentFolder;
    private File[] mCurrentFiles;

    private File selectedFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_navigation_drawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, 0, 0);

//        getSupportActionBar().setTitle("Save As...");

        ListView lvDestinationsList = (ListView) findViewById(R.id.lvDestinationsList);
        final File[] files = {
                Environment.getRootDirectory(),
                Environment.getExternalStorageDirectory(),
                Environment.getDataDirectory(),
                Environment.getDownloadCacheDirectory()
        };

        lvDestinationsList.setAdapter(new ArrayAdapter<>(this,
                R.layout.list_item_destination_folder, R.id.tvDestinationFolder,
                files));
        lvDestinationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                setUpFolder(files[position]);
            }
        });

        initializeFilesList();
    }

    private void initializeFilesList() {
        mFilesList = (ListView) findViewById(R.id.lvFiles);

        File rootDirectory = Environment.getRootDirectory();
        if (D) Log.d(TAG, String.format("Starting at root directory: %s", rootDirectory));
        setUpFolder(rootDirectory);

        mFilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    setUpFolder(mCurrentFolder.getParentFile());
                } else {
                    setUpFolder(mCurrentFiles[position - 1]);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.file_explorer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                break;
            case R.id.action_settings:
                break;
            case R.id.action_search:
                return item.expandActionView();
            default:
                return item.expandActionView();
        }
        return true;
    }

    private void setUpFolder(File folder) {
        try {

            if (folder != null && folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles();
                if (files == null) files = new File[0];
                Arrays.sort(files, fileComparator);

                mCurrentFolder = folder;
                mCurrentFiles = files;
                mFilesList.setAdapter(new FilesAdapter());
            }

        } catch (Exception e) {
            Log.e(TAG, String.format("Couldn't set current folder:\n%s", e.toString()));
        }
    }

    private class FilesAdapter extends BaseAdapter {

        private final LayoutInflater mInflater;

        public FilesAdapter() {
            mInflater = LayoutInflater.from(FileExplorerActivity.this);
        }

        @Override
        public int getCount() {
            return mCurrentFiles.length + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) return "..";
            else return mCurrentFiles[position - 1];
        }

        @Override
        public long getItemId(int position) {
            return position + 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View resultView;

            if (convertView == null) {
                resultView = mInflater.inflate(R.layout.list_item_file_simple, parent, false);
            } else {
                resultView = convertView;
            }

            TextView tvFileName = (TextView) resultView.findViewById(R.id.tvFileName);
            TextView tvFileType = (TextView) resultView.findViewById(R.id.tvFileType);

            if (position == 0) {
                tvFileName.setText("..");
                tvFileType.setText("Folder");
            } else {
                File file = mCurrentFiles[position - 1];
                tvFileName.setText(file.getName());
                if (file.isDirectory()) tvFileType.setText("Folder");
                else tvFileType.setText("File");
            }

            return resultView;
        }
    }
}
