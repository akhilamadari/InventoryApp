package com.example.andriod.inventoryapp;


import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.Random;
import com.example.andriod.inventoryapp.data.InventoryContract.InventoryEntry;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {


        private static final String TAG = MainActivity.class.getSimpleName();

        private static final int INVENTORY_LOADER = 0;

        public final String[] PRODUCT_COLS = {
                InventoryEntry._ID,
                InventoryEntry.COL_NAME,
                InventoryEntry.COL_QUANTITY,
                InventoryEntry.COL_PRICE,
                InventoryEntry.COL_DESCRIPTION,
                InventoryEntry.COL_ITEMS_SOLD,
                InventoryEntry.COL_SUPPLIER,
                InventoryEntry.COL_PICTURE
        };

        private InventoryCursorAdapter mCursorAdapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, InventoryEditor.class);
                    startActivity(intent);
                }
            });


            ListView inventoryListView = (ListView) findViewById(R.id.list);

            View emptyView = findViewById(R.id.empty_view);
            inventoryListView.setEmptyView(emptyView);

            mCursorAdapter = new InventoryCursorAdapter(this, null);
            inventoryListView.setAdapter(mCursorAdapter);


            inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, InventoryEditor.class);

                    Uri currentProductUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                    intent.setData(currentProductUri);
                    startActivity(intent);

                }
            });


            getLoaderManager().initLoader(INVENTORY_LOADER, null, this);

        }


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            return new CursorLoader(this,
                    InventoryEntry.CONTENT_URI,
                    PRODUCT_COLS,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mCursorAdapter.swapCursor(data);

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mCursorAdapter.swapCursor(null);
        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_inventory, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            switch (item.getItemId()) {

                case R.id.action_insert_dummy_data:
                    insertNewRandomData();
                    return true;

                case R.id.action_delete_all_entries:
                    deleteAllProducts();
                    return true;


            }
            return super.onOptionsItemSelected(item);
        }


    /**
     * Helper method to delete all inventories in the database.
     */
        private void deleteAllProducts() {
            int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
            Log.v("CatalogActivity", rowsDeleted + " rows deleted from products database");
        }


        private void insertNewRandomData() {

            Random r = new Random();
            String productName = "Item_" + r.nextInt(40000 - 100);
            int quantity = r.nextInt(40 - 10);
            float price = r.nextInt(200 - 10);

            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COL_NAME, productName);
            values.put(InventoryEntry.COL_QUANTITY, quantity);
            values.put(InventoryEntry.COL_PRICE, price);

            Uri uri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

        }
    }





