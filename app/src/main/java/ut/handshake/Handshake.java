package ut.handshake;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import com.loopj.android.http.*;

import org.json.JSONArray;

public class Handshake extends ActionBarActivity implements ListFragment.OnListListener,
        CreateRoute.OnCreateRouteListener, JoinRoute.OnJoinRouteListener  {

    Toolbar toolbar;
    ListView left_drawer;
    ExpandableListView right_drawer;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    HashMap<String, ArrayList<String>> routeNameMap;
    RightDrawerAdapter mRightDrawerAdapter;
    ArrayList<String> routeTypeList;

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_UNREAD_COUNT = "unread_count";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String PROPERTY_USER_ID = "userId";
    public static final String EMAILS = "emails";
    public static final String PHONES = "phoneNumbers";
    public static final String CREATE_ROUTE_TAG = "CreateRoute";

    public static final String USER_ID_KEY = "userIdKey";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    String SENDER_ID = "846080415162";

    private static final String TAG = Handshake.class.getSimpleName();

    //TextView mDisplay;
    GoogleCloudMessaging gcm;
    //AtomicInteger msgId = new AtomicInteger();
    //SharedPreferences prefs;
    Context context;
    String regid;

    public static int notification_id = 0;
    final public static String NOTIFICATION_CANCELLED = "notification_cancelled";

    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public static String userId;
    public static CopyOnWriteArrayList<Route> routes = new CopyOnWriteArrayList<>();

    public static String activeRoute = null;
    public static String activeConversation = null;
    public static ArrayList<Message> activeMessages = null;

    //GcmBroadcastReceiver mReceiver;

    //final static String GROUP_NAME = "Handshake";
    //static int unread_messages = 0;

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(Handshake.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /*private void sendRegistrationIdToBackend() {
        String msg;
        try {

            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }

            regid = gcm.register("846080415162");
            msg = "Device registered, registration ID=" + regid;
            HandshakeHttpHandlers.putRegId(regid, userId);
            storeRegistrationId(context, regid);
        } catch (IOException e) {
            msg = "Error :" + e.getMessage();
            e.printStackTrace();
        }


    }*/

    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    //sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(Object msg) {
                Log.d(TAG,msg + "\n");
                HandshakeHttpHandlers.putRegId(regid, userId);
            }
        }.execute(null, null, null);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    //Override
    //public void successfulLogin(String userId) {
    //    this.userId = userId;
    //}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handshake);

        context = getApplicationContext();
        HandshakeHttpHandlers.init(context, routes, this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString(USER_ID_KEY);
            HandshakeHttpHandlers.writePrefsString(PROPERTY_USER_ID, userId);
            Log.d(TAG, "Got user Id key in handshake: " + userId);
        }

        userId = HandshakeHttpHandlers.getPrefsString(PROPERTY_USER_ID, null);
        if (userId == null) {
            // no user Id, go to registration page
            Intent intent = new Intent(this, Login.class);
            //intent.putExtra(Handshake.USER_ID_KEY, userId);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        Log.d(TAG,"My UserId: "+userId);

        //routes = HandshakeHttpHandlers.readRoutesFromStorage(context);
        if (routes == null) {
            routes = new CopyOnWriteArrayList<>();
        }

        //HandshakeHttpHandlers.getRoutesForUser(userId);

        if (extras != null) {
            if (extras.getBoolean("FromPush")) {
                // this is from a push notification
                //String routeId = extras.getString("RouteId");
                //String clientUserId = extras.getString("clientUserId");
                //HandshakeHttpHandlers.getMessagesByRouteAndClient();
            }
        }

        //IntentFilter filter = new IntentFilter();
        //filter.addAction("com.google.android.c2dm.intent.RECEIVE");
        //filter.addCategory("ut.handshake");
        //context.registerReceiver(new GcmBroadcastReceiver(), filter, "com.google.android.c2dm.permission.SEND", null);
        //context.registerReceiver(new GcmBroadcastReceiver(), filter);

        //IntentFilter filter_cancel = new IntentFilter();
        //filter.addAction(NOTIFICATION_CANCELLED);
        //context.registerReceiver(new NotificationBroadcastReceiver(), filter_cancel);
        // Check device for Play Services APK. If check succeeds, proceed with

        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

        SharedPreferences curPrefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = curPrefs.edit();
        editor.putInt(Handshake.PROPERTY_UNREAD_COUNT, 0);
        editor.commit();

        //  GCM registration.
        if (checkPlayServices()) {
        gcm = GoogleCloudMessaging.getInstance(this);
        regid = getRegistrationId(context);
        Log.d(TAG, regid);
        //if (regid.isEmpty()) {
            registerInBackground();
            //sendRegistrationIdToBackend();
        //}
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        /*if (toolbar != null) {
            Log.d(("Set toolbar"), "Setting toolbar");
            setSupportActionBar(toolbar);
            //getSupportActionBar().setHomeButtonEnabled(true);
            //getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }*/

        //
        toolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Handle the menu item
                        if (item != null && item.getItemId() == android.R.id.home) {
                            if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
                                mDrawerLayout.closeDrawer(Gravity.START);
                            } else {
                                mDrawerLayout.openDrawer(Gravity.START);
                            }
                        }

                        return true;
                    }
                });

        toolbar.inflateMenu(R.menu.menu_handshake);
        toolbar.setNavigationIcon(R.drawable.ic_action_new);
        toolbar.setTitle("Handshake");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Left Drawer setup
        left_drawer = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(left_drawer);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toolbar.setNavigationIcon(R.drawable.ic_action_back);
                    }
                });
            }
        });
        /*
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.open_drawer,
                R.string.close_drawer
        );

        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //popStackIfNeeded();
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                //mActionBar.setDisplayHomeAsUpEnabled(false);
                mDrawerToggle.setToolbarNavigationClickListener(null);
                mDrawerToggle.setDrawerIndicatorEnabled(true);
            }
        });*/
        //mDrawerToggle.setDrawerIndicatorEnabled(false);
        //mDrawerToggle.setHomeAsUpIndicator(null);
        ArrayList<String> textList = new ArrayList<String>();
        textList.add("Create Route");
        textList.add("Join Route");

        left_drawer.setAdapter(new LeftDrawerAdapter(this, textList));
        left_drawer.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                selectLeftDrawerItem(position);
                mDrawerLayout.closeDrawer(left_drawer);
            }
        });

        View header = getLayoutInflater().inflate(R.layout.header, null);
        left_drawer.addHeaderView(header);

        mDrawerLayout.setDrawerListener( new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (drawerView.equals(right_drawer)) {
                    HandshakeHttpHandlers.getRoutesForUser(userId);
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.END);

        // Right Drawer setup
        right_drawer = (ExpandableListView) findViewById(R.id.right_drawer);
        routeTypeList = new ArrayList<>();
        routeTypeList.add("Owned Routes");
        routeTypeList.add("Joined Routes");

        routeNameMap = new HashMap<>();
        routeNameMap.put(routeTypeList.get(0), new ArrayList<String>());
        routeNameMap.put(routeTypeList.get(1), new ArrayList<String>());

        //TODO: remove this! only for testing!
        /*
        Log.d(TAG, "Setting calendar for testing");
        GregorianCalendar gc0 = new GregorianCalendar(2014, Calendar.DECEMBER,14,1,30,20);
        //Log.d(TAG, "Created calendar 0: " + gc0.getTimeInMillis());
        GregorianCalendar gc1 = new GregorianCalendar(2014,Calendar.DECEMBER,14,2,19,20);
        //Log.d(TAG, "Created calendar 1: "+gc1.getTimeInMillis());
        GregorianCalendar gc2 = new GregorianCalendar(2014,Calendar.DECEMBER,15,2,19,20);
        Range tmp_rng = new Range(gc0, gc1, gc1, gc0);
        Range tmp_rng2 = new Range(gc0, gc2, gc1, gc0);
        ArrayList<Range> ranges = new ArrayList<>();
        ranges.add(tmp_rng);
        ArrayList<Range> ranges2 = new ArrayList<>();
        ranges.add(tmp_rng2);

        ArrayList<Message> testMessages = new ArrayList<>();
        for (int i=0; i<10; ++i) {
            Message testMessage = new Message("test"+i,"","",true,"",new Date());
            testMessages.add(testMessage);
        }

        Conversation convo = new Conversation("Client", testMessages);
        Conversation convo2 = new Conversation("Client2", testMessages);
        Conversation convo3 = new Conversation("Client3", testMessages);
        Conversation convo4 = new Conversation("Client4", testMessages);
        Conversation convo5 = new Conversation("Client5", testMessages);
        Conversation convo6 = new Conversation("Client6", testMessages);
        Conversation special = new Conversation("DestinyGirl", testMessages);
        ArrayList<Conversation> convos = new ArrayList<>();
        convos.add(convo);
        convos.add(convo2);
        convos.add(convo3);
        ArrayList<Conversation> convos2 = new ArrayList<>();
        convos2.add(convo4);
        convos2.add(convo5);
        convos2.add(convo6);
        ArrayList<Conversation> convoSpecial = new ArrayList<>();
        convoSpecial.add(special);

        Route tmp = new Route(ranges, userId, null, null, "tigerman", "TigerMan", Route.OWN_ROUTE_TYPE, convos);
        Route tmp1 = new Route(ranges2, userId, null, null, "RandyMarsh", Route.OWN_ROUTE_TYPE, convos2);
        Route tmp2 = new Route(ranges2, userId, null, null, "DestinyGirl", Route.JOIN_ROUTE_TYPE, convoSpecial);
        Route tmp3 = new Route(ranges, userId, null, null, "PandaGirl", Route.JOIN_ROUTE_TYPE, null);
        Route tmp4 = new Route(ranges2, userId, null, null, "LordeGirl", Route.JOIN_ROUTE_TYPE, null);
        Route tmp5 = new Route(ranges, userId, null, null, "CartmanBrah", Route.JOIN_ROUTE_TYPE, null);
        routes.add(tmp);
        routes.add(tmp1);
        routes.add(tmp2);
        routes.add(tmp3);
        routes.add(tmp4);
        routes.add(tmp5);*/

        // End testing!

        helperUpdateRightDrawer();

        mRightDrawerAdapter = new RightDrawerAdapter(this, routeTypeList, routeNameMap);
        right_drawer.setAdapter(mRightDrawerAdapter);



        mDrawerLayout.closeDrawer(right_drawer);

        right_drawer.setOnChildClickListener( new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.d(TAG, "Expanded to: "+groupPosition+", "+childPosition);
                routeSelectionHandler(groupPosition, childPosition);
                mDrawerLayout.closeDrawer(right_drawer);
                return false;
            }
        });

        right_drawer.addHeaderView(header);
        Log.d(TAG, "got here 3");
    }

    private void routeSelectionHandler(int typeIndex, int routeIndex) {
        // retrieve the active route based on the local maps
        activeRoute = routeNameMap.get(routeTypeList.get(typeIndex)).get(routeIndex);

        for (Route route : routes) {
            if (route.getDisplayName().equals(activeRoute)) {
                activeRoute = route.getRouteId();
                break;
            }
        }

        if (activeRoute != null) {
            Log.d(TAG, "Selected route in routeSelectionHandler: " + activeRoute);
        } else {
            Log.d(TAG, "Selected route is null!!! " + activeRoute);
        }
        // send get request for all convos associated with this route if it is an owner
        // otherwise send a get request for all the messages associated with this route

        if (typeIndex==0) {
            HandshakeHttpHandlers.getConvosByRoute(userId, activeRoute, routes);
            //populateFragmentConvos();
        } else if (typeIndex==1) {
            activeConversation = activeRoute;

            // userId must be client since this is a join route
            HandshakeHttpHandlers.getMessagesByRouteAndClient(userId, activeRoute, "");
            //populateFragmentMessages();
        }

    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        int backStackEntries = fm.getBackStackEntryCount();

        if (backStackEntries > 0) {
            getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            toolbar.setTitle("Handshake");
            if (mDrawerLayout.isDrawerOpen(left_drawer)) {
                mDrawerLayout.closeDrawer(left_drawer);
            }
            mDrawerLayout.openDrawer(right_drawer);
        } else {
            super.onBackPressed();
        }

    }

    public void populateFragmentConvos() {
        Route curRoute = getCurrentRoute();
        ArrayList<String> convoStrings =  new ArrayList<>();
        for (Conversation convo : curRoute.getConversations()) {
            if (convo.getOtherActualDisplayName() != null) {
                convoStrings.add(convo.getOtherDisplayName() + " [" + convo.getOtherActualDisplayName() + "]");
            } else {
                convoStrings.add(convo.getOtherDisplayName());
            }
        }
        //TODO: put actual convo timestamps
        ArrayList<String> convoTimeStamps =  new ArrayList<>();
        ListFragment newListFragment = ListFragment.newInstance(null, convoStrings, convoTimeStamps, false, true);
        toolbar.setTitle(curRoute.getDisplayName());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.center_fragment_container, newListFragment)
                .addToBackStack(null)
                .commit();
    }

    public void populateFragmentMessages() {
        activeMessages = getCurrentConversation().getMessages();
        if (activeConversation.equals(activeRoute)) {
            toolbar.setTitle(getCurrentRoute().getDisplayName());
        } else {

            toolbar.setTitle(getCurrentConversation().getOtherDisplayName()+"@"+getCurrentRoute().getDisplayName());
        }

        ArrayList<String> messageBodies = new ArrayList<>();
        ArrayList<String> messageTimeStamps = new ArrayList<>();

        for (Message m : activeMessages) {
            messageBodies.add(m.getMessageBody());
            long time = m.getMessageTime().getTime().getTime();

            String timeStr = (String) android.text.format.DateUtils.getRelativeTimeSpanString(time);

            messageTimeStamps.add(timeStr);
        }
        ListFragment newListFragment = ListFragment.newInstance(null, messageBodies, messageTimeStamps, true, false);
        //toolbar.setTitle(activeRoute);

        /*android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentByTag(CREATE_ROUTE_TAG);
        if(fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();*/

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.center_fragment_container, newListFragment)
                .addToBackStack(null)
                .commit();
    }

    public static Route getCurrentRoute() {
        for (Route route : routes) {
            if (route.getRouteId().equals(activeRoute)) {
                return route;
            }
        }
        return null;
    }

    public static Conversation getCurrentConversation() {
        for (Conversation convo : getCurrentRoute().getConversations()) {
            if (convo.getOtherId()==null) {
                // must be a client with only 1 convo
                return convo;
            }
            else if (convo.getOtherId().equals(activeConversation)) {
                return convo;
            }
        }
        return null;
    }


    private void selectLeftDrawerItem(int position) {
        Log.d(TAG, "Selected left drawer"+position);
        if (position==1) {
            Log.d(TAG, "At position 1");
            HandshakeHttpHandlers.getUserData(userId);

        } else if (position==2) {
            JoinRoute newFrag = JoinRoute.newInstance();
            toolbar.setTitle("Enter Pairing Phrase");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.center_fragment_container, newFrag, "CreateRoute")
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void conversationClickHandler(String conversationName) {

        // HORRIBLE HACK!
        String[] splits = conversationName.split("\\[");
        String finalName = splits[0].replaceAll("\\s+","");
        for (Conversation convo : getCurrentRoute().getConversations()) {
            if (finalName.equals(convo.getOtherDisplayName())) {
                activeConversation = convo.getOtherId();
            }
        }

        activeMessages = getCurrentConversation().getMessages();


        // check who client is
        String clientUserId = null;
        if (getCurrentRoute().getOwner().equals(userId)) {
            clientUserId = activeConversation;
        } else {
            clientUserId = userId;
        }
        HandshakeHttpHandlers.getMessagesByRouteAndClient(clientUserId, activeRoute, "");

    }

    @Override
    public void messageSendHandler(Message messageToSend) {
        // Attempt to send message
        String receiverId = null;
        if (getCurrentRoute().getOwner().equals(userId)) {
            receiverId = getCurrentConversation().getOtherId();
        }
        HandshakeHttpHandlers.postNewMessage(messageToSend, receiverId);
    }

    @Override
    public void newRouteSelected(JSONArray emails, JSONArray phones, ArrayList<Range> ranges) {
        // Send Create Route request
        HandshakeHttpHandlers.postNewRoute(userId, emails, phones, ranges);
    }

    @Override
    public void joinRouteSelected(String routeName) {
        Log.d(TAG, "Calling handler to join route");
        HandshakeHttpHandlers.postJoinRoute(userId, routeName);
    }

    // Return from API Handlers!

    public void onSuccessGetUserData(JSONArray emails, JSONArray phoneNumbers) {
        // Populate the create route fragment
        CreateRoute newFrag = CreateRoute.newInstance(emails, phoneNumbers);
        toolbar.setTitle("Select Route Details");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.center_fragment_container, newFrag, "CreateRoute")
                .addToBackStack(null)
                .commit();
    }

    public void updateAndNavigateToRouteDrawer() {
        helperUpdateRightDrawer();
        mRightDrawerAdapter.update(routeNameMap);
        mRightDrawerAdapter.notifyDataSetChanged();
        if (!mDrawerLayout.isDrawerOpen(right_drawer)) {
            mDrawerLayout.closeDrawer(left_drawer);
            mDrawerLayout.openDrawer(right_drawer);
        }
        /*int backStackEntries = getSupportFragmentManager().getBackStackEntryCount();

        if (backStackEntries > 0) {
            getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            toolbar.setTitle("Handshake");
        }*/
    }

    public void helperUpdateRightDrawer() {
        routeNameMap.clear();
        routeNameMap.put(routeTypeList.get(0), new ArrayList<String>());
        routeNameMap.put(routeTypeList.get(1), new ArrayList<String>());
        for (Route curRoute : routes) {
            String name = curRoute.getDisplayName();
            String type = curRoute.getType();

            //Log.d(TAG,"Traversing Route");
            if (type.equals(Route.OWN_ROUTE_TYPE)) {
                //Log.d(TAG,"Adding Own route to list");
                routeNameMap.get(routeTypeList.get(0)).add(name);
            } else if (type.equals(Route.JOIN_ROUTE_TYPE)) {
                //Log.d(TAG,"Adding Join route to list");
                routeNameMap.get(routeTypeList.get(1)).add(name);
            } else {
                Log.e(TAG, "Invalid route type detected");
            }
        }
    }

}
