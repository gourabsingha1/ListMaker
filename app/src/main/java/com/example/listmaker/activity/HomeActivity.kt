package com.example.listmaker.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
import com.example.listmaker.R
import com.example.listmaker.adapter.ItemListAdapter
import com.example.listmaker.adapter.ItemListSearchViewAdapter
import com.example.listmaker.databinding.ActivityHomeBinding
import com.example.listmaker.model.ItemList
import com.example.listmaker.model.ItemListWithItems
import com.example.listmaker.viewmodel.ItemListTrashViewModel
import com.example.listmaker.viewmodel.ItemListViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity(), ItemListAdapter.ItemListHomeInterface, ItemListSearchViewAdapter.ItemListSearchViewInterface {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var itemListAdapter: ItemListAdapter
    private lateinit var itemListSearchViewAdapter: ItemListSearchViewAdapter
    private lateinit var itemListViewModel: ItemListViewModel
    private lateinit var itemListTrashViewModel: ItemListTrashViewModel
    private lateinit var navDrawer: NavigationView
    private var actionMode: ActionMode? = null
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var rcSignIn : Int = 0
    private var isSignedIn : Boolean = false
    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize
        navigationDrawer()
        setAdapter()
        setRecyclerView()
        setViewModel()
        searchQuery()
//        setSearchBarMenu()
//        signInWithGoogleInit()
//        showGoogleProfilePic()
//        upMenu()

        // Make a list
        binding.btnMakeList.setOnClickListener {
            startActivity(Intent(this, MakeListActivity::class.java))
        }
    }

    // Set Adapter
    private fun setAdapter() {
        itemListAdapter = ItemListAdapter(this)
        itemListSearchViewAdapter = ItemListSearchViewAdapter(this)
    }

    // Set RecyclerView
    private fun setRecyclerView() {
        binding.rvHome.apply {
            adapter = itemListAdapter
        }
        binding.rvHomeSearchView.apply {
            adapter = itemListSearchViewAdapter
        }
    }

    // Set ViewModel
    private fun setViewModel() {
        itemListTrashViewModel = ViewModelProvider(this)[ItemListTrashViewModel::class.java]
        itemListViewModel = ViewModelProvider(this)[ItemListViewModel::class.java]
        itemListViewModel.itemListsWithItems.observe(this) { itemListsWithItems ->
            // Update ItemListsWithItems
            itemListAdapter.updateItemListsWithItems(itemListsWithItems)

            // Show No ItemList Icon in Home
            if (itemListsWithItems.isEmpty()) {
                binding.ivNoListsInHome.visibility = View.VISIBLE
                binding.tvCreateANewList.visibility = View.VISIBLE
            } else {
                binding.ivNoListsInHome.visibility = View.GONE
                binding.tvCreateANewList.visibility = View.GONE
            }

            // Filter based on ItemList name and Item name
            itemListViewModel.searchQuery.observe(this) { query ->
                var filteredItems = listOf<ItemListWithItems>()
                if(query.isNotEmpty()) {
                    filteredItems = itemListsWithItems.filter { itemListWithItems ->
                        itemListWithItems.itemList?.name?.contains(
                            query,
                            ignoreCase = true
                        )!! || itemListWithItems.items?.any { it.name?.contains(query, ignoreCase = true)!! }!!
                    }
                }
                itemListSearchViewAdapter.updateItemListsWithItems(filteredItems)
            }
        }
    }

    // Navigation Drawer
    private fun navigationDrawer() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navDrawer = findViewById(R.id.navDrawer)
        navDrawer.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navLists -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    this.finish()
                }
                R.id.navTrash -> {
                    startActivity(Intent(this, TrashActivity::class.java))
                    this.finish()
                }
                R.id.navSettings -> {
                    binding.drawerLayout.closeDrawer(navDrawer)
                    Handler(Looper.getMainLooper()).postDelayed({
                        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_bottom, R.anim.slide_out_top)
                        startActivity(Intent(this, SettingsActivity::class.java), options.toBundle())
                    },200)
                }
            }
            true
        }

        // Highlight Menu Item (Lists)
//        navDrawer.menu.findItem(R.id.navLists)?.let { menuItem ->
//            menuItem.icon?.setTint(ContextCompat.getColor(this, R.color.blue))
////            menuItem.actionView?.background = ColorDrawable(ContextCompat.getColor(this, R.color.color27))
//        }

        // Open navigation on clicking navigation icon in search bar
        binding.sbHomeSearch.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(navDrawer)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(navDrawer)) {
            binding.drawerLayout.closeDrawer(navDrawer)
        } else if(binding.svHomeSearch.isShowing) {
            binding.svHomeSearch.hide()
        } else {
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom)
            super.onBackPressed()
        }
    }

//    // Searchbar Menu
//    private fun setSearchBarMenu() {
//        binding.sbHomeSearch.setOnMenuItemClickListener { item ->
//            when (item.itemId) {
//                // Google Sign In
//                R.id.menuHomeSignIn -> {
//                    signInWithGoogle()
//                    isSignedIn = true
//                }
//                // Sign Out
//                R.id.menuHomeSignOut -> {
//                    mGoogleSignInClient.signOut()
//                    isSignedIn = false
//                }
//            }
//            super.onOptionsItemSelected(item)
//        }
//    }
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.home_menu, menu)
//        this.menu = menu
//        return true
//    }
//
//    private fun upMenu() {
//        val menuHomeSignIn = menu?.findItem(R.id.menuHomeSignIn)
//        val menuHomeSignOut = menu?.findItem(R.id.menuHomeSignOut)
//        menuHomeSignIn?.isVisible = !isSignedIn
//        menuHomeSignOut?.isVisible = isSignedIn
//    }

    // Search query
    private fun searchQuery() {
        binding.svHomeSearch.editText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                itemListViewModel.setSearchQuery(s.toString())
            }

        })
    }

    // Implement actionMode callback
    private val actionModeCallBack = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            binding.sbHomeSearch.visibility = View.GONE
            menuInflater.inflate(R.menu.multiple_select_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return true
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.menuMultipleSelectSelectAll -> {
                    itemListViewModel.itemListsWithItems.observe(this@HomeActivity) { itemListWithItems ->
                        var count = 0
                        itemListWithItems.forEach { currentItemListWithItems ->
                            if(currentItemListWithItems.itemList?.selected == true) {
                                count++
                            }
                        }
                        if(count == itemListWithItems.size) {
                            itemListWithItems.forEach { currentItemListWithItems ->
                                currentItemListWithItems.itemList?.selected = false
                            }
                            actionMode?.finish()
                        } else {
                            itemListWithItems.forEach { currentItemListWithItems ->
                                currentItemListWithItems.itemList?.selected = true
                            }
                            actionMode?.title = "${itemListWithItems.size}"
                        }
                        itemListAdapter.notifyDataSetChanged()
                    }
                    return true
                }

                R.id.menuMultipleSelectDelete -> {
                    itemListViewModel.itemListsWithItems.observe(this@HomeActivity) { itemListWithItems ->
                        itemListWithItems.forEach { currentItemListWithItems ->
                            if (currentItemListWithItems.itemList?.selected == true) {
                                // Move to TrashActivity
                                currentItemListWithItems.itemList.selected = false
                                itemListTrashViewModel.upsertItemList(currentItemListWithItems.itemList)
                                currentItemListWithItems.items?.forEach { item ->
                                    itemListTrashViewModel.upsertItem(item)
                                }
                                // Delete from HomeActivity
                                itemListViewModel.deleteItemList(currentItemListWithItems.itemList)
                                currentItemListWithItems.items?.forEach { item ->
                                    itemListViewModel.deleteItem(item)
                                }
                            }
                        }
                        itemListAdapter.notifyDataSetChanged()
                    }
                    actionMode?.finish()
                    return true
                }
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            actionMode = null
            Handler(Looper.getMainLooper()).postDelayed({
                binding.sbHomeSearch.visibility = View.VISIBLE
            }, 500)
        }
    }

    // Toggle selection
    private fun toggleSelection(itemList: ItemList) {
        itemList.selected = !itemList.selected
        itemListAdapter.notifyDataSetChanged()

        // Show count of selected ItemLists
        var count = 0
        itemListViewModel.itemListsWithItems.observe(this@HomeActivity) { itemListWithItems ->
            itemListWithItems.forEach { currentItemListWithItems ->
                if (currentItemListWithItems.itemList?.selected == true) {
                    count++
                }
            }
        }
        if (count == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = "$count"
        }
    }

    // If selected, toggle select on single press. Else expand itemList
    override fun onItemListClick(itemListId: Long, itemList: ItemList) {
        if (actionMode == null) {
            Intent(this, ItemListActivity::class.java).also {
                it.putExtra("EXTRA_ITEM_LIST_ID", itemListId)
                val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_bottom, R.anim.slide_out_top)
                startActivity(it, options.toBundle())
            }
        } else {
            toggleSelection(itemList)
        }
    }

    // Select ItemList onLongClick
    override fun onItemListLongPress(itemList: ItemList) {
        if (actionMode == null) {
            actionMode = startActionMode(actionModeCallBack)
        }
        toggleSelection(itemList)
    }


    // For SearchView Similar to above
    private val actionModeCallBackSearchView = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            binding.svHomeSearch.toolbar.visibility = View.GONE
            menuInflater.inflate(R.menu.multiple_select_menu, menu)
            return true
        }
        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return true
        }
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.menuMultipleSelectSelectAll -> {
                    var count = 0
                    itemListSearchViewAdapter.allItemListsWithItems.forEach { currentItemListWithItems ->
                        if(currentItemListWithItems.itemList?.selected == true) {
                            count++
                        }
                    }
                    if(count == itemListSearchViewAdapter.allItemListsWithItems.size) {
                        itemListSearchViewAdapter.allItemListsWithItems.forEach { currentItemListWithItems ->
                            currentItemListWithItems.itemList?.selected = false
                        }
                        actionMode?.finish()
                    } else {
                        itemListSearchViewAdapter.allItemListsWithItems.forEach { currentItemListWithItems ->
                            currentItemListWithItems.itemList?.selected = true
                        }
                        actionMode?.title = "${itemListSearchViewAdapter.allItemListsWithItems.size}"
                    }
                    itemListSearchViewAdapter.notifyDataSetChanged()
                    return true
                }
                R.id.menuMultipleSelectDelete -> {
                    itemListViewModel.itemListsWithItems.observe(this@HomeActivity) { itemListWithItems ->
                        itemListWithItems.forEach { currentItemListWithItems ->
                            if (currentItemListWithItems.itemList?.selected == true) {
                                // Move to TrashActivity
                                currentItemListWithItems.itemList.selected = false
                                itemListTrashViewModel.upsertItemList(currentItemListWithItems.itemList)
                                currentItemListWithItems.items?.forEach { item ->
                                    itemListTrashViewModel.upsertItem(item)
                                }
                                // Delete from HomeActivity
                                itemListViewModel.deleteItemList(currentItemListWithItems.itemList)
                                currentItemListWithItems.items?.forEach { item ->
                                    itemListViewModel.deleteItem(item)
                                }
                            }
                        }
                        itemListSearchViewAdapter.notifyDataSetChanged()
                    }
                    actionMode?.finish()
                    return true
                }
            }
            return false
        }
        override fun onDestroyActionMode(mode: ActionMode?) {
            actionMode = null
            Handler(Looper.getMainLooper()).postDelayed({
                binding.svHomeSearch.toolbar.visibility = View.VISIBLE
            }, 500)
        }
    }
    private fun toggleSelectionSearchView(itemList: ItemList) {
        itemList.selected = !itemList.selected
        itemListSearchViewAdapter.notifyDataSetChanged()

        // Show count of selected ItemLists
        var count = 0
        itemListViewModel.itemListsWithItems.observe(this@HomeActivity) { itemListWithItems ->
            itemListWithItems.forEach { currentItemListWithItems ->
                if (currentItemListWithItems.itemList?.selected == true) {
                    count++
                }
            }
        }
        if (count == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = "$count"
        }
    }
    override fun onItemListClickSearchView(itemListId: Long, itemList: ItemList) {
        if (actionMode == null) {
            Intent(this, ItemListActivity::class.java).also {
                it.putExtra("EXTRA_ITEM_LIST_ID", itemListId)
                val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_bottom, R.anim.slide_out_top)
                startActivity(it, options.toBundle())
            }
        } else {
            toggleSelectionSearchView(itemList)
        }
    }
    override fun onItemListLongPressSearchView(itemList: ItemList) {
        if (actionMode == null) {
            actionMode = startActionMode(actionModeCallBackSearchView)
        }
        toggleSelectionSearchView(itemList)
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

//    // Google Sign in
//    private fun signInWithGoogleInit() {
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail()
//            .build()
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
//    }
//    private fun signInWithGoogle() {
//        val signInIntent = mGoogleSignInClient.signInIntent
//        startActivityForResult(signInIntent, rcSignIn)
//    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == rcSignIn) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                val account = task.getResult(ApiException::class.java)
////                updateUI(account)
//            } catch (e: ApiException) {
//                Log.w("HomeActivityGoogle", "signInResult:failed code=" + e.statusCode)
////                updateUI(null)
//            }
//        }
//        else {
//            Toast.makeText(this, "Not Signed In", Toast.LENGTH_LONG).show()
//        }
//    }
//    private fun updateUI(account: GoogleSignInAccount?) {
//        val personPhoto = account?.photoUrl
//        // Save the URL in shared preferences
//        val sharedPref = getSharedPreferences("ListMaker", Context.MODE_PRIVATE)
//        with (sharedPref.edit()) {
//            putString("profilePictureUrl", personPhoto.toString())
//            apply()
//        }
//        if (personPhoto != null) {
//            Picasso.get().load(personPhoto).into(binding.ivPfp)
//        }
//    }
//    private fun showGoogleProfilePic() {
//        val sharedPref = getSharedPreferences("ListMaker", Context.MODE_PRIVATE)
//        val personPhoto = sharedPref.getString("profilePictureUrl", null)
//        if (personPhoto != null) {
//            Picasso.get().load(personPhoto).into(binding.ivPfp)
//        }
//    }
}

