
/*
 * Copyright (c) 2015 github.com/4-k
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.fourk.app.sos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Home extends Activity {
    private static boolean SHAKE_ALERTS_SENT = false;

    private GoogleSignInClient mGoogleSignInClient;
    private SensorManager mSensorManager;

    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));

            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;

            if (mAccel > 12) {
                try {
                    List<String> gpsData = getAddress();
                    if (!SHAKE_ALERTS_SENT) {
                        Toast.makeText(getApplicationContext(), "Shake event detected, alerting contacts.", Toast.LENGTH_SHORT).show();
                        SendTextMessage(gpsData.get(0), gpsData.get(1));
                        SHAKE_ALERTS_SENT = true;
                        mAccel = 10f;
                    } else {
                        Toast.makeText(getApplicationContext(), "Shake event detected, contacts already alerted.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "Error:" + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_home);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(
                mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
        );

        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        findViewById(R.id.cnt).setOnClickListener(v -> {
            Intent i = new Intent(getBaseContext(), Acs.class);
            startActivity(i);
        });

        findViewById(R.id.call_police).setOnClickListener(v -> {
            callPolice();
        });

        findViewById(R.id.loc).setOnClickListener(v -> {

        });

        findViewById(R.id.log_out).setOnClickListener(v -> {
            logOut();
        });

        findViewById(R.id.scream).setOnClickListener(v -> {
            try {
                List<String> gpsData = getAddress();
                SendTextMessage(gpsData.get(0), gpsData.get(1));
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "Error:" + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private List<String> getAddress() throws IOException {
        GPSTracker gps = new GPSTracker(Home.this);
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();

        Geocoder geocoder = new Geocoder(Home.this, Locale.getDefault());
        String url = "http://maps.google.com/maps?q=" + latitude + "," + longitude;

        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
        StringBuilder address = new StringBuilder();

        for (int i = 0; i < addresses.toArray().length; i++) {
            address
                    .append(addresses.get(i).getAddressLine(0)).append(",")
                    .append(addresses.get(i).getAddressLine(1)).append(",")
                    .append(addresses.get(i).getAddressLine(2));
        }

        return Arrays.asList(address.toString(), url);
    }

    private void SendEmail(String address, String uRL) {
        try {
            ContactRepo repo = new ContactRepo(getApplicationContext());
            String _uid = repo.getCredentialE();
            final String pass = repo.getCredentialP();
            final String uid = _uid;
            List contacts = repo.getEmContact();
            Iterator iter = contacts.iterator();
            while (iter.hasNext()) {
                String elem = (String) iter.next();
                String cid = elem;
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "465");

                Session session = Session.getDefaultInstance(props,
                        new javax.mail.Authenticator() {
                            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                                return new javax.mail.PasswordAuthentication(uid, pass);
                            }
                        });
                try {
                    String _message = "Hi ! I am in danger. Please find me at " + address + " |" + uRL;
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(_uid));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(cid));
                    message.setSubject("Please Help");
                    message.setText(_message);
                    Transport.send(message);

                } catch (javax.mail.MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception err) {
            Toast.makeText(Home.this, getResources().getString(R.string.gmailResponse), Toast.LENGTH_LONG).show();
        }
    }

    private void SendTextMessage(String address, String uRL) {
        try {
            ContactRepo repo = new ContactRepo(getApplicationContext());
            ArrayList<HashMap<String, String>> contactList = repo.getContactList();
            if (contactList.size() != 0) {
                String list = "";
                boolean err = false;
                String ErrorNo = "Cannot Send to ";
                for (int a = 0; a < contactList.size(); a++) {
                    HashMap<String, String> tmpData = (HashMap<String, String>) contactList.get(a);
                    Set<String> key = tmpData.keySet();
                    Iterator<String> it = key.iterator();
                    String name = "";
                    String phone = "";
                    while (it.hasNext()) {
                        String hmKey = (String) it.next();
                        String hmData = tmpData.get(hmKey);
                        if (hmKey.equals("name")) {
                            name = hmData;
                        }
                        if (hmKey.equals("phone")) {
                            phone = hmData;
                            if (phone.length() == 10) {
                                phone = "0" + phone;
                            }
                        }
                        it.remove();
                    }

                    String message = "Hi " + name + " ! I am in danger. Please find me at " + address + " |" + uRL;

                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        ArrayList<String> msgStringArray = smsManager.divideMessage(message);
                        try {
                            smsManager.sendMultipartTextMessage(phone, null, msgStringArray, null, null);
                        } catch (Exception e) {
                            smsManager.sendTextMessage(phone, null, message, null, null);
                        }
                        Toast.makeText(Home.this, "SMS sent to " + phone, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(Home.this,
                                "SMS failed, please try again. " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    if (err) {
                        Toast.makeText(Home.this,
                                ErrorNo,
                                Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(Home.this,
                        "SMS failed, No contact found ",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception ee) {
            Toast.makeText(Home.this, ee.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void callPolice() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:911"));
        startActivity(intent);
    }

    private void logOut() {
        mGoogleSignInClient
                .signOut()
                .addOnCompleteListener(this, task -> startActivity(new Intent(Home.this, SignIn.class)));
    }

    @Override
    protected void onResume() {
        mSensorManager.registerListener(
                mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
        );
        super.onResume();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}
