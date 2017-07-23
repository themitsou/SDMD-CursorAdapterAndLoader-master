package gr.academic.city.sdmd.cursoradapterandloader;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import gr.academic.city.sdmd.cursoradapterandloader.db.StudentManagementContract;

/**
 * Created by trumpets on 3/14/16.
 */
public class StudentCursorAdapter extends CursorAdapter {

    public StudentCursorAdapter(Context context) {
        super(context, null, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.item_student, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView studentFirstName = (TextView) view.findViewById(R.id.tv_first_name);
        TextView studentLastName = (TextView) view.findViewById(R.id.tv_last_name);
        TextView studentAge = (TextView) view.findViewById(R.id.tv_age);
        CheckBox presenceCheckbox = (CheckBox) view.findViewById(R.id.cb_presence);

        studentFirstName.setText(cursor.getString(
                cursor.getColumnIndexOrThrow(StudentManagementContract.Student.COLUMN_NAME_FIRST_NAME)));

        studentLastName.setText(cursor.getString(
                cursor.getColumnIndexOrThrow(StudentManagementContract.Student.COLUMN_NAME_LAST_NAME)));

        studentAge.setText(String.valueOf(
                cursor.getInt(cursor.getColumnIndexOrThrow(StudentManagementContract.Student.COLUMN_NAME_AGE))));

        presenceCheckbox.setChecked(
                cursor.getInt(cursor.getColumnIndexOrThrow(StudentManagementContract.Student.COLUMN_NAME_AGE)) > 18);
    }
}
