package com.openclassrooms.nomakler.activities

import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.openclassrooms.nomakler.viewmodels.FiltersViewModel
import com.openclassrooms.nomakler.R
import com.openclassrooms.nomakler.utils.Utils
import com.openclassrooms.nomakler.adapters.PropertiesListAdapter
import com.openclassrooms.nomakler.fragments.PropertiesMapFragment
import com.openclassrooms.nomakler.fragments.SearchFragment
import com.openclassrooms.nomakler.models.Property
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import com.openclassrooms.nomakler.database.AppDatabase
import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.ActionBarDrawerToggle
import kotlinx.coroutines.experimental.launch


open class MainActivity : AppCompatActivity() {


    companion object {
        //lateinit var instance: AppDatabase
        var colRef = FirebaseFirestore.getInstance().collection("properties")

        // Shared preferences reference
        const val SHARED_PREFS = "SHARED_PREFS"

        // Notification channel name
        const val NOTIF_CHANNEL = "CREATE_PROP"
    }

    /** List of workmates */
    private var propertiesList = ArrayList<Property>()

    /** Unfiltered list */
    private var unfilteredList = ArrayList<Property>()

    /** Adapter between workmates list and ListView */
    lateinit var mAdapter: PropertiesListAdapter

    /** RecyclerView */
    private lateinit var mRecyclerView: RecyclerView

    /** ViewModel instance */
    private lateinit var viewModel : FiltersViewModel

    /** FirebaseAuth instance */
    private var firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var db : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Room.databaseBuilder(applicationContext,
                AppDatabase::class.java, getString(R.string.app_name))
                .fallbackToDestructiveMigration().build()

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        title = ""
        viewModel = ViewModelProviders.of(this).get(FiltersViewModel::class.java)


        // Test if Internet is available
        if(!Utils.isInternetAvailable(applicationContext)){
            Toast.makeText(this, "Internet connection unavailable.", Toast.LENGTH_SHORT).show()
        }

        // Sign in Firebase Auth anonymously (useful for Storage upload)
        else if(firebaseAuth.currentUser == null){
            firebaseAuth.signInAnonymously()
        }

        mAdapter = PropertiesListAdapter(this, R.layout.row_property, propertiesList)
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL

        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView.layoutManager = llm
        mRecyclerView.adapter = mAdapter

        // Row separator
        mRecyclerView.addItemDecoration(DividerItemDecoration(mRecyclerView.context, llm.orientation))

        colRef.addSnapshotListener(object: EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot> {
            override fun onEvent(result: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                if(result != null){
                    val properties = Utils.documentsToPropertyList(result.documents)
                    launch {
                        db.propertyDao().deleteAll()
                        db.propertyDao().insertAll(properties)
                    }
                    propertiesList.clear()
                    propertiesList.addAll(properties)
                    unfilteredList.clear()
                    unfilteredList.addAll(properties)
                    mAdapter.filter(unfilteredList, viewModel.filter)
                }
            }

        })

        // Drawer configuration
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener {
            drawer_layout.closeDrawers()
            when(it.itemId){
                R.id.nav_search -> {
                    displayFragment(SearchFragment())
                    true
                }
                R.id.nav_map -> {
                    displayFragment(PropertiesMapFragment())
                    true
                }
                else -> { false }
            }
        }








        


        // Updates the list every time a filter value changes
        viewModel.filter.getAllFilters().forEach {
            it?.observeForever {
                mAdapter.filter(unfilteredList, viewModel.filter)
            }
        }
    }

    /** Get properties from Firestore database */
    private fun getPropertyList(): ArrayList<Property> {
        val list = ArrayList<Property>()
        colRef.get().addOnCompleteListener {
            if(it.isSuccessful){
                list.addAll(Utils.documentsToPropertyList(it.result.documents))
            }
        }
        return list
    }



    // Creates the toolbar menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /** Handles transaction to show a fragment in the proper container */
    fun displayFragment(frag: Fragment){
        // Pop backstack to have only one fragment in it
        supportFragmentManager.popBackStack()

        // Transaction
        val transaction = supportFragmentManager.beginTransaction()

        val screenSize = resources.configuration.screenLayout.and(Configuration.SCREENLAYOUT_SIZE_MASK)
        if(screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            // Add the fragment to the 'fragment_container' FrameLayout
            transaction.replace(R.id.large_screen_container, frag)
        } else {
            // Replace whatever is in the fragment_container view with this fragment
            transaction.replace(R.id.fragment_container, frag)
        }
        // add the transaction to the back stack so the user can navigate back
        transaction.addToBackStack(null)

        // Commit the transaction
        transaction.commit()
    }

}
