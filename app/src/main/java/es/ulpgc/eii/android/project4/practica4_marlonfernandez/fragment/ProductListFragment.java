package es.ulpgc.eii.android.project4.practica4_marlonfernandez.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.concurrent.ExecutionException;

import es.ulpgc.eii.android.project4.practica4_marlonfernandez.MainActivity;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.R;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.model.Product;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.soap.GetProducts;

public class ProductListFragment extends Fragment {
    private final static String KEY_ID_PRODUCT = "_id";
    private final static String KEY_NAME_PRODUCT = "name";
    private final static String KEY_DESCRIPTION_PRODUCT = "description";
    private final static String KEY_PRICE_PRODUCT = "price";

    private ListView listView;
    private Product[] products;

    public ProductListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        listView = (ListView) view.findViewById(R.id.list_products);

        // Generate ListView from SQLite Database //
        displayListView();

        return view;
    }

    private void displayListView() {
        Context context = getContext();
        GetProducts getProducts = new GetProducts();
        try {
            products = getProducts.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // The desired columns to be bound
        String[] columns = new String[]{
                KEY_ID_PRODUCT,
                KEY_NAME_PRODUCT,
                KEY_DESCRIPTION_PRODUCT,
                KEY_PRICE_PRODUCT
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[]{
                0,
                R.id.title,
                R.id.subtitle
        };

        MatrixCursor matrixCursor = new MatrixCursor(columns);
        for (Product p : products) {
            matrixCursor.addRow(new Object[]{
                    p.getIdProduct(),
                    p.getName(),
                    p.getDescription(),
                    p.getPrice()
            });
        }

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        SimpleCursorAdapter dataAdapter;
        if (((MainActivity) getActivity()).isSelectViewShown()) {
            dataAdapter = new SimpleCursorAdapter(context,
                    R.layout.item_select_list, matrixCursor, columns, to, 0);
        } else {
            dataAdapter = new SimpleCursorAdapter(
                    context, R.layout.item_list, matrixCursor, columns, to, 0);
        }

        // Assign adapter to ListView
        dataAdapter.notifyDataSetChanged();
        listView.setAdapter(dataAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the state's capital from this row in the database.
                int columnID = cursor.getColumnIndexOrThrow(KEY_ID_PRODUCT);
                int columnName = cursor.getColumnIndexOrThrow(KEY_NAME_PRODUCT);
                int columnDescription = cursor.getColumnIndexOrThrow(KEY_DESCRIPTION_PRODUCT);
                int columnPrice = cursor.getColumnIndexOrThrow(KEY_PRICE_PRODUCT);
                int idProduct = cursor.getInt(columnID);
                float price = cursor.getFloat(columnPrice);
                String name = cursor.getString(columnName);
                String description = cursor.getString(columnDescription);
                Product product = new Product(idProduct, name, description, price);

                if (((MainActivity) getActivity()).isSelectViewShown()) {
                    ((MainActivity) getActivity()).setProduct(product);
                } else {
                    ((MainActivity) getActivity()).openProductForm(product);
                }
            }
        });
    }

}
