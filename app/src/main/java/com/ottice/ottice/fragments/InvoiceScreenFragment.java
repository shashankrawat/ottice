package com.ottice.ottice.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ottice.ottice.AppController;
import com.ottice.ottice.R;
import com.ottice.ottice.services.ServiceConstants;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.DialogBoxs;
import com.ottice.ottice.utils.PrefrencesClass;
import com.payu.india.Interfaces.OneClickPaymentListener;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Payu.Payu;
import com.payu.india.Payu.PayuConstants;
import com.payu.payuui.Activity.PayUBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

/**
 * TODO: Add a class header comment!
 */

public class InvoiceScreenFragment extends Fragment implements View.OnClickListener, OneClickPaymentListener {

    private Context context;
    private TextView spaceName, bookingType, seatsBooked, date, time, netPrice, serviceTax, sbCess, amount, total, payButton;
    private String spaceNameText, typeText, seatsText, dateText, timeText, netPriceText, serviceTaxText, sbCessText, totalText, transactionId;
    // These will hold all the payment parameters
    private PaymentParams mPaymentParams;
    // This sets the configuration
    private PayuConfig payuConfig;
    private String merchantKey,userCredentials;
    private int env = PayuConstants.MOBILE_STAGING_ENV;
    private DialogBoxs progressDialog;
//    private int env = PayuConstants.PRODUCTION_ENV;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        transactionId = args.getString("transactionID");
        spaceNameText = args.getString("spaceName");
        typeText = args.getString("bookingType");
        seatsText = String.valueOf(args.getInt("seatsBooked"));
        dateText = args.getString("date");
        if(typeText.equalsIgnoreCase("Hourly")) {
            timeText = args.getString("startTime") + getString(R.string.to) + args.getString("checkoutTime");
        }else if(typeText.equalsIgnoreCase("Daily") || typeText.equalsIgnoreCase("Monthly") || typeText.equalsIgnoreCase("Yearly")){
            timeText = args.getString("date") + getString(R.string.to) + args.getString("checkoutTime");
        }
        netPriceText = "Rs "+String.valueOf(args.getLong("netTotal"));
        serviceTaxText = "Rs "+String.valueOf(args.getLong("serviceTax"));
        sbCessText = "Rs "+String.valueOf(args.getLong("sbCess"));
        totalText =  String.valueOf(args.getLong("totalAmount"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if(container == null){
            return null;
        }else {
            view = inflater.inflate(R.layout.invoice_screen, container, false);

            context = getContext();
            init(view);

            return view;
        }
    }

    private void init(View view) {
        //TODO Must write below code in your activity to set up initial context for PayU
        Payu.setInstance(context);

        progressDialog = new DialogBoxs(context);

        spaceName = (TextView) view.findViewById(R.id.summarySpaceName);
        bookingType = (TextView) view.findViewById(R.id.bookingTypeValue);
        seatsBooked = (TextView) view.findViewById(R.id.seatsBookedValue);
        date = (TextView) view.findViewById(R.id.dateValue);
        time = (TextView) view.findViewById(R.id.timeValue);
        netPrice = (TextView) view.findViewById(R.id.netPriceValue);
        serviceTax = (TextView) view.findViewById(R.id.cgstValue);
        sbCess = (TextView) view.findViewById(R.id.sgstValue);
        total = (TextView) view.findViewById(R.id.totalAmount);
        amount = (TextView) view.findViewById(R.id.totalPayableAmount);
        payButton = (TextView) view.findViewById(R.id.payButton);

        setValues();

        payButton.setOnClickListener(this);
    }

    private void setValues() {
        spaceName.setText(spaceNameText);
        bookingType.setText(typeText);
        seatsBooked.setText(seatsText);
        date.setText(dateText);
        time.setText(Html.fromHtml(timeText));
        netPrice.setText(netPriceText);
        serviceTax.setText(serviceTaxText);
        sbCess.setText(sbCessText);
        total.setText("Rs "+totalText);
        amount.setText("Rs "+totalText);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.payButton:
                navigateToPaymentActivity();
                break;
        }
    }



    private void navigateToPaymentActivity() {
        merchantKey = env == PayuConstants.PRODUCTION_ENV ? "0MQaQP" : "gtKFFx";
        String email = PrefrencesClass.getPreferenceValue(getContext(), Common.USER_EMAIL);
        String firstName = PrefrencesClass.getPreferenceValue(getContext(), Common.USER_FIRST_NAME);
        String lastName = PrefrencesClass.getPreferenceValue(getContext(), Common.USER_LAST_NAME);

        userCredentials = merchantKey + ":" + email;

        //TODO Below are mandatory params for hash genetation
        mPaymentParams = new PaymentParams();
        /**
         * For Test Environment, merchantKey = "gtKFFx"
         * For Production Environment, merchantKey should be your live key or for testing in live you can use "0MQaQP"
         */
        mPaymentParams.setKey(merchantKey);
        mPaymentParams.setAmount(totalText);
        mPaymentParams.setProductInfo(spaceNameText);
        mPaymentParams.setFirstName(firstName);
        mPaymentParams.setLastName(lastName);
        mPaymentParams.setEmail(email);
        /*
        * Transaction Id should be kept unique for each transaction.
        * */
        mPaymentParams.setTxnId(transactionId);
        /**
         * Surl --> Success url is where the transaction response is posted by PayU on successful transaction
         * Furl --> Failre url is where the transaction response is posted by PayU on failed transaction
         */
        mPaymentParams.setSurl("https://payu.herokuapp.com/success");
        mPaymentParams.setFurl("https://payu.herokuapp.com/failure");
        /*
         * udf1 to udf5 are options params where you can pass additional information related to transaction.
         * If you don't want to use it, then send them as empty string like, udf1=""
         * */
        mPaymentParams.setUdf1("udf1");
        mPaymentParams.setUdf2("udf2");
        mPaymentParams.setUdf3("udf3");
        mPaymentParams.setUdf4("udf4");
        mPaymentParams.setUdf5("udf5");

        /**
         * These are used for store card feature. If you are not using it then user_credentials = "default"
         * user_credentials takes of the form like user_credentials = "merchant_key : user_id"
         * here merchant_key = your merchant key,
         * user_id = unique id related to user like, email, phone number, etc.
         * */
        mPaymentParams.setUserCredentials(userCredentials);

        //TODO Pass this param only if using offer key
        mPaymentParams.setOfferKey("cardnumber@8370");
        mPaymentParams.setCardBin("1234");
        //TODO Sets the payment environment in PayuConfig object
        payuConfig = new PayuConfig();
        payuConfig.setEnvironment(env);

        //TODO It is recommended to generate hash from server only. Keep your key and salt in server side hash generation code.
        generateHashFromServer(mPaymentParams);
    }




    private void generateHashFromServer(PaymentParams mPaymentParams) {

        payButton.setEnabled(false); // lets not allow the user to click the button again and again.
        // lets create the post params

        StringBuffer postParamsBuffer = new StringBuffer();
//        postParamsBuffer.append(concatParams(PayuConstants.KEY, mPaymentParams.getKey()));
        postParamsBuffer.append(concatParams(PayuConstants.TXNID, mPaymentParams.getTxnId()));
        postParamsBuffer.append(concatParams(PayuConstants.AMOUNT, mPaymentParams.getAmount()));
        postParamsBuffer.append(concatParams("productInfo", mPaymentParams.getProductInfo()));
        postParamsBuffer.append(concatParams(PayuConstants.FIRST_NAME, null == mPaymentParams.getFirstName() ? "" : mPaymentParams.getFirstName()));
        postParamsBuffer.append(concatParams(PayuConstants.EMAIL, null == mPaymentParams.getEmail() ? "" : mPaymentParams.getEmail()));
        postParamsBuffer.append(concatParams(PayuConstants.USER_CREDENTIALS, mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF1, mPaymentParams.getUdf1() == null ? "" : mPaymentParams.getUdf1()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF2, mPaymentParams.getUdf2() == null ? "" : mPaymentParams.getUdf2()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF3, mPaymentParams.getUdf3() == null ? "" : mPaymentParams.getUdf3()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF4, mPaymentParams.getUdf4() == null ? "" : mPaymentParams.getUdf4()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF5, mPaymentParams.getUdf5() == null ? "" : mPaymentParams.getUdf5()));

        // for offer_key
        if(null != mPaymentParams.getOfferKey()) {
            postParamsBuffer.append(concatParams("offerKey", mPaymentParams.getOfferKey()));
        }
        // for check_isDomestic
        if(null != mPaymentParams.getCardBin()) {
            postParamsBuffer.append(concatParams("cardBin", mPaymentParams.getCardBin()));
        }

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();
        Log.e("PAY U URL",""+postParams);

        JSONObject postParamObject = new JSONObject();
        try {
            postParamObject.put("data",postParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        getHashFromServer(postParamObject, ServiceConstants.PAYU_HASH_GENERATION_URL);
    }


    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }


    private void getHashFromServer(JSONObject requestBody, String url)
    {
        Log.e("REQUEST BODY",""+requestBody);
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        progressDialog.showProgressBarDialog();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        getResponseValue(response);
                        payButton.setEnabled(true);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY ERROR",""+error.getMessage());
                progressDialog.hideProgressBar();
                payButton.setEnabled(true);
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }


    private void getResponseValue(JSONObject response) {
        Log.e("RESPONSE",""+response);
        PayuHashes payuHashes = new PayuHashes();
        try {
            Iterator<String> payuHashIterator = response.keys();
            while(payuHashIterator.hasNext()){
                String key = payuHashIterator.next();
                Log.e("KEY ",""+key);
                String value = response.getString(key).replaceAll("^\"|\"$", "");
                Log.e("VALUE",""+value);
                switch (key){
                    case "payment_hash":
                        payuHashes.setPaymentHash(value);
                        break;
                    case "get_merchant_ibibo_codes_hash": //
                        payuHashes.setMerchantIbiboCodesHash(value);
                        break;
                    case "vas_for_mobile_sdk_hash":
                        payuHashes.setVasForMobileSdkHash(value);
                        break;
                    case "payment_related_details_for_mobile_sdk_hash":
                        payuHashes.setPaymentRelatedDetailsForMobileSdkHash(value);
                        break;
                    case "delete_user_card_hash":
                        payuHashes.setDeleteCardHash(value);
                        break;
                    case "get_user_cards_hash":
                        payuHashes.setStoredCardsHash(value);
                        break;
                    case "edit_user_card_hash":
                        payuHashes.setEditCardHash(value);
                        break;
                    case "save_user_card_hash":
                        payuHashes.setSaveCardHash(value);
                        break;
                    case "check_offer_status_hash":
                        payuHashes.setCheckOfferStatusHash(value);
                        break;
                    case "check_isDomestic_hash":
                        payuHashes.setCheckIsDomesticHash(value);
                        break;
                    default:
                        break;
                }
            }

            launchSdkUI(payuHashes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void launchSdkUI(PayuHashes payuHashes) {

        Intent intent = new Intent(context, PayUBaseActivity.class);
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);

        //Lets fetch all the one click card tokens first
        fetchMerchantHashes(intent);

//        startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            if (data != null) {

                /**
                 * Here, data.getStringExtra("payu_response") ---> Implicit response sent by PayU
                 * data.getStringExtra("result") ---> Response received from merchant's Surl/Furl
                 *
                 * PayU sends the same response to merchant server and in app. In response check the value of key "status"
                 * for identifying status of transaction. There are two possible status like, success or failure
                 * */
            Log.e("Payu's Data : " ,""+ data.getStringExtra("payu_response") + "\n\n\n Merchant's Data: " + data.getStringExtra("result"));

                Intent args = new Intent();
                args.putExtra("payu_response_data", data.getStringExtra("payu_response"));
                getActivity().setResult(Activity.RESULT_OK, args);
                getActivity().finish();

            } else {
                Snackbar.make(payButton, getString(R.string.booking_cancelled), Snackbar.LENGTH_LONG).show();
            }
        }
    }




    //TODO This method is used only if integrating One Tap Payments

    /**
     * This method fetches merchantHash and cardToken already stored on merchant server.
     */
    private void fetchMerchantHashes(final Intent intent) {
        // now make the api call.
        final String postParams = "merchant_key=" + merchantKey + "&user_credentials=" + userCredentials;
        final Intent baseActivityIntent = intent;
        new AsyncTask<Void, Void, HashMap<String, String>>() {

            @Override
            protected HashMap<String, String> doInBackground(Void... params) {
                try {
                    //TODO Replace below url with your server side file url.
                    URL url = new URL("https://payu.herokuapp.com/get_merchant_hashes");

                    byte[] postParamsByte = postParams.getBytes("UTF-8");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postParamsByte);

                    InputStream responseInputStream = conn.getInputStream();
                    StringBuffer responseStringBuffer = new StringBuffer();
                    byte[] byteContainer = new byte[1024];
                    for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                        responseStringBuffer.append(new String(byteContainer, 0, i));
                    }

                    JSONObject response = new JSONObject(responseStringBuffer.toString());
                    Log.e("FETCHED DATA",response.toString());

                    HashMap<String, String> cardTokens = new HashMap<String, String>();
                    JSONArray oneClickCardsArray = response.getJSONArray("data");
                    int arrayLength = oneClickCardsArray.length();
                    if (arrayLength > 0) {
                        for (int i = 0; i < arrayLength; i++) {
                            cardTokens.put(oneClickCardsArray.getJSONArray(i).getString(0), oneClickCardsArray.getJSONArray(i).getString(1));
                        }
                        return cardTokens;
                    }
                    // pass these to next activity

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(HashMap<String, String> oneClickTokens) {
                super.onPostExecute(oneClickTokens);
                progressDialog.hideProgressBar();
                Log.e("one click Token",""+oneClickTokens);
                baseActivityIntent.putExtra(PayuConstants.ONE_CLICK_CARD_TOKENS, oneClickTokens);
                startActivityForResult(baseActivityIntent, PayuConstants.PAYU_REQUEST_CODE);
                payButton.setEnabled(true);
            }
        }.execute();
    }

    @Override
    public HashMap<String, String> getAllOneClickHash(String userCreds) {
        // 1. GET http request from your server
        // GET params - merchant_key, user_credentials.
        // 2. In response we get a
        // this is a sample code for fetching one click hash from merchant server.
        return getAllOneClickHashHelper(merchantKey, userCreds);
    }

    @Override
    public void getOneClickHash(String cardToken, String merchantKey, String userCredentials) {

    }


    //TODO This method is used only if integrating One Tap Payments

    /**
     * This method will be called as a async task, regardless of merchant implementation.
     * Hence, not to call this function as async task.
     * This function save the oneClickHash corresponding to its cardToken
     *
     * @param cardToken    a string containing the card token
     * @param oneClickHash a string containing the one click hash.
     **/
    @Override
    public void saveOneClickHash(String cardToken, String oneClickHash) {
        // 1. POST http request to your server
        // POST params - merchant_key, user_credentials,card_token,merchant_hash.
        // 2. In this POST method the oneclickhash is stored corresponding to card token in merchant server.
        // this is a sample code for storing one click hash on merchant server.

        storeMerchantHash(cardToken, oneClickHash);
    }


    //TODO This method is used only if integrating One Tap Payments

    /**
     * This method will be called as a async task, regardless of merchant implementation.
     * Hence, not to call this function as async task.
     * This function delete’s the oneClickHash from the merchant server
     *
     * @param cardToken       a string containing the card token
     * @param userCredentials a string containing the user credentials.
     **/
    @Override
    public void deleteOneClickHash(String cardToken, String userCredentials) {

        // 1. POST http request to your server
        // POST params  - merchant_hash.
        // 2. In this POST method the oneclickhash is deleted in merchant server.
        // this is a sample code for deleting one click hash from merchant server.

        deleteMerchantHash(cardToken);
    }



    //TODO This method is used only if integrating One Tap Payments

    /**
     * This method prepares a HashMap of cardToken as key and merchantHash as value.
     *
     * @param merchantKey     merchant key used
     * @param userCredentials unique credentials of the user usually of the form key:userId
     */
    public HashMap<String, String> getAllOneClickHashHelper(String merchantKey, String userCredentials) {

        // now make the api call.
        final String postParams = "merchant_key=" + merchantKey + "&user_credentials=" + userCredentials;
        HashMap<String, String> cardTokens = new HashMap<String, String>();

        try {
            //TODO Replace below url with your server side file url.
            URL url = new URL("https://payu.herokuapp.com/get_merchant_hashes");

            byte[] postParamsByte = postParams.getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postParamsByte);

            InputStream responseInputStream = conn.getInputStream();
            StringBuffer responseStringBuffer = new StringBuffer();
            byte[] byteContainer = new byte[1024];
            for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                responseStringBuffer.append(new String(byteContainer, 0, i));
            }

            JSONObject response = new JSONObject(responseStringBuffer.toString());

            JSONArray oneClickCardsArray = response.getJSONArray("data");
            int arrayLength;
            if ((arrayLength = oneClickCardsArray.length()) >= 1) {
                for (int i = 0; i < arrayLength; i++) {
                    cardTokens.put(oneClickCardsArray.getJSONArray(i).getString(0), oneClickCardsArray.getJSONArray(i).getString(1));
                }

            }
            // pass these to next activity

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cardTokens;
    }




    //TODO This method is used if integrating One Tap Payments

    /**
     * This method stores merchantHash and cardToken on merchant server.
     *
     * @param cardToken    card token received in transaction response
     * @param merchantHash merchantHash received in transaction response
     */
    private void storeMerchantHash(String cardToken, String merchantHash) {

        final String postParams = "merchant_key=" + merchantKey + "&user_credentials=" + userCredentials + "&card_token=" + cardToken + "&merchant_hash=" + merchantHash;

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {

                    //TODO Deploy a file on your server for storing cardToken and merchantHash nad replace below url with your server side file url.
                    URL url = new URL("https://payu.herokuapp.com/store_merchant_hash");

                    byte[] postParamsByte = postParams.getBytes("UTF-8");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postParamsByte);

                    InputStream responseInputStream = conn.getInputStream();
                    StringBuffer responseStringBuffer = new StringBuffer();
                    byte[] byteContainer = new byte[1024];
                    for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                        responseStringBuffer.append(new String(byteContainer, 0, i));
                    }

                    JSONObject response = new JSONObject(responseStringBuffer.toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                this.cancel(true);
            }
        }.execute();
    }




    //TODO This method is used only if integrating One Tap Payments

    /**
     * This method deletes merchantHash and cardToken from server side file.
     *
     * @param cardToken cardToken of card whose merchantHash and cardToken needs to be deleted from merchant server
     */
    private void deleteMerchantHash(String cardToken) {

        final String postParams = "card_token=" + cardToken;

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //TODO Replace below url with your server side file url.
                    URL url = new URL("https://payu.herokuapp.com/delete_merchant_hash");

                    byte[] postParamsByte = postParams.getBytes("UTF-8");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postParamsByte);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                this.cancel(true);
            }
        }.execute();
    }
}
