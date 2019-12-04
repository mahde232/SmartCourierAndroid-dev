package com.raghdak.wardm.smartcourier;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.raghdak.wardm.smartcourier.SQL.DatabaseHelper;
import com.raghdak.wardm.smartcourier.model.Delivery;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class ReportActivity extends AppCompatActivity {
    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    protected static final int EMAIL_REQ_CODE = 7;
    private DatabaseHelper databaseHelper;
    private FusedLocationProviderClient mFusedLocationClient;
    private Button btnSave;
    private CheckBox unAvailableCheckBox;
    private EditText receiverEditText;
    private EditText floorEditText;
    private EditText entranceEditText;
    private EditText numberOfFloorsEditText;
    private CheckBox signedCheckBox;
    private CheckBox pastedCheckBox;
    private EditText poboxEditText;
    private RadioButton privateHouseRadioButton;
    private RadioButton publicHouseRadioButton;
    private Button btnImage1;
    private Button btnImage2;
    private Button btnImage3;
    private Button btnImage4;
    private Button btnImage5;

    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;
    private ImageView image5;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static int btnNum = 0;
    private Bitmap image1bitmap;
    private Bitmap image2bitmap;
    private Bitmap image3bitmap;
    private Bitmap image4bitmap;
    private Bitmap image5bitmap;

    private String image1str;
    private String image2str;
    private String image3str;
    private String image4str;
    private String image5str;

    private EditText image1txt;
    private EditText image2txt;
    private EditText image3txt;
    private EditText image4txt;
    private EditText image5txt;


    private ArrayList<String> images_path;
    private ArrayList<String> images_text;
    private Long DeliveryID;
    private String Report_string = "";
    final int RQS_LOADIMAGE = 0;
    final int RQS_SENDEMAIL = 1;
    ArrayList<Uri> arrayUri = new ArrayList<Uri>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        //TODO: remove : DeliveryID = (String) bundle.get("deliveryID");
        setContentView(R.layout.activity_report);
        databaseHelper = DatabaseHelper.getInstance(this);
        btnSave = (Button) findViewById((R.id.btn_next_address));
        unAvailableCheckBox = (CheckBox) findViewById((R.id.unAvailableCheckBox));
        receiverEditText = (EditText) findViewById((R.id.receiverEditText));
        floorEditText = (EditText) findViewById((R.id.floorEditText));
        entranceEditText = (EditText) findViewById((R.id.entranceEditText));
        numberOfFloorsEditText = (EditText) findViewById((R.id.NumOfFloorsEditText));
        poboxEditText = (EditText) findViewById((R.id.poboxEditText));
        signedCheckBox = (CheckBox) findViewById((R.id.signedCheckBox));
        pastedCheckBox = (CheckBox) findViewById((R.id.pastedCheckBox));
        publicHouseRadioButton = (RadioButton) findViewById((R.id.publicHouseRadioButton));
        privateHouseRadioButton = (RadioButton) findViewById((R.id.privateHouseRadioButton));
        btnImage1 = (Button) findViewById((R.id.btnImage1));
        btnImage2 = (Button) findViewById((R.id.btnImage2));
        btnImage3 = (Button) findViewById((R.id.btnImage3));
        btnImage4 = (Button) findViewById((R.id.btnImage4));
        btnImage5 = (Button) findViewById((R.id.btnImage5));

        image1 = (ImageView) findViewById((R.id.image1));
        image2 = (ImageView) findViewById((R.id.image2));
        image3 = (ImageView) findViewById((R.id.image3));
        image4 = (ImageView) findViewById((R.id.image4));
        image5 = (ImageView) findViewById((R.id.image5));

        image1txt = (EditText) findViewById((R.id.Text1));
        image2txt = (EditText) findViewById((R.id.Text2));
        image3txt = (EditText) findViewById((R.id.Text3));
        image4txt = (EditText) findViewById((R.id.Text4));
        image5txt = (EditText) findViewById((R.id.Text5));

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        images_path = new ArrayList<String>();
        images_text = new ArrayList<String>();

        btnImage2.setEnabled(FALSE);
        btnImage3.setEnabled(FALSE);
        btnImage4.setEnabled(FALSE);
        btnImage5.setEnabled(FALSE);
        image2txt.setEnabled(FALSE);
        image3txt.setEnabled(FALSE);
        image4txt.setEnabled(FALSE);
        image5txt.setEnabled(FALSE);
//------------------------------------------------------------------------------------------------
// Buttons of 5 Images
//------------------------------------------------------------------------------------------------
        btnImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnNum = 1;
                ///  Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile(1);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.i("TAG", "IOException");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri contentUri = FileProvider.getUriForFile(ReportActivity.this, "com.raghdak.wardm.fileprovider", photoFile);
                        cameraIntent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                        cameraIntent.setFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
                        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, contentUri);
                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                        images_path.add(0, image1str);
                        btnImage2.setEnabled(TRUE);
                        image2txt.setEnabled(TRUE);
                    }
                }
            }
        });


        btnImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnNum = 2;
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile(2);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.i("TAG", "IOException");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri contentUri = FileProvider.getUriForFile(ReportActivity.this, "com.raghdak.wardm.fileprovider", photoFile);
                        cameraIntent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                        cameraIntent.setFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                        images_path.add(1, image2str);
                        btnImage3.setEnabled(TRUE);
                        image3txt.setEnabled(TRUE);
                    }
                }

            }
        });

        btnImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnNum = 3;
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile(3);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.i("TAG", "IOException");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri contentUri = FileProvider.getUriForFile(ReportActivity.this, "com.raghdak.wardm.fileprovider", photoFile);
                        cameraIntent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                        cameraIntent.setFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                        images_path.add(2, image3str);
                        btnImage4.setEnabled(TRUE);
                        image4txt.setEnabled(TRUE);
                    }
                }

            }
        });

        btnImage4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnNum = 4;
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile(4);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.i("TAG", "IOException");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri contentUri = FileProvider.getUriForFile(ReportActivity.this, "com.raghdak.wardm.fileprovider", photoFile);
                        cameraIntent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                        cameraIntent.setFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                        images_path.add(3, image4str);
                        btnImage5.setEnabled(TRUE);
                        image5txt.setEnabled(TRUE);
                    }
                }

            }
        });

        btnImage5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnNum = 5;
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile(5);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.i("TAG", "IOException");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri contentUri = FileProvider.getUriForFile(ReportActivity.this, "com.raghdak.wardm.fileprovider", photoFile);
                        cameraIntent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                        cameraIntent.setFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                        images_path.add(4, image5str);
                    }
                }
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Generate report
                String signed = "0";
                String pasted = "0";
                String privateH = "0";
                String NotFound = "0";
                Report_string = "------------------------------------------------------------------------------------------------\n";
                Report_string = Report_string + "דוח סופי למספר משלוח " + DeliveryID;
                Report_string = Report_string + "\n------------------------------------------------------------------------------------------------\n\n";
                Report_string = Report_string + "להלן סיכום הדוח: ";
                Report_string = Report_string + "\n" + "\n" + "\n";
                Report_string = Report_string + "שם המקבל : " + receiverEditText.getText().toString() + "\n" + "\n";
                Report_string = Report_string + "מספר דירה  : " + entranceEditText.getText().toString() + "\n" + "\n";
                Report_string = Report_string + "מספר קומה  : " + floorEditText.getText().toString() + "\n" + "\n";
                Report_string = Report_string + "מספר קומות  : " + entranceEditText.getText().toString() + "\n" + "\n";
                Report_string = Report_string + "תא דואר  : " + poboxEditText.getText().toString() + "\n" + "\n";


                if (signedCheckBox.isChecked()) {
                    signed = "1";
                    Report_string = Report_string + "כן חתם " + "\n" + "\n";
                } else {
                    signed = "0";
                    Report_string = Report_string + "לא חתם " + "\n" + "\n";

                }
                if (pastedCheckBox.isChecked()) {
                    pasted = "1";
                    Report_string = Report_string + "כן הודבק על הדלת " + "\n" + "\n";
                } else {
                    pasted = "0";
                    Report_string = Report_string + "לא הודבק על הדלת " + "\n" + "\n";

                }
                if (privateHouseRadioButton.isSelected()) {
                    privateH = "1";
                    Report_string = Report_string + "גר בבית פרטי  : " + "\n" + "\n";
                }
                if (publicHouseRadioButton.isSelected()) {
                    privateH = "0";
                    Report_string = Report_string + "גר בבית משותף  : " + "\n" + "\n";
                }
                // if address not availabe do not save report to data base and change status to - 3 (wait)
                if (unAvailableCheckBox.isChecked()) {
                    NotFound = "1";
                    Report_string = "---------------------------------------------\n";
                    Report_string = Report_string + "לא נמצא בכתובת " + "\n";
                    Report_string = Report_string + "---------------------------------------------\n";
                    Intent intent = new Intent(getApplicationContext(), ViewDeliveriesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivityIfNeeded(intent, 0);
                } else {
                    NotFound = "0";
                    Delivery newReport = new Delivery(
                            DeliveryID,
                            NotFound,
                            receiverEditText.getText().toString(),
                            floorEditText.getText().toString(),
                            entranceEditText.getText().toString(),
                            numberOfFloorsEditText.getText().toString(),
                            privateH,
                            signed,
                            pasted
                    );


                    if (privateH.equals("") || pasted.equals("") || signed.equals("") || receiverEditText.getText().toString().equals("") || floorEditText.getText().toString().equals("") ||
                            entranceEditText.getText().toString().equals("") || numberOfFloorsEditText.getText().toString().equals("") || poboxEditText.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "נא למלא את כל הנתונים", Toast.LENGTH_LONG).show();
                        if (receiverEditText.getText().toString().equals(""))
                            receiverEditText.setBackgroundColor(Color.rgb(220, 220, 220));
                        else
                            receiverEditText.setBackgroundColor(Color.rgb(255, 255, 255));
                        if (floorEditText.getText().toString().equals(""))
                            floorEditText.setBackgroundColor(Color.rgb(220, 220, 220));
                        else
                            floorEditText.setBackgroundColor(Color.rgb(255,255,255));

                        if (poboxEditText.getText().toString().equals(""))
                            poboxEditText.setBackgroundColor(Color.rgb(220, 220, 220));
                        else
                            poboxEditText.setBackgroundColor(Color.rgb(255,255,255));
                        if (numberOfFloorsEditText.getText().toString().equals(""))
                            numberOfFloorsEditText.setBackgroundColor(Color.rgb(220, 220, 220));
                              else
                            numberOfFloorsEditText.setBackgroundColor(Color.rgb(255,255,255));
                        if (entranceEditText.getText().toString().equals(""))
                            entranceEditText.setBackgroundColor(Color.rgb(220, 220, 220));
                        else
                            entranceEditText.setBackgroundColor(Color.rgb(255,255,255));
                        return;
                    }

                    if (images_path.size() < 2) {
                        Toast.makeText(getApplicationContext(), "נא להכניס לפחות שתי תמונות", Toast.LENGTH_LONG).show();
                        return;
                    }

                    //databaseHelper.addReport(newReport);
                    images_text.add(0, image1txt.getText().toString());
                    images_text.add(1, image2txt.getText().toString());
                    images_text.add(2, image3txt.getText().toString());
                    images_text.add(3, image4txt.getText().toString());
                    images_text.add(4, image5txt.getText().toString());

                    // Updating Images...
                    Delivery images = new Delivery(
                            DeliveryID,
                            images_path,
                            images_text
                    );
                    //databaseHelper.addImages(images);

                    Report_string = Report_string + "\n==============================\n";
                    Report_string = Report_string + "פירוט תמונות :  " + "\n";
                    Report_string = Report_string + "==============================\n\n";
                    for (int i = 0; i < images_path.size(); i++) {
                        Report_string = Report_string + "תמונה   " + Integer.toString(i + 1) + ":" + "\n";
                        Report_string = Report_string + images_text.get(i).toString() + "\n";
                        Report_string = Report_string + "---------------------------------------------\n";
                    }


                    stringtopdf();

                }

            }
        });


        //----------------------------------------------------------------------
        // Unavailabe address Sellected
        //---------------------------------------------------------------------
        unAvailableCheckBox.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (unAvailableCheckBox.isChecked()) {
                    receiverEditText.setEnabled(FALSE);
                    floorEditText.setEnabled(FALSE);
                    entranceEditText.setEnabled(FALSE);
                    numberOfFloorsEditText.setEnabled(FALSE);
                    poboxEditText.setEnabled(FALSE);
                    signedCheckBox.setEnabled(FALSE);
                    pastedCheckBox.setEnabled(FALSE);
                    publicHouseRadioButton.setEnabled(FALSE);
                    privateHouseRadioButton.setEnabled(FALSE);
                    btnImage1.setEnabled(FALSE);
                    btnImage2.setEnabled(FALSE);
                    btnImage3.setEnabled(FALSE);
                    btnImage4.setEnabled(FALSE);
                    btnImage5.setEnabled(FALSE);
                    image1txt.setEnabled(FALSE);
                    image2txt.setEnabled(FALSE);
                    image3txt.setEnabled(FALSE);
                    image4txt.setEnabled(FALSE);
                    image5txt.setEnabled(FALSE);

                } else {
                    receiverEditText.setEnabled(TRUE);
                    floorEditText.setEnabled(TRUE);
                    entranceEditText.setEnabled(TRUE);
                    numberOfFloorsEditText.setEnabled(TRUE);
                    poboxEditText.setEnabled(TRUE);
                    signedCheckBox.setEnabled(TRUE);
                    pastedCheckBox.setEnabled(TRUE);
                    publicHouseRadioButton.setEnabled(TRUE);
                    privateHouseRadioButton.setEnabled(TRUE);
                    btnImage1.setEnabled(TRUE);
                    btnImage2.setEnabled(TRUE);
                    btnImage3.setEnabled(TRUE);
                    btnImage4.setEnabled(TRUE);
                    btnImage5.setEnabled(TRUE);
                    image1txt.setEnabled(TRUE);
                    image2txt.setEnabled(TRUE);
                    image3txt.setEnabled(TRUE);
                    image4txt.setEnabled(TRUE);
                    image5txt.setEnabled(TRUE);
                }
            }
        });

    }

    private File createImageFile(int btnNum) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + btnNum + ".jpg";
        File imagePath = new File(this.getFilesDir(), "my_images");
        File image = new File(imagePath, imageFileName);
        image.getParentFile().mkdirs();

        // Save a file: path for use with ACTION_VIEW intents
        if (btnNum == 1)
            image1str = "file:" + image.getAbsolutePath();
        else if (btnNum == 2)
            image2str = "file:" + image.getAbsolutePath();
        else if (btnNum == 3)
            image3str = "file:" + image.getAbsolutePath();
        else if (btnNum == 4)
            image4str = "file:" + image.getAbsolutePath();
        else if (btnNum == 5)
            image5str = "file:" + image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RQS_LOADIMAGE:
                    Uri imageUri = data.getData();
                    arrayUri.add(imageUri);
                    break;
                case RQS_SENDEMAIL:
                    break;
            }
        }

        if (requestCode == EMAIL_REQ_CODE && resultCode == RESULT_OK) {

            Intent intent = new Intent(getApplicationContext(), ViewDeliveriesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(intent, 0);

        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                if (btnNum == 1) {
                    image1bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(image1str));
                    image1.setImageBitmap(image1bitmap);
                } else if (btnNum == 2) {
                    image2bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(image2str));
                    image2.setImageBitmap(image2bitmap);

                } else if (btnNum == 3) {
                    image3bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(image3str));
                    image3.setImageBitmap(image3bitmap);

                } else if (btnNum == 4) {
                    image4bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(image4str));
                    image4.setImageBitmap(image4bitmap);
                } else if (btnNum == 5) {
                    image5bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(image5str));
                    image5.setImageBitmap(image5bitmap);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void stringtopdf() {
        String Storage = Environment.getExternalStorageDirectory().toString();
        File imagePath = new File(this.getFilesDir(), "my_images");
        File folder = new File(imagePath, "Smart_Courier");
        ////File folder = new File(Storage, "Smart_Courier");
        final File file = new File(folder, image1str);


        if (!folder.exists()) {
            folder.mkdirs();
        }

        String[] emails = {"Wardmohanna@gmail.com"};
        composeEmail(emails, "דוח סיכום משלוח " + DeliveryID);
    }

    public void composeEmail(String[] addresses, String subject) {

        String fixedPath;
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayUri);
        intent.setType("image/*");


        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        for (String file : images_path) {
            fixedPath = file.substring(6);
            File fileIn = new File(fixedPath);
            Uri u = FileProvider.getUriForFile(ReportActivity.this, "com.raghdak.wardm.fileprovider", fileIn);
            //Uri u = Uri.fromFile(fileIn);
            arrayUri.add(u);
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayUri);
        intent.putExtra(android.content.Intent.EXTRA_TEXT,
                Report_string);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Choice App to send email:"), EMAIL_REQ_CODE);

        }
    }


}