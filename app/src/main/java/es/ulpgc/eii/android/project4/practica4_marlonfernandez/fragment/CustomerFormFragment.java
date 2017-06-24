package es.ulpgc.eii.android.project4.practica4_marlonfernandez.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import es.ulpgc.eii.android.project4.practica4_marlonfernandez.FormActivity;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.R;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.json.OrderRequest;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.model.Customer;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.model.Order;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.soap.DeleteCustomer;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.soap.NewCustomer;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.soap.UpdateCustomer;

public class CustomerFormFragment extends Fragment {
    public static final String TAG = CustomerFormFragment.class.getName();
    private static final String CUSTOMER_INSTANCE = Customer.class.getName();
    private static final String CUSTOMERS_ALERT_DIALOG = AlertDialog.class.getSimpleName();
    private static final String CUSTOMERS_NAME = "NAME";
    private static final String CUSTOMERS_ADDRESS = "ADDRESS";

    private String name;
    private String address;
    private EditText editTextName;
    private EditText editTextAddress;
    private Customer customer;
    private AlertDialog dialog;
    private String dialogType;
    private OrderRequest orderRequest;

    public CustomerFormFragment() {
        // Required empty public constructor
    }

    public static CustomerFormFragment newInstance(Bundle arguments) {
        CustomerFormFragment fragment = new CustomerFormFragment();
        if (arguments != null) {
            fragment.setArguments(arguments);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            customer = getArguments().getParcelable(CUSTOMER_INSTANCE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_form, container, false);
        setHasOptionsMenu(true);

        TextView textViewName = (TextView) view.findViewById(R.id.nameLabel);
        TextView textViewAddress = (TextView) view.findViewById(R.id.addressLabel);

        String labelName = textViewName.getText().toString();
        labelName = labelName.substring(0, 1).toUpperCase() + labelName.substring(1);
        String labelAddress = textViewAddress.getText().toString();
        labelAddress = labelAddress.substring(0, 1).toUpperCase() + labelAddress.substring(1);

        textViewName.setText(labelName);
        textViewAddress.setText(labelAddress);

        dialogType = "";
        editTextName = (EditText) view.findViewById(R.id.name);
        editTextAddress = (EditText) view.findViewById(R.id.address);

        if (customer != null) {
            editTextName.setText(customer.getName());
            editTextAddress.setText(customer.getAddress());
        }

        orderRequest = new OrderRequest(this);
        orderRequest.queryOrdersCustomerForm();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            dialogType = savedInstanceState.getString(CUSTOMERS_ALERT_DIALOG);
            name = savedInstanceState.getString(CUSTOMERS_NAME);
            address = savedInstanceState.getString(CUSTOMERS_ADDRESS);
            if (dialogType != null && !dialogType.equals("")) {
                switch (dialogType) {
                    case "SUBMIT":
                        submit();
                        break;
                    case "DELETE":
                        delete();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dialog != null) dialog.dismiss();
        outState.putString(CUSTOMERS_ALERT_DIALOG, dialogType);
        outState.putString(CUSTOMERS_NAME, editTextName.getText().toString());
        outState.putString(CUSTOMERS_ADDRESS, editTextAddress.getText().toString());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_accept, menu);
        if (customer != null) {
            inflater.inflate(R.menu.menu_discard, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_accept:
                name = editTextName.getText().toString();
                address = editTextAddress.getText().toString();
                submit();
                return true;
            case R.id.action_discard:
                delete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void submit() {
        boolean validForm = true;

        if (name.equals("")) {
            editTextName.setError(getString(R.string.error_empty));
            validForm = false;
        }

        if (address.equals("")) {
            editTextAddress.setError(getString(R.string.error_empty));
            validForm = false;
        }

        if (validForm) {
            Context context = getContext();

            String title = getString(R.string.customers);
            title = title.substring(0, 1).toUpperCase() + title.substring(1);
            String positive = getString(android.R.string.yes);
            String negative = getString(android.R.string.cancel);
            String message;
            if (customer == null) {
                message = getString(R.string.create_customer_question);
            } else {
                int idCustomer = customer.getIdCustomer();
                message = String.format(
                        getString(R.string.update_customer_question), idCustomer);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            showSubmitDialog(builder, title, positive, negative, message);
        }
    }

    public void delete() {
        int idCustomer = customer.getIdCustomer();
        if (customerInOrder(idCustomer)) {
            String message = String.format(
                    getString(R.string.impossible_delete_customer), idCustomer);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        } else {
            Context context = getContext();
            String title = getString(R.string.customers);
            title = title.substring(0, 1).toUpperCase() + title.substring(1);
            String positive = getString(android.R.string.yes);
            String negative = getString(android.R.string.cancel);
            String message = String.format(getString(R.string.delete_customer_question), idCustomer);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            showDeleteDialog(builder, idCustomer, title, message, positive, negative);
        }
    }

    private void showSubmitDialog(AlertDialog.Builder builder,
                                  String title, String positive, String negative, String message) {
        dialogType = "SUBMIT";
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                String result;
                int idCustomer;
                if (customer == null) {
                    customer = new Customer(name, address);
                    NewCustomer newCustomer = new NewCustomer();
                    newCustomer.execute(customer);
                    result = getString(R.string.success_customer_created);
                } else {
                    customer.setName(name);
                    customer.setAddress(address);
                    idCustomer = customer.getIdCustomer();
                    UpdateCustomer updateCustomer = new UpdateCustomer();
                    updateCustomer.execute(customer);
                    result = String.format(
                            getString(R.string.success_customer_updated), idCustomer);
                }

                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                ((FormActivity) getActivity()).openListView();
            }
        });
        builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) dialog.dismiss();
                dialogType = "";
            }
        });
        builder.setCancelable(false); // Avoid close the alert with back button of the device //
        if (dialog == null) dialog = builder.show();
        else if (!dialog.isShowing()) dialog = builder.show();
    }

    private void showDeleteDialog(AlertDialog.Builder builder, int id,
                                  String title, String message, String positive, String negative) {
        dialogType = "DELETE";
        final int idCustomer = id;
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String result;

                boolean deleted = false;
                DeleteCustomer deleteCustomer = new DeleteCustomer();
                try {
                    deleted = deleteCustomer.execute(customer).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                if (deleted) {
                    result = String.format(
                            getString(R.string.success_customer_deleted), idCustomer);
                } else {
                    result = String.format(
                            getString(R.string.impossible_delete_customer), idCustomer);
                }

                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                ((FormActivity) getActivity()).openListView();
            }
        });
        builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) dialog.dismiss();
                dialogType = "";
            }
        });
        builder.setCancelable(false); // Avoid close the alert with back button of the device //
        if (dialog == null) dialog = builder.show();
        else if (!dialog.isShowing()) dialog = builder.show();
    }

    private boolean customerInOrder(int idCustomer) {
        boolean inOrder = false;
        Order[] orders = orderRequest.getOrders();
        for (Order order : orders) {
            Customer customer = order.getCustomer();
            if (customer.getIdCustomer() == idCustomer) {
                inOrder = true;
                break;
            }
        }
        return inOrder;
    }

}