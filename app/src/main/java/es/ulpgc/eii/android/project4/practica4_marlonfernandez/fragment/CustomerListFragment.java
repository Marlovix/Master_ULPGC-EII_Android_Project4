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
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.model.Customer;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.soap.GetCustomers;

public class CustomerListFragment extends Fragment {
    private final static String KEY_ID_CUSTOMER = "_id";
    private final static String KEY_NAME_CUSTOMER = "name";
    private final static String KEY_ADDRESS_CUSTOMER = "address";

    private ListView listView;
    private Customer[] customers;

    public CustomerListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_list, container, false);
        listView = (ListView) view.findViewById(R.id.list_customers);

        // Generate ListView from SQLite Database //
        displayListView();

        return view;
    }

    private void displayListView() {
        Context context = getContext();
        GetCustomers getCustomers = new GetCustomers();
        try {
            customers = getCustomers.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // The desired columns to be bound
        String[] columns = new String[]{
                KEY_ID_CUSTOMER,
                KEY_NAME_CUSTOMER,
                KEY_ADDRESS_CUSTOMER
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[]{
                0,
                R.id.title,
                R.id.subtitle
        };


        MatrixCursor matrixCursor = new MatrixCursor(columns);
        for (Customer c : customers) {
            matrixCursor.addRow(new Object[]{
                    c.getIdCustomer(),
                    c.getName(),
                    c.getAddress()
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
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) adapter.getItemAtPosition(position);

                // Get the state's capital from this row in the database.
                int columnID = cursor.getColumnIndexOrThrow(KEY_ID_CUSTOMER);
                int columnName = cursor.getColumnIndexOrThrow(KEY_NAME_CUSTOMER);
                int columnAddress = cursor.getColumnIndexOrThrow(KEY_ADDRESS_CUSTOMER);
                int idCustomer = cursor.getInt(columnID);
                String name = cursor.getString(columnName);
                String address = cursor.getString(columnAddress);
                Customer customer = new Customer(idCustomer, name, address);

                if (((MainActivity) getActivity()).isSelectViewShown()) {
                    ((MainActivity) getActivity()).setCustomer(customer);
                } else {
                    ((MainActivity) getActivity()).openCustomerForm(customer);
                }
            }
        });

    }

}
