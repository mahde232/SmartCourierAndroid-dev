package com.raghdak.wardm.smartcourier.SQL;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.raghdak.wardm.smartcourier.model.Courier;
import com.raghdak.wardm.smartcourier.model.Delivery;
import com.raghdak.wardm.smartcourier.protocol.response.LoginResponse;
import com.raghdak.wardm.smartcourier.protocol.response.RegionResponse;

import java.util.ArrayList;
import java.util.Date;
import com.android.volley.toolbox.Volley;
/**
 * Created by wardm on 4/10/2018.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper mDatabaseHelper;
    private static Context mContext;
    private Courier currentCourier;

    ///  private int DeliveryID = 2;
    public Courier getCurrentCourier() {
        return currentCourier;
    }

    public void setCurrentCourier(Courier currentCourier) {
        this.currentCourier = currentCourier;
    }

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "SmartCourier.db";

    //Courier table name
    private static final String TABLE_COURIER = "couriers";
    //Courier Table Columns names
    private static final String COLUMN_COURIER_EMAIL = "email";
    private static final String COLUMN_COURIER_FIRSTNAME = "first_name";
    private static final String COLUMN_COURIER_LASTNAME = "last_name";
    private static final String COLUMN_COURIER_PASSWORD = "password";
    private static final String COLUMN_COURIER_ADDRESS = "address";
    private static final String COLUMN_COURIER_TELEPHONE = "telephone";
    //Deliveries table name
    private static final String TABLE_DELIVERY = "deliveries";
    //Deliveries Table Columns names
    private static final String COLUMN_DELIVERY_ID = "id";
    private static final String COLUMN_DELIVERY_LAT = "lat";
    private static final String COLUMN_DELIVERY_LNG = "lng";
    private static final String COLUMN_DELIVERY_SUBAREA = "subarea";
    private static final String COLUMN_DELIVERY_ADDRESS = "address";
    private static final String COLUMN_DELIVERY_AREA = "area";
    private static final String COLUMN_DELIVERY_STATUS = "status";
    private static final String COLUMN_DELIVERY_URGENT = "urgent";
    private static final String COLUMN_DELIVERY_CLAIMANT = "claimant";
    private static final String COLUMN_DELIVERY_NAME = "name";
    private static final String COLUMN_DELIVERY_PHONE = "phone";
    private static final String COLUMN_DELIVERY_BOX = "box";
    private static final String COLUMN_DELIVERY_DUEDATE = "duedate";
    private static final String COLUMN_DELIVERY_DATE = "date";
    private static final String COLUMN_DELIVERY_NOTFOUND = "notfound";
    private static final String COLUMN_DELIVERY_RECEIVERNAME = "receivername";
    private static final String COLUMN_DELIVERY_FLOOR = "floor";
    private static final String COLUMN_DELIVERY_ENTRANCE = "entrance";
    private static final String COLUMN_DELIVERY_NUMOFFLOORS = "numoffloors";
    private static final String COLUMN_DELIVERY_PRIVATEHOUSE = "privatehouse";
    private static final String COLUMN_DELIVERY_SIGNED = "signed";
    private static final String COLUMN_DELIVERY_PASTEDONDOOR = "pastedondoor";
    private static final String COLUMN_DELIVERY_TEXT = "text";
    private static final String COLUMN_DELIVERY_COURIERID = "courierid";
    //Image table name
    private static final String TABLE_IMAGE = "images";
    //Image Table Columns names
    private static final String COLUMN_IMAGE_DELIVERYID = "deliveryid";
    private static final String COLUMN_IMAGE_PATH = "path";
    private static final String COLUMN_IMAGE_TEXT = "text";

    // create courier table sql query
    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_COURIER + "("
            + COLUMN_COURIER_EMAIL + " TEXT PRIMARY KEY ," + COLUMN_COURIER_FIRSTNAME + " TEXT,"
            + COLUMN_COURIER_LASTNAME + " TEXT," + COLUMN_COURIER_PASSWORD + " TEXT,"
            + COLUMN_COURIER_ADDRESS + " TEXT," + COLUMN_COURIER_TELEPHONE + " TEXT" + ")";

    // drop courier table sql query
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_COURIER;
    // create deliveries table sql query
    private String CREATE_DELIVERIES_TABLE = "CREATE TABLE " + TABLE_DELIVERY + "("
            + COLUMN_DELIVERY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + COLUMN_DELIVERY_LAT + " INTEGER,"
            + COLUMN_DELIVERY_LNG + " INTEGER,"
            + COLUMN_DELIVERY_SUBAREA + " TEXT,"
            + COLUMN_DELIVERY_ADDRESS + " TEXT,"
            + COLUMN_DELIVERY_AREA + " TEXT,"
            + COLUMN_DELIVERY_STATUS + " TEXT,"
            + COLUMN_DELIVERY_URGENT + " TEXT,"
            + COLUMN_DELIVERY_CLAIMANT + " TEXT,"
            + COLUMN_DELIVERY_NAME + " TEXT,"
            + COLUMN_DELIVERY_PHONE + " TEXT,"
            + COLUMN_DELIVERY_BOX + " TEXT,"
            + COLUMN_DELIVERY_DUEDATE + " INTEGER,"
            + COLUMN_DELIVERY_DATE + " INTEGER,"
            + COLUMN_DELIVERY_NOTFOUND + " TEXT,"
            + COLUMN_DELIVERY_RECEIVERNAME + " TEXT,"
            + COLUMN_DELIVERY_FLOOR + " TEXT,"
            + COLUMN_DELIVERY_ENTRANCE + " TEXT,"
            + COLUMN_DELIVERY_NUMOFFLOORS + " TEXT,"
            + COLUMN_DELIVERY_PRIVATEHOUSE + " TEXT,"
            + COLUMN_DELIVERY_SIGNED + " TEXT,"
            + COLUMN_DELIVERY_PASTEDONDOOR + " TEXT,"
            + COLUMN_DELIVERY_TEXT + " TEXT,"
            + COLUMN_DELIVERY_COURIERID + " TEXT,"
            + " FOREIGN KEY(" + COLUMN_DELIVERY_COURIERID + ") REFERENCES " + TABLE_COURIER + "(" + COLUMN_COURIER_EMAIL + ")"
            + ")";

    // drop deliveries table sql query
    private String DROP_DELIVERIES_TABLE = "DROP TABLE IF EXISTS " + TABLE_DELIVERY;
    // create image table sql query
    private String CREATE_IMAGE_TABLE = "CREATE TABLE " + TABLE_IMAGE + "("
            + COLUMN_IMAGE_DELIVERYID + " TEXT,"
            + COLUMN_IMAGE_PATH + " TEXT,"
            + COLUMN_IMAGE_TEXT + " TEXT,"
            + " FOREIGN KEY(" + COLUMN_IMAGE_DELIVERYID + ") REFERENCES " + TABLE_DELIVERY + "(" + COLUMN_DELIVERY_ID + ")"
            + ")";

    // drop image table sql query
    private String DROP_IMAGE_TABLE = "DROP TABLE IF EXISTS " + TABLE_IMAGE;

    /**
     * getInstance - Since this is a singelton class we need it
     *
     * @param context
     * @return the singleton object of this class
     */

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = new DatabaseHelper(context);
        }
        return mDatabaseHelper;
    }

    /**
     * Constructor
     *
     * @param context
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_DELIVERIES_TABLE);
        db.execSQL(CREATE_IMAGE_TABLE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_DELIVERIES_TABLE);
        db.execSQL(DROP_IMAGE_TABLE);
        // Create tables again
        onCreate(db);

    }

    /**
     * This method is to create user record
     *
     * @param courier
     */
    public void addCourier(Courier courier) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Log.i("DataBase", "addCourier function called with Email: " + courier.getEmail() + ".");
        values.put(COLUMN_COURIER_EMAIL, courier.getEmail());
        // values.put(COLUMN_COURIER_FIRSTNAME, courier.getFirstName());
        //values.put(COLUMN_COURIER_LASTNAME, courier.getLastName());
        values.put(COLUMN_COURIER_PASSWORD, courier.getPassword());
        //values.put(COLUMN_COURIER_ADDRESS, courier.getAddress());
        // values.put(COLUMN_COURIER_TELEPHONE, courier.getTelephone());
        // Inserting Row
        db.insert(TABLE_COURIER, null, values);
        db.close();
    }

    /**
     * This method is to create delivery record
     *
     * @param delivery
     */
    public void addDelivery(Delivery delivery) {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.execSQL(CREATE_DELIVERIES_TABLE);
//        db.execSQL(DROP_USER_TABLE);
//        db.execSQL(DROP_DELIVERIES_TABLE);
//        db.execSQL(DROP_IMAGE_TABLE);
        // Create tables again
//        onCreate(db);
        ContentValues values = new ContentValues();
        //Log.i("DataBase", "addDelivery function called with Delivery area: " + delivery.getArea() + ".");
        values.put(COLUMN_DELIVERY_ID, delivery.getId());
        values.put(COLUMN_DELIVERY_LAT, delivery.getLatitude());
        values.put(COLUMN_DELIVERY_LNG, delivery.getLongitude());
        values.put(COLUMN_DELIVERY_URGENT, delivery.getIsUrgent());
        values.put(COLUMN_DELIVERY_CLAIMANT, delivery.getClaimant());
        values.put(COLUMN_DELIVERY_NAME, delivery.getAddress());
        values.put(COLUMN_DELIVERY_PHONE, delivery.getPhone());
        values.put(COLUMN_DELIVERY_BOX, delivery.getBox());
        values.put(COLUMN_DELIVERY_DUEDATE, delivery.getDueDate().getTime());
        values.put(COLUMN_DELIVERY_COURIERID, delivery.getCourierID());
        values.put(COLUMN_DELIVERY_STATUS, "0");
        // Inserting Row
        db.insert(TABLE_DELIVERY, null, values);
        db.close();
    }

    public void addImages(Delivery delivery) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        Log.i("DataBase", "add Images function called");
        for (int i = 0; i < delivery.getImages_path().size(); i++) {
            values.put(COLUMN_IMAGE_DELIVERYID, delivery.getId());
            values.put(COLUMN_IMAGE_PATH, delivery.getImages_path().get(i));
            values.put(COLUMN_IMAGE_TEXT, delivery.getImages_text().get(i));
            db.insert(TABLE_IMAGE, null, values);
        }
        db.close();
    }

    public void addReport(Delivery delivery) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        Log.i("DataBase", "add Report function called");
        if (delivery.getNotFound().equals("1"))
            values.put(COLUMN_DELIVERY_STATUS, "2");
        else
            values.put(COLUMN_DELIVERY_STATUS, "1");
        values.put(COLUMN_DELIVERY_NOTFOUND, delivery.getNotFound());
        values.put(COLUMN_DELIVERY_RECEIVERNAME, delivery.getReceiverName());
        values.put(COLUMN_DELIVERY_FLOOR, delivery.getFloor());
        values.put(COLUMN_DELIVERY_ENTRANCE, delivery.getEntrance());
        values.put(COLUMN_DELIVERY_NUMOFFLOORS, delivery.getNumOfFloors());
        values.put(COLUMN_DELIVERY_PRIVATEHOUSE, delivery.getPrivateHouse());
        values.put(COLUMN_DELIVERY_SIGNED, delivery.getSigned());
        values.put(COLUMN_DELIVERY_PASTEDONDOOR, delivery.getPastedOnDoor());
        // Update Row
        ////     db.update(TABLE_DELIVERY,values, "id="+COLUMN_DELIVERY_ID,null );
        db.update(TABLE_DELIVERY, values, "id= " + delivery.getId(), null);
        db.close();
    }

    /**
     * This method to check user exist or not
     *
     * @param email
     * @return true/false
     */
    public LoginResponse checkUser(String email, String password) {

        /*Log.i("DataBase", "PATH:" + mContext.getDatabasePath("SmartCourier.db"));
        Log.i("DataBase", "checkUser function called with Email: " + email + ",Password: " + password + ".");
        // array of columns to fetch
        String[] columns = {
                COLUMN_COURIER_EMAIL, COLUMN_COURIER_FIRSTNAME, COLUMN_COURIER_LASTNAME, COLUMN_COURIER_PASSWORD,
                COLUMN_COURIER_ADDRESS, COLUMN_COURIER_TELEPHONE
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_COURIER_EMAIL + " = ?";

        // selection argument
        String[] selectionArgs = {email};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        /*Cursor cursor = db.query(TABLE_COURIER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        if (cursorCount == 0) {
            return LoginResponse.NO_User();
        } else {
            cursor.moveToFirst();
            String pass = cursor.getString(cursor.getColumnIndex(COLUMN_COURIER_PASSWORD));
            if (!password.equals(pass)) {
                return LoginResponse.WRONG_PASSWORD();
            } else {
                Courier courier = new Courier(email, password);
                courier.setFirstName(cursor.getString(cursor.getColumnIndex(COLUMN_COURIER_FIRSTNAME)));
                courier.setLastName(cursor.getString(cursor.getColumnIndex(COLUMN_COURIER_LASTNAME)));
                courier.setAddress(cursor.getString(cursor.getColumnIndex(COLUMN_COURIER_ADDRESS)));
                courier.setTelephone(cursor.getString(cursor.getColumnIndex(COLUMN_COURIER_TELEPHONE)));
                cursor.close();
                db.close();
                LoginResponse response = LoginResponse.OK();
                response.setCourier(courier);
                setCurrentCourier(courier);
                return response;
            }
        }*/
        //setCurrentCourier(courier);
        return LoginResponse.OK();
    }

    public RegionResponse getRegionDeliveries(String region) {
        Log.i("DataBase", "getRegionDeliveries function called with Region: " + region + ".");
        ArrayList<Delivery> deliveries = new ArrayList<Delivery>();
        // array of columns to fetch
        String[] columns = {
                COLUMN_DELIVERY_ID, COLUMN_DELIVERY_LAT, COLUMN_DELIVERY_LNG, COLUMN_DELIVERY_AREA,
                COLUMN_DELIVERY_SUBAREA, COLUMN_DELIVERY_ADDRESS, COLUMN_DELIVERY_STATUS,
                COLUMN_DELIVERY_COURIERID, COLUMN_DELIVERY_CLAIMANT, COLUMN_DELIVERY_URGENT,
                COLUMN_DELIVERY_NAME, COLUMN_DELIVERY_PHONE, COLUMN_DELIVERY_BOX, COLUMN_DELIVERY_DUEDATE
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_DELIVERY_STATUS + " = '0' AND " + COLUMN_DELIVERY_AREA + " = ?";

        // selection argument
        String[] selectionArgs = {region};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query(TABLE_DELIVERY, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        if (cursorCount == 0) {
            return RegionResponse.NO_Delivery();
        } else {
            /*RegionResponse response = RegionResponse.OK();
            cursor.moveToFirst();
            for (int i = 0; i < cursorCount; i++) {
                Delivery delivery = new Delivery(
                        cursor.getDouble(cursor.getColumnIndex(COLUMN_DELIVERY_LAT)),
                        cursor.getDouble(cursor.getColumnIndex(COLUMN_DELIVERY_LNG)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_AREA)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_SUBAREA)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_URGENT)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_CLAIMANT)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_PHONE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_BOX)),
                        new Date(cursor.getInt(cursor.getColumnIndex(COLUMN_DELIVERY_DUEDATE))),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_COURIERID)));
                delivery.setId(cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_ID)));
                deliveries.add(delivery);
                cursor.moveToNext();
            }
            response.setDeliveries(deliveries);
            return response;*/
        }
        return RegionResponse.NO_Delivery();
    }

    public int countAllDeliveries() {
        Log.i("DataBase", "countAllDeliveries!");
        ArrayList<Delivery> deliveries = new ArrayList<Delivery>();
        // array of columns to fetch
        String[] columns = {
                COLUMN_DELIVERY_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query(TABLE_DELIVERY, //Table to query
                columns,                    //columns to return
                null,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        return cursorCount;
    }

    public int countDeliveredDeliveries() {
        Log.i("DataBase", "countDeliveredDeliveries!");
        ArrayList<Delivery> deliveries = new ArrayList<Delivery>();
        // array of columns to fetch
        String[] columns = {
                COLUMN_DELIVERY_ID, COLUMN_DELIVERY_STATUS
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_DELIVERY_STATUS + " = '1'";
        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query(TABLE_DELIVERY, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        return cursorCount;
    }

    public ArrayList<Delivery> getAllDeliveries() {

        Log.i("DataBase", "getAllDeliveries!");
        ArrayList<Delivery> deliveries = new ArrayList<Delivery>();
        // array of columns to fetch
        String[] columns = {
                COLUMN_DELIVERY_ID, COLUMN_DELIVERY_LAT, COLUMN_DELIVERY_LNG, COLUMN_DELIVERY_AREA,
                COLUMN_DELIVERY_SUBAREA, COLUMN_DELIVERY_ADDRESS, COLUMN_DELIVERY_STATUS,
                COLUMN_DELIVERY_COURIERID, COLUMN_DELIVERY_CLAIMANT, COLUMN_DELIVERY_URGENT,
                COLUMN_DELIVERY_NAME, COLUMN_DELIVERY_PHONE, COLUMN_DELIVERY_BOX, COLUMN_DELIVERY_DUEDATE
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_DELIVERY_STATUS + " = '0'";
        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query(TABLE_DELIVERY, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                null,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        if (cursorCount == 0) {
            return null;
        } else {
            /*cursor.moveToFirst();
            for (int i = 0; i < cursorCount; i++) {
                Delivery delivery = new Delivery(
                        cursor.getDouble(cursor.getColumnIndex(COLUMN_DELIVERY_LAT)),
                        cursor.getDouble(cursor.getColumnIndex(COLUMN_DELIVERY_LNG)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_AREA)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_SUBAREA)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_URGENT)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_CLAIMANT)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_PHONE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_BOX)),
                        new Date(cursor.getInt(cursor.getColumnIndex(COLUMN_DELIVERY_DUEDATE))),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_COURIERID)));
                delivery.setId(cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_ID)));
                deliveries.add(delivery);
                cursor.moveToNext();*/
        }
        return deliveries;
    }
}

