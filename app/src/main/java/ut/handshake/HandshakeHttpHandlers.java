package ut.handshake;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.Header;
import org.json.*;
import com.loopj.android.http.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Aurelius on 12/13/14.
 */
public class HandshakeHttpHandlers {
    /*public void getPublicTimeline() throws JSONException {
        HandshakeRestApi.get("statuses/public_timeline.json", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                JSONObject firstEvent = timeline.get(0);
                String tweetText = firstEvent.getString("text");

                // Do something with the response
                System.out.println(tweetText);
            }
        });
    }*/
    private static final String TAG = HandshakeHttpHandlers.class.getSimpleName();
    private static Context mContext;
    private static CopyOnWriteArrayList<Route> mRoutes;
    private static Handshake mHandshakeInstance;
    private static LoginFragment mLoginInstance;
    public static void init(Context context, CopyOnWriteArrayList<Route> routes, Handshake handshakeInstance) {
        mContext = context;
        mRoutes = routes;
        mHandshakeInstance = handshakeInstance;
    }

    public static void putRegId(String regId, String userId) {
        RequestParams params = new RequestParams();
        params.put("pushRegKey", regId);
        HandshakeRestApi.put("user/" + userId + "/notifications", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray

                //try {
                Log.d(TAG, "response: " + response.toString());
                //int status = (Integer) response.get("status");
                Log.d(TAG, "postRegId response code: " + statusCode);
                //} catch (JSONException e) {
                //    e.printStackTrace();
                //}
            }
        });
    }

    public static void getRoutesForUser(String userId) {
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        HandshakeRestApi.get("route/list", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d(TAG, "Got routes: "+response.toString());
                    JSONArray routes = response.getJSONArray("routes");
                    mRoutes.clear();
                    for (int i=0; i<routes.length(); ++i) {
                        JSONObject route = routes.getJSONObject(i);
                        String displayName = route.getString("displayName");
                        JSONArray emails = route.getJSONArray("emails");
                        String routeId = route.getString("id");
                        boolean isOnline = route.getBoolean("open");
                        JSONArray phoneNumbers = route.getJSONArray("phoneNumbers");
                        String owner = route.getString("userId");
                        String type = Route.JOIN_ROUTE_TYPE;
                        if (mHandshakeInstance.userId.equals(owner)) {
                            Log.d(TAG, "Setting type to owner type!!");
                            type = Route.OWN_ROUTE_TYPE;
                        }
                        Route curRoute = new Route(new ArrayList<Range>(), owner, phoneNumbers,
                                emails, routeId, displayName, isOnline, type, new ArrayList<Conversation>());
                        mRoutes.add(curRoute);
                        mHandshakeInstance.updateAndNavigateToRouteDrawer();
                    }
                    Log.d(TAG, "Total routes in store: "+mRoutes.size());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "Failure on getRoutes: " + statusCode);

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d(TAG, "Failure on getRoutes: " + statusCode);

            }
        });


    }

    public static void getRouteByName(String routeName, CopyOnWriteArrayList<Route> routes) {
        HandshakeRestApi.get("route/"+routeName, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d(TAG, "Response: " + response.toString());
                    //TODO: add logic to see if route exists already

                    JSONArray emails = response.getJSONArray("emails");
                    JSONArray ranges = response.getJSONArray("slots");
                    JSONArray phoneNumbers = response.getJSONArray("phoneNumbers");
                    String routeId = response.getString("id");
                    boolean isOnline = response.getBoolean("open");
                    String displayName = response.getString("displayName");
                    String owner = response.getString("userId");
                    //ArrayList<String> nativeEmails = new ArrayList<>(emails.length());
                    ArrayList<Range> nativeRanges = new ArrayList<>(ranges.length());
                    //ArrayList<String> nativePhoneNumbers = new ArrayList<>(phoneNumbers.length());

                    //for (int i=0; i < emails.length(); ++i) {
                    //    nativeEmails.add((String) emails.get(i));
                    //}
                    for (int i=0; i < ranges.length(); ++i) {
                        JSONObject rangeJson = ranges.getJSONObject(i);
                        long start = rangeJson.getLong("start");
                        long end = rangeJson.getLong("end");
                        long repeatInterval = rangeJson.getLong("repeatInterval");
                        long cutoff = rangeJson.getLong("cutoff");
                        //Range(GregorianCalendar start, GregorianCalendar end, int repeatInterval, GregorianCalendar cutoff)
                        GregorianCalendar startCal = new GregorianCalendar();
                        startCal.setTimeInMillis(start);
                        GregorianCalendar endCal = new GregorianCalendar();
                        endCal.setTimeInMillis(end);
                        GregorianCalendar repeatCal = new GregorianCalendar();
                        repeatCal.setTimeInMillis(repeatInterval);
                        GregorianCalendar cutoffCal = new GregorianCalendar();
                        cutoffCal.setTimeInMillis(cutoff);
                        Range newRange = new Range(startCal, endCal, repeatCal, cutoffCal);
                        nativeRanges.add(newRange);
                    }
                    //for (int i=0; i < phoneNumbers.length(); ++i) {
                    //    nativePhoneNumbers.add((String) phoneNumbers.get(i));
                    //}
                    String type = Route.JOIN_ROUTE_TYPE;
                    if (mHandshakeInstance.userId.equals(owner)) {
                        type = Route.OWN_ROUTE_TYPE;
                    }

                    Route newRoute = new Route(nativeRanges, owner, phoneNumbers,
                            emails, routeId, displayName, isOnline, type, new ArrayList<Conversation>());
                    //mRoutes.add(newRoute);
                    //writeRoutesToStorage(mContext, mRoutes);
                } catch (JSONException e) {
                    Log.d(TAG, "Json Except in getRouteByName");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "Invalid route name: "+statusCode);

            }
        });
    }

    public static void getConvosByRoute(String userId, String routeName, CopyOnWriteArrayList<Route> routes) {
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        HandshakeRestApi.get("route/"+routeName+"/member/list", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d(TAG, "Response: "+response.toString());
                    JSONArray members = response.getJSONArray("members");
                    ArrayList<Conversation> newConvos = new ArrayList<>();

                    // retrieve the route from the local storage
                    Route curRoute = mHandshakeInstance.getCurrentRoute();
                    if (curRoute == null) {
                        Log.e(TAG, "The route disappeared from storage!");
                        return;
                    }

                    for (int i=0; i<members.length(); ++i) {
                        JSONObject curPair = members.getJSONObject(i);
                        String memberDisplayName = curPair.getString("memberId");
                        String memberId = curPair.getString("userId");
                        newConvos.add(new Conversation(memberDisplayName, memberId, new ArrayList<Message>()));
                    }
                    curRoute.setConversations(newConvos);
                    mHandshakeInstance.populateFragmentConvos();

                } catch (JSONException e) {
                    Log.d(TAG, "Json Except in getConvosByRoute");
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getMessagesByRouteAndClient(String clientId, String routeName, String cursor) {
        RequestParams params = new RequestParams();
        params.put("cursor", cursor);
        HandshakeRestApi.get("message/"+routeName+"/"+clientId+"/"+"200", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    Log.d(TAG, "Response: " + response.toString());
                    String activeRouteLocal = Handshake.activeRoute;
                    String activeConversationLocal = Handshake.activeConversation;
                    // retrieve the route from the local storage
                    Route curRoute = mHandshakeInstance.getCurrentRoute();
                    if (curRoute == null) {
                        Log.e(TAG, "The route disappeared from storage!");
                        return;
                    }

                    // Special case for client: make another convo
                    if (!curRoute.getOwner().equals(Handshake.userId)) {
                        ArrayList<Conversation> convos = new ArrayList<>();
                        convos.add(new Conversation(curRoute.getRouteId(),null, new ArrayList<Message>()));
                        curRoute.setConversations(convos);
                    }

                    Conversation curConvo = mHandshakeInstance.getCurrentConversation();

                    if (curConvo == null) {
                        Log.e(TAG, "The convo disappeared from storage!");
                        return;
                    }


                    ArrayList<Message> newMessages = new ArrayList<>();
                    // response.getString("cursor");
                    JSONArray messages = response.getJSONArray("messages");
                    for (int i=0; i<messages.length(); ++i) {
                        //TODO: ask nate to put from
                        JSONObject message = messages.getJSONObject(i);
                        String messageBody = message.getString("message");
                        boolean isClient = message.getBoolean("isClient");
                        String id = message.getString("id");
                        String clientUserId = message.getString("clientUserId");
                        String routeId = message.getString("routeId");
                        long timeStamp = message.getLong("created");

                        String from;
                        boolean isMine;
                        if (isClient) {
                            if (clientUserId.equals(Handshake.userId)) {
                                isMine = true;
                            } else {
                                isMine = false;
                            }
                            from = clientUserId;
                        } else {
                            if (clientUserId.equals(Handshake.userId)) {
                                from = mHandshakeInstance.getCurrentRoute().getOwner();
                                isMine = false;
                            } else {
                                from = Handshake.userId;
                                isMine = true;
                            }
                        }

                        Date messageDate = new Date(timeStamp*1000);

                        Message newMessage = new Message (messageBody, from, routeId, isMine, id, messageDate);
                        newMessages.add(newMessage);
                    }
                    curConvo.setMessages(newMessages);
                    mHandshakeInstance.populateFragmentMessages();
                } catch (JSONException e) {
                    Log.d(TAG, "Json Except in getMessagesByRouteAndClient");
                    e.printStackTrace();
                }

            }
        });
    }

    public static void postNewRoute(String userId, JSONArray emails, JSONArray phones, ArrayList<Range> ranges) {
        try {
            RequestParams params = new RequestParams();
            params.put("emails", emails);
            params.put("phoneNumbers", phones);

            if (ranges.size() <= 0) {
                Log.e(TAG, "Need to at least provide a single range!");
                return;
            }
            JSONArray jsonSlots = new JSONArray();
            for (Range range : ranges) {
                JSONObject newJsonObj = new JSONObject();
                long cutOff = range.getCutoff().getTime().getTime()/1000;
                long end = range.getUtcEnd().getTime()/1000;
                long repeatInterval = range.getRepeatInterval().getTime().getTime()/1000;
                long start = range.getUtcStart().getTime()/1000;
                newJsonObj.put("start", start);
                newJsonObj.put("end", end);
                newJsonObj.put("cutoff", cutOff);
                newJsonObj.put("repeatInterval", repeatInterval);
                jsonSlots.put(newJsonObj);
            }

            params.put("slots",jsonSlots.toString());
            params.put("userId", userId);

            Log.d(TAG, "Constructed route in Json:" + params.toString());

            HandshakeRestApi.post("route", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(TAG, "Successfully posted route: " + statusCode);
                    /*
                    try {
                        Log.d(TAG, "Response: " + response.toString());
                        //TODO: add logic to see if route exists already

                        JSONArray emails = response.getJSONArray("emails");
                        JSONArray ranges = response.getJSONArray("slots");
                        JSONArray phoneNumbers = response.getJSONArray("phoneNumbers");
                        String routeId = response.getString("id");
                        String owner = response.getString("userId");
                        boolean isOnline = response.getBoolean("open");
                        String displayName = response.getString("displayName");
                        //ArrayList<String> nativeEmails = new ArrayList<>(emails.length());
                        ArrayList<Range> nativeRanges = new ArrayList<>(ranges.length());
                        //ArrayList<String> nativePhoneNumbers = new ArrayList<>(phoneNumbers.length());

                        //for (int i = 0; i < emails.length(); ++i) {
                        //    nativeEmails.add((String) emails.get(i));
                        //}
                        for (int i = 0; i < ranges.length(); ++i) {
                            JSONObject rangeJson = ranges.getJSONObject(i);
                            long start = rangeJson.getLong("start")*1000;
                            long end = rangeJson.getLong("end")*1000;
                            long repeatInterval = rangeJson.getLong("repeatInterval")*1000;
                            long cutoff = rangeJson.getLong("cutoff")*1000;
                            //Range(GregorianCalendar start, GregorianCalendar end, int repeatInterval, GregorianCalendar cutoff)
                            GregorianCalendar startCal = new GregorianCalendar();
                            startCal.setTimeInMillis(start);
                            GregorianCalendar endCal = new GregorianCalendar();
                            endCal.setTimeInMillis(end);
                            GregorianCalendar repeatCal = new GregorianCalendar();
                            repeatCal.setTimeInMillis(repeatInterval);
                            GregorianCalendar cutoffCal = new GregorianCalendar();
                            cutoffCal.setTimeInMillis(cutoff);
                            Range newRange = new Range(startCal, endCal, repeatCal, cutoffCal);
                            nativeRanges.add(newRange);
                        }
                        //for (int i = 0; i < phoneNumbers.length(); ++i) {
                        //    nativePhoneNumbers.add((String) phoneNumbers.get(i));
                        //}
                        String type = Route.OWN_ROUTE_TYPE;

                        Route newRoute = new Route(nativeRanges, owner, phoneNumbers,
                                emails, routeId, displayName, isOnline, type, new ArrayList<Conversation>());
                        mRoutes.add(newRoute);
                        //writeRoutesToStorage(mContext, mRoutes);


                    } catch (JSONException e) {
                        Log.d(TAG, "Json Except in postNewRoute on Success");
                        e.printStackTrace();
                    }
                */
                    getRoutesForUser(mHandshakeInstance.userId);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d(TAG, "Could not create route: "+statusCode);
                    // TODO: make toast telling the user, that route could not be created
                    Toast.makeText(mHandshakeInstance.getApplicationContext(), "Could not create route!" , Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.d(TAG, "Could not create route: "+statusCode);
                    Toast.makeText(mHandshakeInstance.getApplicationContext(), "Could not create route!" , Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d(TAG, "Failure on create: " + statusCode);
                    Toast.makeText(mHandshakeInstance.getApplicationContext(), responseString , Toast.LENGTH_SHORT).show();
                }


            });
        } catch (JSONException e) {
            Log.d(TAG, "Json Except in postNewRoute");
            e.printStackTrace();
        }

    }

    public static void postUserRegistration(LoginFragment handle, String userId, String email, String name, ArrayList<String> emails,
                                ArrayList<String> phoneNumbers) {
        RequestParams params = new RequestParams();
        mLoginInstance = handle;

        JSONArray emailsJson = new JSONArray(emails);
        JSONArray phoneNumbersJson = new JSONArray(phoneNumbers);
        params.put("email", email);
        params.put("emails", emailsJson.toString());
        params.put("id", userId);
        params.put("name", name);
        params.put("phoneNumbers", phoneNumbersJson);
        Log.d(TAG, "Constructed user in Json:" + params.toString());
        HandshakeRestApi.post("user", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "Successful user registration at backend");
                mLoginInstance.finishResponse();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, "Failure at registration: " + statusCode);
                Toast.makeText(mHandshakeInstance.getApplicationContext(), responseString , Toast.LENGTH_SHORT).show();
            }

        });
    }

    public static void getUserData(String userId) {
        HandshakeRestApi.get("user/" + userId, null, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG,"Failed to get user data"+statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d(TAG,"Received userData: "+response.toString());
                    JSONArray emailJson = response.getJSONArray("emails");
                    JSONArray phoneNumberJson =  response.getJSONArray("phoneNumbers");

                    /*ArrayList<String> emails = new ArrayList<>();
                    ArrayList<String> phoneNumbers = new ArrayList<>();

                    for(int i=0; i<emailJson.length(); ++i) {
                        emails.add(emailJson.getString(i));
                    }
                    for(int i=0; i<phoneNumberJson.length(); ++i) {
                        phoneNumbers.add(phoneNumberJson.getString(i));
                    }
                    Log.d(TAG,"Number emails: "+emails.size());
                    Log.d(TAG,"Number phones: "+phoneNumbers.size());*/
                    mHandshakeInstance.onSuccessGetUserData(emailJson, phoneNumberJson);

                } catch (JSONException e) {
                    Log.d(TAG, "Json Except in getUserData");
                    e.printStackTrace();
                }

            }
        });
    }

    public static void postNewMessage(Message newMessage, String receiverUserId) {
        RequestParams params = new RequestParams();
        params.put("message", newMessage.getMessageBody());

        if (receiverUserId != null) {
            params.put("receiverUserId", receiverUserId);
            Log.d(TAG, "receiverUserId: "+receiverUserId);
        }
        params.put("senderUserId", newMessage.getFrom());
        params.put("routeId", newMessage.getRouteId());
        HandshakeRestApi.post("message/native", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONObject message = response;
                try {
                    //String messageBody = message.getString("message");

                    //boolean isClient = message.getBoolean("isClient");
                    //String id = message.getString("id");
                    String clientUserId = message.getString("clientUserId");
                    String routeId = message.getString("routeId");
                    //long timeStamp = message.getLong("created");

                    /*String from;
                    boolean isMine;
                    if (isClient) {
                        if (clientUserId.equals(Handshake.userId)) {
                            isMine = true;
                        } else {
                            isMine = false;
                        }
                        from = clientUserId;
                    } else {
                        if (clientUserId.equals(Handshake.userId)) {
                            from = mHandshakeInstance.getCurrentRoute().getOwner();
                            isMine = false;
                        } else {
                            from = Handshake.userId;
                            isMine = true;
                        }
                    }*/
                    getMessagesByRouteAndClient(clientUserId, routeId, "");
                    //Date messageDate = new Date(timeStamp*1000);
                    //Message newMessage = new Message (messageBody, from, routeId, isMine, id, messageDate);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "Failure to send Message: " + statusCode);
                //TODO: toast
                Toast.makeText(mHandshakeInstance.getApplicationContext(), "Could not send message, route may be closed!" , Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, "Failure on send Message: " + statusCode);
                Toast.makeText(mHandshakeInstance.getApplicationContext(), responseString , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d(TAG, "Failure on send Message: " + statusCode);
                Toast.makeText(mHandshakeInstance.getApplicationContext(), "Could not send message, route may be closed!" , Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static void postJoinRoute(String userId, String routeName) {
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        HandshakeRestApi.post("route/"+routeName+"/member", params, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "Failure on join: "+statusCode);
                Toast.makeText(mHandshakeInstance.getApplicationContext(), "Route does not exist!" , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, "Failure on join: " + statusCode);
                Toast.makeText(mHandshakeInstance.getApplicationContext(), responseString , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d(TAG, "Failure on join: " + statusCode);
                Toast.makeText(mHandshakeInstance.getApplicationContext(), "Route does not exist!" , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "Joined Route: " + response.toString());
                // Navigate to routes view, don't forget to query all routes;
                getRoutesForUser(mHandshakeInstance.userId);

            }
        });
    }

    public static void writePrefs(Context context, HashMap<String, String> propsToWrite) {

    }

    public static void writePrefsString(String prop, String data) {
        SharedPreferences prefs = mContext.getSharedPreferences(Handshake.class.getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(prop, data);
        editor.commit();
    }

    public static String getPrefsString(String prop, String def) {
        SharedPreferences prefs = mContext.getSharedPreferences(Handshake.class.getSimpleName(), Context.MODE_PRIVATE);
        return prefs.getString(prop,def);
    }

    /*public static String ROUTES_FILE = "routes_file";

    public static void writeRoutesToStorage(Context context, CopyOnWriteArrayList<Route> routes) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(ROUTES_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(routes);
            oos.flush();
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CopyOnWriteArrayList<Route> readRoutesFromStorage(Context context) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(ROUTES_FILE);
            ObjectInputStream oos = new ObjectInputStream(fis);
            CopyOnWriteArrayList<Route> routes = (CopyOnWriteArrayList<Route>) oos.readObject();
            oos.close();
            return routes;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Routes file not found");
            //e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "Could not read from routes file");
            //e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }*/

}
