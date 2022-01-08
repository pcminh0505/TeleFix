package com.example.telefixmain.Util;

import android.content.Context;
import android.widget.Toast;

import com.example.telefixmain.Model.Booking.SOSBilling;
import com.example.telefixmain.Model.Booking.SOSRequest;
import com.example.telefixmain.Model.Booking.SOSProgress;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BookingHandler {
    public static void sendSOSRequest(FirebaseDatabase rootNode,
                                      Context context,
                                      String vendorId,
                                      String userId,
                                      String requestId,
                                      long timeCreated,
                                      double currentLat,
                                      double currentLng,
                                      Runnable callback) {

        System.out.println(vendorId + " " + userId);
        DatabaseReference vendorRef = rootNode.getReference(vendorId);

        SOSRequest sosRequest = new SOSRequest(userId, timeCreated, currentLat, currentLng);

        vendorRef.child("sos").child("request").child(requestId).setValue(sosRequest)
                .addOnCompleteListener(task -> Toast.makeText(context,
                        "Request sent!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "" +
                        e.getMessage(), Toast.LENGTH_SHORT).show());
        callback.run();
    }

    public static void acceptSOSRequest(FirebaseDatabase rootNode,
                                        Context context,
                                        String vendorId,
                                        String requestId,
                                        String mechanicId) {
        DatabaseReference vendorRef = rootNode.getReference(vendorId);

        vendorRef.child("sos").child("request").child(requestId).child("mechanicId").setValue(mechanicId)
                .addOnCompleteListener(task -> Toast.makeText(context,
                        "REQUEST ACCEPTED BY MECHANIC ID " + mechanicId, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "" +
                        e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public static void removeSOSRequest(FirebaseDatabase rootNode,
                                        Context context,
                                        String vendorId,
                                        String requestId) {

        DatabaseReference vendorRef = rootNode.getReference(vendorId);

        vendorRef.child("sos").child("request").child(requestId).removeValue()
                .addOnCompleteListener(task -> Toast.makeText(context,
                        "Request cancelled!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "" +
                        e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public static void createProgressTracking (FirebaseDatabase rootNode,
                                               Context context,
                                               String vendorId,
                                               String requestId,
                                               long timeAccepted,
                                               Runnable callback) {
        DatabaseReference vendorRef = rootNode.getReference(vendorId);

        SOSProgress sosProgress = new SOSProgress(timeAccepted);

        vendorRef.child("sos").child("progress").child(requestId).setValue(sosProgress)
                .addOnCompleteListener(task -> {
                    Toast.makeText(context,
                            "Progress tracking has been initialized successfully!", Toast.LENGTH_SHORT).show();
                    callback.run();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "" +
                        e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public static void updateProgressFromMechanic (FirebaseDatabase rootNode,
                                       Context context,
                                       String vendorId,
                                       String requestId,
                                       long timeStamp,
                                       String type) {
        DatabaseReference progressRef = rootNode.getReference(vendorId).child("sos").child("progress").child(requestId);

        switch (type) {
            case "aborted":
                progressRef.child("abortedTime").setValue(timeStamp)
                    .addOnCompleteListener(task -> Toast.makeText(context,
                            "Unexpected problem from Mechanic. Please try another one.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "" +
                            e.getMessage(), Toast.LENGTH_SHORT).show());

//                addToEventFireStore();
                break;
            case "arrived":
                progressRef.child("startFixingTimestamp").setValue(timeStamp)
                    .addOnCompleteListener(task -> Toast.makeText(context,
                            "Mechanic has arrived. Start fixing.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "" +
                            e.getMessage(), Toast.LENGTH_SHORT).show());
                break;
            case "fixed":
                progressRef.child("startBillingTimestamp").setValue(timeStamp)
                    .addOnCompleteListener(task -> Toast.makeText(context,
                            "Mechanic has finished fixing. Start issue billing.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "" +
                            e.getMessage(), Toast.LENGTH_SHORT).show());

                // init billing
//                intibilling()
                break;
        }
    }

//    initBilling()
    public static void uploadSOSBilling(FirebaseDatabase rootNode,
                                         Context context,
                                         String vendorId,
                                         String requestId,
                                         ArrayList<SOSBilling> currentBilling,
                                         int total,
                                         Runnable callback) {
        DatabaseReference billingRef = rootNode.getReference(vendorId).child("sos").child("billing").child(requestId);

        Map<String, Integer> billingData = new HashMap<>();

        for (SOSBilling bill :
                currentBilling) {
            billingData.put(bill.getItem(), bill.getQuantity());
        }

        billingRef.child("timestamp").setValue(System.currentTimeMillis()/1000L);
        billingRef.child("total").setValue(total);
        billingRef.child("data").setValue(billingData)
                .addOnCompleteListener(task -> {
                    Toast.makeText(context,
                            "CURRENT BILL UPLOADED", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "" +
                        e.getMessage(), Toast.LENGTH_SHORT).show());

        callback.run();
    }
}
