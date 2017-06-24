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

import es.ulpgc.eii.android.project4.practica4_marlonfernandez.model.Customer;

public class GetCustomers extends AsyncTask<Void, ArrayList, Customer[]> {
    private Customer[] customers;

    @Override
    protected Customer[] doInBackground(Void[] params) {

        String METHOD = "QueryCustomers";
        String NAMESPACE = "urn://ulpgc.masterii.moviles";
        SoapObject request = new SoapObject(NAMESPACE, METHOD);

        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
        HttpTransportSE ht = getHttpTransportSE();
        try {
            String SOAP_ACTION = "urn://ulpgc.masterii.moviles/QueryCustomers";
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
            customers = new Customer[result.size()];
            for (int i = 0; i < result.size(); i++) {

                SoapObject object = (SoapObject) result.get(i);

                SoapObject idObject = (SoapObject) object.getProperty(0);
                int idCustomer = Integer.valueOf(idObject.getProperty(1).toString());

                SoapObject nameObject = (SoapObject) object.getProperty(1);
                String nameCustomer = nameObject.getProperty(1).toString();

                SoapObject addressObject = (SoapObject) object.getProperty(2);
                String addressCustomer = addressObject.getProperty(1).toString();

                Customer customer = new Customer(idCustomer, nameCustomer, addressCustomer);
                customers[i] = customer;

            }
        }
        return customers;
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