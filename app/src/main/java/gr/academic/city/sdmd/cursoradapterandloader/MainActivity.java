package gr.academic.city.sdmd.cursoradapterandloader;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import gr.academic.city.sdmd.cursoradapterandloader.db.StudentManagementContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    // Define a projection that specifies which columns from the database
    // you will actually use after this query.
    // To save on resources only return the column values that you actually need.
    private static final String[] PROJECTION = {
            StudentManagementContract.Student._ID,
            StudentManagementContract.Student.COLUMN_NAME_FIRST_NAME,
            StudentManagementContract.Student.COLUMN_NAME_LAST_NAME,
            StudentManagementContract.Student.COLUMN_NAME_AGE
    };

    // How you want the results sorted in the resulting Cursor
    private static final String SORT_ORDER = StudentManagementContract.Student._ID + " ASC";

    // Identifies a particular Loader being used in this component
    private static final int STUDENTS_LOADER = 0;

    private final static String[] FROM_COLUMNS = {
            StudentManagementContract.Student._ID,
            StudentManagementContract.Student.COLUMN_NAME_FIRST_NAME,
            StudentManagementContract.Student.COLUMN_NAME_LAST_NAME,
            StudentManagementContract.Student.COLUMN_NAME_AGE };

    private final static int[] TO_IDS = {
            R.id.cb_presence,
            R.id.tv_first_name,
            R.id.tv_last_name,
            R.id.tv_age};


    private CursorAdapter adapter;

    private BroadcastReceiver getAllStudentsResultBroadcastReceiver;

    {
        getAllStudentsResultBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String issuesResults = intent.getStringExtra(StudentService.EXTRA_STUDENTS_RESULT);

                Issues issues = new Gson().fromJson(issuesResults, Issues.class);

                String result = "";

                for (int i = 0; i < issues.getIssues().length; i++) {
                    Issue issue = issues.getIssue(i);
                    dbInsertStudent(issue.getSubject(), issue.getProject().getName(),issue.getStatus().getName());
                }

                getAllStudents();

            }
        };
    }

    private BroadcastReceiver createStudentResultBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String resultMsg = intent.getStringExtra(StudentService.EXTRA_CREATE_STUDENT_RESULT);

            Toast.makeText(MainActivity.this, resultMsg, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///// UNCOMMENT ANY ONE OF THE ADAPTERS TO OBSERVE ITS OPERATION //////

        // Custom cursor adapter
        adapter = new StudentCursorAdapter(this);

        // Achieving the same functionality as a custom cursor adapter with a SimpleCursorAdapter
//        adapter = new SimpleCursorAdapter(this, R.layout.item_student, null, FROM_COLUMNS, TO_IDS, 0);
//        ((SimpleCursorAdapter) adapter).setViewBinder(new SimpleCursorAdapter.ViewBinder() {
//
//            // Binds the Cursor column defined by the specified index
//            // to the specified view
//            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
//                if (view.getId() != R.id.cb_presence) {
//                    return false;
//                }
//
//                CheckBox cbPresence = (CheckBox) view;
//                cbPresence.setChecked(cursor.getLong(columnIndex) % 2 == 0);
//                return true;
//            }
//        });

        ListView resultsListView = (ListView) findViewById(R.id.lv_results);
        resultsListView.setAdapter(adapter);

        findViewById(R.id.btn_insert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = ((TextView) findViewById(R.id.txt_first_name)).getText().toString();
                String lastName = ((TextView) findViewById(R.id.txt_last_name)).getText().toString();
                String age = ((TextView) findViewById(R.id.txt_age)).getText().toString();

                insertStudent(firstName, lastName, age);
            }
        });

        findViewById(R.id.btn_query).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               downloadAllStudents();
            }
        });
    }

    private void dbInsertStudent(String firstName, String lastName, String age) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(StudentManagementContract.Student.COLUMN_NAME_FIRST_NAME, firstName);
        values.put(StudentManagementContract.Student.COLUMN_NAME_LAST_NAME, lastName);
        values.put(StudentManagementContract.Student.COLUMN_NAME_AGE, age);

        // Insert the new row, returning the Uri of the new row
        Uri newRowUri;
        newRowUri = getContentResolver().insert(
                StudentManagementContract.Student.CONTENT_URI, // the table to insert to
                values); // all the data to insert

        Toast.makeText(MainActivity.this, "New record inserted - Uri " + newRowUri, Toast.LENGTH_SHORT).show();
    }


    private void getAllStudents() {
        getSupportLoaderManager().initLoader(STUDENTS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case STUDENTS_LOADER:
                return new CursorLoader(this,
                        StudentManagementContract.Student.CONTENT_URI,          // The Uri to query
                        PROJECTION,                                             // The columns to return
                        null,                                                   // The columns for the WHERE clause
                        null,                                                   // The values for the WHERE clause
                        SORT_ORDER                                              // The sort order
                );

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        /**
         * Moves the query results into the adapter, causing the
         * AdapterLayout fronting this adapter to re-display
         */
        adapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /**
         * Clears out the adapter's reference to the Cursor.
         * This prevents memory leaks.
         */
        adapter.changeCursor(null);
    }

    private void downloadAllStudents() {
        Intent intent = new Intent(this, StudentService.class);
        intent.setAction(StudentService.ACTION_GET_STUDENTS);

        startService(intent);
    }

    private void insertStudent(String firstName, String lastName, String age) {
        Intent intent = new Intent(this, StudentService.class);
        intent.setAction(StudentService.ACTION_CREATE_STUDENT);

        intent.putExtra(StudentService.EXTRA_FIRST_NAME, firstName);
        intent.putExtra(StudentService.EXTRA_LAST_NAME, lastName);
        intent.putExtra(StudentService.EXTRA_AGE, age);

        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

        IntentFilter getStudentsResultIntentFilter = new IntentFilter(StudentService.ACTION_GET_STUDENTS_RESULT);
        broadcastManager.registerReceiver(getAllStudentsResultBroadcastReceiver, getStudentsResultIntentFilter);

        IntentFilter createStudentResultIntentFilter = new IntentFilter(StudentService.ACTION_CREATE_STUDENT_RESULT);
        broadcastManager.registerReceiver(createStudentResultBroadcastReceiver, createStudentResultIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

        broadcastManager.unregisterReceiver(getAllStudentsResultBroadcastReceiver);
        broadcastManager.unregisterReceiver(createStudentResultBroadcastReceiver);
    }
}
