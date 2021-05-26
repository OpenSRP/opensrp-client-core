package org.smartregister.view.controller;

import android.content.Intent;

import org.smartregister.view.activity.EligibleCoupleDetailActivity;

import static org.smartregister.AllConstants.CASE_ID;

public class ProfileNavigationController {

    public static void navigateToECProfile(android.content.Context context, String caseId) {
        Intent intent = new Intent(context.getApplicationContext(),
                EligibleCoupleDetailActivity.class);
        intent.putExtra(CASE_ID, caseId);
        context.startActivity(intent);
    }

    public static void navigateToANCProfile(android.content.Context context, String caseId) {
//        Intent intent = new Intent(context.getApplicationContext(), ANCDetailActivity.class);
//        intent.putExtra(CASE_ID, caseId);
//        context.startActivity(intent);
    }

    public static void navigateToPNCProfile(android.content.Context context, String caseId) {
//        Intent intent = new Intent(context.getApplicationContext(), PNCDetailActivity.class);
//        intent.putExtra(CASE_ID, caseId);
//        context.startActivity(intent);
    }

    public static void navigateToChildProfile(android.content.Context context, String caseId) {
//        Intent intent = new Intent(context.getApplicationContext(), ChildDetailActivity.class);
//        intent.putExtra(CASE_ID, caseId);
//        context.startActivity(intent);
    }
}
