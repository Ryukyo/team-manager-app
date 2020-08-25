package com.example.teamd.firebase

import android.app.Activity
import android.util.Log
import com.example.teamd.activities.MainActivity
import com.example.teamd.activities.MyProfileActivity
import com.example.teamd.activities.SignInActivity
import com.example.teamd.activities.SignUpActivity
import com.example.teamd.models.User
import com.example.teamd.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            // merging user info
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }

    fun loadUserData(activity: Activity) {
        mFireStore.collection(Constants.USERS)

            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())

                // Convert the document snapshot into the User Data model object.
                val loggedInUser = document.toObject(User::class.java)!!

                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is MyProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting loggedIn user details",
                    e
                )
            }
    }


        fun getCurrentUserID(): String {
            var currentUser = FirebaseAuth.getInstance().currentUser
            var currentUserId = ""
            if (currentUser != null) {
                currentUserId = currentUser.uid
            }
            return currentUserId
        }
    }