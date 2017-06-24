package es.ulpgc.eii.android.project4.practica4_marlonfernandez.soap;

import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import es.ulpgc.eii.android.project4.practica4_marlonfernandez.model.Product;

public class GetProducts extends AsyncTask<Void, ArrayList, Product[]> {
    private Product[] products;

    @Override
    protected Product[] doInBackground(Void[] params) {

        String METHOD = "QueryProducts";
        String NAMESPACE = "urn://ulpgc.masterii.moviles";
        SoapObject request = new SoapObject(NAMESPACE, METHOD);

        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
        HttpTransportSE ht = getHttpTransportSE();
        try {
            String SOAP_ACTION = "urn://ulpgc.masterii.moviles/QueryProducts";
            ht.call(SOAP_ACTION, envelope);
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        Vector result = null;
        try {
            result = (Vector) envelope.getResponse();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        }


        if (result != null) {
            products = new Product[result.size()];
            for (int i = 0; i < result.size(); i++) {

                SoapObject object = (SoapObject) result.get(i);

                SoapObject idObject = (SoapObject) object.getProperty(0);
                int idProduct = Integer.valueOf(idObject.getProperty(1).toString());

                SoapObject nameObject = (SoapObject) object.getProperty(1);
                String nameProduct = nameObject.getProperty(1).toString();

                SoapObject priceObject = (SoapObject) object.getProperty(2);
                float priceCustomer = Float.valueOf(priceObject.getProperty(1).toString());

                SoapObject descriptionObject = (SoapObject) object.getProperty(3);
                String descriptionCustomer = descriptionObject.getProperty(1).toString();

                Product product =
                        new Product(idProduct, nameProduct, descriptionCustomer, priceCustomer);
                products[i] = product;

            }
        }
        return products;
    }

    private SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
        envelope.setOutputSoapObject(request);

        return envelope;
    }

    private HttpTransportSE getHttpTransportSE() {
        String URL = "http://tip.dis.ulpgc.es/ventas/server.php";
        HttpTransportSE ht = new HttpTransportSE(URL);
        ht.debug = true;
        ht.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");
        return ht;
    }
}