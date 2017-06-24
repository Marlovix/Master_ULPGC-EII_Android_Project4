package es.ulpgc.eii.android.project4.practica4_marlonfernandez.fragment;

import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import es.ulpgc.eii.android.project4.practica4_marlonfernandez.MainActivity;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.R;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.json.OrderRequest;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.model.Order;

public class OrderListFragment extends Fragment {
    private final static String KEY_ID_ORDER = "_id";
    private final static String KEY_NAME_CUSTOMER_ORDER = "customer_name";
    private final static String KEY_NAME_PRODUCT_ORDER = "product_name";
    private final static String KEY_CODE_ORDER = "code";

    private ListView listView;
    private OrderRequest orderRequest;

    public OrderListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        listView = (ListView) view.findViewById(R.id.list_products);

        orderRequest = new OrderRequest(this);

        // Generate ListView from SQLite Database //
        orderRequest.queryOrders();

        return view;
    }

    public void setOrders(Order[] orders) {
        // The desired columns to be bound
        String[] columns = new String[]{
                KEY_ID_ORDER,
                KEY_NAME_CUSTOMER_ORDER,
                KEY_NAME_PRODUCT_ORDER,
                KEY_CODE_ORDER
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[]{
                0,
                R.id.title,
                R.id.subtitle,
                R.id.code
        };

        MatrixCursor matrixCursor = new MatrixCursor(columns);
        for (Order o : orders) {
            matrixCursor.addRow(new Object[]{
                    o.getIdOrder(),
                    o.getCustomer().getName(),
                    o.getProduct().getName(),
                    o.getCode()
            });
        }

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(
                listView.getContext(), R.layout.item_list, matrixCursor, columns, to, 0);

        // Assign adapter to ListView
        dataAdapter.notifyDataSetChanged();
        listView.setAdapter(dataAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set

                Order[] orders = orderRequest.getOrders();
                Order order = orders[position];
                ((MainActivity) getActivity()).openOrderForm(order);
            }
        });
    }

}
