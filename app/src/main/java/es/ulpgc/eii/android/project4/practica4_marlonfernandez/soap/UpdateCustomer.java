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

import es.ulpgc.eii.android.project4.practica4_marlonfernandez.model.Customer;

public class UpdateCustomer extends AsyncTask<Customer, ArrayList, Boolean> {

    private final static String KEY_ID_CUSTOMER = "_id";
    private final static String KEY_NAME_CUSTOMER = "name";
    private final static String KEY_ADDRESS_CUSTOMER = "address";

    @Override
    protected Boolean doInBackground(Customer... params) {
        final String METHOD = "updateCustomer";
        String NAMESPACE = "urn://ulpgc.masterii.moviles";
        Customer customer = params[0];
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty(KEY_ID_CUSTOMER, customer.getIdCustomer());
        request.addProperty(KEY_NAME_CUSTOMER, customer.getName());
        request.addProperty(KEY_ADDRESS_CUSTOMER, customer.getAddress());

        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
        HttpTransportSE ht = getHttpTransportSE();
        try {
            String SOAP_ACTION = "urn://ulpgc.masterii.moviles/updateCustomer";
            ht.call(SOAP_ACTION, envelope);
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        Boolean result = false;
        try {
            result = (Boolean) envelope.getResponse();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        }

        return result;
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
        final String URL = "http://tip.dis.ulpgc.es/ventas/server.php";
        HttpTransportSE ht = new HttpTransportSE(URL);
        ht.debug = true;
        ht.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");
        return ht;
    }
}
