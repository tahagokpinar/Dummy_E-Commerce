package com.example.afinal.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.afinal.FavoriteDetailActivity
import com.example.afinal.R
import com.example.afinal.adapters.FavoriteAdapter
import com.example.afinal.configs.AppDatabase
import com.example.afinal.entities.Favorite
import com.example.afinal.utils.SharedPref

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoritesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoritesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var sharedPreferences: SharedPref
    lateinit var favoriteList: RecyclerView

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
        return inflater.inflate(R.layout.fragment_likes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = SharedPref(requireContext())
        favoriteList = view.findViewById(R.id.favoriteList)
        favoriteList.layoutManager = GridLayoutManager(context, 2)

        val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "favorite_database"
        )
            .allowMainThreadQueries()
            .build()

        val favoriteDao = db.favoriteDao()

        val user = sharedPreferences.getUser()
        val userId = user?.id
        val favorites = favoriteDao.getAllFavorites(userId!!)
        Log.d("Favorites", "Favorites: $favorites")
        val favoriteAdapter = FavoriteAdapter(favorites, ::onFavoriteClick)
        favoriteList.adapter = favoriteAdapter
    }

    private fun onFavoriteClick(favorite: Favorite) {
        val intent = Intent(context, FavoriteDetailActivity::class.java)
        intent.putExtra("favorite", favorite)
        startActivity(intent)
        requireActivity().finish()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LikesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoritesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}