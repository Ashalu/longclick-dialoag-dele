package com.developer.aashish.longclick;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import static com.developer.aashish.longclick.R.id.popup;

public class MainActivity extends Activity implements View.OnClickListener

 {

    EditText mText;
    Button mAdd;
    ListView mList;
int a;
    DataBasehelper mHelper;
    SQLiteDatabase mDb;
    Cursor mCursor;
    SimpleCursorAdapter mAdapter;
    Point p;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mText = (EditText) findViewById(R.id.ed1);
        mAdd = (Button) findViewById(R.id.add);
        mAdd.setOnClickListener(this);
        mList = (ListView) findViewById(R.id.list);

//
        mHelper = new DataBasehelper(this);
        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
 a=position;

                if (p != null) {
                    showPopup(MainActivity.this, p);


                }
                return false;
            }

        });
    }


    public void onWindowFocusChanged(boolean hasFocus)

    {

        int[] location = new int[2];
//             = (Button) findViewById(R.id.popup);

        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.
        mList.getLocationOnScreen(location);

        //Initialize the Point with x, and y positions
        p = new Point();
        p.x = location[0];
        p.y = location[1];
//        }
    }

    // The method that displays the popup.

    private void showPopup(final MainActivity context, Point p) {
        int popupWidth = 700;
        int popupHeight = 300;

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);

        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        int OFFSET_X = 30;
        int OFFSET_Y = 30;

        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());



        Button del = (Button)layout.findViewById(R.id.no);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();

            }
        });




        Button sur = (Button)layout.findViewById(R.id.yes);
        sur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(a);
           //     Toast.makeText(MainActivity.this, ""+a, Toast.LENGTH_SHORT).show();
        String rowId = mCursor.getString(0);
        mDb.delete(DataBasehelper.TABLE_NAME, "_id = ?", new String[]{rowId});
        mCursor.requery();
        mAdapter.notifyDataSetChanged();
                popup.dismiss();
//            }

            }
        });








        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

//        Button del = (Button) findViewById(R.id.no);
//        del.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popup.dismiss();
//            }
//        });
//        mAdd.setOnClickListener(new View.OnClickListener()
//
//        {
//            @Override
//            public void onClick(View v) {
//                ContentValues cv = new ContentValues(2);
//                cv.put(DataBasehelper.COL_NAME, mText.getText().toString());
//
//                mDb.insert(DataBasehelper.TABLE_NAME, null, cv);
//                mCursor.requery();
//                mAdapter.notifyDataSetChanged();
//                mText.setText(null);
//            }
//        });

//
//        del.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String rowId = mText.getText().toString();
//                mDb.delete(DataBasehelper.TABLE_NAME, "_id = ?", new String[]{rowId});
//                mCursor.requery();
//                mAdapter.notifyDataSetChanged();
//            }
//        });


    }
    @Override
    public void onResume() {
        super.onResume();
        mDb = mHelper.getWritableDatabase();
        String[] columns = new String[]{"_id", DataBasehelper.COL_NAME};
        mCursor = mDb.query(DataBasehelper.TABLE_NAME, columns, null, null, null, null, null, null);
        String[] headers = new String[]{DataBasehelper.COL_NAME};
        mAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                mCursor, headers, new int[]{android.R.id.text1});
        mList.setAdapter(mAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        mDb.close();
        mCursor.close();
    }

    @Override
    public void onClick(View v) {
        ContentValues cv = new ContentValues(2);
        cv.put(DataBasehelper.COL_NAME, mText.getText().toString());

        mDb.insert(DataBasehelper.TABLE_NAME, null, cv);
        mCursor.requery();
        mAdapter.notifyDataSetChanged();
        mText.setText(null);
    }
}
