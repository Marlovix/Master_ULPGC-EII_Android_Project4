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
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.json.ProductRequest;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.model.Order;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.model.Product;
import es.ulpgc.eii.android.project4.practica4_marlonfernandez.soap.DeleteProduct;

public class ProductFormFragment extends Fragment {
    public static final String TAG = ProductFormFragment.class.getName();
    private static final String PRODUCT_INSTANCE = Product.class.getName();
    private static final String PRODUCTS_ALERT_DIALOG = AlertDialog.class.getSimpleName();
    private static final String PRODUCTS_NAME = "NAME";
    private static final String PRODUCTS_DESCRIPTION = "DESCRIPTION";
    private static final String PRODUCTS_PRICE = "PRICE";

    private String name;
    private String description;
    private float price;
    private EditText editTextName;
    private EditText editTextDescription;
    private EditText editTextPrice;
    private Product product;
    private AlertDialog dialog;
    private String dialogType;
    private ProductRequest productRequest;
    private OrderRequest orderRequest;

    public ProductFormFragment() {
        // Required empty public constructor
    }

    public static ProductFormFragment newInstance(Bundle arguments) {
        ProductFormFragment fragment = new ProductFormFragment();
        if (arguments != null) {
            fragment.setArguments(arguments);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = getArguments().getParcelable(PRODUCT_INSTANCE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_form, container, false);
        setHasOptionsMenu(true);

        TextView textViewName = (TextView) view.findViewById(R.id.nameLabel);
        TextView textViewDescription = (TextView) view.findViewById(R.id.descriptionLabel);
        TextView textViewPrice = (TextView) view.findViewById(R.id.priceLabel);

        String labelName = textViewName.getText().toString();
        labelName = labelName.substring(0, 1).toUpperCase() + labelName.substring(1);
        String labelDescription = textViewDescription.getText().toString();
        labelDescription = labelDescription.substring(0, 1).toUpperCase() + labelDescription.substring(1);
        String labelPrice = textViewPrice.getText().toString();
        labelPrice = labelPrice.substring(0, 1).toUpperCase() + labelPrice.substring(1);

        textViewName.setText(labelName);
        textViewDescription.setText(labelDescription);
        textViewPrice.setText(labelPrice);

        dialogType = "";
        editTextName = (EditText) view.findViewById(R.id.name);
        editTextDescription = (EditText) view.findViewById(R.id.description);
        editTextPrice = (EditText) view.findViewById(R.id.price);

        if (product != null) {
            editTextName.setText(product.getName());
            editTextDescription.setText(product.getDescription());
            editTextPrice.setText(String.valueOf(product.getPrice()));
        }

        productRequest = new ProductRequest(this);
        orderRequest = new OrderRequest(this);
        orderRequest.queryOrdersProductForm();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            dialogType = savedInstanceState.getString(PRODUCTS_ALERT_DIALOG);
            name = savedInstanceState.getString(PRODUCTS_NAME);
            description = savedInstanceState.getString(PRODUCTS_DESCRIPTION);
            price = savedInstanceState.getFloat(PRODUCTS_PRICE);
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
        if (dialog != null) dialog.dismiss();
        if (editTextPrice.getText().toString().equals("")) {
            price = -1f;
        } else {
            price = Float.valueOf(editTextPrice.getText().toString());
        }
        outState.putString(PRODUCTS_ALERT_DIALOG, dialogType);
        outState.putString(PRODUCTS_NAME, editTextName.getText().toString());
        outState.putString(PRODUCTS_DESCRIPTION, editTextDescription.getText().toString());
        outState.putFloat(PRODUCTS_PRICE, price);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_accept, menu);
        if (product != null) {
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
                description = editTextDescription.getText().toString();
                if (editTextPrice.getText().toString().equals("")) {
                    price = -1;
                } else {
                    price = Float.valueOf(editTextPrice.getText().toString());
                }
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

        if (description.equals("")) {
            editTextDescription.setError(getString(R.string.error_empty));
            validForm = false;
        }

        if (price == -1) {
            editTextPrice.setError(getString(R.string.error_empty));
            validForm = false;
        }

        if (validForm) {
            Context context = getContext();
            String title = getString(R.string.products);
            title = title.substring(0, 1).toUpperCase() + title.substring(1);
            String positive = getString(android.R.string.yes);
            String negative = getString(android.R.string.cancel);
            String message;
            if (product == null) {
                message = getString(R.string.create_product_question);
            } else {
                int idProduct = product.getIdProduct();
                message = String.format(
                        getString(R.string.update_product_question), idProduct);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            showSubmitDialog(builder, title, positive, negative, message);
        }
    }

    public void delete() {
        int idProduct = product.getIdProduct();
        if (productInOrder(idProduct)) {
            String message = String.format(
                    getString(R.string.impossible_delete_customer), idProduct);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        } else {
            Context context = getContext();
            String title = getString(R.string.products);
            title = title.substring(0, 1).toUpperCase() + title.substring(1);
            String positive = getString(android.R.string.yes);
            String negative = getString(android.R.string.cancel);
            String message = String.format(getString(R.string.delete_product_question), idProduct);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            showDeleteDialog(builder, idProduct, title, message, positive, negative);
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
                int idProduct;
                if (product == null) {
                    product = new Product(name, description, price);
                    productRequest.insertProduct(product);
                    result = getString(R.string.success_product_created);
                } else {
                    product.setName(name);
                    product.setDescription(description);
                    product.setPrice(price);
                    productRequest.updateProduct(product);
                    idProduct = product.getIdProduct();
                    result = String.format(
                            getString(R.string.success_product_updated), idProduct);
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
        final int idProduct = id;
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String result;

                boolean deleted = false;
                DeleteProduct deleteProduct = new DeleteProduct();
                try {
                    deleted = deleteProduct.execute(product).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                if (deleted) {
                    result = String.format(
                            getString(R.string.success_customer_deleted), idProduct);
                } else {
                    result = String.format(
                            getString(R.string.impossible_delete_customer), idProduct);
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

    private boolean productInOrder(int idProduct) {
        boolean inOrder = false;
        Order[] orders = orderRequest.getOrders();
        for (Order order : orders) {
            Product product = order.getProduct();
            if (product.getIdProduct() == idProduct) {
                inOrder = true;
                break;
            }
        }
        return inOrder;
    }

}