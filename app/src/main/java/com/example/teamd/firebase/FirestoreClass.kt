package com.example.teamd.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.teamd.activities.*
import com.example.teamd.models.Board
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

    fun createBoard(activity: CreateBoardActivity, board: Board) {

        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Board created successfully.")
                Toast.makeText(activity, "Board created successfully.", Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
            }
    }

    fun getBoardsList(activity: MainActivity) {
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                var boardsList: ArrayList<Board> = ArrayList()
                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for (i in document.documents) {
                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boardsList.add(board)
                }
                activity.populateBoardsListToUI(boardsList)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap) // A hashmap of fields which are to be updated, with a string key and any value
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Profile Data updated successfully!")
                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
            }
    }

    fun loadUserData(activity: Activity, readBoardsList: Boolean = false) {
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
                        activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
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

        fun getBoardDetails(activity: TaskListActivity, documentId: String) {
            mFireStore.collection(Constants.BOARDS)
                .document(documentId)
                .get()
                .addOnSuccessListener { document ->
                    Log.i(activity.javaClass.simpleName, document.toString())
                    val board = document.toObject(Board::class.java)!!
                    board.documentId = document.id
                    activity.boardDetails(board)
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
                }
        }

    fun addUpdateTaskList(activity: Activity, board: Board) {

        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "TaskList updated successfully.")

                if (activity is TaskListActivity) {
                    activity.addUpdateTaskListSuccess()
                } else if (activity is CardDetailsActivity) {
                    activity.addUpdateTaskListSuccess()
                }
            }
            .addOnFailureListener { e ->
                if (activity is TaskListActivity) {
                    activity.hideProgressDialog()
                } else if (activity is TaskListActivity) {
                    activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
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

        fun getAssignedMembersListDetails(activity: MembersActivity, assignedTo: ArrayList<String>) {
            mFireStore.collection(Constants.USERS)
                .whereIn(Constants.ID, assignedTo)
                .get()
                .addOnSuccessListener { document ->
                    Log.i(activity.javaClass.simpleName, document.documents.toString())
                    val usersList: ArrayList<User> = ArrayList()

                    for (i in document.documents) {
                        // Convert all the document snapshots
                        val user = i.toObject(User::class.java)!!
                        usersList.add(user)
                    }
                    activity.setupMembersList(usersList)
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while creating a board.",
                        e
                    )
                }
        }

    fun getMemberDetails(activity: MembersActivity, email: String) {
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL, email)
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())

                if (document.documents.size > 0) {
                    val user = document.documents[0].toObject(User::class.java)!!
                    activity.memberDetails(user)
                } else {
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No such member found.")
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details",
                    e
                )
            }
        }

        fun assignMemberToBoard(activity: MembersActivity, board: Board, user: User) {
            val assignedToHashMap = HashMap<String, Any>()
            assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo

            mFireStore.collection(Constants.BOARDS)
                .document(board.documentId)
                .update(assignedToHashMap)
                .addOnSuccessListener {
                    Log.i(activity.javaClass.simpleName, "TaskList updated successfully.")
                    activity.memberAssignSuccess(user)
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
                }
        }
    }