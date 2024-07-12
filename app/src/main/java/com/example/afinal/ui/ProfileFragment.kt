package com.example.afinal.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.afinal.R
import com.example.afinal.configs.ApiClient
import com.example.afinal.models.Address
import com.example.afinal.models.Coordinates
import com.example.afinal.models.User
import com.example.afinal.services.IDummyService
import com.example.afinal.utils.SharedPref
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var iDummyService: IDummyService
    lateinit var sharedPreferences: SharedPref

    lateinit var profileImageView : CircleImageView
    lateinit var nameTextView : EditText
    lateinit var surnameTextView : EditText
    lateinit var usernameTextView : EditText
    lateinit var birthDateTextView : EditText
    lateinit var phoneTextView : EditText
    lateinit var addressTextView : EditText
    lateinit var emailTextView : EditText
    lateinit var passwordTextView : EditText
    lateinit var genderTextView : EditText
    lateinit var editProfileButton : Button


    private var isEditing: Boolean = false
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        profileImageView = view.findViewById(R.id.profileImageView)
        nameTextView = view.findViewById(R.id.nameTextView)
        surnameTextView = view.findViewById(R.id.surnameTextView)
        usernameTextView = view.findViewById(R.id.usernameTextView)
        birthDateTextView = view.findViewById(R.id.birthDateTextView)
        phoneTextView = view.findViewById(R.id.phoneTextView)
        addressTextView = view.findViewById(R.id.addressTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        passwordTextView = view.findViewById(R.id.passwordTextView)
        genderTextView = view.findViewById(R.id.genderTextView)
        editProfileButton = view.findViewById(R.id.editProfileButton)

        editProfileButton.setOnClickListener {
            if(isEditing) {
                saveProfile()
            } else {
                enableEditing(true)
                }
        }

        sharedPreferences = SharedPref(requireContext())
        user = sharedPreferences.getUser()
        if (user != null) {
            updateUI(user!!)
        }

        return view
    }

    private fun updateUI(user : User) {
        Glide.with(this)
            .load(user.image)
            .into(profileImageView)
        nameTextView.setText(user.firstName)
        surnameTextView.setText(user.lastName)
        usernameTextView.setText(user.username)
        birthDateTextView.setText(user.birthDate)
        phoneTextView.setText(user.phone)
        addressTextView.setText(formatAddress(user.address))
        emailTextView.setText(user.email)
        passwordTextView.setText(user.password)
        genderTextView.setText(user.gender)
    }

    private fun formatAddress(address: Address): String {
        return "${address.address}, ${address.city}, ${address.state} (${address.stateCode}), ${address.postalCode}, ${address.country} - Lat: ${address.coordinates.lat}, Lng: ${address.coordinates.lng}"
    }

    private fun enableEditing(enable : Boolean) {
        isEditing = enable
        editProfileButton.text = if (enable) "Save Profile" else "Edit Profile"

        nameTextView.isEnabled = enable
        surnameTextView.isEnabled = enable
        usernameTextView.isEnabled = enable
        birthDateTextView.isEnabled = enable
        phoneTextView.isEnabled = enable
        addressTextView.isEnabled = enable
        emailTextView.isEnabled = enable
        passwordTextView.isEnabled = enable
        genderTextView.isEnabled = enable
    }

    private fun saveProfile() {
        val token = sharedPreferences.getJwtToken()
        val userId = user?.id

        if (token != null && userId != null) {
            val updatedFields = HashMap<String, Any>()

            val firstName = nameTextView.text.toString()
            val lastName = surnameTextView.text.toString()
            val username = usernameTextView.text.toString()
            val birthDate = birthDateTextView.text.toString()
            val phone = phoneTextView.text.toString()
            val email = emailTextView.text.toString()
            val password = passwordTextView.text.toString()
            val gender = genderTextView.text.toString()
            val address = Address(
                address = addressTextView.text.toString(),
                city = "",
                coordinates = Coordinates(0.0, 0.0),
                country = "",
                postalCode = "",
                state = "",
                stateCode = ""
            )

            updatedFields["firstName"] = firstName
            updatedFields["lastName"] = lastName
            updatedFields["username"] = username
            updatedFields["birthDate"] = birthDate
            updatedFields["phone"] = phone
            updatedFields["email"] = email
            updatedFields["password"] = password
            updatedFields["gender"] = gender
            updatedFields["address"] = address

            iDummyService = ApiClient.getClient().create(IDummyService::class.java)
            iDummyService.updateProfile("Bearer $token", userId, updatedFields)
                .enqueue(object : Callback<User> {
                    override fun onResponse(p0: Call<User>, p1: Response<User>) {
                        if (p1.isSuccessful) {
                            val user = p1.body()
                            if (user != null) {
                                updateUI(user)
                                enableEditing(false)
                                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(p0: Call<User>, p1: Throwable) {
                        Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
                        enableEditing(false)
                    }
                })
        } else {
            Toast.makeText(context, "Failed to save profile", Toast.LENGTH_SHORT).show()
            enableEditing(false)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}