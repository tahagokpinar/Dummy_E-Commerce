package com.example.afinal

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.afinal.configs.ApiClient
import com.example.afinal.models.User
import com.example.afinal.services.IDummyService
import com.example.afinal.ui.CategoriesFragment
import com.example.afinal.ui.FavoritesFragment
import com.example.afinal.ui.HomeFragment
import com.example.afinal.ui.OrdersFragment
import com.example.afinal.ui.ProfileFragment
import com.example.afinal.ui.SearchFragment
import com.example.afinal.utils.SharedPref
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfigSettings
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var iDummyService: IDummyService
    lateinit var sharedPreferences: SharedPref
    lateinit var remoteConfig: FirebaseRemoteConfig

    lateinit var nav_content: FrameLayout
    lateinit var drawer_layout: DrawerLayout
    lateinit var toolbar: Toolbar

    private val navigationItemSelectedListener = NavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                val fragment = HomeFragment.newInstance("", "")
                addFragment(fragment)
                drawer_layout.closeDrawer(GravityCompat.START)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_categories -> {
                val fragment = CategoriesFragment.newInstance("", "")
                addFragment(fragment)
                drawer_layout.closeDrawer(GravityCompat.START)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_search -> {
                val fragment = SearchFragment.newInstance("", "")
                addFragment(fragment)
                drawer_layout.closeDrawer(GravityCompat.START)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_favorites -> {
                val fragment = FavoritesFragment.newInstance("", "")
                addFragment(fragment)
                drawer_layout.closeDrawer(GravityCompat.START)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_orders -> {
                val fragment = OrdersFragment.newInstance("", "")
                addFragment(fragment)
                drawer_layout.closeDrawer(GravityCompat.START)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
                val fragment = ProfileFragment.newInstance("", "")
                addFragment(fragment)
                drawer_layout.closeDrawer(GravityCompat.START)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_logout -> {
                sharedPreferences.clear()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
                drawer_layout.closeDrawer(GravityCompat.START)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sharedPreferences = SharedPref(this)

        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        nav_content = findViewById(R.id.nav_content)
        drawer_layout = findViewById(R.id.drawer_layout)

        val drawerNavigationView = findViewById<NavigationView>(R.id.nav_navigation)
        drawerNavigationView.setNavigationItemSelectedListener(navigationItemSelectedListener)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nav_navigation)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        FirebaseApp.initializeApp(this)

        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val updated = it.result
                    Log.d("updated", "Updated: $updated")
                }
            }

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.e("RemoteConfig", "Updated: ${configUpdate.updatedKeys}")
                if (configUpdate.updatedKeys.contains("backGroundColor")) {
                    remoteConfig.activate().addOnCompleteListener {
                        if (it.isSuccessful) {
                            val color = FirebaseRemoteConfig.getInstance().getString("backGroundColor")
                            Log.e("color", "Color: $color")
                            findViewById<View>(R.id.drawer_layout).setBackgroundColor(Color.parseColor(color))
                        }
                    }

                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {

            }

        })

        val headerView = drawerNavigationView.getHeaderView(0)
        val headerImage = headerView.findViewById<CircleImageView>(R.id.header_image)
        val headerName = headerView.findViewById<TextView>(R.id.header_name)
        val headerEmail = headerView.findViewById<TextView>(R.id.header_email)

        val token = sharedPreferences.getJwtToken()

        iDummyService = ApiClient.getClient().create(IDummyService::class.java)
        if (!token.isNullOrEmpty()) {
            iDummyService.getProfile("Bearer $token").enqueue(object : Callback<User> {
                override fun onResponse(p0: Call<User>, p1: Response<User>) {
                    if (p1.isSuccessful) {
                        val user = p1.body()
                        if (user != null) {
                            sharedPreferences.setUser(user)
                            Glide.with(this@MainActivity)
                                .load(user.image)
                                .into(headerImage)
                            headerName.text = "${user.firstName} ${user.lastName}"
                            headerEmail.text = user.email
                        }
                    } else {
                        Log.e("ProfileRequest", "Failed to get profile: ${p1.code()}")
                    }
                }

                override fun onFailure(p0: Call<User>, p1: Throwable) {
                    Log.e("ProfileRequest", "Failed to get profile", p1)
                }
            })
        }

        val fragment = HomeFragment.newInstance("", "")
        addFragment(fragment)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                com.google.android.material.R.anim.design_bottom_sheet_slide_in,
                com.google.android.material.R.anim.design_bottom_sheet_slide_out
            )
            .replace(R.id.nav_content, fragment, fragment.javaClass.simpleName)
            .commit()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}